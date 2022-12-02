package App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.Children;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.MyJTable_DisplayData;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MacrosLeftTable extends MyJTable_DisplayData
{
    public MacrosLeftTable(MyJDBC db, Container parentContainer, String databaseName,Object[][] data, String[] columnNames, int planID,
                           String tableName, ArrayList<String> unEditableColumns, ArrayList<String> colAvoidCentering,
                           ArrayList<String> columnsToHide)
    {
        super(db, parentContainer, databaseName, data, columnNames, planID, tableName, unEditableColumns, colAvoidCentering, columnsToHide);
    }

    public void updateMacrosLeftTable()
    {
        String query = String.format("select * from planMacrosLeft WHERE PlanID = %s", temp_PlanID);
        super.updateTable("MacrosLeftTable", query, 0);
    }

    public boolean updateMacrosLeftTableModelData()
    {
        String query = String.format("select * from planMacrosLeft WHERE PlanID = %s;", temp_PlanID);

        return super.updateTableModelData("planMacrosLeft", query);
    }

}
