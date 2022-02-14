use crate::client::PolarisClient;
use crate::cmd::{APICommand, output_object};
use crate::data::token::CreateableToken;
use chrono::prelude::*;
use clap::Parser;

#[derive(Clone, Debug, Parser)]
pub struct TokenCreate {
    /// Token Name
    #[clap(long)]
    name: String,

    /// Token Expiry Date (RFC-3339 format)
    #[clap(long, parse(try_from_str = DateTime::parse_from_rfc3339))]
    expiry: Option<DateTime<FixedOffset>>,

    /// Token Permissions
    #[clap(long)]
    permissions: String,
}

impl From<TokenCreate> for CreateableToken {
    fn from(t: TokenCreate) -> Self {
        Self {
            name: t.name,
            expiry: t.expiry.map(Into::into),
            permissions: t.permissions.split(',').map(String::from).collect(),
        }
    }
}

#[async_trait::async_trait(?Send)]
impl APICommand for TokenCreate {
    async fn run(&self, client: PolarisClient, json: bool) -> anyhow::Result<()> {
        let token = client.create_token(self.clone().into()).await?;
        output_object( token, json)
    }
}
