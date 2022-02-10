//! Executors for running containers

mod docker;

use anyhow::Result;

/// An executor for running a given type of container
#[async_trait::async_trait]
pub trait Executor<T> {
    /// Create a container and return its id
    async fn create_container(&self, container: T) -> Result<String>;

    /// Start a container from a given id
    async fn start_container(&self, container_id: String) -> Result<String>;

    /// Stop a container from a given id
    async fn stop_container(&self, container_id: String) -> Result<String>;
}
