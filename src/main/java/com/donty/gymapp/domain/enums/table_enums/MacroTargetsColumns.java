package com.donty.gymapp.domain.enums.table_enums;

import com.donty.gymapp.domain.enums.base.Table_Enum;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum MacroTargetsColumns implements Table_Enum
{
    //##################################################################################################################
    // Enums
    //##################################################################################################################
    CURRENT_WEIGHT_KG("current_weight_kg"),
    CURRENT_WEIGHT_IN_POUNDS("current_weight_in_pounds"),
    BODY_FAT_PERCENTAGE("body_fat_percentage"),
    PROTEIN_PER_POUND("protein_per_pound"),
    CARBOHYDRATES_PER_POUND("carbohydrates_per_pound"),
    FIBRE("fibre"),
    FATS_PER_POUND("fats_per_pound"),
    SATURATED_FAT_LIMIT("saturated_fat_limit"),
    SALT_LIMIT("salt_limit"),
    WATER_TARGET("water_target"),
    ADDITIONAL_CALORIES("additional_calories");

    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private static final String SOURCE_NAME = "draft_macros_per_pound_and_limits";

    private final String source_name;
    private final String key;

    private static final Map<String, MacroTargetsColumns> BY_KEY =
            Arrays.stream(values())
                    .collect(Collectors.toMap(
                            MacroTargetsColumns :: key,
                            Function.identity()
                    ));

    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    MacroTargetsColumns(String key)
    {
        this.key = key;
        this.source_name = SOURCE_NAME;
    }

    //##################################################################################################################
    // Methods
    //##################################################################################################################
    @Override
    public String source_Name()
    {
        return source_name;
    }

    @Override
    public String key()
    {
        return key;
    }
}
