use crate::client::auth::{Profile, Profiles};
use crate::Command;
use anyhow::Result;
use clap::Parser;
use dialoguer::theme::ColorfulTheme;
use dialoguer::Password;

/// Login to a polaris server
#[derive(Debug, Parser)]
pub struct Login {
    /// Profile name
    #[clap(long, short)]
    profile: String,

    /// API Token
    #[clap(long, short)]
    token: Option<String>,

    /// Overwrite the default token with this token.
    #[clap(long, short)]
    default: bool,

    /// Polaris Server
    #[clap(default_value = "http://127.0.0.1:8080")]
    server: String,
}

#[async_trait::async_trait(?Send)]
impl Command for Login {
    async fn run(&self) -> Result<()> {
        let mut profiles = Profiles::load_or_create()?;
        let mut profile = Profile {
            server: self.server.clone(),
            token: "".to_string(),
        };

        if let Some(token) = &self.token {
            profile.token = token.clone();
        } else {
            let token = Password::with_theme(&ColorfulTheme::default())
                .with_prompt("Token")
                .interact()?;
            profile.token = token;
        }

        profiles.profiles.insert(self.profile.clone(), profile);

        if profiles.default.is_none() || self.default {
            profiles.default = Some(self.profile.clone());
        }

        profiles.save()?;

        Ok(())
    }
}
