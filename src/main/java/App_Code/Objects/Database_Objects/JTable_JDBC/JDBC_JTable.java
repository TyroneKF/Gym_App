package App_Code.Objects.Database_Objects.JTable_JDBC;

// Packages to import
// https://stackoverflow.com/questions/10347983/making-a-jbutton-clickable-inside-a-jtable
//http://tips4java.wordpress.com/2009/07/12/table-button-column/

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


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
    protected CustomTableModel tableModel;
    protected ArrayList<ArrayList<Object>> savedData, currentData;
    
    protected String tableName;
    protected ArrayList<String> columnNames, guiColumnNames;
    protected String[] columnDataTypes;
    
    // Collection : Customisation Options
    protected ArrayList<String> colAvoidCentering = new ArrayList<>();
    protected ArrayList<Integer> unEditableColumnPositions = new ArrayList<>();
    protected ArrayList<String> columnsToHide = new ArrayList<>();
    
    protected LinkedHashMap<String, Integer[]> columnNamesAndPositions = new LinkedHashMap<>(); // Hello Can be removed
    /*
        Array Pos 1 = Original Position in JTable Data
        Array Pos 2 = Position after columns hidden
    */
    
    //##############################################
    //
    //##############################################
    protected boolean tableInitialised = false, addJTableAction;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public JDBC_JTable
    (
            MyJDBC db, Container parentContainer, boolean setIconsUp, boolean addJTableAction,
            String tableName, ArrayList<ArrayList<Object>> savedData, ArrayList<String> columnNames,
            ArrayList<String> unEditableColumnNames, ArrayList<String> colAvoidCentering, ArrayList<String> columnsToHide
    )
    {
        setLayout(new GridBagLayout());
        
        //##############################################################
        // Variables
        //##############################################################
        this.db = db;
        this.savedData = savedData;
        this.currentData = (ArrayList<ArrayList<Object>>) savedData.clone();
        
        this.parentContainer = parentContainer;
        this.addJTableAction = addJTableAction;
        this.tableName = tableName;
        
        this.columnDataTypes = db.getColumnDataTypes(tableName); //Column Data Types
        
        //##############################################################
        // Column Names & Their Original Positions
        //##############################################################
        this.columnNames = columnNames;
        
        // Adding column names and their original positions to the hashmap
        for (int pos = 0; pos < columnNames.size(); pos++)
        {
            columnNamesAndPositions.put(columnNames.get(pos), new Integer[]{ pos, pos });
        }
        //##############################################################
        
        this.colAvoidCentering = colAvoidCentering;
        this.columnsToHide = columnsToHide;
        
        //##############################################################
        // Get UnEditable Column Positions By Name
        //##############################################################
        if (unEditableColumnNames != null)
        {
            for (String columnName : unEditableColumnNames)
            {
                unEditableColumnPositions.add(columnNamesAndPositions.get(columnName)[0]);
            }
        }
        
        //################################################################
        // Reformat Column Names To be Capitalised on the Application lvl
        //################################################################
        guiColumnNames = new ArrayList<String>();
        
        for (int x = 0; x < columnNames.size(); x++)
        {
            // Get Column Name
            String columnName = columnNames.get(x);
            
            // Re-assign Re-Capitalised Value into list
            guiColumnNames.add(Arrays.stream(columnName.split("[ _]+"))
                    .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                    .collect(Collectors.joining("_")));
        }
        
        //################################################################
        // Table Setup With Table Data
        //################################################################
        if (savedData != null)
        {
            tableSetup(savedData, guiColumnNames, setIconsUp);
        }
        else
        {
            tableSetup(new ArrayList<ArrayList<Object>>(), guiColumnNames, setIconsUp);
        }
        
        //##############################################################
        // Hide Columns
        //##############################################################
        SetUp_HiddenTableColumns(columnsToHide);
    }
    
    //##################################################################################################################
    // Table Model Set Up Methods
    //##################################################################################################################
    public class CustomTableModel extends AbstractTableModel
    {
        
        private ArrayList<ArrayList<Object>> data;
        private ArrayList<String> columnNames;
        
        public CustomTableModel(ArrayList<ArrayList<Object>> inputData, ArrayList<String> columnNames)
        {
            // ######################################################
            //
            // ######################################################
            this.data = new ArrayList<>();
            this.columnNames = columnNames;
    
            /**
             * Although this does look long alternative methods dont work
             * .clone() references the original list so still creates error when recalling savedData list
             */
            // ######################################################
            // Create DataSet by manually adding each Cell
            // ######################################################
            int rowSize = inputData.size(), columnSize = columnNames.size();
            
            for (int row = 0; row < rowSize; row++)
            {
                ArrayList<Object> readingRow = inputData.get(row);
                ArrayList<Object> newRow = new ArrayList<>();
                
                for (int col = 0; col < columnSize; col++)
                {
                    newRow.add(readingRow.get(col));
                }
                
                data.add(newRow);
            }
        }
        
        @Override
        public int getRowCount()
        {
            return data.size();
        }
        
        @Override
        public int getColumnCount()
        {
            return columnNames.size();
        }
        
        @Override
        public Object getValueAt(int row, int col)
        {
            return data.get(row).get(col);
        }
        
        @Override
        public String getColumnName(int col)
        {
            return columnNames.get(col);
        }
        
        @Override
        public void setValueAt(Object value, int row, int col)
        {
            data.get(row).set(col, value);
            fireTableCellUpdated(row, col);
        }
        
        @Override
        public boolean isCellEditable(int row, int col)
        {
            return true;
        }
        
        @Override
        public Class getColumnClass(int c)
        {
            return getValueAt(0, c).getClass();
        }
        
        public void removeRow(int modelRow)
        {
            data.remove(modelRow);
            fireTableRowsDeleted(modelRow, modelRow); // notify JTable
        }
        
        public void addRow()
        {
            //#############################################
            // Adding NULL DATA to Row to FIll ALl Columns
            //#############################################
            ArrayList<Object> newRowData = new ArrayList<>();
            for (int i = 0; i < getColumnCount(); i++)
            {
                newRowData.add(null);
            }
            
            data.add(newRowData);
            
            //#############################################
            // Trigger JTable Update Methods
            //#############################################
            int size = data.size() - 1;
            fireTableRowsInserted(size, size); // Alerts JTable has been updated and refreshes JTable
        }
        
        public void refreshData()
        {
            // ######################################################
            // Clear DATA
            // ######################################################
            data.clear();
            
            // ######################################################
            // Re-Create DataSet
            // ######################################################
            int rowSize = savedData.size(), columnSize = columnNames.size();
    
            for (int row = 0; row < rowSize; row++)
            {
                ArrayList<Object> readingRow = savedData.get(row);
                ArrayList<Object> newRow = new ArrayList<>();
        
                for (int col = 0; col < columnSize; col++)
                {
                    newRow.add(readingRow.get(col));
                }
        
                data.add(newRow);
            }
            
            // ######################################################
            //
            // ######################################################
            fireTableDataChanged(); // notifies JTable to redraw everything
            resizeObject();
        }
    }
    
    //###################################################
    // Table Setup Methods
    //###################################################
    protected void extra_TableSetup()
    {
    
    }
    
    protected void tableSetup(ArrayList<ArrayList<Object>> data, ArrayList<String> columnNames, boolean setIconsUp)
    {
        extra_TableSetup();
        
        //###################################################################################
        // Table Setup
        //###################################################################################
        jTable = new JTable(); //Creating Table
        
        jTable.setRowHeight(jTable.getRowHeight() + 15);
        jTable.setFillsViewportHeight(true);
        jTable.getTableHeader().setPreferredSize(new Dimension(100, 50));  // setting header size
        setTableHeaderFont(new Font("Dialog", Font.BOLD, 16));    // setting text size
        
        tableModel_Setup(data, columnNames); // sets JTable Model
        
        //################################################################################
        // Adding JTable to JScrollPane
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
    
    protected void tableModel_Setup(ArrayList<ArrayList<Object>> data, ArrayList<String> columnNames)
    {
        tableModel = new CustomTableModel(data, columnNames);
        
        if (addJTableAction) { tableModel.addTableModelListener(evt -> tableDataChange_Action(evt)); }
        
        jTable.setModel(tableModel);
        
        //#################################################################################
        // Table Personalisation
        //#################################################################################
        
        //initColumnSizes();
        setCellRenderer();
        
        if (getTableInitialised())  //first time this method is called, special columns aren't defined
        {
            if (getColumnsToHide() != null)
            {
                SetUp_HiddenTableColumns(columnsToHide);
            }
            
            extraTableModel_Setup();
        }
        else
        {
            setTableInitialized();
        }
        resizeObject();
    }
    
    /**
     * This needs to be done before hiding columns
     * Set Column alignment
     */
    protected void setCellRenderer()
    {
        // ###############################################################
        // Centering Column Txt
        // ###############################################################
        
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(0);
        
        int pos = - 1;
        for (String columnName : columnNames)
        {
            pos++;
            
            if (colAvoidCentering != null && colAvoidCentering.contains(columnName))
            {
                continue;
            }
            
            jTable.getColumnModel().getColumn(pos).setCellRenderer(rightRenderer); // Center Column Data
        }
    }
    
    protected void extraTableModel_Setup()
    {
    
    }
    
    protected void setTableModelData(ArrayList<ArrayList<Object>> tableModelData)
    {
        this.savedData = tableModelData;
        resizeObject();
    }
    
    //###################################################
    // Table Model Setup Methods
    //###################################################
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
    
    protected void tableDataChange_Action(TableModelEvent evt)
    {
    
    }
    
    public void updateTable(ArrayList<Object> updateData, int updateRow)
    {
        //########################################################################
        // Updating Table Info
        //########################################################################
        for (int columnPos = 0; columnPos < tableModel.getColumnCount(); columnPos++)
        {
            tableModel.setValueAt(updateData.get(columnPos), updateRow, columnPos);
        }
        
        //########################################################################
        // Redraw JTable
        //########################################################################
        jTable.repaint();
    }
    
    public void refreshData()
    {
        tableModel.refreshData();
        //tableModel_Setup(getSavedData(), getGuiColumnNames());
        currentData = (ArrayList<ArrayList<Object>>) savedData.clone();
    }
    
    //##################################################################################################################
    // Action Methods
    //##################################################################################################################
    protected Boolean areYouSure(String process)
    {
        int reply = JOptionPane.showConfirmDialog(null, String.format("Are you sure you want to %s, \nany unsaved changes will be lost in this Table! \nDo you want to %s?", process, process),
                "Notification", JOptionPane.YES_NO_OPTION); //HELLO Edit
        
        if (reply == JOptionPane.NO_OPTION || reply == JOptionPane.CLOSED_OPTION) { return false; }
        
        return true;
    }
    
    //######################################################
    // Mutator Methods
    //######################################################
    protected void setTableInitialized()
    {
        tableInitialised = true;
    }
    
    public void setTableHeaderFont(Font font)
    {
        jTable.getTableHeader().setFont(font);
    }
    
    public void setTableTextFont(Font font)
    {
        jTable.setFont(font);
    }
    
    //######################################################
    // Accessor Methods
    //######################################################
    protected ArrayList<String> getColumnsToHide()
    {
        return columnsToHide;
    }
    
    protected ArrayList<String> getColumnNames()
    {
        return columnNames;
    }
    
    protected ArrayList<String> getGuiColumnNames() { return guiColumnNames; }
    
    protected int getRowsInTable()
    {
        return getTableModel().getRowCount();
    }
    
    protected CustomTableModel getTableModel()
    {
        return tableModel;
    }
    
    protected JTable getTable()
    {
        return jTable;
    }
    
    protected boolean getTableInitialised()
    {
        return tableInitialised;
    }
    
    //######################################################
    // Get Data Methods
    //######################################################
    protected Object getValueOnTable(int row, int col)
    {
        return getCurrentData().get(row).get(col);
    }
    
    protected ArrayList<ArrayList<Object>> getSavedData()
    {
        return savedData;
    }
    
    protected ArrayList<ArrayList<Object>> getCurrentData()
    {
        return currentData;
    }
    
    //##################################################################################################################
    // Resizing GUi & Add Component Methods
    //##################################################################################################################
    protected void resizeObject()
    {
        jTable.repaint();
        jTable.revalidate();
        jTable.setPreferredScrollableViewportSize(
                new Dimension(
                        jTable.getPreferredSize().width,
                        //jTable.getRowHeight() * rowsInTable +20))
                        jTable.getRowHeight() * getRowsInTable())); // get rid of 20, to have the border be exact
        
        scrollPane.revalidate(); // reshapes scrollpane
        revalidate();
        
        if (parentContainer != null)
        {
            parentContainer.revalidate();
        }
    }
    
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
}
