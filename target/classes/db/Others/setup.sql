DROP DATABASE IF EXISTS gymapp00001;
CREATE DATABASE gymapp00001;

USE gymapp00001;


-- ##########################
-- Set Ingredient Variable
-- ##########################

-- Create Variable for N/A Ingredient ID
SELECT ingredient_id
INTO @na_ingredient_id
FROM ingredients_info
WHERE ingredient_name = @na_ingredient_name
LIMIT 1;

-- Variable Validation 
CALL assert_id_not_null(@na_ingredient_id, 'Seed failed: Ingredient ID @na_ingredient_id could not be resolved');

-- Insert Into Seed Registry Table
INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
VALUES
    ('na_ingredient_id', 'ingredients_info' , @na_ingredient_id)
AS new_vals
ON DUPLICATE KEY UPDATE -- In case of duplicate, ensures fields match correctly to new insert
	entity_id_value = new_vals.entity_id_value;
