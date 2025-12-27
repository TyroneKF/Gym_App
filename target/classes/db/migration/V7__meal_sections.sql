-- #########################################################################
-- DDL SCRIPT | App Setup
-- #########################################################################

-- Sections are snapshot per meal version; no independent section versioning

CREATE TABLE divided_meal_sections
(
   div_meal_sections_id INT PRIMARY KEY AUTO_INCREMENT   
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4;

CREATE TABLE divided_meal_sections_versions
(	
	div_meal_sections_version_id INT PRIMARY KEY AUTO_INCREMENT,

	div_meal_sections_id INT NOT NULL,
    		FOREIGN KEY (div_meal_sections_id)
    			REFERENCES divided_meal_sections(div_meal_sections_id)
    			ON DELETE CASCADE,

	meal_in_plan_version_id INT NOT NULL,
    		FOREIGN KEY (meal_in_plan_version_id)
    			REFERENCES meals_in_plan_versions(meal_in_plan_version_id)
    			ON DELETE CASCADE,

	date_time_last_edited DATETIME(6) NOT NULL,
	sub_meal_name VARCHAR(100), -- can be null
	
	UNIQUE KEY no_repeated_versions(div_meal_sections_id, meal_in_plan_version_id),
	INDEX idx_sections_meal_version (meal_in_plan_version_id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4;
