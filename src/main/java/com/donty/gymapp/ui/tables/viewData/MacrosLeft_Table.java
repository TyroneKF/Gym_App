package com.donty.gymapp.ui.tables.viewData;

import com.donty.gymapp.domain.enums.db_enums.ViewNames;
import com.donty.gymapp.domain.enums.db_enums.columnNames.views.Draft_Gui_Plan_Macros_Left_Columns;
import com.donty.gymapp.persistence.database.MyJDBC_Sqlite;
import com.donty.gymapp.persistence.Shared_Data_Registry;
import com.donty.gymapp.ui.tables.base.ColumnUiRules;
import com.donty.gymapp.ui.tables.base.TableMeta;
import com.donty.gymapp.ui.tables.viewData.base.MyJTable_Display_Data;

import java.awt.*;
import java.util.ArrayList;

public class MacrosLeft_Table extends MyJTable_Display_Data<Draft_Gui_Plan_Macros_Left_Columns>
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
            ColumnUiRules<Draft_Gui_Plan_Macros_Left_Columns> columnUiRules
    )
    {
        super(
                db,
                shared_data_registry,
                parentContainer,
                data,

                new TableMeta<>(
                        Draft_Gui_Plan_Macros_Left_Columns.PLAN_ID,
                        "Macros Left Table",
                        null,
                        ViewNames.DRAFT_GUI_PLAN_MACROS_LEFT
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
