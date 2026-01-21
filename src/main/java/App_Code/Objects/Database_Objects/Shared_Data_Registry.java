package App_Code.Objects.Database_Objects;

import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.*;
import App_Code.Objects.Screens.Meal_Plan_Screen;
import App_Code.Objects.Table_Objects.Tables.Children.Ingredients_Table.Ingredients_Table_Columns;
import App_Code.Objects.Table_Objects.Tables.Children.View_Data_Tables.Children.Total_Meal_Table.Total_Meal_Macro_Columns;
import App_Code.Objects.Table_Objects.MealManager;
import org.jfree.data.general.DefaultPieDataset;

import java.math.BigDecimal;
import java.util.*;

public class Shared_Data_Registry
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    // Strings
    private String plan_Name;
    
    //###############################################
    // Integers
    //###############################################
    
    private Integer selected_plan_id;
    private Integer selected_plan_version_id;
    private Integer user_id;
    private int na_ingredient_id;
    private int na_pdid;
    
    //######################################################################
    // Collections
    //######################################################################
    // Meta Data
    private final LinkedHashMap<Total_Meal_Macro_Columns, Integer> total_meal_macro_pos;
    private final LinkedHashMap<Total_Meal_Macro_Columns, String> total_meal_macro_symbol;
    private final HashMap<Ingredients_Table_Columns, Integer> ingredients_table_cols_positions;
    
    private ArrayList<MealManager> mealManager_ArrayList = new ArrayList<>();
    private final HashMap<MealManager, DefaultPieDataset<Total_Meal_Macro_Columns>> pieChart_Dataset_Map = new HashMap<>();
    
    /*
     * HashMap<String, HashMap<MealManager, BigDecimal>> mealManagers_TotalMeal_MacroValues = new HashMap<>();
     * Stores all the mealManagers TotalMealValues in collections by the macroName
     * <p>
     * <Key: MacroName | Value: HashMap <Key: MealManager, Value:  Quantity>>
     * Etc;  <Key: Salt | Value: HashMap<MealManager, Quantity: 300g >>
     */
    private final LinkedHashMap<Total_Meal_Macro_Columns, HashMap<MealManager, BigDecimal>> totals_by_macro = new LinkedHashMap<>();
    
    private final Map<MealManager, EnumMap<Total_Meal_Macro_Columns, BigDecimal>> totals_by_meal = new HashMap<>();
    
    //###############################################
    // Meta Data Objects
    //###############################################
    /*
     
     */
    
    //##################
    // Stores
    //##################
    private HashMap<Integer, Store_ID_OBJ> stores_Obj_Map = new HashMap<>();
    private ArrayList<Store_ID_OBJ> stores_Obj_AL = new ArrayList<>();
    
    //##################
    // Ingredient Types
    //##################
    private HashMap<Integer, Ingredient_Type_ID_OBJ> ingredient_Types_Obj_Map = new HashMap<>();
    private ArrayList<Ingredient_Type_ID_OBJ> all_Ingredient_Types_Obj_AL = new ArrayList<>();
    
    // Ingredient Types To Names
    private HashMap<Integer, ArrayList<Ingredient_Name_ID_OBJ>> ingredient_Types_To_Names_Map = new HashMap<>();
    private ArrayList<Ingredient_Type_ID_OBJ> ingredient_Types_With_Ingredients_AL = new ArrayList<>();
    
    //##################
    // Ingredient Names
    //##################
    private HashMap<Integer, Ingredient_Name_ID_OBJ> ingredient_Names_Obj_Map = new HashMap<>();
    private ArrayList<Ingredient_Name_ID_OBJ> ingredient_Names_Obj_AL = new ArrayList<>();
    
    //##################
    // Material Type
    //##################
    private HashMap<Integer, Measurement_Material_Type_ID_OBJ> measurement_Material_Type_Obj_Map = new HashMap<>();
    private ArrayList<Measurement_Material_Type_ID_OBJ> measurement_Material_Type_Obj_AL = new ArrayList<>();
    
    //##################
    // Measurements
    //##################
    private HashMap<Integer, Measurement_ID_OBJ> ingredient_Measurement_Obj_Map = new HashMap<>();
    private ArrayList<Measurement_ID_OBJ> ingredient_Measurement_Obj_AL = new ArrayList<>();
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Shared_Data_Registry(Meal_Plan_Screen meal_plan_screen)
    {
        //##################################
        // Variables
        //##################################
        total_meal_macro_pos = meal_plan_screen.get_Total_Meal_Macro_Col_Pos();
        total_meal_macro_symbol = meal_plan_screen.get_Total_Meal_Macro_Symbols();
        
        ingredients_table_cols_positions = meal_plan_screen.get_Ingredients_Table_Col_Pos();
    }
    
    //##################################################################################################################
    //
    //##################################################################################################################
    /*
     
    */
    
    //###############################################################################
    // Sort Methods
    //###############################################################################
    public void sort_MealManager_AL()
    {
        mealManager_ArrayList.sort((a, b) -> a.get_Current_Meal_Time().compareTo(b.get_Current_Meal_Time()));
    }
    
    //###############################################################################
    // ADD Methods
    //###############################################################################
    public void addMealManager(MealManager mealManager, ArrayList<Object> total_meal_data) // ADD Pie DATA
    {
        mealManager_ArrayList.add(mealManager); // Add MealManager to Collection & Sort
        
        add_OR_Replace_MealManager_Macros_DATA(mealManager, total_meal_data); // Add MealManager Macro Values
        
        sort_MealManager_AL();   // Sort LISTS
    }
    
    public void add_OR_Replace_MealManager_Macros_DATA(MealManager mealManager, ArrayList<Object> total_meal_data) // Update done by replacing data
    {
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
        
        /**
         *  <Key: MacroName | Value: HashMap <Key: MealManager, Value:  Quantity>>
         *   Put, Replace have the same effect
         */
        for (Map.Entry<Total_Meal_Macro_Columns, Integer> macro : total_meal_macro_pos.entrySet())
        {
            Total_Meal_Macro_Columns macro_name = macro.getKey();
            int macroPos = macro.getValue();
            
            BigDecimal macro_value = (BigDecimal) total_meal_data.get(macroPos);
            
            /**
             *  <Key: MacroName | Value: HashMap <Key: MealManager, Value:  Quantity>>
             *   Put, Replace have the same effect
             */
            totals_by_macro
                    .computeIfAbsent(macro_name, k -> new HashMap<>())
                    .put(mealManager, macro_value);
            
            totals_by_meal
                    .computeIfAbsent(mealManager, k -> new EnumMap<>(Total_Meal_Macro_Columns.class))
                    .put(macro_name, macro_value);
        }
    }
    
    //###############################################################################
    // DELETE Methods
    //###############################################################################
    public void delete_MealManager(MealManager mealManager)
    {
        remove_MealManager_Macro_Values(mealManager);     // Remove MealManager MacroValues
        
        remove_PieChart_DatasetValues(mealManager);  // Remove MealManager PieChart Data
    }
    
    private void remove_MealManager_Macro_Values(MealManager mealManager)
    {
        /**
         * HashMap<String, HashMap<MealManager, BigDecimal>> mealManagers_TotalMeal_MacroValues = new HashMap<>();
         * Stores all the mealManagers TotalMealValues in collections by the macroName
         * <Key: MacroName | Value: HashMap <Key: MealManager, Value:  Quantity>>
         * Etc;  <Key: Salt | Value: HashMap<MealManager, Quantity: 300g >>
         */
        
        for (Total_Meal_Macro_Columns macro_key : total_meal_macro_pos.keySet())
        {
            // <Key: MacroName | Value: HashMap <Key: MealManager, Value:  Quantity>>
            totals_by_macro.get(macro_key).remove(mealManager);
        }
        
        totals_by_meal.remove(mealManager);
    }
    
    //################################################
    // DELETE MealManager (In Group) Meal_Plan_Screen
    //################################################
    public void delete_MealManagers_MPS()
    {
        //###########################################################
        // Delete MealManagers
        //###########################################################
        for (MealManager mealManager : mealManager_ArrayList)
        {
            mealManager.delete_MealManager(); // Hide Meal Managers
        }
        
        //###########################################################
        // Clear / Recreate EMPTY Collection
        //###########################################################
        totals_by_macro.clear();
        totals_by_meal.clear();
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
            if (! mealManager.is_MealManager_In_DB()) // IF MealManager isn't saved Remove it
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
            mealManager.reload_Table_And_Charts_Data(false, false);
            
            //#################################################################
            // Re-Upload Or, Change Meal MacroData
            //#################################################################
            // add_OR_Replace_MealManager_Macros_DATA(mealManager);
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
    public DefaultPieDataset<Total_Meal_Macro_Columns> get_OR_Create_Updated_PieChart_Dataset(MealManager mealManager)
    {
        //################################################
        // IF Meals Pie Data Exists Return it
        //################################################
        if (pieChart_Dataset_Map.containsKey(mealManager))
        {
            System.err.printf("\ncreate_MM_MacroInfo_PieChart() Already Created %s", mealManager.get_Draft_Meal_In_Plan_ID());
            return pieChart_Dataset_Map.get(mealManager);
        }
        
        //#################################################
        // Pie Chart Data Doesn't Exist Create it & Add it
        //#################################################
        pieChart_Dataset_Map.put(mealManager, create_Updated_PieChart_Dataset(mealManager));
        
        //################################################
        // Return retrieved Dataset
        //################################################
        return pieChart_Dataset_Map.get(mealManager);
    }
    
    //############################
    //  Update Methods
    //############################
    public boolean update_PieChart_Values(MealManager mealManager)
    {
        //#########################################
        // IF PieChart Not Open Exit
        //#########################################
        if (! pieChart_Dataset_Map.containsKey(mealManager)) { return false; }
        
        //#########################################
        // Updated Saved Dataset With New Values
        //#########################################
        DefaultPieDataset<Total_Meal_Macro_Columns> newGenerated = create_Updated_PieChart_Dataset(mealManager);
        DefaultPieDataset<Total_Meal_Macro_Columns> mm_PieData = pieChart_Dataset_Map.get(mealManager);
        
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
    
    private DefaultPieDataset<Total_Meal_Macro_Columns> create_Updated_PieChart_Dataset(MealManager mealManager)
    {
        //###################################################
        // Get Macros
        //###################################################
        LinkedHashMap<Total_Meal_Macro_Columns, BigDecimal> data = new LinkedHashMap<>()
        {{
            // ###########################
            // Protein
            // ###########################
            put(Total_Meal_Macro_Columns.TOTAL_PROTEIN, totals_by_macro.get(Total_Meal_Macro_Columns.TOTAL_PROTEIN).get(mealManager));
            
            // ###########################
            // Carbs
            // ###########################
            BigDecimal sugarCarbsValue = totals_by_macro.get(Total_Meal_Macro_Columns.TOTAL_SUGARS_OF_CARBS).get(mealManager);
            BigDecimal carbsValue = totals_by_macro.get(Total_Meal_Macro_Columns.TOTAL_CARBOHYDRATES).get(mealManager);
            
            put(Total_Meal_Macro_Columns.TOTAL_CARBOHYDRATES, carbsValue.subtract(sugarCarbsValue));
            put(Total_Meal_Macro_Columns.TOTAL_SUGARS_OF_CARBS, sugarCarbsValue);
            
            // ###########################
            // Fats
            // ###########################
            BigDecimal satFatsValue = totals_by_macro.get(Total_Meal_Macro_Columns.TOTAL_SATURATED_FAT).get(mealManager);
            BigDecimal fatsValue = totals_by_macro.get(Total_Meal_Macro_Columns.TOTAL_FATS).get(mealManager);
            
            put(Total_Meal_Macro_Columns.TOTAL_FATS, fatsValue.subtract(satFatsValue));
            put(Total_Meal_Macro_Columns.TOTAL_SATURATED_FAT, satFatsValue);
        }};
        
        //#############################################
        // Add Data to Dataset to represent
        //#############################################
        DefaultPieDataset<Total_Meal_Macro_Columns> dataset = new DefaultPieDataset<>();
        data.forEach(dataset :: setValue); // Transfer data, preserves order too
        
        //#############################################
        // Return Value
        //#############################################
        return dataset;
    }
    
    //############################
    // PieChart: Remove Methods
    //############################
    public void remove_PieChart_DatasetValues(MealManager meal_manager)
    {
        pieChart_Dataset_Map.remove(meal_manager);
    }
    
    //#################################################################################################################
    // Object ID : DATA Methods
    //#################################################################################################################
    public <T extends Storable_IDS_Parent> void sort_ID_Objects_AL(ArrayList<T> list)
    {
        list.sort((a, b) -> a.get_Name().compareToIgnoreCase(b.get_Name()));
    }
    
    private <T extends Storable_IDS_Parent> void add_ID_Object(T id_Obj, ArrayList<T> list, boolean sort, Map<Integer, T> map)
    {
        int id = id_Obj.get_ID(); // Get ID of Object
        
        list.add(id_Obj); // Add Obj to list
        
        if (sort) { sort_ID_Objects_AL(list); } // Sort list if needed
        
        map.put(id, id_Obj); // Add Object in Map
    }
    
    private <T extends Storable_IDS_Parent> boolean remove_ID_Object(T id_Obj, ArrayList<T> list, Map<Integer, T> map)
    {
        int id = id_Obj.get_ID(); // Get ID of Object
        
        if (! map.containsKey(id)) { return false; }  // Check If Object is in Map (should be)
        
        map.remove(id); // Remove From Map
        
        list.remove(id_Obj); // Remove From Stores AL
        
        return true; // Output
    }
    
    
    //###############################################################
    // HashMap Ingredient Types To Ingredient Names
    //###############################################################
    private void remove_Mapped_Ingredient_Type(Ingredient_Type_ID_OBJ type_Obj)
    {
        ingredient_Types_To_Names_Map.remove(type_Obj.get_ID());
        ingredient_Types_With_Ingredients_AL.remove(type_Obj);
    }
    
    private void add_Mapped_Ingredient_Type(Ingredient_Type_ID_OBJ type_Obj, ArrayList<Ingredient_Name_ID_OBJ> ingredient_Names)
    {
        ingredient_Types_To_Names_Map.put(type_Obj.get_ID(), ingredient_Names);
        
        ingredient_Types_With_Ingredients_AL.add(type_Obj);
        sort_ID_Objects_AL(ingredient_Types_With_Ingredients_AL); // Sort List by Ingredient Type Name
    }
    
    // Change Methods
    public boolean change_Ingredient_Type(Ingredient_Type_ID_OBJ to_Type_Obj, Ingredient_Name_ID_OBJ ingredient_Name_Obj)
    {
        //####################################
        // Get Object Info
        //####################################
        // Get ingredient_Type Obj & ID Associated with Ingredient_Name
        Ingredient_Type_ID_OBJ from_Type_Obj = ingredient_Name_Obj.get_Ingredient_Type_Obj();
        int from_Type_id = from_Type_Obj.get_ID();  // Get Ingredient Type Associated with current Ingredient Name
        
        int to_Type_ID = to_Type_Obj.get_ID();
        
        //####################################
        // Exit Clause
        //####################################
        if (from_Type_id == to_Type_ID) { return false; } // No change needed
        
        //####################################
        // From Type
        //####################################
        // Get Ingredient Names associated with From_Type
        ArrayList<Ingredient_Name_ID_OBJ> from_Type_AL = ingredient_Types_To_Names_Map.get(from_Type_id);
        
        // Remove Ingredient Name from From_Type
        if (from_Type_AL == null || ! from_Type_AL.remove(ingredient_Name_Obj)) { return false; }
        
        // Check if from_type is empty after ingredient removal, if yes now remove it
        if (from_Type_AL.isEmpty()) { remove_Mapped_Ingredient_Type(from_Type_Obj); }
        
        //#####################################
        // Set Ingredient_Name Type to to_Type
        //#####################################
        ingredient_Name_Obj.set_Ingredient_Type_ID_Obj(to_Type_Obj); // Set Ingredient Type to new Ingredient Type
        
        //######################################
        // Add to Ingredient_Name to Type & Sort
        //######################################
        // Get Or Create an Empty list if not available
        ArrayList<Ingredient_Name_ID_OBJ> to_Type_Ingredients_Names = ingredient_Types_To_Names_Map.getOrDefault(to_Type_ID, new ArrayList<>());
        
        boolean no_Previous_Ingredients = to_Type_Ingredients_Names.isEmpty(); // If this ingredient_Type Had no previous ingredients = new list
        
        to_Type_Ingredients_Names.add(ingredient_Name_Obj);
        
        // Sort Ingredient Names
        sort_ID_Objects_AL(to_Type_Ingredients_Names);
        
        // Update / Add to_Type to list
        if (no_Previous_Ingredients) // If this ingredient_Type Had no previous ingredients
        {
            add_Mapped_Ingredient_Type(to_Type_Obj, to_Type_Ingredients_Names);
        }
        else // Update Collection (not a new List)
        {
            ingredient_Types_To_Names_Map.put(to_Type_ID, to_Type_Ingredients_Names);
        }
        
        //####################################
        // Return
        //####################################
        return true;
    }
    
    // Bulk
    private void move_Ingredients_In_Bulk_To_Type(Ingredient_Type_ID_OBJ from_Type_Obj, Ingredient_Type_ID_OBJ to_Type_Obj)
    {
        //#####################################
        // Check if From Key is Valid
        //#####################################
        int from_Type_id = from_Type_Obj.get_ID(); // Get from_Type ID
        
        if (! ingredient_Types_To_Names_Map.containsKey(from_Type_id)) { return; } // Nothing to Move
        
        // Get Ingredient Names Associated with from Key
        ArrayList<Ingredient_Name_ID_OBJ> ingredient_Names_From = ingredient_Types_To_Names_Map.get(from_Type_id);
        
        //#####################################
        // Combine Results If Exists
        //#####################################
        int to_Type_id = to_Type_Obj.get_ID();
        
        // IF To_type already has Ingredients = Merge with to_Type
        if (ingredient_Types_To_Names_Map.containsKey(to_Type_id))
        {
            ArrayList<Ingredient_Name_ID_OBJ> ingredient_Names_To = ingredient_Types_To_Names_Map.get(to_Type_id); // get from_Type_Obj values
            
            ingredient_Names_To.addAll(ingredient_Names_From); // combine results
            
            sort_ID_Objects_AL(ingredient_Names_To); // Sort List
        }
        else // Add all ingredient_Names to to_Type
        {
            add_Mapped_Ingredient_Type(to_Type_Obj, ingredient_Names_From);
        }
        
        // Change all Ingredient Types for Ingredients_Name to new  Type
        ingredient_Types_To_Names_Map.get(to_Type_id).forEach(e -> e.set_Ingredient_Type_ID_Obj(to_Type_Obj));
        
        //#####################################
        // Delete From Key
        //#####################################
        remove_Mapped_Ingredient_Type(from_Type_Obj);
    }
    
    //##############################################################
    // Ingredient Names
    //##############################################################
    public void add_Ingredient_Name(Ingredient_Name_ID_OBJ ingredient_Name_Id_Obj, boolean sort)
    {
        //###################################
        // Add Map /AL For Ingredient Names
        //##################################
        add_ID_Object(ingredient_Name_Id_Obj, ingredient_Names_Obj_AL, sort, ingredient_Names_Obj_Map);// Add to Map / AL
        
        //##################################
        // Add Ingredient_Name to Type
        //##################################
        // Get Ingredient_Type OBJ & ID Associated with Ingredient_Name
        Ingredient_Type_ID_OBJ ingredient_Type_ID_Obj = ingredient_Name_Id_Obj.get_Ingredient_Type_Obj();
        int ingredient_Type_Id = ingredient_Type_ID_Obj.get_ID();
        
        // Get Ingredient_Names associated with Ingredient_Type Or Create it
        ArrayList<Ingredient_Name_ID_OBJ> ingredient_Names_Objs_AL = ingredient_Types_To_Names_Map.getOrDefault(ingredient_Type_Id, new ArrayList<>());
        
        // Check if Ingredients_Type has had previous ingredients
        boolean previous_Ingredients = ! ingredient_Names_Objs_AL.isEmpty();
        
        // Add Ingredient Name to List
        ingredient_Names_Objs_AL.add(ingredient_Name_Id_Obj);
        
        // Sort List
        sort_ID_Objects_AL(ingredient_Names_Objs_AL);
        
        // Add Ingredient Types to Map
        if (previous_Ingredients)
        {
            ingredient_Types_To_Names_Map.put(ingredient_Type_Id, ingredient_Names_Objs_AL);
        }
        else
        {
            add_Mapped_Ingredient_Type(ingredient_Type_ID_Obj, ingredient_Names_Objs_AL);
        }
    }
    
    public boolean remove_Ingredient_Name(Ingredient_Name_ID_OBJ ingredient_Name_ID_Obj)
    {
        //###################################
        // Remove from Ingredients Type Map
        //###################################
        // Get Ingredients Type OBJ
        Ingredient_Type_ID_OBJ ingredient_Type_ID_Obj = ingredient_Name_ID_Obj.get_Ingredient_Type_Obj();
        int type_id = ingredient_Type_ID_Obj.get_ID();
        
        // Check IF Map Contains this Ingredient Type
        if (! ingredient_Types_To_Names_Map.containsKey(type_id)) { return false; }
        
        // Get Ingredients Names
        ArrayList<Ingredient_Name_ID_OBJ> ingredient_Names = ingredient_Types_To_Names_Map.get(type_id);
        
        // Remove Ingredients Name From Type List
        ingredient_Names.remove(ingredient_Name_ID_Obj);
        
        // If Ingredients Names is empty remove the whole list
        if (ingredient_Names.isEmpty())
        {
            remove_Mapped_Ingredient_Type(ingredient_Type_ID_Obj);
        }
        
        //###################################
        // Remove From Map & AL
        //###################################
        return remove_ID_Object(ingredient_Name_ID_Obj, ingredient_Names_Obj_AL, ingredient_Names_Obj_Map);
    }
    
    public Ingredient_Name_ID_OBJ get_Ingredient_Name_ID_OBJ_By_ID(int id)
    {
        return ingredient_Names_Obj_Map.get(id);
    }
    
    //##############################################################
    // Ingredient Types - Singular List
    //##############################################################
    public void add_Ingredient_Type(Ingredient_Type_ID_OBJ ingredient_type_Obj, boolean sort)
    {
        add_ID_Object(ingredient_type_Obj, all_Ingredient_Types_Obj_AL, sort, ingredient_Types_Obj_Map);
    }
    
    public boolean remove_Ingredient_Type(Ingredient_Type_ID_OBJ from_Type_Id_Obj)
    {
        // Get 'Un-Assigned' Type
        Ingredient_Type_ID_OBJ to_Type_ID = ingredient_Types_Obj_Map.get(2);
        
        // Move & Delete From Ingredient Type to Un-Assigned
        move_Ingredients_In_Bulk_To_Type(from_Type_Id_Obj, to_Type_ID);
        
        // Remove From Collections
        return remove_ID_Object(from_Type_Id_Obj, all_Ingredient_Types_Obj_AL, ingredient_Types_Obj_Map);
    }
    
    public Ingredient_Type_ID_OBJ get_Type_ID_Obj_By_ID(int id)
    {
        return ingredient_Types_Obj_Map.get(id);
    }
    
    //#################################################################
    // Stores
    //#################################################################
    public void add_Store(Store_ID_OBJ store_ID_Obj, boolean sort)
    {
        add_ID_Object(store_ID_Obj, stores_Obj_AL, sort, stores_Obj_Map);
    }
    
    public boolean remove_Store(Store_ID_OBJ store_ID_Obj)
    {
        return remove_ID_Object(store_ID_Obj, stores_Obj_AL, stores_Obj_Map);
    }
    
    public Store_ID_OBJ get_Store_ID_Obj(int id)
    {
        return stores_Obj_Map.get(id);
    }
    
    
    //#################################################################
    // Measurement Material Type
    //#################################################################
    public void add_Measurement_Material_Type(Measurement_Material_Type_ID_OBJ measurement_material_id_obj, boolean sort)
    {
        add_ID_Object(measurement_material_id_obj, measurement_Material_Type_Obj_AL, sort, measurement_Material_Type_Obj_Map);
    }
    
    public Measurement_Material_Type_ID_OBJ get_Measurement_Material_Type_ID_OBJ(int id)
    {
        return measurement_Material_Type_Obj_Map.get(id);
    }
    
    //#################################################################
    // Measurement
    //#################################################################
    public void add_Measurement(Measurement_ID_OBJ measurement_id_obj, boolean sort)
    {
        add_ID_Object(measurement_id_obj, ingredient_Measurement_Obj_AL, sort, ingredient_Measurement_Obj_Map);
    }
    
    public Measurement_ID_OBJ get_Measurement_ID_Obj(int id)
    {
        return ingredient_Measurement_Obj_Map.get(id);
    }
    
    //##############################################################################################
    // Accessor Methods
    //###############################################################################################
    // Stores
    public ArrayList<Store_ID_OBJ> get_Stores_AL()
    {
        return stores_Obj_AL;
    }
    
    // Measurements
    public ArrayList<Measurement_ID_OBJ> get_Ingredient_Measurement_Obj_AL()
    {
        return ingredient_Measurement_Obj_AL;
    }
    
    //##########################
    // Types
    //##########################
    public ArrayList<Ingredient_Type_ID_OBJ> get_All_Ingredient_Types_AL()
    {
        return all_Ingredient_Types_Obj_AL;
    }
    
    public ArrayList<Ingredient_Type_ID_OBJ> get_Mapped_Ingredient_Types() { return ingredient_Types_With_Ingredients_AL; }
    
    public ArrayList<Ingredient_Name_ID_OBJ> get_Ingredient_Names_From_Type_AL(Integer ingredient_Type_ID)
    {
        return ingredient_Types_To_Names_Map.get(ingredient_Type_ID);
    }
    
    //##################################################################################################################
    // Mutator Methods
    //##################################################################################################################
    public void set_Plan_Name(String plan_name)
    {
        this.plan_Name = plan_name;
    }
    
    public void set_Selected_Plan_ID(int selected_plan_id)
    {
        this.selected_plan_id = selected_plan_id;
    }
    
    public void set_Selected_Plan_Version_ID(Integer selected_plan_version_id)
    {
        this.selected_plan_version_id = selected_plan_version_id;
    }
    
    public void set_User_ID(Integer user_id)
    {
        this.user_id = user_id;
    }
    
    public void set_NA_Ingredient_ID(int ingredient_id)
    {
        na_ingredient_id = ingredient_id;
    }
    
    public void set_NA_Ingredient_PDID(int pdid)
    {
        na_pdid = pdid;
    }
    
    //##################################################################################################################
    // Accessor Methods
    //##################################################################################################################
    
    // String
    public String get_Plan_Name()
    {
        return plan_Name;
    }
    
    //########################
    // Integers
    //########################
    public int get_Active_MealCount()
    {
        return (int) mealManager_ArrayList.stream().filter(mealManager -> ! mealManager.is_Meal_Deleted()).count();
    }
    
    public Integer get_Selected_Plan_ID()
    {
        return selected_plan_id;
    }
    
    public Integer get_Selected_Plan_Version_ID()
    {
        return selected_plan_version_id;
    }
    
    public Integer get_User_ID()
    {
        return user_id;
    }
    
    public int get_Na_Ingredient_ID()
    {
        return na_ingredient_id;
    }
    
    public int get_NA_PDID()
    {
        return na_pdid;
    }
    
    //########################
    // BigDecimal
    //########################
    public BigDecimal get_Meal_Macro_Value(MealManager mealManager, Total_Meal_Macro_Columns macro_name)
    {
        EnumMap<Total_Meal_Macro_Columns, BigDecimal> outer = totals_by_meal.get(mealManager);
        return outer != null ? outer.get(macro_name) : null;
    }
    
    //###########################################################
    // Collections
    //###########################################################
    public LinkedHashMap<Total_Meal_Macro_Columns, String> get_Total_Meal_Macro_Symbols()
    {
        return total_meal_macro_symbol;
    }
    
    public HashMap<Ingredients_Table_Columns, Integer> get_Ingredients_Table_Cols_Positions()
    {
        return ingredients_table_cols_positions;
    }
    
    //############################
    // MealManagers / TotalMeal
    //#############################
    public ArrayList<MealManager> get_MealManager_ArrayList()
    {
        return mealManager_ArrayList;
    }
    
    public LinkedHashMap<Total_Meal_Macro_Columns, HashMap<MealManager, BigDecimal>> get_MealManagers_MacroValues()
    {
        return totals_by_macro;
    }
}
