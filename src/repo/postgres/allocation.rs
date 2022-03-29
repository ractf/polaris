use sqlx::PgPool;
use crate::data::allocation::Allocation;
use crate::repo::allocation::AllocationRepo;

#[derive(Debug, Clone)]
pub struct PostgresAllocationRepo {
    pool: PgPool,
}

impl PostgresAllocationRepo {
    pub fn new(pool: &PgPool) -> Self {
        Self { pool: pool.clone() }
    }
}

#[async_trait::async_trait]
impl AllocationRepo for PostgresAllocationRepo {
    async fn save(&self, allocation: Allocation) -> anyhow::Result<Allocation> {
        let mut returned = allocation.clone();
        let result = sqlx::query!("SELECT id FROM allocation WHERE id=$1", allocation.id)
            .fetch_optional(&self.pool)
            .await?;
        if result.is_none() {
            let insert = sqlx::query!(
                r#"INSERT INTO allocation
                (node_id, start_time, end_time, service, completed, cpu, ram)
                VALUES ($1, $2, $3, $4, $5, $6, $7)
                RETURNING id"#,
                allocation.node_id,
                allocation.start_time,
                allocation.end_time,
                allocation.service,
                allocation.completed,
                allocation.cpu,
                allocation.ram,
            )
                .fetch_one(&self.pool)
                .await?;
            returned.id = Some(insert.id);
        } else {
            sqlx::query!(
                r#"
                    UPDATE allocation
                    SET node_id=$1, start_time=$2, end_time=$3, service=$4, completed=$5, cpu=$6, ram=$7
                    WHERE id = $8"#,
                allocation.node_id,
                allocation.start_time,
                allocation.end_time,
                allocation.service,
                allocation.completed,
                allocation.cpu,
                allocation.ram,
                allocation.id,
            )
                .execute(&self.pool)
                .await?;
        }
        Ok(returned)
    }

    async fn get_by_id(&self, id: i32) -> anyhow::Result<Allocation> {
        let result = sqlx::query!(
            "SELECT id, node_id, start_time, end_time, service, completed, cpu, ram FROM allocation WHERE id=$1", id)
            .fetch_one(&self.pool)
            .await?;
        Ok(Allocation {
            id: Some(result.id),
            node_id: result.node_id,
            start_time: result.start_time,
            end_time: result.end_time,
            service: result.service,
            completed: result.completed,
            cpu: result.cpu,
            ram: result.ram,
        })
    }

    async fn get_all(&self) -> anyhow::Result<Vec<Allocation>> {
        let results = sqlx::query!(
            "SELECT id, node_id, start_time, end_time, service, completed, cpu, ram FROM allocation")
            .fetch_all(&self.pool)
            .await?;
        let mut allocations = Vec::with_capacity(results.len());
        for result in results {
            allocations.push(Allocation {
                id: Some(result.id),
                node_id: result.node_id,
                start_time: result.start_time,
                end_time: result.end_time,
                service: result.service,
                completed: result.completed,
                cpu: result.cpu,
                ram: result.ram,
            })
        }
        Ok(allocations)
    }

    async fn delete_by_id(&self, id: i32) -> anyhow::Result<()> {
        sqlx::query!("DELETE FROM allocation WHERE id=$1", id)
            .execute(&self.pool)
            .await?;
        Ok(())
    }
}
