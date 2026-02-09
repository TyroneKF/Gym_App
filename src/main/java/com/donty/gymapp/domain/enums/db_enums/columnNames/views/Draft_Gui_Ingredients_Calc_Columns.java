package com.donty.gymapp.domain.enums.db_enums.columnNames.views;

import com.donty.gymapp.domain.enums.db_enums.columnNames.base.Table_Enum;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Draft_Gui_Ingredients_Calc_Columns implements Table_Enum
{
    //##################################################################################################################
    // Enums
    //##################################################################################################################
    DRAFT_INGREDIENTS_INDEX("draft_ingredients_index"),
    INGREDIENT_TYPE_NAME("ingredient_type_name"),
    INGREDIENT_NAME("ingredient_name"),
    QUANTITY("quantity"),
    GI("gi"),
    PROTEIN("protein"),
    CARBOHYDRATES("carbohydrates"),
    SUGARS_OF_CARBS("sugars_of_carbs"),
    FIBRE("fibre"),
    FAT("fat"),
    SATURATED_FAT("saturated_fat"),
    SALT("salt"),
    WATER_CONTENT("water_content"),
    CALORIES("calories"),
    DELETE_BTN("delete_button");
    
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private static final String SOURCE_NAME = "draft_gui_ingredients_in_sections_of_meal_calculation";
    
    private final String source_name;
    private final String key;
    
    private static final Map<String, Draft_Gui_Ingredients_Calc_Columns> BY_KEY =
            Arrays.stream(values())
                    .collect(Collectors.toMap(
                            Draft_Gui_Ingredients_Calc_Columns :: key,
                            Function.identity()
                    ));
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    Draft_Gui_Ingredients_Calc_Columns(String key)
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
