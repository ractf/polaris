use crate::client::PolarisClient;
use crate::cmd::{deleted, APICommand};
use clap::Parser;

#[derive(Debug, Parser)]
pub struct TokenDelete {
    /// Token Name
    #[clap(long, required_unless_present = "id")]
    name: Option<String>,

    /// Token ID
    #[clap(long)]
    id: Option<i32>,
}

#[async_trait::async_trait(?Send)]
impl APICommand for TokenDelete {
    async fn run(&self, client: PolarisClient, json: bool) -> anyhow::Result<()> {
        let token_id = if let Some(token_name) = &self.name {
            let token = client.get_token_by_name(token_name).await?;
            token.id.expect("Token has no ID?")
        } else {
            self.id.unwrap()
        };
        client.delete_token(token_id).await?;
        deleted("Token", json)
    }
}
