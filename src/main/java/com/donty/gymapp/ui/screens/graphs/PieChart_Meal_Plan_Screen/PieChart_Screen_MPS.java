package com.donty.gymapp.ui.screens.graphs.PieChart_Meal_Plan_Screen;

import com.donty.gymapp.persistence.database.MyJDBC_Sqlite;
import com.donty.gymapp.ui.components.meal.MealManager;
import com.donty.gymapp.persistence.Shared_Data_Registry;
import com.donty.gymapp.gui.base.Screen_JFrame;
import com.donty.gymapp.ui.screens.graphs.PieChart_Meal_Plan_Screen.Macro_Values.PieChart_Macros_MPS;
import com.donty.gymapp.ui.screens.graphs.PieChart_Meal_Plan_Screen.Total_Meals.PieChart_Totals_Entry_MPS;
import com.donty.gymapp.ui.screens.graphs.PieChart_Meal_Plan_Screen.Total_Meals.PieChart_TotalMeals_MPS;
import com.donty.gymapp.ui.screens.mealPlan.Meal_Plan_Screen;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class PieChart_Screen_MPS extends Screen_JFrame
{
    //#################################################################################################################
    // Variables
    // #################################################################################################################
    
    // Objects
    private Meal_Plan_Screen meal_plan_screen;
    private Shared_Data_Registry shared_Data_Registry;
    
    // Screens
    private PieChart_TotalMeals_MPS pieChart_TotalMeals_MPS;
    private PieChart_Macros_MPS pieChart_Macros_MPS;
    
    // #################################################################################################################
    // Constructor
    // #################################################################################################################
    public PieChart_Screen_MPS(MyJDBC_Sqlite db, Shared_Data_Registry shared_Data_Registry, Meal_Plan_Screen meal_plan_screen)
    {
        // ################################################################
        // Super Constructor
        // ################################################################
        super(db, false, String.format(" %s Pie Chart: Plan Macros", shared_Data_Registry.get_Plan_Name()), 1900, 1200, 0, 0);
        set_Resizable(true);
        
        // ################################################################
        // Variables
        // ################################################################
        this.meal_plan_screen = meal_plan_screen;
        this.shared_Data_Registry = shared_Data_Registry;
        
        //###################################################################################
        // Create ContentPane
        //###################################################################################
        getScrollPaneJPanel().setLayout(new GridLayout(1, 1));
        
        JTabbedPane tp = new JTabbedPane();
        getScrollPaneJPanel().add(tp);
        
        //#################################################
        // Creating TotalMeal Macros Screen
        //#################################################
        pieChart_TotalMeals_MPS = new PieChart_TotalMeals_MPS(shared_Data_Registry);
        tp.add("Macros Per Meal ", pieChart_TotalMeals_MPS);
        
        //#################################################
        // Creating Macros Screen
        //#################################################
        pieChart_Macros_MPS = new PieChart_Macros_MPS(shared_Data_Registry);
        tp.add("Macros Meal Plan ", pieChart_Macros_MPS);
        
        // ################################################################
        // Make Frame Visible
        // ################################################################
        setFrameVisibility(true);
        resizeGUI();
    }
    
    @Override
    public void window_Closed_Event()
    {
        // ####################################
        // Remove Attachment to MealPlanScreen
        // ####################################
        meal_plan_screen.remove_Pie_Chart_Screen();
        
        // ####################################
        // Remove GUI DATA
        // ####################################
        ArrayList<PieChart_Totals_Entry_MPS> pieChartEntry_MPS_AL = pieChart_TotalMeals_MPS.get_PieChart_Entry_MPS();
        
        /**
         * This is requested by the Meal_Plan_Screen when the pieChart screen is closed!
         * Remove the pieChart data which doesn't have a MealManager pie chart actively using it.
         */
        Iterator<PieChart_Totals_Entry_MPS> it = pieChartEntry_MPS_AL.iterator();
        while (it.hasNext())
        {
            PieChart_Totals_Entry_MPS pieChart_entry_mps = it.next();
            MealManager mealManager = pieChart_entry_mps.get_MealManager();
            
            // IF MealManager Associated with Entry isn't open, remove its dataset
            if (! mealManager.is_Pie_Chart_Open())
            {
                shared_Data_Registry.remove_PieChart_DatasetValues(mealManager);
            }
            
            // Remove this item from the MPS PieChart Screen
            it.remove();
        }
        
        // ####################################
        // Close JFrame
        // ####################################
        closeJFrame();
    }
    
    // #################################################################################################################
    // Methods
    // #################################################################################################################
    public void update_PieChart_MealName(MealManager mealManager)
    {
        pieChart_TotalMeals_MPS.update_PieChart_Title(mealManager);  // TotalMeal PieChart MPS
     
        pieChart_Macros_MPS.update_DATA(); // Macros PieChart MPS
    }
    
    public void update_PieChart_MealTime(MealManager mealManager)
    {
        //######################################
        // TotalMeal PieChart MPS
        //######################################
        pieChart_TotalMeals_MPS.update_PieChart_Title(mealManager);
        pieChart_TotalMeals_MPS.redraw_GUI();
        
        //######################################
        // Macros PieChart MPS
        //######################################
        pieChart_Macros_MPS.update_DATA();
    }
    
    public void updateData(MealManager mealManager)
    {
        // TotalMeal PieChart MPS nothing needs to be handled everything updates once the data is updated in the MPS
        
        pieChart_Macros_MPS.update_DATA();   // Macros PieChart MPS
    }
    
    public void refresh()
    {
        //######################################
        // TotalMeal PieChart MPS
        //######################################
        pieChart_TotalMeals_MPS.create_And_Draw_GUI();
        
        //######################################
        // Macros PieChart MPS
        //######################################
        pieChart_Macros_MPS.update_DATA();
    }
    
    public void clear()
    {
        //######################################
        // TotalMeal PieChart MPS
        //######################################
        pieChart_TotalMeals_MPS.clear();
        
        //######################################
        // Macros PieChart MPS
        //######################################
        pieChart_Macros_MPS.update_DATA();
    }
    
    public void add_MealManager_To_GUI(MealManager mealManager)
    {
        //######################################
        // TotalMeal PieChart MPS
        //######################################
        pieChart_TotalMeals_MPS.add_MealManager_To_GUI(mealManager);
        
        //######################################
        // Macros PieChart MPS
        //######################################
        pieChart_Macros_MPS.update_DATA();
    }
    
    public void deleted_MealManager_PieChart(MealManager mealManager)
    {
        //######################################
        // TotalMeal PieChart MPS
        //######################################
        pieChart_TotalMeals_MPS.delete_MealManager(mealManager);
        
        //######################################
        // Macros PieChart MPS
        //######################################
        pieChart_Macros_MPS.update_DATA();
    }
}
