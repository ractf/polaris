//! # RACTF Polaris

//#![deny(missing_docs)]
#![deny(unsafe_code)]

use crate::cmd::{Command, Polaris};
use anyhow::Result;
use clap::Parser;
use serde_json::json;

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
    let polaris: Polaris = Polaris::parse();
    if let Err(e) = cmd::dispatch!(Polaris, polaris, Run, Login, Token, Event) {
        let json = std::env::args().any(|s| s == "--json");
        if json {
            let json = json! ({
                "error": format!("{e:?}").trim(),
            });
            println!("{}", serde_json::to_string(&json)?);
        } else {
            return Err(e);
        }
    }

    Ok(())
}