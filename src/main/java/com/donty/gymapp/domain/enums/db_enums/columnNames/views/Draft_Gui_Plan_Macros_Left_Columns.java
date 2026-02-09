package com.donty.gymapp.domain.enums.db_enums.columnNames.views;

import com.donty.gymapp.domain.enums.db_enums.columnNames.base.Table_Enum;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Draft_Gui_Plan_Macros_Left_Columns implements Table_Enum
{
    //##################################################################################################################
    // Enums
    //##################################################################################################################

    // Macros
    PLAN_ID("plan_id"),
    PROTEIN_GRAMS_LEFT("protein_grams_left"),
    CARB_GRAMS_LEFT("carb_grams_left"),
    FIBRE_GRAMS_LEFT("fibre_grams_left"),
    FAT_GRAMS_LEFT("fat_grams_left"),
    POTENTIAL_SAT_FAT_GRAMS_LEFT("potential_sat_fat_grams_left"),
    POTENTIAL_SALT_GRAMS_LEFT("potential_salt_grams_left"),
    WATER_LEFT_TO_DRINK("water_left_to_drink"),
    CALORIES_LEFT("calories_left"),
    ADDED_CALORIES_LEFT("added_calories_left");

    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private static final String SOURCE_NAME = "draft_gui_plan_macros_left";

    private final String source_name;


    private final String key;

    private static final Map<String, Draft_Gui_Plan_Macros_Left_Columns> BY_KEY =
            Arrays.stream(values())
                    .collect(Collectors.toMap(
                            Draft_Gui_Plan_Macros_Left_Columns :: key,
                            Function.identity()
                    ));

    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    Draft_Gui_Plan_Macros_Left_Columns(String key)
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
