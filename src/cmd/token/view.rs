use crate::client::PolarisClient;
use crate::cmd::{output_object, APICommand};
use clap::Parser;

#[derive(Debug, Parser)]
pub struct TokenView {
    /// Token Name
    #[clap(long, required_unless_present = "id")]
    name: Option<String>,

    /// Token ID
    #[clap(long)]
    id: Option<i32>,
}

#[async_trait::async_trait(?Send)]
impl APICommand for TokenView {
    async fn run(&self, client: PolarisClient, json: bool) -> anyhow::Result<()> {
        let token = if let Some(token_name) = &self.name {
            client.get_token_by_name(token_name).await?
        } else {
            let token_id = self.id.unwrap();
            client.get_token(token_id).await?
        };

        output_object(token, json)
    }
}
