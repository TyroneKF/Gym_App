package com.donty.gymapp.ui.screens.graphs.LineChart_Meal_Plan_Screen;

import com.donty.gymapp.persistence.Shared_Data_Registry;
import com.donty.gymapp.domain.enums.table_enums.totalmeal.Total_Meal_Macro_Columns;
import com.donty.gymapp.ui.components.meal.MealManager;
import com.donty.gymapp.ui.charts.Line_Chart;
import com.donty.gymapp.gui.base.Screen_JPanel;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

class LineChart_Macros_MPS extends Screen_JPanel
{
    //#################################################################################################################
    // Variables
    //#################################################################################################################
    private String title;
    
    //##############################################
    // Objects
    //##############################################
    private Shared_Data_Registry shared_Data_Registry;
    
    //##############################################
    // Collections
    //##############################################
    private ArrayList<Total_Meal_Macro_Columns> macros_To_Check;
    
    /**
     * HashMap<String, HashMap<MealManager, BigDecimal>> mealManagers_TotalMeal_MacroValues = new HashMap<>();
     * Stores all the mealManagers TotalMealValues in collections by the macroName
     * <p>
     * <Key: MacroName | Value: HashMap <Key: MealManager, Value:  Quantity>>
     * Etc;  <Key: Salt | Value: HashMap<MealManager, Quantity: 300g >>
     */
    private LinkedHashMap<Total_Meal_Macro_Columns, HashMap<MealManager, BigDecimal>> mealManagers_TotalMeal_MacroValues;
    
    //##############################################
    // Datasets Objects
    //##############################################
    private TimeSeriesCollection dataset = new TimeSeriesCollection(); // Clear Dataset;
    private Line_Chart line_chart;
    
    
    // #################################################################################################################
    // Constructor
    // #################################################################################################################
    public LineChart_Macros_MPS
    (
            Shared_Data_Registry shared_Data_Registry,
            String title,
            ArrayList<Total_Meal_Macro_Columns> macros_To_Check,
            int frameWidth,
            int frameHeight
    )
    {
        // ##########################################
        // Super Constructors & Variables
        // ##########################################
        super(null, true);
        get_ScrollPane_JPanel().setBackground(Color.WHITE);
        
        // ##########################################
        // Variables
        // ##########################################
        // Objects
        this.shared_Data_Registry = shared_Data_Registry;
        
        // String
        this.title = title;
        
        // Collections
        this.macros_To_Check = macros_To_Check;
        this.mealManagers_TotalMeal_MacroValues = shared_Data_Registry.get_Meal_Managers_Macro_Values();
        
        //############################################
        // Creating Macros / Dataset
        //############################################
        createDataSet();
        
        // ##########################################
        // Create Graph Object & Adding to GUI
        // ##########################################
        line_chart = new Line_Chart(get_Title_String(), frameWidth - 100, frameHeight - 60, dataset);
        add_To_Container(get_ScrollPane_JPanel(), line_chart, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);
    }
    
    // #################################################################################################################
    // Build Methods
    // #################################################################################################################
    private void createDataSet()
    {
        dataset = new TimeSeriesCollection();
        
        for (Total_Meal_Macro_Columns macro_name : macros_To_Check)
        {
            // Create a series for each macroName
            TimeSeries macroTimeSeries = new TimeSeries(convert_MacroName_To_GUI_Version(macro_name.key()));
            dataset.addSeries(macroTimeSeries);
            
            // Add all the values from this macroName into the series
            /**
             * HashMap<String, Map<Integer, Pair<LocalTime, BigDecimal>>> mealManagersMacroValues = new HashMap<>();
             * Stores all the mealManagers TotalMealValues in collections by the macroName
             * <Key: MacroName | Value: Map <Key: MealManagerID, Value: < MealTime, Quantity>>
             * Etc;  <Key: Salt | Value: <MealManagerID: 1, <MealTime: 14:00 , Quantity: 300g >>
             */
            
            HashMap<MealManager, BigDecimal> macroValues = mealManagers_TotalMeal_MacroValues.get(macro_name);

            // Iterate through the recorded MealManager Values for this macro
            for (Map.Entry<MealManager, BigDecimal> mealManagers_Info : macroValues.entrySet())
            {
                // Add time and Value for MealManager
                MealManager mealManager = mealManagers_Info.getKey();
                BigDecimal macroValue = mealManagers_Info.getValue();
                Second meal_time_in_seconds = local_Time_ToSecond(mealManager.get_Current_Meal_Time());

                macroTimeSeries.add(meal_time_in_seconds, macroValue);
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
    private Second local_Time_ToSecond(LocalTime localTime)
    {
        // Convert LocalTime -> Date (fixed base date)
        Date date = Date.from(localTime.atDate(LocalDate.of(1970, 1, 1))
                .atZone(ZoneId.systemDefault())
                .toInstant());
        
        // Wrap into JFree Second
        return new Second(date);
    }
    
    
    //##################################################
    //  Update  Methods
    //##################################################
    public String get_Title_String()
    {
        return String.format("%s : %s", shared_Data_Registry.get_Plan_Name(), title);
    }
    
    
    public void update_LineChart_Title()
    {
        line_chart.setTitle(get_Title_String());
    }
    
    /**
     * * @param mealManager - MealManager responsible for the changes made
     *
     * @param previous_time_input - old meal time
     * @param current_time  - current meal time
     *                     <p>
     *                     For Loop through each macroNutrient Name in HashMap (Outer)
     *                     (Inner HashMap) Contains all the values for all the MealManagers
     *                     Update the values specific to this mealManager
     */
    public void update_MealManager_ChartData(MealManager mealManager, LocalTime previous_time_input, LocalTime current_time)
    {
        // ####################################################
        // Get MealManager Info
        // ####################################################
        boolean time_changed = ! previous_time_input.equals(current_time);
        
        Second previous_time_in_seconds = local_Time_ToSecond(previous_time_input);
        Second current_time_in_seconds = local_Time_ToSecond(current_time);
        
        // ####################################################
        // Get MealManager MacroInfo & Replace
        // ####################################################
        
        /**
         *  <Key: MacroName | Value: HashMap<Key: MealManagerID, Value: < MealManager, Quantity>>
         */
        for (Total_Meal_Macro_Columns macro_name : macros_To_Check)
        {
            // ############################################
            // Macro Info
            // ############################################
            // ############################################
            // Create TimeSeries New Info For Macro
            // ############################################
            /**
             *  <Key: MacroName | Value: HashMap<Key: MealManagerID, Value: < MealManager, Quantity>>
             */
            
            // Get Macro Info Specific to this mealManager correlating to macroName
            BigDecimal newMacroValue = mealManagers_TotalMeal_MacroValues.get(macro_name).get(mealManager);
            
            // Convert Table Column to Key in TimeSeries Collection
            String macroNameGUI = convert_MacroName_To_GUI_Version(macro_name.key());
            
            // #######################################
            // Same Time: Update MacroValue
            // #######################################
            // Get TimeSeries correlated to MacroName in collection
            TimeSeries macroSeries = dataset.getSeries(macroNameGUI);
            
            if (! time_changed) // IF the time hasn't changed just update the series value with the old time
            {
                macroSeries.addOrUpdate(previous_time_in_seconds, newMacroValue); // update value
                continue;
            }
            
            // ########################################
            // New Time : Delete Time & Add New Value
            // ########################################
            macroSeries.delete(previous_time_in_seconds); // IF the time has changed delete the old time from series value
            macroSeries.add(current_time_in_seconds, newMacroValue); // Add new value to Series with new Time
        }
    }
    
    //##################################################
    //  Add
    //##################################################
    public void add_New_MealManager_Data(MealManager mealManager)
    {
        
        // Add MealTime with value of 0 for each Macro because it's essentially initialised null
        Second mealTime = local_Time_ToSecond(mealManager.get_Current_Meal_Time());
        
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
    public void delete_MealManager_Data(LocalTime meal_time)
    {
        //###############################################
        // Delete time point from all series
        //###############################################
        for (int i = 0; i < dataset.getSeriesCount(); i++)
        {
            TimeSeries series = dataset.getSeries(i);
            series.delete(local_Time_ToSecond(meal_time));
        }
    }
}

