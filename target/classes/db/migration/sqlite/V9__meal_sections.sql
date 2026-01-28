-- ##############################################################################################################
-- DDL SCRIPT | App Setup
-- ##############################################################################################################
    /*



    */

-- ##############################################################################################################
-- Main Document
-- ##############################################################################################################

    CREATE TABLE divided_meal_sections
    (
        div_meal_sections_id INTEGER PRIMARY KEY AUTOINCREMENT,

        date_time_of_creation TEXT NOT NULL -- Defined on Insertion
            DEFAULT (strftime('%Y-%m-%d %H:%M:%f', 'now')),

        correlation_uuid BLOB(16) NULL
    );

    -- ####################################################
    -- Constraints (Unique Keys)
    -- ####################################################
        CREATE UNIQUE INDEX sub_meals_unique_uuid
            ON divided_meal_sections(correlation_uuid);

-- ##############################################################################################################
-- Document Versions
-- ##############################################################################################################

    CREATE TABLE divided_meal_sections_versions
    (
        div_meal_sections_version_id INTEGER PRIMARY KEY AUTOINCREMENT,
        correlation_uuid BLOB(16) NULL,

        div_meal_sections_id INTEGER NOT NULL,    -- FK has to be defined at the bottom
        meal_in_plan_version_id INTEGER NOT NULL, -- FK has to be defined at the bottom
        plan_version_id INTEGER NOT NULL,         -- FK has to be defined at the bottom

        date_time_last_edited TEXT NOT NULL -- Defined on Insertion
            DEFAULT (strftime('%Y-%m-%d %H:%M:%f', 'now')),

        sub_meal_name TEXT
            CHECK (length(sub_meal_name) <= 100),

        sub_meal_time TEXT NOT NULL
            CHECK (sub_meal_time GLOB '[0-2][0-9]:[0-5][0-9]*'),

        -- Foreign Keys (SQLite requires table-level declaration)
        FOREIGN KEY (div_meal_sections_id)
            REFERENCES divided_meal_sections(div_meal_sections_id)
                ON DELETE CASCADE,

        FOREIGN KEY (meal_in_plan_version_id)
            REFERENCES meals_in_plan_versions(meal_in_plan_version_id)
                ON DELETE CASCADE,

        FOREIGN KEY (plan_version_id)
            REFERENCES plan_versions(plan_version_id)
                ON DELETE CASCADE
    );

    -- ####################################################
    -- Constraints (Unique Keys)
    -- ####################################################
        CREATE UNIQUE INDEX sub_meal_versions_unique_uuid
            ON divided_meal_sections_versions(correlation_uuid);

        CREATE UNIQUE INDEX no_repeated_meal_sections_per_plan
            ON divided_meal_sections_versions
               (div_meal_sections_id, plan_version_id); -- 1 sub-meal per plan

        CREATE UNIQUE INDEX no_repeated_sub_meal_times_per_plan
            ON divided_meal_sections_versions
               (plan_version_id, sub_meal_time);

    -- ####################################################
    -- Indexes
    -- ####################################################
        CREATE INDEX idx_sub_meals_per_meal_ordered_by_time_fifo
            ON divided_meal_sections_versions
                (meal_in_plan_version_id, sub_meal_time ASC);

        CREATE INDEX idx_plan_version_id
            ON divided_meal_sections_versions (plan_version_id);
