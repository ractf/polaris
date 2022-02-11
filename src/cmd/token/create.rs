use crate::client::PolarisClient;
use crate::cmd::APICommand;
use crate::data::token::CreateableToken;
use clap::Parser;

#[derive(Debug, Parser)]
pub struct TokenCreate {
    #[clap(help = "Token Name")]
    name: String,
}

#[async_trait::async_trait(?Send)]
impl APICommand for TokenCreate {
    async fn run(&self, client: PolarisClient, json: bool) -> anyhow::Result<()> {
        let token = client
            .create_token(CreateableToken {
                name: self.name.clone(),
                permissions: vec![],
                expiry: None,
            })
            .await?;
        if json {
            println!("{}", serde_json::to_string(&token)?);
        } else {
            println!("{}", token);
        }
        Ok(())
    }
}
