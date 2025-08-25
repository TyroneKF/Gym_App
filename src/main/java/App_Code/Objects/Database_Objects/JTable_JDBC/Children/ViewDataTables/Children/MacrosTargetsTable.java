package App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.Children;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.MyJTable_DisplayData;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MacrosTargetsTable extends MyJTable_DisplayData
{

    public MacrosTargetsTable(MyJDBC db, Container parentContainer,  Object[][] data, String[] columnNames, int planID,
                              String tableName, ArrayList<String> unEditableColumns, ArrayList<String> colAvoidCentering,
                              ArrayList<String> columnsToHide)
    {
        super(db, parentContainer,  data, columnNames, planID, tableName, unEditableColumns, colAvoidCentering, columnsToHide);
    }

    public void updateMacrosTargetsTable()
    {
        String query = String.format("SELECT * FROM plan_Macro_Target_Calculations WHERE plan_id = %s;", temp_PlanID);
        super.updateTable("MacrosTargetsTable", query, 0);
    }
}
