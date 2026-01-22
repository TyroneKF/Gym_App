package App_Code.Objects.Screens;

import App_Code.Objects.Data_Objects.ID_Objects.MetaData_ID_Object.Meal_ID_OBJ;
import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.*;
import App_Code.Objects.Database_Objects.Fetched_Results;
import App_Code.Objects.Database_Objects.MyJDBC.MyJDBC_Sqlite;
import App_Code.Objects.Database_Objects.Shared_Data_Registry;
import App_Code.Objects.Table_Objects.Tables.Children.Ingredients_Table.Ingredients_Table_Columns;
import App_Code.Objects.Table_Objects.Tables.Children.View_Data_Tables.Children.MacrosLeft_Table;
import App_Code.Objects.Table_Objects.Tables.Children.View_Data_Tables.Children.MacrosTargets_Table;
import App_Code.Objects.Table_Objects.Tables.Children.View_Data_Tables.Children.Total_Meal_Table.Total_Meal_Macro_Columns;
import App_Code.Objects.Table_Objects.Tables.Children.View_Data_Tables.Children.Total_Meal_Table.Total_Meal_Other_Columns;
import App_Code.Objects.Enums.My_Enum;
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
    
    // Chart Screen Objects
    private PieChart_Screen_MPS pie_Chart_Screen = null;
    private LineChart_MPS line_Chart = null;
    
    //###############################################
    // Booleans
    //###############################################
    private boolean macro_Targets_Changed = false;
    private boolean screen_Created = false;
    
    //#########################################################################################
    // Collections
    //#########################################################################################
    
    
    //#######################################
    // Column Names
    //#######################################
    private ArrayList<String> meal_total_column_Names;
    private ArrayList<String> ingredients_Column_Names;
    private ArrayList<String> macros_left_columnNames;
    private ArrayList<String> macroTargets_ColumnNames;
    
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
    
    private final HashMap<Ingredients_Table_Columns, Integer> ingredients_table_cols_positions = new HashMap<>() // These 2 columns are needed for external charts
    {{
        put(Ingredients_Table_Columns.DRAFT_INGREDIENTS_INDEX, null);
        put(Ingredients_Table_Columns.INGREDIENT_TYPE_NAME, null);
        put(Ingredients_Table_Columns.INGREDIENT_NAME, null);
        put(Ingredients_Table_Columns.QUANTITY, null);
        put(Ingredients_Table_Columns.DELETE_BTN, null);
    }};
    
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
        
        shared_Data_Registry = new Shared_Data_Registry(this);
        
        Loading_Screen loading_Screen = new Loading_Screen(100);
        
        UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 16)); // Set up window msg font
        
        //###############################################################################
        //
        //###############################################################################
        /**
         *  1.) Getting Selected User Info & Their Active Plan Info         [3%]
         *  2.) Getting Table Column Names                                  [2%]
         *  3.) Getting Table Column Positions                              [2%]
         *
         *  4.) Transferring Plan Data To DRAFT Plan_Data                   [4%]
         *  5.) Transfer Plan Targets                                       [3%]
         *  6.) Transferring Meals Data                                     [4%]
         *
         *  7.) Get Ingredient Names & Types                                [4%]
         *  8.) Get Stores Data                                             [3%]
         *  9.) Get Measurement Material Type DATA                          [3%]
         * 10.) Get Measurement DATA                                       [4%]
         * 11.) System Variables DATA                                      [3%]
         *
         * 12.) Get Meals & Sub-Meals DATA                                  [6%]
         * 13.) Get Total Meals Data                                        [3%]
         * 14.) Get Macros Targets DATA                                     [3%]
         * 15.) Get Macros Left DATA                                        [3%]
         *
         * 16.) North GUI Setup : Icons                                     [5%]
         * 17.) Bottom GUI Setup : Macro_Targets / Macro_Left Table         [5%]
         * 18.) Centre GUI Setup : Create Meals                             [40%]
         */
        
        
        //####################################################
        // Get MetaData Methods
        //####################################################
        
        //  1.) Getting Selected User & Plan Info
        if (! setup_Get_User_And_Plan_Info(true, true, true, true))
        {
            failed_Start_UP(loading_Screen); return;
        }
        loading_Screen.increaseBar(3);
        
        // 2.) Getting Table Column Names
        if (! setup_Get_Column_Names()) { failed_Start_UP(loading_Screen); return; }
        loading_Screen.increaseBar(2);
        
        // 3.) Getting Table Column Positions
        setup_Configure_Table_Col_Positions();
        loading_Screen.increaseBar(2);
        
        //####################################################
        // Transfer Plan / Meal Data
        //####################################################
        
        // 4.) Transferring Plan Data To Draft Plan
        if (! setup_Transfer_Plan_Data()) { failed_Start_UP(loading_Screen); return; }
        loading_Screen.increaseBar(4);
        
        // 5.) Transferring Targets To Draft Plan
        if (! setup_Transfer_Macro_Targets_Data()) { failed_Start_UP(loading_Screen); return; }
        loading_Screen.increaseBar(3);
        
        // 6.) Transferring Plan Meals To Draft Meals
        if (! setup_Transfer_Meals_Data()) { failed_Start_UP(loading_Screen); return; }
        loading_Screen.increaseBar(4);
        
        //####################################################
        // Get Reference DATA Methods
        //####################################################
        System.out.printf("\n\n%s \nGetting Meta Data Objects \n%s ", lineSeparator, lineSeparator);
        
        // 7.) Get Ingredient Types Mapped to Ingredient Names
        if (! setup_Get_Ingredient_Types_And_Ingredient_Names()) { failed_Start_UP(loading_Screen); return; }
        loading_Screen.increaseBar(4);
        
        // 8.) Get Stores DATA
        if (! setup_Get_Stores_Data()) { failed_Start_UP(loading_Screen); return; }
        loading_Screen.increaseBar(3);
        
        // 9.) Get Measurement Material Type DATA
        if (! setup_Get_Measurement_Material_Type_Data()) { failed_Start_UP(loading_Screen); return; }
        loading_Screen.increaseBar(3);
        
        // 10.) Get Measurement DATA
        if (! setup_Get_Measurement_Data()) { failed_Start_UP(loading_Screen); return; }
        loading_Screen.increaseBar(4);
        
        // 11.) System Variables DATA
        if (! setup_Get_System_Variables()) { failed_Start_UP(loading_Screen); return; }
        loading_Screen.increaseBar(3);
        
        //####################################################
        // Get Meals DATA Methods
        //####################################################
        ArrayList<Meals_And_Sub_Meals_OBJ> meals_and_sub_meals_AL;
        LinkedHashMap<Integer, ArrayList<Object>> total_Meals_Data_Map;
        ArrayList<ArrayList<Object>> macros_targets_plan_data_AL;
        ArrayList<ArrayList<Object>> macros_left_plan_data_AL;
        
        try
        {
            meals_and_sub_meals_AL = setup_Get_Meal_Data();                 // 13.)  Get Meals Data
            loading_Screen.increaseBar(6);
            
            total_Meals_Data_Map = setup_Get_Total_Meals_Data();            // 14.) Get TotalMeals Data
            loading_Screen.increaseBar(3);
            
            macros_targets_plan_data_AL = setup_Get_Macros_Targets_Data();  // 15.)  Get MacroTargets DATA
            loading_Screen.increaseBar(3);
            
            macros_left_plan_data_AL = setup_Get_Macros_Left_Data();       // 16.)  Get MacrosLeft DATA
            loading_Screen.increaseBar(3);
        }
        catch (Exception e)
        {
            failed_Start_UP(loading_Screen);
            return;
        }
        
        //###############################################################################
        // Build GUI
        //###############################################################################
        System.out.printf("\n\n%s \nMeal_Plan_Screen.java : Creating GUI Screen \n%s ", lineSeparator, lineSeparator); // Update
        
        // Splitting Scroll JPanel
        scroll_JPanel_Center = new JPanel(new GridBagLayout());
        addToContainer(getScrollPaneJPanel(), scroll_JPanel_Center, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0, "center");
        
        JPanel scrollJPanelBottom = new JPanel(new GridBagLayout());
        addToContainer(getScrollPaneJPanel(), scrollJPanelBottom, 0, 1, 1, 1, 0.25, 0.25, "both", 0, 0, "end");
        
        //####################################################
        // North :  JPanel
        //####################################################
        icon_Setup(getMainNorthPanel()); // Icon Setup
        loading_Screen.increaseBar(5);
        
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
                shared_Data_Registry,
                macrosInfoJPanel,
                macros_targets_plan_data_AL,
                macroTargets_ColumnNames,
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
                shared_Data_Registry,
                macrosInfoJPanel,
                macros_left_plan_data_AL,
                macros_left_columnNames,
                null,
                macros_Left_Table_Col_To_Hide
        );
        
        addToContainer(macrosInfoJPanel, macros_Left_JTable, 0, macrosInfoJP_YPos += 1, 1, 1, 0.25, 0.25, "both", 30, 0, null);
        
        //############################
        // Increase Progress
        //############################
        loading_Screen.increaseBar(5);
        
        
        //####################################################
        // Centre : JPanel (Meals)
        //####################################################
        try
        {
            create_Meal_Objects_In_GUI(meals_and_sub_meals_AL, total_Meals_Data_Map, loading_Screen, 40);     // Add Meals to GUI
        }
        catch (Exception e)
        {
            failed_Start_UP(loading_Screen);
            return;
        }
        
        //###############################################################################
        // Initialization Finished
        //###############################################################################
        if (! loading_Screen.isFinished())
        {
            JOptionPane.showMessageDialog(getFrame(), "Error, in configuration! All Tasks Are Not Completed!");
            failed_Start_UP(loading_Screen);
            return;
        }
        
        screen_Created = true;
        
        setFrameVisibility(true);      // Make GUI Visible
        resizeGUI();                   // Resize GUi
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
    
    //#################################################
    // Get DATA Methods
    //#################################################
    private boolean setup_Get_User_And_Plan_Info(boolean user_id, boolean plan_id, boolean plan_version_id, boolean plan_name)
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
            
            // App Must assume by default there is a selected user and 1 plan active otherwise this causes an error
            if (user_id)
            {
                shared_Data_Registry.set_User_ID(((Integer) db_results.getFirst().getFirst()));
            } // user_id
            
            if (plan_id) { shared_Data_Registry.set_Selected_Plan_ID((Integer) db_results.getFirst().get(1)); }
            
            if (plan_name)
            {
                shared_Data_Registry.set_Plan_Name((String) db_results.getFirst().get(3));
            } // plan name
            
            if (plan_version_id) // plan version id
            {
                shared_Data_Registry.set_Selected_Plan_Version_ID((Integer) db_results.getFirst().get(2));
            }
            
            System.out.printf("\n\nUser_ID : %s \nPlan_Version_ID  : %s \nPlan_ID : %s \nPlan_Name : %s",
                    get_User_ID(), get_Selected_Plan_Version_ID(), get_Selected_Plan_ID(), get_Plan_Name());
            
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
        // Output
        //########################################
        return true;
    }
    
    private void setup_Configure_Table_Col_Positions()
    {
        //########################################
        // Column Names : Ingredients Table
        //########################################
        for (int pos = 0; pos < ingredients_Column_Names.size(); pos++)
        {
            String column_name = ingredients_Column_Names.get(pos);
            
            // See if the column Name is an Other Columns Enum
            Optional<Ingredients_Table_Columns> column_enum = My_Enum.get_Enum_From_Key(Ingredients_Table_Columns.class, column_name);
            
            if (column_enum.isEmpty()) { continue; }
            
            ingredients_table_cols_positions.put(column_enum.get(), pos);
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
            if (column_enum.isPresent())
            {
                total_meal_macro_col_positions.put(column_enum.get(), pos);
                continue;
            }
            
            // See if the column Name is an Other Columns Enum
            Optional<Total_Meal_Other_Columns> other_column_enum = My_Enum.get_Enum_From_Key(Total_Meal_Other_Columns.class, column_name);
            if (other_column_enum.isPresent())
            {
                total_meal_other_cols_positions.put(other_column_enum.get(), pos);
            }
        }
    }
    
    private Pair<Integer, Integer> setup_Get_Meal_Counts() throws Exception
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
        
        Object[] counts_Params = new Object[]{ get_User_ID() };
        
        ArrayList<ArrayList<Object>> meal_Count_Results;
        
        //#################################
        // Execute Query
        //#################################
        try
        {
            meal_Count_Results = db.get_2D_Query_AL_Object(plan_Counts_Query, counts_Params, plan_Counts_ErrorMSG, false);
            
            // Format Results
            int no_of_meals = (Integer) meal_Count_Results.getFirst().get(0);
            int no_of_sub_meals = (Integer) meal_Count_Results.getFirst().get(1);
            
            Pair<Integer, Integer> meal_Counts = new Pair<>(no_of_meals, no_of_sub_meals);
            
            System.out.printf("\n\n%s \nSuccessfully Got Meal Counts \n%s", lineSeparator, lineSeparator);
            
            return meal_Counts;  // Execute Query
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s \n%s Error \n%s  \n%s", lineSeparator, get_Class_And_Method_Name(), lineSeparator, e);
            throw new Exception("Failed");
        }
    }
    
    //#################################################
    // Transfer Data Methods
    //#################################################
    private boolean setup_Transfer_Plan_Data()
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
        upload_queries_and_params.add(new Pair<>(query1, new Object[]{ get_Selected_Plan_ID() }));
        
        // Create New Draft Plan Based On Active Plan
        String query2 = "INSERT INTO draft_plans (plan_id, user_id) VALUES (?,?);";
        upload_queries_and_params.add(new Pair<>(query2, new Object[]{ get_Selected_Plan_ID(), get_User_ID() }));
        
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
        System.out.printf("\nChosen Plan: %s  \nChosen Plan Name: %s", get_Selected_Plan_Version_ID(), get_Plan_Name());
        
        return true;
    }
    
    private boolean setup_Transfer_Macro_Targets_Data()
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
        
        upload_queries_and_params.add(new Pair<>(query_03, new Object[]{ get_Selected_Plan_ID(), get_Selected_Plan_Version_ID() }));
        
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
    
    private boolean setup_Transfer_Meals_Data()
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
        
        upload_queries_and_params.add(new Pair<>(query_1, new Object[]{ get_Selected_Plan_Version_ID() }));
        
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
        
        upload_queries_and_params.add(new Pair<>(query_03, new Object[]{ get_Selected_Plan_ID() }));
        
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
        
        upload_queries_and_params.add(new Pair<>(query_04, new Object[]{ get_Selected_Plan_ID() }));
        
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
        
        upload_queries_and_params.add(new Pair<>(query_05, new Object[]{ get_Selected_Plan_ID() }));
        
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
        
        upload_queries_and_params.add(new Pair<>(query_06, new Object[]{ get_Selected_Plan_ID() }));
        
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
        
        upload_queries_and_params.add(new Pair<>(query_07, new Object[]{ get_Selected_Plan_ID() }));
        
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
        String query = "SELECT * FROM measurements ORDER BY unit_name;";
        String errorMSG = "Unable, to get Measurements Data";
        
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
        System.out.println("\nMeasurement Objects Successfully Transferred!");
        return true;
    }
    
    public boolean setup_Get_System_Variables()
    {
        //###########################
        // Set Variables
        //###########################
        String error_MSG = "Unable, to get System Variables!";
        LinkedHashSet<Pair<String, Object[]>> fetch_queries_and_params = new LinkedHashSet<>();
        
        //###########################
        // Fetch Queries
        //###########################
        
        // N/A Ingredient ID
        String query_01 = "SELECT ingredient_id FROM  ingredients_info WHERE ingredient_name = ?;";
        fetch_queries_and_params.add(new Pair<>(query_01, new Object[]{ "None Of The Above" }));
        
        // N/A Shop
        String query_02 = """
                SELECT
                    pdid
                FROM ingredient_in_shops
                WHERE
                    ingredient_id = (SELECT ingredient_id FROM ingredients_info WHERE ingredient_name = ?)
                    AND product_name = ?;""";
        
        fetch_queries_and_params.add(new Pair<>(query_02, new Object[]{ "None Of The Above", "N/A" }));
        
        try
        {
            //###########################
            // Execute
            //###########################
            Fetched_Results fetched_results = db.get_Fetched_Results(fetch_queries_and_params, error_MSG);
            
            if (fetched_results == null) { throw new Exception("Failed Getting Data"); }
            
            //###########################
            // Set Variables
            //###########################
            // Retrieve Variables From Fetched Results
            int na_ingredient_id = (Integer) fetched_results.get_1D_Result_Into_Object(0);
            int na_pdid = (Integer) fetched_results.get_1D_Result_Into_Object(1);
            
            // Set Variables in Shared Data Registry
            shared_Data_Registry.set_NA_Ingredient_ID(na_ingredient_id);
            shared_Data_Registry.set_NA_Ingredient_PDID(na_pdid);
            
            //###########################
            // Success Msg
            //###########################
            System.out.printf("\n\n%s \nSystem Variables Successfully Transferred! \n%s", lineSeparator, lineSeparator);
            
            return true; // Output
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s \n%s Error \n%s  \n%s", lineSeparator, get_Class_And_Method_Name(), lineSeparator, e);
            return false;
        }
    }
    
    //###########################
    // Meals Data
    //###########################
    public ArrayList<Meals_And_Sub_Meals_OBJ> setup_Get_Meal_Data() throws Exception
    {
        //########################################################################
        // Variables
        //########################################################################
        String methodName = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName());
        
        ArrayList<Meals_And_Sub_Meals_OBJ> meals_and_sub_meals_AL = new ArrayList<>();
        
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
        
        Object[] params = new Object[]{ get_Selected_Plan_ID() };
        
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
            throw new Exception("Failed");
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
                    ingredients_values.add(ingredient_node.get("index").asInt());
                    
                    ingredients_values.add(ingredient_node.get("type_id").asInt());// Ingredient Name OBJ
                    ingredients_values.add(ingredient_node.get("id").asInt()); // Ingredient Name Object
                    
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
            return meals_and_sub_meals_AL;
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s ERROR \n%s", methodName, e);
            throw new Exception("Failed");
        }
    }
    
    public LinkedHashMap<Integer, ArrayList<Object>> setup_Get_Total_Meals_Data() throws Exception
    {
        //#################################
        // Create Get Query Results
        //#################################
        LinkedHashMap<Integer, ArrayList<Object>> total_Meals_Data_Map = new LinkedHashMap<>();
        
        String query = """
                SELECT *
                FROM draft_gui_total_meal_view
                WHERE draft_meal_in_plan_id IN (SELECT draft_meal_in_plan_id FROM draft_meals_in_plan WHERE plan_id = ?)
                ORDER BY draft_meal_in_plan_id;""";
        
        String errorMSG = "Unable to get Total Meals Data for Plan!!";
        
        Object[] params = new Object[]{ get_Selected_Plan_ID() };
        
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
            throw new Exception("Failed");
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
        
        return total_Meals_Data_Map;
    }
    
    private ArrayList<ArrayList<Object>> setup_Get_Macros_Targets_Data() throws Exception
    {
        
        ;
        String query_PlanCalc = "SELECT * from draft_gui_plan_macro_target_calculations WHERE plan_id = ?";
        String errorMSG = "Error, Gathering Macros Targets Data!";
        
        Object[] params_planCalc = new Object[]{ get_Selected_Plan_ID() };
        
        // Execute
        try
        {
            ArrayList<ArrayList<Object>> macros_targets_plan_data_AL = db.get_2D_Query_AL_Object(query_PlanCalc, params_planCalc, errorMSG, false);
            System.out.printf("\n\n%s \nPlan Targets Data Successfully Retrieved \n%s ", lineSeparator, lineSeparator);
            return macros_targets_plan_data_AL;
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s \n%s Error \n%s  \n%s", lineSeparator, get_Class_And_Method_Name(), lineSeparator, e);
            throw new Exception("Failed");
        }
    }
    
    private ArrayList<ArrayList<Object>> setup_Get_Macros_Left_Data() throws Exception
    {
        String query_Macros = "SELECT * from draft_gui_plan_macros_left WHERE plan_id = ?;";
        String errorMSG_ML = "Error, Unable to get Plan Macros Left!";
        Object[] params_macros = new Object[]{ get_Selected_Plan_ID() };
        
        try
        {
            ArrayList<ArrayList<Object>> macros_left_plan_data_AL = db.get_2D_Query_AL_Object(query_Macros, params_macros, errorMSG_ML, false);
            System.out.printf("\n\n%s \nPlan Macros Left Data Successfully Retrieved \n%s ", lineSeparator, lineSeparator);
            return macros_left_plan_data_AL;
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s \n%s Error \n%s  \n%s", lineSeparator, get_Class_And_Method_Name(), lineSeparator, e);
            throw new Exception("Failed");
        }
    }
    
    //#################################################
    // Build GUI Methods
    //#################################################
    private void create_Meal_Objects_In_GUI
    (
            ArrayList<Meals_And_Sub_Meals_OBJ> meals_and_sub_meals_AL,
            LinkedHashMap<Integer, ArrayList<Object>> total_Meals_Data_Map,
            Loading_Screen loading_Screen,
            double allocated_task_percentage
    
    ) throws Exception
    {
        try
        {
            //##############################
            // Progress Calculations
            //##############################
            /* -----------------------------------------------------------
             * Progress allocation:
             * - This method owns allocated_task_percentage of the bar
             * - Each meal and sub-meal is a unit of work
             * - Progress is increased incrementally per meal iteration
             * -----------------------------------------------------------
             */
            
            Pair<Integer, Integer> counts = setup_Get_Meal_Counts();       // 12.) Get Meal / Sub Meal Counts
            int no_of_meals = counts.getValue0();
            int no_of_sub_meals = counts.getValue1();
            
            double progress_per_unit = allocated_task_percentage / (no_of_meals + no_of_sub_meals); // % per unit
            
            double carry = 0.0; // handles fractional increments / fractional accumulator
            
            //##############################
            // Add Meals To GUI
            //##############################
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
                MealManager meal_Manager = new MealManager(
                        this,
                        shared_Data_Registry,
                        db,
                        macros_Left_JTable,
                        meal_id_obj,
                        sub_Meal_DATA,
                        total_Meal_DATA
                );
                
                add_And_Replace_MealManger_POS_GUI(meal_Manager, false, false); // Add to GUI
                
                if (loading_Screen != null) // Update Progress
                {
                    int sub_meals_in_meal_count = sub_Meal_DATA.size();                         // sub-meals in this meal
                    double mealProgress = progress_per_unit * (1 + sub_meals_in_meal_count);    // meal + sub-meals
                    
                    carry += mealProgress;        // accumulate
                    int increment = (int) carry;  // whole % only
                    
                    loading_Screen.increaseBar(increment);   // increment bar
                    carry -= increment;                     // retain fraction
                }
                
                System.out.printf("\n\n%s \nSuccessfully Added Meals to GUI \n%s \nMeals In Plan: %s\nSub-Meals In Plan: %s",
                        lineSeparator, lineSeparator, no_of_meals, no_of_sub_meals);
            }
        }
        catch (Exception e)
        {
            throw new Exception();
        }
    }
    
    public void add_And_Replace_MealManger_POS_GUI(MealManager mealManager, boolean reOrder, boolean expandView)
    {
        //###############################################
        // Exit Clause
        //###############################################
        if (! mealManager.is_Object_Created()) { return; }
        
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
    
    
    //##################################################################################################################
    //  Icon Methods & ActionListener Events
    //##################################################################################################################
    @Override
    protected void icon_Setup(Container mainNorthPanel)
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
            delete_Btn_Action();
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
            recipe_List_Btn_Action_Open_Screen();
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
            pie_Chart_Btn_Action_Open_Screen();
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
            
            scroll_Up_Btn_Action();
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
            refresh_Btn_Action();
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
            
            save_Plan_Data(true);
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
            
            open_Macros_Targets_Screen();
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
            
            scroll_Down_Btn_Action();
        });
        
        iconPanelInsert.add(down_ScrollBar_Btn);
    }
    
    // ################################################################
    // Delete BTN Actions
    // ################################################################
    private void delete_Btn_Action()
    {
        //###########################################################
        // Ask for Confirmation
        //###########################################################
        if (! areYouSure("Delete All Meals", "Are you want to 'DELETE' all the meals in this plan?")) { return; }
        
        //###########################################################
        // Delete all the meals in the plans in SQL
        //###########################################################
        String queryDelete = "DELETE FROM table_draft_meals  WHERE plan_id = ?";
        
        Object[] params = new Object[]{ get_Selected_Plan_ID() };
        
        if (! db.upload_Data(queryDelete, params, "Error, unable to DELETE meals in plan!")) { return; }
        
        JOptionPane.showMessageDialog(this, "\n\nSuccessfully, DELETED all meals in plan!");
        
        //###########################################################
        // DELETE all the meals in Memory
        //###########################################################
        shared_Data_Registry.delete_MealManagers_MPS();
        
        //###########################################################
        // Update MacrosLeft
        //###########################################################
        update_Macros_Left_Table();
        
        //###########################################################
        // Update External Graphs
        //###########################################################
        update_External_Charts(true, "clear", null, null, null);
    }
    
    // ###############################################################
    // Recipe BTN Actions
    // ###############################################################
    private void recipe_List_Btn_Action_Open_Screen()
    {
        
    }
    
    // ###############################################################
    // Pie Chart BTN Actions
    // ###############################################################
    public boolean is_PieChart_Screen_Open()
    {
        return pie_Chart_Screen != null;
    }
    
    public void remove_Pie_Chart_Screen()
    {
        pie_Chart_Screen = null;
    }
    
    private void pie_Chart_Btn_Action_Open_Screen()
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
    private void update_Line_Chart_Data(MealManager mealManager, LocalTime previousTime, LocalTime currentTime)
    {
        if (! is_Line_Chart_Screen_Open()) { return; }
        
        line_Chart.update_MealManager_ChartData(mealManager, previousTime, currentTime);
    }
    
    private void add_Meal_To_Line_Chart(MealManager mealManager)
    {
        if (! is_Line_Chart_Screen_Open()) { return; }
        
        line_Chart.add_New_MealManager_Data(mealManager);
    }
    
    private void delete_Meal_In_Line_Chart(LocalTime currentTime)
    {
        if (! is_Line_Chart_Screen_Open()) { return; }
        
        line_Chart.delete_MealManager_Data(currentTime);
    }
    
    private void clear_Line_Chart_DataSet()
    {
        if (! is_Line_Chart_Screen_Open()) { return; }
        
        line_Chart.clear_LineChart_Dataset();
    }
    
    private void refresh_Line_Chart_Data()
    {
        if (! is_Line_Chart_Screen_Open()) { return; }
        
        line_Chart.refresh_Data();
    }
    
    // ###############################################################
    // Scroll Up BTN Action
    // ###############################################################
    private void scroll_Up_Btn_Action()
    {
        super.scroll_To_Top_of_ScrollPane();
    }
    
    // ###############################################################
    // Refresh BTN Actions / Plan Actions
    // ###############################################################
    private void refresh_Btn_Action()
    {
        //####################################################################
        // Confirm Refresh / Edge Cases
        //####################################################################
        String txt = "Are you sure you want to refresh all the meals in this plan?";
        
        // IF there is no plan selected or, user rejects refresh then exit
        if ((! (is_Plan_Selected())) || ! (areYouSure("Refresh Meal Plan Data", txt)))
        {
            return;
        }
        
        //####################################################################
        // Refresh DB Data
        //####################################################################
        setup_Get_User_And_Plan_Info(false, false, false, true); // Get Plan Name
        
/*        setup_Transfer_Plan_Data(); // Transfer Plan Data
        
        if (has_Macro_Targets_Changed()) // Refresh Macro Targets if they have changed
        {
            setup_Transfer_Macro_Targets_Data();    // Transfer Macro Data
            set_Has_Macros_Targets_Changed(false); // reset variable
        }
        
        setup_Transfer_Meals_Data();                            // Transfer Meals Data
        
        if (! setup_Get_Meal_Data()) { return; }               // Get Meals Data
        if (! setup_Get_Total_Meals_Data()) { return; }        // Get Total_Meals Data
        *//*
        if (! setup_Get_Macros_Targets_Data()) { return; }     // Get MacroTargets DATA
        if (! setup_Get_Macros_Left_Data()) { return; }        // Get MacrosLeft DATA
        
         *//*
        macros_Left_JTable.refresh_Data();                     // ID is the same / Same Data = Refresh
        macros_Targets_Table.refresh_Data();                   // ID is the same / Same Data = Refresh
        */
        //####################################################################
        // Refresh MealManagers Collections
        //####################################################################
        shared_Data_Registry.refresh_MealManagers_MPS();
        
        //####################################################################
        // Re-draw GUI Screen
        //####################################################################
        
        scroll_Up_Btn_Action(); // Reposition GUI
        
        //####################################################################
        // Refresh Macro-Targets Table
        //####################################################################
        refresh_Macro_Targets(); // if macroTargets changed ask the user if they would like to refresh this data
        
        update_Macros_Left_Table(); // Update macrosLeft, refresh methods do not work here, needs to be computed
        
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
        if (! (is_Plan_Selected()))
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
        if (! mealManager.is_Object_Created()) { return; }
        
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
    private void save_Plan_Data(boolean show_msg)
    {
        // ########################################
        // Exit Clauses
        // ########################################
        /*
            If there is no selected plan, exit
            If there is no meals in the plan aka there's nothing to save, exit
            If the user  rejects saving the plan, exit
         */
        
        String txt = "Are you want to save all the data in this meal plan?";
        ArrayList<MealManager> mealManager_ArrayList = shared_Data_Registry.get_MealManager_ArrayList();
        
        if (mealManager_ArrayList.isEmpty() || ! is_Plan_Selected() || ! areYouSure("Save Meal Plan Data", txt))
        {
            return;
        }
        
        // ########################################
        // Remove Every Deleted Meal
        // ########################################
        mealManager_ArrayList.removeIf(MealManager :: is_Meal_Deleted);
        
        if (mealManager_ArrayList.isEmpty()) //  If there are no meals left after removing all the deleted meals, exit
        {
            JOptionPane.showMessageDialog(null, "There are no Meals left in this Plan! \nAdd a Meal to Save!");
            return;
        }
        
        // ########################################
        // Save & Create New Plan Version / ID's
        // ########################################
        // Get All the ID's of New Versioned Meals / Sub-Meals
        
     /*   if ((! (transfer_Meal_Ingredients(get_Selected_Plan_ID(), get_Selected_Plan_Version_ID())))) // transfer meals and ingredients from temp plan to original plan
        {
            System.out.println("\n\n#################################### \n2.) save_Plan_Data() Meals Transferred to Original Plan");
            
            JOptionPane.showMessageDialog(this, "\n\n2.)  Error \nUnable to save meals in plan!");
            return;
        }*/
        
        // ########################################
        // Save Each Meal
        // ########################################
        for (MealManager mealManager : mealManager_ArrayList)
        {
            // mealManager.save_Requested_By_Plan();
        }
        
        // ########################################
        // Successful Message
        // ########################################
        if (show_msg) { JOptionPane.showMessageDialog(this, "\n\nAll Meals Are Successfully Saved!"); }
    }
    
    // ###############################################################
    // Add Ingredients Screen & Ingredient Methods
    // ###############################################################
    private void open_Ingredients_Screen()
    {
        if (is_Ingredients_Screen_Open())
        {
            ingredients_Info_Screen.makeJFrameVisible();
            return;
        }
        
        ingredients_Info_Screen = new Ingredients_Info_Screen(db, this, shared_Data_Registry);
    }
    
    public void remove_Ingredients_Info_Screen()
    {
        ingredients_Info_Screen = null;
    }
    
    private boolean is_Ingredients_Screen_Open()
    {
        return ingredients_Info_Screen != null;
    }
    
    public void update_Ingredients_Name_And_Types_In_JTables(boolean ingredientsAddedOrRemove)
    {
        if (ingredientsAddedOrRemove)
        {
            //#####################################
            // Save Plan & Refresh Plan
            //#####################################
            
        }
    }
    
    // ###############################################################
    // Macro Targets Screen & Target Methods
    // ###############################################################
    private void open_Macros_Targets_Screen()
    {
        if (! (is_Plan_Selected()))
        {
            return;
        }
        
        if (is_Macros_Target_Screen_Open())
        {
            macros_Targets_Screen.makeJFrameVisible();
            return;
        }
        macros_Targets_Screen = new Macros_Targets_Screen(db, this, get_Selected_Plan_ID(), get_Plan_Name());
    }
    
    public void remove_Macros_Target_Screen()
    {
        macros_Targets_Screen = null;
    }
    
    private boolean is_Macros_Target_Screen_Open()
    {
        return macros_Targets_Screen != null;
    }
    
    // ###############################################################
    // Scroll Down BTN Action
    // ###############################################################
    private void scroll_Down_Btn_Action()
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
            save_Plan_Data(false);  //Meal Data
        }
        
        // ##########################################
        // Close Other Windows If Open
        // ##########################################
        if (is_Macros_Target_Screen_Open())
        {
            macros_Targets_Screen.window_Closed_Event();
        }
        if (is_Ingredients_Screen_Open()) // HELLO Refactor into screen method
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
                    add_Meal_To_Line_Chart(mealManager); // LineChart Add
                    
                    add_Meal_To_Pie_Chart_Screen(mealManager); // PieChart MPS Screen
                }
                case "clear" ->  // Delete Button requested on MealPlanScreen
                {
                    clear_Line_Chart_DataSet(); // Clear LineChart Data
                    
                    clear_Pie_Chart_Dataset(); // Clear PieChart Screen
                }
                case "refresh" -> // Refresh Button requested on MealPlanScreen
                {
                    refresh_Line_Chart_Data(); // Refresh LineChart Data
                    
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
                update_Line_Chart_Data(mealManager, previousMealTime, currentMealTime);   // Update LineChart Data
                
                update_Pie_Chart_DATA(mealManager);  // Update PieChart DATA
            }
            case "delete" -> // Deleted MealManager
            {
                delete_Meal_In_Line_Chart(previousMealTime);   // Delete LineChart Data
                
                delete_Meal_From_Pie_Chart_Screen(mealManager); // Delete PieChart AKA Re-draw GUI
            }
            case "mealTime" -> // MealTime on MealManager Changed
            {
                // Change data points time on LineChart Data
                update_Line_Chart_Data(mealManager, previousMealTime, currentMealTime);
                
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
                update_Line_Chart_Data(mealManager, previousMealTime, currentMealTime);
                
                // Change PieChart MealName
                update_Pie_Chart_Meal_Name(mealManager);
            }
        }
    }
    
    //##################################################################################################################
    //  Macro Targets/Left Table Methods
    //##################################################################################################################
    public void update_Targets_And_Macros_Left_Table()
    {
        update_Macros_Target_Table();
        update_Macros_Left_Table();
    }
    
    //##########################################
    // MacrosLeft Targets
    //#########################################
    public void update_Macros_Target_Table()
    {
        macros_Targets_Table.update_Table();
    }
    
    private void refresh_Macro_Targets()
    {
        // ##############################################
        // If targets have changed prompt to Refresh
        // ##############################################
        if (has_Macro_Targets_Changed())
        {
            int reply = JOptionPane.showConfirmDialog(this, String.format("Would you like to refresh your MacroTargets Too?"),
                    "Refresh Macro Targets", JOptionPane.YES_NO_OPTION); //HELLO Edit
            
            if (reply == JOptionPane.YES_OPTION)
            {
                /*if (transfer_Targets(get_Selected_Plan_Version_ID(), null, true, false))
                {
                    JOptionPane.showMessageDialog(this, "\n\nMacro-Targets Successfully Refreshed!!");
                    set_Has_Macros_Targets_Changed(false);
                    
                    macros_Targets_Table.refresh_Data();
                }*/
            }
        }
    }
    
    //##########################################
    // MacrosLeft Table
    //#########################################
    public void update_Macros_Left_Table()
    {
        macros_Left_JTable.update_Table();
    }
    
    //##################################################################################################################
    //  Mutator Methods
    //##################################################################################################################
    public void set_Has_Macros_Targets_Changed(boolean bool)
    {
        macro_Targets_Changed = bool;
    }
    
    //##################################################################################################################
    //  Accessor Methods
    //##################################################################################################################
    public boolean is_Plan_Selected()
    {
        if (get_Selected_Plan_Version_ID() == null)
        {
            JOptionPane.showMessageDialog(this, "Please Select A Plan First!");
            return false;
        }
        return true;
    }
    
    public boolean has_Macro_Targets_Changed()
    {
        return macro_Targets_Changed;
    }
    
    //###########################################
    // String
    //###########################################
    public String get_Plan_Name()
    {
        return shared_Data_Registry.get_Plan_Name();
    }
    
    //###########################################
    // Integer
    //###########################################
    public Integer get_Selected_Plan_Version_ID()
    {
        return shared_Data_Registry.get_Selected_Plan_Version_ID();
    }
    
    public Integer get_Selected_Plan_ID()
    {
        return shared_Data_Registry.get_Selected_Plan_ID();
    }
    
    public Integer get_User_ID()
    {
        return shared_Data_Registry.get_User_ID();
    }
    
    //###########################################
    // Objects
    //###########################################
    public JPanel get_Scroll_JPanel_Center()
    {
        return scroll_JPanel_Center;
    }
    
    //####################################################################
    // Collections
    //#####################################################################
    
    //###########################################
    // Total Meal Table :  Collections
    //###########################################
    public ArrayList<String> get_Total_Meal_Table_Col_To_Hide()
    {
        return total_Meal_Table_Col_To_Hide;
    }
    
    public ArrayList<String> get_Meal_Total_Column_Names()
    {
        return meal_total_column_Names;
    }
    
    public HashMap<Total_Meal_Other_Columns, Integer> get_Total_Meal_Other_Col_Pos()
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
    // Ingredients Table : Collections
    //###########################################
    public HashMap<Ingredients_Table_Columns, Integer> get_Ingredients_Table_Col_Pos()
    {
        return ingredients_table_cols_positions;
    }
    
    public ArrayList<String> get_Ingredients_Column_Names()
    {
        return ingredients_Column_Names;
    }
    
    public ArrayList<String> get_Ingredients_Table_Un_Editable_Cells()
    {
        return ingredients_Table_Un_Editable_Cells;
    }
    
    public ArrayList<String> get_Ingredients_Table_Col_Avoid_Centering()
    {
        return ingredients_Table_Col_Avoid_Centering;
    }
    
    public ArrayList<String> get_Ingredients_In_Meal_Table_Col_To_Hide()
    {
        return ingredients_In_Meal_Table_Col_To_Hide;
    }
}
