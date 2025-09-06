package App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.MyJTable_DisplayData;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MacrosLeftTable extends MyJTable_DisplayData
{

    public MacrosLeftTable(MyJDBC db, Container parentContainer, Object[][] data, String[] columnNames, int planID, int temp_PlanID,
                           String tableName, ArrayList<String> unEditableColumns, ArrayList<String> colAvoidCentering,
                           ArrayList<String> columnsToHide)
    {
        super(db, parentContainer, data, columnNames, planID, temp_PlanID, tableName, unEditableColumns, colAvoidCentering, columnsToHide);
        super.query = String.format("SELECT * FROM %s WHERE plan_id = %s;", tableName, temp_PlanID);

        System.out.printf("\n\nMacrosLeftTable() %s \nQuery: %s", temp_PlanID, query);
    }

    public void updateMacrosLeftTable()
    {
        super.updateTable();
    }

    public boolean updateMacrosLeftTableModelData()
    {
        return super.updateTableModelData();
    }
}
