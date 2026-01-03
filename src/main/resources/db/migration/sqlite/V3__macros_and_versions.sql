
-- ##############################################################################################################
-- Main Document
-- ##############################################################################################################
    CREATE TABLE macros_per_pound_and_limits
    (
        macros_id INTEGER PRIMARY KEY AUTOINCREMENT,
        created_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%d %H:%M:%f', 'now')),

        target_number INTEGER NOT NULL
             CHECK (target_number > 0),

        user_id INTEGER NOT NULL, -- FK has to be defined at the bottom
            FOREIGN KEY (user_id)
                REFERENCES users(user_id)
                    ON DELETE CASCADE
    );

    -- ####################################################
     -- Constraints (Unique Keys)
    -- ####################################################
        CREATE UNIQUE INDEX unique_target_number_per_user
            ON macros_per_pound_and_limits(user_id, target_number);

    -- ####################################################
    -- Unique Indexes
    -- ####################################################
        CREATE INDEX idx_macros_user
            ON macros_per_pound_and_limits(user_id);

        CREATE INDEX idx_target_number
            ON macros_per_pound_and_limits(target_number);

-- ###############################################################################################################
-- Document Versions
-- ##############################################################################################################
    CREATE TABLE macros_per_pound_and_limits_versions
    (
        macros_version_id INTEGER PRIMARY KEY AUTOINCREMENT,

        macros_id INTEGER NOT NULL,  -- FK has to be defined at the bottom
        plan_version_id INTEGER NOT NULL,  -- FK has to be defined at the bottom

        version_number INTEGER NOT NULL
             CHECK (version_number > 0),

        date_time_last_edited TEXT NOT NULL
             DEFAULT (strftime('%Y-%m-%d %H:%M:%f', 'now')), -- Creates date at insertion

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
        FOREIGN KEY (macros_id)
            REFERENCES macros_per_pound_and_limits(macros_id)
                ON DELETE CASCADE,

        FOREIGN KEY (plan_version_id)
            REFERENCES plan_versions(plan_version_id)
                ON DELETE CASCADE
    );

    -- ####################################################
     -- Constraints (Unique Keys)
    -- ####################################################
        CREATE UNIQUE INDEX unique_macros_version
            ON macros_per_pound_and_limits_versions(macros_id, version_number);

         CREATE UNIQUE INDEX unique_date_per_macros_in_plan
            ON macros_per_pound_and_limits_versions(plan_version_id, date_time_last_edited);

    -- ####################################################
    -- Unique Indexes
    -- ####################################################
        CREATE INDEX idx_macros_latest_per_plan
            ON macros_per_pound_and_limits_versions
                (plan_version_id, date_time_last_edited DESC);


