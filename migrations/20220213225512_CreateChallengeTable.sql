CREATE TABLE challenge (
    id SERIAL PRIMARY KEY NOT NULL,
    name VARCHAR(255) UNIQUE NOT NULL,
    event_id INT NOT NULL,
    replication JSONB NOT NULL,
    allocation JSONB NOT NULL,
    CONSTRAINT fk_event FOREIGN KEY(event_id) REFERENCES event(id) ON DELETE CASCADE
);

CREATE TABLE pod (
    id SERIAL PRIMARY KEY NOT NULL,
    challenge_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    max_cpu BIGINT NOT NULL,
    max_ram BIGINT NOT NULL,
    pod JSONB NOT NULL,
    CONSTRAINT fk_challenge FOREIGN KEY(challenge_id) REFERENCES challenge(id) ON DELETE CASCADE,
    CONSTRAINT unique_name UNIQUE (name, challenge_id)
);
