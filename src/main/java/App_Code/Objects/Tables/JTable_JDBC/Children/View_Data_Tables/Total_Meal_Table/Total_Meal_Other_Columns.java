package App_Code.Objects.Tables.JTable_JDBC.Children.View_Data_Tables.Total_Meal_Table;

import App_Code.Objects.Tables.JTable_JDBC.My_Enum;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Total_Meal_Other_Columns implements My_Enum
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