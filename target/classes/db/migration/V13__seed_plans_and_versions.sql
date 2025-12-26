-- ###############################################################################
-- Set Variables
-- ###############################################################################

-- Get Active User From Seed
SELECT entity_id_value
INTO @active_user_id
FROM seed_registry
WHERE seed_key = 'active_user_id';

-- Variable Validation
CALL assert_id_not_null(@active_user_id, 'Seed failed: no active user could be resolved');

-- ###############################################################################
-- Plans | Seed DATA
-- ###############################################################################

-- Create Variable for plan name
set @plan_name := 'My Daily Gym Diet'; -- Plan Name

-- #####################################################
-- Insert / Get Plan Data if Exists
-- #####################################################
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

-- #####################################################
-- Create Variable For Plan ID
-- #####################################################

-- Get plan_id for Plan
SELECT plan_id 
INTO @plan_id
FROM plans
WHERE user_id = @active_user_id 
	AND plan_name = @plan_name
LIMIT 1;

-- Validate Variable
CALL assert_id_not_null(@plan_id, 'Seed failed: plan @plan_id could not be resolved');

-- Insert Into Seed Registry Table
INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
VALUES
    ('plan_id', 'plans' , @plan_id)
AS new_vals
ON DUPLICATE KEY UPDATE -- In case of duplicate, ensures fields match correctly to new insert
	entity_id_value = new_vals.entity_id_value;

-- ###############################################################################
--  Plan Versions Insert
-- ###############################################################################

-- GET MAX Version Number for Plan_version for chosen meal plan
SELECT COALESCE(MAX(version_number), 0) + 1
INTO @next_version
FROM plan_versions
WHERE plan_id = @plan_id 
	AND user_id = @active_user_id; -- Locks

-- Validate Version ID Variable
CALL assert_id_not_null(@next_version, 'Seed failed: plans @next_version could not be resolved');

-- #################################
-- Insert Plan Version
-- #################################
INSERT INTO plan_versions
(
	plan_id, 
	user_id, 
	version_number, 
	date_time_last_edited, 
	is_selected_plan
)
VALUES
(@plan_id, @active_user_id, @next_version, now(6), FALSE);

-- #################################
-- Create Variable
-- #################################

 -- Set Variable ID
SET @plan_Version_id := LAST_INSERT_ID(); -- Get last insert PK (plan_Version_ID)

-- Insert Into Seed Data
INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
VALUES
    ('plan_Version_id', 'plan_versions' , @plan_Version_id)
AS new_vals
ON DUPLICATE KEY UPDATE -- In case of duplicate, ensures fields match correctly to new insert
	entity_id_value = new_vals.entity_id_value;