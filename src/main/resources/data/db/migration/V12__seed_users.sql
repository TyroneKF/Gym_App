
-- ###############################################################################
-- USERS | Seed DATA
-- ###############################################################################

START TRANSACTION;

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


-- ####################################
-- SET active user ID
-- ####################################

-- Lock and resolve active user
SELECT user_id
INTO @active_user_id
FROM users
WHERE selected_user_flag = TRUE
ORDER BY user_id
LIMIT 1
FOR UPDATE;
/*
	FOR UPDATE:
	This puts a lock on the rows matching this condition, 
	other transactions cannot update / delete these rows until released by transaction.
*/

-- #####################################################
-- Variable Validation 
-- #####################################################
CALL assert_id_not_null(@active_user_id, 'Seed failed: no active user could be resolved');

COMMIT;