
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

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'active_user_id',
    'users' ,
    @active_user_id,
    'Seed failed: no active user could be resolved'
);

