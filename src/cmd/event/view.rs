use crate::Command;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
pub struct EventView {}

#[async_trait::async_trait(?Send)]
impl Command for EventView {
    async fn run(&self) -> anyhow::Result<()> {
        todo!()
    }
}
