package App_Code.Objects.Screens.Graph_Screens.PieChart_Meal_Plan_Screen.Macro_Values;

import App_Code.Objects.Table_Objects.Tables.Children.View_Data_Tables.Children.Total_Meal_Table.Total_Meal_Macro_Columns;
import App_Code.Objects.Table_Objects.MealManager;
import java.time.LocalTime;
import java.util.Objects;

public class PieChart_MacroKey implements Comparable<PieChart_MacroKey>
{
    // #################################################################################################################
    // Variables
    // #################################################################################################################
    
    // Objects
    private MealManager mealManager;
    
    // Integers
    private int mealInPlanID;
    private String macroSymbol;
    private Total_Meal_Macro_Columns macro_name;
    
    // #################################################################################################################
    // Constructor
    // #################################################################################################################
    public PieChart_MacroKey(MealManager mealManager, Total_Meal_Macro_Columns macro_name, String macroSymbol)
    {
        //############################################
        // Variables
        //############################################
        this.mealManager = mealManager;
        
        this.mealInPlanID = mealManager.get_Draft_Meal_ID();
        this.macro_name = macro_name;
        this.macroSymbol = macroSymbol;
    }
    
    // #################################################################################################################
    // Accessor Methods
    // #################################################################################################################
    public int get_MealInPlanID()
    {
        return mealInPlanID;
    }
    
    public LocalTime get_MealTime()
    {
        return mealManager.get_Current_Meal_Time();
    }
    
    public String get_MealTime_GUI()
    {
        return mealManager.get_Current_Meal_Time().toString();
    }
    
    public String get_MealName() { return mealManager.get_Current_Meal_Name(); }
    
    public String get_MacroSymbol()
    {
        return macroSymbol;
    }
    
    public String get_MacroName()
    {
        return macro_name.key();
    }
    
    // #################################################################################################################
    // Default Methods
    // #################################################################################################################
    @Override
    public int compareTo(PieChart_MacroKey other)
    {
        return this.get_MealTime().compareTo(other.get_MealTime());
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) { return true; }
        if (obj == null || getClass() != obj.getClass()) { return false; }
        
        PieChart_MacroKey other = (PieChart_MacroKey) obj;
        return get_MealInPlanID() == other.get_MealInPlanID();
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(get_MealInPlanID());
    }
    
    @Override
    public String toString()
    {
        return String.format("PieChart_MacroKey{MealManagerID=%s}", get_MealInPlanID());
    }
}
