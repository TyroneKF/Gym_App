
-- ##################################################
-- Insert Into Plans
-- ##################################################
INSERT INTO plans 
(
	is_system,
	date_time_of_creation, 
	user_id, 
	vegan, 
	plan_name

) VALUES

(TRUE,  NOW(6), @active_user_id, FALSE, 'Temp_Plan'),
(FALSE, NOW(6), @active_user_id, FALSE, 'Dummy Plan 1')

ON DUPLICATE KEY UPDATE -- 
	is_system = is_system;

-- ######################################
	-- Variable Setting FOR N/A
-- ######################################

SELECT ingredient_id
INTO @na_type_id
FROM ingredients_info
WHERE ingredient_name = 'None Of The Above'
LIMIT 1;

-- ##############################################
-- SET ALL Plans to not SELECT
-- ##############################################
UPDATE plan_versions
SET is_selected_plan = FALSE
WHERE is_selected_plan = TRUE;	

-- #############################################
-- Get Version Number for Plan
-- #############################################
SELECT 
	COALESCE(COUNT(plan_version_id), 0) + 1
INTO @next_version
FROM plan_versions
WHERE plan_id = @plan_id	
FOR UPDATE; -- Locks 

-- #############################################
-- INSERT into plans_Versions
-- #############################################

INSERT INTO plan_versions
(
	plan_id, 
	user_id, 
	version_number, 
	date_time_of_last_edited, 
	is_selected_plan
	
) VALUES
(@plan_id, @active_user_id, @next_version,  now(), TRUE);

-- ###################################
-- SET Plan_Version_ID
-- ###################################
SET @plan_Version_id := LAST_INSERT_ID(); -- Get last insert PK (plan_Version_ID)




