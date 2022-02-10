//! Database models related to pods

/// A pod that can be ran by Polaris
#[derive(Debug)]
pub enum Pod {
    /// An OCI Container
    OCIContainer(OCIContainer),
}

/// An OCI Container pod
#[derive(Debug)]
pub struct OCIContainer {
    /// The OCI image used by this container
    pub image: String,
    /// The docker registry credentials to use to pull the image
    pub repo_credentials: Option<i32>,
    /// The ports to expose
    pub ports: Vec<ExposedPort>,
    /// The resource limits to apply
    pub resource_limits: Vec<ResourceLimit>,
}

/// A port exposed by the container
#[derive(Debug)]
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
#[derive(Debug)]
pub enum Protocol {
    /// Expose the port on TCP
    TCP,
    /// Expose the port on UDP
    UDP,
    /// Expose the port on both TCP and UDP
    Both,
}

/// Stategies for allocating an external port
#[derive(Debug)]
pub enum PortAllocationStrategy {
    /// Randomly generate a port
    Random,
    /// Require the port to be 1 higher than the last port allocated to this pod
    Sequential,
    /// Require a static port. Note this may make scheduling very difficult
    Static(u16),
}

/// A resource usage limit for a container
#[derive(Debug)]
pub struct ResourceLimit {
    /// The resource to limit
    pub resource: Resource,
    /// The amount of the resource the container can use
    pub limit: u64,
}

/// A resource that can be limited
#[derive(Debug)]
pub enum Resource {
    /// CPU
    CPU,
    /// RAM
    RAM,
}
