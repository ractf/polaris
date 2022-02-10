use crate::data::token::Token;
use actix_web::{get, HttpMessage, HttpRequest, HttpResponse};

#[get("/whoami")]
pub async fn whoami(req: HttpRequest) -> HttpResponse {
    let exts = req.extensions();
    let token = exts.get::<Token>().unwrap();
    HttpResponse::Ok().json(token)
}
