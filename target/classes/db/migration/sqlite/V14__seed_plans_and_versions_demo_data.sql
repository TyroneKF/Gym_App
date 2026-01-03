-- ###############################################################################
-- Plans | Seed DATA
-- ###############################################################################

    INSERT INTO plans   -- Insert / Get Plan Data if Exists
    (
        user_id,
        plan_name
    )
    SELECT

        (SELECT user_id FROM active_user),
        'My Daily Gym Diet'

    WHERE NOT EXISTS   -- NOT EXISTS prevents creating a second plan with the same details = error
    (
        SELECT 1
        FROM plans
        WHERE
            user_id = (SELECT user_id FROM active_user)
            AND plan_name = 'My Daily Gym Diet'
    );

    -- #####################################################
    -- Insert Into Seed Registry Table
    -- #####################################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        SELECT
            'plan_id',
            'plans',
            (
                SELECT plan_id
                FROM plans
                WHERE
                    user_id = (SELECT user_id FROM active_user LIMIT 1)
                    AND plan_name = 'My Daily Gym Diet'
            )
        ON CONFLICT(seed_key)   -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            entity_id_value = excluded.entity_id_value; -- On update triggered by PK or unique Key


-- ###############################################################################
--  Plan Versions Insert
-- ###############################################################################
    WITH
        u_id AS (SELECT user_id FROM active_user), -- user_id
        p_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'plan_id'), -- plan_id

        v_no AS ( -- Version_Number

            SELECT
                COALESCE(MAX(version_number), 0) + 1 AS version_number
            FROM plan_versions
            WHERE
                plan_id = (SELECT entity_id_value FROM p_id)
                AND user_id = (SELECT user_id FROM u_id)
        )

    INSERT INTO plan_versions
    (
        plan_id,
        user_id,
        version_number
    )
    VALUES
    (
        (SELECT entity_id_value FROM p_id),
        (SELECT user_id FROM u_id),
        (SELECT version_number FROM v_no)
    );

    -- ###########################################
    -- Insert Into Seed Registry Table
    -- ###########################################
    WITH
            u_id AS (SELECT user_id FROM active_user), -- user_id
            p_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'plan_id'), -- plan_id

            v_no AS ( -- Version_Number

                SELECT
                    MAX(version_number)  AS version_number
                FROM plan_versions
                WHERE
                    plan_id = (SELECT entity_id_value FROM p_id)
                    AND user_id = (SELECT user_id FROM u_id)
            ),

            id AS ( -- Last plan_version_id (last insert)

                SELECT plan_version_id
                FROM plan_versions
                WHERE
                    plan_id = (SELECT entity_id_value FROM p_id)
                    AND user_id = (SELECT user_id FROM u_id)
                    AND version_number = (SELECT version_number FROM v_no)
            )

    INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
    VALUES
    (
         'plan_version_id',
         'plan_versions',
         (SELECT plan_version_id FROM id)
    )
    ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
        DO UPDATE SET
        entity_id_value = excluded.entity_id_value; -- On update triggered by PK or unique Key

-- ###############################################################################
--  Change Plan Active to Current Plan
-- ###############################################################################

    -- If user has another active plan, set it to this one
    INSERT INTO active_plans
    (
        plan_version_id,
        user_id
    )
    SELECT

        (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'plan_version_id'),
        (SELECT user_id FROM active_user )

    ON CONFLICT(user_id)  -- In case of duplicate, ensures fields match correctly to new insert
        DO UPDATE SET
        plan_version_id = excluded.plan_version_id; -- On update has to be on the the PK or unique Key
