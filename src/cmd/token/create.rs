use crate::client::PolarisClient;
use crate::cmd::APICommand;
use crate::data::token::CreateableToken;
use clap::Parser;

#[derive(Debug, Parser)]
pub struct TokenCreate {
    #[clap(short, long, help = "Token Name")]
    name: String,

    #[clap(short, long, help = "Token permissions")]
    perms: String,
}

#[async_trait::async_trait(?Send)]
impl APICommand for TokenCreate {
    async fn run(&self, client: PolarisClient, json: bool) -> anyhow::Result<()> {
        let token_perms = self.perms.split(',').map(String::from).collect();

        let token = client
            .create_token(CreateableToken {
                name: self.name.clone(),
                permissions: token_perms,
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
