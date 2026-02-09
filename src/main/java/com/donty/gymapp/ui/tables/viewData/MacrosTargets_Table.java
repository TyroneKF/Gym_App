package com.donty.gymapp.ui.tables.viewData;

import com.donty.gymapp.persistence.database.MyJDBC_Sqlite;
import com.donty.gymapp.persistence.Shared_Data_Registry;
import com.donty.gymapp.ui.tables.base.ColumnUiRules;
import com.donty.gymapp.ui.tables.base.TableMeta;
import com.donty.gymapp.ui.tables.viewData.base.MyJTable_Display_Data;

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
            ArrayList<ArrayList<Object>> saved_Data,
            ColumnUiRules columnUiRules
    )
    {
        super(
                db,
                shared_data_registry,
                parentContainer,
                saved_Data,

                new TableMeta(
                        "plan_id",
                        "Macro Targets Table",
                        "macros_per_pound_and_limits",
                        "draft_gui_plan_macro_target_calculations"
                ),

                columnUiRules
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
