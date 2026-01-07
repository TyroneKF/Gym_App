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
        draft_meal_in_plan_id INTEGER PRIMARY KEY AUTOINCREMENT,

        draft_plan_id INTEGER NOT NULL,     -- FK has to be defined at the bottom
        meal_in_plan_id INTEGER NULL, -- FK has to be defined at the bottom

        meal_name TEXT NOT NULL
            CHECK (length(meal_name) <= 100),

        -- TIME does not exist in SQLite; store as ISO time TEXT (HH:MM or HH:MM:SS)
        meal_time TEXT NOT NULL,

        -- Foreign Keys (must be declared at the end in SQLite)
        FOREIGN KEY (meal_in_plan_id)
            REFERENCES meals_in_plan(meal_in_plan_id)
                ON DELETE CASCADE,

        FOREIGN KEY (draft_plan_id)
            REFERENCES draft_plans(draft_plan_id)
                ON DELETE CASCADE
    );

    -- ####################################################
    -- Constraints (Unique Keys)
    -- ####################################################

        -- One version of a meal per plan
        CREATE UNIQUE INDEX one_draft_per_meal_in_plan
            ON draft_meals_in_plan (meal_in_plan_id);

        -- Only one meal per time per plan
        CREATE UNIQUE INDEX no_repeat_draft_meal_times_in_plan
            ON draft_meals_in_plan (draft_plan_id, meal_time);

        -- No duplicate meal names per plan
        CREATE UNIQUE INDEX no_repeat_draft_meal_names_in_plan
            ON draft_meals_in_plan (draft_plan_id, meal_name);

    -- ####################################################
    -- Indexes
    -- ####################################################

        CREATE INDEX idx_draft_meals_by_name_in_plan
            ON draft_meals_in_plan (draft_plan_id, meal_name);

        CREATE INDEX idx_draft_meals_by_time_in_plan
            ON draft_meals_in_plan (draft_plan_id, meal_time);

        CREATE INDEX idx_draft_meal_versions_plan_version
            ON draft_meals_in_plan (draft_plan_id);
