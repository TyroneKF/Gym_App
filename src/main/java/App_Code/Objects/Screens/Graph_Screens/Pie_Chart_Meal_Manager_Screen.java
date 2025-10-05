package App_Code.Objects.Screens.Graph_Screens;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.TotalMealTable;
import App_Code.Objects.Database_Objects.MealManager;
import App_Code.Objects.Database_Objects.MealManagerRegistry;
import App_Code.Objects.Gui_Objects.Screen;
import App_Code.Objects.Graph_Objects.Pie_Chart;
import App_Code.Objects.Screens.Meal_Plan_Screen;
import org.javatuples.Pair;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Map;

public class Pie_Chart_Meal_Manager_Screen extends Screen
{
    //###########################################################################################
    // Variables
    //###########################################################################################
    
    // Collections
    private Map<String, Pair<BigDecimal, String>> macros;
    
    //#####################################
    // Objects
    //#####################################
    private MealManager mealManager;
    private Meal_Plan_Screen meal_plan_screen;
    private Pie_Chart pieChart;
    private MealManagerRegistry mealManagerRegistry;
    
    //#####################################
    // Strings
    //#####################################
    private String meal_name;
    
    //#####################################
    // Integers
    //#####################################
    private int
            frameWidth = 800,
            frameHeight = 600;
    
    private int mealInPlanID;
    
    //###########################################################################################
    // Constructor
    //###########################################################################################
    public Pie_Chart_Meal_Manager_Screen(MyJDBC db, MealManager mealManager)
    {
        // ##########################################
        // Super Constructors & Variables
        // ##########################################
        super(db, false, "Pie Chart : Macronutrients ", 800, 600, 0, 0);
        
        getScrollPaneJPanel().setBackground(Color.WHITE);
        setResizable(false);
        
        // ##########################################
        // Variables
        // ##########################################
        this.mealManager = mealManager;
        this.mealManagerRegistry = mealManager.getMealManagerRegistry();
        this.meal_plan_screen = mealManager.getMeal_plan_screen();
    
        this.mealInPlanID = mealManager.getMealInPlanID();
        this.meal_name = mealManager.getCurrentMealName();
        
        //############################################
        // Creating Macros / Dataset
        //############################################
        updateDataSet();
        
        System.out.printf("Pie_Chart_Meal_Manager_Screen.java \n DataSet: \n%n%s", macros);
        // ##########################################
        // Create Graph Object & Adding to GUI
        // ##########################################
        pieChart = new Pie_Chart(String.format("%s Macros", meal_name), frameWidth - 50, frameHeight - 60, macros);
        addToContainer(getScrollPaneJPanel(), pieChart, 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
        
        setFrameVisibility(true);
    }
    
    //###########################################################################################
    // Methods
    //###########################################################################################
    @Override
    public void windowClosedEvent()
    {
        mealManager.removePieChartScreen();
    }
    
    //####################################
    // Update Methods
    //####################################
    public void update_pieChart()
    {
        updateDataSet(); pieChart.update_dataset(macros);
    }
    
    private void updateDataSet()
    {
        macros = mealManagerRegistry.get_MM_MacroInfo_PieChart(mealInPlanID);
    }
    
    public void update_PieChart_Title()
    {
        meal_name = mealManager.getCurrentMealName();
        pieChart.setTitle(meal_name);
    }
}
