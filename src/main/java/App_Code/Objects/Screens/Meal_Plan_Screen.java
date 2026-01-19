package App_Code.Objects.Screens;

import App_Code.Objects.Data_Objects.ID_Objects.MetaData_ID_Object.Meal_ID_OBJ;
import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.*;
import App_Code.Objects.Database_Objects.MyJDBC.MyJDBC_Sqlite;
import App_Code.Objects.Database_Objects.Shared_Data_Registry;
import App_Code.Objects.Table_Objects.Tables.Children.View_Data_Tables.Children.MacrosLeft_Table;
import App_Code.Objects.Table_Objects.Tables.Children.View_Data_Tables.Children.MacrosTargets_Table;
import App_Code.Objects.Table_Objects.Tables.Children.View_Data_Tables.Children.Total_Meal_Table.Total_Meal_Macro_Columns;
import App_Code.Objects.Table_Objects.Tables.Children.View_Data_Tables.Children.Total_Meal_Table.Total_Meal_Other_Columns;
import App_Code.Objects.Table_Objects.Tables.Parent.My_Enum;
import App_Code.Objects.Table_Objects.MealManager;
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
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

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
    private Shared_Data_Registry shared_Data_Registry;
    
    // JPanels
    private JPanel scroll_JPanel_Center;
    
    // Table Objects
    private MacrosLeft_Table macros_Left_JTable;
    private MacrosTargets_Table macros_Targets_Table;
    
    // Screen Objects
    private Macros_Targets_Screen macros_Targets_Screen = null;
    private Ingredients_Info_Screen ingredients_Info_Screen = null;
    
    private PieChart_Screen_MPS pie_Chart_Screen = null;
    private LineChart_MPS line_Chart = null;
    
    //###############################################
    // Integers
    //###############################################
    private static Integer user_id;
    private Integer selected_Plan_ID;
    private Integer selected_Plan_Version_ID;
    private Integer no_of_meals;
    private Integer no_of_sub_meals;
    
    //###############################################
    // Booleans
    //###############################################
    private boolean macro_Targets_Changed = false;
    private boolean screen_Created = false;
    
    //#########################################################################################
    // Collections
    //#########################################################################################
    // Meals & Plan Data
    private ArrayList<ArrayList<Object>> macros_targets_plan_data_AL;
    private ArrayList<ArrayList<Object>> macros_left_plan_data_AL;
    private ArrayList<Meals_And_Sub_Meals_OBJ> meals_and_sub_meals_AL = new ArrayList<>();
    
    //#######################################
    // Column Names
    //#######################################
    private ArrayList<String> meal_total_column_Names;
    private ArrayList<String> ingredients_Column_Names;
    private ArrayList<String> macros_left_columnNames;
    private ArrayList<String> macroTargets_ColumnNames;
    
    //#######################################
    // Meals Data Collections
    //#######################################
    
    
    private final LinkedHashMap<Integer, ArrayList<Object>> total_Meals_Data_Map = new LinkedHashMap<>();
    
    //#######################################
    // Ingredients Table Columns
    //#######################################
    // Table: draft_gui_ingredients_in_sections_of_meal_calculation
    private final ArrayList<String> ingredients_Table_Col_Avoid_Centering = new ArrayList<>(Arrays.asList(
            "ingredient_type", "ingredient_name"));
    
    private final ArrayList<String> ingredients_Table_Un_Editable_Cells = new ArrayList<>(Arrays.asList(
            "draft_ingredients_index", "protein", "gi", "carbohydrates", "sugars_of_carbs",
            "fibre", "fat", "saturated_fat", "salt", "water_content", "calories"
    ));
    
    private final ArrayList<String> ingredients_In_Meal_Table_Col_To_Hide = new ArrayList<>(Arrays.asList(
            "draft_ingredients_index", "water_content"
    ));
    
    //#######################################
    // Macro_Targets
    //#######################################
    // Table : draft_gui_plan_macro_target_calculations
    private final ArrayList<String> macros_Targets_Table_Col_To_Hide = new ArrayList<String>(List.of("plan_id"));
    
    //#######################################
    //Macro_Left
    //#######################################
    // Table : draft_gui_plan_macros_left
    private final ArrayList<String> macros_Left_Table_Col_To_Hide = new ArrayList<String>(List.of("plan_id"));
    
    //#######################################
    // Total_Meal_View Table
    //#######################################
    // draft_gui_total_meal_view
    private final ArrayList<String> total_Meal_Table_Col_To_Hide = new ArrayList<String>(Arrays.asList(
            "draft_meal_in_plan_id", "meal_name"
    ));
    
    private final LinkedHashMap<Total_Meal_Macro_Columns, Integer> total_meal_macro_col_positions = new LinkedHashMap<>()
    {{
        put(Total_Meal_Macro_Columns.TOTAL_PROTEIN, null);
        put(Total_Meal_Macro_Columns.TOTAL_CARBOHYDRATES, null);
        put(Total_Meal_Macro_Columns.TOTAL_SUGARS_OF_CARBS, null);
        put(Total_Meal_Macro_Columns.TOTAL_FATS, null);
        put(Total_Meal_Macro_Columns.TOTAL_SATURATED_FAT, null);
        put(Total_Meal_Macro_Columns.TOTAL_SALT, null);
        put(Total_Meal_Macro_Columns.TOTAL_FIBRE, null);
        put(Total_Meal_Macro_Columns.TOTAL_WATER, null);
        put(Total_Meal_Macro_Columns.TOTAL_CALORIES, null);
    }};
    
    private final HashMap<Total_Meal_Other_Columns, Integer> total_meal_other_cols_positions = new HashMap<>() // These 2 columns are needed for external charts
    {{
        put(Total_Meal_Other_Columns.MEAL_TIME, null);
        put(Total_Meal_Other_Columns.MEAL_NAME, null);
    }};
    
    private final LinkedHashMap<Total_Meal_Macro_Columns, String> total_meal_macro_symbol = new LinkedHashMap<>()
    {{
        put(Total_Meal_Macro_Columns.TOTAL_PROTEIN, "g");
        put(Total_Meal_Macro_Columns.TOTAL_CARBOHYDRATES, "g");
        put(Total_Meal_Macro_Columns.TOTAL_SUGARS_OF_CARBS, "g");
        put(Total_Meal_Macro_Columns.TOTAL_FATS, "g");
        put(Total_Meal_Macro_Columns.TOTAL_SATURATED_FAT, "g");
        put(Total_Meal_Macro_Columns.TOTAL_SALT, "g");
        put(Total_Meal_Macro_Columns.TOTAL_FIBRE, "g");
        put(Total_Meal_Macro_Columns.TOTAL_WATER, "ml");
        put(Total_Meal_Macro_Columns.TOTAL_CALORIES, "kcal");
    }};
    
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
    
    public Meal_Plan_Screen(MyJDBC_Sqlite db)
    {
        //###############################################################################
        // Super / Variables
        //###############################################################################
        super(db, true, "Gym App", 1925, 1082, 1300, 0);
        
        Loading_Screen loading_Screen = new Loading_Screen(100);
        
        UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 16)); // Set up window msg font
        
        //###############################################################################
        //
        //###############################################################################
        /**
         *  1.) Getting Selected User Info & Their Active Plan Info
         *  2.) Getting Table Column Names
         *  3.) Getting Number Of Meals & Sub meals Count for Active Plan         *
         *  4.) Transferring PLan Data To DRAFT  Plan_Data
         *  5.) Transfer Plan Targets
         *  6.) Transferring Meals Data
         *  7.) Main GUI Setup (excluding adding meals)
         *  8.) MacroTargets Setup
         *  9.) MacrosLeft Setup
         *
         *
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
        
        shared_Data_Registry = new Shared_Data_Registry(this, plan_Name, selected_Plan_ID, selected_Plan_Version_ID);
        
        // 2.) Getting Table Column Names
        if (! setup_Get_Column_Names()) { failed_Start_UP(loading_Screen); return; }
        
        // 3.) Getting Number Of Meals & Sub meals Count
        if (! setup_Get_Meal_Counts()) { failed_Start_UP(loading_Screen); return; }
        
        //####################################################
        // Transferring PLan Data To Temp
        //####################################################
        if (! setup_transfer_Plan_Data()) { failed_Start_UP(loading_Screen); return; }
        
        loading_Screen.increaseBar(10);
        
        //####################################################
        // Transferring Targets From Chosen PLan to Temp
        //####################################################
        if (! setup_transfer_Targets())
        {
            failed_Start_UP(loading_Screen); return;
        }
        
        loading_Screen.increaseBar(10);
        
        //####################################################
        // Transferring this plans Meals  Info to Temp-Plan
        //####################################################
        if (! setup_transfer_Meal_Data())
        {
            failed_Start_UP(loading_Screen); return;
        }
        
        loading_Screen.increaseBar(10);
        
        //####################################################
        // Get DATA Methods
        //####################################################
        System.out.printf("\n\n%s \nGetting Meta Data Objects \n%s ", lineSeparator, lineSeparator);
        
        // Get Ingredient Types Mapped to Ingredient Names
        if (! setup_Get_Ingredient_Types_And_Ingredient_Names()) { failed_Start_UP(loading_Screen); return; }
        
        // Get Stores DATA
        if (! setup_Get_Stores_Data()) { failed_Start_UP(loading_Screen); return; }
        
        // Get Measurement Material Type DATA
        if (! setup_Get_Measurement_Material_Type_Data()) { failed_Start_UP(loading_Screen); return; }
        
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
        System.out.printf("\n\n%s \nMeal_Plan_Screen.java : Creating GUI Screen \n%s ", lineSeparator, lineSeparator); // Update
        
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
                macros_targets_plan_data_AL,
                macroTargets_ColumnNames,
                selected_Plan_ID,
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
                macros_left_plan_data_AL,
                macros_left_columnNames,
                selected_Plan_ID,
                null,
                macros_Left_Table_Col_To_Hide
        );
        
        addToContainer(macrosInfoJPanel, macros_Left_JTable, 0, macrosInfoJP_YPos += 1, 1, 1, 0.25, 0.25, "both", 30, 0, null);
        
        loading_Screen.increaseBar(10);
        
        //####################################################
        // Centre : JPanel (Meals)
        //####################################################
        /**
         *
         */
        for (Meals_And_Sub_Meals_OBJ meal_and_sub_meals : meals_and_sub_meals_AL)
        {
            // Get Details
            Meal_ID_OBJ meal_id_obj = meal_and_sub_meals.get_Meal_ID_OBJ();
            int draft_meal_id = meal_id_obj.get_Draft_Meal_ID();
            
            // Get Associated Sub-Meals
            LinkedHashMap<Integer, ArrayList<ArrayList<Object>>> sub_Meal_DATA = meal_and_sub_meals.get_Sub_Meals_Data_Map();
            
            // Total Meals DATA
            ArrayList<Object> total_Meal_DATA = total_Meals_Data_Map.get(draft_meal_id);
            
            // Create MealManager
            MealManager mealManager = new MealManager(
                    this,
                    shared_Data_Registry,
                    db,
                    macros_Left_JTable,
                    meal_id_obj,
                    sub_Meal_DATA,
                    total_Meal_DATA
            );
            
            // ADD to GUI
            add_And_Replace_MealManger_POS_GUI(mealManager, false, false); // Add to GUI
            
            // Update Progress
            loading_Screen.increaseBar(1 + sub_Meal_DATA.size()); // + original meal + the sub-meal
        }
        
        loading_Screen.increaseBar(8);
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
        
        macros_left_columnNames = null;
        macroTargets_ColumnNames = null;
        
        macros_targets_plan_data_AL = null;
        macros_left_plan_data_AL = null;
        
        meals_and_sub_meals_AL.clear();
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
            ArrayList<ArrayList<Object>> db_results = db.get_2D_Query_AL_Object(queryX, null, errorMSG, false);
            
            // App Must assume by default there is a selected user and 1 plan active otherwise this causes an eror
            user_id = (Integer) db_results.getFirst().get(0);
            selected_Plan_Version_ID = (Integer) db_results.getFirst().get(1);
            selected_Plan_ID = (Integer) db_results.getFirst().get(2);
            plan_Name = (String) db_results.getFirst().get(3);
            
            System.out.printf("\n\nUser_ID : %s \nPlan_Version_ID  : %s \nPlan_ID : %s \nPlan_Name : %s",
                    user_id, selected_Plan_Version_ID, selected_Plan_ID, plan_Name);
            
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
        //########################################
        // Get Column Names
        //########################################
        try
        {
            // column names : ingredients_in_sections_of_meal_calculation
            ingredients_Column_Names = db.get_Column_Names_AL("draft_gui_ingredients_in_sections_of_meal_calculation");
            
            // column names : total_meal_view
            meal_total_column_Names = db.get_Column_Names_AL("draft_gui_total_meal_view");
            
            // column names : plan_macro_target_calculations
            macroTargets_ColumnNames = db.get_Column_Names_AL("draft_gui_plan_macro_target_calculations");
            
            // Get table column names for plan_macros_left
            macros_left_columnNames = db.get_Column_Names_AL("draft_gui_plan_macros_left");
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s \n%s Error \n%s \n\n%s", lineSeparator, get_Class_And_Method_Name(), lineSeparator, e);
            return false;
        }
        
        //########################################
        // Column Names : Total_Meal_View
        //########################################
        for (int pos = 0; pos < meal_total_column_Names.size(); pos++)
        {
            // Get Column Name
            String column_name = meal_total_column_Names.get(pos);
            
            // See if the column Name is an Other Columns Enum
            Optional<Total_Meal_Macro_Columns> column_enum = My_Enum.get_Enum_From_Key(Total_Meal_Macro_Columns.class, column_name);
            if(column_enum.isPresent())
            {
                total_meal_macro_col_positions.put(column_enum.get(), pos);
                continue;
            }
            
            // See if the column Name is an Other Columns Enum
            Optional<Total_Meal_Other_Columns> other_column_enum = My_Enum.get_Enum_From_Key(Total_Meal_Other_Columns.class, column_name);
            if(other_column_enum.isPresent())
            {
                total_meal_other_cols_positions.put(other_column_enum.get(), pos);
            }
        }
        
        //########################################
        // Output
        //########################################
        return true;
    }
    
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
            meal_Count_Results = db.get_2D_Query_AL_Object(plan_Counts_Query, counts_Params, plan_Counts_ErrorMSG, false);
            
            // Format Results
            no_of_meals = (Integer) meal_Count_Results.getFirst().get(0);
            no_of_sub_meals = (Integer) meal_Count_Results.getFirst().get(1);
            
            System.out.printf("\n\n%s \nSuccessfully Got Meal Counts \n%s \nMeals In Plan: %s\nSub-Meals In Plan: %s",
                    lineSeparator, lineSeparator, no_of_meals, no_of_sub_meals);
            
            return true;  // Execute Query
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
    private boolean setup_transfer_Plan_Data()
    {
        //###############################################
        // Variables
        //###############################################
        LinkedHashSet<Pair<String, Object[]>> upload_queries_and_params = new LinkedHashSet<>();
        String errorMSG = "Unable to Transfer Plan Data!";
        
        //###############################################
        // Upload Queries
        //###############################################
        
        // Delete Users Old Active Plan and Replace
        String query1 = "DELETE FROM draft_plans WHERE plan_id = ?";
        upload_queries_and_params.add(new Pair<>(query1, new Object[]{ selected_Plan_ID }));
        
        // Create New Draft Plan Based On Active Plan
        String query2 = "INSERT INTO draft_plans (plan_id, user_id) VALUES (?,?);";
        upload_queries_and_params.add(new Pair<>(query2, new Object[]{ selected_Plan_ID, user_id }));
        
        //####################################
        // Execute Upload Statements
        //####################################
        if (! (db.upload_Data_Batch(upload_queries_and_params, errorMSG)))
        {
            System.err.printf("\n\n%s \n%s Error \n%s \nFailed Transferring Plan_Data",
                    lineSeparator, get_Class_And_Method_Name(), lineSeparator);
            
            return false;
        }
        
        //###############################################
        // Output
        //###############################################
        System.out.printf("\n\n%s \nPlanData Successfully Transferred! \n%s", lineSeparator, lineSeparator);
        System.out.printf("\nChosen Plan: %s  \nChosen Plan Name: %s", selected_Plan_Version_ID, plan_Name);
        
        return true;
    }
    
    private boolean setup_transfer_Targets()
    {
        //###############################################
        // Variables
        //###############################################
        LinkedHashSet<Pair<String, Object[]>> upload_queries_and_params = new LinkedHashSet<>();
        String errorMSG = "Unable to Transfer Plan Macro Targets!";
        
        //###############################################
        // Upload Queries
        //###############################################
        // Insert Plan_Version Macros
        String query_03 = """
                
                INSERT INTO draft_macros_per_pound_and_limits
                (
                    plan_id,
                    user_id,
                    current_weight_kg,
                    current_weight_in_pounds,
                    body_fat_percentage,
                    protein_per_pound,
                    carbohydrates_per_pound,
                    fibre,
                    fats_per_pound,
                    saturated_fat_limit,
                    salt_limit,
                    water_target,
                    additional_calories
                )
                SELECT
                
                    ?,
                    user_id,
                    current_weight_kg,
                    current_weight_in_pounds,
                    body_fat_percentage,
                    protein_per_pound,
                    carbohydrates_per_pound,
                    fibre,
                    fats_per_pound,
                    saturated_fat_limit,
                    salt_limit,
                    water_target,
                    additional_calories
                
                FROM macros_per_pound_and_limits
                WHERE plan_version_id = ?;""";
        
        upload_queries_and_params.add(new Pair<>(query_03, new Object[]{ selected_Plan_ID, selected_Plan_Version_ID }));
        
        //###############################################
        // Execute Upload Statements
        //###############################################
        if (! (db.upload_Data_Batch(upload_queries_and_params, errorMSG)))
        {
            System.err.printf("\n\n%s \n%s Error \n%s \nFailed Transferring Macro Plan Targets!",
                    lineSeparator, get_Class_And_Method_Name(), lineSeparator);
            
            return false;
        }
        
        //###############################################
        // Output
        //###############################################
        System.out.printf("\n\n%s \nMacro Plan Target Data Successfully Transferred! \n%s", lineSeparator, lineSeparator);
        return true;
    }
    
    private boolean setup_transfer_Meal_Data()
    {
        //################################################################
        // Variables
        //################################################################
        LinkedHashSet<Pair<String, Object[]>> upload_queries_and_params = new LinkedHashSet<>();
        String errorMSG = "Unable to Transfer Meals Data Into Plan!";
        
        
        //################################################################
        // Transferring Meals From Versioned to Draft Meals
        //################################################################
        
        // Copy Versioned Meals From Plan_Version Into Temp Table ORDERED by meal_in_plan_version_id
        String query_1 = """
                CREATE TEMPORARY TABLE temp_versioned_meals AS
                
                    SELECT
                
                        ROW_NUMBER() OVER (ORDER BY meal_in_plan_version_id ASC) AS rn,
                
                        meal_in_plan_version_id,
                        meal_in_plan_id,
                
                        plan_version_id,
                        date_time_last_edited,
                        meal_name,
                        meal_time
                
                    FROM meals_in_plan_versions
                    WHERE plan_version_id = ?
                    ORDER BY meal_in_plan_version_id ASC;""";
        
        upload_queries_and_params.add(new Pair<>(query_1, new Object[]{ selected_Plan_Version_ID }));
        
        // Insert Into Draft Meals Using Meal Versions MetaData
        String query_03 = """
                INSERT INTO draft_meals_in_plan
                (
                    meal_in_plan_id,
                    plan_id,
                    date_time_last_edited,
                    meal_name,
                    meal_time
                )
                SELECT
                
                    meal_in_plan_id,
                    ?,
                    date_time_last_edited,
                    meal_name,
                    meal_time
                
                FROM temp_versioned_meals
                ORDER BY rn;""";
        
        upload_queries_and_params.add(new Pair<>(query_03, new Object[]{ selected_Plan_ID }));
        
        // Insert Created Meals Into Anchor Table & Order Draft Meals By Creation Order (ID)
        String query_04 = """
                CREATE TEMPORARY TABLE temp_draft_meals_anchor AS
                SELECT
                
                    ROW_NUMBER() OVER (ORDER BY draft_meal_in_plan_id ASC) AS rn,
                    draft_meal_in_plan_id,
                    meal_in_plan_id
                
                
                FROM draft_meals_in_plan
                WHERE plan_id = ?
                ORDER BY draft_meal_in_plan_id ASC;""";
        
        upload_queries_and_params.add(new Pair<>(query_04, new Object[]{ selected_Plan_ID }));
        
        //################################################################
        // Transferring Versioned Sub-Meals To Draft
        //################################################################
        
        // Insert All Sub-Meals From Versioned Into Temp
        String query_05 = """
                CREATE TEMPORARY TABLE temp_versioned_sub_meals AS
                
                    SELECT
                
                        ROW_NUMBER() OVER (ORDER BY div_meal_sections_version_id ASC) AS rn,
                
                        div_meal_sections_version_id,
                        div_meal_sections_id,
                
                        meal_in_plan_version_id,
                        date_time_last_edited,
                        sub_meal_name,
                        sub_meal_time
                
                    FROM divided_meal_sections_versions
                    WHERE plan_version_id = ?
                    ORDER BY div_meal_sections_version_id ASC;""";
        
        upload_queries_and_params.add(new Pair<>(query_05, new Object[]{ selected_Plan_ID }));
        
        // Insert Versioned Sub-Meals Into Draft Sub-Meals In Order
        String query_06 = """
                INSERT INTO draft_divided_meal_sections
                (
                    div_meal_sections_id,
                    draft_meal_in_plan_id,
                    plan_id,
                    date_time_last_edited,
                    sub_meal_name,
                    sub_meal_time
                )
                SELECT
                
                    S.div_meal_sections_id,
                    T.draft_meal_in_plan_id,
                    ?,
                    S.date_time_last_edited,
                    S.sub_meal_name,
                    S.sub_meal_time
                
                FROM temp_versioned_sub_meals S
                
                INNER JOIN temp_versioned_meals M
                    ON S.meal_in_plan_version_id = M.meal_in_plan_version_id
                
                INNER JOIN temp_draft_meals_anchor T
                    ON M.meal_in_plan_id = T.meal_in_plan_id
                
                ORDER BY S.rn;""";
        
        upload_queries_and_params.add(new Pair<>(query_06, new Object[]{ selected_Plan_ID }));
        
        // Insert Created Sub-Meals Into Anchor Table & Order Draft Sub-Meals By Creation Order (ID)
        String query_07 = """
                CREATE TEMPORARY TABLE tmp_draft_sub_meal_anchors AS
                SELECT
                
                    ROW_NUMBER() OVER (ORDER BY draft_div_meal_sections_id ASC) AS rn,
                    draft_div_meal_sections_id,
                    div_meal_sections_id
                
                FROM draft_divided_meal_sections
                WHERE plan_id = ?
                ORDER BY draft_div_meal_sections_id ASC;""";
        
        upload_queries_and_params.add(new Pair<>(query_07, new Object[]{ selected_Plan_ID }));
        
        //################################################################
        // Transferring Ingredients From Versioned Sub-Meals To Draft
        //################################################################
        
        // Insert Ingredients By Sub-Meal Order then by Index Order (Creation Order)
        String query_08 = """
                INSERT INTO draft_ingredients_in_sections_of_meal
                (
                    draft_div_meal_sections_id,
                    ingredient_id,
                    pdid,
                    quantity
                )
                SELECT
                
                      A.draft_div_meal_sections_id,
                      I.ingredient_id,
                      I.pdid,
                      I.quantity
                
                FROM ingredients_in_sections_of_meal I
                
                INNER JOIN temp_versioned_sub_meals T
                    ON I.div_meal_sections_version_id = T.div_meal_sections_version_id
                
                INNER JOIN tmp_draft_sub_meal_anchors A
                   ON T.div_meal_sections_id = A.div_meal_sections_id
                
                ORDER BY -- Order by sub_meals div order in temp table & then Ingredient index Order
                    T.rn ASC, I.ingredients_index ASC;""";
        
        upload_queries_and_params.add(new Pair<>(query_08, null));
        
        //################################################################
        // Execute
        //################################################################
        if (! (db.upload_Data_Batch(upload_queries_and_params, errorMSG)))
        {
            System.err.printf("\n\n%s \n%s Error \n%s \nFailed Transferring Plan Meals Data!",
                    lineSeparator, get_Class_And_Method_Name(), lineSeparator);
            
            return false;
        }
        
        //################################################################
        // Output
        //################################################################
        System.out.printf("\n\n%s \nMeal Ingredients Successfully Transferred! \n%s", lineSeparator, lineSeparator);
        
        return true;
    }
    
    //############################
    //  Transfer Meta Data
    //###########################
    public boolean setup_Get_Ingredient_Types_And_Ingredient_Names()
    {
        String methodName = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName());
        
        //#######################################
        // Create Get Query Results
        //#######################################
        /*
            This query needs to be a left join as this call gets all ingredient types & joins their associated ingredients if any
            if the join has 0 ingredients its null and the ingredient type is recorded by itself with no ingredients
            if the ingredients do exist its joined into a map and processed
         */
        
        String query = """
                SELECT
                
                    T.ingredient_type_id AS type_id,
                	T.ingredient_type_name AS type_name,
                	T.is_system,
                
                	json_group_array(
                        JSON_OBJECT('id', I.ingredient_id,
                            'is_System', I.is_System,
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
                boolean is_System = ((int) row.get(2)) == 1; // 1 is true
                
                Ingredient_Type_ID_OBJ type_OBJ = new Ingredient_Type_ID_OBJ(type_ID, is_System, type_name);
                
                // Add to DATA
                shared_Data_Registry.add_Ingredient_Type(type_OBJ, false); // Add ingredient Type
                
                //#########################
                // Parsing JSON DATA
                //#########################
                JsonNode json_array = mapper.readTree((String) row.get(3));
                
                for (JsonNode node : json_array) // For loop through each node of Ingredients
                {
                    JsonNode id = node.get("id"); // Per Object Ingredient Object get ID
                    
                    if (id.isNull()) { continue; }  // If values are empty skip
                    
                    // Convert Ingredient Values
                    int id_Value = id.asInt();
                    String name = node.get("name").asText();
                    boolean is_System_Ingredient = (node.get("is_System").asInt()) == 1;
                    
                    // Add Ingredient_Name to DATA IF not NULL
                    Ingredient_Name_ID_OBJ ingredient_Name_ID = new Ingredient_Name_ID_OBJ(
                            id_Value,
                            is_System_Ingredient,
                            name,
                            type_OBJ
                    );
                    
                    shared_Data_Registry.add_Ingredient_Name(ingredient_Name_ID, true);
                }
            }
            
            System.out.println("    \n.) Ingredient Types / Names Objects Successfully Transferred! ");
            return true;
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s error \n\n%s", methodName, e);
            return false;
        }
    }
    
    public boolean setup_Get_Stores_Data()
    {
        //#######################################
        // Create Get Query Results
        //#######################################
        String
                errorMSG = "Error, Unable to get Ingredient Stores in Plan!",
                query = "SELECT * FROM stores ORDER BY store_name ASC;";
        
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
            shared_Data_Registry.add_Store(
                    new Store_ID_OBJ(
                            (int) row.get(0),
                            (int) row.get(1) == 1, // IF True = 1
                            (String) row.get(2)),
                    false
            );
        }
        
        //#######################################
        // Output
        //#######################################
        System.out.println("    \n.) Store Objects Successfully Transferred!");
        return true;
    }
    
    public boolean setup_Get_Measurement_Material_Type_Data()
    {
        // Set Variables
        String
                query = "SELECT * FROM measurement_material_type ORDER BY measurement_material_type_name;",
                errorMSG = "Unable, to get Measurements Material Type Data";
        
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
            String measurement_material_type_name = (String) row.get(1);
            
            shared_Data_Registry.add_Measurement_Material_Type(
                    new Measurement_Material_Type_ID_OBJ(id, true, measurement_material_type_name),
                    false
            );
        }
        
        // Return Output
        System.out.println("    \n.) Measurement Material Type Objects Successfully Transferred!");
        return true;
    }
    
    public boolean setup_Get_Measurement_Data()
    {
        // Set Variables
        String
                query = "SELECT * FROM measurements ORDER BY unit_name;",
                errorMSG = "Unable, to get Measurements Data";
        
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
            boolean is_system = ((int) row.get(1)) == 1; // 1 = true
            String unit_Name = (String) row.get(2);
            String unit_Symbol = (String) row.get(3);
            int measurement_Material_Type_ID = (int) row.get(4);
            
            shared_Data_Registry.add_Measurement(
                    new Measurement_ID_OBJ(
                            id,
                            is_system,
                            unit_Name,
                            unit_Symbol,
                            shared_Data_Registry.get_Measurement_Material_Type_ID_OBJ(measurement_Material_Type_ID)
                    ),
                    false
            );
        }
        
        // Return Output
        System.out.println("    \n.) Measurement Objects Successfully Transferred!");
        return true;
    }
    
    //###########################
    // Meals Data
    //###########################
    public boolean setup_Get_Meal_Data()
    {
        String methodName = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName());
        
        //########################################################################
        // Create Get Query Results
        //########################################################################
        String query = """
                -- Divs with Ingredients
                
                SELECT
                    M.draft_meal_in_plan_id,
                    M.meal_in_plan_id,
                    M.plan_id,
                    M.Meal_Name,
                    M.meal_time,
                
                    /*M.div_meal_sections_id,*/
                
                    json_group_array(
                        JSON_OBJECT(
                            'div_id',      I.draft_div_meal_sections_id,
                            'index',       I.draft_ingredients_index,
                
                            'type_id',     I.ingredient_type_id,
                            'id',          I.ingredient_id,
                
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
                            'calories',    I.calories
                        )
                    ) AS matched_ingredients
                
                FROM draft_meals_in_plan M
                
                INNER JOIN draft_divided_meal_sections D
                    ON M.draft_meal_in_plan_id = D.draft_meal_in_plan_id
                
                INNER JOIN draft_ingredients_in_sections_of_meal_calculation I
                    ON D.draft_div_meal_sections_id = I.draft_div_meal_sections_id
                
                WHERE M.plan_id = ?
                GROUP BY M.draft_meal_in_plan_id  /*, M.div_meal_sections_id*/
                ORDER BY M.meal_time ASC;""";
        
        String errorMSG = "Unable to get Meal Data!";
        
        Object[] params = new Object[]{ selected_Plan_ID };
        
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
         *
         */
        
        try
        {
            for (ArrayList<Object> row : results) // For LOOP through each Meal
            {
                //######################################################
                // Get Meal Info
                //#####################################################
                int draft_meal_id = (int) row.get(0);
                int source_meal_id = (int) row.get(1);
                
                String meal_name = (String) row.get(3);
                
                // Time Conversion
                String meal_Time_From_Db = (String) row.get(4);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                        meal_Time_From_Db.length() == 5 ? "HH:mm" : "HH:mm:ss"
                );
                
                LocalTime meal_Time = LocalTime.parse(meal_Time_From_Db, formatter);
                
                //######################################################
                // Create Meal & Sub-Meal Collections Per Meal & Add
                //######################################################
                Meal_ID_OBJ meal_ID_Obj = new Meal_ID_OBJ(draft_meal_id, source_meal_id, meal_name, meal_Time); //
                LinkedHashMap<Integer, ArrayList<ArrayList<Object>>> sub_meals_and_ingredients_Map = new LinkedHashMap<>();
                
                meals_and_sub_meals_AL.add(new Meals_And_Sub_Meals_OBJ(meal_ID_Obj, sub_meals_and_ingredients_Map));
                
                //######################################################
                // Parsing JSON DATA - Sub-Meals -> Ingredients
                //######################################################
                ObjectMapper mapper = new ObjectMapper();
                JsonNode ingredients_json_array = mapper.readTree((String) row.get(5));
                
                //######################################
                // For Each Ingredient In Sub-Meal
                //######################################
                for (JsonNode ingredient_node : ingredients_json_array) // For loop through each Ingredient belonging to a  Sub-DIV
                {
                    //###########################
                    // Store Ingredients Data
                    //###########################
                    ArrayList<Object> ingredients_values = new ArrayList<>();
                    
                    //###########################
                    // Get Ingredients DATA
                    //###########################
                    int div_id = ingredient_node.get("div_id").asInt();
                    //ingredients_values.add(div_id);
                    
                    ingredients_values.add(ingredient_node.get("index").asInt());
                    
                    // Ingredient Name OBJ
                    int ingredient_Type_ID = ingredient_node.get("type_id").asInt();
                    ingredients_values.add(shared_Data_Registry.get_Type_ID_Obj_By_ID(ingredient_Type_ID));
                    
                    // Ingredient Name Object
                    int ingredient_Name_ID = ingredient_node.get("id").asInt();
                    ingredients_values.add(shared_Data_Registry.get_Ingredient_Name_ID_OBJ_By_ID(ingredient_Name_ID));
                    
                    // Quantity
                    ingredients_values.add(new BigDecimal(ingredient_node.get("quantity").asText()));
                    
                    // Macro Values
                    ingredients_values.add(ingredient_node.get("gi").asInt());
                    ingredients_values.add(new BigDecimal(ingredient_node.get("protein").asText()));
                    ingredients_values.add(new BigDecimal(ingredient_node.get("carbs").asText()));
                    ingredients_values.add(new BigDecimal(ingredient_node.get("sugar_carbs").asText()));
                    ingredients_values.add(new BigDecimal(ingredient_node.get("fibre").asText()));
                    ingredients_values.add(new BigDecimal(ingredient_node.get("fat").asText()));
                    ingredients_values.add(new BigDecimal(ingredient_node.get("sat_fat").asText()));
                    ingredients_values.add(new BigDecimal(ingredient_node.get("salt").asText()));
                    ingredients_values.add(new BigDecimal(ingredient_node.get("water").asText()));
                    ingredients_values.add(new BigDecimal(ingredient_node.get("calories").asText()));
                    
                    ingredients_values.add("Delete Row"); // For Delete BTN
                    
                    //###########################
                    // Store DATA in DIV
                    //###########################
                    sub_meals_and_ingredients_Map // For Meals and Sub-Meals Map
                            .computeIfAbsent(div_id, k -> new ArrayList<>()) // Get or, Section for Div with ingredients
                            .add(ingredients_values); // Add Ingredients Values to list
                }
            }
            
            System.out.printf("\n\n%s \nIngredients In Meal Data Successfully Transferred  \n%s ", lineSeparator, lineSeparator);
            return true;
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s ERROR \n%s", methodName, e);
            return false;
        }
    }
    
    public boolean setup_Get_Total_Meals_Data()
    {
        //#################################
        // Create Get Query Results
        //#################################
        String query = """
                SELECT *
                FROM draft_gui_total_meal_view
                WHERE draft_meal_in_plan_id IN (SELECT draft_meal_in_plan_id FROM draft_meals_in_plan WHERE plan_id = ?)
                ORDER BY draft_meal_in_plan_id;""";
        
        String errorMSG = "Unable to get Total Meals Data for Plan!!";
        
        Object[] params = new Object[]{ selected_Plan_ID };
        
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
            int draft_meal_id = (int) meal_Data.getFirst(); // Get Meal ID
            
            total_Meals_Data_Map.put(draft_meal_id, meal_Data); // Add total Meals Data to storage
        }
        
        //#################################
        // Return Output
        //#################################
        System.out.printf("\n\n%s \nTotal Meal Data Successfully Retrieved \n%s ", lineSeparator, lineSeparator);
        
        return true;
    }
    
    private boolean setup_Get_Macros_Targets_Data()
    {
        String query_PlanCalc = "SELECT * from draft_gui_plan_macro_target_calculations WHERE plan_id = ?";
        String errorMSG = "Error, Gathering Macros Targets Data!";
        
        Object[] params_planCalc = new Object[]{ selected_Plan_ID };
        
        // Execute
        try
        {
            macros_targets_plan_data_AL = db.get_2D_Query_AL_Object(query_PlanCalc, params_planCalc, errorMSG, false);
            System.out.printf("\n\n%s \nPlan Targets Data Successfully Retrieved \n%s ", lineSeparator, lineSeparator);
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
        String query_Macros = "SELECT * from draft_gui_plan_macros_left WHERE plan_id = ?;";
        String errorMSG_ML = "Error, Unable to get Plan Macros Left!";
        
        Object[] params_macros = new Object[]{ selected_Plan_ID };
        
        try
        {
            macros_left_plan_data_AL = db.get_2D_Query_AL_Object(query_Macros, params_macros, errorMSG_ML, false);
            System.out.printf("\n\n%s \nPlan Macros Left Data Successfully Retrieved \n%s ", lineSeparator, lineSeparator);
            return true;
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s \n%s Error \n%s  \n%s", lineSeparator, get_Class_And_Method_Name(), lineSeparator, e);
            return false;
        }
    }
    
    //#################################################
    // Others
    //#################################################
    private boolean transfer_Targets(int fromPlan, int toPlan, boolean deleteFromToPlan, boolean showConfirmMsg)
    {
        return false;
    }
    
    private boolean transfer_Meal_Ingredients(int fromPlanID, int toPlanID)
    {
        return false;
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
            line_Chart_Btn_Action_Open_Screen();
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
    // ################################################################
    private void delete_btnAction()
    {
        //###########################################################
        // Ask for Confirmation
        //###########################################################
        if (! areYouSure("Delete All Meals", "Are you want to 'DELETE' all the meals in this plan?")) { return; }
        
        //###########################################################
        // Delete all the meals in the plans in SQL
        //###########################################################
        String queryDelete = "DELETE FROM table_draft_meals  WHERE plan_id = ?";
        
        Object[] params = new Object[]{ selected_Plan_ID };
        
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
    public boolean is_PieChart_Screen_Open()
    {
        return pie_Chart_Screen != null;
    }
    
    public void removePieChartScreen()
    {
        pie_Chart_Screen = null;
    }
    
    private void pieChart_BtnAction_OpenScreen()
    {
        if (is_PieChart_Screen_Open())
        {
            pie_Chart_Screen.makeJFrameVisible();
            return;
        }
        
        pie_Chart_Screen = new PieChart_Screen_MPS(db, shared_Data_Registry, this);
    }
    
    // #############################
    // PieChart DATA Methods
    // #############################
    private void clear_Pie_Chart_Dataset()
    {
        if (! is_PieChart_Screen_Open()) { return; }
        
        pie_Chart_Screen.clear();
    }
    
    private void refresh_Pie_Chart_Data()
    {
        if (! is_PieChart_Screen_Open()) { return; }
        
        pie_Chart_Screen.refresh();
    }
    
    private void update_Pie_Chart_Meal_Name(MealManager mealManager)
    {
        if (! is_PieChart_Screen_Open()) { return; }
        
        pie_Chart_Screen.update_PieChart_MealName(mealManager);
    }
    
    private void update_Pie_Chart_Meal_Time(MealManager mealManager)
    {
        if (! is_PieChart_Screen_Open()) { return; }
        
        pie_Chart_Screen.update_PieChart_MealTime(mealManager);
    }
    
    private void update_Pie_Chart_DATA(MealManager mealManager)
    {
        if (! is_PieChart_Screen_Open()) { return; }
        
        pie_Chart_Screen.updateData(mealManager);
    }
    
    private void add_Meal_To_Pie_Chart_Screen(MealManager mealManager)
    {
        if (! is_PieChart_Screen_Open()) { return; }
        
        pie_Chart_Screen.add_MealManager_To_GUI(mealManager);
    }
    
    private void delete_Meal_From_Pie_Chart_Screen(MealManager mealManager)
    {
        if (! is_PieChart_Screen_Open()) { return; }
        
        // Data Handling already been processed, screen just needs to be re-drawn
        pie_Chart_Screen.deleted_MealManager_PieChart(mealManager);
    }
    
    // ###############################################################
    // Line Chart BTN Actions
    // ###############################################################
    private boolean is_Line_Chart_Screen_Open()
    {
        return line_Chart != null;
    }
    
    public void remove_Line_Chart_Screen()
    {
        line_Chart = null;
    }
    
    private void line_Chart_Btn_Action_Open_Screen()
    {
        if (! is_Line_Chart_Screen_Open())
        {
            line_Chart = new LineChart_MPS(db, shared_Data_Registry, this);
            return;
        }
        
        line_Chart.makeJFrameVisible();
    }
    
    // #############################
    // LineChart DATA Methods
    // #############################
    private void updateLineChartData(MealManager mealManager, LocalTime previousTime, LocalTime currentTime)
    {
        if (! is_Line_Chart_Screen_Open()) { return; }
        
        line_Chart.update_MealManager_ChartData(mealManager, previousTime, currentTime);
    }
    
    private void add_Meal_To_LineChart(MealManager mealManager)
    {
        if (! is_Line_Chart_Screen_Open()) { return; }
        
        line_Chart.add_New_MealManager_Data(mealManager);
    }
    
    private void deleteLineChartData(LocalTime currentTime)
    {
        if (! is_Line_Chart_Screen_Open()) { return; }
        
        line_Chart.delete_MealManager_Data(currentTime);
    }
    
    private void clear_Line_Chart_DataSet()
    {
        if (! is_Line_Chart_Screen_Open()) { return; }
        
        line_Chart.clear_LineChart_Dataset();
    }
    
    private void refresh_LineChart_Data()
    {
        if (! is_Line_Chart_Screen_Open()) { return; }
        
        line_Chart.refresh_Data();
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
        if (! (transfer_Meal_Ingredients(selected_Plan_Version_ID, selected_Plan_ID))) // transfer meals and ingredients from temp plan to original plan
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
        MealManager mealManager = new MealManager(this, shared_Data_Registry, db, macros_Left_JTable);
        
        //###############################################
        // If Object Creation Failed Exit
        //###############################################
        if (! mealManager.isObjectCreated()) { return; }
        
        JOptionPane.showMessageDialog(null, String.format("Successfully Created Meal in %s at [%s]",
                mealManager.get_Current_Meal_Name(), mealManager.get_Current_Meal_Time()));
              
        //###############################################
        // ADD to GUI & Charts
        //###############################################
        add_And_Replace_MealManger_POS_GUI(mealManager, true, true); // Add to GUI
        
        //###############################################
        // Add to External Charts
        //###############################################
        update_External_Charts(true, "add", mealManager, null, mealManager.get_Current_Meal_Time());
    }
    
    public void add_And_Replace_MealManger_POS_GUI(MealManager mealManager, boolean reOrder, boolean expandView)
    {
        //###############################################
        // Exit Clause
        //###############################################
        if (! mealManager.isObjectCreated()) { return; }
        
        //###############################################
        
        //###############################################
        if (! reOrder) // Just add to GUI / Add to GUI Meal Manager & Its Space Divider
        {
            addToContainer(scroll_JPanel_Center, mealManager.get_Collapsible_JP_Obj(), 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
            addToContainer(scroll_JPanel_Center, mealManager.getSpaceDividerForMealManager(), 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "both", 50, 0, null);
        }
        else // Clear and Redraw
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
                    mm.get_Draft_Meal_In_Plan_ID(), mm.get_Current_Meal_Name(), mm.get_Current_Meal_Time());
            
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
            
            String query = "DELETE FROM %s WHERE plan_id = ?;";
            
            Object[] params = new Object[]{ selected_Plan_Version_ID };
            
            if (! (db.upload_Data(query, params, "Error 2, Unable to Save Meal Data!"))) { return; }
        }
        else // because there are meals save them
        {
            if ((! (transfer_Meal_Ingredients(selected_Plan_ID, selected_Plan_Version_ID)))) // transfer meals and ingredients from temp plan to original plan
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
    
    private boolean is_IngredientScreen_Open()
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
        macros_Targets_Screen = new Macros_Targets_Screen(db, this, selected_Plan_ID, plan_Name);
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
    
    // booleans
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
        
        if (transfer_Targets(selected_Plan_ID, selected_Plan_Version_ID, false, showUpdateMsg))
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
            pie_Chart_Screen.window_Closed_Event();
        }
        if (is_Line_Chart_Screen_Open())
        {
            line_Chart.window_Closed_Event();
        }
        
        // ##########################################
        // Close PieCharts Open by MealManagers
        // ##########################################
        Iterator<MealManager> it = shared_Data_Registry.get_MealManager_ArrayList().iterator();
        while (it.hasNext())
        {
            it.next().close_Pie_Chart_Screen();
            it.remove();
        }
        
        // ##########################################
        // Close DB Pool
        // ##########################################
        if (db != null) { db.close_Connection(); }
    }
    
    public void update_External_Charts(boolean mealPlanScreen_Action, String action, MealManager mealManager,
                                       LocalTime previousMealTime, LocalTime currentMealTime)
    {
        //####################################################################
        // MealPlanScreen
        //####################################################################
        if (mealPlanScreen_Action)
        {
            switch (action)
            {
                case "add" -> // New MealManager Added to GUI
                {
                    add_Meal_To_LineChart(mealManager); // LineChart Add
                    
                    add_Meal_To_Pie_Chart_Screen(mealManager); // PieChart MPS Screen
                }
                case "clear" ->  // Delete Button requested on MealPlanScreen
                {
                    clear_Line_Chart_DataSet(); // Clear LineChart Data
                    
                    clear_Pie_Chart_Dataset(); // Clear PieChart Screen
                }
                case "refresh" -> // Refresh Button requested on MealPlanScreen
                {
                    refresh_LineChart_Data(); // Refresh LineChart Data
                    
                    refresh_Pie_Chart_Data(); //Refresh PieChart Screen
                }
            }
            
            //##########################
            // Exit
            //##########################
            return;
        }
        
        //####################################################################
        // MealManager Requested Action
        //####################################################################
        switch (action)
        {
            case "update" ->  // Update Data
            {
                updateLineChartData(mealManager, previousMealTime, currentMealTime);   // Update LineChart Data
                
                update_Pie_Chart_DATA(mealManager);  // Update PieChart DATA
            }
            case "delete" -> // Deleted MealManager
            {
                deleteLineChartData(previousMealTime);   // Delete LineChart Data
                
                delete_Meal_From_Pie_Chart_Screen(mealManager); // Delete PieChart AKA Re-draw GUI
            }
            case "mealTime" -> // MealTime on MealManager Changed
            {
                // Change data points time on LineChart Data
                updateLineChartData(mealManager, previousMealTime, currentMealTime);
                
                // Update PieChart Title OF Meal & Refresh Interface
                update_Pie_Chart_Meal_Time(mealManager);
            }
            case "mealName" ->  // Meal Name on MealManager Changed
            {
                // LineChart = Nothing Changes
               
                update_Pie_Chart_Meal_Name(mealManager);  // Change PieChart MealName
            }
            case "refresh" ->   // Change Meal Managers Data
            {
                // Refresh MealManager
                updateLineChartData(mealManager, previousMealTime, currentMealTime);
                
                // Change PieChart MealName
                update_Pie_Chart_Meal_Name(mealManager);
            }
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
                /*if (transfer_Targets(selected_Plan_Version_ID, null, true, false))
                {
                    JOptionPane.showMessageDialog(this, "\n\nMacro-Targets Successfully Refreshed!!");
                    macrosTargetsChanged(false);
                    
                    macros_Targets_Table.refresh_Data();
                }*/
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
    
    public HashMap<Total_Meal_Other_Columns, Integer> get_TotalMeal_Other_Cols_Pos()
    {
        return total_meal_other_cols_positions;
    }
    
    public LinkedHashMap<Total_Meal_Macro_Columns, Integer> get_Total_Meal_Macro_Col_Pos()
    {
        return total_meal_macro_col_positions;
    }
    
    public LinkedHashMap<Total_Meal_Macro_Columns, String> get_Total_Meal_Macro_Symbols()
    {
        return total_meal_macro_symbol;
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
    {
        return ingredients_Table_Col_Avoid_Centering;
    }
    
    public ArrayList<String> getIngredients_In_Meal_Table_Col_To_Hide()
    {
        return ingredients_In_Meal_Table_Col_To_Hide;
    }
}
