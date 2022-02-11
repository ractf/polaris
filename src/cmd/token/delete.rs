use crate::client::PolarisClient;
use crate::cmd::APICommand;
use clap::Parser;

#[derive(Debug, Parser)]
pub struct TokenDelete {
    #[clap(short, long, help = "Token Name", required_unless_present = "id")]
    name: Option<String>,

    #[clap(short, long, help = "Token ID")]
    id: Option<i32>,
}

#[async_trait::async_trait(?Send)]
impl APICommand for TokenDelete {
    async fn run(&self, client: PolarisClient, json: bool) -> anyhow::Result<()> {
        if let Some(token_name) = &self.name {
            client.delete_token_by_name(token_name).await?
        } else {
            let token_id = self.id.unwrap();
            client.delete_token(token_id).await?
        };

        if !json {
            println!("Token deleted.");
        }
        Ok(())
    }
}
