-- ##############################################################################################################
-- DDL SCRIPT | App Setup
-- ##############################################################################################################
    /**


    **/


-- ##############################################################################################################
-- Main Document
-- ##############################################################################################################
    CREATE TABLE plans
    (
        plan_id INTEGER PRIMARY KEY AUTOINCREMENT,

        date_time_of_creation TEXT NOT NULL
            DEFAULT (strftime('%Y-%m-%d %H:%M:%f', 'now')),

        user_id INTEGER NOT NULL, -- FK has to be defined at the bottom

        vegan INTEGER NOT NULL DEFAULT 0
            CHECK (vegan IN (0,1)),   -- Default = FALSE

        plan_name TEXT NOT NULL
            CHECK (length(plan_name) <= 100),

        -- Foreign Keys (must be declared at the end in SQLite)
        FOREIGN KEY (user_id)
            REFERENCES users(user_id)
                ON DELETE CASCADE
    );

    -- ####################################################
    -- Constraints (Unique Keys)
    -- ####################################################
        CREATE UNIQUE INDEX unique_plan_name_by_user
            ON plans (user_id, plan_name);

    -- ####################################################
    -- Unique Indexes
    -- ####################################################
        CREATE INDEX idx_plans_name -- global index incase you search by plan_name for any user to copy a plan from
            ON plans (plan_name);

        CREATE INDEX idx_latest_plans_by_user
            ON plans (user_id, date_time_of_creation DESC);

-- ##############################################################################################################
-- Document Versions
-- ##############################################################################################################
    CREATE TABLE plan_versions
    (
        plan_version_id INTEGER PRIMARY KEY AUTOINCREMENT,
        correlation_uuid BLOB(16) NULL,

        plan_id INTEGER NOT NULL, -- FK has to be defined at the bottom
        user_id INTEGER NOT NULL, -- FK has to be defined at the bottom

        version_number INTEGER NOT NULL
            CHECK (version_number > 0),

        date_time_last_edited TEXT NOT NULL
            DEFAULT (strftime('%Y-%m-%d %H:%M:%f', 'now')), -- Defined on Insertion

        -- Foreign Keys (must be declared at the end in SQLite)
        FOREIGN KEY (plan_id)
            REFERENCES plans(plan_id)
                ON DELETE CASCADE,

        FOREIGN KEY (user_id)
            REFERENCES users(user_id)
                ON DELETE CASCADE
    );

    -- ####################################################
    -- Constraints (Unique Keys)
    -- ####################################################
        CREATE UNIQUE INDEX no_repeated_vs_numbers_per_plan
            ON plan_versions (plan_id, version_number);

    -- ####################################################
    -- Unique Indexes
    -- ####################################################
        CREATE INDEX idx_plan_versions_by_user
            ON plan_versions (user_id);

        CREATE INDEX idx_latest_plan_version_per_plan
            ON plan_versions (plan_id, version_number DESC);