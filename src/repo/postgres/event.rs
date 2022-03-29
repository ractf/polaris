use crate::data::event::Event;
use crate::repo::event::EventRepo;
use sqlx::PgPool;
use std::fmt::Debug;
use tracing::instrument;
use crate::data::token::Token;

#[derive(Debug, Clone)]
pub struct PostgresEventRepo {
    pool: PgPool,
}

impl PostgresEventRepo {
    pub fn new(pool: &PgPool) -> Self {
        Self { pool: pool.clone() }
    }
}

#[async_trait::async_trait]
impl EventRepo for PostgresEventRepo {
    #[instrument(skip(self))]
    async fn save(&self, event: Event) -> anyhow::Result<Event> {
        let mut returned = event.clone();
        let result = sqlx::query!("SELECT id FROM event WHERE id=$1", event.id)
            .fetch_optional(&self.pool)
            .await?;
        if result.is_none() {
            let insert = sqlx::query!(
                r#"
                INSERT INTO event (name, start_time, end_time, max_cpu, max_ram, api_url, api_token)
                VALUES ($1, $2, $3, $4, $5, $6, $7)
                RETURNING id
            "#,
                event.name,
                event.start_time,
                event.end_time,
                event.max_cpu,
                event.max_ram,
                event.api_url,
                event.api_token
            )
            .fetch_one(&self.pool)
            .await?;
            returned.id = Some(insert.id);
        } else {
            sqlx::query!(r#"
                UPDATE event SET name = $1, start_time = $2, end_time = $3, max_cpu = $4, max_ram = $5, api_url = $6, api_token = $7 WHERE id = $8
            "#,
                event.name, event.start_time, event.end_time, event.max_cpu, event.max_ram, event.api_url, event.api_token, event.id
            ).execute(&self.pool).await?;
        }
        Ok(returned)
    }

    #[instrument(skip(self))]
    async fn get_by_id(&self, id: i32) -> anyhow::Result<Event> {
        let result = sqlx::query!("SELECT id, name, start_time, end_time, max_cpu, max_ram, api_url, api_token FROM event WHERE id=$1", id).fetch_one(&self.pool).await?;
        //TODO: https://github.com/launchbadge/sqlx/issues/367
        Ok(Event {
            id: Some(result.id),
            name: result.name,
            start_time: result.start_time,
            end_time: result.end_time,
            max_cpu: result.max_cpu,
            max_ram: result.max_ram,
            api_url: result.api_url,
            api_token: result.api_token,
        })
    }

    #[instrument(skip(self))]
    async fn get_all(&self) -> anyhow::Result<Vec<Event>> {
        let results = sqlx::query!("SELECT id, name, start_time, end_time, max_cpu, max_ram, api_url, api_token FROM event").fetch_all(&self.pool).await?;
        //TODO: https://github.com/launchbadge/sqlx/issues/367
        let mut events = Vec::with_capacity(results.len());
        for result in results {
            events.push(Event {
                id: Some(result.id),
                name: result.name,
                start_time: result.start_time,
                end_time: result.end_time,
                max_cpu: result.max_cpu,
                max_ram: result.max_ram,
                api_url: result.api_url,
                api_token: result.api_token,
            });
        }
        Ok(events)
    }

    #[instrument(skip(self))]
    async fn is_name_taken<S: AsRef<str> + Debug + Send>(&self, name: S) -> anyhow::Result<bool> {
        let result = sqlx::query!("SELECT id FROM event WHERE name=$1", name.as_ref())
            .fetch_optional(&self.pool)
            .await?;
        Ok(result.is_some())
    }

    #[instrument(skip(self))]
    async fn get_by_name<S: AsRef<str> + Debug + Send>(&self, name: S) -> anyhow::Result<Event> {
        let result = sqlx::query!("SELECT id, name, start_time, end_time, max_cpu, max_ram, api_url, api_token FROM event WHERE name=$1", name.as_ref()).fetch_one(&self.pool).await?;
        Ok(Event {
            id: Some(result.id),
            name: result.name,
            start_time: result.start_time,
            end_time: result.end_time,
            max_cpu: result.max_cpu,
            max_ram: result.max_ram,
            api_url: result.api_url,
            api_token: result.api_token,
        })
    }

    #[instrument(skip(self))]
    async fn delete_by_id(&self, id: i32) -> anyhow::Result<()> {
        sqlx::query!("DELETE FROM event WHERE id=$1", id)
            .execute(&self.pool)
            .await?;
        Ok(())
    }

    #[instrument(skip(self))]
    async fn get_all_valid_tokens(&self, id: i32) -> anyhow::Result<Vec<Token>> {
        let results = sqlx::query!("SELECT token.id, name, token, permissions, issued, expiry \
                                FROM token \
                                JOIN token_event \
                                    ON token.id = token_id \
                                    WHERE event_id = $1", id)
            .fetch_all(&self.pool).await?;
        let tokens = results.into_iter()
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
}
