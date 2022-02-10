use crate::Command;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
pub struct EventDelete {}

#[async_trait::async_trait(?Send)]
impl Command for EventDelete {
    async fn run(&self) -> anyhow::Result<()> {
        todo!()
    }
}
