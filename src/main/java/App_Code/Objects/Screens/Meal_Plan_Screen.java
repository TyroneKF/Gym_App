package App_Code.Objects.Screens;

import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Ingredient_Name_ID_OBJ;
import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Ingredient_Type_ID_OBJ;
import App_Code.Objects.Data_Objects.ID_Objects.MetaData_ID_Object.Meal_ID;
import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Measurement_ID_OBJ;
import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Store_ID_OBJ;
import App_Code.Objects.Database_Objects.MyJDBC.MyJDBC_Sqlite;
import App_Code.Objects.Database_Objects.Shared_Data_Registry;
import App_Code.Objects.Tables.JTable_JDBC.Children.View_Data_Tables.Children.MacrosLeft_Table;
import App_Code.Objects.Tables.JTable_JDBC.Children.View_Data_Tables.Children.MacrosTargets_Table;
import App_Code.Objects.Tables.MealManager;
import App_Code.Objects.Gui_Objects.*;
import App_Code.Objects.Gui_Objects.Screens.Screen_JFrame;
import App_Code.Objects.Screens.Graph_Screens.LineChart_Meal_Plan_Screen.LineChart_MPS;
import App_Code.Objects.Screens.Graph_Screens.PieChart_Meal_Plan_Screen.PieChart_Screen_MPS;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Ingredients_Info.Ingredients_Info_Screen;
import App_Code.Objects.Screens.Loading_Screen.Loading_Screen;
import App_Code.Objects.Screens.Others.Macros_Targets_Screen;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.javatuples.Pair;
import org.jfree.data.time.Second;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;

public class Meal_Plan_Screen extends Screen_JFrame
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    // Strings
    private String plan_Name;
    
    //#################################################
    // Objects
    //#################################################
    // DATA Object
    private final Shared_Data_Registry shared_Data_Registry;
    
    // JPanels
    private JPanel scroll_JPanel_Center;
    
    // Table Objects
    private MacrosLeft_Table macros_Left_JTable;
    private MacrosTargets_Table macros_Targets_Table;
    
    // Screen Objects
    private Macros_Targets_Screen macros_Targets_Screen = null;
    private Ingredients_Info_Screen ingredients_Info_Screen = null;
    
    private PieChart_Screen_MPS pieChart_Screen_MPS = null;
    private LineChart_MPS lineChart_MPS = null;
   
    //###############################################
    // Integers
    //###############################################
    private static Integer user_id;
    private Integer temp_Plan_ID;
    private Integer selected_Plan_ID;
    private Integer selected_Plan_Version_ID;
    
    private Integer no_of_meals;
    private Integer no_of_sub_meals;
    
    //###############################################
    // Booleans
    //###############################################
    private boolean macro_Targets_Changed = false;
    private Boolean screen_Created = false;
    
    //###############################################
    // Collections
    //###############################################
    private ArrayList<String> meal_total_column_Names;
    private ArrayList<String> ingredients_Column_Names;
    private ArrayList<String> macrosLeft_columnNames;
    private ArrayList<String> macroTargets_ColumnNames;
    
    private ArrayList<ArrayList<Object>> macros_plan_Data;
    private ArrayList<ArrayList<Object>> macrosData;
    
    //########################
    // Meals Data Collections
    //########################
    /**
     * LinkedHashMap<Integer, Meal_ID> meals_Data = new LinkedHashMap<>();
     * LinkedHashMap<Meal_ID, Meal_ID> meals_Data
     * <p>
     * Meal_ID = Meal ID / Name/ Time inside Object
     */
    private LinkedHashMap<Integer, Meal_ID> meals_Data_Map = new LinkedHashMap<>();
    
    /**
     * HashMap<Integer, HashMap<Integer, ArrayList<ArrayList<Object>>>> sub_Meals_Data = new HashMap<>();
     * HashMap<Meal_ID [Meal_ID, Meal_Time, Meal_Name], HashMap<Div_ID, List Of Ingredients>
     */
    private LinkedHashMap<Integer, LinkedHashMap<Integer, ArrayList<ArrayList<Object>>>> sub_Meals_Data_Map = new LinkedHashMap<>();
    
    private final LinkedHashMap<Integer, ArrayList<Object>> total_Meals_Data_Map = new LinkedHashMap<>();
    
    //##################################################
    // Database Table Names
    //##################################################
    private final static String
            db_Scripts_Folder_Path = "/data/database_scripts",
            db_File_Script_List_Name = "0.) Script_List.txt",
    
    // Table Names Frequently Used
    table_Plans_Name = "plans",
            table_name_active_plans = "active_plans",
            table_Plans_Version_Name = "plan_versions",
            table_Macros_Per_Pound_Limit_Name = "macros_per_pound_and_limits_versions",
            table_Plan_Macro_Targets_Name_Calc = "plan_macro_target_calculations",
            table_Ingredients_Info_Name = "ingredients_info",
            table_Ingredients_Type_Name = "ingredient_types",
            table_Stores_Name = "stores",
    
    table_Meals_In_Plan_Name = "meals_in_plan",
            table_Sub_MealsName = "divided_meal_sections_versions",
            table_Ingredients_In_Meal_Sections = "ingredients_in_sections_of_meal_versions",
            table_Ingredients_Calculation_Name = "ingredients_in_sections_of_meal_calculation_gui",
            table_Total_Meals_Table_Name = "total_meal_view",
            table_Total_Plan_Table_Name = "total_plan_view",
            table_Plan_Macros_Left_Name = "plan_macros_left";
    
    //##################################################################################################################
    // Ingredients Table Columns
    //##################################################################################################################
    // Table: ingredients_in_sections_of_meal_calculation
    private final ArrayList<String> ingredients_Table_Col_Avoid_Centering = new ArrayList<>(Arrays.asList(
            "ingredient_type", "ingredient_name"));
    
    private final ArrayList<String> ingredients_Table_Un_Editable_Cells = new ArrayList<>(Arrays.asList(
            "ingredients_index", "protein", "gi", "carbohydrates", "sugars_of_carbs",
            "fibre", "fat", "saturated_fat", "salt", "water_content", "calories"
    ));
    
    private final ArrayList<String> ingredients_In_Meal_Table_Col_To_Hide = new ArrayList<>(Arrays.asList(
            "plan_id", "div_meal_sections_id", "ingredients_index", "water_content"
    ));
    
    //##################################################################################################################
    // TotalMealView Table
    //##################################################################################################################
    private final ArrayList<String> total_Meal_Table_Col_To_Hide = new ArrayList<String>(Arrays.asList(
            "plan_id", "meal_name", "meal_in_plan_id"
    ));
    
    /**
     * LinkedHashMap<String, Pair<Integer, String>> totalMeal_macroColNamePos
     * LinkedHashMap<TotalMeal_MacroName, Pair< Position, Measurement>> totalMeal_macroColNamePos
     */
    private final LinkedHashMap<String, Pair<Integer, String>> total_Meal_Macro_Col_Name_And_Positions = new LinkedHashMap<>()
    {{
        put("total_protein", new Pair<>(null, "g"));
        put("total_carbohydrates", new Pair<>(null, "g"));
        put("total_sugars_of_carbs", new Pair<>(null, "g"));
        put("total_fats", new Pair<>(null, "g"));
        put("total_saturated_fat", new Pair<>(null, "g"));
        put("total_salt", new Pair<>(null, "g"));
        put("total_fibre", new Pair<>(null, "g"));
        put("total_water", new Pair<>(null, "ml"));
        put("total_calories", new Pair<>(null, "kcal"));
    }};
    
    private final HashMap<String, Integer> total_Meal_Other_Cols_Positions = new HashMap<>()
    {{
        put("meal_time", null);
        put("meal_name", null);
    }};
    
    //##################################################################################################################
    // Other Table Customisations
    //##################################################################################################################
    // Table : plan_macro_target_calculations
    private final ArrayList<String> macros_Targets_Table_Col_To_Hide = new ArrayList<String>(Arrays.asList(
            "plan_id", "plan_name", "date_time_of_creation"
    ));
    
    // Table : plan_macros_left
    private final ArrayList<String> macros_Left_Table_Col_To_Hide = new ArrayList<String>(Arrays.asList("plan_id", "plan_name"));
    
    //##################################################################################################################
    // Constructor & Main
    //##################################################################################################################
    public static void main(String[] args)
    {
        //###################################################
        // Create DB Object & run SQL Script
        //####################################################
        MyJDBC_Sqlite db = new MyJDBC_Sqlite();
        
        try
        {
            db.begin_migration();
            if (! db.get_DB_Connection_Status()) { throw new Exception("Failed Initialization!"); }
        }
        catch (Exception e)
        {
            System.err.printf("\n\n %s", e);
            JOptionPane.showMessageDialog(null, "ERROR, Cannot Connect To Database!");
            return;
        }
        
        new Meal_Plan_Screen(db);
    }
    
    public Meal_Plan_Screen(MyJDBC_Sqlite db_Sqlite)
    {
        //###############################################################################
        // Super / Variables
        //###############################################################################
        super(db_Sqlite, true, "Gym App", 1925, 1082, 1300, 0);
        
        shared_Data_Registry = new Shared_Data_Registry(this);
        
        Loading_Screen loading_Screen = new Loading_Screen(100);
        
        UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 16)); // Set up window msg font
        
        //###############################################################################
        //
        //###############################################################################
        /**
         *  1.) Getting Selected User Info & Their Active Plan Info
         *  2.) Getting Table Column Names
         *  3.) Getting Number Of Meals & Sub meals Count for Active Plan         *
         *  4.) Transferring PLan Data To Temp Plan_Data
         *
         *  5.) Main GUI Setup (excluding adding meals)
         *  6.) MacroTargets Setup
         *  7.) MacrosLeft Setup
         *
         *  1.) Transfer Plan Data
         *  2.) Transfer Plan Targets
         *  3.) Transferring Meals Data
         *  4.) Get Ingredient Names & Types
         *  5.) Main GUI Setup (excluding adding meals)
         *  6.) MacroTargets Setup
         *  7.) MacrosLeft Setup
         */
        
        //####################################################
        // Transferring PLan Data To Temp
        //####################################################
        // 1.) Getting Selected User Info & Their Active Plan Info
        if (! setup_Get_User_And_Plan_Info()) { failed_Start_UP(loading_Screen); return; }
        
        // 2.) Getting Table Column Names
        if (! setup_Get_Column_Names()) { failed_Start_UP(loading_Screen); return; }
        
        // 3.) Getting Number Of Meals & Sub meals Count
        if (! setup_Get_Meal_Counts()) { failed_Start_UP(loading_Screen); return; }
        
        //####################################################
        // Transferring PLan Data To Temp
        //####################################################
        if (! transfer_Plan_Data(selected_Plan_Version_ID)) { failed_Start_UP(loading_Screen); return; }
        
        loading_Screen.increaseBar(10);
        System.out.printf("\nChosen Plan: %s  & Chosen Plan Name: %s \n\n%s", selected_Plan_Version_ID, plan_Name, lineSeparator);
        
        //####################################################
        // Transferring Targets From Chosen PLan to Temp
        //####################################################
        if (! transfer_Targets(selected_Plan_Version_ID, temp_Plan_ID, true, false))
        {
            failed_Start_UP(loading_Screen); return;
        }
        
        loading_Screen.increaseBar(10);
        
        //####################################################
        // Transferring this plans Meals  Info to Temp-Plan
        //####################################################
        if (! (transfer_Meal_Ingredients(selected_Plan_Version_ID, temp_Plan_ID)))
        {
            failed_Start_UP(loading_Screen); return;
        }
        
        loading_Screen.increaseBar(10);
        
        //####################################################
        // Get DATA Methods
        //####################################################
        // Get Ingredient Types Mapped to Ingredient Names
        if (! setup_Get_Ingredient_Types_And_Ingredient_Names()) { failed_Start_UP(loading_Screen); return; }
        
        // Get Stores DATA
        if (! setup_Get_Stores_Data()) { failed_Start_UP(loading_Screen); return; }
        
        // Get Measurement DATA
        if (! setup_Get_Measurement_Data()) { failed_Start_UP(loading_Screen); return; }
        
        //####################################################
        // Get DATA Methods
        //####################################################
        // Get Meals Data
        if (! setup_Get_Meal_Data()) { failed_Start_UP(loading_Screen); return; }
        
        // Get TotalMeals Data
        if (! setup_Get_Total_Meals_Data()) { failed_Start_UP(loading_Screen); return; }
        
        loading_Screen.increaseBar(10);
        
        // Get MacroTargets DATA
        if (! setup_Get_Macros_Targets_Data()) { failed_Start_UP(loading_Screen); return; }
        
        // Get MacrosLeft DATA
        if (! setup_Get_Macros_Left_Data()) { failed_Start_UP(loading_Screen); return; }
        
        //###############################################################################
        // Build GUI
        //###############################################################################
        System.out.printf("\nMeal_Plan_Screen.java : Creating GUI Screen \n%s", lineSeparator); // Update
        
        //##############################
        // Splitting Scroll JPanel
        //##############################
        scroll_JPanel_Center = new JPanel(new GridBagLayout());
        addToContainer(getScrollPaneJPanel(), scroll_JPanel_Center, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0, "center");
        
        JPanel scrollJPanelBottom = new JPanel(new GridBagLayout());
        addToContainer(getScrollPaneJPanel(), scrollJPanelBottom, 0, 1, 1, 1, 0.25, 0.25, "both", 0, 0, "end");
        
        //###################################
        // Increase Progress
        //###################################
        loading_Screen.increaseBar(10);
        
        //####################################################
        // North :  JPanel
        //####################################################
        iconSetup(getMainNorthPanel()); // Icon Setup
        
        //#####################################################
        // Bottom : JPanel
        //#####################################################
        JPanel macrosInfoJPanel = new JPanel(new GridBagLayout());   // Add Bottom JPanel to GUI
        addToContainer(scrollJPanelBottom, macrosInfoJPanel, 0, 0, 1, 1, 0.25, 0.25, "horizontal", 0, 0, "end");
        
        int macrosInfoJP_YPos = 0;

        /*//###################################
        // Setting up Horizontal Image Divider
        //#####################################
        int height = 75, width = 0;
        JPanel macrosDividerJPanel = new JPanel(new GridLayout(1, 1));
        macrosDividerJPanel.setPreferredSize(new Dimension(width, height));

        // Border Line Config
        BevelBorder borderLine = new BevelBorder(BevelBorder.LOWERED);  // Create a red line border
        LineBorder redLine = new LineBorder(Color.RED, 2); // 2px thick red border
        CompoundBorder compoundBorder = new CompoundBorder(redLine, borderLine); // Combine them: outer = red line, inner = raised bevel
        macrosDividerJPanel.setBorder(compoundBorder); // Set Border

        // Add image
        URL imageUrl = getClass().getResource("/images/border/border_divider/border_divider4.jpg");
        ImageIcon originalIcon = new ImageIcon(imageUrl);
        Image img = originalIcon.getImage();
        Image scaledImg = img.getScaledInstance(450, 200, Image.SCALE_SMOOTH); // W: 1925
        ImageIcon scaledIcon = new ImageIcon(scaledImg);
        JLabel label = new JLabel(scaledIcon);

        addToContainer(macrosDividerJPanel, label, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0, null);

        addToContainer(macrosInfoJPanel, macrosDividerJPanel, 0, macrosInfoJP_YPos += 1, 1, 1, 0.25, 0.25, "both", 0, 0, null);

        //##########################################
        // Add Space Divider
        //##########################################

        addToContainer(macrosInfoJPanel, createSpaceDivider(0, 20), 0, macrosInfoJP_YPos += 1, 1, 1, 0.25, 0.25, "both", 0, 0, null);
*/
        
        //############################
        // MacroTargets Table
        //############################
        macros_Targets_Table = new MacrosTargets_Table(
                db,
                macrosInfoJPanel,
                macros_plan_Data,
                macroTargets_ColumnNames,
                selected_Plan_Version_ID,
                temp_Plan_ID,
                null,
                macros_Targets_Table_Col_To_Hide
        );
        
        addToContainer(macrosInfoJPanel, macros_Targets_Table, 0, macrosInfoJP_YPos += 1, + 1, 1, 0.25, 0.25, "both", 40, 0, null);
        
        loading_Screen.increaseBar(10);
        
        //############################
        // plan_Macros_Left Table
        //############################
        macros_Left_JTable = new MacrosLeft_Table(
                db,
                macrosInfoJPanel,
                macrosData,
                macrosLeft_columnNames,
                selected_Plan_Version_ID,
                temp_Plan_ID,
                null,
                macros_Left_Table_Col_To_Hide
        );
        
        addToContainer(macrosInfoJPanel, macros_Left_JTable, 0, macrosInfoJP_YPos += 1, 1, 1, 0.25, 0.25, "both", 30, 0, null);
        
        loading_Screen.increaseBar(10);
        
        //####################################################
        // Centre : JPanel (Meals)
        //####################################################
        /**
         * LinkedHashMap<Integer, Meal_ID> meals_Data = new LinkedHashMap<>();
         * LinkedHashMap<Meal_ID, Meal_ID> meals_Data
         *
         * Meal_ID = Meal ID / Name/ Time inside Object
         *
         *  HashMap<Integer, HashMap<Integer, ArrayList<ArrayList<Object>>>> sub_Meals_Data = new HashMap<>();
         *  HashMap<Meal_ID [Meal_ID, Meal_Time, Meal_Name], HashMap<Div_ID, List Of Ingredients>
         */
        for (Map.Entry<Integer, Meal_ID> meal_Entry : meals_Data_Map.entrySet())
        {
            // Get Meal ID
            int meal_ID = meal_Entry.getKey();
            
            // Get Meal OBJ Data From Map
            Meal_ID meal_ID_Obj = meals_Data_Map.get(meal_ID);
            
            // Get Associated Sub-Meals
            LinkedHashMap<Integer, ArrayList<ArrayList<Object>>> sub_Meal_DATA = sub_Meals_Data_Map.get(meal_ID);
            
            // Total Meals DATA
            ArrayList<Object> total_Meal_DATA = total_Meals_Data_Map.get(meal_ID);
            
            // Create MealManager
            MealManager mealManager = new MealManager(this, db, macros_Left_JTable, meal_ID_Obj, sub_Meal_DATA, total_Meal_DATA);
            
            // ADD MealManager To Memory
            shared_Data_Registry.addMealManager(mealManager);
            
            // ADD to GUI
            add_And_Replace_MealManger_POS_GUI(mealManager, false, false); // Add to GUI
            
            // Update Progress
            loading_Screen.increaseBar(1 + sub_Meal_DATA.size()); // + original meal + the sub-meal
        }
        
        //###############################################################################
        //
        //###############################################################################
        if (! loading_Screen.isFinished())
        {
            JOptionPane.showMessageDialog(getFrame(), "Error, in configuration! All Tasks Are Not Completed!");
            failed_Start_UP(loading_Screen);
            return;
        }
        
        //##############################################################################
        //
        //##############################################################################
        clear_Setup_Variable_Data();
        
        setFrameVisibility(true);  // Make GUI Visible
        resizeGUI(); // Resize GUi
        scroll_To_Top_of_ScrollPane(); // Scroll to the top of the gui
    }
    
    //##################################################################################################################
    // App Configuration Methods
    //##################################################################################################################
    
    // Failed Startup
    private void failed_Start_UP(Loading_Screen loadingScreen)
    {
        JOptionPane.showMessageDialog(getFrame(), "Failed to Initialize Application!");
        
        try
        {
            Thread.sleep(2000); // wait 2 seconds
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
        
        if (loadingScreen != null) { loadingScreen.window_Closed_Event(); } ;
        window_Closed_Event();
    }
    
    private void clear_Setup_Variable_Data()
    {
        screen_Created = true;
        
        no_of_meals = null;
        no_of_sub_meals = null;
        
        macrosLeft_columnNames = null;
        macroTargets_ColumnNames = null;
        
        macros_plan_Data = null;
        macrosData = null;
        
        meals_Data_Map = null;
        sub_Meals_Data_Map = null;
        total_Meals_Data_Map.clear();
    }
    
    //#################################################
    // Get DATA Methods
    //#################################################
    private boolean setup_Get_User_And_Plan_Info()
    {
        // Variables
        String errorMSG = "Error, Gathering Plan & Personal User Information!";
        
        String queryX = """
                SELECT
                
                	U.user_id,
                
                	PV.plan_version_id,
                
                	P.plan_id,
                	P.plan_name
                
                FROM active_user U
                
                LEFT JOIN active_plans AP
                    ON U.user_id = AP.user_id
                
                LEFT JOIN plan_versions PV
                    ON AP.plan_version_id = PV.plan_version_id
                
                LEFT JOIN plans P
                    ON PV.plan_id = P.plan_id;""";
        
        // Execute
        try
        {
            ArrayList<ArrayList<Object>> db_results = db_Sqlite.get_2D_Query_AL_Object(queryX, null, errorMSG, false);
            
            // App Must assume by default there is a selected user and 1 plan active otherwise this causes an eror
            user_id = (Integer) db_results.getFirst().get(0);
            selected_Plan_Version_ID = (Integer) db_results.getFirst().get(1);
            selected_Plan_ID = (Integer) db_results.getFirst().get(2);
            plan_Name = (String) db_results.getFirst().get(3);
            
            return true;
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s \n%s Error \n%s  \n%s", lineSeparator, get_Class_And_Method_Name(), lineSeparator, e);
            return false;
        }
    }
    
    private boolean setup_Get_Column_Names()
    {
        try
        {
            // column names : ingredients_in_sections_of_meal_calculation
            ingredients_Column_Names = db_Sqlite.get_Column_Names_AL(table_Ingredients_Calculation_Name);
            
            // column names : total_meal_view
            meal_total_column_Names = db_Sqlite.get_Column_Names_AL(table_Total_Meals_Table_Name);
            
            // column names : plan_macro_target_calculations
            macroTargets_ColumnNames = db_Sqlite.get_Column_Names_AL(table_Plan_Macro_Targets_Name_Calc);
            
            // Get table column names for plan_macros_left
            macrosLeft_columnNames = db_Sqlite.get_Column_Names_AL(table_Plan_Macros_Left_Name);
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s \n%s Error \n%s  \n%s", lineSeparator, get_Class_And_Method_Name(), lineSeparator, e);
            return false;
        }
        
        //########################################
        // Column Names : Total_Meal_View
        //########################################
        for (int pos = 0; pos < meal_total_column_Names.size(); pos++)
        {
            String columnName = meal_total_column_Names.get(pos);
            
            if (total_Meal_Macro_Col_Name_And_Positions.containsKey(columnName))
            {
                String symbol = total_Meal_Macro_Col_Name_And_Positions.get(columnName).getValue1();
                total_Meal_Macro_Col_Name_And_Positions.put(columnName, new Pair<>(pos, symbol));
            }
            else if (total_Meal_Other_Cols_Positions.containsKey(columnName))
            {
                total_Meal_Other_Cols_Positions.put(columnName, pos);
            }
        }
        
        //########################################
        // Output
        //########################################
        return true;
    }
    
    //#######################
    // MetData
    //#######################
    public boolean setup_Get_Stores_Data()
    {
        //#######################################
        // Create Get Query Results
        //#######################################
        String
                errorMSG = "Error, Unable to get Ingredient Stores in Plan!",
                query = String.format("SELECT store_id, store_name FROM %s ORDER BY store_name ASC;", table_Stores_Name);
        
        //#######################################
        // Execute Query
        //#######################################
        ArrayList<ArrayList<Object>> results;
        try
        {
            results = db.get_2D_Query_AL_Object(query, null, errorMSG, false);
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s \n%s Error \n%s  \n%s", lineSeparator, get_Class_And_Method_Name(), lineSeparator, e);
            return false;
        }
        
        //#######################################
        // Process Data
        //#######################################
        for (ArrayList<Object> row : results)
        {
            // Add to DATA
            shared_Data_Registry.add_Store(new Store_ID_OBJ((int) row.get(0), (String) row.get(1)), false);
        }
        
        //#######################################
        // Output
        //#######################################
        return true;
    }
    
    public boolean setup_Get_Ingredient_Types_And_Ingredient_Names()
    {
        String methodName = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName());
        
        //#######################################
        // Create Get Query Results
        //#######################################
        String query = """
                SELECT
                
                    T.ingredient_type_id AS type_id,
                	T.ingredient_type_name AS type_name,
                
                	JSON_ARRAYAGG(
                        JSON_OBJECT('id', I.ingredient_id,
                            'name', I.ingredient_name
                        )
                    ) AS matched_ingredients
                
                FROM  ingredient_types T
                LEFT JOIN  ingredients_info I ON T.ingredient_type_id = I.ingredient_type_id
                
                GROUP BY T.ingredient_type_id, T.ingredient_type_name
                ORDER BY T.ingredient_type_name ASC;""";
        
        String errorMSG = "Unable to get Ingredient Types & Ingredient Names";
        
        //#######################################
        // Execute Query
        //#######################################
        ArrayList<ArrayList<Object>> results;
        
        try
        {
            results = db.get_2D_Query_AL_Object(query, null, errorMSG, false);
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s \n%s Error \n%s  \n%s", lineSeparator, get_Class_And_Method_Name(), lineSeparator, e);
            return false;
        }
        
        //#######################################
        // Go through Results
        //#######################################
        ObjectMapper mapper = new ObjectMapper();
        HashMap<Ingredient_Type_ID_OBJ, ArrayList<Ingredient_Name_ID_OBJ>> mapped_Data = new HashMap<>();
        
        try
        {
            for (ArrayList<Object> row : results)
            {
                //#########################
                // Get Info
                //#########################
                int type_ID = (int) row.get(0);
                String type_name = (String) row.get(1);
                
                Ingredient_Type_ID_OBJ type_OBJ = new Ingredient_Type_ID_OBJ(type_ID, type_name);
                
                // Add to DATA
                shared_Data_Registry.add_Ingredient_Type(type_OBJ, false); // Add ingredient Type
                
                //#########################
                // Parsing JSON DATA
                //#########################
                JsonNode json_array = mapper.readTree((String) row.get(2));
                
                for (JsonNode node : json_array) // For loop through each node of Ingredients
                {
                    // Per Object Ingredient Object
                    JsonNode id = node.get("id");
                    JsonNode name = node.get("name");
                    
                    if (id.isNull()) { continue; }  // If values are empty skip
                    
                    // Add Ingredient_Name to DATA IF not NULL
                    Ingredient_Name_ID_OBJ ingredient_Name_ID = new Ingredient_Name_ID_OBJ(id.asInt(), name.asText(), type_OBJ);
                    shared_Data_Registry.add_Ingredient_Name(ingredient_Name_ID, true);
                }
            }
            
            return true;
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s error \n\n%s", methodName, e);
            return false;
        }
    }
    
    public boolean setup_Get_Measurement_Data()
    {
        // Set Variables
        String
                query = "SELECT * FROM measurements ORDER BY unit_name;",
                errorMSG = "Unable, to get Measurments Data";
        
        // Execute Query
        ArrayList<ArrayList<Object>> data;
        
        try
        {
            data = db.get_2D_Query_AL_Object(query, null, errorMSG, false);
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s \n%s Error \n%s  \n%s", lineSeparator, get_Class_And_Method_Name(), lineSeparator, e);
            return false;
        }
        
        // Add Measurement OBJ
        for (ArrayList<Object> row : data)
        {
            int id = (int) row.get(0);
            String unit_Name = (String) row.get(1);
            String unit_Symbol = (String) row.get(2);
            String measured_Material_Type = (String) row.get(3);
            
            shared_Data_Registry.add_Measurement(
                    new Measurement_ID_OBJ(id, unit_Name, unit_Symbol, measured_Material_Type), false
            );
        }
        
        // Return Output
        return true;
    }
    
    //#######################
    // Meals Data
    //#######################
    private boolean setup_Get_Meal_Counts()
    {
        String plan_Counts_ErrorMSG = "Unable to get Meals & Sub-Meals Count!";
        String plan_Counts_Query = """
                WITH
                
                    active_plan_id AS (
                        SELECT plan_version_id FROM active_plans WHERE user_id = ?
                    ),
                
                    count_cte AS (
                
                        SELECT
                
                            P.plan_version_id,
                            COUNT(DISTINCT(M.meal_in_plan_id)) AS total_meals,
                            COUNT(DISTINCT(D.div_meal_sections_id)) AS total_sub_meals
                
                        FROM active_plan_id P
                
                        LEFT JOIN meals_in_plan_versions M
                            ON P.plan_version_id = M.plan_version_id
                
                        LEFT JOIN divided_meal_sections_versions D
                            ON M.meal_in_plan_version_id = D.meal_in_plan_version_id
                
                        GROUP BY P.plan_version_id
                    )
                
                SELECT
                
                    COALESCE(C.total_meals, 0) AS meal_count,
                    COALESCE(C.total_sub_meals, 0) AS sub_count
                
                FROM count_cte C;""";
        
        Object[] counts_Params = new Object[]{ user_id };
        
        ArrayList<ArrayList<Object>> meal_Count_Results;
        
        //#################################
        // Execute Query
        //#################################
        try
        {
            meal_Count_Results = db_Sqlite.get_2D_Query_AL_Object(plan_Counts_Query, counts_Params, plan_Counts_ErrorMSG, false);
            
            // Format Results
            no_of_meals = (Integer) meal_Count_Results.getFirst().get(0);
            no_of_sub_meals = (Integer) meal_Count_Results.getFirst().get(1);
            
            System.out.printf("\n\n%s \nMeals In Plan: %s\nSub-Meals In Plan: %s \n", lineSeparator, no_of_meals, no_of_sub_meals);
            
            return true;  // Execute Query
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s \n%s Error \n%s  \n%s", lineSeparator, get_Class_And_Method_Name(), lineSeparator, e);
            return false;
        }
    }
    
    public boolean setup_Get_Meal_Data()
    {
        String methodName = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName());
        
        //########################################################################
        // Create Get Query Results
        //########################################################################
        String query = """
                -- Divs with Ingredients
                
                SELECT DISTINCT
                	M.plan_id,
                	M.meal_in_plan_id,
                	M.Meal_Name,
                	M.meal_time,
                
                	/*M.div_meal_sections_id,*/
                
                	JSON_ARRAYAGG(
                		JSON_OBJECT(
                			'plan_id',     I.plan_id,
                			'div_id',      I.div_meal_sections_id,
                			'index',       I.ingredients_index,
                			'type_id',     I.ingredient_type_name,
                			'id',          I.ingredient_name,
                			'quantity',    I.quantity,
                			'gi',          I.gi,
                			'protein',     I.protein,
                			'carbs',       I.carbohydrates,
                			'sugar_carbs', I.sugars_of_carbs,
                			'fibre',       I.fibre,
                			'fat',         I.fat,
                			'sat_fat',     I.saturated_fat,
                			'salt',        I.salt,
                			'water',       I.water_content,
                			'calories',    I.calories,
                			'delete_btn',  I.`delete button`
                		)
                	) AS matched_ingredients
                
                FROM meals_in_plan M
                
                LEFT JOIN divided_meal_sections D ON
                	M.plan_id = D.plan_id AND M.meal_in_plan_id = D.meal_in_plan_id
                
                INNER JOIN ingredients_in_sections_of_meal_calculation_gui I ON
                	I.plan_id = D.plan_id AND I.div_meal_sections_id = D.div_meal_sections_id
                
                WHERE M.plan_id = ?
                GROUP BY M.plan_id, M.meal_in_plan_id, M.meal_time  /*, M.div_meal_sections_id*/
                ORDER BY M.meal_time ASC""";
        
        String errorMSG = "Unable to get Ingredient Types & Ingredient Names";
        
        Object[] params = new Object[]{ temp_Plan_ID };
        
        //########################################################################
        // Execute Query
        //########################################################################
        ArrayList<ArrayList<Object>> results;
        
        try
        {
            results = db.get_2D_Query_AL_Object(query, params, errorMSG, false);
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s \n%s Error \n%s  \n%s", lineSeparator, get_Class_And_Method_Name(), lineSeparator, e);
            return false;
        }
        
        //########################################################################
        // Go Through JSON DATA
        //#########################################################################
        /*
         * Meals Collection:
         *
         * LinkedHashMap<Integer, Meal_ID> meals_Data = new LinkedHashMap<>();
         * LinkedHashMap<Meal_ID, Meal_ID> meals_Data
         *
         * Meal_ID = Meal ID / Name/ Time inside Object
         */
        
        /*
         *  Sub-Meals Collection:
         *
         *  HashMap<Integer, HashMap<Integer, ArrayList<ArrayList<Object>>>> sub_Meals_Data = new HashMap<>();
         *  HashMap<Meal_ID [Meal_ID, Meal_Time, Meal_Name], HashMap<Div_ID, List Of Ingredients>
         */
        
        try
        {
            for (ArrayList<Object> row : results) // For LOOP through each Meal
            {
                //######################################################
                // Get Meal Info
                //#####################################################
                int meal_ID = (int) row.get(1);
                String meal_name = (String) row.get(2);
                LocalTime meal_Time = ((java.sql.Time) row.get(3)).toLocalTime();
                
                //############################
                // Add Meal DATA / Collections
                //############################
                
                // Add to Meals Data
                Meal_ID meal_ID_Obj = new Meal_ID(meal_ID, meal_name, meal_Time);
                meals_Data_Map.put(meal_ID, meal_ID_Obj);
                
                // Add to Sub-Meals Data
                LinkedHashMap<Integer, ArrayList<ArrayList<Object>>> div_Meal_Sections = new LinkedHashMap<>();
                sub_Meals_Data_Map.put(meal_ID, div_Meal_Sections); // ADD to memory
                
                //######################################################
                // Parsing JSON DATA - Ingredients in Meals
                //######################################################
                ObjectMapper mapper = new ObjectMapper();
                JsonNode ingredients_json_array = mapper.readTree((String) row.get(4));
                
                //######################################
                // For Each Ingredient
                //######################################
                for (JsonNode ingredient_node : ingredients_json_array) // For loop through each Ingredient belonging to a  Sub-DIV
                {
                    //###########################
                    // Store Ingredients Data
                    //###########################
                    ArrayList<Object> ingredient_macros = new ArrayList<>();
                    
                    //###########################
                    // Get Ingredients DATA
                    //###########################
                    ingredient_macros.add(ingredient_node.get("plan_id").asInt());
                    
                    int div_id = ingredient_node.get("div_id").asInt();
                    ingredient_macros.add(div_id);
                    
                    ingredient_macros.add(ingredient_node.get("index").asInt());
                    
                    // Ingredient Name OBJ
                    int ingredient_Type_ID = ingredient_node.get("type_id").asInt();
                    ingredient_macros.add(shared_Data_Registry.get_Type_ID_Obj_By_ID(ingredient_Type_ID));
                    
                    // Ingredient Name Object
                    int ingredient_Name_ID = ingredient_node.get("id").asInt();
                    ingredient_macros.add(shared_Data_Registry.get_Ingredient_Name_ID_OBJ_By_ID(ingredient_Name_ID));
                    
                    // Quantity
                    ingredient_macros.add(new BigDecimal(ingredient_node.get("quantity").asText()));
                    
                    // Macro Values
                    ingredient_macros.add(ingredient_node.get("gi").asInt());
                    ingredient_macros.add(new BigDecimal(ingredient_node.get("protein").asText()));
                    ingredient_macros.add(new BigDecimal(ingredient_node.get("carbs").asText()));
                    ingredient_macros.add(new BigDecimal(ingredient_node.get("sugar_carbs").asText()));
                    ingredient_macros.add(new BigDecimal(ingredient_node.get("fibre").asText()));
                    ingredient_macros.add(new BigDecimal(ingredient_node.get("fat").asText()));
                    ingredient_macros.add(new BigDecimal(ingredient_node.get("sat_fat").asText()));
                    ingredient_macros.add(new BigDecimal(ingredient_node.get("salt").asText()));
                    ingredient_macros.add(new BigDecimal(ingredient_node.get("water").asText()));
                    ingredient_macros.add(new BigDecimal(ingredient_node.get("calories").asText()));
                    
                    ingredient_macros.add(ingredient_node.get("delete_btn").asText());
                    
                    //###########################
                    // Store DATA in DIV
                    //###########################
                    if (div_Meal_Sections.containsKey(div_id)) // IF Div already exists add ingredient
                    {
                        div_Meal_Sections.get(div_id).add(ingredient_macros);
                    }
                    else // Create & ADD
                    {
                        ArrayList<ArrayList<Object>> div_Meal = new ArrayList<>();   // Create Meal Div Section
                        div_Meal_Sections.put(div_id, div_Meal); // Add Div to meals Collection
                        
                        div_Meal.add(ingredient_macros); // Add ingredients data
                    }
                }
            }
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s ERROR \n%s", methodName, e);
            return false;
        }
        
        //#######################################
        // Return DATA
        //#######################################
        return true;
    }
    
    public boolean setup_Get_Total_Meals_Data()
    {
        String methodName = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName());
        
        //#################################
        // Create Get Query Results
        //#################################
        String query = String.format("Select * FROM %s WHERE plan_id = ? ORDER BY meal_in_plan_id;", table_Total_Meals_Table_Name);
        
        String errorMSG = "Unable to get Total Meals Data for Plan!!";
        
        Object[] params = new Object[]{ temp_Plan_ID };
        
        //#################################
        // Execute Query
        //#################################
        ArrayList<ArrayList<Object>> meals_Data;
        
        try
        {
            meals_Data = db.get_2D_Query_AL_Object(query, params, errorMSG, false);
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s \n%s Error \n%s  \n%s", lineSeparator, get_Class_And_Method_Name(), lineSeparator, e);
            return false;
        }
        
        //#################################
        // Go Through DATA
        //#################################
        for (ArrayList<Object> meal_Data : meals_Data) // For LOOP through each Meal
        {
            int meal_ID = (int) meal_Data.get(1); // Get Meal Info
            
            total_Meals_Data_Map.put(meal_ID, meal_Data); // Add total Meals Data to storage
        }
        
        //#################################
        // Return Output
        //#################################
        return true;
    }
    
    private boolean setup_Get_Macros_Targets_Data()
    {
        String query_PlanCalc = String.format("SELECT * from %s WHERE plan_id = ?", table_Plan_Macro_Targets_Name_Calc);
        String errorMSG1 = "Error, Gathering Macros Targets Data!";
        
        Object[] params_planCalc = new Object[]{ temp_Plan_ID };
        
        // Execute
        try
        {
            macros_plan_Data = db_Sqlite.get_2D_Query_AL_Object(query_PlanCalc, params_planCalc, errorMSG1, false);
            return true;
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s \n%s Error \n%s  \n%s", lineSeparator, get_Class_And_Method_Name(), lineSeparator, e);
            return false;
        }
    }
    
    private boolean setup_Get_Macros_Left_Data()
    {
        String query_Macros = String.format("SELECT * from %s WHERE plan_id = ?;", table_Plan_Macros_Left_Name);
        String errorMSG_ML = "Error, Unable to get Plan Macros Left!";
        
        Object[] params_macros = new Object[]{ temp_Plan_ID };
        
        try
        {
            macrosData = db_Sqlite.get_2D_Query_AL_Object(query_Macros, params_macros, errorMSG_ML, false);
            return true;
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s \n%s Error \n%s  \n%s", lineSeparator, get_Class_And_Method_Name(), lineSeparator, e);
            return false;
        }
    }
    
    //#################################################
    // Transfer Data Methods
    //#################################################
    private boolean transfer_Plan_Data(int from_Plan_Version_ID)
    {
        //###############################################
        // Queries
        //###############################################
        String query1 = String.format("""
                UPDATE `plans` AS `P`,
                (
                	SELECT plan_name, vegan FROM %s WHERE plan_id = ?
                ) AS `SRC`
                
                SET
                    `P`.`plan_name` = concat("(Temp) ",`SRC`.`plan_name`),`P`.`vegan` = `SRC`.`vegan`
                WHERE
                    `P`.`plan_id` = ?;""", table_Plans_Version_Name);
        
        //###############################################
        // Upload / Params
        //###############################################
        Object[] params = new Object[]{ from_Plan_Version_ID, null };
        
        if (! (db_Sqlite.upload_Data(query1, params, "Unable to Load / Transfer Plan Data!")))
        {
            System.err.printf("\n\n%s \n%s Error \nFailed Transferring Plan_Data \n%s", lineSeparator, get_Class_And_Method_Name(), lineSeparator);
            return false;
        }
        
        //###############################################
        // Output
        //###############################################
        System.out.printf("\nPlanData Successfully transferred! \n\n%s", lineSeparator);
        return true;
    }
    
    private boolean transfer_Targets(int fromPlan, int toPlan, boolean deleteFromToPlan, boolean showConfirmMsg)
    {
        //####################################
        // Queries
        //####################################
        String query00 = String.format("DELETE FROM %s WHERE plan_id = ?;", table_Macros_Per_Pound_Limit_Name);
        String query01 = String.format("DROP TABLE IF EXISTS temp_%s;", table_Macros_Per_Pound_Limit_Name);
        
        String query02 = String.format("CREATE TABLE temp_%s AS SELECT * FROM %s WHERE plan_id = ?;",
                table_Macros_Per_Pound_Limit_Name, table_Macros_Per_Pound_Limit_Name);
        
        String query03 = String.format("UPDATE temp_%s SET plan_id = ?;", table_Macros_Per_Pound_Limit_Name);
        String query04 = String.format("INSERT INTO %s SELECT * FROM temp_%s;", table_Macros_Per_Pound_Limit_Name, table_Macros_Per_Pound_Limit_Name);
        
        String query05 = String.format("DROP TABLE temp_%s;", table_Macros_Per_Pound_Limit_Name);
        
        //####################################
        // Prepare Inputs For Execution
        //####################################
        String errorMSG = "Unable to Transfer Targets Data!";
        
        LinkedHashSet<Pair<String, Object[]>> queries_And_Params = new LinkedHashSet<>()
        {{
            if (deleteFromToPlan) { add(new Pair<>(query00, new Object[]{ toPlan })); }
            
            add(new Pair<>(query01, null));
            add(new Pair<>(query02, new Object[]{ fromPlan }));
            add(new Pair<>(query03, new Object[]{ toPlan }));
            add(new Pair<>(query04, null));
            add(new Pair<>(query05, null));
        }};
        
        //####################################
        // Execute Upload Statements
        //####################################
        if (! (db_Sqlite.upload_Data_Batch(queries_And_Params, errorMSG))) { return false; }
        
        if (showConfirmMsg)
        {
            JOptionPane.showMessageDialog(getFrame(), "\n\nTargets Successfully Saved");
        }
        
        //####################################
        // Return Output
        //####################################
        System.out.printf("\nTargets Successfully transferred! \n\n%s", lineSeparator);
        return true;
    }
    
    private boolean transfer_Meal_Ingredients(int fromPlanID, int toPlanID)
    {
        //################################################################
        // Delete temp tables if they already exist
        //################################################################
        String query0 = String.format("DROP TABLE IF EXISTS temp_%s;", table_Ingredients_In_Meal_Sections);
        String query1 = String.format("DROP TABLE IF EXISTS temp_%s;", table_Sub_MealsName);
        String query2 = String.format("DROP TABLE IF EXISTS temp_%s;", table_Meals_In_Plan_Name);
        
        //################################################################
        // Delete Meals
        //################################################################
        String query3 = String.format("DELETE FROM %s WHERE plan_id = ?;", table_Meals_In_Plan_Name);
        
        //################################################################
        // Transferring Meals From One Plan To Another
        //################################################################
        String query4 = String.format("CREATE table temp_%s AS SELECT * FROM %s WHERE plan_id = ? ORDER BY meal_in_plan_id;", table_Meals_In_Plan_Name, table_Meals_In_Plan_Name);
        
        String query5 = String.format("UPDATE temp_%s SET plan_id = ?;", table_Meals_In_Plan_Name);
        String query6 = String.format("INSERT INTO %s SELECT * FROM temp_%s;", table_Meals_In_Plan_Name, table_Meals_In_Plan_Name);
        
        //################################################################
        // Transferring Sections Of Meals From One Plan To Another
        //################################################################
        String query7 = String.format("CREATE table temp_%s AS SELECT * FROM %s WHERE plan_id = ? ORDER BY div_meal_sections_id;", table_Sub_MealsName, table_Sub_MealsName);
        String query8 = String.format("UPDATE temp_%s SET plan_id = ?;", table_Sub_MealsName);
        String query9 = String.format("INSERT INTO %s SELECT * FROM temp_%s;", table_Sub_MealsName, table_Sub_MealsName);
        
        //################################################################
        // Transferring this plans Ingredients to Temp-Plan
        //################################################################
        // Create Table to transfer ingredients from original plan to temp
        String query10 = String.format("""
                CREATE table temp_%s AS
                SELECT i.*
                FROM %s i
                WHERE i.plan_id = ?;""", table_Ingredients_In_Meal_Sections, table_Ingredients_In_Meal_Sections);
        
        String query11 = String.format("UPDATE temp_%s SET plan_id = ?;", table_Ingredients_In_Meal_Sections);
        String query12 = String.format("INSERT INTO %s SELECT * FROM temp_%s;", table_Ingredients_In_Meal_Sections, table_Ingredients_In_Meal_Sections);
        
        String query13 = String.format("DROP TABLE temp_%s;", table_Meals_In_Plan_Name);
        String query14 = String.format("DROP TABLE temp_%s;", table_Ingredients_In_Meal_Sections);
        String query15 = String.format("DROP TABLE temp_%s;", table_Sub_MealsName);
        
        //####################################################
        // Update
        //####################################################
        String errorMSG = "Error, Unable to Transfer Meal Ingredients";
        
        LinkedHashSet<Pair<String, Object[]>> queries_And_Params = new LinkedHashSet<>()
        {{
            add(new Pair<>(query0, null));
            add(new Pair<>(query1, null));
            add(new Pair<>(query2, null));
            add(new Pair<>(query3, new Object[]{ toPlanID }));
            add(new Pair<>(query4, new Object[]{ fromPlanID }));
            add(new Pair<>(query5, new Object[]{ toPlanID }));
            add(new Pair<>(query6, null));
            add(new Pair<>(query7, new Object[]{ fromPlanID }));
            add(new Pair<>(query8, new Object[]{ toPlanID }));
            add(new Pair<>(query9, null));
            add(new Pair<>(query10, new Object[]{ fromPlanID }));
            add(new Pair<>(query11, new Object[]{ toPlanID }));
            add(new Pair<>(query12, null));
            add(new Pair<>(query13, null));
            add(new Pair<>(query14, null));
            add(new Pair<>(query15, null));
        }};
        
        if (! (db_Sqlite.upload_Data_Batch(queries_And_Params, errorMSG))) { return false; }
        
        //####################################################
        // Output
        //####################################################
        System.out.printf("\nMealIngredients Successfully Transferred! \n\n%s", lineSeparator);
        return true;
    }
    
    //##################################################################################################################
    //  Icon Methods & ActionListener Events
    //##################################################################################################################
    @Override
    protected void iconSetup(Container mainNorthPanel)
    {
        int width, height;
        //##############################################################################################################
        // Top Bar Icon AREA
        //##############################################################################################################
        //Creating JPanels for the area
        
        IconPanel iconPanel = new IconPanel(6, 10, "East");
        JPanel iconPanelInsert = iconPanel.getIconJpanel();
        
        addToContainer(mainNorthPanel, iconPanel.getIconAreaPanel(), 0, 0, 1, 1, 0.25, 0.25, "horizontal", 10, 0, null);
        
        //##########################
        // Clear
        //##########################
        width = 53;
        height = 50;
        
        IconButton clear_Icon_Btn = new IconButton("/images/close_btn/delete.png", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor
        
        clear_Icon_Btn.makeBTntransparent();
        
        JButton clear_Btn = clear_Icon_Btn.returnJButton();
        clear_Btn.setToolTipText("Clear Meal Plan"); //Hover message over icon
        
        clear_Btn.addActionListener(ae -> {
            delete_btnAction();
        });
        
        iconPanelInsert.add(clear_Btn);
        
        //##########################
        // Recipe List
        //##########################
        width = 53;
        height = 50;
        
        IconButton RecipeList_Icon_Btn = new IconButton("/images/RecipeList/recipeList1.png", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor
        
        RecipeList_Icon_Btn.makeBTntransparent();
        
        JButton recipe_Btn = RecipeList_Icon_Btn.returnJButton();
        recipe_Btn.setToolTipText("Get Plan Recipe List"); //Hover message over icon
        
        recipe_Btn.addActionListener(ae -> {
            recipeList_BtnAction_OpenScreen();
        });
        
        iconPanelInsert.add(recipe_Btn);
        
        //##########################
        //  PieChart
        //##########################
        width = 55;
        height = 55;
        
        IconButton pieChart_Icon_Btn = new IconButton("/images/graph/pie7.png", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor
        
        pieChart_Icon_Btn.makeBTntransparent();
        
        JButton pieChart_Btn = pieChart_Icon_Btn.returnJButton();
        pieChart_Btn.setToolTipText("Display Macronutrient Data in Pie"); //Hover message over icon
        
        pieChart_Btn.addActionListener(ae -> {
            pieChart_BtnAction_OpenScreen();
        });
        
        iconPanelInsert.add(pieChart_Btn);
        
        //##########################
        //  LineChart
        //##########################
        width = 51;
        height = 53;
        
        IconButton lineChart_Icon_Btn = new IconButton("/images/graph/bar1.png", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor
        
        lineChart_Icon_Btn.makeBTntransparent();
        
        JButton lineChart_Btn = lineChart_Icon_Btn.returnJButton();
        lineChart_Btn.setToolTipText("Display Macronutrient Data in LineChart"); //Hover message over icon
        
        lineChart_Btn.addActionListener(ae -> {
            lineChart_Btn_Action_OpenScreen();
        });
        
        iconPanelInsert.add(lineChart_Btn);
        
        //##########################
        //  ScrollBar Up
        //##########################
        width = 51;
        height = 53;
        
        IconButton up_ScrollBar_Icon_Btn = new IconButton("/images/scrollBar_Up/scrollBar_Up.png", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor
        
        up_ScrollBar_Icon_Btn.makeBTntransparent();
        
        JButton up_ScrollBar_Btn = up_ScrollBar_Icon_Btn.returnJButton();
        up_ScrollBar_Btn.setToolTipText("Scroll to the top of Meal Plan"); //Hover message over icon
        
        up_ScrollBar_Btn.addActionListener(ae -> {
            
            scrollUp_BtnAction();
        });
        
        iconPanelInsert.add(up_ScrollBar_Btn);
        
        //##########################
        // Refresh Icon
        //##########################
        width = 50;
        height = 50;
        
        IconButton refresh_Icon_Btn = new IconButton("/images/refresh/++refresh.png", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor
        
        JButton refresh_Btn = refresh_Icon_Btn.returnJButton();
        refresh_Btn.setToolTipText("Restore All Meals Data"); //Hover message over icon
        refresh_Icon_Btn.makeBTntransparent();
        
        refresh_Btn.addActionListener(ae -> {
            refreshPlan(true);
        });
        
        iconPanelInsert.add(refresh_Icon_Btn);
        
        //##########################
        //Add BTN
        //##########################
        width = 50;
        height = 52;
        
        IconButton add_Icon_Btn = new IconButton("/images/add/++add.png", width, height, width, height,
                "centre", "right");
        
        JButton add_Btn = add_Icon_Btn.returnJButton();
        add_Btn.setToolTipText("Add Meal"); //Hover message over icon
        add_Icon_Btn.makeBTntransparent();
        
        add_Btn.addActionListener(ae -> {
            
            add_Meal_Btn_Action();
        });
        
        iconPanelInsert.add(add_Icon_Btn);
        
        //##########################
        // Save BTN
        //##########################
        
        width = 54;
        height = 57;
        
        IconButton saveIcon_Icon_Btn = new IconButton("/images/save/+++save.png", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor
        //saveIcon_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));
        saveIcon_Icon_Btn.makeBTntransparent();
        
        JButton save_btn = saveIcon_Icon_Btn.returnJButton();
        save_btn.setToolTipText("Save All Meals"); //Hover message over icon
        
        save_btn.addActionListener(ae -> {
            
            saveMealData(true, true);
        });
        
        iconPanelInsert.add(save_btn);
        
        //##########################
        //  Add_Ingredients Icon
        //##########################
        
        width = 54;
        height = 57;
        
        IconButton add_Ingredients_Icon_Btn = new IconButton("/images/add_Ingredients/add_Ingredients.png", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor
        //saveIcon_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));
        add_Ingredients_Icon_Btn.makeBTntransparent();
        
        JButton add_Ingredients_Btn = add_Ingredients_Icon_Btn.returnJButton();
        add_Ingredients_Btn.setToolTipText("Add Ingredients"); //Hover message over icon
        
        
        add_Ingredients_Btn.addActionListener(ae -> {
            
            open_Ingredients_Screen();
        });
        
        iconPanelInsert.add(add_Ingredients_Btn);
        
        //##########################
        //  Macro_Targets Icon
        //##########################
        
        width = 54;
        height = 50;
        
        IconButton macro_Targets_Icon_Btn = new IconButton("/images/targets/target.png", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor
        //saveIcon_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));
        macro_Targets_Icon_Btn.makeBTntransparent();
        
        JButton macro_Tagets_Btn = macro_Targets_Icon_Btn.returnJButton();
        macro_Tagets_Btn.setToolTipText("Set Macro Targets"); //Hover message over icon
        
        
        macro_Tagets_Btn.addActionListener(ae -> {
            
            open_MacrosTargets_Screen();
        });
        
        iconPanelInsert.add(macro_Tagets_Btn);
        
        //##########################
        //  Scroll Bottom
        //##########################
        width = 51;
        height = 51;
        
        IconButton down_ScrollBar_Icon_Btn = new IconButton("/images/scrollBar_Down/scrollBar_Down5.png", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor
        
        down_ScrollBar_Icon_Btn.makeBTntransparent();
        
        JButton down_ScrollBar_Btn = down_ScrollBar_Icon_Btn.returnJButton();
        down_ScrollBar_Btn.setToolTipText("Scroll to Bottom Of Meal Plan"); //Hover message over icon
        
        down_ScrollBar_Btn.addActionListener(ae -> {
            
            scrollDown_BtnAction();
        });
        
        iconPanelInsert.add(down_ScrollBar_Btn);
    }
    
    // ################################################################
    // Delete BTN Actions
    // ###############################################################
    private void delete_btnAction()
    {
        //###########################################################
        // Ask for Confirmation
        //###########################################################
        if (! areYouSure("Delete All Meals", "Are you want to 'DELETE' all the meals in this plan?")) { return; }
        
        //###########################################################
        // Delete all the meals in the plans in SQL
        //###########################################################
        String queryDelete = "DELETE FROM meals_in_plan WHERE plan_id = ?";
        
        Object[] params = new Object[]{ temp_Plan_ID };
        
        if (! db.upload_Data(queryDelete, params, "Error, unable to DELETE meals in plan!")) { return; }
        
        JOptionPane.showMessageDialog(this, "\n\nSuccessfully, DELETED all meals in plan!");
        
        //###########################################################
        // DELETE all the meals in Memory
        //###########################################################
        shared_Data_Registry.delete_MealManagers_MPS();
        
        //###########################################################
        // Update MacrosLeft
        //###########################################################
        update_MacrosLeftTable();
        
        //###########################################################
        // Update External Graphs
        //###########################################################
        update_External_Charts(true, "clear", null, null, null);
    }
    
    // ###############################################################
    // Recipe BTN Actions
    // ###############################################################
    private void recipeList_BtnAction_OpenScreen()
    {
        
    }
    
    // ###############################################################
    // Pie Chart BTN Actions
    // ###############################################################
    public Boolean is_PieChart_Screen_Open()
    {
        return pieChart_Screen_MPS != null;
    }
    
    public void removePieChartScreen()
    {
        pieChart_Screen_MPS = null;
    }
    
    private void pieChart_BtnAction_OpenScreen()
    {
        if (is_PieChart_Screen_Open())
        {
            pieChart_Screen_MPS.makeJFrameVisible();
            return;
        }
        
        pieChart_Screen_MPS = new PieChart_Screen_MPS(db, this);
    }
    
    // #############################
    // PieChart DATA Methods
    // #############################
    private void clear_PieChart_DATA_MPS()
    {
        if (! is_PieChart_Screen_Open()) { return; }
        
        pieChart_Screen_MPS.clear();
    }
    
    private void refresh_PieChart_DATA_MPS()
    {
        if (! is_PieChart_Screen_Open()) { return; }
        
        pieChart_Screen_MPS.refresh();
    }
    
    private void update_PieChart_MealName(Integer mealInPlanID)
    {
        if (! is_PieChart_Screen_Open()) { return; }
        
        pieChart_Screen_MPS.update_PieChart_MealName(mealInPlanID);
    }
    
    private void update_PieChart_MealTime(Integer mealInPlanID)
    {
        if (! is_PieChart_Screen_Open()) { return; }
        
        pieChart_Screen_MPS.update_PieChart_MealTime(mealInPlanID);
    }
    
    private void update_PieChart_DATA()
    {
        if (! is_PieChart_Screen_Open()) { return; }
        
        pieChart_Screen_MPS.updateData();
    }
    
    private void add_Meal_Manager_PieChart(MealManager mealManager)
    {
        if (! is_PieChart_Screen_Open()) { return; }
        
        pieChart_Screen_MPS.add_MealManager_To_GUI(mealManager);
    }
    
    private void delete_MealManager_PieChart(MealManager mealManager)
    {
        if (! is_PieChart_Screen_Open()) { return; }
        
        // Data Handling already been processed, screen just needs to be re-drawn
        pieChart_Screen_MPS.deleted_MealManager_PieChart(mealManager);
    }
    
    // ###############################################################
    // Line Chart BTN Actions
    // ###############################################################
    private boolean is_LineChart_Screen_Open()
    {
        return lineChart_MPS != null;
    }
    
    public void removeLineChartScreen()
    {
        lineChart_MPS = null;
    }
    
    private void lineChart_Btn_Action_OpenScreen()
    {
        if (! is_LineChart_Screen_Open())
        {
            lineChart_MPS = new LineChart_MPS(db, this);
            return;
        }
        
        lineChart_MPS.makeJFrameVisible();
    }
    
    // #############################
    // LineChart DATA Methods
    // #############################
    private void updateLineChartData(MealManager mealManager, Second previousTime, Second currentTime)
    {
        if (! is_LineChart_Screen_Open()) { return; }
        
        lineChart_MPS.update_MealManager_ChartData(mealManager, previousTime, currentTime);
    }
    
    private void add_Meal_To_LineChart(MealManager mealManager)
    {
        if (! is_LineChart_Screen_Open()) { return; }
        
        lineChart_MPS.add_New_MealManager_Data(mealManager);
    }
    
    private void deleteLineChartData(Second currentTime)
    {
        if (! is_LineChart_Screen_Open()) { return; }
        
        lineChart_MPS.delete_MealManager_Data(currentTime);
    }
    
    private void clearLineChartDataSet()
    {
        if (! is_LineChart_Screen_Open()) { return; }
        
        lineChart_MPS.clear_LineChart_Dataset();
    }
    
    private void refresh_LineChart_Data()
    {
        if (! is_LineChart_Screen_Open()) { return; }
        
        lineChart_MPS.refresh_Data();
    }
    
    // ###############################################################
    // Scroll Up BTN Action
    // ###############################################################
    private void scrollUp_BtnAction()
    {
        super.scroll_To_Top_of_ScrollPane();
    }
    
    // ###############################################################
    // Refresh BTN Actions / Plan Actions
    // ###############################################################
    private void refreshPlan(boolean askPermission)
    {
        String txt = "Are you sure you want to refresh all the meals in this plan?";
        if ((! (get_IsPlanSelected())) || askPermission && ! (areYouSure("Refresh Meal Plan Data", txt)))
        {
            return;
        }
        
        //####################################################################
        // Refresh DB Data
        //####################################################################
        if (! (transfer_Meal_Ingredients(selected_Plan_Version_ID, temp_Plan_ID))) // transfer meals and ingredients from temp plan to original plan
        {
            JOptionPane.showMessageDialog(this, "`\n\nError couldn't transfer ingredients data from temp to real plan !!");
            return;
        }
        
        //####################################################################
        // Refresh MealManagers Collections
        //####################################################################
        shared_Data_Registry.refresh_MealManagers_MPS();
        
        //####################################################################
        // Re-draw GUI Screen
        //####################################################################
        reDraw_GUI();
        scrollUp_BtnAction(); // Reposition GUI
        
        //####################################################################
        // Refresh Macro-Targets Table
        //####################################################################
        refresh_MacroTargets(); // if macroTargets changed ask the user if they would like to refresh this data
        
        update_MacrosLeftTable(); // Update macrosLeft, refresh methods do not work here, needs to be computed
        
        //####################################################################
        // Update External Charts
        //####################################################################
        update_External_Charts(true, "refresh", null, null, null);
    }
    
    // ###############################################################
    // Add Meal BTN Actions
    // ###############################################################
    private void add_Meal_Btn_Action()
    {
        //##############################################################################################################
        //
        //##############################################################################################################
        if (! (get_IsPlanSelected()))
        {
            JOptionPane.showMessageDialog(null, "\n\nCannot Add A  Meal As A Plan Is Not Selected! \nPlease Select A Plan First!!");
            return;
        }
        
        //##############################################################################################################
        // Add MealManager To GUI & Charts
        //##############################################################################################################
        MealManager mealManager = new MealManager(this, db, macros_Left_JTable);
        
        //###############################################
        // If Object Creation Failed Exit
        //###############################################
        if (! mealManager.isObjectCreated()) { return; }
        
        JOptionPane.showMessageDialog(null, String.format("Successfully Created Meal in %s at [%s]",
                mealManager.get_Current_Meal_Name(), mealManager.get_Current_Meal_Time_GUI()));
        
        //###############################################
        // ADD MealManager Info to DATA
        //###############################################
        shared_Data_Registry.addMealManager(mealManager);
        
        //###############################################
        // ADD to GUI & Charts
        //###############################################
        add_And_Replace_MealManger_POS_GUI(mealManager, true, true); // Add to GUI
        
        //###############################################
        // Add to External Charts
        //###############################################
        update_External_Charts(true, "add", mealManager, null, mealManager.getCurrentMealTime());
    }
    
    public void add_And_Replace_MealManger_POS_GUI(MealManager mealManager, boolean reOrder, boolean expandView)
    {
        //###############################################
        // Exit Clause
        //###############################################
        if (! mealManager.isObjectCreated()) { return; }
        
        //###############################################
        // Add to GUI Meal Manager & Its Space Divider
        //###############################################
        if (! reOrder) // Just add to GUI
        {
            addToContainer(scroll_JPanel_Center, mealManager.get_Collapsible_JP_Obj(), 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
            addToContainer(scroll_JPanel_Center, mealManager.getSpaceDividerForMealManager(), 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "both", 50, 0, null);
        }
        
        //###############################################
        // Clear and Redraw
        //###############################################
        else
        {
            reDraw_GUI();
        }
        
        //###############################################
        // Expand Meal ?
        //###############################################
        if (expandView) { mealManager.expand_JPanel(); }
        
        //###############################################
        // Add to GUI Meal Manager & Its Space Divider
        //###############################################
        resizeGUI();
        
        //###############################################
        // Scroll to MealManager
        //###############################################
        scrollToJPanelOnScreen(mealManager.get_Collapsible_JP_Obj());
    }
    
    public void reDraw_GUI()
    {
        shared_Data_Registry.sort_MealManager_AL(); // Sort
        
        scroll_JPanel_Center.removeAll(); // Clear Screen
        
        // Re-Draw all MealManager to GUI
        ArrayList<MealManager> mealManager_ArrayList = shared_Data_Registry.get_MealManager_ArrayList();
        for (MealManager mm : mealManager_ArrayList)
        {
            System.out.printf("\n\nMealManagerID: %s \nMealName : %s \nMealTime : %s",
                    mm.get_Meal_In_Plan_ID(), mm.get_Current_Meal_Name(), mm.get_Current_Meal_Time_GUI());
            
            mm.collapse_MealManager(); // Collapse all meals
            
            // Add MealManager and its Space Separator to GUI
            addToContainer(scroll_JPanel_Center, mm.get_Collapsible_JP_Obj(), 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
            addToContainer(scroll_JPanel_Center, mm.getSpaceDividerForMealManager(), 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "both", 50, 0, null);
        }
        
        scroll_JPanel_Center.repaint();
    }
    
    // ###############################################################
    // Save Plan BTN
    // ###############################################################
    private void saveMealData(boolean askPermission, boolean showMsg)
    {
        // ##############################################################################
        // Exit Clauses
        // ##############################################################################
        // If no plan is selected exit Or,  if user rejects saving when asked  exit
        String txt = "Are you want to save all the data in this meal plan?";
        if ((! (get_IsPlanSelected())) || askPermission && ! (areYouSure("Save Meal Plan Data", txt))) { return; }
        
        // ##############################################################################
        // Remove Delete MealManagers & Check if
        // ##############################################################################
        boolean noMealsLeft = true;
        
        ArrayList<MealManager> mealManager_ArrayList = shared_Data_Registry.get_MealManager_ArrayList();
        
        Iterator<MealManager> it = mealManager_ArrayList.iterator();
        while (it.hasNext())
        {
            MealManager mealManager = it.next();
            if (mealManager.is_Meal_Deleted())
            {
                it.remove();
                continue;
            }
            noMealsLeft = false; // if this line is reached there is a  MealManager (for loop) that hasn't been deleted
        }
        
        // ##############################################################################
        // Save meal plan data in DB
        // ##############################################################################
        // if there are no meals in the temp plan delete all meals / ingredients from original plan as opposed to a transfer
        if (noMealsLeft)
        {
            System.out.println("\n\n#################################### \n1.) saveMealData() Empty Meal Plan Save");
            
            String query = String.format("DELETE FROM %s WHERE plan_id = ?;", table_Meals_In_Plan_Name);
            
            Object[] params = new Object[]{ selected_Plan_Version_ID };
            
            if (! (db.upload_Data(query, params, "Error 2, Unable to Save Meal Data!"))) { return; }
        }
        else // because there are meals save them
        {
            if ((! (transfer_Meal_Ingredients(temp_Plan_ID, selected_Plan_Version_ID)))) // transfer meals and ingredients from temp plan to original plan
            {
                System.out.println("\n\n#################################### \n2.) saveMealData() Meals Transferred to Original Plan");
                
                JOptionPane.showMessageDialog(this, "\n\n2.)  Error \nUnable to save meals in plan!");
                return;
            }
            
            // ###############################################################################
            // Instructing Meal Manager Tables To Update Their Model Data
            // ##############################################################################
            Iterator<MealManager> it2 = mealManager_ArrayList.iterator();
            
            while (it2.hasNext())
            {
                MealManager mealManager = it2.next();
                mealManager.saveData(false);
            }
        }
        
        // ##############################################################################
        // Successful Message
        // ##############################################################################
        if (showMsg) { JOptionPane.showMessageDialog(this, "\n\nAll Meals Are Successfully Saved!"); }
    }
    
    // ###############################################################
    // Add Ingredients Screen & Ingredient Methods
    // ###############################################################
    private void open_Ingredients_Screen()
    {
        if (is_IngredientScreen_Open())
        {
            ingredients_Info_Screen.makeJFrameVisible();
            return;
        }
        
        ingredients_Info_Screen = new Ingredients_Info_Screen(db, this, shared_Data_Registry);
    }
    
    private Boolean is_IngredientScreen_Open()
    {
        return ingredients_Info_Screen != null;
    }
    
    public void remove_Ingredients_Info_Screen()
    {
        ingredients_Info_Screen = null;
    }
    
    public void updateIngredientsNameAndTypesInJTables(boolean ingredientsAddedOrRemove)
    {
        if (ingredientsAddedOrRemove)
        {
            //#####################################
            // Save Plan & Refresh Plan
            //#####################################
            saveMealData(false, false); // Save Plan
            refreshPlan(false); // Refresh Plan
        }
    }
    
    // ###############################################################
    // Macro Targets Screen & Target Methods
    // ###############################################################
    private void open_MacrosTargets_Screen()
    {
        if (! (get_IsPlanSelected()))
        {
            return;
        }
        
        if (is_MacroTargetsScreen_Open())
        {
            macros_Targets_Screen.makeJFrameVisible();
            return;
        }
        macros_Targets_Screen = new Macros_Targets_Screen(db, this, temp_Plan_ID, plan_Name);
    }
    
    private boolean is_MacroTargetsScreen_Open()
    {
        return macros_Targets_Screen != null;
    }
    
    public void remove_macrosTargets_Screen()
    {
        macros_Targets_Screen = null;
    }
    
    public void macrosTargetsChanged(boolean bool)
    {
        macro_Targets_Changed = bool;
    }
    
    // Booleans
    public boolean hasMacroTargetsChanged()
    {
        return macro_Targets_Changed;
    }
    
    private void saveMacroTargets(boolean askPermission, boolean showUpdateMsg)
    {
        // ##############################################
        // If targets haven't changed exit
        // ##############################################
        if (! hasMacroTargetsChanged())
        {
            return;
        }
        
        // ################################################
        // If askPermission as permission to save targets
        // ################################################
        if (askPermission)
        {
            int reply = JOptionPane.showConfirmDialog(this, String.format("Would you like to save your MacroTarget  Changes Too?"),
                    "Save Macro Targets", JOptionPane.YES_NO_OPTION); //HELLO Edit
            
            if (reply == JOptionPane.NO_OPTION || reply == JOptionPane.CLOSED_OPTION)
            {
                return;
            }
        }
        
        if (transfer_Targets(temp_Plan_ID, selected_Plan_Version_ID, false, showUpdateMsg))
        {
            macrosTargetsChanged(false);
            update_Targets_And_MacrosLeftTables();
        }
    }
    
    // ###############################################################
    // Scroll Down BTN Action
    // ###############################################################
    private void scrollDown_BtnAction()
    {
        super.scroll_To_Bottom_of_ScrollPane();
    }
    
    //##################################################################################################################
    // Other Methods
    //##################################################################################################################
    @Override
    public void window_Closed_Event()
    {
        // ##########################################
        // Close Other Windows If Open
        // ##########################################
        if (screen_Created)
        {
            
            // Ask to Save Target DATA
            if (hasMacroTargetsChanged()) // If targets have changed, save them?
            {
                saveMacroTargets(true, false);
            }
            
            saveMealData(true, false);  //Meal Data
        }
        
        // ##########################################
        // Close Other Windows If Open
        // ##########################################
        if (is_MacroTargetsScreen_Open())
        {
            macros_Targets_Screen.window_Closed_Event();
        }
        if (is_IngredientScreen_Open()) // HELLO Refactor into screen method
        {
            ingredients_Info_Screen.window_Closed_Event();
        }
        if (is_PieChart_Screen_Open())
        {
            pieChart_Screen_MPS.window_Closed_Event();
        }
        if (is_LineChart_Screen_Open())
        {
            lineChart_MPS.window_Closed_Event();
        }
        
        // ##########################################
        // Close PieCharts Open by MealManagers
        // ##########################################
        Iterator<MealManager> it = shared_Data_Registry.get_MealManager_ArrayList().iterator();
        while (it.hasNext())
        {
            it.next().close_PieChartScreen();
            it.remove();
        }
        
        // ##########################################
        // Close DB Pool
        // ##########################################
        db.close_Connection();
    }
    
    public void update_External_Charts(boolean mealPlanScreen_Action, String action, MealManager mealManager,
                                       Second previousMealTime, Second currentMealTime)
    {
        //####################################################################
        // MealPlanScreen
        //####################################################################
        if (mealPlanScreen_Action)
        {
            if (action.equals("add")) // New MealManager Added to GUI
            {
                // LineChart Add
                add_Meal_To_LineChart(mealManager);
                
                // PieChart MPS Screen
                add_Meal_Manager_PieChart(mealManager);
            }
            else if (action.equals("clear")) // Delete Button requested on MealPlanScreen
            {
                // Clear LineChart Data
                clearLineChartDataSet();
                
                // Clear PieChart Screen
                clear_PieChart_DATA_MPS();
            }
            else if (action.equals("refresh"))
            {
                // Refresh LineChart Data
                refresh_LineChart_Data();
                
                //Refresh PieChart Screen
                refresh_PieChart_DATA_MPS();
            }
            
            //##########################
            // Exit
            //##########################
            return;
        }
        
        //####################################################################
        // MealManager Requested Action
        //####################################################################
        if (action.equals("update")) // Update MealManager Time
        {
            // Update LineChart Data
            updateLineChartData(mealManager, previousMealTime, currentMealTime);
            
            // Update PieChart DATA
            update_PieChart_DATA();
        }
        else if (action.equals("delete")) // Deleted MealManager
        {
            // Delete LineChart Data
            deleteLineChartData(previousMealTime);
            
            // Delete PieChart AKA Re-draw GUI
            delete_MealManager_PieChart(mealManager);
        }
        else if (action.equals("mealTime")) // MealTime on MealManager Changed
        {
            // Change data points time on LineChart Data
            updateLineChartData(mealManager, previousMealTime, currentMealTime);
            
            // Update PieChart Title OF Meal & Refresh Interface
            update_PieChart_MealTime(mealManager.get_Meal_In_Plan_ID());
        }
        else if (action.equals("mealName")) // MealTime on MealManager Changed
        {
            //############################
            // LineChart
            //############################
            // Nothing Changes
            
            // Change PieChart MealName
            update_PieChart_MealName(mealManager.get_Meal_In_Plan_ID());
        }
        else if (action.equals("refresh")) // Refresh mealPlan was requested
        {
            
            // Refresh MealManager
            updateLineChartData(mealManager, previousMealTime, currentMealTime);
            
            // Change PieChart MealName
            update_PieChart_MealName(mealManager.get_Meal_In_Plan_ID());
        }
    }
    
    //##################################################################################################################
    //  Macro Targets/Left Table Methods
    //##################################################################################################################
    public void update_Targets_And_MacrosLeftTables()
    {
        update_MacrosTargetTable();
        update_MacrosLeftTable();
    }
    
    //##########################################
    // MacrosLeft Targets
    //#########################################
    public void update_MacrosTargetTable()
    {
        macros_Targets_Table.update_Table();
    }
    
    private void refresh_MacroTargets()
    {
        // ##############################################
        // If targets have changed prompt to Refresh
        // ##############################################
        if (hasMacroTargetsChanged())
        {
            int reply = JOptionPane.showConfirmDialog(this, String.format("Would you like to refresh your MacroTargets Too?"),
                    "Refresh Macro Targets", JOptionPane.YES_NO_OPTION); //HELLO Edit
            
            if (reply == JOptionPane.YES_OPTION)
            {
                if (transfer_Targets(selected_Plan_Version_ID, temp_Plan_ID, true, false))
                {
                    JOptionPane.showMessageDialog(this, "\n\nMacro-Targets Successfully Refreshed!!");
                    macrosTargetsChanged(false);
                    
                    macros_Targets_Table.refresh_Data();
                }
            }
        }
    }
    
    //##########################################
    // MacrosLeft Table
    //#########################################
    public void update_MacrosLeftTable()
    {
        macros_Left_JTable.update_Table();
    }
    
    public MacrosLeft_Table get_MacrosLeft_JTable()
    {
        return macros_Left_JTable;
    }
    
    //##################################################################################################################
    //  Accessor Methods
    //##################################################################################################################
    public boolean get_IsPlanSelected()
    {
        if (selected_Plan_Version_ID == null)
        {
            JOptionPane.showMessageDialog(this, "Please Select A Plan First!");
            return false;
        }
        return true;
    }
    
    //###########################################
    // String
    //###########################################
    public String getPlan_Name()
    {
        return plan_Name;
    }
    
    //###########################################
    // Integers
    //###########################################
    public Integer getTemp_Plan_ID()
    {
        return temp_Plan_ID;
    }
    
    public Integer getSelected_Plan_Version_ID()
    {
        return selected_Plan_Version_ID;
    }
    
    //###########################################
    // Objects
    //###########################################
    public Shared_Data_Registry get_MealManagerRegistry()
    {
        return shared_Data_Registry;
    }
    
    public JPanel getScroll_JPanel_Center()
    {
        return scroll_JPanel_Center;
    }
    
    //####################################################################
    // Collections :  Accessor Methods
    //#####################################################################
    
    // TotalMeal Table Collections
    public ArrayList<String> getTotal_Meal_Table_Col_To_Hide()
    {
        return total_Meal_Table_Col_To_Hide;
    }
    
    public ArrayList<String> getMeal_total_column_Names()
    {
        return meal_total_column_Names;
    }
    
    public HashMap<String, Integer> get_TotalMeal_Other_Cols_Pos()
    {
        return total_Meal_Other_Cols_Positions;
    }
    
    public LinkedHashMap<String, Pair<Integer, String>> get_TotalMeal_macro_Col_Name_And_Pos()
    {
        return total_Meal_Macro_Col_Name_And_Positions;
    }
    
    //###########################################
    // Ingredients Table Collections
    //###########################################
    public ArrayList<String> getIngredients_Column_Names()
    {
        return ingredients_Column_Names;
    }
    
    public ArrayList<String> getIngredients_Table_Un_Editable_Cells()
    {
        return ingredients_Table_Un_Editable_Cells;
    }
    
    public ArrayList<String> getIngredients_Table_Col_Avoid_Centering()
    { return ingredients_Table_Col_Avoid_Centering; }
    
    public ArrayList<String> getIngredients_In_Meal_Table_Col_To_Hide()
    {
        return ingredients_In_Meal_Table_Col_To_Hide;
    }
}
