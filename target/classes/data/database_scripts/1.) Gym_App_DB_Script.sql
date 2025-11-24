DROP DATABASE IF EXISTS gymapp00001;

-- ######################################
CREATE DATABASE gymapp00001 CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE gymapp00001;

-- ######################################
CREATE TABLE IF NOT EXISTS users
(
  user_id INT PRIMARY KEY AUTO_INCREMENT,
  user_name VARCHAR(100) NOT NULL,
  
  is_user_selected BOOLEAN NOT NULL DEFAULT FALSE,
  selected_user_flag BOOLEAN GENERATED ALWAYS AS (IF(is_user_selected, TRUE, NULL)) STORED,
  
  UNIQUE KEY no_repeat_user_names(user_name)
);

-- ######################################
CREATE TABLE IF NOT EXISTS plans
(
    plan_id INT PRIMARY KEY AUTO_INCREMENT,
    plan_name VARCHAR(100) NOT NULL,

	user_id INT NOT NULL,
	FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,

    is_selected_plan BOOLEAN NOT NULL DEFAULT FALSE,
    vegan BOOLEAN NOT NULL DEFAULT FALSE,

    -- Ensures a user can only have one active plan, by using this field as an (identifier)
    selected_plan_flag BOOLEAN GENERATED ALWAYS AS (IF(is_selected_plan, TRUE, NULL)) STORED,

    UNIQUE KEY no_multiple_active_plans_per_user (user_id, selected_plan_flag)
);

-- ######################################

CREATE TABLE IF NOT EXISTS macros_per_pound_and_limits
(   	
    plan_id INT NOT NULL,
    FOREIGN KEY (plan_id) REFERENCES plans(plan_id) ON DELETE CASCADE,

	date_time_of_creation DATETIME NOT NULL,

	current_weight_kg DECIMAL(5,2) NOT NULL,
	current_weight_in_pounds DECIMAL(5,2) NOT NULL,

	body_fat_percentage DECIMAL(5,2) NOT NULL,

	protein_per_pound DECIMAL(5,2) NOT NULL,
	carbohydrates_per_pound DECIMAL(5,2) NOT NULL,
	fibre DECIMAL(5,2) NOT NULL,
	
	fats_per_pound DECIMAL(5,2) NOT NULL,
	saturated_fat_limit DECIMAL(5,2) NOT NULL,
	
	salt_limit DECIMAL(5,2) NOT NULL,

    water_target DECIMAL(5,2) NOT NULL,
	liquid_target DECIMAL(5,2) NOT NULL,

	additional_calories DECIMAL(5,2) NOT NULL,

	PRIMARY KEY (plan_id, date_time_of_creation)
);

-- ######################################

DROP VIEW IF EXISTS plan_macro_target_calculations;
CREATE VIEW plan_macro_target_calculations AS

SELECT

P.plan_id,
P.plan_name,

C.date_time_of_creation,

IFNULL(C.expected_protein_grams, 0) AS expected_protein_grams,
IFNULL(C.expected_carbohydrates_grams, 0) AS expected_carbs_grams,
IFNULL(C.fibre, 0) AS expected_fibre_grams,
IFNULL(C.expected_fats_grams, 0) AS expected_fats_grams,
IFNULL(C.saturated_fat_limit, 0) AS saturated_fat_limit,
IFNULL(C.salt_limit, 0) AS salt_limit_grams,
IFNULL(C.water_target, 0) AS water_content_target,
IFNULL(C.liquid_target, 0) AS liquid_content_target,
IFNULL(C.calories_target, 0) AS calories_target,
IFNULL(C.additional_calories_target, 0) AS additional_calories_target

FROM
(
	SELECT  plan_id, plan_name FROM plans
)  P
LEFT JOIN
(
    SELECT

    plan_id,
    date_time_of_creation,
	ROUND(current_weight_in_pounds * protein_per_pound, 2) AS expected_protein_grams, -- returns null if 1 of the values are empty
	ROUND(current_weight_in_pounds * carbohydrates_per_pound, 2) AS expected_carbohydrates_grams,
	fibre,
	ROUND(current_weight_in_pounds * fats_per_pound, 2) AS expected_fats_grams,
	saturated_fat_limit,
    salt_limit,
    water_target,
	liquid_target,
	(
	    ROUND((current_weight_in_pounds * protein_per_pound) * 4, 2) +
		ROUND((current_weight_in_pounds * carbohydrates_per_pound) * 4, 2) +
		ROUND((current_weight_in_pounds * fats_per_pound) * 9, 2)
	) AS calories_target,
	(
	    ROUND((current_weight_in_pounds * protein_per_pound) * 4, 2) +
		ROUND((current_weight_in_pounds * carbohydrates_per_pound) * 4, 2) +
		ROUND((current_weight_in_pounds * fats_per_pound) * 9, 2) +
		additional_calories
	) AS additional_calories_target

	FROM macros_per_pound_and_limits
)  C
ON P.plan_id = C.plan_id;

-- ######################################

CREATE TABLE IF NOT EXISTS ingredient_types
(
    ingredient_type_id INT  PRIMARY KEY AUTO_INCREMENT,

	ingredient_type_name VARCHAR(100) NOT NULL,
	UNIQUE KEY no_repeat_ingredient_type_name (ingredient_type_name)
);

-- ######################################

CREATE TABLE IF NOT EXISTS measurements
 (
    -- PRIMARY KEYS
    measurement_id INT PRIMARY KEY AUTO_INCREMENT,

	unit_name VARCHAR(100) NOT NULL,
	unit_symbol VARCHAR(10) NOT NULL,
	measured_material_type ENUM('solids', 'liquids') NOT NULL,
	
	UNIQUE KEY no_repeat_unit_names(unit_name)
 );
 
-- ######################################

CREATE TABLE IF NOT EXISTS ingredients_info
 (
    -- PRIMARY KEYS
    ingredient_id INT  PRIMARY KEY AUTO_INCREMENT,
	
	measurement_id INT NOT NULL,
	FOREIGN KEY (measurement_id) REFERENCES measurements(measurement_id),
		
	ingredient_name VARCHAR(100) NOT NULL,
	
	ingredient_type_id  INT NOT NULL,
	FOREIGN KEY (ingredient_type_id) REFERENCES ingredient_types(ingredient_type_id) ON DELETE CASCADE,

	based_on_quantity DECIMAL(5,2) NOT NULL,
    glycemic_index INT NOT NULL,
	protein DECIMAL(5,2) NOT NULL,
	carbohydrates DECIMAL(5,2) NOT NULL,
	sugars_of_carbs DECIMAL(5,2) NOT NULL,
	fibre DECIMAL(5,2) NOT NULL,
	fat DECIMAL(5,2) NOT NULL,
	saturated_fat DECIMAL(5,2) NOT NULL,
	salt DECIMAL(5,2) NOT NULL,
	water_content DECIMAL(5,2) NOT NULL,
	liquid_content DECIMAL(5,2) NOT NULL,
	calories DECIMAL(5,2) NOT NULL,

	UNIQUE KEY no_repeat_ingredient_names(ingredient_name)
);

-- ######################################
CREATE TABLE IF NOT EXISTS stores
(
    -- PRIMARY KEYS
    store_id INT  PRIMARY KEY AUTO_INCREMENT,
	store_name VARCHAR(255) NOT NULL,
	UNIQUE KEY no_repeat_store_names (store_name)

);
-- ######################################

CREATE TABLE IF NOT EXISTS ingredient_in_shops
(
    -- PRIMARY KEY , UNIQUE To this Table
    pdid INT  PRIMARY KEY AUTO_INCREMENT,

    ingredient_id INT  NOT NULL,
	FOREIGN KEY (ingredient_id) REFERENCES ingredients_info(ingredient_id) ON DELETE CASCADE,

    product_name VARCHAR(100) NOT NULL,

	volume_per_unit DECIMAL(5,2) NOT NULL,
	cost_per_unit DECIMAL(5,2) NOT NULL,

	store_id INT NOT NULL,
	FOREIGN KEY (store_id) REFERENCES stores(store_id) ON DELETE CASCADE,

    UNIQUE KEY no_repeat_products_in_store(store_id, product_name)
);
-- ######################################

CREATE TABLE IF NOT EXISTS meals_in_plan
(
   meal_in_plan_id INT NOT NULL AUTO_INCREMENT,

   plan_id INT NOT NULL,
   FOREIGN KEY (plan_id) REFERENCES plans(plan_id) ON DELETE CASCADE,

   meal_name VARCHAR(100) NOT NULL,
   meal_time TIME NOT NULL,

   PRIMARY KEY(meal_in_plan_id, plan_id), -- meal_in_plan_id isn't unique enough because its duplicated in temp meal plan for temp data it becomes unique with plan_id
   UNIQUE KEY no_repeat_meal_times_in_plan(plan_id, meal_time), -- Only one meal can be at one time
   UNIQUE KEY no_repeat_meal_names_in_plan(plan_id, meal_name) -- can't have 2 of the same meal_names in a plan
);

-- ######################################

CREATE TABLE IF NOT EXISTS divided_meal_sections
(
   div_meal_sections_id INT AUTO_INCREMENT,

   meal_in_plan_id  INT  NOT NULL,
   plan_id INT NOT NULL,
   
   FOREIGN KEY (meal_in_plan_id, plan_id)
        REFERENCES meals_in_plan (meal_in_plan_id, plan_id)
        ON DELETE CASCADE,
		
   PRIMARY KEY(div_meal_sections_id, plan_id), -- div_meal_sections_id isn't unique enough because its duplicated in temp meal plan for temp data it becomes unique with plan_id
   UNIQUE KEY no_repeat_sub_meals_per_plan(div_meal_sections_id, plan_id) -- need to be able to uniquely identify each sub meal, allowing duplicating doesn't do this

);

-- ######################################
CREATE TABLE IF NOT EXISTS ingredients_in_sections_of_meal
(
    ingredients_index INT  AUTO_INCREMENT,

    div_meal_sections_id INT NOT NULL,
	plan_id INT NOT NULL,
	
	FOREIGN KEY (div_meal_sections_id, plan_id)
		REFERENCES divided_meal_sections(div_meal_sections_id, plan_id)
		ON DELETE CASCADE,

    ingredient_id INT NOT NULL,
	FOREIGN KEY (ingredient_id) REFERENCES ingredients_info(ingredient_id) ON DELETE CASCADE,

	quantity DECIMAL(15,2) NOT NULL,

	pdid INT NULL,
 	FOREIGN KEY (pdid) REFERENCES ingredient_in_shops(pdid), -- Needs to be manually removed when deleted as we don't want the whole row being deleted

	PRIMARY KEY (ingredients_index, plan_id),
	UNIQUE KEY no_repeat_records(ingredients_index, div_meal_sections_id, plan_id)
);

-- ######################################

DROP VIEW IF EXISTS ingredients_in_sections_of_meal_calculation;
CREATE VIEW ingredients_in_sections_of_meal_calculation AS

SELECT

i.plan_id, 
i.div_meal_sections_id,
i.ingredients_index,
i.ingredient_id, 

(SELECT t.ingredient_type_name FROM ingredient_types t WHERE t.ingredient_type_id = info.ingredient_type_id) AS ingredient_type,
info.ingredient_name,

i.quantity,

IFNULL(s.store_name,'N/A') AS  supplier,
IFNULL(p.product_name,'N/A') AS  product_name,

IFNULL(ROUND((i.quantity /p.volume_per_unit)*p.cost_per_unit,2),0) AS ingredient_cost,

IFNULL(info.glycemic_index, 0) AS gi,
IFNULL(ROUND((info.protein /info.based_on_quantity)*i.quantity,2),0) AS protein,
IFNULL(ROUND((info.carbohydrates /info.based_on_quantity)*i.quantity,2),0) AS carbohydrates,
IFNULL(ROUND((info.sugars_of_carbs /info.based_on_quantity)*i.quantity,2),0) AS sugars_of_carbs,
IFNULL(ROUND((info.fibre /info.based_on_quantity)*i.quantity,2),0) AS fibre,
IFNULL(ROUND((info.fat /info.based_on_quantity)*i.quantity,2),0) AS fat,
IFNULL(ROUND((info.saturated_fat /info.based_on_quantity)*i.quantity,2),0) AS saturated_fat,
IFNULL(ROUND((info.salt /info.based_on_quantity)*i.quantity,2),0) AS salt,
IFNULL(ROUND((info.water_content /info.based_on_quantity)*i.quantity,2),0) AS water_content,
IFNULL(ROUND((info.liquid_content /info.based_on_quantity)*i.quantity,2),0) AS liquid_content,
IFNULL(ROUND((info.calories /info.based_on_quantity)*i.quantity,2),0) AS calories,

'Delete Row' AS `delete button`

FROM ingredients_in_sections_of_meal i
LEFT JOIN ingredients_info info ON  info.ingredient_id = i.ingredient_id
LEFT JOIN ingredient_in_shops p ON p.pdid = i.pdid
LEFT JOIN stores s ON p.store_id = s.store_id;

-- ######################################

DROP VIEW IF EXISTS divided_meal_sections_calculations;
CREATE VIEW divided_meal_sections_calculations AS

SELECT

plan_id, 
div_meal_sections_id,
COUNT(ingredient_id) as no_of_ingredients,
IFNULL(ROUND(SUM(quantity),2),0) as weight_of_meal,
IFNULL(ROUND(SUM(ingredient_cost),2),0) as total_cost,
IFNULL(ROUND(SUM(protein),2),0) as total_protein,
IFNULL(ROUND(SUM(carbohydrates),2),0) as total_carbohydrates,
IFNULL(ROUND(SUM(sugars_of_carbs),2),0) as total_sugars_of_carbs,
IFNULL(ROUND(SUM(fibre),2),0) as total_fibre,
IFNULL(ROUND(SUM(fat),2),0) as total_fats,
IFNULL(ROUND(SUM(saturated_fat),2),0) as total_saturated_fat,
IFNULL(ROUND(SUM(salt),2),0) as total_salt,
IFNULL(ROUND(SUM(water_content),2),0) as total_water_content,
IFNULL(ROUND(SUM(liquid_content),2),0) as total_liquid_content,
IFNULL(ROUND(SUM(calories),2),0) as total_calories

FROM  ingredients_in_sections_of_meal_calculation
GROUP BY div_meal_sections_id, plan_id;

-- ######################################

DROP VIEW IF EXISTS total_meal_view;
CREATE VIEW total_meal_view AS

SELECT 
m.plan_id, 
m.meal_in_plan_id,
m.meal_time,
m.meal_name,
IFNULL(ROUND(SUM(di.no_of_ingredients),2),0) as no_of_ingredients,
IFNULL(ROUND(SUM(di.weight_of_meal),2),0) as weight_of_meal,
IFNULL(ROUND(SUM(di.total_cost),2),0) as total_cost,
IFNULL(ROUND(SUM(di.total_protein),2),0) as total_protein,
IFNULL(ROUND(SUM(di.total_carbohydrates),2),0) as total_carbohydrates,
IFNULL(ROUND(SUM(di.total_sugars_of_carbs),2),0) as total_sugars_of_carbs,
IFNULL(ROUND(SUM(di.total_fibre),2),0) as total_fibre,
IFNULL(ROUND(SUM(di.total_fats),2),0) as total_fats,
IFNULL(ROUND(SUM(di.total_saturated_fat),2),0) as total_saturated_fat,
IFNULL(ROUND(SUM(di.total_salt),2),0) as total_salt,
IFNULL(ROUND(SUM(di.total_water_content),2),0) as total_water,
IFNULL(ROUND(SUM(di.total_liquid_content),2),0) as total_liquid,
IFNULL(ROUND(SUM(di.total_calories),2),0) as total_calories

FROM  meals_in_plan m

LEFT JOIN divided_meal_sections d
ON m.meal_in_plan_id = d.meal_in_plan_id AND m.plan_id = d.plan_id

LEFT JOIN divided_meal_sections_calculations di
ON di.div_meal_sections_id = d.div_meal_sections_id AND di.plan_id = d.plan_id

GROUP BY  m.plan_id, m.meal_in_plan_id, Meal_Time, Meal_Name; -- Last 2 were just added because

-- ######################################

DROP VIEW IF EXISTS total_plan_view;
CREATE VIEW total_plan_view AS

SELECT 

P.plan_id, 
P.plan_name, -- needs to be here to prevent ONLY_FULL_GROUP_BY
COUNT(T.meal_in_plan_id) AS no_of_meals, -- always returns 0 or greater
IFNULL(ROUND(SUM(T.no_of_ingredients),2),0) AS ingredients_in_plan,
IFNULL(ROUND(SUM(T.weight_of_meal),2),0) AS weight_in_plan,
IFNULL(ROUND(SUM(T.total_cost),2),0) AS total_cost,
IFNULL(ROUND(SUM(T.total_protein),2),0) AS protein_in_plan,
IFNULL(ROUND(SUM(T.total_carbohydrates),2),0) AS carbohydrates_in_Plan,
IFNULL(ROUND(SUM(T.total_sugars_of_carbs),2),0) AS sugars_of_carbs_in_plan,
IFNULL(ROUND(SUM(T.total_fibre),2),0) AS fibre_in_plan,
IFNULL(ROUND(SUM(T.total_fats),2),0) AS fats_in_plan,
IFNULL(ROUND(SUM(T.total_saturated_fat),2),0) AS saturated_fat_in_plan,
IFNULL(ROUND(SUM(T.total_salt),2),0) AS salt_in_plan,
IFNULL(ROUND(SUM(T.total_water),2),0) AS water_content_in_plan,
IFNULL(ROUND(SUM(T.total_liquid),2),0) AS liquid_content_in_plan,
IFNULL(ROUND(SUM(T.total_calories),2),0) AS total_calories_in_plan

FROM plans P

LEFT  JOIN total_meal_view T
ON P.plan_id = T.plan_id

GROUP BY  P.plan_id, P.plan_name;

-- ######################################

DROP VIEW IF EXISTS plan_macros_left;
CREATE VIEW  plan_macros_left AS

SELECT

P.plan_id,
P.plan_name,

IFNULL(ROUND(C.expected_protein_grams - P.protein_in_plan ,2),0) AS protein_grams_left,
IFNULL(ROUND(C.expected_carbs_grams  - P.carbohydrates_in_Plan ,2),0) AS carb_grams_left,
IFNULL(ROUND(C.expected_fibre_grams  - P.fibre_in_plan ,2),0) AS fibre_grams_left,
IFNULL(ROUND(C.expected_fats_grams - P.fats_in_plan ,2),0) AS fat_grams_left,
IFNULL(ROUND(C.saturated_fat_limit - P.saturated_fat_in_plan ,2),0) AS potential_sat_fat_grams_left,
IFNULL(ROUND(C.salt_limit_grams - P.salt_in_plan ,2),0) AS potential_salt_grams_left,
IFNULL(ROUND(C.water_content_target - P.water_content_in_plan ,2),0) AS  water_left_to_drink,
IFNULL(ROUND(C.liquid_content_target - P.liquid_content_in_plan ,2),0) AS  liquids_left,
IFNULL(ROUND(C.calories_target - P.total_calories_in_plan ,2),0) AS calories_left,
IFNULL(ROUND(C.additional_calories_target - P.total_calories_in_plan ,2),0) AS added_calories_left

FROM
(
  SELECT
       plan_id,
       plan_name,
	   protein_in_plan,
	   carbohydrates_in_Plan,
	   fibre_in_plan,
	   fats_in_plan,
	   saturated_fat_in_plan,
       salt_in_plan,
	   water_content_in_plan,
	   liquid_content_in_plan,
	   total_calories_in_plan

  FROM total_plan_view
) P
LEFT JOIN
(
   SELECT * FROM plan_macro_target_calculations
) C
ON C.plan_id = P.plan_id;