CREATE DATABASE IF NOT EXISTS nutri_scan;
USE nutri_scan;

CREATE TABLE users
(
    id UUID PRIMARY KEY,
    username      VARCHAR(100) UNIQUE NOT NULL,
    email         VARCHAR(255) UNIQUE NOT NULL,
    first_name    VARCHAR(100),
    last_name     VARCHAR(100),
    date_of_birth DATE,
    gender        VARCHAR(20) CHECK (gender IN ('MALE', 'FEMALE')),
    height_cm     DECIMAL(5, 2),
    weight_kg     DECIMAL(5, 2),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE allergies
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(150) UNIQUE NOT NULL
);

CREATE TABLE diseases
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(150) UNIQUE NOT NULL
);

CREATE TABLE scans
(
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    image_url  TEXT,
    status     VARCHAR(20) NOT NULL DEFAULT 'PROCESSING'
        CHECK (status IN ('PROCESSING', 'COMPLETED', 'FAILED')),
    verdict    VARCHAR(20) CHECK (verdict IN ('SAFE', 'CAUTION', 'UNSAFE')),
    summary    TEXT,
    scanned_at TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE
);

CREATE INDEX idx_scans_user_id ON scans (user_id);

CREATE TABLE nutrition_facts
(
    scan_id UUID PRIMARY KEY,
    serving_size         VARCHAR(50),
    calories_per_serving INT,
    sugar_g              DECIMAL(6, 2),
    fat_g                DECIMAL(6, 2),
    saturated_fat_g      DECIMAL(6, 2),
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (scan_id)
        REFERENCES scans (id)
        ON DELETE CASCADE
);

CREATE TABLE scan_flagged_ingredients
(
    id BIGSERIAL PRIMARY KEY,
    scan_id UUID NOT NULL,
    type            VARCHAR(30) CHECK (type IN ('ALLERGY', 'CHRONIC_CONDITION')),
    condition_name  VARCHAR(150),
    ingredient_name VARCHAR(150) NOT NULL,
    reason          TEXT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (scan_id)
        REFERENCES scans (id)
        ON DELETE CASCADE
);

CREATE INDEX idx_scan_flagged_ingredients_scan_id ON scan_flagged_ingredients (scan_id);

CREATE TABLE user_diseases
(
    user_id UUID NOT NULL,
    disease_id INT NOT NULL,
    PRIMARY KEY (user_id, disease_id),
    FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE,
    FOREIGN KEY (disease_id)
        REFERENCES diseases (id)
);

CREATE TABLE user_allergies
(
    user_id UUID NOT NULL,
    allergy_id INT NOT NULL,
    PRIMARY KEY (user_id, allergy_id),
    FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE,
    FOREIGN KEY (allergy_id)
        REFERENCES allergies (id)
);