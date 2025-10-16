package App_Code.Objects.Screens.Graph_Screens.LineChart_Meal_Plan_Screen;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.MealManager;
import App_Code.Objects.Database_Objects.MealManagerRegistry;
import App_Code.Objects.Graph_Objects.Line_Chart;
import App_Code.Objects.Gui_Objects.Screens.Screen_JFrame;
import App_Code.Objects.Gui_Objects.Screens.Screen_JPanel;
import App_Code.Objects.Screens.Meal_Plan_Screen;
import org.javatuples.Pair;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.awt.*;
import java.math.BigDecimal;
import java.util.*;

class LineChart_Macros_MPS extends Screen_JPanel
{
    //#################################################################################################################
    // Variables
    //#################################################################################################################
    private String planName;
    
    //##############################################
    // Objects
    //##############################################
    private Meal_Plan_Screen meal_plan_screen;
    private MealManagerRegistry mealManagerRegistry;
    
    private MyJDBC db;
    
    //##############################################
    // Collections
    //##############################################
    private LinkedHashMap<String, Integer> macronutrientsToCheckAndPos;
    private ArrayList<String> skip_Macros = new ArrayList<>(Arrays.asList("total_water","total_calories"));
    
    //##############################################
    // Datasets Objects
    //##############################################
    private TimeSeriesCollection dataset = new TimeSeriesCollection(); // Clear Dataset;
    private Line_Chart line_chart;
    
    // #################################################################################################################
    // Constructor
    // #################################################################################################################
    public LineChart_Macros_MPS(MyJDBC db, Meal_Plan_Screen meal_plan_screen, int frameWidth, int frameHeight)
    {
        // ##########################################
        // Super Constructors & Variables
        // ##########################################
        super(null, true, frameWidth, frameHeight);
        getScrollPaneJPanel().setBackground(Color.WHITE);
        
        // ##########################################
        // Variables
        // ##########################################
        this.db = db;
        this.meal_plan_screen = meal_plan_screen;
        this.mealManagerRegistry = meal_plan_screen.get_MealManagerRegistry();
        this.planName = meal_plan_screen.getPlanName();
        this.macronutrientsToCheckAndPos = meal_plan_screen.getTotalMeal_MacroColNamePos();
        
        //############################################
        // Creating Macros / Dataset
        //############################################
        createDataSet();
        
        // ##########################################
        // Create Graph Object & Adding to GUI
        // ##########################################
        line_chart = new Line_Chart(String.format("%s : Macros Over 24 Hours", planName), frameWidth - 100, frameHeight - 60, dataset);
        addToContainer(getScrollPaneJPanel(), line_chart, 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);
    }
    
    // ##################################################
    // Build Methods
    // ##################################################
    public void createDataSet()
    {
        dataset = mealManagerRegistry.create_Plan_MacroValues_LineChart();
    }
    
    /**
     * * @param mealManager - MealManager responsible for the changes made
     *
     * @param previousTime - old meal time
     * @param currentTime  - current meal time
     *                     <p>
     *                     For Loop through each macroNutrient Name in HashMap (Outer)
     *                     (Inner HashMap) Contains all the values for all the MealManagers
     *                     Update the values specific to this mealManager
     */
    public void update_MealManager_ChartData(MealManager mealManager, Second previousTime, Second currentTime)
    {
        // ####################################################
        // Get MealManager Info
        // ####################################################
        Boolean timeChanged = ! previousTime.equals(currentTime);
        
        int mealInPlanID = mealManager.getMealInPlanID();
        
        // ####################################################
        // Get MealManager MacroInfo & Replace
        // ####################################################
        Iterator<Map.Entry<String, HashMap<MealManager, BigDecimal>>> it =
                mealManagerRegistry.get_MealManagers_MacroValues().entrySet().iterator();
        
        /**
         *  <Key: MacroName | Value: HashMap<Key: MealManagerID, Value: < MealManager, Quantity>>
         *   Put, Replace
         */
        
        while (it.hasNext())
        {
            // ############################################
            // Create TimeSeries For Macros
            // ############################################
            /**
             *   Map<Key: MealManagerID, Value: < MealManager, Quantity>>
             */
            Map.Entry<String, HashMap<MealManager, BigDecimal>> macroEntry = it.next();
            String macroName = macroEntry.getKey();
            
            // Exit Clause
            if (skip_Macros.contains(macroName)) { continue; }
            
            // ########################################
            // Get Correlated Macro TimeSeries
            // ########################################
            // Convert Table Column to Key in TimeSeries Collection
            String macroNameGUI = convertMacroNameToGuiVersion(macroName);
            
            // Get TimeSeries correlated to MacroName in collection
            TimeSeries macroSeries = dataset.getSeries(macroNameGUI);
            
            // ########################################
            // Get Macronutrient Info
            // ########################################
            // Get Macro Info Specific to this mealManager correlating to macroName
            BigDecimal newMacroValue = macroEntry.getValue().get(mealManager);
            
            // #######################################
            // Same Time: Update MacroValue
            // #######################################
            if (! timeChanged) // IF the time hasn't changed just update the series value with the old time
            {
                macroSeries.addOrUpdate(previousTime, newMacroValue); // update value
                continue;
            }
            
            // ########################################
            // New Time : Delete Time & Add New Value
            // ########################################
            macroSeries.delete(previousTime); // IF the time has changed delete the old time from series value
            macroSeries.add(currentTime, newMacroValue); // Add new value to Series with new Time
        }
    }
    
    // #################################################################################################################
    // Methods
    // #################################################################################################################
    
    
    //##################################################
    // Conversion Methods
    //##################################################
    private String convertMacroNameToGuiVersion(String macroName)
    {
        // Reformat macroName for GUI purposes  "\u00A0" is like space because \t doesn't work in this label format
        return String.format("\u00A0\u00A0%s\u00A0\u00A0", formatStrings(macroName, true));
    }
    
    //##################################################
    //  Update  Methods
    //##################################################
    public void update_LineChart_Title()
    {
        planName = meal_plan_screen.getPlanName();
        line_chart.setTitle(planName);
    }
    
    //##################################################
    //  Add
    //##################################################
    public void add_New_MealManager_Data(MealManager mealManager)
    {
        
        // Add MealTime with value of 0 for each Macro because it's essentially initialised null
        Second mealTime = mealManager.getCurrentMealTime();
        
        for (Object objectSeries : dataset.getSeries())
        {
            TimeSeries series = (TimeSeries) objectSeries;
            
            series.add(mealTime, 0);
        }
    }
    
    //##################################################
    //  Clear
    //##################################################
    public void clear_LineChart_Dataset()
    {
        //####################################
        // Create new Dataset & Re-assign
        //####################################
        dataset = new TimeSeriesCollection();
        line_chart.update_dataset(dataset);
    }
    
    //##################################################
    //  Refresh
    //##################################################
    public void refresh_Data()
    {
        //####################################
        // Create new Dataset & Re-assign
        //####################################
        createDataSet();
        line_chart.update_dataset(dataset);
    }
    
    //##################################################
    //  Delete
    //##################################################
    /*
       Based on MealManagers time the data is deleted in the series collection
     */
    public void delete_MealManager_Data(Second mealTime)
    {
        for (String macroName : macronutrientsToCheckAndPos.keySet())
        {
            // ###########################################
            // Exit Clause
            // ###########################################
            if (skip_Macros.contains(macroName)) { continue; }
            
            // ###########################################
            // Get Correlated Macro TimeSeries
            // ############################################
            // Convert Table Column to Key in TimeSeries Collection
            String macroNameGUI = convertMacroNameToGuiVersion(macroName);
            
            // Get TimeSeries correlated to MacroName in collection
            TimeSeries macroSeries = dataset.getSeries(macroNameGUI);
            
            // #############################################
            // Delete MealManagers Correlated Macro Value
            // #############################################
            macroSeries.delete(mealTime);
        }
    }
}

