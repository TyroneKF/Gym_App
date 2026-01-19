package App_Code.Objects.Table_Objects.Tables.Children.Ingredients_Table;

import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Ingredient_Name_ID_OBJ;
import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Ingredient_Type_ID_OBJ;
import App_Code.Objects.Database_Objects.MyJDBC.MyJDBC_Sqlite;
import App_Code.Objects.Database_Objects.Null_MYSQL_Field;
import App_Code.Objects.Database_Objects.Fetched_Results;
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
import java.sql.Types;
import java.util.*;

public class IngredientsTable extends JDBC_JTable
{
    //#################################################################################################################
    // Variables
    //#################################################################################################################
   
    // Objects
    private final MealManager mealManager;
    private final Shared_Data_Registry shared_Data_Registry;
    private final MacrosLeft_Table macrosLeft_table;
    
    // Screen Objects
    private final JPanel space_Divider;
    private final Frame frame;
    
    //################################################
    // Other Variables
    //################################################
    private final int
            draft_meal_id,
            sub_Meal_ID;
    
    private boolean
            meal_In_DB,
            object_Deleted = false;
    
    private int
            model_Ingredient_Index_Col,
            model_Quantity_Col,
            model_Ingredient_Type_Col,
            model_Ingredient_Name_Col,
            model_DeleteBTN_Col;
    
    private final int none_Of_The_Above_ID = 1;
    private final int none_Of_The_Above_PDID = 1;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    // Ingredients Table
    public IngredientsTable
    (
            MyJDBC_Sqlite db,
            MealManager mealManager,
            Shared_Data_Registry shared_Data_Registry,
            MacrosLeft_Table macrosLeft_table,
            int sub_Meal_ID,
            ArrayList<ArrayList<Object>> data,
            boolean meal_In_DB,
            JPanel space_Divider
    )
    {
        super(
                db,
                mealManager.getCollapsibleCenterJPanel(),
                true,
                "draft_ingredients_index",
                "Ingredients Table",
                "draft_ingredients_in_sections_of_meal",
                "draft_gui_ingredients_in_sections_of_meal_calculation",
                data,
                mealManager.get_Ingredients_Table_Column_Names(),
                mealManager.get_Ingredients_Table_UnEditable_Cells(),
                mealManager.get_Ingredients_Table_Col_Avoid_Centering(),
                mealManager.get_Ingredients_Table_Col_To_Hide()
        );
        
        //##############################################################
        // Other Variables
        //##############################################################
        this.mealManager = mealManager;
        this.shared_Data_Registry = shared_Data_Registry;
        this.macrosLeft_table = macrosLeft_table;
        
        this.sub_Meal_ID = sub_Meal_ID;
        
        this.space_Divider = space_Divider;
        this.meal_In_DB = meal_In_DB;
        
        //#################################
        // Values From MealManager
        //#################################
        draft_meal_id = mealManager.get_Draft_Meal_In_Plan_ID();
        
        parent_Container = mealManager.getCollapsibleCenterJPanel();
        frame = mealManager.getFrame();
        
        //##############################################################
        // Setting Up Columns
        //##############################################################
        
        // Table : draft_ingredients_in_sections_of_meal_calculation
        set_Model_IngredientIndex_Col(column_Names_And_Positions.get(db_row_id_column_name)[0]);
        set_Model_Quantity_Col(column_Names_And_Positions.get("quantity")[0]);
        set_Model_IngredientType_Col(column_Names_And_Positions.get("ingredient_type_name")[0]);
        set_Model_IngredientName_Col(column_Names_And_Positions.get("ingredient_name")[0]);
        set_Model_DeleteBTN_Col(column_Names_And_Positions.get("delete_button")[0]);
        
        // Setting Up JComboBox  / Delete BTN Column Fields on Table
        setup_Special_Columns();
        
        //##############################################################
        // Table Customization
        //##############################################################
        setOpaque(true); //content panes must be opaque
        set_Table_Header_Font(new Font("Dialog", Font.BOLD, 12)); // Ingredients_In_Meal_Calculation Customisation
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
        // Refresh Icon
        //##########################
        
        IconButton refresh_Icon_Btn = new IconButton("/images/refresh/+refresh.png", icon_Size, icon_Size, icon_Size, icon_Size,
                "centre", "right"); // btn text is useless here , refactor
        //refresh_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));
        
        
        JButton refresh_Btn = refresh_Icon_Btn.returnJButton();
        refresh_Btn.setToolTipText("Restore Sub-Meal"); //Hover message over icon
        refresh_Icon_Btn.makeBTntransparent();
        
        refresh_Btn.addActionListener(ae -> {
            
            //#######################################################
            // Ask For Permission
            //#######################################################
            if (! (are_You_Sure("Refresh Data")))
            {
                return;
            }
            
            //#######################################################
            //  Delete meal if not in DB
            //#######################################################
            if (! (get_Meal_In_DB())) // Delete Meal if not in DB
            {
                if (are_You_Sure(" ' Refresh Data' this meal isn't saved and will result in this meal being deleted "))
                {
                    delete_Table_Action();
                    completely_Delete_Ingredients_Table();
                    mealManager.removeIngredientsTable(this); // Remove Ingredients Table from memory
                }
                return;
            }
            
            //#######################################################
            //  Refresh Meal
            //#######################################################
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
            if (are_You_Sure("Save Data"))
            {
                save_Btn_Action(true);
            }
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
            if (are_You_Sure("Delete"))
            {
                delete_Table_Action();
            }
        });
        
        iconPanel_Insert.add(delete_btn);
    }
    
    private void setup_Special_Columns()
    {
        //################################
        // Ingredients Type JC Setup
        //################################
        int ingredient_Type_Column = get_IngredientType_Col(false);
        
        new Ingredient_Type_JComboBox_Column(
                get_JTable(),
                ingredient_Type_Column,
                "Select Ingredient Type to Change Ingredient Names!",
                shared_Data_Registry.get_Mapped_Ingredient_Types()
        );
        
        //################################
        // Ingredients Name JC Setup
        //################################
        new Ingredient_Name_JComboBox_Column(
                get_JTable(),
                shared_Data_Registry,
                get_Ingredient_Name_Col(false),
                ingredient_Type_Column,
                "Select Ingredient Name!"
        );
        
        //################################
        // Delete BTn Column
        //################################
        setup_Delete_Btn_Column(get_DeleteBTN_Col(false));
    }
    
    //###################################
    // Delete Row Btn
    //###################################
    private void setup_Delete_Btn_Column(int delete_Btn_Column)
    {
        new Button_Column(this, delete_Btn_Column, get_IngredientIndex_Col(true));
    }
    
    public void delete_Row_Action(int ingredient_Index, int model_Row)
    {
        //#################################################
        // Can't have an empty Table
        //##################################################
        if (get_Rows_In_Table() == 1)
        {
            String question = """
                    \n\nThere is only 1  ingredient in this subMeal!
                    
                    If you delete this ingredient, this table will also be deleted.
                    
                    Would you still like to proceed?""";
            
            int reply = JOptionPane.showConfirmDialog(null, question, "Delete Ingredients", JOptionPane.YES_NO_OPTION); //HELLO Edit
            
            if (reply == JOptionPane.YES_OPTION)
            {
                delete_Table_Action();
            }
            
            return; // Exit
        }
        
        //#################################################
        // Delete Ingredient From Temp Meal
        //#################################################
        String query = String.format("DELETE FROM %s WHERE %s = ? ;",  db_write_table_name,db_row_id_column_name);
        String error_msg = String.format("Error, unable to delete from '%s' !", table_name);
        
        if (! db.upload_Data(query, new Object[]{ ingredient_Index }, error_msg)) { return; }
        
        //#################################################
        // Remove From Table
        //##################################################
        delete_Row(model_Row);
        resize_Object();
        
        //#################################################
        // Update Table Data
        //##################################################
        update_All_Tables_Data();
    }
    
    //##################################################################################################################
    // Data Changing In Cells Actions
    //##################################################################################################################
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
    
    @Override
    protected boolean table_Data_Changed_Action(int row_In_Model, int column_In_Model, Object new_Value) throws Exception
    {
        //##################################################################
        // Variables
        //##################################################################
        int ingredient_Index = (Integer) get_Value_On_Model_Data(row_In_Model, get_IngredientIndex_Col(true));
        
        //##################################################################
        // Identify Trigger Column
        //##################################################################
        
        // Ingredients Type Column
        if (column_In_Model == get_IngredientType_Col(true)) { return true; } // Nothing to process inside db lvl
        
        //##########################################
        // Ingredients Name Column
        //##########################################
        else if (column_In_Model == get_Ingredient_Name_Col(true))
        {
            System.out.printf("\n\n@tableDataChange_Action() Ingredient Name Changed - %s", table_name);
            
            Ingredient_Name_ID_OBJ selected_Ingredient_Name_OBJ = (Ingredient_Name_ID_OBJ) new_Value;
            int selected_Ingredient_Name_ID = selected_Ingredient_Name_OBJ.get_ID();
            
            //##########################
            // Check IF N/A is selected
            //##########################
            if (selected_Ingredient_Name_OBJ.get_ID().equals(none_Of_The_Above_ID))
            {
                // Can't have multiple 'None Of The Above' ingredients in a meal
                if (is_There_An_Empty_Ingredients("change a current Ingredient in this meal to 'None Of The Above'"))
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
            System.out.printf("\ntableDataChange_Action() Quantity Being Changed - %s", table_name);
            return update_Table_Values_By_Quantity(row_In_Model, ingredient_Index, (BigDecimal) new_Value);
        }
        else
        {
            throw new Exception(String.format("\n\n%s Un-handled Column Event Trigger - %s", get_Method_Name(2), table_name));
        }
    }
    
    private boolean update_Table_By_Ingredient_Name(int row_In_Model, int selected_Ingredient_Name_ID, int ingredient_Index)
    {
        //##################################################################
        // Update & Fetch In MYSQL
        //##################################################################
        
        String error_MSG = String.format("Error, Updating Ingredient Name on '%s'!", table_name);
        LinkedHashSet<Pair<String, Object[]>> upload_Queries_And_Params = new LinkedHashSet<>();
        
        //###########################
        // Update
        //###########################
        String upload_Query = String.format("""
                UPDATE %s
                SET ingredient_id = ?,
                pdid = ?
                WHERE %s = ?;""",  db_write_table_name, db_row_id_column_name);
        
        Object[] params_Upload = new Object[]{
                selected_Ingredient_Name_ID,
                new Null_MYSQL_Field(Types.INTEGER),
                ingredient_Index
        };
        
        upload_Queries_And_Params.add(new Pair<>(upload_Query, params_Upload));
        
        //##############################################
        // Execute
        //##############################################
        return upload_And_Update_Table(row_In_Model, ingredient_Index, upload_Queries_And_Params, error_MSG);
    }
    
    private boolean update_Table_Values_By_Quantity(int row_In_Model, int ingredient_Index, BigDecimal quantity)
    {
        //##################################################################
        // Update & Fetch In MYSQL
        //##################################################################
        
        String error_MSG = "Error, Updating Ingredient Table by Quantity!";
        LinkedHashSet<Pair<String, Object[]>> upload_Queries_And_Params = new LinkedHashSet<>();
        
        //###########################
        // Update
        //###########################
        String upload_Query = String.format("""
                UPDATE  %s
                SET quantity = ?
                WHERE %s = ?;""",  db_write_table_name, db_row_id_column_name);
        
        upload_Queries_And_Params.add(new Pair<>(upload_Query, new Object[]{ quantity, ingredient_Index}));
        
        //##############################################
        // Execute
        //##############################################
        return upload_And_Update_Table(row_In_Model, ingredient_Index, upload_Queries_And_Params, error_MSG);
    }
    
    private boolean upload_And_Update_Table(int row_In_Model, int ingredient_Index, LinkedHashSet<Pair<String, Object[]>> upload_Queries_And_Params, String error_MSG)
    {
        //###################################
        // Fetch
        //###################################
        LinkedHashSet<Pair<String, Object[]>> fetch_Queries_And_Params = new LinkedHashSet<>();
        
        String fetch_Query = String.format("SELECT * FROM %s WHERE %s = ?;",  db_read_view_name, db_row_id_column_name);
        fetch_Queries_And_Params.add(new Pair<>(fetch_Query, new Object[]{ ingredient_Index }));
        
        //##############################################
        // Execute
        //##############################################
        Fetched_Results fetched_Results = db.upload_And_Get_Batch(upload_Queries_And_Params, fetch_Queries_And_Params, error_MSG);
        
        if (fetched_Results == null) { return false; } // Upload & Fetch Failed
        
        //##################################################################
        // Update Table
        //##################################################################
        try
        {
            // Get First Row Results & Format Data to include Storable_ID_Objects
            ArrayList<Object> formatted_Data = format_DB_Results_For_ID_Objects(fetched_Results.get_Result_1D_AL(0));
            
            super.update_Table_Row(formatted_Data, row_In_Model); // Update This Table
            
            update_All_Tables_Data();// Update ALl Other Tables
            return true;
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s \n\n%s", get_Class_And_Method_Name(), e);
            JOptionPane.showMessageDialog(null, String.format("Unable to Update - '%s'!", table_name));
            return false;
        }
    }
    
    private ArrayList<Object> format_DB_Results_For_ID_Objects(ArrayList<Object> new_Data) throws Exception
    {
        //##########################
        // Ingredients Name
        //##########################
        int ingredient_Name_Pos = get_Ingredient_Name_Col(true);
        int ingredient_Name_ID = (Integer) new_Data.get(ingredient_Name_Pos);
        
        Ingredient_Name_ID_OBJ ingredient_Name_ID_OBJ = shared_Data_Registry.get_Ingredient_Name_ID_OBJ_By_ID(ingredient_Name_ID);
        
        if (ingredient_Name_ID_OBJ == null)
        {
            throw new Exception(String.format("%s Error, Ingredient_Name_ID_OBJ returned null %s", get_Class_And_Method_Name(), ingredient_Name_ID));
        }
        
        new_Data.set(ingredient_Name_Pos, ingredient_Name_ID_OBJ);
        
        //##########################
        // Ingredients Type
        //##########################
        int ingredient_Type_Pos = get_IngredientType_Col(true);
        int ingredient_Type_ID = (Integer) new_Data.get(ingredient_Type_Pos);
        
        Ingredient_Type_ID_OBJ ingredient_Type_ID_OBJ = shared_Data_Registry.get_Type_ID_Obj_By_ID(ingredient_Type_ID);
        
        if (ingredient_Type_ID_OBJ == null)
        {
            throw new Exception(String.format("%s Error, Ingredient_Type_ID_OBJ returned null %s", get_Class_And_Method_Name(), ingredient_Type_ID));
        }
        
        new_Data.set(ingredient_Type_Pos, ingredient_Type_ID_OBJ);
        
        //##########################
        // Output
        //##########################
        return new_Data;
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
        if (is_There_An_Empty_Ingredients("add a new row!"))
        {
            return;
        }
        
        //################################################################
        // Upload & Fetch Variables
        //################################################################
        String error_MSG = String.format("\n\nError Adding Additional Ingredient to Meal: \n\nMeal Name: '%s' \nMeal Time: %s!",
                mealManager.get_Current_Meal_Name(), mealManager.get_Current_Meal_Time_GUI());
        
        LinkedHashSet<Pair<String, Object[]>> upload_Queries_And_Params = new LinkedHashSet<>();
        LinkedHashSet<Pair<String, Object[]>> fetch_Queries_And_Params = new LinkedHashSet<>();
        
        ArrayList<Object> ingredient_DATA;
        
        //################################################
        // Uploads
        //###############################################
        
        // 1.) Insert Ingredient to Ingredients Table
        String upload_Q1 = String.format( """
                INSERT INTO %s
                (
                    draft_div_meal_sections_id,
                    ingredient_id,
                    pdid,
                    quantity
                )
                VALUES
                (?, ?, ?, ?);""", db_write_table_name);
        
        upload_Queries_And_Params.add(new Pair<>(upload_Q1,
                new Object[]{ sub_Meal_ID, none_Of_The_Above_ID, none_Of_The_Above_PDID, 0 }));
    
        //#######################################################
        // Fetch Queries
        //#######################################################
        
        // 1.) Get Ingredient ID
        String get_Q1 = String.format("""
                SELECT *
                FROM %s
                WHERE %s = (SELECT last_insert_rowid());""", db_read_view_name, db_row_id_column_name);
        fetch_Queries_And_Params.add(new Pair<>(get_Q1, null));
        
        //#######################################################
        // Execute Query
        //#######################################################
        Fetched_Results fetched_Results_OBJ = db.upload_And_Get_Batch(upload_Queries_And_Params, fetch_Queries_And_Params, error_MSG);
        
        if (fetched_Results_OBJ == null) { System.err.println("\n\n\nFailed Adding Ingredient"); return; }
        
        //#######################################################
        // Set Variables from Results
        //#######################################################
        try
        {
            ingredient_DATA = format_DB_Results_For_ID_Objects(fetched_Results_OBJ.get_Result_1D_AL(0));
            
            System.out.printf("\n\nIngredients Results: \n%s%n", ingredient_DATA);
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null,String.format("Unable to add Ingredient to %s", table_name));
            System.err.printf("\n\n%s \n%s", get_Class_And_Method_Name(), e);
            return;
        }
        
        //#######################################################
        //   Updating Ingredients With New Ingredients DATA
        //#######################################################
        add_Row(ingredient_DATA); // Adding Row Data to Table Model
        
        //#######################################################
        // Update Table Data
        //#######################################################
        mealManager.update_MealManager_DATA(false, false); // No Macros have changed to change data
    }
    
    //###################################################
    // Refresh Button
    //###################################################
    public void refresh_Btn_Action()
    {
        //#############################
        // Reset DB Data
        //#############################
        if (! (transfer_Meal_Data_From_Plans(null, null)))
        {
            JOptionPane.showMessageDialog(frame, "\n\nUnable to transfer ingredients data from  original plan to temp plan!!");
            return;
        }
        
        //#############################
        // Reset Table Info & Data
        //#############################
        refresh_Data(true, true);
    }
    
    public boolean transfer_Meal_Data_From_Plans(Integer from_Plan_ID, Integer to_Plan_ID)
    {
       /* //########################################################
        // Clear Old Data from toPlan and & Temp Tables
        //########################################################
        // Delete tables if they already exist
        String query0 = "DROP TABLE IF EXISTS temp_ingredients_in_meal;";
        
        // Delete ingredients in meal Data from original plan with this mealID
        String query1 = """
                DELETE FROM draft_ingredients_in_sections_of_meal
                WHERE div_meal_sections_id = ? ;""";
        
        //########################################################
        // Insert Meal & dividedMealSections If Not in DB In toPlan
        //########################################################
        
        // insert meal if it does not exist inside to_Plan_ID
        String query2 = """
                INSERT IGNORE INTO meals_in_plan
                (meal_in_plan_id, plan_id, meal_name)
                VALUES
                (?, ?, ?);""";
        
        String query3 = """
                INSERT IGNORE INTO draft_divided_meal_sections
                (div_meal_sections_id, meal_in_plan_id, plan_id)
                VALUES
                (?,?,?);""";
        
        //####################################################
        // Transferring this plans Ingredients to Temp-Plan
        //####################################################
        
        // Create Table to transfer ingredients from original plan to temp
        String query4 = """
                CREATE table temp_ingredients_in_meal  AS
                SELECT i.*
                FROM draft_ingredients_in_sections_of_meal i
                WHERE i.div_meal_sections_id = ? AND i.plan_id = ?;""";
        
        String query5 = "UPDATE temp_ingredients_in_meal  SET plan_id = ?;";
        
        String query6 = "INSERT INTO draft_ingredients_in_sections_of_meal SELECT * FROM temp_ingredients_in_meal;";
        
        String query7 = "DROP TABLE IF EXISTS temp_ingredients_in_meal;";
        
        //####################################################
        // Update
        //####################################################
        String error_MSG = "Error, Unable to transfer Meal Data";
        
        LinkedHashSet<Pair<String, Object[]>> queries_And_Params = new LinkedHashSet<>()
        {{
            add(new Pair<>(query0, null));
            add(new Pair<>(query1, new Object[]{ sub_Meal_ID, to_Plan_ID }));
            add(new Pair<>(query2, new Object[]{ meal_In_Plan_ID, to_Plan_ID, get_Meal_Name() }));
            add(new Pair<>(query3, new Object[]{ sub_Meal_ID, meal_In_Plan_ID, to_Plan_ID }));
            add(new Pair<>(query4, new Object[]{ sub_Meal_ID, from_Plan_ID }));
            add(new Pair<>(query5, new Object[]{ to_Plan_ID }));
            add(new Pair<>(query6, null));
            add(new Pair<>(query7, null));
        }};
        
        if (! (sqlite_db.upload_Data_Batch(queries_And_Params, error_MSG))) { return false; }
        
        //####################################################
        // Output
        //####################################################
        System.out.printf("\nMealIngredients Successfully Transferred! \n\n%s", line_Separator);
        return true;*/
        
        return false;
    }
    
    public void refresh_Data(boolean update_MacrosLeft_Table, boolean update_TotalMeal_Table)
    {
        //#############################################################################################
        // Reset Variable
        //#############################################################################################
        // If Meal was Previously Deleted Reset Variables & State
        if (is_Table_Deleted())
        {
            unHide_Ingredients_Table();
        }
        
        refresh_Data(); // Reset Table Model data
        
        if (update_TotalMeal_Table) // Reset Meal Total  Table Data
        {
            mealManager.update_MealManager_DATA(true, true);
        }
        
        if (update_MacrosLeft_Table) // Update Other Tables Data
        {
            macrosLeft_table.update_Table();
        }
    }
    
    //###################################################
    // Save Button
    //###################################################
    public boolean save_Btn_Action(boolean show_Message)
    {
        save_Data(); // Update Table Model
     
        if (show_Message) // Success Message
        {
            JOptionPane.showMessageDialog(frame, "Table Successfully Updated!");
        }
        
        return true; // Output
    }
    
    //####################################################
    // Delete Button Methods
    //####################################################
    public void delete_Table_Action()
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
        
        Object[] params = new Object[]{ sub_Meal_ID };
        
        if (! db.upload_Data(query, params, error_msg)) { return; }
        
        //################################################
        // Hide JTable object & Collapsible OBJ
        //################################################
        hide_Ingredients_Table();
        
        //################################################
        // Update MacrosLeft Table & TotalMeal Table
        //################################################
        update_All_Tables_Data();
        
        //################################################
        // Tell MealManager This Table Has Been Deleted
        //################################################
        mealManager.ingredientsTableHasBeenDeleted();
        
        //################################################
        // Progress Message
        //################################################
        JOptionPane.showMessageDialog(frame, "Table Successfully Deleted!");
    }
    
    public void completely_Delete_Ingredients_Table()
    {
        // Hide Ingredients Table
        hide_Ingredients_Table();
        
        // remove JTable from GUI
        parent_Container.remove(this);
        
        // remove Space Divider
        parent_Container.remove(space_Divider);
        
        // Tell Parent container to resize
        parent_Container.revalidate();
    }
    
    public void set_Visibility(boolean condition)
    {
        this.setVisible(condition);
        space_Divider.setVisible(condition);
    }
    
    private void hide_Ingredients_Table()
    {
        set_Visibility(false); // hide collapsible Object
        set_Object_Deleted(true);
    }
    
    private void unHide_Ingredients_Table()
    {
        set_Visibility(true); // hide collapsible Object
        set_Object_Deleted(false); // set this object as deleted
    }
    
    //##################################################################################################################
    // Update Other Table Methods
    //##################################################################################################################
    private void update_All_Tables_Data()
    {
        mealManager.update_MealManager_DATA(true, true);
        macrosLeft_table.update_Table();
    }
    
    //##################################################################################################################
    // Mutator Methods
    //##################################################################################################################
    private void set_Object_Deleted(boolean deleted)
    {
        object_Deleted = deleted;
    }
    
    public void set_Meal_In_DB(boolean meal_In_DB)
    {
        this.meal_In_DB = meal_In_DB;
    }
    
    //##################################################################################################################
    // Accessor Methods
    //##################################################################################################################
    public String get_Meal_Name()
    {
        return mealManager.get_Current_Meal_Name();
    }
    
    public boolean get_Meal_In_DB()
    {
        return meal_In_DB;
    }
    
    public Integer get_Sub_Meal_ID()
    {
        return sub_Meal_ID;
    }
    
    public Integer get_Draft_Meal_ID()
    {
        return draft_meal_id;
    }
    
    public boolean is_Table_Deleted()
    {
        return object_Deleted;
    }
    
    private boolean is_There_An_Empty_Ingredients(String attempting_To)
    {
        if (get_Rows_In_Table() == 0) { return false; }
        
        //Checking if the ingredient None Of the Above is already in the table
        for (int row = 0; row < get_Rows_In_Table(); row++)
        {
            // Get Ingredient Name OBJ
            Ingredient_Name_ID_OBJ ingredient_name_id_obj =
                    (Ingredient_Name_ID_OBJ) get_Value_On_Model_Data(row, get_Ingredient_Name_Col(true));
            
            Integer ingredient_OBJ_ID = ingredient_name_id_obj.get_ID(); // Get ID of Ingredient Name
            
            // if None Of  the above is found in the table return true
            if (ingredient_OBJ_ID.equals(none_Of_The_Above_ID))
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
    // Table Columns
    //##################################################################################################################
    
    // Mutator (Set) For Table Column Positions 
    private void set_Model_IngredientIndex_Col(int index)
    {
        model_Ingredient_Index_Col = index;
    }
    
    private void set_Model_Quantity_Col(int index)
    {
        model_Quantity_Col = index;
    }
    
    private void set_Model_IngredientType_Col(int index)
    {
        model_Ingredient_Type_Col = index;
    }
    
    private void set_Model_IngredientName_Col(int index)
    {
        model_Ingredient_Name_Col = index;
    }
    
    protected void set_Model_DeleteBTN_Col(int index)
    {
        model_DeleteBTN_Col = index;
    }
    
    //#############################################################
    // Accessor For Table Column Positions
    //#############################################################
    private int get_IngredientIndex_Col(boolean model_Index)
    {
        if (model_Index) { return model_Ingredient_Index_Col; }
        
        return jTable.convertColumnIndexToView(model_Ingredient_Index_Col);
    }
    
    private int get_Quantity_Col(boolean model_Index)
    {
        if (model_Index) { return model_Quantity_Col; }
        
        return jTable.convertColumnIndexToView(model_Quantity_Col);
    }
    
    private int get_IngredientType_Col(boolean model_Index)
    {
        if (model_Index) { return model_Ingredient_Type_Col; }
        
        return jTable.convertColumnIndexToView(model_Ingredient_Type_Col);
    }
    
    private int get_Ingredient_Name_Col(boolean model_Index)
    {
        if (model_Index) { return model_Ingredient_Name_Col; }
        
        return jTable.convertColumnIndexToView(model_Ingredient_Name_Col);
    }
    
    private Integer get_DeleteBTN_Col(boolean model_Index)
    {
        if (model_Index) { return model_DeleteBTN_Col; }
        
        return jTable.convertColumnIndexToView(model_DeleteBTN_Col);
    }
}
