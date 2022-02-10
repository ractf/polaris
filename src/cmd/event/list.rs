use crate::Command;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
pub struct EventList {}

#[async_trait::async_trait(?Send)]
impl Command for EventList {
    async fn run(&self) -> anyhow::Result<()> {
        todo!()
    }
}
