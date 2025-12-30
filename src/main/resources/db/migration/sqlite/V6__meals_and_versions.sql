-- #########################################################################
-- DDL SCRIPT | App Setup
-- #########################################################################
/*

*/

-- ####################################################
-- Main Document
-- ####################################################
CREATE TABLE meals_in_plan
(
   meal_in_plan_id INT PRIMARY KEY AUTO_INCREMENT,
   date_time_of_creation DATETIME(6) NOT NULL	   
);

-- ####################################################
-- Document Versions
-- ####################################################
CREATE TABLE meals_in_plan_versions
(
	meal_in_plan_version_id INT PRIMARY KEY AUTO_INCREMENT,	
	
	meal_in_plan_id INT NOT NULL,
		FOREIGN KEY (meal_in_plan_id) REFERENCES meals_in_plan(meal_in_plan_id) 
			ON DELETE CASCADE,

	plan_version_id INT NOT NULL,
		FOREIGN KEY (plan_version_id) REFERENCES plan_versions(plan_version_id) 
			ON DELETE CASCADE,

	date_time_last_edited DATETIME(6) NOT NULL,

	meal_name VARCHAR(100) NOT NULL,
	meal_time TIME NOT NULL,

	UNIQUE KEY unique_versions_per_plan (meal_in_plan_id, plan_version_id), -- one version of a meal per plan version
	UNIQUE KEY no_repeat_meal_times_in_plan(plan_version_id, meal_time), -- Only one meal can be at one time
	UNIQUE KEY no_repeat_meal_names_in_plan(plan_version_id, meal_name), -- can't have 2 of the same meal_names in a plan,
	
	INDEX idx_meals_by_plan (plan_version_id, meal_time),
	INDEX idx_meal_versions_plan_version (plan_version_id)
);

