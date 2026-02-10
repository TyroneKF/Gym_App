package com.donty.gymapp.ui.meta.ids.Storable_Ingredient_IDS;

import com.donty.gymapp.ui.meta.ids.ID_Object;

/**
 * Permits restricts the classes that can extend this object enforces type restriction in case its accidentally extended
 */

public sealed class Storable_IDS_Parent extends ID_Object permits
        Ingredient_Name_ID_OBJ,
        Ingredient_Type_ID_OBJ,
        Measurement_ID_OBJ,
        Store_ID_OBJ,
        Measurement_Material_Type_ID_OBJ
{
    protected Storable_IDS_Parent(int id, boolean is_System, String name)
    {
        super(id, is_System, name);
    }
}

