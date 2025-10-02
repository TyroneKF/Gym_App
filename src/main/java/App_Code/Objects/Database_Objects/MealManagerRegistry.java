package App_Code.Objects.Database_Objects;

import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.TotalMealTable;
import App_Code.Objects.Screens.Meal_Plan_Screen;
import org.javatuples.Pair;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;

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
    private Map<String, Map<Integer, Pair<LocalTime, BigDecimal>>> mealManagersMacroValues;
    
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
        LocalTime mealManagerTime = mealManager.getCurrentMealTime();
        TotalMealTable totalMealTable = mealManager.getTotalMealTable();
        
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
    
    public void completelyRemoveMealManager(MealManager mealManager)
    {
        //##########################################
        // mealManager Info
        //##########################################
        int mealManagerID = mealManager.getMealInPlanID();
        
        //##########################################
        // Remove MealManager to Collection
        //##########################################
        mealManagerTreeSet.removeIf(e -> e.getKey().equals(mealManagerID));
        
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
            
            // Relative to TotalMealTable
            String macroName = mapEntry.getKey();
            
            // <Key: MacroName | Value: ArrayList<Key: MealManagerID, Value: < MealTime, Quantity>>
            mealManagersMacroValues.get(macroName).remove(mealManagerID);
        }
    }
    
    public void replaceMealManagerDATA(MealManager mealManager)
    {
        //##########################################
        // mealManager Info
        //##########################################
        int mealManagerID = mealManager.getMealInPlanID();
        LocalTime mealManagerTime = mealManager.getCurrentMealTime();
        TotalMealTable totalMealTable = mealManager.getTotalMealTable();
        
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
    
    //############################################################
    // Accessor Methods
    //############################################################
    public Map<String, Map<Integer, Pair<LocalTime, BigDecimal>>> get_MealManagersMacroValues()
    {
        return mealManagersMacroValues;
    }
    
    public TreeSet<Map.Entry<Integer, MealManager>> get_MealManagerTreeSet()
    {
        return mealManagerTreeSet;
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
            mealManager.reloadingIngredientsTableDataFromRefresh(false, false);
            
            // Replace this mealManagers MacrosValues
            LocalTime mealTime = mealManager.getCurrentMealTime();
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
    
    
}
