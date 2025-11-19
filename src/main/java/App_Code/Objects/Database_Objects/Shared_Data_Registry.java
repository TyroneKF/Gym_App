package App_Code.Objects.Database_Objects;

import App_Code.Objects.Data_Objects.Storable_Ingredient_IDS.Ingredient_Name_ID;
import App_Code.Objects.Data_Objects.Storable_Ingredient_IDS.Ingredient_Type_ID;
import App_Code.Objects.Data_Objects.Storable_Ingredient_IDS.Storable_IDS_Parent;
import App_Code.Objects.Data_Objects.Storable_Ingredient_IDS.Store_ID;
import App_Code.Objects.Tables.JTable_JDBC.Children.ViewDataTables.TotalMeal_Table;
import App_Code.Objects.Screens.Meal_Plan_Screen;

import App_Code.Objects.Tables.MealManager;
import org.javatuples.Pair;
import org.jfree.data.general.DefaultPieDataset;

import java.math.BigDecimal;
import java.util.*;

public class Shared_Data_Registry
{
    private Meal_Plan_Screen meal_plan_screen;
    
    //##################################################################################################################
    // Collections
    //##################################################################################################################
    /**
     * LinkedHashMap<String, Pair<Integer, String>> totalMeal_macroColNamePos
     * LinkedHashMap<TotalMeal_MacroName, Pair< Position, Measurement>> totalMeal_macroColNamePos
     */
    private LinkedHashMap<String, Pair<Integer, String>> totalMeal_Macro_Pos_And_Symbol;
    
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
    
    //##############################
    // Table / Form Data Collections
    //##############################
    // Stores
    private HashMap<Integer, Store_ID> stores_ID_Obj_Map = new HashMap<>();
    private ArrayList<Store_ID> stores_AL = new ArrayList<>();
    
    // Ingredient Types
    private HashMap<Integer, Ingredient_Type_ID> ingredient_Types_ID_Obj_Map = new HashMap<>();
    private ArrayList<Ingredient_Type_ID> ingredient_Types_AL = new ArrayList<>();
    
    // Ingredient Types To Names
    private HashMap<Ingredient_Type_ID, ArrayList<Ingredient_Name_ID>> ingredient_Types_To_Names_Map = new HashMap<>();
    
    // Ingredient Names
    private HashMap<Integer, Ingredient_Name_ID> ingredient_Names_ID_Obj_Map = new HashMap<>();
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Shared_Data_Registry(Meal_Plan_Screen meal_plan_screen)
    {
        //##################################
        // Variables
        //##################################
        this.meal_plan_screen = meal_plan_screen;
        this.totalMeal_Macro_Pos_And_Symbol = meal_plan_screen.get_TotalMeal_macro_Col_Name_And_Pos();
        
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
        for (String macroName : totalMeal_Macro_Pos_And_Symbol.keySet())
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
    //###############################################################################
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
        TotalMeal_Table totalMealTable = mealManager.get_Total_Meal_Table();
        
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
        
        Iterator<Map.Entry<String, Pair<Integer, String>>> it = totalMeal_Macro_Pos_And_Symbol.entrySet().iterator();
        
        while (it.hasNext())
        {
            Map.Entry<String, Pair<Integer, String>> mapEntry = it.next();
            String macroName = mapEntry.getKey();
            Integer macroPos = mapEntry.getValue().getValue0();
            
            BigDecimal macroValue = (BigDecimal) totalMealTable.get_Value_On_Table(0, macroPos);
            
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
        int mealManagerID = mealManager.get_Meal_In_Plan_ID();
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
        
        Iterator<String> it = totalMeal_Macro_Pos_And_Symbol.keySet().iterator();
        
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
            
            //#################################################################
            // Remove MealManager (Not Saved)
            //#################################################################
            if (! mealManager.isMealManagerInDB()) // IF MealManager isn't saved Remove it
            {
                if (! mealManager.is_Meal_Deleted()) // if the meal hasn't beene deleted, delete its data & GUI
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
    
    //#################################################################################################################
    // Pie Chart [TotalMeal] :
    //#################################################################################################################
    
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
        int mealInPlanID = mealManager.get_Meal_In_Plan_ID();
        
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
    
    private DefaultPieDataset<String> get_Updated_PieChart_Dataset(MealManager mealManager)
    {
        //###################################################
        // Get Macros
        //###################################################
        LinkedHashMap<String, BigDecimal> data = new LinkedHashMap<String, BigDecimal>()
        {{
            // ###########################
            // Protein
            // ###########################
            put("Protein", mealManagers_TotalMeal_MacroValues.get("total_protein").get(mealManager));
            
            // ###########################
            // Carbs
            // ###########################
            BigDecimal sugarCarbsValue = mealManagers_TotalMeal_MacroValues.get("total_sugars_of_carbs").get(mealManager);
            BigDecimal carbsValue = mealManagers_TotalMeal_MacroValues.get("total_carbohydrates").get(mealManager);
            
            put("Carbohydrates", carbsValue.subtract(sugarCarbsValue));
            put("Sugars Of Carbs", sugarCarbsValue);
            
            // ###########################
            // Fats
            // ###########################
            BigDecimal satFatsValue = mealManagers_TotalMeal_MacroValues.get("total_saturated_fat").get(mealManager);
            BigDecimal fatsValue = mealManagers_TotalMeal_MacroValues.get("total_fats").get(mealManager);
            
            put("Fats", fatsValue.subtract(satFatsValue));
            put("Saturated Fats", satFatsValue);
        }};
        
        //#############################################
        // Add Data to Dataset to represent
        //#############################################
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        data.forEach(dataset :: setValue); // Transfer data, preserves order too
        
        //#############################################
        // Return Value
        //#############################################
        return dataset;
    }
    
    //############################
    //  Update Methods
    //############################
    public Boolean update_PieChart_Values(MealManager mealManager)
    {
        //#########################################
        //
        //#########################################
        int mealInPlanID = mealManager.get_Meal_In_Plan_ID();
        
        //#########################################
        // IF PieChart Not Open Exit
        //#########################################
        if (! pieChart_Dataset_HashMap.containsKey(mealInPlanID)) { return false; }
        
        //#########################################
        // Updated Saved Dataset With New Values
        //#########################################
        DefaultPieDataset<String> newGenerated = get_Updated_PieChart_Dataset(mealManager);
        DefaultPieDataset<String> mm_PieData = pieChart_Dataset_HashMap.get(mealInPlanID);
        
        // Stop PieDataset event listener from being triggered on each key update and instead on batch (avoids key races)
        mm_PieData.setNotify(false);
        
        // Clear Data First
        mm_PieData.clear();
        
        // Transfer Data Over into this dataset
        newGenerated.getKeys().forEach(key -> {
            mm_PieData.setValue(key, newGenerated.getValue(key));
        });
        
        // Turn Notifications back on
        mm_PieData.setNotify(true);
        
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
    
    //#################################################################################################################
    // Object DATA Methods
    //#################################################################################################################
    public <T extends Storable_IDS_Parent> void sort_ID_Objects_AL(ArrayList<T> list)
    {
        list.sort((a, b) -> a.get_Name().compareTo(b.get_Name()));
    }
    
    private <T extends Storable_IDS_Parent> void add_ID_Obj(T id_Object, boolean sort, ArrayList<T> list)
    {
        // Add lists if they're of these Types
        if (id_Object instanceof Ingredient_Type_ID || id_Object instanceof Store_ID)
        {
            // Add Store Obj to list
            list.add(id_Object);
            
            // Sort list if needed
            if (sort) { sort_ID_Objects_AL(list); }
        }
        
        // Get Object ID
        int id = id_Object.get_ID();
        
        // Add to ordered Map for specific class
        switch (id_Object)
        {
            case Store_ID x -> stores_ID_Obj_Map.put(id, x);
            case Ingredient_Type_ID x -> ingredient_Types_ID_Obj_Map.put(id, x);
            case Ingredient_Name_ID x ->
            {
                // Add to ID Objects to Ingredients Name Map
                ingredient_Names_ID_Obj_Map.put(id, x);
                
                // Get Ingredient Type ID
                Ingredient_Type_ID ingredient_type_id = x.get_Ingredient_Type_ID_Obj();
                
                // Check if this Type already has a list of ingredients
                if (ingredient_Types_To_Names_Map.containsKey(ingredient_type_id))
                {
                    // Get Ingredient Names List
                    ArrayList<Ingredient_Name_ID> ingredient_Names_IDs = ingredient_Types_To_Names_Map.get(ingredient_type_id);
                    
                    // Add Ingredient Name to List
                    ingredient_Names_IDs.add(x);
                    
                    // Sort List
                    sort_ID_Objects_AL(ingredient_Names_IDs);
                }
                else // No Ingredients Assigned to Ingredients Type
                {
                    // Create ArrayList
                    ArrayList<Ingredient_Name_ID> ingredient_Names_IDs = new ArrayList<>();
                    
                    // Add Ingredient Name to List
                    ingredient_Names_IDs.add(x);
                    
                    // Add Ingredient Types to Map
                    ingredient_Types_To_Names_Map.put(ingredient_type_id, ingredient_Names_IDs);
                }
            }
            default -> System.err.print("\n...");// This shouldn't throw an error
        }
    }
    
    
    //###############################################################
    // HashMap Ingredient Types To Ingredient Names
    //###############################################################
    
    // Change Methods
    public boolean move_Ingredient_To_Type(Ingredient_Type_ID to_Type, Ingredient_Type_ID from_Type, Ingredient_Name_ID ingredient_name)
    {
        // Check if From_Type Key has any records to move
        if (! ingredient_Types_To_Names_Map.containsKey(from_Type)) { return false; }
        
        // Assign from_Type to variable
        ArrayList<Ingredient_Name_ID> from_Type_AL = ingredient_Types_To_Names_Map.get(from_Type);
        
        // Check if Ingredient Name Exists in from_Type
        if (! from_Type_AL.contains(ingredient_name)) { return false; }
        
        // Remove Ingredient_Name from from_Type
        from_Type_AL.remove(ingredient_name);
        
        //Check if from_type is empty now remove it
        if (from_Type_AL.isEmpty()) { ingredient_Types_To_Names_Map.remove(from_Type); }
        
        // Add to ingredient_name to to_Type
        //add_Ingredient_Name_To_Type_Map(to_Type, ingredient_name);
        
        return true;
    }
    
    // Bulk
    private void move_Ingredients_In_Bulk_To_Type(Ingredient_Type_ID from_Type, Ingredient_Type_ID to_Type)
    {
        //#####################################
        // Check if From Key is Valid
        //#####################################
        if (! ingredient_Types_To_Names_Map.containsKey(from_Type)) { return; }
        
        ArrayList<Ingredient_Name_ID> ingredient_Names_From = ingredient_Types_To_Names_Map.get(from_Type);  // Get Ingredient Names Associated with from Key
        
        //#####################################
        // Combine Results If Exists
        //#####################################
        // IF To_type Key does exist then combine with From_Type  ingredients then add to To_Type results
        if (ingredient_Types_To_Names_Map.containsKey(to_Type))
        {
            ArrayList<Ingredient_Name_ID> ingredient_Names_To = ingredient_Types_To_Names_Map.get(to_Type); // get from_Type values
            
            ingredient_Names_To.addAll(ingredient_Names_From); // combine results
            
            sort_ID_Objects_AL(ingredient_Names_To); // Sort List
            
            // ingredient_Types_To_Names.put(to_Type, ingredient_Names_To);
        }
        else // Move Ingredients Name from From_Type to To_Type
        {
            ingredient_Types_To_Names_Map.put(to_Type, ingredient_Names_From);
        }
        
        //#####################################
        // Delete From Key
        //#####################################
        ingredient_Types_To_Names_Map.remove(from_Type);
    }
    
    //##############################################################
    // Ingredient Names
    //##############################################################
    public void add_Ingredient_Name(Ingredient_Name_ID ingredient_Name_ID)
    {
        add_ID_Obj(ingredient_Name_ID, false, null);
    }
    
    public boolean remove_Ingredient_Name(Ingredient_Name_ID ingredient_Name_ID)
    {
        //###################################
        // Remove from Ingredients Type Map
        //###################################
        
        // Get Ingredients Type OBJ
        Ingredient_Type_ID type_id_OBJ = ingredient_Name_ID.get_Ingredient_Type_ID_Obj();
        
        // Check IF Map Contains this Ingredient Type
        if (! ingredient_Types_To_Names_Map.containsKey(type_id_OBJ)) { return false; }
        
        // Get Ingredients Names
        ArrayList<Ingredient_Name_ID> ingredient_Names = ingredient_Types_To_Names_Map.get(type_id_OBJ);
        
        // Remove Ingredients Name From Type List
        ingredient_Names.remove(ingredient_Name_ID);
        
        // If Ingredients Names is empty remove the whole list
        if(ingredient_Names.isEmpty()){ ingredient_Types_To_Names_Map.remove(type_id_OBJ);}
        
        //###################################
        // Remove From Ingredients Map
        //###################################
        int id = ingredient_Name_ID.get_ID(); // Get ID
        if (! ingredient_Names_ID_Obj_Map.containsKey(id)) { return false; }
        
        ingredient_Names_ID_Obj_Map.remove(id);// Remove From Map
        
        //###################################
        // Output
        //###################################
        return false;
    }
    
    //##############################################################
    // Ingredient Types - Singular List
    //##############################################################
    public void add_Ingredient_Type(Ingredient_Type_ID ingredient_type_id, boolean sort)
    {
        add_ID_Obj(ingredient_type_id, sort, ingredient_Types_AL);
    }
    
    public boolean remove_Ingredient_Type(Ingredient_Type_ID ingredient_Type_ID) throws Exception
    {
        // Get 'Un-Assigned' Type
        Ingredient_Type_ID  to_Type_ID = ingredient_Types_ID_Obj_Map.get(2);
        
        // Move & Delete From Ingredient Type to Un-Assigned
        move_Ingredients_In_Bulk_To_Type(ingredient_Type_ID, to_Type_ID);
        
        // Return Output
        return false;
    }
    
    //#################################################################
    // Stores
    //#################################################################
    public void add_Store(Integer id, String name, boolean sort)
    {
        add_ID_Obj(new Store_ID(id, name), sort, stores_AL);
    }
    
    public boolean remove_Store(Store_ID store_id)
    {
        int id = store_id.get_ID(); // Get ID
        
        if (! stores_ID_Obj_Map.containsKey(id)) { return false; }  // Check If Object is in Map (should be)
        
        stores_ID_Obj_Map.remove(id);// Remove From Map
        
        stores_AL.remove(store_id); // Remove From Stores AL
        
        return true; // Output
    }
    
    //##############################################################################################
    // Accessor Methods
    //###############################################################################################
    public ArrayList<Store_ID> get_Stores()
    {
        return stores_AL;
    }
    
    // Types
    public ArrayList<Ingredient_Type_ID> get_Ingredient_Types()
    {
        return ingredient_Types_AL;
    }
    
    public ArrayList<Ingredient_Name_ID> get_Ingredient_Names(Ingredient_Type_ID ingredient_type_id)
    {
        return ingredient_Types_To_Names_Map.get(ingredient_type_id);
    }
    
    public HashMap<Ingredient_Type_ID, ArrayList<Ingredient_Name_ID>> get_Ingredient_Types_To_Names()
    {
        return ingredient_Types_To_Names_Map;
    }
    
    //##################################################################################################################
    // Accessor Methods
    //##################################################################################################################
    public int get_Active_MealCount()
    {
        return (int) mealManager_ArrayList.stream().filter(mealManager -> ! mealManager.is_Meal_Deleted()).count();
    }
    
    //############################
    // MealManagers / TotalMeal
    //#############################
    public ArrayList<MealManager> get_MealManager_ArrayList()
    {
        return mealManager_ArrayList;
    }
    
    public LinkedHashMap<String, HashMap<MealManager, BigDecimal>> get_MealManagers_MacroValues()
    {
        return mealManagers_TotalMeal_MacroValues;
    }
    
    public LinkedHashMap<String, Pair<Integer, String>> get_TotalMeal_Macro_Pos_And_Symbol()
    {
        return totalMeal_Macro_Pos_And_Symbol;
    }
}
