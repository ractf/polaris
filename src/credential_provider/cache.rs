use crate::credential_provider::{
    CacheableDockerCredentialProvider, DockerCredentialProvider, DockerCredentials,
};
use chrono::{DateTime, Utc};
use std::sync::Mutex;
use tracing::trace;

pub struct CachingDockerCredentialProvider {
    backing_provider: Box<dyn CacheableDockerCredentialProvider + Send + Sync>,
    cache: Mutex<Option<Cache>>,
}

impl CachingDockerCredentialProvider {
    pub fn new(
        provider: Box<dyn CacheableDockerCredentialProvider + Send + Sync>,
    ) -> CachingDockerCredentialProvider {
        CachingDockerCredentialProvider {
            backing_provider: provider,
            cache: Mutex::new(None),
        }
    }

    fn get_cached_value(&self) -> Option<Cache> {
        if let Ok(inner) = self.cache.lock() {
            if let Some(cache) = inner.as_ref() {
                return Some(cache.clone());
            }
        }
        None
    }

    fn insert_cache(&self, cache: Cache) {
        if let Ok(mut inner) = self.cache.lock() {
            *inner = Some(cache)
        }
    }
}

#[async_trait::async_trait]
impl DockerCredentialProvider for CachingDockerCredentialProvider {
    async fn provide(&self) -> anyhow::Result<DockerCredentials> {
        if let Some(cache) = self.get_cached_value() {
            if cache.expires > Utc::now() {
                trace!("Using cached credentials");
                return Ok(cache.value);
            }
            trace!("Cached credentials have expired");
        }
        let result = self.backing_provider.provide().await?;
        if let Some(expiry) = result.1 {
            let cache = Cache {
                expires: expiry,
                value: result.0.clone(),
            };
            trace!("Caching credentials until {}", expiry);
            self.insert_cache(cache);
        }

        Ok(result.0)
    }
}

#[derive(Clone)]
struct Cache {
    expires: DateTime<Utc>,
    value: DockerCredentials,
}
