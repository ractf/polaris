mod create;
mod delete;
mod view;

use crate::api_dispatch;
use crate::cmd::token::create::TokenCreate;
use crate::cmd::token::delete::TokenDelete;
use crate::cmd::token::view::TokenView;
use crate::cmd::Command;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
pub struct Token {
    pub profile: Option<String>,
    #[structopt(flatten)]
    pub subcommand: TokenSubcommand,
}

#[derive(Debug, StructOpt)]
pub enum TokenSubcommand {
    Create(TokenCreate),
    Delete(TokenDelete),
    View(TokenView),
}

#[async_trait::async_trait(?Send)]
impl Command for Token {
    async fn run(&self) -> anyhow::Result<()> {
        api_dispatch!(TokenSubcommand, self, Create, Delete, View);
        Ok(())
    }
}
