-- ##################################################
-- Step 1.)
-- ##################################################
/*
	The Values 1-7 are discarded but, this section just inserts 7X date values 
	and union combines each row into rows for an insert
*/

INSERT INTO meals_in_plan 
(
	date_time_of_creation 
)
SELECT NOW(6) -- Put the current timestamp into date_time_of_creation with microsecond pricision
FROM          -- EACH SELECT produces one row and union combines them all to produce an insert statement x 7
(             
	    -- Just Inserts x7 
		-- Row generator: produces exactly 7 rows
		-- Values are discarded; they do NOT represent meal IDs or meanings
		SELECT 1 UNION ALL -- 1  # For Breakfast later
		SELECT 2 UNION ALL -- 2  # For MidMorning later   
		SELECT 3 UNION ALL -- 3  # For Lunch later 
		SELECT 4 UNION ALL -- 4  # For Pre-Workout later 
		SELECT 5 UNION ALL -- 5  # For Post - Workout later 
		SELECT 6 UNION ALL -- 6  # For Dinner later 
		SELECT 7           -- 7  # For Bed Snack later 
		
) AS seed;

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
WITH last_7_meals AS 
(
	SELECT
		meal_in_plan_id
	FROM meals_in_plan
	ORDER BY meal_in_plan_id DESC
	LIMIT 7

)
SELECT 

	ROW_NUMBER() OVER (ORDER BY meal_in_plan_id ASC) AS rn,    
	meal_in_plan_id
	
FROM last_7_meals;

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
/*
	Step 3: “Attach meaning to placeholders”

*/

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
    SELECT 1 AS rn,  'Breakfast1'    AS meal_name, '08:20' AS meal_time UNION ALL
    SELECT 2,        'Mid Morning1',               '10:10' UNION ALL
    SELECT 3,        'Lunch1',                     '13:20' UNION ALL
    SELECT 4,        'Pre Workout1',               '16:40' UNION ALL
    SELECT 5,        'Post Workout1',              '18:50' UNION ALL
    SELECT 6,        'Dinner1',                    '20:40' UNION ALL
    SELECT 7,        'Bed Snack1',                 '22:20'
) v ON v.rn = a.rn;

-- ##################################################
-- Step 4.) Map meal_in_plan_version_id
-- ##################################################
/*
	Assign Macro Version ID
*/

-- Create Temporary Table for Meals just inserted

CREATE TEMPORARY TABLE tmp_meal_version_anchors 
( 
	rn INT PRIMARY KEY,
    meal_in_plan_version_id INT NOT NULL
);

INSERT INTO tmp_meal_version_anchors 
( 
	rn, 
	meal_in_plan_version_id
)
WITH last_7_meals_vs AS 
(
	SELECT
		 meal_in_plan_version_id
	FROM meals_in_plan_versions
	ORDER BY meal_in_plan_version_id DESC
	LIMIT 7

)
SELECT 

	ROW_NUMBER() OVER (ORDER BY meal_in_plan_version_id ASC) AS rn,    
	meal_in_plan_version_id
	
FROM last_7_meals_vs;


-- ##########################
-- Breakfast Meal ID \! cls;
-- ##########################

-- Assign Breakfast Meal ID
SELECT meal_in_plan_version_id
INTO @breakfast_mv_id
FROM tmp_meal_version_anchors 
ORDER BY meal_in_plan_version_id
LIMIT 1 OFFSET 0;

CALL assert_id_not_null(@breakfast_mv_id, 'Seed failed: meal_in_plan_versions @breakfast_mv_id could not be resolved');

-- ##########################
-- Mid-Morning Snack Meal ID 
-- ##########################

-- Assign Mid-Morning Snack Meal ID
SELECT meal_in_plan_version_id
INTO @mid_morning_snack_mv_id
FROM tmp_meal_version_anchors 
ORDER BY meal_in_plan_version_id
LIMIT 1 OFFSET 1;

CALL assert_id_not_null(@mid_morning_snack_mv_id, 'Seed failed: meal_in_plan_versions @mid_morning_snack_mv_id could not be resolved');

-- ##########################
-- Lunch Meal ID 
-- ##########################

-- Assign Lunch ID
SELECT meal_in_plan_version_id
INTO @lunch_mv_id
FROM tmp_meal_version_anchors 
ORDER BY meal_in_plan_version_id
LIMIT 1 OFFSET 2;

CALL assert_id_not_null(@lunch_mv_id, 'Seed failed: meal_in_plan_versions @lunch_mv_id could not be resolved');

-- ##########################
-- Pre-Workout ID 
-- ##########################

-- Assign Lunch ID
SELECT meal_in_plan_version_id
INTO @pre_workout_mv_id
FROM tmp_meal_version_anchors 
ORDER BY meal_in_plan_version_id
LIMIT 1 OFFSET 3;

CALL assert_id_not_null(@pre_workout_mv_id, 'Seed failed: meal_in_plan_versions @pre_workout_mv_id could not be resolved');

-- ##########################
-- Post-Workout ID 
-- ##########################

-- Assign Post-Workout ID
SELECT meal_in_plan_version_id
INTO @post_workout_mv_id
FROM tmp_meal_version_anchors 
ORDER BY meal_in_plan_version_id
LIMIT 1 OFFSET 4;

CALL assert_id_not_null(@post_workout_mv_id, 'Seed failed: meal_in_plan_versions @post_workout_mv_id could not be resolved');

-- ##########################
-- Dinner ID 
-- ##########################

-- Assign Dinner ID
SELECT meal_in_plan_version_id
INTO @dinner_mv_id
FROM tmp_meal_version_anchors 
ORDER BY meal_in_plan_version_id
LIMIT 1 OFFSET 5;

CALL assert_id_not_null(@dinner_mv_id, 'Seed failed: meal_in_plan_versions @dinner_mv_id could not be resolved');

-- ##########################
-- Bed Snack ID 
-- ##########################

-- Assign Dinner ID
SELECT meal_in_plan_version_id
INTO @bed_snack_mv_id
FROM tmp_meal_version_anchors 
ORDER BY meal_in_plan_version_id
LIMIT 1 OFFSET 6;

CALL assert_id_not_null(@bed_snack_mv_id, 'Seed failed: meal_in_plan_versions @bed_snack_mv_id could not be resolved');






















