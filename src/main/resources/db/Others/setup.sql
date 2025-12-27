DROP DATABASE IF EXISTS gymapp00001;
CREATE DATABASE gymapp00001;

USE gymapp00001;







-- ##############################
-- Eggs + Plantain
-- ##############################
SELECT div_meal_sections_version_id
INTO @eggs_breakfast_div_vs_id
FROM tmp_div_meal_section_version_anchors
ORDER BY div_meal_sections_version_id
LIMIT 1 OFFSET 2;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'eggs_breakfast_div_vs_id',
    'divided_meal_sections_versions',
    @eggs_breakfast_div_vs_id
    'Seed failed: divided_meal_sections_versions @eggs_breakfast_div_vs_id could not be resolved'
);

-- ##############################
-- Celery Drink
-- ##############################
SELECT div_meal_sections_version_id
INTO @celery_breakfast_div_vs_id
FROM tmp_div_meal_section_version_anchors
ORDER BY div_meal_sections_version_id
LIMIT 1 OFFSET 3;

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
LIMIT 1 OFFSET 4;

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
LIMIT 1 OFFSET 5;

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
LIMIT 1 OFFSET 6;

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
LIMIT 1 OFFSET 7;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    '@lunch_meal_lunch_div_vs_id',
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
LIMIT 1 OFFSET 8;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    '@nuts_lunch_div_vs_id',
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
LIMIT 1 OFFSET 9;

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
LIMIT 1 OFFSET 10;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    '@fruit_smoothie_pre_workout_div_vs_id',
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
LIMIT 1 OFFSET 11;

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
LIMIT 1 OFFSET 12;

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
LIMIT 1 OFFSET 13;

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
LIMIT 1 OFFSET 14;

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
LIMIT 1 OFFSET 15;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    '@protein_shake_bedtime_vs_id',
    'divided_meal_sections_versions',
    @protein_shake_bedtime_vs_id,
    'Seed failed: divided_meal_sections_versions @protein_shake_bedtime_vs_id could not be resolved'
);