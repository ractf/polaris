use crate::client::PolarisClient;
use crate::cmd::APICommand;
use crate::data::token::CreateableToken;
use chrono::prelude::*;
use clap::Parser;

#[derive(Debug, Parser)]
pub struct TokenCreate {
    #[clap(short, long, help = "Token Name")]
    name: String,

    #[clap(short, long, help = "Token Expiry Date (RFC-3339 format)", parse(try_from_str = DateTime::parse_from_rfc3339))]
    expiry: Option<DateTime<FixedOffset>>,

    #[clap(short, long, help = "Token Permissions")]
    perms: String,
}

#[async_trait::async_trait(?Send)]
impl APICommand for TokenCreate {
    async fn run(&self, client: PolarisClient, json: bool) -> anyhow::Result<()> {
        let token_perms = self.perms.split(',').map(String::from).collect();
        let token_expiry = self.expiry.map(Into::into);

        let token = client
            .create_token(CreateableToken {
                name: self.name.clone(),
                permissions: token_perms,
                expiry: token_expiry,
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
