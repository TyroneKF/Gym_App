package App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.Parent;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JTable_JDBC.Parent.JDBC_JTable;

import java.awt.*;
import java.util.ArrayList;

public class MyJTable_DisplayData extends JDBC_JTable
{
    protected Integer planID, temp_PlanID = 1;
    protected boolean setIconsUp;

    public MyJTable_DisplayData(MyJDBC db, Container parentContainer, Object[][] data, String[] columnNames, int planID,
                                String tableName, ArrayList<Integer> unEditableColumns, ArrayList<Integer> colAvoidCentering,
                                ArrayList<String> columnsToHide)
    {
        setLayout(new GridBagLayout());

        //##############################################################
        // Super Variables
        //##############################################################
        super.db = db;
        super.data = data;

        super.parentContainer = parentContainer;
        super.tableName = tableName;

        super.unEditableColumns = unEditableColumns;
        super.colAvoidCentering = colAvoidCentering;

        super.columnDataTypes = db.getColumnDataTypes(tableName); //HELLO Is this needed for this class
        super.columnsInTable = columnNames.length; //HELLO Is this needed for this class
        super.rowsInTable = data.length; //HELLO Is this needed for this class

        super.columnNames = columnNames;
        super.columnsToHide = columnsToHide;

        //##############################################################
        // Column Names & Their Original Positions
        //##############################################################

        // Adding column names and their original positions to the hashmap
        System.out.printf("\n\nConstructor MyJTable_DisplayData \nColumnNames: \n");
        for (int pos = 0; pos < columnNames.length; pos++)
        {
            columnNamesAndPositions.put(columnNames[pos], new Integer[]{pos, pos});
            System.out.printf("\n%s",columnNames[pos]);
        }

        //##############################################################
        // Other Variables
        //##############################################################
        this.planID = planID;
        this.setIconsUp = false;

        //##############################################################
        // Table Setup
        //##############################################################

        if (data!=null)
        {
            super.tableSetup(data, columnNames, setIconsUp);
        }
        else
        {
            super.tableSetup(new Object[0][0], columnNames, setIconsUp);
        }

        //##############################################################
        // Hide Columns
        //##############################################################
        SetUp_HiddenTableColumns(columnsToHide);
    }

    public void refreshData()
    {
        tableModel_Setup(super.getData(), super.getColumnNames());
    }
}
