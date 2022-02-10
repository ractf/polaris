//! Configuration for authentication on the Polaris API

use crate::data::token::Token;
use serde::{Deserialize, Serialize};

/// Configuration for authentication on the Polaris API
#[derive(Serialize, Deserialize, Debug, Clone, Default)]
pub struct AuthConfig {
    /// A list of predefined tokens
    #[serde(default)]
    pub tokens: Vec<Token>,
    /// Hosts that are allowed to use the Polaris API, if this is empty, all hosts will be allowed
    #[serde(default)]
    pub allowed_hosts: Vec<String>,
}
