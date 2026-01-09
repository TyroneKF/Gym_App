
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
                        plan_id AS source_id,

                        NULL AS date_time_of_creation,

                        user_id,
                        current_weight_kg,
                        current_weight_in_pound,
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
                        plan_version_id AS source_id,

                        date_time_of_creation,

                        user_id,
                        current_weight_kg,
                        current_weight_in_pound,
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
                        source_id,

                        ROUND(current_weight_in_pound * protein_per_pound, 2) AS t_protein, -- returns null if 1 of the values are empty
                        ROUND(current_weight_in_pound * carbohydrates_per_pound, 2) AS t_carbs,
                        ROUND(current_weight_in_pound * fats_per_pound, 2) AS t_fats
                    FROM C1
                ),
                C3 AS ( -- Base Calories Calculations

                      SELECT
                          source_id,
                          record_state,

                          ROUND((t_protein * 4) + (t_carbs * 4) + (t_fats * 9) ,2) AS calories_target
                      FROM C2
                )

            SELECT
                C1.record_state,
                C1.source_id,
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
                ON C1.record_state = C2.record_state AND C1.source_id = C2.source_id

            LEFT JOIN C3
                ON C2.record_state = C3.record_state AND C2.source_id = C3.source_id;

    -- ############################################
    -- Draft : View For Macro Calculations
    -- ############################################
       CREATE VIEW draft_plan_macro_target_calculations AS
       SELECT
            source_id AS plan_id,

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
            source_id AS plan_version_id,
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
    --
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

                IFNULL(Info.glycemic_index, 0)                                                    AS gi,
                IFNULL(ROUND((Info.protein / Info.based_on_quantity) * D.quantity,2),0)           AS protein,
                IFNULL(ROUND((Info.carbohydrates / Info.based_on_quantity) * D.quantity,2),0)     AS carbohydrates,
                IFNULL(ROUND((Info.sugars_of_carbs / Info.based_on_quantity) * D.quantity,2),0)   AS sugars_of_carbs,
                IFNULL(ROUND((Info.fibre / Info.based_on_quantity) * D.quantity,2),0)             AS fibre,
                IFNULL(ROUND((Info.fat / Info.based_on_quantity) * D.quantity,2),0)               AS fat,
                IFNULL(ROUND((Info.saturated_fat / Info.based_on_quantity) * D.quantity,2),0)     AS saturated_fat,
                IFNULL(ROUND((Info.salt / Info.based_on_quantity) * D.quantity,2),0)              AS salt,
                IFNULL(ROUND((Info.water_content / Info.based_on_quantity) * D.quantity,2),0)     AS water_content,
                IFNULL(ROUND((Info.calories / Info.based_on_quantity) * D.quantity,2),0)          AS calories

            FROM ingredients_in_sections_of_meal D

            LEFT JOIN ingredients_info Info
                ON Info.ingredient_id = I.ingredient_id;

    -- ###########################################
    -- Versioned : Ingredients_In_Meal
    -- ###########################################
        CREATE VIEW versioned_ingredients_in_sections_of_meal_calculation AS

            SELECT

                id  AS ingredients_index,
                div_id AS div_meal_sections_version_id,

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
            calories

        FROM all_ingredients_in_sections_of_meal_calculation
        WHERE record_state = 'draft';

    -- ###########################################
    -- Draft GUI : Ingredients_In_Meal
    -- ###########################################
        CREATE VIEW draft_gui_ingredients_in_sections_of_meal_calculation_gui AS

        SELECT
            D.*,
            'Delete Row' AS `delete button`

        FROM draft_ingredients_in_sections_of_meal_calculation D;

-- ################################################################################
--
-- ################################################################################
    /*


    */

    -- ###########################################
    --
    -- ###########################################
    CREATE VIEW divided_meal_sections_calculations AS

        WITH A AS (

        )

        SELECT

            div_meal_sections_version_id,

            IFNULL(ROUND(SUM(protein),2),0) as total_protein,
            IFNULL(ROUND(SUM(carbohydrates),2),0) as total_carbohydrates,
            IFNULL(ROUND(SUM(sugars_of_carbs),2),0) as total_sugars_of_carbs,
            IFNULL(ROUND(SUM(fibre),2),0) as total_fibre,
            IFNULL(ROUND(SUM(fat),2),0) as total_fats,
            IFNULL(ROUND(SUM(saturated_fat),2),0) as total_saturated_fat,
            IFNULL(ROUND(SUM(salt),2),0) as total_salt,
            IFNULL(ROUND(SUM(water_content),2),0) as total_water_content,
            IFNULL(ROUND(SUM(calories),2),0) as total_calories

        FROM ingredients_in_sections_of_meal_calculation
        GROUP BY div_meal_sections_version_id;


    -- ###########################################
    --
    -- ###########################################
        CREATE VIEW divided_meal_sections_calculations AS

            SELECT

                div_meal_sections_version_id,

                IFNULL(ROUND(SUM(protein),2),0) as total_protein,
                IFNULL(ROUND(SUM(carbohydrates),2),0) as total_carbohydrates,
                IFNULL(ROUND(SUM(sugars_of_carbs),2),0) as total_sugars_of_carbs,
                IFNULL(ROUND(SUM(fibre),2),0) as total_fibre,
                IFNULL(ROUND(SUM(fat),2),0) as total_fats,
                IFNULL(ROUND(SUM(saturated_fat),2),0) as total_saturated_fat,
                IFNULL(ROUND(SUM(salt),2),0) as total_salt,
                IFNULL(ROUND(SUM(water_content),2),0) as total_water_content,
                IFNULL(ROUND(SUM(calories),2),0) as total_calories

            FROM ingredients_in_sections_of_meal_calculation
            GROUP BY div_meal_sections_version_id;

    -- ###########################################
    --
    -- ###########################################
        CREATE VIEW divided_meal_sections_calculations AS

            SELECT

                div_meal_sections_version_id,

                IFNULL(ROUND(SUM(protein),2),0) as total_protein,
                IFNULL(ROUND(SUM(carbohydrates),2),0) as total_carbohydrates,
                IFNULL(ROUND(SUM(sugars_of_carbs),2),0) as total_sugars_of_carbs,
                IFNULL(ROUND(SUM(fibre),2),0) as total_fibre,
                IFNULL(ROUND(SUM(fat),2),0) as total_fats,
                IFNULL(ROUND(SUM(saturated_fat),2),0) as total_saturated_fat,
                IFNULL(ROUND(SUM(salt),2),0) as total_salt,
                IFNULL(ROUND(SUM(water_content),2),0) as total_water_content,
                IFNULL(ROUND(SUM(calories),2),0) as total_calories

            FROM ingredients_in_sections_of_meal_calculation
            GROUP BY div_meal_sections_version_id;

-- ################################################################################
--
-- ################################################################################
    CREATE VIEW total_meal_view AS

        WITH
            I AS (
                    SELECT

                        D.meal_in_plan_version_id,
                        IFNULL(COUNT(DISTINCT ingredient_id),0) AS cnt

                    FROM divided_meal_sections_versions D

                    LEFT JOIN ingredients_in_sections_of_meal I
                        ON I.div_meal_sections_version_id = D.div_meal_sections_version_id

                    GROUP BY D.meal_in_plan_version_id
            ),

            DI AS (
                    SELECT

                        D.meal_in_plan_version_id,

                        IFNULL(ROUND(SUM(DI.total_protein),2),0) as total_protein,
                        IFNULL(ROUND(SUM(DI.total_carbohydrates),2),0) as total_carbohydrates,
                        IFNULL(ROUND(SUM(DI.total_sugars_of_carbs),2),0) as total_sugars_of_carbs,
                        IFNULL(ROUND(SUM(DI.total_fibre),2),0) as total_fibre,
                        IFNULL(ROUND(SUM(DI.total_fats),2),0) as total_fats,
                        IFNULL(ROUND(SUM(DI.total_saturated_fat),2),0) as total_saturated_fat,
                        IFNULL(ROUND(SUM(DI.total_salt),2),0) as total_salt,
                        IFNULL(ROUND(SUM(DI.total_water_content),2),0) as total_water_content,

                        IFNULL(ROUND(SUM(DI.total_calories),2),0) as total_calories

                    FROM divided_meal_sections_versions D

                    LEFT JOIN divided_meal_sections_calculations DI
                        ON DI.div_meal_sections_version_id = D.div_meal_sections_version_id

                    GROUP BY D.meal_in_plan_version_id
            )

        SELECT

            M.plan_version_id,
            M.meal_in_plan_version_id,

            M.meal_time,
            M.meal_name,

            I.cnt AS no_of_ingredients,

            DI.total_protein,
            DI.total_carbohydrates,
            DI.total_sugars_of_carbs,
            DI.total_fibre,
            DI.total_fats,
            DI.total_saturated_fat,
            DI.total_salt,
            DI.total_water_content AS total_water,
            DI.total_calories

        FROM meals_in_plan_versions M

        LEFT JOIN I ON
            I.meal_in_plan_version_id = M.meal_in_plan_version_id

        LEFT JOIN DI ON
            DI.meal_in_plan_version_id = M.meal_in_plan_version_id;

-- ################################################################################
--
-- ################################################################################
    CREATE VIEW total_plan_view AS

        SELECT

            PV.plan_version_id,
            P.plan_name, -- needs to be here to prevent ONLY_FULL_GROUP_BY

            COUNT(T.meal_in_plan_version_id) AS no_of_meals, -- always returns 0 or greater || This could be its own CTE if the DB scales Higher

            IFNULL(ROUND(SUM(T.total_protein),2),0) AS protein_in_plan,
            IFNULL(ROUND(SUM(T.total_carbohydrates),2),0) AS carbohydrates_in_Plan,
            IFNULL(ROUND(SUM(T.total_sugars_of_carbs),2),0) AS sugars_of_carbs_in_plan,
            IFNULL(ROUND(SUM(T.total_fibre),2),0) AS fibre_in_plan,
            IFNULL(ROUND(SUM(T.total_fats),2),0) AS fats_in_plan,
            IFNULL(ROUND(SUM(T.total_saturated_fat),2),0) AS saturated_fat_in_plan,
            IFNULL(ROUND(SUM(T.total_salt),2),0) AS salt_in_plan,
            IFNULL(ROUND(SUM(T.total_water),2),0) AS water_content_in_plan,

            IFNULL(ROUND(SUM(T.total_calories),2),0) AS total_calories_in_plan

        FROM plan_versions PV

        LEFT JOIN plans P
            ON P.plan_id = PV.plan_id

        LEFT JOIN total_meal_view T
            ON PV.plan_version_id = T.plan_version_id

        GROUP BY PV.plan_version_id, P.plan_name;


-- ################################################################################
--
-- ################################################################################
    CREATE VIEW plan_macros_left AS

        SELECT

            P.plan_version_id,
            P.plan_name,

            IFNULL(ROUND(C.expected_protein_grams - P.protein_in_plan ,2),0) AS protein_grams_left,
            IFNULL(ROUND(C.expected_carbs_grams  - P.carbohydrates_in_Plan ,2),0) AS carb_grams_left,
            IFNULL(ROUND(C.expected_fibre_grams  - P.fibre_in_plan ,2),0) AS fibre_grams_left,
            IFNULL(ROUND(C.expected_fats_grams - P.fats_in_plan ,2),0) AS fat_grams_left,
            IFNULL(ROUND(C.saturated_fat_limit - P.saturated_fat_in_plan ,2),0) AS potential_sat_fat_grams_left,
            IFNULL(ROUND(C.salt_limit_grams - P.salt_in_plan ,2),0) AS potential_salt_grams_left,
            IFNULL(ROUND(C.water_content_target - P.water_content_in_plan ,2),0) AS  water_left_to_drink,

            IFNULL(ROUND(C.calories_target - P.total_calories_in_plan ,2),0) AS calories_left,
            IFNULL(ROUND(C.additional_calories_target - P.total_calories_in_plan ,2),0) AS added_calories_left

        FROM total_plan_view P

        LEFT JOIN plan_macro_target_calculations C
            ON C.plan_version_id = P.plan_version_id;
