package com.donty.gymapp.domain.enums.db_enums.columnNames.views.totalmeal;

import com.donty.gymapp.domain.enums.db_enums.columnNames.base.Table_Enum;

public enum Draft_Gui_Total_Meal_Columns implements Table_Enum
{
    //##################################################################################################################
    // Enums
    //##################################################################################################################

    // Macros
    DRAFT_MEAL_IN_PLAN_ID("draft_meal_in_plan_id"),
    MEAL_NAME("meal_name");

    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private static final String SOURCE_NAME = "draft_gui_total_meal_view";

    private final String source_name;

    private final String key;

    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    Draft_Gui_Total_Meal_Columns(String key)
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
