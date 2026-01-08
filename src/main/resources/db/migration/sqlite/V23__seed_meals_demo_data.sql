-- ###############################################################################
--  Step 1.) Insert Into Meals
-- ###############################################################################
    /*
        The Values 1-7 are discarded but, this section just inserts 7X date values
        and union combines each row into rows for an insert
    */

    -- #########################################
    --
    -- #########################################
       /*
           Defines a recursive CTE that generates a sequential integer set.
           The anchor query starts the sequence at 1, and the recursive step
           repeatedly increments the value by 1, referencing the CTE itself.
           Recursion terminates when the upper bound (n < 7) is reached.
       */

        WITH RECURSIVE seq(n) AS (
           SELECT 1
           UNION ALL
           SELECT n + 1
           FROM seq
           WHERE n < 7
        )

        INSERT INTO meals_in_plan
        (
            date_time_of_creation
        )
        SELECT
            strftime('%Y-%m-%dT%H:%M:%f', 'now')
        FROM seq;

-- ###############################################################################
-- Step 2.) Create Anchors For Meals
-- ###############################################################################
    /*
        Step 2: “Figure out which placeholder belongs to which ID
        Job of this section: create a reliable way to refer to anchor rows without ever hard-coding their IDs.
        It only answers this question: Which anchor row should be treated as the 1st, 2nd, 3rd… meal slot?

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

    -- ###########################
    -- Create Anchor Table
    -- ###########################
    CREATE TEMPORARY TABLE tmp_meal_anchors
    (
        rn INT PRIMARY KEY,
        meal_in_plan_id INT NOT NULL
    );

    -- ###########################
    -- Insert Into Anchor Table
    -- ###########################
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

-- ###############################################################################
-- Step 3.) Insert Into meals_in_plan_versions
-- ###############################################################################
    /*
        Step 3: “Attach meaning to placeholders”

    */
    WITH
        p_id AS (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'plan_id') -- plan_id

    INSERT INTO meals_in_plan_versions
    (
        meal_in_plan_id,
        plan_version_id,
        meal_name,
        meal_time
    )
    SELECT

        a.meal_in_plan_id,
        (SELECT entity_id_value FROM p_id ),
        v.meal_name,
        v.meal_time

    FROM tmp_meal_anchors a
    JOIN
    (
        SELECT 1 AS rn,  'Breakfast'    AS meal_name, '08:20' AS meal_time UNION ALL
        SELECT 2,        'Mid Morning',               '10:10' UNION ALL
        SELECT 3,        'Lunch',                     '13:20' UNION ALL
        SELECT 4,        'Pre Workout',               '16:40' UNION ALL
        SELECT 5,        'Post Workout',              '18:50' UNION ALL
        SELECT 6,        'Dinner',                    '20:40' UNION ALL
        SELECT 7,        'Bedtime',                   '22:20'
    ) v
    ON v.rn = a.rn;

-- ###############################################################################
-- Step 4.) Insert Into meal_in_plan_version_id by Mapping with Anchors
-- ###############################################################################
    /*
        Assign meal_in_plan_version_id to table of the last 7 inserted meals
    */

    -- ##################################
    -- Create Temporary Table for Meals
    -- ##################################
    CREATE TEMPORARY TABLE tmp_meal_version_anchors
    (
        rn INT PRIMARY KEY,
        meal_in_plan_version_id INT NOT NULL
    );

    -- ##################################
    -- Insert Into Anchor Table
    -- ##################################
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

-- ###############################################################################
-- 5.) Insert Into Seed Registry Table
-- ###############################################################################
    /*


    */
    -- ######################################
    -- BreakFast Meal ID
    -- ######################################
        INSERT INTO seed_registry(seed_key,entity_table_name,entity_id_value)
        VALUES
        (
             'breakfast_mv_id',
             'meals_in_plan_versions',
             (
                SELECT meal_in_plan_version_id
                FROM tmp_meal_version_anchors
                ORDER BY meal_in_plan_version_id
                LIMIT 1 OFFSET 0
             )
        )
        ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            entity_id_value = excluded.entity_id_value; -- On update has to be on the the PK or unique Key

        -- ######################################
        -- Mid-Morning Snack Meal ID
       -- ######################################
       INSERT INTO seed_registry(seed_key,entity_table_name,entity_id_value)
       VALUES
        (
             'mid_morning_snack_mv_id',
             'meals_in_plan_versions',
             (
                SELECT meal_in_plan_version_id
                FROM tmp_meal_version_anchors
                ORDER BY meal_in_plan_version_id
                LIMIT 1 OFFSET 1
             )
        )
        ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            entity_id_value = excluded.entity_id_value; -- On update has to be on the the PK or unique Key

        -- ######################################
        -- Lunch Meal ID
        -- ######################################
        INSERT INTO seed_registry(seed_key,entity_table_name,entity_id_value)
        VALUES
        (
             'lunch_mv_id',
             'meals_in_plan_versions',
             (
                SELECT meal_in_plan_version_id
                FROM tmp_meal_version_anchors
                ORDER BY meal_in_plan_version_id
                LIMIT 1 OFFSET 2
             )
        )
        ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            entity_id_value = excluded.entity_id_value; -- On update has to be on the the PK or unique Key

       -- ######################################
       -- Pre-Workout ID
       -- ######################################
       INSERT INTO seed_registry(seed_key,entity_table_name,entity_id_value)
       VALUES
        (
              'pre_workout_mv_id',
              'meals_in_plan_versions',
              (
                 SELECT meal_in_plan_version_id
                 FROM tmp_meal_version_anchors
                 ORDER BY meal_in_plan_version_id
                 LIMIT 1 OFFSET 3
              )
         )
         ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
             DO UPDATE SET
             entity_id_value = excluded.entity_id_value; -- On update has to be on the the PK or unique Key

         -- ######################################
         -- Post-Workout ID
         -- ######################################
         INSERT INTO seed_registry(seed_key,entity_table_name,entity_id_value)
         VALUES
         (
              'post_workout_mv_id',
              'meals_in_plan_versions',
              (
                 SELECT meal_in_plan_version_id
                 FROM tmp_meal_version_anchors
                 ORDER BY meal_in_plan_version_id
                 LIMIT 1 OFFSET 4
              )
         )
         ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
             DO UPDATE SET
             entity_id_value = excluded.entity_id_value; -- On update has to be on the the PK or unique Key

         -- ######################################
         -- Dinner ID
         -- ######################################
         INSERT INTO seed_registry(seed_key,entity_table_name,entity_id_value)
         VALUES
         (
              'dinner_mv_id',
              'meals_in_plan_versions',
              (
                 SELECT meal_in_plan_version_id
                 FROM tmp_meal_version_anchors
                 ORDER BY meal_in_plan_version_id
                 LIMIT 1 OFFSET 5
              )
         )
         ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
             DO UPDATE SET
             entity_id_value = excluded.entity_id_value; -- On update has to be on the the PK or unique Key

         -- ######################################
         -- Bedtime ID
         -- ######################################
         INSERT INTO seed_registry(seed_key,entity_table_name,entity_id_value)
         VALUES
         (
              'bedtime_mv_id',
              'meals_in_plan_versions',
              (
                 SELECT meal_in_plan_version_id
                 FROM tmp_meal_version_anchors
                 ORDER BY meal_in_plan_version_id
                 LIMIT 1 OFFSET 6
              )
         )
        ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            entity_id_value = excluded.entity_id_value; -- On update has to be on the the PK or unique Key



















