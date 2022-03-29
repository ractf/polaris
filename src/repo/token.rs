use crate::data::token::Token;
use anyhow::Result;
use std::fmt::Debug;
use crate::data::event::Event;

#[async_trait::async_trait]
pub trait TokenRepo {
    /// Save the token
    async fn save(&self, token: Token) -> Result<Token>;
    /// Get a token with a given id
    async fn get_by_id(&self, id: i32) -> Result<Token>;
    /// Get all existing tokens(this will not include the bootstrap token if enabled)
    async fn get_all(&self) -> Result<Vec<Token>>;
    /// Check if a token's name is taken
    async fn is_name_taken<S: AsRef<str> + Debug + Send>(&self, name: S) -> Result<bool>;
    /// Get a token by Bearer token
    async fn get_by_token(&self, token: String) -> Result<Token>;
    /// Get a token by name
    async fn get_by_name<S: AsRef<str> + Debug + Send>(&self, name: S) -> Result<Token>;
    /// Delete a token with a given id
    async fn delete_by_id(&self, id: i32) -> Result<()>;
    /// Check if a token is allowed to be used for an event
    async fn is_token_valid_for_event(&self, token: &Token, event_id: i32) -> bool;
    /// Allow a token to be used for a given event id
    async fn add_event(&self, token_id: i32, event_id: i32) -> Result<()>;
    /// Disallow a token to be used for a given event id
    async fn remove_event(&self, token_id: i32, event_id: i32) -> Result<()>;
    /// Get all events a token is allowed to be used for
    async fn get_events(&self, token_id: i32) -> Result<Vec<Event>>;
}
