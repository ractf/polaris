//! Profile system to manage authenticating with Polaris from the CLI

use anyhow::Result;
use serde::{Deserialize, Serialize};
use std::collections::HashMap;
use std::fs;
use std::io::ErrorKind;

/// Profiles for authenticating with a Polaris server
#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct Profiles {
    /// All profiles that can be used
    pub profiles: HashMap<String, Profile>,
    /// The default profile
    pub default: Option<String>,
}

/// A profile for connecting to a Polaris server with an API token
#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct Profile {
    /// The address of the server to connect to
    pub server: String,
    /// The Bearer token to use
    pub token: String,
}

impl Profiles {
    /// Load profiles.toml from the user's config directory or create it if it doesn't exist
    pub fn load_or_create() -> Result<Self> {
        let mut path = dirs::config_dir().unwrap();
        path.push("polaris");
        if !path.exists() {
            fs::create_dir(&path)?;
        }

        path.push("profiles.json");
        let string = fs::read_to_string(&path);
        return match string {
            Ok(s) => Ok(serde_json::from_str(s.as_str())?),
            Err(e) => match e.kind() {
                ErrorKind::PermissionDenied => Err(anyhow::Error::from(e)),
                _ => {
                    println!("aaa");
                    let profiles = Self {
                        profiles: Default::default(),
                        default: None,
                    };
                    profiles.save()?;
                    Ok(profiles)
                }
            },
        };
    }

    /// Save profiles.toml to the user's config directory
    pub fn save(&self) -> Result<()> {
        let mut path = dirs::config_dir().unwrap();
        path.push("polaris");
        if !path.exists() {
            fs::create_dir(&path)?;
        }

        path.push("profiles.json");
        fs::write(&path, serde_json::to_string(self)?)?;
        Ok(())
    }
}
