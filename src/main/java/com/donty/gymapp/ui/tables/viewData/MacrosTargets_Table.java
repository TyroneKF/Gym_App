package com.donty.gymapp.ui.tables.viewData;

import com.donty.gymapp.domain.enums.db_enums.TableNames;
import com.donty.gymapp.domain.enums.db_enums.ViewNames;
import com.donty.gymapp.domain.enums.db_enums.columnNames.views.Draft_Gui_Plan_Macro_Targets_Calc_Columns;
import com.donty.gymapp.persistence.database.MyJDBC_Sqlite;
import com.donty.gymapp.persistence.Shared_Data_Registry;
import com.donty.gymapp.ui.tables.base.ColumnUiRules;
import com.donty.gymapp.ui.tables.base.TableMeta;
import com.donty.gymapp.ui.tables.viewData.base.MyJTable_Display_Data;

import java.awt.*;
import java.util.ArrayList;

public class MacrosTargets_Table extends MyJTable_Display_Data<Draft_Gui_Plan_Macro_Targets_Calc_Columns>
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
            ColumnUiRules<Draft_Gui_Plan_Macro_Targets_Calc_Columns> columnUiRules
    )
    {
        super(
                db,
                shared_data_registry,
                parentContainer,
                saved_Data,

                new TableMeta<>(
                        Draft_Gui_Plan_Macro_Targets_Calc_Columns.PLAN_ID,
                        "Macro Targets Table",
                        TableNames.MACROS_PER_POUND_AND_LIMITS,
                       ViewNames.DRAFT_GUI_PLAN_MACRO_TARGET_CALCULATIONS
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
