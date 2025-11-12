
-- ##################################################################
-- ##################################################################

-- ALL the ingredient types: id/name matched with ingredient id/names

WITH
    ingred_info AS (SELECT ingredient_id, ingredient_name, ingredient_type_id FROM ingredients_info),
	ingred_type AS (SELECT * FROM ingredient_types)

SELECT
    t.ingredient_type_id AS type_id,
	t.ingredient_type_name AS type_name,

	JSON_ARRAYAGG(
        JSON_OBJECT(
            'id', i.ingredient_id,
            'name', i.ingredient_name
        )
    ) AS matched_ingredients

FROM ingred_type t
LEFT JOIN ingred_info i ON t.ingredient_type_id = i.ingredient_type_id

GROUP BY t.ingredient_type_id, t.ingredient_type_name;

-- ##################################################################
-- ##################################################################

-- Divs with Ingredients

WITH
   M AS (SELECT * FROM meals_in_plan),
   D AS (SELECT * FROM divided_meal_sections),
   I AS (SELECT * FROM ingredients_in_sections_of_meal_calculation)

SELECT DISTINCT
    Q.plan_id,
	Q.meal_in_plan_id,
	Q.meal_time,

	JSON_ARRAYAGG(
        JSON_OBJECT(
            'div_id', Q.div_meal_sections_id,
			'id', Q.ingredient_id
        )
    ) AS matched_ingredients

FROM
(
	SELECT M.plan_id AS p_id, M.meal_in_plan_id , M.meal_time, I.*
	FROM M
	LEFT JOIN D ON M.plan_id = D.plan_id AND M.meal_in_plan_id = D.meal_in_plan_id
 	LEFT JOIN I ON I.plan_id = D.plan_id AND I.div_meal_sections_id = D.div_meal_sections_id
) AS Q
WHERE Q.plan_id = 1
GROUP BY Q.plan_id, Q.meal_in_plan_id, Q.meal_time  /*, Q.div_meal_sections_id*/
ORDER BY Q.div_meal_sections_id;