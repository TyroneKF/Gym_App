
-- #################################################################################################
-- N/A Ingredients Types
-- #################################################################################################

-- Create Ingredient Type For N/A
SET @na_ingredient_type_name := 'N/A';

INSERT INTO ingredient_types
(
	ingredient_type_name, 
	is_system
)
VALUES
    (@na_ingredient_type_name, TRUE),
    ('Un-Assigned', TRUE)
ON DUPLICATE KEY UPDATE -- In case of duplicate, ensures fields match correctly to new insert is
    ingredient_type_name = ingredient_type_name;

-- ######################################
-- Variable Setting FOR N/A Type
-- ######################################

-- Create Variable for N/A ID
SELECT ingredient_type_id
INTO @na_type_id
FROM ingredient_types
WHERE ingredient_type_name = @na_ingredient_type_name
LIMIT 1;

-- Validate Variable N/A & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'na_type_id',
    'ingredient_types',
    @na_type_id,
    'Seed failed: @na_type_id (ingredient types) could not be resolved'
);

-- #################################################################################################
-- Ingredient Measurements
-- #################################################################################################
/*

*/

-- ###################################################################
-- Measured Material Types
-- ####################################################################

-- Set Measured_Material_Type Variables
SET @solids_material_type_name := 'Solids';
SET @liquids_material_type_name := 'Liquids';
SET @na_material_type_name := 'N/A';

-- ###############################################
-- Insert SEED DATA for Measurement Variables
-- ###############################################
INSERT INTO measurement_material_type
(
	measurement_material_type_name
)
VALUES
    (@solids_material_type_name),
    (@liquids_material_type_name),
    (@na_material_type_name)

ON DUPLICATE KEY UPDATE -- In case of duplicate, ensures fields match correctly to new insert
    measurement_material_type_name = measurement_material_type_name;


-- ###############################################
-- Variable Setting
-- ###############################################
-- Solids
SELECT measurement_material_type_id
INTO @solids_material_type_id
FROM measurement_material_type
WHERE measurement_material_type_name = @solids_material_type_name
LIMIT 1;

-- Validate
CALL assert_id_not_null(@solids_material_type_id, 'Seed failed: @solids_material_type_id (measurement_material_type) could not be resolved');

-- Liquids
SELECT measurement_material_type_id
INTO @liquids_material_type_id
FROM measurement_material_type
WHERE measurement_material_type_name = @liquids_material_type_name
LIMIT 1;

-- Validate
CALL assert_id_not_null (@liquids_material_type_id, 'Seed failed: @liquids_material_type_id (measurement_material_type) could not be resolved');


-- N/A
SELECT measurement_material_type_id
INTO @na_material_type_id
FROM measurement_material_type
WHERE measurement_material_type_name = @na_material_type_name
LIMIT 1;

-- Validate
CALL assert_id_not_null (@na_material_type_id, 'Seed failed: @na_material_type_id (measurement_material_type) could not be resolved');


-- ####################################################################
-- Measurements
-- ####################################################################

-- Set Measurement Variables
SET @litres_measurement_name := 'Litres';
SET @grams_measurement_name := 'Grams';
SET @na_measurement_name := 'N/A';

-- ###############################################
-- Inserting SEED DATA for Measurement Variables
-- ###############################################
INSERT INTO measurements 
( 
	is_system,
	unit_name, 
	unit_symbol,
	measurement_material_type_id
)
VALUES
    (TRUE, @litres_measurement_name, 'L', @liquids_material_type_id),
    (TRUE, @grams_measurement_name, 'g',  @solids_material_type_id),
    (TRUE, @na_measurement_name, 'N/A',   @na_material_type_id)

ON DUPLICATE KEY UPDATE -- In case of duplicate, ensures fields match correctly to new insert
    unit_name = unit_name;

-- #####################################################
-- Variable Setting
-- #####################################################

-- Litres
SELECT measurement_id
INTO @litres_measurement_id
FROM measurements
WHERE unit_name = @litres_measurement_name
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'litres_measurement_id',
    'measurements',
    @litres_measurement_id,
    'Seed failed: @litres_measurement_id (measurement) could not be resolved'
);

-- ############################
-- Grams
-- ############################
SELECT measurement_id
INTO @grams_measurement_id
FROM measurements
WHERE unit_name = @grams_measurement_name
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'grams_measurement_id',
    'measurements',
    @grams_measurement_id,
    'Seed failed: @grams_measurement_id (measurement) could not be resolved'
);

-- ############################
-- N/A
-- ############################
SELECT measurement_id
INTO @na_measurement_id
FROM measurements
WHERE unit_name = @na_measurement_name
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'na_measurement_id',
    'measurements' ,
     @na_measurement_id,
     'Seed failed: @na_measurement_id (measurement) could not be resolved'
 );

-- #################################################################################################
-- Ingredient Stores
-- #################################################################################################

-- Set Stores Variables
SET @na_store_name := 'No Shop';

-- ###############################################
-- Create N/A Shop
-- ###############################################
INSERT INTO stores 
(
	is_system,
	store_name
	
) VALUES
(
	TRUE, @na_store_name
)
ON DUPLICATE KEY UPDATE -- In case of duplicate, ensures fields match correctly to new insert
    store_name = store_name;

-- ###############################################
-- Set N/A Shop Variable
-- ###############################################
SELECT store_id
INTO @na_store_id
FROM stores
WHERE store_name = @na_store_name
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'na_store_id',
    'stores' ,
    @na_store_id,
    'Seed failed: Store ID @na_store_id could not be resolved'
);

-- #################################################################################################
-- Creating N/A Ingredient
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
)
VALUES

(TRUE, @na_measurement_id, @na_ingredient_name, @na_type_id, 0,0,0,0,0,0,0,0,0,0,0,0)

ON DUPLICATE KEY UPDATE -- In case of duplicate, ensures fields match correctly to new insert
	ingredient_name = ingredient_name;

-- ##########################
-- Set Ingredient Variable
-- ##########################

-- Create Variable for N/A Ingredient ID
SELECT ingredient_id
INTO @na_ingredient_id
FROM ingredients_info
WHERE ingredient_name = @na_ingredient_name
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'na_ingredient_id',
    'ingredients_info' ,
    @na_ingredient_id,
    'Seed failed: Ingredient ID @na_ingredient_id could not be resolved'
);