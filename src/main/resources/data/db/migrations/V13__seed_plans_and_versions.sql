START TRANSACTION;

-- ###############################################################################
-- Plans | Seed DATA
-- ###############################################################################

-- Plan Name 
set @plan_name := 'My Daily Gym Diet';

-- Insert / Get Plan Data if Exists
INSERT INTO plans  
(
	date_time_of_creation, 
	user_id, 
	vegan, 
	plan_name
)
SELECT 

	NOW(6), 
	@active_user_id,
	FALSE,
	@plan_name
	
FROM DUAL
WHERE NOT EXISTS ( -- NOT EXISTS prevents creating a second plan with the same details = error
    
	SELECT 1
    FROM plans
    WHERE user_id = @active_user_id 
		AND plan_name = @plan_name
		
);

-- Get plan_id for Plan
SELECT plan_id 
INTO @plan_id
FROM plans
WHERE user_id = @active_user_id 
	AND plan_name = @plan_name
LIMIT 1;

-- #############################################
-- Temp Plan
-- #############################################

-- Temp Plan Name 
SET @temp_plan_name := CONCAT('Temp : ', @plan_name);

-- Insert / Get Temp Plan Data if Exists
INSERT INTO plans  
(
	date_time_of_creation, 
	user_id, 
	vegan, 
	plan_name
)
SELECT 

	NOW(6), 
	@active_user_id,
	FALSE,
	@temp_plan_name
	
FROM DUAL
WHERE NOT EXISTS ( -- NOT EXISTS prevents creating a second plan with the same details = error
    
	SELECT 1
    FROM plans
    WHERE user_id = @active_user_id 
		AND plan_name = @temp_plan_name
		
);

-- Get plan_id for Plan
SELECT plan_id
INTO @temp_plan_id
FROM plans
WHERE user_id = @active_user_id 
	AND plan_name = @temp_plan_name
LIMIT 1;

-- DELETE ALL potential temp edit versions
DELETE FROM plan_versions WHERE plan_id = @temp_plan_id;

-- ###############################################################################
--  Plan Versions
-- ###############################################################################

-- GET MAX Version Number for Plan
SELECT COALESCE(MAX(version_number), 0) + 1
INTO @next_version
FROM plan_versions
WHERE plan_id = @plan_id AND user_id = @active_user_id
FOR UPDATE; -- Locks 

-- Insert Into Plan Version
INSERT INTO plan_versions
(
	plan_id, 
	user_id, 
	version_number, 
	date_time_last_edited, 
	is_selected_plan
	
) VALUES
(@plan_id, @active_user_id, @next_version, now(6), FALSE);

-- Get Plan_Version_ID
SET @plan_Version_id := LAST_INSERT_ID(); -- Get last insert PK (plan_Version_ID)

-- #######################################
-- Temp Plan Versions
-- #######################################

-- SET Plans To False
UPDATE plan_versions
SET 
	is_selected_plan = FALSE,
	date_time_last_edited = NOW(6)
WHERE is_selected_plan = TRUE 
	AND user_id = @active_user_id;


INSERT INTO plan_versions
(
	plan_id, 
	user_id, 
	version_number, 
	date_time_last_edited, 
	is_selected_plan
	
) VALUES
(@temp_plan_id, @active_user_id, 1,  now(6), TRUE);

-- Get Plan_Version_ID
SET @plan_temp_version_id := LAST_INSERT_ID(); -- Get last insert PK (plan_Version_ID)


COMMIT;