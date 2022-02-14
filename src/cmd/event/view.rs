use crate::Command;
use clap::Parser;
use crate::client::PolarisClient;
use crate::cmd::{APICommand, output_object};

#[derive(Debug, Parser)]
pub struct EventView {
    /// Event Name
    #[clap(long, required_unless_present = "id")]
    name: Option<String>,

    /// Token ID
    #[clap(long)]
    id: Option<i32>,
}

#[async_trait::async_trait(?Send)]
impl APICommand for EventView {
    async fn run(&self, client: PolarisClient, json: bool) -> anyhow::Result<()> {
        let event = if let Some(event_name) = &self.name {
            client.get_event_by_name(event_name).await?
        } else {
            let event_id = self.id.unwrap();
            client.get_event(event_id).await?
        };

        output_object(event, json)
    }
}
