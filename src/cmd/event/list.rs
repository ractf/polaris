use crate::Command;
use clap::Parser;

#[derive(Debug, Parser)]
pub struct EventList {}

#[async_trait::async_trait(?Send)]
impl Command for EventList {
    async fn run(&self) -> anyhow::Result<()> {
        todo!()
    }
}
