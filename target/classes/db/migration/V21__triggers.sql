-- ###########################################################################
-- Trigger Syntax
-- ###########################################################################

/*

	CREATE TRIGGER prevent_any_update
	BEFORE UPDATE ON some_table
	FOR EACH ROW
	BEGIN
		SIGNAL SQLSTATE '45000'
		SET MESSAGE_TEXT = 'This table is immutable';
	END;

	Allow list approach
	IF NEW.D <> OLD.D
	THEN OK
	ELSE SIGNAL;

*/

-- ###########################################################################
-- Users Trigger | Allows only certain rows to be updated
-- ###########################################################################

DELIMITER $$

	CREATE TRIGGER trg_users_allow_only_safe_updates
	BEFORE UPDATE ON users
	FOR EACH ROW
	BEGIN
		IF NOT -- any column not listed here is not updated
		(
		    NEW.user_name <=> OLD.user_name
			AND NEW.is_user_selected <=> OLD.is_user_selected
		) 
		THEN
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT =
					'Only : user_name and is_user_selected may be updated on users table';
		END IF;
	END$$

DELIMITER ;

-- ###########################################################################
-- Plans Trigger | Prevents table from being updated
-- ###########################################################################
DELIMITER $$

	CREATE TRIGGER trg_plans_prevent_any_update
	BEFORE UPDATE ON plans
	FOR EACH ROW
	BEGIN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'plans table is immutable';
	END$$

DELIMITER ;

-- ###########################################################################
-- Plans_versions | Allows only certain rows to be updated
-- ###########################################################################
DELIMITER $$

	CREATE TRIGGER trg_plan_versions_allow_only_safe_updates
	BEFORE UPDATE ON plan_versions
	FOR EACH ROW
	BEGIN
		IF NOT -- any column not listed here is not updated
		(
			NEW.date_time_last_edited <=> OLD.date_time_last_edited
			AND NEW.is_selected_plan <=> OLD.is_selected_plan
		) 
		THEN
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT =
					'Only is_selected_plan and date_time_last_edited may be updated on plan_versions';
		END IF;
	END$$

DELIMITER ;

-- ###########################################################################
-- macros_per_pound_and_limits Trigger | Prevents table from being updated
-- ###########################################################################
DELIMITER $$

	CREATE TRIGGER trg_macros_per_pound_and_limits_prevent_any_update
	BEFORE UPDATE ON macros_per_pound_and_limits
	FOR EACH ROW
	BEGIN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'macros_per_pound_and_limits table is immutable';
	END$$

DELIMITER ;

-- ###########################################################################
-- macros_per_pound_and_limits_versions | Allows only certain rows to be updated
-- ###########################################################################
DELIMITER $$

	CREATE TRIGGER trg_macros_per_pound_and_limits_versions_allow_only_safe_updates
	BEFORE UPDATE ON macros_per_pound_and_limits_versions
	FOR EACH ROW
	BEGIN
		IF NOT -- any column not listed here is not updated
		(
			NEW.date_time_last_edited <=> OLD.date_time_last_edited
			
			AND NEW.current_weight_kg <=> OLD.current_weight_kg
			AND NEW.current_weight_in_pounds <=> OLD.current_weight_in_pounds			
			AND NEW.body_fat_percentage <=> OLD.body_fat_percentage
			AND NEW.protein_per_pound <=> OLD.protein_per_pound			
			AND NEW.carbohydrates_per_pound <=> OLD.carbohydrates_per_pound			
			AND NEW.fibre <=> OLD.fibre			
			AND NEW.fats_per_pound <=> OLD.fats_per_pound			
			AND NEW.saturated_fat_limit <=> OLD.saturated_fat_limit
			AND NEW.salt_limit <=> OLD.salt_limit
			AND NEW.water_target <=> OLD.water_target
			
			AND NEW.additional_calories <=> OLD.additional_calories
		) 
		THEN
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT =
					'Only date & Macro Values can be updated on macros_per_pound_and_limits_versions';
		END IF;
	END$$

DELIMITER ;

-- ###########################################################################
-- ingredient_types Trigger | Allows only certain rows to be updated
-- ###########################################################################
DELIMITER $$

	CREATE TRIGGER trg_ingredient_types_allow_only_safe_updates
	BEFORE UPDATE ON ingredient_types
	FOR EACH ROW
	BEGIN
		IF NOT -- any column not listed here is not updated
		(
			NEW.ingredient_type_name <=> OLD.ingredient_type_name		
		) 
		THEN
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT =
					'Only ingredient_type_name may be updated on ingredient_types';
		END IF;
	END$$

DELIMITER ;

-- ###########################################################################
-- measurements Trigger | Allows only certain rows to be updated
-- ###########################################################################
DELIMITER $$

	CREATE TRIGGER trg_measurements_allow_only_safe_updates
	BEFORE UPDATE ON measurements
	FOR EACH ROW
	BEGIN
		IF NOT -- any column not listed here is not updated
		(
			NEW.unit_name <=> OLD.unit_name
			AND NEW.unit_symbol <=> OLD.unit_symbol
			AND NEW.measured_material_type <=> OLD.measured_material_type
		) 
		THEN
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT =
					'Only : unit_name, unit_symbol, measured_material_type may be updated on measurements table';
		END IF;
	END$$

DELIMITER ;

-- ###########################################################################
-- ingredients_info Trigger | Allows only certain rows to be updated
-- ###########################################################################
DELIMITER $$

	CREATE TRIGGER trg_ingredients_info_allow_only_safe_updates
	BEFORE UPDATE ON ingredients_info
	FOR EACH ROW
	BEGIN
		IF NOT -- any column not listed here is not updated
		(
			NEW.measurement_id <=> OLD.measurement_id
			AND NEW.ingredient_name <=> OLD.ingredient_name
			AND NEW.ingredient_type_id <=> OLD.ingredient_type_id
			
			AND NEW.based_on_quantity <=> OLD.based_on_quantity
			AND NEW.glycemic_index <=> OLD.glycemic_index
			
			AND NEW.protein <=> OLD.protein
			AND NEW.carbohydrates <=> OLD.carbohydrates
			AND NEW.sugars_of_carbs <=> OLD.sugars_of_carbs
			
			AND NEW.fibre <=> OLD.fibre
			AND NEW.fat <=> OLD.fat
			AND NEW.saturated_fat <=> OLD.saturated_fat
			AND NEW.salt <=> OLD.salt
			
			AND NEW.water_content <=> OLD.water_content
			AND NEW.liquid_content <=> OLD.liquid_content
			
			AND NEW.calories <=> OLD.calories
		) 
		THEN
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT =
					'Only : measurement_id, ingredient_name, ingredient_type_id, based_on_quantity, glycemic_index, protein, carbohydrates, sugars_of_carbs, fibre, fat, saturated_fat, salt, water_content, liquid_content, calories can be updated on ingredients_info';
		END IF;
	END$$

DELIMITER ;

-- ###########################################################################
-- stores Trigger | Allows only certain rows to be updated
-- ###########################################################################
DELIMITER $$

	CREATE TRIGGER trg_stores_allow_only_safe_updates
	BEFORE UPDATE ON stores
	FOR EACH ROW
	BEGIN
		IF NOT -- any column not listed here is not updated
		(
			NEW.store_name <=> OLD.store_name
		) 
		THEN
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT =
					'Only store_names can be updated on stores';
		END IF;
	END$$

DELIMITER ;

-- ###########################################################################
-- ingredient_in_shops Trigger | Allows only certain rows to be updated
-- ###########################################################################
DELIMITER $$

	CREATE TRIGGER trg_ingredient_in_shops_allow_only_safe_updates
	BEFORE UPDATE ON ingredient_in_shops
	FOR EACH ROW
	BEGIN
		IF NOT -- any column not listed here is not updated
		(
			NEW.product_name <=> OLD.product_name
			AND NEW.volume_per_unit <=> OLD.volume_per_unit
			AND NEW.cost_per_unit <=> OLD.cost_per_unit
			AND NEW.store_id <=> OLD.store_id
		) 
		THEN
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT =
					'Only product_name, volume_per_unit, cost_per_unit, store_id  can be updated on ingredient_in_shops';
		END IF;
	END$$

DELIMITER ;

-- ###########################################################################
-- meals_in_plan  Trigger | Prevents table from being updated
-- ###########################################################################
DELIMITER $$

	CREATE TRIGGER trg_meals_in_plan_prevent_any_update
	BEFORE UPDATE ON meals_in_plan 
	FOR EACH ROW
	BEGIN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'meals_in_plan table is immutable';
	END$$

DELIMITER ;

-- ###########################################################################
-- meals_in_plan_versions Trigger | Allows only certain rows to be updated
-- ###########################################################################
DELIMITER $$

	CREATE TRIGGER trg_meals_in_plan_versions_allow_only_safe_updates
	BEFORE UPDATE ON meals_in_plan_versions
	FOR EACH ROW
	BEGIN
		IF NOT -- any column not listed here is not updated
		(
			NEW.date_time_last_edited <=> OLD.date_time_last_edited
			AND NEW.meal_name <=> OLD.meal_name
			AND NEW.meal_time <=> OLD.meal_time
		) 
		THEN
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT =
					'Only date_time_last_edited, meal_name, meal_time can be updated on meals_in_plan_versions';
		END IF;
	END$$

DELIMITER ;

-- ###########################################################################
-- divided_meal_sections Trigger | Prevents table from being updated
-- ###########################################################################
DELIMITER $$

	CREATE TRIGGER trg_divided_meal_sections_prevent_any_update
	BEFORE UPDATE ON divided_meal_sections 
	FOR EACH ROW
	BEGIN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'divided_meal_sections table is immutable';
	END$$

DELIMITER ;


-- ###########################################################################
-- divided_meal_sections_versions Trigger | Allows only certain rows to be updated
-- ###########################################################################
DELIMITER $$

	CREATE TRIGGER trg_divided_meal_sections_versions_allow_only_safe_updates
	BEFORE UPDATE ON divided_meal_sections_versions
	FOR EACH ROW
	BEGIN
		IF NOT -- any column not listed here is not updated
		(
			NEW.date_time_last_edited <=> OLD.date_time_last_edited
			AND NEW.sub_meal_name <=> OLD.sub_meal_name
		) 
		THEN
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT =
					'Only date_time_last_edited, sub_meal_name can be updated on divided_meal_sections_versions';
		END IF;
	END$$

DELIMITER ;

-- ###########################################################################
-- ingredients_in_sections_of_meal Trigger | Prevents table from being updated
-- ###########################################################################
DELIMITER $$

	CREATE TRIGGER trg_ingredients_in_sections_of_meal_prevent_any_update
	BEFORE UPDATE ON ingredients_in_sections_of_meal
	FOR EACH ROW
	BEGIN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'ingredients_in_sections_of_meal table is immutable';
	END$$

DELIMITER ;

-- ###########################################################################
-- ingredients_in_sections_of_meal_versions Trigger | Allows only certain rows to be updated
-- ###########################################################################
DELIMITER $$

	CREATE TRIGGER trg_ingredients_in_meal_allow_only_safe_updates
	BEFORE UPDATE ON ingredients_in_sections_of_meal
	FOR EACH ROW
	BEGIN
		IF NOT -- any column not listed here is not updated
		(
			NEW.ingredient_id <=> OLD.ingredient_id
			AND NEW.quantity <=> OLD.quantity
			AND NEW.pdid <=> OLD.pdid
		) 
		THEN
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT =
					'Only ingredient_id, quantity and pdid can be updated on ingredients_in_sections_of_meal';
		END IF;
	END$$

DELIMITER ;

