package App_Code.Objects.Screens.Graph_Screens.PieChart_Meal_Plan_Screen.Macro_Values;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.MealManager;
import App_Code.Objects.Database_Objects.MealManagerRegistry;
import App_Code.Objects.Graph_Objects.Pie_Chart;
import App_Code.Objects.Gui_Objects.Screens.Screen_JFrame;
import App_Code.Objects.Gui_Objects.Screens.Screen_JPanel;
import App_Code.Objects.Screens.Graph_Screens.PieChart_Meal_Plan_Screen.Total_Meals.PieChart_Entry_MPS;
import App_Code.Objects.Screens.Meal_Plan_Screen;
import org.javatuples.Pair;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Second;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.*;

public class PieChart_Macros_MPS extends Screen_JPanel
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private Meal_Plan_Screen meal_plan_screen;
    private MealManagerRegistry mealManagerRegistry;
    
    // Graph Preferences
    
    private int
            col = 2,
            pieWidth = (frameWidth / col) - 30,
            pieHeight = 730,
            rotateDelay = 200; //580
    private Font
            titleFont = new Font("Serif", Font.PLAIN, 27),
            labelFont = new Font("SansSerif", Font.BOLD, 22),
            legendFont = new Font("Serif", Font.PLAIN, 21);
    
    private String planName;
    
    //##############################################
    // Collections
    //##############################################
    /**
     * HashMap<String, Map<Integer, Pair<LocalTime, BigDecimal>>> mealManagersMacroValues = new HashMap<>();
     * Stores all the mealManagers TotalMealValues in collections by the macroName
     * <Key: MacroName | Value: Map <Key: MealManagerID, Value: < MealTime, Quantity>>
     * Etc;  <Key: Salt | Value: <MealManagerID: 1, <MealTime: 14:00 , Quantity: 300g >>
     */
    private LinkedHashMap<String, HashMap<MealManager, BigDecimal>> mealManagers_TotalMeal_MacroValues;
    
    private HashMap<String, DefaultPieDataset<String>> macro_Dataset = new HashMap<>();
    
    //##################################################################################################################
    // Constructors
    //##################################################################################################################
    public PieChart_Macros_MPS(Meal_Plan_Screen meal_plan_screen, int frameWidth, int frameHeight)
    {
        super(null, true, frameWidth, frameHeight);
    
        // ################################################################
        // Variables
        // ################################################################
        this.meal_plan_screen = meal_plan_screen;
        this.mealManagerRegistry = meal_plan_screen.get_MealManagerRegistry();
    
        planName = meal_plan_screen.getPlanName();
        
        // #####################################
        /// Collections
        // ######################################
         mealManagers_TotalMeal_MacroValues = mealManagerRegistry.get_MealManagers_MacroValues();
    
        // #####################################
        /// Create GUI
        // ######################################
        create_And_Draw_GUI();
    }
    
    // #################################################################################################################
    //  Update / Draw GUI Methods
    // #################################################################################################################
    private int get_PieChart_Count()
    {
        return (int) mealManagers_TotalMeal_MacroValues.size();
    }
    
    private void create_And_Draw_GUI()
    {
        // ################################################################
        // Clean & Build / Clear
        // ################################################################
        int rows = (int) Math.ceil((double) get_PieChart_Count() / col);
        
        getScrollPaneJPanel().removeAll();
        getScrollPaneJPanel().setLayout(new GridLayout(rows, col));
    
        macro_Dataset.clear(); // Clear Storage
        
        // ################################################################
        // Build DATA
        // ################################################################
        Iterator<String> it = mealManagers_TotalMeal_MacroValues.keySet().iterator();
        while (it.hasNext())
        {
            //##############################
            // Macro Info
            //##############################
            String macroName = it.next();
            
            //##############################
            // Create PieChart & Add to List
            //##############################
            String title = String.format(" %s  Across Meals", formatStrings(macroName, true));
            DefaultPieDataset<String> pieDataset = mealManagerRegistry.create_Macro_PieChart_Dataset(macroName);
            
            Pie_Chart pieChart = new Pie_Chart(title, pieWidth, pieHeight, rotateDelay, titleFont, labelFont, legendFont, pieDataset);
            
            //##############################
            // Add PieChart to GUI
            //##############################
            JPanel x = new JPanel(new GridBagLayout());
            getScrollPaneJPanel().add(x);
            
            addToContainer(x, pieChart, 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "both", 10, 10, null);
            
            addToContainer(x, createSpaceDivider(20, 50), 0, getAndIncreaseContainerYPos(), 1, 1,
                    0.25, 0.25, "both", 0, 0, null);
    
            //##############################
            // Add to DATA
            //##############################
            macro_Dataset.put(macroName, pieDataset);
        }
    
        //##############################
        // Re-paint GUI
        //##############################
        resizeGUI();
    }
    
    public void redraw_GUI()
    {
        /*// ####################################################
        // Clear GUI
        // ####################################################
        getScrollPaneJPanel().removeAll();
        
        int rows = (int) Math.ceil((double) pieChart_MPS_Entries.size() / col);
        getScrollPaneJPanel().setLayout(new GridLayout(rows, col));
        
        // ####################################################
        // Paint GUI
        // ####################################################
        Iterator<PieChart_Entry_MPS> it = pieChart_MPS_Entries.iterator();
        while (it.hasNext())
        {
            //##############################
            // GET Pie_Entry Object
            //##############################
            Pie_Chart pieChart = it.next().get_PieChart();
            
            //##############################
            // Add PieChart to GUI
            //##############################
            JPanel x = new JPanel(new GridBagLayout());
            addToContainer(x, pieChart, 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "both", 10, 10, null);
            
            addToContainer(x, createSpaceDivider(20, 50), 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);
            getScrollPaneJPanel().add(x);
        }
        
        resizeGUI();*/
    }
}
