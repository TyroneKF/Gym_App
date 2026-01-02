-- ##############################################################################################################
-- Document Versions
-- ##############################################################################################################

    CREATE TABLE seed_registry
    (
        seed_key TEXT PRIMARY KEY
            CHECK (length(seed_key) <= 100),

        entity_table_name TEXT NOT NULL
            CHECK (length(entity_table_name) <= 100),

        entity_id_value INT NOT NULL
    );

    -- ####################################################
    -- Constraints (Unique Keys)
    -- ####################################################
        CREATE UNIQUE INDEX unique_id_per_table
            ON seed_registry (entity_table_name, entity_id_value );