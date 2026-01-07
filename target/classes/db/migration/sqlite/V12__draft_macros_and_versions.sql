-- ###############################################################################################################
-- Rules
-- ##############################################################################################################
  /*
     A user can only have one draft plan active
     A user can only have one draft macro per draft plan
  */


-- ###############################################################################################################
-- Document
-- ##############################################################################################################
    CREATE TABLE draft_macros_per_pound_and_limits
    (
        macros_version_id INTEGER PRIMARY KEY AUTOINCREMENT,

        user_id INTEGER NOT NULL,           -- FK has to be defined at the bottom
        draft_plan_id INTEGER NOT NULL,   -- FK has to be defined at the bottom

        current_weight_kg REAL NOT NULL,
        current_weight_in_pounds REAL NOT NULL,
        body_fat_percentage REAL NOT NULL,

        protein_per_pound REAL NOT NULL,
        carbohydrates_per_pound REAL NOT NULL,
        fibre REAL NOT NULL,
        fats_per_pound REAL NOT NULL,

        saturated_fat_limit REAL NOT NULL,
        salt_limit REAL NOT NULL,
        water_target REAL NOT NULL,
        additional_calories REAL NOT NULL,

        -- Foreign Keys (must be declared at the end in SQLite)
        FOREIGN KEY (user_id)
            REFERENCES users(user_id)
                ON DELETE CASCADE,

        FOREIGN KEY (draft_plan_id)
            REFERENCES draft_plans(draft_plan_id)
                ON DELETE CASCADE
    );

    -- ####################################################
    -- Constraints (Unique Keys)
    -- ####################################################
        CREATE UNIQUE INDEX one_draft_macro_per_user
            ON draft_macros_per_pound_and_limits(user_id);

        CREATE UNIQUE INDEX one_macro_draft_per_plan
            ON draft_macros_per_pound_and_limits(draft_plan_id);

    -- ####################################################
    -- Unique Indexes
    -- ####################################################
       CREATE INDEX idx_draft_macros_user_id
           ON draft_macros_per_pound_and_limits
              (user_id);

       CREATE INDEX idx_draft_macros_plan_id
           ON draft_macros_per_pound_and_limits
              (draft_plan_id);


