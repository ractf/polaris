//! Database models for storing docker registry tokens

use anyhow::Result;
use serde::{Deserialize, Serialize};
use sqlx::PgPool;

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
}
