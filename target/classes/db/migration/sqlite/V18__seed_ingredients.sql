-- ###############################################################################
-- Set Variables
-- ###############################################################################
/*

*/

-- ####################################################
-- Set Measurements Variables (Alphabetical Order)
-- ####################################################
SET @grams_measurement_id := get_seed_id_by_key(
    'grams_measurement_id',
    'Seed failed: measurements  @grams_measurement_id could not be resolved'
);

SET @litres_measurement_id := get_seed_id_by_key(
    'litres_measurement_id',
    'Seed failed: measurements  @litres_measurement_id could not be resolved'
);

-- ####################################################
-- Set Ingredient Type Variables (Alphabetical Order)
-- ####################################################
SET @breads_type_id := get_seed_id_by_key(
    'breads_type_id',
    'Seed failed: ingredient_types @breads_type_id could not be resolved'
);

SET @cereals_type_id := get_seed_id_by_key(
    'cereals_type_id',
    'Seed failed: ingredient_types @cereals_type_id could not be resolved'
);

SET @dairy_type_id := get_seed_id_by_key(
    'dairy_type_id',
    'Seed failed: ingredient_types @dairy_type_id could not be resolved'
);

SET @flour_type_id := get_seed_id_by_key(
    'flour_type_id',
    'Seed failed: ingredient_types @flour_type_id could not be resolved'
);

SET @frozen_fruit_type_id := get_seed_id_by_key(
    'frozen_fruit_type_id',
    'Seed failed: ingredient_types @frozen_fruit_type_id could not be resolved'
);

SET @fruit_type_id := get_seed_id_by_key(
    'fruit_type_id',
    'Seed failed: ingredient_types @fruit_type_id could not be resolved'
);

SET @grains_and_legumes_type_id := get_seed_id_by_key(
    'grains_and_legumes_type_id',
    'Seed failed: ingredient_types @grains_and_legumes_type_id could not be resolved'
);

SET @liquids_type_id := get_seed_id_by_key(
    'liquids_type_id',
    'Seed failed: ingredient_types @liquids_type_id could not be resolved'
);

SET @meat_type_id := get_seed_id_by_key(
    'meat_type_id',
    'Seed failed: ingredient_types @meat_type_id could not be resolved'
);

SET @milk_type_id := get_seed_id_by_key(
    'milk_type_id',
    'Seed failed: ingredient_types @milk_type_id could not be resolved'
);

SET @nuts_and_seeds_type_id := get_seed_id_by_key(
    'nuts_and_seeds_type_id',
    'Seed failed: ingredient_types @nuts_and_seeds_type_id could not be resolved'
);

SET @pasta_type_id := get_seed_id_by_key(
    'pasta_type_id',
    'Seed failed: ingredient_types @pasta_type_id could not be resolved'
);

SET @plant_milk_type_id := get_seed_id_by_key(
    'plant_milk_type_id',
    'Seed failed: ingredient_types @plant_milk_type_id could not be resolved'
);

SET @poultry_type_id := get_seed_id_by_key(
    'poultry_type_id',
    'Seed failed: ingredient_types @poultry_type_id could not be resolved'
);

SET @protein_powder_type_id := get_seed_id_by_key(
    'protein_powder_type_id',
    'Seed failed: ingredient_types @protein_powder_type_id could not be resolved'
);

SET @sauce_type_id := get_seed_id_by_key(
    'sauce_type_id',
    'Seed failed: ingredient_types @sauce_type_id could not be resolved'
);

SET @sugar_type_id := get_seed_id_by_key(
    'sugar_type_id',
    'Seed failed: ingredient_types @sugar_type_id could not be resolved'
);

SET @syrup_and_spreads_type_id := get_seed_id_by_key(
    'syrup_and_spreads_type_id',
    'Seed failed: ingredient_types @syrup_and_spreads_type_id could not be resolved'
);

SET @vegan_vitamin_powders_type_id := get_seed_id_by_key(
    'vegan_vitamin_powders_type_id',
    'Seed failed: ingredient_types @vegan_vitamin_powders_type_id could not be resolved'
);

SET @vegetables_type_id := get_seed_id_by_key(
    'vegetables_type_id',
    'Seed failed: ingredient_types @vegetables_type_id could not be resolved'
);

SET @yoghurt_type_id := get_seed_id_by_key(
    'yoghurt_type_id',
    'Seed failed: ingredient_types @yoghurt_type_id could not be resolved'
);

-- ##################################################################################
-- Inserting BULK Non-Seed DATA
-- ##################################################################################
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

("Water", @litres_measurement_id, @liquids_type_id, 100.00, 0, 0, 0, 0, 0, 0, 0, 0, 100, 100, 0),
("Banana", @grams_measurement_id, @fruit_type_id, 100.00, 51, 1.1, 23, 12, 2.6, 0.3, 0.1, 0, 0, 0, 89),
("Wholemeal Wraps (Tesco)", @grams_measurement_id, @breads_type_id, 1.00, 30, 5.5, 27.9, 2, 4.2, 3.2, 1.5, 0.55, 0, 0, 170),
("Spinach", @grams_measurement_id, @vegetables_type_id, 100.00, 15, 2.9, 3.6, 0.4, 2.2, 0.4, 0.1, 0, 0, 0, 23),
("Heinz Classic Barbecue Sauce", @grams_measurement_id, @sauce_type_id, 100.00, 0, 0.9, 34, 29, 0, 0.2, 0.1, 1.2, 0, 100, 138),
("Kale", @grams_measurement_id, @vegetables_type_id, 100.00, 3, 2.92, 4.42, 0.8, 4.1, 1.49, 0, 0, 0, 0, 20),
("Sweet Potato", @grams_measurement_id, @vegetables_type_id, 100.00, 68, 1.6, 20, 4.2, 3, 0.1, 0, 0, 0, 0, 86),
("Carrots", @grams_measurement_id, @vegetables_type_id, 100.00, 41, 0.9, 10, 4.7, 2.8, 0.2, 0, 0, 0, 0, 41),
("Broccolli", @grams_measurement_id, @vegetables_type_id, 100.00, 15, 2.57, 6.27, 1.4, 2.4, 0.34, 0.03, 0, 0, 0, 39),
("Celery", @grams_measurement_id, @vegetables_type_id, 100.00, 35, 0.49, 3.32, 0, 0, 0.16, 0, 0, 0, 0, 17),

("Frozen Mangoes (ASDA)", @grams_measurement_id, @frozen_fruit_type_id, 100.00, 53, 0.7, 14, 14, 2.6, 0, 0, 0, 0, 0, 66),
("Frozen Pineapple (ASDA)", @grams_measurement_id, @frozen_fruit_type_id, 100.00, 62, 0, 10, 10, 1.2, 0, 0, 0, 0, 0, 46),
("Oranges", @grams_measurement_id, @fruit_type_id, 100.00, 40, 0.9, 12, 9, 2.4, 0.1, 0, 0, 0, 0, 47),
("Frozen Strawberries (ASDA)", @grams_measurement_id, @frozen_fruit_type_id, 100.00, 40, 0.6, 6.1, 6.1, 3.8, 0, 0, 0, 0, 0, 39),
("Frozen Blueberries (ASDA)", @grams_measurement_id, @frozen_fruit_type_id, 100.00, 53, 0.9, 9.1, 9.1, 1.5, 0, 0, 0, 0, 0, 45),
("Frozen Raspberries (ASDA)", @grams_measurement_id, @frozen_fruit_type_id, 100.00, 32, 0.8, 5.1, 5.1, 3.7, 0, 0, 0, 0, 0, 34),

("Pumpkin Seeds",  @grams_measurement_id, @nuts_and_seeds_type_id, 100.00, 25, 19, 54, 0, 18, 19, 3.7, 0, 0, 0, 446),
("Sunflower Seeds", @grams_measurement_id, @nuts_and_seeds_type_id, 100.00, 22, 21, 20, 2.6, 9, 51, 4.5, 0, 0, 0, 584),
("Pistachios", @grams_measurement_id, @nuts_and_seeds_type_id, 100.00, 15, 20, 28, 8, 10, 45, 6, 0, 0, 0, 562),
("Cashew Nuts", @grams_measurement_id, @nuts_and_seeds_type_id, 100.00, 25, 18, 30, 6, 3.3, 44, 8, 0, 0, 0, 553),

("Chicken Mince (ASDA)", @grams_measurement_id, @poultry_type_id, 100.00, 0, 28, 0.5, 0, 0.5, 5.7, 1.5, 0, 0, 0, 166),
("Lean Turkey Mince (ASDA)", @grams_measurement_id, @poultry_type_id, 100.00, 0, 19, 0.5, 0.5, 0.5, 6.8, 1.9, 0.25, 0, 0, 140),

("Maca (VivoLife)", @grams_measurement_id, @vegan_vitamin_powders_type_id, 100.00, 54, 10, 75, 25, 8.5, 2.2, 0, 0, 0, 0, 377),
("Thrive (VivoLife)",  @grams_measurement_id, @vegan_vitamin_powders_type_id, 1.00, 0, 0.4, 3.4, 0.9, 2.5, 0.05, 0.02, 0.08, 0, 0, 21),

("ASDA Scottish Porridge Oats", @grams_measurement_id, @cereals_type_id, 100.00, 55, 1.6, 7.8, 0.5, 1.3, 0.8, 0.1, 0.01, 0, 0, 47),
("Rowse Organic Honey",  @grams_measurement_id,@syrup_and_spreads_type_id, 100.00, 58, 0.5, 81.5, 80.8, 0.5, 0.5, 0.2, 0.03, 0, 100, 329),

("Rice Dream Original Organic Milk", @litres_measurement_id, @plant_milk_type_id, 100.00, 86, 0.1, 9.9, 7.1, 0, 1.1, 0.1, 0.07, 0, 100, 50),
("Alpro Soya Light Long Life Drink", @litres_measurement_id, @plant_milk_type_id, 100.00, 30, 2.1, 1.7, 1.5, 0.9, 1.2, 0.2, 0.11, 0, 100, 28),
("Alpro Organic Soya", @litres_measurement_id, @plant_milk_type_id, 100.00, 30, 3.3, 0, 0, 0.6, 1.9, 0.3, 0.03, 0, 100, 32),

("Creatine Monohydrate (MyProtein)", @grams_measurement_id, @protein_powder_type_id, 4.00, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0),
("Whey Isolate Natural Strawberry (MyProtein)", @grams_measurement_id, @protein_powder_type_id, 25.00, 0, 22, 1.5, 1.5, 0, 0.1, 0.1, 0.13, 0, 0, 93),
("Whey Isolate Chocolate Smooth (MyProtein)", @grams_measurement_id, @protein_powder_type_id, 25.00, 0, 21, 1.6, 0.6, 0, 0.2, 0.1, 0.13, 0, 0, 91),
("Slow-Release Casein Vanilla (MyProtein)", @grams_measurement_id, @protein_powder_type_id, 30.00, 0, 25, 3, 2.4, 0, 0.4, 0.2, 0.08, 0, 0, 116),

("Dolmio Bolognese Pasta Sauce Low Fat", @litres_measurement_id, @sauce_type_id, 100.00, 35, 1.3, 6.7, 3.8, 1.1, 0.5, 0.1, 0.71, 0, 100, 36),
("Wholewheat Spaghetti (ASDA)", @grams_measurement_id, @pasta_type_id, 100.00, 40, 4.8, 28, 0.5, 3.7, 0.9, 0.2, 0.03, 0, 0, 148),
("Sweetcorn", @grams_measurement_id, @vegetables_type_id, 100.00, 55, 3.2, 19, 3.2, 2.7, 1.2, 0.2, 0, 0, 0, 86),
("Semi Skin Cow Milk", @litres_measurement_id, @milk_type_id, 100.00, 31, 3.6, 4.8, 4.8, 0, 1.8, 1.1, 0.11, 0, 100, 50),
("Black Beans", @grams_measurement_id, @grains_and_legumes_type_id, 172.00, 30, 15.2, 40.8, 0.6, 15, 0.9, 0.2, 0, 0, 0, 227),
("Grapefruit",  @grams_measurement_id, @fruit_type_id, 100.00, 25, 0.6, 8.1, 7, 1.1, 0.1, 0, 0, 0, 0, 32),
("Corned Beef (Princess Reduced Fat)", @grams_measurement_id, @meat_type_id, 100.00, 0, 25, 0, 0, 0, 13.5, 6.5, 1.4, 0, 0, 222),
("Large Eggs (60g)", @grams_measurement_id, @poultry_type_id, 1.00, 0, 8.3, 0.5, 0.5, 0.5, 5.7, 1.6, 0.22, 0, 0, 85),
("Fage Greek Yoghurt", @grams_measurement_id, @yoghurt_type_id, 100.00, 12, 10.3, 3, 3, 0, 0, 0, 0.1, 0, 100, 54),
("Be-Ro Plain Flour",  @grams_measurement_id, @flour_type_id, 100.00, 85, 10.4, 70.1, 1.4, 3.2, 1.3, 0.2, 0, 0, 0, 340),
("Allisons Wholemeal Flour", @grams_measurement_id, @flour_type_id, 100.00, 60, 12, 65, 1.4, 10, 2.6, 0.5, 0.03, 0, 0, 350),
("Bertolli Olive Oil Spread", @litres_measurement_id, @dairy_type_id, 100.00, 0, 0.5, 0.5, 0.5, 0, 59, 17, 1.1, 0, 0, 531),
("Allinson Self-Raising Wholemeal Flour", @grams_measurement_id, @flour_type_id, 100.00, 60, 11, 69.3, 1.1, 10, 2.4, 0.4, 1.1, 0, 0, 359),
("Plantin", @grams_measurement_id, @fruit_type_id, 100.00, 40, 1.3, 32, 15, 2.3, 0.4, 0.1, 0, 0, 0, 122),
("Billingtons Brown Sugar", @grams_measurement_id, @sugar_type_id, 100.00, 70, 0, 99, 99, 0, 0, 0, 0.25, 0, 0, 398)

ON DUPLICATE KEY UPDATE  -- Incase of duplicate, ensures fields match correctly to new insert
    ingredient_type_id = VALUES(ingredient_type_id);

-- ###############################################################################
-- 5.) Set Ingredient ID Variables (Alphabetical Order)
-- ###############################################################################
/*


*/

-- #####################################
-- Alpro Organic Soya
-- #####################################
SELECT ingredient_id
INTO @alpro_organic_soya_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Alpro Organic Soya';

CALL validate_and_insert_into_seed_registry
(
    'alpro_organic_soya_ingredient_id',
    'ingredients_info',
    @alpro_organic_soya_ingredient_id,
    'Seed failed: ingredients_info @alpro_organic_soya_ingredient_id could not be resolved'
);

-- #####################################
-- Alpro Soya Light Long Life Drink
-- #####################################
SELECT ingredient_id
INTO @alpro_soya_light_long_life_drink_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Alpro Soya Light Long Life Drink';

CALL validate_and_insert_into_seed_registry
(
    'alpro_soya_light_long_life_drink_ingredient_id',
    'ingredients_info',
    @alpro_soya_light_long_life_drink_ingredient_id,
    'Seed failed: ingredients_info @alpro_soya_light_long_life_drink_ingredient_id could not be resolved'
);

-- #####################################
-- ASDA Scottish Porridge Oats
-- #####################################
SELECT ingredient_id
INTO @asda_scottish_porridge_oats_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'ASDA Scottish Porridge Oats';

CALL validate_and_insert_into_seed_registry
(
    'asda_scottish_porridge_oats_ingredient_id',
    'ingredients_info',
    @asda_scottish_porridge_oats_ingredient_id,
    'Seed failed: ingredients_info @asda_scottish_porridge_oats_ingredient_id could not be resolved'
);

-- #####################################
-- Banana
-- #####################################
SELECT ingredient_id
INTO @banana_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Banana';

CALL validate_and_insert_into_seed_registry
(
    'banana_ingredient_id',
    'ingredients_info',
    @banana_ingredient_id,
    'Seed failed: ingredients_info @banana_ingredient_id could not be resolved'
);

-- #####################################
-- Be-Ro Plain Flour
-- #####################################
SELECT ingredient_id
INTO @be_ro_plain_flour_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Be-Ro Plain Flour';

CALL validate_and_insert_into_seed_registry
(
    'be_ro_plain_flour_ingredient_id',
    'ingredients_info',
    @be_ro_plain_flour_ingredient_id,
    'Seed failed: ingredients_info @be_ro_plain_flour_ingredient_id could not be resolved'
);

-- #####################################
-- Bertolli Olive Oil Spread
-- #####################################
SELECT ingredient_id
INTO @bertolli_olive_oil_spread_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Bertolli Olive Oil Spread';

CALL validate_and_insert_into_seed_registry
(
    'bertolli_olive_oil_spread_ingredient_id',
    'ingredients_info',
    @bertolli_olive_oil_spread_ingredient_id,
    'Seed failed: ingredients_info @bertolli_olive_oil_spread_ingredient_id could not be resolved'
);

-- #####################################
-- Billingtons Brown Sugar
-- #####################################
SELECT ingredient_id
INTO @billingtons_brown_sugar_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Billingtons Brown Sugar';

CALL validate_and_insert_into_seed_registry
(
    'billingtons_brown_sugar_ingredient_id',
    'ingredients_info',
    @billingtons_brown_sugar_ingredient_id,
    'Seed failed: ingredients_info @billingtons_brown_sugar_ingredient_id could not be resolved'
);

-- #####################################
-- Black Beans
-- #####################################
SELECT ingredient_id
INTO @black_beans_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Black Beans';

CALL validate_and_insert_into_seed_registry
(
    'black_beans_ingredient_id',
    'ingredients_info',
    @black_beans_ingredient_id,
    'Seed failed: ingredients_info @black_beans_ingredient_id could not be resolved'
);

-- #####################################
-- Broccolli
-- #####################################
SELECT ingredient_id
INTO @broccoli_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Broccolli';

CALL validate_and_insert_into_seed_registry
(
    'broccoli_ingredient_id',
    'ingredients_info',
    @broccoli_ingredient_id,
    'Seed failed: ingredients_info @broccoli_ingredient_id could not be resolved'
);

-- #####################################
-- Carrots
-- #####################################
SELECT ingredient_id
INTO @carrots_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Carrots';

CALL validate_and_insert_into_seed_registry
(
    'carrots_ingredient_id',
    'ingredients_info',
    @carrots_ingredient_id,
    'Seed failed: ingredients_info @carrots_ingredient_id could not be resolved'
);

-- #####################################
-- Cashew Nuts
-- #####################################
SELECT ingredient_id
INTO @cashew_nuts_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Cashew Nuts';

CALL validate_and_insert_into_seed_registry
(
    'cashew_nuts_ingredient_id',
    'ingredients_info',
    @cashew_nuts_ingredient_id,
    'Seed failed: ingredients_info @cashew_nuts_ingredient_id could not be resolved'
);

-- #####################################
-- Celery
-- #####################################
SELECT ingredient_id
INTO @celery_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Celery';

CALL validate_and_insert_into_seed_registry
(
    'celery_ingredient_id',
    'ingredients_info',
    @celery_ingredient_id,
    'Seed failed: ingredients_info @celery_ingredient_id could not be resolved'
);

-- #####################################
-- Chicken Mince (ASDA)
-- #####################################
SELECT ingredient_id
INTO @chicken_mince_asda_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Chicken Mince (ASDA)';

CALL validate_and_insert_into_seed_registry
(
    'chicken_mince_asda_ingredient_id',
    'ingredients_info',
    @chicken_mince_asda_ingredient_id,
    'Seed failed: ingredients_info @chicken_mince_asda_ingredient_id could not be resolved'
);

-- #####################################
-- Corned Beef (Princess Reduced Fat)
-- #####################################
SELECT ingredient_id
INTO @corned_beef_princess_reduced_fat_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Corned Beef (Princess Reduced Fat)';

CALL validate_and_insert_into_seed_registry
(
    'corned_beef_princess_reduced_fat_ingredient_id',
    'ingredients_info',
    @corned_beef_princess_reduced_fat_ingredient_id,
    'Seed failed: ingredients_info @corned_beef_princess_reduced_fat_ingredient_id could not be resolved'
);

-- #####################################
-- Creatine Monohydrate (MyProtein)
-- #####################################
SELECT ingredient_id
INTO @creatine_monohydrate_myprotein_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Creatine Monohydrate (MyProtein)';

CALL validate_and_insert_into_seed_registry
(
    'creatine_monohydrate_myprotein_ingredient_id',
    'ingredients_info',
    @creatine_monohydrate_myprotein_ingredient_id,
    'Seed failed: ingredients_info @creatine_monohydrate_myprotein_ingredient_id could not be resolved'
);

-- #####################################
-- Dolmio Bolognese Pasta Sauce Low Fat
-- #####################################
SELECT ingredient_id
INTO @dolmio_bolognese_pasta_sauce_low_fat_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Dolmio Bolognese Pasta Sauce Low Fat';

CALL validate_and_insert_into_seed_registry
(
    'dolmio_bolognese_pasta_sauce_low_fat_ingredient_id',
    'ingredients_info',
    @dolmio_bolognese_pasta_sauce_low_fat_ingredient_id,
    'Seed failed: ingredients_info @dolmio_bolognese_pasta_sauce_low_fat_ingredient_id could not be resolved'
);

-- #####################################
-- Fage Greek Yoghurt
-- #####################################
SELECT ingredient_id
INTO @fage_greek_yoghurt_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Fage Greek Yoghurt';

CALL validate_and_insert_into_seed_registry
(
    'fage_greek_yoghurt_ingredient_id',
    'ingredients_info',
    @fage_greek_yoghurt_ingredient_id,
    'Seed failed: ingredients_info @fage_greek_yoghurt_ingredient_id could not be resolved'
);

-- #####################################
-- Frozen Blueberries (ASDA)
-- #####################################
SELECT ingredient_id
INTO @frozen_blueberries_asda_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Frozen Blueberries (ASDA)';

CALL validate_and_insert_into_seed_registry
(
    'frozen_blueberries_asda_ingredient_id',
    'ingredients_info',
    @frozen_blueberries_asda_ingredient_id,
    'Seed failed: ingredients_info @frozen_blueberries_asda_ingredient_id could not be resolved'
);

-- #####################################
-- Frozen Mangoes (ASDA)
-- #####################################
SELECT ingredient_id
INTO @frozen_mangoes_asda_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Frozen Mangoes (ASDA)';

CALL validate_and_insert_into_seed_registry
(
    'frozen_mangoes_asda_ingredient_id',
    'ingredients_info',
    @frozen_mangoes_asda_ingredient_id,
    'Seed failed: ingredients_info @frozen_mangoes_asda_ingredient_id could not be resolved'
);

-- #####################################
-- Frozen Pineapple (ASDA)
-- #####################################
SELECT ingredient_id
INTO @frozen_pineapple_asda_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Frozen Pineapple (ASDA)';

CALL validate_and_insert_into_seed_registry
(
    'frozen_pineapple_asda_ingredient_id',
    'ingredients_info',
    @frozen_pineapple_asda_ingredient_id,
    'Seed failed: ingredients_info @frozen_pineapple_asda_ingredient_id could not be resolved'
);

-- #####################################
-- Frozen Raspberries (ASDA)
-- #####################################
SELECT ingredient_id
INTO @frozen_raspberries_asda_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Frozen Raspberries (ASDA)';

CALL validate_and_insert_into_seed_registry
(
    'frozen_raspberries_asda_ingredient_id',
    'ingredients_info',
    @frozen_raspberries_asda_ingredient_id,
    'Seed failed: ingredients_info @frozen_raspberries_asda_ingredient_id could not be resolved'
);

-- #####################################
-- Frozen Strawberries (ASDA)
-- #####################################
SELECT ingredient_id
INTO @frozen_strawberries_asda_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Frozen Strawberries (ASDA)';

CALL validate_and_insert_into_seed_registry
(
    'frozen_strawberries_asda_ingredient_id',
    'ingredients_info',
    @frozen_strawberries_asda_ingredient_id,
    'Seed failed: ingredients_info @frozen_strawberries_asda_ingredient_id could not be resolved'
);

-- #####################################
-- Grapefruit
-- #####################################
SELECT ingredient_id
INTO @grapefruit_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Grapefruit';

CALL validate_and_insert_into_seed_registry
(
    'grapefruit_ingredient_id',
    'ingredients_info',
    @grapefruit_ingredient_id,
    'Seed failed: ingredients_info @grapefruit_ingredient_id could not be resolved'
);

-- #####################################
-- Heinz Classic Barbecue Sauce
-- #####################################
SELECT ingredient_id
INTO @heinz_classic_barbecue_sauce_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Heinz Classic Barbecue Sauce';

CALL validate_and_insert_into_seed_registry
(
    'heinz_classic_barbecue_sauce_ingredient_id',
    'ingredients_info',
    @heinz_classic_barbecue_sauce_ingredient_id,
    'Seed failed: ingredients_info @heinz_classic_barbecue_sauce_ingredient_id could not be resolved'
);

-- #####################################
-- Kale
-- #####################################
SELECT ingredient_id
INTO @kale_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Kale';

CALL validate_and_insert_into_seed_registry
(
    'kale_ingredient_id',
    'ingredients_info',
    @kale_ingredient_id,
    'Seed failed: ingredients_info @kale_ingredient_id could not be resolved'
);

-- #####################################
-- Large Eggs (60g)
-- #####################################
SELECT ingredient_id
INTO @large_eggs_60g_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Large Eggs (60g)';

CALL validate_and_insert_into_seed_registry
(
    'large_eggs_60g_ingredient_id',
    'ingredients_info',
    @large_eggs_60g_ingredient_id,
    'Seed failed: ingredients_info @large_eggs_60g_ingredient_id could not be resolved'
);

-- #####################################
-- Lean Turkey Mince (ASDA)
-- #####################################
SELECT ingredient_id
INTO @lean_turkey_mince_asda_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Lean Turkey Mince (ASDA)';

CALL validate_and_insert_into_seed_registry
(
    'lean_turkey_mince_asda_ingredient_id',
    'ingredients_info',
    @lean_turkey_mince_asda_ingredient_id,
    'Seed failed: ingredients_info @lean_turkey_mince_asda_ingredient_id could not be resolved'
);

-- #####################################
-- Maca (VivoLife)
-- #####################################
SELECT ingredient_id
INTO @maca_vivolife_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Maca (VivoLife)';

CALL validate_and_insert_into_seed_registry
(
    'maca_vivolife_ingredient_id',
    'ingredients_info',
    @maca_vivolife_ingredient_id,
    'Seed failed: ingredients_info @maca_vivolife_ingredient_id could not be resolved'
);

-- #####################################
-- Oranges
-- #####################################
SELECT ingredient_id
INTO @oranges_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Oranges';

CALL validate_and_insert_into_seed_registry
(
    'oranges_ingredient_id',
    'ingredients_info',
    @oranges_ingredient_id,
    'Seed failed: ingredients_info @oranges_ingredient_id could not be resolved'
);

-- #####################################
-- Pistachios
-- #####################################
SELECT ingredient_id
INTO @pistachios_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Pistachios';

CALL validate_and_insert_into_seed_registry
(
    'pistachios_ingredient_id',
    'ingredients_info',
    @pistachios_ingredient_id,
    'Seed failed: ingredients_info @pistachios_ingredient_id could not be resolved'
);

-- #####################################
-- Plantin
-- #####################################
SELECT ingredient_id
INTO @plantin_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Plantin';

CALL validate_and_insert_into_seed_registry
(
    'plantin_ingredient_id',
    'ingredients_info',
    @plantin_ingredient_id,
    'Seed failed: ingredients_info @plantin_ingredient_id could not be resolved'
);

-- #####################################
-- Pumpkin Seeds
-- #####################################
SELECT ingredient_id
INTO @pumpkin_seeds_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Pumpkin Seeds';

CALL validate_and_insert_into_seed_registry
(
    'pumpkin_seeds_ingredient_id',
    'ingredients_info',
    @pumpkin_seeds_ingredient_id,
    'Seed failed: ingredients_info @pumpkin_seeds_ingredient_id could not be resolved'
);

-- #####################################
-- Rice Dream Original Organic Milk
-- #####################################
SELECT ingredient_id
INTO @rice_dream_original_organic_milk_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Rice Dream Original Organic Milk';

CALL validate_and_insert_into_seed_registry
(
    'rice_dream_original_organic_milk_ingredient_id',
    'ingredients_info',
    @rice_dream_original_organic_milk_ingredient_id,
    'Seed failed: ingredients_info @rice_dream_original_organic_milk_ingredient_id could not be resolved'
);

-- #####################################
-- Rowse Organic Honey
-- #####################################
SELECT ingredient_id
INTO @rowse_organic_honey_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Rowse Organic Honey';

CALL validate_and_insert_into_seed_registry
(
    'rowse_organic_honey_ingredient_id',
    'ingredients_info',
    @rowse_organic_honey_ingredient_id,
    'Seed failed: ingredients_info @rowse_organic_honey_ingredient_id could not be resolved'
);

-- #####################################
-- Semi Skin Cow Milk
-- #####################################
SELECT ingredient_id
INTO @semi_skin_cow_milk_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Semi Skin Cow Milk';

CALL validate_and_insert_into_seed_registry
(
    'semi_skin_cow_milk_ingredient_id',
    'ingredients_info',
    @semi_skin_cow_milk_ingredient_id,
    'Seed failed: ingredients_info @semi_skin_cow_milk_ingredient_id could not be resolved'
);

-- #####################################
-- Slow-Release Casein Vanilla (MyProtein)
-- #####################################
SELECT ingredient_id
INTO @slow_release_casein_vanilla_myprotein_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Slow-Release Casein Vanilla (MyProtein)';

CALL validate_and_insert_into_seed_registry
(
    'slow_release_casein_vanilla_myprotein_ingredient_id',
    'ingredients_info',
    @slow_release_casein_vanilla_myprotein_ingredient_id,
    'Seed failed: ingredients_info @slow_release_casein_vanilla_myprotein_ingredient_id could not be resolved'
);

-- #####################################
-- Spinach
-- #####################################
SELECT ingredient_id
INTO @spinach_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Spinach';

CALL validate_and_insert_into_seed_registry
(
    'spinach_ingredient_id',
    'ingredients_info',
    @spinach_ingredient_id,
    'Seed failed: ingredients_info @spinach_ingredient_id could not be resolved'
);

-- #####################################
-- Sunflower Seeds
-- #####################################
SELECT ingredient_id
INTO @sunflower_seeds_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Sunflower Seeds';

CALL validate_and_insert_into_seed_registry
(
    'sunflower_seeds_ingredient_id',
    'ingredients_info',
    @sunflower_seeds_ingredient_id,
    'Seed failed: ingredients_info @sunflower_seeds_ingredient_id could not be resolved'
);

-- #####################################
-- Sweet Potato
-- #####################################
SELECT ingredient_id
INTO @sweet_potato_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Sweet Potato';

CALL validate_and_insert_into_seed_registry
(
    'sweet_potato_ingredient_id',
    'ingredients_info',
    @sweet_potato_ingredient_id,
    'Seed failed: ingredients_info @sweet_potato_ingredient_id could not be resolved'
);

-- #####################################
-- Sweetcorn
-- #####################################
SELECT ingredient_id
INTO @sweetcorn_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Sweetcorn';

CALL validate_and_insert_into_seed_registry
(
    'sweetcorn_ingredient_id',
    'ingredients_info',
    @sweetcorn_ingredient_id,
    'Seed failed: ingredients_info @sweetcorn_ingredient_id could not be resolved'
);

-- #####################################
-- Thrive (VivoLife)
-- #####################################
SELECT ingredient_id
INTO @thrive_vivolife_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Thrive (VivoLife)';

CALL validate_and_insert_into_seed_registry
(
    'thrive_vivolife_ingredient_id',
    'ingredients_info',
    @thrive_vivolife_ingredient_id,
    'Seed failed: ingredients_info @thrive_vivolife_ingredient_id could not be resolved'
);

-- #####################################
-- Water
-- #####################################
SELECT ingredient_id
INTO @water_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Water';

CALL validate_and_insert_into_seed_registry
(
    'water_ingredient_id',
    'ingredients_info',
    @water_ingredient_id,
    'Seed failed: ingredients_info @water_ingredient_id could not be resolved'
);

-- #####################################
-- Whey Isolate Chocolate Smooth (MyProtein)
-- #####################################
SELECT ingredient_id
INTO @whey_isolate_chocolate_smooth_myprotein_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Whey Isolate Chocolate Smooth (MyProtein)';

CALL validate_and_insert_into_seed_registry
(
    'whey_isolate_chocolate_smooth_myprotein_ingredient_id',
    'ingredients_info',
    @whey_isolate_chocolate_smooth_myprotein_ingredient_id,
    'Seed failed: ingredients_info @whey_isolate_chocolate_smooth_myprotein_ingredient_id could not be resolved'
);

-- #####################################
-- Whey Isolate Natural Strawberry (MyProtein)
-- #####################################
SELECT ingredient_id
INTO @whey_isolate_natural_strawberry_myprotein_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Whey Isolate Natural Strawberry (MyProtein)';

CALL validate_and_insert_into_seed_registry
(
    'whey_isolate_natural_strawberry_myprotein_ingredient_id',
    'ingredients_info',
    @whey_isolate_natural_strawberry_myprotein_ingredient_id,
    'Seed failed: ingredients_info @whey_isolate_natural_strawberry_myprotein_ingredient_id could not be resolved'
);

-- #####################################
-- Wholemeal Wraps (Tesco)
-- #####################################
SELECT ingredient_id
INTO @wholemeal_wraps_tesco_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Wholemeal Wraps (Tesco)';

CALL validate_and_insert_into_seed_registry
(
    'wholemeal_wraps_tesco_ingredient_id',
    'ingredients_info',
    @wholemeal_wraps_tesco_ingredient_id,
    'Seed failed: ingredients_info @wholemeal_wraps_tesco_ingredient_id could not be resolved'
);

-- #####################################
-- Wholewheat Spaghetti (ASDA)
-- #####################################
SELECT ingredient_id
INTO @wholewheat_spaghetti_asda_ingredient_id
FROM ingredients_info
WHERE ingredient_name = 'Wholewheat Spaghetti (ASDA)';

CALL validate_and_insert_into_seed_registry
(
    'wholewheat_spaghetti_asda_ingredient_id',
    'ingredients_info',
    @wholewheat_spaghetti_asda_ingredient_id,
    'Seed failed: ingredients_info @wholewheat_spaghetti_asda_ingredient_id could not be resolved'
);

