package com.donty.gymapp.gui.controls.combobox.base.storableID.IngredientType.base;

import com.donty.gymapp.gui.controls.combobox.base.storableID.base.Field_JCombo_Storable_ID;
import com.donty.gymapp.persistence.Shared_Data_Registry;
import com.donty.gymapp.ui.meta.ids.storableIDs.Ingredient_Type_ID_OBJ;

import java.util.ArrayList;

public class Field_JC_Ingredient_Type extends Field_JCombo_Storable_ID<Ingredient_Type_ID_OBJ>
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    protected final int un_assigned_type_id;

    protected boolean allow_un_assigned;

    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Field_JC_Ingredient_Type
    (
            boolean allow_un_assigned,
            Shared_Data_Registry shared_data_registry,
            ArrayList<Ingredient_Type_ID_OBJ> data_list
    )
    {
        super("Ingredient Type", Ingredient_Type_ID_OBJ.class, data_list);

        this.allow_un_assigned = allow_un_assigned;

        un_assigned_type_id = shared_data_registry.get_Un_assigned_Ingredient_Type_ID();

        init();
    }

    //##################################################################################################################
    // Methods
    //##################################################################################################################
    @Override
    protected boolean remove_Item_From_JC_Model(Ingredient_Type_ID_OBJ iteration_item)
    {
        // Only remove items when we are hiding system vars AND the item is a system var
        if (! (hide_system_var && iteration_item.get_is_System()))
        {
            return false;
        }

        // If we get to this section we are removing system variables
        boolean is_unassigned_item = iteration_item.get_ID().equals(un_assigned_type_id);

        /*
            If item isn't the un_assigned item remove it
            IF the item is the un_assigned item and allow un_aissnged is false = remove
        */
        return ! is_unassigned_item || ! allow_un_assigned ;
    }

    @Override
    public void reset_JC()
    {
        setSelectedItem(null);
        change_Allow_Un_Assigned_State_And_Reload(false);
    }

    public void change_Allow_Un_Assigned_State_And_Reload(boolean allow_un_assigned)
    {
        set_Allow_Un_Assigned(allow_un_assigned);
        load_Items();
    }

    //##################################################################################################################
    // Mutator Methods
    //##################################################################################################################
    private void set_Allow_Un_Assigned(boolean allow_un_assigned)
    {
        this.allow_un_assigned = allow_un_assigned;
    }
}
