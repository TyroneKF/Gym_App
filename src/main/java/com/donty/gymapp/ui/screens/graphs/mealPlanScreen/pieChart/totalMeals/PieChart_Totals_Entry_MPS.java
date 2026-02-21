package com.donty.gymapp.ui.screens.graphs.mealPlanScreen.pieChart.totalMeals;

import com.donty.gymapp.ui.components.meal.MealManager;
import java.time.LocalTime;

/*
 * @param mealManager
 */
public record PieChart_Totals_Entry_MPS
(
        MealManager mealManager,
        Pie_Chart_Totals pieChart
)
{
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

    public Pie_Chart_Totals get_PieChart()
    {
        return pieChart;
    }

    public MealManager get_MealManager()
    {
        return mealManager;
    }
}
