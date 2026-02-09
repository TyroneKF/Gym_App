package com.donty.gymapp.domain.enums.db_enums.columnNames.views;

import com.donty.gymapp.domain.enums.db_enums.columnNames.base.Table_Enum;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Draft_Gui_Plan_Macro_Targets_Calc_Columns implements Table_Enum
{
    //##################################################################################################################
    // Enums
    //##################################################################################################################

    // Macros
    PLAN_ID("plan_id"),
    EXPECTED_PROTEIN_GRAMS("expected_protein_grams"),
    EXPECTED_CARBS_GRAMS("expected_carbs_grams"),
    EXPECTED_FATS_GRAMS("expected_fats_grams"),
    SATURATED_FAT_LIMIT("saturated_fat_limit"),
    EXPECTED_FIBRE_GRAMS("expected_fibre_grams"),
    SALT_LIMIT_GRAMS("salt_limit_grams"),
    WATER_CONTENT_TARGET("water_content_target"),
    CALORIES_TARGET("calories_target"),
    ADDITIONAL_CALORIES_TARGET("additional_calories_target");

    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private static final String SOURCE_NAME = "draft_gui_plan_macro_target_calculations";

    private final String source_name;


    private final String key;

    private static final Map<String, Draft_Gui_Plan_Macro_Targets_Calc_Columns> BY_KEY =
            Arrays.stream(values())
                    .collect(Collectors.toMap(
                            Draft_Gui_Plan_Macro_Targets_Calc_Columns :: key,
                            Function.identity()
                    ));

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