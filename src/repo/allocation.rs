use anyhow::Result;
use crate::data::allocation::Allocation;

#[async_trait::async_trait]
pub trait AllocationRepo {
    /// Save the allocation
    async fn save(&self, allocation: Allocation) -> Result<Allocation>;
    /// Get an allocation with a given id
    async fn get_by_id(&self, id: i32) -> Result<Allocation>;
    /// Get all existing allocations
    async fn get_all(&self) -> Result<Vec<Allocation>>;
    /// Delete an allocation with a given id
    async fn delete_by_id(&self, id: i32) -> Result<()>;
}