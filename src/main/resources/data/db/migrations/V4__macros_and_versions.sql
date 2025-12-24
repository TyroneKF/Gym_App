-- #########################################################################
-- DDL SCRIPT | App Setup
-- #########################################################################


CREATE TABLE macros_per_pound_and_limits
(   	
    macros_ID INT PRIMARY KEY AUTO_INCREMENT,
	created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
);

CREATE TABLE macros_per_pound_and_limits_versions
(
	macros_version_id INT PRIMARY KEY AUTO_INCREMENT,	
	
	macros_ID INT NOT NULL,
		FOREIGN KEY (macros_ID) REFERENCES macros_per_pound_and_limits(macros_ID) 
			ON DELETE CASCADE,
	
	user_id INT NOT NULL,
		FOREIGN KEY (user_id) REFERENCES users(user_id) 
			ON DELETE CASCADE,
	
	plan_version_id INT NOT NULL,
		FOREIGN KEY (plan_version_id) REFERENCES plan_versions(plan_version_id) 
			ON DELETE CASCADE,		
	
	date_time_last_edited DATETIME(6) NOT NULL,
	version_number INT NOT NULL,	

	current_weight_kg DECIMAL(7,2) NOT NULL,
	current_weight_in_pounds DECIMAL(7,2) NOT NULL,
	body_fat_percentage DECIMAL(7,2) NOT NULL,
	protein_per_pound DECIMAL(7,2) NOT NULL,
	carbohydrates_per_pound DECIMAL(7,2) NOT NULL,
	fibre DECIMAL(7,2) NOT NULL,	
	fats_per_pound DECIMAL(7,2) NOT NULL,
	saturated_fat_limit DECIMAL(7,2) NOT NULL,	
	salt_limit DECIMAL(7,2) NOT NULL,
    water_target DECIMAL(7,2) NOT NULL,

	additional_calories DECIMAL(7,2) NOT NULL,
	
	UNIQUE KEY unique_macros_version (macros_ID, version_number),
	UNIQUE KEY unique_date_per_macros_in_plan (plan_version_id, date_time_last_edited),
	
	INDEX idx_macros_latest_per_plan (plan_version_id, date_time_last_edited DESC),
	INDEX idx_macros_user (user_id)
);