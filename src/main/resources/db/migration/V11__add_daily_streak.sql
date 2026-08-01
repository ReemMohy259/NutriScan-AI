ALTER TABLE users
    ADD COLUMN daily_streak INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN last_active_date DATE;