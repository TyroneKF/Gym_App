-- ###########################################################################
-- draft_plans | Prevent Certain Rows from being updated
-- ###########################################################################
    CREATE TRIGGER trg_draft_plans_allow_only_safe_updates
    BEFORE UPDATE OF user_id, plan_version_id ON draft_plans
    FOR EACH ROW
        BEGIN
            SELECT RAISE(ABORT, 'UPDATE Trigger - draft_plans only mutable columns can change!');
        END;

    -- Immutable Columns (Not Allowed)
        -- user_id
        -- plan_version_id

    -- Mutable Columns (Implicitly Allowed):
        -- date_time_last_edited

-- ###########################################################################
-- draft_macros_per_pound_and_limits |
-- ###########################################################################
    /*


    */
    -- ##########################################
    -- Allows only certain rows to be updated
    -- ##########################################
        CREATE TRIGGER trg_draft_macros_per_pound_and_limits_allow_only_safe_updates
        BEFORE UPDATE OF user_id ON draft_macros_per_pound_and_limits
        FOR EACH ROW
            BEGIN
                SELECT RAISE(ABORT, 'UPDATE Trigger - draft_macros_per_pound_and_limits only mutable columns can change! ');
            END;

            -- Immutable Columns (Not Allowed)
                -- user_id

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

-- ###########################################################################
-- draft_meals_in_plan Trigger | Prevent Certain Rows From Being Updated
-- ###########################################################################
    /*
    
    
    */    
    -- ##########################################
    -- Prevent Certain Rows From Being Updated
    -- ##########################################
        CREATE TRIGGER trg_draft_meals_in_plan_allow_only_safe_updates
        BEFORE UPDATE OF plan_id
        ON draft_meals_in_plan
        FOR EACH ROW
            BEGIN
                SELECT RAISE(ABORT, 'UPDATE Trigger -  draft_meals_in_plan table only mutable columns can change!');
            END;

         -- Immutable Columns (Not Allowed)
            -- plan_id

        -- Mutable Columns (Implicitly Allowed):
            -- meal_in_plan_id
            -- meal_name
            -- meal_time
            -- date_time_last_edited

    -- ##########################################
     -- On Update : Update Meal External Update
     -- ##########################################
         CREATE TRIGGER trg_on_draft_meals_update_then_update_plans_date
         AFTER UPDATE ON draft_meals_in_plan
         FOR EACH ROW
             BEGIN

                 -- Update plans Last Edit time when meals are updated
                 UPDATE draft_plans
                     SET date_time_last_edited = strftime('%Y-%m-%d %H:%M:%f', 'now')
                 WHERE  plan_id = OLD.plan_id;

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
                WHERE  plan_id = NEW.plan_id;

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
    -- On Update: Prevents Certain Rows Updating
    -- ##########################################
        CREATE TRIGGER trg_draft_divided_meal_sections_allow_only_safe_updates
        BEFORE UPDATE OF draft_meal_in_plan_id, plan_id
        ON draft_divided_meal_sections
        FOR EACH ROW
            BEGIN
                SELECT RAISE(ABORT,'UPDATE Trigger - draft_divided_meal_sections only mutable columns can change!');
            END;

         -- Immutable Columns (Not Allowed)
            -- draft_meal_in_plan_id
            -- plan_id

        -- Mutable Columns (Implicitly Allowed):
            -- div_meal_sections_id
            -- sub_meal_name
            -- sub_meal_time
            -- date_time_last_edited

    -- ##########################################
    -- After Update : External Table Update
    -- ##########################################
        CREATE TRIGGER trg_on_draft_sub_meals_update_then_update_meals_date
        AFTER UPDATE ON draft_divided_meal_sections
        FOR EACH ROW
            BEGIN

                -- Update Sub-meal Last Edit time when ingredients are updated
                UPDATE draft_meals_in_plan
                    SET date_time_last_edited = strftime('%Y-%m-%d %H:%M:%f', 'now')
                WHERE  draft_meal_in_plan_id = OLD.draft_meal_in_plan_id;

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
                WHERE  draft_meal_in_plan_id = NEW.draft_meal_in_plan_id;

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
        BEFORE UPDATE OF draft_div_meal_sections_id
        ON draft_ingredients_in_sections_of_meal
        FOR EACH ROW
            BEGIN
                SELECT RAISE(ABORT,'UPDATE Trigger - draft_ingredients_in_sections_of_meal only mutable columns can change!');
            END;

        -- Immutable Columns (Not Allowed)
            -- draft_div_meal_sections_id

        -- Mutable Columns (Implicitly Allowed):
            -- ingredient_id
            -- quantity
            -- pdid

    -- ##########################################
    -- On Update : Update Sub-Meal Date
    -- ##########################################
        CREATE TRIGGER trg_on_draft_ingredients_update_then_update_sub_meals_date
        AFTER UPDATE
        ON draft_ingredients_in_sections_of_meal
        FOR EACH ROW
            BEGIN

                -- Update Sub-meal Last Edit time when ingredients are updated
                UPDATE draft_divided_meal_sections
                    SET date_time_last_edited = strftime('%Y-%m-%d %H:%M:%f', 'now')
                WHERE draft_div_meal_sections_id = OLD.draft_div_meal_sections_id;

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