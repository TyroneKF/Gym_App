
-- ################################################################################
-- Macro Calculations
-- ################################################################################
    /*

    */

    -- ############################################
    --
    -- ############################################
        CREATE VIEW all_plan_macro_target_calculations AS

            WITH
                C1 AS (

                    SELECT
                       'draft' AS record_state,
                        plan_id AS plan_id,

                        NULL AS date_time_of_creation,

                        user_id,
                        current_weight_kg,
                        current_weight_in_pounds,
                        body_fat_percentage,
                        protein_per_pound,
                        carbohydrates_per_pound,
                        fibre,
                        fats_per_pound,
                        saturated_fat_limit,
                        salt_limit,
                        water_target,
                        additional_calories

                    FROM draft_macros_per_pound_and_limits

                    UNION ALL

                    SELECT
                       'versioned' AS record_state,
                        plan_version_id AS plan_id,

                        date_time_of_creation,

                        user_id,
                        current_weight_kg,
                        current_weight_in_pounds,
                        body_fat_percentage,
                        protein_per_pound,
                        carbohydrates_per_pound,
                        fibre,
                        fats_per_pound,
                        saturated_fat_limit,
                        salt_limit,
                        water_target,
                        additional_calories

                    FROM macros_per_pound_and_limits
                ),
                C2 AS ( -- Macro Target Calculations

                    SELECT
                        record_state,
                        plan_id,

                        ROUND(current_weight_in_pounds * protein_per_pound, 2) AS t_protein, -- returns null if 1 of the values are empty
                        ROUND(current_weight_in_pounds * carbohydrates_per_pound, 2) AS t_carbs,
                        ROUND(current_weight_in_pounds * fats_per_pound, 2) AS t_fats
                    FROM C1
                ),
                C3 AS ( -- Base Calories Calculations

                      SELECT
                          plan_id,
                          record_state,

                          ROUND((t_protein * 4) + (t_carbs * 4) + (t_fats * 9) ,2) AS calories_target
                      FROM C2
                )

            SELECT
                C1.record_state,
                C1.plan_id,
                C1.user_id,

                C1.date_time_of_creation,

                C2.t_protein             AS expected_protein_grams,
                C2.t_carbs               AS expected_carbs_grams,
                C1.fibre                 AS expected_fibre_grams,
                C2.t_fats                AS expected_fats_grams,
                C1.saturated_fat_limit   AS saturated_fat_limit,
                C1.salt_limit            AS salt_limit_grams,
                C1.water_target          AS water_content_target,

                ROUND(C3.calories_target ,2) AS calories_target,
                ROUND(C3.calories_target + C1.additional_calories ,2) AS additional_calories_target

            FROM C1

            LEFT JOIN C2
                ON C1.record_state = C2.record_state AND C1.plan_id = C2.plan_id

            LEFT JOIN C3
                ON C2.record_state = C3.record_state AND C2.plan_id = C3.plan_id;

    -- ############################################
    -- Draft : View For Macro Calculations
    -- ############################################
       CREATE VIEW draft_plan_macro_target_calculations AS
       SELECT
            plan_id AS plan_id,
            date_time_of_creation,

            expected_protein_grams,
            expected_carbs_grams,
            expected_fats_grams,
            saturated_fat_limit,
            expected_fibre_grams,
            salt_limit_grams,
            water_content_target,
            calories_target,
            additional_calories_target

       FROM all_plan_macro_target_calculations
       WHERE record_state = 'draft';

    -- ############################################
    -- Draft : View For Macro Calculations
    -- ############################################
       CREATE VIEW draft_gui_plan_macro_target_calculations AS
       SELECT
            plan_id AS plan_id,

            expected_protein_grams,
            expected_carbs_grams,
            expected_fats_grams,
            saturated_fat_limit,
            expected_fibre_grams,
            salt_limit_grams,
            water_content_target,
            calories_target,
            additional_calories_target

       FROM all_plan_macro_target_calculations
       WHERE record_state = 'draft';

    -- ############################################
    -- Versioned : View For Macro Calculations
    -- ############################################
        CREATE VIEW versioned_plan_macro_target_calculations AS
        SELECT
            plan_id AS plan_version_id,
            date_time_of_creation,

            expected_protein_grams,
            expected_carbs_grams,
            expected_fats_grams,
            saturated_fat_limit,
            expected_fibre_grams,
            salt_limit_grams,
            water_content_target,
            calories_target,
            additional_calories_target

        FROM all_plan_macro_target_calculations
        WHERE record_state = 'versioned';

-- ################################################################################
--
-- ################################################################################
    /*


    */

    -- ###########################################
    -- ALL (Source) : Ingredients_Calculations
    -- ###########################################
        CREATE VIEW all_ingredients_in_sections_of_meal_calculation AS

            WITH
                D AS ( -- DATA from draft_ingredients_in_sections_of_meal & ingredients_in_sections_of_meal

                        SELECT
                           'draft' AS record_state,
                            draft_ingredients_index AS id,
                            draft_div_meal_sections_id AS div_id,
                            ingredient_id,
                            pdid,
                            quantity

                        FROM draft_ingredients_in_sections_of_meal

                        UNION ALL

                        SELECT
                            'versioned' AS record_state,
                            ingredients_index AS id,
                            div_meal_sections_version_id AS div_id,
                            ingredient_id,
                            pdid,
                            quantity

                        FROM ingredients_in_sections_of_meal
                )

            SELECT

                D.record_state,
                D.id,
                D.div_id,

                Info.ingredient_type_id,
                D.ingredient_id,
                D.quantity,

                Info.glycemic_index                                                                AS gi,
                IFNULL(ROUND((Info.protein / Info.based_on_quantity) * D.quantity, 2),0)           AS protein,
                IFNULL(ROUND((Info.carbohydrates / Info.based_on_quantity) * D.quantity, 2),0)     AS carbohydrates,
                IFNULL(ROUND((Info.sugars_of_carbs / Info.based_on_quantity) * D.quantity, 2),0)   AS sugars_of_carbs,
                IFNULL(ROUND((Info.fibre / Info.based_on_quantity) * D.quantity, 2),0)             AS fibre,
                IFNULL(ROUND((Info.fat / Info.based_on_quantity) * D.quantity, 2),0)               AS fat,
                IFNULL(ROUND((Info.saturated_fat / Info.based_on_quantity) * D.quantity, 2),0)     AS saturated_fat,
                IFNULL(ROUND((Info.salt / Info.based_on_quantity) * D.quantity, 2),0)              AS salt,
                IFNULL(ROUND((Info.water_content / Info.based_on_quantity) * D.quantity, 2),0)     AS water_content,
                IFNULL(ROUND((Info.calories / Info.based_on_quantity) * D.quantity, 2),0)          AS calories

            FROM  D

            LEFT JOIN ingredients_info Info
                ON D.ingredient_id = Info.ingredient_id;

    -- ###########################################
    -- Versioned : Ingredients_In_Meal
    -- ###########################################
        CREATE VIEW versioned_ingredients_in_sections_of_meal_calculation AS

            SELECT

                id  AS ingredients_index,
                div_id AS div_meal_sections_version_id,

                ingredient_type_id,
                ingredient_id,
                quantity,
                gi,
                protein,
                carbohydrates,
                sugars_of_carbs,
                fibre,
                fat,
                saturated_fat,
                salt,
                water_content,
                calories

        FROM all_ingredients_in_sections_of_meal_calculation
        WHERE record_state = 'versioned';

    -- ###########################################
    -- Draft : Ingredients_In_Meal
    -- ###########################################
        CREATE VIEW draft_ingredients_in_sections_of_meal_calculation AS

        SELECT

            id  AS draft_ingredients_index,
            div_id AS draft_div_meal_sections_id,

            ingredient_type_id,
            ingredient_id,
            quantity,
            gi,
            protein,
            carbohydrates,
            sugars_of_carbs,
            fibre,
            fat,
            saturated_fat,
            salt,
            water_content,
            calories

        FROM all_ingredients_in_sections_of_meal_calculation
        WHERE record_state = 'draft';

    -- ###########################################
    -- Draft GUI : Ingredients_In_Meal
    -- ###########################################
        CREATE VIEW draft_gui_ingredients_in_sections_of_meal_calculation AS

        SELECT

            draft_ingredients_index,
            ingredient_type_id AS ingredient_type_name,
            ingredient_id AS ingredient_name,
            quantity,
            gi,
            protein,
            carbohydrates,
            sugars_of_carbs,
            fibre,
            fat,
            saturated_fat,
            salt,
            water_content,
            calories,
            'delete button' AS delete_button

        FROM draft_ingredients_in_sections_of_meal_calculation D;

-- ################################################################################
--
-- ################################################################################
    /*


    */
    -- ###########################################
    -- All Draft + Version | Sub-Meal Data
    -- ###########################################
        CREATE VIEW all_divided_meal_sections_calculations AS

            SELECT

                record_state,
                div_id,

                IFNULL(ROUND(SUM(protein),2),0)         AS total_protein,
                IFNULL(ROUND(SUM(carbohydrates),2),0)   AS total_carbohydrates,
                IFNULL(ROUND(SUM(sugars_of_carbs),2),0) AS total_sugars_of_carbs,
                IFNULL(ROUND(SUM(fibre),2),0)           AS total_fibre,
                IFNULL(ROUND(SUM(fat),2),0)             AS total_fats,
                IFNULL(ROUND(SUM(saturated_fat),2),0)   AS total_saturated_fat,
                IFNULL(ROUND(SUM(salt),2),0)            AS total_salt,
                IFNULL(ROUND(SUM(water_content),2),0)   AS total_water_content,
                IFNULL(ROUND(SUM(calories),2),0)        AS total_calories

            FROM all_ingredients_in_sections_of_meal_calculation
            GROUP BY record_state, div_id;

    -- ###########################################
    -- Versioned - Sub-Meals Calculation
    -- ###########################################
        CREATE VIEW versioned_divided_meal_sections_calculations AS

            SELECT

                div_id AS div_meal_sections_version_id,

                total_protein,
                total_carbohydrates,
                total_sugars_of_carbs,
                total_fibre,
                total_fats,
                total_saturated_fat,
                total_salt,
                total_water_content,
                total_calories

            FROM all_divided_meal_sections_calculations
            WHERE record_state = 'versioned';

    -- ###########################################
    -- Draft - Sub-Meals Calculation
    -- ###########################################
        CREATE VIEW draft_divided_meal_sections_calculations AS

            SELECT

                div_id AS  draft_div_meal_sections_id,

                total_protein,
                total_carbohydrates,
                total_sugars_of_carbs,
                total_fibre,
                total_fats,
                total_saturated_fat,
                total_salt,
                total_water_content,
                total_calories

            FROM all_divided_meal_sections_calculations
            WHERE record_state = 'draft';

-- ################################################################################
--
-- ################################################################################
    /*

    */
    -- ###################################
    -- All Total Meal | Draft + Versioned
    -- ###################################
        CREATE VIEW all_total_meal_view AS

            WITH
                I AS (         -- Per Meal : Ingredient Counts (Versioned & Drafted)
                        SELECT -- Ingredients Count Per Versioned Meal

                            'versioned' AS record_state,
                            D.meal_in_plan_version_id AS meal_id,
                            IFNULL(COUNT(DISTINCT I.ingredient_id),0) AS cnt

                        FROM divided_meal_sections_versions  D
                        LEFT JOIN ingredients_in_sections_of_meal I
                            ON D.div_meal_sections_version_id = I.div_meal_sections_version_id
                        GROUP BY D.meal_in_plan_version_id

                        UNION ALL

                        SELECT -- Ingredients Count Per Draft Meal

                            'draft' AS record_state,
                            D.draft_meal_in_plan_id AS meal_id,
                            IFNULL(COUNT(DISTINCT I.ingredient_id),0) AS cnt

                        FROM draft_divided_meal_sections  D
                        LEFT JOIN draft_ingredients_in_sections_of_meal I
                            ON D.draft_div_meal_sections_id = I.draft_div_meal_sections_id
                        GROUP BY D.draft_meal_in_plan_id
                ),
                M AS (      -- Per Sub-Meal & Meal Meta Info (Versioned & Draft)

                        SELECT
                            'versioned'                     AS record_state,
                            D.div_meal_sections_version_id  AS div_id,
                            D.meal_in_plan_version_id       AS meal_id,
                            M.plan_version_id               AS plan_id,
                            M.meal_name,
                            M.meal_time

                        FROM divided_meal_sections_versions D
                        LEFT JOIN meals_in_plan_versions M
                            ON D.meal_in_plan_version_id = M.meal_in_plan_version_id

                        UNION ALL

                        SELECT
                            'draft'                         AS record_state,
                            D.draft_div_meal_sections_id    AS div_id,
                            D.draft_meal_in_plan_id         AS meal_id,
                            M.plan_id                       AS plan_id,
                            M.meal_name,
                            M.meal_time

                        FROM draft_divided_meal_sections D
                        LEFT JOIN draft_meals_in_plan M
                            ON D.draft_meal_in_plan_id = M.draft_meal_in_plan_id
                )

            SELECT

                M.record_state,
                M.meal_id,

                MAX(M.plan_id)   AS plan_id,  -- Max's are used just to satisfy aggregate & strict mysql mode vs having them in the group by clause
                MAX(M.meal_time) AS meal_time, -- Max's are used just to satisfy aggregate & strict mysql mode vs having them in the group by clause
                MAX(M.meal_name) AS meal_name, -- Max's are used just to satisfy aggregate & strict mysql mode vs having them in the group by clause

                MAX(I.cnt) AS no_of_ingredients, -- Used to to satisfy SQL grouping rules / make the dependency explicit and SQL-portable although, it isn't needed MAX

                IFNULL(ROUND(SUM(DI.total_protein),2),0) AS total_protein,
                IFNULL(ROUND(SUM(DI.total_carbohydrates),2),0) AS total_carbohydrates,
                IFNULL(ROUND(SUM(DI.total_sugars_of_carbs),2),0) AS total_sugars_of_carbs,
                IFNULL(ROUND(SUM(DI.total_fibre),2),0) AS total_fibre,
                IFNULL(ROUND(SUM(DI.total_fats),2),0) AS total_fats,
                IFNULL(ROUND(SUM(DI.total_saturated_fat),2),0) AS total_saturated_fat,
                IFNULL(ROUND(SUM(DI.total_salt),2),0) AS total_salt,
                IFNULL(ROUND(SUM(DI.total_water_content),2),0) AS total_water,

                IFNULL(ROUND(SUM(DI.total_calories),2),0) AS total_calories

            FROM  M -- Meals INFO

            LEFT JOIN I -- Meals Counts
                ON M.record_state = I.record_state AND M.meal_id = I.meal_id

            LEFT JOIN all_divided_meal_sections_calculations DI
                ON M.record_state = DI.record_state AND M.div_id = DI.div_id

            GROUP BY M.record_state, M.meal_id;

    -- ###################################
    -- Versioned Total Meals
    -- ###################################
        CREATE VIEW versioned_total_meal_view AS

            SELECT

                meal_id AS meal_in_plan_version_id,
                plan_id AS plan_version_id,
                meal_time,
                meal_name,
                no_of_ingredients,
                total_protein,
                total_carbohydrates,
                total_sugars_of_carbs,
                total_fibre,
                total_fats,
                total_saturated_fat,
                total_salt,
                total_water,
                total_calories

            FROM all_total_meal_view
            WHERE record_state = 'versioned';

    -- ###################################
    -- Draft Total Meals
    -- ###################################
        CREATE VIEW draft_total_meal_view AS

            SELECT

                meal_id AS draft_meal_in_plan_id,
                plan_id,
                meal_time,
                meal_name,
                no_of_ingredients,
                total_protein,
                total_carbohydrates,
                total_sugars_of_carbs,
                total_fibre,
                total_fats,
                total_saturated_fat,
                total_salt,
                total_water,
                total_calories

            FROM all_total_meal_view
            WHERE record_state = 'draft';

    -- ###################################
    -- Draft GUI Total Meals
    -- ###################################
        CREATE VIEW draft_gui_total_meal_view AS

            SELECT

                meal_id AS draft_meal_in_plan_id,
                meal_time,
                meal_name,
                no_of_ingredients,
                total_protein,
                total_carbohydrates,
                total_sugars_of_carbs,
                total_fibre,
                total_fats,
                total_saturated_fat,
                total_salt,
                total_water,
                total_calories

            FROM all_total_meal_view
            WHERE record_state = 'draft';

-- ################################################################################
--
-- ################################################################################
    /*


    */

    -- #############################################
    -- ALL Total_Plan_View | Draft + Versioned Plans
    -- #############################################
        CREATE VIEW all_total_plan_view AS

            WITH
                P1 AS ( -- Get Plans From Draft & Versioned
                        SELECT

                           'versioned' AS record_state,
                            PV.date_time_last_edited,
                            PV.plan_version_id AS plan_id,
                            PV.version_number  AS version_number

                        FROM plan_versions PV

                        UNION ALL

                        SELECT

                           'draft' AS record_state,
                            P.date_time_last_edited,
                            P.plan_id AS plan_id,
                            NULL AS version_number

                        FROM draft_plans P
                ),
                P2 AS ( -- Plans MetaData For Draft + Versioned Plans

                       SELECT
                           P1.record_state,
                           P1.plan_id,
                           P.plan_name
                       FROM P1
                       LEFT JOIN plans P
                            ON P1.plan_id = P.plan_id
                )

            SELECT

                P1.record_state,
                P1.plan_id,

                MAX(P1.version_number) AS version_number, -- Max's are used just to satisfy aggregate & strict mysql mode vs having them in the group by clause
                MAX(P2.plan_name) AS plan_name, -- Max's are used just to satisfy aggregate & strict mysql mode vs having them in the group by clause
                MAX(P1.date_time_last_edited) AS date_time_last_edited, -- Max's are used just to satisfy aggregate & strict mysql mode vs having them in the group by clause

                COUNT(T.meal_id) AS no_of_meals, -- always returns 0 or greater || This could be its own CTE if the DB scales Higher
                                                 -- COUNT(DISTINCT T.meal_id) was recommended, I don't like it / hides errors as there shouldn't be identical duplicates

                IFNULL(ROUND(SUM(T.total_protein),2),0) AS protein_in_plan,
                IFNULL(ROUND(SUM(T.total_carbohydrates),2),0) AS carbohydrates_in_Plan,
                IFNULL(ROUND(SUM(T.total_sugars_of_carbs),2),0) AS sugars_of_carbs_in_plan,
                IFNULL(ROUND(SUM(T.total_fibre),2),0) AS fibre_in_plan,
                IFNULL(ROUND(SUM(T.total_fats),2),0) AS fats_in_plan,
                IFNULL(ROUND(SUM(T.total_saturated_fat),2),0) AS saturated_fat_in_plan,
                IFNULL(ROUND(SUM(T.total_salt),2),0) AS salt_in_plan,
                IFNULL(ROUND(SUM(T.total_water),2),0) AS water_content_in_plan,

                IFNULL(ROUND(SUM(T.total_calories),2),0) AS total_calories_in_plan

            FROM P1

            LEFT JOIN P2
                ON P1.record_state = P2.record_state AND P1.plan_id = P2.plan_id

            LEFT JOIN all_total_meal_view T
                 ON P1.record_state = T.record_state AND P1.plan_id = T.plan_id

            GROUP BY P1.record_state, P1.plan_id;


        -- #############################################
        -- Versioned Total_Plan_View
        -- #############################################
            CREATE VIEW versioned_total_plan_view AS
            SELECT

                plan_id AS plan_version_id,
                plan_name,
                version_number,
                date_time_last_edited,
                no_of_meals,
                protein_in_plan,
                carbohydrates_in_Plan,
                sugars_of_carbs_in_plan,
                fibre_in_plan,
                fats_in_plan,
                saturated_fat_in_plan,
                salt_in_plan,
                water_content_in_plan,
                total_calories_in_plan

            FROM all_total_plan_view
            WHERE record_state = 'versioned';

        -- #############################################
        -- Draft Total_Plan_View
        -- #############################################
            CREATE VIEW draft_total_plan_view AS
            SELECT

                plan_id,
                plan_name,
                date_time_last_edited,
                no_of_meals,
                protein_in_plan,
                carbohydrates_in_Plan,
                sugars_of_carbs_in_plan,
                fibre_in_plan,
                fats_in_plan,
                saturated_fat_in_plan,
                salt_in_plan,
                water_content_in_plan,
                total_calories_in_plan

            FROM all_total_plan_view
            WHERE record_state = 'draft';


-- ################################################################################
--
-- ################################################################################
    /*


    */

    -- #########################################
    -- ALL Plan Macros Left : Versioned + Draft
    -- #########################################
        CREATE VIEW all_plan_macros_left AS

            SELECT

                P.record_state,
                P.plan_id,
                P.plan_name,
                P.version_number,

                IFNULL(ROUND(C.expected_protein_grams - P.protein_in_plan ,2),0) AS protein_grams_left,
                IFNULL(ROUND(C.expected_carbs_grams  - P.carbohydrates_in_Plan ,2),0) AS carb_grams_left,
                IFNULL(ROUND(C.expected_fibre_grams  - P.fibre_in_plan ,2),0) AS fibre_grams_left,
                IFNULL(ROUND(C.expected_fats_grams - P.fats_in_plan ,2),0) AS fat_grams_left,
                IFNULL(ROUND(C.saturated_fat_limit - P.saturated_fat_in_plan ,2),0) AS potential_sat_fat_grams_left,
                IFNULL(ROUND(C.salt_limit_grams - P.salt_in_plan ,2),0) AS potential_salt_grams_left,
                IFNULL(ROUND(C.water_content_target - P.water_content_in_plan ,2),0) AS  water_left_to_drink,

                IFNULL(ROUND(C.calories_target - P.total_calories_in_plan ,2),0) AS calories_left,
                IFNULL(ROUND(C.additional_calories_target - P.total_calories_in_plan ,2),0) AS added_calories_left

            FROM all_total_plan_view P

            LEFT JOIN all_plan_macro_target_calculations C
                ON P.record_state = C.record_state AND P.plan_id = C.plan_id;

    -- #########################################
    -- Versioned Macros Left
    -- #########################################
        CREATE VIEW versioned_plan_macros_left AS

            SELECT

                P.plan_id AS plan_version_id,
                P.plan_name,
                P.version_number,

                protein_grams_left,
                carb_grams_left,
                fibre_grams_left,
                fat_grams_left,
                potential_sat_fat_grams_left,
                potential_salt_grams_left,
                water_left_to_drink,
                calories_left,
                added_calories_left

            FROM all_plan_macros_left P
            WHERE record_state = 'versioned';

    -- #########################################
    -- Draft Macros Left
    -- #########################################
        CREATE VIEW draft_plan_macros_left AS

        SELECT

          P.plan_id ,
          P.plan_name,

          protein_grams_left,
          carb_grams_left,
          fibre_grams_left,
          fat_grams_left,
          potential_sat_fat_grams_left,
          potential_salt_grams_left,
          water_left_to_drink,
          calories_left,
          added_calories_left

        FROM all_plan_macros_left P
        WHERE record_state = 'draft';

        -- #########################################
        -- Draft GUI Macros Left
        -- #########################################
            CREATE VIEW draft_gui_plan_macros_left AS

            SELECT

              P.plan_id,

              protein_grams_left,
              carb_grams_left,
              fibre_grams_left,
              fat_grams_left,
              potential_sat_fat_grams_left,
              potential_salt_grams_left,
              water_left_to_drink,
              calories_left,
              added_calories_left

            FROM all_plan_macros_left P
            WHERE record_state = 'draft';