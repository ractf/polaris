use crate::api::error::APIError;
use crate::api::AppState;
use crate::data::event::Event;
use crate::data::token::{CreateableToken, Token};
use crate::{assert_event_id_exists, handle_result, require_permission};
use actix_web::web::{Data, Json, Path};
use actix_web::{delete, get, post, HttpMessage, HttpRequest, HttpResponse};
use chrono::Utc;
use tracing::info;

#[post("/token")]
pub async fn create_token(
    new_token: Json<CreateableToken>,
    state: Data<AppState>,
    req: HttpRequest,
) -> HttpResponse {
    require_permission!(req, "root");

    let new_token = new_token.into_inner();

    if let Ok(true) = Token::is_name_taken(&state.pool, &new_token.name).await {
        info!("Token submitted with in use name, rejecting.");
        return HttpResponse::BadRequest().json(APIError::name_taken(new_token.name));
    }

    info!("Creating token {}.", new_token.name);
    let mut db_token: Token = new_token.into();
    db_token.issued = Some(Utc::now());

    handle_result!(db_token.save(&state.pool).await.map(|_| db_token))
}

#[get("/token")]
pub async fn get_tokens(state: Data<AppState>, req: HttpRequest) -> HttpResponse {
    require_permission!(req, "root");
    handle_result!(Token::get_all(&state.pool).await)
}

#[get("/token/{id}")]
pub async fn get_token(id: Path<i32>, state: Data<AppState>, req: HttpRequest) -> HttpResponse {
    require_permission!(req, "root");
    let token_id = id.into_inner();
    handle_result!(Token::get(&state.pool, token_id).await, token_id)
}

#[get("/token/bearer/{bearer}")]
pub async fn get_token_by_bearer(
    bearer: Path<String>,
    state: Data<AppState>,
    req: HttpRequest,
) -> HttpResponse {
    require_permission!(req, "root");
    let bearer = bearer.into_inner();
    handle_result!(
        Token::get_by_token(&state.pool, bearer.as_str()).await,
        bearer
    )
}

#[get("/token/name/{name}")]
pub async fn get_token_by_name(
    name: Path<String>,
    state: Data<AppState>,
    req: HttpRequest,
) -> HttpResponse {
    require_permission!(req, "root");
    let token_name = name.into_inner();
    handle_result!(
        Token::get_by_name(&state.pool, token_name.as_str()).await,
        token_name
    )
}

#[delete("/token/{id}")]
pub async fn delete_token(id: Path<i32>, state: Data<AppState>, req: HttpRequest) -> HttpResponse {
    require_permission!(req, "root");
    let token_id = id.into_inner();
    handle_result!(Token::delete_id(&state.pool, token_id).await, token_id)
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
    assert_event_id_exists!(&state.pool, event_id);
    handle_result!(token.is_valid_for_event(&state.pool, event_id).await)
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
    assert_event_id_exists!(&state.pool, event_id);
    handle_result!(token.add_event(&state.pool, event_id).await, event_id)
}

#[post("/token/revoke/{event_id}")]
pub async fn revoke_token_for_event(
    event_id: Path<i32>,
    token: Json<Token>,
    state: Data<AppState>,
    req: HttpRequest,
) -> HttpResponse {
    require_permission!(req, "root");
    let event_id = event_id.into_inner();
    assert_event_id_exists!(&state.pool, event_id);
    handle_result!(token.remove_event(&state.pool, event_id).await, event_id)
}
