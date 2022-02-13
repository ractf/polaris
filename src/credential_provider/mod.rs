//! Credential providers for getting Docker registry API credentials

pub mod aws;
pub mod cache;
pub mod default;

use anyhow::Result;
use chrono::{DateTime, Utc};
use serde::{Deserialize, Serialize};

/// Provide credentials for a docker registry
#[async_trait::async_trait]
pub trait DockerCredentialProvider {
    /// Provide credentials for a docker registry
    async fn provide(&self) -> Result<DockerCredentials>;
}

/// Provide credentials and an expiry time for a docker registry
#[async_trait::async_trait]
pub trait CacheableDockerCredentialProvider {
    /// Provide credentials and an expiry time for a docker registry
    async fn provide(&self) -> Result<(DockerCredentials, Option<DateTime<Utc>>)>;
}

/// Credentials for authenticating with a Docker registry, mirrored from `bollard::auth::DockerCredentials`
#[derive(Debug, Clone, Serialize, Deserialize)]
#[allow(missing_docs)]
pub struct DockerCredentials {
    pub username: Option<String>,
    pub password: Option<String>,
    pub auth: Option<String>,
    pub email: Option<String>,
    pub serveraddress: Option<String>,
    pub identitytoken: Option<String>,
    pub registrytoken: Option<String>,
}

impl DockerCredentials {
    pub fn empty() -> Self {
        Self {
            username: None,
            password: None,
            auth: None,
            email: None,
            serveraddress: None,
            identitytoken: None,
            registrytoken: None,
        }
    }
}

impl From<bollard::auth::DockerCredentials> for DockerCredentials {
    fn from(from: bollard::auth::DockerCredentials) -> Self {
        DockerCredentials {
            username: from.username,
            password: from.password,
            auth: from.auth,
            email: from.email,
            serveraddress: from.serveraddress,
            identitytoken: from.identitytoken,
            registrytoken: from.registrytoken,
        }
    }
}

impl From<DockerCredentials> for bollard::auth::DockerCredentials {
    fn from(from: DockerCredentials) -> Self {
        bollard::auth::DockerCredentials {
            username: from.username,
            password: from.password,
            auth: from.auth,
            email: from.email,
            serveraddress: from.serveraddress,
            identitytoken: from.identitytoken,
            registrytoken: from.registrytoken,
        }
    }
}
