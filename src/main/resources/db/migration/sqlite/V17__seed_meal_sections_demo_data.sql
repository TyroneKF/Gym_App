-- ###############################################################################
-- Step 1.)
-- ###############################################################################
    /*
        The Values 1-15 are discarded but, this section just inserts 15X ID
        and union combines each row into rows for an insert
    */

    INSERT INTO divided_meal_sections
    (
        date_time_of_creation
    )
    SELECT strftime('%Y-%m-%dT%H:%M:%f', 'now') -- Put the current timestamp into date_time_of_creation with microsecond precision
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

    WITH
        breakfast_mv_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'breakfast_mv_id'),
        mid_morning_snack_mv_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'mid_morning_snack_mv_id'),
        lunch_mv_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'lunch_mv_id'),
        pre_workout_mv_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'pre_workout_mv_id'),
        post_workout_mv_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'post_workout_mv_id'),
        dinner_mv_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'dinner_mv_id'),
        bedtime_mv_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'bedtime_mv_id')

    INSERT INTO divided_meal_sections_versions
    (
        div_meal_sections_id,
        meal_in_plan_version_id,
        sub_meal_name
    )
    SELECT

        d.div_meal_sections_id,
        v.meal_id,
        v.sub_name

    FROM tmp_div_meal_anchors  d
    JOIN
    (
        -- # Breakfast :
        SELECT 1 AS rn,   (SELECT entity_id_value FROM breakfast_mv_id LIMIT 1) AS meal_id,  'Pancakes' AS sub_name  UNION ALL
        SELECT 2 ,        (SELECT entity_id_value FROM breakfast_mv_id LIMIT 1) ,            'Eggs + Plantain'       UNION ALL
        SELECT 3 ,        (SELECT entity_id_value FROM breakfast_mv_id LIMIT 1) ,            'Celery Drink'          UNION ALL

        -- # Mid-Morning :
        SELECT 4 ,        (SELECT entity_id_value FROM mid_morning_snack_mv_id LIMIT 1) ,    'Oatmeal'               UNION ALL
        SELECT 5 ,        (SELECT entity_id_value FROM mid_morning_snack_mv_id LIMIT 1),     'Bananas'               UNION ALL
        SELECT 6 ,        (SELECT entity_id_value FROM mid_morning_snack_mv_id LIMIT 1) ,    'Mango Smoothie'        UNION ALL

        --  # Lunch :
        SELECT 7 ,        (SELECT entity_id_value FROM lunch_mv_id LIMIT 1) ,                'Lunch Meal'            UNION ALL
        SELECT 8 ,        (SELECT entity_id_value FROM lunch_mv_id LIMIT 1) ,                'Nuts'                  UNION ALL
        SELECT 9 ,        (SELECT entity_id_value FROM lunch_mv_id LIMIT 1) ,                'Multivitamin Shake'    UNION ALL

        --  # Pre-Workout :
        SELECT 10 ,       (SELECT entity_id_value FROM pre_workout_mv_id LIMIT 1) ,          'Fruit Smoothie'        UNION ALL

        --  # Post-Workout :
        SELECT 11 ,       (SELECT entity_id_value FROM post_workout_mv_id LIMIT 1) ,         'Bananas'               UNION ALL
        SELECT 12 ,       (SELECT entity_id_value FROM post_workout_mv_id LIMIT 1) ,         'Whey Protein Shake'    UNION ALL
        SELECT 13 ,       (SELECT entity_id_value FROM post_workout_mv_id LIMIT 1) ,         'Vegetable Meal'        UNION ALL

        --  # Dinner :
        SELECT 14 ,       (SELECT entity_id_value FROM dinner_mv_id LIMIT 1) ,               'Pasta Meal'            UNION ALL

        --  # Bedtime
        SELECT 15 ,      (SELECT entity_id_value FROM bedtime_mv_id LIMIT 1) ,               'Casein Shake'

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
-- 5.) Insert Into Seed Registry Table
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
        INSERT INTO seed_registry(seed_key,entity_table_name,entity_id_value)
        VALUES
        (
          'pancakes_breakfast_div_vs_id',
          'divided_meal_sections_versions',
          (
             SELECT div_meal_sections_version_id
             FROM tmp_div_meal_section_version_anchors
             ORDER BY div_meal_sections_version_id
             LIMIT 1 OFFSET 0
          )
        )
        ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            entity_id_value = excluded.entity_id_value; -- On update has to be on the the PK or unique Key

    -- ##############################
    -- Eggs + Plantain
    -- ##############################
        INSERT INTO seed_registry(seed_key,entity_table_name,entity_id_value)
        VALUES
        (
          'eggs_breakfast_div_vs_id',
          'divided_meal_sections_versions',
          (
             SELECT div_meal_sections_version_id
             FROM tmp_div_meal_section_version_anchors
             ORDER BY div_meal_sections_version_id
             LIMIT 1 OFFSET 1
          )
        )
        ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            entity_id_value = excluded.entity_id_value; -- On update has to be on the the PK or unique Key

    -- ##############################
    -- Celery Drink
    -- ##############################
        INSERT INTO seed_registry(seed_key,entity_table_name,entity_id_value)
        VALUES
        (
          'celery_breakfast_div_vs_id',
          'divided_meal_sections_versions',
          (
             SELECT div_meal_sections_version_id
             FROM tmp_div_meal_section_version_anchors
             ORDER BY div_meal_sections_version_id
             LIMIT 1 OFFSET 2
          )
        )
        ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            entity_id_value = excluded.entity_id_value; -- On update has to be on the the PK or unique Key

-- ########################################################
-- Mid-Morning : Sub-Meal ID's
-- ########################################################
        /*
           These are all the sub-meals in Mid-morning meal

        */

    -- ##############################
    -- Oatmeal
    -- ##############################
        INSERT INTO seed_registry(seed_key,entity_table_name,entity_id_value)
        VALUES
        (
          'oatmeal_mid_morning_div_vs_id',
          'divided_meal_sections_versions',
          (
             SELECT div_meal_sections_version_id
             FROM tmp_div_meal_section_version_anchors
             ORDER BY div_meal_sections_version_id
             LIMIT 1 OFFSET 3
          )
        )
        ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            entity_id_value = excluded.entity_id_value; -- On update has to be on the the PK or unique Key

    -- ##############################
    -- Bananas
    -- ##############################
        INSERT INTO seed_registry(seed_key,entity_table_name,entity_id_value)
        VALUES
        (
          'bananas_mid_morning_div_vs_id',
          'divided_meal_sections_versions',
          (
             SELECT div_meal_sections_version_id
             FROM tmp_div_meal_section_version_anchors
             ORDER BY div_meal_sections_version_id
             LIMIT 1 OFFSET 4
          )
        )
        ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            entity_id_value = excluded.entity_id_value; -- On update has to be on the the PK or unique Key

    -- ##############################
    -- Mango Smoothie
    -- ##############################
        INSERT INTO seed_registry(seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'mango_smoothie_mid_morning_div_vs_id',
          'divided_meal_sections_versions',
          (
             SELECT div_meal_sections_version_id
             FROM tmp_div_meal_section_version_anchors
             ORDER BY div_meal_sections_version_id
             LIMIT 1 OFFSET 5
          )
        )
        ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            entity_id_value = excluded.entity_id_value; -- On update has to be on the the PK or unique Key

-- ########################################################
-- Lunch : Sub-Meal ID's
-- ########################################################
        /*
           These are all the sub-meal in Lunch Meal

        */

    -- ##############################
    -- Lunch Meal
    -- ##############################
        INSERT INTO seed_registry(seed_key,entity_table_name,entity_id_value)
        VALUES
        (
          'lunch_meal_lunch_div_vs_id',
          'divided_meal_sections_versions',
          (
             SELECT div_meal_sections_version_id
             FROM tmp_div_meal_section_version_anchors
             ORDER BY div_meal_sections_version_id
             LIMIT 1 OFFSET 6
          )
        )
        ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            entity_id_value = excluded.entity_id_value; -- On update has to be on the the PK or unique Key

    -- ##############################
    -- Nuts
    -- ##############################
        INSERT INTO seed_registry(seed_key,entity_table_name,entity_id_value)
        VALUES
        (
          'nuts_lunch_div_vs_id',
          'divided_meal_sections_versions',
          (
             SELECT div_meal_sections_version_id
             FROM tmp_div_meal_section_version_anchors
             ORDER BY div_meal_sections_version_id
             LIMIT 1 OFFSET 7
          )
        )
        ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            entity_id_value = excluded.entity_id_value; -- On update has to be on the the PK or unique Key

    -- ##############################
    -- Multivitamin Shake
    -- ##############################
        INSERT INTO seed_registry(seed_key,entity_table_name,entity_id_value)
        VALUES
        (
          'multivitamin_shake_lunch_div_vs_id',
          'divided_meal_sections_versions',
          (
             SELECT div_meal_sections_version_id
             FROM tmp_div_meal_section_version_anchors
             ORDER BY div_meal_sections_version_id
             LIMIT 1 OFFSET 8
          )
        )
        ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            entity_id_value = excluded.entity_id_value; -- On update has to be on the the PK or unique Key

-- ########################################################
-- Pre-Workout : Sub-Meal ID's
-- ########################################################
    /*
       These are all the sub-meal in Pre-Workout Meal

    */

    -- ##############################
    -- Fruit Smoothie
    -- ##############################
        INSERT INTO seed_registry(seed_key,entity_table_name,entity_id_value)
        VALUES
        (
          'fruit_smoothie_pre_workout_div_vs_id',
          'divided_meal_sections_versions',
          (
             SELECT div_meal_sections_version_id
             FROM tmp_div_meal_section_version_anchors
             ORDER BY div_meal_sections_version_id
             LIMIT 1 OFFSET 9
          )
        )
        ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            entity_id_value = excluded.entity_id_value; -- On update has to be on the the PK or unique Key

-- ########################################################
-- Post-Workout : Sub-Meal ID's
-- ########################################################
    /*
       These are all the sub-meal in Dinner Meal

    */

    -- ##############################
    -- Post-Workout - Bananas
    -- ##############################
        INSERT INTO seed_registry(seed_key,entity_table_name,entity_id_value)
        VALUES
        (
          'bananas_post_workout_div_vs_id',
          'divided_meal_sections_versions',
          (
             SELECT div_meal_sections_version_id
             FROM tmp_div_meal_section_version_anchors
             ORDER BY div_meal_sections_version_id
             LIMIT 1 OFFSET 10
          )
        )
        ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            entity_id_value = excluded.entity_id_value; -- On update has to be on the the PK or unique Key

    -- ##############################
    -- Post-Workout - Protein Shake
    -- ##############################
        INSERT INTO seed_registry(seed_key,entity_table_name,entity_id_value)
        VALUES
        (
          'protein_shake_post_workout_div_vs_id',
          'divided_meal_sections_versions',
          (
             SELECT div_meal_sections_version_id
             FROM tmp_div_meal_section_version_anchors
             ORDER BY div_meal_sections_version_id
             LIMIT 1 OFFSET 11
          )
        )
        ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            entity_id_value = excluded.entity_id_value; -- On update has to be on the the PK or unique Key

    -- ##############################
    -- Post-Workout - Veg Meal
    -- ##############################
        INSERT INTO seed_registry(seed_key,entity_table_name,entity_id_value)
        VALUES
        (
          'veg_meal_post_workout_div_vs_id',
          'divided_meal_sections_versions',
          (
             SELECT div_meal_sections_version_id
             FROM tmp_div_meal_section_version_anchors
             ORDER BY div_meal_sections_version_id
             LIMIT 1 OFFSET 12
          )
        )
        ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            entity_id_value = excluded.entity_id_value; -- On update has to be on the the PK or unique Key

-- ########################################################
-- Dinner : Sub-Meal ID's
-- ########################################################
        /*
           These are all the sub-meal in Dinner Meal

        */

        -- ##############################
        -- Dinner - Pasta Meal
        -- ##############################
            INSERT INTO seed_registry(seed_key,entity_table_name,entity_id_value)
            VALUES
            (
              'dinner_div_vs_id',
              'divided_meal_sections_versions',
              (
                 SELECT div_meal_sections_version_id
                 FROM tmp_div_meal_section_version_anchors
                 ORDER BY div_meal_sections_version_id
                 LIMIT 1 OFFSET 13
              )
            )
            ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
                DO UPDATE SET
                entity_id_value = excluded.entity_id_value; -- On update has to be on the the PK or unique Key

-- ########################################################
-- Bed Snack : Sub-Meal ID's
-- ########################################################
    /*
       These are all the sub-meal in Pre-Workout Meal

    */

    -- ##############################
    -- Protein Shake
    -- ##############################
        INSERT INTO seed_registry(seed_key,entity_table_name,entity_id_value)
        VALUES
        (
          'protein_shake_bedtime_vs_id',
          'divided_meal_sections_versions',
          (
             SELECT div_meal_sections_version_id
             FROM tmp_div_meal_section_version_anchors
             ORDER BY div_meal_sections_version_id
             LIMIT 1 OFFSET 14
          )
        )
        ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            entity_id_value = excluded.entity_id_value; -- On update has to be on the the PK or unique Key