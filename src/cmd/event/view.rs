use crate::Command;
use clap::Parser;

#[derive(Debug, Parser)]
pub struct EventView {}

#[async_trait::async_trait(?Send)]
impl Command for EventView {
    async fn run(&self) -> anyhow::Result<()> {
        todo!()
    }
}
