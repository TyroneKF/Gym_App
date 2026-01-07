-- ##############################################################################################################
-- Ingredient_Types
-- ##############################################################################################################

    CREATE TABLE ingredient_types
    (
        ingredient_type_id INTEGER PRIMARY KEY AUTOINCREMENT,

        -- DEFAULT FALSE
        is_system INTEGER NOT NULL DEFAULT 0
            CHECK (is_system IN (0,1)), -- DEFAULT FALSE

        ingredient_type_name TEXT NOT NULL
            CHECK (length(ingredient_type_name) <= 100)
    );

    -- ####################################################
    -- Constraints (Unique Keys)
    -- ####################################################
    CREATE UNIQUE INDEX no_repeat_ingredient_type_name
        ON ingredient_types (ingredient_type_name);


-- ##############################################################################################################
-- Measurement_Material_Type
-- ##############################################################################################################

    CREATE TABLE measurement_material_type
    (
        measurement_material_type_id INTEGER PRIMARY KEY AUTOINCREMENT,

        measurement_material_type_name TEXT NOT NULL
            CHECK (length(measurement_material_type_name) <= 100)
    );

    -- ####################################################
    -- Constraints (Unique Keys)
    -- ####################################################
    CREATE UNIQUE INDEX no_repeat_material_type_name
        ON measurement_material_type (measurement_material_type_name);


-- ##############################################################################################################
-- Measurements
-- ##############################################################################################################

    CREATE TABLE measurements
    (
        measurement_id INTEGER PRIMARY KEY AUTOINCREMENT,

        is_system INTEGER NOT NULL DEFAULT 0
            CHECK (is_system IN (0,1)), -- DEFAULT FALSE

        unit_name TEXT NOT NULL
            CHECK (length(unit_name) <= 100),

        unit_symbol TEXT NOT NULL
            CHECK (length(unit_symbol) <= 10),

        -- Foreign Keys (must be declared at the end in SQLite)
        measurement_material_type_id INTEGER NOT NULL,
            FOREIGN KEY (measurement_material_type_id)
                REFERENCES measurement_material_type(measurement_material_type_id)
                    ON DELETE CASCADE
    );

    -- ####################################################
    -- Constraints (Unique Keys)
    -- ####################################################
    CREATE UNIQUE INDEX no_repeat_unit_names
        ON measurements (unit_name);
