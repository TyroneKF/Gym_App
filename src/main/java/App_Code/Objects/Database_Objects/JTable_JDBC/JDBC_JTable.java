package App_Code.Objects.Database_Objects.JTable_JDBC;

// Packages to import
// https://stackoverflow.com/questions/10347983/making-a-jbutton-clickable-inside-a-jtable
//http://tips4java.wordpress.com/2009/07/12/table-button-column/

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
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
    protected DefaultTableModel tableModel;
    protected Object[][] data;
    protected int rowsInTable = 0, columnsInTable = 0;
    protected String  tableName;
    protected String[] columnDataTypes, columnNames, guiColumnNames;

    protected ArrayList<String> colAvoidCentering = new ArrayList<>();
    protected ArrayList<Integer> unEditableColumns = new ArrayList<>();
    protected ArrayList<String> columnsToHide = new ArrayList<>();

    //##############################################
    protected boolean tableInitialised = false;

    protected HashMap<Integer, ArrayList<String>> jComboMap = new HashMap<>();

    protected Integer deleteColumn = null;
    protected Object previousJComboItem;
    protected Object selected_JCombo_Item;

    //###############################################

    //###############################################
    protected LinkedHashMap<String, Integer[]> columnNamesAndPositions = new LinkedHashMap<>();
    /*
        Array Pos 1 = Original Position in JTable Data
        Array Pos 2 = Position after columns hidden
    */

    //##################################################################################################################
    public JDBC_JTable(MyJDBC db, Container parentContainer, boolean setIconsUp, String tableName, Object[][] data,
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

        //################################################################
        // Reformat Column Names To be Capitalised on the Application lvl
        //################################################################
        guiColumnNames = new String[columnNames.length];

        for (int x = 0; x < columnNames.length ; x ++)
        {
            // Get Column Name
            String columnName = columnNames[x];

            // Re-assign Re-Capitalised Value into list
            guiColumnNames[x] = Arrays.stream(columnName.split("_"))
                    .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                    .collect(Collectors.joining("_"));

        }

        //################################################################
        // Table Setup With Table Data
        //################################################################
        if (data !=null)
        {
            tableSetup(data, guiColumnNames, setIconsUp);
        }
        else
        {
            tableSetup(new Object[0][0], guiColumnNames, setIconsUp);
        }

        //##############################################################
        // Hide Columns
        //##############################################################
        SetUp_HiddenTableColumns(columnsToHide);
    }

    //##################################################################################################################
    // Set Up Methods
    //##################################################################################################################


    //###################################################
    // Table Setup Methods
    //###################################################
    protected void extra_TableSetup()
    {

    }

    protected void tableSetup(Object[][] data, String[] columnNames, boolean setIconsUp)
    {
        extra_TableSetup();

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

    //###################################################
    // Table Model Setup Methods
    //###################################################
    protected void extraTableModel_Setup()
    {

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
            extraTableModel_Setup();

            if (columnsToHide!=null)
            {
                SetUp_HiddenTableColumns(columnsToHide);
            }
        }
        else
        {
            tableInitialised = true;
        }
        resizeObject();
    }

    protected void setTableModelData(Object[][] tableModelData)
    {
        this.data = tableModelData;
        resizeObject();
    }

    //##################################################################################################################
    // Action Methods
    //##################################################################################################################

    protected Boolean areYouSure(String process)
    {
        int reply = JOptionPane.showConfirmDialog(null, String.format("Are you sure you want to %s, \nany unsaved changes will be lost in this Table! \nDo you want to %s?", process, process),
                "Notification", JOptionPane.YES_NO_OPTION); //HELLO Edit

        if (reply==JOptionPane.NO_OPTION || reply==JOptionPane.CLOSED_OPTION)
        {
            return false;
        }
        return true;
    }

    protected void tableDataChange_Action()
    {

    }

    public void updateTable(ArrayList<Object> updateData, int updateRow)
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

    public void refreshData()
    {
        tableModel_Setup(getData(), getColumnNames());
    }

    //##################################################################################################################
    //
    //##################################################################################################################

    protected boolean getTableInitialised()
    {
        return tableInitialised;
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


    //##################################################################################################################

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
