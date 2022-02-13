use crate::credential_provider::{DockerCredentialProvider, DockerCredentials};
use anyhow::Result;

#[derive(Debug)]
pub struct DefaultDockerCredentialProvider {
    credentials: DockerCredentials,
}

impl DefaultDockerCredentialProvider {
    pub fn new(credentials: DockerCredentials) -> Self {
        Self { credentials }
    }
}

#[async_trait::async_trait]
impl DockerCredentialProvider for DefaultDockerCredentialProvider {
    async fn provide(&self) -> Result<DockerCredentials> {
        Ok(self.credentials.clone())
    }
}
