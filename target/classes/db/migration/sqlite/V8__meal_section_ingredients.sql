-- ##############################################################################################################
-- DDL SCRIPT | App Setup
-- ##############################################################################################################
/*
    Ingredients are snapshot per sub-meal version; no independent ingredient versioning
*/

-- ##############################################################################################################
-- Main Document
-- ##############################################################################################################

    CREATE TABLE ingredients_in_sections_of_meal
    (
        ingredients_index INTEGER PRIMARY KEY AUTOINCREMENT,
        date_time_of_creation TEXT NOT NULL -- Defined on Insertion
            DEFAULT (strftime('%Y-%m-%d %H:%M:%f', 'now'))
    );

-- ##############################################################################################################
-- Document Versions
-- ##############################################################################################################

    CREATE TABLE ingredients_in_sections_of_meal_versions
    (
        ingredients_index_version_id INTEGER PRIMARY KEY AUTOINCREMENT,

        ingredients_index INTEGER NOT NULL, -- FK has to be defined at the bottom
        div_meal_sections_version_id INTEGER NOT NULL, -- FK has to be defined at the bottom

        ingredient_id INTEGER NOT NULL, -- FK has to be defined at the bottom
        pdid INTEGER DEFAULT NULL, -- FK has to be defined at the bottom

        quantity REAL NOT NULL
            CHECK (quantity > 0),

        -- Foreign Keys (SQLite requires table-level declaration)
        FOREIGN KEY (ingredients_index)
            REFERENCES ingredients_in_sections_of_meal(ingredients_index)
                ON DELETE CASCADE,

        FOREIGN KEY (div_meal_sections_version_id)
            REFERENCES divided_meal_sections_versions(div_meal_sections_version_id)
                ON DELETE CASCADE,

        FOREIGN KEY (ingredient_id)
            REFERENCES ingredients_info(ingredient_id)
                ON DELETE CASCADE,

        FOREIGN KEY (pdid)
            REFERENCES ingredient_in_shops(pdid)
                ON DELETE RESTRICT
    );

-- ####################################################
-- Constraints (Unique Keys)
-- ####################################################

    CREATE UNIQUE INDEX no_repeat_records
        ON ingredients_in_sections_of_meal_versions
           (ingredients_index_version_id, div_meal_sections_version_id);

-- ####################################################
-- Indexes
-- ####################################################

    CREATE INDEX idx_ingredients_by_section_versions
        ON ingredients_in_sections_of_meal_versions (div_meal_sections_version_id);

    CREATE INDEX idx_ingredients_by_ingredient_versions
        ON ingredients_in_sections_of_meal_versions (ingredient_id);

    CREATE INDEX idx_ingredient_index_versions
        ON ingredients_in_sections_of_meal_versions (ingredients_index_version_id);
