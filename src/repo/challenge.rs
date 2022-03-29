use crate::data::challenge::Challenge;
use anyhow::Result;
use std::fmt::Debug;

#[async_trait::async_trait]
pub trait ChallengeRepo {
    async fn save(&self, event: Challenge) -> Result<Challenge>;
    async fn get_by_id(&self, id: i32) -> Result<Challenge>;
    async fn get_all(&self) -> Result<Vec<Challenge>>;
    async fn is_name_taken<S: AsRef<str> + Debug + Send>(&self, name: S) -> Result<bool>;
    async fn get_by_name<S: AsRef<str> + Debug + Send>(&self, name: S) -> Result<Challenge>;
    async fn delete_by_id(&self, id: i32) -> Result<()>;
}
