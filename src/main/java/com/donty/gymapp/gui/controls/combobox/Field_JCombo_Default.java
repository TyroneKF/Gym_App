package com.donty.gymapp.gui.controls.combobox;

import com.donty.gymapp.gui.controls.combobox.base.Field_JComboBox;

import java.util.ArrayList;

public class Field_JCombo_Default<T> extends Field_JComboBox<T>
{
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Field_JCombo_Default(String label, Class<T> typeCast, ArrayList<T> data_AL)
    {
        super(label, typeCast, data_AL);

        init();
    }

    //##################################################################################################################
    // Method
    //##################################################################################################################
    @Override
    protected void init()
    {
        load_Items();
    }
}
