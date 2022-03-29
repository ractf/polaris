use crate::credential_provider::{CacheableDockerCredentialProvider, DockerCredentials};
use anyhow::Result;
use aws_sdk_ecr::Client;
use aws_smithy_types_convert::date_time::DateTimeExt;
use aws_types::credentials::SharedCredentialsProvider;
use aws_types::region::Region;
use aws_types::{Credentials, SdkConfig};
use chrono::{DateTime, Utc};
use itertools::Itertools;
use thiserror::Error;

#[derive(Debug, Error)]
pub enum AWSError {
    #[error("Authorization token not provided")]
    AuthorizationTokenNotProvided,
}

#[derive(Debug)]
pub struct AWSCredentialProvider {
    client: Client,
}

impl AWSCredentialProvider {
    pub fn new(access_key_id: String, secret_key: String, region: String) -> Self {
        let cred_provider = SharedCredentialsProvider::new(Credentials::new(
            access_key_id,
            secret_key,
            None,
            None,
            "Polaris",
        ));
        let config = &SdkConfig::builder()
            .credentials_provider(cred_provider)
            .region(Region::new(region))
            .build();
        let client = Client::new(config);
        Self { client }
    }
}

#[async_trait::async_trait]
impl CacheableDockerCredentialProvider for AWSCredentialProvider {
    async fn provide(&self) -> Result<(DockerCredentials, Option<DateTime<Utc>>)> {
        let auth_token = self.client.get_authorization_token().send().await?;
        let vec = auth_token.authorization_data.unwrap_or_default();
        if !vec.is_empty() {
            let x = vec[0]
                .authorization_token
                .as_ref()
                .ok_or_else::<anyhow::Error, _>(|| {
                    AWSError::AuthorizationTokenNotProvided.into()
                })?;
            let decoded = base64::decode(x)?;
            let decoded = String::from_utf8(decoded)?;
            let split = decoded.split(':');
            let split = split.collect_vec();

            if split.len() >= 2 {
                Ok((
                    DockerCredentials {
                        username: Some(split[0].to_string()),
                        password: Some(split[1].to_string()),
                        auth: None,
                        email: None,
                        serveraddress: None,
                        identitytoken: None,
                        registrytoken: None,
                    },
                    vec[0].expires_at.map(|x| x.to_chrono_utc()),
                ))
            } else {
                Err(AWSError::AuthorizationTokenNotProvided.into())
            }
        } else {
            Err(AWSError::AuthorizationTokenNotProvided.into())
        }
    }
}
