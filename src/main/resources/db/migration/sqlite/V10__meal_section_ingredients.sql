-- ##############################################################################################################
-- DDL SCRIPT | App Setup
-- ##############################################################################################################
    /*


    */
-- ##############################################################################################################
-- Main Document
-- ##############################################################################################################
    CREATE TABLE ingredients_in_sections_of_meal
    (
        ingredients_index INTEGER PRIMARY KEY AUTOINCREMENT,
      
        div_meal_sections_version_id INTEGER NOT NULL, -- FK has to be defined at the bottom
        ingredient_id INTEGER NOT NULL, -- FK has to be defined at the bottom
        pdid INTEGER DEFAULT NULL, -- FK has to be defined at the bottom

        quantity REAL NOT NULL
            CHECK (quantity >= 0),

        -- Foreign Keys (SQLite requires table-level declaration)
        FOREIGN KEY (div_meal_sections_version_id)
            REFERENCES divided_meal_sections_versions(div_meal_sections_version_id)
                ON DELETE CASCADE,

        FOREIGN KEY (ingredient_id)
            REFERENCES ingredients_info(ingredient_id)
                ON DELETE RESTRICT,  -- Change Ingredient before deleting it
                                     -- Allows application to substitute ingredient if deleted vs deleting row

        FOREIGN KEY (pdid)
            REFERENCES ingredient_in_shops(pdid)
                ON DELETE RESTRICT -- Change supplier not delete the ingredient 
    );

    -- ####################################################
    -- Constraints (Unique Keys)
    -- ####################################################
       /*
       CREATE UNIQUE INDEX no_duplicate_ingredient_per_section
       ON ingredients_in_sections_of_meal
          (div_meal_sections_version_id, ingredient_id);
       */
    -- ####################################################
    -- Indexes
    -- ####################################################
        CREATE INDEX idx_ingredients_by_section_version
            ON ingredients_in_sections_of_meal (div_meal_sections_version_id);
    
        CREATE INDEX idx_ingredient_index_by_ingredient_id
            ON ingredients_in_sections_of_meal (ingredient_id);

        CREATE INDEX idx_ingredient_index_by_product
            ON ingredients_in_sections_of_meal (pdid);