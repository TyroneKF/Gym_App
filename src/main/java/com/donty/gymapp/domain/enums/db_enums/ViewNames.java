package com.donty.gymapp.domain.enums.db_enums;

import com.donty.gymapp.domain.enums.base.My_Enum;

public enum ViewNames implements My_Enum
{
    //##################################################################################################################
    // Enums
    //##################################################################################################################
    DRAFT_GUI_PLAN_MACROS_LEFT("draft_gui_plan_macros_left"),
    DRAFT_GUI_PLAN_MACRO_TARGET_CALCULATIONS("draft_gui_plan_macro_target_calculations"),
    DRAFT_GUI_TOTAL_MEAL_VIEW("draft_gui_total_meal_view"),
    DRAFT_GUI_INGREDIENTS_IN_SECTIONS_OF_MEAL_CALCULATION("draft_gui_ingredients_in_sections_of_meal_calculation");


    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private final String key;


    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    ViewNames(String key)
    {
        this.key = key;
    }

    //##################################################################################################################
    // Methods
    //##################################################################################################################

    @Override
    public String key()
    {
        return key;
    }
}