//! Executors for running containers

pub mod docker;

use crate::config::Config;
use crate::executor::docker::DockerExecutor;
use anyhow::Result;
use tracing::warn;

/// An executor for running a given type of container
#[async_trait::async_trait]
pub trait Executor<T, H: ToString> {
    /// Pull files required to create a container
    async fn pull_container(&self, container: &T) -> Result<()>;

    /// Create a container and return its id
    async fn create_container(&self, container: &T) -> Result<H>;

    /// Start a container from a given id
    async fn start_container(&self, container_id: &H) -> Result<()>;

    /// Stop a container from a given id
    async fn stop_container(&self, container_id: &H) -> Result<()>;
}

pub fn setup_executors(config: &Config) {
    if config.docker.enabled {
        if let Ok(docker) = DockerExecutor::new(config) {
        } else {
            warn!("Failed to start docker executor");
        }
    }
}
