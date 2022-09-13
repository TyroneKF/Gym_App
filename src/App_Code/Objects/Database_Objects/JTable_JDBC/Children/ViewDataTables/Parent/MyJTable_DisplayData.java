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

        this.planID = planID;
    }

    public void refreshData()
    {
        tableModel_Setup(super.getData(), super.getColumnNames());
    }
}
