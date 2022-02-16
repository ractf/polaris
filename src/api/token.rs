use crate::api::error::APIError;
use crate::api::AppState;
use crate::data::token::{CreateableToken, Token};
use crate::require_permission;
use actix_web::web::{Data, Json, Path};
use actix_web::{delete, get, post, HttpMessage, HttpRequest, HttpResponse};
use chrono::Utc;
use tracing::{error, info};

#[post("/token")]
pub async fn create_token(
    new_token: Json<CreateableToken>,
    state: Data<AppState>,
    req: HttpRequest,
) -> HttpResponse {
    require_permission!(req, "root");

    let new_token = new_token.into_inner();

    if let Ok(taken) = Token::is_name_taken(&state.pool, &new_token.name).await {
        if taken {
            info!("Token submitted with in use name, rejecting.");
            return HttpResponse::BadRequest().json(APIError::name_taken(new_token.name));
        }
    }

    info!("Creating token {}.", new_token.name);

    let mut db_token: Token = new_token.into();

    db_token.issued = Some(Utc::now());

    let result = db_token.save(&state.pool).await;
    if result.is_err() {
        error!("{:?}", result);
        return HttpResponse::InternalServerError().json(APIError::DatabaseError);
    }

    HttpResponse::Ok().json(db_token)
}

#[get("/token")]
pub async fn get_tokens(state: Data<AppState>, req: HttpRequest) -> HttpResponse {
    require_permission!(req, "root");

    let tokens = Token::get_all(&state.pool).await;

    if let Ok(tokens) = tokens {
        HttpResponse::Ok().json(tokens)
    } else {
        HttpResponse::InternalServerError().json(APIError::DatabaseError)
    }
}

#[get("/token/{id}")]
pub async fn get_token(id: Path<i32>, state: Data<AppState>, req: HttpRequest) -> HttpResponse {
    require_permission!(req, "root");

    let token_id = id.into_inner();
    let tokens = Token::get(&state.pool, token_id).await;

    if let Ok(token) = tokens {
        HttpResponse::Ok().json(token)
    } else {
        HttpResponse::InternalServerError().json(APIError::resource_not_found(token_id))
    }
}

#[get("/token/bearer/{bearer}")]
pub async fn get_token_by_bearer(
    bearer: Path<String>,
    state: Data<AppState>,
    req: HttpRequest,
) -> HttpResponse {
    require_permission!(req, "root");

    let bearer = bearer.into_inner();
    let token = Token::get_by_token(&state.pool, bearer.as_str()).await;

    if let Ok(tok) = token {
        HttpResponse::Ok().json(tok)
    } else {
        HttpResponse::InternalServerError().json(APIError::resource_name_not_found(bearer))
    }
}

#[get("/token/name/{name}")]
pub async fn get_token_by_name(
    name: Path<String>,
    state: Data<AppState>,
    req: HttpRequest,
) -> HttpResponse {
    require_permission!(req, "root");

    let token_name = name.into_inner();
    if let Ok(token) = Token::get_by_name(&state.pool, token_name.as_str()).await {
        HttpResponse::Ok().json(token)
    } else {
        HttpResponse::InternalServerError().json(APIError::resource_name_not_found(token_name))
    }
}

#[delete("/token/{id}")]
pub async fn delete_token(id: Path<i32>, state: Data<AppState>, req: HttpRequest) -> HttpResponse {
    require_permission!(req, "root");

    let token_id = id.into_inner();
    let result = Token::delete_by_id(&state.pool, token_id).await;

    if result.is_ok() {
        HttpResponse::Ok().finish()
    } else {
        HttpResponse::InternalServerError().json(APIError::resource_not_found(token_id))
    }
}

#[post("/token/is_valid/{event_id}")]
pub async fn token_is_valid_for_event(
    event_id: Path<i32>,
    token: Json<Token>,
    state: Data<AppState>,
    req: HttpRequest,
) -> HttpResponse {
    require_permission!(req, "root");

    let event_id = event_id.into_inner();
    let result = token.is_valid_for_event(&state.pool, event_id).await;

    HttpResponse::Ok().json(result)
}

#[post("/token/auth/{event_id}")]
pub async fn auth_token_for_event(
    event_id: Path<i32>,
    token: Json<Token>,
    state: Data<AppState>,
    req: HttpRequest,
) -> HttpResponse {
    require_permission!(req, "root");

    let event_id = event_id.into_inner();
    let result = token.add_event(&state.pool, event_id).await;

    if result.is_ok() {
        HttpResponse::Ok().json(())
    } else {
        HttpResponse::InternalServerError().json(APIError::resource_not_found(event_id))
    }
}
