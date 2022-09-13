package App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.Parent;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JTable_JDBC.Parent.JDBC_JTable;

import java.awt.*;
import java.util.ArrayList;

public class MyJTable_DisplayData extends JDBC_JTable
{
    protected Integer planID, temp_PlanID = 1;

    public MyJTable_DisplayData(MyJDBC db, Container parentContainer, String databaseName, Object[][] data, String[] columnNames, int planID,
                                String tableName, ArrayList<String> unEditableColumns, ArrayList<String> colAvoidCentering,
                                ArrayList<String> columnsToHide)
    {
        super(db, parentContainer, false, databaseName, tableName, data, columnNames, unEditableColumns, colAvoidCentering, columnsToHide);

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


        //##############################################################
        // Table Setup
        //##############################################################

        if (data!=null)
        {
            super.tableSetup(data, columnNames, false);
        }
        else
        {
            super.tableSetup(new Object[0][0], columnNames, false);
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
