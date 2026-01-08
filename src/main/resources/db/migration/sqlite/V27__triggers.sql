-- ###########################################################################
-- Plans Trigger | Prevents CERTAIN Rows from being updated
-- ###########################################################################
    CREATE TRIGGER trg_plans_allow_only_safe_updates
    BEFORE UPDATE ON plans
    FOR EACH ROW
    WHEN

        -- Immutable Columns (Not Allowed)
        NEW.date_time_of_creation  != OLD.date_time_of_creation
        AND NEW.user_id            != OLD.user_id
        AND NEW.vegan              != OLD.vegan

        -- Mutable Columns (Implicitly Allowed):
            -- plan_name

    BEGIN
            SELECT RAISE(ABORT, "Trigger - Only approved columns may be updated on 'plans': being 'plan_name'");
    END;

-- ###########################################################################
-- Plans_versions | Prevents table from being updated
-- ###########################################################################
    CREATE TRIGGER trg_draft_plans_versions_prevent_any_update
    BEFORE UPDATE ON plan_versions
    FOR EACH ROW
    BEGIN
      SELECT RAISE(ABORT, ' Trigger -  plans_versions table is immutable');
    END;

-- ###########################################################################
-- draft_plans | Prevent Certain Rows from being updated
-- ###########################################################################
    CREATE TRIGGER trg_draft_plans_allow_only_safe_updates
    BEFORE UPDATE ON draft_plans
    FOR EACH ROW
    WHEN
        -- Immutable Columns (Not Allowed)
        NEW.user_id  != user_id

        -- Mutable Columns (Implicitly Allowed):
            -- date_time_last_edited
    BEGIN
            SELECT RAISE(ABORT, "Trigger -  'draft_plans' Only approved columns may be updated on : being 'date_time_last_edited'");
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
-- draft_macros_per_pound_and_limits | Allows only certain rows to be updated
-- ###########################################################################
    CREATE TRIGGER trg_draft_macros_per_pound_and_limits_allow_only_safe_updates
    BEFORE UPDATE ON draft_macros_per_pound_and_limits
    FOR EACH ROW
    WHEN
        -- Immutable Columns (Not Allowed)
        NEW.user_id != OLD.user_id

        -- Mutable Columns (Implicitly Allowed):     
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

    BEGIN
        SELECT RAISE(ABORT, ' Trigger - draft_macros_per_pound_and_limits only date & Macro Values can be updated ! ');
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
            SELECT RAISE(ABORT, ' Trigger -   - Update not allowed on ingredient_types: record is FINAL');
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
            SELECT RAISE(ABORT, ' Trigger -   - Deletion not allowed on record with is_system');
        END;

    -- #################################################
    -- Allows Only Certain Rows to be Updated
    -- #################################################
        CREATE TRIGGER trg_ingredient_types_allow_only_safe_updates
        BEFORE UPDATE ON ingredient_types
        FOR EACH ROW
        WHEN
                -- Immutable Columns (Not Allowed)
                NEW.is_system != OLD.is_system

                -- Mutable Columns (Implicitly Allowed):
                    -- ingredient_type_name
        BEGIN
            SELECT RAISE(ABORT, ' Trigger -  Only ingredient_type_name may be updated on ingredient_types');
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
            SELECT RAISE(ABORT, ' Trigger -  DELETE is not allowed on this measurement_material_type');
        END;

    -- #################################################
    --  Prevents Table Updates
    -- #################################################
        CREATE TRIGGER trg_measurement_material_type_prevent_any_update
        BEFORE UPDATE ON measurement_material_type
        FOR EACH ROW
        BEGIN
            SELECT RAISE(ABORT, ' Trigger -  measurement_material_type table is immutable');
        END;

    -- #################################################
    --  Prevents Table Inserts
    -- #################################################
        CREATE TRIGGER prevent_insert_on_measurement_material_type
        BEFORE INSERT ON measurement_material_type
        FOR EACH ROW
        BEGIN
            SELECT RAISE(ABORT,'Inserts are not allowed on table measurement_material_type');
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
            SELECT RAISE(ABORT, ' Trigger -  DELETE is not allowed on this measurements');
        END;

    -- #################################################
    --  Prevents Table Updates
    -- #################################################
        CREATE TRIGGER trg_measurements_prevent_any_update
        BEFORE UPDATE ON measurements
        FOR EACH ROW
        BEGIN
                SELECT RAISE(ABORT, ' Trigger -  measurements table is immutable');
        END;

    -- #################################################
    --  Prevents Table Inserts
    -- #################################################
        CREATE TRIGGER prevent_insert_on_measurements
        BEFORE INSERT ON measurements
        FOR EACH ROW
        BEGIN
             SELECT RAISE(ABORT, ' Trigger -  Inserts are not allowed on table measurements');
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
            SELECT RAISE(ABORT, ' Trigger -  Deletion not allowed: ingredients_info is_System');
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
            SELECT RAISE(ABORT, ' Trigger -  Update not allowed on ingredients_info: record is FINAL');
        END;

    -- #################################################
    --  Prevents Updates On Values
    -- #################################################
        CREATE TRIGGER trg_ingredients_info_allow_only_safe_updates
        BEFORE UPDATE ON ingredients_info
        FOR EACH ROW
        WHEN
            -- Immutable Columns (Not Allowed)
            NEW.is_system != OLD.is_system

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
            SELECT RAISE(ABORT, ' Trigger -  Only : measurement_id, ingredient_name, ingredient_type_id, based_on_quantity, glycemic_index, protein, carbohydrates, sugars_of_carbs, fibre, fat, saturated_fat, salt, water_content, liquid_content, calories can be updated on ingredients_info');
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
            SELECT RAISE(ABORT, ' Trigger -  Deletion not allowed: stores is_System');
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
            SELECT RAISE(ABORT, ' Trigger -  Update not allowed on stores: record is FINAL');
        END;

    -- #################################################
    --  Prevents Updates On Values
    -- #################################################
        CREATE TRIGGER trg_stores_allow_only_safe_updates
        BEFORE UPDATE ON stores
        FOR EACH ROW
        WHEN
                -- Immutable Columns (Not Allowed)
                NEW.is_system != OLD.is_system
        BEGIN
            SELECT RAISE(ABORT,'Only : store_name can be updated on stores');
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
            SELECT RAISE(ABORT, ' Trigger -  Deletion not allowed: ingredient_in_shops is_System');
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
            SELECT RAISE(ABORT, ' Trigger -  Update not allowed on ingredient_in_shops: record is FINAL');
        END;

    -- #################################################
    --  Prevents Updates On Values
    -- #################################################
        CREATE TRIGGER trg_ingredient_in_shops_allow_only_safe_updates
        BEFORE UPDATE ON ingredient_in_shops
        FOR EACH ROW
        WHEN
            -- Immutable Columns (Not Allowed)
            NEW.is_system != OLD.is_system
            AND NEW.ingredient_id != OLD.ingredient_id

            -- Mutable Columns (Implicitly Allowed):
                -- product_name
                -- volume_per_unit
                -- cost_per_unit
                -- store_id

        BEGIN
            SELECT RAISE(ABORT, ' Trigger -  Only product_name, volume_per_unit, cost_per_unit, store_id  can be updated on ingredient_in_shops');
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
-- draft_meals_in_plan Trigger | Prevent Certain Rows From Being Updated
-- ###########################################################################
    CREATE TRIGGER trg_draft_meals_in_plan_prevent_any_update
    BEFORE UPDATE ON draft_meals_in_plan
    FOR EACH ROW
    WHEN
            -- Immutable Columns (Not Allowed)
            NEW.meal_in_plan_id != OLD.meal_in_plan_id
            AND NEW.plan_id != OLD.plan_id
    
            -- Mutable Columns (Implicitly Allowed):
                -- meal_name
                -- meal_time
                -- date_time_last_edited
    
    BEGIN
        SELECT RAISE(ABORT, ' Trigger -  draft_meals_in_plan table only the following fields are mutable: meal_name, meal_time, date_time_last_edited! ');
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
-- draft_divided_meal_sections Trigger | Allows only certain rows to be updated
-- ###########################################################################
    CREATE TRIGGER trg_draft_divided_meal_sections_allow_only_safe_updates
    BEFORE UPDATE ON draft_divided_meal_sections
    FOR EACH ROW
    WHEN
        -- Immutable Columns (Not Allowed)
        NEW.div_meal_sections_id != OLD.div_meal_sections_id
        AND NEW.draft_meal_in_plan_id != OLD.draft_meal_in_plan_id
        AND NEW.plan_id != OLD.plan_id

        -- Mutable Columns (Implicitly Allowed):
            -- sub_meal_name
            -- sub_meal_time
            -- date_time_last_edited
    BEGIN
        SELECT RAISE(ABORT,'Trigger - draft_divided_meal_sections Only: sub_meal_name, sub_meal_time, date_time_last_edited can be updated!');
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


-- ###########################################################################
-- ingredients_in_sections_of_meal Trigger | Allows only certain rows to be updated
-- ###########################################################################
    CREATE TRIGGER trg_ingredients_in_meal_versions_allow_only_safe_updates
    BEFORE UPDATE ON draft_ingredients_in_sections_of_meal
    FOR EACH ROW
    WHEN
        -- Immutable Columns (Not Allowed)
        NEW.draft_div_meal_sections_id != OLD.draft_div_meal_sections_id
        AND NEW.div_meal_sections_version_id != OLD.div_meal_sections_version_id

        -- Mutable Columns (Implicitly Allowed):
            -- ingredient_id
            -- quantity
            -- pdid
    BEGIN
        SELECT RAISE(ABORT,'Trigger - draft_ingredients_in_sections_of_meal Only: ingredient_id, quantity and pdid can be updated on draft_ingredients_in_sections_of_meal');
    END;



