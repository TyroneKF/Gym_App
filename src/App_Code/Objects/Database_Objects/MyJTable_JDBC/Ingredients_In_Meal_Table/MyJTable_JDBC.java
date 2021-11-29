package App_Code.Objects.Database_Objects.MyJTable_JDBC.Ingredients_In_Meal_Table;


import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JTable_JDBC.JDBC_JTable;
import App_Code.Objects.Gui_Objects.CollapsibleJPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

public class MyJTable_JDBC extends JDBC_JTable
{
    //####################################
    // Objects
    //####################################
    private JPanel spaceDivider;
    private CollapsibleJPanel collapsibleObj;
    private MyJTable_JDBC total_Meal_Table, macrosLeft_Table, ingredientsTableCalculation;


    //####################################
    // Other Variables
    //####################################
    private Integer planID, temp_PlanID = 1, mealID, tempPlan_Meal_ID;
    private String mealName;

    private ArrayList<Integer> triggerColumns = null;

    private boolean rowBeingEdited = false, setIconsUp = false, meal_In_DB = true, objectDeleted = false,
            isTable_A_Plan_TotalTable = false;

    //##################################################################
    // Ingredients In Meal Table
    //##################################################################
    private int
            ingredientsTable_Index_Col = 2,
            ingredientsTable_ID_Col = 3,
            ingredientsTable_Quantity_Col = 4,
            ingredientsTable_IngredientsName_Col = 5,
            ingredientsTable_Supplier_Col = 7;

    //SupplierName JComboBox Variables
    Object previous_Supplier_JComboItem, selected_Supplier_JCombo_Item;
    boolean supplierNameChanged = false;

    //IngredientName JComboBox Variables
    boolean ingredientNameChanged = false;
    Object previous_IngredientName_JComboItem, selected_IngredientName_JCombo_Item;


    private int ingredientsTable_Col_Update_Start = 6;

    final private int NoneOfTheAbove_PDID = 1;


    //##################################################################
    // Total Meal Table
    //##################################################################
    final private int total_Meal_Table_MealID_Col = 1;

    final private int totalMealTable_Col_Update_Start = 3;

    //##################################################################
    // Macros Left Table
    //##################################################################
    final private int macrosLeft_Table_Col_Update_Start = 1;

    //##################################################################################################################
    // Constructors
    //##################################################################################################################

    // Inherited Constructor From JDBC_JTable
    public MyJTable_JDBC(MyJDBC db, Container parentContainer, String databaseName, String tableName,
                         ArrayList<Integer> unEditableColumns, ArrayList<Integer> colAvoidCentering)
    {
        super(db, parentContainer, databaseName, tableName, unEditableColumns, colAvoidCentering);
    }

    // Total Plan Table
    public MyJTable_JDBC(MyJDBC db, Container parentContainer, Object[][] data, String[] columnNames, int planID,
                         String tableName, ArrayList<Integer> unEditableColumns, ArrayList<Integer> colAvoidCentering)
    {
        super.db = db;

        super.data = data;
        super.columnNames = columnNames;


        this.planID = planID;

        super.parentContainer = parentContainer;
        super.tableName = tableName;


        super.unEditableColumns = unEditableColumns;
        super.colAvoidCentering = colAvoidCentering;

        this.setIconsUp = false;

        isTable_A_Plan_TotalTable = true;

        setUp();
    }

    // Total Meal Table
    public MyJTable_JDBC(MyJDBC db, CollapsibleJPanel collapsibleObj, String databaseName, Object[][] data, String[] columnNames, int planID,
                         Integer mealID, Integer tempPlan_Meal_ID, String mealName, String tableName,
                         ArrayList<Integer> unEditableColumns, ArrayList<Integer> colAvoidCentering, boolean setIconsUp)
    {
        super.db = db;

        super.data = data;
        super.columnNames = columnNames;
        this.databaseName = databaseName;

        this.mealID = mealID;
        this.mealName = mealName;
        this.planID = planID;
        this.tempPlan_Meal_ID = tempPlan_Meal_ID;

        this.collapsibleObj = collapsibleObj;

        super.parentContainer = collapsibleObj.getCentreJPanel();
        super.tableName = tableName;

        super.unEditableColumns = unEditableColumns;
        super.colAvoidCentering = colAvoidCentering;
        this.setIconsUp = setIconsUp;

        setUp();
    }

    // Ingredients Table
    public MyJTable_JDBC(MyJDBC db, CollapsibleJPanel collapsibleObj, String databaseName, Object[][] data, String[] columnNames, int planID,
                         Integer mealID, Integer tempPlan_Meal_ID, String mealName, String tableName, ArrayList<Integer> triggerColumns,
                         ArrayList<Integer> unEditableColumns, ArrayList<Integer> colAvoidCentering, boolean setIconsUp,
                         MyJTable_JDBC total_Meal_Table, MyJTable_JDBC macrosLeft_Table)
    {
        super.db = db;
        ingredientsTableCalculation = this;
        super.data = data;
        super.columnNames = columnNames;
        this.databaseName = databaseName;

        this.mealID = mealID;
        if (mealID==null)
        {
            set_Meal_In_DB(false);
        }

        this.tempPlan_Meal_ID = tempPlan_Meal_ID;
        this.mealName = mealName;
        this.planID = planID;


        this.collapsibleObj = collapsibleObj;

        super.parentContainer = collapsibleObj.getCentreJPanel();
        super.tableName = tableName;


        super.unEditableColumns = unEditableColumns;
        super.colAvoidCentering = colAvoidCentering;
        this.triggerColumns = triggerColumns;
        this.setIconsUp = setIconsUp;

        this.total_Meal_Table = total_Meal_Table;
        this.macrosLeft_Table = macrosLeft_Table;
        this.ingredientsTableCalculation = this;

        setUp();
    }

    //##################################################################################################################
    // Experiments
    //##################################################################################################################
    public void setUpSupplierColumn(int col)
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

        renderer.setToolTipText("Click for combo box");
        tableColumn.setCellRenderer(renderer);
    }

    /**
     * Custom editor that changes the combo choices based
     * on the "Vegetarian" column value
     */
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

            comboBox.addActionListener(ae -> {
                supplierNameChanged = false;
            });

            comboBox.addItemListener(new ItemListener()
            {
                public void itemStateChanged(ItemEvent ie)
                {
                    if (ie.getStateChange()==ItemEvent.SELECTED)
                    {
                        supplierNameChanged = true;

                        if (previous_Supplier_JComboItem==null)
                        {
                            selected_Supplier_JCombo_Item = ie.getItem();
                            previous_Supplier_JComboItem = ie.getItem();
                        }
                        else
                        {
                            previous_Supplier_JComboItem = selected_Supplier_JCombo_Item;
                            selected_Supplier_JCombo_Item = ie.getItem();
                        }
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
                    SELECT  IFNULL(C.STORE, 'N/A') AS STORE
                    FROM 
                    (
                    	SELECT i.IngredientID FROM ingredients_info i
                    	WHERE i.IngredientID = %s
                    ) AS t 
                                        
                    LEFT JOIN
                    (
                       SELECT l.IngredientID, l.STORE FROM ingredientInShops l 
                    	
                    )  AS C
                                        
                    ON t.IngredientID = C.IngredientID;""", ingredientID);

            ArrayList<String> storesResults = db.getSingleColumnQuery_ArrayList(queryStore);

            //HELLO REMOVE
            /*
            String seperator = "#######################################################################";
            System.out.printf("\n\n%s \n\nQuery: \n%s \n\nList Of Available Shops:\n\n%s", seperator, queryStore, storesResults);

             */

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

    //############################################

    @Override
    public void setUpJComboColumn(int col, String type, ArrayList<String> items)
    {
        if ((type.equals("IngredientName") && (items==null || items.size()==0))
                || (!type.equals("IngredientName") && !type.equals("SupplierName")))
        {
            return;
        }

        TableColumn tableColumn = jTable.getColumnModel().getColumn(col);

        //Set up the editor for the sport cells.

        JComboBox comboBox = new JComboBox();
        comboBox.setEditable(true);

        comboBox.addActionListener(ae -> {

            switch (type)
            {
                case "IngredientName":
                    ingredientNameChanged = false;
                    break;
                case "SupplierName":
                    supplierNameChanged = false;
                    break;
            }
        });

        comboBox.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent ie)
            {
                if (ie.getStateChange()==ItemEvent.SELECTED)
                {
                    switch (type)
                    {
                        case "IngredientName":
                            ingredientNameChanged = true;
                            break;
                        case "SupplierName":
                            supplierNameChanged = true;
                            break;
                    }

                    if (type.equals("IngredientName"))
                    {
                        if (previous_IngredientName_JComboItem==null)
                        {
                            selected_IngredientName_JCombo_Item = ie.getItem();
                            previous_IngredientName_JComboItem = ie.getItem();
                        }
                        else
                        {
                            previous_IngredientName_JComboItem = selected_IngredientName_JCombo_Item;
                            selected_IngredientName_JCombo_Item = ie.getItem();
                        }
                    }
                    else if (type.equals("SupplierName"))
                    {
                        if (previous_Supplier_JComboItem==null)
                        {
                            selected_Supplier_JCombo_Item = ie.getItem();
                            previous_Supplier_JComboItem = ie.getItem();
                        }
                        else
                        {
                            previous_Supplier_JComboItem = selected_Supplier_JCombo_Item;
                            selected_Supplier_JCombo_Item = ie.getItem();
                        }
                    }
                }
            }
        });


        DefaultComboBoxModel model = new DefaultComboBoxModel();
        if (type.equals("IngredientName"))
        {
            getJcomboMap().put(col, items);

            for (int i = 0; i < items.size(); i++)
            {
                model.addElement(items.get(i));
            }

            comboBox.setModel(model);
            tableColumn.setCellEditor(new DefaultCellEditor(comboBox)); // sets column to a comboBox

            //##############################################################################
            // Not Sure why this step has to be repeated, but, it doesn't work otherwise//
            //##############################################################################

            model = new DefaultComboBoxModel();
            for (int i = 0; i < items.size(); i++)
            {
                model.addElement(items.get(i));
            }
        }

        //######################################################
        // Centre ComboBox Items
        //######################################################

        ((JLabel) comboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        //######################################################
        // Make JComboBox Visible
        //######################################################

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

    @Override //HELLO FIX, doesnt reload with table
    public void setupDeleteBtnColumn(int deleteBtnColumn)
    {
        setDeleteBTNColumn(deleteBtnColumn);
        System.out.printf("\n\nsetupDeleteBtnColumn() Column: %s", getDeleteBTN_Col());

        Action delete = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {


                JTable table = (JTable) e.getSource();
                Object ingredients_Index = table.getValueAt(table.getSelectedRow(), getIngredientsTable_Index_Col());

                if (ingredients_Index!=null)
                {
                    deleteRowAction(ingredients_Index); // command to update db

                    int modelRow = Integer.parseInt(e.getActionCommand());
                    ((DefaultTableModel) table.getModel()).removeRow(modelRow);

                    rowsInTable--; // -1 from row count number
                    resizeObject();
                }
            }
        };
        Working_ButtonColumn2 workingButtonColumn = new Working_ButtonColumn2(jTable, delete, getDeleteBTN_Col());
        workingButtonColumn.setMnemonic(KeyEvent.VK_D);
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

        if (getTableInitilized())  //first time this method is called, special columns aren't defined
        {
            if (getHideColumns()!=null)//Must be first
            {
                SetUp_HiddenTableColumns(getHideColumns(), get_StartingUpdateColumn());
            }

            if (getDeleteBTN_Col()!=null)
            {
                setupDeleteBtnColumn(getDeleteBTN_Col()); // specifying delete column
            }

            // Setting up JcomboBox Field
            for (Integer key : getJcomboMap().keySet())
            {
                setUpJComboColumn(key, "IngredientName", getJcomboMap().get(key));
            }

            if (ingredientsTableCalculation!=null)
            {
                setUpSupplierColumn(getIngredientsTable_Supplier_Col());
            }
        }
        else
        {
            setTableInitilized();
        }
        resizeObject();
    }

    //############################################
    @Override //HELLO MOVE BACK TO ACTION METHODS SECTION
    protected void tableDataChange_Action()
    {
        int rowEdited = jTable.getEditingRow(), columnEdited = jTable.getEditingColumn();

        //######################################################################
        // Check if cell that triggered this event can execute
        //######################################################################
        // Avoids endless loop / if edited cell column, is supposed to a trigger an action
        if (rowBeingEdited || triggerColumns==null || !(triggerColumns.contains(columnEdited)))
        {
            //HELLO REMOVE
           // System.out.printf("\nExited tableDataChange_Action() Row: %s, Column: %s", rowEdited, columnEdited);
            return;
        }

        System.out.printf("\n\ntableDataChange_Action()");
        setRowBeingEdited();

        Object ingredientID = jTable.getValueAt(rowEdited, getIngredientsTable_ID_Col());

        Object ingredientIndex = jTable.getValueAt(rowEdited, getIngredientsTable_Index_Col());

        Object cellValue = jTable.getValueAt(rowEdited, columnEdited);


        //######################################################################
        // If the Quantity Value on this Row Is Null Set it to 0 + Error MSG
        //######################################################################
        if (columnEdited==getIngredientsTable_Quantity_Col() && jTable.getValueAt(rowEdited, columnEdited)==null)
        {
            JOptionPane.showMessageDialog(null, String.format("\n\nPlease insert a reasonable 'Quantity' value in the cell at: \n\nRow: %s \nColumn: %s", rowEdited + 1, columnEdited + 1));

            cellValue = 0.00;
            jTable.setValueAt(cellValue, rowEdited, columnEdited);
        }

        //########################
        //Other Trigger Columns
        //########################

        // Ingredients Name Column
        else if (columnEdited==getIngredientsTable_IngredientsName_Col())
        {
            //HELLO DELETE
            /*
            System.out.printf("\n\ningredientsTable_IngredientsName Row %s, Column %s \nPrevious Item %s \nCurrent Item %s",
                    rowEdited, columnEdited, previous_IngredientName_JComboItem, selected_IngredientName_JCombo_Item); //HELLO REMOVE

             */

            // if the same item is selected avoid processing
            if (!(ingredientNameChanged))
            {
                setRowBeingEdited();
                return;
            }

            if (selected_IngredientName_JCombo_Item.equals("None Of The Above"))
            {
                if (isNonOfTheABoveInTable(rowEdited, "change a current Ingredient in this meal to 'None Of The Above'"))
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

            //#######################################
            //Create IngredientID  Update Statement
            //#######################################
            String uploadQuery = String.format("""
                    UPDATE  ingredients_in_meal
                    SET IngredientID = %s
                    WHERE Ingredients_Index = %s AND PlanID = %s;

                        """, selected_Ingredient_ID, ingredientIndex, temp_PlanID);

            //#######################################
            //Create PDID Update Statement
            //#######################################

            //Get new PDID for New Ingredient which matches previously Selected Store
            String query_PDID = String.format("""
                                        
                    SELECT IFNULL
                    (
                    	(
                    		SELECT   i._NEW_POSSIBLE_PDID
                    		FROM 
                    		(       SELECT  n.IngredientID AS _NEW_IngredientID , i.PDID AS OLD_PDID
                    				
                    				FROM ingredients_in_meal i, ingredients_info n
                    				
                    				WHERE n.IngredientID = %s
                    				AND i.PlanID = %s AND i.Ingredients_Index = %s
                    		) AS t
                                        
                    		INNER JOIN
                    		(
                    		  SELECT PDID AS _PDID, Store AS OLD_STORE FROM ingredientInShops
                    		 
                    		) AS c
                                        
                    		ON c._PDID = t.OLD_PDID
                                        
                    		INNER JOIN
                    		(
                    			SELECT PDID _NEW_POSSIBLE_PDID, IngredientID, Store AS NEW_POSSIBLE_Supplier
                    			FROM ingredientInShops		
                    		) AS i
                                        
                    		ON
                    		i.IngredientID = t._NEW_IngredientID
                    		AND
                    		c.OLD_STORE = i.NEW_POSSIBLE_Supplier
                    		
                    	)
                    	
                    , NULL);
                    """, selected_Ingredient_ID, temp_PlanID, ingredientIndex);

            ArrayList<String> newPDIDResults = db.getSingleColumnQuery_ArrayList(query_PDID);
            if (newPDIDResults==null)
            {
                JOptionPane.showMessageDialog(null, "\n\n ERROR:\n\nUnable to retrieve pricing info!");
                // Change Jtable JComboBox Back To Original Value
                jTable.setValueAt(previous_IngredientName_JComboItem, rowEdited, columnEdited);

                setRowBeingEdited();
                return;
            }

            // Create  Statement for changing PDID (Ingredient_Index)
            String uploadQuery2 = String.format("""
                    UPDATE  ingredients_in_meal
                    SET PDID = %s
                    WHERE Ingredients_Index = %s AND PlanID = %s;

                        """, newPDIDResults.get(0), ingredientIndex, temp_PlanID);

            //  System.out.printf("\n\nQUERY PDID: \n'''%s''' \n\nPDID = %s \n\nUpload Query \n'''%s'''", query_PDID, newPDIDResults.get(0), uploadQuery2);

            //##################################################################################################
            // Upload IngredientName & NEW PDID
            //##################################################################################################
            if (!(db.uploadData_Batch(new String[]{uploadQuery, uploadQuery2})))
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

        // Ingredients Supplier Column
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
                        SELECT PDID 
                        FROM ingredientInShops
                        WHERE IngredientID = %s  AND Store = '%s';""", ingredientID, cellValue);

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
                        UPDATE  ingredients_in_meal
                        SET PDID = %s
                        WHERE Ingredients_Index = %s AND PlanID = %s;

                            """, newPDIDResults.get(0), ingredientIndex, temp_PlanID);

                System.out.printf("\n\nQUERY PDID: \n'''%s''' \n\nPDID = %s \n\nUpload Query \n'''%s'''", getPDIDQuery, newPDIDResults.get(0), uploadQuery);
            }
            else
            {
                // Create  Statement for changing PDID (Ingredient_Index)
                uploadQuery = String.format("""
                        UPDATE  ingredients_in_meal
                        SET PDID = NULL
                        WHERE Ingredients_Index = %s AND PlanID = %s;

                            """, ingredientIndex, temp_PlanID);
            }

            //##################################################################################################
            // Upload IngredientName & NEW PDID
            //##################################################################################################
            if (!(db.uploadData_Batch(new String[]{uploadQuery})))
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

        // Ingredients Quantity Column
        else if (columnEdited==getIngredientsTable_Quantity_Col())
        {
            System.out.printf("\ntableDataChange_Action() Quantity Being Changed");
            setRowBeingEdited();// HELLO

            updateTableValuesByQuantity(rowEdited, ingredientIndex, cellValue);
            return;
        }

    }

    //##################################################################################################################

    private void setUp()
    {
        setLayout(new GridBagLayout());
        if (db.isDatabaseConnected())
        {
            if (data!=null)
            {

                //###############################
                // Table Data
                //###############################
                super.data = data;

                super.columnNames = columnNames;
                super.columnDataTypes = db.getColumnDataTypes(tableName); //Column Data Types

                super.columnsInTable = columnNames.length;
                super.rowsInTable = data.length;

                //##############################
                // Table Setup
                //##############################
                super.tableSetup(data, columnNames, setIconsUp);

            }
            else
            {
                super.tableSetup(new Object[0][0], columnNames, setIconsUp);
            }
        }
    }

    public boolean isNonOfTheABoveInTable(Integer rowTriggeredAt, String attemptingTo)
    {
        if (rowsInTable > 0)
        {
            //Checking if the ingredient None Of the Above is already in the table
            for (int row = 0; row < rowsInTable; row++)
            {
                // Currently, changing  ingredient to NONE OF the ABOVE
                if (rowTriggeredAt!=null && row==rowTriggeredAt && rowsInTable > 1)
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

    //##################################################################################################################
    // Action Methods / ActionListener Events
    //##################################################################################################################

    @Override
    public void deleteTableAction() // Works
    {
        //##########################################
        // Delete table from database
        //##########################################

        /*
            Delete all ingredients from this meal (using mealID) from table "ingredients_in_meal"
            Delete meal from meals database
         */

        String query2 = String.format(" DELETE FROM ingredients_in_meal WHERE MealID = %s;", tempPlan_Meal_ID);
        String query4 = String.format(" DELETE FROM  meals WHERE MealID = %s;", tempPlan_Meal_ID);

        String[] query = new String[]{query2, query4};

        if (db.uploadData_Batch(query))
        {
            //##########################################
            /**
             * Hide JTable object & Collapsible OBJ
             *
             */
            //##########################################

            setVisibility(false); // hide collapsible Object


            outside_Update_MacrosLeft_Table();// update macrosLeft table, due to number deductions from this meal

            setObjectDeleted(true); // set this object as deleted

            JOptionPane.showMessageDialog(null, "Table Successfully Deleted!");
            return;
        }
        else
        {
            JOptionPane.showMessageDialog(null, "Table Un-Successfully Deleted! ");
            return;
        }
    }

    @Override
    protected void deleteRowAction(Object ingredientIndex)
    {
        System.out.printf("\n\nIngredient Index To Delete: %s", ingredientIndex);
        //#################################################
        // Delete Ingredient From Temp Meal
        //#################################################

        System.out.printf("\n\ningredientIndex: %s \nMealPlanID: %s", ingredientIndex, tempPlan_Meal_ID);

        String query = String.format("DELETE FROM ingredients_in_meal WHERE Ingredients_Index = %s AND PlanID = %s;", ingredientIndex, temp_PlanID);

        String[] queryUpload = new String[]{query};

        if (!(db.uploadData_Batch(queryUpload)))
        {
            JOptionPane.showMessageDialog(null, "Unable To delete Ingredient from Meal in Database");
        }

        //#################################################
        // Update Table Data
        //##################################################

        updateData();

        //HELLO REMOVE
        System.out.printf("\n\n#########################################################################");
    }

    public void completely_Deleted_JTables() // Works
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

    @Override
    protected void add_btn_Action()
    {
        //#########################################################
        // Check If There Is Already An Empty Row
        //#########################################################

        if (isNonOfTheABoveInTable(null, "add a new row!"))
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
        String getNextIndexQuery = String.format("SELECT MAX(Ingredients_Index) FROM %s ;", "ingredients_in_meal");

        String[] newIngredientsIndex = db.getSingleColumnQuery(getNextIndexQuery);

        if (newIngredientsIndex==null)
        {
            JOptionPane.showMessageDialog(null, "Unable to create new ingredient in table! \nUnable to generate ingredients_Index!!");
            return;
        }

        int newIngredientsIndex2 = Integer.parseInt(newIngredientsIndex[0]) + 1;
        //#########################################################
        // Insert into Database
        //#########################################################

        String query1 = String.format("""
                        
                INSERT INTO ingredients_in_meal
                (Ingredients_Index, MealID, PlanID, IngredientID, Quantity, PDID)
                                        
                VALUES
                (%s, %s, %s, %s, %s, %s); 
                        """, newIngredientsIndex2, tempPlan_Meal_ID, temp_PlanID, ingredientID, quantity, NoneOfTheAbove_PDID);

        if (!(db.uploadData_Batch(new String[]{query1})))
        {
            JOptionPane.showMessageDialog(null, "Un-able to Insert new row into the Database!");
            return;
        }

        //####################################################################
        //  Getting Row Data For New Ingredient Addition
        //####################################################################

        String query = String.format("""
                SELECT *
                FROM ingredients_in_meal_calculation
                WHERE Ingredients_Index = %s AND PlanID = %s;
                """, newIngredientsIndex2, temp_PlanID);


        System.out.printf("\n\n%s", query); // HELLO REMOVE

        ArrayList<ArrayList<Object>> results = db.get_Multi_ColumnQuery_Object(query);

        System.out.printf("\n\n\n\n%s\n\n", results);  // HELLO REMOVE

        if (results==null)
        {
            JOptionPane.showMessageDialog(null, "ERROR 2: Un-able to get Ingredient info for row in table!");
            return;
        }

        //#########################################
        //   Updating Ingredients In Meal Table
        //########################################

        setRowBeingEdited(); // stops endless loop being called for all cells being editted

        ArrayList<Object> ingredientsTable_UpdateData = results.get(0);
        super.updateTable(this, ingredientsTable_UpdateData, rowsInTable);

        setRowBeingEdited(); // stops endless loop being called for all cells being editted

        //##################################################################################
        // Resize Jtable & GUI with new Data
        //###################################################################################
        rowsInTable++;
        resizeObject();

        //##################################################################################
        // Update Table Data
        //###################################################################################
        updateData();
    }

    @Override
    public void refresh_Btn_Action(boolean updateTotalPlanTable)
    {
        //#######################################################
        //If Meal is not deleted from the temp plan in database
        //#######################################################
        if (getObjectDeleted())
        {

            //##########################################################
            // Re-Insert Meal Name Into Temp Meal
            //##########################################################

            String query1 = String.format("""
                                        
                    INSERT INTO meals (MealID, PlanID, Meal_Name) VALUES
                    (%s, %s,'%s'); """, tempPlan_Meal_ID, temp_PlanID, mealName);

            if (!(db.uploadData_Batch(new String[]{query1})))
            {
                JOptionPane.showMessageDialog(null, "Unable To re-insert deleted meals with action requested \nbeing refresh!");
                return;
            }

            setVisibility(true);
            setObjectDeleted(false);
        }
        //######################################
        // Reset Temp Data For Meal in Database
        //######################################

        // delete all ingredients in tempMeal
        String query1 = String.format("DELETE FROM ingredients_in_meal WHERE MealID = %s;", tempPlan_Meal_ID);

        //####################################################
        // Transferring this plans Ingredients to Temp-Plan
        // Resetting the Database
        //####################################################

        //Copy the current data for this meal in the database
        String query2 = String.format("DROP TABLE IF EXISTS temp_ingredients_in_meal;");
        String query3 = String.format(""" 
                CREATE table temp_ingredients_in_meal  AS
                SELECT *
                FROM ingredients_in_meal i                                                      
                WHERE PlanID= %s AND MealID = %s; """, planID, mealID);

        String query4 = String.format("UPDATE temp_ingredients_in_meal  SET PlanID = %s; ", temp_PlanID);
        String query5 = String.format("UPDATE temp_ingredients_in_meal  SET MEALID = %s; ", tempPlan_Meal_ID);

        String query8 = String.format("INSERT INTO ingredients_in_meal SELECT * FROM temp_ingredients_in_meal; ");
        String query9 = String.format("DROP TABLE temp_ingredients_in_meal; ");

        String[] query_Temp_Data = new String[]{query1, query2, query3,
                //query3_2, query3_3,
                query4, query5, query8, query9};

        if (!(db.uploadData_Batch(query_Temp_Data)))
        {
            JOptionPane.showMessageDialog(null, "ERROR: \nCannot revert database to previous data!");
            return;
        }

        //##############################
        /** //HELLO
         * Reset Ingredients Table Data
         * However, the index changes everytime we delete and upload,
         */
        //##############################

        //Reset this tables data (ingredients_in_meal table)
        tableModel_Setup(getData(), getColumnNames());

        //##############################
        // Reset Meal Total  Table Data
        //##############################

        // Reset Total View Table
        if (total_Meal_Table!=null)
        {
            System.out.printf("\n\nReset Table Data:\n\n %s", Arrays.deepToString(total_Meal_Table.getData()));
            total_Meal_Table.tableModel_Setup(total_Meal_Table.getData(), total_Meal_Table.getColumnNames());
        }

        //##############################
        // Update Other Tables Data
        //##############################

        if (updateTotalPlanTable)
        {
            outside_Update_MacrosLeft_Table();
        }
    }

    @Override
    public boolean saveDataAction()
    {

        if (!(getMealInDB()))     // If Meal Not In Original PlanID Add To PlanID
        {
            System.out.printf(String.format("\n\n\\Save Data Action() Meal Not in Original DB"));

            //######################################################################
            // Add Meal To Original Plan
            //######################################################################
            String uploadQuery = String.format(" INSERT INTO meals (PlanID, Meal_Name) VALUES (%s,'%s')", planID, mealName);

            //#####################################
            // If Upload Un-Successful
            //#####################################
            if (!(db.uploadData_Batch(new String[]{uploadQuery})))
            {
                JOptionPane.showMessageDialog(null, "\n\nUnable To Create Meal In Original Plan!!");
                return false;
            }

            //######################################################################
            // Set Meals MealID by Retrieving it from DB
            //######################################################################
            // Get MealID of meal in plan

            System.out.printf(String.format("\n\nSelect MealID FROM Meals WHERE MealName ='%s' AND PlanID = %s;", mealName, planID));
            String[] orginalMealID_Result = db.getSingleColumnQuery(String.format("Select MealID FROM Meals WHERE Meal_Name ='%s' AND PlanID = %s;",
                    mealName, planID));


            if (orginalMealID_Result==null)
            {
                JOptionPane.showMessageDialog(null, "\n\nUnable To Get MealID in Plan to Update Meal");
                return false;
            }

            //tempPlan_Meal_ID = mealID;//HELLO What does this do?
            mealID = Integer.valueOf(orginalMealID_Result[0]);

            System.out.printf(String.format("\n\nPlanId: %s \nTempPlanID: %s \n\nMealID: %s  \nTempMealID: %s ",
                    planID, temp_PlanID, mealID, tempPlan_Meal_ID));

            //##########################################
            // Meal Successfully Added TO DB
            //##########################################
            set_Meal_In_DB(true);
        }

        //HELLO FOR OPTIMISATION THIS STEP SHOULDNT BE DONE IF THE STEP ABOVE IS DONE
        //##########################################
        // Copying Temp-Plan Meal Data to Real Plan
        //##########################################

        // delete all ingredients in the real meal
        String query1 = String.format("DELETE FROM ingredients_in_meal WHERE MealID = %s;", mealID);

        //####################################################
        // Transferring Data From Temp-Plan To Real Plan
        //####################################################

        String query2 = String.format("DROP TABLE IF EXISTS temp_ingredients_in_meal;");
        String query3 = String.format(""" 
                CREATE table temp_ingredients_in_meal  AS
                SELECT *
                FROM ingredients_in_meal i                                                      
                WHERE PlanID= 1 AND MealID = %s; """, tempPlan_Meal_ID);

        String query4 = String.format("UPDATE temp_ingredients_in_meal  SET PlanID = %s;", planID);
        String query5 = String.format("UPDATE temp_ingredients_in_meal  SET MEALID = %s;", mealID);

        String query8 = String.format(" INSERT INTO ingredients_in_meal SELECT * FROM temp_ingredients_in_meal;");
        String query9 = String.format(" DROP TABLE temp_ingredients_in_meal;");

        String[] query_Temp_Data = new String[]{query1, query2, query3, query4, query5, query8, query9};

        //##########################################
        // If Upload Un-Successful
        //##########################################
        if (!(db.uploadData_Batch(query_Temp_Data)))
        {
            JOptionPane.showMessageDialog(null, "ERROR: \nUnable to update table to Database!!");
            return false;
        }

        //##########################################
        // Changing Ingredients In Meal Table Model
        //##########################################
        String tableInQuery = "ingredients_in_meal_calculation";

        String query = String.format("Select * from %s WHERE MealID = %s;", tableInQuery, tempPlan_Meal_ID);
        Object[][] ingredients_Data = db.getTableDataObject(query, tableInQuery);

        if (ingredients_Data!=null)
        {
            setTableModelData(ingredients_Data);
        }
        else
        {
            return false;
        }

        //##########################################
        // Changing Total  Ingredients Table Model
        //##########################################

        if (total_Meal_Table!=null)
        {
            // Setting totals tables Data model to new data
            String totalTableQuery = String.format("SELECT *  FROM total_meal_view WHERE MealID = %s;", tempPlan_Meal_ID);

            Object[][] totalTableData = db.getTableDataObject(totalTableQuery, "total_meal_view");
            if (totalTableData!=null)
            {
                total_Meal_Table.setTableModelData(totalTableData);
            }
            else
            {
                JOptionPane.showMessageDialog(null, "ERROR: \nUn-able to Update Totals Table!");
                return false;
            }
        }

        JOptionPane.showMessageDialog(null, "Table Successfully Updated!");
        return true;
    }

    //##################################################################################################################
    // Update Table / Accessor Methods
    //##################################################################################################################
    public void updateData()
    {
        update_TotalMeal_Table();
        outside_Update_MacrosLeft_Table();
    }

    private void updateTableValuesByQuantity(int row, Object ingredients_Index, Object quantity)
    {
        setRowBeingEdited();

        //#######################################
        // Updating Quantity Value in temp plan
        //########################################

        String query1 = String.format("""
                UPDATE  ingredients_in_meal
                SET Quantity = %s 
                WHERE PlanID = %s  AND Ingredients_Index = %s;
                """, quantity, temp_PlanID, ingredients_Index);

        //HELLO DELETE
        // System.out.printf("\n\nupdateTableValuesByQuantity() \nQuery: \n\n%s", query1);

        if (!(db.uploadData_Batch(new String[]{query1})))
        {
            JOptionPane.showMessageDialog(null, "Un-able to Update row based on cell value!");

            setRowBeingEdited();
            return;
        }


        //####################################################################
        //  Update Ingredients table based on DB
        //####################################################################

        String query = String.format("SELECT  * FROM ingredients_in_meal_calculation WHERE  ingredients_Index = %s AND PlanID = %s;",
                ingredients_Index, temp_PlanID);

        // HELLO REMOVE
        // System.out.printf("\n\n%s", query);

        ArrayList<ArrayList<Object>> ingredientsUpdateData = db.get_Multi_ColumnQuery_Object(query);

        System.out.printf("\n\n\n\n%s\n\n", ingredientsUpdateData);  // HELLO REMOVE

        if (ingredientsUpdateData==null)
        {
            JOptionPane.showMessageDialog(null, "ERROR 2: Un-able to Update Ingredient in table row!");

            setRowBeingEdited();
            return;
        }

        //##########################################################################
        //   Updating Ingredients In Meal Table
        //##########################################################################

        ArrayList<Object> ingredientsTable_UpdateData = ingredientsUpdateData.get(0);
        super.updateTable(this, ingredientsTable_UpdateData, row);

        if (jTable.getValueAt(row, getIngredientsTable_IngredientsName_Col()).equals("None Of The Above"))
        {
            jTable.setValueAt("No Shop", row, getIngredientsTable_Supplier_Col());
        }

        //##########################################################################
        //   Updating Total  Meal Table
        ///##########################################################################
        setRowBeingEdited();

        updateData();
    }

    private void update_TotalMeal_Table()
    {
        //##########################################################################
        //   Updating Total  Meal Table
        ///##########################################################################
        if (total_Meal_Table!=null)
        {
            setRowBeingEdited();

            String totalMealTableQuery = String.format("SELECT  * FROM total_meal_view   WHERE  MealID = %s; ", tempPlan_Meal_ID);

            ArrayList<ArrayList<Object>> totalMealData = db.get_Multi_ColumnQuery_Object(totalMealTableQuery);

            if (totalMealData==null)
            {
                JOptionPane.showMessageDialog(null, "ERROR: \nUn-able to Update Totals Table!");

                setRowBeingEdited();
                return;
            }
            else
            {
                ArrayList<Object> totalMeal_UpdateData = totalMealData.get(0);
                super.updateTable(total_Meal_Table, totalMeal_UpdateData, 0);
            }

            setRowBeingEdited();
        }
    }

    public void outside_Update_MacrosLeft_Table()
    {

        //##########################################################################
        //   Updating Total  Meal Table
        ///##########################################################################
        if (macrosLeft_Table!=null)
        {
            setRowBeingEdited();

            String macrosLeftQuery = String.format("select * from planMacrosLeft WHERE PlanID = %s", temp_PlanID);

            ArrayList<ArrayList<Object>> macrosLeftTableData = db.get_Multi_ColumnQuery_Object(macrosLeftQuery);

            if (macrosLeftTableData==null)
            {
                JOptionPane.showMessageDialog(null, "ERROR: \nUn-able to MacrosLeft Table!");

                setRowBeingEdited();
                return;
            }
            else
            {
                ArrayList<Object> macrosLeft_UpdateData = macrosLeftTableData.get(0);
                super.updateTable(macrosLeft_Table, macrosLeft_UpdateData, 0);
            }
            setRowBeingEdited();
            return;
        }
    }

    public void internal_Update_MacrosLeft_Table()
    {
        //##########################################################################
        //   Updating MacrosLeft_Table
        ///##########################################################################

        setRowBeingEdited();

        String macrosLeftQuery = String.format("select * from planMacrosLeft WHERE PlanID = %s", temp_PlanID);

        ArrayList<ArrayList<Object>> macrosLeftTableData = db.get_Multi_ColumnQuery_Object(macrosLeftQuery);

        if (macrosLeftTableData==null)
        {
            JOptionPane.showMessageDialog(null, "ERROR: \nUn-able to MacrosLeft Table!");

            setRowBeingEdited();
            return;
        }
        else
        {
            ArrayList<Object> macrosLeft_UpdateData = macrosLeftTableData.get(0);
            super.updateTable(this, macrosLeft_UpdateData, 0);
        }
        setRowBeingEdited();
        return;

    }

    public void internal_Update_MacroTargets()
    {
        //##########################################################################
        //  Update MacrosLeft Table Data
        ///##########################################################################

        setRowBeingEdited();

        String macrosTargetQuery = String.format("select * from plan_Macro_Target_Calculations WHERE PlanID = %s", temp_PlanID);

        ArrayList<ArrayList<Object>> macroTargetsData = db.get_Multi_ColumnQuery_Object(macrosTargetQuery);

        if (macroTargetsData == null)
        {
            JOptionPane.showMessageDialog(null, "ERROR: \nUn-able to MacrosLeft Table!");

            setRowBeingEdited();
            return;
        }
        else
        {
            ArrayList<Object> macrosTarget_UpdateData = macroTargetsData.get(0);
            updateTable(this, macrosTarget_UpdateData, 0);
        }
        setRowBeingEdited();
        return;
    }

    //##################################################################################################################
    // Mutator Methods
    //##################################################################################################################

    public void setRowBeingEdited()
    {
        rowBeingEdited = !rowBeingEdited; // flip it
    }

    public void setObjectDeleted(boolean deleted)
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

    public void set_TriggerColumns(Integer[] columns)
    {
        set_IngredientsTable_Index_Col(columns[0]);
        set_IngredientsTable_ID_Col(columns[1]);
        set_IngredientsTable_Quantity_Col(columns[2]);
        set_IngredientsTable_IngredientsName_Col(columns[3]);
        set_IngredientsTable_Supplier_Col(columns[4]);

        triggerColumns = new ArrayList(Arrays.asList(getIngredientsTable_Index_Col(), getIngredientsTable_ID_Col(),
                getIngredientsTable_Quantity_Col(), getIngredientsTable_IngredientsName_Col(), getIngredientsTable_Supplier_Col()));
    }

    //###############################################################################
    public void set_IngredientsTable_Index_Col(int value)
    {
        ingredientsTable_Index_Col = value;
    }

    public void set_IngredientsTable_ID_Col(int value)
    {
        ingredientsTable_ID_Col = value;
    }

    public void set_IngredientsTable_Quantity_Col(int value)
    {
        ingredientsTable_Quantity_Col = value;
    }

    public void set_IngredientsTable_IngredientsName_Col(int value)
    {
        ingredientsTable_IngredientsName_Col = value;
    }

    public void set_IngredientsTable_Supplier_Col(int value)
    {
        ingredientsTable_Supplier_Col = value;
    }

    //##################################################################################################################
    // Accessor Methods
    //##################################################################################################################
    public int getIngredientsTable_Index_Col()
    {
        return ingredientsTable_Index_Col;
    }

    public int getIngredientsTable_ID_Col()
    {
        return ingredientsTable_ID_Col;
    }

    public int getIngredientsTable_Quantity_Col()
    {
        return ingredientsTable_Quantity_Col;
    }

    public int getIngredientsTable_IngredientsName_Col()
    {
        return ingredientsTable_IngredientsName_Col;
    }

    public int getIngredientsTable_Supplier_Col()
    {
        return ingredientsTable_Supplier_Col;
    }

    //###############################################################################
    public boolean getMealInDB()
    {
        return meal_In_DB;
    }

    public Integer getMealID()
    {
        return mealID;
    }

    public Integer getTempMealID()
    {
        return tempPlan_Meal_ID;
    }

    public boolean getObjectDeleted()
    {
        return objectDeleted;
    }
    //##################################################################################################################
}
