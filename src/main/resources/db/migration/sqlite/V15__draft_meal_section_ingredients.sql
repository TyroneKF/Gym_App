-- ##############################################################################################################
-- DDL SCRIPT | App Setup
-- ##############################################################################################################
    /*

    */

-- ##############################################################################################################
-- Main Document
-- ##############################################################################################################
    CREATE TABLE draft_ingredients_in_sections_of_meal
    (
        draft_ingredients_index INTEGER PRIMARY KEY AUTOINCREMENT,

        draft_div_meal_sections_id INTEGER NOT NULL, -- FK has to be defined at the bottom

        ingredient_id INTEGER NOT NULL,              -- FK has to be defined at the bottom
        pdid INTEGER DEFAULT NULL,                   -- FK has to be defined at the bottom

        quantity REAL NOT NULL
            CHECK (quantity > 0),

        -- Foreign Keys (SQLite requires table-level declaration)
        FOREIGN KEY (draft_div_meal_sections_id)
            REFERENCES draft_divided_meal_sections(draft_div_meal_sections_id)
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
       /*
       CREATE UNIQUE INDEX no_duplicate_ingredients_per_draft_section
           ON draft_ingredients_in_sections_of_meal
              (draft_div_meal_sections_id, ingredient_id);
       */

    -- ####################################################
    -- Indexes
    -- ####################################################

        CREATE INDEX idx_draft_ingredients_by_section
            ON draft_ingredients_in_sections_of_meal (draft_div_meal_sections_id);

        CREATE INDEX idx_draft_ingredients_by_ingredient
            ON draft_ingredients_in_sections_of_meal (ingredient_id);

        CREATE INDEX idx_draft_ingredient_index
            ON draft_ingredients_in_sections_of_meal (draft_ingredients_index);
