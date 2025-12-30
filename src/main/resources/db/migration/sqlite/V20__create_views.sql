
-- ################################################################################
--
-- ################################################################################
CREATE VIEW plan_macro_target_calculations AS
	
WITH
C AS (	
			SELECT
			
			    macros_version_id,
				date_time_last_edited,
				version_number,
				
				plan_version_id,				
				
				ROUND(current_weight_in_pounds * protein_per_pound, 2) AS protein, -- returns null if 1 of the values are empty
				ROUND(current_weight_in_pounds * carbohydrates_per_pound, 2) AS carbs,
				fibre,
				ROUND(current_weight_in_pounds * fats_per_pound, 2) AS fats,
				saturated_fat_limit,
				salt_limit,
				water_target,
				additional_calories
				
			FROM macros_per_pound_and_limits_versions
		)
	
SELECT

	PV.plan_version_id,
	
	P.plan_name,
	
	C.macros_version_id,
	C.date_time_last_edited,
	C.version_number,
	
	IFNULL(C.protein, 0) AS expected_protein_grams,
	IFNULL(C.carbs, 0) AS expected_carbs_grams,
	IFNULL(C.fibre, 0) AS expected_fibre_grams,
	IFNULL(C.fats, 0) AS expected_fats_grams,
	IFNULL(C.saturated_fat_limit, 0) AS saturated_fat_limit,
	IFNULL(C.salt_limit, 0) AS salt_limit_grams,
	IFNULL(C.water_target, 0) AS water_content_target,
	
	IFNULL(ROUND((C.protein * 4) + (C.carbs * 4) + (C.fats * 9) ,2), 0) AS calories_target,
	IFNULL(ROUND((C.protein * 4) + (C.carbs * 4) + (C.fats * 9) + C.additional_calories ,2), 0) AS additional_calories_target

FROM plan_versions PV 

LEFT JOIN plans P ON
	P.plan_id = PV.plan_id

LEFT JOIN C 
	ON C.plan_version_id = PV.plan_version_id;

-- ################################################################################
--
-- ################################################################################
CREATE VIEW ingredients_in_sections_of_meal_calculation AS
	
SELECT

	I.ingredients_index,
	I.div_meal_sections_version_id,	

	Info.ingredient_type_id,
	I.ingredient_id, 

	I.quantity,

	IFNULL(Info.glycemic_index, 0) AS gi,
	IFNULL(ROUND((Info.protein /Info.based_on_quantity)*I.quantity,2),0) AS protein,
	IFNULL(ROUND((Info.carbohydrates /Info.based_on_quantity)*I.quantity,2),0) AS carbohydrates,
	IFNULL(ROUND((Info.sugars_of_carbs /Info.based_on_quantity)*I.quantity,2),0) AS sugars_of_carbs,
	IFNULL(ROUND((Info.fibre /Info.based_on_quantity)*I.quantity,2),0) AS fibre,
	IFNULL(ROUND((Info.fat /Info.based_on_quantity)*I.quantity,2),0) AS fat,
	IFNULL(ROUND((Info.saturated_fat /Info.based_on_quantity)*I.quantity,2),0) AS saturated_fat,
	IFNULL(ROUND((Info.salt /Info.based_on_quantity)*I.quantity,2),0) AS salt,
	IFNULL(ROUND((Info.water_content /Info.based_on_quantity)*I.quantity,2),0) AS water_content,
	IFNULL(ROUND((Info.calories /Info.based_on_quantity)*I.quantity,2),0) AS calories

FROM ingredients_in_sections_of_meal I
LEFT JOIN ingredients_info Info ON Info.ingredient_id = I.ingredient_id;


CREATE VIEW ingredients_in_sections_of_meal_calculation_gui AS
	
SELECT

    ingredients_index,
    div_meal_sections_version_id,
	
    ingredient_type_id AS ingredient_type_name,
    ingredient_id AS ingredient_name,
    quantity,
    gi,
    protein,
    carbohydrates,
    sugars_of_carbs,
    fibre,
    fat,
    saturated_fat,
    salt,
    water_content,
    calories,
    'Delete Row' AS `delete button`
	
FROM ingredients_in_sections_of_meal_calculation;

-- ################################################################################
--
-- ################################################################################
CREATE VIEW divided_meal_sections_calculations AS

SELECT

	div_meal_sections_version_id,
	
	IFNULL(ROUND(SUM(protein),2),0) as total_protein,
	IFNULL(ROUND(SUM(carbohydrates),2),0) as total_carbohydrates,
	IFNULL(ROUND(SUM(sugars_of_carbs),2),0) as total_sugars_of_carbs,
	IFNULL(ROUND(SUM(fibre),2),0) as total_fibre,
	IFNULL(ROUND(SUM(fat),2),0) as total_fats,
	IFNULL(ROUND(SUM(saturated_fat),2),0) as total_saturated_fat,
	IFNULL(ROUND(SUM(salt),2),0) as total_salt,
	IFNULL(ROUND(SUM(water_content),2),0) as total_water_content,
	IFNULL(ROUND(SUM(calories),2),0) as total_calories

FROM ingredients_in_sections_of_meal_calculation
GROUP BY div_meal_sections_version_id;

-- ################################################################################
--
-- ################################################################################
CREATE VIEW total_meal_view AS

WITH
	I AS (	
			SELECT 

				D.meal_in_plan_version_id,			
				IFNULL(COUNT(DISTINCT ingredient_id),0) AS cnt

			FROM divided_meal_sections_versions D

			LEFT JOIN ingredients_in_sections_of_meal I
				ON I.div_meal_sections_version_id = D.div_meal_sections_version_id

			GROUP BY D.meal_in_plan_version_id
	),
	
	DI AS (
			SELECT 
			
				D.meal_in_plan_version_id,
				
				IFNULL(ROUND(SUM(DI.total_protein),2),0) as total_protein,
				IFNULL(ROUND(SUM(DI.total_carbohydrates),2),0) as total_carbohydrates,
				IFNULL(ROUND(SUM(DI.total_sugars_of_carbs),2),0) as total_sugars_of_carbs,
				IFNULL(ROUND(SUM(DI.total_fibre),2),0) as total_fibre,
				IFNULL(ROUND(SUM(DI.total_fats),2),0) as total_fats,
				IFNULL(ROUND(SUM(DI.total_saturated_fat),2),0) as total_saturated_fat,
				IFNULL(ROUND(SUM(DI.total_salt),2),0) as total_salt,
				IFNULL(ROUND(SUM(DI.total_water_content),2),0) as total_water_content,
				
				IFNULL(ROUND(SUM(DI.total_calories),2),0) as total_calories
				
			FROM divided_meal_sections_versions D
			
			LEFT JOIN divided_meal_sections_calculations DI 
				ON DI.div_meal_sections_version_id = D.div_meal_sections_version_id
			
			GROUP BY D.meal_in_plan_version_id		
	)
	
SELECT 

	M.plan_version_id, 
	M.meal_in_plan_version_id,
	
	M.meal_time,
	M.meal_name,	
	
	I.cnt AS no_of_ingredients,
	
	DI.total_protein,
	DI.total_carbohydrates, 
	DI.total_sugars_of_carbs,
	DI.total_fibre,
	DI.total_fats,
	DI.total_saturated_fat,
	DI.total_salt, 
	DI.total_water_content AS total_water,
	DI.total_calories
		
FROM meals_in_plan_versions M

LEFT JOIN I ON
	I.meal_in_plan_version_id = M.meal_in_plan_version_id

LEFT JOIN DI ON 
	DI.meal_in_plan_version_id = M.meal_in_plan_version_id;

-- ################################################################################
--
-- ################################################################################
CREATE VIEW total_plan_view AS

SELECT 

	PV.plan_version_id, 
	P.plan_name, -- needs to be here to prevent ONLY_FULL_GROUP_BY
	
	COUNT(T.meal_in_plan_version_id) AS no_of_meals, -- always returns 0 or greater || This could be its own CTE if the DB scales Higher
	
	IFNULL(ROUND(SUM(T.total_protein),2),0) AS protein_in_plan,
	IFNULL(ROUND(SUM(T.total_carbohydrates),2),0) AS carbohydrates_in_Plan,
	IFNULL(ROUND(SUM(T.total_sugars_of_carbs),2),0) AS sugars_of_carbs_in_plan,
	IFNULL(ROUND(SUM(T.total_fibre),2),0) AS fibre_in_plan,
	IFNULL(ROUND(SUM(T.total_fats),2),0) AS fats_in_plan,
	IFNULL(ROUND(SUM(T.total_saturated_fat),2),0) AS saturated_fat_in_plan,
	IFNULL(ROUND(SUM(T.total_salt),2),0) AS salt_in_plan,	
	IFNULL(ROUND(SUM(T.total_water),2),0) AS water_content_in_plan,
	
	IFNULL(ROUND(SUM(T.total_calories),2),0) AS total_calories_in_plan

FROM plan_versions PV

LEFT JOIN plans P 
	ON P.plan_id = PV.plan_id
	
LEFT JOIN total_meal_view T 
	ON PV.plan_version_id = T.plan_version_id

GROUP BY PV.plan_version_id, P.plan_name;


-- ################################################################################
--
-- ################################################################################
CREATE VIEW plan_macros_left AS
	
WITH 
    M AS ( -- GRAIN per Plan get Target ID of max date associate with each plan
				
			SELECT macros_version_id AS macro_ID, plan_version_id AS plan_version_id_x
			FROM (
					SELECT
					macros_version_id,	
					plan_version_id,
					ROW_NUMBER() OVER ( -- Split AND ORDER BY
						PARTITION BY plan_version_id -- Split the result set into groups based on plan_version_id
						ORDER BY date_time_last_edited DESC -- ORDER BY date
					) AS rn					
				FROM macros_per_pound_and_limits_versions
			) T
			WHERE T.rn = 1 -- Get First the first row per group 
	),
	
	C AS ( -- GRAIN Per Plan : TARGETS Calulcations 
			SELECT * 			
			FROM plan_macro_target_calculations T
			
			INNER JOIN M ON M.macro_ID = T.macros_version_id -- JOIN enforces relationship 
				AND M.plan_version_id_x = T.plan_version_id
    )

SELECT

	P.plan_version_id,
	P.plan_name,

	IFNULL(ROUND(C.expected_protein_grams - P.protein_in_plan ,2),0) AS protein_grams_left,
	IFNULL(ROUND(C.expected_carbs_grams  - P.carbohydrates_in_Plan ,2),0) AS carb_grams_left,
	IFNULL(ROUND(C.expected_fibre_grams  - P.fibre_in_plan ,2),0) AS fibre_grams_left,
	IFNULL(ROUND(C.expected_fats_grams - P.fats_in_plan ,2),0) AS fat_grams_left,
	IFNULL(ROUND(C.saturated_fat_limit - P.saturated_fat_in_plan ,2),0) AS potential_sat_fat_grams_left,
	IFNULL(ROUND(C.salt_limit_grams - P.salt_in_plan ,2),0) AS potential_salt_grams_left,	
	IFNULL(ROUND(C.water_content_target - P.water_content_in_plan ,2),0) AS  water_left_to_drink,
	
	IFNULL(ROUND(C.calories_target - P.total_calories_in_plan ,2),0) AS calories_left,
	IFNULL(ROUND(C.additional_calories_target - P.total_calories_in_plan ,2),0) AS added_calories_left

FROM total_plan_view P
LEFT JOIN C ON C.plan_version_id = P.plan_version_id;
