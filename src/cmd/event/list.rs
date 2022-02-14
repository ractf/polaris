use clap::Parser;
use crate::client::PolarisClient;
use crate::cmd::{APICommand, output_objects};

#[derive(Debug, Parser)]
pub struct EventList {}

#[async_trait::async_trait(?Send)]
impl APICommand for EventList {
    async fn run(&self, client: PolarisClient, json: bool) -> anyhow::Result<()> {
        let events = client.get_events().await?;
        output_objects(events, json)
    }
}
