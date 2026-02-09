package com.donty.gymapp.domain.enums.table_enums;

import com.donty.gymapp.domain.enums.table_enums.base.Table_Enum;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum IngredientsTableColumns implements Table_Enum
{
    //##################################################################################################################
    // Enums
    //##################################################################################################################
    DRAFT_INGREDIENTS_INDEX("draft_ingredients_index"),
    INGREDIENT_TYPE_NAME("ingredient_type_name"),
    INGREDIENT_NAME("ingredient_name"),
    QUANTITY("quantity"),
    DELETE_BTN("delete_button");
    
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private static final String SOURCE_NAME = "draft_gui_ingredients_in_sections_of_meal_calculation";
    
    private final String source_name;
    private final String key;
    
    private static final Map<String, IngredientsTableColumns> BY_KEY =
            Arrays.stream(values())
                    .collect(Collectors.toMap(
                            IngredientsTableColumns :: key,
                            Function.identity()
                    ));
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    IngredientsTableColumns(String key)
    {
        this.key = key;
        this.source_name = SOURCE_NAME;
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    
    @Override
    public String key()
    {
        return key;
    }
    
    
    @Override
    public String source_Name()
    {
        return source_name;
    }
}
