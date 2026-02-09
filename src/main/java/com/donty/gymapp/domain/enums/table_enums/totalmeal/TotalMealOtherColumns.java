package com.donty.gymapp.domain.enums.table_enums.totalmeal;

import com.donty.gymapp.domain.enums.table_enums.base.Table_Enum;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum TotalMealOtherColumns implements Table_Enum
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
    
    private static final Map<String, TotalMealOtherColumns> BY_KEY =
            Arrays.stream(values())
                    .collect(Collectors.toMap(
                            TotalMealOtherColumns :: key,
                            Function.identity()
                    ));
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    TotalMealOtherColumns(String key)
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