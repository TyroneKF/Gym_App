-- #############################################################################
-- #############################################################################


-- ##################################################
-- Step 1.)
-- ##################################################
/*	
	Step 1: “Make sure placeholders exist”
	Insert 7 rows once, with a safety switch that prevents inserting again.
	
    This is effectively “insert 7 rows once.”
	The bottom part prevents inserting again.
	The numbers do not represent meals, IDs, or meanings — they only force repetition.
*/

INSERT INTO meals_in_plan 
(
	date_time_of_creation 
)
SELECT NOW(6) -- Put the current timestamp into date_time_of_creation with microsecond pricision
FROM          -- EACH SELECT produces one row and union combines them all to produce an insert statement x 7
(             
	    
		-- Row generator: produces exactly 7 rows
		-- Values are discarded; they do NOT represent meal IDs or meanings
		SELECT 1 UNION ALL -- 1  # For Breakfast later
		SELECT 2 UNION ALL -- 2  # For MidMorning later   
		SELECT 3 UNION ALL -- 3  # For Lunch later 
		SELECT 4 UNION ALL -- 4  # For Pre-Workout later 
		SELECT 5 UNION ALL -- 5  # For Post - Workout later 
		SELECT 6 UNION ALL -- 6  # For Dinner later 
		SELECT 7           -- 7  # For Bed Snack later 
		
) AS seed 
WHERE NOT EXISTS -- Safety switch: Only insert rows if the table has fewer than 7 rows
(
    SELECT 1 
	FROM meals_in_plan
    LIMIT 7
);

-- ##################################################
-- Step 2.)
-- ##################################################
/*
	Step 2: “Figure out which placeholder belongs to which ID
	Job of this section: create a reliable way to refer to anchor rows without ever hard-coding their IDs.
	It only answers this question: Which anchor row should be treated as the 1st, 2nd, 3rd… meal slot?
*/
CREATE TEMPORARY TABLE tmp_meal_anchors 
(
    rn INT PRIMARY KEY,
    meal_in_plan_id INT NOT NULL
);

INSERT INTO tmp_meal_anchors 
(
	rn, 
	meal_in_plan_id	
)
SELECT

    ROW_NUMBER() OVER (ORDER BY meal_in_plan_id) AS rn, -- for each for produces rn  | meal_in_plan_id
    meal_in_plan_id
	
FROM meals_in_plan
ORDER BY meal_in_plan_id
LIMIT 7; -- Only map for 7 anchors we care about being the 7 slots for 7 meals
/*	
tmp_meal_anchors:
	rn  | meal_in_plan_id
    _______________________
	1	| 14
	2	| 22
	3	| 31
	4	| 45
	5	| 58
	6	| 61
	7	| 79
*/





-- ##################################################
-- Step 3.)
-- ##################################################
Step 3: “Attach meaning to placeholders”



INSERT INTO meals_in_plan_versions
(
	meal_in_plan_id, 
	plan_version_id, 
	date_time_last_edited, 
	meal_name, 
	meal_time
)
SELECT

    a.meal_in_plan_id,
    @plan_version_id,
    NOW(6),
    v.meal_name,
    v.meal_time
	
FROM tmp_meal_anchors a
JOIN 
(
    SELECT 1 AS rn,  'Breakfast'    AS meal_name, '08:00' AS meal_time UNION ALL
    SELECT 2,        'Mid Morning',               '10:30' UNION ALL
    SELECT 3,        'Lunch',                     '13:00' UNION ALL
    SELECT 4,        'Pre Workout',               '16:30' UNION ALL
    SELECT 5,        'Post Workout',              '18:00' UNION ALL
    SELECT 6,        'Dinner',                    '20:00' UNION ALL
    SELECT 7,        'Bed Snack',                 '22:00'
) v ON v.rn = a.rn;
