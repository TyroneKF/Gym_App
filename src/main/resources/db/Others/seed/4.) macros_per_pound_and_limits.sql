

-- ######################################
-- Inserting Macro
-- ######################################
INSERT INTO macros_per_pound_and_limits (macros_ID) VALUES
(null);

SET @macros_id := LAST_INSERT_ID();
	
-- ######################################
-- Inserting Macro Document Version
-- ######################################
INSERT INTO macros_per_pound_and_limits_versions
(
	macros_ID,
	user_id,
	plan_version_id,
	
	date_time_of_creation, 
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

(@macros_id, @active_user_id, @plan_Version_id, now(), 1, 102.5, 225.5, 25, 1, 2, 30, 0.4, 30, 30, 5000, 400);
