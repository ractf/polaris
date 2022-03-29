use crate::data::event::Event;
use anyhow::Result;
use std::fmt::Debug;
use crate::data::token::Token;

#[async_trait::async_trait]
pub trait EventRepo {
    async fn save(&self, event: Event) -> Result<Event>;
    async fn get_by_id(&self, id: i32) -> Result<Event>;
    async fn get_all(&self) -> Result<Vec<Event>>;
    async fn is_name_taken<S: AsRef<str> + Debug + Send>(&self, name: S) -> Result<bool>;
    async fn get_by_name<S: AsRef<str> + Debug + Send>(&self, name: S) -> Result<Event>;
    async fn delete_by_id(&self, id: i32) -> Result<()>;
    async fn get_all_valid_tokens(&self, id: i32) -> Result<Vec<Token>>;
}
