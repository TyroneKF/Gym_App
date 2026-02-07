package com.donty.gymapp.tables.Tables.Children.View_Data_Tables.Children;

import com.donty.gymapp.database.MyJDBC.MyJDBC_Sqlite;
import com.donty.gymapp.database.Shared_Data_Registry;
import com.donty.gymapp.tables.Tables.Children.View_Data_Tables.Parent.MyJTable_Display_Data;

import java.awt.*;
import java.util.ArrayList;

public class MacrosTargets_Table extends MyJTable_Display_Data
{
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public MacrosTargets_Table
    (
            MyJDBC_Sqlite db,
            Shared_Data_Registry shared_data_registry,
            Container parentContainer,
            ArrayList<ArrayList<Object>> data,
            ArrayList<String> columnNames,
            ArrayList<String> colAvoidCentering,
            ArrayList<String> columnsToHide
    )
    {
        super(
                db,
                shared_data_registry,
                parentContainer,
                data,
                columnNames,
                "plan_id",
                "Macro Targets Table",
                "macros_per_pound_and_limits",
                "draft_gui_plan_macro_target_calculations",
                columnNames,
                colAvoidCentering,
                columnsToHide
        );
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    @Override
    protected Object[] get_Params()
    {
        return new Object[]{ shared_data_registry.get_Selected_Plan_ID() };
    }
}
