ALTER TABLE token_events
ADD UNIQUE (token_id, event_id)
