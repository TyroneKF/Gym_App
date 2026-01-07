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
        draft_div_meal_sections_id INTEGER PRIMARY KEY AUTOINCREMENT,

        div_meal_sections_id  NULL,             -- FK has to be defined at the bottom
        draft_meal_in_plan_id INTEGER NOT NULL, -- FK has to be defined at the bottom

        sub_meal_name TEXT NULL
            CHECK (length(sub_meal_name) <= 100),

        -- Foreign Keys (SQLite requires table-level declaration)
        FOREIGN KEY (div_meal_sections_id)
            REFERENCES divided_meal_sections(div_meal_sections_id)
                ON DELETE CASCADE,

        FOREIGN KEY (draft_meal_in_plan_id)
            REFERENCES draft_meals_in_plan(draft_meal_in_plan_id)
                ON DELETE CASCADE
    );

    -- ####################################################
    -- Constraints (Unique Keys)
    -- ####################################################
        CREATE UNIQUE INDEX no_repeated_draft_meal_sections_vs_per_plan
            ON draft_divided_meal_sections
               (draft_meal_in_plan_id, div_meal_sections_id);

        CREATE UNIQUE INDEX no_repeated_draft_sub_meal_names_per_meal
            ON draft_divided_meal_sections
               (draft_meal_in_plan_id, sub_meal_name);

    -- ####################################################
    -- Indexes
    -- ####################################################
        CREATE INDEX idx_draft_meal_section_id
            ON draft_divided_meal_sections (draft_meal_in_plan_id);
