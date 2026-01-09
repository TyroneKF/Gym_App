-- ##############################################################################################################
-- DDL SCRIPT | App Setup
-- ##############################################################################################################
   /*       
       A user can only have one draft plan active
       A user can have multiple meals per draft_plan but, these meals aren't versioned in drafts
       One draft edit exists per meal
   */

-- ##############################################################################################################
-- Document Versions
-- ##############################################################################################################
    CREATE TABLE draft_meals_in_plan
    (
        -- Enforces one draft per draft meal as this is the FK / Unique
        draft_meal_in_plan_id INTEGER PRIMARY KEY AUTOINCREMENT,

        meal_in_plan_id INTEGER NULL DEFAULT NULL, -- FK has to be defined at the bottom
                                                   -- Can be null might not have a version to attach to

        plan_id INTEGER NOT NULL,     -- FK has to be defined at the bottom     
                                      -- Application layer enforcement has to be done to ensure
                                      -- A draft meal linked to a versioned meal must belong to the same plan as that meal’s plan version.

        date_time_last_edited TEXT NOT NULL
            DEFAULT (strftime('%Y-%m-%d %H:%M:%f', 'now')), -- Defined on Insertion

        meal_name TEXT NOT NULL
            CHECK (length(meal_name) <= 100),

        meal_time TEXT NOT NULL   -- TIME does not exist in SQLite; store as ISO time TEXT (HH:MM or HH:MM:SS)
            CHECK (meal_time GLOB '[0-2][0-9]:[0-5][0-9]*'),

        -- Foreign Keys (must be declared at the end in SQLite)
        FOREIGN KEY (meal_in_plan_id)
            REFERENCES meals_in_plan(meal_in_plan_id)
                ON DELETE CASCADE,

        FOREIGN KEY (plan_id)
            REFERENCES draft_plans(plan_id)
                ON DELETE CASCADE
    );

    -- ####################################################
    -- Constraints (Unique Keys)
    -- ####################################################

        -- Only one meal per time per plan
        CREATE UNIQUE INDEX no_repeat_draft_meal_times_in_plan
            ON draft_meals_in_plan (plan_id, meal_time);

        -- No duplicate meal names per plan
        CREATE UNIQUE INDEX no_repeat_draft_meal_names_in_plan
            ON draft_meals_in_plan (plan_id, meal_name);

    -- ####################################################
    -- Constraints (Unique Keys)
    -- ####################################################
       -- Creates an index when value exists
       CREATE UNIQUE INDEX one_draft_row_per_versioned_meal
            ON draft_meals_in_plan (meal_in_plan_id)
                WHERE meal_in_plan_id IS NOT NULL;