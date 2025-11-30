package App_Code.Objects.Tables.JTable_JDBC.Children;


import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JDBC.Null_MYSQL_Field;
import App_Code.Objects.Database_Objects.JDBC.Fetched_Results;
import App_Code.Objects.Tables.JTable_JDBC.JDBC_JTable;
import App_Code.Objects.Tables.MealManager;
import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Gui_Objects.IconPanel;
import org.javatuples.Pair;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.*;

public class IngredientsTable extends JDBC_JTable
{
    //#################################################################################################################
    // Collections
    //#################################################################################################################
    private TreeMap<String, TreeSet<String>> map_ingredient_Types_To_Names;
    private ArrayList<Integer> trigger_Columns;
    
    //#################################################################################################################
    // Objects
    //#################################################################################################################
    private JPanel space_Divider;
    private MealManager mealManager;
    private Frame frame;
    
    //#################################################################################################################
    // Other Variables
    //#################################################################################################################
    private Integer plan_ID, temp_Plan_ID = 1, meal_In_Plan_ID, sub_Meal_ID;
    private String mealName;
    
    private boolean
            data_Changed_In_Table = false,
            is_row_Being_Edited = false,
            meal_In_DB,
            object_Deleted = false,
            ingredient_Name_Changed = false;
    
    private int
            model_Ingredient_Index_Col,
            model_Ingredient_ID_Col,
            model_Quantity_Col,
            model_Ingredient_Type_Col,
            model_Ingredient_Name_Col,
            model_Supplier_Col,
            model_ProductName_Col,
            model_DeleteBTN_Col;
    
    private final int none_Of_The_Above_ID = 1;
    
    private final int none_Of_The_Above_PDID = 1;
    
    //SupplierName JComboBox Variables
    private Object
            previous_ProductName_JC_Item, selected_ProductName_JC_Item,
            previous_Supplier_JC_Item, selected_Supplier_JC_Item,
            previous_IngredientName_JC_Item, selected_IngredientName_JC_Item,
            previous_IngredientType_JC_Item, selected_IngredientType_JC_Item;
    
    private String line_Separator = "###############################################################################";
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    // Ingredients Table
    public IngredientsTable(MyJDBC db, MealManager mealManager, int sub_Meal_ID, ArrayList<ArrayList<Object>> data,
                            boolean meal_In_DB, JPanel space_Divider)
    {
        super(db,
                mealManager.getCollapsibleCenterJPanel(),
                true,
                true,
                "ingredients_in_sections_of_meal_calculation",
                data,
                mealManager.get_Ingredients_Table_Column_Names(),
                mealManager.get_Ingredients_Table_UnEditable_Cells(),
                mealManager.get_Ingredients_Table_Col_Avoid_Centering(),
                mealManager.get_Ingredients_Table_Col_To_Hide()
        );
        
        //##############################################################
        // Other Variables
        //##############################################################
        this.space_Divider = space_Divider;
        this.meal_In_DB = meal_In_DB;
        this.mealManager = mealManager;
        
        //#################################
        // Values From MealManager
        //#################################
        this.plan_ID = mealManager.get_Temp_PlanID();
        this.meal_In_Plan_ID = mealManager.get_Meal_In_Plan_ID();
        this.sub_Meal_ID = sub_Meal_ID;
        
        parent_Container = mealManager.getCollapsibleCenterJPanel();
        
        map_ingredient_Types_To_Names = mealManager.getMap_ingredient_Types_To_Names();
        frame = mealManager.getFrame();
        
        //##############################################################
        // Setting Trigger Columns
        //##############################################################
        
        // Table : ingredients_in_sections_of_meal_calculation
        set_Model_IngredientIndex_Col(column_Names_And_Positions.get("ingredients_index")[0]);
        set_Model_IngredientID_Col(column_Names_And_Positions.get("ingredient_id")[0]);
        set_Model_Quantity_Col(column_Names_And_Positions.get("quantity")[0]);
        set_Model_IngredientType_Col(column_Names_And_Positions.get("ingredient_type")[0]);
        set_Model_IngredientName_Col(column_Names_And_Positions.get("ingredient_name")[0]);
        set_Model_Supplier_Col(column_Names_And_Positions.get("supplier")[0]);
        set_Model_ProductName_Col(column_Names_And_Positions.get("product_name")[0]);
        set_Model_DeleteBTN_Col(column_Names_And_Positions.get("delete button")[0]);
        
        //##############################################################
        // Setting Trigger Columns
        //##############################################################
        this.trigger_Columns = new ArrayList<>(Arrays.asList(
                get_IngredientIndex_Col(true), get_IngredientID_Col(true),
                get_Quantity_Col(true), get_IngredientType_Col(true), get_IngredientName_Col(true), get_Supplier_Col(true),
                get_ProductName_Col(true)));
        
        //##############################################################
        // Setting Up JComboBox Fields on Table
        //##############################################################
        new SetupIngredientTypeColumn(get_IngredientType_Col(false));
        new SetupIngredientNameColumn(get_IngredientName_Col(false));
        new SetupSupplierColumn(get_Supplier_Col(false));
        new SetupProduct_NameColumn(get_ProductName_Col(false));
        
        //##############################################################
        // Setting Up Delete Button On JTable
        //##############################################################
        setup_Delete_Btn_Column(get_DeleteBTN_Col(false));
        
        //##############################################################
        // Table Customization
        //##############################################################
        setOpaque(true); //content panes must be opaque
        set_Table_Header_Font(new Font("Dialog", Font.BOLD, 12)); // Ingredients_In_Meal_Calculation Customisation
    }
    
    //##################################################################################################################
    // Table Setup
    //##################################################################################################################
    @Override
    protected void extra_TableModel_Setup()
    {
        // Setup Specials sections in table
        new SetupIngredientTypeColumn(get_IngredientType_Col(false));
        new SetupIngredientNameColumn(get_IngredientName_Col(false));
        new SetupSupplierColumn(get_Supplier_Col(false));
        new SetupProduct_NameColumn(get_ProductName_Col(false));
        
        setup_Delete_Btn_Column(get_DeleteBTN_Col(false)); // specifying delete column
    }
    
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
    
    //##################################################################################################################
    // Data Changing In Cells Action
    //##################################################################################################################
    //HELLO!! FIX Editing Now
    private boolean has_Data_Changed_In_Cell(int col, Object ingredientsIndex, Object cellValue)
    {
        //######################################################
        // Get Table Data
        //######################################################
        ArrayList<ArrayList<Object>> tableData = get_Saved_Data();
        
        //######################################################
        // Get Index Of IngredientsIndex in Original Data
        //######################################################
        Integer indexOf_IngredientsIndex = null;
        
        for (int pos = 0; pos < column_Names.size(); pos++)
        {
            if (column_Names.get(pos).equals("ingredients_index"))
            {
                indexOf_IngredientsIndex = pos;
                break;
            }
            pos++;
        }
        
        
        //######################################################
        for (ArrayList<Object> rowData : tableData)
        {
        
        }
        
        //######################################################
        return true;
    }
    
    //Editing Now
    @Override
    protected void table_Data_Changed_Action(TableModelEvent evt)
    {
        int row_Model = evt.getFirstRow(), column_Model = evt.getColumn();
        
        //#############################################################################################################
        // Check if cell that triggered this event can execute
        //#############################################################################################################
        // Avoids endless loop / if edited cell column, is supposed to a trigger an action
        if (is_row_Being_Edited || trigger_Columns == null || ! (trigger_Columns.contains(column_Model))) { return; }
        
        //#############################################################################################################
        // Variables
        //#############################################################################################################
        set_Row_Being_Edited();
        
        Object ingredient_ID = tableModel.getValueAt(row_Model, get_IngredientID_Col(true));
        
        Object ingredient_Index = tableModel.getValueAt(row_Model, get_IngredientIndex_Col(true));
        
        Object cell_Value = tableModel.getValueAt(row_Model, column_Model);
        
        //#############################################################################################################
        // Check if CellData has changed
        //#############################################################################################################
        
        if (! (has_Data_Changed_In_Cell(column_Model, ingredient_Index, cell_Value)))
        {
            set_Row_Being_Edited();
            return;
        }
        
        //#############################################################################################################
        // Trigger Columns
        //#############################################################################################################
        
        // // Ingredients Type Column
        if (column_Model == get_IngredientType_Col(true))
        {
            System.out.printf("\n\n@tableDataChange_Action() Ingredient Type Changed"); //HELLO REMOVE
            set_Row_Being_Edited();
            return;
        }
        
        //###############################
        // Ingredients Quantity Column
        // ###############################
        if (column_Model == get_Quantity_Col(true) && tableModel.getValueAt(row_Model, column_Model) == null)
        {
            JOptionPane.showMessageDialog(frame, String.format("\n\nPlease insert a reasonable 'Quantity' value in the cell at: \n\nRow: %s \nColumn: %s", row_Model + 1, column_Model + 1));
            
            cell_Value = 0.00;
            set_Row_Being_Edited(); // re-triggers this method on quantity changed
            
            tableModel.setValueAt(cell_Value, row_Model, column_Model);
        }
        
        else if (column_Model == get_Quantity_Col(true))
        {
            System.out.printf("\ntableDataChange_Action() Quantity Being Changed");
            set_Row_Being_Edited();// HELLO
            
            update_Table_Values_By_Quantity(row_Model, ingredient_Index, cell_Value);
            return;
        }
        
        //###############################
        // Ingredients Name Column
        //###############################
        else if (column_Model == get_IngredientName_Col(true))
        {
            System.out.println("\n\n@tableDataChange_Action() Ingredient Name Changed");
            
            // if the same item is selected avoid processing
            if (! (ingredient_Name_Changed))
            {
                System.out.printf("\n\nExit No Update Ingredient Name Changed \ningredientNameChanged: %s", ingredient_Name_Changed);
                set_Row_Being_Edited();
                return;
            }
            
            if (selected_IngredientName_JC_Item.equals("None Of The Above"))
            {
                if (is_There_An_Empty_Ingredients(row_Model, "change a current Ingredient in this meal to 'None Of The Above'"))
                {
                    tableModel.setValueAt(previous_IngredientName_JC_Item, row_Model, column_Model);
                    set_Row_Being_Edited();
                    return;
                }
            }
            
            //##################################################################################################
            // Get Chosen Ingredient ID For Chosen Item (Ingredient Name)
            //##################################################################################################
            String
                    query = "Select ingredient_id From ingredients_info WHERE ingredient_name = ?;",
                    errorMSG = "Unable to retrieve chosen Ingredient ID from DB!";
            
            Object[] params = new Object[]{ selected_IngredientName_JC_Item };
            
            ArrayList<Integer> results_Ingredient_ID = db.get_Single_Col_Query_Int(query, params, errorMSG);
            
            if (results_Ingredient_ID == null)
            {
                JOptionPane.showMessageDialog(frame, "Unable to retrieve chosen Ingredient ID from DB!");
                
                // Change Jtable JComboBox Back To Original Value
                tableModel.setValueAt(previous_IngredientName_JC_Item, row_Model, column_Model);
                set_Row_Being_Edited();
                return;
            }
            
            Integer selected_Ingredient_ID = results_Ingredient_ID.getFirst();
            
            System.out.printf("\nPrevious JCombo Value: %s \nPrevious JCombo  ID: %s \n\nSelected JCombo Value: %s\nSelected JCombo ID: %s" +
                            "\n\nRow  Selected: %s \nColumn Selected: %s",
                    previous_IngredientName_JC_Item, ingredient_ID, selected_IngredientName_JC_Item, selected_Ingredient_ID, row_Model, column_Model);
            System.out.println("\n\n#########################################################################");
            
            //##################################################################################################
            // Get Rid off of old chosen product_name
            //##################################################################################################
            
            String upload_Query = """
                    UPDATE  ingredients_in_sections_of_meal
                    SET ingredient_id = ?,
                    pdid = ?
                    WHERE ingredients_index = ? AND plan_id = ?;""";
            
            Object[] params_Upload = new Object[]{
                    selected_Ingredient_ID,
                    new Null_MYSQL_Field(Types.INTEGER),
                    (Integer) ingredient_Index,
                    temp_Plan_ID
            };
            
            // Upload IngredientName & NEW PDID
            if (! (db.upload_Data(upload_Query, params_Upload, "Error, Unable to update Ingredient Info In DB!")))
            {
                // Change JTable JComboBox Back To Original Value
                tableModel.setValueAt(previous_IngredientName_JC_Item, row_Model, column_Model);
                
                set_Row_Being_Edited();
                return;
            }
            
            //###################################
            // Update  Other Table Values
            //###################################
            set_Row_Being_Edited(); //HELLO
            
            update_Table_Values_By_Quantity(row_Model, ingredient_Index, tableModel.getValueAt(row_Model, get_Quantity_Col(true)));
            return;
        }
        
        //###############################
        // Ingredients Supplier Column
        //###############################
        else if (column_Model == get_Supplier_Col(true))
        {
            System.out.printf("\n\n@tableDataChange_Action() Ingredient Supplier Changed"); //HELLO REMOVE
            set_Row_Being_Edited();
            return;
        }
        
        //#################################
        // Ingredients Product Name Column
        //#################################
        else if (column_Model == get_ProductName_Col(true))
        {
            String upload_Query = "";
            Object[] params;
            
            if (cell_Value.equals("N/A"))
            {
                upload_Query = """
                        UPDATE  ingredients_in_sections_of_meal
                        SET pdid = ?
                        WHERE ingredients_index = ? AND plan_id = ?;""";
                
                params = new Object[]{
                        new Null_MYSQL_Field(Types.INTEGER), (Integer) ingredient_Index, temp_Plan_ID
                };
            }
            else
            {
                //######################################################
                // Get PDID For Product Name For Ingredient Statement
                //######################################################
                String store_Name = (String) tableModel.getValueAt(row_Model, get_Supplier_Col(true));
                
                String get_PDID_Query = """
                        SELECT pdid
                        FROM
                        (
                             SELECT pdid, product_name, ingredient_id, store_id FROM ingredient_in_shops
                             WHERE ingredient_id = ?
                        ) AS i
                        LEFT JOIN
                        (
                              SELECT store_id, store_name FROM stores
                         ) AS s
                        ON i.store_id = s.store_id
                        WHERE i.product_name = ? AND s.store_name = ?;""";
                
                Object[] store_ID_Params = new Object[]{ ingredient_ID, cell_Value, store_Name };
                
                String error_MSG = "Error, Unable to get Selected Ingredient Shop ID Info!";
                
                ArrayList<Integer> new_PDID_Results = db.get_Single_Col_Query_Int(get_PDID_Query, store_ID_Params, error_MSG);
                
                if (new_PDID_Results == null)
                {
                    // HELLO Create Previous value for supplier column
                    tableModel.setValueAt(previous_ProductName_JC_Item, row_Model, column_Model);
                    
                    set_Row_Being_Edited();
                    return;
                }
                
                //######################################################
                // Create PDID Upload Statement
                //######################################################
                
                // Create  Statement for changing PDID (Ingredient_Index)
                upload_Query = """
                        UPDATE  ingredients_in_sections_of_meal
                        SET pdid = ?
                        WHERE ingredients_index = ? AND plan_id = ?;""";
                
                params = new Object[]{
                        Integer.valueOf(new_PDID_Results.getFirst()), (Integer) ingredient_Index, temp_Plan_ID
                };
                
                
            }
            
            //##################################################################################################
            // Upload Selected Product Name for Ingredient
            //##################################################################################################
            if (! db.upload_Data(upload_Query, params, "Error, Unable to update Ingredient Store Info"))
            {
                // HELLO Create Previous value for supplier column
                tableModel.setValueAt(previous_ProductName_JC_Item, row_Model, column_Model);
                
                set_Row_Being_Edited();
                return;
            }
            
            set_Row_Being_Edited();
            update_Table_Values_By_Quantity(row_Model, ingredient_Index, tableModel.getValueAt(row_Model, get_Quantity_Col(true)));
        }
    }
    
    private void update_Table_Values_By_Quantity(int row, Object ingredients_Index, Object quantity)
    {
        set_Row_Being_Edited();
        
        //####################################################################
        // Updating Quantity Value in temp plan In DB
        //####################################################################
        
        String query1 = """
                UPDATE  ingredients_in_sections_of_meal
                SET quantity = ?
                WHERE plan_id = ?  AND ingredients_index = ?;""";
        
        Object[] params = new Object[]{ (BigDecimal) quantity, temp_Plan_ID, (Integer) ingredients_Index };
        
        if (! (db.upload_Data(query1, params, "Error, unable to change Ingredients Values!")))
        {
            set_Row_Being_Edited();
            return;
        }
        
        //####################################################################
        //  Data Changed in DB Relating To Table
        //####################################################################
        data_Changed_In_Table = true;
        
        //####################################################################
        //  Getting DB data to update Ingredients Table In GUI
        //####################################################################
        String
                query = "SELECT * FROM ingredients_in_sections_of_meal_calculation WHERE ingredients_index = ? AND plan_id = ?;",
                error_MSG = "Error, Updating IngredientTable by Quantity!";
        
        Object[] params2 = new Object[]{ ingredients_Index, temp_Plan_ID };
        
        ArrayList<ArrayList<Object>> ingredients_Update_Data = db.get_2D_Query_AL_Object(query, params2, error_MSG);
        
        if (ingredients_Update_Data == null)
        {
            JOptionPane.showMessageDialog(frame, error_MSG);
            set_Row_Being_Edited();
            return;
        }
        
        //##########################################################################
        //   Updating Ingredients In Meal Table
        //##########################################################################
        
        ArrayList<Object> ingredients_Table_UpdateData = ingredients_Update_Data.getFirst();
        super.update_Table(ingredients_Table_UpdateData, row);
        
        if (tableModel.getValueAt(row, get_IngredientName_Col(true)).equals("None Of The Above"))
        {
            tableModel.setValueAt("No Shop", row, get_Supplier_Col(true));
        }
        
        //##########################################################################
        //   Updating Other Tables
        //##########################################################################
        set_Row_Being_Edited();
        
        update_All_Tables_Data();
    }
    
    //##################################################################################################################
    // Delete Button Methods
    //##################################################################################################################
    public void setup_Delete_Btn_Column(int delete_Btn_Column)
    {
        Action delete = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                JTable table = (JTable) e.getSource();
                CustomTableModel jTable_Model = (CustomTableModel) table.getModel();
                
                Object ingredients_Index = jTable_Model.getValueAt(table.getSelectedRow(), get_IngredientIndex_Col(true));
                
                if (ingredients_Index != null)
                {
                    int model_Row = Integer.parseInt(e.getActionCommand());
                    delete_Row_Action(ingredients_Index, model_Row); // command to update db
                }
            }
        };
        Working_ButtonColumn2 working_Button_Column = new Working_ButtonColumn2(jTable, delete, delete_Btn_Column);
        working_Button_Column.setMnemonic(KeyEvent.VK_D);
    }
    
    protected void delete_Row_Action(Object ingredient_Index, int modelRow)
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
            else // instead of delete last ingredient, replace with ingredient None Of Above
            {
                return;
            }
        }
        
        //#################################################
        // Remove From Row From DB
        //##################################################
        if (ingredient_Index != null)
        {
            //#################################################
            // Delete Ingredient From Temp Meal
            //#################################################
            String query = "DELETE FROM ingredients_in_sections_of_meal WHERE ingredients_index = ? AND plan_id = ?;";
            
            Object[] params = new Object[]{ (Integer) ingredient_Index, temp_Plan_ID };
            
            if (! db.upload_Data(query, params, "Error, Unable to delete Ingredient in Table!")) { return; }
        }
        
        //#################################################
        // Remove From Table
        //##################################################
        tableModel.remove_Row(modelRow);
        resize_Object();
        
        //#################################################
        // Update Table Data
        //##################################################
        update_All_Tables_Data();
    }
    
    public void delete_Table_Action()
    {
        //################################################
        // Delete table from database
        //################################################
         /*
            Delete all ingredients from this meal (using mealID) from table "ingredients_in_meal"
            Delete meal from meals database
         */
        
        String query = "DELETE FROM divided_meal_sections WHERE div_meal_sections_id = ? AND plan_id = ?;";
        
        Object[] params = new Object[]{ sub_Meal_ID, temp_Plan_ID };
        
        if (! db.upload_Data(query, params, "Error, Table Un-Successfully Deleted! ")) { return; }
        
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
    // Button Events
    //##################################################################################################################
    // Add Button
    protected void add_btn_Action()
    {
        //################################################################
        // Check If There Is Already An Empty Row
        //################################################################
        if (is_There_An_Empty_Ingredients(null, "add a new row!"))
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
        String upload_Q1 = """
                INSERT IGNORE INTO ingredients_in_sections_of_meal
                (plan_id, pdid, div_meal_sections_id, ingredient_id, quantity) VALUES
                (?, ?, ?, ?, ?);""";
        
        upload_Queries_And_Params.add(new Pair<>(upload_Q1,
                new Object[]{ temp_Plan_ID, none_Of_The_Above_PDID, sub_Meal_ID, none_Of_The_Above_ID, 0 }));
        
        //######################################
        // 2.) Get Ingredient Index
        //######################################
        String var_Ingredient_ID = "@ingredientIndex";
        String upload_Q2 = String.format("Set %s = LAST_INSERT_ID();", var_Ingredient_ID);
        
        upload_Queries_And_Params.add(new Pair<>(upload_Q2, null));
        
        //#######################################################
        // Fetch Queries
        //#######################################################
        
        // 1.) Get Ingredient ID
        String get_Q1 = String.format("""
                SELECT *
                FROM ingredients_in_sections_of_meal_calculation
                WHERE plan_id = ? AND ingredients_index = %s;""", var_Ingredient_ID);
        fetch_Queries_And_Params.add(new Pair<>(get_Q1, new Object[]{ temp_Plan_ID }));
        
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
            ingredient_DATA = fetched_Results_OBJ.get_Result_1D_AL(0);
            
            System.out.printf("\n\nIngredients Results: \n%s%n", ingredient_DATA);
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s", e);
            return;
        }
        
        //#######################################################
        //   Updating Ingredients With New Ingredients DATA
        //#######################################################
        
        set_Row_Being_Edited(); // stops endless loop being called for all cells being edited
        
        tableModel.add_Row(); // Adding Row Data to Table Model
        int table_Row = get_Rows_In_Table() - 1;
        
        super.update_Table(ingredient_DATA, table_Row);
        
        set_Row_Being_Edited(); // stops endless loop being called for all cells being editted
        
        //#######################################################
        // Resize JTable & GUI with new Data
        //#######################################################
        resize_Object();
        
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
        if (! (transfer_Meal_Data_From_Plans(plan_ID, temp_Plan_ID)))
        {
            JOptionPane.showMessageDialog(frame, "\n\nUnable to transfer ingredients data from  original plan to temp plan!!");
            return;
        }
        
        //#############################
        // Reset Table Info & Data
        //#############################
        refresh_Data(true, true);
    }
    
    public void refresh_Data(boolean update_MacrosLeft_Table, boolean update_TotalMeal_Table)
    {
        //#############################################################################################
        // Reset Variable
        //#############################################################################################
        data_Changed_In_Table = false;
        
        if (is_Table_Deleted())  // If Meal was previously deleted reset variables & state
        {
            unHide_Ingredients_Table();
        }
        
        //##############################################################################################
        // Reset Table Model data
        //#############################################################################################
        refresh_Data();
        
        //#############################################################################################
        // Reset Meal Total  Table Data
        //#############################################################################################
        if (update_TotalMeal_Table)
        {
            mealManager.update_MealManager_DATA(true, true);
        }
        
        //#############################################################################################
        // Update Other Tables Data
        //#############################################################################################
        if (update_MacrosLeft_Table)
        {
            mealManager.update_MacrosLeft_Table();
        }
    }
    
    //###################################################
    // Save Button
    //###################################################
    public boolean save_Btn_Action(boolean show_Message)
    {
        //######################################################################
        // Transfer Data from temp plan to origin plan
        //######################################################################
        if (! (transfer_Meal_Data_From_Plans(temp_Plan_ID, plan_ID)))     // If Meal Not In Original PlanID Add To PlanID
        {
            if (show_Message)
            {
                JOptionPane.showMessageDialog(frame, "\n\nUnable to transfer ingredients data from temp to original plan ");
            }
            return false;
        }
        
        //######################################################################
        // Update Table Model
        //######################################################################
        save_Data();
        
        //######################################################################
        // Success Message
        //######################################################################
        if (show_Message)
        {
            JOptionPane.showMessageDialog(frame, "Table Successfully Updated!");
        }
        
        //#############################################################################################
        // Reset Variable
        //#############################################################################################
        meal_In_DB = true;
        data_Changed_In_Table = false;
        
        //#############################################################################################
        
        return true;
    }
    
    //################################################
    // Events For Refresh & Save
    //################################################
    public boolean transfer_Meal_Data_From_Plans(int from_Plan_ID, int to_Plan_ID)
    {
        //########################################################
        // Clear Old Data from toPlan and & Temp Tables
        //########################################################
        // Delete tables if they already exist
        String query0 = "DROP TABLE IF EXISTS temp_ingredients_in_meal;";
        
        // Delete ingredients in meal Data from original plan with this mealID
        String query1 = """
                DELETE FROM ingredients_in_sections_of_meal
                WHERE div_meal_sections_id = ? AND plan_id = ?;""";
        
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
                INSERT IGNORE INTO divided_meal_sections
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
                FROM ingredients_in_sections_of_meal i
                WHERE i.div_meal_sections_id = ? AND i.plan_id = ?;""";
        
        String query5 = "UPDATE temp_ingredients_in_meal  SET plan_id = ?;";
        
        String query6 = "INSERT INTO ingredients_in_sections_of_meal SELECT * FROM temp_ingredients_in_meal;";
        
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
        
        if (! (db.upload_Data_Batch(queries_And_Params, error_MSG))) { return false; }
        
        //####################################################
        // Output
        //####################################################
        System.out.printf("\nMealIngredients Successfully Transferred! \n\n%s", line_Separator);
        return true;
    }
    
    //##################################################################################################################
    // JCombo Box Classes & Setups
    //##################################################################################################################
    public class SetupProduct_NameColumn
    {
        public SetupProduct_NameColumn(int col)
        {
            TableColumn tableColumn = jTable.getColumnModel().getColumn(col);
            
            //Set up the editor for the sport cells.
            tableColumn.setCellEditor(new ComboEditor());
            
            //Set up tool tips for the sport cells.
            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer()
            {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                                                               boolean isSelected, boolean hasFocus, int row, int column)
                {
                    JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    label.setIcon(UIManager.getIcon("Table.descendingSortIcon"));
                    return label;
                }
            };
            
            renderer.setToolTipText("Click to see suppliers who sell this ingredient!");
            tableColumn.setCellRenderer(renderer);
        }
        
        class ComboEditor extends DefaultCellEditor
        {
            DefaultComboBoxModel model1;
            JComboBox comboBox;
            
            public ComboEditor()
            {
                super(new JComboBox());
                model1 = (DefaultComboBoxModel) ((JComboBox) getComponent()).getModel();
                
                comboBox = ((JComboBox) getComponent());
                comboBox.setEditable(true);
                
                comboBox.addItemListener(new ItemListener()
                {
                    public void itemStateChanged(ItemEvent ie)
                    {
                        if (ie.getStateChange() == ItemEvent.DESELECTED)
                        {
                            previous_ProductName_JC_Item = ie.getItem();
                        }
                        if (ie.getStateChange() == ItemEvent.SELECTED)
                        {
                            selected_ProductName_JC_Item = ie.getItem();
                        }
                    }
                });
                
                
                //######################################################
                // Centre ComboBox Items
                //######################################################
                ((JLabel) comboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
                
                //######################################################
                // Make JComboBox Visible
                //######################################################
                ComboBoxTableCellRenderer renderer = new ComboBoxTableCellRenderer();
                
                renderer.setModel(model1);
                
                TableColumn tableColumn = jTable.getColumnModel().getColumn(get_ProductName_Col(false));
                tableColumn.setCellRenderer(renderer);
            }
            
            
            //First time the cell is created
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
            {
                //########################################
                // Get Previous Stored Item
                ////######################################
                model1.removeAllElements();
                
                Integer ingredientID = (Integer) tableModel.getValueAt(row, get_IngredientID_Col(true));
                String ingredientSupplier = (String) tableModel.getValueAt(row, get_Supplier_Col(true));
                
                //########################################
                // Get product names Based on store selected
                //######################################
                
                String
                        errorMSG = "Error, Unable to Query Ingredient Product Names!",
                        query_store_name = """
                                SELECT IFNULL(S.product_name, 'N/A') AS product_name
                                FROM
                                (
                                    SELECT ingredient_id FROM ingredients_info
                                ) AS I
                                LEFT JOIN
                                (
                                    SELECT ingredient_id, product_name, store_id  FROM ingredient_in_shops
                                ) AS S
                                ON I.ingredient_id = S.ingredient_id
                                AND S.store_id = (SELECT store_id FROM stores WHERE store_name = ? )
                                WHERE I.ingredient_id = ?
                                ORDER BY S.product_name ASC;""";
                
                Object[] store_namee_Params = new Object[]{ ingredientSupplier, ingredientID };
                
                ArrayList<String> productNameResults = db.get_Single_Col_Query_String(query_store_name, store_namee_Params, errorMSG);
                
                if (productNameResults != null)
                {
                    for (String productName : productNameResults)
                    {
                        model1.addElement(productName);
                    }
                }
                else
                {
                    model1.addElement("N/A");
                }
                
                return super.getTableCellEditorComponent(table, value, isSelected, row, column);
            }
        }
    }
    
    public class SetupSupplierColumn
    {
        public SetupSupplierColumn(int col)
        {
            TableColumn tableColumn = jTable.getColumnModel().getColumn(col);
            
            //Set up the editor for the sport cells.
            tableColumn.setCellEditor(new ComboEditor());
            
            //Set up tool tips for the sport cells.
            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer()
            {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                                                               boolean isSelected, boolean hasFocus, int row, int column)
                {
                    JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    label.setIcon(UIManager.getIcon("Table.descendingSortIcon"));
                    return label;
                }
            };
            
            renderer.setToolTipText("Click to see suppliers who sell this ingredient!");
            tableColumn.setCellRenderer(renderer);
        }
        
        class ComboEditor extends DefaultCellEditor
        {
            DefaultComboBoxModel model1;
            JComboBox comboBox;
            
            public ComboEditor()
            {
                super(new JComboBox());
                model1 = (DefaultComboBoxModel) ((JComboBox) getComponent()).getModel();
                
                comboBox = ((JComboBox) getComponent());
                comboBox.setEditable(true);
                
                comboBox.addItemListener(new ItemListener()
                {
                    public void itemStateChanged(ItemEvent ie)
                    {
                        if (ie.getStateChange() == ItemEvent.DESELECTED)
                        {
                            previous_Supplier_JC_Item = ie.getItem();
                        }
                        if (ie.getStateChange() == ItemEvent.SELECTED)
                        {
                            selected_Supplier_JC_Item = ie.getItem();
                        }
                    }
                });
                
                
                //######################################################
                // Centre ComboBox Items
                //######################################################
                ((JLabel) comboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
                
                //######################################################
                // Make JComboBox Visible
                //######################################################
                
                ComboBoxTableCellRenderer renderer = new ComboBoxTableCellRenderer();
                
                renderer.setModel(model1);
                
                TableColumn tableColumn = jTable.getColumnModel().getColumn(get_Supplier_Col(false));
                tableColumn.setCellRenderer(renderer);
            }
            
            
            //First time the cell is created
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
            {
                // HELLO
            /*
               save previous option then set it to it if it exists in list
             */
                //########################################
                // Get Previous Stored Item
                //########################################
                model1.removeAllElements();
                
                Integer ingredientID = (Integer) tableModel.getValueAt(row, get_IngredientID_Col(true));
                String ingredientName = (String) tableModel.getValueAt(row, get_IngredientName_Col(true));
                
                //########################################
                // Get Supplier Based on ingredientIndex
                //########################################
                String errorMSG_Store = "Error, Setting Available Stores for Ingredient!";
                
                String query_Store = """
                        SELECT DISTINCT IFNULL(D.store_name, 'N/A') AS store_name
                        FROM
                        (
                        	SELECT ingredient_id FROM ingredients_info
                        ) AS T
                        LEFT JOIN
                        (
                           SELECT ingredient_id, store_id FROM ingredient_in_shops
                        ) AS C
                        ON T.ingredient_id = C.ingredient_id
                        LEFT JOIN
                        (
                          SELECT store_id, store_name FROM stores
                        ) AS D
                        ON C.store_id = D.store_id
                        WHERE T.ingredient_id = ?
                        ORDER BY D.store_name ASC;""";
                
                Object[] store_Params = new Object[]{ ingredientID };
                
                ArrayList<String> storesResults = db.get_Single_Col_Query_String(query_Store, store_Params, errorMSG_Store);
                
                if (storesResults != null)
                {
                    boolean NA_in_List = false;
                    for (String store : storesResults)
                    {
                        model1.addElement(store);
                        if (store.equals("N/A"))
                        {
                            NA_in_List = true;
                        }
                        //System.out.printf("\n\n%s", store); //HELLO Remove
                    }
                    
                    if (! (ingredientName.equals("None Of The Above")) && ! NA_in_List)
                    {
                        model1.addElement("N/A");
                    }
                }
                else
                {
                    //HELLO FIX WILL SOMEHOW CAUSE ERROR
                    JOptionPane.showMessageDialog(frame, errorMSG_Store);
                }
                
                return super.getTableCellEditorComponent(table, value, isSelected, row, column);
            }
        }
    }
    
    public class SetupIngredientTypeColumn
    {
        DefaultComboBoxModel model1;
        JComboBox comboBox;
        
        public SetupIngredientTypeColumn(int col)
        {
            TableColumn tableColumn = jTable.getColumnModel().getColumn(col);
            
            //Set up the editor for the sport cells.
            tableColumn.setCellEditor(new ComboEditor());
            
            //Set up tool tips for the sport cells.
            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer()
            {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                                                               boolean isSelected, boolean hasFocus, int row, int column)
                {
                    JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    label.setIcon(UIManager.getIcon("Table.descendingSortIcon"));
                    return label;
                }
            };
            
            renderer.setToolTipText("Change ingredient type to search for new ingredients in the IngredientsName column!");
            tableColumn.setCellRenderer(renderer);
        }
        
        class ComboEditor extends DefaultCellEditor
        {
            public ComboEditor()
            {
                super(new JComboBox());
                model1 = (DefaultComboBoxModel) ((JComboBox) getComponent()).getModel();
                
                comboBox = ((JComboBox) getComponent());
                comboBox.setEditable(true);
                
                comboBox.addItemListener(new ItemListener()
                {
                    public void itemStateChanged(ItemEvent ie)
                    {
                        if (ie.getStateChange() == ItemEvent.DESELECTED)
                        {
                            previous_IngredientType_JC_Item = ie.getItem();
                        }
                        if (ie.getStateChange() == ItemEvent.SELECTED)
                        {
                            selected_IngredientType_JC_Item = ie.getItem();
                        }
                    }
                });
                
                //######################################################
                // Centre ComboBox Items
                //######################################################
                ((JLabel) comboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
                
                //######################################################
                // Make JComboBox Visible
                //######################################################
                
                ComboBoxTableCellRenderer renderer = new ComboBoxTableCellRenderer();
                
                renderer.setModel(model1);
                
                TableColumn tableColumn = jTable.getColumnModel().getColumn(get_IngredientType_Col(false));
                tableColumn.setCellRenderer(renderer);
            }
            
            
            //First time the cell is created
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
            {
                //########################################
                // Get Previous Stored Item
                ////######################################
                
                model1.removeAllElements();
                
                for (String key : map_ingredient_Types_To_Names.keySet())
                {
                    model1.addElement(key);
                }
                
                return super.getTableCellEditorComponent(table, value, isSelected, row, column);
            }
        }
        
        public DefaultComboBoxModel getModel()
        {
            return model1;
        }
        
        public JComboBox getComboBox()
        {
            return comboBox;
        }
    }
    
    public class SetupIngredientNameColumn
    {
        DefaultComboBoxModel model1;
        JComboBox comboBox;
        
        public SetupIngredientNameColumn(int col)
        {
            TableColumn tableColumn = jTable.getColumnModel().getColumn(col);
            
            //Set up the editor for the sport cells.
            tableColumn.setCellEditor(new ComboEditor());
            
            //Set up tool tips for the sport cells.
            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer()
            {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                                                               boolean isSelected, boolean hasFocus, int row, int column)
                {
                    JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    label.setIcon(UIManager.getIcon("Table.descendingSortIcon"));
                    return label;
                }
            };
            
            renderer.setToolTipText("Click to select an ingredient!");
            tableColumn.setCellRenderer(renderer);
        }
        
        class ComboEditor extends DefaultCellEditor
        {
            public ComboEditor()
            {
                super(new JComboBox());
                model1 = (DefaultComboBoxModel) ((JComboBox) getComponent()).getModel();
                
                comboBox = ((JComboBox) getComponent());
                comboBox.setEditable(true);
                
                comboBox.addItemListener(new ItemListener()
                {
                    public void itemStateChanged(ItemEvent ie)
                    {
                        if (ie.getStateChange() == ItemEvent.DESELECTED)
                        {
                            previous_IngredientName_JC_Item = ie.getItem();
                        }
                        if (ie.getStateChange() == ItemEvent.SELECTED)
                        {
                            ingredient_Name_Changed = true;
                            //System.out.printf("\n\nIngredientName itemStateChanged() Item Selected \ningredientNameChanged: %s", ingredientNameChanged); //HELLO REMOVE
                            selected_IngredientName_JC_Item = ie.getItem();
                        }
                    }
                });
                
                
                //######################################################
                // Centre ComboBox Items
                //######################################################
                ((JLabel) comboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
                
                //######################################################
                // Make JComboBox Visible
                //######################################################
                
                ComboBoxTableCellRenderer renderer = new ComboBoxTableCellRenderer();
                
                renderer.setModel(model1);
                
                TableColumn tableColumn = jTable.getColumnModel().getColumn(get_IngredientName_Col(false));
                tableColumn.setCellRenderer(renderer);
            }
            
            
            //First time the cell is created
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
            {
                //########################################
                // Get Previous Stored Item
                ////######################################
                
                model1.removeAllElements();
                
                String ingredientType = table.getValueAt(row, get_IngredientType_Col(false)).toString();
                
                for (String item : map_ingredient_Types_To_Names.get(ingredientType))
                {
                    model1.addElement(item);
                }
                
                return super.getTableCellEditorComponent(table, value, isSelected, row, column);
            }
        }
        
        public DefaultComboBoxModel getModel()
        {
            return model1;
        }
        
        public JComboBox getComboBox()
        {
            return comboBox;
        }
    }
    
    //#######################################################################################
    public class ComboBoxTableCellRenderer extends JComboBox implements TableCellRenderer
    {
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            setSelectedItem(value);
            return this;
        }
        
    }
    //#######################################################################################
    
    /**
     * http://www.camick.com/java/source/ButtonColumn.java
     * <p>
     * The Working_ButtonColumn2 class provides a renderer and an editor that looks like a
     * JButton. The renderer and editor will then be used for a specified column
     * in the table. The TableModel will contain the String to be displayed on
     * the button.
     * <p>
     * The button can be invoked by a mouse click or by pressing the space bar
     * when the cell has focus. Optionally a mnemonic can be set to invoke the
     * button. When the button is invoked the provided Action is invoked. The
     * source of the Action will be the table. The action command will contain
     * the model row number of the button that was clicked.
     */
    
    protected class Working_ButtonColumn2 extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener, MouseListener
    {
        private JTable table;
        private Action action;
        private int mnemonic;
        private Border originalBorder;
        private Border focusBorder;
        
        private JButton renderButton;
        private JButton editButton;
        private Object editorValue;
        private boolean isButtonColumnEditor;
        
        /**
         * Create the Working_ButtonColumn2 to be used as a renderer and editor. The
         * renderer and editor will automatically be installed on the TableColumn
         * of the specified column.
         *
         * @param table  the table containing the button renderer/editor
         * @param action the Action to be invoked when the button is invoked
         * @param column the column to which the button renderer/editor is added
         */
        public Working_ButtonColumn2(JTable table, Action action, int column)
        {
            this.table = table;
            this.action = action;
            
            renderButton = new JButton();
            editButton = new JButton();
            editButton.setFocusPainted(false);
            editButton.addActionListener(this);
            originalBorder = editButton.getBorder();
            setFocusBorder(new LineBorder(Color.BLUE));
            
            TableColumnModel columnModel = table.getColumnModel();
            columnModel.getColumn(column).setCellRenderer(this);
            columnModel.getColumn(column).setCellEditor(this);
            table.addMouseListener(this);
        }
        
        
        /**
         * Get foreground color of the button when the cell has focus
         *
         * @return the foreground color
         */
        public Border getFocusBorder()
        {
            return focusBorder;
        }
        
        /**
         * The foreground color of the button when the cell has focus
         *
         * @param focusBorder the foreground color
         */
        public void setFocusBorder(Border focusBorder)
        {
            this.focusBorder = focusBorder;
            editButton.setBorder(focusBorder);
        }
        
        public int getMnemonic()
        {
            return mnemonic;
        }
        
        /**
         * The mnemonic to activate the button when the cell has focus
         *
         * @param mnemonic the mnemonic
         */
        public void setMnemonic(int mnemonic)
        {
            this.mnemonic = mnemonic;
            renderButton.setMnemonic(mnemonic);
            editButton.setMnemonic(mnemonic);
        }
        
        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column)
        {
            if (value == null)
            {
                editButton.setText("");
                editButton.setIcon(null);
            }
            else if (value instanceof Icon)
            {
                editButton.setText("");
                editButton.setIcon((Icon) value);
            }
            else
            {
                editButton.setText(value.toString());
                editButton.setIcon(null);
            }
            
            this.editorValue = value;
            return editButton;
        }
        
        @Override
        public Object getCellEditorValue()
        {
            return editorValue;
        }
        
        //
        //  Implement TableCellRenderer interface
        //
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            if (isSelected)
            {
                renderButton.setForeground(table.getSelectionForeground());
                renderButton.setBackground(table.getSelectionBackground());
            }
            else
            {
                renderButton.setForeground(table.getForeground());
                renderButton.setBackground(UIManager.getColor("Button.background"));
            }
            
            if (hasFocus)
            {
                renderButton.setBorder(focusBorder);
            }
            else
            {
                renderButton.setBorder(originalBorder);
            }
            
            //		renderButton.setText( (value == null) ? "" : value.toString() );
            if (value == null)
            {
                renderButton.setText("");
                renderButton.setIcon(null);
            }
            else if (value instanceof Icon)
            {
                renderButton.setText("");
                renderButton.setIcon((Icon) value);
            }
            else
            {
                renderButton.setText(value.toString());
                renderButton.setIcon(null);
            }
            
            return renderButton;
        }
        
        //
        //  Implement ActionListener interface
        //
        /*
         *	The button has been pressed. Stop editing and invoke the custom Action
         */
        public void actionPerformed(ActionEvent e)
        {
            int row = table.getEditingRow();
            
            fireEditingStopped();
            
            //  Invoke the Action
            
            ActionEvent event = new ActionEvent(
                    table,
                    ActionEvent.ACTION_PERFORMED,
                    "" + row);
            action.actionPerformed(event);
            
        }
        
        //
        //  Implement MouseListener interface
        //
        /*
         *  When the mouse is pressed the editor is invoked. If you then then drag
         *  the mouse to another cell before releasing it, the editor is still
         *  active. Make sure editing is stopped when the mouse is released.
         */
        public void mousePressed(MouseEvent e)
        {
            if (table.isEditing()
                    && table.getCellEditor() == this)
            {
                isButtonColumnEditor = true;
            }
        }
        
        public void mouseReleased(MouseEvent e)
        {
            if (isButtonColumnEditor
                    && table.isEditing())
            {
                table.getCellEditor().stopCellEditing();
            }
            
            isButtonColumnEditor = false;
        }
        
        public void mouseClicked(MouseEvent e)
        {
        }
        
        public void mouseEntered(MouseEvent e)
        {
        }
        
        public void mouseExited(MouseEvent e)
        {
        }
    }
    
    //##################################################################################################################
    // Update Other Table Methods
    //##################################################################################################################
    private void update_All_Tables_Data()
    {
        mealManager.update_MealManager_DATA(true, true);
        mealManager.update_MacrosLeft_Table();
    }
    
    //##################################################################################################################
    // Mutator Methods
    //##################################################################################################################
    private void set_Row_Being_Edited()
    {
        is_row_Being_Edited = ! is_row_Being_Edited; // flip it
    }
    
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
    
    public Integer get_Meal_In_Plan_ID()
    {
        return meal_In_Plan_ID;
    }
    
    public boolean is_Table_Deleted()
    {
        return object_Deleted;
    }
    
    private boolean is_There_An_Empty_Ingredients(Integer row_Triggered_At, String attempting_To)
    {
        if (get_Rows_In_Table() == 0) { return false; }
        
        //Checking if the ingredient None Of the Above is already in the table
        for (int row = 0; row < get_Rows_In_Table(); row++)
        {
            // Currently, changing  ingredient to NONE OF the ABOVE
            if (row_Triggered_At != null && row == row_Triggered_At)
            {
                continue;
            }
            
            // if None Of  the above is found in the table return true
            if (tableModel.getValueAt(row, get_IngredientID_Col(true)).equals(1) || tableModel.getValueAt(row, get_IngredientID_Col(true)).equals("1"))
            {
                String message = String.format("""
                        \n\nPlease change the Ingredient at:
                        \nRow: %s \nColumn: %s
                        \nFrom the ingredient 'None Of The Above' to another ingredient!
                        \nBefore attempting to %s!
                        """, row + 1, get_IngredientName_Col(false) + 1, attempting_To);
                JOptionPane.showMessageDialog(frame, message);
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
    
    private void set_Model_IngredientID_Col(int index)
    {
        model_Ingredient_ID_Col = index;
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
    
    private void set_Model_Supplier_Col(int index)
    {
        model_Supplier_Col = index;
    }
    
    private void set_Model_ProductName_Col(int index) { model_ProductName_Col = index; }
    
    protected void set_Model_DeleteBTN_Col(int index)
    {
        model_DeleteBTN_Col = index;
    }
    
    //#############################################################
    // Accessor For Table Column Positions
    //#############################################################
    private int get_IngredientIndex_Col(Boolean model_Index)
    {
        if (model_Index) { return model_Ingredient_Index_Col; }
        
        return jTable.convertColumnIndexToView(model_Ingredient_Index_Col);
    }
    
    private int get_IngredientID_Col(Boolean model_Index)
    {
        if (model_Index) { return model_Ingredient_ID_Col; }
        
        return jTable.convertColumnIndexToView(model_Ingredient_ID_Col);
    }
    
    private int get_Quantity_Col(Boolean model_Index)
    {
        if (model_Index) { return model_Quantity_Col; }
        
        return jTable.convertColumnIndexToView(model_Quantity_Col);
    }
    
    private int get_IngredientType_Col(Boolean model_Index)
    {
        if (model_Index) { return model_Ingredient_Type_Col; }
        
        return jTable.convertColumnIndexToView(model_Ingredient_Type_Col);
    }
    
    private int get_IngredientName_Col(Boolean model_Index)
    {
        if (model_Index) { return model_Ingredient_Name_Col; }
        
        return jTable.convertColumnIndexToView(model_Ingredient_Name_Col);
    }
    
    private int get_Supplier_Col(Boolean model_Index)
    {
        if (model_Index) { return model_Supplier_Col; }
        
        return jTable.convertColumnIndexToView(model_Supplier_Col);
    }
    
    private int get_ProductName_Col(Boolean model_Index)
    {
        if (model_Index) { return model_ProductName_Col; }
        
        return jTable.convertColumnIndexToView(model_ProductName_Col);
    }
    
    private Integer get_DeleteBTN_Col(Boolean model_Index)
    {
        if (model_Index) { return model_DeleteBTN_Col; }
        
        return jTable.convertColumnIndexToView(model_DeleteBTN_Col);
    }
    
    //##################################################################################################################
}
