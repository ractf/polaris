use crate::config::Config;
use crate::credential_provider::default::DefaultDockerCredentialProvider;
use crate::credential_provider::DockerCredentialProvider;
use crate::data::pod::OCIContainer;
use crate::executor::Executor;
use anyhow::{anyhow, Result};
use bollard::image::CreateImageOptions;
use bollard::{ClientVersion, Docker};
use futures::stream::StreamExt;
use std::collections::HashMap;
use tracing::trace;

type CredProvider = Box<dyn DockerCredentialProvider + Send + Sync>;

pub struct DockerExecutor {
    docker: Docker,
    credential_providers: HashMap<String, CredProvider>,
    default_credential_provider: CredProvider,
    default_repo: String,
}

pub struct DockerContainerHandle(String);

impl ToString for DockerContainerHandle {
    fn to_string(&self) -> String {
        self.0.to_string()
    }
}

impl DockerExecutor {
    pub fn new(config: &Config) -> Result<DockerExecutor> {
        let docker_url = config.docker.docker_url.clone();
        let docker = if docker_url.starts_with("http://") {
            Docker::connect_with_http(
                docker_url.as_str(),
                config.docker.docker_timeout,
                &ClientVersion {
                    major_version: config.docker.docker_api_version_major as usize,
                    minor_version: config.docker.docker_api_version_minor as usize,
                },
            )?
        } else if docker_url.starts_with("unix://") {
            Docker::connect_with_unix(
                docker_url.as_str(),
                config.docker.docker_timeout,
                &ClientVersion {
                    major_version: config.docker.docker_api_version_major as usize,
                    minor_version: config.docker.docker_api_version_minor as usize,
                },
            )?
        } else {
            return Err(anyhow!("Invalid url {}", docker_url));
        };

        Ok(DockerExecutor {
            docker,
            credential_providers: HashMap::new(),
            default_credential_provider: Box::new(DefaultDockerCredentialProvider::new(
                config.docker.default_credentials.clone(),
            )),
            default_repo: config.docker.default_repo.clone(),
        })
    }
}

impl DockerExecutor {
    fn match_credential_provider(&self, image: &DockerImage) -> &CredProvider {
        let image_match = format!("{}/{}", image.repo, image.image);
        if let Some(cred_provider) = self.credential_providers.get(&image_match) {
            return cred_provider;
        }
        let repo_match = image.repo.clone();
        if let Some(cred_provider) = self.credential_providers.get(&repo_match) {
            return cred_provider;
        }
        &self.default_credential_provider
    }
}

#[async_trait::async_trait]
impl Executor<OCIContainer, DockerContainerHandle> for DockerExecutor {
    async fn pull_container(&self, container: &OCIContainer) -> Result<()> {
        let image = parse_docker_image(container.image.clone(), self.default_repo.clone())?;
        let cred_provider = self.match_credential_provider(&image);
        let creds = cred_provider.provide().await?;
        let mut result = self
            .docker
            .create_image(Some(image.into()), None, Some(creds.into()));
        while let Some(x) = result.next().await {
            trace!("{:?}", x);
        }
        Ok(())
    }

    async fn create_container(&self, container: &OCIContainer) -> Result<DockerContainerHandle> {
        todo!()
    }

    async fn start_container(&self, container_id: &DockerContainerHandle) -> Result<()> {
        todo!()
    }

    async fn stop_container(&self, container_id: &DockerContainerHandle) -> Result<()> {
        todo!()
    }
}

#[derive(Debug, PartialEq)]
pub struct DockerImage {
    pub repo: String,
    pub image: String,
    pub tag: String,
}

fn parse_docker_image(image: String, default_repo: String) -> Result<DockerImage> {
    let image = if image.starts_with("http://") || image.starts_with("https://") {
        image.splitn(3, '/').last().unwrap().to_string()
    } else {
        image
    };

    let (repo, image) = if let Some(split) = image.rsplit_once('/') {
        (split.0.to_string(), split.1.to_string())
    } else {
        (default_repo, image)
    };

    let (tag, image) = if let Some(split) = image.rsplit_once('@') {
        (split.1.to_string(), split.0.to_string())
    } else if let Some(split) = image.rsplit_once(':') {
        (split.1.to_string(), split.0.to_string())
    } else {
        (String::from("latest"), image)
    };

    Ok(DockerImage { repo, image, tag })
}

impl From<DockerImage> for CreateImageOptions<String> {
    fn from(image: DockerImage) -> Self {
        let from_image = format!("{}/{}", image.repo, image.image);
        Self {
            from_image,
            from_src: String::from(""),
            repo: String::from(""),
            tag: image.tag,
            platform: String::from(""),
        }
    }
}

#[cfg(test)]
mod tests {
    use crate::executor::docker::{parse_docker_image, DockerImage};

    #[test]
    fn test_parse() {
        let expected = DockerImage {
            repo: String::from("123456.dkr.ecr.eu-west-2.amazonaws.com"),
            image: String::from("image"),
            tag: String::from("tag"),
        };
        assert_eq!(
            parse_docker_image(
                String::from("http://123456.dkr.ecr.eu-west-2.amazonaws.com/image:tag"),
                String::from("docker.io")
            )
            .unwrap(),
            expected
        );
    }

    #[test]
    fn test_parse_single() {
        let expected = DockerImage {
            repo: String::from("docker.io"),
            image: String::from("ubuntu"),
            tag: String::from("latest"),
        };
        assert_eq!(
            parse_docker_image(String::from("ubuntu"), String::from("docker.io")).unwrap(),
            expected
        );
    }
}
