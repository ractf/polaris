CREATE TABLE node (
     id SERIAL PRIMARY KEY NOT NULL,
     hostname VARCHAR(255) UNIQUE NOT NULL,
     total_ram BIGINT NOT NULL,
     total_cpu BIGINT NOT NULL,
     available_ram BIGINT NOT NULL,
     available_cpu BIGINT NOT NULL,
     schedulable BOOLEAN NOT NULL,
     version VARCHAR(255) NOT NULL,
     kernel_version VARCHAR(255) NOT NULL,
     public_ip VARCHAR(255) NOT NULL,
     bind_address VARCHAR(255) NOT NULL,
     last_updated TIMESTAMPTZ NOT NULL
);