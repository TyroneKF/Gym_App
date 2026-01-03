-- ###############################################################################
-- Set Variables
-- ###############################################################################
    /*

    */



-- ##################################################################################
-- Inserting BULK Non-Seed DATA
-- ##################################################################################
    WITH
        -- Measurements ID's
       grams_measurement_id   AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'grams_measurement_id'),
       litres_measurement_id  AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'litres_measurement_id'),
       
       -- Ingredient Types ID's
       breads_type_id           AS  (SELECT entity_id_value FROM seed_registry WHERE seed_key =  'breads_type_id'),
       cereals_type_id          AS  (SELECT entity_id_value FROM seed_registry WHERE seed_key =  'cereals_type_id'),
       dairy_type_id            AS  (SELECT entity_id_value FROM seed_registry WHERE seed_key =  'dairy_type_id'),
       flour_type_id            AS  (SELECT entity_id_value FROM seed_registry WHERE seed_key =  'flour_type_id'),
       frozen_fruit_type_id     AS  (SELECT entity_id_value FROM seed_registry WHERE seed_key =  'frozen_fruit_type_id'),
       fruit_type_id            AS  (SELECT entity_id_value FROM seed_registry WHERE seed_key =  'fruit_type_id'),
       grains_and_legumes_type_id AS  (SELECT entity_id_value FROM seed_registry WHERE seed_key =  'grains_and_legumes_type_id'),
       liquids_type_id          AS  (SELECT entity_id_value FROM seed_registry WHERE seed_key =  'liquids_type_id'),
       meat_type_id             AS  (SELECT entity_id_value FROM seed_registry WHERE seed_key =  'meat_type_id'),
       milk_type_id             AS  (SELECT entity_id_value FROM seed_registry WHERE seed_key =  'milk_type_id'),
       nuts_and_seeds_type_id   AS  (SELECT entity_id_value FROM seed_registry WHERE seed_key =  'nuts_and_seeds_type_id'),
       pasta_type_id            AS  (SELECT entity_id_value FROM seed_registry WHERE seed_key =  'pasta_type_id'),
       plant_milk_type_id       AS  (SELECT entity_id_value FROM seed_registry WHERE seed_key =  'plant_milk_type_id'),
       poultry_type_id          AS  (SELECT entity_id_value FROM seed_registry WHERE seed_key =  'poultry_type_id'),
       protein_powder_type_id   AS  (SELECT entity_id_value FROM seed_registry WHERE seed_key =  'protein_powder_type_id'),
       sauce_type_id            AS  (SELECT entity_id_value FROM seed_registry WHERE seed_key =  'sauce_type_id'),
       sugar_type_id            AS  (SELECT entity_id_value FROM seed_registry WHERE seed_key =  'sugar_type_id'),
       syrup_and_spreads_type_id AS  (SELECT entity_id_value FROM seed_registry WHERE seed_key =  'syrup_and_spreads_type_id'),
       vegan_vitamin_powders_type_id AS  (SELECT entity_id_value FROM seed_registry WHERE seed_key =  'vegan_vitamin_powders_type_id'),
       vegetables_type_id       AS  (SELECT entity_id_value FROM seed_registry WHERE seed_key =  'vegetables_type_id'),
       yoghurt_type_id          AS  (SELECT entity_id_value FROM seed_registry WHERE seed_key =  'yoghurt_type_id')

    INSERT INTO ingredients_info
    (
        ingredient_name,
        measurement_id,
        ingredient_type_id,
        based_on_quantity,
        glycemic_index,
        protein,
        carbohydrates,
        sugars_of_carbs,
        fibre,
        fat,
        saturated_fat,
        salt,
        water_content,
        liquid_content,
        calories
    )
    VALUES
    
    ("Water", (SELECT entity_id_value FROM litres_measurement_id), (SELECT entity_id_value FROM liquids_type_id), 100.00, 0, 0, 0, 0, 0, 0, 0, 0, 100, 100, 0),
    ("Banana", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM fruit_type_id), 100.00, 51, 1.1, 23, 12, 2.6, 0.3, 0.1, 0, 0, 0, 89),
    ("Wholemeal Wraps (Tesco)", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM breads_type_id), 1.00, 30, 5.5, 27.9, 2, 4.2, 3.2, 1.5, 0.55, 0, 0, 170),
    ("Spinach", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM vegetables_type_id), 100.00, 15, 2.9, 3.6, 0.4, 2.2, 0.4, 0.1, 0, 0, 0, 23),
    ("Heinz Classic Barbecue Sauce", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM sauce_type_id), 100.00, 0, 0.9, 34, 29, 0, 0.2, 0.1, 1.2, 0, 100, 138),
    ("Kale", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM vegetables_type_id), 100.00, 3, 2.92, 4.42, 0.8, 4.1, 1.49, 0, 0, 0, 0, 20),
    ("Sweet Potato", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM vegetables_type_id), 100.00, 68, 1.6, 20, 4.2, 3, 0.1, 0, 0, 0, 0, 86),
    ("Carrots", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM vegetables_type_id), 100.00, 41, 0.9, 10, 4.7, 2.8, 0.2, 0, 0, 0, 0, 41),
    ("Broccolli", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM vegetables_type_id), 100.00, 15, 2.57, 6.27, 1.4, 2.4, 0.34, 0.03, 0, 0, 0, 39),
    ("Celery", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM vegetables_type_id), 100.00, 35, 0.49, 3.32, 0, 0, 0.16, 0, 0, 0, 0, 17),

    ("Frozen Mangoes (ASDA)", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM frozen_fruit_type_id), 100.00, 53, 0.7, 14, 14, 2.6, 0, 0, 0, 0, 0, 66),
    ("Frozen Pineapple (ASDA)", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM frozen_fruit_type_id), 100.00, 62, 0, 10, 10, 1.2, 0, 0, 0, 0, 0, 46),
    ("Oranges", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM fruit_type_id), 100.00, 40, 0.9, 12, 9, 2.4, 0.1, 0, 0, 0, 0, 47),
    ("Frozen Strawberries (ASDA)", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM frozen_fruit_type_id), 100.00, 40, 0.6, 6.1, 6.1, 3.8, 0, 0, 0, 0, 0, 39),
    ("Frozen Blueberries (ASDA)", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM frozen_fruit_type_id), 100.00, 53, 0.9, 9.1, 9.1, 1.5, 0, 0, 0, 0, 0, 45),
    ("Frozen Raspberries (ASDA)", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM frozen_fruit_type_id), 100.00, 32, 0.8, 5.1, 5.1, 3.7, 0, 0, 0, 0, 0, 34),

    ("Pumpkin Seeds",  (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM nuts_and_seeds_type_id), 100.00, 25, 19, 54, 0, 18, 19, 3.7, 0, 0, 0, 446),
    ("Sunflower Seeds", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM nuts_and_seeds_type_id), 100.00, 22, 21, 20, 2.6, 9, 51, 4.5, 0, 0, 0, 584),
    ("Pistachios", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM nuts_and_seeds_type_id), 100.00, 15, 20, 28, 8, 10, 45, 6, 0, 0, 0, 562),
    ("Cashew Nuts", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM nuts_and_seeds_type_id), 100.00, 25, 18, 30, 6, 3.3, 44, 8, 0, 0, 0, 553),

    ("Chicken Mince (ASDA)", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM poultry_type_id), 100.00, 0, 28, 0.5, 0, 0.5, 5.7, 1.5, 0, 0, 0, 166),
    ("Lean Turkey Mince (ASDA)", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM poultry_type_id), 100.00, 0, 19, 0.5, 0.5, 0.5, 6.8, 1.9, 0.25, 0, 0, 140),

    ("Maca (VivoLife)", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM vegan_vitamin_powders_type_id), 100.00, 54, 10, 75, 25, 8.5, 2.2, 0, 0, 0, 0, 377),
    ("Thrive (VivoLife)",  (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM vegan_vitamin_powders_type_id), 1.00, 0, 0.4, 3.4, 0.9, 2.5, 0.05, 0.02, 0.08, 0, 0, 21),

    ("ASDA Scottish Porridge Oats", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM cereals_type_id), 100.00, 55, 1.6, 7.8, 0.5, 1.3, 0.8, 0.1, 0.01, 0, 0, 47),
    ("Rowse Organic Honey",  (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM syrup_and_spreads_type_id), 100.00, 58, 0.5, 81.5, 80.8, 0.5, 0.5, 0.2, 0.03, 0, 100, 329),

    ("Rice Dream Original Organic Milk", (SELECT entity_id_value FROM litres_measurement_id), (SELECT entity_id_value FROM plant_milk_type_id), 100.00, 86, 0.1, 9.9, 7.1, 0, 1.1, 0.1, 0.07, 0, 100, 50),
    ("Alpro Soya Light Long Life Drink", (SELECT entity_id_value FROM litres_measurement_id), (SELECT entity_id_value FROM plant_milk_type_id), 100.00, 30, 2.1, 1.7, 1.5, 0.9, 1.2, 0.2, 0.11, 0, 100, 28),
    ("Alpro Organic Soya", (SELECT entity_id_value FROM litres_measurement_id), (SELECT entity_id_value FROM plant_milk_type_id), 100.00, 30, 3.3, 0, 0, 0.6, 1.9, 0.3, 0.03, 0, 100, 32),

    ("Creatine Monohydrate (MyProtein)", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM protein_powder_type_id), 4.00, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    ("Whey Isolate Natural Strawberry (MyProtein)", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM protein_powder_type_id), 25.00, 0, 22, 1.5, 1.5, 0, 0.1, 0.1, 0.13, 0, 0, 93),
    ("Whey Isolate Chocolate Smooth (MyProtein)", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM protein_powder_type_id), 25.00, 0, 21, 1.6, 0.6, 0, 0.2, 0.1, 0.13, 0, 0, 91),
    ("Slow-Release Casein Vanilla (MyProtein)", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM protein_powder_type_id), 30.00, 0, 25, 3, 2.4, 0, 0.4, 0.2, 0.08, 0, 0, 116),

    ("Dolmio Bolognese Pasta Sauce Low Fat", (SELECT entity_id_value FROM litres_measurement_id), (SELECT entity_id_value FROM sauce_type_id), 100.00, 35, 1.3, 6.7, 3.8, 1.1, 0.5, 0.1, 0.71, 0, 100, 36),
    ("Wholewheat Spaghetti (ASDA)", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM pasta_type_id), 100.00, 40, 4.8, 28, 0.5, 3.7, 0.9, 0.2, 0.03, 0, 0, 148),
    ("Sweetcorn", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM vegetables_type_id), 100.00, 55, 3.2, 19, 3.2, 2.7, 1.2, 0.2, 0, 0, 0, 86),
    ("Semi Skin Cow Milk", (SELECT entity_id_value FROM litres_measurement_id), (SELECT entity_id_value FROM milk_type_id), 100.00, 31, 3.6, 4.8, 4.8, 0, 1.8, 1.1, 0.11, 0, 100, 50),
    ("Black Beans", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM grains_and_legumes_type_id), 172.00, 30, 15.2, 40.8, 0.6, 15, 0.9, 0.2, 0, 0, 0, 227),
    ("Grapefruit",  (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM fruit_type_id), 100.00, 25, 0.6, 8.1, 7, 1.1, 0.1, 0, 0, 0, 0, 32),
    ("Corned Beef (Princess Reduced Fat)", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM meat_type_id), 100.00, 0, 25, 0, 0, 0, 13.5, 6.5, 1.4, 0, 0, 222),
    ("Large Eggs (60g)", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM poultry_type_id), 1.00, 0, 8.3, 0.5, 0.5, 0.5, 5.7, 1.6, 0.22, 0, 0, 85),
    ("Fage Greek Yoghurt", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM yoghurt_type_id), 100.00, 12, 10.3, 3, 3, 0, 0, 0, 0.1, 0, 100, 54),
    ("Be-Ro Plain Flour",  (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM flour_type_id), 100.00, 85, 10.4, 70.1, 1.4, 3.2, 1.3, 0.2, 0, 0, 0, 340),
    ("Allisons Wholemeal Flour", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM flour_type_id), 100.00, 60, 12, 65, 1.4, 10, 2.6, 0.5, 0.03, 0, 0, 350),
    ("Bertolli Olive Oil Spread", (SELECT entity_id_value FROM litres_measurement_id), (SELECT entity_id_value FROM dairy_type_id), 100.00, 0, 0.5, 0.5, 0.5, 0, 59, 17, 1.1, 0, 0, 531),
    ("Allinson Self-Raising Wholemeal Flour", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM flour_type_id), 100.00, 60, 11, 69.3, 1.1, 10, 2.4, 0.4, 1.1, 0, 0, 359),
    ("Plantin", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM fruit_type_id), 100.00, 40, 1.3, 32, 15, 2.3, 0.4, 0.1, 0, 0, 0, 122),
    ("Billingtons Brown Sugar", (SELECT entity_id_value FROM grams_measurement_id), (SELECT entity_id_value FROM sugar_type_id), 100.00, 70, 0, 99, 99, 0, 0, 0, 0.25, 0, 0, 398)

    ON CONFLICT(ingredient_id)  -- In case of duplicate, ensures fields match correctly to new insert
        DO UPDATE SET
        ingredient_id = excluded.ingredient_id; -- On update has to be on the the PK or unique Key


-- ###############################################################################
-- 5.) Set Ingredient ID Variables (Alphabetical Order)
-- ###############################################################################
    /*


    */

    -- #####################################
    -- Alpro Organic Soya
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'alpro_organic_soya_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Alpro Organic Soya'
            )
        )
        ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
           DO UPDATE SET
           entity_id_value = excluded.entity_id_value; -- On update triggered by PK or unique Key

    -- #####################################
    -- Alpro Soya Light Long Life Drink
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'alpro_soya_light_long_life_drink_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Alpro Soya Light Long Life Drink'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- ASDA Scottish Porridge Oats
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'asda_scottish_porridge_oats_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'ASDA Scottish Porridge Oats'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Banana
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'banana_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Banana'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Be-Ro Plain Flour
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'be_ro_plain_flour_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Be-Ro Plain Flour'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Bertolli Olive Oil Spread
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'bertolli_olive_oil_spread_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Bertolli Olive Oil Spread'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Billingtons Brown Sugar
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'billingtons_brown_sugar_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Billingtons Brown Sugar'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Black Beans
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'black_beans_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Black Beans'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Broccoli
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'broccoli_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Broccolli'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Carrots
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'carrots_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Carrots'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Cashew Nuts
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'cashew_nuts_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Cashew Nuts'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Celery
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'celery_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Celery'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Chicken Mince (ASDA)
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'chicken_mince_asda_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Chicken Mince (ASDA)'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Corned Beef (Princess Reduced Fat)
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'corned_beef_princess_reduced_fat_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Corned Beef (Princess Reduced Fat)'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Creatine Monohydrate (MyProtein)
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'creatine_monohydrate_myprotein_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Creatine Monohydrate (MyProtein)'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Dolmio Bolognese Pasta Sauce Low Fat
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'dolmio_bolognese_pasta_sauce_low_fat_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Dolmio Bolognese Pasta Sauce Low Fat'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Fage Greek Yoghurt
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'fage_greek_yoghurt_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Fage Greek Yoghurt'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Frozen Blueberries (ASDA)
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'frozen_blueberries_asda_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Frozen Blueberries (ASDA)'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Frozen Mangoes (ASDA)
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'frozen_mangoes_asda_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Frozen Mangoes (ASDA)'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Frozen Pineapple (ASDA)
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'frozen_pineapple_asda_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Frozen Pineapple (ASDA)'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Frozen Raspberries (ASDA)
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'frozen_raspberries_asda_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Frozen Raspberries (ASDA)'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Frozen Strawberries (ASDA)
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'frozen_strawberries_asda_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Frozen Strawberries (ASDA)'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Grapefruit
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'grapefruit_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Grapefruit'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Heinz Classic Barbecue Sauce
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'heinz_classic_barbecue_sauce_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Heinz Classic Barbecue Sauce'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Kale
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'kale_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Kale'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Large Eggs (60g)
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'large_eggs_60g_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Large Eggs (60g)'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Lean Turkey Mince (ASDA)
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'lean_turkey_mince_asda_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Lean Turkey Mince (ASDA)'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Maca (VivoLife)
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'maca_vivolife_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Maca (VivoLife)'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Oranges
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'oranges_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Oranges'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Pistachios
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'pistachios_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Pistachios'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Plantin
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'plantin_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Plantin'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Pumpkin Seeds
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'pumpkin_seeds_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Pumpkin Seeds'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Rice Dream Original Organic Milk
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'rice_dream_original_organic_milk_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Rice Dream Original Organic Milk'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Rowse Organic Honey
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'rowse_organic_honey_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Rowse Organic Honey'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Semi Skin Cow Milk
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'semi_skin_cow_milk_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Semi Skin Cow Milk'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Slow-Release Casein Vanilla (MyProtein)
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'slow_release_casein_vanilla_myprotein_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Slow-Release Casein Vanilla (MyProtein)'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Spinach
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'spinach_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Spinach'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Sunflower Seeds
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'sunflower_seeds_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Sunflower Seeds'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Sweet Potato
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'sweet_potato_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Sweet Potato'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Sweetcorn
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'sweetcorn_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Sweetcorn'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Thrive (VivoLife)
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'thrive_vivolife_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Thrive (VivoLife)'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Water
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'water_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Water'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Whey Isolate Chocolate Smooth (MyProtein)
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'whey_isolate_chocolate_smooth_myprotein_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Whey Isolate Chocolate Smooth (MyProtein)'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Whey Isolate Natural Strawberry (MyProtein)
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'whey_isolate_natural_strawberry_myprotein_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Whey Isolate Natural Strawberry (MyProtein)'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Wholemeal Wraps (Tesco)
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'wholemeal_wraps_tesco_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Wholemeal Wraps (Tesco)'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;

    -- #####################################
    -- Wholewheat Spaghetti (ASDA)
    -- #####################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'wholewheat_spaghetti_asda_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'Wholewheat Spaghetti (ASDA)'
            )
        )
        ON CONFLICT(seed_key)
           DO UPDATE SET
           entity_id_value = EXCLUDED.entity_id_value;
