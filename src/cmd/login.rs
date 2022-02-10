use crate::client::auth::{Profile, Profiles};
use crate::Command;
use anyhow::Result;
use dialoguer::theme::ColorfulTheme;
use dialoguer::Password;
use structopt::StructOpt;

/// Login to a polaris server
#[derive(Debug, StructOpt)]
pub struct Login {
    #[structopt(long, help = "Profile name")]
    profile: String,
    #[structopt(long, short, help = "API Token")]
    token: Option<String>,
    #[structopt(long, short, help = "Overwrite the default token with this token.")]
    default: bool,
    #[structopt(help = "Polaris Server", default_value = "http://127.0.0.1:8080")]
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
