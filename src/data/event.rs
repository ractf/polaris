//! Database models related to events

use crate::data::sep;
use serde::{Deserialize, Serialize};
use sqlx::types::chrono::{DateTime, Utc};
use std::fmt;
use std::fmt::Formatter;

/// Database model for an Event
#[derive(Debug, Deserialize, Serialize, Clone)]
pub struct Event {
    /// The database id
    pub id: Option<i32>,
    /// The name of the event
    pub name: String,
    /// When the event starts
    pub start_time: Option<DateTime<Utc>>,
    /// When the event ends
    pub end_time: Option<DateTime<Utc>>,
    /// The max CPU time allocated for this event's challenges
    pub max_cpu: Option<i64>,
    /// The max RAM allocated for this event's challenges (in bytes)
    pub max_ram: Option<i64>,
    /// The URL of this event's instance of RACTF Core
    pub api_url: Option<String>,
    /// An API token for interacting with this event's instance of RACTF Core
    pub api_token: Option<String>,
}

impl Event {

}

impl fmt::Display for Event {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        write!(f, "Event `{}`", self.name)?;

        if let Some(start_time) = self.start_time {
            sep(f)?;
            write!(f, "starting on: {}", start_time.to_rfc2822())?;
        }

        if let Some(end_time) = self.end_time {
            sep(f)?;
            write!(f, "ending on: {}", end_time.to_rfc2822())?;
        }

        if let Some(max_cpu) = self.max_cpu {
            sep(f)?;
            write!(f, "max CPU: {}", max_cpu)?;
        }

        if let Some(max_ram) = self.max_ram {
            sep(f)?;
            write!(f, "max RAM: {}", max_ram)?;
        }

        if let Some(api_url) = &self.api_url {
            sep(f)?;
            write!(f, "API URL: {}", api_url)?;
        }

        if let Some(api_token) = &self.api_token {
            sep(f)?;
            write!(f, "API Token: {}", api_token)?;
        }

        Ok(())
    }
}
