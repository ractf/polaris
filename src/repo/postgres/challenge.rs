use crate::data::challenge::Challenge;
use crate::data::pod::Pod;
use crate::repo::challenge::ChallengeRepo;
use sqlx::types::Json;
use sqlx::PgPool;
use std::fmt::Debug;
use tracing::instrument;

#[derive(Debug, Clone)]
pub struct PostgresChallengeRepo {
    pool: PgPool,
}

impl PostgresChallengeRepo {
    pub fn new(pool: &PgPool) -> Self {
        Self { pool: pool.clone() }
    }
}

#[async_trait::async_trait]
impl ChallengeRepo for PostgresChallengeRepo {
    #[instrument(skip(self))]
    async fn save(&self, challenge: Challenge) -> anyhow::Result<Challenge> {
        let mut returned = challenge.clone();
        let result = sqlx::query!("SELECT id FROM challenge WHERE id=$1", challenge.id)
            .fetch_optional(&self.pool)
            .await?;
        let transaction = self.pool.begin().await?;
        if result.is_none() {
            let insert = sqlx::query!(
                r#"
                INSERT INTO challenge (name, event_id, replication, allocation)
                VALUES ($1, $2, $3, $4)
                RETURNING id
            "#,
                challenge.name,
                challenge.event,
                Json(challenge.replication) as _,
                Json(challenge.allocation) as _,
            )
            .fetch_one(&self.pool)
            .await?;
            returned.id = Some(insert.id);
            returned.pods = vec![];
            for pod in challenge.pods {
                let mut returned_pod = pod.clone();
                let insert = sqlx::query!(
                    r#"INSERT INTO pod (challenge_id, name, max_cpu, max_ram, pod)
                    VALUES ($1, $2, $3, $4, $5)
                    RETURNING id"#,
                    challenge.id,
                    pod.name,
                    pod.max_cpu,
                    pod.max_ram,
                    Json(pod.pod) as _,
                )
                .fetch_one(&self.pool)
                .await?;
                returned_pod.id = Some(insert.id);
                returned.pods.push(returned_pod);
            }
        } else {
            sqlx::query!(
                r#"
                UPDATE challenge SET name = $1, event_id = $2, replication = $3, allocation = $4
                WHERE id = $5
            "#,
                challenge.name,
                challenge.event,
                Json(challenge.replication) as _,
                Json(challenge.allocation) as _,
                challenge.id,
            )
            .fetch_one(&self.pool)
            .await?;
            for pod in challenge.pods {
                sqlx::query!(
                    "UPDATE pod SET name = $1, max_cpu = $2, max_ram = $3, pod = $4 WHERE id = $5",
                    pod.name,
                    pod.max_cpu,
                    pod.max_ram,
                    Json(pod.pod) as _,
                    pod.id
                )
                .execute(&self.pool)
                .await?;
            }
        }
        transaction.commit().await?;
        Ok(returned)
    }

    #[instrument(skip(self))]
    async fn get_by_id(&self, id: i32) -> anyhow::Result<Challenge> {
        let mut pods = vec![];
        let pod_results = sqlx::query!(
            "SELECT id, name, max_cpu, max_ram, pod FROM pod WHERE challenge_id=$1",
            id
        )
        .fetch_all(&self.pool)
        .await?;
        for pod in pod_results {
            pods.push(Pod {
                id: Some(pod.id),
                name: pod.name,
                max_cpu: Some(pod.max_cpu),
                max_ram: Some(pod.max_ram),
                pod: serde_json::from_value(pod.pod)?,
            })
        }
        let challenge = sqlx::query!(
            "SELECT id, name, event_id, replication, allocation FROM challenge WHERE id=$1",
            id
        )
        .fetch_one(&self.pool)
        .await?;
        Ok(Challenge {
            id: Some(challenge.id),
            name: challenge.name,
            event: challenge.event_id,
            pods,
            replication: serde_json::from_value(challenge.replication)?,
            allocation: serde_json::from_value(challenge.allocation)?,
        })
    }

    #[instrument(skip(self))]
    async fn get_all(&self) -> anyhow::Result<Vec<Challenge>> {
        //TODO: This is incredibly cursed and bad, fuck off am I figuring out how to write a join right now
        let challenges =
            sqlx::query!("SELECT id, name, event_id, replication, allocation FROM challenge")
                .fetch_all(&self.pool)
                .await?;
        let mut results = Vec::with_capacity(challenges.len());
        for challenge in challenges {
            let mut pods = vec![];
            let pod_results = sqlx::query!(
                "SELECT id, name, max_cpu, max_ram, pod FROM pod WHERE challenge_id=$1",
                challenge.id
            )
            .fetch_all(&self.pool)
            .await?;
            for pod in pod_results {
                pods.push(Pod {
                    id: Some(pod.id),
                    name: pod.name,
                    max_cpu: Some(pod.max_cpu),
                    max_ram: Some(pod.max_ram),
                    pod: serde_json::from_value(pod.pod)?,
                })
            }
            results.push(Challenge {
                id: Some(challenge.id),
                name: challenge.name,
                event: challenge.event_id,
                pods,
                replication: serde_json::from_value(challenge.replication)?,
                allocation: serde_json::from_value(challenge.allocation)?,
            })
        }
        Ok(results)
    }

    #[instrument(skip(self))]
    async fn is_name_taken<S: AsRef<str> + Debug + Send>(&self, name: S) -> anyhow::Result<bool> {
        let result = sqlx::query!("SELECT id FROM challenge WHERE name=$1", name.as_ref())
            .fetch_optional(&self.pool)
            .await?;
        Ok(result.is_some())
    }

    #[instrument(skip(self))]
    async fn get_by_name<S: AsRef<str> + Debug + Send>(
        &self,
        name: S,
    ) -> anyhow::Result<Challenge> {
        let challenge = sqlx::query!(
            "SELECT id, name, event_id, replication, allocation FROM challenge WHERE name=$1",
            name.as_ref()
        )
        .fetch_one(&self.pool)
        .await?;
        let mut pods = vec![];
        let pod_results = sqlx::query!(
            "SELECT id, name, max_cpu, max_ram, pod FROM pod WHERE challenge_id=$1",
            challenge.id
        )
        .fetch_all(&self.pool)
        .await?;
        for pod in pod_results {
            pods.push(Pod {
                id: Some(pod.id),
                name: pod.name,
                max_cpu: Some(pod.max_cpu),
                max_ram: Some(pod.max_ram),
                pod: serde_json::from_value(pod.pod)?,
            })
        }
        Ok(Challenge {
            id: Some(challenge.id),
            name: challenge.name,
            event: challenge.event_id,
            pods,
            replication: serde_json::from_value(challenge.replication)?,
            allocation: serde_json::from_value(challenge.allocation)?,
        })
    }

    #[instrument(skip(self))]
    async fn delete_by_id(&self, id: i32) -> anyhow::Result<()> {
        sqlx::query!("DELETE FROM challenge WHERE id=$1", id)
            .execute(&self.pool)
            .await?;
        Ok(())
    }
}
