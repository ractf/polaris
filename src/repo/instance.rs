use anyhow::Result;
use crate::data::instance::ChallengeInstance;

#[async_trait::async_trait]
pub trait InstanceRepo {
    async fn save(&self, instance: ChallengeInstance) -> Result<ChallengeInstance>;
    async fn get_by_id(&self, id: i32) -> Result<ChallengeInstance>;
    async fn get_all(&self) -> Result<Vec<ChallengeInstance>>;
    async fn delete_by_id(&self, id: i32) -> Result<()>;
}
