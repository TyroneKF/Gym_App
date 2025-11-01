package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info;

import java.text.Collator;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Gui_Objects.Screens.Screen_JFrame;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Add_Ingredients_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Edit_Ingredients.Edit_Ingredients_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Stores.Ingredient_Stores_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Ingredient_Types.Ingredients_Types_Screen;
import App_Code.Objects.Screens.Meal_Plan_Screen;

import javax.swing.*;
import java.awt.*;
import java.util.*;


public class Ingredients_Info_Screen extends Screen_JFrame
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    
    // Objects
    private Meal_Plan_Screen mealPlanScreen;
    
    // Screen Objects
    private Add_Ingredients_Screen add_IngredientsInfo_Screen;
    private Edit_Ingredients_Screen edit_IngredientsInfo_Screen;
    
    // Int
    private int
            tempPlanID, planID;
    
    // String
    private String planName;
    
    // Booleans
    private boolean
            updateIngredientInfo = false;
    
    //#######################################
    // Collections
    //#######################################
    // Ordered Collections applied from once it's released from MYJDC LVL
    private Collection<String> ingredientsTypesList, storesNamesList;
    
    // Sorted Hashmap by key String
    private TreeMap<String, TreeSet<String>> map_ingredientTypesToNames = new TreeMap<String, TreeSet<String>>(new Comparator<String>()
    {
        public int compare(String o1, String o2)
        {
            return o1.toLowerCase().compareTo(o2.toLowerCase());
        }
    });
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Ingredients_Info_Screen(MyJDBC db, Meal_Plan_Screen mealPlanScreen)
    {
        //###################################################################################
        // Super Constructor
        //###################################################################################
        super(db, false, "Add/Edit Ingredients Screen", 800, 880, 0, 0);
        
        //###################################################################################
        // Variables
        //###################################################################################
        // Objects
        this.mealPlanScreen = mealPlanScreen;
        
        // Integer
        planID = mealPlanScreen.getPlanID();
        tempPlanID = mealPlanScreen.getTempPlanID();
        
        // String
        planName = mealPlanScreen.getPlanName();
        
        // Collections
        this.map_ingredientTypesToNames = mealPlanScreen.getMap_ingredientTypesToNames();
        this.ingredientsTypesList =  mealPlanScreen.get_IngredientsTypes_List();
        this.storesNamesList = mealPlanScreen.get_StoresNames_List();
        
        //###################################################################################
        // Check DB Connection
        //###################################################################################
        if (! db.is_DB_Connected())
        {
            JOptionPane.showMessageDialog(null, "Error bringing up screen to edit ingredients! Database Not Connected");
            return;
        }
        
        //###################################################################################
        // Frame Set-Up
        //###################################################################################
        set_Resizable(false);
        
        //###################################################################################
        // Create ContentPane
        //###################################################################################
        getScrollPaneJPanel().setLayout(new GridLayout(1, 1));
        
        //##################################################################################
        // Creating TabbedPane
        //##################################################################################
        JTabbedPane tp = new JTabbedPane();
        getScrollPaneJPanel().add(tp);
        
        //#################################################
        // Creating Add Ingredients Screen
        //#################################################
        add_IngredientsInfo_Screen = new Add_Ingredients_Screen(this, db);
        tp.add("Add Ingredients", add_IngredientsInfo_Screen);
        
        //#################################################
        // Creating Edit Ingredients Screen
        //##################################################
        edit_IngredientsInfo_Screen = new Edit_Ingredients_Screen(this, db);
        tp.add("Edit Ingredients", edit_IngredientsInfo_Screen);
        
        //#################################################
        // Creating Edit Ingredient Types Screen
        //##################################################
        Ingredients_Types_Screen edit_IngredientTypes = new Ingredients_Types_Screen(db, this, ingredientsTypesList);
        tp.add("Edit Ingredient Types", edit_IngredientTypes);
        
        //#################################################
        // Creating Edit Ingredients Stores Screen
        //##################################################
        Ingredient_Stores_Screen edit_Stores_Screen = new Ingredient_Stores_Screen(db, this, storesNamesList);
        tp.add("Edit Ingredient Stores", edit_Stores_Screen);
        
        // ################################################################
        // Make Frame Visible
        // ################################################################
        setFrameVisibility(true);
        resizeGUI();
    }
    
    @Override
    public void window_Closed_Event()
    {
        mealPlanScreen.remove_Ingredients_Info_Screen();
        mealPlanScreen.updateIngredientsNameAndTypesInJTables(updateIngredientInfo);
        closeJFrame();
    }
    
    //##################################################################################################################
    // Update Methods
    //##################################################################################################################
    public void update_IngredientsForm_Type_JComboBoxes()
    {
        add_IngredientsInfo_Screen.update_IngredientForm_Type_JComboBox();
        edit_IngredientsInfo_Screen.update_IngredientForm_Type_JComboBox();
    }
    
    public void update_Ingredient_Suppliers_JComboBoxes()
    {
        add_IngredientsInfo_Screen.clear_ShopForm();
        edit_IngredientsInfo_Screen.refresh_Interface(true, true);
    }
    
    public void update_EditIngredientsInfo_IngredientsTypes()
    {
        edit_IngredientsInfo_Screen.update_IngredientsType_JComboBox();
    }
    
    //##################################################################################################################
    // Mutator Methods
    //##################################################################################################################
    
    // Ingredient Types
    public boolean add_Change_Or_Remove_IngredientsTypeName(String process, String newKey, String oldKey)
    {
        if (process.equals("addKey"))
        {
            return edit_IngredientsType_List(process, newKey, null);
        }
        else if (map_ingredientTypesToNames.containsKey(oldKey)) // if the key had no ingredientNames attached to it, do nothing
        {
            if (process.equals("removeKey"))
            {
                String unAssignedKey = "UnAssigned";
                
                Collection<String> oldKeyListData = map_ingredientTypesToNames.remove(oldKey); // get the old ingredientNames associated with the old key
                
                if (! map_ingredientTypesToNames.containsKey(unAssignedKey)) // add UnAssigned as key if it doesn't exist
                {
                    map_ingredientTypesToNames.put(unAssignedKey, new TreeSet<String>(Collator.getInstance()));
                }
                
                // Get Unassigned List Data
                TreeSet<String> unassignedKeyListData = map_ingredientTypesToNames.remove(unAssignedKey); // get ingredientNames assigned with the key unassigned
                
                // Add OldKey IngredientNames to Unassigned List
                unassignedKeyListData.addAll(oldKeyListData);
                
                //  Add Unassigned list back to the map
                map_ingredientTypesToNames.put(unAssignedKey, unassignedKeyListData);
            }
            else if (process.equals("changeKeyName"))
            {
                if (map_ingredientTypesToNames.containsKey(oldKey))
                {
                    map_ingredientTypesToNames.put(newKey, map_ingredientTypesToNames.remove(oldKey));
                }
            }
        }
        
        return edit_IngredientsType_List(process, newKey, oldKey);
    }
    
    private boolean edit_IngredientsType_List(String process, String newKey, String oldKey)
    {
        //#########################################
        // addKey
        //#########################################
        if (process.equals("addKey"))
        {
            ingredientsTypesList.add(newKey);
            return true;
        }
        
        //#########################################
        // Check if oldKey exists if not error
        //#########################################
        if (oldKey == null || ! (ingredientsTypesList.contains(oldKey)))
        {
            System.out.print("\n\nError1");
            return false;
        }
        
        //#########################################
        // removeKey / changeKey
        //#########################################
        if (process.equals("removeKey"))
        {
            ingredientsTypesList.remove(oldKey);
        }
        else if (process.equals("changeKeyName"))
        {
            ingredientsTypesList.remove(oldKey);
            ingredientsTypesList.add(newKey);
        }
        
        //#########################################
        // removeKey / changeKey
        //#########################################
        
        return true;
    }
    
    //
    public void add_Or_Remove_Supplier_From_List(String process, String newKey, String oldKey)
    {
        // Validate input value
        Set<String> processes = Set.of("add", "replace", "delete");
        assert processes.contains(process) : String.format("addOrRemoveIngredientsTypeNameFromList() Invalid process: %s", process);
        
        // Add Supplier
        if (process.equals("add"))
        {
            storesNamesList.add(newKey);
            return;
        }
        
        // Processes left are delete & Replace Suppliers which both involve deleting oldKey
        storesNamesList.remove(oldKey); // Delete suppliers
        
        if (process.equals("delete")) { return; } // Nothing left to do
        
        // only process left is replace which is only to add the newKey
        storesNamesList.add(newKey);
    }
    
    public void set_Update_IngredientInfo(boolean status)
    {
        updateIngredientInfo = status;
    }
    
    //##################################################################################################################
    // Accessor Methods (Get) Methods
    //##################################################################################################################
    
    // Objects
    public Edit_Ingredients_Screen get_Edit_Ingredients_Form()
    {
        return edit_IngredientsInfo_Screen;
    }
    
    public Meal_Plan_Screen get_MealPlan_Screen()
    {
        return mealPlanScreen;
    }
    
    //#############################################
    // Collections
    //#############################################
    public TreeMap<String, TreeSet<String>> get_Map_IngredientTypes_To_Names()
    {
        return map_ingredientTypesToNames;
    }
    
    public Collection<String> get_IngredientsTypes_List()
    {
        return ingredientsTypesList;
    }
    
    public Collection<String> get_StoresNames_List()
    {
        return storesNamesList;
    }
    
    //############################################
    // Integer
    //############################################
    public Integer get_TempPlanID()
    {
        return tempPlanID;
    }
    
    public Integer get_PlanID()
    {
        return planID;
    }
    
    //############################################
    // String
    //############################################
    public String get_PlanName()
    {
        return planName;
    }
}
