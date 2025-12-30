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

ON DUPLICATE KEY UPDATE -- In case of duplicate, ensures fields match correctly to new insert
    ingredient_type_name = VALUES(ingredient_type_name);

-- ##################################################################################
-- Create Variables
-- ##################################################################################
/*

*/

-- ###################################
-- Breads (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @breads_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Breads'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'breads_type_id',
    'ingredient_types',
    @breads_type_id,
    'Seed failed: ingredient_types @breads_type_id could not be resolved'
);

-- ###################################
-- Cake (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @cake_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Cake'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'cake_type_id',
    'ingredient_types',
    @cake_type_id,
    'Seed failed: ingredient_types  @cake_type_id could not be resolved'
);

-- ###################################
-- Cereals (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @cereals_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Cereals'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'cereals_type_id',
    'ingredient_types',
    @cereals_type_id,
    'Seed failed: ingredient_types @cereals_type_id could not be resolved'
);

-- ###################################
-- Cereal Bars (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @cereal_bars_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Cereal Bars'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'cereal_bars_type_id',
    'ingredient_types',
    @cereal_bars_type_id,
    'Seed failed: ingredient_types @cereal_bars_type_id could not be resolved'
);

-- ###################################
-- Cheese (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @cheese_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Cheese'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'cheese_type_id',
    'ingredient_types',
    @cheese_type_id,
    'Seed failed: ingredient_types @cheese_type_id could not be resolved'
);

-- ###################################
-- Chocolate  (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @chocolate_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Chocolate'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'chocolate_type_id',
    'ingredient_types',
    @chocolate_type_id,
    'Seed failed: ingredient_types @chocolate_type_id could not be resolved'
);

-- ###################################
-- Dairy  (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @dairy_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Dairy'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'dairy_type_id',
    'ingredient_types',
    @dairy_type_id,
    'Seed failed: ingredient_types @dairy_type_id could not be resolved'
);

-- ###################################
-- Desserts  (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @desserts_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Desserts'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'desserts_type_id',
    'ingredient_types',
    @desserts_type_id,
    'Seed failed: ingredient_types @desserts_type_id could not be resolved'
);
-- ###################################
-- Fish  (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @fish_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Fish'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'fish_type_id',
    'ingredient_types',
    @fish_type_id,
    'Seed failed: ingredient_types @fish_type_id could not be resolved'
);
-- ###################################
-- Flour  (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @flour_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Flour'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'flour_type_id',
    'ingredient_types',
    @flour_type_id,
    'Seed failed: ingredient_types @flour_type_id could not be resolved'
);

-- ###################################
-- Frozen Fruit  (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @frozen_fruit_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Frozen Fruit'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'frozen_fruit_type_id',
    'ingredient_types',
    @frozen_fruit_type_id,
    'Seed failed: ingredient_types @frozen_fruit_type_id could not be resolved'
);

-- ###################################
-- Frozen Vegetables  (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @frozen_vegetables_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Frozen Vegetables'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'frozen_vegetables_type_id',
    'ingredient_types',
    @frozen_vegetables_type_id,
    'Seed failed: ingredient_types @frozen_vegetables_type_id could not be resolved'
);

-- ###################################
-- Fruit Vegetables  (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @fruit_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Fruit'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'fruit_type_id',
    'ingredient_types',
    @fruit_type_id,
    'Seed failed: ingredient_types @fruit_type_id could not be resolved'
);

-- ###################################
-- Fruit Juice  (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @fruit_juice_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Fruit Juice'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'fruit_juice_type_id',
    'ingredient_types',
    @fruit_juice_type_id,
    'Seed failed: ingredient_types @fruit_juice_type_id could not be resolved'
);

-- ###################################
-- Grains & Legumes  (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @grains_and_legumes_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Grains & Legumes'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'grains_and_legumes_type_id',
    'ingredient_types',
    @grains_and_legumes_type_id,
    'Seed failed: ingredient_types @grains_and_legumes_type_id could not be resolved'
);

-- ###################################
-- Juice  (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @juice_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Juice'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'juice_type_id',
    'ingredient_types',
    @juice_type_id,
    'Seed failed: ingredient_types @juice_type_id could not be resolved'
);

-- ###################################
-- Lean Meat  (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @lean_meat_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Lean Meat'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'lean_meat_type_id',
    'ingredient_types',
    @lean_meat_type_id,
    'Seed failed: ingredient_types @lean_meat_type_id could not be resolved'
);

-- ###################################
-- Liquids  (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @liquids_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Liquids'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'liquids_type_id',
    'ingredient_types',
    @liquids_type_id,
    'Seed failed: ingredient_types @liquids_type_id could not be resolved'
);

-- ###################################
-- Meat  (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @meat_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Meat'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'meat_type_id',
    'ingredient_types',
    @meat_type_id,
    'Seed failed: ingredient_types @meat_type_id could not be resolved'
);

-- ###################################
-- Milk  (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @milk_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Milk'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'milk_type_id',
    'ingredient_types',
    @milk_type_id,
    'Seed failed: ingredient_types @milk_type_id could not be resolved'
);

-- ###################################
-- Noodles  (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @noodles_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Noodles'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'noodles_type_id',
    'ingredient_types',
    @noodles_type_id,
    'Seed failed: ingredient_types @noodles_type_id could not be resolved'
);

-- ###################################
-- Nut Powder  (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @nut_powder_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Nut Powder'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'nut_powder_type_id',
    'ingredient_types',
    @nut_powder_type_id,
    'Seed failed: ingredient_types @nut_powder_type_id could not be resolved'
);

-- ###################################
-- Nuts & Seeds  (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @nuts_and_seeds_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Nuts & Seeds'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'nuts_and_seeds_type_id',
    'ingredient_types',
    @nuts_and_seeds_type_id,
    'Seed failed: ingredient_types @nuts_and_seeds_type_id could not be resolved'
);

-- ###################################
-- Other Grains (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @other_grains_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Other Grains'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'other_grains_type_id',
    'ingredient_types',
    @other_grains_type_id,
    'Seed failed: ingredient_types @other_grains_type_id could not be resolved'
);

-- ###################################
-- Pasta (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @pasta_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Pasta'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    '@pasta_type_id',
    'ingredient_types',
    @pasta_type_id,
    'Seed failed: ingredient_types @pasta_type_id could not be resolved'
);

-- ###################################
-- Plant Milk (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @plant_milk_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Plant Milk'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'plant_milk_type_id',
    'ingredient_types',
    @plant_milk_type_id,
    'Seed failed: ingredient_types @plant_milk_type_id could not be resolved'
);

-- ###################################
-- Poultry (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @poultry_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Poultry'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'poultry_type_id',
    'ingredient_types',
    @poultry_type_id,
    'Seed failed: ingredient_types @poultry_type_id could not be resolved'
);

-- ###################################
-- Potatoes (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @potatoes_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Potatoes'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'potatoes_type_id',
    'ingredient_types',
    @potatoes_type_id,
    'Seed failed: ingredient_types @potatoes_type_id could not be resolved'
);

-- ###################################
-- Protein Powder (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @protein_powder_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Protein Powder'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'protein_powder_type_id',
    'ingredient_types',
    @protein_powder_type_id,
    'Seed failed: ingredient_types @protein_powder_type_id could not be resolved'
);

-- ###################################
-- Rice (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @rice_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Rice'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'rice_type_id',
    'ingredient_types',
    @rice_type_id,
    'Seed failed: ingredient_types @rice_type_id could not be resolved'
);

-- ###################################
-- Sauce (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @sauce_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Sauce'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'sauce_type_id',
    'ingredient_types',
    @sauce_type_id,
    'Seed failed: ingredient_types @sauce_type_id could not be resolved'
);

-- ###################################
-- Smoothie (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @smoothie_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Smoothie'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'smoothie_type_id',
    'ingredient_types',
    @smoothie_type_id,
    'Seed failed: ingredient_types @smoothie_type_id could not be resolved'
);

-- ###################################
-- Spices And Herb Packs (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @spices_and_herb_packs_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Spices And Herb Packs'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'spices_and_herb_packs_type_id',
    'ingredient_types',
    @spices_and_herb_packs_type_id,
    'Seed failed: ingredient_types @spices_and_herb_packs_type_id could not be resolved'
);

-- ###################################
-- Sugar (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @sugar_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Sugar'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'sugar_type_id',
    'ingredient_types',
    @sugar_type_id,
    'Seed failed: ingredient_types @sugar_type_id could not be resolved'
);

-- ###################################
-- Syrup And Spreads (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @syrup_and_spreads_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Syrup And Spreads'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'syrup_and_spreads_type_id',
    'ingredient_types',
    @syrup_and_spreads_type_id,
    'Seed failed: ingredient_types @syrup_and_spreads_type_id could not be resolved'
);

-- ###################################
-- Vegan Vitamin Powders (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @vegan_vitamin_powders_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Vegan Vitamin Powders'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'vegan_vitamin_powders_type_id',
    'ingredient_types',
    @vegan_vitamin_powders_type_id,
    'Seed failed: ingredient_types @vegan_vitamin_powders_type_id could not be resolved'
);

-- ###################################
-- Vegan Milk (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @vegan_milk_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Vegan Milk'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'vegan_milk_type_id',
    'ingredient_types',
    @vegan_milk_type_id,
    'Seed failed: ingredient_types @vegan_milk_type_id could not be resolved'
);

-- ###################################
-- Vegetables (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @vegetables_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Vegetables'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'vegetables_type_id',
    'ingredient_types',
    @vegetables_type_id,
    'Seed failed: ingredient_types @vegetables_type_id could not be resolved'
);

-- ###################################
-- Vitamins (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @vitamins_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Vitamins'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'vitamins_type_id',
    'ingredient_types',
    @vitamins_type_id,
    'Seed failed: ingredient_types @vitamins_type_id could not be resolved'
);

-- ###################################
-- Whole Wheat (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @whole_wheat_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Whole Wheat'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'whole_wheat_type_id',
    'ingredient_types',
    @whole_wheat_type_id,
    'Seed failed: ingredient_types @whole_wheat_type_id could not be resolved'
);

-- ###################################
-- Yoghurt (Type ID)
-- ###################################
SELECT ingredient_type_id
INTO @yoghurt_type_id
FROM ingredient_types
WHERE ingredient_type_name = 'Yoghurt'
LIMIT 1;

-- Validate & Insert Into Seed Registry Table
CALL validate_and_insert_into_seed_registry
(
    'yoghurt_type_id',
    'ingredient_types',
    @yoghurt_type_id,
    'Seed failed: ingredient_types @yoghurt_type_id could not be resolved'
);