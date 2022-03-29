use crate::data::registry::RegistryToken;
use crate::repo::registry::RegistryTokenRepo;
use crate::{
    assert_name_not_taken, delete_object_and_return, get_object_as_response_or_404,
    get_object_or_404, handle_result, require_permission, save_object_and_return,
};
use actix_web::web::{delete, get, post, Data, Json, Path, ServiceConfig};
use actix_web::{HttpRequest, HttpResponse};
use tracing::info;

pub fn configure_registry_token_endpoints<T: 'static + RegistryTokenRepo>(
    config: &mut ServiceConfig,
) {
    config
        .route("/", post().to(create_registry_token::<T>))
        .route("/", get().to(get_registry_tokens::<T>))
        .route("/{id}", get().to(get_registry_token::<T>))
        .route("/{id}", delete().to(delete_registry_token::<T>));
}

pub async fn create_registry_token<T: 'static + RegistryTokenRepo>(
    new_token: Json<RegistryToken>,
    repo: Data<T>,
    req: HttpRequest,
) -> HttpResponse {
    require_permission!(req, "registry_token.create");

    let new_token = new_token.into_inner();

    assert_name_not_taken!(repo, new_token.name);

    info!("Creating registry token {}.", new_token.name);
    save_object_and_return!(repo, new_token)
}

//TODO: I'm not sure the best way of handling access control for these routes, for now its root until I find a less strict solution that works
pub async fn get_registry_tokens<T: 'static + RegistryTokenRepo>(
    repo: Data<T>,
    req: HttpRequest,
) -> HttpResponse {
    require_permission!(req, "root");
    handle_result!(repo.get_all().await)
}

pub async fn get_registry_token<T: 'static + RegistryTokenRepo>(
    id: Path<i32>,
    repo: Data<T>,
    req: HttpRequest,
) -> HttpResponse {
    require_permission!(req, "root");
    let token_id = id.into_inner();
    get_object_as_response_or_404!(repo, token_id)
}

pub async fn delete_registry_token<T: 'static + RegistryTokenRepo>(
    id: Path<i32>,
    repo: Data<T>,
    req: HttpRequest,
) -> HttpResponse {
    require_permission!(req, "registry_token.delete");
    let token_id = id.into_inner();
    get_object_or_404!(repo, token_id);
    delete_object_and_return!(repo, token_id)
}
