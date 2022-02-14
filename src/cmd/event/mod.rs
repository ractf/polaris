mod create;
mod delete;
mod list;
mod view;

use crate::api_dispatch;
use crate::cmd::event::create::EventCreate;
use crate::cmd::event::delete::EventDelete;
use crate::cmd::event::list::EventList;
use crate::cmd::event::view::EventView;
use crate::cmd::Command;
use clap::{Parser, Subcommand};

#[derive(Debug, Parser)]
pub struct Event {
    pub profile: Option<String>,

    #[clap(long)]
    pub json: bool,

    #[clap(subcommand)]
    pub subcommand: EventSubcommand,
}

#[derive(Debug, Subcommand)]
pub enum EventSubcommand {
    Create(EventCreate),
    Delete(EventDelete),
    List(EventList),
    View(EventView),
}

#[async_trait::async_trait(?Send)]
impl Command for Event {
    async fn run(&self) -> anyhow::Result<()> {
        api_dispatch!(EventSubcommand, self, Create, Delete, List, View);
        Ok(())
    }
}
