use crate::api::AppState;
use crate::data::token::Token;
use actix_web::dev::ServiceRequest;
use actix_web::web::Data;
use actix_web::{Error, HttpMessage};
use actix_web_httpauth::extractors::bearer::BearerAuth;
use actix_web_httpauth::extractors::AuthenticationError;
use actix_web_httpauth::headers::www_authenticate::bearer::Bearer;
use chrono::Utc;

pub async fn bearer_auth_validator(
    req: ServiceRequest,
    credentials: BearerAuth,
) -> Result<ServiceRequest, Error> {
    let state = req.app_data::<Data<AppState>>();
    if let Some(state) = state {
        let auth = &state.config.api.auth;
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

        if let Some(token) = &state.config.api.auth.bootstrap_token {
            if token.token == credentials.token() {
                req.extensions_mut().insert(token.clone());
                return Ok(req);
            }
        }

        let token = Token::get_by_token(&state.pool, credentials.token().to_string()).await;
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
    Err(AuthenticationError::new(Bearer::default())
        .with_error_description("Invalid token.")
        .into())
}
