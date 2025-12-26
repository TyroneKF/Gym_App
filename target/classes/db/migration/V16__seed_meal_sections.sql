-- ##################################################
-- Step 1.)
-- ##################################################
/*
	The Values 1-7 are discarded but, this section just inserts 7X date values 
	and union combines each row into rows for an insert
*/

INSERT INTO divided_meal_sections(div_meal_sections_id)
SELECT NULL -- 
FROM    -- Just Inserts x15     
(             	    
		-- Row generator: produces exactly 15 rows
		
		-- # Breakfast : 
		SELECT 1 UNION ALL -- #  Pancakes  
		SELECT 2 UNION ALL -- #  Eggs + Plantain    
		SELECT 3 UNION ALL -- #  Celery Drink  		
		
		-- # MidMorning :   
		SELECT 4 UNION ALL -- #  Oatmeal  
		SELECT 5 UNION ALL -- #  Bananas  
		SELECT 6 UNION ALL -- #  Mango Smoothie  
		
		--  # Lunch : 
		SELECT 7 UNION ALL -- #  Lunch Meal  
		SELECT 8 UNION ALL -- #  Nuts  
		SELECT 9 UNION ALL -- #  Multivitamin Shake  
		
		--  # Pre-Workout : 
		SELECT 10 UNION ALL -- #  Fruit Smoothie  
		
		--  # Post-Workout : 		
		SELECT 11 UNION ALL -- #  Bananas  
		SELECT 12 UNION ALL -- #  Protein Shake  
		SELECT 13 UNION ALL -- #  Veg Meal  
		
		--  # Dinner : 
		SELECT 14 UNION ALL -- #  Pasta Meal  
		
		--  # Bed Snack	
		SELECT 15          -- #  Protein Shake  
		
) AS seed;

-- ##################################################
-- Step 2.)
-- ##################################################
/*
	Step 2: “Figure out which placeholder belongs to which ID
	Job of this section: create a reliable way to refer to anchor rows without ever hard-coding their IDs.
	It only answers this question: Which anchor row should be treated as the 1st, 2nd, 3rd… meal slot?
*/

CREATE TEMPORARY TABLE tmp_div_meal_anchors 
(
    rn INT PRIMARY KEY,
    div_meal_sections_id INT NOT NULL
);

INSERT INTO tmp_div_meal_anchors  
( 
	rn, 
	div_meal_sections_id
)
WITH last_15_div_meals AS 
(
	SELECT
		 div_meal_sections_id
	FROM divided_meal_sections
	ORDER BY div_meal_sections_id DESC
	LIMIT 15

)
SELECT 

	ROW_NUMBER() OVER (ORDER BY div_meal_sections_id ASC) AS rn,    
	div_meal_sections_id
	
FROM last_15_div_meals;

-- ##################################################
-- Step 3.)
-- ##################################################
/*
	Step 3: “Attach meaning to placeholders”

*/






