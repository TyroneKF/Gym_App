package App_Code.Objects.Screens.Graph_Screens.LineChart_Meal_Plan_Screen;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.MealManager;
import App_Code.Objects.Database_Objects.MealManagerRegistry;
import App_Code.Objects.Gui_Objects.Screens.Screen_JFrame;
import App_Code.Objects.Screens.Meal_Plan_Screen;
import org.jfree.data.time.Second;
import javax.swing.*;
import java.awt.*;

public class LineChart_MPS extends Screen_JFrame
{
    //#################################################################################################################
    // Variables
    // #################################################################################################################
    
    // Objects
    private Meal_Plan_Screen meal_plan_screen;
    private MealManagerRegistry mealManagerRegistry;
    
    // Screens
    LineChart_Macros_MPS lineChart_Macros_MPS;
    
    
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
        this.mealManagerRegistry = meal_plan_screen.get_MealManagerRegistry();
        
        //###################################################################################
        // Create ContentPane
        //###################################################################################
        getScrollPaneJPanel().setLayout(new GridLayout(1, 1));
        
        JTabbedPane tp = new JTabbedPane();
        getScrollPaneJPanel().add(tp);
        
        //#################################################
        // Creating Macros LineChart Screen
        //#################################################
        lineChart_Macros_MPS = new LineChart_Macros_MPS(db, meal_plan_screen, 1000, 900);
        tp.add("Macros Per Meal ", lineChart_Macros_MPS);
        
        //#################################################
        // Creating Macros Screen
        //#################################################
        /*
        pieChart_Macros_MPS = new PieChart_Macros_MPS(meal_plan_screen, frameWidth, frameHeight);
        tp.add("Macros Meal Plan ", pieChart_Macros_MPS);
        */
        
        // ################################################################
        // Make Frame Visible
        // ################################################################
        setFrameVisibility(true);
        resizeGUI();
    }
    
    @Override
    public void windowClosedEvent()
    {
        meal_plan_screen.removeLineChartScreen();
        closeJFrame();
    }
    
    // #################################################################################################################
    // Methods
    // #################################################################################################################
    public void update_MealManager_ChartData(MealManager mealManager, Second previousTime, Second currentTime)
    {
        lineChart_Macros_MPS.update_MealManager_ChartData(mealManager, previousTime, currentTime);
    }
    
    //##################################################
    //  Update  Methods
    //##################################################
    public void update_LineChart_Title()
    {
        lineChart_Macros_MPS.update_LineChart_Title();
    }
    
    //##################################################
    //  Add
    //##################################################
    public void add_New_MealManager_Data(MealManager mealManager)
    {
        lineChart_Macros_MPS.add_New_MealManager_Data(mealManager);
    }
    
    //##################################################
    //  Clear
    //##################################################
    public void clear_LineChart_Dataset()
    {
        lineChart_Macros_MPS.clear_LineChart_Dataset();
    }
    
    //##################################################
    //  Refresh
    //##################################################
    public void refresh_Data()
    {
        lineChart_Macros_MPS.refresh_Data();
    }
    
    //##################################################
    //  Delete
    //##################################################
    /*
       Based on MealManagers time the data is deleted in the series collection
    */
    public void delete_MealManager_Data(Second mealTime)
    {
        lineChart_Macros_MPS.delete_MealManager_Data(mealTime);
    }
}
