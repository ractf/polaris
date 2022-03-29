use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Debug, Clone, Default)]
pub struct NodeConfig {
    pub hostname: Option<String>,
    pub total_ram: Option<u64>,
    pub total_cpu: Option<u32>,
    pub schedulable: Option<bool>,
    pub version: Option<String>,
    pub kernel_version: Option<String>,
    pub public_ip: String,
}
