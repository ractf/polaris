use crate::config::Config;
use crate::repo::token::TokenRepo;
use actix_web::dev::ServiceRequest;
use actix_web::web::Data;
use actix_web::{Error, HttpMessage};
use actix_web_httpauth::extractors::bearer::BearerAuth;
use actix_web_httpauth::extractors::AuthenticationError;
use actix_web_httpauth::headers::www_authenticate::bearer::Bearer;
use chrono::Utc;

pub async fn bearer_auth_validator<T: 'static + TokenRepo>(
    req: ServiceRequest,
    credentials: BearerAuth,
) -> Result<ServiceRequest, Error> {
    let config = req.app_data::<Data<Config>>();
    let token_repo = req.app_data::<Data<T>>();
    if let Some(config) = config {
        let auth = &config.api.auth;
        if !auth.allowed_hosts.is_empty() {
            let mut allowed = false;
            for allowed_hosts in &auth.allowed_hosts {
                if allowed_hosts == &req.peer_addr().unwrap().to_string() {
                    allowed = true;
                }
            }
            if !allowed {
                return Err(AuthenticationError::new(Bearer::default())
                    .with_error_description("Not in allowed hosts.")
                    .into());
            }
        }

        if let Some(token) = &config.api.auth.bootstrap_token {
            if token.token == credentials.token() {
                req.extensions_mut().insert(token.clone());
                return Ok(req);
            }
        }

        if let Some(token_repo) = token_repo {
            let token = token_repo
                .get_by_token(credentials.token().to_string())
                .await;
            return if let Ok(token) = token {
                if let Some(expiry) = token.expiry {
                    if expiry < Utc::now() {
                        return Err(AuthenticationError::new(Bearer::default())
                            .with_error_description("Expired token.")
                            .into());
                    }
                }
                req.extensions_mut().insert(token);
                Ok(req)
            } else {
                Err(AuthenticationError::new(Bearer::default())
                    .with_error_description("Invalid token.")
                    .into())
            };
        }
    }
    Err(AuthenticationError::new(Bearer::default())
        .with_error_description("Invalid token.")
        .into())
}
