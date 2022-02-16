use crate::cmd::APICommand;
use crate::client::PolarisClient;
use crate::data::event::Event;
use crate::data::token::Token;
use clap::Parser;
use chrono::prelude::*;

#[derive(Debug, Parser)]
pub struct AuthManage {
    /// The token to operate on
    #[clap(long, required_unless_present_any = ["token-name", "token-id"])]
    pub token: Option<String>,

    /// The name of the token to operate on
    #[clap(long)]
    pub token_name: Option<String>,

    /// The ID of the token to operate on
    #[clap(long)]
    pub token_id: Option<i32>,

    /// The names of the events to authorize the token for
    #[clap(short, long, use_delimiter = true)]
    pub authorize: Vec<String>,
}

impl AuthManage {
    async fn get_token(&self, client: &PolarisClient) -> anyhow::Result<Token> {
        let token = if let Some(bearer) = self.token.clone() {
            client.get_token_by_bearer(bearer).await?
        } else if let Some(token_name) = self.token_name.clone() {
            client.get_token_by_name(token_name).await?
        } else if let Some(token_id) = self.token_id {
            client.get_token(token_id).await?
        } else {
            unreachable!("clap guarantees at least one of these is present")
        };

        Ok(token)
    }
}

#[async_trait::async_trait(?Send)]
impl APICommand for AuthManage {
    async fn run(&self, client: PolarisClient, json: bool) -> anyhow::Result<()> {
        let token = self.get_token(&client).await?;

        for event_name in &self.authorize {
            let event = client.get_event_by_name(event_name).await?;
            let event_id = event.id.unwrap();
            let res = client.auth_token_for_event(event_id, &token).await;

            // check whether the token is valid for the event, if it is then we tried to add the
            // row again which violated the unique constraint
            let valid_for_event = client.token_is_valid_for_event(event_id, &token).await?;
            if res.is_err() && valid_for_event {
                println!("Encountered error authorizing the token for event {event_name}.");
                println!("This token is already valid for this event.");
            } else {
                // propagate the error
                let _ = res?;
            }
        }

        Ok(())
    }
}