package App_Code.Objects.Screens.Graph_Screens.PieChart_Meal_Plan_Screen;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.MealManager;
import App_Code.Objects.Database_Objects.MealManagerRegistry;
import App_Code.Objects.Gui_Objects.Screens.Screen_JFrame;
import App_Code.Objects.Screens.Graph_Screens.PieChart_Meal_Plan_Screen.Macro_Values.PieChart_Macros_MPS;
import App_Code.Objects.Screens.Graph_Screens.PieChart_Meal_Plan_Screen.Total_Meals.PieChart_TotalMeal_Macros_MPS;
import App_Code.Objects.Screens.Meal_Plan_Screen;

import javax.swing.*;
import java.awt.*;

public class PieChart_Screen_MPS extends Screen_JFrame
{
    //#################################################################################################################
    // Variables
    // #################################################################################################################
    
    // Objects
    private Meal_Plan_Screen meal_plan_screen;
    private MealManagerRegistry mealManagerRegistry;
    
    private PieChart_TotalMeal_Macros_MPS pieChart_Total_MPS;
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
        pieChart_Total_MPS = new PieChart_TotalMeal_Macros_MPS (meal_plan_screen, frameWidth, frameHeight);
        tp.add("Macros Per Meal ", pieChart_Total_MPS);
    
        //#################################################
        // Creating Macros Screen
        //#################################################
        pieChart_Macros_MPS = new PieChart_Macros_MPS(meal_plan_screen, frameWidth, frameHeight);
        tp.add("Macros Meal Plan ", pieChart_Macros_MPS);
        
        // ################################################################
        // Make Frame Visible
        // ################################################################
        setFrameVisibility(true);
        resizeGUI();
    }
    
    @Override
    public void windowClosedEvent()
    {
        // ####################################
        // Remove Attachment to MealPlanScreen
        // ####################################
        meal_plan_screen.removePieChartScreen();
        
        // ####################################
        // Remove GUI DATA
        // ####################################
        mealManagerRegistry.remove_Unused_PieData();
        
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
        pieChart_Total_MPS.update_PieChart_MealName(mealInPlanID);
    
        //######################################
        // Macros PieChart MPS
        //######################################
        
    }
    
    public void refresh()
    {
        //######################################
        // TotalMeal PieChart MPS
        //######################################
        pieChart_Total_MPS.create_And_Draw_GUI();
    
        //######################################
        // Macros PieChart MPS
        //######################################
        
    }
    
    public void deleted_MealManager_PieChart(MealManager mealManager)
    {
        //######################################
        // TotalMeal PieChart MPS
        //######################################
        pieChart_Total_MPS.redraw_GUI();
    
        //######################################
        // Macros PieChart MPS
        //######################################
        
    }
    
    
    public void add_MealManager_To_GUI(MealManager mealManager)
    {
        //######################################
        // TotalMeal PieChart MPS
        //######################################
        pieChart_Total_MPS.add_MealManager_To_GUI(mealManager);
    
        //######################################
        // Macros PieChart MPS
        //######################################
    }
}
