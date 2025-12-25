
DROP VIEW IF EXISTS Monthly_Food_Calculations;
CREATE VIEW Monthly_Food_Calculations AS 
	
SELECT 
   D.PlanID, D.MealInPlanID, D.Meal_Name, D.IngredientID, D.Ingredient_Type_Name, D.Ingredient_Name, D.Product_Name,
   ((D.Quantity*30)) AS Quantity,
   D.Store_Name, D.Volume_Per_Unit, D.Cost_Per_Unit,
   
   (IFNULL(CEIL((D.Quantity*30) / Volume_Per_Unit),0)) AS Total_Items,
   (IFNULL((CEIL((D.Quantity*30) / Volume_Per_Unit)) * D.Cost_Per_Unit ,0)) AS Total_Price 

FROM
(
   SELECT 
   PlanID, MealInPlanID, Meal_Name, IngredientID,Ingredient_Type_Name, Ingredient_Name,Product_Name, Quantity,
   Store_Name,Volume_Per_Unit,Cost_Per_Unit FROM Daily_Food_Calculations
) AS D;


-- ######################################################################

SELECT  * FROM Daily_Food_Calculations WHERE IngredientID = 30 AND PlanID = 1 ORDER BY MealInPlanID;
SELECT  * FROM Weekly_Food_Calculations WHERE IngredientID = 30 AND PlanID = 1 ORDER BY MealInPlanID;

SELECT  * FROM Monthly_Food_Calculations WHERE IngredientID = 30 AND PlanID = 1 ORDER BY MealInPlanID;
