
-- #################################################################################################
-- Ingredients Types 
-- #################################################################################################

-- Inserting SEED DATA for System Variables

INSERT IGNORE ingredient_types 
(
	ingredient_type_name, 
	is_system
	
) VALUES

('None Of The Above', TRUE),
('Un-Assigned', TRUE)

ON DUPLICATE KEY UPDATE -- Incase of duplicate, ensures fields match correctly to new insert is
    ingredient_type_name = ingredient_type_name;

-- ######################################
-- Variable Setting FOR N/A
-- ######################################

SELECT ingredient_id
INTO @na_type_id
FROM ingredients_info
WHERE ingredient_name = 'None Of The Above'
LIMIT 1;

-- ######################################
-- Validate Variable N/A
-- ######################################
DO
CASE
    WHEN
		@na_type_id IS NULL
    THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Seed failed: one or more ingredient types could not be resolved';
END CASE;

-- #################################################################################################
-- Ingredient Measurements
-- #################################################################################################

-- Inserting SEED DATA for System Variables
INSERT INTO measurements 
( 
	is_system,
	unit_name, 
	unit_symbol, 
	

) VALUES

(TRUE, 'Litres', 'L', 'Liquids'),
(TRUE, 'Grams', 'g', 'Solids'),
(TRUE, 'N/A', 'N/A', 'N/A')

ON DUPLICATE KEY UPDATE -- Incase of duplicate, ensures fields match correctly to new insert
    unit_name = VALUES(unit_name),
	unit_symbol = VALUES(unit_symbol);

-- #####################################################
-- Variable Setting
-- #####################################################

-- Litres
SELECT measurement_id
INTO @litres_id
FROM measurements
WHERE unit_name = 'Litres'
LIMIT 1;

-- Grams
SELECT measurement_id
INTO @grams_id
FROM measurements
WHERE unit_name = 'Grams'
LIMIT 1;

-- N/A
SELECT measurement_id
INTO @na_meassurement_id
FROM measurements
WHERE unit_name = 'N/A'
LIMIT 1;

-- #####################################################
-- Variable Validation 
-- #####################################################
DO
CASE
    WHEN
        @litres_id IS NULL
     OR @grams_id IS NULL
     OR @na_meassurement_id IS NULL
    THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Seed failed: one or more measurement units could not be resolved';
END CASE;


-- #################################################################################################
-- Ingredient Stores
-- #################################################################################################
-- Inserting SEED DATA for System Variables

INSERT INTO stores 
(
	is_system
	store_name
	
) VALUES

(TRUE, 'No Shop')

ON DUPLICATE KEY UPDATE -- Incase of duplicate, ensures fields match correctly to new insert
    store_name = VALUES(store_name);

-- ##########################
-- Set Variable
-- ##########################
SELECT store_id
INTO @na_store_id
FROM stores
WHERE store_name = 'No Shop'
LIMIT 1;

-- ##########################
-- Variable Validation 
-- ##########################
DO
CASE
    WHEN
        @na_store_id IS NULL
    THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Seed failed: Store ID @na_store_id could not be resolved';
END CASE;

-- #################################################################################################
-- Inserting SEED DATA for System Variables
-- #################################################################################################

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

(TRUE, @na_meassurement_id, 'None Of The Above', @na_type_id, 0,0,0,0,0,0,0,0,0,0,0,0)

ON DUPLICATE KEY UPDATE -- Incase of duplicate, ensures fields match correctly to new insert
	ingredient_type_name = ingredient_type_name;

-- ##########################
-- Set Variable
-- ##########################
SELECT ingredient_id
INTO @na_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'None Of The Above'
LIMIT 1;

-- ##########################
-- Variable Validation 
-- ##########################
DO
CASE
    WHEN
        @na_ingredient_id IS NULL
    THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Seed failed: Ingredient ID @na_ingredient_id could not be resolved';
END CASE;
