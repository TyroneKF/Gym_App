package com.donty.gymapp.ui.screens.ingredientsAndInventory.ingredientsInfo.base.ingredients;

import com.donty.gymapp.domain.enums.db_enums.columnNames.base.Table_Enum;

public enum Ingredient_Info_Columns implements Table_Enum
{
    //##################################################################################################################
    // Enums
    //##################################################################################################################
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
    private final String key;



    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    Ingredient_Info_Columns(String key)
    {
        this.key = key;
    }

    //##################################################################################################################
    // Methods
    //##################################################################################################################
    @Override
    public String key()
    {
        return key;
    }
}
