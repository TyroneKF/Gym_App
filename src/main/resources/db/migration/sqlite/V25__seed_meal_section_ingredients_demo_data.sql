
-- #####################################################################################################################
-- Step 1.) Insert Into ingredients_in_sections_of_meal
-- ######################################################################################################################
    /*


    */

-- #################################################################################
-- # Breakfast - Part 1: Pancakes
-- #################################################################################

    WITH
        -- #################################
        -- Breakfast Sub-Meals
        -- #################################
           pancakes_breakfast_div_vs_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'pancakes_breakfast_div_vs_id'),

        -- #################################
         -- Ingredients
         -- #################################
            alpro_soya_light_long_life_drink_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'alpro_soya_light_long_life_drink_ingredient_id'),
            be_ro_plain_flour_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'be_ro_plain_flour_ingredient_id'),
            billingtons_brown_sugar_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'billingtons_brown_sugar_ingredient_id'),
            fage_greek_yoghurt_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'fage_greek_yoghurt_ingredient_id'),
            large_eggs_60g_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'large_eggs_60g_ingredient_id')

    -- #####################################################
    -- Insert Statements
    -- #####################################################
        INSERT INTO ingredients_in_sections_of_meal
        (
            div_meal_sections_version_id,
            ingredient_id,
            quantity
        )
        SELECT
        
            (SELECT entity_id_value FROM pancakes_breakfast_div_vs_id),
            v.ingredient_id,
            v.quantity
            
        FROM
        (
            SELECT  (SELECT entity_id_value FROM large_eggs_60g_ingredient_id) AS  ingredient_id,   1    AS quantity  UNION ALL  -- EGGS
            SELECT  (SELECT entity_id_value FROM be_ro_plain_flour_ingredient_id),                  110               UNION ALL  -- Flour
            SELECT  (SELECT entity_id_value FROM billingtons_brown_sugar_ingredient_id),            28                UNION ALL  -- Sugar
            SELECT  (SELECT entity_id_value FROM alpro_soya_light_long_life_drink_ingredient_id),   140               UNION ALL  -- Milk
            SELECT  (SELECT entity_id_value FROM fage_greek_yoghurt_ingredient_id),                 110                          -- Yoghurt
        ) v;;
-- #################################################################################
-- # Breakfast - Part 2: (Eggs + Plantain)
-- #################################################################################

    WITH
        -- #################################
        -- Breakfast Sub-Meals
        -- #################################
           eggs_breakfast_div_vs_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'eggs_breakfast_div_vs_id'),

        -- #################################
         -- Ingredients
         -- #################################
            alpro_soya_light_long_life_drink_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'alpro_soya_light_long_life_drink_ingredient_id'),
            large_eggs_60g_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'large_eggs_60g_ingredient_id'),
            plantin_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'plantin_ingredient_id')


    -- #####################################################
    -- Insert Statements
    -- #####################################################
        INSERT INTO ingredients_in_sections_of_meal
        (
            div_meal_sections_version_id,
            ingredient_id,
            quantity
        )
        SELECT

            (SELECT entity_id_value FROM eggs_breakfast_div_vs_id),
            v.ingredient_id,
            v.quantity

        FROM
        (
            SELECT  (SELECT entity_id_value FROM large_eggs_60g_ingredient_id) AS ingredient_id,   3 AS quantity  UNION ALL   -- EGGS
            SELECT  (SELECT entity_id_value FROM alpro_soya_light_long_life_drink_ingredient_id), 110             UNION ALL   -- Milk
            SELECT  (SELECT entity_id_value FROM plantin_ingredient_id),                          28                          -- Plantain
        ) v;
        

-- #################################################################################
-- # Breakfast - Part 3:  (Celery Drink)
-- #################################################################################

    WITH
        -- #################################
        -- Breakfast Sub-Meals
        -- #################################
           celery_breakfast_div_vs_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'celery_breakfast_div_vs_id'),

        -- #################################
        -- Ingredients
        -- #################################
           celery_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'celery_ingredient_id'),
           water_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'water_ingredient_id')

    -- #####################################################
    -- Insert Statements
    -- #####################################################
        INSERT INTO ingredients_in_sections_of_meal
        (
            div_meal_sections_version_id,
            ingredient_id,
            quantity
        )
        SELECT

            (SELECT entity_id_value FROM celery_breakfast_div_vs_id),
            v.ingredient_id,
            v.quantity

        FROM         
        (
            SELECT (SELECT entity_id_value FROM celery_ingredient_id) AS ingredient_id,  110 AS quantity  UNION ALL   -- Celery
            SELECT (SELECT entity_id_value FROM water_ingredient_id),                    500                          -- Water
        ) v;
        

-- #################################################################################
-- # Mid-Morning - Part 1: Oatmeal
-- #################################################################################

    WITH
        -- #################################
        -- Breakfast Sub-Meals
        -- #################################
           oatmeal_mid_morning_div_vs_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'oatmeal_mid_morning_div_vs_id'),

        -- #################################
        -- Ingredients
        -- #################################
           alpro_soya_light_long_life_drink_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'alpro_soya_light_long_life_drink_ingredient_id'),
           asda_scottish_porridge_oats_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'asda_scottish_porridge_oats_ingredient_id'),
           rowse_organic_honey_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'rowse_organic_honey_ingredient_id')

    -- #####################################################
    -- Insert Statements
    -- #####################################################
        INSERT INTO ingredients_in_sections_of_meal
        (
            div_meal_sections_version_id,
            ingredient_id,
            quantity
        )

        SELECT

            (SELECT entity_id_value FROM oatmeal_mid_morning_div_vs_id),
            v.ingredient_id,
            v.quantity

        FROM 
        
        (
            SELECT (SELECT entity_id_value FROM alpro_soya_light_long_life_drink_ingredient_id) AS ingredient_id,  110 AS quantity  UNION ALL   -- Oatmeal
             -- ((SELECT entity_id_value FROM oatmeal_mid_morning_div_vs_id), , 35), -- Nut Powder
            SELECT (SELECT entity_id_value FROM asda_scottish_porridge_oats_ingredient_id),                        500              UNION ALL   -- Milk
            SELECT (SELECT entity_id_value FROM rowse_organic_honey_ingredient_id),                                500                          -- Honey
        ) v;
        

-- #################################################################################
-- # Mid-Morning - Part 2: Bananas
-- #################################################################################

    WITH
        -- Breakfast Sub-Meals
           bananas_mid_morning_div_vs_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'bananas_mid_morning_div_vs_id'),

        -- Ingredients
           banana_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'banana_ingredient_id')

    -- #####################################################
    -- Insert Statements
    -- #####################################################
        INSERT INTO ingredients_in_sections_of_meal
        (
            div_meal_sections_version_id,
            ingredient_id,
            quantity
        )
        VALUES
        (
            (SELECT entity_id_value FROM bananas_mid_morning_div_vs_id),
            (SELECT entity_id_value FROM banana_ingredient_id),
            200
        );

-- #################################################################################
-- # Mid-Morning - Part 3 :  Mango & Pineapple Smoothie
-- #################################################################################

    WITH
        -- #################################
        -- Breakfast Sub-Meals
        -- #################################
           mango_smoothie_mid_morning_div_vs_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'mango_smoothie_mid_morning_div_vs_id'),

        -- #################################
        -- Ingredients
        -- #################################
           frozen_mangoes_asda_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'frozen_mangoes_asda_ingredient_id'),
           frozen_pineapple_asda_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'frozen_pineapple_asda_ingredient_id'),
           water_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'water_ingredient_id')

    -- #####################################################
    -- Insert Statements
    -- #####################################################
        INSERT INTO ingredients_in_sections_of_meal
        (
            div_meal_sections_version_id,
            ingredient_id,
            quantity
        )
        SELECT

            (SELECT entity_id_value FROM mango_smoothie_mid_morning_div_vs_id),
            v.ingredient_id,
            v.quantity

        FROM 
        (
            SELECT  (SELECT entity_id_value FROM frozen_mangoes_asda_ingredient_id) AS ingredient_id,  110  AS quantity  UNION ALL   -- Mango
            SELECT  (SELECT entity_id_value FROM frozen_pineapple_asda_ingredient_id),                 110               UNION ALL   -- Pineapple
            SELECT  (SELECT entity_id_value FROM water_ingredient_id),                                 500                           -- Water
        ) v;
        

-- #################################################################################
-- # Lunch - Part 1 : Lunch Meal
-- #################################################################################

    WITH
        -- #################################
        -- Breakfast Sub-Meals
        -- #################################
           lunch_meal_lunch_div_vs_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'lunch_meal_lunch_div_vs_id'),

        -- #################################
        -- Ingredients
        -- #################################
           wholemeal_wraps_tesco_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'wholemeal_wraps_tesco_ingredient_id'),
           chicken_mince_asda_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'chicken_mince_asda_ingredient_id')

    -- #####################################################
    -- Insert Statements
    -- #####################################################
        INSERT INTO ingredients_in_sections_of_meal
        (
            div_meal_sections_version_id,
            ingredient_id,
            quantity
        )
        SELECT

            (SELECT entity_id_value FROM lunch_meal_lunch_div_vs_id),
            v.ingredient_id,
            v.quantity

        FROM 
        (
            SELECT (SELECT entity_id_value FROM wholemeal_wraps_tesco_ingredient_id) AS ingredient_id,  2   AS quantity  UNION ALL   -- Wraps
            SELECT (SELECT entity_id_value FROM chicken_mince_asda_ingredient_id),                     150                           -- Chicken Mince
             -- ((SELECT entity_id_value FROM lunch_meal_lunch_div_vs_id), , 60), -- BBQ Sauce
        ) v;
        


-- #################################################################################
-- # Lunch - Part 2 : Nuts Part
-- #################################################################################

    WITH
        -- #################################
        -- Breakfast Sub-Meals
        -- #################################
           nuts_lunch_div_vs_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'nuts_lunch_div_vs_id'),

        -- #################################
        -- Ingredients
        -- #################################
           pistachios_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'pistachios_ingredient_id'),
           pumpkin_seeds_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'pumpkin_seeds_ingredient_id'),
           sunflower_seeds_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'sunflower_seeds_ingredient_id')


    -- #####################################################
    -- Insert Statements
    -- #####################################################
        INSERT INTO ingredients_in_sections_of_meal
        (
            div_meal_sections_version_id,
            ingredient_id,
            quantity
        )
        SELECT

            (SELECT entity_id_value FROM nuts_lunch_div_vs_id),
            v.ingredient_id,
            v.quantity

        FROM
        (
            SELECT  (SELECT entity_id_value FROM sunflower_seeds_ingredient_id) AS ingredient_id,      7    AS quantity  UNION ALL   -- Sunflower Seeds
            SELECT  (SELECT entity_id_value FROM pumpkin_seeds_ingredient_id),                         7                 UNION ALL   -- Pumpkin Seeds
            SELECT  (SELECT entity_id_value FROM pistachios_ingredient_id),                            12                            -- Pistachios
        ) v;
        

-- #################################################################################
-- # Lunch - Part 3 : Multivitamin Shake
-- #################################################################################

    WITH
        -- #################################
        -- Breakfast Sub-Meals
        -- #################################
           multivitamin_shake_lunch_div_vs_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'multivitamin_shake_lunch_div_vs_id'),

        -- #################################
        -- Ingredients
        -- #################################
           maca_vivolife_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'maca_vivolife_ingredient_id'),
           thrive_vivolife_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'thrive_vivolife_ingredient_id'),
           water_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'water_ingredient_id')

    -- #####################################################
    -- Insert Statements
    -- #####################################################
        INSERT INTO ingredients_in_sections_of_meal
        (
            div_meal_sections_version_id,
            ingredient_id,
            quantity
        )
        SELECT

            (SELECT entity_id_value FROM multivitamin_shake_lunch_div_vs_id),
            v.ingredient_id,
            v.quantity

        FROM
        (
            SELECT  (SELECT entity_id_value FROM maca_vivolife_ingredient_id) AS ingredient_id,        5    AS  quantity  UNION ALL   -- Maca
            SELECT  (SELECT entity_id_value FROM thrive_vivolife_ingredient_id),                       3                  UNION ALL   -- Vivo Life
            SELECT  (SELECT entity_id_value FROM water_ingredient_id),                                 500                            -- Water
        ) v;
        

-- #################################################################################
-- # Pre-Workout - Part 1:  Fruit Smoothie Part
-- #################################################################################

    WITH
        -- #################################
        -- Breakfast Sub-Meals
        -- #################################
           nuts_lunch_div_vs_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'nuts_lunch_div_vs_id'),

        -- #################################
        -- Ingredients
        -- #################################
           pistachios_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'pistachios_ingredient_id'),
           pumpkin_seeds_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'pumpkin_seeds_ingredient_id'),
           sunflower_seeds_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'sunflower_seeds_ingredient_id')


    -- #####################################################
    -- Insert Statements
    -- #####################################################
        INSERT INTO ingredients_in_sections_of_meal
        (
            div_meal_sections_version_id,
            ingredient_id,
            quantity
        )
        SELECT
            
            (SELECT entity_id_value FROM nuts_lunch_div_vs_id),
            v.ingredient_id,
            v.quantity

        FROM
        (
            SELECT  (SELECT entity_id_value FROM sunflower_seeds_ingredient_id) AS ingredient_id,      7    AS quantity  UNION ALL   -- Sunflower Seeds
            SELECT  (SELECT entity_id_value FROM pumpkin_seeds_ingredient_id),                         7                 UNION ALL   -- Pumpkin Seeds
            SELECT  (SELECT entity_id_value FROM pistachios_ingredient_id),                            12                            -- Pistachios
        ) v;
        

-- #################################################################################
-- # Post-Workout - Part 1 : Bananas
-- #################################################################################

     WITH
            -- Breakfast Sub-Meal
              bananas_post_workout_div_vs_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'bananas_post_workout_div_vs_id'),

            -- Ingredient
               banana_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'banana_ingredient_id')

        -- #####################################################
        -- Insert Statements
        -- #####################################################
            INSERT INTO ingredients_in_sections_of_meal
            (
                div_meal_sections_version_id,
                ingredient_id,
                quantity
            )
            VALUES  -- Adding Banana
            (
                (SELECT entity_id_value FROM bananas_post_workout_div_vs_id),
                (SELECT entity_id_value FROM banana_ingredient_id),
                200
            );

-- #################################################################################
-- # Post-Workout- Part 2 : Protein Shake
-- #################################################################################

    WITH
        -- #################################
        -- Breakfast Sub-Meals
        -- #################################
           nuts_lunch_div_vs_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'nuts_lunch_div_vs_id'),

        -- #################################
        -- Ingredients
        -- #################################
           alpro_soya_light_long_life_drink_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'alpro_soya_light_long_life_drink_ingredient_id'),
           creatine_monohydrate_myprotein_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'creatine_monohydrate_myprotein_ingredient_id'),
           whey_isolate_natural_strawberry_myprotein_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'whey_isolate_natural_strawberry_myprotein_ingredient_id')

    -- #####################################################
    -- Insert Statements
    -- #####################################################
        INSERT INTO ingredients_in_sections_of_meal
        (
            div_meal_sections_version_id,
            ingredient_id,
            quantity
        )
        SELECT

            (SELECT entity_id_value FROM nuts_lunch_div_vs_id),
            v.ingredient_id,
            v.quantity

        FROM 
        (
            SELECT  (SELECT entity_id_value FROM whey_isolate_natural_strawberry_myprotein_ingredient_id) AS ingredient_id,  35    AS quantity  UNION ALL   -- Protein Powder
            SELECT  (SELECT entity_id_value FROM creatine_monohydrate_myprotein_ingredient_id),                              5                  UNION ALL   -- Creatine
            SELECT  (SELECT entity_id_value FROM alpro_soya_light_long_life_drink_ingredient_id),                            450                            -- Milk
        ) v;
        

-- #################################################################################
-- # Post-Workout - Part 3 : Veg Meal
-- #################################################################################

    WITH
        -- #################################
        -- Breakfast Sub-Meals
        -- #################################
           veg_meal_post_workout_div_vs_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'veg_meal_post_workout_div_vs_id'),

        -- #################################
        -- Ingredients
        -- #################################
           broccoli_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'broccoli_ingredient_id'),
           carrots_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'carrots_ingredient_id'),
           sweet_potato_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'sweet_potato_ingredient_id'),
           kale_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'kale_ingredient_id')

    -- #####################################################
    -- Insert Statements
    -- #####################################################
        INSERT INTO ingredients_in_sections_of_meal
        (
            div_meal_sections_version_id,
            ingredient_id,
            quantity
        )
        SELECT
            
            (SELECT entity_id_value FROM veg_meal_post_workout_div_vs_id),
            v.ingredient_id,
            v.quantity

        FROM
        (
            SELECT  (SELECT entity_id_value FROM broccoli_ingredient_id) AS ingredient_id,  110  AS  quantity  UNION ALL   -- Broccoli
            SELECT  (SELECT entity_id_value FROM carrots_ingredient_id),                    110                UNION ALL   -- Carrots
            SELECT  (SELECT entity_id_value FROM carrots_ingredient_id),                    110                UNION ALL   -- Sweet Potato
            SELECT  (SELECT entity_id_value FROM kale_ingredient_id),                       110                            -- Kale
        ) v;
        

-- #################################################################################
-- # Dinner - Part 1 : Pasta Meal
-- #################################################################################

    WITH
        -- #################################
        -- Breakfast Sub-Meals
        -- #################################
           dinner_div_vs_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'dinner_div_vs_id'),

        -- #################################
        -- Ingredients
        -- #################################
           dolmio_bolognese_pasta_sauce_low_fat_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'dolmio_bolognese_pasta_sauce_low_fat_ingredient_id'),
           lean_turkey_mince_asda_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'lean_turkey_mince_asda_ingredient_id'),
           sweetcorn_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'sweetcorn_ingredient_id'),
           wholewheat_spaghetti_asda_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'wholewheat_spaghetti_asda_ingredient_id')

    -- #####################################################
    -- Insert Statements
    -- #####################################################
        INSERT INTO ingredients_in_sections_of_meal
        (
            div_meal_sections_version_id,
            ingredient_id,
            quantity
        )
        SELECT

            (SELECT entity_id_value FROM dinner_div_vs_id),
            v.ingredient_id,
            v.quantity

        FROM
        (
            SELECT (SELECT entity_id_value FROM wholewheat_spaghetti_asda_ingredient_id)    AS ingredient_id,  75   AS  quantity   UNION ALL  -- Pasta
            SELECT (SELECT entity_id_value FROM lean_turkey_mince_asda_ingredient_id),                         150                 UNION ALL  -- Turkey Mince
            SELECT (SELECT entity_id_value FROM sweetcorn_ingredient_id),                                      60                  UNION ALL  -- Sweetcorn
            SELECT (SELECT entity_id_value FROM dolmio_bolognese_pasta_sauce_low_fat_ingredient_id),           60                             -- Dolmio Sauce
        ) v;
        

-- #################################################################################
-- # Bed Snack - Part 1 : Protein Shake
-- #################################################################################

    WITH
        -- #################################
        -- Breakfast Sub-Meals
        -- #################################
           protein_shake_bedtime_vs_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'protein_shake_bedtime_vs_id'),

        -- #################################
        -- Ingredients
        -- #################################
           alpro_organic_soya_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'alpro_organic_soya_ingredient_id'),
           slow_release_casein_vanilla_myprotein_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'slow_release_casein_vanilla_myprotein_ingredient_id')

    -- #####################################################
    -- Insert Statements
    -- #####################################################
        INSERT INTO ingredients_in_sections_of_meal
        (
            div_meal_sections_version_id,
            ingredient_id,
            quantity
        )
        SELECT

            (SELECT entity_id_value FROM protein_shake_bedtime_vs_id),
            v.ingredient_id,
            v.quantity

        FROM
        (
            SELECT  (SELECT entity_id_value FROM slow_release_casein_vanilla_myprotein_ingredient_id)  AS ingredient_id,  35    AS  quantity   UNION ALL  -- Casein
            SELECT  (SELECT entity_id_value FROM alpro_organic_soya_ingredient_id),                                       450                             -- Milk
        ) v;
        
