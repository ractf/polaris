//! Configuration for the Polaris API

use crate::config::authentication::AuthConfig;
use serde::{Deserialize, Serialize};

/// Configuration for the Polaris API
#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct APIConfig {
    /// The address and port to bind to
    #[serde(default = "bind")]
    pub bind: String,
    /// Authentication config
    #[serde(default)]
    pub auth: AuthConfig,
}

impl Default for APIConfig {
    fn default() -> Self {
        Self {
            bind: bind(),
            auth: Default::default(),
        }
    }
}

fn bind() -> String {
    String::from("127.0.0.1:8080")
}
