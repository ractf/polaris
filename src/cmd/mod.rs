//! StructOpt commands for using Polaris

mod event;
mod login;
mod run;
mod token;

use crate::client::PolarisClient;
use crate::cmd::event::Event;
use crate::cmd::login::Login;
use crate::cmd::run::Run;
use crate::cmd::token::Token;
use anyhow::Result;
use clap::Parser;
use serde::Serialize;
use std::fmt::Display;
use serde_json::json;

/// Trait implemented by all subcommands of `polaris` to define a common interface
#[async_trait::async_trait(?Send)]
pub trait Command {
    /// Run the subcommand
    async fn run(&self) -> Result<()>;
}

/// Trait implemented by all subcommands of `polaris` to define a common interface
#[async_trait::async_trait(?Send)]
pub trait APICommand {
    /// Run the subcommand
    async fn run(&self, client: PolarisClient, json: bool) -> Result<()>;
}

/// Dispatch a clap command from an enum variant
#[macro_export]
macro_rules! dispatch {
    ($y:ident, $dst:expr, $($x:ident),*) => {
        match $dst {
            $($y::$x(cmd) => cmd.run().await,)*
        }
    };
}

/// Dispatch a clap command from an enum variant, and select a profile from the command line
#[macro_export]
macro_rules! api_dispatch {
    ($y:ident, $dst:expr, $($x:ident),*) => {
        use crate::cmd::APICommand;
        let profiles = crate::client::auth::Profiles::load_or_create().unwrap();
        let profile = if let Some(profile) = &$dst.profile {
            profile.clone()
        } else {
            if let Some(profile) = &profiles.default {
                profile.clone()
            } else {
                eprintln!("No profile specified and no default profile set. Please run polaris login.");
                std::process::exit(1);
            }
        };
        let default_profile = if let Some(profile) = profiles.profiles.get(&profile) {
            profile.clone()
        } else {
            if $dst.profile.is_none() {
                eprintln!("Invalid default profile. Please run polaris login.");
            } else {
                eprintln!("Invalid profile specified.");
            }
            std::process::exit(1);
        };
        let client = crate::client::PolarisClient::new(default_profile).expect("Error creating polaris client.");
        match &$dst.subcommand {
            $($y::$x(cmd) => cmd.run(client, $dst.json).await?,)*
        }
    };
}

pub fn output_object<T: Serialize + Display>(obj: T, json: bool) -> Result<()> {
    if json {
        println!("{}", serde_json::to_string(&obj)?);
    } else {
        println!("{:#}", obj);
    }

    Ok(())
}

pub fn output_objects<T: Serialize + Display>(objs: Vec<T>, json: bool) -> Result<()> {
    if json {
        println!("{}", serde_json::to_string(&objs)?);
    } else {
        for obj in objs {
            println!("{}", obj);
        }
    }

    Ok(())
}

pub fn deleted<T: Display>(name: T, json: bool) -> Result<()> {
    if json {
        let val = json!({
            "message": format!("{name} Deleted."),
        });
        println!("{}", serde_json::to_string(&val)?);
    } else {
        println!("{name} Deleted.")
    }

    Ok(())
}

/// RACTF Polaris
#[derive(Parser, Debug)]
pub enum Polaris {
    /// Run Polaris
    Run(Run),
    /// Login to Polaris
    Login(Login),
    /// Manage Polaris tokens
    Token(Token),
    /// Manage Polaris events
    Event(Event),
}

pub use {api_dispatch, dispatch};
