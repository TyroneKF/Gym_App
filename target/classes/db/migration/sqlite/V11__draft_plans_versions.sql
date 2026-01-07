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
        draft_plan_id INTEGER PRIMARY KEY AUTOINCREMENT,

        plan_id INTEGER NOT NULL, -- FK has to be defined at the bottom
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
        CREATE UNIQUE INDEX one_draft_plan_per_user
            ON draft_plans (user_id);

        CREATE UNIQUE INDEX no_repeat_draft_plans
            ON draft_plans (plan_id);

    -- ####################################################
    -- Unique Indexes
    -- ####################################################
        CREATE INDEX idx_draft_plans_plan_id
            ON draft_plans (plan_id);

        CREATE INDEX idx_draft_plans_user
            ON draft_plans (user_id);

