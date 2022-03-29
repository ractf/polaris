CREATE TABLE allowed_registry_token (
      id SERIAL PRIMARY KEY NOT NULL,
      registry_token_id INT NOT NULL,
      event_id INT NOT NULL,
      CONSTRAINT fk_token FOREIGN KEY(registry_token_id) REFERENCES registry_token(id),
      CONSTRAINT fk_registry_token FOREIGN KEY(event_id) REFERENCES event(id)
)