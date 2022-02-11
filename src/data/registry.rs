//! Database models for storing docker registry tokens

use anyhow::Result;
use serde::{Deserialize, Serialize};
use sqlx::PgPool;
use crate::data::token::Token;

/// Information required to generate a set of docker registry credentials
#[derive(Debug, Serialize, Deserialize)]
pub struct RegistryToken {
    /// The id in the database
    pub id: Option<i32>,
    /// The name of the token
    pub name: String,
    /// The information used to generate credentials
    pub inner: RegistryTokenInner,
}

/// Information required to generate a set of docker registry credentials
#[derive(Debug, Serialize, Deserialize)]
pub enum RegistryTokenInner {
    /// Standard docker credentials
    Standard,
    /// AWS access keys that can generate docker registry credentials
    AWS(AWSAccessToken),
}

/// AWS access keys that can generate docker registry credentials
#[derive(Debug, Serialize, Deserialize)]
pub struct AWSAccessToken {
    /// The AWS access key id
    access_key_id: String,
    /// The AWS secret key
    secret_key: String,
    /// The AWS region the token is valid for
    region: String,
}

impl RegistryToken {
    /// Save the token to the database with either an INSERT or an UPDATE
    pub async fn save(&mut self, pool: &PgPool) -> Result<()> {
        let result = sqlx::query!("SELECT id FROM registry_tokens WHERE id=$1", self.id)
            .fetch_optional(pool)
            .await?;
        let json = sqlx::types::Json(&self.inner);
        if result.is_none() {
            let insert = sqlx::query!(
                r#"
                INSERT INTO registry_tokens (name, data)
                VALUES ($1, $2)
                RETURNING id
            "#,
                self.name,
                json as _,
            )
            .fetch_one(pool)
            .await?;
            self.id = Some(insert.id);
        } else {
            sqlx::query!(
                r#"
                UPDATE registry_tokens SET name = $1, data = $2 WHERE id = $3
            "#,
                self.name,
                json as _,
                self.id
            )
            .execute(pool)
            .await?;
        }
        Ok(())
    }

    /// Get a token with a given id
    pub async fn get(pool: &PgPool, id: i32) -> Result<RegistryToken> {
        let result = sqlx::query!(
            "SELECT id, name, data FROM registry_tokens WHERE id=$1",
            id
        )
            .fetch_one(pool)
            .await?;
        Ok(RegistryToken {
            id: Some(result.id),
            name: result.name,
            inner: serde_json::from_value(result.data)?,
        })
    }

    /// Delete a token from the database
    pub async fn delete(&mut self, pool: &PgPool) -> Result<()> {
        sqlx::query!("DELETE FROM registry_tokens WHERE id=$1", self.id)
            .execute(pool)
            .await?;
        Ok(())
    }

    /// Check if a human readable name is taken
    pub async fn is_name_taken(pool: &PgPool, name: &String) -> Result<bool> {
        let result = sqlx::query!("SELECT id FROM registry_tokens WHERE name=$1", name)
            .fetch_optional(pool)
            .await?;
        Ok(result.is_some())
    }

    /// Get all registry tokens stored in the database
    pub async fn get_all(pool: &PgPool) -> Result<Vec<RegistryToken>> {
        let results =
            sqlx::query!("SELECT id, name, data FROM registry_tokens")
                .fetch_all(pool)
                .await?;
        let mut tokens = Vec::with_capacity(results.len());
        for result in results {
            tokens.push(RegistryToken {
                id: Some(result.id),
                name: result.name,
                inner: serde_json::from_value(result.data)?,
            });
        }
        Ok(tokens)
    }

    /// Delete a token by id
    pub async fn delete_by_id(pool: &PgPool, id: i32) -> Result<()> {
        sqlx::query!("DELETE FROM registry_tokens WHERE id=$1", id)
            .execute(pool)
            .await?;
        Ok(())
    }

    /// Check if a registry token can be accessed in a given event
    pub async fn can_be_used_by_event(&self, pool: &PgPool, event_id: i32) -> bool {
        let result = sqlx::query!(
            "SELECT id FROM allowed_registry_tokens WHERE registry_token_id=$1 AND event_id=$2",
            self.id,
            event_id,
        )
            .fetch_optional(pool)
            .await;
        if let Ok(result) = result {
            result.is_some()
        } else {
            false
        }
    }

    /// Allow a registry token to be used in an event
    pub async fn grant_access(&self, pool: &PgPool, event_id: i32) -> Result<()> {
        sqlx::query!(
            "INSERT INTO allowed_registry_tokens (registry_token_id, event_id) VALUES ($1, $2)",
            self.id,
            event_id,
        )
            .execute(pool)
            .await?;
        Ok(())
    }

    /// Remove a registry token from a given event
    pub async fn revoke_access(&self, pool: &PgPool, event_id: i32) -> Result<()> {
        sqlx::query!(
            "DELETE FROM allowed_registry_tokens WHERE event_id=$1 AND registry_token_id=$2",
            event_id,
            self.id,
        )
            .execute(pool)
            .await?;
        Ok(())
    }
}
