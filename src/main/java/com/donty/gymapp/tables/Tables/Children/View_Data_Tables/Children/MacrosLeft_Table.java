package com.donty.gymapp.Table_Objects.Tables.Children.View_Data_Tables.Children;

import com.donty.gymapp.Database_Objects.MyJDBC.MyJDBC_Sqlite;
import com.donty.gymapp.Database_Objects.Shared_Data_Registry;
import com.donty.gymapp.Table_Objects.Tables.Children.View_Data_Tables.Parent.MyJTable_Display_Data;

import java.awt.*;
import java.util.ArrayList;

public class MacrosLeft_Table extends MyJTable_Display_Data
{
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public MacrosLeft_Table
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
                "Macros Left Table",
                null,
                "draft_gui_plan_macros_left",
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
