use crate::data::registry::RegistryToken;
use crate::repo::registry::RegistryTokenRepo;
use anyhow::Result;
use sqlx::PgPool;
use std::fmt::Debug;
use tracing::instrument;

#[derive(Debug, Clone)]
pub struct PostgresRegistryTokenRepo {
    pool: PgPool,
}

impl PostgresRegistryTokenRepo {
    pub fn new(pool: &PgPool) -> Self {
        Self { pool: pool.clone() }
    }
}

#[async_trait::async_trait]
impl RegistryTokenRepo for PostgresRegistryTokenRepo {
    #[instrument(skip(self, token))]
    async fn save(&self, token: RegistryToken) -> Result<RegistryToken> {
        let mut saved_token = token.clone();
        let result = sqlx::query!("SELECT id FROM registry_token WHERE id=$1", token.id)
            .fetch_optional(&self.pool)
            .await?;
        let json = sqlx::types::Json(&token.inner);
        if result.is_none() {
            let insert = sqlx::query!(
                r#"
                INSERT INTO registry_token (name, data)
                VALUES ($1, $2)
                RETURNING id
            "#,
                token.name,
                json as _,
            )
            .fetch_one(&self.pool)
            .await?;
            saved_token.id = Some(insert.id);
        } else {
            sqlx::query!(
                r#"
                UPDATE registry_token SET name = $1, data = $2 WHERE id = $3
            "#,
                token.name,
                json as _,
                token.id
            )
            .execute(&self.pool)
            .await?;
        }
        Ok(saved_token)
    }

    #[instrument(skip(self))]
    async fn get_by_id(&self, id: i32) -> Result<RegistryToken> {
        let result = sqlx::query!("SELECT id, name, data FROM registry_token WHERE id=$1", id)
            .fetch_one(&self.pool)
            .await?;
        Ok(RegistryToken {
            id: Some(result.id),
            name: result.name,
            inner: serde_json::from_value(result.data)?,
        })
    }

    #[instrument(skip(self))]
    async fn get_all(&self) -> Result<Vec<RegistryToken>> {
        let results = sqlx::query!("SELECT id, name, data FROM registry_token")
            .fetch_all(&self.pool)
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

    #[instrument(skip(self))]
    async fn is_name_taken<S: AsRef<str> + Debug + Send>(&self, name: S) -> Result<bool> {
        let result = sqlx::query!("SELECT id FROM registry_token WHERE name=$1", name.as_ref())
            .fetch_optional(&self.pool)
            .await?;
        Ok(result.is_some())
    }

    #[instrument(skip(self))]
    async fn delete_by_id(&self, id: i32) -> Result<()> {
        sqlx::query!("DELETE FROM registry_token WHERE id=$1", id)
            .execute(&self.pool)
            .await?;
        Ok(())
    }

    #[instrument(skip(self, token))]
    async fn is_token_valid_for_event(&self, token: &RegistryToken, event_id: i32) -> bool {
        let result = sqlx::query!(
            "SELECT id FROM allowed_registry_token WHERE registry_token_id=$1 AND event_id=$2",
            token.id,
            event_id,
        )
        .fetch_optional(&self.pool)
        .await;
        if let Ok(result) = result {
            result.is_some()
        } else {
            false
        }
    }

    #[instrument(skip(self))]
    async fn add_event(&self, token_id: i32, event_id: i32) -> Result<()> {
        sqlx::query!(
            "INSERT INTO allowed_registry_token (registry_token_id, event_id) VALUES ($1, $2)",
            token_id,
            event_id,
        )
        .execute(&self.pool)
        .await?;
        Ok(())
    }

    #[instrument(skip(self))]
    async fn remove_event(&self, token_id: i32, event_id: i32) -> Result<()> {
        sqlx::query!(
            "DELETE FROM allowed_registry_token WHERE event_id=$1 AND registry_token_id=$2",
            event_id,
            token_id,
        )
        .execute(&self.pool)
        .await?;
        Ok(())
    }
}
