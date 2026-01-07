-- ##############################################################################################################
-- Active Document
-- ##############################################################################################################

    CREATE TABLE active_plans
    (
        user_id INTEGER PRIMARY KEY,      -- FK has to be defined at the bottom / Only allows 1 plan per user_ID

        plan_version_id INTEGER NOT NULL, -- FK has to be defined at the bottom

        -- Foreign Keys (must be declared at the end in SQLite)
        FOREIGN KEY (user_id)
            REFERENCES users(user_id)
                ON DELETE CASCADE

        FOREIGN KEY (plan_version_id)
            REFERENCES plan_versions(plan_version_id)
                ON DELETE CASCADE
    );

    -- ####################################################
    -- Constraints (Unique Keys)
    -- ####################################################
        CREATE UNIQUE INDEX unique_ownership_per_plan
            ON active_plans (plan_version_id);