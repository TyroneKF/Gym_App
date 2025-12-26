
-- #################################################################################################
-- Ingredients Types 
-- #################################################################################################

-- Inserting SEED DATA for System Variables

START TRANSACTION;

SET @na_ingredient_type_name := 'N/A';

INSERT IGNORE ingredient_types 
(
	ingredient_type_name, 
	is_system
	
) VALUES

(@na_ingredient_type_name, TRUE),
('Un-Assigned', TRUE)

ON DUPLICATE KEY UPDATE -- Incase of duplicate, ensures fields match correctly to new insert is
    ingredient_type_name = ingredient_type_name;

-- ######################################
-- Variable Setting FOR N/A
-- ######################################

SELECT ingredient_type_id
INTO @na_type_id
FROM ingredient_types
WHERE ingredient_type_name = @na_ingredient_type_name
LIMIT 1;

-- Validate Variable N/A
CALL assert_id_not_null(@na_type_id, 'Seed failed: @na_type_id (ingredient types) could not be resolved');

-- #################################################################################################
-- Ingredient Measurements
-- #################################################################################################

-- Set Variables 
SET @litres_measurement_name := 'Litres';
SET @grams_measurement_name := 'Grams';
SET @na_measurement_name := 'N/A';

-- Inserting SEED DATA for System Variables
INSERT INTO measurements 
( 
	is_system,
	unit_name, 
	unit_symbol,
	measured_material_type	
	
) VALUES

(TRUE, @litres_measurement_name, 'L', 'Liquids'),
(TRUE, @grams_measurement_name, 'g', 'Solids'),
(TRUE, @na_measurement_name, 'N/A', 'N/A')

ON DUPLICATE KEY UPDATE -- Incase of duplicate, ensures fields match correctly to new insert
    unit_name = unit_name;

-- #####################################################
-- Variable Setting
-- #####################################################

-- Litres
SELECT measurement_id
INTO @litres_id
FROM measurements
WHERE unit_name = @litres_measurement_name
LIMIT 1;

-- Litres Variable Validation 
CALL assert_id_not_null(@litres_id, 'Seed failed: @litres_id (measurement) could not be resolved');

-- ############################
-- Grams
-- ############################
SELECT measurement_id
INTO @grams_id
FROM measurements
WHERE unit_name = @grams_measurement_name
LIMIT 1;

-- Grams Variable Validation 
CALL assert_id_not_null(@grams_id, 'Seed failed: @grams_id (measurement) could not be resolved');

-- ############################
-- N/A
-- ############################
SELECT measurement_id
INTO @na_meassurement_id
FROM measurements
WHERE unit_name = @na_measurement_name
LIMIT 1;

-- N/A Variable Validation 
CALL assert_id_not_null(@na_meassurement_id, 'Seed failed: @na_meassurement_id (measurement) could not be resolved');

-- #################################################################################################
-- Ingredient Stores
-- #################################################################################################

SET @na_store_name := 'No Shop';

-- Inserting SEED DATA for System Variables
INSERT INTO stores 
(
	is_system,
	store_name
	
) VALUES
(
	TRUE, @na_store_name
)
ON DUPLICATE KEY UPDATE -- Incase of duplicate, ensures fields match correctly to new insert
    store_name = store_name;

-- ##########################
-- Set Variable
-- ##########################
SELECT store_id
INTO @na_store_id
FROM stores
WHERE store_name = @na_store_name
LIMIT 1;

-- Variable Validation 
CALL assert_id_not_null(@na_store_id, 'Seed failed: Store ID @na_store_id could not be resolved');

-- #################################################################################################
-- Inserting SEED DATA for System Variables
-- #################################################################################################

SET @na_ingredient_name := 'None Of The Above';

INSERT INTO ingredients_info 
(	
	is_system,
	measurement_id, 
	ingredient_name, 
	ingredient_type_id, 
	based_on_quantity, 
	glycemic_index, 
	protein, 
	carbohydrates, 
	sugars_of_carbs, 
	fibre, 
	fat, 
	saturated_fat, 
	salt, 
	water_content, 
	liquid_content, 
	calories
	
) VALUES

(TRUE, @na_measurement_id, @na_ingredient_name, @na_type_id, 0,0,0,0,0,0,0,0,0,0,0,0)

ON DUPLICATE KEY UPDATE -- Incase of duplicate, ensures fields match correctly to new insert
	ingredient_name = ingredient_name;

-- ##########################
-- Set Variable
-- ##########################
SELECT ingredient_id
INTO @na_ingredient_id
FROM ingredients_info
WHERE ingredient_name = @na_ingredient_name
LIMIT 1;

-- Variable Validation 
CALL assert_id_not_null(@na_ingredient_id, 'Seed failed: Ingredient ID @na_ingredient_id could not be resolved');

COMMIT;
