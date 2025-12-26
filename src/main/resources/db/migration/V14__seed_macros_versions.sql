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

-- Get Active Plan Version From Seed
SELECT entity_id_value
INTO @plan_Version_id
FROM seed_registry
WHERE seed_key = 'plan_Version_id';

-- Variable Validation
CALL assert_id_not_null(@plan_Version_id, 'Seed failed: @plan_Version_id could be resolved');

-- ###############################################################################
-- Insert Into Macro
-- ###############################################################################
-- Inserting Macro
INSERT INTO macros_per_pound_and_limits (macros_ID) VALUES
(null);

SET @macros_id := LAST_INSERT_ID();

-- Validate Variable
CALL assert_id_not_null(@macros_id, 'Seed failed: macros_per_pound_and_limits @macros_id could not be resolved');

-- ###############################################################################
-- Inserting Macro Versions
-- ###############################################################################
INSERT INTO macros_per_pound_and_limits_versions
(
	macros_ID,
	user_id,
	plan_version_id,
	
	date_time_last_edited, 
	version_number,
	
	current_weight_kg, 
	current_weight_in_pounds, 
	body_fat_percentage, 
	protein_per_pound, 
	carbohydrates_per_pound, 
	fibre, fats_per_pound, 
	saturated_fat_limit, 
	salt_limit, 
	water_target,
	
	additional_calories
	
)
VALUES
(@macros_id, @active_user_id, @plan_Version_id, NOW(6), 1, 102.5, 225.5, 25, 1, 2, 30, 0.4, 30, 30, 5000, 400);

-- ######################################
-- Create Variable
-- ######################################

-- Set Macros Version ID
SET @macros_version_id := LAST_INSERT_ID();

-- Validate Variable
CALL assert_id_not_null(@macros_version_id, 'Seed failed: macros_per_pound_and_limits @macros_version_id could not be resolved');

-- Insert Into Seed Registry Table
INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
VALUES
    ('macros_version_id', 'macros_per_pound_and_limits_versions' , @macros_version_id)
AS new_vals
ON DUPLICATE KEY UPDATE -- In case of duplicate, ensures fields match correctly to new insert
	entity_id_value = new_vals.entity_id_value;

