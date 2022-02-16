//! HTTP API for RACTF Polaris

mod auth;
pub mod error;
mod event;
mod registry_tokens;
mod token;
mod whoami;

use crate::api::auth::bearer_auth_validator;
use crate::api::event::{
    create_event, delete_event, get_event, get_event_by_name, get_events, update_event,
};
use crate::api::registry_tokens::{
    create_registry_token, delete_registry_token, get_registry_token, get_registry_tokens,
};
use crate::api::token::{
    auth_token_for_event, create_token, delete_token, get_token, get_token_by_bearer,
    get_token_by_name, get_tokens, token_is_valid_for_event,
};
use crate::api::whoami::whoami as whoami_route;
use crate::config::Config;
use actix_web::web::{scope, Data};
use actix_web::{App, HttpServer};
use actix_web_httpauth::middleware::HttpAuthentication;
use anyhow::Result;
use sqlx::PgPool;
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
                    .service(token_is_valid_for_event)
                    .service(delete_event)
                    .service(update_event)
                    .service(create_token)
                    .service(get_tokens)
                    .service(get_token)
                    .service(get_token_by_name)
                    .service(get_token_by_bearer)
                    .service(delete_token)
                    .service(auth_token_for_event)
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
