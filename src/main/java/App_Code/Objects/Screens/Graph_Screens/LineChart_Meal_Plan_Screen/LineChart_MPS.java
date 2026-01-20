package App_Code.Objects.Screens.Graph_Screens.LineChart_Meal_Plan_Screen;

import App_Code.Objects.Database_Objects.MyJDBC.MyJDBC_Sqlite;
import App_Code.Objects.Table_Objects.Tables.Children.View_Data_Tables.Children.Total_Meal_Table.Total_Meal_Macro_Columns;
import App_Code.Objects.Table_Objects.MealManager;
import App_Code.Objects.Database_Objects.Shared_Data_Registry;
import App_Code.Objects.Gui_Objects.Screens.Screen_JFrame;
import App_Code.Objects.Screens.Meal_Plan_Screen;
import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

public class LineChart_MPS extends Screen_JFrame
{
    //#################################################################################################################
    // Variables
    // #################################################################################################################
    
    // Objects
    private final Meal_Plan_Screen meal_plan_screen;
    private Shared_Data_Registry shared_Data_Registry;
    
    // Screens
    private final LineChart_Macros_MPS lineChart_Main_Macros_MPS;
    private final LineChart_Macros_MPS lineChart_Bigger_Macros_MPS;
    
    // #################################################################################################################
    // Constructor
    // #################################################################################################################
    public LineChart_MPS(MyJDBC_Sqlite db, Shared_Data_Registry shared_Data_Registry, Meal_Plan_Screen meal_plan_screen)
    {
        // ################################################################
        // Super Constructor
        // ################################################################
        super(db, false, String.format(" %s Line Chart: Plan Macros", shared_Data_Registry.get_Plan_Name()), 1900, 1200, 0, 0);
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
        // Creating Macros LineChart Screen
        //#################################################
        ArrayList<Total_Meal_Macro_Columns> macros_To_Check = new ArrayList<>(Arrays.asList(
               Total_Meal_Macro_Columns.TOTAL_PROTEIN,
                Total_Meal_Macro_Columns.TOTAL_CARBOHYDRATES,
                Total_Meal_Macro_Columns.TOTAL_SUGARS_OF_CARBS,
                Total_Meal_Macro_Columns.TOTAL_FATS,
                Total_Meal_Macro_Columns.TOTAL_SATURATED_FAT,
                Total_Meal_Macro_Columns.TOTAL_SALT,
                Total_Meal_Macro_Columns.TOTAL_FIBRE
        ));
        
        lineChart_Main_Macros_MPS = new LineChart_Macros_MPS(
                shared_Data_Registry,
                "Macros Over 24 Hours",
                macros_To_Check,
                1000,
                900
        );
        
        tp.add("Macros Per Meal ", lineChart_Main_Macros_MPS);
        
        //#################################################
        // Creating Macros Screen
        //#################################################
        /**
         *  Create and override methods that display data as we're just interested in calories and water in this chart
         */
        
        ArrayList<Total_Meal_Macro_Columns> macros_To_Check2 = new ArrayList<>(Arrays.asList(
                Total_Meal_Macro_Columns.TOTAL_WATER,
                Total_Meal_Macro_Columns.TOTAL_CALORIES
        ));
        
        lineChart_Bigger_Macros_MPS = new LineChart_Macros_MPS(
                shared_Data_Registry,
                "Calories & Water Over 24 Hours",
                macros_To_Check2,
                1000,
                900
        );
        
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
        meal_plan_screen.remove_Line_Chart_Screen();
        closeJFrame();
    }
    
    // #################################################################################################################
    // Methods
    // #################################################################################################################
    public void update_MealManager_ChartData(MealManager mealManager, LocalTime previousTime, LocalTime currentTime)
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
    public void delete_MealManager_Data(LocalTime mealTime)
    {
        // LineChart Main Macros
        lineChart_Main_Macros_MPS.delete_MealManager_Data(mealTime);
        
        // LineChart Water & Calories Macros
        lineChart_Bigger_Macros_MPS.delete_MealManager_Data(mealTime);
    }
}
