-- #################################################################################################
-- Seed Ingredient  Types For Demo Data
-- #################################################################################################






SELECT
    'breads'                     AS type, @breads                     IS NULL AS missing UNION ALL
SELECT 'cake',                        @cake                        IS NULL UNION ALL
SELECT 'cereals',                     @cereals                     IS NULL UNION ALL
SELECT 'cereal_bars',                 @cereal_bars                 IS NULL UNION ALL
SELECT 'cheese',                      @cheese                      IS NULL UNION ALL
SELECT 'chocolate',                   @chocolate                   IS NULL UNION ALL
SELECT 'dairy',                       @dairy                       IS NULL UNION ALL
SELECT 'desserts',                    @desserts                    IS NULL UNION ALL
SELECT 'fish',                        @fish                        IS NULL UNION ALL
SELECT 'flour',                       @flour                       IS NULL UNION ALL
SELECT 'frozen_fruit',                @frozen_fruit                IS NULL UNION ALL
SELECT 'frozen_vegetables',           @frozen_vegetables           IS NULL UNION ALL
SELECT 'fruit',                       @fruit                       IS NULL UNION ALL
SELECT 'fruit_juice',                 @fruit_juice                 IS NULL UNION ALL
SELECT 'grains_and_legumes',           @grains_and_legumes           IS NULL UNION ALL
SELECT 'juice',                       @juice                       IS NULL UNION ALL
SELECT 'lean_meat',                   @lean_meat                   IS NULL UNION ALL
SELECT 'liquids',                     @liquids                     IS NULL UNION ALL
SELECT 'meat',                        @meat                        IS NULL UNION ALL
SELECT 'milk',                        @milk                        IS NULL UNION ALL
SELECT 'noodles',                     @noodles                     IS NULL UNION ALL
SELECT 'nut_powder',                  @nut_powder                  IS NULL UNION ALL
SELECT 'nuts_and_seeds',              @nuts_and_seeds              IS NULL UNION ALL
SELECT 'other_grains',                @other_grains                IS NULL UNION ALL
SELECT 'pasta',                       @pasta                       IS NULL UNION ALL
SELECT 'plant_milk',                  @plant_milk                  IS NULL UNION ALL
SELECT 'poultry',                     @poultry                     IS NULL UNION ALL
SELECT 'potatoes',                    @potatoes                    IS NULL UNION ALL
SELECT 'protein_powder',              @protein_powder              IS NULL UNION ALL
SELECT 'rice',                        @rice                        IS NULL UNION ALL
SELECT 'sauce',                       @sauce                       IS NULL UNION ALL
SELECT 'smoothie',                    @smoothie                    IS NULL UNION ALL
SELECT 'spices_and_herb_packs',        @spices_and_herb_packs        IS NULL UNION ALL
SELECT 'sugar',                       @sugar                       IS NULL UNION ALL
SELECT 'syrup_and_spreads',            @syrup_and_spreads            IS NULL UNION ALL
SELECT 'vegan_vitamin_powders',        @vegan_vitamin_powders        IS NULL UNION ALL
SELECT 'vegan_milk',                  @vegan_milk                  IS NULL UNION ALL
SELECT 'vegetables',                  @vegetables                  IS NULL UNION ALL
SELECT 'vitamins',                    @vitamins                    IS NULL UNION ALL
SELECT 'whole_wheat',                 @whole_wheat                 IS NULL UNION ALL
SELECT 'yoghurt',                     @yoghurt                     IS NULL;

-- #################################################################################################
-- Seed Ingredients For Demo Dat
-- #################################################################################################

-- Create Ingredients
INSERT INTO ingredients_info
(
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

("Water", @liquids, 100.00, 0, 0, 0, 0, 0, 0, 0, 0, 100, 100, 0),
("Banana", @fruit, 100.00, 51, 1.1, 23, 12, 2.6, 0.3, 0.1, 0, 0, 0, 89),
("Wholemeal Wraps (Tesco)", @breads, 1.00, 30, 5.5, 27.9, 2, 4.2, 3.2, 1.5, 0.55, 0, 0, 170),
("Spinach", @vegetables, 100.00, 15, 2.9, 3.6, 0.4, 2.2, 0.4, 0.1, 0, 0, 0, 23),
("Heinz Classic Barbecue Sauce", @sauce, 100.00, 0, 0.9, 34, 29, 0, 0.2, 0.1, 1.2, 0, 100, 138),
("Kale", @vegetables, 100.00, 3, 2.92, 4.42, 0.8, 4.1, 1.49, 0, 0, 0, 0, 20),
("Sweet Potatoe", @vegetables, 100.00, 68, 1.6, 20, 4.2, 3, 0.1, 0, 0, 0, 0, 86),
("Carrots", @vegetables, 100.00, 41, 0.9, 10, 4.7, 2.8, 0.2, 0, 0, 0, 0, 41),
("Brocolli", @vegetables, 100.00, 15, 2.57, 6.27, 1.4, 2.4, 0.34, 0.03, 0, 0, 0, 39),
("Celery", @vegetables, 100.00, 35, 0.49, 3.32, 0, 0, 0.16, 0, 0, 0, 0, 17),

("Frozen Mangoes (ASDA)", @frozen_fruit, 100.00, 53, 0.7, 14, 14, 2.6, 0, 0, 0, 0, 0, 66),
("Frozen Pineapple (ASDA)", @frozen_fruit, 100.00, 62, 0, 10, 10, 1.2, 0, 0, 0, 0, 0, 46),
("Oranges", @fruit, 100.00, 40, 0.9, 12, 9, 2.4, 0.1, 0, 0, 0, 0, 47),
("Frozen Strawberries (ASDA)", @frozen_fruit, 100.00, 40, 0.6, 6.1, 6.1, 3.8, 0, 0, 0, 0, 0, 39),
("Frozen Blueberries (ASDA)", @frozen_fruit, 100.00, 53, 0.9, 9.1, 9.1, 1.5, 0, 0, 0, 0, 0, 45),
("Frozen Raspberries (ASDA)", @frozen_fruit, 100.00, 32, 0.8, 5.1, 5.1, 3.7, 0, 0, 0, 0, 0, 34),

("Pumpkin Seeds", @nuts_and_seeds, 100.00, 25, 19, 54, 0, 18, 19, 3.7, 0, 0, 0, 446),
("Sunflower Seeds", @nuts_and_seeds, 100.00, 22, 21, 20, 2.6, 9, 51, 4.5, 0, 0, 0, 584),
("Pistachios", @nuts_and_seeds, 100.00, 15, 20, 28, 8, 10, 45, 6, 0, 0, 0, 562),
("Cashew Nuts", @nuts_and_seeds, 100.00, 25, 18, 30, 6, 3.3, 44, 8, 0, 0, 0, 553),

("Chicken Mince (ASDA)", @poultry, 100.00, 0, 28, 0.5, 0, 0.5, 5.7, 1.5, 0, 0, 0, 166),
("Lean Turkey Mince (ASDA)", @poultry, 100.00, 0, 19, 0.5, 0.5, 0.5, 6.8, 1.9, 0.25, 0, 0, 140),

("Maca (VivoLife)", @vegan_vitamin_powders, 100.00, 54, 10, 75, 25, 8.5, 2.2, 0, 0, 0, 0, 377),
("Thrive (VivoLife)", @vegan_vitamin_powders, 1.00, 0, 0.4, 3.4, 0.9, 2.5, 0.05, 0.02, 0.08, 0, 0, 21),

("ASDA Scottish Porridge Oats", @cereals, 100.00, 55, 1.6, 7.8, 0.5, 1.3, 0.8, 0.1, 0.01, 0, 0, 47),
("Rowse Organic Honey", @syrup_and_spreads, 100.00, 58, 0.5, 81.5, 80.8, 0.5, 0.5, 0.2, 0.03, 0, 100, 329),

("Rice Dream Original Organic Milk", @plant_milk, 100.00, 86, 0.1, 9.9, 7.1, 0, 1.1, 0.1, 0.07, 0, 100, 50),
("Alpro Soya Light Long Life Drink", @plant_milk, 100.00, 30, 2.1, 1.7, 1.5, 0.9, 1.2, 0.2, 0.11, 0, 100, 28),
("Alpro Organic Soya", @plant_milk, 100.00, 30, 3.3, 0, 0, 0.6, 1.9, 0.3, 0.03, 0, 100, 32),

("Creatine Monohydrate (MyProtein)", @protein_powder, 4.00, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0),
("Whey Isolate Natural Strawberry (MyProtein)", @protein_powder, 25.00, 0, 22, 1.5, 1.5, 0, 0.1, 0.1, 0.13, 0, 0, 93),
("Whey Isolate Chocolate Smooth (MyProtein)", @protein_powder, 25.00, 0, 21, 1.6, 0.6, 0, 0.2, 0.1, 0.13, 0, 0, 91),
("Slow-Release Casein Vanilla (MyProtein)", @protein_powder, 30.00, 0, 25, 3, 2.4, 0, 0.4, 0.2, 0.08, 0, 0, 116),

("Dolmio Bolognese Pasta Sauce Low Fat", @sauce, 100.00, 35, 1.3, 6.7, 3.8, 1.1, 0.5, 0.1, 0.71, 0, 100, 36),
("Wholewheat Spaghetti (ASDA)", @pasta, 100.00, 40, 4.8, 28, 0.5, 3.7, 0.9, 0.2, 0.03, 0, 0, 148),
("Sweetcorn", @vegetables, 100.00, 55, 3.2, 19, 3.2, 2.7, 1.2, 0.2, 0, 0, 0, 86),
("Semi Skin Cow Milk", @milk, 100.00, 31, 3.6, 4.8, 4.8, 0, 1.8, 1.1, 0.11, 0, 100, 50),
("Black Beans", @grains_and_legumes, 172.00, 30, 15.2, 40.8, 0.6, 15, 0.9, 0.2, 0, 0, 0, 227),
("Grapefruit", @fruit, 100.00, 25, 0.6, 8.1, 7, 1.1, 0.1, 0, 0, 0, 0, 32),
("Corned Beef (Princess Reduced Fat)", @meat, 100.00, 0, 25, 0, 0, 0, 13.5, 6.5, 1.4, 0, 0, 222),
("Large Eggs (60g)", @poultry, 1.00, 0, 8.3, 0.5, 0.5, 0.5, 5.7, 1.6, 0.22, 0, 0, 85),
("Fage Greek Yoghurt", @yoghurt, 100.00, 12, 10.3, 3, 3, 0, 0, 0, 0.1, 0, 100, 54),
("Be-Ro Plain Flour", @flour, 100.00, 85, 10.4, 70.1, 1.4, 3.2, 1.3, 0.2, 0, 0, 0, 340),
("Allisons Wholemeal Flour", @flour, 100.00, 60, 12, 65, 1.4, 10, 2.6, 0.5, 0.03, 0, 0, 350),
("Bertolli Olive Oil Spread", @dairy, 100.00, 0, 0.5, 0.5, 0.5, 0, 59, 17, 1.1, 0, 0, 531),
("Allinson Self-Raising Wholemeal Flour", @flour, 100.00, 60, 11, 69.3, 1.1, 10, 2.4, 0.4, 1.1, 0, 0, 359),
("Plantin", @fruit, 100.00, 40, 1.3, 32, 15, 2.3, 0.4, 0.1, 0, 0, 0, 122),
("Billingtons Brown Sugar", @sugar, 100.00, 70, 0, 99, 99, 0, 0, 0, 0.25, 0, 0, 398);

-- ##########################################################
-- Create Variables for Ingredients
-- ##########################################################
