package App_Code.Objects.Table_Objects.Tables.Children.Ingredients_Table;

import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Ingredient_Name_ID_OBJ;
import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Ingredient_Type_ID_OBJ;
import App_Code.Objects.Database_Objects.MyJDBC.MyJDBC_Sqlite;
import App_Code.Objects.Database_Objects.Fetched_Results;
import App_Code.Objects.Database_Objects.MyJDBC.Batch_Objects.Batch_Upload_And_Fetch_Statements;
import App_Code.Objects.Database_Objects.MyJDBC.Batch_Objects.Batch_Upload_Statements;
import App_Code.Objects.Database_Objects.MyJDBC.Statements.Fetch_Statement;
import App_Code.Objects.Database_Objects.MyJDBC.Statements.Fetch_Statement_Full;
import App_Code.Objects.Database_Objects.MyJDBC.Statements.Upload_Statement;
import App_Code.Objects.Database_Objects.MyJDBC.Statements.Upload_Statement_Full;
import App_Code.Objects.Database_Objects.Shared_Data_Registry;
import App_Code.Objects.Table_Objects.Tables.Children.Ingredients_Table.Buttons.Button_Column;
import App_Code.Objects.Table_Objects.Tables.Children.Ingredients_Table.JCombo_Boxes.Child.Ingredient_Name_JComboBox_Column;
import App_Code.Objects.Table_Objects.Tables.Children.Ingredients_Table.JCombo_Boxes.Child.Ingredient_Type_JComboBox_Column;
import App_Code.Objects.Table_Objects.Tables.Children.View_Data_Tables.Children.MacrosLeft_Table;
import App_Code.Objects.Table_Objects.Tables.Parent.JDBC_JTable;
import App_Code.Objects.Table_Objects.MealManager;
import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Gui_Objects.IconPanel;
import org.javatuples.Pair;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.*;

public class IngredientsTable extends JDBC_JTable
{
    //#################################################################################################################
    // Variables
    //#################################################################################################################

    //################################################
    // Objects
    //################################################
    private final MealManager meal_manager;
    private final MacrosLeft_Table macrosLeft_table;
    private final Ingredient_Name_ID_OBJ na_ingredient_id_obj;

    //######################
    // Screen Objects
    //######################
    private final JPanel space_divider;
    private final Frame frame;

    //################################################
    // Booleans
    //################################################
    private boolean
            sub_meal_in_db,
            sub_meal_saved,
            table_deleted = false,
            sub_meal_meta_data_changed = false,
            sub_meal_data_changed = false;

    private boolean
            has_Sub_Meal_Name_Been_Changed = false,
            has_Sub_Meal_Time_Been_Changed = false;

    //################################################
    // Integers
    //################################################
    private final UUID internalId = UUID.randomUUID();

    private Integer source_sub_meal_id;

    private final int
            draft_meal_id,
            draft_sub_meal_id;

    private int
            model_ingredient_index_col,
            model_quantity_col,
            model_ingredient_type_col,
            model_ingredient_name_col,
            model_delete_btn_col;

    private final int na_ingredient_id;
    private final int na_pdid;

    //################################################
    // String
    //################################################
    private String
            saved_sub_meal_name = null,
            current_sub_meal_name = null;

    //################################################
    // LocalTime
    //################################################
    private LocalTime
            saved_sub_meal_time = null,
            current_sub_meal_time = null;

    private DateTimeFormatter time_Formatter = DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);

    //################################################
    // Collections
    //################################################
    private HashMap<Ingredients_Table_Columns, Integer> ingredients_table_cols_positions;

    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    // Ingredients Table
    public IngredientsTable
    (
            MyJDBC_Sqlite db,
            MealManager meal_manager,
            Shared_Data_Registry shared_data_registry,
            MacrosLeft_Table macros_left_table,
            int draft_meal_id,
            Integer source_sub_meal_id,
            int draft_sub_meal_id,
            ArrayList<ArrayList<Object>> data,
            boolean sub_meal_in_db,
            JPanel space_divider
    )
    {
        super(
                db,
                shared_data_registry,
                meal_manager.get_Collapsible_Center_JPanel(),
                true,
                "draft_ingredients_index",
                "Ingredients Table",
                "draft_ingredients_in_sections_of_meal",
                "draft_gui_ingredients_in_sections_of_meal_calculation",
                data,
                shared_data_registry.get_Ingredients_Table_Column_Names(),
                shared_data_registry.get_Ingredients_Table_Un_Editable_Cols(),
                shared_data_registry.get_Ingredients_Table_Avoid_Centering_Cols(),
                shared_data_registry.get_Ingredients_Table_Cols_To_Hide()
        );

        //##############################################################
        // Other Variables
        //##############################################################
        this.meal_manager = meal_manager;
        this.macrosLeft_table = macros_left_table;

        this.draft_meal_id = draft_meal_id;
        this.source_sub_meal_id = source_sub_meal_id;
        this.draft_sub_meal_id = draft_sub_meal_id;

        this.space_divider = space_divider;

        this.sub_meal_in_db = sub_meal_in_db;
        sub_meal_saved = sub_meal_in_db;

        //#################################
        // Values From MealManager
        //#################################
        parent_Container = meal_manager.get_Collapsible_Center_JPanel();
        frame = meal_manager.getFrame();

        //#################################
        // Values From Shared_Data_Registry
        //#################################
        na_ingredient_id_obj = shared_data_registry.get_Na_Ingredient_ID_OBJ();
        na_ingredient_id = shared_data_registry.get_Na_Ingredient_ID();
        na_pdid = shared_data_registry.get_NA_PDID();
    }

    public IngredientsTable
            (
                    MyJDBC_Sqlite db,
                    MealManager meal_manager,
                    Shared_Data_Registry shared_data_registry,
                    MacrosLeft_Table macros_left_table,
                    int draft_meal_id,
                    Integer source_sub_meal_id,
                    int draft_sub_meal_id,
                    String sub_meal_name,
                    LocalTime sub_meal_time,
                    ArrayList<ArrayList<Object>> data,
                    boolean sub_meal_in_db,
                    JPanel space_divider
            )
    {
        super(
                db,
                shared_data_registry,
                meal_manager.get_Collapsible_Center_JPanel(),
                true,
                "draft_ingredients_index",
                "Ingredients Table",
                "draft_ingredients_in_sections_of_meal",
                "draft_gui_ingredients_in_sections_of_meal_calculation",
                data,
                shared_data_registry.get_Ingredients_Table_Column_Names(),
                shared_data_registry.get_Ingredients_Table_Un_Editable_Cols(),
                shared_data_registry.get_Ingredients_Table_Avoid_Centering_Cols(),
                shared_data_registry.get_Ingredients_Table_Cols_To_Hide()
        );

        //##############################################################
        // Other Variables
        //##############################################################
        this.meal_manager = meal_manager;
        this.macrosLeft_table = macros_left_table;

        this.draft_meal_id = draft_meal_id;
        this.source_sub_meal_id = source_sub_meal_id;
        this.draft_sub_meal_id = draft_sub_meal_id;

        this.space_divider = space_divider;

        this.sub_meal_in_db = sub_meal_in_db;
        sub_meal_saved = sub_meal_in_db;

        //#################################
        // Values From MealManager
        //#################################
        parent_Container = meal_manager.get_Collapsible_Center_JPanel();
        frame = meal_manager.getFrame();

        //#################################
        // Values From Shared_Data_Registry
        //#################################
        na_ingredient_id_obj = shared_data_registry.get_Na_Ingredient_ID_OBJ();
        na_ingredient_id = shared_data_registry.get_Na_Ingredient_ID();
        na_pdid = shared_data_registry.get_NA_PDID();

        //#################################
        //
        //#################################
        set_Sub_Meal_Time_Variable(false, sub_meal_time, sub_meal_time);
        set_Sub_Meal_Name_Variable(false, sub_meal_name, sub_meal_name);
    }

    //##################################################################################################################
    // Data Formatting Methods
    //##################################################################################################################
    @Override
    protected void child_Variable_Configurations()
    {
        ingredients_table_cols_positions = shared_data_registry.get_Ingredients_Table_Cols_Positions();

        // Table : draft_ingredients_in_sections_of_meal_calculation
        set_Model_Ingredient_Index_Col(ingredients_table_cols_positions.get(Ingredients_Table_Columns.DRAFT_INGREDIENTS_INDEX));
        set_Model_Quantity_Col(ingredients_table_cols_positions.get(Ingredients_Table_Columns.QUANTITY));
        set_Model_Ingredient_Type_Col(ingredients_table_cols_positions.get(Ingredients_Table_Columns.INGREDIENT_TYPE_NAME));
        set_Model_Ingredient_Name_Col(ingredients_table_cols_positions.get(Ingredients_Table_Columns.INGREDIENT_NAME));
        set_Model_Delete_BTN_Col(ingredients_table_cols_positions.get(Ingredients_Table_Columns.DELETE_BTN));
    }

    @Override
    protected boolean format_Table_Data(ArrayList<ArrayList<Object>> table_data)
    {
        try
        {
            for (ArrayList<Object> data : table_data)
            {
                format_Table_Row_Data(data);
            }
            return true;
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s Error -> formatting table data \n%s", get_Class_And_Method_Name(), e);
            JOptionPane.showMessageDialog(null, String.format("Failed Formatting %s DATA!", table_name));
            return false;
        }
    }

    @Override
    protected void format_Table_Row_Data(ArrayList<Object> table_data) throws Exception
    {
        //##########################
        // Ingredients Name
        //##########################
        int ingredient_Name_Pos = ingredients_table_cols_positions.get(Ingredients_Table_Columns.INGREDIENT_NAME);
        int ingredient_Name_ID = (Integer) table_data.get(ingredient_Name_Pos);

        Ingredient_Name_ID_OBJ ingredient_Name_ID_OBJ = shared_data_registry.get_Ingredient_Name_ID_OBJ_By_ID(ingredient_Name_ID);

        if (ingredient_Name_ID_OBJ == null)
        {
            throw new Exception(String.format("%s Error, Ingredient_Name_ID_OBJ returned null %s", get_Class_And_Method_Name(), ingredient_Name_ID));
        }

        table_data.set(ingredient_Name_Pos, ingredient_Name_ID_OBJ);

        //##########################
        // Ingredients Type
        //##########################
        int ingredient_Type_Pos = ingredients_table_cols_positions.get(Ingredients_Table_Columns.INGREDIENT_TYPE_NAME);

        int ingredient_Type_ID = (Integer) table_data.get(ingredient_Type_Pos);

        Ingredient_Type_ID_OBJ ingredient_Type_ID_OBJ = shared_data_registry.get_Type_ID_Obj_By_ID(ingredient_Type_ID);

        if (ingredient_Type_ID_OBJ == null)
        {
            throw new Exception(String.format("%s Error, Ingredient_Type_ID_OBJ returned null %s", get_Class_And_Method_Name(), ingredient_Type_ID));
        }

        table_data.set(ingredient_Type_Pos, ingredient_Type_ID_OBJ);
    }

    //##################################################################################################################
    // Table Setup / Special Column Methods
    //##################################################################################################################
    @Override
    protected void extra_Table_Setup()
    {
        icon_Setup();
    }

    protected void icon_Setup()
    {
        //###################################################################################
        // Table Icon Setup
        //###################################################################################
        int icon_Size = 20;

        IconPanel icon_Panel = new IconPanel(3, 10, "East");
        JPanel iconPanel_Insert = icon_Panel.getIconJpanel();

        add_To_Container(this, icon_Panel.getIconAreaPanel(), 0, 0, 1, 1, 0.25, 0.25, "horizontal", "east");

        //##########################
        //Add BTN
        //##########################
        IconButton add_Icon_Btn = new IconButton("/images/add/add.png", icon_Size, icon_Size, icon_Size, icon_Size, "centre", "right");
        // add_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));

        JButton add_Btn = add_Icon_Btn.returnJButton();
        add_Icon_Btn.setToolTipText("Add Ingredients"); //Hover message over icon
        add_Icon_Btn.makeBTntransparent();

        add_Btn.addActionListener(ae -> {
            add_btn_Action();
        });

        iconPanel_Insert.add(add_Icon_Btn);

        //##########################
        // Edit Name BTN
        //##########################
        IconButton edit_Icon_Btn = new IconButton("/images/edit/edit.png", icon_Size, icon_Size, icon_Size, icon_Size, "centre", "right");
        // add_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));

        JButton edit_Btn = edit_Icon_Btn.returnJButton();
        edit_Btn.setToolTipText("Edit Meal Name"); //Hover message over icon
        edit_Icon_Btn.makeBTntransparent();

        edit_Btn.addActionListener(ae -> {
            edit_Sub_Meal_Name_BTN();
        });

        iconPanel_Insert.add(edit_Icon_Btn);

        //##########################
        // Edit Time BTN
        //##########################
        IconButton editTime_Icon_Btn = new IconButton("/images/edit_Time/edit_Time.png", icon_Size, icon_Size, icon_Size, icon_Size, "centre", "right");
        // add_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));

        JButton editTime_Btn = editTime_Icon_Btn.returnJButton();
        editTime_Btn.setToolTipText("Edit Meal Time"); //Hover message over icon
        editTime_Icon_Btn.makeBTntransparent();

        editTime_Btn.addActionListener(ae -> {
            edit_Sub_Meal_Time_BTN();
        });

        iconPanel_Insert.add(editTime_Icon_Btn);

        //##########################
        // Refresh Icon
        //##########################

        IconButton refresh_Icon_Btn = new IconButton("/images/refresh/+refresh.png", icon_Size, icon_Size, icon_Size, icon_Size,
                "centre", "right"); // btn text is useless here , refactor
        //refresh_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));


        JButton refresh_Btn = refresh_Icon_Btn.returnJButton();
        refresh_Btn.setToolTipText("Restore Sub-Meal"); //Hover message over icon
        refresh_Icon_Btn.makeBTntransparent();

        refresh_Btn.addActionListener(ae -> {
            refresh_Btn_Action();
        });

        iconPanel_Insert.add(refresh_Icon_Btn);

        //##########################
        // Update Icon
        //##########################

        IconButton save_Icon_Btn = new IconButton("/images/save/save.png", icon_Size, icon_Size, icon_Size, icon_Size,
                "centre", "right"); // btn text is useless here , refactor
        //save_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));
        save_Icon_Btn.makeBTntransparent();

        JButton save_btn = save_Icon_Btn.returnJButton();
        save_btn.setToolTipText("Save Sub-Meal"); //Hover message over icon

        save_btn.addActionListener(ae -> {
            save_Btn_Action();
        });

        iconPanel_Insert.add(save_btn);

        //##########################
        // Delete Icon
        //##########################

        IconButton delete_Icon_Btn = new IconButton("/images/delete/+delete.png", icon_Size, icon_Size, icon_Size + 10, icon_Size,
                "centre", "right"); // btn text is useless here , refactor
        //delete_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));
        delete_Icon_Btn.makeBTntransparent();

        JButton delete_btn = delete_Icon_Btn.returnJButton();
        delete_btn.setToolTipText("Delete Sub-Meal"); //Hover message over icon

        delete_btn.addActionListener(ae -> {
            delete_Table_BTN_Action();
        });

        iconPanel_Insert.add(delete_btn);
    }

    @Override
    protected void child_Table_Configurations()
    {
        setup_Special_Columns();  // Setting Up JComboBox  / Delete BTN Column Fields on Table

        set_Table_Header_Font(new Font("Dialog", Font.BOLD, 12)); // Table Customization / Ingredients_In_Meal_Calculation Customisation
    }

    private void setup_Special_Columns()
    {
        //################################
        // Ingredients Type JC Setup
        //################################
        int ingredient_Type_Column = get_Ingredient_Type_Col(false);

        new Ingredient_Type_JComboBox_Column(
                get_JTable(),
                ingredient_Type_Column,
                "Select Ingredient Type to Change Ingredient Names!",
                shared_data_registry.get_Mapped_Ingredient_Types()
        );

        //################################
        // Ingredients Name JC Setup
        //################################
        new Ingredient_Name_JComboBox_Column(
                get_JTable(),
                shared_data_registry,
                get_Ingredient_Name_Col(false),
                ingredient_Type_Column,
                "Select Ingredient Name!"
        );

        //################################
        // Delete BTn Column
        //################################
        setup_Delete_Btn_Column(get_Delete_BTN_Col(false));
    }

    //###################################
    // Delete Row Btn
    //###################################
    private void setup_Delete_Btn_Column(int delete_Btn_Column)
    {
        new Button_Column(this, delete_Btn_Column, get_Ingredient_Index_Col(true));
    }

    public void delete_Row_Action(int ingredient_Index, int model_Row)
    {
        //#################################################
        // Can't have an empty Table
        //##################################################
        if (get_Rows_In_Table() == 1)
        {
            String question = """
                    There is only 1 ingredient in this Sub-Meal Table!
                    
                    If you delete this ingredient, this table will also be deleted.
                    
                    Would you still like to proceed?""";

            int reply = JOptionPane.showConfirmDialog(null, question, "Delete Ingredients", JOptionPane.YES_NO_OPTION); //HELLO Edit

            if (reply == JOptionPane.YES_OPTION) // IF user requests to delete the table
            {
                delete_Table_Action();
            }

            return; // Exit
        }

        //#################################################
        // Delete Ingredient From Temp Meal
        //#################################################
        String query = String.format("DELETE FROM %s WHERE %s = ? ;", db_write_table_name, db_row_id_column_name);
        String error_msg = String.format("Error, unable to delete from '%s' !", table_name);
        Object[] params = new Object[]{ ingredient_Index };

        Upload_Statement_Full sql_statement = new Upload_Statement_Full(query, params, error_msg, true);

        if (! db.upload_Data(sql_statement)) { return; }

        //#################################################
        // Remove From Table
        //##################################################
        delete_Row(model_Row);
        resize_Object();
        set_Sub_Meal_Data_Changed(true);

        //#################################################
        // Update Table Data
        //##################################################
        update_All_Tables_Data();
    }

    //##################################################################################################################
    // Data Changing In Cells Actions
    //##################################################################################################################
    @Override
    protected boolean table_Data_Changed_Action(int row_In_Model, int column_In_Model, Object new_Value) throws Exception
    {
        //##################################################################
        // Variables
        //##################################################################
        int ingredient_Index = (Integer) get_Value_On_Model_Data(row_In_Model, get_Ingredient_Index_Col(true));

        //##################################################################
        // Identify Trigger Column
        //##################################################################

        // Ingredients Type Column
        if (column_In_Model == get_Ingredient_Type_Col(true)) { return true; } // Nothing to process inside db lvl

        //##########################################
        // Ingredients Name Column
        //##########################################
        else if (column_In_Model == get_Ingredient_Name_Col(true))
        {
            System.out.printf("\n\n@tableDataChange_Action() Ingredient Name Changed - %s \nIndex: %s", table_name, ingredient_Index);

            Ingredient_Name_ID_OBJ selected_Ingredient_Name_OBJ = (Ingredient_Name_ID_OBJ) new_Value;
            int selected_Ingredient_Name_ID = selected_Ingredient_Name_OBJ.get_ID();

            //##########################
            // Check IF N/A is selected
            //##########################
            if (selected_Ingredient_Name_OBJ.equals(na_ingredient_id_obj))
            {
                // Can't have multiple 'None Of The Above' ingredients in a meal
                if (found_NA_Ingredient_In_Table("change a current Ingredient in this meal to 'None Of The Above'"))
                {
                    return false;
                }
            }

            //##########################
            // Update Table
            //##########################
            return update_Table_By_Ingredient_Name(row_In_Model, selected_Ingredient_Name_ID, ingredient_Index);
        }

        //################################
        // Ingredients Quantity Column
        // ###############################
        else if (column_In_Model == get_Quantity_Col(true))
        {
            System.out.printf("\ntableDataChange_Action() Quantity Being Changed - %s \nIndex: %s", table_name, ingredient_Index);
            return update_Table_Values_By_Quantity(row_In_Model, ingredient_Index, (BigDecimal) new_Value);
        }
        else
        {
            throw new Exception(String.format("\n\n%s Un-handled Column Event Trigger - %s", get_Method_Name(2), table_name));
        }
    }

    @Override
    protected boolean has_Cell_Data_Changed(Class<?> type, Object old_Value, Object new_Value, int col) throws Exception
    {
        //########################################
        //  Comparison For Other Types
        //########################################

        /*
         *  The Compared Types are only needed for cells that are editable by the user, if the cell isn't editable
         *  has_Cell_Data_Changed isn't called.
         *
         */

        if (type == Ingredient_Type_ID_OBJ.class) // Ingredient Type
        {
            return ! ((Ingredient_Type_ID_OBJ) old_Value).equals(((Ingredient_Type_ID_OBJ) new_Value));
        }
        else if (type == Ingredient_Name_ID_OBJ.class) // Ingredient Name
        {
            return ! ((Ingredient_Name_ID_OBJ) old_Value).equals(((Ingredient_Name_ID_OBJ) new_Value));
        }
        else if (type == BigDecimal.class) // Quantity Field = Big Decimal
        {
            return ((BigDecimal) old_Value).compareTo(((BigDecimal) new_Value)) != 0;
        }
        else if (type == String.class)
        {
            return ! ((String) old_Value).equals(((String) new_Value));
        }

        //########################################
        //  Edge Cases : Error (Unexpected Type)
        //########################################
        throw new Exception(String.format("\n\n%s Error \nUnexpected Class Type: %s - %s", get_Class_And_Method_Name(), new_Value.getClass(), table_name));
    }

    private boolean update_Table_By_Ingredient_Name(int row_In_Model, int selected_Ingredient_Name_ID, int ingredient_Index)
    {
        //##################################################################
        // Update & Fetch In MYSQL
        //##################################################################

        String error_MSG = String.format("Error, Updating Ingredient Name on '%s'!", table_name);
        Batch_Upload_And_Fetch_Statements batch_statements = new Batch_Upload_And_Fetch_Statements(error_MSG);

        //###########################
        // Update
        //###########################
        String upload_Query = String.format("""
                UPDATE %s
                SET ingredient_id = ?,
                pdid = ?
                WHERE %s = ?;""", db_write_table_name, db_row_id_column_name);

        Object[] params_Upload = new Object[]{selected_Ingredient_Name_ID, null,ingredient_Index};

        batch_statements.add_Uploads(new Upload_Statement(upload_Query, params_Upload, true));

        //##############################################
        // Execute
        //##############################################
        return upload_And_Update_Table(row_In_Model, ingredient_Index, batch_statements);
    }

    private boolean update_Table_Values_By_Quantity(int row_In_Model, int ingredient_Index, BigDecimal quantity)
    {
        //##################################################################
        // Update & Fetch In MYSQL
        //##################################################################

        String error_MSG = "Error, Updating Ingredient Table by Quantity!";
        Batch_Upload_And_Fetch_Statements batch_statements = new Batch_Upload_And_Fetch_Statements(error_MSG);

        //###########################
        // Update
        //###########################
        String upload_Query = String.format("""
                UPDATE  %s
                SET quantity = ?
                WHERE %s = ?;""", db_write_table_name, db_row_id_column_name);

        Object[] params = new Object[]{ quantity, ingredient_Index };

        batch_statements.add_Uploads(new Upload_Statement(upload_Query, params, true));

        //##############################################
        // Execute
        //##############################################
        return upload_And_Update_Table(row_In_Model, ingredient_Index, batch_statements);
    }

    private boolean upload_And_Update_Table(int row_In_Model, int ingredient_Index, Batch_Upload_And_Fetch_Statements batch_statements)
    {
        //###################################
        // Fetch
        //###################################
        String fetch_Query = String.format("SELECT * FROM %s WHERE %s = ?;", db_read_view_name, db_row_id_column_name);
        batch_statements.add_Fetches(new Fetch_Statement(fetch_Query, new Object[]{ ingredient_Index }));

        //##############################################
        // Execute
        //##############################################
        Fetched_Results fetched_Results = db.upload_And_Get_Batch(batch_statements);

        if (fetched_Results == null) { return false; } // Upload & Fetch Failed

        //##################################################################
        // Update Table
        //##################################################################
        try
        {
            // Get First Row Results & Format Data to include Storable_ID_Objects
            ArrayList<Object> data = fetched_Results.get_Result_1D_AL(0);
            format_Table_Row_Data(data); // format data

            super.update_Table_Row(data, row_In_Model); // Update This Table

            set_Sub_Meal_Data_Changed(true);

            update_All_Tables_Data(); // Update ALl Other Tables
            return true;
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s \n\n%s", get_Class_And_Method_Name(), e);
            JOptionPane.showMessageDialog(null, String.format("Unable to Update - '%s'!", table_name));
            return false;
        }
    }

    //##################################################################################################################
    // Button Events
    //##################################################################################################################
    // Add Button
    protected void add_btn_Action()
    {
        //################################################################
        // Check If There Is Already An Empty Row
        //################################################################
        if (found_NA_Ingredient_In_Table("add a new row!")) { return; }

        //################################################################
        // Upload & Fetch Variables
        //################################################################
        String error_MSG = String.format("\n\nError Adding Additional Ingredient to Meal: \n\nMeal Name: '%s' \nMeal Time: %s!",
                meal_manager.get_Current_Meal_Name(), meal_manager.get_Current_Meal_Time());

        Batch_Upload_And_Fetch_Statements batch_statements = new Batch_Upload_And_Fetch_Statements(error_MSG);

        ArrayList<Object> ingredient_DATA;

        //################################################
        // Uploads
        //###############################################

        // 1.) Insert Ingredient to Ingredients Table
        String upload_Q1 = String.format("""
                INSERT INTO %s
                (
                    draft_div_meal_sections_id,
                    ingredient_id,
                    pdid,
                    quantity
                )
                VALUES
                (?, ?, ?, ?);""", db_write_table_name);

        Object[] params_1 = new Object[]{ draft_sub_meal_id, na_ingredient_id, na_pdid, 0 };

        batch_statements.add_Uploads(new Upload_Statement(upload_Q1, params_1, true));

        //#######################################################
        // Fetch Queries
        //#######################################################

        // 1.) Get Ingredient ID
        String fetch_Q1 = String.format("""
                SELECT *
                FROM %s
                WHERE %s = (SELECT last_insert_rowid());""", db_read_view_name, db_row_id_column_name);

        batch_statements.add_Fetches(new Fetch_Statement(fetch_Q1, null));

        //#######################################################
        // Execute Query
        //#######################################################
        Fetched_Results fetched_Results_OBJ = db.upload_And_Get_Batch(batch_statements);

        if (fetched_Results_OBJ == null) { System.err.println("\n\n\nFailed Adding Ingredient"); return; }

        //#######################################################
        // Set Variables from Results
        //#######################################################
        try
        {
            ingredient_DATA = fetched_Results_OBJ.get_Result_1D_AL(0);
            format_Table_Row_Data(ingredient_DATA); // format Ingredients Data

            System.out.printf("\n\nIngredients Results: \n%s%n", ingredient_DATA);
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, String.format("Unable to add Ingredient to %s", table_name));
            System.err.printf("\n\n%s \n%s", get_Class_And_Method_Name(), e);
            return;
        }

        //#######################################################
        //   Updating Ingredients With New Ingredients DATA
        //#######################################################
        add_Row(ingredient_DATA); // Adding Row Data to Table Model
        set_Sub_Meal_Data_Changed(true);

        //#######################################################
        // Update Table Data
        //#######################################################
        meal_manager.update_Total_Meal();
    }

    //###################################################
    // Refresh Button
    //###################################################
    public void refresh_Btn_Action()
    {
        // Exit : Edge Cases
        if (! are_You_Sure("Refresh Data")) { return; } // Ask For Permission

        if (! is_Sub_Meal_Saved()) // If Meal Is not in DB, then refresh does nothing
        {
            JOptionPane.showMessageDialog(null, "\n\nThere's nothing to refresh! \nThis meal was never saved!");
            return;
        }

        // Refresh DB Data
        if (! refresh_DB_Data())
        {
            JOptionPane.showMessageDialog(frame, "\n\nUnable to transfer ingredients data from  original plan to temp plan!!");
            return;
        }

        // Reset Table Info & Data
        refresh_Action();

        // Update Tables
        meal_manager.update_MealManager_DATA();
        macrosLeft_table.update_Table();

        // Success MSG
        JOptionPane.showMessageDialog(frame, "\n\nSub-Meal Successfully Refreshed!!");
    }

    private boolean refresh_DB_Data()
    {
        //###################################################
        // Upload
        //###################################################
        /*

         */

        String error_msg = String.format("Unable to Refresh Sub-Meal !");
        Batch_Upload_Statements batch_upload_statements = new Batch_Upload_Statements(error_msg);

        //###################################################
        // Execute
        //###################################################
        add_Refresh_Statements(batch_upload_statements);
        return db.upload_Data_Batch(batch_upload_statements);
    }

    public void add_Refresh_Statements(Batch_Upload_Statements batch_upload_statements)
    {
        //###################################################
        // Re-Insert Sub-Meal Incase Deleted
        //###################################################
        if (is_Sub_Meal_Deleted() || has_Sub_Meal_Meta_Data_Changed())
        {
            // DELETE OLD Sub-Meal
            String upload_query00 = "DELETE FROM draft_divided_meal_sections WHERE draft_div_meal_sections_id = ?";
            Object[] upload_params_00 = new Object[]{ draft_sub_meal_id };
            batch_upload_statements.add_Uploads(new Upload_Statement(upload_query00, upload_params_00, true));

            // Re-Insert With Old Sub-Meal Values
            Pair<String, Object[]> upload_query_pair_01;

            String upload_query_01;
            Object[] params_01;
            if (is_Sub_Meal_In_DB()) // If Meal is in DB
            {
                upload_query_01 = """
                        INSERT INTO draft_divided_meal_sections
                        (
                            draft_div_meal_sections_id,
                            div_meal_sections_id,
                            draft_meal_in_plan_id,
                            plan_id,
                            sub_meal_name,
                            sub_meal_time
                        )
                        VALUES
                        (?,?,?,?,?,?);""";

                params_01 = new Object[]{
                        draft_sub_meal_id, source_sub_meal_id, draft_meal_id,
                        get_Plan_ID(), saved_sub_meal_name, saved_sub_meal_time
                };

            }
            else // Sub-Meal isn't in DB and doesn't have a source ID
            {
                upload_query_01 = """
                        INSERT INTO draft_divided_meal_sections
                        (
                            draft_div_meal_sections_id,
                            draft_meal_in_plan_id,
                            plan_id,
                            sub_meal_name,
                            sub_meal_time
                        )
                        VALUES
                        (?,?,?,?,?);""";

                params_01 = new Object[]{ draft_sub_meal_id, draft_meal_id, get_Plan_ID(), saved_sub_meal_name, saved_sub_meal_time };
            }

            batch_upload_statements.add_Uploads(new Upload_Statement(upload_query_01, params_01, true));
        }

        //###################################################
        // Delete Sub-Meal Ingredients
        //###################################################
        String upload_query_02 = "DELETE FROM draft_ingredients_in_sections_of_meal WHERE draft_div_meal_sections_id = ?";
        Object[] upload_params_02 = new Object[]{ draft_sub_meal_id };

        batch_upload_statements.add_Uploads(new Upload_Statement(upload_query_02, upload_params_02, true));

        //###################################################
        // Create Insert String
        //###################################################

        // Re-insert Saved Ingredients In Sub-Meal
        String upload_query_03_tmp = """
                INSERT INTO draft_ingredients_in_sections_of_meal
                (
                    draft_ingredients_index,
                    draft_div_meal_sections_id,
                    ingredient_id,
                    quantity
                )
                VALUES
                
                """;

        //#############################
        // Create Values String
        //#############################
        String values = "(?, ?, ?, ?),".repeat(saved_Data.size()); // repeat values section for as many rows as there are
        values = values.substring(0, values.length() - 1) + ";";   // Close off upload string with ';' instead of ','

        String upload_query_03 = upload_query_03_tmp + values;

        //#############################
        // Create Params
        //#############################
        int pos = - 1;
        Object[] params_03 = new Object[4 * saved_Data.size()];

        for (ArrayList<Object> row : saved_Data)
        {
            params_03[pos += 1] = row.get(model_ingredient_index_col);                                    // Get Ingredient Index
            params_03[pos += 1] = draft_sub_meal_id;                                                            // Get Sub-Meal ID
            params_03[pos += 1] = ((Ingredient_Name_ID_OBJ) row.get(model_ingredient_name_col)).get_ID(); // Get Ingredient ID
            params_03[pos += 1] = row.get(model_quantity_col);                                            // Get Quantity
        }

        //#############################
        // Create Upload Statements
        //#############################
        batch_upload_statements.add_Uploads(new Upload_Statement(upload_query_03, params_03, true));

        //#############################
        // Create Upload Statements
        //#############################
    }

    public void refresh_Action()
    {
        refresh_Data(); // Reset Table Model data

        set_Sub_Meal_Data_Changed(false);
        set_Sub_Meal_Meta_Data_Changed(false);

        un_Hide_Ingredients_Table();

        set_Sub_Meal_Time_Variable(false, saved_sub_meal_time, saved_sub_meal_time);
        set_Sub_Meal_Name_Variable(false, saved_sub_meal_name, saved_sub_meal_name);
    }

    //###################################################
    // Save Button
    //###################################################
    private void save_Btn_Action()
    {
        if (! are_You_Sure("Save Data")) { return; }

        save_Data_Action();

        JOptionPane.showMessageDialog(frame, "Table Successfully Updated!");
    }

    public void save_Data_Action()
    {
        save_Data(); // Update Table Model

        set_Sub_Meal_Saved(true);

        set_Sub_Meal_Data_Changed(false);
        set_Sub_Meal_Meta_Data_Changed(false);

        set_Sub_Meal_Time_Variable(false, current_sub_meal_time, current_sub_meal_time);
        set_Sub_Meal_Name_Variable(false, current_sub_meal_name, current_sub_meal_name);
    }

    //####################################################
    // Delete Table Methods
    //####################################################
    private void delete_Table_BTN_Action()
    {
        if (! are_You_Sure("Delete")) { return; }

        delete_Table_Action();
    }

    private void delete_Table_Action()
    {
        //################################################
        // Delete table from database
        //################################################
         /*
            Delete all ingredients from this meal (using mealID) from table "ingredients_in_meal"
            Delete meal from meals database
         */

        String query = "DELETE FROM draft_divided_meal_sections WHERE draft_div_meal_sections_id = ? ;";
        String error_msg = String.format("Unable to delete from '%s' !", table_name);
        Object[] params = new Object[]{ draft_sub_meal_id };

        Upload_Statement_Full sql_statement = new Upload_Statement_Full(query, params, error_msg, true);

        if (! db.upload_Data(sql_statement)) { return; }

        //################################################
        // Hide JTable object & Collapsible OBJ
        //################################################
        hide_Ingredients_Table();

        //################################################
        // Tell MealManager This Table Has Been Deleted
        //################################################
        meal_manager.ingredients_Table_Has_Been_Deleted(); // deletes meal if this is the last sub-meal

        //################################################
        // Update MacrosLeft Table & TotalMeal Table
        //################################################
        update_All_Tables_Data();

        //################################################
        // Progress Message
        //################################################
        JOptionPane.showMessageDialog(frame, "Table Successfully Deleted!");
    }

    //#######################
    //
    //#######################
    private void hide_Ingredients_Table()
    {
        set_Visibility(false); // hide collapsible Object
        set_Table_Deleted(true);
    }

    private void un_Hide_Ingredients_Table()
    {
        set_Visibility(true); // hide collapsible Object
        set_Table_Deleted(false); // set this object as deleted
    }

    public void completely_Delete()
    {
        // remove JTable from GUI
        parent_Container.remove(this);

        // remove Space Divider
        parent_Container.remove(space_divider);

        // Tell Parent container to resize
        parent_Container.revalidate();
    }

    //####################################################
    // Sub-Meal Name Methods
    //####################################################
    private void edit_Sub_Meal_Name_BTN()
    {
        set_Sub_Meal_Meta_Data_Changed(true);
        set_Sub_Meal_Name_Variable(true, saved_sub_meal_name, null);
    }

    private void set_Sub_Meal_Name_Variable(boolean has_Sub_Meal_Name_Been_Changed, String saved_sub_meal_name, String current_sub_meal_name)
    {
        this.has_Sub_Meal_Name_Been_Changed = has_Sub_Meal_Name_Been_Changed;

        this.saved_sub_meal_name = saved_sub_meal_name;
        this.current_sub_meal_name = current_sub_meal_name;
    }

    public String get_Current_Sub_Meal_Name()
    {
        return current_sub_meal_name;
    }

    //####################################################
    // Sub-Meal Time Methods
    //####################################################
    private void edit_Sub_Meal_Time_BTN()
    {
        //###############################
        // Prompt User for time Input
        //###############################

        LocalTime new_meal_time = prompt_User_For_Meal_Time(false, true);
        LocalTime old_current_time = get_Current_Sub_Meal_Time();

        if (new_meal_time == null) { return; } // Error occurred in validation checks above

        //###############################
        // Update DB
        //###############################
        String upload_Query = """
                UPDATE draft_divided_meal_sections
                SET sub_meal_time = ?
                WHERE draft_div_meal_sections_id = ?;""";

        Object[] params = new Object[]{ new_meal_time, draft_sub_meal_id };
        String error_msg = "Error, unable to change Sub-Meal Time!";
        Upload_Statement_Full sql_statement = new Upload_Statement_Full(upload_Query, params, error_msg, true);

        // Upload Into Database Table
        if (! db.upload_Data(sql_statement)) { return; }

        //###############################
        // Update Variables
        //###############################
        set_Sub_Meal_Meta_Data_Changed(true);
        set_Sub_Meal_Time_Variable(true, saved_sub_meal_time, null);
    }

    private LocalTime prompt_User_For_Meal_Time(boolean skip_confirmation, boolean comparison)
    {
        try
        {
            //###############################
            // Get Sub-Meal Time Ranges
            //###############################
            Pair<LocalTime, LocalTime> sub_meal_time_range = meal_manager.get_Available_Sub_Meal_Time_Ranges();

            //###############################
            // Get User Input
            //###############################
            String txt = String.format("Input Sub-Meal Time in the ranges \" %s - %s \"?",
                    sub_meal_time_range.getValue0(), sub_meal_time_range.getValue1());

            String input_meal_time = JOptionPane.showInputDialog(txt);

            if (input_meal_time == null || input_meal_time.isEmpty()) { return null; }

            //###############################
            // Return Validation Results
            //###############################
            return input_Meal_Time_Validation(sub_meal_time_range, input_meal_time, skip_confirmation, comparison);
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s \n%s", get_Class_And_Method_Name(), e);
            JOptionPane.showMessageDialog(null, "Error changing Sub-Meal Time!");
            return null;
        }
    }

    private LocalTime input_Meal_Time_Validation
            (
                    Pair<LocalTime, LocalTime> sub_meal_time_Ranges,
                    String input_meal_time_string,
                    boolean skip_confirmation,
                    boolean comparison
            )
    {
        //#######################################################
        // Validation Checks
        //#######################################################

        // Prior to this method being called the users input_meal_time_string is checked if its null or "" and rejected
        LocalTime new_input_time_local_time;
        LocalTime old_current_meal_time = get_Current_Sub_Meal_Time();

        LocalTime start_sub_meal_time_range = sub_meal_time_Ranges.getValue0();
        LocalTime end_sub_meal_time_range = sub_meal_time_Ranges.getValue1();

        try
        {
            new_input_time_local_time = LocalTime.parse(input_meal_time_string, time_Formatter);
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s | Error, converting input_meal_time_string to time string! \n%s", get_Class_And_Method_Name(), e);
            JOptionPane.showMessageDialog(null, "Error, converting input_meal_time_string to Time!!");
            return null;
        }

        // ####################################################
        // Is Sub-Meal Time In Acceptable Range
        // ####################################################
        if (! new_input_time_local_time.isAfter(start_sub_meal_time_range) && new_input_time_local_time.isBefore(end_sub_meal_time_range))
        {
            String err_msg = String.format("Error, New Sub-Meal Time For %s isn't in the Time Ranges of [%s - %s]!",
                    get_Current_Sub_Meal_Name(), start_sub_meal_time_range, end_sub_meal_time_range);

            JOptionPane.showMessageDialog(null, err_msg);
            return null;
        }

        // ####################################################
        // Compare with saved correlating values
        // ####################################################
        if (comparison)
        {
            if (old_current_meal_time.equals(new_input_time_local_time)) // Time : User enters same meal time
            {
                JOptionPane.showMessageDialog(null, String.format("This meal 'time' already has the value '%s' !!", old_current_meal_time));
                return null;
            }
        }

        // ######################################################
        // Check Database if Value Already Exists
        // ######################################################
        String query = " SELECT 1 FROM draft_divided_meal_sections WHERE plan_id = ? AND sub_meal_time = ?";
        String errorMSG = "Error, Validating Meal Time!";
        Object[] params = new Object[]{ get_Plan_ID(), new_input_time_local_time };

        Fetch_Statement_Full fetch_statement = new Fetch_Statement_Full(query, params, errorMSG);

        try // Execute Query
        {
            if (! db.get_Single_Col_Query_Obj(fetch_statement, true).isEmpty()) // Means value already exists, returns N/A if the value doesn't
            {
                JOptionPane.showMessageDialog(null, String.format("A meal in this plan already has a meal time of '%s' !!", new_input_time_local_time));
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
            if (! are_You_Sure(String.format("change meal time from '%s' to '%s'", get_Current_Sub_Meal_Time().toString(), input_meal_time_string)))
            {
                return null;
            }
        }

        //################################################################
        // Return Value
        //#################################################################
        return new_input_time_local_time;
    }

    private void set_Sub_Meal_Time_Variable(boolean has_Sub_Meal_Time_Been_Changed, LocalTime saved_sub_meal_time, LocalTime current_sub_meal_time)
    {
        this.has_Sub_Meal_Time_Been_Changed = has_Sub_Meal_Time_Been_Changed;

        this.saved_sub_meal_time = saved_sub_meal_time;
        this.current_sub_meal_time = current_sub_meal_time;
    }

    public LocalTime get_Current_Sub_Meal_Time()
    {
        return current_sub_meal_time;
    }

    //##################################################################################################################
    // Update Other Table Methods
    //##################################################################################################################
    private void update_All_Tables_Data()
    {
        meal_manager.update_MealManager_DATA();
        update_Macros_Left_Table();
    }

    private void update_Macros_Left_Table()
    {
        macrosLeft_table.update_Table();
    }

    private void update_Meal_Manager_True_State_Change()
    {
        meal_manager.set_Has_Meal_Data_Changed(true);
    }

    //##################################################################################################################
    // Mutator Methods
    //##################################################################################################################
    public void set_Source_Sub_Meal_ID(int source_sub_meal_id)
    {
        this.source_sub_meal_id = source_sub_meal_id;
    }

    //########################
    // Delete
    //########################
    private void set_Table_Deleted(boolean deleted)
    {
        table_deleted = deleted;
    }

    private void set_Visibility(boolean condition)
    {
        this.setVisible(condition);
        space_divider.setVisible(condition);
    }

    //########################
    // Sub-Meal Time / Name
    //########################


    //########################
    // Save
    //########################
    public void set_Meal_In_DB(boolean meal_In_DB)
    {
        this.sub_meal_in_db = meal_In_DB;
    }

    private void set_Sub_Meal_Saved(boolean state)
    {
        sub_meal_saved = state;
    }

    private void set_Sub_Meal_Data_Changed(boolean state)
    {
        sub_meal_data_changed = state;

        if (state) { update_Meal_Manager_True_State_Change(); }
    }

    private void set_Sub_Meal_Meta_Data_Changed(boolean state)
    {
        sub_meal_meta_data_changed = state;

        if (state) { update_Meal_Manager_True_State_Change(); }
    }

    //##################################################################################################################
    // Accessor Methods
    //##################################################################################################################


    //###############################
    // Integer
    //###############################
    public int get_Draft_Sub_Meal_ID()
    {
        return draft_sub_meal_id;
    }

    private int get_Plan_ID()
    {
        return shared_data_registry.get_Selected_Plan_ID();
    }

    //###############################
    // Boolean
    //###############################
    public boolean is_Sub_Meal_In_DB()
    {
        return sub_meal_in_db;
    }

    public boolean has_Sub_Meal_Data_Changed()
    {
        return sub_meal_data_changed;
    }

    public boolean has_Sub_Meal_Meta_Data_Changed()
    {
        return sub_meal_meta_data_changed;
    }

    public boolean is_Sub_Meal_Saved()
    {
        return sub_meal_saved;
    }

    public boolean is_Sub_Meal_Deleted()
    {
        return table_deleted;
    }

    private boolean found_NA_Ingredient_In_Table(String attempting_To)
    {
        if (get_Rows_In_Table() == 0) { return false; }

        int ingredient_name_model_pos = get_Ingredient_Name_Col(true);

        for (int row = 0; row < get_Rows_In_Table(); row++) // Checking if N/A ingredient is in table
        {
            // Get Ingredient Name OBJ
            Ingredient_Name_ID_OBJ ingredient_name_id_obj =
                    (Ingredient_Name_ID_OBJ) get_Value_On_Model_Data(row, ingredient_name_model_pos);

            if (ingredient_name_id_obj.equals(na_ingredient_id_obj))
            {
                String message = String.format("""
                        
                        Please change the Ingredient at:
                        Row: %s
                        Column: %s
                        
                        From the ingredient 'None Of The Above' to another ingredient!
                        
                        Before attempting to %s!
                        """, row + 1, get_Ingredient_Name_Col(false) + 1, attempting_To);

                JOptionPane.showMessageDialog(frame, message); // Show Error MSG
                return true;
            }
        }
        return false;
    }

    //##################################################################################################################
    // Table Column Methods
    //##################################################################################################################
    /*

     */

    //##############################################################
    // Mutator Methods: Table Columns
    //##############################################################
    private void set_Model_Ingredient_Index_Col(int index)
    {
        model_ingredient_index_col = index;
    }

    private void set_Model_Quantity_Col(int index)
    {
        model_quantity_col = index;
    }

    private void set_Model_Ingredient_Type_Col(int index)
    {
        model_ingredient_type_col = index;
    }

    private void set_Model_Ingredient_Name_Col(int index)
    {
        model_ingredient_name_col = index;
    }

    protected void set_Model_Delete_BTN_Col(int index)
    {
        model_delete_btn_col = index;
    }

    //#############################################################
    // Accessor Methods : Table Columns
    //#############################################################
    private int get_Ingredient_Index_Col(boolean model_Index)
    {
        if (model_Index) { return model_ingredient_index_col; }

        return jTable.convertColumnIndexToView(model_ingredient_index_col);
    }

    private int get_Quantity_Col(boolean model_Index)
    {
        if (model_Index) { return model_quantity_col; }

        return jTable.convertColumnIndexToView(model_quantity_col);
    }

    private int get_Ingredient_Type_Col(boolean model_Index)
    {
        if (model_Index) { return model_ingredient_type_col; }

        return jTable.convertColumnIndexToView(model_ingredient_type_col);
    }

    private int get_Ingredient_Name_Col(boolean model_Index)
    {
        if (model_Index) { return model_ingredient_name_col; }

        return jTable.convertColumnIndexToView(model_ingredient_name_col);
    }

    private Integer get_Delete_BTN_Col(boolean model_Index)
    {
        if (model_Index) { return model_delete_btn_col; }

        return jTable.convertColumnIndexToView(model_delete_btn_col);
    }

    //##################################################################################################################
    // Object Equality Methods
    //##################################################################################################################
    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }

        if (! (o instanceof IngredientsTable other)) { return false; }

        return internalId.equals(other.internalId);
    }

    @Override
    public int hashCode()
    {
        return internalId.hashCode();
    }

}
