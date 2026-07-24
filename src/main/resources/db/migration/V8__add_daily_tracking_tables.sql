CREATE TABLE daily_tracking
(
    id       SERIAL PRIMARY KEY,
    user_id  UUID    NOT NULL,
    date     DATE    NOT NULL,
    target_water_cnt INTEGER NOT NULL DEFAULT 8,
    water_cnt INTEGER NOT NULL DEFAULT 0,
    steps_cnt INTEGER,

    CONSTRAINT uk_daily_user_date UNIQUE (user_id, date),

    CONSTRAINT fk_users_daily FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE daily_tracking_meals
(
    daily_id INTEGER NOT NULL,
    scan_id  UUID NOT NULL,
    meal_cnt  INTEGER NOT NULL,

    PRIMARY KEY (daily_id, scan_id),

    CONSTRAINT fk_daily_daily_meals FOREIGN KEY (daily_id) REFERENCES daily_tracking (id),

    CONSTRAINT fk_scans_daily_meals FOREIGN KEY (scan_id) REFERENCES scans (id)
);

ALTER TABLE scans
ADD COLUMN favorite BOOLEAN NOT NULL DEFAULT FALSE;

CREATE INDEX idx_daily_date ON daily_tracking(date);