-- #################################################################################################
-- Insert Ingredients Into Meals
-- #################################################################################################
    WITH
        -- ########################################
        -- Sub-Meals
        -- ########################################

        -- Breakfast Sub-Meals
        pancakes_breakfast_div_vs_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'pancakes_breakfast_div_vs_id'),
        eggs_breakfast_div_vs_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'eggs_breakfast_div_vs_id'),
        celery_breakfast_div_vs_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'celery_breakfast_div_vs_id'),

        -- Mid-Morning Snack Sub-Meals
        oatmeal_mid_morning_div_vs_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'oatmeal_mid_morning_div_vs_id'),
        bananas_mid_morning_div_vs_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'bananas_mid_morning_div_vs_id'),
        mango_smoothie_mid_morning_div_vs_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'mango_smoothie_mid_morning_div_vs_id'),

        -- Lunch Sub-Meals
        lunch_meal_lunch_div_vs_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'lunch_meal_lunch_div_vs_id'),
        nuts_lunch_div_vs_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'nuts_lunch_div_vs_id'),
        multivitamin_shake_lunch_div_vs_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'multivitamin_shake_lunch_div_vs_id'),

        -- Pre-Workout Sub-Meals
        fruit_smoothie_pre_workout_div_vs_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'fruit_smoothie_pre_workout_div_vs_id'),

        -- Post-Workout Sub-Meals
        bananas_post_workout_div_vs_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'bananas_post_workout_div_vs_id'),
        protein_shake_post_workout_div_vs_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'protein_shake_post_workout_div_vs_id'),
        veg_meal_post_workout_div_vs_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'veg_meal_post_workout_div_vs_id'),

        -- Dinner Sub-Meals
        dinner_div_vs_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'dinner_div_vs_id'),

        -- Bed-time Sub-Meals
        protein_shake_bedtime_vs_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'protein_shake_bedtime_vs_id'),

        -- ########################################
        -- Ingredients
        -- ########################################
        alpro_organic_soya_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'alpro_organic_soya_ingredient_id'),
        alpro_soya_light_long_life_drink_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'alpro_soya_light_long_life_drink_ingredient_id'),
        asda_scottish_porridge_oats_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'asda_scottish_porridge_oats_ingredient_id'),
        banana_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'banana_ingredient_id'),
        be_ro_plain_flour_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'be_ro_plain_flour_ingredient_id'),
        billingtons_brown_sugar_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'billingtons_brown_sugar_ingredient_id'),
        broccoli_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'broccoli_ingredient_id'),
        carrots_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'carrots_ingredient_id'),
        celery_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'celery_ingredient_id'),
        chicken_mince_asda_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'chicken_mince_asda_ingredient_id'),
        creatine_monohydrate_myprotein_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'creatine_monohydrate_myprotein_ingredient_id'),
        dolmio_bolognese_pasta_sauce_low_fat_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'dolmio_bolognese_pasta_sauce_low_fat_ingredient_id'),
        fage_greek_yoghurt_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'fage_greek_yoghurt_ingredient_id'),
        frozen_blueberries_asda_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'frozen_blueberries_asda_ingredient_id'),
        frozen_mangoes_asda_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'frozen_mangoes_asda_ingredient_id'),
        frozen_pineapple_asda_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'frozen_pineapple_asda_ingredient_id'),
        frozen_raspberries_asda_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'frozen_raspberries_asda_ingredient_id'),
        frozen_strawberries_asda_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'frozen_strawberries_asda_ingredient_id'),
        grapefruit_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'grapefruit_ingredient_id'),
        kale_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'kale_ingredient_id'),
        large_eggs_60g_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'large_eggs_60g_ingredient_id'),
        lean_turkey_mince_asda_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'lean_turkey_mince_asda_ingredient_id'),
        maca_vivolife_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'maca_vivolife_ingredient_id'),
        oranges_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'oranges_ingredient_id'),
        pistachios_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'pistachios_ingredient_id'),
        plantin_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'plantin_ingredient_id'),
        pumpkin_seeds_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'pumpkin_seeds_ingredient_id'),
        rowse_organic_honey_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'rowse_organic_honey_ingredient_id'),
        slow_release_casein_vanilla_myprotein_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'slow_release_casein_vanilla_myprotein_ingredient_id'),
        sunflower_seeds_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'sunflower_seeds_ingredient_id'),
        sweet_potato_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'sweet_potato_ingredient_id'),
        sweetcorn_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'sweetcorn_ingredient_id'),
        thrive_vivolife_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'thrive_vivolife_ingredient_id'),
        water_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'water_ingredient_id'),
        whey_isolate_natural_strawberry_myprotein_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'whey_isolate_natural_strawberry_myprotein_ingredient_id'),
        wholemeal_wraps_tesco_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'wholemeal_wraps_tesco_ingredient_id'),
        wholewheat_spaghetti_asda_ingredient_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'wholewheat_spaghetti_asda_ingredient_id')

        -- ########################################
        -- Insert Statements
        -- ########################################
        INSERT INTO ingredients_in_sections_of_meal
        (
            div_meal_sections_version_id,
            ingredient_id,
            quantity
        )

        VALUES

        -- #####################################################
        -- # Breakfast
        -- #####################################################

        -- # Breakfast Part 1 (Pancakes)
        ((SELECT entity_id_value FROM pancakes_breakfast_div_vs_id), (SELECT entity_id_value FROM large_eggs_60g_ingredient_id), 3),  -- EGGS
        ((SELECT entity_id_value FROM pancakes_breakfast_div_vs_id), (SELECT entity_id_value FROM be_ro_plain_flour_ingredient_id), 110), -- Flour
        -- ((SELECT entity_id_value FROM pancakes_breakfast_div_vs_id), , 28), -- Butter
        ((SELECT entity_id_value FROM pancakes_breakfast_div_vs_id), (SELECT entity_id_value FROM billingtons_brown_sugar_ingredient_id), 28), -- Sugar
        ((SELECT entity_id_value FROM pancakes_breakfast_div_vs_id), (SELECT entity_id_value FROM alpro_soya_light_long_life_drink_ingredient_id), 140), -- Milk
        ((SELECT entity_id_value FROM pancakes_breakfast_div_vs_id), (SELECT entity_id_value FROM fage_greek_yoghurt_ingredient_id), 110), -- Yoghurt

        -- # Breakfast Part 2 (Eggs + Plantain)
        ((SELECT entity_id_value FROM eggs_breakfast_div_vs_id), (SELECT entity_id_value FROM large_eggs_60g_ingredient_id), 3), -- EGGS
        ((SELECT entity_id_value FROM eggs_breakfast_div_vs_id), (SELECT entity_id_value FROM alpro_soya_light_long_life_drink_ingredient_id), 40), -- Milk
        ((SELECT entity_id_value FROM eggs_breakfast_div_vs_id), (SELECT entity_id_value FROM plantin_ingredient_id), 120), -- Plantain

        -- # Breakfast Part 3 (Celery Drink)
        ((SELECT entity_id_value FROM celery_breakfast_div_vs_id), (SELECT entity_id_value FROM celery_ingredient_id), 110), -- Celery
        ((SELECT entity_id_value FROM celery_breakfast_div_vs_id), (SELECT entity_id_value FROM water_ingredient_id), 500), -- Water

        -- #####################################################
        -- # Mid-Morning
        -- #####################################################

        -- # Oatmeal Part 4
        ((SELECT entity_id_value FROM oatmeal_mid_morning_div_vs_id), (SELECT entity_id_value FROM asda_scottish_porridge_oats_ingredient_id), 110), -- Oatmeal
        -- ((SELECT entity_id_value FROM oatmeal_mid_morning_div_vs_id), , 35), -- Nut Powder
        ((SELECT entity_id_value FROM oatmeal_mid_morning_div_vs_id), (SELECT entity_id_value FROM alpro_soya_light_long_life_drink_ingredient_id), 450), -- Milk
        ((SELECT entity_id_value FROM oatmeal_mid_morning_div_vs_id), (SELECT entity_id_value FROM rowse_organic_honey_ingredient_id), 16), -- Honey

        -- # Bananas Part 5
        ((SELECT entity_id_value FROM bananas_mid_morning_div_vs_id), (SELECT entity_id_value FROM banana_ingredient_id), 200), -- Bananas

        -- # Mango & Pineapple Smoothie Part 6
        ((SELECT entity_id_value FROM mango_smoothie_mid_morning_div_vs_id), (SELECT entity_id_value FROM frozen_mangoes_asda_ingredient_id), 110), -- Mango
        ((SELECT entity_id_value FROM mango_smoothie_mid_morning_div_vs_id), (SELECT entity_id_value FROM frozen_pineapple_asda_ingredient_id), 110), -- Pineapple
        ((SELECT entity_id_value FROM mango_smoothie_mid_morning_div_vs_id), (SELECT entity_id_value FROM water_ingredient_id), 500), -- Water


        -- #####################################################
        -- # Lunch
        -- #####################################################

        -- # Lunch Part 7
        ((SELECT entity_id_value FROM lunch_meal_lunch_div_vs_id), (SELECT entity_id_value FROM wholemeal_wraps_tesco_ingredient_id), 2), -- Wraps
        ((SELECT entity_id_value FROM lunch_meal_lunch_div_vs_id), (SELECT entity_id_value FROM chicken_mince_asda_ingredient_id), 150), -- Chicken Mince
        -- ((SELECT entity_id_value FROM lunch_meal_lunch_div_vs_id), , 60), -- BBQ Sauce

        -- # Nuts Part 8
        ((SELECT entity_id_value FROM nuts_lunch_div_vs_id), (SELECT entity_id_value FROM sunflower_seeds_ingredient_id), 7), -- Sunflower Seeds
        ((SELECT entity_id_value FROM nuts_lunch_div_vs_id), (SELECT entity_id_value FROM pumpkin_seeds_ingredient_id), 7), -- Pumpkin Seeds
        ((SELECT entity_id_value FROM nuts_lunch_div_vs_id), (SELECT entity_id_value FROM pistachios_ingredient_id), 12), -- Pistachios

        -- # Multivitamin Shake Part 9
        ((SELECT entity_id_value FROM multivitamin_shake_lunch_div_vs_id), (SELECT entity_id_value FROM maca_vivolife_ingredient_id), 5), -- Maca
        ((SELECT entity_id_value FROM multivitamin_shake_lunch_div_vs_id), (SELECT entity_id_value FROM thrive_vivolife_ingredient_id), 3), -- Vivo Life
        ((SELECT entity_id_value FROM multivitamin_shake_lunch_div_vs_id), (SELECT entity_id_value FROM water_ingredient_id), 500), -- Water

        -- #####################################################
        -- # Pre-Workout
        -- #####################################################

        -- # Fruit Smoothie Part 10
        ((SELECT entity_id_value FROM fruit_smoothie_pre_workout_div_vs_id), (SELECT entity_id_value FROM frozen_raspberries_asda_ingredient_id), 60), -- Raspberry
        ((SELECT entity_id_value FROM fruit_smoothie_pre_workout_div_vs_id), (SELECT entity_id_value FROM frozen_blueberries_asda_ingredient_id), 60), -- Blueberries
        ((SELECT entity_id_value FROM fruit_smoothie_pre_workout_div_vs_id), (SELECT entity_id_value FROM frozen_strawberries_asda_ingredient_id), 60), -- Strawberry
        ((SELECT entity_id_value FROM fruit_smoothie_pre_workout_div_vs_id), (SELECT entity_id_value FROM grapefruit_ingredient_id), 110), -- Grapefruit
        ((SELECT entity_id_value FROM fruit_smoothie_pre_workout_div_vs_id), (SELECT entity_id_value FROM oranges_ingredient_id), 110), -- Oranges
        ((SELECT entity_id_value FROM fruit_smoothie_pre_workout_div_vs_id), (SELECT entity_id_value FROM water_ingredient_id), 1000), -- Water

        -- #####################################################
        -- # Post-Workout
        -- #####################################################

        -- # Bananas Part 11
        ((SELECT entity_id_value FROM bananas_post_workout_div_vs_id), (SELECT entity_id_value FROM banana_ingredient_id), 200), -- Bananas

        -- # Protein Shake Part 12
        ((SELECT entity_id_value FROM protein_shake_post_workout_div_vs_id), (SELECT entity_id_value FROM whey_isolate_natural_strawberry_myprotein_ingredient_id), 35), -- Protein Powder
        ((SELECT entity_id_value FROM protein_shake_post_workout_div_vs_id), (SELECT entity_id_value FROM creatine_monohydrate_myprotein_ingredient_id), 5), -- Creatine
        ((SELECT entity_id_value FROM protein_shake_post_workout_div_vs_id), (SELECT entity_id_value FROM alpro_soya_light_long_life_drink_ingredient_id), 450), -- Milk

        -- # Veg Meal Part 13
        ((SELECT entity_id_value FROM veg_meal_post_workout_div_vs_id), (SELECT entity_id_value FROM broccoli_ingredient_id), 110), -- Broccoli
        ((SELECT entity_id_value FROM veg_meal_post_workout_div_vs_id), (SELECT entity_id_value FROM carrots_ingredient_id), 110), -- Carrots
        ((SELECT entity_id_value FROM veg_meal_post_workout_div_vs_id), (SELECT entity_id_value FROM sweet_potato_ingredient_id), 110), -- Sweet Potato
        ((SELECT entity_id_value FROM veg_meal_post_workout_div_vs_id), (SELECT entity_id_value FROM kale_ingredient_id), 110), -- Kale

        -- #####################################################
        -- # Dinner
        -- #####################################################

        -- # Pasta Meal Part 14
        ((SELECT entity_id_value FROM dinner_div_vs_id), (SELECT entity_id_value FROM dolmio_bolognese_pasta_sauce_low_fat_ingredient_id), 60), -- Dolmio Sauce
        ((SELECT entity_id_value FROM dinner_div_vs_id), (SELECT entity_id_value FROM wholewheat_spaghetti_asda_ingredient_id), 75), -- Pasta
        ((SELECT entity_id_value FROM dinner_div_vs_id), (SELECT entity_id_value FROM lean_turkey_mince_asda_ingredient_id), 150), -- Turkey Mince
        ((SELECT entity_id_value FROM dinner_div_vs_id), (SELECT entity_id_value FROM sweetcorn_ingredient_id), 60), -- Sweetcorn

        -- #####################################################
        -- # Bed Snack
        -- #####################################################

        -- # Protein Shake Part 15
        ((SELECT entity_id_value FROM protein_shake_bedtime_vs_id), (SELECT entity_id_value FROM slow_release_casein_vanilla_myprotein_ingredient_id), 35), -- Casein
        ((SELECT entity_id_value FROM protein_shake_bedtime_vs_id), (SELECT entity_id_value FROM alpro_organic_soya_ingredient_id), 450); -- Milk



