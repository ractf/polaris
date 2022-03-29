use crate::data::challenge::Challenge;
use crate::repo::challenge::ChallengeRepo;
use crate::{assert_name_not_taken, require_permission, save_object_and_return};
use actix_web::web::{post, Data, Json, ServiceConfig};
use actix_web::{HttpRequest, HttpResponse};
use tracing::info;

pub fn configure_challenge_endpoints<T: 'static + ChallengeRepo>(cfg: &mut ServiceConfig) {
    cfg.route("/", post().to(create_challenge::<T>));
}

async fn create_challenge<T: ChallengeRepo>(
    challenge: Json<Challenge>,
    req: HttpRequest,
    challenge_repo: Data<T>,
) -> HttpResponse {
    require_permission!(req, "root");

    let challenge = challenge.into_inner();

    assert_name_not_taken!(challenge_repo, challenge.name);
    info!("Creating challenge {}.", challenge.name);

    save_object_and_return!(challenge_repo, challenge)
}
