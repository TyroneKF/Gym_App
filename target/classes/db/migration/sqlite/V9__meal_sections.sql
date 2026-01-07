-- ##############################################################################################################
-- DDL SCRIPT | App Setup
-- ##############################################################################################################
    /*
        Sections are snapshot per meal version; no independent section versioning
    */

-- ##############################################################################################################
-- Main Document
-- ##############################################################################################################

    CREATE TABLE divided_meal_sections
    (
        div_meal_sections_id INTEGER PRIMARY KEY AUTOINCREMENT,
        date_time_of_creation TEXT NOT NULL -- Defined on Insertion
            DEFAULT (strftime('%Y-%m-%d %H:%M:%f', 'now'))
    );

-- ##############################################################################################################
-- Document Versions
-- ##############################################################################################################

    CREATE TABLE divided_meal_sections_versions
    (
        div_meal_sections_version_id INTEGER PRIMARY KEY AUTOINCREMENT,

        div_meal_sections_id INTEGER NOT NULL,    -- FK has to be defined at the bottom
        meal_in_plan_version_id INTEGER NOT NULL, -- FK has to be defined at the bottom

        date_time_last_edited TEXT NOT NULL -- Defined on Insertion
            DEFAULT (strftime('%Y-%m-%d %H:%M:%f', 'now')),

        sub_meal_name TEXT
            CHECK (length(sub_meal_name) <= 100),

        -- Foreign Keys (SQLite requires table-level declaration)
        FOREIGN KEY (div_meal_sections_id)
            REFERENCES divided_meal_sections(div_meal_sections_id)
                ON DELETE CASCADE,

        FOREIGN KEY (meal_in_plan_version_id)
            REFERENCES meals_in_plan_versions(meal_in_plan_version_id)
                ON DELETE CASCADE
    );

    -- ####################################################
    -- Constraints (Unique Keys)
    -- ####################################################
        CREATE UNIQUE INDEX no_repeated_meal_sections_vs_per_plan
            ON divided_meal_sections_versions
               (div_meal_sections_id, meal_in_plan_version_id);

    -- ####################################################
    -- Indexes
    -- ####################################################
        CREATE INDEX idx_meal_version_id
            ON divided_meal_sections_versions (meal_in_plan_version_id);
