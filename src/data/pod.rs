//! Database models related to pods

use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Pod {
    pub id: Option<i32>,
    pub name: String,
    pub max_cpu: Option<i64>,
    pub max_ram: Option<i64>,
    pub pod: PodType,
}

/// A pod that can be ran by Polaris
#[derive(Debug, Clone, Serialize, Deserialize)]
pub enum PodType {
    /// An OCI Container
    OCIContainer(OCIContainer),
}

/// An OCI Container pod
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct OCIContainer {
    /// The OCI image used by this container
    pub image: String,
    /// The docker registry credentials to use to pull the image
    pub repo_credentials: Option<i32>,
    /// The ports to expose
    pub ports: Vec<ExposedPort>,
}

/// A port exposed by the container
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct ExposedPort {
    /// The internal port number
    pub port: u16,
    /// The protocol(s) to expose the port on
    pub protocol: Protocol,
    /// If the port should be sent to non admin users
    pub advertise: bool,
    /// How the port should be allocated
    pub allocation: PortAllocationStrategy,
}

/// The protocol(s) to expose a port on
#[derive(Debug, Clone, Serialize, Deserialize)]
pub enum Protocol {
    /// Expose the port on TCP
    TCP,
    /// Expose the port on UDP
    UDP,
    /// Expose the port on both TCP and UDP
    Both,
}

/// Stategies for allocating an external port
#[derive(Debug, Clone, Serialize, Deserialize)]
pub enum PortAllocationStrategy {
    /// Randomly generate a port
    Random,
    /// Require the port to be 1 higher than the last port allocated to this pod
    Sequential,
    /// Require a static port. Note this may make scheduling very difficult
    Static(u16),
}
