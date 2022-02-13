//! Configuration for the docker executor

use crate::credential_provider::DockerCredentials;
use serde::{Deserialize, Serialize};

/// Docker config
#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct DockerConfig {
    /// Is the docker executor enabled? defaults to true
    #[serde(default = "enabled")]
    pub enabled: bool,
    /// The repo to use for unqualified image names, eg `ubuntu`
    #[serde(default = "default_repo")]
    pub default_repo: String,
    /// Default docker credentials
    #[serde(default = "default_credentials")]
    pub default_credentials: DockerCredentials,
    /// Docker API url
    #[serde(default = "docker_url")]
    pub docker_url: String,
    /// Docker API request timeout
    #[serde(default = "docker_timeout")]
    pub docker_timeout: u64,
    /// Docker API major version
    #[serde(default = "docker_api_version_major")]
    pub docker_api_version_major: u16,
    /// Docker API minor version
    #[serde(default = "docker_api_version_minor")]
    pub docker_api_version_minor: u16,
}

fn enabled() -> bool {
    true
}

fn default_repo() -> String {
    String::from("docker.io")
}

fn default_credentials() -> DockerCredentials {
    DockerCredentials::empty()
}

fn docker_url() -> String {
    String::from("unix:///var/run/docker.sock")
}

fn docker_timeout() -> u64 {
    120
}

fn docker_api_version_major() -> u16 {
    1
}

fn docker_api_version_minor() -> u16 {
    40
}

impl Default for DockerConfig {
    fn default() -> Self {
        Self {
            enabled: true,
            default_repo: default_repo(),
            default_credentials: default_credentials(),
            docker_url: docker_url(),
            docker_timeout: docker_timeout(),
            docker_api_version_major: docker_api_version_major(),
            docker_api_version_minor: docker_api_version_minor(),
        }
    }
}
