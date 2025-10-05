package App_Code.Objects.Database_Objects;

import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.TotalMealTable;
import App_Code.Objects.Screens.Meal_Plan_Screen;
import org.javatuples.Pair;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class MealManagerRegistry
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private Meal_Plan_Screen meal_plan_screen;
    
    //##################################
    // Collections
    //##################################
    private Map<String, Integer> totalMeal_macroColNamePos;
    
    private TreeSet<Map.Entry<Integer, MealManager>> mealManagerTreeSet = new TreeSet<Map.Entry<Integer, MealManager>>(new Comparator<Map.Entry<Integer, MealManager>>()
    {
        @Override
        public int compare(Map.Entry<Integer, MealManager> o1, Map.Entry<Integer, MealManager> o2)
        {
            return o1.getValue().getCurrentMealTime().compareTo(o2.getValue().getCurrentMealTime());
        }
    });
    
    /**
     * HashMap<String, Map<Integer, Pair<LocalTime, BigDecimal>>> mealManagersMacroValues = new HashMap<>();
     * Stores all the mealManagers TotalMealValues in collections by the macroName
     * <Key: MacroName | Value: Map <Key: MealManagerID, Value: < MealTime, Quantity>>
     * Etc;  <Key: Salt | Value: <MealManagerID: 1, <MealTime: 14:00 , Quantity: 300g >>
     */
    private Map<String, HashMap<Integer, Pair<Second, BigDecimal>>> mealManagersMacroValues;
    
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
    //  Methods
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
        // Add MealManager to Collection
        //##########################################
        mealManagerTreeSet.add(Map.entry(mealManagerID, mealManager));
        
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
    
    public void replaceMealManagerDATA(MealManager mealManager) // Update done by replacing data
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
        mealManagerTreeSet.removeIf(e -> e.getKey().equals(mealManagerID));
        mealManagerTreeSet.add(Map.entry(mealManagerID, mealManager));
        
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
        mealManagerTreeSet.removeIf(e -> e.getKey().equals(mealManagerID));
        
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
        Iterator<Map.Entry<Integer, MealManager>> it = mealManagerTreeSet.iterator();
        while (it.hasNext())
        {
            it.next().getValue().hideMealManager(); // Hide Meal Managers
        }
        
        //##################################
        // Clear / Recreate EMPTY Collection
        //##################################
        recreateEmpty_MealManagersMacrosValues();
    }
    
    public void refresh_MealManagers()
    {
        Iterator<Map.Entry<Integer, MealManager>> it = mealManagerTreeSet.iterator();
        while (it.hasNext())
        {
            //####################################
            // MealManager Info
            //####################################
            MealManager mealManager = it.next().getValue();
            int mealManagerID = mealManager.getMealInPlanID();
            
            //####################################
            // Remove mealManager from memory
            //####################################
            it.remove();
            
            //####################################
            // Remove MealManager (Not Saved)
            //####################################
            if (! mealManager.isMealManagerInDB()) // IF MealManager isn't saved Remove it
            {
                mealManager.completely_Delete_MealManager();  // mealManager is not in the DB erase it from the GUI
                
                // Remove this mealManagers MacrosValues
                for (String macroName : totalMeal_macroColNamePos.keySet())
                {
                    mealManagersMacroValues.get(macroName).remove(mealManagerID);
                }
                continue;
            }
            
            //####################################
            // MealManagers in DB NOW ADD Info
            //####################################
            // Add back to TreeSet to re-correct ORDER
            mealManagerTreeSet.add(Map.entry(mealManagerID, mealManager));
            
            // Reload MealManager Data
            mealManager.reloadTableAndChartsData(false, false);
            
            // Replace this mealManagers MacrosValues
            Second mealTime = mealManager.getCurrentMealTime();
            TotalMealTable totalMealTable = mealManager.getTotalMealTable();
            
            // For Each Macro Replace its old value
            Iterator<Map.Entry<String, Integer>> it2 = totalMeal_macroColNamePos.entrySet().iterator();
            while (it2.hasNext())
            {
                Map.Entry<String, Integer> entry = it2.next();
                
                String macroName = entry.getKey();
                Integer macroPos = entry.getValue();
                
                BigDecimal macroValue = totalMealTable.get_ValueOnTable(0, macroPos);
                
                // Replace OLD Value
                mealManagersMacroValues.get(macroName).put(mealManagerID, new Pair<>(mealTime, macroValue));
            }
        }
    }
    
    //##################################################################################################################
    // Accessor Methods
    ///##################################################################################################################
    
    
    //#########################################
    // Pie Chart
    //#########################################
    private int percent_Calculator(BigDecimal value, BigDecimal overall)
    {
        BigDecimal ratio = value.divide(overall, 4, RoundingMode.DOWN); // 4 decimal places, rounded
        BigDecimal percent = ratio.multiply(BigDecimal.valueOf(100));      // Convert to %
        return percent.setScale(0, RoundingMode.HALF_DOWN).intValueExact();
    }
    
    public DefaultPieDataset<String> create_MM_MacroInfo_PieChart(Integer mealInPlanID)
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
        
        //############################################
        // Add Data to Dataset to represent
        //############################################]
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        for (Map.Entry<String, BigDecimal> macroValues : data.entrySet())
        {
            dataset.setValue(String.format("  %s - %s g   ", macroValues.getKey(), macroValues.getValue()), macroValues.getValue());
        }
        
        return dataset;
    }
    
    //#########################################
    // LineChart Methods
    //#########################################
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
    
    //#########################################
    // Objects
    //#########################################
    public TreeSet<Map.Entry<Integer, MealManager>> get_MealManagerTreeSet()
    {
        return mealManagerTreeSet;
    }
    
    public Map<String, HashMap<Integer, Pair<Second, BigDecimal>>> get_MealManagersMacroValues()
    {
        return mealManagersMacroValues;
    }
}
