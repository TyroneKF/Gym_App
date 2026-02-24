package com.donty.gymapp.gui.controls.combobox.base.storableID;

import com.donty.gymapp.gui.controls.combobox.base.storableID.base.Field_JCombo_Storable_ID;
import com.donty.gymapp.persistence.Shared_Data_Registry;
import com.donty.gymapp.ui.meta.ids.storableIDs.Store_ID_OBJ;

public class Field_JC_Stores extends Field_JCombo_Storable_ID<Store_ID_OBJ>
{
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Field_JC_Stores(Shared_Data_Registry shared_data_registry)
    {
        super(
                "Store Name",
                Store_ID_OBJ.class,
                shared_data_registry.get_Stores_AL()
        );

        init();
    }
}
