CREATE TABLE elasticsearch_sync
(
    id           UUID PRIMARY KEY,
    entity_type  VARCHAR(50) NOT NULL,
    entity_id    UUID        NOT NULL,
    operation    VARCHAR(20) NOT NULL,
    created_at   TIMESTAMP   NOT NULL DEFAULT NOW(),
    processed    BOOLEAN     NOT NULL DEFAULT FALSE,
    processed_at TIMESTAMP,
    retry_count  INTEGER     NOT NULL DEFAULT 0,
    last_error   TEXT
);

CREATE INDEX idx_elastic_sync_processed
    ON elasticsearch_sync (processed);

CREATE INDEX idx_elastic_sync_entity
    ON elasticsearch_sync (entity_type, entity_id);