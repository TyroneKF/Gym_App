package com.donty.gymapp.ui.screens.ingredientsAndInventory.Ingredients_Info.base.ingredients;

import com.donty.gymapp.domain.enums.base.Table_Enum;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Ingredient_Enum implements Table_Enum
{
    //##################################################################################################################
    // Enums
    //##################################################################################################################
    INGREDIENT_ID("ingredient_id"),
    IS_SYSTEM("is_system"),
    MEASUREMENT_ID("measurement_id"),
    INGREDIENT_TYPE_ID("ingredient_type_id"),
    INGREDIENT_NAME("ingredient_name"),
    BASED_ON_QUANTITY("based_on_quantity"),
    GLYCEMIC_INDEX("glycemic_index"),
    PROTEIN("protein"),
    CARBOHYDRATES("carbohydrates"),
    SUGARS_OF_CARBS("sugars_of_carbs"),
    FIBRE("fibre"),
    FAT("fat"),
    SATURATED_FAT("saturated_fat"),
    SALT("salt"),
    WATER_CONTENT("water_content"),
    LIQUID_CONTENT("liquid_content"),
    CALORIES("calories");

    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private static final String SOURCE_NAME = "ingredients_info";

    private final String source_name;
    private final String key;

    private static final Map<String, Ingredient_Enum> BY_KEY =
            Arrays.stream(values())
                    .collect(Collectors.toMap(
                            Ingredient_Enum :: key,
                            Function.identity()
                    ));

    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    Ingredient_Enum(String key)
    {
        this.key = key;
        this.source_name = SOURCE_NAME;
    }

    //##################################################################################################################
    // Methods
    //##################################################################################################################
    @Override
    public String source_Name()
    {
        return source_name;
    }

    @Override
    public String key()
    {
        return key;
    }
}
