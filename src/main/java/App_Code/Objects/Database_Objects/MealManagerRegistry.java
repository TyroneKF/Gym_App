package App_Code.Objects.Database_Objects;

import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.TotalMealTable;
import App_Code.Objects.Screens.Graph_Screens.PieChart_Meal_Plan_Screen.PieChart_Entry_MPS;
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
    private Map<String, Integer> totalMeal_macroColNamePos;
    
    /**
     * HashMap<String, Map<Integer, Pair<LocalTime, BigDecimal>>> mealManagersMacroValues = new HashMap<>();
     * Stores all the mealManagers TotalMealValues in collections by the macroName
     * <Key: MacroName | Value: Map <Key: MealManagerID, Value: < MealTime, Quantity>>
     * Etc;  <Key: Salt | Value: <MealManagerID: 1, <MealTime: 14:00 , Quantity: 300g >>
     */
    private HashMap<String, HashMap<Integer, Pair<Second, BigDecimal>>> mealManagers_TotalMeal_MacroValues = new HashMap<>(); // Can be refactored to include mealManager
    
    private ArrayList<MealManager> mealManager_ArrayList = new ArrayList<>();
    
    //#############################
    // Chart Data Collections
    //#############################
    private HashMap<Integer, DefaultPieDataset<String>> pieChart_Dataset_HashMap = new HashMap<>();
    
    private ArrayList<PieChart_Entry_MPS> pieChartEntry_MPS_AL = new ArrayList<>();
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public MealManagerRegistry(Meal_Plan_Screen meal_plan_screen, Map<String, Integer> totalMeal_macroColNamePos)
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
    
    //###############################################################################
    // Sort Methods
    ///###############################################################################
    private void sortLists()
    {
        sort_MealManager_AL();
        if (meal_plan_screen.is_PieChart_Screen_Open()) { sort_PieChartEntry_AL(); }
    }
    
    private void sort_MealManager_AL()
    {
        mealManager_ArrayList.sort((a, b) -> a.getCurrentMealTime().compareTo(b.getCurrentMealTime()));
    }
    
    public void sort_PieChartEntry_AL()
    {
        pieChartEntry_MPS_AL.sort((a, b) -> a.get_MealTime().compareTo(b.get_MealTime()));
    }
    
    //###############################################################################
    // ADD Methods
    ///###############################################################################
    public void addMealManager(MealManager mealManager)
    {
        //##########################################
        // mealManager Info
        //##########################################
        int mealManagerID = mealManager.getMealInPlanID();
        Second mealManagerTime = mealManager.getCurrentMealTime();
        TotalMealTable totalMealTable = mealManager.getTotalMealTable();
        
        System.out.printf("\n\nMealManagerRegistry.java : addMealManager() \nMealInPlanID :  %s", mealManagerID);
        
        //##########################################
        // Add MealManager Results to Collection
        //##########################################
        Iterator<Map.Entry<String, Integer>> it = totalMeal_macroColNamePos.entrySet().iterator();
        
        while (it.hasNext())
        {
            Map.Entry<String, Integer> mapEntry = it.next();
            
            // Relative to TotalMealTable
            String macroName = mapEntry.getKey();
            Integer macroPos = mapEntry.getValue();
            
            BigDecimal macroValue = totalMealTable.get_ValueOnTable(0, macroPos);
            
            /**
             * HashMap<String, Map<Integer, Pair<LocalTime, BigDecimal>>> mealManagersMacroValues = new HashMap<>();
             * Stores all the mealManagers TotalMealValues in collections by the macroName
             * <Key: MacroName | Value: Map <Key: MealManagerID, Value: < MealTime, Quantity>>
             * Etc;  <Key: Salt | Value: <MealManagerID: 1, <MealTime: 14:00 , Quantity: 300g >>
             */
            mealManagers_TotalMeal_MacroValues.get(macroName) // Map Returned
                    .put(mealManagerID, new Pair<>(mealManagerTime, macroValue));
        }
        
        //##########################################
        // Add MealManager to Collection & Sort
        //##########################################
        mealManager_ArrayList.add(mealManager);
        sortLists();
    }
    
    //###############################################################################
    // Replace Methods
    //###############################################################################
    public void replaceMealManagerDATA(MealManager mealManager, Boolean skipSorting) // Update done by replacing data
    {
        //##########################################
        // mealManager Info
        //##########################################
        int mealManagerID = mealManager.getMealInPlanID();
        Second mealManagerTime = mealManager.getCurrentMealTime();
        TotalMealTable totalMealTable = mealManager.getTotalMealTable();
        
        System.out.printf("\n\nMealManagerRegistry.java : replaceMealManagerDATA() \nMealInPlanID :  %s", mealManagerID);
        
        //##########################################
        // Remove / ADD MealManager to Collection
        //##########################################
        if (! skipSorting) { sort_MealManager_AL(); }
        
        //##########################################
        // Remove MealManager Results to Collection
        //##########################################
        /**
         * HashMap<String, Map<Integer, Pair<LocalTime, BigDecimal>>> mealManagersMacroValues = new HashMap<>();
         * Stores all the mealManagers TotalMealValues in collections by the macroName
         * <Key: MacroName | Value: Map <Key: MealManagerID, Value: < MealTime, Quantity>>
         * Etc;  <Key: Salt | Value: <MealManagerID: 1, <MealTime: 14:00 , Quantity: 300g >>
         */
        
        Iterator<Map.Entry<String, Integer>> it = totalMeal_macroColNamePos.entrySet().iterator();
        
        while (it.hasNext())
        {
            Map.Entry<String, Integer> mapEntry = it.next();
            String macroName = mapEntry.getKey();
            Integer macroPos = mapEntry.getValue();
            
            BigDecimal macroValue = totalMealTable.get_ValueOnTable(0, macroPos);
            
            /**
             *  <Key: MacroName | Value: Map<Key: MealManagerID, Value: < MealTime, Quantity>>
             *   Put, Replace
             */
            
            mealManagers_TotalMeal_MacroValues.get(macroName).put(mealManagerID, new Pair<>(mealManagerTime, macroValue));
        }
    }
    
    //###############################################################################
    // DELETE Methods
    //###############################################################################
    public void delete_MealManager(MealManager mealManager)
    {
        //###########################################
        // Remove MealManager TotalMeal Macro Values
        //###########################################
        int mealManagerID = mealManager.getMealInPlanID();
        remove_MealManagers_MacroValues(mealManagerID);
    }
    
    private void remove_MealManagers_MacroValues(Integer mealManagerID)
    {
        //##########################################
        // Remove MealManager Results to Collection
        //##########################################
        /**
         * HashMap<String, Map<Integer, Pair<LocalTime, BigDecimal>>> mealManagersMacroValues = new HashMap<>();
         * Stores all the mealManagers TotalMealValues in collections by the macroName
         * <Key: MacroName | Value: Map <Key: MealManagerID, Value: < MealTime, Quantity>>
         * Etc; <Key: Salt | Value: <MealManagerID: 1, <MealTime: 14:00 , Quantity: 300g >>
         */
        
        Iterator<Map.Entry<String, Integer>> it = totalMeal_macroColNamePos.entrySet().iterator();
        
        while (it.hasNext())
        {
            Map.Entry<String, Integer> mapEntry = it.next();
            
            // Relative to TotalMealTable
            String macroName = mapEntry.getKey();
            
            // <Key: MacroName | Value: ArrayList<Key: MealManagerID, Value: < MealTime, Quantity>>
            mealManagers_TotalMeal_MacroValues.get(macroName).remove(mealManagerID);
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
            it.next().delete_MealManager(false, false, false); // Hide Meal Managers
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
                mealManager.completely_Delete_MealManager();  // mealManager is not in the DB erase it from the GUI
                
                // ###########################################
                // Remove Data from Collections
                // ###########################################
                // Remove this mealManagers MacrosValues
                for (String macroName : totalMeal_macroColNamePos.keySet())
                {
                    mealManagers_TotalMeal_MacroValues.get(macroName).remove(mealManagerID);
                }
                
                pieChart_Dataset_HashMap.remove(mealManagerID);
                pieChartEntry_MPS_AL.remove(mealManager);
                
                // ###########################################
                // Remove & Continue
                // ###########################################
                it.remove();
                continue;
            }
            
            //#################################################################
            // Reload MealManager Data
            //#################################################################
            mealManager.reloadTableAndChartsData(false, false, true);
        }
    
        //#################################################################
        // Refresh MealRegistry DATA
        //#################################################################
        
        //#################################################################
        // Sort MealManager Order
        //#################################################################
        sort_MealManager_AL();
    }
    
    ///#################################################################################################################
    // Pie Chart
    ///#################################################################################################################
    /**
     * @param mealInPlanID - ID of MealManager
     * @return PieChart Dataset
     *
     * This method is used to retrieve pieChart Data based on MealInPlanID
     * if it exists it is returned. Otherwise, it's created and added to DATA (Collection) and then it's returned.
     */
    public DefaultPieDataset<String> get_OR_Create_PieChart_Dataset(Integer mealInPlanID)
    {
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
        pieChart_Dataset_HashMap.put(mealInPlanID, get_Updated_PieChart_Dataset(mealInPlanID));
        
        //##############################################
        // Add to HashMapDataset
        //##############################################
        return pieChart_Dataset_HashMap.get(mealInPlanID);
    }
    
    private int percent_Calculator(BigDecimal value, BigDecimal overall)
    {
        BigDecimal ratio = value.divide(overall, 4, RoundingMode.DOWN); // 4 decimal places, rounded
        BigDecimal percent = ratio.multiply(BigDecimal.valueOf(100));      // Convert to %
        return percent.setScale(0, RoundingMode.HALF_DOWN).intValueExact();
    }
    
    ///########################################
    // Update Methods
    ///########################################
    public Boolean update_PieChart_Values(Integer mealInPlanID)
    {
        //#########################################
        // IF PieChart Not Open Exit
        //#########################################
        if (! pieChart_Dataset_HashMap.containsKey(mealInPlanID)) { return false; }
        
        //#########################################
        // Updated Saved Dataset With New Values
        //#########################################
        DefaultPieDataset<String> newGenerated = get_Updated_PieChart_Dataset(mealInPlanID);
        DefaultPieDataset<String> mm_PieData = pieChart_Dataset_HashMap.get(mealInPlanID);
        
        // Clear Data First
        mm_PieData.clear();
        
        // Transfer Data Over into this dataset
        newGenerated.getKeys().forEach(key -> {
            mm_PieData.setValue(key, newGenerated.getValue(key));
        });
        
        //#########################################
        // Updated Saved Dataset With New Values
        //#########################################
        return true;
    }
    
    /**
     *
     */
    private DefaultPieDataset<String> get_Updated_PieChart_Dataset(Integer mealInPlanID)
    {
        /**
         *  mealManagersMacroValues : <Key: Salt | Value: <MealManagerID: 1, <MealTime: 14:00 , Quantity: 300g >>
         */
        LinkedHashMap<String, BigDecimal> data = new LinkedHashMap<>()
        {{
            // ###########################
            // Overall Components
            // ###########################
            BigDecimal proteinValue = mealManagers_TotalMeal_MacroValues.get("total_protein").get(mealInPlanID).getValue1();
            BigDecimal carbsValue = mealManagers_TotalMeal_MacroValues.get("total_carbohydrates").get(mealInPlanID).getValue1();
            BigDecimal fatsValue = mealManagers_TotalMeal_MacroValues.get("total_fats").get(mealInPlanID).getValue1();
            
            BigDecimal total = proteinValue.add(carbsValue).add(fatsValue);
            // ###########################
            // Protein
            // ###########################
            put(String.format("Protein [ %d%% ] ", percent_Calculator(proteinValue, total)), proteinValue);
            
            // ###########################
            // Carbs
            // ###########################
            BigDecimal sugarCarbsValue = mealManagers_TotalMeal_MacroValues.get("total_sugars_of_carbs").get(mealInPlanID).getValue1();
            
            put(String.format("Carbohydrates [ %d%% ] ", percent_Calculator(carbsValue, total)), carbsValue.subtract(sugarCarbsValue));
            put("Sugars Of Carbs", sugarCarbsValue);
            
            // ###########################
            // Fats
            // ###########################
            BigDecimal satFatsValue = mealManagers_TotalMeal_MacroValues.get("total_saturated_fat").get(mealInPlanID).getValue1();
            
            put(String.format("Fats [ %d%% ] ", percent_Calculator(fatsValue, total)), fatsValue.subtract(satFatsValue));
            put("Saturated Fats", satFatsValue);
        }};
        
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
    
    ///########################################
    // Remove Methods
    ///########################################
    public void remove_PieChart_DatasetValues(Integer mealInPlanID)
    {
        System.out.printf("\n\nRemoved PieChart Data %s", mealInPlanID);
        pieChart_Dataset_HashMap.remove(mealInPlanID);
    }
    
    //###############################################################################
    // PieChart [MealPlanScreen]
    //###############################################################################
    
    /**
     * This is requested by the Meal_Plan_Screen when the pieChart screen is closed!
     * Remove the pieChart data which doesn't have a MealManager pie chart actively using it.
     */
    public void remove_Unused_PieData()
    {
        Iterator<PieChart_Entry_MPS> it = pieChartEntry_MPS_AL.iterator();
        while (it.hasNext())
        {
            PieChart_Entry_MPS pieChart_entry_mps = it.next();
            MealManager mealManager = pieChart_entry_mps.get_MealManager();
            
            int mealManagerID = mealManager.getMealInPlanID();
            
            // IF MealManager Associated with Entry isn't open, remove its dataset
            if (! mealManager.is_PieChartOpen())
            {
                if (pieChart_Dataset_HashMap.containsKey(mealManagerID))
                {
                    pieChart_Dataset_HashMap.remove(mealManagerID);
                }
            }
            
            // Remove this item from the MPS PieChart Screen
            it.remove();
        }
    }
    
    //##################################################################################################################
    // LineChart Methods [Meal_Plan_Screen]
    //##################################################################################################################
    public TimeSeriesCollection create_Plan_MacroValues_LineChart()
    {
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        
        for (String macroName : totalMeal_macroColNamePos.keySet())
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
            
            Map<Integer, Pair<Second, BigDecimal>> macroValues = mealManagers_TotalMeal_MacroValues.get(macroName);
            Iterator<Map.Entry<Integer, Pair<Second, BigDecimal>>> it = macroValues.entrySet().iterator();
            
            while (it.hasNext()) // Iterate through the recorded MealManager Values for this macro
            {
                Map.Entry<Integer, Pair<Second, BigDecimal>> mealManagers_Info = it.next();
                Pair<Second, BigDecimal> mealManagerValues = mealManagers_Info.getValue();
                
                // Add time and Value for MealManager
                macroTimeSeries.add(mealManagerValues.getValue0(), mealManagerValues.getValue1());
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
        return txt =
                separateWords ?
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
    
    public Map<String, HashMap<Integer, Pair<Second, BigDecimal>>> get_MealManagers_MacroValues()
    {
        return mealManagers_TotalMeal_MacroValues;
    }
    
    public ArrayList<PieChart_Entry_MPS> get_PieChart_MPS_Entries()
    {
        return pieChartEntry_MPS_AL;
    }
}
