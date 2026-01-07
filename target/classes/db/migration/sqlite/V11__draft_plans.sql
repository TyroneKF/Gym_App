-- ###############################################################################################################
-- Rules
-- ##############################################################################################################
   /*
       This table schema ensures one draft plan is active per user to represent the plan
       they have active / No versioning per plan
   */

-- ###############################################################################################################
-- DDL Table Creation
-- ##############################################################################################################
    CREATE TABLE draft_plans -- Equivalent of plan_versions for drafts
    (
        -- Enforces one draft per plan as this is the FK / Unique
        plan_id INTEGER PRIMARY KEY, -- FK has to be defined at the bottom

        user_id INTEGER NOT NULL, -- FK has to be defined at the bottom

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
        CREATE UNIQUE INDEX one_draft_per_user_for_active_plan
            ON draft_plans (user_id);




