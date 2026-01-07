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
        -- Enforces one draft macro per draft plan as this is the FK / Unique
        plan_id INTEGER PRIMARY KEY, -- FK has to be defined at the bottom

        user_id INTEGER NOT NULL,    -- FK has to be defined at the bottom

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
        FOREIGN KEY (plan_id)
            REFERENCES draft_plans(plan_id)
                ON DELETE CASCADE,

        FOREIGN KEY (user_id)
            REFERENCES users(user_id)
                ON DELETE CASCADE
    );

    -- ####################################################
    -- Constraints (Unique Keys)
    -- ####################################################
        CREATE UNIQUE INDEX one_draft_macro_per_user -- slightly redundant unique check but, double enforcement from plans too
            ON draft_macros_per_pound_and_limits(user_id);


