
-- ###############################################################################
-- USERS | Seed DATA
-- ###############################################################################
    INSERT INTO users (user_name)
    SELECT
        '@USERNAME@'
    WHERE NOT EXISTS -- NOT EXISTS prevents creating a second active user
    (
        SELECT 1 FROM active_user
    )
    ON CONFLICT(user_name) -- In case of duplicate, ensures fields match correctly to new insert
        DO UPDATE SET
        user_name = excluded.user_name; -- In case of duplicate, ensures fields match correctly to new insert is

-- ###############################################################################
-- Active_USERS | Seed DATA
-- ###############################################################################
    INSERT INTO active_user(user_id)
    SELECT
        user_id
    FROM users
    WHERE user_name = '@USERNAME@'
    AND NOT EXISTS  -- NOT EXISTS prevents creating a second active user
    (
        SELECT 1 FROM active_user
    );

-- ###############################################################################
-- Insert Into Seed Registry Table
-- ###############################################################################
    INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
    VALUES
    (
        'active_user_id',
        'active_user',
        ( SELECT user_id FROM active_user )
    )
    ON CONFLICT(seed_key)   -- In case of duplicate, ensures fields match correctly to new insert
        DO UPDATE SET
        entity_id_value = excluded.entity_id_value; -- On update triggered by PK or unique Key

