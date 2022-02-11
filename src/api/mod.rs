//! HTTP API for RACTF Polaris

mod auth;
pub mod error;
mod event;
mod token;
mod whoami;
mod registry_tokens;

use crate::api::auth::bearer_auth_validator;
use crate::api::event::{create_event, delete_event, get_event, get_events, update_event};
use crate::api::token::{create_token, delete_token, delete_token_by_name, get_token, get_token_by_name, get_tokens};
use crate::api::whoami::whoami as whoami_route;
use crate::config::Config;
use actix_web::web::Data;
use actix_web::{App, HttpServer};
use actix_web_httpauth::middleware::HttpAuthentication;
use anyhow::Result;
use sqlx::PgPool;
use tracing_actix_web::TracingLogger;
use crate::api::registry_tokens::{create_registry_token, delete_registry_token, get_registry_token, get_registry_tokens};

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
            .wrap(TracingLogger::default())
            .wrap(auth)
            .app_data(Data::new(state.clone()))
            .service(create_event)
            .service(get_event)
            .service(get_events)
            .service(delete_event)
            .service(update_event)
            .service(create_token)
            .service(get_tokens)
            .service(get_token)
            .service(get_token_by_name)
            .service(delete_token)
            .service(delete_token_by_name)
            .service(whoami_route)
            .service(create_registry_token)
            .service(get_registry_tokens)
            .service(get_registry_token)
            .service(delete_registry_token)
    })
    .bind(&config.api.bind)?
    .run()
    .await?;
    Ok(())
}
