-- #######################################################################################
-- Trigger Syntax
-- #######################################################################################

/*
    -- #################################################
    -- Trigger Immutable Table
    -- #################################################

    DELIMITER $$

        CREATE TRIGGER prevent_any_update
        BEFORE UPDATE ON some_table
        FOR EACH ROW
        BEGIN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'This table is immutable';
        END;

	DELIMITER ;

	Allow list approach
	IF NEW.D <> OLD.D
	THEN OK
	ELSE SIGNAL;



	-- #################################################
    -- Trigger Prevents Certain rows from Updating
    -- #################################################

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

    -- #################################################
    --  Prevents Table Deletions
    -- #################################################
    DELIMITER $$

        CREATE TRIGGER prevent_delete_on_table
        BEFORE DELETE ON my_table
        FOR EACH ROW
        BEGIN
          SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'DELETE is not allowed on this table';
        END$$

    DELIMITER ;

    -- #################################################
    --  Prevents Table Inserts
    -- #################################################
    DELIMITER $$

            CREATE TRIGGER prevent_insert_on_table_name
            BEFORE INSERT ON table_name
            FOR EACH ROW
            BEGIN
              SIGNAL SQLSTATE '45000'
                 SET MESSAGE_TEXT =
                        'Inserts are not allowed on table table_name';
            END$$

    DELIMITER ;


    -- #############################################
    --   Trigger Prevents Delete on row Column value
    -- #############################################
    DELIMITER $$

        CREATE TRIGGER prevent_paid_order_delete
        BEFORE DELETE ON orders
        FOR EACH ROW
        BEGIN
            IF OLD.status = 'PAID' THEN
                SIGNAL SQLSTATE '45000'
                    SET MESSAGE_TEXT = 'Deletion not allowed: order is already PAID';
            END IF;
        END$$

    DELIMITER ;

    -- #############################################
    --   Trigger Prevents Update on Variable
    -- #############################################
    DELIMITER $$

        CREATE TRIGGER prevent_update_on_ingredient_types_when_final
        BEFORE UPDATE ON ingredient_types
        FOR EACH ROW
        BEGIN
            IF OLD.is_system = TRUE THEN
                SIGNAL SQLSTATE '45000'
                    SET MESSAGE_TEXT = 'Update not allowed on ingredient_types: record is FINAL';
            END IF;
        END$$

    DELIMITER ;


*/

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
-- ingredient_types Trigger
-- ###########################################################################
/*


*/

    -- #################################################
    -- Prevent is_System Rows from being Updated
    -- #################################################
    DELIMITER $$

        CREATE TRIGGER prevent_update_on_ingredient_types_when_final
        BEFORE UPDATE ON ingredient_types
        FOR EACH ROW
        BEGIN
            IF OLD.is_system = TRUE THEN
                SIGNAL SQLSTATE '45000'
                    SET MESSAGE_TEXT = 'Update not allowed on ingredient_types: record is FINAL';
            END IF;
        END$$

    DELIMITER ;

    -- #############################################
    -- Prevents is_System Variable Deletion
    -- #############################################
    DELIMITER $$

        CREATE TRIGGER prevent_ingredient_types_delete_on_is_systems
        BEFORE DELETE ON ingredient_types
        FOR EACH ROW
        BEGIN
            IF OLD.is_system = TRUE THEN
                SIGNAL SQLSTATE '45000'
                    SET MESSAGE_TEXT = 'Deletion not allowed on record with is_system';
            END IF;
        END$$

    DELIMITER ;

    -- #################################################
    -- Allows Only Certain Rows to be Updated
    -- #################################################
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
-- Measurements_Material_Type Trigger | Once insert Immutable
-- ###########################################################################
/*


*/

    -- #################################################
    --  Prevents Table Deletions
    -- #################################################
    DELIMITER $$

            CREATE TRIGGER prevent_delete_on_measurement_material_type
            BEFORE DELETE ON measurement_material_type
            FOR EACH ROW
            BEGIN
              SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'DELETE is not allowed on this measurement_material_type';
            END$$

    DELIMITER ;

    -- #################################################
    --  Prevents Table Updates
    -- #################################################
    DELIMITER $$

        CREATE TRIGGER trg_measurement_material_type_prevent_any_update
        BEFORE UPDATE ON measurement_material_type
        FOR EACH ROW
        BEGIN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'measurement_material_type table is immutable';
        END$$

    DELIMITER ;

    -- #################################################
    --  Prevents Table Inserts
    -- #################################################
    DELIMITER $$

        CREATE TRIGGER prevent_insert_on_measurement_material_type
        BEFORE INSERT ON measurement_material_type
        FOR EACH ROW
        BEGIN
          SIGNAL SQLSTATE '45000'
             SET MESSAGE_TEXT =
                    'Inserts are not allowed on table measurement_material_type';
        END$$

    DELIMITER ;


-- ###########################################################################
-- measurements Trigger | Allows only certain rows to be updated
-- ###########################################################################
/*

*/

    -- #################################################
    --  Prevents Table Deletions
    -- #################################################
    DELIMITER $$

            CREATE TRIGGER prevent_delete_on_measurements
            BEFORE DELETE ON measurements
            FOR EACH ROW
            BEGIN
              SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'DELETE is not allowed on this measurements';
            END$$

    DELIMITER ;

    -- #################################################
    --  Prevents Table Updates
    -- #################################################
    DELIMITER $$

        CREATE TRIGGER trg_measurements_prevent_any_update
        BEFORE UPDATE ON measurements
        FOR EACH ROW
        BEGIN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'measurements table is immutable';
        END$$

    DELIMITER ;

    -- #################################################
    --  Prevents Table Inserts
    -- #################################################
        DELIMITER $$

            CREATE TRIGGER prevent_insert_on_measurements
            BEFORE INSERT ON measurements
            FOR EACH ROW
            BEGIN
              SIGNAL SQLSTATE '45000'
                 SET MESSAGE_TEXT =
                        'Inserts are not allowed on table measurements';
            END$$

        DELIMITER ;


-- ###########################################################################
-- ingredients_info Trigger
-- ###########################################################################
/*

*/
    -- #################################################
    --  Prevents Table Deletions is_system
    -- #################################################
        DELIMITER $$

                CREATE TRIGGER prevent_is_system_delete_on_ingredients_info
                BEFORE DELETE ON ingredients_info
                FOR EACH ROW
                BEGIN
                    IF OLD.is_system = TRUE THEN
                        SIGNAL SQLSTATE '45000'
                            SET MESSAGE_TEXT = 'Deletion not allowed: ingredients_info is_System';
                    END IF;
                END$$

        DELIMITER ;

    -- #############################################
    --  Prevents Table Update on is_system Variable
    -- #############################################
        DELIMITER $$

            CREATE TRIGGER prevent_update_on_ingredients_info_when_final
            BEFORE UPDATE ON ingredients_info
            FOR EACH ROW
            BEGIN
                IF OLD.is_system = TRUE THEN
                    SIGNAL SQLSTATE '45000'
                        SET MESSAGE_TEXT = 'Update not allowed on ingredients_info: record is FINAL';
                END IF;
            END$$

        DELIMITER ;

    -- #################################################
    --  Prevents Updates On Values
    -- #################################################
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
/*

*/


    -- #################################################
    --  Prevents Table Deletions is_system
    -- #################################################
        DELIMITER $$

                CREATE TRIGGER prevent_is_system_delete_on_stores
                BEFORE DELETE ON stores
                FOR EACH ROW
                BEGIN
                    IF OLD.is_system = TRUE THEN
                        SIGNAL SQLSTATE '45000'
                            SET MESSAGE_TEXT = 'Deletion not allowed: stores is_System';
                    END IF;
                END$$

        DELIMITER ;

    -- #############################################
    --  Prevents Table Update on is_system Variable
    -- #############################################
        DELIMITER $$

            CREATE TRIGGER prevent_update_on_stores_when_final
            BEFORE UPDATE ON stores
            FOR EACH ROW
            BEGIN
                IF OLD.is_system = TRUE THEN
                    SIGNAL SQLSTATE '45000'
                        SET MESSAGE_TEXT = 'Update not allowed on stores: record is FINAL';
                END IF;
            END$$

        DELIMITER ;

    -- #################################################
    --  Prevents Updates On Values
    -- #################################################
    DELIMITER $$

        CREATE TRIGGER trg_stores_allow_only_safe_updates
        BEFORE UPDATE ON stores
        FOR EACH ROW
        BEGIN
            IF NOT
            (
                -- Immutable Columns (Not Allowed)
                NEW.is_system <=> OLD.is_system
            )
            THEN
                SIGNAL SQLSTATE '45000'
                    SET MESSAGE_TEXT =
                        'Only : store_name can be updated on stores';
            END IF;
        END$$

    DELIMITER ;

-- ###########################################################################
-- ingredient_in_shops Trigger | Allows only certain rows to be updated
-- ###########################################################################
/*


*/

    -- #################################################
    --  Prevents Table Deletions is_system
    -- #################################################
        DELIMITER $$

                CREATE TRIGGER prevent_is_system_delete_on_ingredient_in_shops
                BEFORE DELETE ON ingredient_in_shops
                FOR EACH ROW
                BEGIN
                    IF OLD.is_system = TRUE THEN
                        SIGNAL SQLSTATE '45000'
                            SET MESSAGE_TEXT = 'Deletion not allowed: ingredient_in_shops is_System';
                    END IF;
                END$$

        DELIMITER ;

    -- #############################################
    --  Prevents Table Update on is_system Variable
    -- #############################################
        DELIMITER $$

            CREATE TRIGGER prevent_update_on_ingredient_in_shops_when_final
            BEFORE UPDATE ON ingredient_in_shops
            FOR EACH ROW
            BEGIN
                IF OLD.is_system = TRUE THEN
                    SIGNAL SQLSTATE '45000'
                        SET MESSAGE_TEXT = 'Update not allowed on ingredient_in_shops: record is FINAL';
                END IF;
            END$$

        DELIMITER ;

    -- #################################################
    --  Prevents Updates On Values
    -- #################################################
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

