//! Database models for storing docker registry tokens

use serde::{Deserialize, Serialize};

/// Information required to generate a set of docker registry credentials
#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct RegistryToken {
    /// The id in the database
    pub id: Option<i32>,
    /// The name of the token
    pub name: String,
    /// The information used to generate credentials
    pub inner: RegistryTokenInner,
}

/// Information required to generate a set of docker registry credentials
#[derive(Debug, Serialize, Deserialize, Clone)]
pub enum RegistryTokenInner {
    /// Standard docker credentials
    Standard,
    /// AWS access keys that can generate docker registry credentials
    AWS(AWSAccessToken),
}

/// AWS access keys that can generate docker registry credentials
#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct AWSAccessToken {
    /// The AWS access key id
    access_key_id: String,
    /// The AWS secret key
    secret_key: String,
    /// The AWS region the token is valid for
    region: String,
}
