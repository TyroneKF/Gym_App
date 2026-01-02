-- ##############################################################################################################
-- Ingredients Info
-- ##############################################################################################################
    CREATE TABLE ingredients_info
    (
        ingredient_id INTEGER PRIMARY KEY AUTOINCREMENT,

        is_system INTEGER NOT NULL DEFAULT 0 -- DEFAULT FALSE
            CHECK (is_system IN (0,1)),

        measurement_id INTEGER NOT NULL, -- FK has to be defined at the bottom
        ingredient_type_id INTEGER NOT NULL, -- FK has to be defined at the bottom

        ingredient_name TEXT NOT NULL
            CHECK (length(ingredient_name) <= 100),

        based_on_quantity REAL NOT NULL
            CHECK (based_on_quantity >= 0),

        glycemic_index INTEGER NOT NULL
            CHECK (glycemic_index >= 0 AND glycemic_index <= 100),

        protein REAL NOT NULL,
        carbohydrates REAL NOT NULL,
        sugars_of_carbs REAL NOT NULL,
        fibre REAL NOT NULL,
        fat REAL NOT NULL,
        saturated_fat REAL NOT NULL,
        salt REAL NOT NULL,
        water_content REAL NOT NULL,
        liquid_content REAL NOT NULL,
        calories REAL NOT NULL,

        -- Foreign Keys (must be declared at the end in SQLite)
        FOREIGN KEY(measurement_id)
            REFERENCES measurements(measurement_id)
                ON DELETE RESTRICT,

        FOREIGN KEY(ingredient_type_id)
            REFERENCES ingredient_types(ingredient_type_id)
                ON DELETE RESTRICT
    );

    -- ####################################################
    -- Constraints (Unique Keys)
    -- ####################################################
        CREATE UNIQUE INDEX no_repeat_ingredient_names
            ON ingredients_info (ingredient_name);


-- ##############################################################################################################
-- Stores
-- ##############################################################################################################

    CREATE TABLE stores
    (
        store_id INTEGER PRIMARY KEY AUTOINCREMENT,

        is_system INTEGER NOT NULL DEFAULT 0
            CHECK (is_system IN (0,1)),

        store_name TEXT NOT NULL
            CHECK (length(store_name) <= 100)
    );

    -- ####################################################
    -- Constraints (Unique Keys)
    -- ####################################################
        CREATE UNIQUE INDEX no_repeat_store_names
            ON stores (store_name);


-- ##############################################################################################################
-- Ingredient Products in Shops
-- ##############################################################################################################
    CREATE TABLE ingredient_in_shops
    (
        pdid INTEGER PRIMARY KEY AUTOINCREMENT,

        is_system INTEGER NOT NULL DEFAULT 0 -- DEFAULT FALSE
            CHECK (is_system IN (0,1)),

        ingredient_id INTEGER NOT NULL, -- FK has to be defined at the bottom
        store_id INTEGER NOT NULL,  -- FK has to be defined at the bottom

        product_name TEXT NOT NULL
            CHECK (length(product_name) <= 100),

        volume_per_unit REAL NOT NULL
            CHECK (volume_per_unit > 0),

        cost_per_unit REAL NOT NULL
            CHECK (cost_per_unit >= 0),

        -- Foreign Keys (must be declared at the end in SQLite)
        FOREIGN KEY (ingredient_id)
            REFERENCES ingredients_info(ingredient_id)
                ON DELETE CASCADE,

        FOREIGN KEY (store_id)
            REFERENCES stores(store_id)
                ON DELETE CASCADE
    );

    -- ####################################################
    -- Constraints (Unique Keys)
    -- ####################################################
        CREATE UNIQUE INDEX no_repeat_products_in_store
            ON ingredient_in_shops (store_id, product_name);

    -- ####################################################
    -- Indexes
    -- ####################################################
        CREATE INDEX idx_shop_ingredient
            ON ingredient_in_shops (ingredient_id);

        CREATE INDEX idx_shop_store
            ON ingredient_in_shops (store_id);
