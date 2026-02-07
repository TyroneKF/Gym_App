package com.donty.gymapp.ui.meta.bindings;

import com.donty.gymapp.ui.meta.ids.Storable_Ingredient_IDS.Storable_IDS_Parent;
import com.donty.gymapp.gui.controls.combobox.Field_JCombo_Storable_ID;
import com.donty.gymapp.gui.controls.textfields.base.Field_JTxtField_Parent;

public class Shop_Form_Binding<T extends Storable_IDS_Parent>  extends Field_Binding<T>
{
    public Shop_Form_Binding(String gui_Label, Field_JCombo_Storable_ID<T> component, String mysql_Field, int query_Field_Pos)
    {
        super(gui_Label, component, mysql_Field, query_Field_Pos);
    }
    
    public Shop_Form_Binding(String gui_Label, Field_JTxtField_Parent<?> component, String mysql_Field, int query_Field_Pos)
    {
        super(gui_Label, component, mysql_Field, query_Field_Pos);
    }
}
