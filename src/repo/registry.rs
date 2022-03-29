use crate::data::registry::RegistryToken;
use anyhow::Result;
use std::fmt::Debug;

#[async_trait::async_trait]
pub trait RegistryTokenRepo {
    /// Save the registry token
    async fn save(&self, token: RegistryToken) -> Result<RegistryToken>;
    /// Get a registry token with a given id
    async fn get_by_id(&self, id: i32) -> Result<RegistryToken>;
    /// Get all existing registry tokens
    async fn get_all(&self) -> Result<Vec<RegistryToken>>;
    /// Check if a token's name is taken
    async fn is_name_taken<S: AsRef<str> + Debug + Send>(&self, name: S) -> Result<bool>;
    /// Delete a token with a given id
    async fn delete_by_id(&self, id: i32) -> Result<()>;
    /// Check if a token is allowed to be used for an event
    async fn is_token_valid_for_event(&self, token: &RegistryToken, event_id: i32) -> bool;
    /// Allow a token to be used for a given event id
    async fn add_event(&self, token_id: i32, event_id: i32) -> Result<()>;
    /// Disallow a token to be used for a given event id
    async fn remove_event(&self, token_id: i32, event_id: i32) -> Result<()>;
}
