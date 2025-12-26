
-- ###############################################################################
-- USERS | Seed DATA
-- ###############################################################################

-- INSERT into users if no active user
INSERT INTO users 
(
	user_name, 
	is_user_selected
)
SELECT 
	'@USERNAME@', 
	TRUE
FROM DUAL
WHERE NOT EXISTS ( -- NOT EXISTS prevents creating a second active user
    SELECT 1
    FROM users
    WHERE selected_user_flag = TRUE
)
ON DUPLICATE KEY UPDATE
    user_name = user_name; -- keeps the existing active user


-- #####################################################
-- Create Variable
-- #####################################################

-- SET active user ID
SELECT user_id
INTO @active_user_id
FROM users
WHERE selected_user_flag = TRUE
ORDER BY user_id
LIMIT 1;

-- Variable Validation
CALL assert_id_not_null(@active_user_id, 'Seed failed: no active user could be resolved');

-- Insert Into Seed Registry Table
INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
VALUES
    ('active_user_id', 'users' , @active_user_id)
AS new_vals
ON DUPLICATE KEY UPDATE -- In case of duplicate, ensures fields match correctly to new insert
	entity_id_value = new_vals.entity_id_value;


