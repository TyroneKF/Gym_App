package com.donty.gymapp.domain.enums.db_enums.columnNames.tables;

import com.donty.gymapp.domain.enums.db_enums.columnNames.base.Table_Enum;


public enum MacroTargetsColumns implements Table_Enum
{
    //##################################################################################################################
    // Enums
    //##################################################################################################################
    CURRENT_WEIGHT_KG("current_weight_kg"),
    BODY_FAT_PERCENTAGE("body_fat_percentage"),
    PROTEIN_PER_POUND("protein_per_pound"),
    CARBOHYDRATES_PER_POUND("carbohydrates_per_pound"),
    FIBRE("fibre"),
    FATS_PER_POUND("fats_per_pound"),
    SATURATED_FAT_LIMIT("saturated_fat_limit"),
    SALT_LIMIT("salt_limit"),
    WATER_TARGET("water_target");

    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private static final String SOURCE_NAME = "draft_macros_per_pound_and_limits";

    private final String source_name;
    private final String key;

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
