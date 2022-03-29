use crate::config::node::NodeConfig;
use crate::data::node::Node;
use crate::repo::node::NodeRepo;
use chrono::Utc;
use nanoid::nanoid;
use std::time::Duration;
use sysinfo::{ProcessorExt, RefreshKind, System, SystemExt};
use tokio::time;
use tracing::debug;

pub async fn start_node_sync<T: 'static + NodeRepo + Send + Sync>(
    node_repo: T,
    node_config: &NodeConfig,
    bind_address: String,
) {
    let node_config = node_config.clone();
    tokio::spawn(async move {
        let refresh_kind = RefreshKind::new()
            .with_cpu()
            .with_disks()
            .with_memory()
            .with_networks();
        let mut sys_info = sysinfo::System::new_with_specifics(refresh_kind);

        let hostname = get_hostname(&node_config, &sys_info);

        let (id, schedulable) = if let Ok(node) = node_repo.get_by_name(&hostname).await {
            debug!("Found by name {} {:?}", hostname, node.id);
            (node.id, Some(node.schedulable))
        } else {
            debug!("Not found by name {}", hostname);
            (None, node_config.schedulable)
        };

        let mut interval = time::interval(Duration::from_secs(10));

        let total_ram = if let Some(total_ram) = node_config.total_ram {
            total_ram
        } else {
            sys_info.total_memory()
        };
        let total_cpu = if let Some(total_cpu) = node_config.total_cpu {
            total_cpu
        } else {
            let mut t = 0;
            for processor in sys_info.processors() {
                t += processor.frequency();
            }
            t as u32
        };

        let mut node = Node {
            id,
            hostname,
            total_ram: total_ram as i64,
            total_cpu: total_cpu as i64,
            available_ram: 0,
            available_cpu: 0,
            schedulable: schedulable.unwrap_or(true),
            version: env!("CARGO_PKG_VERSION").to_string(),
            kernel_version: sys_info.kernel_version().unwrap_or(String::from("unknown")),
            public_ip: node_config.public_ip,
            bind_address,
            last_updated: Utc::now(),
        };

        loop {
            interval.tick().await;
            sys_info.refresh_specifics(
                RefreshKind::new()
                    .with_cpu()
                    .with_disks()
                    .with_memory()
                    .with_networks(),
            );
            update_node(&mut node, &sys_info);
            node_repo.save(node.clone()).await.unwrap();
        }
    });
}

fn get_hostname(node_config: &NodeConfig, sys_info: &System) -> String {
    let hostname = if let Some(hostname) = node_config.hostname.clone() {
        hostname
    } else if let Some(hostname) = sys_info.host_name() {
        hostname
    } else {
        nanoid!(32)
    };
    hostname
}

fn update_node(node: &mut Node, sys_info: &System) {
    let mut total = 0f32;
    for processor in sys_info.processors() {
        total += processor.cpu_usage();
    }
    node.available_ram = sys_info.available_memory() as i64;
    node.available_cpu = sys_info.processors().len() as i64 * 100 - total as i64;
    node.last_updated = Utc::now();
    let mut t = 0;
    for processor in sys_info.processors() {
        t += processor.frequency();
    }
    node.total_cpu = t as i64;
}
