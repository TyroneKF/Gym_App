
-- ###############################################################################
-- Inserting into Macro Versions
-- ###############################################################################
    WITH
        u_id AS (SELECT user_id FROM active_user), -- user_id
        pv_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'plan_version_id') -- plan_version_id

    INSERT INTO macros_per_pound_and_limits
    (
        user_id,
        plan_version_id,

        current_weight_kg,
        current_weight_in_pounds,
        body_fat_percentage,
        protein_per_pound,
        carbohydrates_per_pound,
        fibre,
        fats_per_pound,
        saturated_fat_limit,
        salt_limit,
        water_target,

        additional_calories
    )
    VALUES
    (
        (SELECT user_id FROM u_id),  -- user_id
        (SELECT entity_id_value FROM pv_id), -- plan_version_id

        102.5,   -- current_weight_kg
        225.5,   -- current_weight_in_pounds
        25,      -- body_fat_percentage
        1,       -- protein_per_pound
        2,       -- carbohydrates_per_pound
        30,      -- fibre
        0.4,     -- fats_per_pound
        30,      -- saturated_fat_limit
        30,      -- salt_limit
        5000,    -- water_target,
        400      -- additional_calories
    );