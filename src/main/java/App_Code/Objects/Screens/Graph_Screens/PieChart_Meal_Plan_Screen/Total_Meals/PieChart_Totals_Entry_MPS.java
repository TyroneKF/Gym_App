package App_Code.Objects.Screens.Graph_Screens.PieChart_Meal_Plan_Screen.Total_Meals;

import App_Code.Objects.Table_Objects.MealManager;
import App_Code.Objects.Graph_Objects.Pie_Chart;
import org.jfree.data.time.Second;

public class PieChart_Totals_Entry_MPS
{
    
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    // Objects
    MealManager mealManager;
    Pie_Chart_Totals pieChart;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    PieChart_Totals_Entry_MPS(MealManager mealManager, Pie_Chart_Totals pieChart)
    {
        this.mealManager = mealManager;
        this.pieChart = pieChart;
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    public void update_PieChart_Title()
    {
        pieChart.update_PieChart_Title();
    }
    
    //##################################################################################################################
    // Accessors
    //##################################################################################################################
    public Second get_MealTime()
    {
        return mealManager.getCurrentMealTime();
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
