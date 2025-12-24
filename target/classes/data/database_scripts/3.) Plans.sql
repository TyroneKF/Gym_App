
-- ###################################
-- Insert Into Plans
-- ###################################
START TRANSACTION;

	INSERT INTO plans 
	(
		date_time_of_creation, 
		user_id, 
		vegan, 
		plan_name

	) VALUES

	(now(), @active_user_id, FALSE, 'Temp_Plan'),
	(now(), @active_user_id, FALSE, 'Dummy Plan 1')

	ON DUPLICATE KEY UPDATE -- CHECKS if an insert is unique based on the tables PK and unique fields
		plan_id = LAST_INSERT_ID(plan_id); -- IF this row already exists based on constraints etc, not unique then set ID to last insert

	SET @plan_id := LAST_INSERT_IDXX(); -- Get last insert PK (plan_ID)

	-- ###################################
	-- SET ALL Plans to not SELECT
	-- ###################################
	UPDATE plan_versions
	SET is_selected_plan = FALSE
	WHERE is_selected_plan = TRUE;	
	
	-- ###################################
	-- Get Version Number for Plan
	-- ###################################
	SELECT 
		COALESCE(COUNT(plan_version_id), 0) + 1
	INTO @next_version
	FROM plan_versions
	WHERE plan_id = @plan_id	
	FOR UPDATE; -- Locks 
	
	-- ###################################
	-- INSERT into plans_Versions
	-- ###################################
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

COMMIT;


