package com.donty.gymapp.ui.screens.graphs.PieChart_Meal_Plan_Screen.Total_Meals;

import com.donty.gymapp.ui.components.meal.MealManager;
import com.donty.gymapp.ui.charts.Pie_Chart;
import java.time.LocalTime;

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
    public LocalTime get_MealTime()
    {
        return mealManager.get_Current_Meal_Time();
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
