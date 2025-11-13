package App_Code.Objects.Screens.Graph_Screens.LineChart_Meal_Plan_Screen;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Tables.MealManager;
import App_Code.Objects.Database_Objects.Shared_Data_Registry;
import App_Code.Objects.Gui_Objects.Screens.Screen_JFrame;
import App_Code.Objects.Screens.Meal_Plan_Screen;
import org.jfree.data.time.Second;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class LineChart_MPS extends Screen_JFrame
{
    //#################################################################################################################
    // Variables
    // #################################################################################################################
    
    // Objects
    private Meal_Plan_Screen meal_plan_screen;
    private Shared_Data_Registry shared_Data_Registry;
    
    // Screens
    private LineChart_Macros_MPS lineChart_Main_Macros_MPS, lineChart_Bigger_Macros_MPS;
    
    
    // #################################################################################################################
    // Constructor
    // #################################################################################################################
    public LineChart_MPS(MyJDBC db, Meal_Plan_Screen meal_plan_screen)
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
        this.shared_Data_Registry = meal_plan_screen.get_MealManagerRegistry();
        
        //###################################################################################
        // Create ContentPane
        //###################################################################################
        getScrollPaneJPanel().setLayout(new GridLayout(1, 1));
        
        JTabbedPane tp = new JTabbedPane();
        getScrollPaneJPanel().add(tp);
        
        //#################################################
        // Creating Macros LineChart Screen
        //#################################################
        ArrayList<String> macros_To_Check = new ArrayList<>(Arrays.asList(
                "total_protein",
                "total_carbohydrates",
                "total_sugars_of_carbs",
                "total_fats",
                "total_saturated_fat",
                "total_salt",
                "total_fibre"
        ));
        
        String title = String.format("%s : Macros Over 24 Hours", meal_plan_screen.getPlanName());
        
        lineChart_Main_Macros_MPS = new LineChart_Macros_MPS(db, meal_plan_screen, title, macros_To_Check, 1000, 900);
        
        tp.add("Macros Per Meal ", lineChart_Main_Macros_MPS);
        
        //#################################################
        // Creating Macros Screen
        //#################################################
        /**
         *  Create and override methods that display data as we're just interested in calories and water in this chart
         */
        
        ArrayList<String> macros_To_Check2 = new ArrayList<>(Arrays.asList(
                "total_water", "total_calories"
                //,"total_liquid"
        ));
    
        String title2 = String.format("%s : Calories & Water Over 24 Hours", meal_plan_screen.getPlanName());
        
        lineChart_Bigger_Macros_MPS = new LineChart_Macros_MPS(db, meal_plan_screen, title2, macros_To_Check2, 1000, 900);
        
        tp.add("Calories & Water Per Meal ", lineChart_Bigger_Macros_MPS);
        
        // ################################################################
        // Make Frame Visible
        // ################################################################
        setFrameVisibility(true);
        resizeGUI();
    }
    
    @Override
    public void window_Closed_Event()
    {
        meal_plan_screen.removeLineChartScreen();
        closeJFrame();
    }
    
    // #################################################################################################################
    // Methods
    // #################################################################################################################
    public void update_MealManager_ChartData(MealManager mealManager, Second previousTime, Second currentTime)
    {
        // LineChart Main Macros
        lineChart_Main_Macros_MPS.update_MealManager_ChartData(mealManager, previousTime, currentTime);
        
        // LineChart Water & Calories Macros
        lineChart_Bigger_Macros_MPS.update_MealManager_ChartData(mealManager, previousTime, currentTime);
    }
    
    //##################################################
    //  Update  Methods
    //##################################################
    public void update_LineChart_Title()
    {
        // LineChart Main Macros
        lineChart_Main_Macros_MPS.update_LineChart_Title();
        
        // LineChart Water & Calories Macros
        lineChart_Bigger_Macros_MPS.update_LineChart_Title();
    }
    
    //##################################################
    //  Add
    //##################################################
    public void add_New_MealManager_Data(MealManager mealManager)
    {
        // LineChart Main Macros
        lineChart_Main_Macros_MPS.add_New_MealManager_Data(mealManager);
        
        // LineChart Water & Calories Macros
        lineChart_Bigger_Macros_MPS.add_New_MealManager_Data(mealManager);
    }
    
    //##################################################
    //  Clear
    //##################################################
    public void clear_LineChart_Dataset()
    {
        // LineChart Main Macros
        lineChart_Main_Macros_MPS.clear_LineChart_Dataset();
        
        // LineChart Water & Calories Macros
        lineChart_Bigger_Macros_MPS.clear_LineChart_Dataset();
    }
    
    //##################################################
    //  Refresh
    //##################################################
    public void refresh_Data()
    {
        // LineChart Main Macros
        lineChart_Main_Macros_MPS.refresh_Data();
        
        // LineChart Water & Calories Macros
        lineChart_Bigger_Macros_MPS.refresh_Data();
    }
    
    //##################################################
    //  Delete
    //##################################################
    /*
       Based on MealManagers time the data is deleted in the series collection
    */
    public void delete_MealManager_Data(Second mealTime)
    {
        // LineChart Main Macros
        lineChart_Main_Macros_MPS.delete_MealManager_Data(mealTime);
        
        // LineChart Water & Calories Macros
        lineChart_Bigger_Macros_MPS.delete_MealManager_Data(mealTime);
    }
}
