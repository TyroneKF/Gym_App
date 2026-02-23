package com.donty.gymapp.ui.screens.ingredientsAndInventory.ingredientsInfo.base.products;

import com.donty.gymapp.domain.enums.db_enums.columnNames.base.Table_Enum;


public enum productEnum implements Table_Enum
{
    //##################################################################################################################
    // Enums
    //##################################################################################################################
    INGREDIENT_ID("ingredient_id"),
    PRODUCT_NAME("product_name"),
    VOLUME_PER_UNIT("volume_per_unit"),
    COST_PER_UNIT("cost_per_unit");

    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private static final String SOURCE_NAME = "ingredients_info";

    private final String source_name;
    private final String key;

    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    productEnum(String key)
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
