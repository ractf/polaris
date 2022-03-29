use crate::data::node::Node;
use anyhow::Result;
use std::fmt::Debug;

#[async_trait::async_trait]
pub trait NodeRepo {
    async fn save(&self, node: Node) -> Result<Node>;
    async fn get_by_id(&self, id: i32) -> Result<Node>;
    async fn get_all(&self) -> Result<Vec<Node>>;
    async fn get_by_name<S: AsRef<str> + Debug + Send>(&self, name: S) -> Result<Node>;
    async fn delete_by_id(&self, id: i32) -> Result<()>;
}
