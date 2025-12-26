INSERT INTO ingredients_in_sections_of_meal_versions 
(
	div_meal_sections_version_id, 
	
	ingredients_index, 
	ingredients_index_version_id, 
	
	pdid, 
	
	ingredient_id, 
	quantity
	
) VALUES

-- #####################################################
-- # Breakfast
-- #####################################################

-- # Breakfast Part 1 (Pancakes)
(1, 1, 1, NULL, 44, 3),  -- EGGS
(1, 2, 2, NULL, 47, 110), -- Flour
(1, 3, 3, NULL, 48, 28), -- Butter
(1, 4, 4, NULL, 51, 28), -- Sugar
(1, 5, 5, NULL, 40, 140), -- Milk
(1, 6, 6, NULL, 45, 110), -- Yoghurt


-- # Breakfast Part 2 (Eggs + Plantain)
(2, 7, 7,  NULL, 44, 3), -- EGGS
(2, 8, 8,  NULL, 40, 40), -- Milk
(2, 9, 9,  NULL, 50, 120), -- Plantain

-- # Breakfast Part 3 (Celery Drink)
(3, 10, 10, NULL, 11, 110), -- Celery
(3, 11, 11, NULL, 2, 500), -- Water

-- #####################################################
-- # Mid-Morning
-- #####################################################

-- # Oatmeal Part 4
(4, 12, 12, NULL, 26, 110), -- Oatmeal
(4, 13, 13, NULL, 28, 35), -- Nut Powder
(4, 14, 14, NULL, 40, 450), -- Milk
(4, 15, 15, NULL,27, 16), -- Honey

-- # Bananas Part 5 
(5, 16, 16, NULL, 3, 200 ), -- Bananas

-- # Mango & Pineapple Smoothie Part 6 
(6, 17, 17, NULL, 12, 110), -- Mango
(6, 18, 18, NULL, 13, 110), -- Pineapple
(6, 19, 19, NULL, 2,  500), -- Water

-- #####################################################
-- # Lunch
-- #####################################################

-- # Lunch  Part 7
(7, 20, 20, NULL, 4, 2),    -- Wraps
(7, 21, 21, NULL, 22, 150), -- Chicken Mince
(7, 22, 22, NULL, 6,60),    -- BBQ Sauce


-- # Nuts  Part 8
(8, 23, 23, NULL, 19, 7),  -- Sunflower Seeeds
(8, 24, 24, NULL, 18, 7),  -- Pumpkin Seeds
(8, 25, 25, NULL, 20, 12), -- Pistachios

-- # Multivitamin Shake Part 9 
(9, 26, 26, NULL, 24 , 5), -- Maca
(9, 27, 27, NULL, 25, 30), -- Vivo Life
(9, 28, 28, NULL, 2, 500), -- Water

-- #####################################################
-- # Pre-Workout (null, 2, null, 4, , ), -- 
-- #####################################################

-- # Fruit Smoothie Part 10
(10, 29, 29, NULL, 17, 60),  -- Raspberry
(10, 30, 30, NULL, 16, 60),  -- Blueberries
(10, 31, 31, NULL, 15, 60),  -- Strawberry
(10, 32, 32, NULL, 42, 110), -- Grapefruit
(10, 33, 33, NULL, 14, 110), -- Oranges 
(10, 34, 34, NULL, 2, 1000), -- Water

-- #####################################################
-- # Post-Workout 
-- #####################################################

-- # Bananas Part 11
(11, 35, 35, NULL, 3, 200 ), -- Bananas

-- # Protein Shake Part 12
(12, 36, 36, NULL, 35, 35), -- Protein Powder
(12, 37, 37, NULL, 33, 5),  -- Creatine
(12, 38, 38, NULL, 40, 2),   -- Milk

-- # Veg Meal Part 13
(13, 39, 39, NULL, 10, 110),  -- Broccoli
(13, 40, 40, NULL, 9,  110 ), -- Carrots
(13, 41, 41, NULL, 8,  110 ), -- Sweet Potatoe
(13, 42, 42, NULL, 7, 110 ), -- Kale

-- #####################################################
-- # Dinner
-- #####################################################

-- # Pasta Meal Part 14
(14, 43, 43, NULL, 38, 60),    -- Dolmio Sauce
(14, 44, 44, NULL, 38, 75),  --  Pasta
(14, 45, 45, NULL, 23, 150), --  Turkey Mince
(14, 46, 46, NULL, 39, 60),  --  Sweetcorn

-- #####################################################
-- # Bed Snack
-- #####################################################

-- # Pasta Meal Part 15
(15, 47, 47, NULL, 36, 35),   -- Casein
(15, 48, 48, NULL, 40, 2 );   -- Milk
