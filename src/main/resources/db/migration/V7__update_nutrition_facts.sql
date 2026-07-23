ALTER TABLE nutrition_facts
    RENAME COLUMN calories_per_serving TO calories;

ALTER TABLE nutrition_facts
    ADD COLUMN protein_g DECIMAL(6,2),
    ADD COLUMN carbs_g DECIMAL(6,2),
    ADD COLUMN fiber_g DECIMAL(6,2),
    ADD COLUMN sodium_mg DECIMAL(8,2);

ALTER TABLE nutrition_facts
DROP COLUMN serving_size,
DROP COLUMN saturated_fat_g;