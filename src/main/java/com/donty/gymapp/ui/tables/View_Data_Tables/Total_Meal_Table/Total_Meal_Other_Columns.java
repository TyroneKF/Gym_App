package com.donty.gymapp.ui.tables.View_Data_Tables.Total_Meal_Table;

import com.donty.gymapp.domain.enums.Table_Enum;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Total_Meal_Other_Columns implements Table_Enum
{
    //##################################################################################################################
    // Enums
    //##################################################################################################################

    // Other Columns
    MEAL_TIME("meal_time"),
    MEAL_NAME("meal_name");
    
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private static final String SOURCE_NAME = "draft_gui_total_meal_view";
    
    private final String source_name;
    
    private final String key;
    
    private static final Map<String, Total_Meal_Other_Columns> BY_KEY =
            Arrays.stream(values())
                    .collect(Collectors.toMap(
                            Total_Meal_Other_Columns :: key,
                            Function.identity()
                    ));
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    Total_Meal_Other_Columns(String key)
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