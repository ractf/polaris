use crate::api::start_api;
use crate::cmd::Command;
use crate::config::Config;
use crate::data::token::Token;
use crate::repo::postgres::challenge::PostgresChallengeRepo;
use crate::repo::postgres::event::PostgresEventRepo;
use crate::repo::postgres::node::PostgresNodeRepo;
use crate::repo::postgres::registry::PostgresRegistryTokenRepo;
use crate::repo::postgres::token::PostgresTokenRepo;
use crate::sync::start_node_sync;
use anyhow::{bail, Result};
use clap::Parser;
use sqlx::postgres::PgPoolOptions;
use std::io;
use std::mem::ManuallyDrop;
use std::path::PathBuf;
use std::str::FromStr;
use tracing::{error, Level};
use tracing::{info, warn};
use tracing_appender::rolling::{RollingFileAppender, Rotation};
use tracing_subscriber::fmt::format::FmtSpan;
use tracing_subscriber::fmt::writer::MakeWriterExt;
use tracing_subscriber::{fmt, layer::SubscriberExt};

/// Arguments for running polaris
#[derive(Debug, Parser)]
pub struct Run {
    /// Location of polaris config file
    #[clap(parse(from_os_str), default_value = "/etc/polaris.toml")]
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

        let pool = PgPoolOptions::new()
            .max_connections(5)
            .connect(&config.database_url)
            .await?;
        sqlx::migrate!().run(&pool).await?;

        if Token::get_all(&pool).await?.is_empty() && config.api.auth.bootstrap_token.is_none() {
            error!("There are no API tokens in the database and no bootstrap token is set, the API will be inaccessible.");
        }

        if &config.node.public_ip == "" {
            warn!("No public IP set in config.");
        }

        info!("Starting Polaris.");

        let token_repo = PostgresTokenRepo::new(&pool);
        let registry_repo = PostgresRegistryTokenRepo::new(&pool);
        let event_repo = PostgresEventRepo::new(&pool);
        let node_repo = PostgresNodeRepo::new(&pool);
        let challenge_repo = PostgresChallengeRepo::new(&pool);

        start_node_sync(node_repo.clone(), &config.node, config.api.bind.clone()).await;
        start_api(
            config,
            token_repo,
            registry_repo,
            event_repo,
            node_repo,
            challenge_repo,
        )
        .await?;

        Ok(())
    }
}

fn setup_logging(config: &Config) -> Result<()> {
    let stdout_layer = fmt::Layer::new()
        .with_span_events(FmtSpan::FULL)
        .with_writer(io::stdout.with_max_level(Level::from_str(&config.log.level)?));

    println!("{:?}", config);

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

fn log_rotation_from_string<T: AsRef<str>>(rotation: T) -> Result<Rotation> {
    match rotation.as_ref().to_ascii_lowercase().as_str() {
        "daily" => Ok(Rotation::DAILY),
        "hourly" => Ok(Rotation::HOURLY),
        "minutely" => Ok(Rotation::MINUTELY),
        "never" => Ok(Rotation::NEVER),
        _ => bail!("Invalid log rotation {}", rotation.as_ref()),
    }
}
