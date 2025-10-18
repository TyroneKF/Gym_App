package App_Code.Objects.Screens.Graph_Screens;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.MealManager;
import App_Code.Objects.Database_Objects.MealManagerRegistry;
import App_Code.Objects.Gui_Objects.Screens.Screen_JFrame;
import App_Code.Objects.Graph_Objects.Pie_Chart;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.*;
import java.util.Random;

public class Pie_Chart_Meal_Manager_Screen extends Screen_JFrame
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
    
    //##############################################
    // Colors
    //##############################################
    private Random randomIntGenerator = new Random();
    private Color[][] colors = {
            {
                    new Color(0xFF33CC), // contrast - magenta
                    new Color(0x3399FF), // light blue
                    new Color(0x0033CC), // dark blue
                    new Color(0x33FF66), // light green
                    new Color(0x00CC33)  // dark green
            },
            {
                    new Color(0x99FF33), // contrast - lime
                    new Color(0xFF3333), // bright red
                    new Color(0xCC0000), // deep red
                    new Color(0xCC66FF), // lavender
                    new Color(0x9933FF)  // violet
            },
            {
                    new Color(0x00FFFF), // contrast - cyan
                    new Color(0xFF9933), // bright orange
                    new Color(0xFF6600), // deep orange
                    new Color(0xFF66CC), // bright pink
                    new Color(0xFF3399)  // hot pink
            },
            {
                    new Color(0xFFFF33), // contrast - yellow
                    new Color(0x3399FF), // sky blue
                    new Color(0x0033CC), // navy blue
                    new Color(0xCC66FF), // lavender
                    new Color(0x9933FF)  // deep purple
            },
            {
                    new Color(0xFF3333), // contrast - red
                    new Color(0x33FF57), // bright lime
                    new Color(0x00CC44), // forest green
                    new Color(0x3399FF), // bright blue
                    new Color(0x0033CC)  // royal blue
            },
            {
                    new Color(0xFF9900), // contrast - orange
                    new Color(0x00CCCC), // teal
                    new Color(0x009999), // dark teal
                    new Color(0xCC66FF), // light purple
                    new Color(0x9933FF)  // violet
            },
            {
                    new Color(0x3366FF), // contrast - vivid blue
                    new Color(0xFF3333), // bright red
                    new Color(0xCC0000), // crimson
                    new Color(0xFFFF66), // pale yellow
                    new Color(0xFFCC00)  // deep yellow
            },
            {
                    new Color(0x9933FF), // contrast - purple
                    new Color(0x3399FF), // bright blue
                    new Color(0x0033CC), // dark blue
                    new Color(0x66FF66), // bright green
                    new Color(0x33CC33)  // medium green
            },
            {
                    new Color(0xFF66CC), // contrast - pink
                    new Color(0x3399FF), // sky blue
                    new Color(0x0033CC), // royal blue
                    new Color(0xFF9933), // bright orange
                    new Color(0xFF6600)  // deep orange
            },
            {
                    new Color(0x33FF66), // contrast - bright green
                    new Color(0xFF3333), // bright red
                    new Color(0xCC0000), // deep red
                    new Color(0xCC66FF), // lavender
                    new Color(0x9933FF)  // violet
            },
            {
                    new Color(0x00CCCC), // contrast - teal
                    new Color(0xFF9933), // bright orange
                    new Color(0xFF6600), // deep orange
                    new Color(0x3399FF), // light blue
                    new Color(0x0033CC)  // dark blue
            },
            {
                    new Color(0xFFCC33), // contrast - gold
                    new Color(0x33FF57), // neon green
                    new Color(0x00CC44), // dark green
                    new Color(0xFF66CC), // bright pink
                    new Color(0xFF3399)  // hot pink
            }
    };
    
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
        set_Resizable(false);
        
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
        DefaultPieDataset<String> dataset = mealManagerRegistry.get_OR_Create_PieChart_Dataset(mealManager);
        
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
        
        // ################################################################
        // Generate Color Palette
        // ################################################################
        // Generate a random integer between 0 (inclusive) and 100 (exclusive)
        int randomInRange = randomIntGenerator.nextInt(colors.length);
        Color[] colorPalette = colors[randomInRange];
        
        //#####################################
        // Create PieChart
        //#####################################
        pieChart = new Pie_Chart(title, colorPalette, pieWidth, pieHeight, 150, titleFont, labelFont, legendFont, dataset);
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
    public void windowClosedEvent() { mealManager.removePieChartScreen(); closeJFrame(); }
    
    //####################################
    // Update Methods
    //####################################
    public void update_PieChart_Title()
    {
        String title = String.format("[%s]      %s Macros", mealManager.getCurrentMealTimeGUI(), mealManager.getCurrentMealName());
        pieChart.setTitle(title);
    }
}
