package App_Code.Objects.Table_Objects;

import App_Code.Objects.Data_Objects.ID_Objects.MetaData_ID_Object.Meal_ID_OBJ;
import App_Code.Objects.Database_Objects.Fetched_Results;
import App_Code.Objects.Database_Objects.MyJDBC.MyJDBC_Sqlite;
import App_Code.Objects.Database_Objects.Shared_Data_Registry;
import App_Code.Objects.Table_Objects.Tables.Children.Ingredients_Table.IngredientsTable;
import App_Code.Objects.Table_Objects.Tables.Children.View_Data_Tables.Children.MacrosLeft_Table;
import App_Code.Objects.Table_Objects.Tables.Children.View_Data_Tables.Children.Total_Meal_Table.TotalMeal_Table;
import App_Code.Objects.Gui_Objects.CollapsibleJPanel;
import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Gui_Objects.IconPanel;
import App_Code.Objects.Screens.Graph_Screens.PieChart_MealManager_Screen.Pie_Chart_Meal_Manager_Screen;
import App_Code.Objects.Screens.Meal_Plan_Screen;
import App_Code.Objects.Table_Objects.Tables.Children.View_Data_Tables.Children.Total_Meal_Table.Total_Meal_Other_Columns;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;
import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MealManager
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    
    //############################################
    // Boolean
    //############################################
    private boolean
            is_Meal_Saved = false,
            is_Object_Created = false,
            is_MealManager_In_DB = false,
            has_Meal_Name_Been_Changed = false,
            has_Meal_Time_Been_Changed = false;
    
    //############################################
    // Integers
    //############################################
    private int
            source_meal_id,
            draft_meal_ID,
            yPoInternally = 0,
            na_ingredient_id,
            na_pdid,
            total_meal_time_col_pos,
            total_meal_name_col_pos;
    
    private final UUID internalId = UUID.randomUUID();
    
    //############################################
    // String
    //############################################
    private String class_Name = new Object() { }.getClass().getEnclosingClass().getName();
    private String lineSeparator = "###############################################################################";
    private String saved_meal_name = "", currentMealName = "";
    
    //############################################
    // Time
    //############################################
    private LocalTime saved_meal_time = null, current_meal_time = null;
    private DateTimeFormatter time_Formatter = DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);
    
    //############################################
    // Collections
    //############################################
    private final ArrayList<IngredientsTable> ingredient_tables_AL = new ArrayList<>();
    
    //################################################################################
    // Objects
    //################################################################################
    
    // Other Objects
    //############################################
    private JPanel collapsibleCenterJPanel, spaceDividerForMealManager = new JPanel();
    private final MyJDBC_Sqlite db;
    private GridBagConstraints gbc;
    private Container container;
    private CollapsibleJPanel collapsibleJpObj;
    private Shared_Data_Registry shared_Data_Registry;
    
    //############################################
    // Screens
    //############################################
    private Pie_Chart_Meal_Manager_Screen pie_chart_screen;
    
    //############################################
    // Table Objects
    //############################################
    private final MacrosLeft_Table macrosLeft_JTable;
    private final Meal_Plan_Screen meal_plan_screen;
    private TotalMeal_Table totalMealTable;
    
    //##################################################################################################################
    // Constructors
    //##################################################################################################################
    public MealManager
    (
            Meal_Plan_Screen meal_plan_screen,
            Shared_Data_Registry shared_Data_Registry,
            MyJDBC_Sqlite db,
            MacrosLeft_Table macrosLeft_JTable,
            Meal_ID_OBJ meal_ID_Obj,
            LinkedHashMap<Integer, ArrayList<ArrayList<Object>>> sub_Meal_DATA,
            ArrayList<Object> total_Meal_Data
    )
    {
        //################################################
        // Global Variables
        //################################################
        this.meal_plan_screen = meal_plan_screen;
        this.shared_Data_Registry = shared_Data_Registry;
        this.db = db;
        this.macrosLeft_JTable = macrosLeft_JTable;
        
        draft_meal_ID = meal_ID_Obj.get_Draft_Meal_ID();
        source_meal_id = meal_ID_Obj.get_Source_Meal_ID();
        
        na_pdid = shared_Data_Registry.get_NA_PDID();
        na_ingredient_id = shared_Data_Registry.get_Na_Ingredient_ID();
        
        // Set Meal Time Variables
        LocalTime meal_time = meal_ID_Obj.get_Meal_Time();
        set_Time_Variables(false, meal_time, meal_time); // Set MealTime Variables
        
        // Set Meal Name Variables
        String mealName = meal_ID_Obj.get_Name();
        set_Meal_Name_Variables(false, mealName, mealName); // Set MealName Variables
        
        set_MealManager_In_DB(true);  // Set Variable which identifies in this meal associated with this object is in the database
        
        //################################################
        // Setup Methods
        //################################################
        setup_GUI(total_Meal_Data); // GUI
        
        add_Multiple_Sub_Meals(sub_Meal_DATA); // Add Sub-Meal to GUI
    }
    
    //
    public MealManager
    (
            Meal_Plan_Screen meal_plan_screen,
            Shared_Data_Registry shared_Data_Registry,
            MyJDBC_Sqlite db,
            MacrosLeft_Table macrosLeft_JTable
    )
    {
        //############################################################################
        // Setting Variables
        //############################################################################
        this.meal_plan_screen = meal_plan_screen;
        this.shared_Data_Registry = shared_Data_Registry;
        this.macrosLeft_JTable = macrosLeft_JTable;
        this.db = db;
        
        na_pdid = shared_Data_Registry.get_NA_PDID();
        na_ingredient_id = shared_Data_Registry.get_Na_Ingredient_ID();
        
        //############################################################################
        // Getting user input for Meal Name & Time
        //############################################################################
        String new_Meal_Name = prompt_User_For_Meal_Name(true, false);
        
        if (new_Meal_Name == null) { return; } // Error occurred in validation checks above
        
        //############################################################################
        // Validating User Input
        //############################################################################
        LocalTime new_Meal_Time = prompt_User_For_Meal_Time(true, false);
        
        if (new_Meal_Time == null) { return; } // Error occurred in validation checks above
        
        //############################################################################
        // Upload & Fetch Variables
        //############################################################################
        String errorMSG = String.format("\n\nError Creating Meal with credentials: \n\nMeal Name: '%s' \nMeal Time: %s!", new_Meal_Name, new_Meal_Time);
        
        LinkedHashSet<Pair<String, Object[]>> upload_Queries_And_Params = new LinkedHashSet<>();
        LinkedHashSet<Pair<String, Object[]>> fetch_Queries_And_Params = new LinkedHashSet<>();
        
        //#######################################################
        // Upload Queries
        //#######################################################
        /*
         
         
         */
        //###############################
        // Insert Into Meals
        //###############################
        String upload_Q1 = """
                INSERT INTO draft_meals_in_plan
                (plan_id, meal_name, meal_time)
                VALUES
                (?,?,?);""";
        
        upload_Queries_And_Params.add(new Pair<>(upload_Q1, new Object[]{ get_Plan_ID(), new_Meal_Name, new_Meal_Time }));
        
        //###############################
        // Insert Into Sub-Meals
        //###############################
        String upload_Q2 = """
                INSERT INTO draft_divided_meal_sections
                (draft_meal_in_plan_id, plan_id)
                VALUES
                (
                    (SELECT last_insert_rowid()),
                    ?
                );""";
        
        upload_Queries_And_Params.add(new Pair<>(upload_Q2, new Object[]{ get_Plan_ID() })); // Upload Q1
        
        //###############################
        // Insert Ingredients Into Sub-Meal
        //###############################
        String upload_Q3 = """
                INSERT INTO draft_ingredients_in_sections_of_meal
                (
                    draft_div_meal_sections_id,
                    ingredient_id,
                    pdid,
                    quantity
                )
                VALUES
                (
                    (SELECT last_insert_rowid()),
                     ?,
                     ?,
                     ?
                );""";
        
        
        Object[] q3_params = new Object[]{ na_ingredient_id, na_pdid, 0 };
        upload_Queries_And_Params.add(new Pair<>(upload_Q3, q3_params)); // Upload Q3
        
        //#######################################################
        // Fetch Query
        //#######################################################
        
        // Get IDs / Ingredients Row DATA
        String fetch_query_01 = """
                
                SELECT
                
                    M.draft_meal_in_plan_id,
                    S.draft_div_meal_sections_id,
                    I2.*
                
                FROM draft_meals_in_plan M
                
                INNER JOIN draft_divided_meal_sections S
                    ON M.draft_meal_in_plan_id = S.draft_meal_in_plan_id
                
                INNER JOIN draft_ingredients_in_sections_of_meal I1
                    ON S.draft_div_meal_sections_id = I1.draft_div_meal_sections_id
                
                INNER JOIN draft_gui_ingredients_in_sections_of_meal_calculation I2
                    ON I1.draft_ingredients_index = I2.draft_ingredients_index
                
                WHERE
                    M.plan_id = ? AND M.meal_name = ?
                
                LIMIT 1;""";
        
        fetch_Queries_And_Params.add(new Pair<>(fetch_query_01, new Object[]{ get_Plan_ID(), new_Meal_Name })); // Upload Params
        
        // Get TotalMeal Data
        String fetch_query_02 = """
                WITH
                    Meal_ID AS (
                
                        SELECT draft_meal_in_plan_id
                        FROM draft_meals_in_plan
                        WHERE plan_id = ? AND meal_name = ?
                    )
                
                SELECT *
                FROM  draft_gui_total_meal_view
                WHERE draft_meal_in_plan_id = (SELECT draft_meal_in_plan_id FROM Meal_ID);""";
        
        fetch_Queries_And_Params.add(new Pair<>(fetch_query_02, new Object[]{ get_Plan_ID(), new_Meal_Name })); // Upload Params
        
        //#######################################################
        // Execute Query
        //#######################################################
        Fetched_Results fetched_Results_OBJ = db.upload_And_Get_Batch(upload_Queries_And_Params, fetch_Queries_And_Params, errorMSG);
        
        if (fetched_Results_OBJ == null) { System.err.println("\n\n\nFailed Creating Meal"); return; }
        
        //#######################################################
        // Set Variables from Results
        //#######################################################
        int sub_Meal_ID;
        ArrayList<ArrayList<Object>> sub_Meal_DATA;
        ArrayList<Object> total_Meal_Data = null;
        
        try
        
        {
            ArrayList<ArrayList<Object>> results = fetched_Results_OBJ.get_Fetched_Result_2D_AL(0);
            ArrayList<Object> combined_results = results.getFirst();
            
            draft_meal_ID = (Integer) combined_results.removeFirst(); // Get Draft Meal ID & Remove IT
            
            sub_Meal_ID = (Integer) combined_results.removeFirst();  // Get Draft Sub ID & Remove IT
            
            sub_Meal_DATA = results; // Get Sub Ingredients & Remove IT
            
            total_Meal_Data = fetched_Results_OBJ.get_Result_1D_AL(1); // Get Total Meal Data
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s", e);
            return;
        }
        
        //#############################
        // Set Name & Time Variables
        //#############################
        set_Meal_Name_Variables(false, new_Meal_Name, new_Meal_Name); // Set MealName Variables
        
        set_Time_Variables(false, new_Meal_Time, new_Meal_Time);     // Set MealTime Variables
        
        set_MealManager_In_DB(false);
        
        //#######################################################
        // Add Meals To GUI
        //#######################################################
        setup_GUI(total_Meal_Data); // GUI
        
        add_Sub_Meal(false, sub_Meal_ID, sub_Meal_DATA); // Add Sub-Meal to GUI
        
        //#######################################################
        // Return Variables
        //#######################################################
        is_Object_Created = true;
    }
    
    //##################################################################################################################
    //  Setup
    //##################################################################################################################
    private void iconSetup()
    {
        int iconSize = 40;
        //########################################################################
        // Icons Top RIGHT
        //########################################################################
        JPanel eastJPanel = collapsibleJpObj.get_East_JPanel();
        eastJPanel.setLayout(new GridBagLayout());
        
        IconPanel iconPanel = new IconPanel(2, 10, "East");
        JPanel iconPanelInsert = iconPanel.getIconJpanel();
        
        add_To_Container(eastJPanel, iconPanel.getIconAreaPanel(), 0, 0, 1, 1, 0.25, 0.25, "horizontal", 10, 0, null);
        
        //##########################
        // Pie Chart Graph BTN
        //##########################
        IconButton graph_Icon_Btn = new IconButton("/images/graph/pie2.png", iconSize, iconSize, iconSize, iconSize, "centre", "right");
        // add_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));
        
        JButton graph_Btn = graph_Icon_Btn.returnJButton();
        graph_Btn.setToolTipText("Get Pie Chart Data"); //Hover message over icon
        graph_Icon_Btn.makeBTntransparent();
        
        graph_Btn.addActionListener(ae -> {
            pieChart_Action();
        });
        
        iconPanelInsert.add(graph_Icon_Btn);
        
        //##########################
        // Edit Name BTN
        //##########################
        IconButton edit_Icon_Btn = new IconButton("/images/edit/edit.png", iconSize, iconSize, iconSize, iconSize, "centre", "right");
        // add_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));
        
        JButton edit_Btn = edit_Icon_Btn.returnJButton();
        edit_Btn.setToolTipText("Edit Meal Name"); //Hover message over icon
        edit_Icon_Btn.makeBTntransparent();
        
        edit_Btn.addActionListener(ae -> {
            edit_Name_BTN_Action();
        });
        
        iconPanelInsert.add(edit_Icon_Btn);
        
        //##########################
        // Edit Time BTN
        //##########################
        IconButton editTime_Icon_Btn = new IconButton("/images/edit_Time/edit_Time.png", 45, 45, 45, 45, "centre", "right");
        // add_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));
        
        JButton editTime_Btn = editTime_Icon_Btn.returnJButton();
        editTime_Btn.setToolTipText("Edit Meal Time"); //Hover message over icon
        editTime_Icon_Btn.makeBTntransparent();
        
        editTime_Btn.addActionListener(ae -> {
            edit_Time_Btn_Action();
        });
        
        iconPanelInsert.add(editTime_Icon_Btn);
        
        //##########################
        // Add BTN
        //##########################
        IconButton add_Icon_Btn = new IconButton("/images/add/add.png", iconSize, iconSize, iconSize, iconSize, "centre", "right");
        // add_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));
        
        JButton add_Btn = add_Icon_Btn.returnJButton();
        add_Icon_Btn.setToolTipText("Add Sub-Meal In Meal"); //Hover message over icon
        add_Icon_Btn.makeBTntransparent();
        
        add_Btn.addActionListener(ae -> {
            add_Btn_Action();
        });
        
        iconPanelInsert.add(add_Icon_Btn);
        
        //##########################
        // Refresh Icon
        //##########################
        
        IconButton refresh_Icon_Btn = new IconButton("/images/refresh/+refresh.png", iconSize, iconSize, iconSize, iconSize,
                "centre", "right"); // btn text is useless here , refactor
        
        JButton refresh_Btn = refresh_Icon_Btn.returnJButton();
        refresh_Btn.setToolTipText("Restore All Sub-Meals Data"); //Hover message over icon
        refresh_Icon_Btn.makeBTntransparent();
        
        refresh_Btn.addActionListener(ae -> {
            
            //#######################################################
            // Ask For Permission
            //#######################################################
            
            if (areYouSure("Refresh Data"))
            {
                refresh_Btn_Action();
            }
        });
        
        iconPanelInsert.add(refresh_Icon_Btn);
        
        //##########################
        // Update Icon
        //##########################
        
        IconButton saveIcon_Icon_Btn = new IconButton("/images/save/save.png", iconSize, iconSize, iconSize, iconSize,
                "centre", "right"); // btn text is useless here , refactor
        //saveIcon_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));
        saveIcon_Icon_Btn.makeBTntransparent();
        
        JButton save_btn = saveIcon_Icon_Btn.returnJButton();
        save_btn.setToolTipText("Save All Sub-Meals Data"); //Hover message over icon
        
        save_btn.addActionListener(ae -> {
            if (areYouSure("Save Data"))
            {
                save_Btn_Action();
            }
        });
        
        iconPanelInsert.add(save_btn);
        
        //##########################
        // Delete Icon
        //##########################
        
        IconButton deleteIcon_Icon_Btn = new IconButton("/images/delete/+delete.png", iconSize, iconSize, iconSize + 10, iconSize,
                "centre", "right"); // btn text is useless here , refactor
        //deleteIcon_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));
        deleteIcon_Icon_Btn.makeBTntransparent();
        
        JButton delete_btn = deleteIcon_Icon_Btn.returnJButton();
        delete_btn.setToolTipText("Delete Meal"); //Hover message over icon
        
        delete_btn.addActionListener(ae -> {
            delete_Btn_Action();
        });
        
        iconPanelInsert.add(delete_btn);
    }
    
    // GUI Setup
    private void setup_GUI(ArrayList<Object> total_Meal_Data)
    {
        //################################################################
        // Variables
        //################################################################
        gbc = new GridBagConstraints();
        container = meal_plan_screen.get_Scroll_JPanel_Center();
        
        total_meal_time_col_pos =
                shared_Data_Registry
                        .get_Other_Total_Meal_Table_Column_Pos(Total_Meal_Other_Columns.MEAL_TIME);
        
        total_meal_name_col_pos =
                shared_Data_Registry
                        .get_Other_Total_Meal_Table_Column_Pos(Total_Meal_Other_Columns.MEAL_NAME);
        
        //################################################################
        // Create Collapsible Object
        //################################################################
        //collapsibleJpObj = new CollapsibleJPanel(container, remove_Seconds_On_Time_String(savedMealTime), 150, 50); // time as btn txt
        collapsibleJpObj = new CollapsibleJPanel(container, saved_meal_name, 180, 50); // time as btn txt
        collapsibleCenterJPanel = collapsibleJpObj.get_Centre_JPanel();
        collapsibleCenterJPanel.setBackground(Color.YELLOW);
        
        //################################################################
        // Icon Setup in Collapsible Object
        //################################################################
        iconSetup();
        
        //################################################################
        // Create TotalMeal Objects
        //################################################################
        totalMealTable = new TotalMeal_Table(db, this, shared_Data_Registry, total_Meal_Data);
        
        JPanel southPanel = collapsibleJpObj.get_South_JPanel();   // TotalMeal_Table to Collapsible Object
        
        // adds space between Ingredients_In_Meal_Calculation table and total_in_meal table
        add_To_Container(southPanel, new JPanel(), 0, 1, 1, 1, 0.25, 0.25, "both", 50, 0, null);
        
        // Adding total table to CollapsibleOBJ
        add_To_Container(southPanel, totalMealTable, 0, 2, 1, 1, 0.25, 0.25, "both", 0, 0, null);
        
        // Add Initial Space Between For the First Divided Meal
        add_To_Container(collapsibleCenterJPanel, new JPanel(), 0, yPoInternally++, 1, 1, 0.25, 0.25, "both", 10, 0, null);
        
        is_Object_Created = true;  // Set Object Created
        
        //################################################################
        // Add To Shared Data
        //################################################################
        shared_Data_Registry.add_Meal_Manager(this, total_Meal_Data);
    }
    
    //#################################
    // Meal Setup Methods
    //#################################
    private void add_Multiple_Sub_Meals(LinkedHashMap<Integer, ArrayList<ArrayList<Object>>> sub_Meal_DATA)
    {
        for (Map.Entry<Integer, ArrayList<ArrayList<Object>>> div_data : sub_Meal_DATA.entrySet())
        {
            add_Sub_Meal(true, div_data.getKey(), div_data.getValue());
        }
    }
    
    private void add_Sub_Meal(boolean is_Sub_Meal_In_DB, int div_id, ArrayList<ArrayList<Object>> sub_Meal_Data)
    {
        //##############################################
        // Create Ingredient Table Object
        //##############################################
        JPanel spaceDivider = new JPanel();
        
        IngredientsTable ingredients_Table =
                new IngredientsTable(
                        db,
                        this,
                        shared_Data_Registry,
                        macrosLeft_JTable,
                        div_id,
                        sub_Meal_Data,
                        is_Sub_Meal_In_DB,
                        spaceDivider
                );
        
        //################################################
        // Add Ingredients Table To GUI
        //################################################
        add_To_Container(collapsibleCenterJPanel, ingredients_Table, 0, yPoInternally++, 1, 1, 0.25, 0.25, "both", 0, 0, null);
        add_To_Container(collapsibleCenterJPanel, spaceDivider, 0, yPoInternally++, 1, 1, 0.25, 0.25, "both", 50, 0, null);
        
        //################################################
        // Add Ingredient Table To Collection
        //################################################
        ingredient_tables_AL.add(ingredients_Table);
    }
    
    //##################################################################################################################
    // Actions
    //##################################################################################################################
    private boolean areYouSure(String process)
    {
        int reply = JOptionPane.showConfirmDialog(getFrame(), String.format("You are requesting to %s ! \n\nAre you sure you want to %s?", process, process),
                "Notification", JOptionPane.YES_NO_OPTION); //HELLO Edit
        
        return reply != JOptionPane.NO_OPTION && reply != JOptionPane.CLOSED_OPTION;
    }
    
    //#################################################################################
    // Meal Name & Meal Time Functions
    //#################################################################################
    private boolean contains_Symbols(String string_To_Check)
    {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9 '\\-&]+$");
        Matcher matcher = pattern.matcher(string_To_Check);
        
        return ! matcher.matches();
    }
    
    //########################################################
    // Meal Time Functions
    //########################################################
    private void edit_Time_Btn_Action()
    {
        //###############################
        // Prompt User for time Input
        //###############################
        
        LocalTime new_meal_time = prompt_User_For_Meal_Time(false, true);
        LocalTime old_current_time = get_Current_Meal_Time();
        
        if (new_meal_time == null) { return; } // Error occurred in validation checks above
        
        //###############################
        // Update
        //###############################
        String uploadQuery = """
                UPDATE draft_meals_in_plan
                SET meal_time = ?
                WHERE draft_meal_in_plan_id = ?;""";
        
        Object[] params = new Object[]{ new_meal_time, draft_meal_ID };
        
        //###############################
        // Upload Into Database Table
        //###############################
        if (! db.upload_Data(uploadQuery, params, "Error, unable to change Meal Time!")) { return; }
        
        //###############################
        // Update GUI & Variables
        //###############################
        
        JOptionPane.showMessageDialog(getFrame(), String.format("Successfully, changed meal time from '%s' to '%s'",
                old_current_time, new_meal_time)); // Success MSG
        
        //###############################
        // Update total Meal Table Time
        //###############################
        totalMealTable.set_Value_On_Table(new_meal_time, 0, total_meal_time_col_pos);
        
        //###############################
        // Update Time Variables
        //###############################
        set_Time_Variables(true, saved_meal_time, new_meal_time); // Set Meal Time Variables
        
        //###############################
        // Update Internal PieChart Name
        //###############################
        pie_Chart_Update_Title();
        
        //###############################
        //  Update GUI
        //###############################
        meal_plan_screen.add_And_Replace_MealManger_POS_GUI(this, true, true);
        
        //###############################
        // Update External Charts
        //###############################
        meal_plan_screen.update_External_Charts(false, "mealTime", this, old_current_time, new_meal_time);
    }
    
    private LocalTime prompt_User_For_Meal_Time(boolean skip_confirmation, boolean comparison)
    {
        // User info prompt
        String input_meal_time = JOptionPane.showInputDialog("Input Meal Time etc \"09:00\"?");
        
        if (input_meal_time == null || input_meal_time.isEmpty()) { return null; }
        
        return input_Meal_Time_Validation(input_meal_time, skip_confirmation, comparison);
    }
    
    private LocalTime input_Meal_Time_Validation(String input_meal_time_string, boolean skip_confirmation, boolean comparison)
    {
        //#######################################################
        // Validation Checks
        //#######################################################
        
        // Prior to this method being called the users input_meal_time_string is checked if its null or "" and rejected
        LocalTime new_input_time_local_time = null;
        LocalTime old_current_meal_time = get_Current_Meal_Time();
        
        try
        {
            new_input_time_local_time = LocalTime.parse(input_meal_time_string, time_Formatter);
        }
        catch (Exception e)
        {
            System.err.printf("\n\nMealManager.java: input_Meal_Time_Validation() | Error, converting input_meal_time_string to time string! \n%s", e);
            JOptionPane.showMessageDialog(getFrame(), "Error, converting input_meal_time_string to Time!!");
            return null;
        }
        
        // ####################################################
        // Compare with saved correlating values
        // ####################################################
        if (comparison)
        {
            if (old_current_meal_time.equals(new_input_time_local_time)) // Time : User enters same meal time
            {
                JOptionPane.showMessageDialog(getFrame(), String.format("This meal 'time' already has the value '%s' !!", old_current_meal_time));
                return null;
            }
        }
        
        // ######################################################
        // Check Database if Value Already Exists
        // ######################################################
        String query = " SELECT 1 FROM draft_meals_in_plan WHERE plan_id = ? AND meal_time = ?";
        String errorMSG = "Error, Validating Meal Time!";
        Object[] params = new Object[]{ get_Plan_ID(), new_input_time_local_time };
        
        // Execute Query
        try
        {
            if (! db.get_Single_Col_Query_Obj(query, params, errorMSG, true).isEmpty()) // Means value already exists, returns N/A if the value doesn't
            {
                JOptionPane.showMessageDialog(getFrame(), String.format("A meal in this plan already has a meal time of '%s' !!", new_input_time_local_time));
                throw new Exception(); // Return null
            }
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s", e);
            return null;
        }
        
        //#############################################################################
        // User Confirmation
        //#############################################################################
        if (! skip_confirmation) // If requested not to skip a confirmation msg prompt confirmation
        {
            if (! areYouSure(String.format("change meal time from '%s' to '%s'", get_Current_Meal_Time().toString(), input_meal_time_string)))
            {
                return null;
            }
        }
        
        //################################################################
        // Return Value
        //#################################################################
        return new_input_time_local_time;
    }
    
    private void set_Time_Variables(boolean hasMealTimeBeenChanged, LocalTime savedMealTime, LocalTime currentMealTime)
    {
        this.has_Meal_Time_Been_Changed = hasMealTimeBeenChanged;
        this.saved_meal_time = savedMealTime;
        this.current_meal_time = currentMealTime;
    }
    
    //########################
    // Accessor Methods
    //########################
    public LocalTime get_Current_Meal_Time()
    {
        return current_meal_time;
    }
    
    public LocalTime get_Saved_Meal_Time()
    {
        return saved_meal_time;
    }
    
    //####################################
    // Meal Name Functions
    //####################################
    private void edit_Name_BTN_Action()
    {
        //#########################################################################################################
        // Validation Checks
        //#########################################################################################################
        // Get User Input
        String new_input_meal_name = prompt_User_For_Meal_Name(false, true);
        
        if (new_input_meal_name == null) { return; } // Error occurred in validation checks above
        
        //#########################################################################################################
        // Update DB
        //#########################################################################################################
        String upload_query = """
                UPDATE draft_meals_in_plan
                SET meal_name = ?
                WHERE draft_meal_in_plan_id = ?;""";
        
        Object[] params = new Object[]{ new_input_meal_name, draft_meal_ID };
        
        if (! db.upload_Data(upload_query, params, "Error, unable to change Meal Name!")) { return; }
        
        //#########################################################################################################
        // Change Variable DATA & Object
        //#########################################################################################################
        collapsibleJpObj.set_Icon_Btn_Text(new_input_meal_name);
        set_Meal_Name_Variables(true, saved_meal_name, new_input_meal_name);  // Set Meal Name Variables
        
        //#########################################################################################################
        // Update total Meal View Time Col
        //#########################################################################################################
        totalMealTable.set_Value_On_Table(new_input_meal_name, 0, total_meal_name_col_pos);
        
        //#########################################################################################################
        // Internal / External Graphs
        //#########################################################################################################
        pie_Chart_Update_Title(); // Change Internal Graph Title if exists
        
        // Update External
        meal_plan_screen.update_External_Charts(false, "mealName", this, null, null);
        
        //#########################################################################################################
        // Change Button Text & Related Variables
        //#########################################################################################################
        // Success MSG
        JOptionPane.showMessageDialog(getFrame(), String.format("Successfully, changed meal name from ' %s ' to ' %s ' ", currentMealName, new_input_meal_name));
    }
    
    private String prompt_User_For_Meal_Name(boolean skip_confirmation, boolean comparison)
    {
        // Get User Input For Meal Name
        String new_meal_name = JOptionPane.showInputDialog(getFrame(), "Input Meal Name?");
        
        // User Cancelled or entered nothing
        if (new_meal_name == null || new_meal_name.isEmpty()) { return null; }
        
        // validate user input
        return input_Meal_Name_Validation(new_meal_name, comparison, skip_confirmation);
    }
    
    private String input_Meal_Name_Validation(String new_meal_name, boolean comparison, boolean skipConfirmation)
    {
        // Remove whitespace at the end of variable
        new_meal_name = StringUtils.capitalize(new_meal_name.trim());
        
        //#######################################################
        // Validation Checks
        //#######################################################
        if (contains_Symbols(new_meal_name)) // Name: check if any symbols are inside
        {
            JOptionPane.showMessageDialog(getFrame(), "\n\nError, 'Meal Name' cannot contain symbols!");
            return null;
        }
        
        // ####################################################
        // Compare with saved correlating values
        // ####################################################
        if (comparison)
        {
            // User enters same meal name
            if (new_meal_name.equals(get_Current_Meal_Name()))
            {
                JOptionPane.showMessageDialog(getFrame(), String.format("This meal 'Meal Name' already has the value '%s' !!", get_Current_Meal_Name()));
                return null;
            }
        }
        
        // ######################################################
        // Check Database if Value Already Exists
        // ######################################################
        String query = "SELECT 1 FROM draft_meals_in_plan WHERE plan_id = ? AND meal_name = ?";
        String errorMSG = "Error, Validating Meal Name!";
        Object[] params = new Object[]{ get_Plan_ID(), new_meal_name };
        
        // Execute Query
        try
        {
            if (! db.get_Single_Col_Query_Obj(query, params, errorMSG, true).isEmpty()) // Means value already exists, returns N/A if the value doesn't
            {
                JOptionPane.showMessageDialog(getFrame(), String.format("A meal in this plan already has a meal Meal Name of '%s' !!", new_meal_name));
                throw new Exception(); // Return null
            }
        }
        catch (Exception e)
        {
            return null;
        }
        
        //##############################################################################################################
        // User Confirmation
        //##############################################################################################################
        if (! skipConfirmation) // If requested not to skip a confirmation msg prompt confirmation
        {
            if (! areYouSure(String.format("change meal Meal Name from '%s' to '%s'", get_Current_Meal_Name(), new_meal_name)))
            {
                return null;
            }
        }
        
        //##############################################################################################################
        // Return Value
        //##############################################################################################################
        return new_meal_name;
    }
    
    private void set_Meal_Name_Variables(boolean hasMealNameBeenChanged, String savedMealName, String currentMealName)
    {
        this.has_Meal_Name_Been_Changed = hasMealNameBeenChanged;
        this.saved_meal_name = savedMealName;
        this.currentMealName = currentMealName;
    }
    
    public String get_Current_Meal_Name()
    {
        return currentMealName;
    }
    
    //#################################################################################
    // Add BTN
    //#################################################################################
    // HELLO, Needs to scroll down to the bottom of the MealManager
    private void add_Btn_Action()
    {
        //###########################################################
        // Upload & Fetch Variables
        //############################################################
        String errorMSG = "Error, unable to add SubMeal to Meal!";
        
        LinkedHashSet<Pair<String, Object[]>> upload_Queries_And_Params = new LinkedHashSet<>();
        LinkedHashSet<Pair<String, Object[]>> fetch_Queries_And_Params = new LinkedHashSet<>();
        
        int sub_Meal_ID;
        ArrayList<ArrayList<Object>> sub_Meal_DATA;
        
        //###############################
        // Insert Into Sub-Meals
        //###############################
        String upload_Q1 = """
                INSERT INTO draft_divided_meal_sections
                (draft_meal_in_plan_id, plan_id)
                VALUES (?,?);""";
        
        upload_Queries_And_Params.add(new Pair<>(upload_Q1, new Object[]{ draft_meal_ID, get_Plan_ID() })); // Upload Q1
        
        //###############################
        // Insert Ingredients Into Sub-Meal
        //###############################
        String upload_Q2 = """
                INSERT INTO draft_ingredients_in_sections_of_meal
                (
                    draft_div_meal_sections_id,
                    ingredient_id,
                    pdid,
                    quantity
                )
                VALUES
                (
                    (SELECT last_insert_rowid()),
                     ?,
                     ?,
                     ?
                );""";
        
        
        Object[] q2_params = new Object[]{ na_ingredient_id, na_pdid, 0 };
        upload_Queries_And_Params.add(new Pair<>(upload_Q2, q2_params)); // Upload Q3
        
        //#######################################################
        // Fetch Query
        //#######################################################
        // Get IDs / Ingredients Row DATA
        String fetch_query_01 = """
                
                SELECT
                    D.draft_div_meal_sections_iD,
                    I2.*
                
                FROM draft_ingredients_in_sections_of_meal I1
                
                INNER JOIN draft_gui_ingredients_in_sections_of_meal_calculation I2
                    ON I1.draft_ingredients_index = I2.draft_ingredients_index
                
                INNER JOIN draft_divided_meal_sections D
                    ON I1.draft_div_meal_sections_id = D.draft_div_meal_sections_id
                
                WHERE
                    I1.draft_ingredients_index = (SELECT last_insert_rowid())
                
                LIMIT 1""";
        
        fetch_Queries_And_Params.add(new Pair<>(fetch_query_01, null)); // Upload Params
        
        //#######################################################
        // Execute Query
        //#######################################################
        Fetched_Results fetched_Results_OBJ = db.upload_And_Get_Batch(upload_Queries_And_Params, fetch_Queries_And_Params, errorMSG);
        
        if (fetched_Results_OBJ == null) { System.err.println("\n\n\nFailed Creating Meal"); return; }
        
        //#######################################################
        // Set Variables from Results
        //#######################################################
        try
        {
            ArrayList<ArrayList<Object>> combined_data = fetched_Results_OBJ.get_Fetched_Result_2D_AL(0);
            
            // Get SubMeal ID then removed it
            sub_Meal_ID = (int) combined_data.getFirst().removeFirst();
            
            sub_Meal_DATA = combined_data;
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s", e);
            return;
        }
        
        //#######################################################
        // Add Meal To GUI
        //#######################################################
        add_Sub_Meal(false, sub_Meal_ID, sub_Meal_DATA); // Add Sub-Meal to GUI
        
        //#######################################################
        // Success MSG & Expand Meal View
        //#######################################################
        JOptionPane.showMessageDialog(null, String.format("Successfully Created Sub-Meal in %s at [%s]",
                currentMealName, get_Current_Meal_Time())); // Show Success MSG
        
        expand_JPanel(); // Expand Meal View
        
        meal_plan_screen.scrollToJPanelOnScreen(get_Collapsible_JP_Obj()); // Scroll GUI to MealManager
    }
    
    //#################################################################################
    // Delete BTN Methods
    //#################################################################################
    private void delete_Btn_Action()
    {
        if (! areYouSure("Delete")) { return; }
        
        delete_Meal_Manager_Action();
        
        update_MacrosLeft_Table(); // Update MacrosLeft_Table
        
        JOptionPane.showMessageDialog(null, "Table Successfully Deleted!"); // Show MSG
    }
    
    private void delete_Meal_Manager_Action()
    {
        //##########################################
        // Delete MealManager Queries
        //##########################################
        String query = "DELETE FROM draft_meals_in_plan WHERE draft_meal_in_plan_id = ?";
        Object[] params = new Object[]{ draft_meal_ID };
        
        //##########################################
        // Execute Update
        //##########################################
        if (! db.upload_Data(query, params, "Table Un-Successfully Deleted!")) { return; }
        
        //##########################################
        // Update GUI
        //##########################################
        delete_MealManager(); // Delete MealManager Actions
        
        // Delete in External Charts
        meal_plan_screen.update_External_Charts(false, "delete", this, get_Current_Meal_Time(), get_Current_Meal_Time());
    }
    
    private void delete_MealManager()
    {
        shared_Data_Registry.delete_MealManager(this); // Update Registry Data
        
        hide_MealManager(); // Hide JTable object & Collapsible OBJ
    }
    
    private void hide_MealManager()
    {
        //##########################################
        // Set Variables
        //##########################################
        set_Visibility(false); // hide collapsible Object
        
        //##########################################
        // Remove From GUI
        //##########################################
        container.remove(collapsibleJpObj); // remove the GUI elements from GUI
        container.remove(spaceDividerForMealManager);    // remove space divider from GUI
        
        //##########################################
        // Delete PieChart (Meal is Gone)
        //##########################################
        close_Pie_Chart_Screen();
    }
    
    private void set_Visibility(boolean condition)
    {
        collapsibleJpObj.setVisible(condition);
        spaceDividerForMealManager.setVisible(condition);
    }
    
    //################################################
    // Delete Ingredients_Table Methods
    //################################################
    public void ingredients_Table_Has_Been_Deleted()
    {
        boolean active_Table =
                ingredient_tables_AL  // returns true if there is a meal that hasn't been deleted
                        .stream()
                        .anyMatch(t -> ! t.is_Table_Deleted()); // noneMatch returns true if predicate isn't met
        
        if (active_Table) { return; } // If there are meals still in this plan exit
        
        delete_Meal_Manager_Action(); // delete table
    }
    
    public void remove_Ingredients_Table(IngredientsTable ingredientsTable)
    {
        ingredient_tables_AL.remove(ingredientsTable);
    }
    
    //#################################################################################
    // Refresh Methods
    //#################################################################################
    private void refresh_Btn_Action()
    {
        // Check IF OLD Table Name & Meal Time Are Available To Assign Back To This Meal
        String errorMSG = "Error, Unable to Refresh Meal!";
        String query = """
                SELECT IFNULL(M.pos, "N/A") AS pos
                FROM
                (
                  -- This being the anchor had to restricted to make sure there's always a true value
                  -- to attach to for IFNULL to work
                
                  SELECT plan_id FROM plans WHERE plan_id = ?
                
                ) AS P
                LEFT JOIN
                (
                	-- Has to be restricted on plan_id then count as
                
                	SELECT
                	ROW_NUMBER() OVER (ORDER BY meal_time ASC) AS pos,
                	meal_in_plan_id, plan_id, meal_name, meal_time
                	FROM meals_in_plan
                	WHERE plan_id = ?
                
                ) AS M
                
                ON P.plan_id = M.plan_id
                AND (M.meal_name = ? OR M.meal_time = ?)
                AND M.meal_in_plan_id != ?;""";
        
        Object[] params_refresh = new Object[]{ null, null, saved_meal_name, get_Saved_Meal_Time(), draft_meal_ID };
        
        ArrayList<ArrayList<Object>> results;
        
        try
        {
            results = db.get_2D_Query_AL_Object(query, params_refresh, errorMSG, false);
        }
        catch (Exception e)
        {
            return;
        }
        
        //###########################################
        // IF TRUE Notify Meal Name/Time Taken
        //###########################################
        ArrayList<Object> positions = new ArrayList<>();
        
        if ((results.get(0).getFirst().toString()).equals("N/A"))
        {
            positions.add(results.get(0).getFirst());
        }
        
        if (results.size() >= 2 && ! (results.get(1).getFirst().toString()).equals("N/A"))
        {
            positions.add(results.get(1).getFirst());
        }
        
        if (! positions.isEmpty())
        {
            JOptionPane.showMessageDialog(getFrame(), String.format("""
                            \n\nA meal in this plan already has this meals saved info:
                            
                            'Meal Name' of : '%s' or 'Meal Time' of : '%s'
                            
                            at positions : %s  which is stopping this meal from refreshing !
                            
                            Change those values first at positions : %s in this plan
                            to be able to refresh this meal!
                            
                            Or, refresh the whole plan if the other meals won't be affected !""", saved_meal_name,
                    saved_meal_time, positions, positions));
            
            return;
        }
        
        //##############################################################################################
        // Reset DB Data
        //##############################################################################################
        if (! (transferMealDataToPlan("refresh", null, null)))
        {
            JOptionPane.showMessageDialog(null, "\n\nUnable to transfer mealData toS!!");
            return;
        }
        
        //##############################################################################################
        // RELOAD IngredientsTable & TotalMeal Table & Update  Chart DATA
        //##############################################################################################
        reload_Table_And_Charts_Data();
        
        //##############################################################################################
        // Remove & Re-add to GUI
        //##############################################################################################
        meal_plan_screen.add_And_Replace_MealManger_POS_GUI(this, true, true);
    }
    
    private boolean transferMealDataToPlan(String process, Integer fromPlanID, Integer toPlanID)
    {
        System.out.printf("\n\n%s transferMealDataToPlan()  %s %s %s", lineSeparator, process, fromPlanID, toPlanID);
        //########################################################
        // Drop Temp Tables
        //########################################################
        
        // Delete tables if they already exist
        String query0 = "DROP TABLE IF EXISTS temp_ingredients_in_meal;";
        
        String query1 = "DROP TABLE IF EXISTS temp_divided_meal_sections;";
        
        //########################################################
        // Clear Old Data from toPlan and & Temp Tables
        //########################################################
        // Delete sub-meals from this meal in toPlan
        String query2 = "DELETE FROM divided_meal_sections WHERE meal_in_plan_id = ? AND plan_id = ? ;";
        
        //####################################################
        // Transferring this plans Ingredients to Temp-Plan
        //####################################################
        
        // Create Table to transfer ingredients from original plan to temp
        String query3 = """
                CREATE TABLE temp_divided_meal_sections  AS
                SELECT i.*
                FROM divided_meal_sections i
                WHERE i.meal_in_plan_id = ? AND i.plan_id = ?;""";
        
        String query4 = "UPDATE temp_divided_meal_sections SET plan_id = ?;";
        
        String query5 = "INSERT INTO divided_meal_sections SELECT * FROM temp_divided_meal_sections;";
        //####################################################
        // Transferring ingredients from this meal in toPlan
        //####################################################
        
        // Create Table to transfer ingredients from original plan to temp
        String query6 = """
                CREATE table temp_ingredients_in_meal  AS
                SELECT i.*
                FROM ingredients_in_sections_of_meal i
                WHERE div_meal_sections_id IN (SELECT div_meal_sections_id FROM divided_meal_sections WHERE meal_in_plan_id = ? AND plan_id = ?)
                AND plan_id = ?;""";
        
        String query7 = "UPDATE temp_ingredients_in_meal SET plan_id = ?;";
        
        String query8 = "INSERT INTO ingredients_in_sections_of_meal SELECT * FROM temp_ingredients_in_meal;";
        
        String query9 = "DROP TABLE IF EXISTS temp_ingredients_in_meal;";
        
        String query10 = "DROP TABLE IF EXISTS temp_divided_meal_sections;";
        
        //#####################################################
        // Meal Name & Time Updates If Changed
        //#####################################################
        String updateMealName = process.equals("refresh") ? saved_meal_name : currentMealName; // set mealName to refresh
        LocalTime updateMealTime = process.equals("refresh") ? get_Saved_Meal_Time() : get_Current_Meal_Time(); // set mealTime to time
        
        //#####################################################
        // Create Query Formatted Data for Method
        //#####################################################
        String errorMSG = "Error, Unable to Transfer Plan Data!";
        
        LinkedHashSet<Pair<String, Object[]>> queries_And_Params = new LinkedHashSet<>()
        {{
            
            add(new Pair<>(query0, null));
            add(new Pair<>(query1, null));
            
            if (has_Meal_Name_Been_Changed || has_Meal_Time_Been_Changed)
            {
                String uploadQuery = """
                        UPDATE meals_in_plan
                        SET meal_name = ?, meal_time = ?
                        WHERE plan_id = ? AND meal_in_plan_id = ?;""";
                
                add(new Pair<>(uploadQuery, new Object[]{ updateMealName, updateMealTime, toPlanID, draft_meal_ID }));
            }
            
            add(new Pair<>(query2, new Object[]{ draft_meal_ID, toPlanID })); // Already in correct dataType
            add(new Pair<>(query3, new Object[]{ draft_meal_ID, fromPlanID })); //
            add(new Pair<>(query4, new Object[]{ toPlanID }));
            add(new Pair<>(query5, null));
            add(new Pair<>(query6, new Object[]{ draft_meal_ID, fromPlanID, fromPlanID }));
            add(new Pair<>(query7, new Object[]{ toPlanID }));
            add(new Pair<>(query8, null));
            add(new Pair<>(query9, null));
            add(new Pair<>(query10, null));
        }};
        
        //####################################################
        // Return Update /Output
        //####################################################
        return db.upload_Data_Batch(queries_And_Params, errorMSG);
    }
    
    private void reload_Table_And_Charts_Data()
    {
        //#############################################################################################
        // Reset GUI  & Variables
        //##############################################################################################
        // Reset Time & MealName Variables
        set_Time_Variables(false, saved_meal_time, saved_meal_time);
        set_Meal_Name_Variables(false, saved_meal_name, saved_meal_name);
        
        collapsibleJpObj.set_Icon_Btn_Text(saved_meal_name); // Reset Meal Name in GUI to Old Txt
        
        //##############################################################################################
        // Refresh IngredientTables
        //##############################################################################################
        Iterator<IngredientsTable> it = ingredient_tables_AL.iterator();
        while (it.hasNext())
        {
            IngredientsTable ingredientsTable = it.next();
            
            // If ingredientsTable is in DB  then refresh
            if (ingredientsTable.get_Meal_In_DB())
            {
                ingredientsTable.refresh_Data(false, false);
                continue;
            }
            
            // Because mealManager has been deleted remove it
            ingredientsTable.completely_Delete_Ingredients_Table(); // delete table from db
            
            // Because mealManager has been deleted remove it
            it.remove(); // remove from list
        }
        
        //##############################################################################################
        // Refresh TotalMeal_Table DATA & Charts
        //##############################################################################################
        update_MealManager_DATA();
        
        pie_Chart_Update_Title(); // Update Internal PieChart Title
        
        update_MacrosLeft_Table();
    }
    
    //##############################################################################################
    // Save BTN Methods
    //##############################################################################################
    public void save_Btn_Action()
    {
        save_Data_MM(true);
        
        
        // ###############################################################################
        // Set Variables
        // ###############################################################################
        set_Time_Variables(false, current_meal_time, current_meal_time);
        set_Meal_Name_Variables(false, currentMealName, currentMealName);
        
        
        // ###############################################################################
        // Removing Sub-Meals that have been deleted & Saving The Other Tables
        // ##############################################################################
        Iterator<IngredientsTable> it = ingredient_tables_AL.iterator();
        while (it.hasNext())
        {
            IngredientsTable table = it.next();
            
            // #####################################
            // Remove Deleted Ingredients Table
            // #####################################
            if (table.is_Table_Deleted())   // If objected is deleted, completely delete it then skip to next JTable
            {
                table.completely_Delete_Ingredients_Table();
                it.remove();
                continue;
            }
            
            table.save_Data();
        }
        
        // ##############################################################################
        // Successful Message
        // ##############################################################################
        
        JOptionPane.showMessageDialog(getFrame(), "\n\nAll SubMeals Within Meal Have Successfully Saved!");
        
    }
    
    private void save_Data_MM(boolean show_Update_Message)
    {
    
    }
    
    //######################################
    //
    //######################################
    public void save_Requested_By_Plan(int new_draft_meal_ID, ArrayList<Integer> new_sub_meal_ids)
    {
        draft_meal_ID = new_draft_meal_ID;
        
        set_MealManager_In_DB(true);
        
    }
    
    
    //######################################
    // Others
    //######################################
    public void expand_JPanel()
    {
        get_Collapsible_JP_Obj().expand_JPanel();
    }
    
    public void collapse_MealManager()
    {
        get_Collapsible_JP_Obj().collapse_JPanel();
    }
    
    //#################################################################################
    // Pie Charts
    //#################################################################################
    private void pieChart_Action()
    {
        // If pieChart is already created bring up to the surface and make it visible
        if (is_Pie_Chart_Open())
        {
            System.out.println("\n\nB : pieChart_Action() 1 !!!!");
            
            pie_chart_screen.makeJFrameVisible();
            return;
        }
        
        pie_chart_screen = new Pie_Chart_Meal_Manager_Screen(db, shared_Data_Registry, this);
    }
    
    private void pie_Chart_Update_Title()
    {
        if (is_Pie_Chart_Open())  // Change Internal Graph Title if exists
        {
            pie_chart_screen.update_Pie_Chart_Title();
        }
    }
    
    /**
     * 1.) Remove Pie Chart Object
     * <p>
     * 2.) IF Meal_Plan_Screen PieChart Screen is NULL, Pie Data can be removed
     * as it's not in use and not on display somewhere else     *
     */
    public void remove_Pie_Chart_Screen()
    {
        pie_chart_screen = null;
        
        //############################################
        // External DATA if not USED
        //############################################
        if (meal_plan_screen.is_PieChart_Screen_Open()) { return; }
        
        shared_Data_Registry.remove_PieChart_DatasetValues(this);
    }
    
    // External Call Usage
    public void close_Pie_Chart_Screen()
    {
        if (! is_Pie_Chart_Open()) { return; }
        
        pie_chart_screen.window_Closed_Event();
    }
    
    public boolean is_Pie_Chart_Open()
    {
        return pie_chart_screen != null;
    }
    
    //##################################################################################################################
    // Updating Other Tables
    //##################################################################################################################
    public void update_MealManager_DATA()
    {
        try
        {
            ArrayList<Object> total_meal_data = update_TotalMeal_Table_And_Get_Data(); // Update TotalMealView (Has to be first)
            
            shared_Data_Registry.add_OR_Replace_MealManager_Macros_DATA(this, total_meal_data);  // Update Registry Data (Second)
            
            update_Charts(); // Update Charts
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s Error -> \n%s", get_Class_And_Method_Name(), e);
        }
    }
    
    private void update_Charts()
    {
        /**
         * Update data behind pieCharts which will effectively update all pieCharts actively using this data
         * etc the MPS totals pie chart screen
         */
        if (! shared_Data_Registry.update_PieChart_Values(this)) // Update Internal Charts
        {
            System.err.printf("\n\nMPS : update_Pie_Chart_DATA() \nPieChart not Open %s", get_Draft_Meal_In_Plan_ID());
        }
        
        // Update External Charts
        meal_plan_screen.update_External_Charts(false, "update", this, get_Current_Meal_Time(), get_Current_Meal_Time());
    }
    
    private ArrayList<Object> update_TotalMeal_Table_And_Get_Data() throws Exception
    {
        return totalMealTable.update_Table_And_Get_Data();
    }
    
    public void update_Total_Meal()
    {
        totalMealTable.update_Table();
    }
    
    private void update_MacrosLeft_Table()
    {
        macrosLeft_JTable.update_Table();
    }
    
    //##################################################################################################################
    // Mutator Methods
    //##################################################################################################################
    public void set_Draft_meal_ID(int draft_meal_ID)
    {
        this.draft_meal_ID = draft_meal_ID;
    }
    
    private void set_MealManager_In_DB(boolean state)
    {
        this.is_MealManager_In_DB = state;
    }
    
    private void set_Is_Meal_Saved(boolean state)
    {
        is_Meal_Saved = state;
    }
    
    //##################################################################################################################
    // Accessor Methods
    //##################################################################################################################
    
    
    // ############################################
    // Booleans
    // ############################################
    public boolean is_Object_Created()
    {
        return is_Object_Created;
    }
    
    public boolean is_MealManager_In_DB()
    {
        return is_MealManager_In_DB;
    }
    
    public boolean is_Meal_Saved()
    {
        return is_Meal_Saved;
    }
    
    // ############################################
    // Other Objects
    // ############################################
    public Frame getFrame()
    {
        return meal_plan_screen.getFrame();
    }
    
    public JPanel getSpaceDividerForMealManager()
    {
        return spaceDividerForMealManager;
    }
    
    // ###########################
    // JPanel
    // ###########################
    public CollapsibleJPanel get_Collapsible_JP_Obj()
    {
        return collapsibleJpObj;
    }
    
    public JPanel get_Collapsible_Center_JPanel()
    {
        return collapsibleCenterJPanel;
    }
    
    // ############################################
    // Integers
    // ############################################
    public int get_Draft_Meal_In_Plan_ID()
    {
        return draft_meal_ID;
    }
    
    public int get_Source_Meal_ID()
    {
        return source_meal_id;
    }
    
    public int get_Plan_ID()
    {
        return shared_Data_Registry.get_Selected_Plan_ID();
    }
    
    //##################################################################################################################
    // Resizing GUI
    //##################################################################################################################
    private void add_To_Container(Container container, Component addToContainer, Integer gridX, Integer gridy, Integer gridWidth,
                                  Integer gridHeight, Double weightX, Double weightY, String fill, Integer ipadY, Integer ipadX, String anchor)
    {
        if (gridX != null)
        {
            gbc.gridx = gridX;
        }
        if (gridy != null)
        {
            gbc.gridy = gridy;
        }
        
        gbc.gridwidth = gridWidth;
        gbc.gridheight = gridHeight;
        gbc.weightx = weightX;
        gbc.weighty = weightY;
        
        gbc.ipady = ipadY;
        gbc.ipadx = ipadX;
        
        switch (fill.toLowerCase())
        {
            case "horizontal":
                gbc.fill = GridBagConstraints.HORIZONTAL;
                break;
            case "vertical":
                gbc.fill = GridBagConstraints.VERTICAL;
                break;
            
            case "both":
                gbc.fill = GridBagConstraints.BOTH;
                break;
        }
        
        if (anchor != null)
        {
            switch (anchor.toLowerCase())
            {
                case "start":
                    gbc.anchor = GridBagConstraints.PAGE_START;
                    break;
                
                case "scroll_To_The_End":
                    gbc.anchor = GridBagConstraints.PAGE_END;
                    break;
            }
        }
        container.add(addToContainer, gbc);
    }
    
    //##################################################################################################################
    // Object Equality Methods
    //##################################################################################################################
    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        
        if (! (o instanceof MealManager other)) { return false; }
        
        return internalId.equals(other.internalId);
    }
    
    @Override
    public int hashCode()
    {
        return internalId.hashCode();
    }
    
    //##################################################################################################################
    // Debugging Print Statements
    //##################################################################################################################
    protected String get_Class_Name()
    {
        return class_Name;
    }
    
    protected String get_Method_Name()
    {
        return String.format("%s()", Thread.currentThread().getStackTrace()[2].getMethodName());
    }
    
    protected String get_Class_And_Method_Name()
    {
        return String.format("%s -> @%s", get_Class_Name(), get_Method_Name());
    }
}
