use crate::cmd::event::create::EventCreate;
use crate::cmd::event::delete::EventDelete;
use crate::cmd::event::list::EventList;
use crate::cmd::event::view::EventView;
use clap::Parser;

mod create;
mod delete;
mod list;
mod view;

#[derive(Debug, Parser)]
pub enum Event {
    Create(EventCreate),
    Delete(EventDelete),
    List(EventList),
    View(EventView),
}
