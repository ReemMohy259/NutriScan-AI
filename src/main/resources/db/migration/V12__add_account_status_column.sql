ALTER TABLE users
    ADD COLUMN account_status VARCHAR NOT NULL DEFAULT 'ACTIVE';

ALTER TABLE users
    ADD COLUMN deletion_requested_at TIMESTAMP;

ALTER TABLE users
    ADD CONSTRAINT chk_users_account_status
        CHECK (account_status IN ('ACTIVE', 'PENDING_DELETION'));