use gymapp00001;

INSERT INTO ingredients_info 
(IngredientID, Measurement, Ingredient_Name, Ingredient_Type_ID, Based_On_Quantity, Glycemic_Index, Protein, Carbohydrates, Sugars_Of_Carbs, Fibre, Fat, Saturated_Fat, Salt, Water_Content, Liquid_Content, Calories) 
VALUES
(1, 'Grams','None Of The Above',1, 0,0,0,0,0,0,0,0,0,0,0,0),
(NULL,("Grams"),("test1"),(SELECT Ingredient_Type_ID FROM ingredientTypes WHERE Ingredient_Type_Name = 'Cake'),(1),(2),(3),(4),(5),(6),(7),(8),(9),(10),(11),(12)),
(NULL,("Grams"),("test3"),(SELECT Ingredient_Type_ID FROM ingredientTypes WHERE Ingredient_Type_Name = 'Cake'),(100),(1),(2),(3),(34),(5),(6),(7),(8),(9),(10),(11)),
(NULL,("Grams"),("NewNew"),(SELECT Ingredient_Type_ID FROM ingredientTypes WHERE Ingredient_Type_Name = 'Cake'),(1),(2),(3),(4),(5),(6),(7),(8),(9),(10),(11),(12));;