package com.donty.gymapp.ui.screens.ingredientsAndInventory.ingredientsInfo.base.ingredients;

import com.donty.gymapp.gui.controls.combobox.base.storableID.Field_JC_Ingredient_Type;
import com.donty.gymapp.persistence.Shared_Data_Registry;

public class Field_JC_Ingredient_Form_Type extends Field_JC_Ingredient_Type
{
    public Field_JC_Ingredient_Form_Type(Shared_Data_Registry shared_data_registry)
    {
        super(
                false,
                shared_data_registry,
                shared_data_registry.get_All_Ingredient_Types_AL()
        );
    }
}
