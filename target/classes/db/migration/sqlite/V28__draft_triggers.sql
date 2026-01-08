-- ###########################################################################
-- draft_plans | Prevent Certain Rows from being updated
-- ###########################################################################
    CREATE TRIGGER trg_draft_plans_allow_only_safe_updates
    BEFORE UPDATE ON draft_plans
    FOR EACH ROW
    WHEN
        -- Immutable Columns (Not Allowed)
        NEW.user_id  IS NOT OLD.user_id

        -- Mutable Columns (Implicitly Allowed):
            -- date_time_last_edited
    BEGIN
            SELECT RAISE(ABORT, "Trigger - 'draft_plans' only mutable columns can change!");
    END;

-- ###########################################################################
-- draft_macros_per_pound_and_limits |
-- ###########################################################################
    /*


    */
    -- ##########################################
    -- Allows only certain rows to be updated
    -- ##########################################
    CREATE TRIGGER trg_draft_macros_per_pound_and_limits_allow_only_safe_updates
    BEFORE UPDATE ON draft_macros_per_pound_and_limits
    FOR EACH ROW
    WHEN
        -- Immutable Columns (Not Allowed)
        NEW.user_id IS NOT OLD.user_id

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
        SELECT RAISE(ABORT, ' Trigger - draft_macros_per_pound_and_limits only mutable columns can change! ');
    END;

-- ###########################################################################
-- draft_meals_in_plan Trigger | Prevent Certain Rows From Being Updated
-- ###########################################################################
    /*
    
    
    */    
    -- ##########################################
    -- Prevent Certain Rows From Being Updated
    -- ##########################################
        CREATE TRIGGER trg_draft_meals_in_plan_allow_only_safe_updates
        BEFORE UPDATE ON draft_meals_in_plan
        FOR EACH ROW
        WHEN
                -- Immutable Columns (Not Allowed)
                NEW.meal_in_plan_id IS NOT OLD.meal_in_plan_id
                OR NEW.plan_id IS NOT OLD.plan_id
    
                -- Mutable Columns (Implicitly Allowed):
                    -- meal_name
                    -- meal_time
                    -- date_time_last_edited
    
        BEGIN
            SELECT RAISE(ABORT, ' Trigger -  draft_meals_in_plan table only mutable columns can change!');
        END;
    
    -- ##########################################
    -- On INSERT : Update Meal
    -- ##########################################
        CREATE TRIGGER trg_on_draft_meals_insert_then_update_plans_date
        AFTER INSERT ON draft_meals_in_plan
        FOR EACH ROW
        BEGIN

            -- Update plans Last Edit time when meals are updated
            UPDATE draft_plans 
                SET date_time_last_edited = strftime('%Y-%m-%d %H:%M:%f', 'now')
            WHERE  plan_id = NEW. plan_id;

        END;

    -- ##########################################
    -- On Update : Update Meal External Update
    -- ##########################################
        CREATE TRIGGER trg_on_draft_meals_update_then_update_plans_date
        AFTER UPDATE ON draft_meals_in_plan
        FOR EACH ROW
        WHEN
             NEW.meal_name    IS NOT OLD.meal_name
             OR NEW.meal_time IS NOT OLD.meal_time
        BEGIN

            -- Update plans Last Edit time when meals are updated
            UPDATE draft_plans 
                SET date_time_last_edited = strftime('%Y-%m-%d %H:%M:%f', 'now')
            WHERE  plan_id = OLD.plan_id;

        END;

    -- ##########################################
    -- On Delete : Update Sub-Meal
    -- ##########################################
        CREATE TRIGGER trg_on_draft_meals_delete_then_update_plans_date
        AFTER DELETE ON draft_meals_in_plan
        FOR EACH ROW
        BEGIN

            -- Update plans Last Edit time when meals are updated
            UPDATE draft_plans 
                SET date_time_last_edited = strftime('%Y-%m-%d %H:%M:%f', 'now')
            WHERE  plan_id = OLD.plan_id;

        END;

-- ###########################################################################
-- draft_divided_meal_sections Trigger
-- ###########################################################################
    /*

    */

    -- ##########################################
    -- Allows Only Certain Rows To Be Updated
    -- ##########################################
        CREATE TRIGGER trg_draft_divided_meal_sections_allow_only_safe_updates
        BEFORE UPDATE ON draft_divided_meal_sections
        FOR EACH ROW
        WHEN
            -- Immutable Columns (Not Allowed)
            NEW.div_meal_sections_id IS NOT OLD.div_meal_sections_id
            AND NEW.draft_meal_in_plan_id IS NOT OLD.draft_meal_in_plan_id
            AND NEW.plan_id IS NOT OLD.plan_id

            -- Mutable Columns (Implicitly Allowed):
                -- sub_meal_name
                -- sub_meal_time
                -- date_time_last_edited
        BEGIN
            SELECT RAISE(ABORT,'Trigger - draft_divided_meal_sections only mutable columns can change!');
        END;

    -- ##########################################
    -- On INSERT : Update Sub-Meal
    -- ##########################################
        CREATE TRIGGER trg_on_draft_sub_meals_insert_then_update_meals_date
        AFTER INSERT ON draft_divided_meal_sections
        FOR EACH ROW
        BEGIN

            -- Update Sub-meal Last Edit time when ingredients are updated
            UPDATE draft_meals_in_plan
                SET date_time_last_edited = strftime('%Y-%m-%d %H:%M:%f', 'now')
            WHERE  draft_meal_in_plan_id = NEW. draft_meal_in_plan_id;

        END;

    -- ##########################################
    -- On Update : Update Meal External Update
    -- ##########################################
        CREATE TRIGGER trg_on_draft_sub_meals_update_then_update_meals_date
        AFTER UPDATE ON draft_divided_meal_sections
        FOR EACH ROW
        WHEN
             NEW.date_time_last_edited  IS NOT OLD.date_time_last_edited
        BEGIN

            -- Update Sub-meal Last Edit time when ingredients are updated
            UPDATE draft_meals_in_plan
                SET date_time_last_edited = strftime('%Y-%m-%d %H:%M:%f', 'now')
            WHERE  draft_meal_in_plan_id = OLD.draft_meal_in_plan_id;

        END;

    -- ##########################################
    -- On Delete : Update Sub-Meal
    -- ##########################################
        CREATE TRIGGER trg_on_draft_sub_meals_delete_then_update_meals_date
        AFTER DELETE ON draft_divided_meal_sections
        FOR EACH ROW
        BEGIN

            -- Update Sub-meal Last Edit time when ingredients are updated
            UPDATE draft_meals_in_plan
                SET date_time_last_edited = strftime('%Y-%m-%d %H:%M:%f', 'now')
            WHERE  draft_meal_in_plan_id = OLD.draft_meal_in_plan_id;

        END;

-- ###########################################################################
-- draft_ingredients_in_sections_of_meal Trigger
-- ###########################################################################
    /*

    */

    -- ##########################################
    -- Allows Only Certain Rows To Be Updated
    -- ##########################################
        CREATE TRIGGER trg_ingredients_in_meal_versions_allow_only_safe_updates
        BEFORE UPDATE ON draft_ingredients_in_sections_of_meal
        FOR EACH ROW
        WHEN
            -- Immutable Columns (Not Allowed)
            NEW.draft_div_meal_sections_id IS NOT OLD.draft_div_meal_sections_id

            -- Mutable Columns (Implicitly Allowed):
                -- ingredient_id
                -- quantity
                -- pdid
        BEGIN
            SELECT RAISE(ABORT,'Trigger - draft_ingredients_in_sections_of_meal only mutable columns can change!');
        END;

    -- ##########################################
    -- On INSERT : Update Sub-Meal Date
    -- ##########################################
        CREATE TRIGGER trg_on_draft_ingredients_insert_then_update_sub_meals_date
        AFTER INSERT ON draft_ingredients_in_sections_of_meal
        FOR EACH ROW
        BEGIN

            -- Update Sub-meal Last Edit time when ingredients are updated
            UPDATE draft_divided_meal_sections
                SET date_time_last_edited = strftime('%Y-%m-%d %H:%M:%f', 'now')
            WHERE draft_div_meal_sections_id = NEW.draft_div_meal_sections_id;

        END;

    -- ##########################################
    -- On Update : Update Sub-Meal Date
    -- ##########################################
        CREATE TRIGGER trg_on_draft_ingredients_update_then_update_sub_meals_date
        AFTER UPDATE ON draft_ingredients_in_sections_of_meal
        FOR EACH ROW
        WHEN
             NEW.quantity IS NOT OLD.quantity
             OR NEW.ingredient_id IS NOT OLD.ingredient_id
             OR NEW.pdid IS NOT OLD.pdid
        BEGIN

            -- Update Sub-meal Last Edit time when ingredients are updated
            UPDATE draft_divided_meal_sections
                SET date_time_last_edited = strftime('%Y-%m-%d %H:%M:%f', 'now')
            WHERE draft_div_meal_sections_id = OLD.draft_div_meal_sections_id;

        END;

    -- ##########################################
    -- On Delete : Update Sub-Meal Date
    -- ##########################################
        CREATE TRIGGER trg_on_draft_ingredients_delete_then_update_sub_meals_date
        AFTER DELETE ON draft_ingredients_in_sections_of_meal
        FOR EACH ROW
        BEGIN

            -- Update Sub-meal Last Edit time when ingredients are updated
            UPDATE draft_divided_meal_sections
                SET date_time_last_edited = strftime('%Y-%m-%d %H:%M:%f', 'now')
            WHERE draft_div_meal_sections_id = OLD.draft_div_meal_sections_id;

        END;