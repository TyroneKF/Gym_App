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
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private Meal_Plan_Screen meal_plan_screen;
    
    //#####################################################################
    // Collections
    //#####################################################################
    private Map<String, Integer> totalMeal_macroColNamePos;
    
    //#############################
    // Chart Data Collections
    //#############################
    private HashMap<Integer, DefaultPieDataset<String>> pieDatasetHashMap = new HashMap<>();
    
    private TreeSet<Map.Entry<Integer, PieChart_Entry_MPS>> pieChart_MPS_Entries = new TreeSet<Map.Entry<Integer, PieChart_Entry_MPS>>(new Comparator<Map.Entry<Integer, PieChart_Entry_MPS>>()
    {
        @Override
        public int compare(Map.Entry<Integer, PieChart_Entry_MPS> o1, Map.Entry<Integer, PieChart_Entry_MPS> o2)
        {
            return o1.getValue().get_MealTime().compareTo(o2.getValue().get_MealTime());
        }
    });
    
    /**
     * HashMap<String, Map<Integer, Pair<LocalTime, BigDecimal>>> mealManagersMacroValues = new HashMap<>();
     * Stores all the mealManagers TotalMealValues in collections by the macroName
     * <Key: MacroName | Value: Map <Key: MealManagerID, Value: < MealTime, Quantity>>
     * Etc;  <Key: Salt | Value: <MealManagerID: 1, <MealTime: 14:00 , Quantity: 300g >>
     */
    private Map<String, HashMap<Integer, Pair<Second, BigDecimal>>> mealManagersMacroValues; // Can be refactored to include mealManager
    private ArrayList<MealManager> mealManager_ArrayList = new ArrayList<>();
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public MealManagerRegistry(Meal_Plan_Screen meal_plan_screen, Map<String, Integer> totalMeal_macroColNamePos)
    {
        //##################################
        // Variables
        //##################################
        this.meal_plan_screen = meal_plan_screen;
        this.totalMeal_macroColNamePos = totalMeal_macroColNamePos;
        
        //##################################
        // Create Macros Collection
        //##################################
        recreateEmpty_MealManagersMacrosValues();
    }
    
    //##################################################################################################################
    //  MealManager Methods in mealManagerTreeSet
    //##################################################################################################################
    public void recreateEmpty_MealManagersMacrosValues()
    {
        mealManagersMacroValues = new HashMap<>();
        
        //##################################
        // Create Macros Collection
        //##################################
        for (String macroName : totalMeal_macroColNamePos.keySet())
        {
            mealManagersMacroValues.put(macroName, new HashMap<>());
        }
    }
    
    private void sort_MealManager_AL()
    {
        mealManager_ArrayList.sort((a, b) -> a.getCurrentMealTime().compareTo(b.getCurrentMealTime()));
    }
    
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
        // Add MealManager to Collection & Sort
        //##########################################
        mealManager_ArrayList.add(mealManager);
        sort_MealManager_AL();
        
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
            mealManagersMacroValues.get(macroName) // Map Returned
                    .put(mealManagerID, new Pair<>(mealManagerTime, macroValue));
        }
    }
    
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
            
            mealManagersMacroValues.get(macroName).put(mealManagerID, new Pair<>(mealManagerTime, macroValue));
        }
    }
    
    //##################################
    // DELETE Methods
    //##################################
    public void completelyDeleteMealManager(MealManager mealManager)
    {
        //##########################################
        // GET MealManager Info
        //##########################################
        int mealManagerID = mealManager.getMealInPlanID();
        
        //##########################################
        // Remove MealManager to Collection
        //##########################################
        mealManager_ArrayList.remove(mealManager);
        
        //##########################################
        // Remove MealManager Results to Collection
        //##########################################
        removeMealManagersMacroValues(mealManagerID);
    }
    
    public void deleteMealManager(int mealManagerID)
    {
        //##########################################
        // Remove MealManager Results to Collection
        //##########################################
        removeMealManagersMacroValues(mealManagerID);
    }
    
    private void removeMealManagersMacroValues(Integer mealManagerID)
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
            mealManagersMacroValues.get(macroName).remove(mealManagerID);
        }
    }
    
    //##################################################################################################################
    //  Meal_Plan_Screen Methods
    //##################################################################################################################
    public void delete_MealManagers()
    {
        //##################################
        // Delete MealManagers
        //##################################
        Iterator<MealManager> it = mealManager_ArrayList.iterator();
        while (it.hasNext())
        {
            it.next().hideMealManager(); // Hide Meal Managers
        }
        
        //##################################
        // Clear / Recreate EMPTY Collection
        //##################################
        recreateEmpty_MealManagersMacrosValues();
    }
    
    public void refresh_MealManagers()
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
                    mealManagersMacroValues.get(macroName).remove(mealManagerID);
                }
                
                pieDatasetHashMap.remove(mealManagerID);
                pieChart_MPS_Entries.removeIf(entry -> entry.getKey().equals(mealManagerID));
                
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
     * <p>
     * This method is used to retrieve pieChart Data based on MealInPlanID
     * if it exists it is returned. Otherwise, it's created and added to DATA (Collection) and then it's returned
     */
    public DefaultPieDataset<String> create_MM_MacroInfo_PieChart(Integer mealInPlanID)
    {
        //##############################################
        // Add to HashMapDataset
        //##############################################
        if (pieDatasetHashMap.containsKey(mealInPlanID))
        {
            System.err.printf("\ncreate_MM_MacroInfo_PieChart() Already Created");
            return pieDatasetHashMap.get(mealInPlanID);
        }
        
        //##############################################
        // Add to HashMapDataset
        //##############################################
        pieDatasetHashMap.put(mealInPlanID, get_NewUpdatedPieData(mealInPlanID));
        
        //##############################################
        // Add to HashMapDataset
        //##############################################
        return pieDatasetHashMap.get(mealInPlanID);
    }
    
    private DefaultPieDataset<String> get_NewUpdatedPieData(Integer mealInPlanID)
    {
        /**
         *  mealManagersMacroValues : <Key: Salt | Value: <MealManagerID: 1, <MealTime: 14:00 , Quantity: 300g >>
         */
        LinkedHashMap<String, BigDecimal> data = new LinkedHashMap<>()
        {{
            // ###########################
            // Overall Components
            // ###########################
            BigDecimal proteinValue = mealManagersMacroValues.get("total_protein").get(mealInPlanID).getValue1();
            BigDecimal carbsValue = mealManagersMacroValues.get("total_carbohydrates").get(mealInPlanID).getValue1();
            BigDecimal fatsValue = mealManagersMacroValues.get("total_fats").get(mealInPlanID).getValue1();
            
            BigDecimal total = proteinValue.add(carbsValue).add(fatsValue);
            // ###########################
            // Protein
            // ###########################
            put(String.format("Protein [ %d%% ] ", percent_Calculator(proteinValue, total)), proteinValue);
            
            // ###########################
            // Carbs
            // ###########################
            BigDecimal sugarCarbsValue = mealManagersMacroValues.get("total_sugars_of_carbs").get(mealInPlanID).getValue1();
            
            put(String.format("Carbohydrates [ %d%% ] ", percent_Calculator(carbsValue, total)), carbsValue.subtract(sugarCarbsValue));
            put("Sugars Of Carbs", sugarCarbsValue);
            
            // ###########################
            // Fats
            // ###########################
            BigDecimal satFatsValue = mealManagersMacroValues.get("total_saturated_fat").get(mealInPlanID).getValue1();
            
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
    
    private int percent_Calculator(BigDecimal value, BigDecimal overall)
    {
        BigDecimal ratio = value.divide(overall, 4, RoundingMode.DOWN); // 4 decimal places, rounded
        BigDecimal percent = ratio.multiply(BigDecimal.valueOf(100));      // Convert to %
        return percent.setScale(0, RoundingMode.HALF_DOWN).intValueExact();
    }
    
    public Boolean updatePieChart_MM_Values(Integer mealInPlanID)
    {
        //#########################################
        // IF PieChart Not Open Exit
        //#########################################
        if (! pieDatasetHashMap.containsKey(mealInPlanID)) { return false; }
        
        //#########################################
        // Updated Saved Dataset With New Values
        //#########################################
        DefaultPieDataset<String> newGenerated = get_NewUpdatedPieData(mealInPlanID);
        DefaultPieDataset<String> mm_PieData = pieDatasetHashMap.get(mealInPlanID);
        
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
    
    public void removePieChartDatasetValue(Integer mealInPlanID)
    {
        System.out.printf("\n\nRemoved Data %s", mealInPlanID);
        pieDatasetHashMap.remove(mealInPlanID);
    }
    
    /**
     * This is requested by the Meal_Plan_Screen when the pieChart screen is closed!
     * Remove the pieChart data which doesn't have a MealManager pie chart actively using it.
     */
    public void remove_Unused_PieData()
    {
        Iterator<Integer> it = pieDatasetHashMap.keySet().iterator();
        while (it.hasNext())
        {
            Integer mmKey = it.next();
            if (! get_MealManager_In_MM_ArrayList(mmKey).is_PieChartOpen())
            {
                it.remove(); System.out.printf("\nPieDelete: %s", mmKey);
            }
        }
    }
    
    ///######################################################
    // MealPlanScreen
    ///######################################################
    
    
    //##################################################################################################################
    // LineChart Methods [Meal_Plan_Screen]
    //##################################################################################################################
    public TimeSeriesCollection get_Plan_MacroValues_LineChart()
    {
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        
        for (String macroName : totalMeal_macroColNamePos.keySet())
        {
            // Create a series for each macroName
            TimeSeries macroTimeSeries = new TimeSeries(convertMacroNameToGuiVersion(macroName));
            timeSeriesCollection.addSeries(macroTimeSeries);
            
            // Add all the values from this macroName into the series
            /**
             * HashMap<String, Map<Integer, Pair<LocalTime, BigDecimal>>> mealManagersMacroValues = new HashMap<>();
             * Stores all the mealManagers TotalMealValues in collections by the macroName
             * <Key: MacroName | Value: Map <Key: MealManagerID, Value: < MealTime, Quantity>>
             * Etc;  <Key: Salt | Value: <MealManagerID: 1, <MealTime: 14:00 , Quantity: 300g >>
             */
            
            Map<Integer, Pair<Second, BigDecimal>> macroValues = mealManagersMacroValues.get(macroName);
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
    
    private String convertMacroNameToGuiVersion(String macroName)
    {
        // Reformat macroName for GUI purposes  "\u00A0" is like space because \t doesn't work in this label format
        
        //  return String.format("\u00A0\u00A0%s\u00A0\u00A0", macroName, true);
        
        return String.format("\u00A0\u00A0%s\u00A0\u00A0", formatStrings(macroName, true));
    }
    
    private String formatStrings(String txt, boolean separateWords)
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
    public ArrayList<MealManager> get_MealManager_ArrayList()
    {
        return mealManager_ArrayList;
    }
    
    public Map<String, HashMap<Integer, Pair<Second, BigDecimal>>> get_MealManagersMacroValues()
    {
        return mealManagersMacroValues;
    }
    
    public TreeSet<Map.Entry<Integer, PieChart_Entry_MPS>> get_PieChart_MPS_Entries()
    {
        return pieChart_MPS_Entries;
    }
    
    public HashMap<Integer, DefaultPieDataset<String>> get_PieDatasetHashMap()
    {
        return pieDatasetHashMap;
    }
    
    //######################################
    // Objects
    //######################################
    public MealManager get_MealManager_In_MM_ArrayList(Integer mealInPlanID)
    {
        Iterator<MealManager> it = mealManager_ArrayList.iterator();
        while (it.hasNext())
        {
            MealManager mealManager = it.next();
            if (mealManager.equals(mealInPlanID)) { return mealManager; }
        }
        return null;
    }
    
    public PieChart_Entry_MPS get_PieChart_MPS(Integer mealInPlanID)
    {
        Iterator<Map.Entry<Integer, PieChart_Entry_MPS>> it = pieChart_MPS_Entries.iterator();
        while (it.hasNext())
        {
            Map.Entry<Integer, PieChart_Entry_MPS> entry = it.next();
            
            if (entry.getKey().equals(mealInPlanID))
            {
                return entry.getValue();
            }
        }
        return null;
    }
}
