use crate::Command;
use clap::Parser;

#[derive(Debug, Parser)]
pub struct EventCreate {}

#[async_trait::async_trait(?Send)]
impl Command for EventCreate {
    async fn run(&self) -> anyhow::Result<()> {
        todo!()
    }
}
