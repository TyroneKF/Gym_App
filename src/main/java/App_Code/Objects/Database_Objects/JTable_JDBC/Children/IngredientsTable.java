package App_Code.Objects.Database_Objects.JTable_JDBC.Children;


import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JTable_JDBC.JDBC_JTable;
import App_Code.Objects.Database_Objects.MealManager;
import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Gui_Objects.IconPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.*;

public class IngredientsTable extends JDBC_JTable
{
    //#################################################################################################################
    // Collections
    //#################################################################################################################
    private TreeMap<String, Collection<String>> map_ingredientTypesToNames;
    private ArrayList<Integer> triggerColumns;
    
    //#################################################################################################################
    // Objects
    //#################################################################################################################
    private JPanel spaceDivider;
    private MealManager mealManager;
    private Frame frame;
    
    //#################################################################################################################
    // Other Variables
    //#################################################################################################################
    private Integer planID, temp_PlanID = 1, mealInPlanID, divMealSectionsID;
    private String mealName;
    
    private boolean
            dataChangedInTable = false,
            rowBeingEdited = false,
            meal_In_DB,
            objectDeleted = false,
            ingredientNameChanged = false;
    
    private int
            model_ingredientsIndex_Col,
            model_ingredientID_Col,
            model_Quantity_Col,
            model_ingredientType_Col,
            model_IngredientName_Col,
            model_Supplier_Col,
            model_ProductName_Col,
            model_DeleteBTN_Col;
    
    private final int NoneOfTheAbove_PDID = 1;
    
    //SupplierName JComboBox Variables
    private Object
            previous_ProductName_JComboItem, selected_ProductName_JComboItem,
            previous_Supplier_JComboItem, selected_Supplier_JCombo_Item,
            previous_IngredientName_JComboItem, selected_IngredientName_JCombo_Item,
            previous_IngredientType_JComboItem, selected_IngredientType_JComboItem;
    
    private String lineSeparator = "###############################################################################";
    
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    // Ingredients Table
    
    /**
     * @param db
     * @param data
     * @param columnNames
     * @param planID
     * @param mealInPlanID
     * @param meal_In_DB
     * @param mealName
     * @param tableName
     * @param unEditableColumns
     * @param colAvoidCentering
     */
    public IngredientsTable(MyJDBC db, MealManager mealManager, ArrayList<ArrayList<Object>> data, ArrayList<String> columnNames, int planID,
                            Integer mealInPlanID, Integer divMealSectionsID, boolean meal_In_DB, String mealName, String tableName,
                            ArrayList<String> unEditableColumns, ArrayList<String> colAvoidCentering, ArrayList<String> columnsToHide, JPanel spaceDivider)
    {
        super(db, mealManager.getCollapsibleCenterJPanel(), true, true, tableName, data, columnNames, unEditableColumns, colAvoidCentering, columnsToHide);
        
        //##############################################################
        // Other Variables
        //##############################################################
        this.mealName = mealName;
        this.planID = planID;
        this.mealInPlanID = mealInPlanID;
        this.divMealSectionsID = divMealSectionsID;
        
        this.meal_In_DB = meal_In_DB;
        
        this.mealManager = mealManager;
        this.parentContainer = mealManager.getCollapsibleCenterJPanel();
        
        this.spaceDivider = spaceDivider;
        
        this.map_ingredientTypesToNames = mealManager.getMap_ingredientTypesToNames();
        this.frame = mealManager.getFrame();
        
        //##############################################################
        // Setting Trigger Columns
        //##############################################################
        
        // Table : ingredients_in_sections_of_meal_calculation
        set_Model_IngredientIndex_Col(columnNamesAndPositions.get("ingredients_index")[0]);
        set_Model_IngredientID_Col(columnNamesAndPositions.get("ingredient_id")[0]);
        set_Model_Quantity_Col(columnNamesAndPositions.get("quantity")[0]);
        set_Model_IngredientType_Col(columnNamesAndPositions.get("ingredient_type")[0]);
        set_Model_IngredientName_Col(columnNamesAndPositions.get("ingredient_name")[0]);
        set_Model_Supplier_Col(columnNamesAndPositions.get("supplier")[0]);
        set_Model_ProductName_Col(columnNamesAndPositions.get("product_name")[0]);
        set_Model_DeleteBTN_Col(columnNamesAndPositions.get("delete button")[0]);
        
        //##############################################################
        // Setting Trigger Columns
        //##############################################################
        
        this.triggerColumns = new ArrayList(Arrays.asList(
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
        setupDeleteBtnColumn(get_DeleteBTN_Col(false));
        
        //##############################################################
        // Table Customization
        //##############################################################
        setOpaque(true); //content panes must be opaque
        setTableHeaderFont(new Font("Dialog", Font.BOLD, 12)); // Ingredients_In_Meal_Calculation Customisation
        
        //##############################################################
        // Add Ingredient If Meal Empty / Add New Meal
        //##############################################################
        if (data.size() == 0)
        {
            add_btn_Action();// Add Ingredient to JTable
        }
    }
    
    //##################################################################################################################
    // Table Setup
    //##################################################################################################################
    @Override
    protected void extraTableModel_Setup()
    {
        // Setup Specials sections in table
        new SetupIngredientTypeColumn(get_IngredientType_Col(false));
        new SetupIngredientNameColumn(get_IngredientName_Col(false));
        new SetupSupplierColumn(get_Supplier_Col(false));
        new SetupProduct_NameColumn(get_ProductName_Col(false));
        
        setupDeleteBtnColumn(get_DeleteBTN_Col(false)); // specifying delete column
    }
    
    @Override
    protected void extra_TableSetup()
    {
        iconSetup();
    }
    
    protected void iconSetup()
    {
        //###################################################################################
        // Table Icon Setup
        //###################################################################################
        int iconSize = 20;
        
        IconPanel iconPanel = new IconPanel(3, 10, "East");
        JPanel iconPanelInsert = iconPanel.getIconJpanel();
        
        addToContainer(this, iconPanel.getIconAreaPanel(), 0, 0, 1, 1, 0.25, 0.25, "horizontal", "east");
        
        //##########################
        //Add BTN
        //##########################
        IconButton add_Icon_Btn = new IconButton("/images/add/add.png", iconSize, iconSize, iconSize, iconSize, "centre", "right");
        // add_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));
        
        JButton add_Btn = add_Icon_Btn.returnJButton();
        add_Icon_Btn.setToolTipText("Add Ingredients"); //Hover message over icon
        add_Icon_Btn.makeBTntransparent();
        
        add_Btn.addActionListener(ae -> {
            
            add_btn_Action();
        });
        
        iconPanelInsert.add(add_Icon_Btn);
        
        //##########################
        // Refresh Icon
        //##########################
        
        IconButton refresh_Icon_Btn = new IconButton("/images/refresh/+refresh.png", iconSize, iconSize, iconSize, iconSize,
                "centre", "right"); // btn text is useless here , refactor
        //refresh_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));
        
        
        JButton refresh_Btn = refresh_Icon_Btn.returnJButton();
        refresh_Btn.setToolTipText("Restore Sub-Meal"); //Hover message over icon
        refresh_Icon_Btn.makeBTntransparent();
        
        refresh_Btn.addActionListener(ae -> {
            
            //#######################################################
            // Ask For Permission
            //#######################################################
            if (! (areYouSure("Refresh Data")))
            {
                return;
            }
            
            //#######################################################
            //  Delete meal if not in DB
            //#######################################################
            if (! (getMealInDB())) // Delete Meal if not in DB
            {
                if (areYouSure(" ' Refresh Data' this meal isn't saved and will result in this meal being deleted "))
                {
                    deleteTableAction();
                    completely_Delete_IngredientsJTable();
                    mealManager.removeIngredientsTable(this); // Remove Ingredients Table from memory
                }
                return;
            }
            
            //#######################################################
            //  Refresh Meal
            //#######################################################
            refresh_Btn_Action();
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
        save_btn.setToolTipText("Save Sub-Meal"); //Hover message over icon
        
        save_btn.addActionListener(ae -> {
            if (areYouSure("Save Data"))
            {
                save_Btn_Action(true);
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
        delete_btn.setToolTipText("Delete Sub-Meal"); //Hover message over icon
        
        
        delete_btn.addActionListener(ae -> {
            if (areYouSure("Delete"))
            {
                deleteTableAction();
            }
        });
        
        iconPanelInsert.add(delete_btn);
    }
    
    //##################################################################################################################
    // Data Changing In Cells Action
    //##################################################################################################################
    //HELLO!! FIX Editing Now
    private boolean hasDataChangedInCell(int col, Object ingredientsIndex, Object cellValue)
    {
        //######################################################
        // Get Table Data
        //######################################################
        ArrayList<ArrayList<Object>> tableData = getSavedData();
        
        //######################################################
        // Get Index Of IngredientsIndex in Original Data
        //######################################################
        Integer indexOf_IngredientsIndex = null;
        
        for (Integer pos = 0; pos < columnNames.size(); pos++)
        {
            if (columnNames.get(pos).equals("ingredients_index"))
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
    protected void tableDataChange_Action(TableModelEvent evt)
    {
        int rowModel = evt.getFirstRow(), columnModel = evt.getColumn();
        
        //#############################################################################################################
        // Check if cell that triggered this event can execute
        //#############################################################################################################
        // Avoids endless loop / if edited cell column, is supposed to a trigger an action
        if (rowBeingEdited || triggerColumns == null || ! (triggerColumns.contains(columnModel))) { return; }
        
        //#############################################################################################################
        // Variables
        //#############################################################################################################
        setRowBeingEdited();
        
        Object ingredientID = tableModel.getValueAt(rowModel, get_IngredientID_Col(true));
        
        Object ingredientIndex = tableModel.getValueAt(rowModel, get_IngredientIndex_Col(true));
        
        Object cellValue = tableModel.getValueAt(rowModel, columnModel);
        
        //#############################################################################################################
        // Check if CellData has changed
        //#############################################################################################################
        
        if (! (hasDataChangedInCell(columnModel, ingredientIndex, cellValue)))
        {
            setRowBeingEdited();
            return;
        }
        
        //#############################################################################################################
        // Trigger Columns
        //#############################################################################################################
        
        // // Ingredients Type Column
        if (columnModel == get_IngredientType_Col(true))
        {
            System.out.printf("\n\n@tableDataChange_Action() Ingredient Type Changed"); //HELLO REMOVE
            setRowBeingEdited();
            return;
        }
        
        //###############################
        // Ingredients Quantity Column
        // ###############################
        if (columnModel == get_Quantity_Col(true) && tableModel.getValueAt(rowModel, columnModel) == null)
        {
            JOptionPane.showMessageDialog(frame, String.format("\n\nPlease insert a reasonable 'Quantity' value in the cell at: \n\nRow: %s \nColumn: %s", rowModel + 1, columnModel + 1));
            
            cellValue = 0.00;
            setRowBeingEdited(); // re-triggers this method on quantity changed
            
            tableModel.setValueAt(cellValue, rowModel, columnModel);
        }
        
        else if (columnModel == get_Quantity_Col(true))
        {
            System.out.printf("\ntableDataChange_Action() Quantity Being Changed");
            setRowBeingEdited();// HELLO
            
            updateTableValuesByQuantity(rowModel, ingredientIndex, cellValue);
            return;
        }
        
        //###############################
        // Ingredients Name Column
        //###############################
        else if (columnModel == get_IngredientName_Col(true))
        {
            System.out.printf("\n\n@tableDataChange_Action() Ingredient Name Changed");
            
            // if the same item is selected avoid processing
            if (! (ingredientNameChanged))
            {
                System.out.printf("\n\nExit No Update Ingredient Name Changed \ningredientNameChanged: %s", ingredientNameChanged);
                setRowBeingEdited();
                return;
            }
            
            if (selected_IngredientName_JCombo_Item.equals("None Of The Above"))
            {
                if (getNonOfTheAboveInTableStatus(rowModel, "change a current Ingredient in this meal to 'None Of The Above'"))
                {
                    tableModel.setValueAt(previous_IngredientName_JComboItem, rowModel, columnModel);
                    setRowBeingEdited();
                    return;
                }
            }
            
            //##################################################################################################
            // Get Chosen Ingredient ID For Chosen Item (Ingredient Name)
            //##################################################################################################
            
            String query = String.format("Select ingredient_id From ingredients_info WHERE ingredient_name = '%s';", selected_IngredientName_JCombo_Item);
            System.out.printf("\n\n Query:\n\n %s", query);
            
            ArrayList<ArrayList<Object>> results_Ingredient_ID = db.get_Multi_ColumnQuery_Object(query);
            
            if (results_Ingredient_ID == null)
            {
                JOptionPane.showMessageDialog(frame, "Unable to retrieve chosen Ingredient ID from DB!");
                
                // Change Jtable JComboBox Back To Original Value
                tableModel.setValueAt(previous_IngredientName_JComboItem, rowModel, columnModel);
                setRowBeingEdited();
                return;
            }
            
            Object selected_Ingredient_ID = results_Ingredient_ID.get(0).get(0);
            
            System.out.printf("\nPrevious JCombo Value: %s \nPrevious JCombo  ID: %s \n\nSelected JCombo Value: %s\nSelected JCombo ID: %s" +
                            "\n\nRow  Selected: %s \nColumn Selected: %s",
                    previous_IngredientName_JComboItem, ingredientID, selected_IngredientName_JCombo_Item, selected_Ingredient_ID, rowModel, columnModel);
            System.out.println("\n\n#########################################################################");
            
            //##################################################################################################
            // Get Rid off of old chosen product_name
            //##################################################################################################
            
            String uploadQuery = String.format("""
                    UPDATE  ingredients_in_sections_of_meal
                    SET ingredient_id = %s, 
                    pdid = NULL
                    WHERE ingredients_index = %s AND plan_id = %s; """, selected_Ingredient_ID, ingredientIndex, temp_PlanID);
            
            // Upload IngredientName & NEW PDID
            
            if (! (db.uploadData_Batch_Altogether(new String[]{ uploadQuery })))
            {
                JOptionPane.showMessageDialog(frame, "\n\n ERROR:\n\nUnable to update Ingredient In DB!");
                
                // Change Jtable JComboBox Back To Original Value
                tableModel.setValueAt(previous_IngredientName_JComboItem, rowModel, columnModel);
                
                setRowBeingEdited();
                return;
            }
            
            //###################################
            // Update  Other Table Values
            //###################################
            setRowBeingEdited(); //HELLO
            
            updateTableValuesByQuantity(rowModel, ingredientIndex, tableModel.getValueAt(rowModel, get_Quantity_Col(true)));
            return;
        }
        
        //###############################
        // Ingredients Supplier Column
        //###############################
        else if (columnModel == get_Supplier_Col(true))
        {
            System.out.printf("\n\n@tableDataChange_Action() Ingredient Supplier Changed"); //HELLO REMOVE
            setRowBeingEdited();
            return;
        }
        
        //#################################
        // Ingredients Product Name Column
        //#################################
        else if (columnModel == get_ProductName_Col(true))
        {
            String uploadQuery = "";
            
            if (cellValue.equals("N/A"))
            {
                uploadQuery = String.format("""
                        UPDATE  ingredients_in_sections_of_meal
                        SET pdid = NULL
                        WHERE ingredients_index = %s AND plan_id = %s; """, ingredientIndex, temp_PlanID);
            }
            else if (! (cellValue.equals("N/A")))
            {
                //######################################################
                // Get PDID For Product Name For Ingredient Statement
                //######################################################
                Object storeName = tableModel.getValueAt(rowModel, get_Supplier_Col(true));
                String getPDIDQuery = String.format("""
                        SELECT pdid
                        FROM
                        (
                             SELECT pdid, product_name, ingredient_id, store_id FROM ingredient_in_shops 
                             WHERE ingredient_id = %s
                        ) AS i
                        LEFT JOIN
                        (
                              SELECT store_id, store_name FROM stores
                         ) AS s
                        ON i.store_id = s.store_id
                        WHERE i.product_name = '%s' AND s.store_name = '%s';""", ingredientID, cellValue, storeName);
                
                ArrayList<String> newPDIDResults = db.getSingleColumnQuery_ArrayList(getPDIDQuery);
                if (newPDIDResults == null)
                {
                    JOptionPane.showMessageDialog(frame, "\n\n ERROR:\n\nUnable to retrieve  Ingredient In Shop PDID info!");
                    
                    // HELLO Create Previous value for supplier column
                    tableModel.setValueAt(previous_ProductName_JComboItem, rowModel, columnModel);
                    
                    setRowBeingEdited();
                    return;
                }
                
                //######################################################
                // Create PDID Upload Statement
                //######################################################
                
                // Create  Statement for changing PDID (Ingredient_Index)
                uploadQuery = String.format("""
                        UPDATE  ingredients_in_sections_of_meal
                        SET pdid = %s
                        WHERE ingredients_index = %s AND plan_id = %s;""", newPDIDResults.get(0), ingredientIndex, temp_PlanID);
                
                System.out.printf("\n\nQUERY PDID: \n'''%s''' \n\nPDID = %s \n\nUpload Query \n'''%s'''", getPDIDQuery, newPDIDResults.get(0), uploadQuery);
            }
            else
            {
                // Create  Statement for changing PDID (Ingredient_Index)
                uploadQuery = String.format("""
                        UPDATE  ingredients_in_sections_of_meal
                        SET pdid = NULL
                        WHERE ingredients_index = %s AND plan_id = %s;""", ingredientIndex, temp_PlanID);
            }
            
            //##################################################################################################
            // Upload Selected Product Name for Ingredient
            //##################################################################################################
            if (! (db.uploadData_Batch_Altogether(new String[]{ uploadQuery })))
            {
                JOptionPane.showMessageDialog(frame, "\n\n ERROR:\n\nUnable to update Ingredient Store In DB!");
                
                // HELLO Create Previous value for supplier column
                tableModel.setValueAt(previous_ProductName_JComboItem, rowModel, columnModel);
                
                setRowBeingEdited();
                return;
            }
            
            setRowBeingEdited();
            updateTableValuesByQuantity(rowModel, ingredientIndex, tableModel.getValueAt(rowModel, get_Quantity_Col(true)));
            return;
        }
    }
    
    private void updateTableValuesByQuantity(int row, Object ingredients_Index, Object quantity)
    {
        setRowBeingEdited();
        
        //####################################################################
        // Updating Quantity Value in temp plan In DB
        //####################################################################
        
        String query1 = String.format("""
                UPDATE  ingredients_in_sections_of_meal
                SET quantity = %s 
                WHERE plan_id = %s  AND ingredients_index = %s;""", quantity, temp_PlanID, ingredients_Index);
        
        //HELLO DELETE
        System.out.printf("\n\nupdateTableValuesByQuantity() \nQuery: \n\n%s", query1);
        
        if (! (db.uploadData_Batch_Altogether(new String[]{ query1 })))
        {
            JOptionPane.showMessageDialog(frame, "Un-able to Update row based on cell value!");
            
            setRowBeingEdited();
            return;
        }
        
        //####################################################################
        //  Data Changed in DB Relating To Table
        //####################################################################
        dataChangedInTable = true;
        
        //####################################################################
        //  Getting DB data to update Ingredients Table In GUI
        //####################################################################
        
        String query = String.format("SELECT * FROM ingredients_in_sections_of_meal_calculation WHERE ingredients_index = %s AND plan_id = %s;", ingredients_Index, temp_PlanID);
        
        // HELLO REMOVE
        System.out.printf("\n\n%s", query);
        
        ArrayList<ArrayList<Object>> ingredientsUpdateData = db.getTableDataObject_AL(query, tableName);
        
        System.out.printf("\n\n\n\nUpdate DATA: \n%s\n\n", ingredientsUpdateData);  // HELLO REMOVE
        
        if (ingredientsUpdateData == null)
        {
            JOptionPane.showMessageDialog(frame, "ERROR updateTableValuesByQuantity(): Un-able to Update Ingredient in table row!");
            
            setRowBeingEdited();
            return;
        }
        
        //##########################################################################
        //   Updating Ingredients In Meal Table
        //##########################################################################
        
        ArrayList<Object> ingredientsTable_UpdateData = ingredientsUpdateData.get(0);
        super.updateTable(ingredientsTable_UpdateData, row);
        
        if (tableModel.getValueAt(row, get_IngredientName_Col(true)).equals("None Of The Above"))
        {
            tableModel.setValueAt("No Shop", row, get_Supplier_Col(true));
        }
        
        //##########################################################################
        //   Updating Other Tables
        ///##########################################################################
        setRowBeingEdited();
        
        updateAllTablesData();
    }
    
    //##################################################################################################################
    // Delete Button Methods
    //##################################################################################################################
    public void setupDeleteBtnColumn(int deleteBtnColumn)
    {
        Action delete = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                JTable table = (JTable) e.getSource();
                CustomTableModel jTableModel = (CustomTableModel) table.getModel();
                
                Object ingredients_Index = jTableModel.getValueAt(table.getSelectedRow(), get_IngredientIndex_Col(true));
                
                if (ingredients_Index != null)
                {
                    int modelRow = Integer.parseInt(e.getActionCommand());
                    deleteRowAction(ingredients_Index, modelRow); // command to update db
                }
            }
        };
        Working_ButtonColumn2 workingButtonColumn = new Working_ButtonColumn2(jTable, delete, deleteBtnColumn);
        workingButtonColumn.setMnemonic(KeyEvent.VK_D);
    }
    
    protected void deleteRowAction(Object ingredientIndex, int modelRow)
    {
        //#################################################
        // Can't have an empty Table
        //##################################################
        if (getRowsInTable() == 1)
        {
            String question = """
                    \n\nThere is only 1  ingredient in this subMeal!
                                        
                    If you delete this ingredient, this table will also be deleted.
                                       
                    Would you still like to proceed?""";
            
            int reply = JOptionPane.showConfirmDialog(null, question, "Delete Ingredients", JOptionPane.YES_NO_OPTION); //HELLO Edit
            
            if (reply == JOptionPane.YES_OPTION)
            {
                deleteTableAction();
            }
            else if (reply == JOptionPane.NO_OPTION) // instead of delete last ingredient, replace with ingredient None Of Above
            {
                return;
            }
        }
        
        //#################################################
        // Remove From Row From DB
        //##################################################
        if (ingredientIndex != null)
        {
            //#################################################
            // Delete Ingredient From Temp Meal
            //#################################################
            
            System.out.printf("\n\nDeleting Row in table %s \ningredientIndex: %s | meal_in_plan_id: %s", mealName, ingredientIndex, mealInPlanID);
            
            String query = String.format("DELETE FROM ingredients_in_sections_of_meal WHERE ingredients_index = %s AND plan_id = %s;", ingredientIndex, temp_PlanID);
            
            String[] queryUpload = new String[]{ query };
            
            if (! (db.uploadData_Batch_Altogether(queryUpload)))
            {
                JOptionPane.showMessageDialog(frame, "Unable To delete Ingredient from Meal in Database");
                return;
            }
        }
        
        //#################################################
        // Remove From Table
        //##################################################
        tableModel.removeRow(modelRow);
        resizeObject();
        
        //#################################################
        // Update Table Data
        //##################################################
        updateAllTablesData();
    }
    
    public void deleteTableAction()
    {
        //################################################
        // Delete table from database
        //################################################

         /*
            Delete all ingredients from this meal (using mealID) from table "ingredients_in_meal"
            Delete meal from meals database
         */
        
        String query1 = "SET FOREIGN_KEY_CHECKS = 0;"; // Disable Foreign Key Checks
        String query2 = String.format("DELETE FROM ingredients_in_sections_of_meal WHERE div_meal_sections_id = %s AND plan_id = %s;", divMealSectionsID, temp_PlanID);
        String query4 = String.format("DELETE FROM divided_meal_sections WHERE div_meal_sections_id = %s AND plan_id = %s;", divMealSectionsID, temp_PlanID);
        String query5 = "SET FOREIGN_KEY_CHECKS = 1;"; // Enable Foreign Key Checks
        
        if (! db.uploadData_Batch_Altogether(new String[]{ query1, query2, query4, query5 }))
        {
            JOptionPane.showMessageDialog(frame, "Table Un-Successfully Deleted! ");
            return;
        }
        
        //################################################
        // Hide JTable object & Collapsible OBJ
        //################################################
        hideIngredientsTable();
        
        //################################################
        // Update MacrosLeft Table & TotalMeal Table
        //################################################
        updateAllTablesData();
        
        //################################################
        // Tell MealManager This Table Has Been Deleted
        //################################################
        mealManager.ingredientsTableHasBeenDeleted();
        
        //################################################
        // Progress Message
        //################################################
        JOptionPane.showMessageDialog(frame, "Table Successfully Deleted!");
    }
    
    public void completely_Delete_IngredientsJTable()
    {
        // Hide Ingredients Table
        hideIngredientsTable();
        
        // remove JTable from GUI
        parentContainer.remove(this);
        
        // remove Space Divider
        parentContainer.remove(spaceDivider);
        
        // Tell Parent container to resize
        parentContainer.revalidate();
    }
    
    public void setVisibility(boolean condition)
    {
        this.setVisible(condition);
        spaceDivider.setVisible(condition);
    }
    
    private void hideIngredientsTable()
    {
        setVisibility(false); // hide collapsible Object
        setObjectDeleted(true);
    }
    
    private void unHideIngredientsTable()
    {
        setVisibility(true); // hide collapsible Object
        setObjectDeleted(false); // set this object as deleted
    }
    
    //##################################################################################################################
    // Button Events
    //##################################################################################################################
    // Add Button
    protected void add_btn_Action()
    {
        //#########################################################
        // Check If There Is Already An Empty Row
        //#########################################################
        if (getNonOfTheAboveInTableStatus(null, "add a new row!"))
        {
            return;
        }
        
        //#########################################################
        // Get Next Ingredients_Index For This Ingredient Addition
        //#########################################################
        String getNextIndexQuery = "SELECT IFNULL(MAX(`ingredients_index`),0) + 1 AS nextId FROM `ingredients_in_sections_of_meal`;";
        
        String[] newIngredientsIndex = db.getSingleColumnQuery(getNextIndexQuery);
        
        if (newIngredientsIndex == null)
        {
            JOptionPane.showMessageDialog(frame, "Unable to create new ingredient in table! \nUnable to generate ingredients_index!!");
            return;
        }
        
        int newIngredientsIndex2 = Integer.parseInt(newIngredientsIndex[0]);
        
        //#########################################################
        // Insert into Database
        //#########################################################
        int ingredientID = 1;
        BigDecimal quantity = new BigDecimal("0.00");
        
        String query1 = String.format("""
                INSERT INTO ingredients_in_sections_of_meal
                (ingredients_index, div_meal_sections_id, plan_id, ingredient_id, quantity, pdid)                                        
                VALUES (%s, %s, %s, %s, %s, %s);""", newIngredientsIndex2, divMealSectionsID, temp_PlanID, ingredientID, quantity, NoneOfTheAbove_PDID);
        
        if (! (db.uploadData_Batch_Altogether(new String[]{ query1 })))
        {
            JOptionPane.showMessageDialog(frame, "Un-able to Insert new row into the Database!");
            return;
        }
        
        //#########################################################
        //  Getting Row Data For New Ingredient Addition
        //#########################################################
        String query = String.format("""
                SELECT * FROM ingredients_in_sections_of_meal_calculation
                WHERE ingredients_index = %s AND plan_id = %s;""", newIngredientsIndex2, temp_PlanID);
        
        ArrayList<ArrayList<Object>> results = db.getTableDataObject_AL(query, tableName);
        
        if (results == null)
        {
            JOptionPane.showMessageDialog(frame, "ERROR add_btn_Action(): Un-able to get Ingredient info for row in table!");
            return;
        }
        
        //##############################################################################################################
        //   Updating Ingredients In Meal Table
        //##############################################################################################################
        
        setRowBeingEdited(); // stops endless loop being called for all cells being editted
        
        //#########################################################
        // Adding Row Data to Table Model
        //#########################################################
        tableModel.addRow();
        int tableRow = getRowsInTable() - 1;
        
        ArrayList<Object> ingredientsTable_UpdateData = results.get(0);
        super.updateTable(ingredientsTable_UpdateData, tableRow);
        
        setRowBeingEdited(); // stops endless loop being called for all cells being editted
        
        //##############################################################################################################
        // Resize JTable & GUI with new Data
        //##############################################################################################################
        resizeObject();
        
        //##################################################################################
        // Update Table Data
        //###################################################################################
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
        if (! (transferMealDataToPlan(planID, temp_PlanID)))
        {
            JOptionPane.showMessageDialog(frame, "\n\nUnable to transfer ingredients data from  original plan to temp plan!!");
            return;
        }
        
        //#############################
        // Reset Table Info & Data
        //#############################
        reloadingDataFromRefresh(true, true);
    }
    
    public void reloadingDataFromRefresh(boolean updateMacrosLeftTable, boolean updateTotalMealTable)
    {
        //#############################################################################################
        // Reset Variable
        //#############################################################################################
        dataChangedInTable = false;
        
        if (hasIngredientsTableBeenDeleted())  // If Meal was previously deleted reset variables & state
        {
            unHideIngredientsTable();
        }
        
        //##############################################################################################
        // Reset Table Model data
        ///#############################################################################################
        refreshData();
        
        //#############################################################################################
        // Reset Meal Total  Table Data
        //#############################################################################################
        if (updateTotalMealTable)
        {
            mealManager.update_MealManager_DATA(true, true);
        }
        
        //#############################################################################################
        // Update Other Tables Data
        //#############################################################################################
        if (updateMacrosLeftTable)
        {
            mealManager.update_MacrosLeft_Table();
        }
    }
    
    //###################################################
    // Save Button
    //###################################################
    public boolean save_Btn_Action(boolean showMessage)
    {
        //######################################################################
        // Transfer Data from temp plan to origin plan
        //######################################################################
        if (! (transferMealDataToPlan(temp_PlanID, planID)))     // If Meal Not In Original PlanID Add To PlanID
        {
            if (showMessage)
            {
                JOptionPane.showMessageDialog(frame, "\n\nUnable to transfer ingredients data from temp to original plan ");
            }
            return false;
        }
        
        //######################################################################
        // Update Table Model
        //######################################################################
        savedData();
        
        //######################################################################
        // Success Message
        //######################################################################
        if (showMessage)
        {
            JOptionPane.showMessageDialog(frame, "Table Successfully Updated!");
        }
        
        //#############################################################################################
        // Reset Variable
        //#############################################################################################
        meal_In_DB = true;
        dataChangedInTable = false;
        
        //#############################################################################################
        
        return true;
    }
    
    //################################################
    // Events For Refresh & Save
    //################################################
    public boolean transferMealDataToPlan(int fromPlanID, int toPlanID)
    {
        // Transferring this Meals Info from one plan to another
        
        //########################################################
        // Clear Old Data from toPlan and & Temp Tables
        //########################################################
        
        // Delete tables if they already exist
        String query0 = "DROP TABLE IF EXISTS temp_ingredients_in_meal;";
        
        String query1 = "SET FOREIGN_KEY_CHECKS = 0;"; // Disable Foreign Key Checks
        
        // Delete ingredients in meal Data from original plan with this mealID
        String query2 = String.format("DELETE FROM ingredients_in_sections_of_meal WHERE div_meal_sections_id = %s AND plan_id = %s;", divMealSectionsID, toPlanID);
        
        String query3 = "SET FOREIGN_KEY_CHECKS = 1;"; // Enable Foreign Key Checks
        
        //########################################################
        // Insert Meal & dividedMealSections If Not in DB In toPlan
        //########################################################
        // insert meal if it does not exist inside toPlanID
        String query4 = String.format("""
                INSERT IGNORE INTO meals_in_plan
                (meal_in_plan_id, plan_id, meal_name)                                
                VALUES
                (%s, %s, '%s');""", mealInPlanID, toPlanID, mealName);
        
        String query5 = String.format("""
                INSERT IGNORE INTO divided_meal_sections
                (div_meal_sections_id, meal_in_plan_id, plan_id)            
                VALUES
                (%s, %s, '%s'); """, divMealSectionsID, mealInPlanID, toPlanID);
        
        //####################################################
        // Transferring this plans Ingredients to Temp-Plan
        //####################################################
        
        // Create Table to transfer ingredients from original plan to temp
        String query6 = String.format("""                                     
                CREATE table temp_ingredients_in_meal  AS
                SELECT i.*
                FROM ingredients_in_sections_of_meal i                                                       
                WHERE i.div_meal_sections_id = %s AND i.plan_id = %s;""", divMealSectionsID, fromPlanID);
        
        String query7 = String.format("UPDATE temp_ingredients_in_meal  SET plan_id = %s;", toPlanID);
        
        String query8 = "INSERT INTO ingredients_in_sections_of_meal SELECT * FROM temp_ingredients_in_meal;";
        
        //####################################################
        // Update
        //####################################################
        String[] query_Temp_Data = new String[]{ query0, query1, query2, query3, query4, query5, query6, query7, query8 };
        
        if (! (db.uploadData_Batch_Altogether(query_Temp_Data)))
        {
            JOptionPane.showMessageDialog(frame, "\n\ntransferMealIngredients() Cannot Create Temporary Plan In DB to Allow Editing");
            return false;
        }
        
        System.out.printf("\nMealIngredients Successfully Transferred! \n\n%s", lineSeparator);
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
                            previous_ProductName_JComboItem = ie.getItem();
                        }
                        if (ie.getStateChange() == ItemEvent.SELECTED)
                        {
                            selected_ProductName_JComboItem = ie.getItem();
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
                
                Object ingredientID = tableModel.getValueAt(row, get_IngredientID_Col(true));
                Object ingredientSupplier = tableModel.getValueAt(row, get_Supplier_Col(true));
                
                //########################################
                // Get product names Based on store selected
                ////######################################
                
                String queryStore = String.format("""
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
                        AND S.store_id = (SELECT store_id FROM stores WHERE store_name = '%s')
                        WHERE I.ingredient_id = %s
                        ORDER BY S.product_name ASC;""", ingredientSupplier, ingredientID);
                
                ArrayList<String> productNameResults = db.getSingleColumnQuery_ArrayList(queryStore);
                
                //HELLO REMOVE
                String seperator = "#######################################################################";
                System.out.printf("\n\n%s \n\nQuery: \n%s \n\nList Of Available Shops:\n%s", seperator, queryStore, productNameResults);
                
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
                            previous_Supplier_JComboItem = ie.getItem();
                        }
                        if (ie.getStateChange() == ItemEvent.SELECTED)
                        {
                            selected_Supplier_JCombo_Item = ie.getItem();
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
                ////######################################
                
                model1.removeAllElements();
                
                Object ingredientID = tableModel.getValueAt(row, get_IngredientID_Col(true));
                Object ingredientName = tableModel.getValueAt(row, get_IngredientName_Col(true));
                
                //########################################
                // Get Supplier Based on ingredientIndex
                ////######################################
                
                String queryStore = String.format("""
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
                        WHERE T.ingredient_id = %s
                        ORDER BY D.store_name ASC;""", ingredientID);
                
                ArrayList<String> storesResults = db.getSingleColumnQuery_ArrayList(queryStore);
                
                //HELLO REMOVE
                
                String seperator = "#######################################################################";
                System.out.printf("\n\n%s \n\nQuery: \n%s \n\nList Of Available Shops:\n\n%s", seperator, queryStore, storesResults);
                
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
                    JOptionPane.showMessageDialog(frame, "\n\nError \nSetting Available Stores for Ingredient!");
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
                            previous_IngredientType_JComboItem = ie.getItem();
                        }
                        if (ie.getStateChange() == ItemEvent.SELECTED)
                        {
                            selected_IngredientType_JComboItem = ie.getItem();
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
                
                for (String key : map_ingredientTypesToNames.keySet())
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
                            previous_IngredientName_JComboItem = ie.getItem();
                        }
                        if (ie.getStateChange() == ItemEvent.SELECTED)
                        {
                            ingredientNameChanged = true;
                            //System.out.printf("\n\nIngredientName itemStateChanged() Item Selected \ningredientNameChanged: %s", ingredientNameChanged); //HELLO REMOVE
                            selected_IngredientName_JCombo_Item = ie.getItem();
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
                
                for (String item : map_ingredientTypesToNames.get(ingredientType))
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
    private void updateAllTablesData()
    {
        mealManager.update_MealManager_DATA(true, true);
        mealManager.update_MacrosLeft_Table();
    }
    
    //##################################################################################################################
    // Mutator Methods
    //##################################################################################################################
    private void setRowBeingEdited()
    {
        rowBeingEdited = ! rowBeingEdited; // flip it
    }
    
    private void setObjectDeleted(boolean deleted)
    {
        objectDeleted = deleted;
    }
    
    public void set_Meal_In_DB(boolean mealInDB)
    {
        meal_In_DB = mealInDB;
    }
    
    //##################################################################################################################
    // Accessor Methods
    //##################################################################################################################
    public String getMealName()
    {
        return mealName;
    }
    
    public boolean getMealInDB()
    {
        return meal_In_DB;
    }
    
    public Integer getDivMealSectionsID()
    {
        return divMealSectionsID;
    }
    
    public Integer getMealInPlanID()
    {
        return mealInPlanID;
    }
    
    public boolean hasIngredientsTableBeenDeleted()
    {
        return objectDeleted;
    }
    
    private boolean getNonOfTheAboveInTableStatus(Integer rowTriggeredAt, String attemptingTo)
    {
        if (getRowsInTable() == 0) { return false; }
        
        //Checking if the ingredient None Of the Above is already in the table
        for (int row = 0; row < getRowsInTable(); row++)
        {
            // Currently, changing  ingredient to NONE OF the ABOVE
            if (rowTriggeredAt != null && row == rowTriggeredAt)
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
                        """, row + 1, get_IngredientName_Col(false) + 1, attemptingTo);
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
        model_ingredientsIndex_Col = index;
    }
    
    private void set_Model_IngredientID_Col(int index)
    {
        model_ingredientID_Col = index;
    }
    
    private void set_Model_Quantity_Col(int index)
    {
        model_Quantity_Col = index;
    }
    
    private void set_Model_IngredientType_Col(int index)
    {
        model_ingredientType_Col = index;
    }
    
    private void set_Model_IngredientName_Col(int index)
    {
        model_IngredientName_Col = index;
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
    private int get_IngredientIndex_Col(Boolean modelIndex)
    {
        if (modelIndex) { return model_ingredientsIndex_Col; }
        
        return jTable.convertColumnIndexToView(model_ingredientsIndex_Col);
    }
    
    private int get_IngredientID_Col(Boolean modelIndex)
    {
        if (modelIndex) { return model_ingredientID_Col; }
        
        return jTable.convertColumnIndexToView(model_ingredientID_Col);
    }
    
    private int get_Quantity_Col(Boolean modelIndex)
    {
        if (modelIndex) { return model_Quantity_Col; }
        
        return jTable.convertColumnIndexToView(model_Quantity_Col);
    }
    
    private int get_IngredientType_Col(Boolean modelIndex)
    {
        if (modelIndex) { return model_ingredientType_Col; }
        
        return jTable.convertColumnIndexToView(model_ingredientType_Col);
    }
    
    private int get_IngredientName_Col(Boolean modelIndex)
    {
        if (modelIndex) { return model_IngredientName_Col; }
        
        return jTable.convertColumnIndexToView(model_IngredientName_Col);
    }
    
    private int get_Supplier_Col(Boolean modelIndex)
    {
        if (modelIndex) { return model_Supplier_Col; }
        
        return jTable.convertColumnIndexToView(model_Supplier_Col);
    }
    
    private int get_ProductName_Col(Boolean modelIndex)
    {
        if (modelIndex) { return model_ProductName_Col; }
        
        return jTable.convertColumnIndexToView(model_ProductName_Col);
    }
    
    private Integer get_DeleteBTN_Col(Boolean modelIndex)
    {
        if (modelIndex) { return model_DeleteBTN_Col; }
        
        return jTable.convertColumnIndexToView(model_DeleteBTN_Col);
    }
    
    //##################################################################################################################
}
