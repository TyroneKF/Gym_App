
#######################################################################################
INSERT IGNORE INTO mealsInPlan (MealInPlanID, plan_id, Meal_Name, Meal_Time) VALUES
(1, 2, "Breakfast", '09:00:00'),         -- 1
(2, 2, "Mid-Morning Snack", '12:00:00'), -- 2
(3, 2, "Lunch", '15:00:00'),             -- 3
(4, 2, "Pre-Workout", '17:00:00'),       -- 4
(5, 2, "Post-Workout", '19:00:00'),      -- 5
(6, 2, "Dinner", '21:00:00'),            -- 6
(7, 2, "Bed Snack", '23:00:00');         -- 7

#######################################################################################

INSERT IGNORE INTO dividedMealSections (MealInPlanID, plan_id) VALUES

# Breakfast
(1, 2), -- Pancakes             -- 1
(1, 2), -- Eggs + Plantain      -- 2
(1, 2), -- Celery Drink         -- 3
                         
# MidMorning         
(2, 2), -- Oatmeal              -- 4    
(2, 2), -- Bananas              -- 5
(2, 2),  -- Mango Smoothie      -- 6

# Lunch
(3, 2), -- Lunch                -- 7 
(3, 2), -- Nuts                 -- 8 
(3, 2), -- Multivitamin Shake   -- 9 

# Pre-Workout
(4, 2), -- Fruit Smoothie       -- 10 

# Post - Workout
(5, 2), -- Bananas              -- 11 
(5, 2), -- Protein Shake        -- 12 
(5, 2), -- Veg Meal             -- 13 

# Dinner
(6, 2), -- Pasta Meal           -- 14 

# Bed Snack
(7, 2); -- Protein Shake        -- 15 

#######################################################################################

INSERT IGNORE INTO ingredients_in_sections_of_meal 
(Ingredients_Index, plan_id, PDID, DivMealSectionsID, ingredient_id, Quantity) VALUES

#####################################################
# Breakfast
#####################################################

# Breakfast Part 1 (Pancakes)
(null, 2, null, 1, 44, 3),  -- EGGS
(null, 2, null, 1, 47, 110), -- Butter
(null, 2, null, 1, 48, 28), -- Flour
(null, 2, null, 1, 40, 140), -- Milk
(null, 2, null, 1, 45, 110), -- Yoghurt
(null, 2, null, 1, 51, 28), -- Sugar

# Breakfast Part 2 (Eggs + Plantain)
(null, 2, null, 2, 44, 3), -- EGGS
(null, 2, null, 1, 40, 40), -- Milk
(null, 2, null, 2, 50, 120), -- Plantain

# Breakfast Part 3 (Celery Drink)
(null, 2, null, 3, 11, 110), -- Celery
(null, 2, null, 3, 2, 500), -- Water

#####################################################
# Mid-Morning
#####################################################

# Oatmeal Part 4
(null, 2, null, 4, 26, 110), -- Oatmeal
(null, 2, null, 4, 28, 35), -- Nut Powder
(null, 2, null, 4, 40, 450), -- Milk
(null, 2, null, 4, 27, 16), -- Honey

# Bananas Part 5 
(null, 2, null, 5, 3, 200 ), -- Bananas

# Mango & Pineapple Smoothie Part 6 
(null, 2, null, 6, 12, 110), -- Mango
(null, 2, null, 6, 13, 110), -- Pineapple
(null, 2, null, 6, 2,  500), -- Water

#####################################################
# Lunch
#####################################################

# Lunch  Part 7
(null, 2, null, 7, 4, 2),    -- Wraps
(null, 2, null, 7, 22, 150), -- Chicken Mince
(null, 2, null, 7, 6,60),    -- BBQ Sauce


# Nuts  Part 8
(null, 2, null, 8, 19, 7),  -- Sunflower Seeeds
(null, 2, null, 8, 18, 7),  -- Pumpkin Seeds
(null, 2, null, 8, 20, 12), -- Pistachios

# Multivitamin Shake Part 9 
(null, 2, null, 9, 24 , 5), -- Maca
(null, 2, null, 9, 25, 30), -- Vivo Life
(null, 2, null, 9, 2, 500), -- Water

#####################################################
# Pre-Workout (null, 2, null, 4, , ), -- 
#####################################################

# Fruit Smoothie Part 10
(null, 2, null, 10, 17, 60),  -- Raspberry
(null, 2, null, 10, 16, 60),  -- Blueberries
(null, 2, null, 10, 15, 60),  -- Strawberry
(null, 2, null, 10, 42, 110), -- Grapefruit
(null, 2, null, 10, 14, 110), -- Oranges 
(null, 2, null, 10, 2, 1000), -- Water

#####################################################
# Post-Workout 
#####################################################

# Bananas Part 11
(null, 2, null, 11, 3, 200 ), -- Bananas

# Protein Shake Part 11
(null, 2, null, 12, 35, 35), -- Protein Powder
(null, 2, null, 12, 33, 5),  -- Creatine
(null, 2, null, 12, 40, 2 ),   -- Milk

# Veg Meal Part 13
(null, 2, null, 13, 10, 110),  -- Broccoli
(null, 2, null, 13, 9, 110 ), -- Carrots
(null, 2, null, 13, 8, 110 ), -- Sweet Potatoe
(null, 2, null, 13, 7, 110 ), -- Kale

#####################################################
# Dinner
#####################################################

# Pasta Meal Part 14
(null, 2, null, 14, 38,60),    -- Dolmio Sauce
(null, 2, null, 14, 38, 75),  --  Pasta
(null, 2, null, 14, 23, 150), --  Turkey Mince
(null, 2, null, 14, 39, 60),  --  Sweetcorn

#####################################################
# Bed Snack
#####################################################

# Pasta Meal Part 15
(null, 2, null, 15, 36, 35),   -- Casein
(null, 2, null, 15, 40, 2 );   -- Milk

