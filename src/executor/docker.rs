use std::collections::HashMap;
use anyhow::Result;
use bollard::Docker;
use crate::credential_provider::DockerCredentialProvider;
use crate::data::pod::OCIContainer;
use crate::executor::Executor;

pub struct DockerExecutor {
    docker: Docker,
    credential_providers: HashMap<String, Box<dyn DockerCredentialProvider + Send + Sync>>,
}

impl DockerExecutor {
    pub fn new() -> Result<DockerExecutor> {
        let docker = Docker::connect_with_local_defaults()?;
        Ok(DockerExecutor {
            docker,
            credential_providers: Default::default()
        })
    }
}

impl DockerExecutor {
    async fn pull_image(&self, image: String) {

    }
}

#[async_trait::async_trait]
impl Executor<OCIContainer> for DockerExecutor {
    async fn create_container(&self, container: OCIContainer) -> anyhow::Result<String> {
        todo!()
    }

    async fn start_container(&self, container_id: String) -> anyhow::Result<String> {
        todo!()
    }

    async fn stop_container(&self, container_id: String) -> anyhow::Result<String> {
        todo!()
    }
}
