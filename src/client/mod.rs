//! HTTP client for interacting with Polaris

pub mod auth;
mod error;

use crate::client::auth::Profile;
use crate::client::error::{PolarisError, Result};
use crate::data::event::Event;
use crate::data::registry::RegistryToken;
use crate::data::token::{CreateableToken, Token};
use reqwest::header::HeaderMap;
use reqwest::{Client, ClientBuilder, Response};
use serde::de::DeserializeOwned;
use std::fmt::Display;

/// HTTP client for interacting with Polaris
#[derive(Debug, Clone)]
pub struct PolarisClient {
    profile: Profile,
    client: Client,
}

macro_rules! url {
    ($dst:expr, $($arg:tt)*) => {
        format!("{}/api{}", $dst.profile.server, format!($($arg)*))
    }
}

macro_rules! get {
    ($dst:expr, $($arg:tt)*) => {{
        let url = url!($dst, $($arg)*);
        let response = $dst.client.get(url).send().await?;
        $dst.parse_response(response).await
    }}
}

macro_rules! post {
    ($dst:expr, $body:expr, $($arg:tt)*) => {{
        let url = url!($dst, $($arg)*);
        let response = $dst.client.post(url).json($body).send().await?;
        $dst.parse_response(response).await
    }}
}

macro_rules! put {
    ($dst:expr, $body:expr, $($arg:tt)*) => {{
        let url = url!($dst, $($arg)*);
        let response = $dst.client.put(url).json($body).send().await?;
        $dst.parse_response(response).await
    }}
}

macro_rules! delete {
    ($dst:expr, $($arg:tt)*) => {{
        let url = url!($dst, $($arg)*);
        let response = $dst.client.delete(url).send().await?;
        $dst.parse_response(response).await
    }}
}

impl PolarisClient {
    /// Create a new API client
    pub fn new(profile: Profile) -> anyhow::Result<Self> {
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

    async fn parse_response<T: DeserializeOwned>(&self, response: Response) -> Result<T> {
        if response.status().is_success() {
            Ok(response.json().await?)
        } else {
            Err(PolarisError::APIError(response.json().await?))
        }
    }

    /// Create a new event
    pub async fn create_event(&self, event: Event) -> Result<Event> {
        post!(self, &event, "/event")
    }

    /// Get an event from the API
    pub async fn get_event(&self, id: i32) -> Result<Event> {
        get!(self, "/event/{id}")
    }

    /// Get all events from the API
    pub async fn get_events(&self) -> Result<Vec<Event>> {
        get!(self, "/event")
    }

    /// Delete an event
    pub async fn delete_event(&self, id: i32) -> Result<()> {
        delete!(self, "/event/{id}")
    }

    /// Update an existing event
    pub async fn update_event(&self, event: Event) -> Result<Event> {
        put!(self, &event, "/event")
    }

    /// Create a new API token
    pub async fn create_token(&self, token: CreateableToken) -> Result<Token> {
        post!(self, &token, "/token")
    }

    /// Get all tokens
    pub async fn get_tokens(&self) -> Result<Vec<Token>> {
        get!(self, "/token")
    }

    /// Get token by ID
    pub async fn get_token(&self, id: i32) -> Result<Token> {
        get!(self, "/token/{id}")
    }

    /// Get token by name
    pub async fn get_token_by_name<T: Display + AsRef<str>>(&self, name: T) -> Result<Token> {
        get!(self, "/token/name/{name}")
    }

    /// Delete token by ID
    pub async fn delete_token(&self, id: i32) -> Result<()> {
        delete!(self, "/token/{id}")
    }

    /// Delete token by name
    pub async fn delete_token_by_name<T: Display + AsRef<str>>(&self, name: T) -> Result<()> {
        delete!(self, "/token/name/{name}")
    }

    /// Get the current token
    pub async fn whoami(&self) -> Result<Token> {
        delete!(self, "/whoami")
    }

    /// Create a registry token
    pub async fn create_registry_token(&self, token: RegistryToken) -> Result<RegistryToken> {
        post!(self, &token, "/registry_token")
    }

    /// Get all stored registry tokens
    pub async fn get_registry_tokens(&self) -> Result<Vec<RegistryToken>> {
        get!(self, "/registry_token")
    }

    /// Get a registry token by id
    pub async fn get_registry_token(&self, id: i32) -> Result<RegistryToken> {
        get!(self, "/registry_token/{id}")
    }

    /// Delete a registry token by id
    pub async fn delete_registry_token(&self, id: i32) -> Result<RegistryToken> {
        delete!(self, "/registry_token/{id}")
    }
}
