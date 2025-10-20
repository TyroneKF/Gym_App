package App_Code.Objects.Screens.Graph_Screens.LineChart_Meal_Plan_Screen;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.MealManager;
import App_Code.Objects.Database_Objects.MealManagerRegistry;
import App_Code.Objects.Graph_Objects.Line_Chart;
import App_Code.Objects.Gui_Objects.Screens.Screen_JPanel;
import App_Code.Objects.Screens.Meal_Plan_Screen;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.awt.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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
    private ArrayList<String> macros_To_Check;
    
    /**
     * HashMap<String, HashMap<MealManager, BigDecimal>> mealManagers_TotalMeal_MacroValues = new HashMap<>();
     * Stores all the mealManagers TotalMealValues in collections by the macroName
     * <p>
     * <Key: MacroName | Value: HashMap <Key: MealManager, Value:  Quantity>>
     * Etc;  <Key: Salt | Value: HashMap<MealManager, Quantity: 300g >>
     */
    private LinkedHashMap<String, HashMap<MealManager, BigDecimal>> mealManagers_TotalMeal_MacroValues;
    
    //##############################################
    // Datasets Objects
    //##############################################
    private TimeSeriesCollection dataset = new TimeSeriesCollection(); // Clear Dataset;
    private Line_Chart line_chart;
    
    // #################################################################################################################
    // Constructor
    // #################################################################################################################
    public LineChart_Macros_MPS(MyJDBC db, Meal_Plan_Screen meal_plan_screen, String title, ArrayList<String> macros_To_Check,
                                int frameWidth, int frameHeight)
    {
        // ##########################################
        // Super Constructors & Variables
        // ##########################################
        super(null, true, frameWidth, frameHeight);
        getScrollPaneJPanel().setBackground(Color.WHITE);
        
        // ##########################################
        // Variables
        // ##########################################
        // Objects
        this.db = db;
        this.meal_plan_screen = meal_plan_screen;
        this.mealManagerRegistry = meal_plan_screen.get_MealManagerRegistry();
       
        // String
        this.planName = meal_plan_screen.getPlanName();
       
        // Collections
        this.macros_To_Check = macros_To_Check;
        this.mealManagers_TotalMeal_MacroValues = mealManagerRegistry.get_MealManagers_MacroValues();
        
        //############################################
        // Creating Macros / Dataset
        //############################################
        createDataSet();
        
        // ##########################################
        // Create Graph Object & Adding to GUI
        // ##########################################
        line_chart = new Line_Chart(title, frameWidth - 100, frameHeight - 60, dataset);
        addToContainer(getScrollPaneJPanel(), line_chart, 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);
    }
    
    // #################################################################################################################
    // Build Methods
    // #################################################################################################################
    private void createDataSet()
    {
        dataset = new TimeSeriesCollection();
        
        for (String macroName : macros_To_Check)
        {
            
            // Create a series for each macroName
            TimeSeries macroTimeSeries = new TimeSeries(convert_MacroName_To_GUI_Version(macroName));
            dataset.addSeries(macroTimeSeries);
            
            // Add all the values from this macroName into the series
            /**
             * HashMap<String, Map<Integer, Pair<LocalTime, BigDecimal>>> mealManagersMacroValues = new HashMap<>();
             * Stores all the mealManagers TotalMealValues in collections by the macroName
             * <Key: MacroName | Value: Map <Key: MealManagerID, Value: < MealTime, Quantity>>
             * Etc;  <Key: Salt | Value: <MealManagerID: 1, <MealTime: 14:00 , Quantity: 300g >>
             */
            
            HashMap<MealManager, BigDecimal> macroValues = mealManagers_TotalMeal_MacroValues.get(macroName);
            Iterator<Map.Entry<MealManager, BigDecimal>> it = macroValues.entrySet().iterator();
            
            while (it.hasNext()) // Iterate through the recorded MealManager Values for this macro
            {
                Map.Entry<MealManager, BigDecimal> mealManagers_Info = it.next();
                
                // Add time and Value for MealManager
                MealManager mealManager = mealManagers_Info.getKey();
                BigDecimal macroValue = mealManagers_Info.getValue();
                
                macroTimeSeries.add(mealManager.getCurrentMealTime(), macroValue);
            }
        }
    }
    
    private String convert_MacroName_To_GUI_Version(String macroName)
    {
        // Capitalize and split word
        macroName = Arrays.stream(macroName.split("[ _]+"))
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "));
        
        
        // Reformat macroName for GUI purposes  "\u00A0" is like space because \t doesn't work in this label format
        return String.format("\u00A0\u00A0%s\u00A0\u00A0", macroName);
    }

    // #################################################################################################################
    // Methods
    // #################################################################################################################
    
    //##################################################
    //  Update  Methods
    //##################################################
    public void update_LineChart_Title()
    {
        planName = meal_plan_screen.getPlanName();
        line_chart.setTitle(planName);
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
        
        // ####################################################
        // Get MealManager MacroInfo & Replace
        // ####################################################
        Iterator<String> it = macros_To_Check.iterator();
        
        while (it.hasNext())
        {
            // ############################################
            // Macro Info
            // ############################################
            String macroName = it.next();
            
            // ############################################
            // Create TimeSeries New Info For Macro
            // ############################################
            /**
             *  <Key: MacroName | Value: HashMap<Key: MealManagerID, Value: < MealManager, Quantity>>
             */
            
            // Get Macro Info Specific to this mealManager correlating to macroName
            BigDecimal newMacroValue = mealManagers_TotalMeal_MacroValues.get(macroName).get(mealManager);
            
            // Convert Table Column to Key in TimeSeries Collection
            String macroNameGUI = convert_MacroName_To_GUI_Version(macroName);
            
            // #######################################
            // Same Time: Update MacroValue
            // #######################################
            // Get TimeSeries correlated to MacroName in collection
            TimeSeries macroSeries = dataset.getSeries(macroNameGUI);
            
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
        //###############################################
        // Delete time point from all series
        //###############################################
        for (int i = 0; i < dataset.getSeriesCount(); i++)
        {
            TimeSeries series = dataset.getSeries(i);
            series.delete(mealTime);
        }
    }
}

