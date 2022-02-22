//! HTTP API for RACTF Polaris

mod auth;
pub mod error;
mod event;
mod registry_tokens;
mod token;
mod whoami;

use crate::api::auth::bearer_auth_validator;
use crate::api::error::APIError;
use crate::api::event::{
    create_event, delete_event, get_event, get_event_by_name, get_events, update_event,
};
use crate::api::registry_tokens::{
    create_registry_token, delete_registry_token, get_registry_token, get_registry_tokens,
};
use crate::api::token::{auth_token_for_event, create_token, delete_token, get_token, get_token_by_bearer, get_token_by_name, get_tokens, list_events_token_is_valid_for, revoke_token_for_event, token_is_valid_for_event};
use crate::api::whoami::whoami as whoami_route;
use crate::config::Config;
use actix_web::web::{scope, Data};
use actix_web::{App, HttpResponse, HttpServer};
use actix_web_httpauth::middleware::HttpAuthentication;
use anyhow::Result;
use serde::Serialize;
use sqlx::PgPool;
use tracing::error;
use tracing_actix_web::TracingLogger;

/// Internal state passed to API functions
#[derive(Clone)]
pub struct AppState {
    /// The config provided via polaris.toml
    pub config: Config,
    /// Postgres connection pool
    pub pool: PgPool,
}

/// Start the actix web API
pub async fn start_api(config: &Config, pool: &PgPool) -> Result<()> {
    let state = AppState {
        config: config.clone(),
        pool: pool.clone(),
    };
    HttpServer::new(move || {
        let auth = HttpAuthentication::bearer(bearer_auth_validator);
        App::new()
            .app_data(Data::new(state.clone()))
            .wrap(TracingLogger::default())
            .service(
                scope("/api")
                    .wrap(auth)
                    .service(create_event)
                    .service(get_event)
                    .service(get_event_by_name)
                    .service(get_events)
                    .service(delete_event)
                    .service(update_event)
                    .service(create_token)
                    .service(get_tokens)
                    .service(get_token)
                    .service(get_token_by_name)
                    .service(get_token_by_bearer)
                    .service(delete_token)
                    .service(token_is_valid_for_event)
                    .service(list_events_token_is_valid_for)
                    .service(auth_token_for_event)
                    .service(revoke_token_for_event)
                    .service(whoami_route)
                    .service(create_registry_token)
                    .service(get_registry_tokens)
                    .service(get_registry_token)
                    .service(delete_registry_token),
            )
    })
    .bind(&config.api.bind)?
    .run()
    .await?;
    Ok(())
}

#[macro_export]
macro_rules! require_permission {
    ($dst:expr, $arg:tt) => {{
        let exts = $dst.extensions();
        let token = exts.get::<Token>().unwrap();
        if !token.has_permission($arg) {
            return HttpResponse::Forbidden().json(APIError::missing_permission($arg));
        }
        token.clone()
    }};
}

enum QueryItem {
    Id(i32),
    Name(String),
    Empty,
}

impl From<i32> for QueryItem {
    fn from(id: i32) -> Self {
        QueryItem::Id(id)
    }
}

impl From<String> for QueryItem {
    fn from(name: String) -> Self {
        QueryItem::Name(name)
    }
}

impl From<QueryItem> for APIError {
    fn from(qi: QueryItem) -> Self {
        match qi {
            QueryItem::Id(id) => APIError::resource_not_found(id),
            QueryItem::Name(name) => APIError::resource_name_not_found(name),
            QueryItem::Empty => APIError::DatabaseError,
        }
    }
}

enum APIResult<T> {
    Res(Result<T>),
    Bool(bool),
}

impl<T> From<Result<T>> for APIResult<T> {
    fn from(res: Result<T>) -> Self {
        APIResult::Res(res)
    }
}

impl From<bool> for APIResult<()> {
    fn from(b: bool) -> Self {
        APIResult::Bool(b)
    }
}

fn _handle_result_inner<T: Serialize>(
    res: impl Into<APIResult<T>>,
    query: impl Into<QueryItem>,
) -> HttpResponse {
    match res.into() {
        APIResult::Res(Ok(inner)) => HttpResponse::Ok().json(inner),
        APIResult::Res(Err(err)) => {
            error!("{:?}", err);
            let api_err: APIError = query.into().into();
            HttpResponse::InternalServerError().json(api_err)
        }
        APIResult::Bool(b) => HttpResponse::Ok().json(b),
    }
}

/// Handles the pattern of: get thing from database, return thing if good
/// otherwise log and return an error
#[macro_export]
macro_rules! handle_result {
    ($res:expr, $query:expr) => {
        crate::api::_handle_result_inner($res, $query)
    };
    ($res:expr) => {
        crate::api::_handle_result_inner($res, crate::api::QueryItem::Empty)
    };
}

#[macro_export]
macro_rules! assert_event_id_exists {
    ($pool:expr, $id:expr) => {
        match Event::id_exists($pool, $id).await {
            Ok(false) => {
                return HttpResponse::BadRequest().json(APIError::event_not_found($id));
            }
            Err(_) => {
                return HttpResponse::InternalServerError().json(APIError::DatabaseError);
            }
            _ => (),
        }
    };
}
