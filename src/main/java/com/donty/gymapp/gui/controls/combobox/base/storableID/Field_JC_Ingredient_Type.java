package com.donty.gymapp.gui.controls.combobox.base.storableID;

import com.donty.gymapp.gui.controls.combobox.base.storableID.base.Field_JCombo_Storable_ID;
import com.donty.gymapp.persistence.Shared_Data_Registry;
import com.donty.gymapp.ui.meta.ids.storableIDs.Ingredient_Type_ID_OBJ;

public class Field_JC_Ingredient_Type extends Field_JCombo_Storable_ID<Ingredient_Type_ID_OBJ>
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    protected Shared_Data_Registry shared_data_registry;
    protected int un_assigned_type_id;

    protected boolean allow_un_assigned;

    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Field_JC_Ingredient_Type(Shared_Data_Registry shared_data_registry, boolean allow_un_assigned)
    {
        super(
                "Ingredient Type",
                Ingredient_Type_ID_OBJ.class,
                shared_data_registry.get_Mapped_Ingredient_Types()
        );

        this.shared_data_registry = shared_data_registry;
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
        if (hide_system_var && iteration_item.get_is_System()) // IF hide System var && the item is a system Var
        {
            // IF allow un-assigned & the item is un-assigned type id return true else false

            if ( allow_un_assigned && iteration_item.get_ID().equals(un_assigned_type_id)) { return false; }

            return true;
        }

        return false;
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
    public void set_Allow_Un_Assigned(boolean allow_un_assigned)
    {
        this.allow_un_assigned = allow_un_assigned;
    }
}
