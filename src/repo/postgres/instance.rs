use sqlx::PgPool;
use std::fmt::Debug;
use crate::data::instance::{ChallengeInstance, ChallengeInstancePort, PortProtocol};
use crate::repo::instance::InstanceRepo;

#[derive(Debug, Clone)]
pub struct PostgresInstanceRepo {
    pool: PgPool,
}

impl PostgresInstanceRepo {
    pub fn new(pool: &PgPool) -> Self {
        Self { pool: pool.clone() }
    }
}

#[async_trait::async_trait]
impl InstanceRepo for PostgresInstanceRepo {
    async fn save(&self, instance: ChallengeInstance) -> anyhow::Result<ChallengeInstance> {
        let mut returned = instance.clone();
        let result = sqlx::query!("SELECT id FROM challenge_instance WHERE id=$1", instance.id)
            .fetch_optional(&self.pool)
            .await?;
        let transaction = self.pool.begin().await?;
        if result.is_none() {
            let insert = sqlx::query!(
                r#"
                INSERT INTO challenge_instance (allocation, challenge)
                VALUES ($1, $2)
                RETURNING id
            "#,
                instance.allocation,
                instance.challenge,
            )
                .fetch_one(&self.pool)
                .await?;
            returned.id = Some(insert.id);
            returned.ports = vec![];
            for port in instance.ports {
                let mut returned_port = port.clone();
                let insert = sqlx::query!(
                    r#"INSERT INTO challenge_instance_port
                    (instance, pod, port, internal_port, advertise, protocol)
                    VALUES ($1, $2, $3, $4, $5, $6)
                    RETURNING id"#,
                    port.instance,
                    port.pod,
                    port.port as i32,
                    port.internal_port as i32,
                    port.advertise,
                    port.protocol as _,
                )
                    .fetch_one(&self.pool)
                    .await?;
                returned_port.id = Some(insert.id);
                returned.ports.push(returned_port);
            }
        } else {
            sqlx::query!(
                r#"
                UPDATE challenge_instance
                SET allocation = $1, challenge = $2
                WHERE id = $3
            "#,
                instance.allocation,
                instance.challenge,
                instance.id,
            )
                .fetch_one(&self.pool)
                .await?;
            for port in instance.ports {
                sqlx::query!(
                    r#"UPDATE challenge_instance_port
                    SET instance = $1, pod = $2, port = $3, internal_port = $4, advertise = $5, protocol = $6
                    WHERE id = $7"#,
                    port.instance,
                    port.pod,
                    port.port as i32,
                    port.internal_port as i32,
                    port.advertise,
                    port.protocol as _,
                    port.id,
                )
                    .execute(&self.pool)
                    .await?;
            }
        }
        transaction.commit().await?;
        Ok(returned)
    }

    async fn get_by_id(&self, id: i32) -> anyhow::Result<ChallengeInstance> {
        let result = sqlx::query!(
            "SELECT id, allocation, challenge FROM challenge_instance WHERE id=$1", id)
            .fetch_one(&self.pool)
            .await?;
        let mut ports = vec![];
        let db_ports = sqlx::query!(
            "SELECT id, instance, pod, port, internal_port, advertise, protocol as \"protocol: PortProtocol\" FROM challenge_instance_port WHERE instance=$1", id)
            .fetch_all(&self.pool)
            .await?;
        for port in db_ports {
            ports.push(ChallengeInstancePort {
                id: Some(port.id),
                instance: port.instance,
                pod: port.pod,
                port: port.port as u16,
                internal_port: port.internal_port as u16,
                advertise: port.advertise,
                protocol: port.protocol,
            })
        }
        Ok(ChallengeInstance {
            id: Some(result.id),
            allocation: result.allocation,
            challenge: result.challenge,
            ports
        })
    }

    async fn get_all(&self) -> anyhow::Result<Vec<ChallengeInstance>> {
        let results = sqlx::query!(
            "SELECT id, allocation, challenge FROM challenge_instance")
            .fetch_all(&self.pool)
            .await?;
        let mut instances = Vec::with_capacity(results.len());
        for result in results {
            let mut ports = vec![];
            let db_ports = sqlx::query!(
            "SELECT id, instance, pod, port, internal_port, advertise, protocol as \"protocol: PortProtocol\" FROM challenge_instance_port WHERE instance=$1", result.id)
                .fetch_all(&self.pool)
                .await?;
            for port in db_ports {
                ports.push(ChallengeInstancePort {
                    id: Some(port.id),
                    instance: port.instance,
                    pod: port.pod,
                    port: port.port as u16,
                    internal_port: port.internal_port as u16,
                    advertise: port.advertise,
                    protocol: port.protocol,
                })
            }
            instances.push(ChallengeInstance {
                id: Some(result.id),
                allocation: result.allocation,
                challenge: result.challenge,
                ports
            })
        }
        Ok(instances)
    }

    async fn delete_by_id(&self, id: i32) -> anyhow::Result<()> {
        sqlx::query!("DELETE FROM challenge_instance WHERE id=$1", id)
            .execute(&self.pool)
            .await?;
        Ok(())
    }
}
