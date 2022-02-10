//! StructOpt commands for using Polaris

mod event;
mod login;
mod run;
mod token;

use crate::client::PolarisClient;
use crate::cmd::login::Login;
use crate::cmd::run::Run;
use crate::cmd::token::Token;
use anyhow::Result;
use structopt::StructOpt;

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
    async fn run(&self, client: PolarisClient) -> Result<()>;
}

/// Dispatch a structopt command from an enum variant
#[macro_export]
macro_rules! dispatch {
    ($y:ident, $dst:expr, $($x:ident),*) => {
        match $dst {
            $($y::$x(cmd) => cmd.run().await?,)*
        }
    };
}

/// Dispatch a structopt command from an enum variant, and select a profile from the command line
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
            if $dst.profile.is_some() {
                eprintln!("Invalid default profile. Please run polaris login.");
            } else {
                eprintln!("Invalid profile specified.");
            }
            std::process::exit(1);
        };
        let client = crate::client::PolarisClient::new(default_profile).expect("Error creating polaris client.");
        match &$dst.subcommand {
            $($y::$x(cmd) => cmd.run(client).await?,)*
        }
    };
}

/// RACTF Polaris
#[derive(StructOpt, Debug)]
pub enum Polaris {
    /// Run Polaris
    Run(Run),
    /// Login to Polaris
    Login(Login),
    /// Manage Polaris tokens
    Token(Token),
}

pub use {api_dispatch, dispatch};
