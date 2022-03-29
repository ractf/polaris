use crate::data::token::Token;
use crate::repo::token::TokenRepo;
use anyhow::Result;
use sqlx::PgPool;
use std::fmt::Debug;
use tracing::instrument;
use crate::data::event::Event;

#[derive(Debug, Clone)]
pub struct PostgresTokenRepo {
    pool: PgPool,
}

impl PostgresTokenRepo {
    pub fn new(pool: &PgPool) -> Self {
        Self { pool: pool.clone() }
    }
}

#[async_trait::async_trait]
impl TokenRepo for PostgresTokenRepo {
    #[instrument(skip(self, token))]
    async fn save(&self, token: Token) -> Result<Token> {
        let mut returned = token.clone();
        let result = sqlx::query!("SELECT id FROM token WHERE id=$1", token.id)
            .fetch_optional(&self.pool)
            .await?;
        if result.is_none() {
            let insert = sqlx::query!(
                r#"
                INSERT INTO token (name, token, permissions, issued, expiry)
                VALUES ($1, $2, $3, $4, $5)
                RETURNING id
            "#,
                token.name,
                token.token,
                &token.permissions,
                token.issued,
                token.expiry,
            )
            .fetch_one(&self.pool)
            .await?;
            returned.id = Some(insert.id);
            Ok(returned)
        } else {
            sqlx::query!(r#"
                UPDATE token SET name = $1, token = $2, permissions = $3, issued = $4, expiry = $5 WHERE id = $6
            "#,
                token.name, token.token, &token.permissions, token.issued, token.expiry, token.id
            ).execute(&self.pool).await?;
            Ok(returned)
        }
    }

    #[instrument(skip(self))]
    async fn get_by_id(&self, id: i32) -> Result<Token> {
        let result = sqlx::query!(
            "SELECT id, name, token, permissions, issued, expiry FROM token WHERE id=$1",
            id
        )
        .fetch_one(&self.pool)
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

    #[instrument(skip(self))]
    async fn get_all(&self) -> Result<Vec<Token>> {
        let results =
            sqlx::query!("SELECT id, name, token, permissions, issued, expiry FROM token")
                .fetch_all(&self.pool)
                .await?;
        let mut tokens = Vec::with_capacity(results.len());
        for result in results {
            tokens.push(Token {
                id: Some(result.id),
                name: result.name,
                token: result.token,
                permissions: result.permissions,
                issued: result.issued,
                expiry: result.expiry,
            });
        }
        Ok(tokens)
    }

    #[instrument(skip(self))]
    async fn is_name_taken<S: AsRef<str> + Debug + Send>(&self, name: S) -> Result<bool> {
        let result = sqlx::query!("SELECT id FROM token WHERE name=$1", name.as_ref())
            .fetch_optional(&self.pool)
            .await?;
        Ok(result.is_some())
    }

    #[instrument(skip(self, token))]
    async fn get_by_token(&self, token: String) -> Result<Token> {
        let result = sqlx::query!(
            "SELECT id, name, token, permissions, issued, expiry FROM token WHERE token=$1",
            token
        )
        .fetch_one(&self.pool)
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

    #[instrument(skip(self))]
    async fn get_by_name<S: AsRef<str> + Debug + Send>(&self, name: S) -> Result<Token> {
        let result = sqlx::query!(
            "SELECT id, name, token, permissions, issued, expiry FROM token WHERE name=$1",
            name.as_ref()
        )
        .fetch_one(&self.pool)
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

    #[instrument(skip(self))]
    async fn delete_by_id(&self, id: i32) -> Result<()> {
        sqlx::query!("DELETE FROM token WHERE id=$1", id)
            .execute(&self.pool)
            .await?;
        Ok(())
    }

    #[instrument(skip(self, token))]
    async fn is_token_valid_for_event(&self, token: &Token, event_id: i32) -> bool {
        if token.has_permission("root") || token.has_permission("event.all") {
            return true;
        }
        let result = sqlx::query!(
            "SELECT id FROM token_event WHERE token_id=$1 AND event_id=$2",
            token.id,
            event_id
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
            "INSERT INTO token_event (token_id, event_id) VALUES ($1, $2)",
            token_id,
            event_id
        )
        .execute(&self.pool)
        .await?;
        Ok(())
    }

    #[instrument(skip(self))]
    async fn remove_event(&self, token_id: i32, event_id: i32) -> Result<()> {
        sqlx::query!(
            "DELETE FROM token_event WHERE token_id=$1 AND event_id=$2",
            token_id,
            event_id
        )
        .execute(&self.pool)
        .await?;
        Ok(())
    }

    #[instrument(skip(self))]
    async fn get_events(&self, token_id: i32) -> Result<Vec<Event>> {
        let results =
            sqlx::query!("SELECT event.id, name, start_time, end_time, max_cpu, \
                                 max_ram, api_url, api_token \
                          FROM event \
                          JOIN token_event \
                              ON event.id = event_id \
                              WHERE token_id = $1", token_id)
                .fetch_all(&self.pool)
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
