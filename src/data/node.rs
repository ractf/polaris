//! Database models for representing a Polaris node

use chrono::{DateTime, Utc};
use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Node {
    pub id: Option<i32>,
    pub hostname: String,
    pub total_ram: i64,
    pub total_cpu: i64,
    pub available_ram: i64,
    pub available_cpu: i64,
    pub schedulable: bool,
    pub version: String,
    pub kernel_version: String,
    pub public_ip: String,
    pub bind_address: String,
    pub last_updated: DateTime<Utc>,
}
