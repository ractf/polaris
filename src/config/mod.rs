//! Configuration used by Polaris

pub mod api;
mod authentication;
pub mod docker;
pub mod log;

use crate::config::api::APIConfig;
use crate::config::docker::DockerConfig;
use crate::config::log::LogConfig;
use serde::{Deserialize, Serialize};

/// Configuration used by Polaris
#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct Config {
    /// Logging configuration
    #[serde(default)]
    pub log: LogConfig,
    /// API Configuration
    #[serde(default)]
    pub api: APIConfig,
    /// The URL of the Postgres database used by Polaris
    #[serde(default = "database_url")]
    pub database_url: String,
    /// Docker configuration
    #[serde(default)]
    pub docker: DockerConfig,
}

fn database_url() -> String {
    if let Ok(x) = std::env::var(String::from("DATABASE_URL")) {
        x
    } else {
        String::from("postgres://localhost/polaris")
    }
}
