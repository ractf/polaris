//! Database models related to API tokens

use crate::data::sep;
use anyhow::Result;
use nanoid::nanoid;
use serde::{Deserialize, Serialize};
use sqlx::types::chrono::{DateTime, Utc};
use sqlx::PgPool;
use std::fmt;
use std::fmt::Formatter;

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

impl Token {
    /// Save the token to the database with either an INSERT or an UPDATE
    pub async fn save(&mut self, pool: &PgPool) -> Result<()> {
        let result = sqlx::query!("SELECT id FROM token WHERE id=$1", self.id)
            .fetch_optional(pool)
            .await?;
        if result.is_none() {
            let insert = sqlx::query!(
                r#"
                INSERT INTO token ( name, token, permissions, issued, expiry )
                VALUES ($1, $2, $3, $4, $5)
                RETURNING id
            "#,
                self.name,
                self.token,
                &self.permissions,
                self.issued,
                self.expiry,
            )
            .fetch_one(pool)
            .await?;
            self.id = Some(insert.id);
        } else {
            sqlx::query!(r#"
                UPDATE token SET name = $1, token = $2, permissions = $3, issued = $4, expiry = $5 WHERE id = $6
            "#,
                self.name, self.token, &self.permissions, self.issued, self.expiry, self.id
            ).execute(pool).await?;
        }
        Ok(())
    }

    /// Get a token with a given id
    pub async fn get(pool: &PgPool, id: i32) -> Result<Token> {
        let result = sqlx::query!(
            "SELECT id, name, token, permissions, issued, expiry FROM token WHERE id=$1",
            id
        )
        .fetch_one(pool)
        .await?;
        Ok(Token {
            id: Some(result.id),
            name: result.name,
            token: result.token,
            permissions: result.permissions,
            issued: result.issued,
            expiry: result.expiry,
        })
    }

    /// Get a token from the token's name
    pub async fn get_by_name<T: AsRef<str>>(pool: &PgPool, name: T) -> Result<Token> {
        let result = sqlx::query!(
            "SELECT id, name, token, permissions, issued, expiry FROM token WHERE name=$1",
            name.as_ref()
        )
        .fetch_one(pool)
        .await?;

        Ok(Token {
            id: Some(result.id),
            name: result.name,
            token: result.token,
            permissions: result.permissions,
            issued: result.issued,
            expiry: result.expiry,
        })
    }

    /// Get a token from a Bearer token
    pub async fn get_by_token<T: AsRef<str>>(pool: &PgPool, token: T) -> Result<Token> {
        let result = sqlx::query!(
            "SELECT id, name, token, permissions, issued, expiry FROM token WHERE token=$1",
            token.as_ref()
        )
        .fetch_one(pool)
        .await?;
        Ok(Token {
            id: Some(result.id),
            name: result.name,
            token: result.token,
            permissions: result.permissions,
            issued: result.issued,
            expiry: result.expiry,
        })
    }

    /// Delete a token from the database
    pub async fn delete(&mut self, pool: &PgPool) -> Result<()> {
        sqlx::query!("DELETE FROM token WHERE id=$1", self.id)
            .execute(pool)
            .await?;
        Ok(())
    }

    /// Check if a token is valid for a given event
    pub async fn is_valid_for_event(&self, pool: &PgPool, event_id: i32) -> bool {
        if self.has_permission("root") || self.has_permission("event.all") {
            return true;
        }
        let result = sqlx::query!(
            "SELECT id FROM token_events WHERE token_id=$1 AND event_id=$2",
            self.id,
            event_id
        )
        .fetch_optional(pool)
        .await;
        result.ok().flatten().is_some()
    }

    /// Get all events the token is valid in
    pub async fn get_events(&self, pool: &PgPool) -> Result<Vec<i32>> {
        let results = sqlx::query!(
            "SELECT event_id FROM token_events WHERE token_id=$1",
            self.id
        )
        .fetch_all(pool)
        .await?;
        let events = results.into_iter().map(|r| r.event_id).collect();
        Ok(events)
    }

    /// Make this token valid for a given event id
    pub async fn add_event(&self, pool: &PgPool, event_id: i32) -> Result<()> {
        sqlx::query!(
            "INSERT INTO token_events (token_id, event_id) VALUES ($1, $2)",
            self.id,
            event_id
        )
        .execute(pool)
        .await?;
        Ok(())
    }

    /// Remove an event from this token
    pub async fn remove_event(&self, pool: &PgPool, event_id: i32) -> Result<()> {
        sqlx::query!(
            "DELETE FROM token_events WHERE token_id=$1 AND event_id=$2",
            self.id,
            event_id
        )
        .execute(pool)
        .await?;
        Ok(())
    }

    /// Check if this token has a permission
    pub fn has_permission<T: ToString>(&self, permission: T) -> bool {
        self.permissions.contains(&String::from("root"))
            || self.permissions.contains(&permission.to_string())
    }

    /// Check if a human readable name is taken
    pub async fn is_name_taken<T: AsRef<str>>(pool: &PgPool, name: T) -> Result<bool> {
        let result = sqlx::query!("SELECT id FROM token WHERE name=$1", name.as_ref())
            .fetch_optional(pool)
            .await?;
        Ok(result.is_some())
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

    /// Delete a token by id
    pub async fn delete_id(pool: &PgPool, id: i32) -> Result<()> {
        sqlx::query!("DELETE FROM token WHERE id=$1", id)
            .execute(pool)
            .await?;
        Ok(())
    }

    /// Check if a token id is stored in the database
    pub async fn id_exists(pool: &PgPool, id: i32) -> Result<bool> {
        let result = sqlx::query!("SELECT id FROM token WHERE id=$1", id)
            .fetch_optional(pool)
            .await?;
        Ok(result.is_some())
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
