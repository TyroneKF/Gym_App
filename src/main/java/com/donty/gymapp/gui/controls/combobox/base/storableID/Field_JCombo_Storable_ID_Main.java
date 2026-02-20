package com.donty.gymapp.gui.controls.combobox.base.storableID;

import com.donty.gymapp.gui.controls.combobox.base.storableID.base.Field_JCombo_Storable_ID;
import com.donty.gymapp.ui.meta.ids.storableIDs.Storable_IDS_Parent;

import java.util.ArrayList;

public class Field_JCombo_Storable_ID_Main<T extends Storable_IDS_Parent> extends Field_JCombo_Storable_ID<T>
{
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Field_JCombo_Storable_ID_Main
    (
            String label,
            Class<T> typeCast,
            ArrayList<T> data_AL
    )
    {
        super(label, typeCast, data_AL);

        init();
    }

    public Field_JCombo_Storable_ID_Main
    (
            String label,
            Class<T> typeCast,
            boolean hide_system_var,
            ArrayList<T> data_AL
    )
    {
        super(label, typeCast, data_AL);

        this.hide_system_var = hide_system_var;

        init();
    }

    //##################################################################################################################
    // Constructor
    //##################################################################################################################
}
