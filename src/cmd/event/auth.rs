use std::collections::HashMap;
use crate::client::PolarisClient;
use crate::cmd::APICommand;
use crate::data::token::Token;
use clap::Parser;
use serde_json::json;

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

    /// The names of the events to revoke the token for
    #[clap(short, long, use_delimiter = true)]
    pub revoke: Vec<String>,

    /// The names of the events to check the permission for
    #[clap(short, long, use_delimiter = true)]
    pub check: Vec<String>,

    /// List all of the events a token is valid for
    #[clap(long)]
    pub list_valid: bool,
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

        let mut warnings: Vec<String> = Vec::new();
        for event_name in &self.authorize {
            let event = client.get_event_by_name(event_name).await?;
            let event_id = event.id.unwrap();

            // check whether the token is already valid for the event, if it is then we make emit
            // a warning and continue the loop
            let valid_for_event = client.token_is_valid_for_event(event_id, &token).await?;
            if valid_for_event {
                let warning = format!("Token is already valid for event `{event_name}`.");
                if json {
                    warnings.push(warning);
                } else {
                    println!("{}", warning)
                }
                continue
            }

            client.auth_token_for_event(event_id, &token).await?;
        }

        for event_name in &self.revoke {
            let event = client.get_event_by_name(event_name).await?;
            let event_id = event.id.unwrap();
            client.revoke_token_for_event(event_id, &token).await?;
        }

        let mut valid: HashMap<String, bool> = HashMap::new();
        for event_name in &self.check {
            let event = client.get_event_by_name(event_name).await?;
            let event_id = event.id.unwrap();
            let is_valid = client.token_is_valid_for_event(event_id, &token).await?;
            if json {
                valid.insert(event_name.clone(), is_valid);
            } else if is_valid {
                println!("Token `{}` is valid for Event `{event_name}`", token.name);
            } else {
                println!("Token `{}` is not valid for Event `{event_name}`", token.name);
            }
        }

        let valid_for = if self.list_valid {
            let events = client.list_events_token_valid_for(&token).await?;
            if json {
                events
            } else {
                for event in events {
                    println!("Token is valid for: {}", event);
                }
                vec![]
            }
        } else {
            vec![]
        };

        if json {
            let val = json!({
                "warnings": warnings,
                "token_is_valid_for_check": valid,
                "token_is_valid_for_list": valid_for,
            });
            println!("{}", serde_json::to_string(&val)?);
        }

        Ok(())
    }
}
