package com.donty.gymapp.gui.controls.combobox;

import com.donty.gymapp.gui.controls.combobox.base.Field_JComboBox;
import com.donty.gymapp.ui.meta.ids.Storable_Ingredient_IDS.Storable_IDS_Parent;
import javax.swing.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Optional;

public class Field_JCombo_Storable_ID<T extends Storable_IDS_Parent> extends Field_JComboBox<T>
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    boolean hide_system_var = true;


    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Field_JCombo_Storable_ID(String label, Class<T> typeCast,  ArrayList<T> data_AL)
    {
        super(label, typeCast, data_AL);

        init();
    }

    public Field_JCombo_Storable_ID(String label, Class<T> typeCast, boolean hide_system_var, ArrayList<T> data_AL)
    {
        super(label, typeCast, data_AL);

        this.hide_system_var = hide_system_var;

        init();
    }

    //##################################################################################################################
    // Methods
    //##################################################################################################################
    @Override
    protected void init()
    {
        load_Items();

        addItemListener(ie -> {
            if (ie.getStateChange() == ItemEvent.SELECTED)
            {
                actionListener();
            }
        });
    }

    public void load_Items()
    {
        //############################
        // Get Previous Item
        //############################
        T selected_item = typeCast.cast(getSelectedItem());

        boolean
                was_a_selected_item_chosen = selected_item != null,
                selected_item_in_list = false;

        //############################
        // Load List
        //############################
        DefaultComboBoxModel<T> model = (DefaultComboBoxModel<T>) getModel();

        model.removeAllElements(); // Remove all Elements

        for (T item : data_AL) // Populate Model From AL that's updated
        {
            if (was_a_selected_item_chosen && item.equals(selected_item)) // Check if selected item is in list
            {
                selected_item_in_list = true;
            }

            System.out.printf("\n\n%s -> %s ", item.toString(), item.get_is_System());

            if (hide_system_var && item.get_is_System()) { continue; } // IF items is_System & Skip system = continue

            model.addElement(item);
        }

        //############################
        // Set Selected Item
        //############################
        if (was_a_selected_item_chosen && selected_item_in_list)  // Set Item back to original Item
        {
            setSelectedItem(selected_item);
        }
        else
        {
            reset_JC(); // Set Selected Item to Nothing
        }
    }

    protected void actionListener() { }

    //##################################################################################################################
    // Mutator Methods
    //##################################################################################################################
    public void set_Item_By_ID(int id) throws Exception
    {
        Optional<T> x = data_AL.stream().filter(e -> e.get_ID() == id).findFirst(); // Stream & find item in list

        // if optional is empty product doesn't exist
        if (x.isEmpty()) { throw new Exception("set_Item_By_ID() : Item by ID not found! "); }

        setSelectedItem(x.get()); // Set Item to selected item and get by ID
    }

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
