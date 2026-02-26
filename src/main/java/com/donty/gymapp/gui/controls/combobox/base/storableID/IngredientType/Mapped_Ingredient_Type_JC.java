package com.donty.gymapp.gui.controls.combobox.base.storableID.IngredientType;

import com.donty.gymapp.gui.controls.combobox.base.storableID.IngredientType.base.Field_JC_Ingredient_Type;
import com.donty.gymapp.persistence.Shared_Data_Registry;

public class Mapped_Ingredient_Type_JC extends Field_JC_Ingredient_Type
{
    public Mapped_Ingredient_Type_JC(Shared_Data_Registry shared_data_registry)
    {
        super(
                true,
                shared_data_registry,
                shared_data_registry.get_Mapped_Ingredient_Types()
        );
    }
}
