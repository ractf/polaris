//! Database models used by polaris

pub mod challenge;
pub mod event;
pub mod allocation;
pub mod node;
pub mod notification;
pub mod pod;
pub mod registry;
pub mod token;
pub mod instance;

fn sep(f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
    write!(f, "{}", if f.alternate() { "\n" } else { ", " })
}
