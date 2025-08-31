
\! cls;

DROP DATABASE IF EXISTS gymapp00001;
CREATE DATABASE gymapp00001;
USE gymapp00001;

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

CREATE TABLE IF NOT EXISTS macros_Per_Pound_And_Limits
(   	
    plan_id INT NOT NULL,
    FOREIGN KEY (plan_id) REFERENCES plans(plan_id) ON DELETE CASCADE,

	date_time_of_creation DATETIME NOT NULL,

	current_weight_kg DECIMAL(7,2) NOT NULL,
	current_weight_in_pounds DECIMAL(7,2) AS (ROUND(current_weight_kg * 2.2, 2)) STORED,

	body_fat_percentage DECIMAL(7,2) NOT NULL,

	protein_per_pound DECIMAL(7,2) NOT NULL,
	carbohydrates_per_pound DECIMAL(7,2) NOT NULL,
	fibre DECIMAL(7,2) NOT NULL,
	fats_per_pound DECIMAL(7,2) NOT NULL,

	saturated_fat_limit DECIMAL(7,2) NOT NULL,
	salt_limit DECIMAL(7,2) NOT NULL,

    water_target DECIMAL(7,2) NOT NULL,
	liquid_target DECIMAL(7,2) NOT NULL,

	additional_calories DECIMAL(7,2) NOT NULL,

	PRIMARY KEY (plan_id, date_time_of_creation)
);

-- ######################################

DROP VIEW IF EXISTS plan_Macro_Target_Calculations;
CREATE VIEW plan_Macro_Target_Calculations AS

SELECT 

P.plan_id, 
P.plan_name,
C.date_time_of_creation,
C.expected_protein_grams, 
C.expected_carbohydrates_grams, 
C.expected_fibre_grams, 
C.expected_fats_grams,
C.saturated_fat_limit, 
C.salt_limit_grams, 
C.water_content_target, 
C.liquid_content_target, 
C.calories_target,
C.additional_calories_target

FROM
(
	SELECT  plan_id, plan_name FROM plans
)  P
LEFT JOIN
(
    SELECT
    M.plan_id, M.date_time_of_creation,
	IFNULL(ROUND(M.current_weight_in_pounds * M.protein_per_pound, 2),0) AS expected_protein_grams,
	IFNULL(ROUND(M.current_weight_in_pounds * M.carbohydrates_per_pound, 2),0) AS expected_carbohydrates_grams,
	IFNULL(M.fibre, 0) AS expected_fibre_grams,
	IFNULL(ROUND(M.current_weight_in_pounds * M.fats_per_pound, 2),0) AS expected_fats_grams,
	IFNULL(M.saturated_fat_limit, 0) AS saturated_fat_limit,
    IFNULL(M.salt_limit, 0)  AS salt_limit_grams,
    IFNULL(M.water_target, 0) AS water_content_target,
	IFNULL(M.liquid_target, 0) AS liquid_content_target,
	IFNULL(
	    ROUND((M.current_weight_in_pounds * M.protein_per_pound) * 4, 2) +
		ROUND((M.current_weight_in_pounds * M.carbohydrates_per_pound) * 4, 2) +
		ROUND((M.current_weight_in_pounds * M.fats_per_pound) *9, 2)
	, 0) AS calories_target,
	IFNULL(
	    ROUND((M.current_weight_in_pounds * M.protein_per_pound) * 4, 2) +
		ROUND((M.current_weight_in_pounds * M.carbohydrates_per_pound) * 4, 2) +
		ROUND((M.current_weight_in_pounds * M.fats_per_pound) *9, 2) +
		M.additional_calories
	, 0) AS additional_calories_target

	FROM macros_Per_Pound_And_Limits M
)  C
ON P.plan_id = C.plan_id;

--######################################

CREATE TABLE IF NOT EXISTS ingredientTypes
(
    ingredient_type_id INT  PRIMARY KEY AUTO_INCREMENT,

	ingredient_type_name VARCHAR(100) NOT NULL,
	UNIQUE KEY no_repeat_ingredient_type_name (ingredient_type_name)
);

--######################################

CREATE TABLE IF NOT EXISTS ingredients_info
 (
    -- PRIMARY KEYS
    ingredient_id INT  PRIMARY KEY AUTO_INCREMENT,

	measurement ENUM('Litres', 'Grams') NOT NULL,

	ingredient_name VARCHAR(100) NOT NULL,

	ingredient_type_id  INT NOT NULL,
	FOREIGN KEY (ingredient_type_id) REFERENCES ingredientTypes(ingredient_type_id) ON DELETE CASCADE,

	based_on_quantity DECIMAL(7,2) NOT NULL,

    glycemic_index INT NOT NULL,
	protein DECIMAL(7,2) NOT NULL,

	carbohydrates DECIMAL(7,2) NOT NULL,
	sugars_of_carbs DECIMAL(7,2) NOT NULL,
	fibre DECIMAL(7,2) NOT NULL,

	fat DECIMAL(7,2) NOT NULL,
	saturated_fat DECIMAL(7,2) NOT NULL,
	salt DECIMAL(7,2) NOT NULL,
	water_content DECIMAL(7,2) NOT NULL,
	liquid_content DECIMAL(7,2) NOT NULL,

	calories DECIMAL(7,2) NOT NULL,

	UNIQUE KEY no_repeat_ingredient_names(ingredient_name)
);

--######################################
CREATE TABLE IF NOT EXISTS stores
(
    -- PRIMARY KEYS
    store_id INT  PRIMARY KEY AUTO_INCREMENT,
	store_name VARCHAR(255) NOT NULL,
	UNIQUE KEY no_repeat_store_names (store_name)

);
--######################################

CREATE TABLE IF NOT EXISTS ingredientInShops
(
    -- PRIMARY KEY , UNIQUE To this Table
    pdid INT  PRIMARY KEY AUTO_INCREMENT,

    ingredient_id INT  NOT NULL,
	FOREIGN KEY (ingredient_id) REFERENCES ingredients_info(ingredient_id) ON DELETE CASCADE,

    product_name VARCHAR(100) NOT NULL,

	volume_per_unit DECIMAL(7,2) NOT NULL,
	cost_per_unit DECIMAL(7,2) NOT NULL,

	store_id INT NOT NULL,
	FOREIGN KEY (store_id) REFERENCES stores(store_id) ON DELETE CASCADE,

    UNIQUE KEY no_repeat_products_in_store(store_id, product_name)
);
--######################################

CREATE TABLE IF NOT EXISTS mealsInPlan
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

--######################################

CREATE TABLE IF NOT EXISTS dividedMealSections
(
   div_meal_sections_id INT AUTO_INCREMENT,

   meal_in_plan_id  INT  NOT NULL,
   plan_id INT NOT NULL,
   
   FOREIGN KEY (meal_in_plan_id, plan_id)
        REFERENCES mealsInPlan (meal_in_plan_id, plan_id)
        ON DELETE CASCADE,
		
   PRIMARY KEY(div_meal_sections_id, plan_id), -- div_meal_sections_id isn't unique enough because its duplicated in temp meal plan for temp data it becomes unique with plan_id
   UNIQUE KEY No_Repeat_Sub_Meals_Per_Plan(div_meal_sections_id, meal_in_plan_id, plan_id)

);

--######################################
CREATE TABLE IF NOT EXISTS ingredients_in_sections_of_meal
(
    Ingredients_Index INT  AUTO_INCREMENT,

    div_meal_sections_id INT NOT NULL,
	plan_id INT NOT NULL,
	
	FOREIGN KEY (div_meal_sections_id, plan_id)
		REFERENCES dividedMealSections(div_meal_sections_id, plan_id)
		ON DELETE CASCADE,

    ingredient_id INT NOT NULL,
	FOREIGN KEY (ingredient_id) REFERENCES ingredients_info(ingredient_id) ON DELETE CASCADE,

	Quantity DECIMAL(15,2) NOT NULL,

	pdid INT NULL,
 	FOREIGN KEY (pdid) REFERENCES ingredientInShops(pdid) ON DELETE CASCADE,

	PRIMARY KEY (Ingredients_Index, plan_id),
	UNIQUE KEY No_Repeat_Meals (Ingredients_Index, div_meal_sections_id, plan_id) -- #HELLO is div_meal_sections_id needed
);

--######################################

DROP VIEW IF EXISTS ingredients_in_sections_of_meal_calculation;
CREATE VIEW ingredients_in_sections_of_meal_calculation AS

SELECT

i.plan_id, 
i.div_meal_sections_id,
i.Ingredients_Index,  
i.ingredient_id AS Ingredient_ID, 
i.Quantity,
(SELECT t.ingredient_type_name FROM ingredientTypes t WHERE t.ingredient_type_id = info.ingredient_type_id)  AS Ingredient_Type,
info.ingredient_name AS Ingredient_Name,
IFNULL(ROUND((i.Quantity /p.volume_per_unit)*p.cost_per_unit,2),0) AS Ingredient_Cost,
IFNULL(s.store_name,'N/A') AS  Supplier,
IFNULL(p.product_name,'N/A') AS  Product_Name,
IFNULL(ROUND((info.protein /info.based_on_quantity)*i.Quantity,2),0) AS Protein,
IFNULL(info.glycemic_index, 0) AS GI,
IFNULL(ROUND((info.carbohydrates /info.based_on_quantity)*i.Quantity,2),0) AS Carbohydrates,
IFNULL(ROUND((info.sugars_of_carbs /info.based_on_quantity)*i.Quantity,2),0) AS Sugars_Of_Carbs,
IFNULL(ROUND((info.fibre /info.based_on_quantity)*i.Quantity,2),0) AS Fibre,
IFNULL(ROUND((info.fat /info.based_on_quantity)*i.Quantity,2),0) AS Fat,
IFNULL(ROUND((info.saturated_fat /info.based_on_quantity)*i.Quantity,2),0) AS Saturated_Fat,
IFNULL(ROUND((info.salt /info.based_on_quantity)*i.Quantity,2),0) AS Salt,
IFNULL(ROUND((info.water_content /info.based_on_quantity)*i.Quantity,2),0) AS Water_Content,
IFNULL(ROUND((info.liquid_content /info.based_on_quantity)*i.Quantity,2),0) AS Liquid_Content,
IFNULL(ROUND((info.calories /info.based_on_quantity)*i.Quantity,2),0) AS Calories,
'Delete Row' AS `Delete Button`

FROM ingredients_in_sections_of_meal i
LEFT JOIN ingredients_info info ON  info.ingredient_id = i.ingredient_id
LEFT JOIN ingredientInShops p ON p.pdid = i.pdid
LEFT JOIN stores s ON p.store_id = s.store_id;

--######################################

DROP VIEW IF EXISTS divided_meal_sections_calculations;
CREATE VIEW divided_meal_sections_calculations AS

SELECT

plan_id, 
div_meal_sections_id,
COUNT(ingredient_id) as No_Of_Ingredients,
IFNULL(ROUND(SUM(Quantity),2),0) as Weight_OF_Meal,
IFNULL(ROUND(SUM(Ingredient_Cost),2),0) as Total_Cost,
IFNULL(ROUND(SUM(Protein),2),0) as Total_Protein,
IFNULL(ROUND(SUM(Carbohydrates),2),0) as Total_Carbohydrates,
IFNULL(ROUND(SUM(Sugars_Of_Carbs),2),0) as Total_Sugars_Of_Carbs,
IFNULL(ROUND(SUM(Fibre),2),0) as Total_Fibre,
IFNULL(ROUND(SUM(Fat),2),0) as Total_Fats,
IFNULL(ROUND(SUM(Saturated_Fat),2),0) as Total_Saturated_Fat,
IFNULL(ROUND(SUM(Salt),2),0) as Total_Salt,
IFNULL(ROUND(SUM(Water_Content),2),0) as Total_Water_Content,
IFNULL(ROUND(SUM(Liquid_Content),2),0) as Total_Liquid_Content,
IFNULL(ROUND(SUM(Calories),2),0) as Total_Calories

FROM  ingredients_in_sections_of_meal_calculation
GROUP BY div_meal_sections_id, plan_id;

--######################################

DROP VIEW IF EXISTS total_meal_view;
CREATE VIEW total_meal_view AS

SELECT 
m.plan_id, 
m.meal_in_plan_id,
m.Meal_Time AS Meal_Time,
m.meal_name AS Meal_Name,
IFNULL(ROUND(SUM(di.No_Of_Ingredients),2),0) as No_Of_Ingredients,
IFNULL(ROUND(SUM(di.Weight_OF_Meal),2),0) as Weight_OF_Meal,
IFNULL(ROUND(SUM(di.Total_Cost),2),0) as Total_Cost,
IFNULL(ROUND(SUM(di.Total_Protein),2),0) as Total_Protein,
IFNULL(ROUND(SUM(di.Total_Carbohydrates),2),0) as Total_Carbohydrates,
IFNULL(ROUND(SUM(di.Total_Sugars_Of_Carbs),2),0) as Total_Sugars_Of_Carbs,
IFNULL(ROUND(SUM(di.Total_Fibre),2),0) as Total_Fibre,
IFNULL(ROUND(SUM(di.Total_Fats),2),0) as Total_Fats,
IFNULL(ROUND(SUM(di.Total_Saturated_Fat),2),0) as Total_Saturated_Fat,
IFNULL(ROUND(SUM(di.Total_Salt),2),0) as Total_Salt,
IFNULL(ROUND(SUM(di.Total_Water_Content),2),0) as Total_Water_Content,
IFNULL(ROUND(SUM(di.Total_Liquid_Content),2),0) as Total_Liquid_Content,
IFNULL(ROUND(SUM(di.Total_Calories),2),0) as Total_Calories

FROM  mealsInPlan m

LEFT JOIN dividedMealSections d
ON m.meal_in_plan_id = d.meal_in_plan_id AND m.plan_id = d.plan_id

LEFT JOIN divided_meal_sections_calculations di
ON di.div_meal_sections_id = d.div_meal_sections_id AND di.plan_id = d.plan_id

GROUP BY  m.plan_id, m.meal_in_plan_id, Meal_Time, Meal_Name; -- Last 2 were just added because

--######################################

DROP VIEW IF EXISTS total_plan_view;
CREATE VIEW total_plan_view AS

SELECT 

P.plan_id, 
P.plan_name, -- needs to be here to prevent ONLY_FULL_GROUP_BY
COUNT(T.meal_in_plan_id) AS No_Of_Meals,
IFNULL(ROUND(SUM(T.No_Of_Ingredients),2),0) AS Ingredients_In_Plan,
IFNULL(ROUND(SUM(T.Weight_OF_Meal),2),0) AS Weight_In_Plan,
IFNULL(ROUND(SUM(T.Total_Cost),2),0) AS Total_Cost,
IFNULL(ROUND(SUM(T.Total_Protein),2),0) AS Protein_In_Plan,
IFNULL(ROUND(SUM(T.Total_Carbohydrates),2),0) AS Carbohydrates_In_Plan,
IFNULL(ROUND(SUM(T.Total_Sugars_Of_Carbs),2),0) AS Sugars_Of_Carbs_In_Plan,
IFNULL(ROUND(SUM(T.Total_Fibre),2),0) AS Fibre_In_Plan,
IFNULL(ROUND(SUM(T.Total_Fats),2),0) AS Fats_In_Plan,
IFNULL(ROUND(SUM(T.Total_Saturated_Fat),2),0) AS Saturated_Fat_In_Plan,
IFNULL(ROUND(SUM(T.Total_Salt),2),0) AS Salt_In_Plan,
IFNULL(ROUND(SUM(T.Total_Water_Content),2),0) AS Water_Content_In_Plan,
IFNULL(ROUND(SUM(T.Total_Liquid_Content),2),0) AS Liquid_Content_In_Plan,
IFNULL(ROUND(SUM(T.Total_Calories),2),0) AS Total_Calories_In_Plan

FROM plans P

LEFT  JOIN total_meal_view T
ON P.plan_id = T.plan_id

GROUP BY  P.plan_id, P.plan_name;

--######################################

DROP VIEW IF EXISTS planMacrosLeft;
CREATE VIEW  planMacrosLeft AS

SELECT

P.plan_id, 
P.plan_name,
IFNULL(ROUND(C.expected_protein_grams - P.Protein_In_Plan ,2),0) AS Protein_Grams_Left,
IFNULL(ROUND(C.expected_carbohydrates_grams  - P.Carbohydrates_In_Plan ,2),0) AS Carbohydrates_Grams_Left,
IFNULL(ROUND(C.expected_fibre_grams  - P.Fibre_In_Plan ,2),0) AS Fibre_Grams_Left,
IFNULL(ROUND(C.expected_fats_grams - P.Fats_In_Plan ,2),0) AS Fats_Grams_Left,
IFNULL(ROUND(C.saturated_fat_limit - P.Saturated_Fat_In_Plan ,2),0) AS Potential_Saturated_Fat_Grams,
IFNULL(ROUND(C.salt_limit_grams - P.Salt_In_Plan ,2),0) AS Potential_Salt,
IFNULL(ROUND(C.water_content_target - P.Water_Content_In_Plan ,2),0) AS  Water_Left_To_Drink,
IFNULL(ROUND(C.liquid_content_target - P.Liquid_Content_In_Plan ,2),0) AS  Liquids_Left,
IFNULL(ROUND(C.calories_target - P.Total_Calories_In_Plan ,2),0) AS Calories_Left,
IFNULL(ROUND(C.additional_calories_target - P.Total_Calories_In_Plan ,2),0) AS Added_Calories_Left

FROM
(
  SELECT
       plan_id, plan_name,
	   IFNULL( Protein_In_Plan, 0) AS Protein_In_Plan,
	   IFNULL( Carbohydrates_In_Plan, 0) AS Carbohydrates_In_Plan,
	   IFNULL(Fibre_In_Plan,0) AS  Fibre_In_Plan,
	   IFNULL( Fats_In_Plan, 0) AS Fats_In_Plan,
	   IFNULL( Saturated_Fat_In_Plan,0) AS Saturated_Fat_In_Plan,
       IFNULL( Salt_In_Plan, 0) AS Salt_In_Plan,
	   IFNULL( Water_Content_In_Plan, 0) AS Water_Content_In_Plan,
	   IFNULL( Liquid_Content_In_Plan, 0) AS Liquid_Content_In_Plan,
	   IFNULL( Total_Calories_In_Plan, 0) AS Total_Calories_In_Plan

  FROM total_plan_view

) P
LEFT JOIN
(
   SELECT * FROM plan_Macro_Target_Calculations
) C
ON C.plan_id = P.plan_id;