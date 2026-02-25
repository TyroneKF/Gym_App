package com.donty.gymapp.ui.screens.ingredientsAndInventory.ingredientsInfo.edit.screen;

import com.donty.gymapp.gui.controls.combobox.base.storableID.Field_JC_Ingredient_Type;
import com.donty.gymapp.persistence.Shared_Data_Registry;

public class Edit_Ingredients_Screen_Type_JC extends Field_JC_Ingredient_Type
{
    public Edit_Ingredients_Screen_Type_JC(Shared_Data_Registry shared_data_registry)
    {
        super(
                true,
                shared_data_registry,
                shared_data_registry.get_Mapped_Ingredient_Types()
        );
    }
}
