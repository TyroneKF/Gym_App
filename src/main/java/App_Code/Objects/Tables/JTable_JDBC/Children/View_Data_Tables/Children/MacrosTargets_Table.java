package App_Code.Objects.Tables.JTable_JDBC.Children.View_Data_Tables.Children;

import App_Code.Objects.Database_Objects.MyJDBC.MyJDBC_MySQL;
import App_Code.Objects.Database_Objects.MyJDBC.MyJDBC_Sqlite;
import App_Code.Objects.Tables.JTable_JDBC.Children.View_Data_Tables.Parent.MyJTable_DisplayData;

import java.awt.*;
import java.util.ArrayList;

public class MacrosTargets_Table extends MyJTable_DisplayData
{
    public MacrosTargets_Table(
            MyJDBC_Sqlite db,
            Container parentContainer,
            ArrayList<ArrayList<Object>> data,
            ArrayList<String> columnNames,
            int planID,
            ArrayList<String> colAvoidCentering,
            ArrayList<String> columnsToHide
    )
    {
        super(
                db,
                parentContainer,
                data,
                columnNames,
                planID,
                "draft_gui_plan_macro_target_calculations",
                columnNames,
                colAvoidCentering,
                columnsToHide
        );
    }
    
    @Override
    protected String get_Query()
    {
        return String.format("SELECT * FROM %s WHERE plan_id = ?;", table_Name);
    }
    
    @Override
    protected Object[] get_Params()
    {
        return new Object[]{ temp_Plan_ID };
    }
}
