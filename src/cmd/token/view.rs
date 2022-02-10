use crate::client::PolarisClient;
use crate::cmd::APICommand;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
pub struct TokenView {}

#[async_trait::async_trait(?Send)]
impl APICommand for TokenView {
    async fn run(&self, _client: PolarisClient) -> anyhow::Result<()> {
        Ok(())
    }
}
