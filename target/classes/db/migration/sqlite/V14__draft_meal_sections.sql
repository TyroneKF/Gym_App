-- ##############################################################################################################
-- DDL SCRIPT | App Setup
-- ##############################################################################################################
    /*

    */

-- ##############################################################################################################
-- Main Document
-- ##############################################################################################################
    CREATE TABLE draft_divided_meal_sections
    (
        draft_div_meal_sections_id INTEGER PRIMARY KEY,

        div_meal_sections_id INTEGER NULL DEFAULT NULL,   -- FK has to be defined at the bottom
                                                          -- Can be null might not have a version to attach to

        draft_meal_in_plan_id INTEGER NOT NULL, -- FK has to be defined at the bottom
        plan_id INTEGER NOT NULL,               -- FK has to be defined at the bottom

        date_time_last_edited TEXT NOT NULL
            DEFAULT (strftime('%Y-%m-%d %H:%M:%f', 'now')), -- Defined on Insertion

        sub_meal_name TEXT NULL
            CHECK (length(sub_meal_name) <= 100),

        sub_meal_time TEXT NULL -- Revert back to not null when incorporated in app lvl
            CHECK (sub_meal_time GLOB '[0-2][0-9]:[0-5][0-9]*'),

        -- Foreign Keys (SQLite requires table-level declaration)
        FOREIGN KEY (div_meal_sections_id)
            REFERENCES divided_meal_sections(div_meal_sections_id)
                ON DELETE CASCADE,

        FOREIGN KEY (draft_meal_in_plan_id)
            REFERENCES draft_meals_in_plan(draft_meal_in_plan_id)
                ON DELETE CASCADE,

        FOREIGN KEY (plan_id)
            REFERENCES draft_plans(plan_id)
                ON DELETE CASCADE
    );

    -- ####################################################
    -- Constraints (Unique Keys)
    -- ####################################################
        CREATE UNIQUE INDEX no_repeated_draft_meal_sections_vs_per_plan
            ON draft_divided_meal_sections
                (plan_id, div_meal_sections_id);

        CREATE UNIQUE INDEX no_repeated_draft_sub_meal_names_per_meal
            ON draft_divided_meal_sections
                (draft_meal_in_plan_id, sub_meal_name);

        CREATE UNIQUE INDEX no_repeated_draft_sub_meal_times_per_plan
            ON draft_divided_meal_sections
                (plan_id, sub_meal_time);

    -- ####################################################
    -- Indexes
    -- ####################################################
        CREATE INDEX idx_draft_meal_section_id
            ON draft_divided_meal_sections  (draft_meal_in_plan_id);

        CREATE INDEX idx_draft_sub_meals_per_meal_ordered_by_time
            ON draft_divided_meal_sections
                (draft_meal_in_plan_id, sub_meal_time ASC);

       -- Creates an index when value exists
       CREATE UNIQUE INDEX one_draft_row_per_versioned_sub_meal
            ON draft_divided_meal_sections (div_meal_sections_id)
                WHERE div_meal_sections_id IS NOT NULL;
