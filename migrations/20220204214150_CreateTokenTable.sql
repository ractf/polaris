CREATE TABLE token (
    id SERIAL PRIMARY KEY NOT NULL,
    name VARCHAR(255) UNIQUE NOT NULL,
    token VARCHAR(255) UNIQUE NOT NULL,
    permissions text[] NOT NULL,
    issued timestamptz,
    expiry timestamptz
);

CREATE TABLE token_events (
    id SERIAL PRIMARY KEY NOT NULL,
    token_id INT NOT NULL,
    event_id INT NOT NULL,
    CONSTRAINT fk_token FOREIGN KEY(token_id) REFERENCES token(id),
    CONSTRAINT fk_event FOREIGN KEY(event_id) REFERENCES event(id)
)