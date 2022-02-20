use crate::api::error::APIError;
use crate::api::AppState;
use crate::data::registry::RegistryToken;
use crate::data::token::Token;
use crate::{handle_result, require_permission};
use actix_web::web::{Data, Json, Path};
use actix_web::{delete, get, post, HttpMessage, HttpRequest, HttpResponse};
use tracing::{info};

#[post("/registry_token")]
pub async fn create_registry_token(
    new_token: Json<RegistryToken>,
    state: Data<AppState>,
    req: HttpRequest,
) -> HttpResponse {
    require_permission!(req, "registry_token.create");

    let mut new_token = new_token.into_inner();
    if let Ok(true) = RegistryToken::is_name_taken(&state.pool, &new_token.name).await {
        info!("Registry token submitted with in use name, rejecting.");
        return HttpResponse::BadRequest().json(APIError::name_taken(new_token.name));
    }

    info!("Creating registry token {}.", new_token.name);
    handle_result!(new_token.save(&state.pool).await.map(|_| new_token))
}

//TODO: I'm not sure the best way of handling access control for these routes, for now its root until I find a less strict solution that works
#[get("/registry_token")]
pub async fn get_registry_tokens(state: Data<AppState>, req: HttpRequest) -> HttpResponse {
    require_permission!(req, "root");
    handle_result!(RegistryToken::get_all(&state.pool).await)
}

#[get("/registry_token/{id}")]
pub async fn get_registry_token(
    id: Path<i32>,
    state: Data<AppState>,
    req: HttpRequest,
) -> HttpResponse {
    require_permission!(req, "root");
    let token_id = id.into_inner();
    handle_result!(RegistryToken::get(&state.pool, token_id).await, token_id)
}

#[delete("/registry_token/{id}")]
pub async fn delete_registry_token(
    id: Path<i32>,
    state: Data<AppState>,
    req: HttpRequest,
) -> HttpResponse {
    require_permission!(req, "registry_token.delete");
    let token_id = id.into_inner();
    handle_result!(RegistryToken::delete_by_id(&state.pool, token_id).await)
}
