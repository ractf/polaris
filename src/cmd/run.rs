use crate::api::start_api;
use crate::cmd::Command;
use crate::config::Config;
use anyhow::{bail, Result};
use sqlx::postgres::PgPoolOptions;
use std::io;
use std::mem::ManuallyDrop;
use std::path::PathBuf;
use std::process::exit;
use std::str::FromStr;
use structopt::StructOpt;
use tracing::{error, Level};
use tracing::{info, warn};
use tracing_appender::rolling::{RollingFileAppender, Rotation};
use tracing_subscriber::fmt::format::FmtSpan;
use tracing_subscriber::fmt::writer::MakeWriterExt;
use tracing_subscriber::{fmt, layer::SubscriberExt};
use crate::credential_provider::aws::AWSCredentialProvider;
use crate::credential_provider::cache::CachingDockerCredentialProvider;
use crate::credential_provider::{CacheableDockerCredentialProvider, DockerCredentialProvider};

/// Arguments for running polaris
#[derive(Debug, StructOpt)]
pub struct Run {
    #[structopt(
        parse(from_os_str),
        default_value = "/etc/polaris.toml",
        help = "Location of polaris config file"
    )]
    config: PathBuf,
}

#[async_trait::async_trait(?Send)]
impl Command for Run {
    async fn run(&self) -> Result<()> {
        let config_string = std::fs::read_to_string(&self.config)
            .unwrap_or_else(|_| panic!("Could not read config file {:?}.", &self.config));

        let config: Config = if self.config.to_string_lossy().ends_with(".toml") {
            toml::from_str(&config_string).expect("Could not parse TOML config file.")
        } else {
            serde_json::from_str(&config_string).expect("Could not parse JSON config file.")
        };

        setup_logging(&config)?;

        if config.api.auth.tokens.is_empty() {
            warn!("No tokens are defined!");
        }

        let mut usable_tokens = 0;
        for token in &config.api.auth.tokens {
            if token.token.is_empty() {
                warn!("Token \"{}\" is empty and will not be usable.", token.name);
            } else {
                usable_tokens += 1;
            }
        }
        if usable_tokens == 0 {
            error!(
                "There are no usable tokens, please setup a token in {:?}",
                &self.config
            );
            exit(0);
        }

        let pool = PgPoolOptions::new()
            .max_connections(5)
            .connect(&config.database_url)
            .await?;
        sqlx::migrate!().run(&pool).await?;

        info!("Starting Polaris.");

        let provider = AWSCredentialProvider::new(String::from("AKIAZFVPOYEPLRI5FFEY"), String::from("Z70RYQ0Iz/OcNszneBkrbZyLWxC0ge2B4O0/VEGe"), String::from("eu-west-2"));
        let provider = CachingDockerCredentialProvider::new(Box::new(provider));
        let x = provider.provide().await?;
        let x = provider.provide().await?;
        let x = provider.provide().await?;
        let x = provider.provide().await?;
        println!("{:?}", x);

        start_api(&config, &pool).await?;

        Ok(())
    }
}

fn setup_logging(config: &Config) -> Result<()> {
    let stdout_layer = fmt::Layer::new()
        .with_span_events(FmtSpan::FULL)
        .with_writer(io::stdout.with_min_level(Level::from_str(&config.log.level)?));

    if let Some(log_path) = &config.log.path {
        let file_appender = RollingFileAppender::new(
            log_rotation_from_string(&config.log.rotation)?,
            log_path,
            &config.log.file_name,
        );
        let (non_blocking, guard) = tracing_appender::non_blocking(file_appender);
        let _ = ManuallyDrop::new(guard);

        let subscriber = tracing_subscriber::registry().with(stdout_layer).with(
            fmt::Layer::new()
                .json()
                .with_span_events(FmtSpan::FULL)
                .with_writer(non_blocking),
        );

        tracing::subscriber::set_global_default(subscriber)
            .expect("Unable to set a global subscriber");
    } else {
        let subscriber = tracing_subscriber::registry().with(stdout_layer);

        tracing::subscriber::set_global_default(subscriber)
            .expect("Unable to set a global subscriber");
    }
    Ok(())
}

fn log_rotation_from_string(rotation: &String) -> Result<Rotation> {
    match rotation.to_ascii_lowercase().as_str() {
        "daily" => Ok(Rotation::DAILY),
        "hourly" => Ok(Rotation::HOURLY),
        "minutely" => Ok(Rotation::MINUTELY),
        "never" => Ok(Rotation::NEVER),
        _ => bail!("Invalid log rotation {}", rotation),
    }
}
