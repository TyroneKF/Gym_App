-- #############################################################
--  Script INFO
-- #############################################################
/*
  Insert 

*/

-- ##############################################################
-- INSERT into users if no active user
-- #############################################################
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
FOR UPDATE
LIMIT 1;

/*
	FOR UPDATE:
	This puts a lock on the rows matching this condition, 
	other transactions cannot update / delete these rows until released by transaction.
*/

-- #####################################################
-- Variable Validation 
-- #####################################################
DO
CASE
    WHEN @active_user_id IS NULL
    THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Seed failed: no active user could be resolved';
END CASE;
