-- ##############################################################################################################
-- DDL SCRIPT | App Setup
-- ##############################################################################################################
   /*


   */

-- ##############################################################################################################
-- Main Document
-- ##############################################################################################################

    CREATE TABLE meals_in_plan
    (
        meal_in_plan_id INTEGER PRIMARY KEY AUTOINCREMENT,

        date_time_of_creation TEXT NOT NULL -- Defined on Insertion
            DEFAULT (strftime('%Y-%m-%d %H:%M:%f', 'now')),

        correlation_uuid BLOB(16) NULL
    );

    -- ####################################################
    -- Constraints (Unique Keys)
    -- ####################################################
        CREATE UNIQUE INDEX meals_unique_uuid
            ON meals_in_plan(correlation_uuid);


-- ##############################################################################################################
-- Document Versions
-- ##############################################################################################################

    CREATE TABLE meals_in_plan_versions
    (
        meal_in_plan_version_id INTEGER PRIMARY KEY AUTOINCREMENT,
        correlation_uuid BLOB(16) NULL,

        meal_in_plan_id INTEGER NOT NULL, -- FK has to be defined at the bottom
        plan_version_id INTEGER NOT NULL, -- FK has to be defined at the bottom

        date_time_last_edited TEXT NOT NULL -- Defined on Insertion
            DEFAULT (strftime('%Y-%m-%d %H:%M:%f', 'now')),

        meal_name TEXT NOT NULL
            CHECK (length(meal_name) <= 100),

        -- TIME does not exist in SQLite; store as ISO time TEXT (HH:MM or HH:MM:SS)
        meal_time TEXT NOT NULL
            CHECK (meal_time GLOB '[0-2][0-9]:[0-5][0-9]*'),

        -- Foreign Keys (must be declared at the end in SQLite)
        FOREIGN KEY (meal_in_plan_id)
            REFERENCES meals_in_plan(meal_in_plan_id)
                ON DELETE CASCADE,

        FOREIGN KEY (plan_version_id)
            REFERENCES plan_versions(plan_version_id)
                ON DELETE CASCADE
    );

    -- ####################################################
    -- Constraints (Unique Keys)
    -- ####################################################

        CREATE UNIQUE INDEX meals_versions_unique_uuid
            ON meals_in_plan_versions(correlation_uuid);

        -- One meal can only be reference once per plan version / maybe later can be used in other plans
        CREATE UNIQUE INDEX unique_versions_per_plan
            ON meals_in_plan_versions (meal_in_plan_id, plan_version_id);

        -- Only one meal per time per plan version
        CREATE UNIQUE INDEX no_repeat_meal_times_in_plan
            ON meals_in_plan_versions (plan_version_id, meal_time);

        -- No duplicate meal names per plan version
        CREATE UNIQUE INDEX no_repeat_meal_names_in_plan
            ON meals_in_plan_versions (plan_version_id, meal_name);
