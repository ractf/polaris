use serde::Serialize;

#[derive(Serialize)]
#[serde(tag = "type")]
pub enum APIError {
    DatabaseError,
    InvalidField { field: String },
    MissingPermission { permission: String },
    NameTaken { name: String },
    EventNotFound { event: i32 },
    ResourceNotFound { id: i32 },
}

impl APIError {
    pub fn invalid_field<S: ToString>(field: S) -> APIError {
        APIError::InvalidField {
            field: field.to_string(),
        }
    }

    pub fn missing_permission<S: ToString>(permission: S) -> APIError {
        APIError::MissingPermission {
            permission: permission.to_string(),
        }
    }

    pub fn name_taken<S: ToString>(name: S) -> APIError {
        APIError::NameTaken {
            name: name.to_string(),
        }
    }

    pub fn event_not_found(event: i32) -> APIError {
        APIError::EventNotFound { event }
    }

    pub fn resource_not_found(id: i32) -> APIError {
        APIError::ResourceNotFound { id }
    }
}
