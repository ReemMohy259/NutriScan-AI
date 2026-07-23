CREATE TABLE family_members
(
    id UUID PRIMARY KEY,

    user_id UUID NOT NULL,

    name VARCHAR(150) NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_family_members_user
    ON family_members(user_id);

CREATE TABLE family_member_allergies
(
    family_member_id UUID NOT NULL,

    allergy_id INT NOT NULL,

    PRIMARY KEY (family_member_id, allergy_id),

    FOREIGN KEY (family_member_id)
        REFERENCES family_members(id)
        ON DELETE CASCADE,

    FOREIGN KEY (allergy_id)
        REFERENCES allergies(id)
);

CREATE TABLE family_member_diseases
(
    family_member_id UUID NOT NULL,

    disease_id INT NOT NULL,

    PRIMARY KEY (family_member_id, disease_id),

    FOREIGN KEY (family_member_id)
        REFERENCES family_members(id)
        ON DELETE CASCADE,

    FOREIGN KEY (disease_id)
        REFERENCES diseases(id)
);