package App_Code.Objects.Screens.Graph_Screens;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.MealManager;
import App_Code.Objects.Database_Objects.MealManagerRegistry;
import App_Code.Objects.Gui_Objects.Screen;
import App_Code.Objects.Graph_Objects.Pie_Chart;
import App_Code.Objects.Screens.Meal_Plan_Screen;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.*;

public class Pie_Chart_Meal_Manager_Screen extends Screen
{
    //###########################################################################################
    // Variables
    //###########################################################################################
    
    
    //#####################################
    // Objects
    //#####################################
    private MealManager mealManager;
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
            frameHeight = 600,
            mealInPlanID;
    
    //###########################################################################################
    // Constructor
    //###########################################################################################
    public Pie_Chart_Meal_Manager_Screen(MyJDBC db, MealManager mealManager)
    {
        // ##########################################
        // Super Constructors & Variables
        // ##########################################
        super(db, false, "Pie Chart : Macronutrients ", 850, 700, 0, 0);
        
        getScrollPaneJPanel().setBackground(Color.WHITE);
        setResizable(false);
        
        // ##########################################
        // Variables
        // ##########################################
        this.mealManager = mealManager;
        this.mealManagerRegistry = mealManager.getMealManagerRegistry();
        
        this.mealInPlanID = mealManager.getMealInPlanID();
        this.meal_name = mealManager.getCurrentMealName();
        
        //############################################
        // Creating Macros / Dataset
        //############################################
        DefaultPieDataset<String> dataset = mealManagerRegistry.get_OR_Create_PieChart_Dataset(mealInPlanID);
        
        //#####################################
        // Graph Preferences
        //#####################################
        String title = String.format("[%s]      %s Macros", mealManager.getCurrentMealTimeGUI(), mealManager.getCurrentMealName());
        int
                pieWidth = frameWidth - 50,
                pieHeight = frameHeight - 20;
        
        Font
                titleFont = new Font("Serif", Font.PLAIN, 27),
                labelFont = new Font("SansSerif", Font.BOLD, 22),
                legendFont = new Font("Serif", Font.PLAIN, 23);
        
        //#####################################
        // Create PieChart
        //#####################################
        pieChart = new Pie_Chart(title, pieWidth, pieHeight, 150, titleFont, labelFont, legendFont, dataset);
        addToContainer(getScrollPaneJPanel(), pieChart, 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
        
        //#####################################
        // Frame
        //#####################################
        setFrameVisibility(true);
    }
    
    //###########################################################################################
    // Methods
    //###########################################################################################
    @Override
    public void windowClosedEvent()
    {
        //############################################
        // Remove Itself / External DATA if not USED
        //############################################
        mealManager.removePieChartScreen();
        mealManagerRegistry.remove_PieChart_DatasetValues(mealInPlanID);
    
        //############################################
        // Exit
        //############################################
        closeJFrame();
    }
    
    //####################################
    // Update Methods
    //####################################
    public void update_PieChart_Title()
    {
        String title = String.format("[%s]      %s Macros", mealManager.getCurrentMealTimeGUI(), mealManager.getCurrentMealName());
        pieChart.setTitle(title);
    }
}
