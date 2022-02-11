use std::fmt::{Display, Formatter};
use reqwest::Error;
use crate::api::error::APIError;

pub type Result<T> = std::result::Result<T, PolarisError>;

/// Errors to handle when interacting with the polaris API
#[derive(Debug)]
pub enum PolarisError {
    APIError(APIError),
    UnknownError(anyhow::Error),
}

impl Display for PolarisError {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        match &self {
            PolarisError::APIError(error) => {
                writeln!(f, "{:?}", error)?;
            }
            PolarisError::UnknownError(error) => {
                writeln!(f, "{}", error)?;
            }
        }
        Ok(())
    }
}

impl std::error::Error for PolarisError {}

impl std::convert::From<reqwest::Error> for PolarisError {
    fn from(error: Error) -> Self {
        Self::UnknownError(error.into())
    }
}
