package com.donty.gymapp.ui.components.meal;

import com.donty.gymapp.ui.meta.ids.MetaData_ID_Object.Meal_ID_OBJ;
import com.donty.gymapp.ui.meta.ids.MetaData_ID_Object.Sub_Meal_ID_OBJ;
import com.donty.gymapp.persistence.database.Fetched_Results;
import com.donty.gymapp.persistence.database.MyJDBC_Sqlite;
import com.donty.gymapp.persistence.database.batch.Batch_Upload_And_Fetch_Statements;
import com.donty.gymapp.persistence.database.batch.Batch_Upload_Statements;
import com.donty.gymapp.persistence.database.statements.Fetch_Statement;
import com.donty.gymapp.persistence.database.statements.Fetch_Statement_Full;
import com.donty.gymapp.persistence.database.statements.Upload_Statement;
import com.donty.gymapp.persistence.database.statements.Upload_Statement_Full;
import com.donty.gymapp.persistence.Shared_Data_Registry;
import com.donty.gymapp.ui.screens.mealPlan.Meal_And_Sub_Meals_OBJ;
import com.donty.gymapp.ui.tables.ingredients.IngredientsTable;
import com.donty.gymapp.ui.tables.viewData.MacrosLeft_Table;
import com.donty.gymapp.ui.tables.viewData.TotalMeal_Table;
import com.donty.gymapp.gui.panels.CollapsibleJPanel;
import com.donty.gymapp.gui.controls.IconButton;
import com.donty.gymapp.gui.panels.IconPanel;
import com.donty.gymapp.ui.screens.graphs.PieChart_MealManager_Screen.Pie_Chart_Meal_Manager_Screen;
import com.donty.gymapp.ui.screens.mealPlan.Meal_Plan_Screen;
import com.donty.gymapp.domain.enums.db_enums.views.totalmeal.Draft_Gui_Total_Meal_Other_Columns;
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
    private boolean is_Object_Created = false;

    private boolean
            is_MealManager_In_DB = false,
            is_Meal_Saved = false,
            has_MealManager_Data_Changed = false;

    private boolean
            has_Meal_Name_Been_Changed = false,
            has_Meal_Time_Been_Changed = false;

    //############################################
    // Integers
    //############################################
    private final UUID internalId = UUID.randomUUID();

    private final int
            na_ingredient_id,
            na_pdid;

    private int
            source_meal_id,
            draft_meal_ID,
            yPoInternally = 0,
            total_meal_time_col_pos,
            total_meal_name_col_pos;

    //############################################
    // String
    //############################################
    private String class_Name = new Object() { }.getClass().getEnclosingClass().getName();
    private String lineSeparator = "###############################################################################";
    private String
            saved_meal_name = null,
            current_meal_name = null;

    //############################################
    // Time
    //############################################
    private LocalTime
            saved_meal_time = null,
            current_meal_time = null;

    private DateTimeFormatter time_Formatter = DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);

    //############################################
    // Collections
    //############################################
    private final ArrayList<IngredientsTable> ingredient_tables_AL = new ArrayList<>();

    //################################################################################
    // Objects
    //################################################################################

    // Other Objects
    private JPanel
            collapsibleCenterJPanel,
            spaceDividerForMealManager = new JPanel();

    private final MyJDBC_Sqlite db;
    private GridBagConstraints gbc;
    private Container container;
    private CollapsibleJPanel collapsibleJpObj;
    private final Shared_Data_Registry shared_Data_Registry;

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
            Meal_And_Sub_Meals_OBJ meal_and_sub_meals_obj,
            ArrayList<Object> total_Meal_Data
    )
    {
        //################################################
        // Global Variables
        //################################################
        Meal_ID_OBJ meal_id_obj = meal_and_sub_meals_obj.get_Meal_ID_OBJ();

        this.meal_plan_screen = meal_plan_screen;
        this.shared_Data_Registry = shared_Data_Registry;
        this.db = db;
        this.macrosLeft_JTable = macrosLeft_JTable;

        draft_meal_ID = meal_id_obj.get_Draft_Meal_ID();
        source_meal_id = meal_id_obj.get_Source_Meal_ID();

        is_MealManager_In_DB = true;
        set_Is_Meal_Saved(true);  // Set Variable which identifies in this meal associated with this object is in the database

        na_pdid = shared_Data_Registry.get_NA_PDID();
        na_ingredient_id = shared_Data_Registry.get_Na_Ingredient_ID();

        // Set Meal Time Variables
        LocalTime meal_time = meal_id_obj.get_Meal_Time();
        set_Time_Variables(false, meal_time, meal_time); // Set MealTime Variables

        // Set Meal Name Variables
        String mealName = meal_id_obj.get_Name();
        set_Meal_Name_Variables(false, mealName, mealName); // Set MealName Variables

        //################################################
        // Setup Methods
        //################################################
        setup_GUI(total_Meal_Data); // GUI

        add_Multiple_Sub_Meals(meal_and_sub_meals_obj); // Add Sub-Meal to GUI
    }

    //#####################################
    //
    //#####################################
    public MealManager
    (
            Meal_Plan_Screen meal_plan_screen,
            Shared_Data_Registry shared_Data_Registry,
            MyJDBC_Sqlite db,
            MacrosLeft_Table macrosLeft_JTable
    )
    {
        //#######################################################
        // Setting Variables
        //#######################################################
        this.meal_plan_screen = meal_plan_screen;
        this.shared_Data_Registry = shared_Data_Registry;
        this.macrosLeft_JTable = macrosLeft_JTable;
        this.db = db;

        na_pdid = shared_Data_Registry.get_NA_PDID();
        na_ingredient_id = shared_Data_Registry.get_Na_Ingredient_ID();

        //#######################################################
        // Get & Validate Meal Name Input
        //#######################################################
        String input_meal_name = prompt_User_For_Meal_Name("Input Meal Name?");
        if (input_meal_name == null) { return; } // Error occurred in validation checks above

        String new_Meal_Name = input_Meal_Name_Validation(input_meal_name, "Meal");
        if (new_Meal_Name == null) { return; } // Error occurred in validation checks above

        if (! is_Meal_Name_Available(new_Meal_Name)) { return; }

        //#######################################################
        // Get & Validate Meal Time Input
        //#######################################################
        String time_input = prompt_User_For_Time_Input("Enter New Meal Time");

        LocalTime new_meal_time = convert_To_Local_Time(time_input);

        if (new_meal_time == null) { return; } // Error occurred in validation checks above

        if (! is_Meal_Time_Available(new_meal_time)) { return; }


        //#######################################################
        // Upload & Get Results
        //#######################################################
        String sub_meal_name = "New Sub-Meal";

        Fetched_Results fetched_Results_OBJ = create_New_Meal(new_Meal_Name, new_meal_time, sub_meal_name);

        if (fetched_Results_OBJ == null) { System.err.println("\n\n\nFailed Creating Meal"); return; }

        //###############################
        // Set Variables from Results
        //###############################
        ArrayList<Object> total_Meal_Data = null;
        Sub_Meal_ID_OBJ sub_meal_id_obj = null;

        try

        {
            ArrayList<ArrayList<Object>> sub_Meal_DATA = fetched_Results_OBJ.get_Fetched_Result_2D_AL(0);
            ArrayList<Object> combined_results = sub_Meal_DATA.getFirst();

            draft_meal_ID = (Integer) combined_results.removeFirst(); // Get Draft Meal ID & Remove IT

            int draft_sub_Meal_ID = (Integer) combined_results.removeFirst();  // Get Draft Sub ID & Remove IT

            // Get Sub Ingredients & Remove IT
            sub_meal_id_obj = new Sub_Meal_ID_OBJ(
                    draft_sub_Meal_ID,
                    sub_meal_name,
                    new_meal_time,
                    sub_Meal_DATA,
                    draft_meal_ID
            );

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

        set_Time_Variables(false, new_meal_time, new_meal_time);     // Set MealTime Variables

        //#######################################################
        // Add Meals To GUI
        //#######################################################
        setup_GUI(total_Meal_Data); // GUI
        add_Sub_Meal(sub_meal_id_obj); // Add Sub-Meal to GUI
    }

    private Fetched_Results create_New_Meal(String new_Meal_Name, LocalTime new_meal_time, String sub_meal_name)
    {
        //#######################################################
        // Upload & Fetch Variables
        //#######################################################
        String error_msg = String.format("\n\nError Creating Meal with credentials: \n\nMeal Name: '%s' \nMeal Time: %s!", new_Meal_Name, new_meal_time);

        Batch_Upload_And_Fetch_Statements batch_Statements = new Batch_Upload_And_Fetch_Statements(error_msg);

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

        batch_Statements.add_Uploads(new Upload_Statement(upload_Q1, new Object[]{ get_Plan_ID(), new_Meal_Name, new_meal_time }, true));

        //###############################
        // Insert Into Sub-Meals
        //###############################
        String upload_Q2 = """
                INSERT INTO draft_divided_meal_sections
                (
                    draft_meal_in_plan_id,
                    plan_id,
                    sub_meal_name,
                    sub_meal_time
                )
                VALUES
                (
                    (SELECT last_insert_rowid()),
                    ?,
                    ?,
                    ?
                );""";

        Object[] upload_params_02 = new Object[]{ get_Plan_ID(), sub_meal_name, new_meal_time };

        batch_Statements.add_Uploads(new Upload_Statement(upload_Q2, upload_params_02, true)); // Upload Q1

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
        batch_Statements.add_Uploads(new Upload_Statement(upload_Q3, q3_params, true)); // Upload Q3

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

        // Upload Params
        batch_Statements.add_Fetches(new Fetch_Statement(fetch_query_01, new Object[]{ get_Plan_ID(), new_Meal_Name }));

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

        batch_Statements.add_Fetches(new Fetch_Statement(fetch_query_02, new Object[]{ get_Plan_ID(), new_Meal_Name })); // Upload Params

        //#######################################################
        // Execute Query
        //#######################################################
        return db.upload_And_Get_Batch(batch_Statements);
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
            pie_Chart_Action();
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

            if (are_You_Sure("Refresh Data"))
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
            if (are_You_Sure("Save Data"))
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
                        .get_Other_Total_Meal_Table_Column_Pos(Draft_Gui_Total_Meal_Other_Columns.MEAL_TIME);

        total_meal_name_col_pos =
                shared_Data_Registry
                        .get_Other_Total_Meal_Table_Column_Pos(Draft_Gui_Total_Meal_Other_Columns.MEAL_NAME);

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
    private void add_Multiple_Sub_Meals(Meal_And_Sub_Meals_OBJ meal_and_sub_meals_obj)
    {
        HashMap<Integer, Sub_Meal_ID_OBJ> sub_meal_id_map = meal_and_sub_meals_obj.get_Sub_Meal_ID_Map();

        // Iterate Through each Sub-Meal Data & Add to GUI
        for (Sub_Meal_ID_OBJ sub_meal_id_obj : sub_meal_id_map.values())
        {
            add_Sub_Meal(sub_meal_id_obj);
        }
    }

    private void add_Sub_Meal(Sub_Meal_ID_OBJ sub_meal_id_obj)
    {
        //##############################################
        // Create Ingredient Table Object
        //##############################################
        IngredientsTable ingredients_Table =
                new IngredientsTable(
                        db,
                        this,
                        shared_Data_Registry,
                        sub_meal_id_obj
                );

        //################################################
        // Add Ingredients Table To GUI
        //################################################
        add_Sub_Meal_To_GUI(ingredients_Table);


        //################################################
        // Add Ingredient Table To Collection
        //################################################
        ingredient_tables_AL.add(ingredients_Table);
    }

    private void add_Sub_Meal_To_GUI(IngredientsTable ingredients_table)
    {
        JPanel spaceDivider = ingredients_table.get_Space_Divider();

        add_To_Container(collapsibleCenterJPanel, ingredients_table, 0, yPoInternally++, 1, 1, 0.25, 0.25, "both", 0, 0, null);
        add_To_Container(collapsibleCenterJPanel, spaceDivider, 0, yPoInternally++, 1, 1, 0.25, 0.25, "both", 50, 0, null);
    }

    //##################################################################################################################
    // Actions
    //##################################################################################################################
    private boolean are_You_Sure(String process)
    {
        int reply = JOptionPane.showConfirmDialog(getFrame(), String.format("You are requesting to %s ! \n\nAre you sure you want to %s?", process, process),
                "Notification", JOptionPane.YES_NO_OPTION); //HELLO Edit

        return reply != JOptionPane.NO_OPTION && reply != JOptionPane.CLOSED_OPTION;
    }


    //########################################################
    // Sub-Meal Ordering & Positioning
    //########################################################
    public void sort_And_Re_Draw_Sub_Meals()
    {
        yPoInternally = 0; // Reset Positioning

        sort_Sub_Meals(); // Sort Sub-Meals

        collapsibleCenterJPanel.removeAll();  // Clear MealManager

        ingredient_tables_AL.forEach(this :: add_Sub_Meal_To_GUI); // Add Each Meal to GUI
    }

    private void sort_Sub_Meals()
    {
        ingredient_tables_AL.sort((a, b) -> a.get_Current_Sub_Meal_Time().compareTo(b.get_Current_Sub_Meal_Time()));
    }

    //########################################################
    // Meal Time Functions
    //########################################################
    private void edit_Time_Btn_Action()
    {
        //###############################
        // Get & Validate Time Input
        //###############################

        // Prompt User for Time Input
        String time_input = prompt_User_For_Time_Input("Input Meal Time etc \"09:00\"?");

        // Convert To LocalTime / Syntax Validate
        LocalTime new_meal_time = convert_To_Local_Time(time_input);
        if (new_meal_time == null) { return; }

        // Compare current time with new time input
        if (get_Current_Meal_Time().equals(new_meal_time)) // Time : User enters same meal time
        {
            JOptionPane.showMessageDialog(getFrame(), String.format("This meal 'time' already has the value '%s' !!", new_meal_time));
            return;
        }

        // DB Validation
        if (! is_Meal_Time_Available(new_meal_time)) { return; }

        //###############################
        // User Confirmation
        //###############################
        LocalTime old_current_time = get_Current_Meal_Time();

        if (! are_You_Sure(String.format("change meal time from '%s' to '%s'", old_current_time, new_meal_time)))
        {
            return;
        }

        //###############################
        // Update
        //###############################
        String uploadQuery = """
                UPDATE draft_meals_in_plan
                SET meal_time = ?
                WHERE draft_meal_in_plan_id = ?;""";

        String error_msg = "Error, unable to change Meal Time!";
        Object[] params = new Object[]{ new_meal_time, draft_meal_ID };

        // Upload Into Database Table
        Upload_Statement_Full sql_statement = new Upload_Statement_Full(uploadQuery, params, error_msg, true);
        if (! db.upload_Data(sql_statement)) { return; }

        //###############################
        // Update Variables & DATA
        //###############################
        set_Time_Variables(true, saved_meal_time, new_meal_time); // Set Meal Time Variables
        set_Has_Meal_Data_Changed(true);

        // Re-Draw GUI for time positioning
        meal_plan_screen.add_And_Replace_MealManger_POS_GUI(this, true, true);

        // Update External Charts
        meal_plan_screen.update_External_Charts(false, "mealTime", this, old_current_time, new_meal_time);

        // Update TotalMeal Time Slot
        totalMealTable.set_Value_On_Table(new_meal_time, 0, total_meal_time_col_pos); // Update total Meal Table Time Value

        pie_Chart_Update_Title(); // Update PieChart Title

        //###############################
        // Success Msg
        //###############################
        JOptionPane.showMessageDialog(getFrame(), String.format("Successfully, changed meal time from '%s' to '%s'",
                old_current_time, new_meal_time)); // Success MSG
    }

    //###########################
    // Time Validation
    //###########################
    private String prompt_User_For_Time_Input(String msg)
    {
        // User info prompt
        String input_meal_time = JOptionPane.showInputDialog(msg);

        return input_meal_time == null || input_meal_time.isEmpty() ? null : input_meal_time;
    }

    private LocalTime convert_To_Local_Time(String input_meal_time_string)
    {
        //#######################################################
        // Validation Checks
        //#######################################################
        // Prior to this method being called the users input_meal_time_string is checked if its null or "" and rejected
        LocalTime converted_time = null;

        // Switch ';' for ':' removes un-intentional errors
        input_meal_time_string = input_meal_time_string.replaceAll(";", ":");

        try
        {
            converted_time = LocalTime.parse(input_meal_time_string, time_Formatter);
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s | Error, converting input_meal_time_string to time string! \n%s", get_Class_And_Method_Name(), e);
            JOptionPane.showMessageDialog(getFrame(), "Error, converting input to LocalTime!");
            return null;
        }

        //################################################################
        // Return Value
        //#################################################################
        return converted_time;
    }

    private boolean is_Meal_Time_Available(LocalTime new_meal_time)
    {
        // ######################################################
        // Check Database if Value Already Exists
        // ######################################################
        String query = " SELECT 1 FROM draft_meals_in_plan WHERE plan_id = ? AND meal_time = ?";
        String errorMSG = "Error, Validating Meal Time!";
        Object[] params = new Object[]{ get_Plan_ID(), new_meal_time };

        Fetch_Statement_Full fetch_statement = new Fetch_Statement_Full(query, params, errorMSG);

        try // Execute Query
        {
            if (db.get_Single_Col_Query_Obj(fetch_statement, true).isEmpty()) { return true; }

            // Means value already exists, returns N/A if the value doesn't
            JOptionPane.showMessageDialog(getFrame(), String.format("A meal in this plan already has a meal time of '%s' !!", new_meal_time));

            throw new Exception();
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s", e);
            return false;
        }
    }

    //####################################
    // Meal Name Functions
    //####################################
    private void edit_Name_BTN_Action()
    {
        //##########################################
        // Get & Validate Meal Name Input
        //##########################################
        String input_meal_name = prompt_User_For_Meal_Name("Input Meal Name?");
        if (input_meal_name == null) { return; } // Error occurred in validation checks above

        String new_meal_name = input_Meal_Name_Validation(input_meal_name, "Meal");
        if (new_meal_name == null) { return; } // Error occurred in validation checks above

        if (! is_Meal_Name_Available(new_meal_name)) { return; }

        if (new_meal_name.equals(get_Current_Meal_Name()))   // User enters same meal name
        {
            JOptionPane.showMessageDialog(getFrame(), String.format("This meal already has the name '%s' !!", get_Current_Meal_Name()));
            return;
        }

        //##########################################
        // User Confirmation
        //##########################################
        if (! are_You_Sure(String.format("change meal Meal Name from '%s' to '%s'", get_Current_Meal_Name(), new_meal_name)))
        {
            return;
        }

        //##########################################
        // Update DB
        //##########################################
        String upload_query = """
                UPDATE draft_meals_in_plan
                SET meal_name = ?
                WHERE draft_meal_in_plan_id = ?;""";

        Object[] params = new Object[]{ new_meal_name, draft_meal_ID };
        String error_msg = "Error, unable to change Meal Name!";
        Upload_Statement_Full sql_statement = new Upload_Statement_Full(upload_query, params, error_msg, true);

        if (! db.upload_Data(sql_statement)) { return; }

        //##########################################
        // Update Variables & DATA / Objects
        //##########################################
        set_Meal_Name_Variables(true, saved_meal_name, new_meal_name);  // Set Meal Name Variables
        set_Has_Meal_Data_Changed(true);

        collapsibleJpObj.set_Icon_Btn_Text(new_meal_name); // Update Meal Manager Name

        pie_Chart_Update_Title(); // Change Internal Graph Title if exists

        // Update External Graphs
        meal_plan_screen.update_External_Charts(false, "mealName", this, null, null);

        // Update Total Meal Table Name Col
        totalMealTable.set_Value_On_Table(new_meal_name, 0, total_meal_name_col_pos);

        //##########################################
        // Success MSG
        //##########################################
        String msg = String.format("Successfully, changed meal name from ' %s ' to ' %s ' ", current_meal_name, new_meal_name);
        JOptionPane.showMessageDialog(getFrame(), msg);
    }

    private String prompt_User_For_Meal_Name(String title)
    {
        // Get User Input For Meal Name
        String new_meal_name = JOptionPane.showInputDialog(getFrame(), title);

        // User Cancelled or entered nothing
        return new_meal_name == null || new_meal_name.isEmpty() ? null : new_meal_name;
    }

    //#####################
    // Name Validation
    //#####################
    private String input_Meal_Name_Validation(String new_meal_name, String type)
    {
        //##########################################
        // Validation Checks
        //##########################################
        // Remove whitespace at the end of variable
        new_meal_name = StringUtils.capitalize(new_meal_name.trim());

        if (contains_Symbols(new_meal_name)) // Name: check if any symbols are inside
        {
            JOptionPane.showMessageDialog(getFrame(), String.format("\n\nError, '%s Name' cannot contain symbols!", type));
            return null;
        }

        //#########################################
        // Return Value
        //#########################################
        return new_meal_name;
    }

    private boolean contains_Symbols(String string_To_Check)
    {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9 '\\-&]+$");
        Matcher matcher = pattern.matcher(string_To_Check);

        return ! matcher.matches();
    }

    private boolean is_Meal_Name_Available(String new_meal_name)
    {
        //##########################################
        // Check Database if Value Already Exists
        //##########################################
        String query = "SELECT 1 FROM draft_meals_in_plan WHERE plan_id = ? AND meal_name = ?";
        String error_msg = "Error, Validating Meal Name!";
        Object[] params = new Object[]{ get_Plan_ID(), new_meal_name };

        Fetch_Statement_Full fetch_statement = new Fetch_Statement_Full(query, params, error_msg);

        try // Execute Query
        {
            if (db.get_Single_Col_Query_Obj(fetch_statement, true).isEmpty()) { return true; }

            // Means value already exists, returns N/A if the value doesn't
            JOptionPane.showMessageDialog(getFrame(), String.format("A meal in this plan already has a meal Meal Name of '%s' !!", new_meal_name));

            throw new Exception();
        }
        catch (Exception _)
        {
            return false;
        }
    }

    //#################################################################################
    // Add BTN
    //#################################################################################
    private void add_Btn_Action() // HELLO, Needs to scroll down to the bottom of the MealManager
    {
        //#######################################################
        // Get & Validate Meal Name Input
        //#######################################################
        String input_meal_name = prompt_User_For_Meal_Name("Input Sub-Meal Name?");
        if (input_meal_name == null) { return; } // Error occurred in validation checks above

        String sub_meal_name = input_Meal_Name_Validation(input_meal_name, "Sub-Meal");
        if (sub_meal_name == null) { return; } // Error occurred in validation checks above

        if (! is_Sub_Meal_Name_Available(sub_meal_name)) { return; }

        //#######################################################
        // Get & Validate Meal Time Input
        //#######################################################
        String time_input = prompt_User_For_Time_Input("Enter New Meal Time");

        LocalTime sub_meal_time = convert_To_Local_Time(time_input);

        if (sub_meal_time == null) { return; } // Error occurred in validation checks above

        if (! is_Sub_Meal_Time_Available(sub_meal_time)) { return; }

        if (! is_Sub_Meal_Time_In_Valid_Range(sub_meal_time)) { return; }

        //#######################################################
        // Upload & Fetch Variables
        //#######################################################
        String errorMSG = "Error, unable to add Sub-Meal to Meal!";
        Batch_Upload_And_Fetch_Statements batch_statements = new Batch_Upload_And_Fetch_Statements(errorMSG);

        int draft_sub_meal_id;
        ArrayList<ArrayList<Object>> sub_Meal_DATA;

        //###############################
        // Insert Into Sub-Meals
        //###############################
        String upload_Q1 = """
                INSERT INTO draft_divided_meal_sections
                (
                    draft_meal_in_plan_id,
                    plan_id,
                    sub_meal_name,
                    sub_meal_time
                )
                VALUES (?,?,?,?);""";

        Object[] params_1 = new Object[]{ draft_meal_ID, get_Plan_ID(), sub_meal_name, sub_meal_time };

        // Upload Q1
        batch_statements.add_Uploads(new Upload_Statement(upload_Q1, params_1, true));

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
        batch_statements.add_Uploads(new Upload_Statement(upload_Q2, q2_params, true)); // Upload Q3

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

        batch_statements.add_Fetches(new Fetch_Statement(fetch_query_01, null)); // Upload Params

        //#######################################################
        // Execute Query
        //#######################################################
        Fetched_Results fetched_Results_OBJ = db.upload_And_Get_Batch(batch_statements);

        if (fetched_Results_OBJ == null) { System.err.println("\n\n\nFailed Creating Meal"); return; }

        //#######################################################
        // Set Variables from Results
        //#######################################################
        try
        {
            ArrayList<ArrayList<Object>> combined_data = fetched_Results_OBJ.get_Fetched_Result_2D_AL(0);

            draft_sub_meal_id = (int) combined_data.getFirst().removeFirst(); // Get SubMeal ID then remove it

            sub_Meal_DATA = combined_data;
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s", e);
            return;
        }

        Sub_Meal_ID_OBJ sub_meal_id_obj = new Sub_Meal_ID_OBJ(
                draft_sub_meal_id,
                sub_meal_name,
                sub_meal_time,
                sub_Meal_DATA,
                draft_meal_ID
        );

        //#######################################################
        // Add Meal To GUI
        //#######################################################
        add_Sub_Meal(sub_meal_id_obj); // Add Sub-Meal to GUI

        expand_JPanel(); // Expand Meal View

        meal_plan_screen.scrollToJPanelOnScreen(get_Collapsible_JP_Obj()); // Scroll GUI to MealManager*/

        //#######################################################
        // Success MSG & Expand Meal View
        //#######################################################
        JOptionPane.showMessageDialog(null, String.format("Successfully Created Sub-Meal in %s at [%s]",
                current_meal_name, get_Current_Meal_Time())); // Show Success MSG
    }

    //############################
    // Sub-Meal Name Methods
    //############################
    private boolean is_Sub_Meal_Name_Available(String sub_meal_name)
    {
        //##########################################
        // Check Database if Value Already Exists
        //##########################################
        String query = "SELECT 1 FROM draft_divided_meal_sections WHERE plan_id = ? AND sub_meal_name = ?";
        String error_msg = "Error, Validating Sub-Meal Name!";
        Object[] params = new Object[]{ get_Plan_ID(), sub_meal_name };

        Fetch_Statement_Full fetch_statement = new Fetch_Statement_Full(query, params, error_msg);

        try // Execute Query
        {
            if (db.get_Single_Col_Query_Obj(fetch_statement, true).isEmpty()) { return true; }

            // Means value already exists, returns N/A if the value doesn't
            JOptionPane.showMessageDialog(getFrame(), String.format("A sub-meal in this plan already has a Sub-Meal of '%s' !!", sub_meal_name));

            throw new Exception();
        }
        catch (Exception _)
        {
            return false;
        }
    }

    //############################
    // Sub-Meal Time Methods
    //############################
    private boolean is_Sub_Meal_Time_Available(LocalTime sub_meal_time)
    {
        //##########################################
        // Check Database if Value Already Exists
        //##########################################
        String query = "SELECT 1 FROM draft_divided_meal_sections WHERE plan_id = ? AND sub_meal_time = ?";
        String error_msg = "Error, Validating Sub-Meal Name!";
        Object[] params = new Object[]{ get_Plan_ID(), sub_meal_time };

        Fetch_Statement_Full fetch_statement = new Fetch_Statement_Full(query, params, error_msg);

        try // Execute Query
        {
            if (db.get_Single_Col_Query_Obj(fetch_statement, true).isEmpty()) { return true; }

            // Means value already exists, returns N/A if the value doesn't
            JOptionPane.showMessageDialog(getFrame(), String.format("A sub-meal in this plan already has a Sub-Meal of '%s' !!", sub_meal_time));

            throw new Exception();
        }
        catch (Exception _)
        {
            return false;
        }
    }

    private boolean is_Sub_Meal_Time_In_Valid_Range(LocalTime sub_meal_time)
    {
        try
        {
            Pair<LocalTime, LocalTime> time_range = get_Available_Sub_Meal_Time_Ranges();

            LocalTime upper_bound = time_range.getValue0();
            LocalTime lower_bound = time_range.getValue1();

            if (sub_meal_time.isBefore(upper_bound) || sub_meal_time.isAfter(lower_bound))
            {
                String msg = String.format("Sub-Meal Time isn't in the available range [%s - %s ]!", upper_bound, lower_bound);

                JOptionPane.showMessageDialog(null, msg);

                return false;
            }

            return true;
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s \n\n%s", get_Class_And_Method_Name(), e);
            JOptionPane.showMessageDialog(null, "Error, validating if input time is valid in time range!");

            return false;
        }
    }

    //#################################################################################
    // Delete BTN Methods
    //#################################################################################
    private void delete_Btn_Action()
    {
        if (! are_You_Sure("Delete")) { return; }

        delete_Meal_Action(); // Delete MealManager Actions
    }

    public void delete_Meal_Action()
    {
        if (! delete_Meal_DB()) { return; } // Failed Update = Exit

        hide_MealManager(); // Delete MealManager

        shared_Data_Registry.delete_MealManager(this); // Update Registry Data

        // Delete in External Charts
        meal_plan_screen.update_External_Charts(false, "delete", this, get_Current_Meal_Time(), get_Current_Meal_Time());

        update_Externals(); // Macros Left & MPS Indicators

        JOptionPane.showMessageDialog(null, "Table Successfully Deleted!"); // Show MSG
    }

    private boolean delete_Meal_DB()
    {
        //##########################################
        // Delete MealManager Queries
        //##########################################
        String query = "DELETE FROM draft_meals_in_plan WHERE draft_meal_in_plan_id = ?";
        Object[] params = new Object[]{ draft_meal_ID };
        String error_msg = "Table Un-Successfully Deleted!";

        Upload_Statement_Full sql_statement = new Upload_Statement_Full(query, params, error_msg, true);

        //##########################################
        // Execute Update
        //##########################################
        return db.upload_Data(sql_statement); // IF it fails upload has its own error prompt msg
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
        close_MealManager_Related_Screens();
    }

    //################################################
    // Delete Ingredients_Table Methods
    //################################################
    public boolean are_There_Any_Active_Sub_Meals()
    {
        return
                ingredient_tables_AL  // returns true if there is a meal that hasn't been deleted
                        .stream()
                        .anyMatch(t -> ! t.has_Sub_Meal_Been_Deleted()); // noneMatch returns true if predicate isn't met
    }

    //#################################################################################
    // Refresh Methods
    //#################################################################################
    private void refresh_Btn_Action()
    {
        //###########################################
        // Edge Cases : Exit
        //###########################################
        if (! is_Meal_Saved()) // IF meal isn't saved
        {
            JOptionPane.showMessageDialog(null, "This Meal Hasn't Been Saved To Refresh!");
            return;
        }

        if (! has_MealManager_Data_Changed())
        {
            JOptionPane.showMessageDialog(null, "No Data in this Meal Has Changed To Refresh!");
            return;
        }

        if (! refresh_IS_Old_Meal_Time_Available()) { return; }

        //###########################################
        // Reset DB Data
        //###########################################
        if (! refresh_DB_Data())
        {
            JOptionPane.showMessageDialog(null, "\n\nUnable to Transfer Meal Data to DB!!");
            return;
        }

        //###########################################
        // Update App
        //###########################################
        refresh_Action(); // Refresh Interview Variables

        update_Everything(); // Update Macros_Left, Total_Meal, Shared_Data, Charts

        meal_plan_screen.add_And_Replace_MealManger_POS_GUI(this, true, true);   // Reposition this Meal in GUI

        //###########################################
        // Success MSG
        //###########################################
        JOptionPane.showMessageDialog(getFrame(), String.format("\n\n[%s] %s has been successfully refreshed! ", current_meal_time, current_meal_name));
    }

    private boolean refresh_IS_Old_Meal_Time_Available()
    {
        //###########################################
        // Check IF OLD Meal Time & Name Available
        //###########################################
        String error_msg = "Error, Unable to Refresh Meal!";

        String query = // Check IF OLD Table Name & Meal Time Are Available To Assign Back To This Meal
                """
                        WITH M AS (
                            -- Has to be restricted on plan_id then count as
                        
                            SELECT
                        
                                ROW_NUMBER() OVER (ORDER BY meal_time ASC) AS pos,
                                draft_meal_in_plan_id,
                                plan_id,
                                meal_name,
                                meal_time
                        
                            FROM draft_meals_in_plan
                            WHERE plan_id = ?
                        )
                        
                        SELECT
                        
                            M.pos
                        
                        FROM M
                        WHERE
                        
                            (meal_name = ? OR meal_time = ?)
                        
                            AND draft_meal_in_plan_id != ?;""";

        Object[] params = new Object[]{ get_Plan_ID(), saved_meal_name, saved_meal_time, draft_meal_ID };

        ArrayList<ArrayList<Object>> results;

        //#############################
        // Execute
        //#############################
        try
        {
            results = db.get_2D_Query_AL_Object(new Fetch_Statement_Full(query, params, error_msg), true);
        }
        catch (Exception e)
        {
            return false;
        }

        //###########################################
        // IF TRUE Notify Meal Name / Time Taken
        //###########################################
        /*
            There can be 2 possible matches from 2 possible different meals being
            if a meal has taken the Meal_Time or, Meal_Name
        */

        if (! results.isEmpty())
        {
            ArrayList<Integer> positions = new ArrayList<>();
            results.forEach(e -> positions.add((int) e.getFirst()));

            String msg = String.format("""
                            There is a meal / meals  in this plan that already have
                            this Meals old saved info (Meal Time / Name) being :
                            
                            'Meal Name' of : '%s'
                            
                            'Meal Time' of : '%s'
                            
                            at positions : %s  which is stopping this meal from refreshing !
                            
                            Change these values first at positions : %s
                            in this plan to be able to refresh this meal!
                            
                            Or, refresh the whole plan if the other meals won't be affected !""",

                    saved_meal_name, saved_meal_time, positions, positions);

            JOptionPane.showMessageDialog(getFrame(), msg);
            return false;
        }

        //###########################################
        // Output
        //###########################################
        return true;
    }

    private boolean refresh_DB_Data()
    {
        System.out.printf("\n\n%s refresh_DB_Data()  %s %s %s", lineSeparator, "refresh", null, null);

        //#####################################################
        //
        //#####################################################
        String errorMSG = "Error, Unable to Transfer Plan Data!";
        Batch_Upload_Statements upload_statements = new Batch_Upload_Statements(errorMSG);

        //#####################################################
        // Add Sub-Meal Refresh Updates
        //#####################################################
        ingredient_tables_AL
                .stream()
                .filter(IngredientsTable :: has_Sub_Meal_Data_Changed)
                .forEach(e -> {
                    e.add_Refresh_Statements(upload_statements); // Add Sub-Meals Update Statements
                });

        //#####################################################
        // Revert Meal Time / Name IF Changed
        //#####################################################
        if (has_Meal_Time_Been_Changed)
        {
            String upload_meal_name = """
                    UPDATE draft_meals_in_plan
                    SET meal_name = ?
                    WHERE draft_meal_in_plan_id = ?""";

            Object[] params = new Object[]{ saved_meal_name, draft_meal_ID };

            upload_statements.add_Uploads(new Upload_Statement(upload_meal_name, params, true));
        }
        if (has_Meal_Name_Been_Changed)
        {
            String upload_meal_name = """
                    UPDATE draft_meals_in_plan
                    SET meal_time = ?
                    WHERE draft_meal_in_plan_id = ?""";

            Object[] params = new Object[]{ saved_meal_time, draft_meal_ID };
            upload_statements.add_Uploads(new Upload_Statement(upload_meal_name, params, true));
        }

        //####################################################
        // Return Update /Output
        //####################################################
        return db.upload_Data_Batch(upload_statements);
    }

    private void refresh_Action()
    {
        //##########################################
        // Reset GUI  & Variables
        //##########################################
        // Reset Time & MealName Variables
        set_Time_Variables(false, saved_meal_time, saved_meal_time);
        set_Meal_Name_Variables(false, saved_meal_name, saved_meal_name);

        set_Has_Meal_Data_Changed(false);

        collapsibleJpObj.set_Icon_Btn_Text(saved_meal_name); // Reset Meal Name in GUI to Old Txt

        //##########################################
        // Refresh Ingredient Tables
        //##########################################
        Iterator<IngredientsTable> it = ingredient_tables_AL.iterator();
        while (it.hasNext())
        {
            IngredientsTable ingredientsTable = it.next();

            if (ingredientsTable.is_Sub_Meal_Saved()) // If ingredients Table is Saved Then refresh
            {
                ingredientsTable.refresh_Action();
                continue;
            }

            ingredientsTable.completely_Delete();

            it.remove(); // remove from list
        }

        sort_And_Re_Draw_Sub_Meals(); // Sort & Redraw GUI
    }

    //###################################################################################
    // Save BTN Methods
    //###################################################################################
    private void save_Btn_Action()
    {
        // ########################################
        //
        // ########################################
        Iterator<IngredientsTable> it = ingredient_tables_AL.iterator();
        while (it.hasNext())
        {
            IngredientsTable table = it.next();

            if (table.has_Sub_Meal_Been_Deleted())   // If objected is deleted, completely delete it then skip to next JTable
            {
                table.completely_Delete();
                it.remove();
                continue;
            }

            table.save_Data_Action();
        }

        save_Data_Action();

        // ########################################
        // Successful Message
        // ########################################
        JOptionPane.showMessageDialog(getFrame(), "\n\nAll Sub-Meals Within Meal Have Successfully Saved!");
    }

    public void save_Data_Action()
    {
        // #####################################
        // Set Variables
        // #####################################
        set_Time_Variables(false, current_meal_time, current_meal_time);
        set_Meal_Name_Variables(false, current_meal_name, current_meal_name);

        set_Is_Meal_Saved(true);
        set_Has_Meal_Data_Changed(false);
    }

    //#################################################################################
    // Pie Charts
    //#################################################################################
    private void pie_Chart_Action()
    {
        // If pieChart is already created bring up to the surface and make it visible
        if (is_Pie_Chart_Open())
        {
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
    private void close_Pie_Chart_Screen()
    {
        if (! is_Pie_Chart_Open()) { return; }

        pie_chart_screen.window_Closed_Event();
    }

    public boolean is_Pie_Chart_Open()
    {
        return pie_chart_screen != null;
    }

    //##################################################################################################################
    // Update Methods
    //##################################################################################################################
    public void update_Everything()
    {
        update_MealManager_DATA(); // Update TotalMeal Data & Shared Data

        update_Charts(); // Update Charts Internal & External

        update_Externals();
    }

    private void update_MealManager_DATA()
    {
        try
        {
            ArrayList<Object> total_meal_data = totalMealTable.update_Table_And_Get_Data(); // Update TotalMealView (Has to be first)

            shared_Data_Registry.add_OR_Replace_MealManager_Macros_DATA(this, total_meal_data);  // Update Registry Data (Second)
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
            System.err.printf("\n\nMPS : update_Pie_Chart_DATA() \nPieChart not Open %s", get_Draft_Meal_ID());
        }

        pie_Chart_Update_Title(); // Update Internal PieChart Title

        // Update External Charts
        meal_plan_screen.update_External_Charts(false, "update", this, get_Current_Meal_Time(), get_Current_Meal_Time());
    }

    public void update_Externals()
    {
        update_MacrosLeft_Table(); // Update Macros Left

        meal_plan_screen.update_Macro_Indicators(); // Update Macro Indicators
    }

    //############################
    // Update Tables
    //############################
    public void update_Total_Meal()
    {
        totalMealTable.update_Table();
    }

    private void update_MacrosLeft_Table()
    {
        macrosLeft_JTable.update_Table();
    }

    //##################################################################################################################
    // Others
    //##################################################################################################################
    public void expand_JPanel()
    {
        get_Collapsible_JP_Obj().expand_JPanel();
    }

    public void collapse_MealManager()
    {
        get_Collapsible_JP_Obj().collapse_JPanel();
    }

    public void close_MealManager_Related_Screens()
    {
        close_Pie_Chart_Screen();
    }

    //##################################################################################################################
    // Mutator Methods
    //##################################################################################################################
    public void set_Source_Meal_ID(int source_meal_id)
    {
        this.source_meal_id = source_meal_id;
    }

    private void set_Time_Variables(boolean hasMealTimeBeenChanged, LocalTime savedMealTime, LocalTime currentMealTime)
    {
        this.has_Meal_Time_Been_Changed = hasMealTimeBeenChanged;
        this.saved_meal_time = savedMealTime;
        this.current_meal_time = currentMealTime;
    }

    private void set_Meal_Name_Variables(boolean hasMealNameBeenChanged, String savedMealName, String currentMealName)
    {
        this.has_Meal_Name_Been_Changed = hasMealNameBeenChanged;
        this.saved_meal_name = savedMealName;
        this.current_meal_name = currentMealName;
    }

    //#################################
    // Boolean
    //#################################
    public void set_MealManager_In_DB(boolean state)
    {
        this.is_MealManager_In_DB = state;
    }

    private void set_Is_Meal_Saved(boolean state)
    {
        is_Meal_Saved = state;
    }

    public void set_Has_Meal_Data_Changed(boolean state)
    {
        has_MealManager_Data_Changed = state;

        // IF data changed is true update MPS to update its state
        if (state) { meal_plan_screen.set_has_Data_Changed(true); }
    }

    private void set_Visibility(boolean condition)
    {
        collapsibleJpObj.setVisible(condition);
        spaceDividerForMealManager.setVisible(condition);
    }

    //##################################################################################################################
    // Accessor Methods
    //##################################################################################################################
    public ArrayList<IngredientsTable> get_Ingredient_Tables_AL()
    {
        return ingredient_tables_AL;
    }

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

    public boolean has_MealManager_Data_Changed()
    {
        return has_MealManager_Data_Changed || // IF Meal Data has specifically changed return it

                ingredient_tables_AL          // Else compute if Sub-Meals data changed
                        .stream()
                        .anyMatch(IngredientsTable :: has_Sub_Meal_Data_Changed); // If any Sub-Meal data has changed return true
    }

    public boolean any_Session_Created_Sub_Meals()
    {
        return ingredient_tables_AL
                .stream()
                .anyMatch(e -> ! e.is_Sub_Meal_In_DB());
    }

    // ############################################
    // Meal Times
    // ############################################
    public LocalTime get_Current_Meal_Time()
    {
        return current_meal_time;
    }

    public Pair<LocalTime, LocalTime> get_Available_Sub_Meal_Time_Ranges() throws Exception
    {
        return meal_plan_screen.get_Available_Sub_Meal_Time_Ranges_For_Meal(this);
    }

    // ############################################
    // Strings
    // ############################################
    public String get_Current_Meal_Name()
    {
        return current_meal_name;
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
    public int get_Draft_Meal_ID()
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
