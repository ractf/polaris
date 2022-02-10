//! # RACTF Polaris

//#![deny(missing_docs)]

use crate::cmd::{Command, Polaris};
use anyhow::Result;
use structopt::StructOpt;

pub mod api;
pub mod client;
pub mod cmd;
pub mod config;
pub mod credential_provider;
pub mod data;
pub mod executor;
pub mod notification;

#[actix_web::main]
async fn main() -> Result<()> {
    let polaris: Polaris = Polaris::from_args();
    cmd::dispatch!(Polaris, polaris, Run, Login, Token);
    Ok(())
}
