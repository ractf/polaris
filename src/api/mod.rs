//! HTTP API for RACTF Polaris

mod auth;
mod challenge;
pub mod error;
mod event;
mod registry_tokens;
mod token;

use crate::api::auth::bearer_auth_validator;
use crate::api::challenge::configure_challenge_endpoints;
use crate::api::error::APIError;
use crate::api::event::configure_event_endpoints;
use crate::api::registry_tokens::configure_registry_token_endpoints;
use crate::api::token::configure_token_endpoints;
use crate::config::Config;
use crate::repo::challenge::ChallengeRepo;
use crate::repo::event::EventRepo;
use crate::repo::node::NodeRepo;
use crate::repo::registry::RegistryTokenRepo;
use crate::repo::token::TokenRepo;
use actix_web::web::{scope, Data};
use actix_web::{App, HttpResponse, HttpServer};
use actix_web_httpauth::middleware::HttpAuthentication;
use anyhow::Result;
use reqwest::StatusCode;
use serde::Serialize;
use tracing::error;
use tracing_actix_web::TracingLogger;

/// Start the actix web API
pub async fn start_api<T, R, E, N, C>(
    config: Config,
    token_repo: T,
    registry_token_repo: R,
    event_repo: E,
    node_repo: N,
    challenge_repo: C,
) -> Result<()>
where
    T: 'static + TokenRepo + Send + Clone,
    R: 'static + RegistryTokenRepo + Send + Clone,
    E: 'static + EventRepo + Send + Clone,
    N: 'static + NodeRepo + Send + Clone,
    C: 'static + ChallengeRepo + Send + Clone,
{
    let bind = config.api.bind.clone();
    HttpServer::new(move || {
        let auth = HttpAuthentication::bearer(bearer_auth_validator::<T>);
        App::new()
            .app_data(Data::new(config.clone()))
            .app_data(Data::new(token_repo.clone()))
            .app_data(Data::new(registry_token_repo.clone()))
            .app_data(Data::new(event_repo.clone()))
            .app_data(Data::new(node_repo.clone()))
            .app_data(Data::new(challenge_repo.clone()))
            .wrap(TracingLogger::default())
            .service(
                scope("/api")
                    .wrap(auth)
                    .service(scope("/event").configure(configure_event_endpoints::<E, T>))
                    .service(scope("/token").configure(configure_token_endpoints::<T>))
                    .service(
                        scope("/registry_token").configure(configure_registry_token_endpoints::<R>),
                    )
                    .service(scope("/challenge").configure(configure_challenge_endpoints::<C>)),
            )
    })
    .bind(&bind)?
    .run()
    .await?;
    Ok(())
}

#[macro_export]
macro_rules! require_permission {
    ($dst:expr, $arg:tt) => {{
        use actix_web::HttpMessage;
        let exts = $dst.extensions();
        let token = exts.get::<crate::data::token::Token>().unwrap();
        if !token.has_permission($arg) {
            return HttpResponse::Forbidden().json(crate::api::error::APIError::missing_permission($arg));
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
    status_code: StatusCode,
) -> HttpResponse {
    match res.into() {
        APIResult::Res(Ok(inner)) => HttpResponse::Ok().json(inner),
        APIResult::Res(Err(err)) => {
            error!("{:?}", err);
            let api_err: APIError = query.into().into();
            HttpResponse::build(status_code).json(api_err)
        }
        APIResult::Bool(b) => HttpResponse::Ok().json(b),
    }
}

/// Handles the pattern of: get thing from database, return thing if good
/// otherwise log and return an error
#[macro_export]
macro_rules! handle_result {
    ($res:expr, $query:expr, $status:expr) => {
        crate::api::_handle_result_inner($res, $query, $status)
    };
    ($res:expr, $status:expr) => {
        crate::api::_handle_result_inner($res, crate::api::QueryItem::Empty, $status)
    };
    ($res:expr) => {
        crate::api::_handle_result_inner(
            $res,
            crate::api::QueryItem::Empty,
            actix_web::http::StatusCode::INTERNAL_SERVER_ERROR,
        )
    };
}

#[macro_export]
macro_rules! assert_event_id_exists {
    ($pool:expr, $id:expr) => {
        match Event::id_exists($pool, $id).await {
            Ok(false) => {
                return HttpResponse::NotFound().json(APIError::event_not_found($id));
            }
            Err(_) => {
                return HttpResponse::InternalServerError().json(APIError::DatabaseError);
            }
            _ => (),
        }
    };
}

#[macro_export]
macro_rules! get_object_or_404 {
    ($repo:ident, $id:expr) => {
        if let Ok(object) = $repo.get_by_id($id).await {
            object
        } else {
            return HttpResponse::NotFound()
                .json(crate::api::error::APIError::resource_not_found($id));
        }
    };
}

#[macro_export]
macro_rules! get_object_as_response_or_404 {
    ($repo:ident, $id:expr) => {
        if let Ok(object) = $repo.get_by_id($id).await {
            HttpResponse::Ok().json(object)
        } else {
            HttpResponse::NotFound().json(crate::api::error::APIError::resource_not_found($id))
        }
    };
}

#[macro_export]
macro_rules! get_object_by_name_or_404 {
    ($repo:ident, $name:expr) => {
        if let Ok(object) = $repo.get_by_name(&$name).await {
            object
        } else {
            return HttpResponse::NotFound()
                .json(crate::api::error::APIError::resource_name_not_found($name));
        }
    };
}

#[macro_export]
macro_rules! get_object_by_name_as_response_or_404 {
    ($repo:ident, $name:expr) => {
        if let Ok(object) = $repo.get_by_name($name).await {
            HttpResponse::Ok().json(object)
        } else {
            HttpResponse::NotFound()
                .json(crate::api::error::APIError::resource_name_not_found($name))
        }
    };
}

#[macro_export]
macro_rules! save_object_and_return {
    ($repo:ident, $object:ident) => {
        crate::handle_result!($repo.save($object).await, actix_web::http::StatusCode::BAD_REQUEST)
    };
}

#[macro_export]
macro_rules! delete_object_and_return {
    ($repo:ident, $object:ident) => {
        crate::handle_result!(
            $repo.delete_by_id($object).await,
            actix_web::http::StatusCode::INTERNAL_SERVER_ERROR
        )
    };
}

#[macro_export]
macro_rules! assert_name_not_taken {
    ($repo:ident, $name:expr) => {
        if let Ok(true) = $repo.is_name_taken(&$name).await {
            info!("Object submitted with in use name, rejecting.");
            return HttpResponse::BadRequest().json(crate::api::error::APIError::name_taken($name));
        }
    };
}

//TODO: Properly handle APIError construction
#[macro_export]
macro_rules! assert_ok {
    ($res:expr, $status:expr) => {
        if let Ok(result) = $res {
            result
        } else {
            return actix_web::HttpResponse::build($status)
                .json(crate::api::error::APIError::DatabaseError);
        }
    };
    ($res:expr) => {
        crate::assert_ok!($res, actix_web::http::StatusCode::INTERNAL_SERVER_ERROR)
    };
}

#[macro_export]
macro_rules! save_object {
    ($repo:ident, $object:ident) => {
        crate::try!($repo.save($object).await)
    };
}

#[macro_export]
macro_rules! delete_object {
    ($repo:ident, $object:ident) => {
        crate::try!($repo.delete_by_id($object).await)
    };
}

#[macro_export]
macro_rules! assert_token_valid_for_event {
    ($repo:ident, $token:ident, $event_id:ident) => {
        if !$repo.is_token_valid_for_event(&$token, $event_id).await {
            return actix_web::HttpResponse::NotFound()
                .json(crate::api::error::APIError::event_not_found($event_id));
        }
    };
}

#[macro_export]
macro_rules! assert_some {
    ($res:expr, $status:expr, $error:expr) => {
        if let Some(result) = $res {
            result
        } else {
            return actix_web::HttpResponse::build($status).json($error);
        }
    };
    ($res:expr, $status:expr) => {
        crate::assert_some!($res, $status, crate::api::error::APIError::DatabaseError)
    };
    ($res:expr) => {
        crate::assert_some!($res, actix_web::http::StatusCode::INTERNAL_SERVER_ERROR)
    };
}
