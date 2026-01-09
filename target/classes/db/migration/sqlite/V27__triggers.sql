-- ###########################################################################
-- Plans Trigger | Prevents CERTAIN Rows from being updated
-- ###########################################################################
    CREATE TRIGGER trg_plans_allow_only_safe_updates
    BEFORE UPDATE ON plans
    FOR EACH ROW
    WHEN

        -- Immutable Columns (Not Allowed)
        NEW.date_time_of_creation  IS NOT OLD.date_time_of_creation
        AND NEW.user_id            IS NOT OLD.user_id
        AND NEW.vegan              IS NOT OLD.vegan

        -- Mutable Columns (Implicitly Allowed):
            -- plan_name

    BEGIN
            SELECT RAISE(ABORT, "Trigger - plans only mutable columns can change!");
    END;

-- ###########################################################################
-- Plans_versions | Prevents table from being updated
-- ###########################################################################
    CREATE TRIGGER trg_draft_plans_versions_prevent_any_update
    BEFORE UPDATE ON plan_versions
    FOR EACH ROW
    BEGIN
      SELECT RAISE(ABORT, ' Trigger - plans_versions table is immutable');
    END;

-- ###########################################################################
-- macros_per_pound_and_limits Trigger | Prevents table from being updated
-- ###########################################################################
    CREATE TRIGGER trg_macros_per_pound_and_limits_prevent_any_update
    BEFORE UPDATE ON macros_per_pound_and_limits
    FOR EACH ROW
    BEGIN
            SELECT RAISE(ABORT, ' Trigger - macros_per_pound_and_limits table is immutable');
    END;

-- ###########################################################################
-- ingredient_types Trigger
-- ###########################################################################
    /*

    */

    -- #################################################
    -- Prevent is_System Rows from being Updated
    -- #################################################
        CREATE TRIGGER prevent_update_on_ingredient_types_when_final
        BEFORE UPDATE ON ingredient_types
        FOR EACH ROW
        WHEN
             OLD.is_system = 1
        BEGIN
            SELECT RAISE(ABORT, ' Trigger - ingredient_types cannot update because record is FINAL (is_system = true)');
        END;

    -- #############################################
    -- Prevents is_System Variable Deletion
    -- #############################################
        CREATE TRIGGER prevent_ingredient_types_delete_on_is_systems
        BEFORE DELETE ON ingredient_types
        FOR EACH ROW
        WHEN
            OLD.is_system = 1
        BEGIN
            SELECT RAISE(ABORT, ' Trigger - ingredient_types Deletion not allowed on record with is_system = true');
        END;

    -- #################################################
    -- Allows Only Certain Rows to be Updated
    -- #################################################
        CREATE TRIGGER trg_ingredient_types_allow_only_safe_updates
        BEFORE UPDATE ON ingredient_types
        FOR EACH ROW
        WHEN
                -- Immutable Columns (Not Allowed)
                NEW.is_system IS NOT OLD.is_system

                -- Mutable Columns (Implicitly Allowed):
                    -- ingredient_type_name
        BEGIN
            SELECT RAISE(ABORT, ' Trigger - ingredient_types only mutable columns can change!');
        END;

-- ###########################################################################
-- Measurements_Material_Type Trigger | Once insert Immutable
-- ###########################################################################
    /*


    */

    -- #################################################
    --  Prevents Table Deletions
    -- #################################################
        CREATE TRIGGER prevent_delete_on_measurement_material_type
        BEFORE DELETE ON measurement_material_type
        FOR EACH ROW
        BEGIN
            SELECT RAISE(ABORT, ' Trigger - measurement_material_type DELETE is not allowed on this table');
        END;

    -- #################################################
    --  Prevents Table Updates
    -- #################################################
        CREATE TRIGGER trg_measurement_material_type_prevent_any_update
        BEFORE UPDATE ON measurement_material_type
        FOR EACH ROW
        BEGIN
            SELECT RAISE(ABORT, ' Trigger - measurement_material_type table is immutable');
        END;

    -- #################################################
    --  Prevents Table Inserts
    -- #################################################
        CREATE TRIGGER prevent_insert_on_measurement_material_type
        BEFORE INSERT ON measurement_material_type
        FOR EACH ROW
        BEGIN
            SELECT RAISE(ABORT,'Trigger - measurement_material_type Inserts are not allowed on this table!');
        END;

-- ###########################################################################
-- measurements Trigger | Allows only certain rows to be updated
-- ###########################################################################
    /*

    */

    -- #################################################
    --  Prevents Table Deletions
    -- #################################################
        CREATE TRIGGER prevent_delete_on_measurements
        BEFORE DELETE ON measurements
        FOR EACH ROW
        BEGIN
            SELECT RAISE(ABORT, ' Trigger - measurements DELETE is not allowed on this table!');
        END;

    -- #################################################
    --  Prevents Table Updates
    -- #################################################
        CREATE TRIGGER trg_measurements_prevent_any_update
        BEFORE UPDATE ON measurements
        FOR EACH ROW
        BEGIN
                SELECT RAISE(ABORT, ' Trigger -  measurements table is immutable!');
        END;

    -- #################################################
    --  Prevents Table Inserts
    -- #################################################
        CREATE TRIGGER prevent_insert_on_measurements
        BEFORE INSERT ON measurements
        FOR EACH ROW
        BEGIN
             SELECT RAISE(ABORT, ' Trigger - measurements Inserts are not allowed on this table!');
        END;

-- ###########################################################################
-- ingredients_info Trigger
-- ###########################################################################
    /*

    */
    -- #################################################
    --  Prevents Table Deletions is_system
    -- #################################################
        CREATE TRIGGER prevent_is_system_delete_on_ingredients_info
        BEFORE DELETE ON ingredients_info
        FOR EACH ROW
        WHEN
             OLD.is_system = 1
        BEGIN
            SELECT RAISE(ABORT, ' Trigger - ingredients_info Deletion not allowed on rows with is_System = true');
        END;

    -- #############################################
    --  Prevents Table Update on is_system Variable
    -- #############################################
        CREATE TRIGGER prevent_update_on_ingredients_info_when_final
        BEFORE UPDATE ON ingredients_info
        FOR EACH ROW
        WHEN
            OLD.is_system = 1
        BEGIN
            SELECT RAISE(ABORT, ' Trigger - ingredients_info Update not allowed on ingredients_info: record is FINAL');
        END;

    -- #################################################
    --  Prevents Updates On Values
    -- #################################################
        CREATE TRIGGER trg_ingredients_info_allow_only_safe_updates
        BEFORE UPDATE ON ingredients_info
        FOR EACH ROW
        WHEN
            -- Immutable Columns (Not Allowed)
            NEW.is_system IS NOT OLD.is_system

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

        BEGIN
            SELECT RAISE(ABORT, ' Trigger - ingredients_info only mutable columns can change!');
        END;

-- ###########################################################################
-- stores Trigger | Allows only certain rows to be updated
-- ###########################################################################
    /*

    */


    -- #################################################
    --  Prevents Table Deletions is_system
    -- #################################################
        CREATE TRIGGER prevent_is_system_delete_on_stores
        BEFORE DELETE ON stores
        FOR EACH ROW
        WHEN
             OLD.is_system = 1
        BEGIN
            SELECT RAISE(ABORT, ' Trigger - stores Deletion not allowed on record with is_System = true');
        END;

    -- #############################################
    --  Prevents Table Update on is_system Variable
    -- #############################################
        CREATE TRIGGER prevent_update_on_stores_when_final
        BEFORE UPDATE ON stores
        FOR EACH ROW
        WHEN
            OLD.is_system = 1
        BEGIN
            SELECT RAISE(ABORT, ' Trigger - stores cannot update record because record is FINAL (is_system = true)');
        END;

    -- #################################################
    --  Prevents Updates On Values
    -- #################################################
        CREATE TRIGGER trg_stores_allow_only_safe_updates
        BEFORE UPDATE ON stores
        FOR EACH ROW
        WHEN
                -- Immutable Columns (Not Allowed)
                NEW.is_system IS NOT OLD.is_system
        BEGIN
            SELECT RAISE(ABORT,'Trigger - Stores only mutable columns can change!');
        END;

-- ###########################################################################
-- ingredient_in_shops Trigger | Allows only certain rows to be updated
-- ###########################################################################
    /*


    */

    -- #################################################
    --  Prevents Table Deletions is_system
    -- #################################################
        CREATE TRIGGER prevent_is_system_delete_on_ingredient_in_shops
        BEFORE DELETE ON ingredient_in_shops
        FOR EACH ROW
        WHEN
             OLD.is_system = 1
        BEGIN
            SELECT RAISE(ABORT, ' Trigger - ingredient_in_shops Deletion not allowed: because record is FINAL (is_system = true)');
        END;

    -- #############################################
    --  Prevents Table Update on is_system Variable
    -- #############################################
        CREATE TRIGGER prevent_update_on_ingredient_in_shops_when_final
        BEFORE UPDATE ON ingredient_in_shops
        FOR EACH ROW
        WHEN
             OLD.is_system = 1
        BEGIN
            SELECT RAISE(ABORT, ' Trigger - ingredient_in_shops cannot update record because is FINAL (is_system = true)');
        END;

    -- #################################################
    --  Prevents Updates On Values
    -- #################################################
        CREATE TRIGGER trg_ingredient_in_shops_allow_only_safe_updates
        BEFORE UPDATE ON ingredient_in_shops
        FOR EACH ROW
        WHEN
            -- Immutable Columns (Not Allowed)
            NEW.is_system IS NOT OLD.is_system
            AND NEW.ingredient_id IS NOT OLD.ingredient_id

            -- Mutable Columns (Implicitly Allowed):
                -- product_name
                -- volume_per_unit
                -- cost_per_unit
                -- store_id

        BEGIN
            SELECT RAISE(ABORT, ' Trigger - ingredient_in_shops only mutable columns can change!');
        END;



-- ###########################################################################
-- meals_in_plan  Trigger | Prevents table from being updated
-- ###########################################################################
    CREATE TRIGGER trg_meals_in_plan_prevent_any_update
    BEFORE UPDATE ON meals_in_plan
    FOR EACH ROW
    BEGIN
            SELECT RAISE(ABORT, ' Trigger -  meals_in_plan table is immutable');
    END;

-- ###########################################################################
-- meals_in_plan_versions Trigger | Prevent Table From Being Updated
-- ###########################################################################
    CREATE TRIGGER trg_meals_in_plan_versions_prevent_any_update
    BEFORE UPDATE ON meals_in_plan_versions
    FOR EACH ROW
    BEGIN
        SELECT RAISE(ABORT, ' Trigger -  meals_in_plan_versions table is immutable');
    END;

-- ###########################################################################
-- divided_meal_sections Trigger | Prevents table from being updated
-- ###########################################################################
    CREATE TRIGGER trg_divided_meal_sections_prevent_any_update
    BEFORE UPDATE ON divided_meal_sections
    FOR EACH ROW
    BEGIN
        SELECT RAISE(ABORT, ' Trigger -  divided_meal_sections table is immutable');
    END;

-- ###########################################################################
-- divided_meal_sections_versions Trigger | Allows only certain rows to be updated
-- ###########################################################################
    CREATE TRIGGER trg_divided_meal_sections_versions_prevent_any_update
    BEFORE UPDATE ON divided_meal_sections_versions
    FOR EACH ROW
    BEGIN
        SELECT RAISE(ABORT, ' Trigger -  divided_meal_sections_versions table is immutable');
    END;

-- ###########################################################################
-- ingredients_in_sections_of_meal Trigger | Prevents table from being updated
-- ###########################################################################
    CREATE TRIGGER trg_ingredients_in_sections_of_meal_prevent_any_update
    BEFORE UPDATE ON ingredients_in_sections_of_meal
    FOR EACH ROW
    BEGIN
        SELECT RAISE(ABORT, ' Trigger -  ingredients_in_sections_of_meal table is immutable');
    END;





