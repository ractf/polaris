use crate::client::PolarisClient;
use crate::cmd::{deleted, APICommand};
use clap::Parser;

#[derive(Debug, Parser)]
pub struct EventDelete {
    /// Event Name
    #[clap(long, required_unless_present = "id")]
    name: Option<String>,

    /// Token ID
    #[clap(long)]
    id: Option<i32>,
}

#[async_trait::async_trait(?Send)]
impl APICommand for EventDelete {
    async fn run(&self, client: PolarisClient, json: bool) -> anyhow::Result<()> {
        let event_id = if let Some(event_name) = &self.name {
            let event = client.get_event_by_name(event_name).await?;
            event.id.expect("Event has no ID?")
        } else {
            self.id.unwrap()
        };
        client.delete_event(event_id).await?;
        deleted("Token", json)
    }
}
