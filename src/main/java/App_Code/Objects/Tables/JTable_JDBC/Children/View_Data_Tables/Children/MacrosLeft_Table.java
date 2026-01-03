package App_Code.Objects.Tables.JTable_JDBC.Children.View_Data_Tables.Children;

import App_Code.Objects.Database_Objects.MyJDBC.MyJDBC_MySQL;
import App_Code.Objects.Tables.JTable_JDBC.Children.View_Data_Tables.Parent.MyJTable_DisplayData;

import java.awt.*;
import java.util.ArrayList;

public class MacrosLeft_Table extends MyJTable_DisplayData
{
    
    public MacrosLeft_Table(
            MyJDBC_MySQL db,
            Container parentContainer,
            ArrayList<ArrayList<Object>> data,
            ArrayList<String> columnNames,
            int planID,
            int temp_PlanID,
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
                temp_PlanID,
                "plan_macros_left",
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
