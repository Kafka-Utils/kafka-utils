ALTER TABLE cluster
    ADD COLUMN request_timeout_ms BIGINT NOT NULL DEFAULT 5000