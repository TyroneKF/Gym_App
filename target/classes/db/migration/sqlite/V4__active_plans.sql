-- ##############################################################################################################
-- Active Document
-- ##############################################################################################################

    CREATE TABLE active_plans
    (
        row_id INTEGER PRIMARY KEY AUTOINCREMENT,

        plan_version_id INTEGER NOT NULL, -- FK has to be defined at the bottom
        user_id INTEGER NOT NULL, -- FK has to be defined at the bottom

        -- Foreign Keys (must be declared at the end in SQLite)
        FOREIGN KEY (plan_version_id)
            REFERENCES plan_versions(plan_version_id)
                ON DELETE CASCADE,

        FOREIGN KEY (user_id)
            REFERENCES users(user_id)
                ON DELETE CASCADE
    );

    -- ####################################################
    -- Unique Indexes
    -- ####################################################
        CREATE UNIQUE INDEX only_one_active_plan_per_user
            ON active_plans (user_id);

        CREATE UNIQUE INDEX unique_ownership_plan_active
            ON active_plans (plan_version_id);