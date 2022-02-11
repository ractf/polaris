use crate::client::PolarisClient;
use crate::cmd::APICommand;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
pub struct TokenView {
    #[structopt(short, long, help = "Token Name", required_unless = "id")]
    name: Option<String>,

    #[structopt(short, long, help = "Token ID")]
    id: Option<i32>,
}

#[async_trait::async_trait(?Send)]
impl APICommand for TokenView {
    async fn run(&self, client: PolarisClient) -> anyhow::Result<()> {
        let token = if let Some(token_name) = &self.name {
            client.get_token_by_name(token_name.as_str()).await?
        } else {
            let token_id = self.id.unwrap();
            client.get_token(token_id).await?
        };

        println!("{}", token);
        Ok(())
    }
}
