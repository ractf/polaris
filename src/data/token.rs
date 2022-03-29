//! Database models related to API tokens

use crate::data::sep;
use anyhow::Result;
use nanoid::nanoid;
use serde::{Deserialize, Serialize};
use sqlx::types::chrono::{DateTime, Utc};
use sqlx::PgPool;
use std::fmt;
use std::fmt::Formatter;
use crate::data::event::Event;

/// A token that can be used to access the Polaris API
#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct Token {
    /// The database id
    pub id: Option<i32>,
    /// The human readable name of the token
    pub name: String,
    /// The Bearer token
    pub token: String,
    /// Permissions granted to the token
    pub permissions: Vec<String>,
    /// When the token was issued
    pub issued: Option<DateTime<Utc>>,
    /// When the token expires
    pub expiry: Option<DateTime<Utc>>,
}

#[derive(Serialize, Deserialize)]
pub struct TokenEventPair {
    pub token_id: i32,
    pub event_id: i32,
}

impl Token {
    /// Check if this token has a permission
    pub fn has_permission<T: ToString>(&self, permission: T) -> bool {
        self.permissions.contains(&String::from("root"))
            || self.permissions.contains(&permission.to_string())
    }

    /// Get all tokens stored in the database, this does not include tokens specified in polaris.toml
    pub async fn get_all(pool: &PgPool) -> Result<Vec<Token>> {
        let results =
            sqlx::query!("SELECT id, name, token, permissions, issued, expiry FROM token")
                .fetch_all(pool)
                .await?;
        let tokens = results
            .into_iter()
            .map(|result| Token {
                id: Some(result.id),
                name: result.name,
                token: result.token,
                permissions: result.permissions,
                issued: result.issued,
                expiry: result.expiry,
            })
            .collect();
        Ok(tokens)
    }

    /// Get all events the given token is valid for
    pub async fn list_events_token_valid_for(&self, pool: &PgPool) -> Result<Vec<Event>> {
        let results =
            sqlx::query!("SELECT event.id, name, start_time, end_time, max_cpu, \
                                 max_ram, api_url, api_token \
                          FROM event \
                          JOIN token_event \
                              ON event.id = event_id \
                              WHERE token_id = $1", self.id)
                .fetch_all(pool)
                .await?;
        let events = results
            .into_iter()
            .map(|result| Event {
                id: Some(result.id),
                name: result.name,
                start_time: result.start_time,
                end_time: result.end_time,
                max_cpu: result.max_cpu,
                max_ram: result.max_ram,
                api_url: result.api_url,
                api_token: result.api_token,
            })
            .collect();
        Ok(events)
    }
}

/// The fields that can be specified by a user creating a token
#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct CreateableToken {
    /// The human readable name of the token
    pub name: String,
    /// The permissions granted to the token
    pub permissions: Vec<String>,
    /// The time the token expires
    pub expiry: Option<DateTime<Utc>>,
}

impl From<CreateableToken> for Token {
    fn from(token: CreateableToken) -> Self {
        Token {
            id: None,
            name: token.name,
            token: nanoid!(64),
            permissions: token.permissions,
            issued: Some(Utc::now()),
            expiry: token.expiry,
        }
    }
}

impl fmt::Display for Token {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        write!(f, "Token `{}`: {}", self.name, self.token)?;

        if !self.permissions.is_empty() {
            sep(f)?;
            write!(f, "with permissions: {:?}", self.permissions)?;
        }

        if let Some(expires) = self.expiry {
            sep(f)?;
            write!(f, "expiring on: {}", expires.to_rfc2822())?;
        }

        Ok(())
    }
}
