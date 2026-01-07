-- #####################################################
-- Inserting BULK Non-Seed DATA
-- #####################################################
    INSERT INTO ingredient_types  -- Bulk - is_system is Default
    (
        ingredient_type_name
    )

    VALUES

        ('Breads'),
        ('Cake'),
        ('Cereals'),
        ('Cereal Bars'),
        ('Cheese'),
        ('Chocolate'),
        ('Dairy'),
        ('Desserts'),
        ('Fish'),
        ('Flour'),
        ('Frozen Fruit'),
        ('Frozen Vegetables'),
        ('Fruit'),
        ('Fruit Juice'),
        ('Grains & Legumes'),
        ('Juice'),
        ('Lean Meat'),
        ('Liquids'),
        ('Meat'),
        ('Milk'),
        ('Noodles'),
        ('Nut Powder'),
        ('Nuts & Seeds'),
        ('Other Grains'),
        ('Pasta'),
        ('Plant Milk'),
        ('Poultry'),
        ('Potatoes'),
        ('Protein Powder'),
        ('Rice'),
        ('Sauce'),
        ('Smoothie'),
        ('Spices And Herb Packs'),
        ('Sugar'),
        ('Syrup And Spreads'),
        ('Vegan Vitamin Powders'),
        ('Vegan Milk'),
        ('Vegetables'),
        ('Vitamins'),
        ('Whole Wheat'),
        ('Yoghurt')

    ON CONFLICT(ingredient_type_name)
        DO UPDATE SET -- In case of duplicate, ensures fields match correctly to new insert
            ingredient_type_name = excluded.ingredient_type_name;

-- ##################################################################################
-- Create Variables
-- ##################################################################################
    /*

    */

    -- ###################################
    -- Breads (Type ID)
    -- ###################################
        INSERT INTO seed_registry(seed_key,entity_table_name,entity_id_value)
        VALUES
        (
          'breads_type_id',
          'ingredient_types',
          (
             SELECT ingredient_type_id
             FROM ingredient_types
             WHERE ingredient_type_name = 'Breads'
          )
        )
        ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            entity_id_value = excluded.entity_id_value;

    -- ###################################
    -- Cake (Type ID)
    -- ###################################
        INSERT INTO seed_registry(seed_key,entity_table_name,entity_id_value)
        VALUES
        (
          'cake_type_id',
          'ingredient_types',
          (
             SELECT ingredient_type_id
             FROM ingredient_types
             WHERE ingredient_type_name = 'Cake'
          )
        )
        ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            entity_id_value = excluded.entity_id_value;

    -- ###################################
    -- Cereals (Type ID)
    -- ###################################
       INSERT INTO seed_registry(seed_key,entity_table_name,entity_id_value)
        VALUES
        (
          'cereals_type_id',
          'ingredient_types',
          (
             SELECT ingredient_type_id
             FROM ingredient_types
             WHERE ingredient_type_name = 'Cereals'
          )
        )
        ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            entity_id_value = excluded.entity_id_value;

    -- ###################################
    -- Cereal Bars (Type ID)
    -- ###################################
        INSERT INTO seed_registry(seed_key,entity_table_name,entity_id_value)
        VALUES
        (
          'cereal_bars_type_id',
          'ingredient_types',
          (
             SELECT ingredient_type_id
             FROM ingredient_types
             WHERE ingredient_type_name = 'Cereal Bars'
          )
        )
        ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            entity_id_value = excluded.entity_id_value;

    -- ###################################
    -- Cheese (Type ID)
    -- ###################################
        INSERT INTO seed_registry(seed_key,entity_table_name,entity_id_value)
        VALUES
        (
          'cheese_type_id',
          'ingredient_types',
          (
             SELECT ingredient_type_id
             FROM ingredient_types
             WHERE ingredient_type_name = 'Cheese'
          )
        )
        ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
            DO UPDATE SET
            entity_id_value = excluded.entity_id_value;

    -- ###################################
    -- Chocolate  (Type ID)
    -- ###################################
        INSERT INTO seed_registry(seed_key,entity_table_name,entity_id_value)
        VALUES
        (
            'chocolate_type_id',
            'ingredient_types',
            (
                 SELECT ingredient_type_id
                 FROM ingredient_types
                 WHERE ingredient_type_name = 'Chocolate'
            )
        )
        ON CONFLICT(seed_key)  -- In case of duplicate, ensures fields match correctly to new insert
        DO UPDATE SET
        entity_id_value = excluded.entity_id_value;


    -- ###################################
    -- Dairy
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'dairy_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Dairy')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Desserts
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'desserts_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Desserts')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Fish
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'fish_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Fish')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Flour
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'flour_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Flour')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Frozen Fruit
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'frozen_fruit_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Frozen Fruit')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Frozen Vegetables
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'frozen_vegetables_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Frozen Vegetables')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Fruit
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'fruit_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Fruit')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Fruit Juice
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'fruit_juice_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Fruit Juice')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Grains & Legumes
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'grains_and_legumes_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Grains & Legumes')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Juice
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'juice_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Juice')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Lean Meat
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'lean_meat_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Lean Meat')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Liquids
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'liquids_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Liquids')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Meat
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'meat_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Meat')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Milk
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'milk_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Milk')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Noodles
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'noodles_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Noodles')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Nut Powder
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'nut_powder_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Nut Powder')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Nuts & Seeds
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'nuts_and_seeds_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Nuts & Seeds')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Other Grains
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'other_grains_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Other Grains')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Pasta
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'pasta_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Pasta')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Plant Milk
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'plant_milk_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Plant Milk')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Poultry
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'poultry_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Poultry')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Potatoes
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'potatoes_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Potatoes')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Protein Powder
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'protein_powder_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Protein Powder')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Rice
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'rice_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Rice')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Sauce
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'sauce_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Sauce')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Smoothie
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'smoothie_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Smoothie')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Spices And Herb Packs
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'spices_and_herb_packs_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Spices And Herb Packs')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Sugar
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'sugar_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Sugar')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Syrup And Spreads
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'syrup_and_spreads_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Syrup And Spreads')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Vegan Vitamin Powders
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'vegan_vitamin_powders_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Vegan Vitamin Powders')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Vegan Milk
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'vegan_milk_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Vegan Milk')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Vegetables
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'vegetables_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Vegetables')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Vitamins
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'vitamins_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Vitamins')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Whole Wheat
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'whole_wheat_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Whole Wheat')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;

    -- ###################################
    -- Yoghurt
    -- ###################################
        INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
        VALUES
        (
          'yoghurt_type_id',
          'ingredient_types',
          (SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = 'Yoghurt')
        )
        ON CONFLICT (seed_key)
            DO UPDATE SET
            entity_id_value = EXCLUDED.entity_id_value;
