
-- ##############################################################################################################
-- N/A Ingredients Types
-- ##############################################################################################################
        INSERT INTO ingredient_types
        (
            ingredient_type_name,
            is_system
        )
        VALUES
            ('N/A', 1),
            ('Un-Assigned', 1)
        ON CONFLICT(ingredient_type_name) -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            ingredient_type_name = excluded.ingredient_type_name; -- On update triggered by PK or unique Key

    -- ######################################
    -- Insert Into Seed Registry Table
    -- ######################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'na_type_id',
            'ingredient_types',
            (
                SELECT ingredient_type_id
                FROM ingredient_types
                WHERE ingredient_type_name = 'N/A'
            )
        )
        ON CONFLICT(seed_key)   -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            entity_id_value = excluded.entity_id_value; -- On update triggered by PK or unique Key

-- ##############################################################################################################
-- Ingredient Measurements
-- ##############################################################################################################
    /*

    */

    -- ####################################################################
    -- Insert SEED DATA for Measurement Variables
    -- ####################################################################
        INSERT INTO measurement_material_type
        (
            measurement_material_type_name
        )
        VALUES
            ('Solids'),
            ('Liquids'),
            ('N/A')
        ON CONFLICT(measurement_material_type_name)   -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            measurement_material_type_name = excluded.measurement_material_type_name; -- On update triggered by PK or unique Key

    -- ####################################################################
    -- Variables : Insert Into Seed Registry Table
    -- ####################################################################
       /*
       
       */
    -- #################################
    -- Solids
    -- #################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'solids_material_type_id',
            'measurement_material_type',
            (
                SELECT measurement_material_type_id
                FROM measurement_material_type
                WHERE measurement_material_type_name = 'Solids'
            )
        )
        ON CONFLICT(seed_key) -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            entity_id_value = excluded.entity_id_value; -- On update triggered by PK or unique Key
            
    -- #################################
    -- Liquids
    -- #################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'liquids_material_type_id',
            'measurement_material_type',
            (
                SELECT measurement_material_type_id
                FROM measurement_material_type
                WHERE measurement_material_type_name = 'Liquids'
            )
        )
        ON CONFLICT(seed_key) -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            seed_key = excluded.seed_key; -- On update triggered by PK or unique Key

    -- #################################
    -- N/A
    -- #################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'na_material_type_id',
            'measurement_material_type',
            (
                SELECT measurement_material_type_id
                FROM measurement_material_type
                WHERE measurement_material_type_name = 'N/A'
            )
        )
        ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
           entity_id_value = excluded.entity_id_value; -- On update triggered by PK or unique Key

-- ##############################################################################################################
 -- Measurements
-- ##############################################################################################################

   /*


   */

    -- ####################################################################
    -- Inserting SEED DATA for Measurement Variables
    -- ####################################################################
        INSERT INTO measurements
        (
            is_system,
            unit_name,
            unit_symbol,
            measurement_material_type_id
        )
        VALUES
            (1, 'Litres', 'L', (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'liquids_material_type_id')),
            (1, 'Grams', 'g',  (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'solids_material_type_id')),
            (1, 'N/A', 'N/A',  (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'na_material_type_id'))

        ON CONFLICT(unit_name) -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            unit_name = excluded.unit_name; -- On update triggered by PK or unique Key

    -- ####################################################################
    -- Variables : Insert Into Seed Registry Table
    --- ####################################################################
         /*

         */

        -- #################################
        -- Litres
        -- #################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'litres_measurement_id',
            'measurements',
            (
                SELECT measurement_id
                FROM measurements
                WHERE unit_name = 'Litres'
                LIMIT 1
            )
        )
        ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            entity_id_value = excluded.entity_id_value; -- On update triggered by PK or unique Key

    -- #################################
    -- Grams
    -- #################################
       INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
       VALUES
       (
            'grams_measurement_id',
            'measurements',
            (
                SELECT measurement_id
                FROM measurements
                WHERE unit_name = 'Grams'
                LIMIT 1
            )
       )
       ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
           DO UPDATE SET
           entity_id_value = excluded.entity_id_value; -- On update triggered by PK or unique Key

    -- #################################
    -- N/A
    -- #################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
             'na_measurement_id',
             'measurements',
             (SELECT measurement_id FROM measurements WHERE unit_name = 'N/A' LIMIT 1)
        )
        ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            entity_id_value = excluded.entity_id_value; -- On update triggered by PK or unique Key



-- ##############################################################################################################
 -- Ingredient Stores
-- ##############################################################################################################
   /*


   */

    -- ###############################################
    -- Create N/A Shop
    -- ###############################################
        INSERT INTO stores
        (
            is_system,
            store_name

        ) VALUES
        (
            1, 'No Shop'
        )
        ON CONFLICT(store_name) -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            store_name = excluded.store_name; -- On update triggered by PK or unique Key

    -- ######################################
    -- Insert Into Seed Registry Table
    -- ######################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'na_store_id',
            'stores',
            (
                SELECT store_id
                FROM stores
                WHERE store_name = 'No Shop'
                LIMIT 1
            )
        )
        ON CONFLICT(seed_key)   -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            entity_id_value = excluded.entity_id_value; -- On update triggered by PK or unique Key


-- ##############################################################################################################
 -- Creating N/A Ingredient
-- ##############################################################################################################

    INSERT INTO ingredients_info
    (
        is_system,
        measurement_id,
        ingredient_name,
        ingredient_type_id,

        based_on_quantity,
        glycemic_index,
        protein,
        carbohydrates,
        sugars_of_carbs,
        fibre,
        fat,
        saturated_fat,
        salt,
        water_content,
        liquid_content,
        calories
    )
    VALUES

    (
        1,
        (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'na_measurement_id'),
        'None Of The Above',
        (SELECT entity_id_value FROM seed_registry WHERE seed_key = 'na_type_id'),
        0,0,0,0,0,0,0,0,0,0,0,0
    )
    ON CONFLICT(ingredient_name) -- In case of duplicate, ensures fields match correctly to new insert
        DO UPDATE SET
        ingredient_name = excluded.ingredient_name; -- On update triggered by PK or unique Key

    -- ####################################################################
    -- Variables : Insert Into Seed Registry Table
    --- ####################################################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
            'na_ingredient_id',
            'ingredients_info',
            (
                SELECT ingredient_id
                FROM ingredients_info
                WHERE ingredient_name = 'None Of The Above'
                LIMIT 1
            )
        )
        ON CONFLICT(seed_key)   -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            entity_id_value = excluded.entity_id_value; -- On update triggered by PK or unique Key
