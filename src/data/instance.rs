use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct ChallengeInstance {
    pub id: Option<i32>,
    pub allocation: i32,
    pub challenge: i32,
    pub ports: Vec<ChallengeInstancePort>,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct ChallengeInstancePort {
    pub id: Option<i32>,
    pub instance: i32,
    pub pod: i32,
    pub port: u16,
    pub internal_port: u16,
    pub advertise: bool,
    pub protocol: PortProtocol,
}

#[derive(Debug, Clone, Serialize, Deserialize, sqlx::Type)]
#[sqlx(type_name="port", rename_all="lowercase")]
pub enum PortProtocol {
    TCP,
    UDP,
}