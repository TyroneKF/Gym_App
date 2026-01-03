
-- ###############################################################################
-- Inserting into Macro
-- ###############################################################################
    WITH
        u_id AS (SELECT user_id FROM active_user), -- user_id

        t_no AS ( -- target_number
            SELECT COALESCE(MAX(target_number), 0) + 1 AS target_number
            FROM macros_per_pound_and_limits
            WHERE user_id = (SELECT user_id FROM u_id)
        )

    INSERT INTO macros_per_pound_and_limits (user_id, target_number)
    VALUES
    (
        (SELECT user_id FROM u_id),
        (SELECT target_number FROM t_no)
    );

-- ###############################################################################
-- Inserting into Macro Versions
-- ###############################################################################
    WITH
        u_id AS (SELECT user_id FROM active_user), -- user_id
        pv_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'plan_version_id'), -- plan_version_id

        t_no AS (   -- target_number
        
            SELECT MAX(target_number) AS target_number
            FROM macros_per_pound_and_limits
            WHERE
                user_id = (SELECT user_id FROM u_id)
        ),

        macro_id AS (

           SELECT macros_id
           FROM macros_per_pound_and_limits
           WHERE
                user_id = (SELECT user_id FROM u_id)
                AND target_number = (SELECT target_number FROM t_no)
        )

    INSERT INTO macros_per_pound_and_limits_versions
    (
        macros_id,
        plan_version_id,

        version_number,

        current_weight_kg,
        current_weight_in_pounds,
        body_fat_percentage,
        protein_per_pound,
        carbohydrates_per_pound,
        fibre, fats_per_pound,
        saturated_fat_limit,
        salt_limit,
        water_target,

        additional_calories
    )
    VALUES
    (
        (SELECT macros_id FROM macro_id),
        (SELECT entity_id_value FROM pv_id),

        1,

        102.5, 225.5, 25, 1, 2, 30, 0.4, 30, 30, 5000,

        400
    );