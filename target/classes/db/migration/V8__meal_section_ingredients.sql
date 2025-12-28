-- #########################################################################
-- DDL SCRIPT | App Setup
-- #########################################################################

-- Ingredients are snapshot per sub-meal version; no independent ingredient versioning

CREATE TABLE ingredients_in_sections_of_meal
(
    ingredients_index INT PRIMARY KEY AUTO_INCREMENT,

    div_meal_sections_version_id INT NOT NULL,
		FOREIGN KEY (div_meal_sections_version_id) 
			REFERENCES divided_meal_sections_versions(div_meal_sections_version_id) 
				ON DELETE CASCADE,

    ingredient_id INT NOT NULL,
		FOREIGN KEY (ingredient_id) REFERENCES ingredients_info(ingredient_id) 
			ON DELETE CASCADE,

	quantity DECIMAL(15,2) NOT NULL,

	pdid INT DEFAULT NULL,
		FOREIGN KEY (pdid) 
			REFERENCES ingredient_in_shops(pdid)
			ON DELETE RESTRICT, -- Prevents deleting ingredient if product is being deleted, app logic will have to set this value to null first then delete parent
		 -- Needs to be manually removed when deleted as we don't want the whole row being deleted
		
	UNIQUE KEY no_repeat_records(ingredients_index, div_meal_sections_version_id),
	
	INDEX idx_ingredients_by_section (div_meal_sections_version_id),
	INDEX idx_ingredients_by_ingredient (ingredient_id),
	INDEX idx_ingredient_index (ingredients_index)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4;