package App_Code.Objects.Database_Objects.JTable_JDBC;

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
    //##############################################
    // Objects
    //##############################################
    protected MyJDBC db;
    protected Container parentContainer;
    protected JScrollPane scrollPane = new JScrollPane();
    protected static GridBagConstraints gbc = new GridBagConstraints(); //HELLO DELETE

    //##############################################
    // Table Customization Variables
    //##############################################
    protected JTable jTable = new JTable();
    protected DefaultTableModel tableModel;
    protected Object[][] data;
    protected int rowsInTable = 0, columnsInTable = 0;
    protected String databaseName, tableName;
    protected String[] columnDataTypes, columnNames;

    protected ArrayList<String> colAvoidCentering = new ArrayList<>();
    protected ArrayList<Integer> unEditableColumns = new ArrayList<>();
    protected ArrayList<String> columnsToHide = new ArrayList<>();

    //##############################################
    protected boolean tableInitialised = false;

    protected HashMap<Integer, ArrayList<String>> jcomboMap = new HashMap<>();



    protected Integer deleteColumn = null;
    protected Object previousJComboItem;
    protected Object selected_Jcombo_Item;
    //##############################################

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

    public JDBC_JTable(MyJDBC db, Container parentContainer, boolean setIconsUp, String databaseName, String tableName, Object[][] data,
                       String[] columnNames,
                      ArrayList<String> unEditableColumns, ArrayList<String> colAvoidCentering,  ArrayList<String> columnsToHide)
    {
        setLayout(new GridBagLayout());

        //##############################################################
        // Variables
        //##############################################################
        this.db = db;
        this.data = data;

        this.parentContainer = parentContainer;
        this.databaseName = databaseName;
        this.tableName = tableName;

        this.columnDataTypes = db.getColumnDataTypes(tableName); //Column Data Types
        this.columnsInTable = columnNames.length;
        this.rowsInTable = data != null ? data.length : 0;

        //##############################################################
        // Column Names & Their Original Positions
        //##############################################################

        this.columnNames = columnNames;

        // Adding column names and their original positions to the hashmap
        for (int pos = 0; pos < columnNames.length; pos++)
        {
            columnNamesAndPositions.put(columnNames[pos], new Integer[]{pos, pos});
        }
        //##############################################################

        this.colAvoidCentering = colAvoidCentering;
        this.columnsToHide = columnsToHide;

        //##############################################################
        // Get UnEditable Column Positions By Name
        //##############################################################
        ArrayList<Integer> columnPositions = new ArrayList<Integer>();

        if (unEditableColumns != null)
        {
            for(String columnName: unEditableColumns)
            {
                columnPositions.add(columnNamesAndPositions.get(columnName)[0]);
            }
        }

        this.unEditableColumns = columnPositions;

        //##############################################################
        // Table Setup With Table Data
        //##############################################################
        if (data !=null)
        {
            tableSetup(data, columnNames, setIconsUp);
        }
        else
        {
            tableSetup(new Object[0][0], columnNames, setIconsUp);
        }

        //##############################################################
        // Hide Columns
        //##############################################################
        SetUp_HiddenTableColumns(columnsToHide);
    }

    // Can be replaced with Hashmap retreival of positions



    //##################################################################################################################
    // Set Up Methods
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

    protected void extraSetup()
    {

    }


    protected void tableSetup(Object[][] data, String[] columnNames, boolean setIconsUp)
    {
        extraSetup();

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

        if (tableInitialised)  //first time this method is called, special columns aren't defined
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
            tableInitialised = true;
        }
        resizeObject();
    }

    //##################################################################################################################


    //##########################################
    // Overwirte Methods
    //##########################################

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
    protected boolean getTableInitialised()
    {
        return tableInitialised;
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
        tableInitialised = true;
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
