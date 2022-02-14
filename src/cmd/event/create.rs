use crate::cmd::{APICommand, output_object};
use crate::client::PolarisClient;
use crate::data::event::Event;
use clap::Parser;
use chrono::prelude::*;

#[derive(Clone, Debug, Parser)]
pub struct EventCreate {
    /// The name of the event
    #[clap(long)]
    pub name: String,

    /// When the event starts
    #[clap(long, parse(try_from_str = DateTime::parse_from_rfc3339))]
    pub start_time: Option<DateTime<FixedOffset>>,

    /// When the event ends
    #[clap(long, parse(try_from_str = DateTime::parse_from_rfc3339))]
    pub end_time: Option<DateTime<FixedOffset>>,

    /// The max CPU time allocated for this event's challenges
    #[clap(long)]
    pub max_cpu: Option<i64>,

    /// The max RAM allocated for this event's challenges (in bytes)
    #[clap(long)]
    pub max_ram: Option<i64>,

    /// The URL of this event's instance of RACTF Core
    #[clap(long)]
    pub api_url: Option<String>,

    /// An API token for interacting with this event's instance of RACTF Core
    #[clap(long)]
    pub api_token: Option<String>,
}

impl From<EventCreate> for Event {
    fn from(e: EventCreate) -> Self {
        Self {
            id: None,
            name: e.name,
            start_time: e.start_time.map(Into::into),
            end_time: e.end_time.map(Into::into),
            max_cpu: e.max_cpu,
            max_ram: e.max_ram,
            api_url: e.api_url,
            api_token: e.api_token,
        }
    }
}

#[async_trait::async_trait(?Send)]
impl APICommand for EventCreate {
    async fn run(&self, client: PolarisClient, json: bool) -> anyhow::Result<()> {
        let event = client.create_event(self.clone().into()).await?;
        output_object(event, json)
    }
}
