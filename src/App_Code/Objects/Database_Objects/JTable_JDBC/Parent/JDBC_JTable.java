package App_Code.Objects.Database_Objects.JTable_JDBC.Parent;

// Packages to import
// https://stackoverflow.com/questions/10347983/making-a-jbutton-clickable-inside-a-jtable
//http://tips4java.wordpress.com/2009/07/12/table-button-column/

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
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


public class JDBC_JTable extends JPanel
{
    protected MyJDBC db;
    protected JTable jTable = new JTable();
    protected DefaultTableModel tableModel;

    protected static GridBagConstraints gbc = new GridBagConstraints(); //HELLO DELETE
    protected boolean DEBUG = false;

    protected Container parentContainer;
    protected JScrollPane scrollPane = new JScrollPane();

    protected boolean tableInitilized = false;
    protected HashMap<Integer, ArrayList<String>> jcomboMap = new HashMap<>();

    protected ArrayList<String> colAvoidCentering = new ArrayList<>();
    protected ArrayList<Integer> unEditableColumns = new ArrayList<>();

    protected Integer deleteColumn = null;
    protected ArrayList<String> columnsToHide = null;

    protected int rowsInTable = 0, columnsInTable = 0;
    protected String databaseName, tableName;
    protected String[] columnDataTypes, columnNames;
    protected Object[][] data;

    protected Object previousJComboItem;
    protected Object selected_Jcombo_Item;

    //##################################################################################################################
    protected LinkedHashMap<String, Integer[]> columnNamesAndPositions = new LinkedHashMap<>();
     /*
        Array Pos 1 = Original Position in JTable Data
        Array Pos 2 = Position after columns hidden
     */

    //###########################################
    // Ingredients In Table Columns
    //###########################################

    //##################################################################################################################
    public JDBC_JTable()
    {
    }

    public JDBC_JTable(MyJDBC db, Container parentContainer, String databaseName,
                       String tableName, ArrayList<String> unEditableColumns, ArrayList<String> colAvoidCentering)
    {
        this.db = db;
        this.parentContainer = parentContainer;
        this.tableName = tableName;
        this.databaseName = databaseName;
        this.unEditableColumns = getPosOfColumnsByNames(unEditableColumns);

        this.colAvoidCentering = colAvoidCentering;

        setLayout(new GridBagLayout());

        //###############################
        // Getting Table Data
        //###############################

        String query = String.format("select * from %s;", tableName);
        data = db.getTableDataObject(query, tableName);

        if (data!=null)
        {
            //###############################
            // Getting Column Names
            //###############################
            String columnNamesQuery = String.format("""                    
                    select column_name
                    from information_schema.columns
                      where table_schema = '%s'
                    and table_name = '%s'
                    order by ordinal_position;                      
                                           """, databaseName, tableName);

            //###############################
            // Table Data
            //###############################
            columnDataTypes = db.getColumnDataTypes(tableName); //Column Data Types
            columnNames = db.getSingleColumnQuery(columnNamesQuery); // Collumn Names

            columnsInTable = columnNames.length;
            rowsInTable = data.length;

            //##############################
            // Table Setup
            //##############################

            tableSetup(data, columnNames, true);
        }
        else
        {
            tableSetup(new Object[0][0], columnNames, true);
        }
    }


    protected ArrayList<Integer> getPosOfColumnsByNames(ArrayList<String> xColumnNames)
    {
        ArrayList<Integer> columnPositions = new ArrayList<Integer>();

        if (xColumnNames!=null)
        {
            int pos = 0;
            for (String columnName : columnNames)
            {
                Integer columnNamePos = xColumnNames.indexOf(columnName);
                if(columnNamePos != -1)
                {
                    columnPositions.add(pos);
                }
                pos++;
            }
        }

        return columnPositions;
    }


    //##################################################################################################################
    // Set Up Methods
    //##################################################################################################################

    protected void iconSetup()
    {
        //###################################################################################
        // Table Icon Setup
        //###################################################################################


        IconPanel iconPanel = new IconPanel(3, 10, "East");
        JPanel iconPanelInsert = iconPanel.getIconJpanel();

        addToContainer(this, iconPanel.getIconAreaPanel(), 0, 0, 1, 1, 0.25, 0.25, "horizontal", "east");

        //##########################
        //Add BTN
        //##########################
        IconButton add_Icon_Btn = new IconButton("src/images/add/add.png", "", 40, 40, 40, 40, "centre", "right");
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

        IconButton refresh_Icon_Btn = new IconButton("src/images/refresh/+refresh.png", "", 40, 40, 40, 40,
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

        IconButton saveIcon_Icon_Btn = new IconButton("src/images/save/save.png", "", 40, 40, 40, 40,
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

        IconButton deleteIcon_Icon_Btn = new IconButton("src/images/delete/+delete.png", "", 50, 40, 50, 40,
                "centre", "right"); // btn text is useless here , refactor
        //deleteIcon_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));
        deleteIcon_Icon_Btn.makeBTntransparent();

        JButton delete_btn = deleteIcon_Icon_Btn.returnJButton();


        delete_btn.addActionListener(ae -> {

            delete_Btn_Action();
        });

        iconPanelInsert.add(delete_btn);
    }

    protected void tableSetup(Object[][] data, String[] columnNames, boolean setIconsUp)
    {
        if (setIconsUp)
        {
            iconSetup();
        }

        //###################################################################################
        // Table Setup
        //###################################################################################
        //Creating Table

        jTable = new JTable();
        //instance table model

        tableModel_Setup(data, columnNames); // sets Jtable Model
        jTable.setRowHeight(jTable.getRowHeight() + 15);
        jTable.setFillsViewportHeight(true);

        // setting text size
        setTableHeaderFont(new Font("Dialog", Font.BOLD, 16));

        // setting header size
        jTable.getTableHeader().setPreferredSize(new Dimension(100, 50));

        //################################################################################
        // Adding Jtable to JscrollPane
        //################################################################################

        // Create the scroll pane and add the table to it, has to be added to a scrollpane as otherwise it doesnt work

        scrollPane.setViewportView(jTable);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        addToContainer(this, scrollPane, 0, 1, 1, 1, 0.25, 0.25, "both", "");

        //#################################################################################
        // Sizing
        //#################################################################################
        resizeObject();
    }

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

        if (tableInitilized)  //first time this method is called, special columns aren't defined
        {
            if (deleteColumn!=null)
            {
                setupDeleteBtnColumn(deleteColumn); // specifying delete column
            }
            if (columnsToHide!=null)
            {
                SetUp_HiddenTableColumns(columnsToHide);
            }

            // Setting up JcomboBox Field
            for (Integer key : jcomboMap.keySet())
            {
                setUpJComboColumn(key, "IngredientName", jcomboMap.get(key));
            }
        }
        else
        {
            tableInitilized = true;
        }
        resizeObject();
    }

    //##################################################################################################################
    protected Boolean areYouSure(String process)
    {
        int reply = JOptionPane.showConfirmDialog(null, String.format("Are you sure you want to %s, \nany unsaved changes will be lost in this Table! \nDo you want to %s?", process, process),
                "Restart Game", JOptionPane.YES_NO_OPTION); //HELLO Edit

        if (reply==JOptionPane.NO_OPTION || reply==JOptionPane.CLOSED_OPTION)
        {
            return false;
        }
        return true;
    }

    protected void add_btn_Action()
    {
        Object[] rowData = new Object[columnsInTable];

        //#######################################
        // Adding Row Data to Table
        //#######################################

        tableModel.addRow(rowData);

        //#######################################
        // Configuring Row ID
        //#######################################

        int tableRow = rowsInTable==0 ? 0:rowsInTable;

        if (rowsInTable==0)
        {
            // Get max id of last item in DB and +1
            String[] queryResults = db.getSingleColumnQuery(String.format("SELECT MAX(%s) FROM %s;", columnNames[0], tableName));
            Integer id = Integer.parseInt(queryResults[0]) + 1;

            tableModel.setValueAt(id, tableRow, 0);
        }
        else
        {
            int lastRowID = (Integer) jTable.getModel().getValueAt(tableModel.getRowCount() - 2, 0);
            tableModel.setValueAt(lastRowID + 1, tableRow, 0);
        }

        //#######################################
        // Adding blank data based on data type
        //######################################
        for (int col = 1; col < columnsInTable; col++)
        {
            if (col==deleteColumn)
            {
                tableModel.setValueAt("Delete Row", tableRow, col);
                continue;
            }
            else if (jcomboMap.containsKey(col))
            {
                tableModel.setValueAt("None of the Above", tableRow, col);
                continue;
            }

            String colDataType = columnDataTypes[col];
            try
            {
                // Convert to appropriate datatype
                switch (colDataType)
                {
                    case "varchar":
                        tableModel.setValueAt("", tableRow, col);
                        break;
                    case "tinyint":
                        tableModel.setValueAt(false, tableRow, col);
                        break;
                    case "int":
                        tableModel.setValueAt(0, tableRow, col);
                        break;
                    case "decimal": // HELLO CHANGE
                        tableModel.setValueAt(new BigDecimal(0.00), tableRow, col);
                        //tableModel.setValueAt(0.00, tableRow, col);
                        break;
                    case "bigint":
                        tableModel.setValueAt(Long.valueOf(0), tableRow, col);
                    default:
                        throw new Exception();
                }
            }
            catch (Exception e)
            {
                System.out.printf("\n\nUn-accountable Data type JDBC_JTable(),add_Btn.addActionListener() \n%s", e);
            }
        }

        //#######################################
        // Resize Jtable & GUI with new Data
        //#######################################
        rowsInTable++;
        resizeObject();
    }

    protected void save_Btn_Action()
    {
        if (rowsInTable > 0)
        {
            if (!(saveDataAction(true)))
            {
                JOptionPane.showMessageDialog(null, "Error, uploading  table data to Database!");
            }

        }
        else
        {
            JOptionPane.showMessageDialog(null, " Table is empty! \n To save Table Data, please add rows to the table!");
        }

    }

    //##########################################
    // Overwirte Methods
    //##########################################

    protected void refresh_Btn_Action(boolean updateTotalPlanTable)
    {

    }

    protected boolean saveDataAction(boolean showMessage)
    {
        return false;
    }

    protected void delete_Btn_Action()
    {
        if (areYouSure("Delete"))
        {
            deleteTableAction();
        }
    }

    //##########################################
    // Overwirte Methods
    //##########################################
    protected void deleteTableAction()
    {

    }

    protected void deleteRowAction(Object ingredients_MealID, int modelRow)
    {
        ((DefaultTableModel) jTable.getModel()).removeRow(modelRow);

        rowsInTable--; // -1 from row count number
        resizeObject();
    }

    protected void tableDataChange_Action()
    {

    }

    //##################################################################################################################
    // Accessor Methods
    //##################################################################################################################
    protected boolean getTableInitilized()
    {
        return tableInitilized;
    }

    protected HashMap<Integer, ArrayList<String>> getJcomboMap()
    {
        return jcomboMap;
    }

    protected Integer getDeleteBTN_Col()
    {
        return deleteColumn;
    }


    protected Object[][] getData()
    {
        return data;
    }

    protected String[] getColumnNames()
    {
        return columnNames;
    }


    protected JTable getTable()
    {
        return jTable;
    }

    protected int getNoOfColumns()
    {
        return columnsInTable;
    }

    protected ArrayList<String> getColumnsToHide()
    {
        return columnsToHide;
    }

    // EDIT REMOVE myJTable  Param
    public void updateTable(JDBC_JTable myJTable, ArrayList<Object> updateData, int updateRow)
    {
        //########################################################################
        // Updating Table Info
        //#######################################################################
        int pos = 0;
        for (Map.Entry<String, Integer[]> jTableColumn : columnNamesAndPositions.entrySet())
        {
            //############################################
            // Extracting Info
            //############################################
            String columnName = jTableColumn.getKey();

            /*
             Pos 1 column original position in JTable Data
             Pos 2 column position in JTable after columns are hidden
            */
            Integer[] columnPositionsList = jTableColumn.getValue();
            Integer columnPosAfterHidingColumns = columnPositionsList[1];

            //############################################
            // Update Table
            //############################################
            if (columnPosAfterHidingColumns!=null) // this column isn't visible in the JTable Data
            {
                jTable.setValueAt(updateData.get(pos), updateRow, columnPosAfterHidingColumns);
            }

            //############################################
            pos++;
        }
    }

    /*
     As columns are hidden the position changes by -1 for the next time its called
   */
    protected void SetUp_HiddenTableColumns(ArrayList<String> columnsToHide)
    {
        int pos = 0, numberOfColumnsHidden = 0, jTablePosAfterHiding = 0;

        for (Map.Entry<String, Integer[]> jTableColumn : columnNamesAndPositions.entrySet())
        {
            jTablePosAfterHiding = pos - numberOfColumnsHidden;

            //########################################################################
            // Extracting Info
            //#######################################################################
            String columnName = jTableColumn.getKey();

            /*
             Pos 1 column original position in JTable Data
             Pos 2 column position in JTable after columns are hidden
            */
            Integer[] columnPositionsList = jTableColumn.getValue();

            //#######################################################################
            // Hide Or Update Column Pos After Hiding
            //#######################################################################

            if (columnsToHide.contains(columnName)) // Hide Column In JTable
            {
                columnPositionsList[1] = null; // No Longer In The JTable so position is null
                columnNamesAndPositions.replace(columnName, columnPositionsList); // Update position

                jTable.removeColumn(jTable.getColumnModel().getColumn(jTablePosAfterHiding)); // Hide Column in JTable

                numberOfColumnsHidden++;
            }
            else // Adjust current column position
            {
                columnPositionsList[1] = jTablePosAfterHiding;
                columnNamesAndPositions.replace(columnName, columnPositionsList);
            }
            //#######################################################################

            pos++;
        }
    }

    //##################################################################################################################
    protected void set_IngredientsTable_DeleteBTN_Col(int deleteColumn)
    {
        this.deleteColumn = deleteColumn;
    }

    protected void setTableInitilized()
    {
        tableInitilized = true;
    }

    protected void setTableModelData(Object[][] tableModelData)
    {
        this.data = tableModelData;
        resizeObject();
    }

    public void setTableHeaderFont(Font font)
    {
        jTable.getTableHeader().setFont(font);
    }

    public void setTableTextFont(Font font)
    {
        jTable.setFont(font);
    }

    public void setupDeleteBtnColumn(int deleteBtnColumn)
    {
        deleteColumn = deleteBtnColumn;

        Action delete = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                int modelRow = Integer.parseInt(e.getActionCommand());
                deleteRowAction(null, modelRow); // command to update db
            }
        };
        Working_ButtonColumn2 workingButtonColumn = new Working_ButtonColumn2(jTable, delete, deleteBtnColumn);
        workingButtonColumn.setMnemonic(KeyEvent.VK_D);
    }

    public void setUpJComboColumn(int col, String type, ArrayList<String> items)
    {
        jcomboMap.put(col, items);

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
                            selected_Jcombo_Item = ie.getItem();
                            previousJComboItem = selected_Jcombo_Item;
                        }
                        else
                        {
                            previousJComboItem = selected_Jcombo_Item;
                            selected_Jcombo_Item = ie.getItem();
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


    //##################################################################################################################
    public class ComboBoxTableCellRenderer extends JComboBox implements TableCellRenderer
    {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            setSelectedItem(value);
            return this;
        }

    }
    //##################################################################################################################

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
    protected void resizeObject()
    {
        jTable.revalidate();
        jTable.setPreferredScrollableViewportSize(
                new Dimension(
                        jTable.getPreferredSize().width,
                        //jTable.getRowHeight() * rowsInTable +20))
                        jTable.getRowHeight() * rowsInTable)); // get rid of 20, to have the border be exact

        scrollPane.revalidate(); // reshapes scrollpane
        revalidate();

        if (parentContainer!=null)
        {
            parentContainer.revalidate();
        }
    }

    protected void initColumnSizes()
    {
        Object[] longValues = {"Jane", "Kathy",
                "None of the above",
                new Integer(20), Boolean.TRUE};

        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;

        TableCellRenderer headerRenderer =
                jTable.getTableHeader().getDefaultRenderer();

        for (int i = 0; i < 5; i++)
        {
            column = jTable.getColumnModel().getColumn(i);

            comp = headerRenderer.getTableCellRendererComponent(
                    null, column.getHeaderValue(),
                    false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;


            comp = jTable.getDefaultRenderer(tableModel.getColumnClass(i)).
                    getTableCellRendererComponent(
                            jTable, longValues[i],
                            false, false, 0, i);
            cellWidth = comp.getPreferredSize().width;

            if (DEBUG)
            {
                System.out.println("Initializing width of column "
                        + i + ". "
                        + "headerWidth = " + headerWidth
                        + "; cellWidth = " + cellWidth);


            }

            column.setPreferredWidth(Math.max(headerWidth + 20, cellWidth)); //HELLO I added 20

        }
    }

    /**
     * This needs to be done before hiding columns
     *
     * @param alignment
     * @param columnByNameToSkipCentering
     */
    protected void setCellsAlignment(int alignment, ArrayList<String> columnByNameToSkipCentering)
    {
        /*DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(alignment);

        TableModel tableModel = jTable.getModel();

        for (int columnIndex = 0; columnIndex < tableModel.getColumnCount(); columnIndex++)
        {
            if (skipRows!=null && skipRows.contains(columnIndex))
            {
                continue;
            }

            jTable.getColumnModel().getColumn(columnIndex).setCellRenderer(rightRenderer);
        }*/


        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(alignment);

        int pos = -1;
        for (String columnName : columnNames)
        {
            pos++;

            if (columnByNameToSkipCentering!=null && columnByNameToSkipCentering.contains(columnName))
            {
                continue;
            }

            jTable.getColumnModel().getColumn(pos).setCellRenderer(rightRenderer); // Center Column Data
        }
    }

    //##################################################################################################################

    protected static void addToContainer(Container container, Component addToContainer, int gridx, int gridy, int gridwidth,
                                         int gridheight, double weightx, double weighty, String fill, String anchor)
    {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.weightx = weightx;
        gbc.weighty = weighty;

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

        switch (anchor.toLowerCase())
        {
            case "east":
                gbc.anchor = GridBagConstraints.EAST;
                break;
            case "west":
                gbc.anchor = GridBagConstraints.WEST;
                break;
            case "north":
                gbc.anchor = GridBagConstraints.NORTH;
                break;
            case "south":
                gbc.anchor = GridBagConstraints.SOUTH;
                break;
        }

        container.add(addToContainer, gbc);

    }
    //##################################################################################################################
}
