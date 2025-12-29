-- #######################################################################################
-- Trigger Syntax
-- #######################################################################################

/*
    #################################################
    Structure of Imutable Table Trigger
    #################################################

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

	#################################################
    Structure of Imutable Table Trigger
    #################################################

    DELIMITER $$
        CREATE TRIGGER trg_table_name_allow_only_safe_updates
        BEFORE UPDATE ON plan_versions
        FOR EACH ROW
        BEGIN
            IF NOT (

                -- Immutable Columns (Not Allowed)
                NEW.colum_name           <=> OLD.colum_name
                AND NEW.colum_name        <=> OLD.colum_name
                ......

                -- Mutable Columns (Implicitly Allowed):
                    -- colum_name
                    -- colum_name
                    ......

            ) THEN
                SIGNAL SQLSTATE '45000'
                    SET MESSAGE_TEXT =
                    'Trigger - Only approved columns may be updated on plan_versions';
            END IF;
        END$$

    DELIMITER ;
*/

-- ############################################################################
-- Users Trigger | Allows only certain rows to be updated
-- ############################################################################








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
    CREATE TRIGGER trg_plans_versions_allow_only_safe_updates
    BEFORE UPDATE ON plan_versions
    FOR EACH ROW
    BEGIN
        IF NOT (
        
            -- Immutable Columns (Not Allowed)
            NEW.plan_id           <=> OLD.plan_id
            AND NEW.user_id        <=> OLD.user_id
            AND NEW.version_number <=> OLD.version_number

            -- Mutable Columns (Implicitly Allowed):
                -- date_time_last_edited
                -- is_selected_plan BOOLEAN

        ) THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT =
                'Trigger - Only approved columns may be updated on plan_versions';
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
		IF NOT
		(
		    -- Immutable Columns (Not Allowed)
			NEW.macros_ID <=> OLD.macros_ID
			AND NEW.user_id <=> OLD.user_id
			AND NEW.plan_version_id <=> OLD.plan_version_id
			AND NEW.version_number <=> OLD.version_number

			-- Mutable Columns (Implicitly Allowed):
                --  date_time_last_edited
                --  current_weight_kg
                --  current_weight_in_pounds
                --  body_fat_percentage
                --  protein_per_pound
                --  carbohydrates_per_pound
                --  fibre
                --  fats_per_pound
                --  saturated_fat_limit
                --  salt_limit
                --  water_target
                --  additional_calories
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
		IF NOT
		(
		    -- Immutable Columns (Not Allowed)
			NEW.is_system <=> OLD.is_system

			-- Mutable Columns (Implicitly Allowed):
			    -- ingredient_type_name
		) 
		THEN
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT =
					'Only ingredient_type_name may be updated on ingredient_types';
		END IF;
	END$$

DELIMITER ;

-- ###########################################################################
-- Measurements Material Type Trigger | Once insert Immutable
-- ###########################################################################
DELIMITER $$

	CREATE TRIGGER trg_measurement_material_type_prevent_any_update
	BEFORE UPDATE ON measurement_material_type
	FOR EACH ROW
	BEGIN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'measurement_material_type table is immutable';
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
		IF NOT 
		(
		    -- Immutable Columns (Not Allowed)
		   NEW.is_system <=> OLD.is_system
           
           	-- Mutable Columns (Implicitly Allowed):          			    
                -- unit_name  
                -- unit_symbol  
                -- measured_material_type  
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
		IF NOT 
		(
		    -- Immutable Columns (Not Allowed)
		    NEW.is_system <=> OLD.is_system
		
			-- Mutable Columns (Implicitly Allowed):
                -- measurement_id
                -- ingredient_name
                -- ingredient_type_id
                -- based_on_quantity
                -- glycemic_index
                -- protein
                -- carbohydrates
                -- sugars_of_carbs
                -- fibre
                -- fat
                -- saturated_fat
                -- salt
                -- water_content
                -- liquid_content
			    -- calories
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
		IF NOT 
		(
		    -- Immutable Columns (Not Allowed)
			NEW.is_system <=> OLD.is_system

			-- Mutable Columns (Implicitly Allowed):
			    -- store_name
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
		IF NOT 
		(
		    -- Immutable Columns (Not Allowed)
            NEW.is_system <=> OLD.is_system
            AND NEW.ingredient_id <=> OLD.ingredient_id
            
            -- Mutable Columns (Implicitly Allowed):
			    -- product_name
			    -- volume_per_unit
			    -- cost_per_unit
			    -- store_id
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
		IF NOT
		(
		    -- Immutable Columns (Not Allowed)
		    NEW.meal_in_plan_id <=> OLD.meal_in_plan_id
            AND NEW.plan_version_id <=> OLD.plan_version_id           

		    -- Mutable Columns (Implicitly Allowed):
                -- date_time_last_edited
                -- meal_name
                -- meal_time
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
		IF NOT 
		(
		    -- Immutable Columns (Not Allowed)
			NEW.div_meal_sections_id <=> OLD.div_meal_sections_id
			AND NEW.meal_in_plan_version_id <=> OLD.sub_meal_name
			
			-- Mutable Columns (Implicitly Allowed):
                -- date_time_last_edited
                -- sub_meal_name
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
/*
DELIMITER $$

	CREATE TRIGGER trg_ingredients_in_sections_of_meal_prevent_any_update
	BEFORE UPDATE ON ingredients_in_sections_of_meal
	FOR EACH ROW
	BEGIN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'ingredients_in_sections_of_meal table is immutable';
	END$$

DELIMITER ;
*/

-- ###########################################################################
-- ingredients_in_sections_of_meal Trigger | Allows only certain rows to be updated
-- ###########################################################################
DELIMITER $$

	CREATE TRIGGER trg_ingredients_in_meal_allow_only_safe_updates
	BEFORE UPDATE ON ingredients_in_sections_of_meal
	FOR EACH ROW
	BEGIN
		IF NOT 
		(
		    -- Immutable Columns (Not Allowed)
			NEW.div_meal_sections_version_id <=> OLD.div_meal_sections_version_id
			
			-- Mutable Columns (Implicitly Allowed):
                -- ingredient_id
                -- quantity
                -- pdid
		) 
		THEN
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT =
					'Only ingredient_id, quantity and pdid can be updated on ingredients_in_sections_of_meal';
		END IF;
	END$$

DELIMITER ;

