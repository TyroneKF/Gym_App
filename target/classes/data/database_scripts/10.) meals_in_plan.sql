
-- #######################################################################################
INSERT INTO meals_in_plan
(	
	meal_in_plan_id, 
	date_time_of_creation
	
) VALUES

(1, now()), -- 1  # Breakfast ID
(2, now()), -- 2  # MidMorning ID  
(3, now()), -- 3  # Lunch ID
(4, now()), -- 4  # Pre-Workout ID
(5, now()), -- 5  # Post - Workout ID
(6, now()), -- 6  # Dinner ID
(7, now()); -- 7  # Bed Snack ID


INSERT INTO meals_in_plan_versions
(
	date_time_last_edit,
	meal_in_plan_id, 
	meal_in_plan_version_id,
	
	plan_version_id,
	
	meal_name, 
	meal_time
	
) VALUES

(now(), 1, 1, 1,  "Breakfast", '09:00:00'),         -- 1  # Breakfast
(now(), 2, 2, 1,  "Mid-Morning Snack", '12:00:00'), -- 2  # MidMorning 
(now(), 3, 3, 1,  "Lunch", '15:00:00'),             -- 3  # Lunch
(now(), 4, 4, 1,  "Pre-Workout", '17:00:00'),       -- 4  # Pre-Workout
(now(), 5, 5, 1,  "Post-Workout", '19:00:00'),      -- 5  # Post - Workout
(now(), 6, 6, 1,  "Dinner", '21:00:00'),            -- 6  # Dinner
(now(), 7, 7, 1,  "Bed Snack", '23:00:00');         -- 7  # Bed Snack

-- #######################################################################################


INSERT INTO divided_meal_sections (div_meal_sections_id) VALUES

-- # Breakfast : 
(1),  -- 1  = Pancakes 
(2),  -- 2  = Eggs + Plantain
(3),  -- 3  = Celery Drink

-- # MidMorning :   
(4),  -- 4  = Oatmeal
(5),  -- 5  = Bananas
(6),  -- 6  = Mango Smoothie

--  # Lunch :   
(7),  -- 7  = Lunch Meal
(8),  -- 8  = Nuts
(9),  -- 9  = Multivitamin Shake

--  # Pre-Workout :   
(10), -- 10 = Fruit Smoothie

--  # Post-Workout :   
(11), -- 11 = Bananas
(12), -- 12 = Protein Shake
(13), -- 13 = Veg Meal

--  # Dinner :   
(14), -- 14 = Pasta Meal 

--  # Bed Snack
(15); -- 15 = Protein Shake


INSERT INTO divided_meal_sections_versions 
(
	meal_in_plan_version_id,
	
	div_meal_sections_id,	
	div_meal_sections_version_id,
	
	sub_meal_name
	
) VALUES

-- # Breakfast : 
(1, 1,  1,  "Pancakes "),  -- 1  = Pancakes 
(1, 2,  2,  "Eggs + Plantain"),  -- 2  = Eggs + Plantain
(1, 3,  3,  "Celery Drink"),  -- 3  = Celery Drink

-- # MidMorning :   
(2, 4,  4,  "Oatmeal"),  -- 4  = Oatmeal
(2, 5,  5,  "Bananas"),  -- 5  = Bananas
(2, 6,  6,  "Mango Smoothie"),  -- 6  = Mango Smoothie

--  # Lunch :   
(3, 7,  7,  "Lunch Meal"),  -- 7  = Lunch Meal
(3, 8,  8,  "Nuts"),  -- 8  = Nuts
(3, 9,  9,  "Multivitamin Shake"),  -- 9  = Multivitamin Shake

--  # Pre-Workout :   
(4, 10, 10, "Fruit Smoothie"), -- 10 = Fruit Smoothie

--  # Post-Workout :   
(5, 11, 11, "Bananas"), -- 11 = Bananas
(5, 12, 12, "Whey Protein Shake"), -- 12 = Protein Shake
(5, 13, 13, "Vegetable Meal"), -- 13 = Veg Meal

--  # Dinner :   
(6, 14, 14, "Pasta Meal"), -- 14 = Pasta Meal 

--  # Bed Snack
(7, 15, 15, "Casein Shake"); -- 15 = Protein Shake

-- #######################################################################################
INSERT INTO ingredients_in_sections_of_meal (ingredients_index)
VALUES
(DEFAULT),(DEFAULT),(DEFAULT),(DEFAULT),(DEFAULT),(DEFAULT),
(DEFAULT),(DEFAULT),(DEFAULT),(DEFAULT),(DEFAULT),(DEFAULT),
(DEFAULT),(DEFAULT),(DEFAULT),(DEFAULT),(DEFAULT),(DEFAULT),
(DEFAULT),(DEFAULT),(DEFAULT),(DEFAULT),(DEFAULT),(DEFAULT),
(DEFAULT),(DEFAULT),(DEFAULT),(DEFAULT),(DEFAULT),(DEFAULT),
(DEFAULT),(DEFAULT),(DEFAULT),(DEFAULT),(DEFAULT),(DEFAULT),
(DEFAULT),(DEFAULT),(DEFAULT),(DEFAULT),(DEFAULT),(DEFAULT),
(DEFAULT),(DEFAULT),(DEFAULT),(DEFAULT),(DEFAULT),(DEFAULT);

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

