package com.donty.gymapp.Table_Objects.Tables.Children.View_Data_Tables.Children.Total_Meal_Table;

import com.donty.gymapp.Table_Objects.Tables.Parent.Table_Enum;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Total_Meal_Macro_Columns implements Table_Enum
{
    //##################################################################################################################
    // Enums
    //##################################################################################################################
    
    // Macros
    TOTAL_PROTEIN("total_protein"),
    TOTAL_CARBOHYDRATES("total_carbohydrates"),
    TOTAL_SUGARS_OF_CARBS("total_sugars_of_carbs"),
    TOTAL_FATS("total_fats"),
    TOTAL_SATURATED_FAT("total_saturated_fat"),
    TOTAL_SALT("total_salt"),
    TOTAL_FIBRE("total_fibre"),
    TOTAL_WATER("total_water"),
    TOTAL_CALORIES("total_calories");
    
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private static final String SOURCE_NAME = "draft_gui_total_meal_view";
    
    private final String source_name;
    
    
    private final String key;
    
    private static final Map<String, Total_Meal_Macro_Columns> BY_KEY =
            Arrays.stream(values())
                    .collect(Collectors.toMap(
                            Total_Meal_Macro_Columns :: key,
                            Function.identity()
                    ));
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    Total_Meal_Macro_Columns(String key)
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