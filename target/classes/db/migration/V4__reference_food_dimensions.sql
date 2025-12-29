-- #########################################################################
-- DDL SCRIPT | App Setup
-- #########################################################################
/*


*/

-- ####################################################
-- Ingredient Types
-- ####################################################
CREATE TABLE ingredient_types
(
    ingredient_type_id INT  PRIMARY KEY AUTO_INCREMENT,
	is_system BOOLEAN NOT NULL DEFAULT FALSE,

	ingredient_type_name VARCHAR(100) NOT NULL,
	UNIQUE KEY no_repeat_ingredient_type_name (ingredient_type_name)
);

-- ####################################################
-- Measurements Material Type
-- ####################################################
CREATE TABLE measurement_material_type
(
    measurement_material_type_id INT PRIMARY KEY AUTO_INCREMENT,
    measurement_material_type_name VARCHAR(100) NOT NULL,

    UNIQUE KEY no_repeat_material_type_name(measurement_material_type_name)
);

-- ####################################################
-- Measurements
-- ####################################################
CREATE TABLE measurements
 (
    -- PRIMARY KEYS
    measurement_id INT PRIMARY KEY AUTO_INCREMENT,
	is_system BOOLEAN NOT NULL DEFAULT FALSE,

	unit_name VARCHAR(100) NOT NULL,
	unit_symbol VARCHAR(10) NOT NULL,

	measurement_material_type_id INT NOT NULL,
    		FOREIGN KEY (measurement_material_type_id)
    		    REFERENCES measurement_material_type(measurement_material_type_id)
    			    ON DELETE CASCADE,
	
	UNIQUE KEY no_repeat_unit_names(unit_name)
 );
