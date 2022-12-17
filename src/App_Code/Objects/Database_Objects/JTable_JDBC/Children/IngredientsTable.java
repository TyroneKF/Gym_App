package App_Code.Objects.Database_Objects.JTable_JDBC.Children;


import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JTable_JDBC.JDBC_JTable;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.Children.MacrosLeftTable;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.Children.TotalMealTable.TotalMealTable;
import App_Code.Objects.Gui_Objects.CollapsibleJPanel;
import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Gui_Objects.IconPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.*;

public class IngredientsTable extends JDBC_JTable
{
    private TreeMap<String, Collection<String>> map_ingredientTypesToNames;

    //#################################################################################################################
    // Objects
    //#################################################################################################################
    private MacrosLeftTable macrosLeft_Table;
    private TotalMealTable total_Meal_Table;

    private JPanel spaceDivider;
    private CollapsibleJPanel collapsibleObj;

    //#################################################################################################################
    // Other Variables
    //#################################################################################################################
    private Integer planID, temp_PlanID = 1, mealInPlanID, divMealSectionsID;
    private String mealName;

    private ArrayList<Integer> triggerColumns;

    private boolean
            dataChangedInTable = false,
            rowBeingEdited = false,
            meal_In_DB = true,
            objectDeleted = false,
            ingredientNameChanged = false;

    private int
            ingredientsTable_Index_Col,
            ingredientsTable_ID_Col,
            ingredientsTable_Quantity_Col,
            ingredientsTable_Type_Col,
            ingredientsTable_IngredientsName_Col,
            ingredientsTable_Supplier_Col;

    private final int NoneOfTheAbove_PDID = 1;

    //SupplierName JComboBox Variables
    private Object
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
     * @param collapsibleObj
     * @param map_ingredientTypesToNames
     * @param databaseName
     * @param data
     * @param columnNames
     * @param planID
     * @param mealInPlanID
     * @param meal_In_DB
     * @param mealName
     * @param tableName
     * @param unEditableColumns
     * @param colAvoidCentering
     * @param total_Meal_Table
     * @param macrosLeft_Table
     *
     */
    public IngredientsTable(MyJDBC db, CollapsibleJPanel collapsibleObj, TreeMap<String, Collection<String>> map_ingredientTypesToNames,
                            String databaseName, Object[][] data, String[] columnNames, int planID,
                            Integer mealInPlanID, Integer divMealSectionsID, boolean meal_In_DB, String mealName, String tableName,
                            ArrayList<String> unEditableColumns, ArrayList<String> colAvoidCentering,
                            ArrayList<String> columnsToHide,
                            TotalMealTable total_Meal_Table, MacrosLeftTable macrosLeft_Table)
    {
        super(db, collapsibleObj.getCentreJPanel(), true, databaseName, tableName, data, columnNames, unEditableColumns, colAvoidCentering, columnsToHide);

        //##############################################################
        // Other Variables
        //##############################################################
        this.mealName = mealName;
        this.planID = planID;
        this.mealInPlanID = mealInPlanID;
        this.divMealSectionsID = divMealSectionsID;

        this.map_ingredientTypesToNames = map_ingredientTypesToNames;
        this.meal_In_DB = meal_In_DB;

        this.collapsibleObj = collapsibleObj;

        this.total_Meal_Table = total_Meal_Table;
        this.macrosLeft_Table = macrosLeft_Table;

        //##############################################################
        // Setting Trigger Columns
        //##############################################################
        set_IngredientsTable_Index_Col(columnNamesAndPositions.get("Ingredients_Index")[1]);
        set_IngredientsTable_ID_Col(columnNamesAndPositions.get("IngredientID")[1]);
        set_IngredientsTable_Quantity_Col(columnNamesAndPositions.get("Quantity")[1]);
        set_IngredientsTable_IngredientType_Col(columnNamesAndPositions.get("Ingredient_Type")[1]);
        set_IngredientsTable_IngredientsName_Col(columnNamesAndPositions.get("Ingredient_Name")[1]);
        set_IngredientsTable_Supplier_Col(columnNamesAndPositions.get("Supplier")[1]);

        set_IngredientsTable_DeleteBTN_Col(columnNamesAndPositions.get("Delete Button")[1]);

        //##############################################################
        // Setting Trigger Columns
        //##############################################################

        this.triggerColumns = new ArrayList(Arrays.asList(getIngredientsTable_Index_Col(), getIngredientsTable_ID_Col(),
                getIngredientsTable_Quantity_Col(), getIngredientsTable_Type_Col(), getIngredientsTable_IngredientsName_Col(), getIngredientsTable_Supplier_Col()));

        //##############################################################
        // Setting Up JComboBox Fields on Table
        //##############################################################
        new SetupIngredientTypeColumn(getIngredientsTable_Type_Col());
        new SetupIngredientNameColumn(getIngredientsTable_IngredientsName_Col());
        new SetupSupplierColumn(getIngredientsTable_Supplier_Col());

        //##############################################################
        // Setting Up Delete Button On JTable
        //##############################################################
        setupDeleteBtnColumn(columnNamesAndPositions.get("Delete Button")[1]);

        //##############################################################
        // Add Ingredient If Meal Empty / Add New Meal
        //##############################################################
        if (!(meal_In_DB))
        {
            addIngredient();
        }
    }

    //##################################################################################################################
    // Table Setup
    //##################################################################################################################

    @Override
    protected void extraTableModel_Setup()
    {
        if (deleteColumn!=null)
        {
            setupDeleteBtnColumn(deleteColumn); // specifying delete column
        }

        // Setting up JcomboBox Field
        for (Integer key : jComboMap.keySet())
        {
            setUpJComboColumn(key, "IngredientName", jComboMap.get(key));
        }
    }

    @Override
    protected void tableModel_Setup(Object[][] data, String[] columnNames)
    {
        tableModel = new DefaultTableModel(data, columnNames)
        {
            @Override
            public boolean isCellEditable(int row, int col)
            {
                //Note that the data/cell address is constant,
                //no matter where the cell appears onscreen.
                if (unEditableColumns.contains(col))
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @Override
            public Class getColumnClass(int c)
            {
                return getValueAt(0, c).getClass();
            }
        };

        tableModel.addTableModelListener(
                evt -> tableDataChange_Action());

        jTable.setModel(tableModel);

        rowsInTable = data.length;

        //#################################################################################
        // Table Personalisation
        //#################################################################################

        //initColumnSizes();
        setCellsAlignment(0, colAvoidCentering);

        if (getTableInitialised())  //first time this method is called, special columns aren't defined
        {
            if (getColumnsToHide()!=null)//Must be first
            {
                SetUp_HiddenTableColumns(getColumnsToHide());
            }

            //EDITING
            new SetupIngredientTypeColumn(getIngredientsTable_Type_Col());
            new SetupIngredientNameColumn(getIngredientsTable_IngredientsName_Col());
            new SetupSupplierColumn(getIngredientsTable_Supplier_Col());

            setupDeleteBtnColumn(getDeleteBTN_Col()); // specifying delete column

        }
        else
        {
            setTableInitialized();
        }
        resizeObject();
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
                IconButton add_Icon_Btn = new IconButton("src/images/add/add.png", "", iconSize, iconSize, iconSize, iconSize, "centre", "right");
        // add_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));

        JButton add_Btn = add_Icon_Btn.returnJButton();
        add_Icon_Btn.makeBTntransparent();

        add_Btn.addActionListener(ae -> {

            add_btn_Action();
        });

        iconPanelInsert.add(add_Icon_Btn);

        //##########################
        // Refresh Icon
        //##########################

        IconButton refresh_Icon_Btn = new IconButton("src/images/refresh/+refresh.png", "", iconSize, iconSize, iconSize, iconSize,
                "centre", "right"); // btn text is useless here , refactor
        //refresh_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));


        JButton refresh_Btn = refresh_Icon_Btn.returnJButton();
        refresh_Icon_Btn.makeBTntransparent();

        refresh_Btn.addActionListener(ae -> {

            //#######################################################
            // Ask For Permission
            //#######################################################

            if (areYouSure("Refresh Data"))
            {
                refresh_Btn_Action(true);
            }
        });

        iconPanelInsert.add(refresh_Icon_Btn);

        //##########################
        // Update Icon
        //##########################

        IconButton saveIcon_Icon_Btn = new IconButton("src/images/save/save.png", "", iconSize, iconSize, iconSize, iconSize,
                "centre", "right"); // btn text is useless here , refactor
        //saveIcon_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));
        saveIcon_Icon_Btn.makeBTntransparent();

        JButton save_btn = saveIcon_Icon_Btn.returnJButton();


        save_btn.addActionListener(ae -> {
            if (areYouSure("Save Data"))
            {
                saveDataAction(true);
            }
        });

        iconPanelInsert.add(save_btn);

        //##########################
        // Delete Icon
        //##########################

        IconButton deleteIcon_Icon_Btn = new IconButton("src/images/delete/+delete.png", "", iconSize, iconSize, iconSize+10, iconSize,
                "centre", "right"); // btn text is useless here , refactor
        //deleteIcon_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));
        deleteIcon_Icon_Btn.makeBTntransparent();

        JButton delete_btn = deleteIcon_Icon_Btn.returnJButton();


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
        Object[][] tableData = getData();

        //######################################################
        // Get Index Of IngredientsIndex in Original Data
        //######################################################
        Integer indexOf_IngredientsIndex = null;

        for (Integer pos = 0; pos < columnNames.length; pos++)
        {
            if (columnNames[pos].equals("Ingredients_Index"))
            {
                indexOf_IngredientsIndex = pos;
                break;
            }
            pos++;
        }


        //######################################################
        for (Object[] rowData : tableData)
        {

        }

        //######################################################
        return true;
    }

    //Editing Now
    @Override
    protected void tableDataChange_Action()
    {
        int rowEdited = jTable.getEditingRow(), columnEdited = jTable.getEditingColumn();

        //#############################################################################################################
        // Check if cell that triggered this event can execute
        //#############################################################################################################
        // Avoids endless loop / if edited cell column, is supposed to a trigger an action
        if (rowBeingEdited || triggerColumns==null || !(triggerColumns.contains(columnEdited)))
        {
            //HELLO REMOVE
            // System.out.printf("\nExited tableDataChange_Action() Row: %s, Column: %s", rowEdited, columnEdited);
            return;
        }

        //#############################################################################################################
        // Variables
        //#############################################################################################################
        setRowBeingEdited();

        Object ingredientID = jTable.getValueAt(rowEdited, getIngredientsTable_ID_Col());

        Object ingredientIndex = jTable.getValueAt(rowEdited, getIngredientsTable_Index_Col());

        Object cellValue = jTable.getValueAt(rowEdited, columnEdited);

        //#############################################################################################################
        // Check if CellData has changed
        //#############################################################################################################

        if (!(hasDataChangedInCell(columnEdited, ingredientIndex, cellValue)))
        {
            setRowBeingEdited();
            return;
        }

        //#############################################################################################################
        // Trigger Columns
        //#############################################################################################################

        // // Ingredients Type Column
        if (columnEdited==getIngredientsTable_Type_Col())
        {
            System.out.printf("\n\n@tableDataChange_Action() Ingredient Type Changed"); //HELLO REMOVE
            setRowBeingEdited();
            return;
        }

        //###############################
        // Ingredients Quantity Column
        // ###############################
        if (columnEdited==getIngredientsTable_Quantity_Col() && jTable.getValueAt(rowEdited, columnEdited)==null)
        {
            JOptionPane.showMessageDialog(null, String.format("\n\nPlease insert a reasonable 'Quantity' value in the cell at: \n\nRow: %s \nColumn: %s", rowEdited + 1, columnEdited + 1));

            cellValue = 0.00;
            setRowBeingEdited(); // re-triggers this method on quantity changed

            jTable.setValueAt(cellValue, rowEdited, columnEdited);
        }

        else if (columnEdited==getIngredientsTable_Quantity_Col())
        {
            System.out.printf("\ntableDataChange_Action() Quantity Being Changed");
            setRowBeingEdited();// HELLO

            updateTableValuesByQuantity(rowEdited, ingredientIndex, cellValue);
            return;
        }

        //###############################
        // Ingredients Name Column
        //###############################
        else if (columnEdited==getIngredientsTable_IngredientsName_Col())
        {
            System.out.printf("\n\n@tableDataChange_Action() Ingredient Name Changed");

            // if the same item is selected avoid processing
            if (!(ingredientNameChanged))
            {
                System.out.printf("\n\nExit No Update Ingredient Name Changed \ningredientNameChanged: %s", ingredientNameChanged);
                setRowBeingEdited();
                return;
            }

            if (selected_IngredientName_JCombo_Item.equals("None Of The Above"))
            {
                if (getNonOfTheAboveInTableStatus(rowEdited, "change a current Ingredient in this meal to 'None Of The Above'"))
                {
                    jTable.setValueAt(previous_IngredientName_JComboItem, rowEdited, columnEdited);
                    setRowBeingEdited();
                    return;
                }
            }

            //##################################################################################################
            // Get Chosen Ingredient ID For Chosen Item (Ingredient Name)
            //##################################################################################################

            String query = String.format("Select IngredientID From ingredients_info WHERE Ingredient_Name = '%s';", selected_IngredientName_JCombo_Item);
            System.out.printf("\n\n Query:\n\n %s", query);

            ArrayList<ArrayList<Object>> results_Ingredient_ID = db.get_Multi_ColumnQuery_Object(query);

            if (results_Ingredient_ID==null)
            {
                JOptionPane.showMessageDialog(null, "Unable to retrieve chosen Ingredient ID from DB!");

                // Change Jtable JComboBox Back To Original Value
                jTable.setValueAt(previous_IngredientName_JComboItem, rowEdited, columnEdited);
                setRowBeingEdited();
                return;
            }

            Object selected_Ingredient_ID = results_Ingredient_ID.get(0).get(0);

            System.out.printf("\nPrevious JCombo Value: %s \nPrevious JCombo  ID: %s \n\nSelected JCombo Value: %s\nSelected JCombo ID: %s" +
                            "\n\nRow  Selected: %s \nColumn Selected: %s",
                    previous_IngredientName_JComboItem, ingredientID, selected_IngredientName_JCombo_Item, selected_Ingredient_ID, rowEdited, columnEdited);
            System.out.println("\n\n#########################################################################");

            //##################################################################################################
            // Create Update Statements
            //##################################################################################################

            //Get new PDID for New Ingredient which matches previously Selected Store
            String query_PDID = String.format("""
                    SELECT
                    (
                        SELECT IFNULL(F.PDID, NULL) AS NEW_PDID
                    	FROM
                    	(
                    		SELECT C.OLD_StoreID
                    		FROM
                    		(
                    		   -- OLD PDID
                    		   SELECT IngredientID AS Old_IngredientID, PDID AS OLD_PDID
                    		   FROM ingredients_in_sections_of_meal
                    		   WHERE PlanID = %s AND Ingredients_Index = %s AND IngredientID = %s
                                       
                    		) AS B
                    		LEFT JOIN
                    		(
                    		   SELECT PDID AS PDID,  StoreID AS OLD_StoreID
                    		   FROM ingredientInShops
                    		 
                    		)AS C
                    		ON B.OLD_PDID  = C.PDID
                    		LEFT JOIN
                    		(
                    		  SELECT StoreID AS StoreID, Store_Name
                    		  FROM stores
                    		
                    		)D	
                    		ON C.OLD_StoreID = D.StoreID 
                    	)E
                    	LEFT JOIN
                    	(
                    	   SELECT S.PDID AS PDID, S.IngredientID, S.StoreID AS StoreID
                    	   FROM ingredientInShops S 
                    	   WHERE S.IngredientID = %s
                    	)F
                    	ON F.StoreID = E.OLD_StoreID
                    	
                    ) AS NEW_PDID""", temp_PlanID, ingredientIndex, ingredientID, selected_Ingredient_ID);


            //#######################################
            //Create IngredientID  Update Statement
            //#######################################
            String uploadQuery = String.format("""
                    UPDATE  ingredients_in_sections_of_meal
                    SET IngredientID = %s, 
                    PDID = (%s)
                    WHERE Ingredients_Index = %s AND PlanID = %s; """, selected_Ingredient_ID, query_PDID, ingredientIndex, temp_PlanID);

            //  System.out.printf("\n\nQUERY PDID: \n'''%s''' \n\nPDID = %s \n\nUpload Query \n'''%s'''", query_PDID, newPDIDResults.get(0), uploadQuery2);

            //##################################################################################################
            // Upload IngredientName & NEW PDID
            //##################################################################################################

            System.out.printf("\n\nQuery1 \n\n%s", uploadQuery);
            if (!(db.uploadData_Batch_Altogether(new String[]{uploadQuery})))
            {
                JOptionPane.showMessageDialog(null, "\n\n ERROR:\n\nUnable to update Ingredient In DB!");

                // Change Jtable JComboBox Back To Original Value
                jTable.setValueAt(previous_IngredientName_JComboItem, rowEdited, columnEdited);

                setRowBeingEdited();
                return;
            }

            //###################################
            // Update IngredientName In JTable
            //###################################
            //HELLO if updateTableValuesByQuantity() updates this column remove this line of code
            jTable.setValueAt(selected_Ingredient_ID, rowEdited, getIngredientsTable_ID_Col());

            //###################################
            // Update  Other Table Values
            //###################################
            setRowBeingEdited(); //HELLO

            updateTableValuesByQuantity(rowEdited, ingredientIndex, jTable.getValueAt(rowEdited, getIngredientsTable_Quantity_Col()));
            return;
        }

        //###############################
        // Ingredients Supplier Column
        //###############################
        else if (columnEdited==getIngredientsTable_Supplier_Col())
        {
            String uploadQuery = "";

            if (cellValue.equals("No Shop"))
            {
                setRowBeingEdited();
                return;
            }

            if (!(cellValue.equals("N/A")))
            {
                //######################################################
                // Get PDID For Chosen Store For Ingredient Statement
                //######################################################
                String getPDIDQuery = String.format("""
                        SELECT i.PDID
                        FROM ingredientInShops i
                        LEFT JOIN
                        (
                           SELECT StoreID, Store_Name FROM stores
                        ) s
                        ON s.StoreID = i.StoreID
                        WHERE i.IngredientID = %s AND s.Store_Name = '%s';""", ingredientID, cellValue);

                ArrayList<String> newPDIDResults = db.getSingleColumnQuery_ArrayList(getPDIDQuery);
                if (newPDIDResults==null)
                {
                    JOptionPane.showMessageDialog(null, "\n\n ERROR:\n\nUnable to retrieve  Ingredient In Shop PDID info!");

                    // HELLO Create Previous value for supplier column
                    jTable.setValueAt(previous_Supplier_JComboItem, rowEdited, columnEdited);

                    setRowBeingEdited();
                    return;
                }

                //######################################################
                // Create PDID Upload Statement
                //######################################################

                // Create  Statement for changing PDID (Ingredient_Index)
                uploadQuery = String.format("""
                        UPDATE  ingredients_in_sections_of_meal
                        SET PDID = %s
                        WHERE Ingredients_Index = %s AND PlanID = %s;""", newPDIDResults.get(0), ingredientIndex, temp_PlanID);

                System.out.printf("\n\nQUERY PDID: \n'''%s''' \n\nPDID = %s \n\nUpload Query \n'''%s'''", getPDIDQuery, newPDIDResults.get(0), uploadQuery);
            }
            else
            {
                // Create  Statement for changing PDID (Ingredient_Index)
                uploadQuery = String.format("""
                        UPDATE  ingredients_in_sections_of_meal
                        SET PDID = NULL
                        WHERE Ingredients_Index = %s AND PlanID = %s;""", ingredientIndex, temp_PlanID);
            }

            //##################################################################################################
            // Upload IngredientName & NEW PDID
            //##################################################################################################
            if (!(db.uploadData_Batch_Altogether(new String[]{uploadQuery})))
            {
                JOptionPane.showMessageDialog(null, "\n\n ERROR:\n\nUnable to update Ingredient Store In DB!");

                // HELLO Create Previous value for supplier column
                jTable.setValueAt(previous_Supplier_JComboItem, rowEdited, columnEdited);

                setRowBeingEdited();
                return;
            }

            setRowBeingEdited();
            updateTableValuesByQuantity(rowEdited, ingredientIndex, jTable.getValueAt(rowEdited, getIngredientsTable_Quantity_Col()));
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
                SET Quantity = %s 
                WHERE PlanID = %s  AND Ingredients_Index = %s;""", quantity, temp_PlanID, ingredients_Index);

        //HELLO DELETE
        System.out.printf("\n\nupdateTableValuesByQuantity() \nQuery: \n\n%s", query1);

        if (!(db.uploadData_Batch_Altogether(new String[]{query1})))
        {
            JOptionPane.showMessageDialog(null, "Un-able to Update row based on cell value!");

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

        String query = String.format("SELECT  * FROM ingredients_in_sections_of_meal_calculation WHERE  ingredients_Index = %s AND PlanID = %s;",
                ingredients_Index, temp_PlanID);

        // HELLO REMOVE
        System.out.printf("\n\n%s", query);

        ArrayList<ArrayList<Object>> ingredientsUpdateData = db.get_Multi_ColumnQuery_Object(query);

        System.out.printf("\n\n\n\nUpdate DATA: \n%s\n\n", ingredientsUpdateData);  // HELLO REMOVE

        if (ingredientsUpdateData==null)
        {
            JOptionPane.showMessageDialog(null, "ERROR updateTableValuesByQuantity(): Un-able to Update Ingredient in table row!");

            setRowBeingEdited();
            return;
        }

        //##########################################################################
        //   Updating Ingredients In Meal Table
        //##########################################################################

        ArrayList<Object> ingredientsTable_UpdateData = ingredientsUpdateData.get(0);
        super.updateTable(ingredientsTable_UpdateData, row);

        if (jTable.getValueAt(row, getIngredientsTable_IngredientsName_Col()).equals("None Of The Above"))
        {
            jTable.setValueAt("No Shop", row, getIngredientsTable_Supplier_Col());
        }

        //##########################################################################
        //   Updating Other Tables
        ///##########################################################################
        setRowBeingEdited();

        updateOtherTablesData();
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
                Object ingredients_Index = table.getValueAt(table.getSelectedRow(), getIngredientsTable_Index_Col());

                if (ingredients_Index!=null)
                {
                    int modelRow = Integer.parseInt(e.getActionCommand());
                    deleteRowAction(ingredients_Index, modelRow); // command to update db
                }
            }
        };
        Working_ButtonColumn2 workingButtonColumn = new Working_ButtonColumn2(jTable, delete, getDeleteBTN_Col());
        workingButtonColumn.setMnemonic(KeyEvent.VK_D);
    }

    protected void set_IngredientsTable_DeleteBTN_Col(int deleteColumn)
    {
        this.deleteColumn = deleteColumn;
    }

    protected void deleteRowAction(Object ingredientIndex, int modelRow)
    {
        //#################################################
        // Can't have an empty Table
        //##################################################
        if (rowsInTable==1)
        {
            String question = String.format("""
                    \n\nThere is only 1 ingredient in this table (' %s '), 
                                        
                    if you delete this ingredient, this table will also be deleted.
                                       
                    Would you like to delete this table?
                     """, mealName);

            int reply = JOptionPane.showConfirmDialog(null, question, "Delete Ingredients", JOptionPane.YES_NO_OPTION); //HELLO Edit

            if (reply==JOptionPane.YES_OPTION)
            {
                deleteTableAction();
            }
            else if (reply==JOptionPane.NO_OPTION)
            {
                Object ingredients_Index = jTable.getValueAt(0, ingredientsTable_Index_Col);
                Object ingredientID = 1;

                //#######################################
                // Change Quantity & IngredientID
                //########################################

                // Update DB Values
                String query1 = String.format("""
                        UPDATE  ingredients_in_sections_of_meal
                        SET IngredientID = %s, Quantity = 0
                        WHERE PlanID = %s  AND Ingredients_Index = %s;""", ingredientID, temp_PlanID, ingredients_Index);

                //HELLO DELETE
                System.out.printf("\n\ndeleteRowAction() \nQuery: \n\n%s", query1);

                if (!(db.uploadData_Batch_Altogether(new String[]{query1})))
                {
                    JOptionPane.showMessageDialog(null, "Un-able to change last row values!");

                    setRowBeingEdited();
                    return;
                }

                // Change Table
                updateTableValuesByQuantity(0, ingredients_Index, 0);
            }

            return;
        }
        //#################################################
        // Remove From DB
        //##################################################
        if (ingredientIndex!=null)
        {
            //#################################################
            // Delete Ingredient From Temp Meal
            //#################################################

            System.out.printf("\n\nDeleting Row in table %s \ningredientIndex: %s | mealInPlanID: %s", mealName, ingredientIndex, mealInPlanID);

            String query = String.format("DELETE FROM ingredients_in_sections_of_meal WHERE Ingredients_Index = %s AND PlanID = %s;", ingredientIndex, temp_PlanID);

            String[] queryUpload = new String[]{query};

            if (!(db.uploadData_Batch_Altogether(queryUpload)))
            {
                JOptionPane.showMessageDialog(null, "Unable To delete Ingredient from Meal in Database");
            }
        }

        //#################################################
        // Remove From Table
        //##################################################
        ((DefaultTableModel) jTable.getModel()).removeRow(modelRow);

        rowsInTable--; // -1 from row count number
        resizeObject();
        //#################################################
        // Update Table Data
        //##################################################

        updateOtherTablesData();

        //HELLO REMOVE
        System.out.printf("\n\n#########################################################################");
    }

    public void deleteTableAction()
    {
        //##########################################
        // Delete table from database
        //##########################################

         /*
            Delete all ingredients from this meal (using mealID) from table "ingredients_in_meal"
            Delete meal from meals database
         */

        String query1 = "SET FOREIGN_KEY_CHECKS = 0;"; // Disable Foreign Key Checks
        String query2 = String.format(" DELETE FROM ingredients_in_sections_of_meal WHERE DivMealSectionsID = %s AND PlanID = %s;", divMealSectionsID, temp_PlanID);
        String query4 = String.format(" DELETE FROM  mealsInPlan WHERE mealInPlanID = %s AND PlanID = %s;", mealInPlanID, temp_PlanID);
        String query5 = "SET FOREIGN_KEY_CHECKS = 1;"; // Enable Foreign Key Checks

        if (!db.uploadData_Batch_Altogether(new String[]{query1, query2, query4, query5}))
        {
            JOptionPane.showMessageDialog(null, "Table Un-Successfully Deleted! ");
            return;
        }

        //##########################################
        // Hide JTable object & Collapsible OBJ
        //##########################################

        setVisibility(false); // hide collapsible Object

        update_MacrosLeft_Table();// update macrosLeft table, due to number deductions from this meal

        setObjectDeleted(true); // set this object as deleted

        JOptionPane.showMessageDialog(null, "Table Successfully Deleted!");

    }

    public void completely_Deleted_JTables()
    {
        // parentContainer =  collapsibleObj
        //#################################################
        // Remove  Main Jtable from collapsible Object
        //#################################################
        parentContainer.setSize(new Dimension(0, 0)); // set collapsibleObj to size 0
        parentContainer.remove(this); // remove jtables for collapsible Object

        //##################################################
        // Delete Meal Total Table From Collapsible Object
        //##################################################
        if (total_Meal_Table!=null && collapsibleObj!=null)
        {
            collapsibleObj.getParentContainer().remove(spaceDivider); // remove spaceDivider from GUI

            JPanel collapsible_SouthPanel = collapsibleObj.getSouthJPanel();
            collapsible_SouthPanel.remove(total_Meal_Table);
            collapsibleObj.getSouthJPanel();
        }

        parentContainer.revalidate();

        //##################################################
        //  Notify GUI to delete collapsible Object
        //##################################################
        if (collapsibleObj!=null)
        {
            collapsibleObj.removeCollapsibleJPanel(); // notifies GUI to delete collapsible Object
        }
    }

    //##################################################################################################################
    // Button Events
    //##################################################################################################################

    //###################################################
    // Add Button
    //###################################################
    public void addIngredient()
    {
        add_btn_Action();
    }

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
        // Adding Row Data to Table Model
        //#########################################################

        Object[] rowData = new Object[columnsInTable];

        tableModel.addRow(rowData);

        //#########################################################
        // Setting Variables
        //#########################################################

        int tableRow = rowsInTable==0 ? 0:rowsInTable;
        int ingredientID = 1;
        BigDecimal quantity = new BigDecimal("0.00");

        //#########################################################
        // Get Next Ingredients_Index For This Ingredient Addition
        //#########################################################
        String getNextIndexQuery = "SELECT IFNULL(MAX(`Ingredients_Index`),0) + 1 AS nextId FROM `ingredients_in_sections_of_meal`;";

        String[] newIngredientsIndex = db.getSingleColumnQuery(getNextIndexQuery);

        if (newIngredientsIndex==null)
        {
            JOptionPane.showMessageDialog(null, "Unable to create new ingredient in table! \nUnable to generate ingredients_Index!!");
            return;
        }

        int newIngredientsIndex2 = Integer.parseInt(newIngredientsIndex[0]);
        //#########################################################
        // Insert into Database
        //#########################################################

        String query1 = String.format("""
                        
                INSERT INTO ingredients_in_sections_of_meal
                (Ingredients_Index, divMealSectionsID, PlanID, IngredientID, Quantity, PDID)
                                        
                VALUES
                (%s, %s, %s, %s, %s, %s); 
                        """, newIngredientsIndex2, divMealSectionsID, temp_PlanID, ingredientID, quantity, NoneOfTheAbove_PDID);

        if (!(db.uploadData_Batch_Altogether(new String[]{query1})))
        {
            JOptionPane.showMessageDialog(null, "Un-able to Insert new row into the Database!");
            return;
        }

        //####################################################################
        //  Getting Row Data For New Ingredient Addition
        //####################################################################

        String query = String.format("""
                SELECT *
                FROM ingredients_in_sections_of_meal_calculation
                WHERE Ingredients_Index = %s AND PlanID = %s;
                """, newIngredientsIndex2, temp_PlanID);


        System.out.printf("\n\n%s", query); // HELLO REMOVE

        ArrayList<ArrayList<Object>> results = db.get_Multi_ColumnQuery_Object(query);

        System.out.printf("\n\n\n\n%s\n\n", results);  // HELLO REMOVE

        if (results==null)
        {
            JOptionPane.showMessageDialog(null, "ERROR add_btn_Action(): Un-able to get Ingredient info for row in table!");
            return;
        }

        //#########################################
        //   Updating Ingredients In Meal Table
        //########################################

        setRowBeingEdited(); // stops endless loop being called for all cells being editted

        ArrayList<Object> ingredientsTable_UpdateData = results.get(0);
        super.updateTable(ingredientsTable_UpdateData, rowsInTable);

        setRowBeingEdited(); // stops endless loop being called for all cells being editted

        //##################################################################################
        // Resize Jtable & GUI with new Data
        //###################################################################################
        rowsInTable++;
        resizeObject();

        //##################################################################################
        // Update Table Data
        //###################################################################################
        updateOtherTablesData();
    }

    //###################################################
    // Refresh Button
    //###################################################
    public void refresh_Btn_Action(boolean updateMacrosLeft)
    {
        //#############################################################################################
        // Reset DB Data
        //##############################################################################################
        if (!(transferMealDataToPlan(planID, temp_PlanID)))
        {
            JOptionPane.showMessageDialog(null, "\n\nUnable to transfer ingredients data from  original plan to temp plan!!");
            return;
        }

        //#############################################################################################
        // Reset Table Info & Data
        //##############################################################################################
        refresh(updateMacrosLeft);
    }

    public void refresh(boolean updateMacrosLeft)
    {
        //#############################################################################################
        // If Meal was previously deleted reset variables & state
        //##############################################################################################
        if (getObjectDeleted())
        {
            setVisibility(true);
            setObjectDeleted(false);
        }

        //##############################################################################################
        // Reset Table Model data
        ///#############################################################################################
        tableModel_Setup(getData(), getColumnNames());

        //#############################################################################################
        // Reset Meal Total  Table Data
        //#############################################################################################
        refreshTotalMealTable();

        //#############################################################################################
        // Update Other Tables Data
        //#############################################################################################
        if (updateMacrosLeft)
        {
            refreshMacrosLeftTable();
        }

        //#############################################################################################
        // Reset Variable
        //#############################################################################################
        dataChangedInTable = false;
    }

    //###################################################
    // Save Button
    //###################################################
    public boolean saveDataAction(boolean showMessage)
    {
        //######################################################################
        // Transfer Data from temp plan to origin plan
        //######################################################################
        if (!(transferMealDataToPlan(temp_PlanID, planID)))     // If Meal Not In Original PlanID Add To PlanID
        {
            if (showMessage)
            {
                JOptionPane.showMessageDialog(null, "\n\nUnable to transfer ingredients data from temp to original plan ");
            }
            return false;
        }

        //######################################################################
        // Change Setting
        //######################################################################
        if (!(getMealInDB()))     // If Meal Not In Original PlanID Add To PlanID
        {
            set_Meal_In_DB(true);
        }

        //######################################################################
        // Update Table Model
        //######################################################################
        if (!updateTableModelData())
        {
            if (showMessage)
            {
                JOptionPane.showMessageDialog(null, "\n\nUnable to update table model!");
            }
            return false;
        }

        //######################################################################
        // Success Message
        //######################################################################
        if (showMessage)
        {
            JOptionPane.showMessageDialog(null, "Table Successfully Updated!");
        }

        //#############################################################################################
        // Reset Variable
        //#############################################################################################
        dataChangedInTable = false;

        return true;
    }

    public boolean updateTableModelData()
    {
        //##########################################
        // Changing Ingredients In Meal Table Model
        //##########################################
        String tableInQuery = "ingredients_in_sections_of_meal_calculation";

        String query = String.format("Select * from %s WHERE DivMealSectionsID = %s AND PlanID = %s;", tableInQuery, divMealSectionsID, temp_PlanID);
        System.out.printf("\n\n################################################### \nupdateTableModelData() \n%s", query);

        Object[][] ingredients_Data = db.getTableDataObject(query, tableInQuery);

        if (ingredients_Data==null)
        {
            System.out.printf("\n\nUnable to change table model: %s", getMealName());
            return false;
        }

        setTableModelData(ingredients_Data);


        //##########################################
        // Changing Total  Ingredients Table Model
        //##########################################
        if (!(total_Meal_Table.updateTotalMealTableModelData()))
        {
            return false;
        }

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
        String query0 = String.format("DROP TABLE IF EXISTS temp_ingredients_in_meal;");

        String query1 = "SET FOREIGN_KEY_CHECKS = 0;"; // Disable Foreign Key Checks

        // Delete ingredients in meal Data from original plan with this mealID
        String query2 = String.format("DELETE FROM ingredients_in_sections_of_meal WHERE DivMealSectionsID = %s AND PlanID = %s;", divMealSectionsID, toPlanID);

        String query3 = "SET FOREIGN_KEY_CHECKS = 1;"; // Enable Foreign Key Checks

        //########################################################
        // Insert Meal & dividedMealSections If Not in DB In toPlan
        //########################################################
        // insert meal if it does not exist inside toPlanID
        String query4 = String.format("""
                INSERT IGNORE INTO mealsInPlan
                (MealInPlanID, PlanID, Meal_Name)
                                
                VALUES
                (%s, %s, '%s');
                  """, mealInPlanID, toPlanID, mealName);

        String query5 = String.format("""
                INSERT IGNORE INTO dividedMealSections
                (DivMealSectionsID, MealInPlanID, PlanID)
                                
                VALUES
                (%s, %s, '%s');
                  """, divMealSectionsID,  mealInPlanID, toPlanID);

        //####################################################
        // Transferring this plans Ingredients to Temp-Plan
        //####################################################

        // Create Table to transfer ingredients from original plan to temp
        String query6 = String.format("""                                     
                CREATE table temp_ingredients_in_meal  AS
                SELECT i.*
                FROM ingredients_in_sections_of_meal i                                                       
                WHERE i.DivMealSectionsID = %s AND i.PlanID = %s;          
                """, divMealSectionsID, fromPlanID);

        String query7 = String.format("UPDATE temp_ingredients_in_meal  SET PlanID = %s;", toPlanID);

        String query8 = String.format("INSERT INTO ingredients_in_sections_of_meal SELECT * FROM temp_ingredients_in_meal;");

        //####################################################
        // Update
        //####################################################
        String[] query_Temp_Data = new String[]{query0, query1, query2, query3, query4, query5, query6, query7, query8};

        if (!(db.uploadData_Batch_Altogether(query_Temp_Data)))
        {
            JOptionPane.showMessageDialog(null, "\n\ntransferMealIngredients() Cannot Create Temporary Plan In DB to Allow Editing");
            return false;
        }

        System.out.printf("\nMealIngredients Successfully Transferred! \n\n%s", lineSeparator);
        return true;
    }

    //##################################################################################################################
    // JCombo Boxes
    //##################################################################################################################
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
                        if (ie.getStateChange()==ItemEvent.DESELECTED)
                        {
                            previous_Supplier_JComboItem = ie.getItem();
                        }
                        if (ie.getStateChange()==ItemEvent.SELECTED)
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

                TableColumn tableColumn = jTable.getColumnModel().getColumn(getIngredientsTable_Supplier_Col());
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

                String keyColumnValue = jTable.getValueAt(row, getIngredientsTable_ID_Col()).toString(); // HELLO Not Sure what this does
                Object ingredientID = jTable.getValueAt(row, getIngredientsTable_ID_Col());
                Object ingredientIndex = jTable.getValueAt(row, getIngredientsTable_Index_Col());
                Object ingrdientName = table.getValueAt(row, getIngredientsTable_IngredientsName_Col());

                TableColumn tableColumn = jTable.getColumnModel().getColumn(column);

                //########################################
                // Get Supplier Based on ingredientIndex
                ////######################################

                String queryStore = String.format("""
                        SELECT  IFNULL(D.STORE_Name, 'N/A') AS STORE
                        FROM 
                        (
                        	SELECT i.IngredientID FROM ingredients_info i
                        	WHERE i.IngredientID = %s
                        ) AS t 
                                            
                        LEFT JOIN
                        (
                           SELECT l.IngredientID, l.StoreID FROM ingredientInShops l 
                        	
                        )  AS C
                        ON t.IngredientID = C.IngredientID 
                        LEFT JOIN
                        (
                          SELECT StoreID, Store_Name FROM stores
                        ) D
                        ON C.StoreID = D.StoreID
                        ORDER BY STORE;""", ingredientID);

                ArrayList<String> storesResults = db.getSingleColumnQuery_ArrayList(queryStore);

                //HELLO REMOVE

                String seperator = "#######################################################################";
                System.out.printf("\n\n%s \n\nQuery: \n%s \n\nList Of Available Shops:\n\n%s", seperator, queryStore, storesResults);


                if (storesResults!=null)
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

                    if (!(ingrdientName.equals("None Of The Above")) && !NA_in_List)
                    {
                        model1.addElement("N/A");
                    }
                }
                else
                {
                    //HELLO FIX WILL SOMEHOW CAUSE ERROR
                    JOptionPane.showMessageDialog(null, "\n\nError \nSetting Available Stores for Ingredient!");
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
                        if (ie.getStateChange()==ItemEvent.DESELECTED)
                        {
                            previous_IngredientType_JComboItem = ie.getItem();
                        }
                        if (ie.getStateChange()==ItemEvent.SELECTED)
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

                TableColumn tableColumn = jTable.getColumnModel().getColumn(getIngredientsTable_Supplier_Col());
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

                String keyColumnValue = jTable.getValueAt(row, getIngredientsTable_ID_Col()).toString(); // HELLO Not Sure what this does
                Object ingredientID = jTable.getValueAt(row, getIngredientsTable_ID_Col());
                Object ingredientIndex = jTable.getValueAt(row, getIngredientsTable_Index_Col());
                Object ingrdientName = table.getValueAt(row, getIngredientsTable_IngredientsName_Col());

                TableColumn tableColumn = jTable.getColumnModel().getColumn(column);

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
                        if (ie.getStateChange()==ItemEvent.DESELECTED)
                        {
                            previous_IngredientName_JComboItem = ie.getItem();
                        }
                        if (ie.getStateChange()==ItemEvent.SELECTED)
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

                TableColumn tableColumn = jTable.getColumnModel().getColumn(getIngredientsTable_Supplier_Col());
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

                String keyColumnValue = jTable.getValueAt(row, getIngredientsTable_ID_Col()).toString(); // HELLO Not Sure what this does
                Object ingredientID = jTable.getValueAt(row, getIngredientsTable_ID_Col());
                Object ingredientIndex = jTable.getValueAt(row, getIngredientsTable_Index_Col());
                Object ingrdientName = table.getValueAt(row, getIngredientsTable_IngredientsName_Col());

                TableColumn tableColumn = jTable.getColumnModel().getColumn(column);

                String ingredientType = table.getValueAt(row, getIngredientsTable_Type_Col()).toString();

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

    public void setUpJComboColumn(int col, String type, ArrayList<String> items)
    {
        jComboMap.put(col, items);

        if (items!=null && items.size() > 0)
        {
            TableColumn sportColumn = jTable.getColumnModel().getColumn(col);

            //Set up the editor for the sport cells.

            JComboBox comboBox = new JComboBox();
            comboBox.setEditable(true);


            comboBox.addItemListener(new ItemListener()
            {
                public void itemStateChanged(ItemEvent ie)
                {
                    if (ie.getStateChange()==ItemEvent.SELECTED)
                    {
                        if (previousJComboItem==null)
                        {
                            selected_JCombo_Item = ie.getItem();
                            previousJComboItem = selected_JCombo_Item;
                        }
                        else
                        {
                            previousJComboItem = selected_JCombo_Item;
                            selected_JCombo_Item = ie.getItem();
                        }
                    }
                }

            });


            DefaultComboBoxModel model = new DefaultComboBoxModel();

            for (int i = 0; i < items.size(); i++)
            {
                model.addElement(items.get(i));
            }

            comboBox.setModel(model);
            sportColumn.setCellEditor(new DefaultCellEditor(comboBox)); // sets column to a comboBox

            //#########################################################################
            /* Not Sure why this step has to be repeated, but, it doesn't work otherwise*/
            //#########################################################################

            model = new DefaultComboBoxModel();
            for (int i = 0; i < items.size(); i++)
            {
                model.addElement(items.get(i));
            }

            //######################################################
            // Centre ComboBox Items
            //######################################################

            ((JLabel) comboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

            //######################################################
            // Make JComboBox Visible
            //######################################################

            TableColumn tableColumn = jTable.getColumnModel().getColumn(col);
            ComboBoxTableCellRenderer renderer = new ComboBoxTableCellRenderer()
            {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
                {
                    setSelectedItem(value);
                    return this;
                }
            };


            renderer.setModel(model);
            tableColumn.setCellRenderer(renderer);
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
            if (value==null)
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
            if (value==null)
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
                    && table.getCellEditor()==this)
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
    // Update Table & Refresh Tables
    //##################################################################################################################

    //##################################################
    // Update Table Methods
    //##################################################

    private void updateOtherTablesData()
    {
        update_TotalMeal_Table();
        update_MacrosLeft_Table();
    }

    private void update_TotalMeal_Table()
    {
        total_Meal_Table.updateTotalMealTable();
    }

    public void update_MacrosLeft_Table()
    {
        macrosLeft_Table.updateMacrosLeftTable();
    }

    //##################################################
    // Refresh Table Methods
    //##################################################
    public void refreshTotalMealTable()
    {
        total_Meal_Table.refreshData();
    }

    public void refreshMacrosLeftTable()
    {
        macrosLeft_Table.refreshData();
    }

    //##################################################################################################################
    // Mutator Methods
    //##################################################################################################################

    private void setRowBeingEdited()
    {
        rowBeingEdited = !rowBeingEdited; // flip it
    }

    private void setObjectDeleted(boolean deleted)
    {
        objectDeleted = deleted;
    }

    public void setVisibility(boolean condition)
    {
        collapsibleObj.setVisible(condition);
        spaceDivider.setVisible(condition);
    }

    public void setSpaceDivider(JPanel jPanel)
    {
        spaceDivider = jPanel;
    }

    public void set_Meal_In_DB(boolean mealInDB)
    {
        meal_In_DB = mealInDB;
    }

    //#############################################################
    // Mutator (Set) For Table Column Positions
    //#############################################################
    private void set_IngredientsTable_Index_Col(int value)
    {
        ingredientsTable_Index_Col = value;
    }

    private void set_IngredientsTable_ID_Col(int value)
    {
        ingredientsTable_ID_Col = value;
    }

    private void set_IngredientsTable_Quantity_Col(int value)
    {
        ingredientsTable_Quantity_Col = value;
    }

    private void set_IngredientsTable_IngredientType_Col(int value)
    {
        ingredientsTable_Type_Col = value;
    }

    private void set_IngredientsTable_IngredientsName_Col(int value)
    {
        ingredientsTable_IngredientsName_Col = value;
    }

    private void set_IngredientsTable_Supplier_Col(int value)
    {
        ingredientsTable_Supplier_Col = value;
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

    public boolean getObjectDeleted()
    {
        return objectDeleted;
    }

    private boolean getNonOfTheAboveInTableStatus(Integer rowTriggeredAt, String attemptingTo)
    {
        if (rowsInTable > 0)
        {
            //Checking if the ingredient None Of the Above is already in the table
            for (int row = 0; row < rowsInTable; row++)
            {
                // Currently, changing  ingredient to NONE OF the ABOVE
                if (rowTriggeredAt!=null && row==rowTriggeredAt)
                {
                    continue;
                }

                // if None Of  the above is found in the table return true
                if (jTable.getValueAt(row, getIngredientsTable_ID_Col()).equals(1) || jTable.getValueAt(row, getIngredientsTable_ID_Col()).equals("1"))
                {
                    String message = String.format("""
                            \n\nPlease change the Ingredient at: 
                            \nRow: %s \nColumn: %s                             
                            \nFrom the ingredient 'None Of The Above' to another ingredient! 
                            \nBefore attempting to %s!
                            """, row + 1, getIngredientsTable_IngredientsName_Col() + 1, attemptingTo);
                    JOptionPane.showMessageDialog(null, message);
                    return true;
                }
            }
        }
        return false;
    }

    //#############################################################
    // Accessor For Table Column Positions
    //#############################################################
    private Integer getDeleteBTN_Col()
    {
        return deleteColumn;
    }

    private int getIngredientsTable_Index_Col()
    {
        return ingredientsTable_Index_Col;
    }

    private int getIngredientsTable_ID_Col()
    {
        return ingredientsTable_ID_Col;
    }

    private int getIngredientsTable_Quantity_Col()
    {
        return ingredientsTable_Quantity_Col;
    }

    private int getIngredientsTable_Type_Col()
    {
        return ingredientsTable_Type_Col;
    }

    private int getIngredientsTable_IngredientsName_Col()
    {
        return ingredientsTable_IngredientsName_Col;
    }

    private int getIngredientsTable_Supplier_Col()
    {
        return ingredientsTable_Supplier_Col;
    }

    //##################################################################################################################
}
