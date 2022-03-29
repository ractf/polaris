use actix_web::http::StatusCode;
use actix_web::web::{delete, get, post, put, Data, Json, Path, ServiceConfig};
use actix_web::{HttpRequest, HttpResponse};
use tracing::{error, info};

use crate::api::error::APIError;
use crate::data::event::Event;
use crate::repo::event::EventRepo;
use crate::repo::token::TokenRepo;
use crate::{
    assert_name_not_taken, assert_ok, assert_some, assert_token_valid_for_event,
    get_object_by_name_or_404, get_object_or_404, handle_result, require_permission,
    save_object_and_return,
};

pub fn configure_event_endpoints<T: 'static + EventRepo, R: 'static + TokenRepo>(
    cfg: &mut ServiceConfig,
) {
    cfg.route("/", post().to(create_event::<T, R>))
        .route("/{id}", get().to(get_event::<T, R>))
        .route("/name/{name}", get().to(get_event_by_name::<T, R>))
        .route("/", get().to(get_events::<T>))
        .route("/{id}", delete().to(delete_event::<T, R>))
        .route("/{id}", put().to(update_event::<T, R>))
        .route("/{id}/tokens", get().to(get_valid_tokens::<T>));
}

pub async fn create_event<T: 'static + EventRepo, R: 'static + TokenRepo>(
    event: Json<Event>,
    event_repo: Data<T>,
    token_repo: Data<R>,
    req: HttpRequest,
) -> HttpResponse {
    let token = require_permission!(req, "event.create");

    let event = event.into_inner();

    assert_name_not_taken!(event_repo, event.name);

    info!("Creating event {}.", event.name);
    let saved_event = assert_ok!(event_repo.save(event).await);

    let event_id = assert_some!(saved_event.id);
    if let Some(token_id) = token.id {
        // We don't error on token not having an id because the bootstrap token exists
        let result = token_repo.add_event(token_id, event_id).await;
        if result.is_err() {
            error!("Failed to add event {} to token {}", event_id, token.name);
        }
    }

    HttpResponse::Ok().json(saved_event)
}

pub async fn get_event<T: 'static + EventRepo, R: 'static + TokenRepo>(
    event: Path<i32>,
    event_repo: Data<T>,
    token_repo: Data<R>,
    req: HttpRequest,
) -> HttpResponse {
    let token = require_permission!(req, "event.view");
    let event_id = event.into_inner();

    assert_token_valid_for_event!(token_repo, token, event_id);

    let mut event = get_object_or_404!(event_repo, event_id);

    if !token.has_permission("event.view.sensitive") {
        event.api_token = None
    }
    HttpResponse::Ok().json(event)
}

pub async fn get_event_by_name<T: 'static + EventRepo, R: 'static + TokenRepo>(
    event_name: Path<String>,
    event_repo: Data<T>,
    token_repo: Data<R>,
    req: HttpRequest,
) -> HttpResponse {
    let token = require_permission!(req, "event.view");
    let event_name = event_name.into_inner();

    let mut event = get_object_by_name_or_404!(event_repo, event_name);
    let event_id = assert_some!(event.id);
    assert_token_valid_for_event!(token_repo, token, event_id);

    if !token.has_permission("event.view.sensitive") {
        event.api_token = None
    }
    HttpResponse::Ok().json(event)
}

pub async fn get_events<T: 'static + EventRepo>(
    event_repo: Data<T>,
    req: HttpRequest,
) -> HttpResponse {
    let token = require_permission!(req, "event.view.all");

    let mut events = assert_ok!(event_repo.get_all().await);

    if !token.has_permission("event.view.sensitive") {
        for event in events.iter_mut() {
            event.api_token = None;
        }
    }
    HttpResponse::Ok().json(events)
}

pub async fn delete_event<T: 'static + EventRepo, R: 'static + TokenRepo>(
    event: Path<i32>,
    event_repo: Data<T>,
    token_repo: Data<R>,
    req: HttpRequest,
) -> HttpResponse {
    let token = require_permission!(req, "event.delete");
    let event_id = event.into_inner();
    assert_token_valid_for_event!(token_repo, token, event_id);
    get_object_or_404!(event_repo, event_id);
    handle_result!(event_repo.delete_by_id(event_id).await)
}

pub async fn update_event<T: 'static + EventRepo, R: 'static + TokenRepo>(
    event: Json<Event>,
    event_repo: Data<T>,
    token_repo: Data<R>,
    req: HttpRequest,
) -> HttpResponse {
    let token = require_permission!(req, "event.update");

    let event = event.into_inner();
    let event_id = assert_some!(
        event.id,
        StatusCode::BAD_REQUEST,
        APIError::invalid_field("id")
    );
    assert_token_valid_for_event!(token_repo, token, event_id);

    info!("Updating event {}.", event.name);
    save_object_and_return!(event_repo, event)
}

pub async fn get_valid_tokens<T: 'static + EventRepo>(
    id: Path<i32>,
    event_repo: Data<T>,
    req: HttpRequest,
) -> HttpResponse {
    require_permission!(req, "root");
    let event_id = id.into_inner();
    handle_result!(event_repo.get_all_valid_tokens(event_id).await)
}
