package App_Code.Objects.Screens.Graph_Screens.PieChart_Meal_Plan_Screen;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.MealManager;
import App_Code.Objects.Database_Objects.MealManagerRegistry;
import App_Code.Objects.Gui_Objects.Screens.Screen_JFrame;
import App_Code.Objects.Screens.Graph_Screens.PieChart_Meal_Plan_Screen.Macro_Values.PieChart_Macros_MPS;
import App_Code.Objects.Screens.Graph_Screens.PieChart_Meal_Plan_Screen.Total_Meals.PieChart_Totals_Entry_MPS;
import App_Code.Objects.Screens.Graph_Screens.PieChart_Meal_Plan_Screen.Total_Meals.PieChart_TotalMeals_MPS;
import App_Code.Objects.Screens.Meal_Plan_Screen;

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
    private MealManagerRegistry mealManagerRegistry;
    
    // Screens
    private PieChart_TotalMeals_MPS pieChart_TotalMeals_MPS;
    private PieChart_Macros_MPS pieChart_Macros_MPS;
    
    // #################################################################################################################
    // Constructor
    // #################################################################################################################
    public PieChart_Screen_MPS(MyJDBC db, Meal_Plan_Screen meal_plan_screen)
    {
        // ################################################################
        // Super Constructor
        // ################################################################
        super(db, false, String.format(" %s Pie Chart: Plan Macros", meal_plan_screen.getPlanName()), 1900, 1200, 0, 0);
        set_Resizable(true);
        
        // ################################################################
        // Variables
        // ################################################################
        this.meal_plan_screen = meal_plan_screen;
        this.mealManagerRegistry = meal_plan_screen.get_MealManagerRegistry();
        
        //###################################################################################
        // Create ContentPane
        //###################################################################################
        getScrollPaneJPanel().setLayout(new GridLayout(1, 1));
        
        JTabbedPane tp = new JTabbedPane();
        getScrollPaneJPanel().add(tp);
        
        //#################################################
        // Creating TotalMeal Macros Screen
        //#################################################
        pieChart_TotalMeals_MPS = new PieChart_TotalMeals_MPS(meal_plan_screen);
        tp.add("Macros Per Meal ", pieChart_TotalMeals_MPS);
        
        //#################################################
        // Creating Macros Screen
        //#################################################
        pieChart_Macros_MPS = new PieChart_Macros_MPS(meal_plan_screen);
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
        meal_plan_screen.removePieChartScreen();
        
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
            
            int mealManagerID = mealManager.getMealInPlanID();
            
            // IF MealManager Associated with Entry isn't open, remove its dataset
            if (! mealManager.is_PieChartOpen())
            {
                mealManagerRegistry.remove_PieChart_DatasetValues(mealManagerID);
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
    public void update_PieChart_MealName(int mealInPlanID)
    {
        //######################################
        // TotalMeal PieChart MPS
        //######################################
        pieChart_TotalMeals_MPS.update_PieChart_Title(mealInPlanID);
        
        //######################################
        // Macros PieChart MPS
        //######################################
        pieChart_Macros_MPS.update_DATA();
    }
    
    /**
     * @param mealInPlanID
     */
    public void update_PieChart_MealTime(int mealInPlanID)
    {
        //######################################
        // TotalMeal PieChart MPS
        //######################################
        pieChart_TotalMeals_MPS.update_PieChart_Title(mealInPlanID);
        pieChart_TotalMeals_MPS.redraw_GUI();
        
        //######################################
        // Macros PieChart MPS
        //######################################
        pieChart_Macros_MPS.update_DATA();
    }
    
    public void updateData()
    {
        //######################################
        // TotalMeal PieChart MPS
        //######################################
        // No Update needed, mealManager handles it
        
        //######################################
        // Macros PieChart MPS
        //######################################
        pieChart_Macros_MPS.update_DATA();
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
