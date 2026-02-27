package com.donty.gymapp.domain.enums.db_enums.columnNames.views.totalmeal;

import com.donty.gymapp.domain.enums.db_enums.columnNames.base.Table_Enum;

public enum Draft_Gui_Total_Meal_Macro_Columns implements Table_Enum
{
    //##################################################################################################################
    // Enums
    //##################################################################################################################

    // Macros
    TOTAL_PROTEIN("total_protein"),
    TOTAL_CARBOHYDRATES("total_carbohydrates"),
    TOTAL_SUGARS_OF_CARBS("total_sugars_of_carbs"),
    TOTAL_FATS("total_fats"),
    TOTAL_SATURATED_FAT("total_saturated_fat"),
    TOTAL_SALT("total_salt"),
    TOTAL_FIBRE("total_fibre"),
    TOTAL_WATER("total_water"),
    TOTAL_CALORIES("total_calories");

    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private final String key;




    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    Draft_Gui_Total_Meal_Macro_Columns(String key)
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