package App_Code.Objects.Table_Objects.Tables.Children.View_Data_Tables.Children;

import App_Code.Objects.Database_Objects.MyJDBC.MyJDBC_Sqlite;
import App_Code.Objects.Table_Objects.Tables.Children.View_Data_Tables.Parent.MyJTable_Display_Data;

import java.awt.*;
import java.util.ArrayList;

public class MacrosTargets_Table extends MyJTable_Display_Data
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    protected int plan_ID;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public MacrosTargets_Table
    (
            MyJDBC_Sqlite db,
            Container parentContainer,
            ArrayList<ArrayList<Object>> data,
            ArrayList<String> columnNames,
            int plan_ID,
            ArrayList<String> colAvoidCentering,
            ArrayList<String> columnsToHide
    )
    {
        super(
                db,
                parentContainer,
                data,
                columnNames,
                "plan_id",
                "Macro Targets Table",
                "macros_per_pound_and_limits",
                "draft_gui_plan_macro_target_calculations",
                new Object[]{ plan_ID },
                columnNames,
                colAvoidCentering,
                columnsToHide
        );
        
        //##########################################
        // Variables
        //###########################################
        this.plan_ID = plan_ID;
    }
}
