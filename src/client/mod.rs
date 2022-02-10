//! HTTP client for interacting with Polaris

pub mod auth;

use crate::client::auth::Profile;
use crate::data::event::Event;
use crate::data::token::{CreateableToken, Token};
use anyhow::Result;
use reqwest::header::HeaderMap;
use reqwest::{Client, ClientBuilder};

/// HTTP client for interacting with Polaris
#[derive(Debug, Clone)]
pub struct PolarisClient {
    profile: Profile,
    client: Client,
}

impl PolarisClient {
    /// Create a new API client
    pub fn new(profile: Profile) -> Result<Self> {
        let mut header_map = HeaderMap::new();
        header_map.insert(
            "Authorization",
            format!("Bearer {}", profile.token).parse()?,
        );
        let client = ClientBuilder::new()
            .user_agent("polaris")
            .default_headers(header_map)
            .build()?;
        Ok(Self { profile, client })
    }

    fn make_url<T: ToString>(&self, endpoint: T) -> String {
        if let Some(stripped) = self.profile.server.strip_suffix('/') {
            return format!("{}{}", stripped, endpoint.to_string());
        }
        return format!("{}{}", self.profile.server, endpoint.to_string());
    }

    /// Create a new API token
    pub async fn create_token(&self, token: CreateableToken) -> Result<Token> {
        let url = self.make_url("/token");
        let response = self.client.post(url).json(&token).send().await?;
        return Ok(response.json().await?);
    }

    /// Get all events from the API
    pub async fn get_events(&self) -> Result<Vec<Event>> {
        let url = self.make_url("/event");
        let response = self.client.get(url).send().await?;
        return Ok(response.json().await?);
    }
}
