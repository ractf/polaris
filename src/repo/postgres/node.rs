use crate::data::node::Node;
use crate::repo::node::NodeRepo;
use sqlx::PgPool;
use std::fmt::Debug;
use tracing::instrument;

#[derive(Debug, Clone)]
pub struct PostgresNodeRepo {
    pool: PgPool,
}

impl PostgresNodeRepo {
    pub fn new(pool: &PgPool) -> Self {
        Self { pool: pool.clone() }
    }
}

#[async_trait::async_trait]
impl NodeRepo for PostgresNodeRepo {
    #[instrument(skip(self))]
    async fn save(&self, node: Node) -> anyhow::Result<Node> {
        let mut returned = node.clone();
        let result = sqlx::query!("SELECT id FROM node WHERE id=$1", node.id)
            .fetch_optional(&self.pool)
            .await?;
        if result.is_none() {
            let insert = sqlx::query!(
                r#"INSERT INTO node
                (hostname, total_ram, total_cpu, available_ram, available_cpu, schedulable,
                        version, kernel_version, public_ip, bind_address, last_updated)
                VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11)
                RETURNING id"#,
                node.hostname,
                node.total_ram,
                node.total_cpu,
                node.available_ram,
                node.available_cpu,
                node.schedulable,
                node.version,
                node.kernel_version,
                node.public_ip,
                node.bind_address,
                node.last_updated,
            )
            .fetch_one(&self.pool)
            .await?;
            returned.id = Some(insert.id);
        } else {
            sqlx::query!(
                r#"
                    UPDATE node
                    SET hostname = $1, total_ram = $2, total_cpu = $3,
                        available_ram = $4, available_cpu = $5, schedulable = $6,
                        version = $7, kernel_version = $8, public_ip = $9, bind_address = $10,
                        last_updated = $11
                    WHERE id = $12"#,
                node.hostname,
                node.total_ram,
                node.total_cpu,
                node.available_ram,
                node.available_cpu,
                node.schedulable,
                node.version,
                node.kernel_version,
                node.public_ip,
                node.bind_address,
                node.last_updated,
                node.id
            )
            .execute(&self.pool)
            .await?;
        }
        Ok(returned)
    }

    #[instrument(skip(self))]
    async fn get_by_id(&self, id: i32) -> anyhow::Result<Node> {
        let result = sqlx::query!("SELECT id, hostname, total_ram, total_cpu, available_ram, available_cpu, schedulable,
                        version, kernel_version, public_ip, bind_address, last_updated FROM node WHERE id=$1", id)
            .fetch_one(&self.pool)
            .await?;
        Ok(Node {
            id: Some(result.id),
            hostname: result.hostname,
            total_ram: result.total_ram,
            total_cpu: result.total_cpu,
            available_ram: result.available_ram,
            available_cpu: result.available_cpu,
            schedulable: result.schedulable,
            version: result.version,
            kernel_version: result.kernel_version,
            public_ip: result.public_ip,
            bind_address: result.bind_address,
            last_updated: result.last_updated,
        })
    }

    #[instrument(skip(self))]
    async fn get_all(&self) -> anyhow::Result<Vec<Node>> {
        let results = sqlx::query!(
            "SELECT id, hostname, total_ram, total_cpu, available_ram, available_cpu, schedulable,
                        version, kernel_version, public_ip, bind_address, last_updated FROM node"
        )
        .fetch_all(&self.pool)
        .await?;
        let mut nodes = Vec::with_capacity(results.len());
        for result in results {
            nodes.push(Node {
                id: Some(result.id),
                hostname: result.hostname,
                total_ram: result.total_ram,
                total_cpu: result.total_cpu,
                available_ram: result.available_ram,
                available_cpu: result.available_cpu,
                schedulable: result.schedulable,
                version: result.version,
                kernel_version: result.kernel_version,
                public_ip: result.public_ip,
                bind_address: result.bind_address,
                last_updated: result.last_updated,
            });
        }
        Ok(nodes)
    }

    #[instrument(skip(self))]
    async fn get_by_name<S: AsRef<str> + Debug + Send>(&self, name: S) -> anyhow::Result<Node> {
        let result = sqlx::query!("SELECT id, hostname, total_ram, total_cpu, available_ram, available_cpu, schedulable,
                        version, kernel_version, public_ip, bind_address, last_updated FROM node WHERE hostname=$1", name.as_ref())
            .fetch_one(&self.pool)
            .await?;
        Ok(Node {
            id: Some(result.id),
            hostname: result.hostname,
            total_ram: result.total_ram,
            total_cpu: result.total_cpu,
            available_ram: result.available_ram,
            available_cpu: result.available_cpu,
            schedulable: result.schedulable,
            version: result.version,
            kernel_version: result.kernel_version,
            public_ip: result.public_ip,
            bind_address: result.bind_address,
            last_updated: result.last_updated,
        })
    }

    #[instrument(skip(self))]
    async fn delete_by_id(&self, id: i32) -> anyhow::Result<()> {
        sqlx::query!("DELETE FROM node WHERE id=$1", id)
            .execute(&self.pool)
            .await?;
        Ok(())
    }
}
