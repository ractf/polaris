CREATE TABLE event (
    id SERIAL PRIMARY KEY NOT NULL,
    name VARCHAR(255) UNIQUE NOT NULL,
    start_time TIMESTAMPTZ,
    end_time TIMESTAMPTZ,
    max_cpu BIGINT,
    max_ram BIGINT,
    api_url VARCHAR(255),
    api_token VARCHAR(255)
)