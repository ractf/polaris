use crate::client::PolarisClient;
use crate::cmd::APICommand;
use crate::data::token::CreateableToken;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
pub struct TokenCreate {
    #[structopt(help = "Token Name")]
    name: String,
}

#[async_trait::async_trait(?Send)]
impl APICommand for TokenCreate {
    async fn run(&self, client: PolarisClient) -> anyhow::Result<()> {
        let token = client
            .create_token(CreateableToken {
                name: self.name.clone(),
                permissions: vec![],
                expiry: None,
            })
            .await?;
        println!("{:?}", token);
        Ok(())
    }
}
