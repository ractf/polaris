use crate::client::PolarisClient;
use crate::cmd::APICommand;
use clap::Parser;

#[derive(Debug, Parser)]
pub struct TokenView {
    #[clap(short, long, help = "Token Name", required_unless_present = "id")]
    name: Option<String>,

    #[clap(short, long, help = "Token ID")]
    id: Option<i32>,
}

#[async_trait::async_trait(?Send)]
impl APICommand for TokenView {
    async fn run(&self, client: PolarisClient, json: bool) -> anyhow::Result<()> {
        let token = if let Some(token_name) = &self.name {
            client.get_token_by_name(token_name.as_str()).await?
        } else {
            let token_id = self.id.unwrap();
            client.get_token(token_id).await?
        };

        if json {
            println!("{}", serde_json::to_string(&token)?);
        } else {
            println!("{}", token);
        }
        Ok(())
    }
}
