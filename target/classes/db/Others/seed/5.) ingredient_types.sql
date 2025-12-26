-- #####################################################
-- Inserting BULK Non-Seed DATA
-- #####################################################
INSERT INTO ingredient_types  -- Bulk - is_system is Default
(
	ingredient_type_name
	
) VALUES

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

ON DUPLICATE KEY UPDATE -- Incase of duplicate, ensures fields match correctly to new insert
    ingredient_type_name = VALUES(ingredient_type_name);

-- ######################################
-- Variables
-- ######################################
SELECT ingredient_id INTO @breads
FROM ingredients_info
WHERE ingredient_name = 'Breads'
LIMIT 1;

SELECT ingredient_id INTO @cake
FROM ingredients_info
WHERE ingredient_name = 'Cake'
LIMIT 1;

SELECT ingredient_id INTO @cereals
FROM ingredients_info
WHERE ingredient_name = 'Cereals'
LIMIT 1;

SELECT ingredient_id INTO @cereal_bars
FROM ingredients_info
WHERE ingredient_name = 'Cereal Bars'
LIMIT 1;

SELECT ingredient_id INTO @cheese
FROM ingredients_info
WHERE ingredient_name = 'Cheese'
LIMIT 1;

SELECT ingredient_id INTO @chocolate
FROM ingredients_info
WHERE ingredient_name = 'Chocolate'
LIMIT 1;

SELECT ingredient_id INTO @dairy
FROM ingredients_info
WHERE ingredient_name = 'Dairy'
LIMIT 1;

SELECT ingredient_id INTO @desserts
FROM ingredients_info
WHERE ingredient_name = 'Desserts'
LIMIT 1;

SELECT ingredient_id INTO @fish
FROM ingredients_info
WHERE ingredient_name = 'Fish'
LIMIT 1;

SELECT ingredient_id INTO @flour
FROM ingredients_info
WHERE ingredient_name = 'Flour'
LIMIT 1;

SELECT ingredient_id INTO @frozen_fruit
FROM ingredients_info
WHERE ingredient_name = 'Frozen Fruit'
LIMIT 1;

SELECT ingredient_id INTO @frozen_vegetables
FROM ingredients_info
WHERE ingredient_name = 'Frozen Vegetables'
LIMIT 1;

SELECT ingredient_id INTO @fruit
FROM ingredients_info
WHERE ingredient_name = 'Fruit'
LIMIT 1;

SELECT ingredient_id INTO @fruit_juice
FROM ingredients_info
WHERE ingredient_name = 'Fruit Juice'
LIMIT 1;

SELECT ingredient_id INTO @grains_and_legumes
FROM ingredients_info
WHERE ingredient_name = 'Grains & Legumes'
LIMIT 1;

SELECT ingredient_id INTO @juice
FROM ingredients_info
WHERE ingredient_name = 'Juice'
LIMIT 1;

SELECT ingredient_id INTO @lean_meat
FROM ingredients_info
WHERE ingredient_name = 'Lean Meat'
LIMIT 1;

SELECT ingredient_id INTO @liquids
FROM ingredients_info
WHERE ingredient_name = 'Liquids'
LIMIT 1;

SELECT ingredient_id INTO @meat
FROM ingredients_info
WHERE ingredient_name = 'Meat'
LIMIT 1;

SELECT ingredient_id INTO @milk
FROM ingredients_info
WHERE ingredient_name = 'Milk'
LIMIT 1;

SELECT ingredient_id INTO @noodles
FROM ingredients_info
WHERE ingredient_name = 'Noodles'
LIMIT 1;

SELECT ingredient_id INTO @nut_powder
FROM ingredients_info
WHERE ingredient_name = 'Nut Powder'
LIMIT 1;

SELECT ingredient_id INTO @nuts_and_seeds
FROM ingredients_info
WHERE ingredient_name = 'Nuts & Seeds'
LIMIT 1;

SELECT ingredient_id INTO @other_grains
FROM ingredients_info
WHERE ingredient_name = 'Other Grains'
LIMIT 1;

SELECT ingredient_id INTO @pasta
FROM ingredients_info
WHERE ingredient_name = 'Pasta'
LIMIT 1;

SELECT ingredient_id INTO @plant_milk
FROM ingredients_info
WHERE ingredient_name = 'Plant Milk'
LIMIT 1;

SELECT ingredient_id INTO @poultry
FROM ingredients_info
WHERE ingredient_name = 'Poultry'
LIMIT 1;

SELECT ingredient_id INTO @potatoes
FROM ingredients_info
WHERE ingredient_name = 'Potatoes'
LIMIT 1;

SELECT ingredient_id INTO @protein_powder
FROM ingredients_info
WHERE ingredient_name = 'Protein Powder'
LIMIT 1;

SELECT ingredient_id INTO @rice
FROM ingredients_info
WHERE ingredient_name = 'Rice'
LIMIT 1;

SELECT ingredient_id INTO @sauce
FROM ingredients_info
WHERE ingredient_name = 'Sauce'
LIMIT 1;

SELECT ingredient_id INTO @smoothie
FROM ingredients_info
WHERE ingredient_name = 'Smoothie'
LIMIT 1;

SELECT ingredient_id INTO @spices_and_herb_packs
FROM ingredients_info
WHERE ingredient_name = 'Spices And Herb Packs'
LIMIT 1;

SELECT ingredient_id INTO @sugar
FROM ingredients_info
WHERE ingredient_name = 'Sugar'
LIMIT 1;

SELECT ingredient_id INTO @syrup_and_spreads
FROM ingredients_info
WHERE ingredient_name = 'Syrup And Spreads'
LIMIT 1;

SELECT ingredient_id INTO @vegan_vitamin_powders
FROM ingredients_info
WHERE ingredient_name = 'Vegan Vitamin Powders'
LIMIT 1;

SELECT ingredient_id INTO @vegan_milk
FROM ingredients_info
WHERE ingredient_name = 'Vegan Milk'
LIMIT 1;

SELECT ingredient_id INTO @vegetables
FROM ingredients_info
WHERE ingredient_name = 'Vegetables'
LIMIT 1;

SELECT ingredient_id INTO @vitamins
FROM ingredients_info
WHERE ingredient_name = 'Vitamins'
LIMIT 1;

SELECT ingredient_id INTO @whole_wheat
FROM ingredients_info
WHERE ingredient_name = 'Whole Wheat'
LIMIT 1;

SELECT ingredient_id INTO @yoghurt
FROM ingredients_info
WHERE ingredient_name = 'Yoghurt'
LIMIT 1;

-- ######################################
-- Validate Variables Created
-- ######################################
DO
CASE
    WHEN
		@breads IS NULL
     OR @cake IS NULL
     OR @cereals IS NULL
     OR @cereal_bars IS NULL
     OR @cheese IS NULL
     OR @chocolate IS NULL
     OR @dairy IS NULL
     OR @desserts IS NULL
     OR @fish IS NULL
     OR @flour IS NULL
     OR @frozen_fruit IS NULL
     OR @frozen_vegetables IS NULL
     OR @fruit IS NULL
     OR @fruit_juice IS NULL
     OR @grains_and_legumes IS NULL
     OR @juice IS NULL
     OR @lean_meat IS NULL
     OR @liquids IS NULL
     OR @meat IS NULL
     OR @milk IS NULL
     OR @noodles IS NULL
     OR @nut_powder IS NULL
     OR @nuts_and_seeds IS NULL
     OR @other_grains IS NULL
     OR @pasta IS NULL
     OR @plant_milk IS NULL
     OR @poultry IS NULL
     OR @potatoes IS NULL
     OR @protein_powder IS NULL
     OR @rice IS NULL
     OR @sauce IS NULL
     OR @smoothie IS NULL
     OR @spices_and_herb_packs IS NULL
     OR @sugar IS NULL
     OR @syrup_and_spreads IS NULL
     OR @vegan_vitamin_powders IS NULL
     OR @vegan_milk IS NULL
     OR @vegetables IS NULL
     OR @vitamins IS NULL
     OR @whole_wheat IS NULL
     OR @yoghurt IS NULL
    THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Seed failed: one or more ingredient types could not be resolved';
END CASE;



