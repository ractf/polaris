use crate::Command;
use clap::Parser;

#[derive(Debug, Parser)]
pub struct EventDelete {}

#[async_trait::async_trait(?Send)]
impl Command for EventDelete {
    async fn run(&self) -> anyhow::Result<()> {
        todo!()
    }
}
