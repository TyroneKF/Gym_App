package com.donty.gymapp.domain.enums.db_enums.columnNames.views;

import com.donty.gymapp.domain.enums.db_enums.columnNames.base.Table_Enum;

public enum Draft_Gui_Plan_Macro_Targets_Calc_Columns implements Table_Enum
{
    //##################################################################################################################
    // Enums
    //##################################################################################################################

    // Macros
    PLAN_ID("plan_id");




    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private static final String SOURCE_NAME = "draft_gui_plan_macro_target_calculations";

    private final String source_name;
    private final String key;


    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    Draft_Gui_Plan_Macro_Targets_Calc_Columns(String key)
    {
        this.key = key;
        this.source_name = SOURCE_NAME;
    }

    //##################################################################################################################
    // Methods
    //##################################################################################################################

    @Override
    public String key()
    {
        return key;
    }

    @Override
    public String source_Name()
    {
        return source_name;
    }
}