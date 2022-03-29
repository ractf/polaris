use crate::api::error::APIError;
use crate::data::token::{CreateableToken, Token, TokenEventPair};
use crate::repo::token::TokenRepo;
use crate::{
    assert_name_not_taken, get_object_as_response_or_404, get_object_by_name_as_response_or_404,
    get_object_or_404, handle_result, require_permission, save_object_and_return, assert_some
};
use actix_web::http::StatusCode;
use actix_web::web::{delete, get, post, Data, Json, Path, ServiceConfig};
use actix_web::{HttpMessage, HttpRequest, HttpResponse};
use chrono::Utc;
use tracing::info;

pub fn configure_token_endpoints<T: 'static + TokenRepo>(config: &mut ServiceConfig) {
    config
        .route("/", post().to(create_token::<T>))
        .route("/", get().to(get_tokens::<T>))
        .route("/{id}", get().to(get_token::<T>))
        .route("/bearer/{bearer}", get().to(get_token_by_bearer::<T>))
        .route("/name/{name}", get().to(get_token_by_name::<T>))
        .route("/{id}", delete().to(delete_token::<T>))
        .route("/is_valid", post().to(token_is_valid_for_event::<T>))
        .route("/auth", post().to(auth_token_for_event::<T>))
        .route("/auth/revoke", post().to(remove_auth_token_for_event::<T>))
        .route("/self", get().to(view_self))
        .route("/events", get().to(get_events::<T>));
}

async fn create_token<T: TokenRepo>(
    new_token: Json<CreateableToken>,
    req: HttpRequest,
    token_repo: Data<T>,
) -> HttpResponse {
    require_permission!(req, "root");

    let new_token = new_token.into_inner();

    assert_name_not_taken!(token_repo, new_token.name);
    info!("Creating token {}.", new_token.name);

    let mut db_token: Token = new_token.into();
    db_token.issued = Some(Utc::now());

    save_object_and_return!(token_repo, db_token)
}

async fn get_tokens<T: TokenRepo>(token_repo: Data<T>, req: HttpRequest) -> HttpResponse {
    require_permission!(req, "root");
    handle_result!(
        token_repo.get_all().await,
        StatusCode::INTERNAL_SERVER_ERROR
    )
}

async fn get_token<T: TokenRepo>(
    id: Path<i32>,
    token_repo: Data<T>,
    req: HttpRequest,
) -> HttpResponse {
    require_permission!(req, "root");
    let id = id.into_inner();
    get_object_as_response_or_404!(token_repo, id)
}

async fn get_token_by_bearer<T: TokenRepo>(
    bearer: Path<String>,
    token_repo: Data<T>,
    req: HttpRequest,
) -> HttpResponse {
    require_permission!(req, "root");

    let bearer = bearer.into_inner();
    handle_result!(
        token_repo.get_by_token(bearer.clone()).await,
        bearer,
        StatusCode::NOT_FOUND
    )
}

async fn get_token_by_name<T: TokenRepo>(
    name: Path<String>,
    token_repo: Data<T>,
    req: HttpRequest,
) -> HttpResponse {
    require_permission!(req, "root");

    let token_name = name.into_inner();
    get_object_by_name_as_response_or_404!(token_repo, &token_name)
}

async fn delete_token<T: TokenRepo>(
    id: Path<i32>,
    token_repo: Data<T>,
    req: HttpRequest,
) -> HttpResponse {
    require_permission!(req, "root");

    let token_id = id.into_inner();
    get_object_or_404!(token_repo, token_id);
    handle_result!(
        token_repo.delete_by_id(token_id).await,
        StatusCode::INTERNAL_SERVER_ERROR
    )
}

async fn token_is_valid_for_event<T: TokenRepo>(
    pair: Json<TokenEventPair>,
    token_repo: Data<T>,
    req: HttpRequest,
) -> HttpResponse {
    require_permission!(req, "root");

    let pair = pair.into_inner();
    if let Ok(token) = token_repo.get_by_id(pair.token_id).await {
        HttpResponse::Ok().json(
            token_repo
                .is_token_valid_for_event(&token, pair.event_id)
                .await,
        )
    } else {
        HttpResponse::BadRequest().json(APIError::resource_not_found(pair.token_id))
    }
}

async fn auth_token_for_event<T: TokenRepo>(
    token: Json<TokenEventPair>,
    token_repo: Data<T>,
    req: HttpRequest,
) -> HttpResponse {
    require_permission!(req, "root");

    let pair = token.into_inner();
    let result = token_repo.add_event(pair.token_id, pair.event_id).await;

    if result.is_ok() {
        HttpResponse::Ok().json(())
    } else {
        if token_repo.get_by_id(pair.token_id).await.is_ok() {
            HttpResponse::BadRequest().json(APIError::resource_not_found(pair.token_id))
        } else {
            HttpResponse::BadRequest().json(APIError::resource_not_found(pair.event_id))
        }
    }
}

async fn remove_auth_token_for_event<T: TokenRepo>(
    token: Json<TokenEventPair>,
    token_repo: Data<T>,
    req: HttpRequest,
) -> HttpResponse {
    require_permission!(req, "root");

    let pair = token.into_inner();
    let result = token_repo.remove_event(pair.token_id, pair.event_id).await;

    if result.is_ok() {
        HttpResponse::Ok().json(())
    } else {
        if token_repo.get_by_id(pair.token_id).await.is_ok() {
            HttpResponse::BadRequest().json(APIError::resource_not_found(pair.token_id))
        } else {
            HttpResponse::BadRequest().json(APIError::resource_not_found(pair.event_id))
        }
    }
}

async fn view_self(req: HttpRequest) -> HttpResponse {
    let exts = req.extensions();
    let token = exts.get::<Token>().unwrap();
    HttpResponse::Ok().json(token)
}

async fn get_events<T: TokenRepo>(
    token_repo: Data<T>,
    req: HttpRequest,
) -> HttpResponse {
    let token = require_permission!(req, "root");
    handle_result!(token_repo.get_events(assert_some!(token.id)).await)
}
