
-- ###############################################################################################################
-- Document
-- ##############################################################################################################
    CREATE TABLE macros_per_pound_and_limits
    (
        macros_id INTEGER PRIMARY KEY AUTOINCREMENT,

        user_id INTEGER NOT NULL,           -- FK has to be defined at the bottom
        plan_version_id INTEGER NOT NULL,   -- FK has to be defined at the bottom

        date_time_of_creation TEXT NOT NULL
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
        FOREIGN KEY (user_id)
            REFERENCES users(user_id)
                ON DELETE CASCADE,

        FOREIGN KEY (plan_version_id)
            REFERENCES plan_versions(plan_version_id)
                ON DELETE CASCADE
    );

    -- ####################################################
    -- Constraints (Unique Keys)
    -- ####################################################
        CREATE UNIQUE INDEX unique_macros_per_plan_version -- 1 to 1 relationship, 1 macro  per plan version
            ON macros_per_pound_and_limits(plan_version_id);


