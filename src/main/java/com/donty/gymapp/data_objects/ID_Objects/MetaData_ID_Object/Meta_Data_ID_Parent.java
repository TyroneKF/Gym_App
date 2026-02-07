package com.donty.gymapp.data_objects.ID_Objects.MetaData_ID_Object;

import com.donty.gymapp.data_objects.ID_Objects.ID_Object;

public sealed class Meta_Data_ID_Parent extends ID_Object permits Meal_ID_OBJ, Product_Info_ID, Sub_Meal_ID_OBJ
{
    public Meta_Data_ID_Parent(int id, String name)
    {
        super(id, name);
    }
}
