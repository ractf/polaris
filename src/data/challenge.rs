//! Database models related to challenges

use crate::data::pod::Pod;
use serde::{Deserialize, Serialize};
use std::collections::HashMap;

/// A challenge ran by Polaris
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Challenge {
    /// The database id
    pub id: Option<i32>,
    pub name: String,
    pub event: i32,
    /// The pods required to run the challenge
    pub pods: Vec<Pod>,
    pub replication: Option<ReplicationStrategy>,
    pub allocation: Option<Allocation>,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub enum ReplicationStrategy {
    Static(i32),
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Allocation {
    pub id: Option<i32>,
    pub limits: HashMap<String, i32>,
    pub strict_limits: bool,
    pub cache_allocations: Option<String>,
}
