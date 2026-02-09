package com.donty.gymapp.domain.enums.db_enums.views.totalmeal;

import com.donty.gymapp.domain.enums.db_enums.base.Table_Enum;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Draft_Gui_Total_Meal_Other_Columns implements Table_Enum
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
    
    private static final Map<String, Draft_Gui_Total_Meal_Other_Columns> BY_KEY =
            Arrays.stream(values())
                    .collect(Collectors.toMap(
                            Draft_Gui_Total_Meal_Other_Columns :: key,
                            Function.identity()
                    ));
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    Draft_Gui_Total_Meal_Other_Columns(String key)
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