-- ##################################################################
-- ##################################################################

-- ALL the ingredient types: id/name matched with ingredient id/names

WITH
    I AS (SELECT ingredient_id, ingredient_name, ingredient_type_id FROM ingredients_info),
	T AS (SELECT * FROM ingredient_types)

SELECT
    T.ingredient_type_id AS type_id,
	T.ingredient_type_name AS type_name,

	JSON_ARRAYAGG(
        JSON_OBJECT('id', I.ingredient_id,
            'name', I.ingredient_name
        )

    ) AS matched_ingredients

FROM  T
LEFT JOIN I ON T.ingredient_type_id = I.ingredient_type_id
GROUP BY T.ingredient_type_id, T.ingredient_type_name
ORDER BY T.ingredient_type_name ASC;

-- ##################################################################
-- ##################################################################

-- Divs with Ingredients

WITH
   M AS (SELECT * FROM meals_in_plan),
   D AS (SELECT * FROM divided_meal_sections),
   I AS (SELECT * FROM ingredients_in_sections_of_meal_calculation)

SELECT DISTINCT
    Q.plan_id,
	Q.meal_in_plan_id AS meal_id,
	Q.meal_time,	
	Q.meal_name,

	JSON_ARRAYAGG(
        JSON_OBJECT(
            'div_id', Q.div_meal_sections_id,
			'id', Q.ingredient_id
        )
    ) AS matched_ingredients

FROM
(
	SELECT M.plan_id AS p_id, M.meal_in_plan_id , M.meal_time, M.meal_name, I.*
	FROM M
	LEFT JOIN D ON M.plan_id = D.plan_id AND M.meal_in_plan_id = D.meal_in_plan_id
 	LEFT JOIN I ON I.plan_id = D.plan_id AND I.div_meal_sections_id = D.div_meal_sections_id
) AS Q
WHERE Q.plan_id = 1
GROUP BY Q.plan_id, Q.meal_in_plan_id, Q.meal_time /*, Q.div_meal_sections_id*/;


-- ##################################################################
-- ##################################################################

-- Divs with Ingredients
                
WITH
   M AS (SELECT * FROM meals_in_plan ORDER BY meal_time ASC),
   D AS (SELECT * FROM divided_meal_sections ORDER BY div_meal_sections_id ASC ),
   I AS (SELECT * FROM ingredients_in_sections_of_meal_calculation ORDER BY ingredients_index ASC)

SELECT DISTINCT
    Q.plan_id,
	Q.meal_in_plan_id AS meal_id,
	Q.meal_time,	
	Q.meal_name,

	JSON_ARRAYAGG(
		JSON_OBJECT(
			'plan_id',     Q.plan_id,
			'div_id',      Q.div_meal_sections_id,
			'index',       Q.ingredients_index


		)
	) AS matched_ingredients

FROM
(
	SELECT M.plan_id AS p_id, M.meal_in_plan_id , M.meal_time, M.meal_name, I.*
	FROM M
	LEFT JOIN D ON M.plan_id = D.plan_id AND M.meal_in_plan_id = D.meal_in_plan_id
	LEFT JOIN I ON I.plan_id = D.plan_id AND I.div_meal_sections_id = D.div_meal_sections_id
) AS Q
WHERE Q.plan_id = 1
GROUP BY Q.plan_id, Q.meal_in_plan_id, Q.meal_time  /*, Q.div_meal_sections_id */
ORDER BY Q.meal_time;