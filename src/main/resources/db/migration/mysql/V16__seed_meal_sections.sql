-- ###############################################################################
-- Set Variables
-- ###############################################################################

-- Get Active Plan Version From Seed
SET @plan_Version_id := get_seed_id_by_key('plan_Version_id', 'Seed failed: @plan_Version_id could be resolved');

-- Get Meal ID's
SET @breakfast_mv_id := get_seed_id_by_key('breakfast_mv_id', 'Seed failed: @breakfast_mv_id could be resolved');
SET @mid_morning_snack_mv_id := get_seed_id_by_key('mid_morning_snack_mv_id', 'Seed failed: @mid_morning_snack_mv_id could be resolved');
SET @lunch_mv_id := get_seed_id_by_key('lunch_mv_id', 'Seed failed: @lunch_mv_id could be resolved');
SET @pre_workout_mv_id := get_seed_id_by_key('pre_workout_mv_id', 'Seed failed: @pre_workout_mv_id could be resolved');
SET @post_workout_mv_id := get_seed_id_by_key('post_workout_mv_id', 'Seed failed: @post_workout_mv_id could be resolved');
SET @dinner_mv_id := get_seed_id_by_key('dinner_mv_id', 'Seed failed: @dinner_mv_id could be resolved');
SET @bedtime_mv_id := get_seed_id_by_key('bedtime_mv_id', 'Seed failed: @bedtime_mv_id could be resolved');

-- ###############################################################################
-- Step 1.)
-- ###############################################################################
/*
	The Values 1-15 are discarded but, this section just inserts 15X ID
	and union combines each row into rows for an insert
*/

INSERT INTO divided_meal_sections
(
    div_meal_sections_id
)
SELECT NULL -- Row generator: produces exactly 15 rows
FROM
(
		-- # Breakfast :
		SELECT 1 UNION ALL -- #  Pancakes
		SELECT 2 UNION ALL -- #  Eggs + Plantain
		SELECT 3 UNION ALL -- #  Celery Drink

		-- # Mid-Morning :
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

		--  # Bedtime
		SELECT 15          -- #  Protein Shake

) AS seed;


-- ###############################################################################
-- Step 2.)
-- ###############################################################################
/*
	Step 2: “Figure out which placeholder belongs to which ID
	Job of this section: create a reliable way to refer to anchor rows without ever hard-coding their IDs.
	It only answers this question: Which anchor row should be treated as the 1st, 2nd, 3rd… meal section slot?
*/

-- ###########################
-- Create Anchor Table
-- ###########################
CREATE TEMPORARY TABLE tmp_div_meal_anchors
(
    rn INT PRIMARY KEY,
    div_meal_sections_id INT NOT NULL
);

-- ###########################
-- Insert Into Anchor Table
-- ###########################
INSERT INTO tmp_div_meal_anchors
(
	rn,
	div_meal_sections_id
)
WITH last_15_div_meals AS
(
	SELECT div_meal_sections_id
	FROM divided_meal_sections
	ORDER BY div_meal_sections_id DESC
	LIMIT 15
)
SELECT

	ROW_NUMBER() OVER (ORDER BY div_meal_sections_id ASC) AS rn,
	div_meal_sections_id

FROM last_15_div_meals;

-- ###############################################################################
-- Step 3.) Insert Into divided_meal_sections_versions
-- ###############################################################################
/*
	Step 3: “Attach meaning to placeholders”
	Sections are snapshot per meal version; no independent section versioning
*/

INSERT INTO divided_meal_sections_versions
(
    div_meal_sections_id,
    meal_in_plan_version_id,
	date_time_last_edited,
	sub_meal_name
)
SELECT

    d.div_meal_sections_id,
    v.meal_id,
    NOW(6),
    v.sub_name

FROM tmp_div_meal_anchors  d
JOIN
(
    -- # Breakfast :
    SELECT 1 AS rn,   @breakfast_mv_id AS meal_id,  'Pancakes' AS sub_name  UNION ALL
    SELECT 2 ,        @breakfast_mv_id ,            'Eggs + Plantain'       UNION ALL
    SELECT 3 ,        @breakfast_mv_id ,            'Celery Drink'          UNION ALL

    -- # Mid-Morning :
    SELECT 4 ,        @mid_morning_snack_mv_id ,    'Oatmeal'               UNION ALL
    SELECT 5 ,        @mid_morning_snack_mv_id ,    'Bananas'               UNION ALL
    SELECT 6 ,        @mid_morning_snack_mv_id ,    'Mango Smoothie'        UNION ALL

    --  # Lunch :
    SELECT 7 ,        @lunch_mv_id ,                'Lunch Meal'            UNION ALL
    SELECT 8 ,        @lunch_mv_id ,                'Nuts'                  UNION ALL
    SELECT 9 ,        @lunch_mv_id ,                'Multivitamin Shake'    UNION ALL

    --  # Pre-Workout :
    SELECT 10 ,       @pre_workout_mv_id ,          'Fruit Smoothie'        UNION ALL

    --  # Post-Workout :
    SELECT 11 ,       @post_workout_mv_id ,         'Bananas'               UNION ALL
    SELECT 12 ,       @post_workout_mv_id ,         'Whey Protein Shake'    UNION ALL
    SELECT 13 ,       @post_workout_mv_id ,         'Vegetable Meal'        UNION ALL

    --  # Dinner :
    SELECT 14 ,       @dinner_mv_id ,               'Pasta Meal'            UNION ALL

    --  # Bedtime
    SELECT 15 ,      @bedtime_mv_id ,               'Casein Shake'

) v
ON v.rn = d.rn;

-- ###############################################################################
-- Step 4.) Insert Into meal_in_plan_version_id by Mapping with Anchors
-- ###############################################################################
/*
	Assign div_meal_sections_version_id ID to table of the last 15 inserted sub meals
*/

-- ##################################
-- Create Temporary Table for Meals
-- ##################################
CREATE TEMPORARY TABLE tmp_div_meal_section_version_anchors
(
	rn INT PRIMARY KEY,
    div_meal_sections_version_id INT NOT NULL
);

-- ##################################
-- Insert Into Anchor Table
-- ##################################
INSERT INTO tmp_div_meal_section_version_anchors
(
	rn,
	div_meal_sections_version_id
)
WITH last_15_sub_meals_vs AS
(
	SELECT
		 div_meal_sections_version_id
	FROM divided_meal_sections_versions
	ORDER BY div_meal_sections_version_id DESC
	LIMIT 15
)
SELECT

	ROW_NUMBER() OVER (ORDER BY div_meal_sections_version_id ASC) AS rn,
	div_meal_sections_version_id

FROM last_15_sub_meals_vs;


-- #####################################################################################################################
-- 5.) Set Variables
-- #####################################################################################################################
/*
   Set Variables for the last 15 sub Meals from the table above
*/

-- ########################################################
-- Breakfast : Sub-Meal ID's
-- ########################################################
/*
   These are all the sub-meal in Breakfast Meal

*/

-- ##############################
-- Pancakes
-- ##############################
SELECT div_meal_sections_version_id
INTO @pancakes_breakfast_div_vs_id
FROM tmp_div_meal_section_version_anchors
ORDER BY div_meal_sections_version_id
LIMIT 1 OFFSET 0;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'pancakes_breakfast_div_vs_id',
    'divided_meal_sections_versions',
    @pancakes_breakfast_div_vs_id,
    'Seed failed: divided_meal_sections_versions @pancakes_breakfast_div_vs_id could not be resolved'
);

-- ##############################
-- Eggs + Plantain
-- ##############################
SELECT div_meal_sections_version_id
INTO @eggs_breakfast_div_vs_id
FROM tmp_div_meal_section_version_anchors
ORDER BY div_meal_sections_version_id
LIMIT 1 OFFSET 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'eggs_breakfast_div_vs_id',
    'divided_meal_sections_versions',
    @eggs_breakfast_div_vs_id,
    'Seed failed: divided_meal_sections_versions @eggs_breakfast_div_vs_id could not be resolved'
);
-- ##############################
-- Celery Drink
-- ##############################
SELECT div_meal_sections_version_id
INTO @celery_breakfast_div_vs_id
FROM tmp_div_meal_section_version_anchors
ORDER BY div_meal_sections_version_id
LIMIT 1 OFFSET 2;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'celery_breakfast_div_vs_id',
    'divided_meal_sections_versions',
    @celery_breakfast_div_vs_id,
    'Seed failed: divided_meal_sections_versions @celery_breakfast_div_vs_id could not be resolved'
);

-- ########################################################
-- Mid-Morning : Sub-Meal ID's
-- ########################################################
/*
   These are all the sub-meals in Mid-morning meal

*/

-- ##############################
-- Oatmeal
-- ##############################
SELECT div_meal_sections_version_id
INTO @oatmeal_mid_morning_div_vs_id
FROM tmp_div_meal_section_version_anchors
ORDER BY div_meal_sections_version_id
LIMIT 1 OFFSET 3;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'oatmeal_mid_morning_div_vs_id',
    'divided_meal_sections_versions',
    @oatmeal_mid_morning_div_vs_id,
    'Seed failed: divided_meal_sections_versions @oatmeal_mid_morning_div_vs_id could not be resolved'
);

-- ##############################
-- Bananas
-- ##############################
SELECT div_meal_sections_version_id
INTO @bananas_mid_morning_div_vs_id
FROM tmp_div_meal_section_version_anchors
ORDER BY div_meal_sections_version_id
LIMIT 1 OFFSET 4;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'bananas_mid_morning_div_vs_id',
    'divided_meal_sections_versions',
    @bananas_mid_morning_div_vs_id,
    'Seed failed: divided_meal_sections_versions @bananas_mid_morning_div_vs_id could not be resolved'
);

-- ##############################
-- Mango Smoothie
-- ##############################
SELECT div_meal_sections_version_id
INTO @mango_smoothie_mid_morning_div_vs_id
FROM tmp_div_meal_section_version_anchors
ORDER BY div_meal_sections_version_id
LIMIT 1 OFFSET 5;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'mango_smoothie_mid_morning_div_vs_id',
    'divided_meal_sections_versions',
    @mango_smoothie_mid_morning_div_vs_id,
    'Seed failed: divided_meal_sections_versions @mango_smoothie_mid_morning_div_vs_id could not be resolved'
);

-- ########################################################
-- Lunch : Sub-Meal ID's
-- ########################################################
/*
   These are all the sub-meal in Lunch Meal

*/

-- ##############################
-- Lunch Meal
-- ##############################
SELECT div_meal_sections_version_id
INTO @lunch_meal_lunch_div_vs_id
FROM tmp_div_meal_section_version_anchors
ORDER BY div_meal_sections_version_id
LIMIT 1 OFFSET 6;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'lunch_meal_lunch_div_vs_id',
    'divided_meal_sections_versions',
    @lunch_meal_lunch_div_vs_id,
    'Seed failed: divided_meal_sections_versions @lunch_meal_lunch_div_vs_id could not be resolved'
);

-- ##############################
-- Nuts
-- ##############################
SELECT div_meal_sections_version_id
INTO @nuts_lunch_div_vs_id
FROM tmp_div_meal_section_version_anchors
ORDER BY div_meal_sections_version_id
LIMIT 1 OFFSET 7;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'nuts_lunch_div_vs_id',
    'divided_meal_sections_versions',
    @nuts_lunch_div_vs_id,
    'Seed failed: divided_meal_sections_versions @nuts_lunch_div_vs_id could not be resolved'
);

-- ##############################
-- Multivitamin Shake
-- ##############################
SELECT div_meal_sections_version_id
INTO @multivitamin_shake_lunch_div_vs_id
FROM tmp_div_meal_section_version_anchors
ORDER BY div_meal_sections_version_id
LIMIT 1 OFFSET 8;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'multivitamin_shake_lunch_div_vs_id',
    'divided_meal_sections_versions',
    @multivitamin_shake_lunch_div_vs_id,
    'Seed failed: divided_meal_sections_versions @multivitamin_shake_lunch_div_vs_id could not be resolved'
);

-- ########################################################
-- Pre-Workout : Sub-Meal ID's
-- ########################################################
/*
   These are all the sub-meal in Pre-Workout Meal

*/

-- ##############################
-- Fruit Smoothie
-- ##############################
SELECT div_meal_sections_version_id
INTO @fruit_smoothie_pre_workout_div_vs_id
FROM tmp_div_meal_section_version_anchors
ORDER BY div_meal_sections_version_id
LIMIT 1 OFFSET 9;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'fruit_smoothie_pre_workout_div_vs_id',
    'divided_meal_sections_versions',
    @fruit_smoothie_pre_workout_div_vs_id,
    'Seed failed: divided_meal_sections_versions @fruit_smoothie_pre_workout_div_vs_id could not be resolved'
);

-- ########################################################
-- Post-Workout : Sub-Meal ID's
-- ########################################################
/*
   These are all the sub-meal in Dinner Meal

*/

-- ##############################
-- Post-Workout - Bananas
-- ##############################
SELECT div_meal_sections_version_id
INTO @bananas_post_workout_div_vs_id
FROM tmp_div_meal_section_version_anchors
ORDER BY div_meal_sections_version_id
LIMIT 1 OFFSET 10;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'bananas_post_workout_div_vs_id',
    'divided_meal_sections_versions',
    @bananas_post_workout_div_vs_id,
    'Seed failed: divided_meal_sections_versions @bananas_post_workout_div_vs_id could not be resolved'
);

-- ##############################
-- Post-Workout - Protein Shake
-- ##############################
SELECT div_meal_sections_version_id
INTO @protein_shake_post_workout_div_vs_id
FROM tmp_div_meal_section_version_anchors
ORDER BY div_meal_sections_version_id
LIMIT 1 OFFSET 11;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'protein_shake_post_workout_div_vs_id',
    'divided_meal_sections_versions',
    @protein_shake_post_workout_div_vs_id,
    'Seed failed: divided_meal_sections_versions @protein_shake_post_workout_div_vs_id could not be resolved'
);

-- ##############################
-- Post-Workout - Veg Meal
-- ##############################
SELECT div_meal_sections_version_id
INTO  @veg_meal_post_workout_div_vs_id
FROM tmp_div_meal_section_version_anchors
ORDER BY div_meal_sections_version_id
LIMIT 1 OFFSET 12;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'veg_meal_post_workout_div_vs_id',
    'divided_meal_sections_versions',
    @veg_meal_post_workout_div_vs_id,
    'Seed failed: divided_meal_sections_versions @veg_meal_post_workout_div_vs_id could not be resolved'
);

-- ########################################################
-- Dinner : Sub-Meal ID's
-- ########################################################
/*
   These are all the sub-meal in Dinner Meal

*/

-- ##############################
-- Dinner - Pasta Meal
-- ##############################
SELECT div_meal_sections_version_id
INTO @dinner_div_vs_id
FROM tmp_div_meal_section_version_anchors
ORDER BY div_meal_sections_version_id
LIMIT 1 OFFSET 13;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'dinner_div_vs_id',
    'divided_meal_sections_versions',
    @dinner_div_vs_id,
    'Seed failed: divided_meal_sections_versions @dinner_div_vs_id could not be resolved'
);

-- ########################################################
-- Bed Snack : Sub-Meal ID's
-- ########################################################
/*
   These are all the sub-meal in Pre-Workout Meal

*/

-- ##############################
-- Protein Shake
-- ##############################
SELECT div_meal_sections_version_id
INTO @protein_shake_bedtime_vs_id
FROM tmp_div_meal_section_version_anchors
ORDER BY div_meal_sections_version_id
LIMIT 1 OFFSET 14;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'protein_shake_bedtime_vs_id',
    'divided_meal_sections_versions',
    @protein_shake_bedtime_vs_id,
    'Seed failed: divided_meal_sections_versions @protein_shake_bedtime_vs_id could not be resolved'
);