package App_Code.Objects.Database_Objects;

import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.TotalMealTable;
import App_Code.Objects.Screens.Graph_Screens.PieChart_Meal_Plan_Screen.Total_Meals.PieChart_Entry_MPS;
import App_Code.Objects.Screens.Meal_Plan_Screen;
import org.javatuples.Pair;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class MealManagerRegistry
{
    private Meal_Plan_Screen meal_plan_screen;
    
    //##################################################################################################################
    // Collections
    //##################################################################################################################
    private LinkedHashMap<String, Integer> totalMeal_macroColNamePos;
    
    /**
     * HashMap<String, HashMap<MealManager, BigDecimal>> mealManagers_TotalMeal_MacroValues = new HashMap<>();
     * Stores all the mealManagers TotalMealValues in collections by the macroName
     * <p>
     * <Key: MacroName | Value: HashMap <Key: MealManager, Value:  Quantity>>
     * Etc;  <Key: Salt | Value: HashMap<MealManager, Quantity: 300g >>
     */
    private LinkedHashMap<String, HashMap<MealManager, BigDecimal>> mealManagers_TotalMeal_MacroValues = new LinkedHashMap<>(); // Can be refactored to include mealManager
    
    private ArrayList<MealManager> mealManager_ArrayList = new ArrayList<>();
    
    //#############################
    // Chart Data Collections
    //#############################
    private HashMap<Integer, DefaultPieDataset<String>> pieChart_Dataset_HashMap = new HashMap<>();
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public MealManagerRegistry(Meal_Plan_Screen meal_plan_screen, LinkedHashMap<String, Integer> totalMeal_macroColNamePos)
    {
        //##################################
        // Variables
        //##################################
        this.totalMeal_macroColNamePos = totalMeal_macroColNamePos;
        this.meal_plan_screen = meal_plan_screen;
        
        //##################################
        // Create Macros Collection
        //##################################
        initialize_MealManagers_MacrosValues();
    }
    
    //##################################################################################################################
    //  Methods
    //##################################################################################################################
    public void initialize_MealManagers_MacrosValues()
    {
        mealManagers_TotalMeal_MacroValues.clear();
        
        //##################################
        // Create Macros Collection
        //##################################
        for (String macroName : totalMeal_macroColNamePos.keySet())
        {
            mealManagers_TotalMeal_MacroValues.put(macroName, new HashMap<>());
        }
    }
    
    //##################################################################################################################
    // Sort Methods
    //##################################################################################################################
    public void sort_MealManager_AL()
    {
        mealManager_ArrayList.sort((a, b) -> a.getCurrentMealTime().compareTo(b.getCurrentMealTime()));
    }
    
    //###############################################################################
    // ADD Methods
    ///###############################################################################
    public void addMealManager(MealManager mealManager) // ADD Pie DATA
    {
        //##########################################
        // Add MealManager to Collection & Sort
        //##########################################
        mealManager_ArrayList.add(mealManager);
        
        //###############################################
        // Add MealManager Macro Values
        //###############################################
        add_OR_Replace_MealManager_Macros_DATA(mealManager);
        
        //###############################################
        // Sort LISTS
        //###############################################
        sort_MealManager_AL();
    }
    
    public void add_OR_Replace_MealManager_Macros_DATA(MealManager mealManager) // Update done by replacing data
    {
        //##########################################
        // mealManager Info
        //##########################################
        TotalMealTable totalMealTable = mealManager.getTotalMealTable();
        
        //##########################################
        // Remove MealManager Results to Collection
        //##########################################
        /**
         * HashMap<String, HashMap<MealManager, BigDecimal>> mealManagers_TotalMeal_MacroValues = new HashMap<>();
         * Stores all the mealManagers TotalMealValues in collections by the macroName
         *
         * <Key:  MacroName | Value: HashMap <Key: MealManager, Value:  Quantity>>
         * Etc;  <Key: Salt | Value: HashMap<MealManager, Quantity: 300g >>
         */
        
        Iterator<Map.Entry<String, Integer>> it = totalMeal_macroColNamePos.entrySet().iterator();
        
        while (it.hasNext())
        {
            Map.Entry<String, Integer> mapEntry = it.next();
            String macroName = mapEntry.getKey();
            Integer macroPos = mapEntry.getValue();
            
            BigDecimal macroValue = totalMealTable.get_ValueOnTable(0, macroPos);
            
            /**
             *  <Key: MacroName | Value: HashMap <Key: MealManager, Value:  Quantity>>
             *   Put, Replace have the same effect
             */
            
            mealManagers_TotalMeal_MacroValues.get(macroName).put(mealManager, macroValue);
        }
    }
    
    //###############################################################################
    // DELETE Methods
    //###############################################################################
    public void delete_MealManager(MealManager mealManager)
    {
        //###########################################
        // Remove MealManager MacroValues
        //###########################################
        remove_MealManager_MacroValues(mealManager);
        
        //###########################################
        // Remove MealManager PieChart Data
        //###########################################
        int mealManagerID = mealManager.getMealInPlanID();
        remove_PieChart_DatasetValues(mealManagerID);
    }
    
    private void remove_MealManager_MacroValues(MealManager mealManager)
    {
        /**
         * HashMap<String, HashMap<MealManager, BigDecimal>> mealManagers_TotalMeal_MacroValues = new HashMap<>();
         * Stores all the mealManagers TotalMealValues in collections by the macroName
         *
         * <Key: MacroName | Value: HashMap <Key: MealManager, Value:  Quantity>>
         * Etc;  <Key: Salt | Value: HashMap<MealManager, Quantity: 300g >>
         */
        
        Iterator<String> it = totalMeal_macroColNamePos.keySet().iterator();
        
        while (it.hasNext())
        {
            String macroName = it.next();
            
            // <Key: MacroName | Value: HashMap <Key: MealManager, Value:  Quantity>>
            mealManagers_TotalMeal_MacroValues.get(macroName).remove(mealManager);
        }
    }
    
    //################################################
    // DELETE MealManager (In Group) Meal_Plan_Screen
    //################################################
    public void delete_MealManagers_MPS()
    {
        //###########################################################
        // Delete MealManagers
        //###########################################################
        Iterator<MealManager> it = mealManager_ArrayList.iterator();
        while (it.hasNext())
        {
            it.next().delete_MealManager(); // Hide Meal Managers
        }
        
        //###########################################################
        // Clear / Recreate EMPTY Collection
        //###########################################################
        initialize_MealManagers_MacrosValues();
    }
    
    //###############################################################################
    // Refresh Methods
    //###############################################################################
    public void refresh_MealManagers_MPS()
    {
        Iterator<MealManager> it = mealManager_ArrayList.iterator();
        while (it.hasNext())
        {
            //#################################################################
            // MealManager Info
            //#################################################################
            MealManager mealManager = it.next();
            int mealManagerID = mealManager.getMealInPlanID();
            
            //#################################################################
            // Remove MealManager (Not Saved)
            //#################################################################
            if (! mealManager.isMealManagerInDB()) // IF MealManager isn't saved Remove it
            {
                if( ! mealManager.is_Meal_Deleted()) // if the meal hasn't beene deleted, delete its data & GUI
                {
                    mealManager.delete_MealManager();
                }
                
                it.remove(); continue;
            }
            
            //#################################################################
            // Reload MealManager Data
            //#################################################################
            mealManager.reloadTableAndChartsData(false, false);
            
            //#################################################################
            // Re-Upload Or, Change Meal MacroData
            //#################################################################
            add_OR_Replace_MealManager_Macros_DATA(mealManager);
        }
        
        //#################################################################
        // Sort MealManager Order
        //#################################################################
        sort_MealManager_AL();
    }
    
    ///#################################################################################################################
    // Pie Chart [TotalMeal] :
    ///#################################################################################################################
    
    /**
     * @return PieChart Dataset
     * <p>
     * This method is used to retrieve pieChart Data based on MealInPlanID
     * if it exists it is returned. Otherwise, it's created and added to DATA (Collection) and then it's returned.
     */
    public DefaultPieDataset<String> get_OR_Create_PieChart_Dataset(MealManager mealManager)
    {
        //#########################################
        // MealManager Info
        //#########################################
        int mealInPlanID = mealManager.getMealInPlanID();
        
        //##############################################
        // Add to HashMapDataset
        //##############################################
        if (pieChart_Dataset_HashMap.containsKey(mealInPlanID))
        {
            System.err.printf("\ncreate_MM_MacroInfo_PieChart() Already Created");
            return pieChart_Dataset_HashMap.get(mealInPlanID);
        }
        
        //##############################################
        // Add to HashMapDataset
        //##############################################
        pieChart_Dataset_HashMap.put(mealInPlanID, get_Updated_PieChart_Dataset(mealManager));
        
        //##############################################
        // Add to HashMapDataset
        //##############################################
        return pieChart_Dataset_HashMap.get(mealInPlanID);
    }
    
    private int percent_Calculator(BigDecimal value, BigDecimal overall)
    {
        //#######################################
        // Exit Clause
        //#######################################
        if (overall.compareTo(BigDecimal.ZERO) == 0) { return 0; }
        
        //#######################################
        // Create % in int
        //#######################################
        BigDecimal ratio = value.divide(overall, 4, RoundingMode.DOWN); // 4 decimal places, rounded
        BigDecimal percent = ratio.multiply(BigDecimal.valueOf(100));      // Convert to %
        return percent.setScale(0, RoundingMode.HALF_DOWN).intValueExact();
    }
    
    private DefaultPieDataset<String> get_Updated_PieChart_Dataset(MealManager mealManager)
    {
        //###################################################
        // Get Macros
        //###################################################
        LinkedHashMap<String, BigDecimal> data;
        
        BigDecimal proteinValue = mealManagers_TotalMeal_MacroValues.get("total_protein").get(mealManager);
        BigDecimal carbsValue = mealManagers_TotalMeal_MacroValues.get("total_carbohydrates").get(mealManager);
        BigDecimal fatsValue = mealManagers_TotalMeal_MacroValues.get("total_fats").get(mealManager);
        
        BigDecimal total = proteinValue.add(carbsValue).add(fatsValue);
        
        //###################################################
        // Produce Output
        //###################################################
        if (total.compareTo(BigDecimal.ZERO) > 0) // IF totalCalories > 0, it's not a new meal  / empty
        {
            /**
             *  mealManagersMacroValues : <Key: Salt | Value: <MealManager: mealManager, Quantity: 300g >>
             */
            data = new LinkedHashMap<String, BigDecimal>()
            {{
                // ###########################
                // Protein
                // ###########################
                put(String.format("Protein [ %d%% ] ", percent_Calculator(proteinValue, total)), proteinValue);
                
                // ###########################
                // Carbs
                // ###########################
                BigDecimal sugarCarbsValue = mealManagers_TotalMeal_MacroValues.get("total_sugars_of_carbs").get(mealManager);
                
                put(String.format("Carbohydrates [ %d%% ] ", percent_Calculator(carbsValue, total)), carbsValue.subtract(sugarCarbsValue));
                put("Sugars Of Carbs", sugarCarbsValue);
                
                // ###########################
                // Fats
                // ###########################
                BigDecimal satFatsValue = mealManagers_TotalMeal_MacroValues.get("total_saturated_fat").get(mealManager);
                
                put(String.format("Fats [ %d%% ] ", percent_Calculator(fatsValue, total)), fatsValue.subtract(satFatsValue));
                put("Saturated Fats", satFatsValue);
            }};
        }
        else
        {
            data = new LinkedHashMap<String, BigDecimal>()
            {{
                // ###########################
                // Protein
                // ###########################
                put(String.format("Protein [ %d%% ] ", 0), new BigDecimal(0));
                put(String.format("Carbohydrates [ %d%% ] ", 0), new BigDecimal(0));
                put("Sugars Of Carbs", new BigDecimal(0));
                put(String.format("Fats [ %d%%] ", 0), new BigDecimal(0));
                put("Saturated Fats", new BigDecimal(0));
            }};
        }
        
        //#############################################
        // Add Data to Dataset to represent
        //#############################################
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        for (Map.Entry<String, BigDecimal> macroValues : data.entrySet())
        {
            dataset.setValue(String.format("  %s - %s g   ", macroValues.getKey(), macroValues.getValue()), macroValues.getValue());
        }
        
        //#############################################
        // Return Value
        //#############################################
        return dataset;
    }
    
    ///############################
    //  Update Methods
    ///############################
    public Boolean update_PieChart_Values(MealManager mealManager)
    {
        //#########################################
        //
        //#########################################
        int mealInPlanID = mealManager.getMealInPlanID();
        
        //#########################################
        // IF PieChart Not Open Exit
        //#########################################
        if (! pieChart_Dataset_HashMap.containsKey(mealInPlanID)) { return false; }
        
        //#########################################
        // Updated Saved Dataset With New Values
        //#########################################
        DefaultPieDataset<String> newGenerated = get_Updated_PieChart_Dataset(mealManager);
        DefaultPieDataset<String> mm_PieData = pieChart_Dataset_HashMap.get(mealInPlanID);
        
        // Clear Data First
        mm_PieData.clear();
    
        System.out.printf("\n\nChart Updated: %s", mealManager.getCurrentMealName());
        
        // Transfer Data Over into this dataset
        newGenerated.getKeys().forEach(key -> {
            mm_PieData.setValue(key, newGenerated.getValue(key));
            System.out.printf("\n%s - %s", key, newGenerated.getValue(key));
        });
        
        //#########################################
        // Updated Saved Dataset With New Values
        //#########################################
        return true;
    }
    
    // PieChart: Remove Methods
    public void remove_PieChart_DatasetValues(Integer mealInPlanID)
    {
        if (pieChart_Dataset_HashMap.containsKey(mealInPlanID)) { pieChart_Dataset_HashMap.remove(mealInPlanID); }
    }
    
    //##################################################################################################################
    // PieChart [ Plan Macros ]
    //##################################################################################################################
    public DefaultPieDataset<String> create_Macro_PieChart_Dataset(String macroName)
    {
        //##############################################
        // Collections
        //##############################################
        DefaultPieDataset<String> macroDataset = new DefaultPieDataset<>();
        Map<MealManager, BigDecimal> macroValues = mealManagers_TotalMeal_MacroValues.get(macroName);
        
        //##############################################
        // Get Total
        //##############################################
        BigDecimal total = new BigDecimal(0);
        for (BigDecimal x : macroValues.values())
        {
            total = total.add(x);
        }
        
        //##############################################
        //  Sort Data & Create Dataset
        //##############################################
        BigDecimal finalTotal = total;
        macroValues.entrySet().stream()
                .sorted(Comparator.comparingLong(e -> e.getKey().getCurrentMealTime().getFirstMillisecond()))
                .forEachOrdered(totalMeal_Values ->
                {
                    MealManager mealManager = totalMeal_Values.getKey();
                    BigDecimal macroValue = totalMeal_Values.getValue();
                    
                    String title = String.format(" [%s]  %s  (%d%%) -  %s g  ", mealManager.getCurrentMealTimeGUI(),
                            mealManager.getCurrentMealName(), percent_Calculator(macroValue, finalTotal), macroValue);
                    
                    macroDataset.setValue(title, macroValue);
                });
        
        //##############################################
        // Return Values
        //##############################################
        return macroDataset;
    }
    
    //##################################################################################################################
    // LineChart Methods [Meal_Plan_Screen]
    //##################################################################################################################
    
    // Pass in list of variables macros data needs to be checked on
    public TimeSeriesCollection create_Plan_MacroValues_LineChart(ArrayList<String> macroToCheck)
    {
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        
        for (String macroName : macroToCheck)
        {
            
            // Create a series for each macroName
            TimeSeries macroTimeSeries = new TimeSeries(convert_MacroName_To_GUI_Version(macroName));
            timeSeriesCollection.addSeries(macroTimeSeries);
            
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
        
        return timeSeriesCollection;
    }
    
    private String convert_MacroName_To_GUI_Version(String macroName)
    {
        // Reformat macroName for GUI purposes  "\u00A0" is like space because \t doesn't work in this label format
        
        return String.format("\u00A0\u00A0%s\u00A0\u00A0", format_Strings(macroName, true));
    }
    
    private String format_Strings(String txt, boolean separateWords)
    {
        // Re-assign Re-Capitalised Value into list
        return separateWords
                ?
                Arrays.stream(txt.split("[ _]+"))
                        .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                        .collect(Collectors.joining(" "))
                :
                Arrays.stream(txt.split("[ _]+"))
                        .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                        .collect(Collectors.joining("_"));
    }
    
    //##################################################################################################################
    // Accessor Methods
    //##################################################################################################################
    // Collections
    public ArrayList<MealManager> get_MealManager_ArrayList()
    {
        return mealManager_ArrayList;
    }
    
    public LinkedHashMap<String, HashMap<MealManager, BigDecimal>> get_MealManagers_MacroValues()
    {
        return mealManagers_TotalMeal_MacroValues;
    }
    
    public int get_Active_MealCount()
    {
        return (int) mealManager_ArrayList.stream().filter(mealManager -> ! mealManager.is_Meal_Deleted()).count();
    }
}
