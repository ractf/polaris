use crate::Command;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
pub struct EventCreate {}

#[async_trait::async_trait(?Send)]
impl Command for EventCreate {
    async fn run(&self) -> anyhow::Result<()> {
        todo!()
    }
}
