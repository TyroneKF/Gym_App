-- #########################################################################
-- DDL SCRIPT | App Setup
-- #########################################################################
/*


*/

-- ####################################################
-- ingredient_types
-- ####################################################
CREATE TABLE ingredient_types
(
    ingredient_type_id INT  PRIMARY KEY AUTO_INCREMENT,
	is_system BOOLEAN NOT NULL DEFAULT FALSE,

	ingredient_type_name VARCHAR(100) NOT NULL,
	UNIQUE KEY no_repeat_ingredient_type_name (ingredient_type_name)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4;

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
	measured_material_type ENUM('solids', 'liquids', 'N/A') NOT NULL,
	
	UNIQUE KEY no_repeat_unit_names(unit_name)
 )
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4;
