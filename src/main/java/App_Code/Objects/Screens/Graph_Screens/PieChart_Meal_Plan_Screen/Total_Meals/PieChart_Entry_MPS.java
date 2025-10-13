package App_Code.Objects.Screens.Graph_Screens.PieChart_Meal_Plan_Screen.Total_Meals;

import App_Code.Objects.Database_Objects.MealManager;
import App_Code.Objects.Graph_Objects.Pie_Chart;
import org.jfree.data.time.Second;

public class PieChart_Entry_MPS
{
    
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    Integer mealInPlanID;
    
    //#######################################
    // Objects
    //#######################################
    MealManager mealManager;
    Pie_Chart pieChart;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    PieChart_Entry_MPS(Integer mealInPlanID, MealManager mealManager, Pie_Chart pieChart)
    {
        this.mealManager = mealManager;
        this.mealInPlanID = mealInPlanID;
        this.pieChart = pieChart;
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    public void update_PieChart_Title()
    {
        String title = String.format("[%s]      %s Macros", mealManager.getCurrentMealTimeGUI(), mealManager.getCurrentMealName());
        pieChart.setTitle(title);
    }
    
    //##################################################################################################################
    // Accessors
    //##################################################################################################################
    public Second get_MealTime()
    {
        return mealManager.getCurrentMealTime();
    }
    
    public int get_MealInPlanID()
    {
        return mealInPlanID;
    }
    
    public Pie_Chart get_PieChart()
    {
        return pieChart;
    }
    
    public MealManager get_MealManager()
    {
        return mealManager;
    }
}
