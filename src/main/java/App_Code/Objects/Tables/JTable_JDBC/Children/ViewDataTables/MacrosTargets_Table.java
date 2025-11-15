package App_Code.Objects.Tables.JTable_JDBC.Children.ViewDataTables;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;

import java.awt.*;
import java.util.ArrayList;

public class MacrosTargets_Table extends MyJTable_DisplayData
{
    public MacrosTargets_Table(MyJDBC db, Container parentContainer, ArrayList<ArrayList<Object>> data, ArrayList<String> columnNames, int planID, int temp_PlanID,
                               String tableName, ArrayList<String> unEditableColumns, ArrayList<String> colAvoidCentering,
                               ArrayList<String> columnsToHide)
    {
        super(db, parentContainer, data, columnNames, planID, temp_PlanID, tableName, unEditableColumns, colAvoidCentering, columnsToHide);
        super.query = String.format("SELECT * FROM %s WHERE plan_id = ?;", tableName);
        super.params = new Object[]{temp_PlanID};
    }
    
    public void update_MacrosTargets_Table()
    {
        super.update_Table();
    }
}
