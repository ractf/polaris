CREATE TABLE allocation (
    id SERIAL PRIMARY KEY NOT NULL,
    node_id INT NOT NULL,
    start_time TIMESTAMPTZ NOT NULL,
    end_time TIMESTAMPTZ,
    service BOOLEAN NOT NULL,
    completed BOOLEAN NOT NULL,
    cpu BIGINT NOT NULL,
    ram BIGINT NOT NULL,
    CONSTRAINT fk_node_id FOREIGN KEY(node_id) REFERENCES node(id) ON DELETE CASCADE
)
