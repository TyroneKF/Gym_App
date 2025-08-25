--######################################
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

--######################################

CREATE TABLE IF NOT EXISTS macros_Per_Pound_And_Limits
(   	
    plan_id INT NOT NULL,
    FOREIGN KEY (plan_id) REFERENCES plans(plan_id) ON DELETE CASCADE,

	DateTime_Of_Creation DATETIME NOT NULL,

	current_Weight_KG DECIMAL(7,2) NOT NULL,
	current_Weight_In_Pounds DECIMAL(7,2) AS (ROUND(current_Weight_KG * 2.2, 2)) STORED,

	BodyFatPercentage DECIMAL(7,2) NOT NULL,

	Protein_PerPound DECIMAL(7,2) NOT NULL,
	Carbohydrates_PerPound DECIMAL(7,2) NOT NULL,
	Fibre DECIMAL(7,2) NOT NULL,
	Fats_PerPound DECIMAL(7,2) NOT NULL,

	Saturated_Fat_Limit DECIMAL(7,2) NOT NULL,
	Salt_Limit DECIMAL(7,2) NOT NULL,

    Water_Target DECIMAL(7,2) NOT NULL,
	Liquid_Target DECIMAL(7,2) NOT NULL,

	Additional_Calories DECIMAL(7,2) NOT NULL,

	PRIMARY KEY (plan_id, DateTime_Of_Creation)
);

--######################################

DROP VIEW IF EXISTS plan_Macro_Target_Calculations;
CREATE  VIEW plan_Macro_Target_Calculations AS

SELECT P.plan_id, P.plan_name,

C.DateTime_Of_Creation,
C.Expected_Protein_Grams, C.Expected_Carbohydrates_Grams, C.Expected_Fibre_Grams, C.Expected_Fats_Grams,
C.Saturated_Fat_Limit, C.Salt_Limit_Grams, C.Water_Content_Target, C.Liquid_Content_Target, C.Calories_Target,
C.Additional_Calories_Target

FROM
(
	SELECT  plan_id, plan_name FROM plans
)  P

LEFT JOIN
(
    SELECT  M.plan_id, M.DateTime_Of_Creation,
	IFNULL(ROUND(M.current_Weight_In_Pounds * M.Protein_PerPound, 2),0) AS Expected_Protein_Grams,
	IFNULL(ROUND(M.current_Weight_In_Pounds * M.Carbohydrates_PerPound, 2),0) AS Expected_Carbohydrates_Grams,
	IFNULL(M.Fibre, 0) AS Expected_Fibre_Grams,
	IFNULL(ROUND(M.current_Weight_In_Pounds * M.Fats_PerPound, 2),0) AS Expected_Fats_Grams,
	IFNULL(M.Saturated_Fat_Limit, 0) AS Saturated_Fat_Limit,
    IFNULL(M.Salt_Limit, 0)  AS Salt_Limit_Grams,
    IFNULL(M.Water_Target, 0) AS Water_Content_Target,
	IFNULL(M.Liquid_Target, 0) AS Liquid_Content_Target,
	IFNULL(
	    ROUND((M.current_Weight_In_Pounds * M.Protein_PerPound) * 4, 2) +
		ROUND((M.current_Weight_In_Pounds * M.Carbohydrates_PerPound) * 4, 2) +
		ROUND((M.current_Weight_In_Pounds * M.Fats_PerPound) *9, 2)
	, 0) AS Calories_Target,
	IFNULL(
	    ROUND((M.current_Weight_In_Pounds * M.Protein_PerPound) * 4, 2) +
		ROUND((M.current_Weight_In_Pounds * M.Carbohydrates_PerPound) * 4, 2) +
		ROUND((M.current_Weight_In_Pounds * M.Fats_PerPound) *9, 2) +
		M.Additional_Calories
	, 0) AS Additional_Calories_Target

	FROM macros_Per_Pound_And_Limits M

)  C
ON P.plan_id = C.plan_id;

--######################################

CREATE TABLE IF NOT EXISTS ingredientTypes
(
    Ingredient_Type_ID INT  PRIMARY KEY AUTO_INCREMENT,

	Ingredient_Type_Name VARCHAR(100) NOT NULL,
	UNIQUE KEY unique_type_name (Ingredient_Type_Name)
);

--######################################

CREATE TABLE IF NOT EXISTS ingredients_info
 (
    -- PRIMARY KEYS
    IngredientID INT  PRIMARY KEY AUTO_INCREMENT,

	Measurement ENUM('Litres', 'Grams') NOT NULL,

	Ingredient_Name VARCHAR(100) NOT NULL,

	Ingredient_Type_ID  INT NOT NULL,
	FOREIGN KEY (Ingredient_Type_ID) REFERENCES ingredientTypes(Ingredient_Type_ID) ON DELETE CASCADE,

	Based_On_Quantity DECIMAL(7,2) NOT NULL,

    Glycemic_Index INT NOT NULL,
	Protein DECIMAL(7,2) NOT NULL,

	Carbohydrates DECIMAL(7,2) NOT NULL,
	Sugars_Of_Carbs DECIMAL(7,2) NOT NULL,
	Fibre DECIMAL(7,2) NOT NULL,

	Fat DECIMAL(7,2) NOT NULL,
	Saturated_Fat DECIMAL(7,2) NOT NULL,
	Salt DECIMAL(7,2) NOT NULL,
	Water_Content DECIMAL(7,2) NOT NULL,
	Liquid_Content DECIMAL(7,2) NOT NULL,

	Calories DECIMAL(7,2) NOT NULL,

	UNIQUE KEY unique_ingredient_name(Ingredient_Name)
);

--######################################
CREATE TABLE IF NOT EXISTS stores
(
    -- PRIMARY KEYS
    StoreID INT  PRIMARY KEY AUTO_INCREMENT,
	Store_Name VARCHAR(255) NOT NULL,
	UNIQUE KEY unique_store_name (Store_Name)

);
--######################################

CREATE TABLE IF NOT EXISTS ingredientInShops
(
    -- PRIMARY KEY , UNIQUE To this Table
    PDID INT  PRIMARY KEY AUTO_INCREMENT,

    IngredientID INT  NOT NULL,
	FOREIGN KEY (IngredientID) REFERENCES ingredients_info(IngredientID) ON DELETE CASCADE,

    Product_Name VARCHAR(100) NOT NULL,

	Volume_Per_Unit DECIMAL(7,2) NOT NULL,
	Cost_Per_Unit DECIMAL(7,2) NOT NULL,

	StoreID INT NOT NULL,
	FOREIGN KEY (StoreID) REFERENCES stores(StoreID) ON DELETE CASCADE,

    UNIQUE KEY Product_In_Store(StoreID, Product_Name)
);
--######################################

CREATE TABLE IF NOT EXISTS mealsInPlan
(
   MealInPlanID INT NOT NULL AUTO_INCREMENT,

   plan_id INT NOT NULL,
   FOREIGN KEY (plan_id) REFERENCES plans(plan_id) ON DELETE CASCADE,

   Meal_Name VARCHAR(100) NOT NULL,
   Meal_Time TIME NOT NULL,

   PRIMARY KEY(MealInPlanID, plan_id), -- MealInPlanID isn't unique enough because its duplicated in temp meal plan for temp data it becomes unique with plan_id 
   UNIQUE KEY Time_For_Meal(plan_id, Meal_Time), -- Only one meal can be at one time
   UNIQUE KEY No_Repeat_Meal_Names_In_Plan(plan_id, Meal_Name) -- can't have 2 of the same meal_names in a plan
);

--######################################

CREATE TABLE IF NOT EXISTS dividedMealSections
(
   DivMealSectionsID INT AUTO_INCREMENT,

   MealInPlanID  INT  NOT NULL, 
   plan_id INT NOT NULL,
   
   FOREIGN KEY (MealInPlanID, plan_id)  
        REFERENCES mealsInPlan (MealInPlanID, plan_id)
        ON DELETE CASCADE,
		
   PRIMARY KEY(DivMealSectionsID, plan_id), -- DivMealSectionsID isn't unique enough because its duplicated in temp meal plan for temp data it becomes unique with plan_id
   UNIQUE KEY No_Repeat_Sub_Meals_Per_Plan(DivMealSectionsID, MealInPlanID, plan_id)

);

--######################################
CREATE TABLE IF NOT EXISTS ingredients_in_sections_of_meal
(
    Ingredients_Index INT  AUTO_INCREMENT,

    DivMealSectionsID INT NOT NULL,
	plan_id INT NOT NULL,
	
	FOREIGN KEY (DivMealSectionsID, plan_id) 
		REFERENCES dividedMealSections(DivMealSectionsID, plan_id) 
		ON DELETE CASCADE,

    IngredientID INT NOT NULL,
	FOREIGN KEY (IngredientID) REFERENCES ingredients_info(IngredientID) ON DELETE CASCADE,

	Quantity DECIMAL(15,2) NOT NULL,

	PDID INT NULL,
 	FOREIGN KEY (PDID) REFERENCES ingredientInShops(PDID) ON DELETE CASCADE,

	PRIMARY KEY (Ingredients_Index, plan_id),
	UNIQUE KEY No_Repeat_Meals (Ingredients_Index, DivMealSectionsID, plan_id) -- #HELLO is DivMealSectionsID needed
);

--######################################

DROP VIEW IF EXISTS ingredients_in_sections_of_meal_calculation;
CREATE VIEW ingredients_in_sections_of_meal_calculation AS

SELECT

i.plan_id, i.DivMealSectionsID, i.Ingredients_Index,  i.IngredientID, i.Quantity,
(SELECT t.Ingredient_Type_Name FROM ingredientTypes t WHERE t.Ingredient_Type_ID = info.Ingredient_Type_ID)  AS Ingredient_Type,

info.Ingredient_Name,

IFNULL(ROUND((i.Quantity /p.Volume_Per_Unit)*p.Cost_Per_Unit,2),0) AS Ingredient_Cost,
IFNULL(s.Store_Name,'N/A') AS  Supplier,
IFNULL(p.Product_Name,'N/A') AS  Product_Name,

IFNULL(ROUND((info.Protein /info.Based_On_Quantity)*i.Quantity,2),0) AS Protein,
IFNULL(info.Glycemic_Index, 0) AS GI,
IFNULL(ROUND((info.Carbohydrates /info.Based_On_Quantity)*i.Quantity,2),0) AS Carbohydrates,
IFNULL(ROUND((info.Sugars_Of_Carbs /info.Based_On_Quantity)*i.Quantity,2),0) AS Sugars_Of_Carbs,
IFNULL(ROUND((info.Fibre /info.Based_On_Quantity)*i.Quantity,2),0) AS Fibre,
IFNULL(ROUND((info.Fat /info.Based_On_Quantity)*i.Quantity,2),0) AS Fat,
IFNULL(ROUND((info.Saturated_Fat /info.Based_On_Quantity)*i.Quantity,2),0) AS Saturated_Fat,
IFNULL(ROUND((info.Salt /info.Based_On_Quantity)*i.Quantity,2),0) AS Salt,
IFNULL(ROUND((info.Water_Content /info.Based_On_Quantity)*i.Quantity,2),0) AS Water_Content,
IFNULL(ROUND((info.Liquid_Content /info.Based_On_Quantity)*i.Quantity,2),0) AS Liquid_Content,
IFNULL(ROUND((info.Calories /info.Based_On_Quantity)*i.Quantity,2),0) AS Calories,
'Delete Row' AS `Delete Button`

FROM ingredients_in_sections_of_meal i
LEFT JOIN ingredients_info info ON  info.IngredientID = i.IngredientID
LEFT JOIN ingredientInShops p ON p.PDID = i.PDID
LEFT JOIN stores s ON p.StoreID = s.StoreID;

--######################################

DROP VIEW IF EXISTS divided_meal_sections_calculations;
CREATE VIEW divided_meal_sections_calculations AS

SELECT

plan_id, DivMealSectionsID,
COUNT(IngredientID) as No_Of_Ingredients,

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
GROUP BY DivMealSectionsID, plan_id;

--######################################

DROP VIEW IF EXISTS total_meal_view;
CREATE VIEW total_meal_view AS

SELECT m.plan_id, m.MealInPlanID,

m.Meal_Time AS Meal_Time,
m.Meal_Name AS Meal_Name,

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
ON m.MealInPlanID = d.MealInPlanID AND m.plan_id = d.plan_id

LEFT JOIN divided_meal_sections_calculations di
ON di.DivMealSectionsID = d.DivMealSectionsID AND di.plan_id = d.plan_id

GROUP BY  m.plan_id, m.MealInPlanID, Meal_Time, Meal_Name; -- Last 2 were just added because

--######################################

DROP VIEW IF EXISTS total_plan_view;
CREATE VIEW total_plan_view AS

SELECT P.plan_id, P.plan_name, -- needs to be here to prevent ONLY_FULL_GROUP_BY

COUNT(T.MealInPlanID) AS No_Of_Meals,

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
P.plan_id, P.plan_name,
IFNULL(ROUND(C.Expected_Protein_Grams - P.Protein_In_Plan ,2),0) AS Protein_Grams_Left,
IFNULL(ROUND(C.Expected_Carbohydrates_Grams  - P.Carbohydrates_In_Plan ,2),0) AS Carbohydrates_Grams_Left,
IFNULL(ROUND(C.Expected_Fibre_Grams  - P.Fibre_In_Plan ,2),0) AS Fibre_Grams_Left,
IFNULL(ROUND(C.Expected_Fats_Grams - P.Fats_In_Plan ,2),0) AS Fats_Grams_Left,
IFNULL(ROUND(C.Saturated_Fat_Limit - P.Saturated_Fat_In_Plan ,2),0) AS Potential_Saturated_Fat_Grams,

IFNULL(ROUND(C.Salt_Limit_Grams - P.Salt_In_Plan ,2),0) AS Potential_Salt,
IFNULL(ROUND(C.Water_Content_Target - P.Water_Content_In_Plan ,2),0) AS  Water_Left_To_Drink,
IFNULL(ROUND(C.Liquid_Content_Target - P.Liquid_Content_In_Plan ,2),0) AS  Liquids_Left,

IFNULL(ROUND(C.Calories_Target - P.Total_Calories_In_Plan ,2),0) AS Calories_Left,
IFNULL(ROUND(C.Additional_Calories_Target - P.Total_Calories_In_Plan ,2),0) AS Added_Calories_Left

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