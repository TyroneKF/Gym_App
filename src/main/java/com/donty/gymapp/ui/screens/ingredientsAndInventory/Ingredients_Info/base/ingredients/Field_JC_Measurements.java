package com.donty.gymapp.ui.screens.ingredientsAndInventory.Ingredients_Info.base.ingredients;

import com.donty.gymapp.gui.controls.combobox.base.storableID.base.Field_JCombo_Storable_ID;
import com.donty.gymapp.persistence.Shared_Data_Registry;
import com.donty.gymapp.ui.meta.ids.Storable_Ingredient_IDS.Measurement_ID_OBJ;

public class Field_JC_Measurements extends Field_JCombo_Storable_ID<Measurement_ID_OBJ>
{
    //##################################################################################################################
    // Variable
    //##################################################################################################################
    protected Shared_Data_Registry shared_data_registry;
    protected int na_measurement_id;


    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Field_JC_Measurements(Shared_Data_Registry shared_data_registry)
    {
        super(
                "Ingredient Measurement In",
                Measurement_ID_OBJ.class,
                false,
                shared_data_registry.get_Ingredient_Measurement_Obj_AL()
        );

        this.shared_data_registry = shared_data_registry;
        na_measurement_id = shared_data_registry.get_NA_Measurement_ID();

        init();
    }

    //##################################################################################################################
    // Methods
    //##################################################################################################################
    @Override
    protected boolean remove_Item_From_JC_Model(Measurement_ID_OBJ iteration_item)
    {
        return iteration_item.get_ID().equals(na_measurement_id);
    }
}
