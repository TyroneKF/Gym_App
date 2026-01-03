-- #################################################################################################
-- Set Variables | Meals & Sub-Meals
-- ################################################################################################
/*

*/

-- ####################################################
-- Breakfast Sub-Meal
-- ####################################################
SET @pancakes_breakfast_div_vs_id := get_seed_id_by_key
(
    'pancakes_breakfast_div_vs_id',
    'Seed failed: divided_meal_sections_versions  @pancakes_breakfast_div_vs_id could not be resolved'
);

SET @eggs_breakfast_div_vs_id := get_seed_id_by_key
(
    'eggs_breakfast_div_vs_id',
    'Seed failed: divided_meal_sections_versions  @eggs_breakfast_div_vs_id could not be resolved'
);

SET @celery_breakfast_div_vs_id := get_seed_id_by_key
(
    'celery_breakfast_div_vs_id',
    'Seed failed: divided_meal_sections_versions  @celery_breakfast_div_vs_id could not be resolved'
);

-- ####################################################
-- Mid-Morning Snack - Sub-Meals
-- ####################################################

SET @oatmeal_mid_morning_div_vs_id := get_seed_id_by_key
(
    'oatmeal_mid_morning_div_vs_id',
    'Seed failed: divided_meal_sections_versions  @oatmeal_mid_morning_div_vs_id could not be resolved'
);

SET @bananas_mid_morning_div_vs_id := get_seed_id_by_key
(
    'bananas_mid_morning_div_vs_id',
    'Seed failed: divided_meal_sections_versions  @bananas_mid_morning_div_vs_id could not be resolved'
);

SET @mango_smoothie_mid_morning_div_vs_id := get_seed_id_by_key
(
    'mango_smoothie_mid_morning_div_vs_id',
    'Seed failed: divided_meal_sections_versions @mango_smoothie_mid_morning_div_vs_id could not be resolved'
);


-- ####################################################
-- Lunch Meal - Sub-Meals
-- ####################################################

SET @lunch_meal_lunch_div_vs_id := get_seed_id_by_key
(
    'pancakes_breakfast_div_vs_id',
    'Seed failed: divided_meal_sections_versions  @div_meal_sections_version_id could not be resolved'
);

SET @nuts_lunch_div_vs_id := get_seed_id_by_key
(
    'nuts_lunch_div_vs_id',
    'Seed failed: divided_meal_sections_versions  @nuts_lunch_div_vs_id could not be resolved'
);

SET @multivitamin_shake_lunch_div_vs_id := get_seed_id_by_key
(
    'multivitamin_shake_lunch_div_vs_id',
    'Seed failed: divided_meal_sections_versions  @multivitamin_shake_lunch_div_vs_id could not be resolved'
);

-- ####################################################
-- Pre-Workout Meal  - Sub-Meals
-- ####################################################
SET @fruit_smoothie_pre_workout_div_vs_id := get_seed_id_by_key
(
    'fruit_smoothie_pre_workout_div_vs_id',
    'Seed failed: divided_meal_sections_versions  @fruit_smoothie_pre_workout_div_vs_id could not be resolved'
);

-- ####################################################
-- Post-Workout Meal  - Sub-Meals
-- ####################################################
SET @bananas_post_workout_div_vs_id := get_seed_id_by_key
(
    'bananas_post_workout_div_vs_id',
    'Seed failed: divided_meal_sections_versions  @bananas_post_workout_div_vs_id could not be resolved'
);

SET @protein_shake_post_workout_div_vs_id := get_seed_id_by_key
(
    'protein_shake_post_workout_div_vs_id',
    'Seed failed: divided_meal_sections_versions  @protein_shake_post_workout_div_vs_id could not be resolved'
);

SET @veg_meal_post_workout_div_vs_id := get_seed_id_by_key
(
    'veg_meal_post_workout_div_vs_id',
    'Seed failed: divided_meal_sections_versions  @veg_meal_post_workout_div_vs_id could not be resolved'
);

-- ####################################################
-- Dinner Meal  - Sub-Meals
-- ####################################################
SET @dinner_div_vs_id := get_seed_id_by_key
(
    'dinner_div_vs_id',
    'Seed failed: divided_meal_sections_versions  @dinner_div_vs_id could not be resolved'
);

-- ####################################################
-- Bedtime Meal  - Sub-Meals
-- ####################################################
SET @protein_shake_bedtime_vs_id := get_seed_id_by_key
(
    'protein_shake_bedtime_vs_id',
    'Seed failed: divided_meal_sections_versions  @protein_shake_bedtime_vs_id could not be resolved'
);

-- #################################################################################################
-- Set Ingredients Variables (Alphabetical Order)
-- #################################################################################################

-- Alpro Organic Soya
SET @alpro_organic_soya_ingredient_id := get_seed_id_by_key
(
    'alpro_organic_soya_ingredient_id',
    'Seed failed: ingredients_info  @alpro_organic_soya_ingredient_id could not be resolved'
);

-- Alpro Soya Light Long Life Drink
SET @alpro_soya_light_long_life_drink_ingredient_id := get_seed_id_by_key
(
    'alpro_soya_light_long_life_drink_ingredient_id',
    'Seed failed: ingredients_info  @alpro_soya_light_long_life_drink_ingredient_id could not be resolved'
);

-- ASDA Scottish Porridge Oats
SET @asda_scottish_porridge_oats_ingredient_id := get_seed_id_by_key
(
    'asda_scottish_porridge_oats_ingredient_id',
    'Seed failed: ingredients_info  @asda_scottish_porridge_oats_ingredient_id could not be resolved'
);

-- Banana
SET @banana_ingredient_id := get_seed_id_by_key
(
    'banana_ingredient_id',
    'Seed failed: ingredients_info  @banana_ingredient_id could not be resolved'
);

-- Be-Ro Plain Flour
SET @be_ro_plain_flour_ingredient_id := get_seed_id_by_key
(
    'be_ro_plain_flour_ingredient_id',
    'Seed failed: ingredients_info  @be_ro_plain_flour_ingredient_id could not be resolved'
);

-- Bertolli Olive Oil Spread
SET @bertolli_olive_oil_spread_ingredient_id := get_seed_id_by_key
(
    'bertolli_olive_oil_spread_ingredient_id',
    'Seed failed: ingredients_info  @bertolli_olive_oil_spread_ingredient_id could not be resolved'
);

-- Billingtons Brown Sugar
SET @billingtons_brown_sugar_ingredient_id := get_seed_id_by_key
(
    'billingtons_brown_sugar_ingredient_id',
    'Seed failed: ingredients_info  @billingtons_brown_sugar_ingredient_id could not be resolved'
);

-- Black Beans
SET @black_beans_ingredient_id := get_seed_id_by_key
(
    'black_beans_ingredient_id',
    'Seed failed: ingredients_info  @black_beans_ingredient_id could not be resolved'
);

-- Brocolli
SET @broccoli_ingredient_id := get_seed_id_by_key
(
    'broccoli_ingredient_id',
    'Seed failed: ingredients_info  @broccoli_ingredient_id could not be resolved'
);

-- Carrots
SET @carrots_ingredient_id := get_seed_id_by_key
(
    'carrots_ingredient_id',
    'Seed failed: ingredients_info  @carrots_ingredient_id could not be resolved'
);

-- Cashew Nuts
SET @cashew_nuts_ingredient_id := get_seed_id_by_key
(
    'cashew_nuts_ingredient_id',
    'Seed failed: ingredients_info  @cashew_nuts_ingredient_id could not be resolved'
);

-- Celery
SET @celery_ingredient_id := get_seed_id_by_key
(
    'celery_ingredient_id',
    'Seed failed: ingredients_info  @celery_ingredient_id could not be resolved'
);

-- Chicken Mince (ASDA)
SET @chicken_mince_asda_ingredient_id := get_seed_id_by_key
(
    'chicken_mince_asda_ingredient_id',
    'Seed failed: ingredients_info  @chicken_mince_asda_ingredient_id could not be resolved'
);

-- Corned Beef (Princess Reduced Fat)
SET @corned_beef_princess_reduced_fat_ingredient_id := get_seed_id_by_key
(
    'corned_beef_princess_reduced_fat_ingredient_id',
    'Seed failed: ingredients_info  @corned_beef_princess_reduced_fat_ingredient_id could not be resolved'
);

-- Creatine Monohydrate (MyProtein)
SET @creatine_monohydrate_myprotein_ingredient_id := get_seed_id_by_key
(
    'creatine_monohydrate_myprotein_ingredient_id',
    'Seed failed: ingredients_info  @creatine_monohydrate_myprotein_ingredient_id could not be resolved'
);

-- Dolmio Bolognese Pasta Sauce Low Fat
SET @dolmio_bolognese_pasta_sauce_low_fat_ingredient_id := get_seed_id_by_key
(
    'dolmio_bolognese_pasta_sauce_low_fat_ingredient_id',
    'Seed failed: ingredients_info  @dolmio_bolognese_pasta_sauce_low_fat_ingredient_id could not be resolved'
);

-- Fage Greek Yoghurt
SET @fage_greek_yoghurt_ingredient_id := get_seed_id_by_key
(
    'fage_greek_yoghurt_ingredient_id',
    'Seed failed: ingredients_info  @fage_greek_yoghurt_ingredient_id could not be resolved'
);

-- Frozen Blueberries (ASDA)
SET @frozen_blueberries_asda_ingredient_id := get_seed_id_by_key
(
    'frozen_blueberries_asda_ingredient_id',
    'Seed failed: ingredients_info  @frozen_blueberries_asda_ingredient_id could not be resolved'
);

-- Frozen Mangoes (ASDA)
SET @frozen_mangoes_asda_ingredient_id := get_seed_id_by_key
(
    'frozen_mangoes_asda_ingredient_id',
    'Seed failed: ingredients_info  @frozen_mangoes_asda_ingredient_id could not be resolved'
);

-- Frozen Pineapple (ASDA)
SET @frozen_pineapple_asda_ingredient_id := get_seed_id_by_key
(
    'frozen_pineapple_asda_ingredient_id',
    'Seed failed: ingredients_info  @frozen_pineapple_asda_ingredient_id could not be resolved'
);

-- Frozen Raspberries (ASDA)
SET @frozen_raspberries_asda_ingredient_id := get_seed_id_by_key
(
    'frozen_raspberries_asda_ingredient_id',
    'Seed failed: ingredients_info  @frozen_raspberries_asda_ingredient_id could not be resolved'
);

-- Frozen Strawberries (ASDA)
SET @frozen_strawberries_asda_ingredient_id := get_seed_id_by_key
(
    'frozen_strawberries_asda_ingredient_id',
    'Seed failed: ingredients_info  @frozen_strawberries_asda_ingredient_id could not be resolved'
);

-- Grapefruit
SET @grapefruit_ingredient_id := get_seed_id_by_key
(
    'grapefruit_ingredient_id',
    'Seed failed: ingredients_info  @grapefruit_ingredient_id could not be resolved'
);

-- Heinz Classic Barbecue Sauce
SET @heinz_classic_barbecue_sauce_ingredient_id := get_seed_id_by_key
(
    'heinz_classic_barbecue_sauce_ingredient_id',
    'Seed failed: ingredients_info  @heinz_classic_barbecue_sauce_ingredient_id could not be resolved'
);

-- Kale
SET @kale_ingredient_id := get_seed_id_by_key
(
    'kale_ingredient_id',
    'Seed failed: ingredients_info  @kale_ingredient_id could not be resolved'
);

-- Large Eggs (60g)
SET @large_eggs_60g_ingredient_id := get_seed_id_by_key
(
    'large_eggs_60g_ingredient_id',
    'Seed failed: ingredients_info  @large_eggs_60g_ingredient_id could not be resolved'
);

-- Lean Turkey Mince (ASDA)
SET @lean_turkey_mince_asda_ingredient_id := get_seed_id_by_key
(
    'lean_turkey_mince_asda_ingredient_id',
    'Seed failed: ingredients_info  @lean_turkey_mince_asda_ingredient_id could not be resolved'
);

-- Maca (VivoLife)
SET @maca_vivolife_ingredient_id := get_seed_id_by_key
(
    'maca_vivolife_ingredient_id',
    'Seed failed: ingredients_info  @maca_vivolife_ingredient_id could not be resolved'
);

-- Oranges
SET @oranges_ingredient_id := get_seed_id_by_key
(
    'oranges_ingredient_id',
    'Seed failed: ingredients_info  @oranges_ingredient_id could not be resolved'
);

-- Pistachios
SET @pistachios_ingredient_id := get_seed_id_by_key
(
    'pistachios_ingredient_id',
    'Seed failed: ingredients_info  @pistachios_ingredient_id could not be resolved'
);

-- Plantin
SET @plantin_ingredient_id := get_seed_id_by_key
(
    'plantin_ingredient_id',
    'Seed failed: ingredients_info  @plantin_ingredient_id could not be resolved'
);

-- Pumpkin Seeds
SET @pumpkin_seeds_ingredient_id := get_seed_id_by_key
(
    'pumpkin_seeds_ingredient_id',
    'Seed failed: ingredients_info  @pumpkin_seeds_ingredient_id could not be resolved'
);

-- Rice Dream Original Organic Milk
SET @rice_dream_original_organic_milk_ingredient_id := get_seed_id_by_key
(
    'rice_dream_original_organic_milk_ingredient_id',
    'Seed failed: ingredients_info  @rice_dream_original_organic_milk_ingredient_id could not be resolved'
);

-- Rowse Organic Honey
SET @rowse_organic_honey_ingredient_id := get_seed_id_by_key
(
    'rowse_organic_honey_ingredient_id',
    'Seed failed: ingredients_info  @rowse_organic_honey_ingredient_id could not be resolved'
);

-- Semi Skin Cow Milk
SET @semi_skin_cow_milk_ingredient_id := get_seed_id_by_key
(
    'semi_skin_cow_milk_ingredient_id',
    'Seed failed: ingredients_info  @semi_skin_cow_milk_ingredient_id could not be resolved'
);

-- Slow-Release Casein Vanilla (MyProtein)
SET @slow_release_casein_vanilla_myprotein_ingredient_id := get_seed_id_by_key
(
    'slow_release_casein_vanilla_myprotein_ingredient_id',
    'Seed failed: ingredients_info  @slow_release_casein_vanilla_myprotein_ingredient_id could not be resolved'
);

-- Spinach
SET @spinach_ingredient_id := get_seed_id_by_key
(
    'spinach_ingredient_id',
    'Seed failed: ingredients_info  @spinach_ingredient_id could not be resolved'
);

-- Sunflower Seeds
SET @sunflower_seeds_ingredient_id := get_seed_id_by_key
(
    'sunflower_seeds_ingredient_id',
    'Seed failed: ingredients_info  @sunflower_seeds_ingredient_id could not be resolved'
);

-- Sweet Potatoe
SET @sweet_potato_ingredient_id := get_seed_id_by_key
(
    'sweet_potato_ingredient_id',
    'Seed failed: ingredients_info  @sweet_potato_ingredient_id could not be resolved'
);

-- Sweetcorn
SET @sweetcorn_ingredient_id := get_seed_id_by_key
(
    'sweetcorn_ingredient_id',
    'Seed failed: ingredients_info  @sweetcorn_ingredient_id could not be resolved'
);

-- Thrive (VivoLife)
SET @thrive_vivolife_ingredient_id := get_seed_id_by_key
(
    'thrive_vivolife_ingredient_id',
    'Seed failed: ingredients_info  @thrive_vivolife_ingredient_id could not be resolved'
);

-- Water
SET @water_ingredient_id := get_seed_id_by_key
(
    'water_ingredient_id',
    'Seed failed: ingredients_info  @water_ingredient_id could not be resolved'
);

-- Whey Isolate Chocolate Smooth (MyProtein)
SET @whey_isolate_chocolate_smooth_myprotein_ingredient_id := get_seed_id_by_key
(
    'whey_isolate_chocolate_smooth_myprotein_ingredient_id',
    'Seed failed: ingredients_info  @whey_isolate_chocolate_smooth_myprotein_ingredient_id could not be resolved'
);

-- Whey Isolate Natural Strawberry (MyProtein)
SET @whey_isolate_natural_strawberry_myprotein_ingredient_id := get_seed_id_by_key
(
    'whey_isolate_natural_strawberry_myprotein_ingredient_id',
    'Seed failed: ingredients_info  @whey_isolate_natural_strawberry_myprotein_ingredient_id could not be resolved'
);

-- Wholemeal Wraps (Tesco)
SET @wholemeal_wraps_tesco_ingredient_id := get_seed_id_by_key
(
    'wholemeal_wraps_tesco_ingredient_id',
    'Seed failed: ingredients_info  @wholemeal_wraps_tesco_ingredient_id could not be resolved'
);

-- Wholewheat Spaghetti (ASDA)
SET @wholewheat_spaghetti_asda_ingredient_id := get_seed_id_by_key
(
    'wholewheat_spaghetti_asda_ingredient_id',
    'Seed failed: ingredients_info  @wholewheat_spaghetti_asda_ingredient_id could not be resolved'
);



-- #################################################################################################
-- Insert Ingredients Into Meals
-- #################################################################################################

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
(@pancakes_breakfast_div_vs_id,  @large_eggs_60g_ingredient_id, 3),  -- EGGS
(@pancakes_breakfast_div_vs_id,  @be_ro_plain_flour_ingredient_id, 110), -- Flour
-- (@pancakes_breakfast_div_vs_id,  , 28), -- Butter
(@pancakes_breakfast_div_vs_id,  @billingtons_brown_sugar_ingredient_id, 28), -- Sugar
(@pancakes_breakfast_div_vs_id,  @alpro_soya_light_long_life_drink_ingredient_id, 140), -- Milk
(@pancakes_breakfast_div_vs_id,  @fage_greek_yoghurt_ingredient_id, 110), -- Yoghurt


-- # Breakfast Part 2 (Eggs + Plantain)
(@eggs_breakfast_div_vs_id,   @large_eggs_60g_ingredient_id, 3), -- EGGS
(@eggs_breakfast_div_vs_id,   @alpro_soya_light_long_life_drink_ingredient_id, 40), -- Milk
(@eggs_breakfast_div_vs_id,   @plantin_ingredient_id, 120), -- Plantain

-- # Breakfast Part 3 (Celery Drink)
(@celery_breakfast_div_vs_id,  @celery_ingredient_id, 110), -- Celery
(@celery_breakfast_div_vs_id,  @water_ingredient_id, 500), -- Water

-- #####################################################
-- # Mid-Morning
-- #####################################################

-- # Oatmeal Part 4
(@oatmeal_mid_morning_div_vs_id,  @asda_scottish_porridge_oats_ingredient_id, 110), -- Oatmeal
-- (@oatmeal_mid_morning_div_vs_id,  p, 35), -- Nut Powder
(@oatmeal_mid_morning_div_vs_id, @alpro_soya_light_long_life_drink_ingredient_id, 450), -- Milk
(@oatmeal_mid_morning_div_vs_id, @rowse_organic_honey_ingredient_id, 16), -- Honey

-- # Bananas Part 5
(@bananas_mid_morning_div_vs_id,  @banana_ingredient_id, 200 ), -- Bananas

-- # Mango & Pineapple Smoothie Part 6
(@mango_smoothie_mid_morning_div_vs_id,  @frozen_mangoes_asda_ingredient_id, 110), -- Mango
(@mango_smoothie_mid_morning_div_vs_id,  @frozen_pineapple_asda_ingredient_id, 110), -- Pineapple
(@mango_smoothie_mid_morning_div_vs_id,  @water_ingredient_id,  500), -- Water

-- #####################################################
-- # Lunch
-- #####################################################

-- # Lunch  Part 7
(@lunch_meal_lunch_div_vs_id,  @wholemeal_wraps_tesco_ingredient_id, 2),    -- Wraps
(@lunch_meal_lunch_div_vs_id,  @chicken_mince_asda_ingredient_id, 150), -- Chicken Mince
-- (@lunch_meal_lunch_div_vs_id,  @ ,60),    -- BBQ Sauce


-- # Nuts  Part 8
(@nuts_lunch_div_vs_id,  @sunflower_seeds_ingredient_id, 7),  -- Sunflower Seeds
(@nuts_lunch_div_vs_id,   @pumpkin_seeds_ingredient_id, 7),  -- Pumpkin Seeds
(@nuts_lunch_div_vs_id,   @pistachios_ingredient_id, 12), -- Pistachios

-- # Multivitamin Shake Part 9
(@multivitamin_shake_lunch_div_vs_id,  @maca_vivolife_ingredient_id , 5), -- Maca
(@multivitamin_shake_lunch_div_vs_id,  @thrive_vivolife_ingredient_id, 3), -- Vivo Life
(@multivitamin_shake_lunch_div_vs_id,  @water_ingredient_id, 500), -- Water

-- #####################################################
-- # Pre-Workout (null, 2, null, 4, , ), --
-- #####################################################

-- # Fruit Smoothie Part 10
(@fruit_smoothie_pre_workout_div_vs_id,  @frozen_raspberries_asda_ingredient_id, 60),  -- Raspberry
(@fruit_smoothie_pre_workout_div_vs_id,  @frozen_blueberries_asda_ingredient_id, 60),  -- Blueberries
(@fruit_smoothie_pre_workout_div_vs_id,  @frozen_strawberries_asda_ingredient_id, 60),  -- Strawberry
(@fruit_smoothie_pre_workout_div_vs_id,  @grapefruit_ingredient_id, 110), -- Grapefruit
(@fruit_smoothie_pre_workout_div_vs_id,  @oranges_ingredient_id, 110), -- Oranges
(@fruit_smoothie_pre_workout_div_vs_id,  @water_ingredient_id, 1000), -- Water

-- #####################################################
-- # Post-Workout
-- #####################################################

-- # Bananas Part 11
(@bananas_post_workout_div_vs_id,  @banana_ingredient_id, 200 ), -- Bananas

-- # Protein Shake Part 12
(@protein_shake_post_workout_div_vs_id,  @whey_isolate_natural_strawberry_myprotein_ingredient_id, 35), -- Protein Powder
(@protein_shake_post_workout_div_vs_id,  @creatine_monohydrate_myprotein_ingredient_id, 5),  -- Creatine
(@protein_shake_post_workout_div_vs_id,  @alpro_soya_light_long_life_drink_ingredient_id, 450),   -- Milk

-- # Veg Meal Part 13
(@veg_meal_post_workout_div_vs_id,  @broccoli_ingredient_id, 110),  -- Broccoli
(@veg_meal_post_workout_div_vs_id,  @carrots_ingredient_id,  110 ), -- Carrots
(@veg_meal_post_workout_div_vs_id,  @sweet_potato_ingredient_id,  110 ), -- Sweet Potato
(@veg_meal_post_workout_div_vs_id,  @kale_ingredient_id, 110 ), -- Kale

-- #####################################################
-- # Dinner
-- #####################################################

-- # Pasta Meal Part 14
(@dinner_div_vs_id,  @dolmio_bolognese_pasta_sauce_low_fat_ingredient_id, 60),    -- Dolmio Sauce
(@dinner_div_vs_id,  @wholewheat_spaghetti_asda_ingredient_id, 75),  --  Pasta
(@dinner_div_vs_id,  @lean_turkey_mince_asda_ingredient_id, 150), --  Turkey Mince
(@dinner_div_vs_id,  @sweetcorn_ingredient_id, 60),  --  Sweetcorn

-- #####################################################
-- # Bed Snack
-- #####################################################

-- # Protein Shake Part 15
(@protein_shake_bedtime_vs_id,  @slow_release_casein_vanilla_myprotein_ingredient_id, 35),   -- Casein
(@protein_shake_bedtime_vs_id,  @alpro_organic_soya_ingredient_id, 450 );   -- Milk

