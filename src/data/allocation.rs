use chrono::{DateTime, Utc};
use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Allocation {
    pub id: Option<i32>,
    pub node_id: i32,
    pub start_time: DateTime<Utc>,
    pub end_time: Option<DateTime<Utc>>,
    pub service: bool,
    pub completed: bool,
    pub cpu: i64,
    pub ram: i64,
}
