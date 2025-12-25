-- ######################################
-- Inserting Macro
-- ######################################
INSERT INTO macros_per_pound_and_limits (macros_ID) VALUES
(null);

SET @macros_id := LAST_INSERT_ID();

-- Validate Variable
CALL assert_id_not_null(@macros_id, 'Seed failed: macros_per_pound_and_limits @macros_id could not be resolved');


-- ######################################
-- Inserting Macro Document Version
-- ######################################
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
	
) VALUES

(@macros_id, @active_user_id, @plan_Version_id, NOW(6), 1, 102.5, 225.5, 25, 1, 2, 30, 0.4, 30, 30, 5000, 400);

-- Set Macros Version ID
SET @macros_version_id := LAST_INSERT_ID();

-- Validate Variable
CALL assert_id_not_null(@macros_version_id, 'Seed failed: macros_per_pound_and_limits @macros_version_id could not be resolved');
