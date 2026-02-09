package com.donty.gymapp.gui.controls.combobox;

import com.donty.gymapp.ui.meta.ids.Storable_Ingredient_IDS.Storable_IDS_Parent;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class Field_JCombo_Storable_ID<T extends Storable_IDS_Parent> extends Field_JComboBox<T>
{
    public Field_JCombo_Storable_ID(String label, Class<T> typeCast, boolean hide_Is_System, ArrayList<T> data_AL)
    {
        super(
                label,
                typeCast,
                // Remove is_System Variables if required
                ! hide_Is_System ?
                        data_AL :
                        data_AL.stream().filter(e -> ! e.get_is_System()).collect(Collectors.toCollection(ArrayList :: new))
        );

        addItemListener(ie -> {
            if (ie.getStateChange() == ItemEvent.SELECTED)
            {
                actionListener();
            }
        });
    }

    //##################################################################################################################
    // Accessor Methods
    //##################################################################################################################
    public void set_Item_By_ID(int id) throws Exception
    {
        Optional<T> x = data_AL.stream().filter(e -> e.get_ID() == id).findFirst(); // Stream & find item in list

        // if optional is empty product doesn't exist
        if (x.isEmpty()) { throw new Exception("set_Item_By_ID() : Item by ID not found! "); }

        setSelectedItem(x.get()); // Set Item to selected item and get by ID
    }

    protected void actionListener() { }

    //##################################################################################################################
    // Accessor Methods
    //##################################################################################################################
    public Integer get_Selected_Item_ID()
    {
        // Get Object
        Object item = getSelectedItem();

        if (item == null) { return null; }

        return typeCast.cast(item).get_ID();
    }

    public boolean does_Selected_Item_ID_Equal(int id)
    {
        Integer selected_ID = get_Selected_Item_ID();

        return selected_ID != null && id == selected_ID;
    }
}
