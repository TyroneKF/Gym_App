-- #########################################################################
-- DDL SCRIPT | App Setup
-- #########################################################################

CREATE TABLE ingredients_info
(
    -- PRIMARY KEYS
    ingredient_id INT  PRIMARY KEY AUTO_INCREMENT,
	is_system BOOLEAN NOT NULL DEFAULT FALSE,
	
	measurement_id INT NOT NULL,
	FOREIGN KEY (measurement_id) 
		REFERENCES measurements(measurement_id)
		ON DELETE RESTRICT, -- Re-assign meassurment if FK meassurment is deleted vs Deleting ingredient too,
		
	ingredient_name VARCHAR(100) NOT NULL,
	
	ingredient_type_id  INT NOT NULL,
	FOREIGN KEY (ingredient_type_id) 
		REFERENCES ingredient_types(ingredient_type_id) 
		ON DELETE RESTRICT, -- Re-assign ingredient Type if FK Type is deleted vs Deleting ingredient too,

	based_on_quantity DECIMAL(7,2) NOT NULL,
    glycemic_index INT NOT NULL,
	protein DECIMAL(7,2) NOT NULL,
	carbohydrates DECIMAL(7,2) NOT NULL,
	sugars_of_carbs DECIMAL(7,2) NOT NULL,
	fibre DECIMAL(7,2) NOT NULL,
	fat DECIMAL(7,2) NOT NULL,
	saturated_fat DECIMAL(7,2) NOT NULL,
	salt DECIMAL(7,2) NOT NULL,
	water_content DECIMAL(7,2) NOT NULL,
	liquid_content DECIMAL(7,2) NOT NULL, 
	calories DECIMAL(7,2) NOT NULL,

	UNIQUE KEY no_repeat_ingredient_names(ingredient_name)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4;

-- ######################################
CREATE TABLE stores
(
    -- PRIMARY KEYS
    store_id INT  PRIMARY KEY AUTO_INCREMENT,
	is_system BOOLEAN NOT NULL DEFAULT FALSE,
	
	store_name VARCHAR(255) NOT NULL,
	UNIQUE KEY no_repeat_store_names (store_name)

)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4;
-- ######################################

CREATE TABLE ingredient_in_shops
(
    -- PRIMARY KEY , UNIQUE To this Table
    pdid INT  PRIMARY KEY AUTO_INCREMENT,
	is_system BOOLEAN NOT NULL DEFAULT FALSE,

    ingredient_id INT  NOT NULL,
		FOREIGN KEY (ingredient_id) REFERENCES ingredients_info(ingredient_id) 
			ON DELETE CASCADE,

    product_name VARCHAR(100) NOT NULL,

	volume_per_unit DECIMAL(7,2) NOT NULL,
	cost_per_unit DECIMAL(7,2) NOT NULL,

	store_id INT NOT NULL,
		FOREIGN KEY (store_id) REFERENCES stores(store_id) 
			ON DELETE CASCADE,

    UNIQUE KEY no_repeat_products_in_store(store_id, product_name),
	INDEX idx_shop_ingredient (ingredient_id),
	INDEX idx_shop_store (store_id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4;

