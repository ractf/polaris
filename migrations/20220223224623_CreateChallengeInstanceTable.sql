CREATE TYPE protocol AS ENUM ('tcp', 'udp');

CREATE TABLE challenge_instance (
    id SERIAL PRIMARY KEY NOT NULL,
    allocation INT NOT NULL,
    challenge INT NOT NULL,
    CONSTRAINT fk_allocation_id FOREIGN KEY(allocation) REFERENCES allocation(id) ON DELETE CASCADE,
    CONSTRAINT fk_challenge_id FOREIGN KEY(challenge) REFERENCES challenge(id) ON DELETE CASCADE
);

CREATE TABLE challenge_instance_port (
    id SERIAL PRIMARY KEY NOT NULL,
    instance INT NOT NULL,
    pod INT NOT NULL,
    port INT NOT NULL,
    internal_port INT NOT NULL,
    advertise BOOLEAN NOT NULL,
    protocol protocol NOT NULL,
    CONSTRAINT fk_instance_id FOREIGN KEY(instance) REFERENCES challenge_instance(id) ON DELETE CASCADE,
    CONSTRAINT fk_pod_id FOREIGN KEY(pod) REFERENCES pod(id) ON DELETE CASCADE
);
