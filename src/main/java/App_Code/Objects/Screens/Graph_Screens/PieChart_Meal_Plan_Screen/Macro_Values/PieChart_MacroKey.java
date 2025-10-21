package App_Code.Objects.Screens.Graph_Screens.PieChart_Meal_Plan_Screen.Macro_Values;

import App_Code.Objects.Database_Objects.MealManager;
import App_Code.Objects.Database_Objects.MealManagerRegistry;

import java.util.Objects;

public class PieChart_MacroKey implements Comparable<PieChart_MacroKey>
{
    // ############################################################################################
    // Variables
    // ############################################################################################
    
    // Objects
    private MealManager mealManager;
    private MealManagerRegistry mealManagerRegistry;
    
    // Integers
    private int mealInPlanID;
    private String macroName, macroSymbol;
    
    // #################################################################################################################
    // Constructor
    // #################################################################################################################
    public PieChart_MacroKey(MealManager mealManager, String macroName, String macroSymbol)
    {
        //############################################
        // Variables
        //############################################
        this.mealManager = mealManager;
        
        this.mealInPlanID = mealManager.getMealInPlanID();
        this.macroName = macroName;
        this.macroSymbol = macroSymbol;
    }
    
    // #################################################################################################################
    // Accessor Methods
    // #################################################################################################################
    public int get_MealInPlanID()
    {
        return mealInPlanID;
    }
    
    public long get_MealTime()
    {
        return mealManager.getCurrentMealTime().getFirstMillisecond();
    }
    
    public String get_MealTime_GUI()
    {
        return mealManager.getCurrentMealTimeGUI();
    }
    
    public String get_MealName() { return mealManager.getCurrentMealName(); }
    
    public String get_MacroSymbol()
    {
        return macroSymbol;
    }
    
    public String get_MacroName()
    {
        return macroName;
    }
    
    // #################################################################################################################
    // Default Methods
    // #################################################################################################################
    @Override
    public int compareTo(PieChart_MacroKey other)
    {
        return Long.compare(this.get_MealTime(), other.get_MealTime());
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
