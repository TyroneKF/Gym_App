-- ###########################################################################
-- ingredient_types Trigger
-- ###########################################################################
    /*

    */

    -- #############################################
    -- Prevents is_System Variable Deletion
    -- #############################################
        CREATE TRIGGER prevent_ingredient_types_delete_on_is_systems
        BEFORE DELETE ON ingredient_types
        FOR EACH ROW
        WHEN
            OLD.is_system = 1
        BEGIN
            SELECT RAISE(ABORT, 'DELETE Trigger - ingredient_types: system rows are immutable!');
        END;

    -- #################################################
    -- Allows Only Certain Rows to be Updated
    -- #################################################
        CREATE TRIGGER trg_ingredient_types_allow_only_safe_updates
        BEFORE UPDATE OF is_system
        ON ingredient_types
        FOR EACH ROW
            BEGIN
                SELECT RAISE(ABORT, 'Update Trigger - ingredient_types: system rows are immutable after insertion!');
            END;

        -- Immutable Columns (Not Allowed)
            -- is_system

        -- Mutable Columns (Implicitly Allowed):
            -- ingredient_type_name

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
                SELECT RAISE(ABORT, 'DELETE Trigger - measurement_material_type : Table is immutable !');
            END;

    -- #################################################
    --  Prevents Table Updates
    -- #################################################
        CREATE TRIGGER trg_measurement_material_type_prevent_any_update
        BEFORE UPDATE ON measurement_material_type
        FOR EACH ROW
            BEGIN
                SELECT RAISE(ABORT, 'UPDATE Trigger - measurement_material_type : Table is immutable');
            END;

    -- #################################################
    --  Prevents Table Inserts
    -- #################################################
        CREATE TRIGGER prevent_insert_on_measurement_material_type
        BEFORE INSERT ON measurement_material_type
        FOR EACH ROW
            BEGIN
                SELECT RAISE(ABORT,'INSERT Trigger - measurement_material_type : Table is immutable !');
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
                SELECT RAISE(ABORT, 'DELETE Trigger - measurements : Table is immutable !');
            END;

    -- #################################################
    --  Prevents Table Updates
    -- #################################################
        CREATE TRIGGER trg_measurements_prevent_any_update
        BEFORE UPDATE ON measurements
        FOR EACH ROW
            BEGIN
                    SELECT RAISE(ABORT, 'UPDATE Trigger -  measurements : Table is immutable !');
            END;

    -- #################################################
    --  Prevents Table Inserts
    -- #################################################
        CREATE TRIGGER prevent_insert_on_measurements
        BEFORE INSERT ON measurements
        FOR EACH ROW
            BEGIN
                 SELECT RAISE(ABORT, 'INSERT Trigger - measurements : Table is immutable !');
            END;

-- ###########################################################################
-- ingredients_info Trigger
-- ###########################################################################
    /*

    */

    -- #############################################
    --  Prevents Table Update on is_system Variable
    -- #############################################
        CREATE TRIGGER prevent_update_on_ingredients_info_system_rows_immutable
        BEFORE UPDATE ON ingredients_info
        FOR EACH ROW
        WHEN
            OLD.is_system = 1
        BEGIN
            SELECT RAISE(ABORT, 'UPDATE Trigger - ingredients_info Update : system rows are immutable after insertion!');
        END;

    -- #################################################
    --  Prevents Updates On Values
    -- #################################################
        CREATE TRIGGER trg_ingredients_info_allow_only_safe_updates
        BEFORE UPDATE OF is_system ON ingredients_info
        FOR EACH ROW
        BEGIN
            SELECT RAISE(ABORT, 'UPDATE  Trigger - ingredients_info : only mutable columns can change!');
        END;

         -- Immutable Columns (Not Allowed)
            -- is_system

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
            SELECT RAISE(ABORT, 'DELETE Trigger - stores : system rows are immutable !');
        END;

    -- #################################################
    --  Prevents Updates On Values
    -- #################################################
        CREATE TRIGGER trg_stores_allow_only_safe_updates
        BEFORE UPDATE OF is_system ON stores
        FOR EACH ROW
            BEGIN
                SELECT RAISE(ABORT,'UPDATE Trigger - Stores : system rows are immutable!');
            END;

        -- Immutable Columns (Not Allowed)
           -- is_system

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
            SELECT RAISE(ABORT, 'DELETE Trigger - ingredient_in_shops : system rows are immutable!');
        END;

    -- #################################################
    --  Prevents Updates On Values
    -- #################################################
        CREATE TRIGGER trg_ingredient_in_shops_allow_only_safe_updates
        BEFORE UPDATE OF ingredient_id, is_system
        ON ingredient_in_shops
        FOR EACH ROW
            BEGIN
                SELECT RAISE(ABORT, 'UPDATE Trigger - ingredient_in_shops : only mutable columns can change!');
            END;

        -- Immutable Columns (Not Allowed)
            -- is_system
            -- ingredient_id

        -- Mutable Columns (Implicitly Allowed):
            -- product_name
            -- volume_per_unit
            -- cost_per_unit
            -- store_id


