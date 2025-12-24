-- #############################################################
--  Script INFO
-- #############################################################
/*
SEE if there is an active user already activated 
IF this condition is met, get that users ID
ELSE : Insert new ID / user into users DB

Set ID of retreived / last inserted ID into variable

*/

-- ##############################################################
--  Script
-- #############################################################

-- ####################################
-- INSERT into users if no active user
-- ####################################
INSERT INTO users 
(
	user_name, 
	is_user_selected
)
SELECT 
	'@USERNAME@', TRUE
FROM DUAL
WHERE NOT EXISTS (
	SELECT 1
	FROM users
	WHERE selected_user_flag = TRUE
	FOR UPDATE -- IF USer exists this user it is locked from being updated / deleted
	/*
		This puts a lock on the rows matching this condition, 
		other transactions cannot update / delete these rows.
		
		IF These rows don't exist other transactions cannot satify this condition by inserting a user_ID with TRUE
		
	*/
);

-- ####################################
-- SET active user ID
-- ####################################
SET @active_user_id := (SELECT user_id FROM users WHERE selected_user_flag = TRUE FOR UPDATE);



