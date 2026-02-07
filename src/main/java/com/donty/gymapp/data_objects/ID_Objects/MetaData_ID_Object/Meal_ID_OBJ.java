package com.donty.gymapp.data_objects.ID_Objects.MetaData_ID_Object;

import java.time.LocalTime;

public final class Meal_ID_OBJ extends Meta_Data_ID_Parent
{
    // #################################################################################################################
    // Variables
    // #################################################################################################################
    private final LocalTime meal_Time;
    private final int source_meal_id;

    // #################################################################################################################
    // Constructor
    // #################################################################################################################
    public Meal_ID_OBJ(int draft_meal_id, int source_meal_id, String meal_name, LocalTime meal_Time)
    {
        super(draft_meal_id, meal_name);
        this.meal_Time = meal_Time;
        this.source_meal_id = source_meal_id;
    }

    // #################################################################################################################
    // Accessor Methods
    // #################################################################################################################
    public String get_Meal_Name()
    {
        return super.get_Name();
    }

    public LocalTime get_Meal_Time()
    {
        return meal_Time;
    }

    public int get_Draft_Meal_ID()
    {
        return get_ID();
    }

    public int get_Source_Meal_ID()
    {
        return source_meal_id;
    }
}
