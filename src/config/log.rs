//! Logging configuration for Polaris

use serde::{Deserialize, Serialize};

/// Logging configuration for Polaris
#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct LogConfig {
    /// Path to write log files to
    pub path: Option<String>,
    /// The minimum logging level used in Polaris
    #[serde(default = "log_level")]
    pub level: String,
    /// The file name used for Polaris logs
    #[serde(default = "log_file_name")]
    pub file_name: String,
    /// The frequency of log rotation, daily, hourly, minutely or never
    #[serde(default = "rotation")]
    pub rotation: String,
}

impl Default for LogConfig {
    fn default() -> Self {
        Self {
            path: None,
            level: log_level(),
            file_name: log_file_name(),
            rotation: rotation(),
        }
    }
}

fn log_level() -> String {
    String::from("INFO")
}

fn log_file_name() -> String {
    String::from("polaris.log")
}

fn rotation() -> String {
    String::from("never")
}
