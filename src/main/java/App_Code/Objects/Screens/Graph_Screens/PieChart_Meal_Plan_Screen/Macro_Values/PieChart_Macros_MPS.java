package App_Code.Objects.Screens.Graph_Screens.PieChart_Meal_Plan_Screen.Macro_Values;

import App_Code.Objects.Database_Objects.MealManager;
import App_Code.Objects.Database_Objects.MealManagerRegistry;
import App_Code.Objects.Graph_Objects.Pie_Chart;
import App_Code.Objects.Gui_Objects.Screens.Screen_JPanel;
import App_Code.Objects.Screens.Meal_Plan_Screen;
import org.jfree.data.general.DefaultPieDataset;

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
    
    private HashMap<String, DefaultPieDataset<String>> pieChart_Dataset = new HashMap<>();
    
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
    private int get_RowsCount()
    {
        return (int) Math.ceil((double) mealManagers_TotalMeal_MacroValues.size() / col);
    }
    
    private void create_And_Draw_GUI()
    {
        // ################################################################
        // Clean & Build / Clear
        // ################################################################
        int rows = get_RowsCount();
        
        getScrollPaneJPanel().removeAll();
        getScrollPaneJPanel().setLayout(new GridLayout(rows, col));
    
        pieChart_Dataset.clear(); // Clear Storage
        
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
            
            pieChart_Dataset.put(macroName, pieDataset); // Put Data into meory
            
            Pie_Chart pieChart = new Pie_Chart(title, pieWidth, pieHeight, rotateDelay, titleFont, labelFont, legendFont,
                    pieChart_Dataset.get(macroName));
            
            //##############################
            // Add PieChart to GUI
            //##############################
            JPanel x = new JPanel(new GridBagLayout());
            getScrollPaneJPanel().add(x);
            
            addToContainer(x, pieChart, 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "both", 10, 10, null);
            
            addToContainer(x, createSpaceDivider(20, 50), 0, getAndIncreaseContainerYPos(), 1, 1,
                    0.25, 0.25, "both", 0, 0, null);
        }
    
        //##############################
        // Re-paint GUI
        //##############################
        resizeGUI();
    }
    
    public void update_DATA()
    {
        // ############################################################################
        // Paint GUI
        // ############################################################################
        Iterator<Map.Entry<String, DefaultPieDataset<String>>> it = pieChart_Dataset.entrySet().iterator();
        while (it.hasNext())
        {
            // #########################################
            // Get Info
            // #########################################
            Map.Entry<String, DefaultPieDataset<String>> pieEntry = it.next();
            
            String macroName = pieEntry.getKey();
            DefaultPieDataset<String> pieDataset = pieEntry.getValue();
            
            // #########################################
            // Get Updated Data
            // #########################################
            DefaultPieDataset<String>  updated_Dataset = mealManagerRegistry.create_Macro_PieChart_Dataset(macroName);
            
            // #########################################
            // Transfer Data Over into PieChart Dataset
            // #########################################
            pieDataset.clear(); // Clear
            updated_Dataset.getKeys().forEach(key -> {
                pieDataset.setValue(key, updated_Dataset.getValue(key));
            });
        }
    
        // ############################################################################
        // Resize GUI
        // ############################################################################
        resizeGUI();
    }
}
