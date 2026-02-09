package com.donty.gymapp.ui.screens.ingredientsAndInventory.Ingredients_Info.base.products;

import com.donty.gymapp.domain.enums.table_enums.base.Table_Enum;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum productEnum implements Table_Enum
{
    //##################################################################################################################
    // Enums
    //##################################################################################################################
    PDID("pdid"),
    IS_SYSTEM("is_system"),
    INGREDIENT_ID("ingredient_id"),
    STORE_ID("store_id"),
    PRODUCT_NAME("product_name"),
    VOLUME_PER_UNIT("volume_per_unit"),
    COST_PER_UNIT("cost_per_unit");

    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private static final String SOURCE_NAME = "ingredients_info";

    private final String source_name;
    private final String key;

    private static final Map<String, productEnum> BY_KEY =
            Arrays.stream(values())
                    .collect(Collectors.toMap(
                            productEnum :: key,
                            Function.identity()
                    ));

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
