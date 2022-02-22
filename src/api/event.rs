use actix_web::web::{Data, Json, Path};
use actix_web::{delete, get, post, put, HttpMessage, HttpRequest, HttpResponse};
use tracing::{error, info};

use crate::api::error::APIError;
use crate::api::AppState;
use crate::data::event::Event;
use crate::data::token::Token;
use crate::{handle_result, require_permission};

#[post("/event")]
pub async fn create_event(
    event: Json<Event>,
    state: Data<AppState>,
    req: HttpRequest,
) -> HttpResponse {
    let token = require_permission!(req, "event.create");

    let mut event = event.into_inner();

    if event.id.is_some() {
        info!("Event submitted with set id, rejecting.");
        return HttpResponse::BadRequest().json(APIError::invalid_field("id"));
    }

    if let Ok(true) = Event::is_name_taken(&state.pool, &event.name).await {
        info!("Event submitted with in use name, rejecting.");
        return HttpResponse::BadRequest().json(APIError::name_taken(event.name));
    }

    info!("Creating event {}.", event.name);
    let result = event.save(&state.pool).await;
    if let Err(e) = result {
        error!("{:?}", e);
        return HttpResponse::InternalServerError().json(APIError::DatabaseError);
    }

    if let Some(id) = event.id {
        let result = token.add_event(&state.pool, id).await;
        if result.is_err() {
            error!("Failed to add event {} to token {}", id, token.name);
        }
    }

    HttpResponse::Ok().json(event)
}

#[get("/event/{id}")]
pub async fn get_event(event: Path<i32>, state: Data<AppState>, req: HttpRequest) -> HttpResponse {
    let token = require_permission!(req, "event.view");
    let event_id = event.into_inner();

    if !token.is_valid_for_event(&state.pool, event_id).await {
        return HttpResponse::NotFound().json(APIError::event_not_found(event_id));
    }

    let event = Event::get(&state.pool, event_id).await;

    if let Ok(mut event) = event {
        if !token.has_permission("event.view.sensitive") {
            event.api_token = None
        }
        HttpResponse::Ok().json(event)
    } else {
        error!("{:?}", event);
        HttpResponse::NotFound().json(APIError::event_not_found(event_id))
    }
}

#[get("/event/name/{name}")]
pub async fn get_event_by_name(
    event_name: Path<String>,
    state: Data<AppState>,
    req: HttpRequest,
) -> HttpResponse {
    let token = require_permission!(req, "event.view");
    let event_name = event_name.into_inner();

    let event = Event::get_by_name(&state.pool, &event_name).await;

    if let Ok(mut event) = event {
        if let Some(event_id) = event.id {
            if !token.is_valid_for_event(&state.pool, event_id).await {
                return HttpResponse::NotFound().json(APIError::event_not_found(event_id));
            }
        } else {
            return HttpResponse::NotFound().json(APIError::event_name_not_found(event_name));
        }

        if !token.has_permission("event.view.sensitive") {
            event.api_token = None
        }
        HttpResponse::Ok().json(event)
    } else {
        error!("{:?}", event);
        HttpResponse::NotFound().json(APIError::event_name_not_found(event_name))
    }
}

#[get("/event")]
pub async fn get_events(state: Data<AppState>, req: HttpRequest) -> HttpResponse {
    let token = require_permission!(req, "event.view.all");

    let events = Event::get_all(&state.pool).await;

    if let Ok(mut events) = events {
        if !token.has_permission("event.view.sensitive") {
            for event in events.iter_mut() {
                event.api_token = None;
            }
        }
        HttpResponse::Ok().json(events)
    } else {
        HttpResponse::InternalServerError().json(APIError::DatabaseError)
    }
}

#[delete("/event/{id}")]
pub async fn delete_event(
    event: Path<i32>,
    state: Data<AppState>,
    req: HttpRequest,
) -> HttpResponse {
    let token = require_permission!(req, "event.delete");

    let event_id = event.into_inner();
    if !token.is_valid_for_event(&state.pool, event_id).await {
        return HttpResponse::NotFound().json(APIError::event_not_found(event_id));
    }

    let event_exists = Event::id_exists(&state.pool, event_id).await;
    if !matches!(event_exists, Ok(true)) {
        return HttpResponse::NotFound().json(APIError::event_not_found(event_id));
    }

    handle_result!(Event::delete_id(&state.pool, event_id).await)
}

#[put("/event")]
pub async fn update_event(
    event: Json<Event>,
    state: Data<AppState>,
    req: HttpRequest,
) -> HttpResponse {
    let token = require_permission!(req, "event.update");

    let mut event = event.into_inner();

    if event.id.is_none() {
        return HttpResponse::BadRequest().json(APIError::invalid_field("id"));
    }
    let event_id = event.id.unwrap();

    if !token.is_valid_for_event(&state.pool, event_id).await {
        return HttpResponse::NotFound().json(APIError::event_not_found(event_id));
    }

    info!("Updating event {}.", event.name);
    handle_result!(event.save(&state.pool).await.map(|_| event))
}

#[get("/event/{id}/tokens")]
pub async fn list_tokens_valid_for_event(
    id: Path<i32>,
    state: Data<AppState>,
    req: HttpRequest,
) -> HttpResponse {
    require_permission!(req, "root");
    let event_id = id.into_inner();
    handle_result!(Event::get_all_valid_tokens(&state.pool, event_id).await)
}
