DROP VIEW IF EXISTS Daily_Food_Calculations2;
CREATE VIEW Daily_Food_Calculations2 AS 

SELECT 
    i.PlanID,
	m.MealInPlanID, m.Meal_Name, i.IngredientID,
	j.Ingredient_Type_Name,	n.Ingredient_Name, 
	IFNULL(s.Product_Name, "N/A") AS Product_Name,
	SUM(i.Quantity) AS Quantity, 	
	IFNULL(st.Store_Name, "N/A") AS Store_Name,
	IFNULL (s.Volume_Per_Unit,0) AS Volume_Per_Unit,
	IFNULL (s.Cost_Per_Unit,0) AS Cost_Per_Unit,
    
   (IFNULL(CEIL(SUM(i.Quantity) / s.Volume_Per_Unit),0) ) AS Total_Items,
   (IFNULL((CEIL(SUM(i.Quantity) / s.Volume_Per_Unit))*s.Cost_Per_Unit,0)) AS Total_Price                          
   
FROM (	
	SELECT  *
	FROM ingredients_in_sections_of_meal		
) AS i

LEFT JOIN ingredientInShops s
ON i.PDID = s.PDID

LEFT JOIN stores st
ON s.StoreID = st.StoreID

RIGHT JOIN 
(
	SELECT DivMealSectionsID, MealInPlanID, PlanID FROM dividedMealSections
) AS D
ON i.DivMealSectionsID = D.DivMealSectionsID AND i.PlanID = D.PlanID

RIGHT JOIN 
(
	SELECT MealInPlanID, Meal_Name,PlanID FROM mealsInPlan
) AS m
ON m.MealInPlanID = D.MealInPlanID AND m.PlanID = D.PlanID

RIGHT JOIN
(
	SELECT  IngredientID, Ingredient_Name, Ingredient_Type_ID  FROM ingredients_info
) AS n 
ON n.IngredientID = i.IngredientID

RIGHT JOIN ingredientTypes j
ON n.Ingredient_Type_ID = j.Ingredient_Type_ID

GROUP BY i.PlanID, i.IngredientID, Product_Name, s.StoreID;
	

#######################################################################
SELECT  * FROM Daily_Food_Calculations WHERE PlanID = 1 AND INGREDIENTID = 30 ORDER BY MealInPlanID;
SELECT  * FROM Daily_Food_Calculations2 WHERE PlanID = 1 AND INGREDIENTID = 30 ORDER BY MealInPlanID;

