use crate::api::error::APIError;
use reqwest::Error;
use std::fmt::{Display, Formatter};

pub type Result<T> = std::result::Result<T, PolarisError>;

/// Errors to handle when interacting with the polaris API
#[derive(Debug)]
pub enum PolarisError {
    APIError(APIError),
    UnknownError(anyhow::Error),
    Unauthorized,
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

            PolarisError::Unauthorized => {
                writeln!(f, "Permission denied.")?;
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
