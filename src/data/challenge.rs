//! Database models related to challenges

use crate::data::pod::Pod;

/// A challenge ran by Polaris
#[derive(Debug)]
pub struct Challenge {
    /// The database id
    pub id: Option<i32>,
    /// The pods required to run the challenge
    pub pods: Vec<Pod>,
}
