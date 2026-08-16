
CREATE TABLE scan_family_alert
(
    id BIGSERIAL PRIMARY KEY,
    scan_id UUID NOT NULL,
    verdict            VARCHAR(20) CHECK (verdict IN ('UNSAFE', 'CAUTION')),
    target_profile  VARCHAR(60),
    reason          TEXT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (scan_id)
        REFERENCES scans (id)
        ON DELETE CASCADE
);