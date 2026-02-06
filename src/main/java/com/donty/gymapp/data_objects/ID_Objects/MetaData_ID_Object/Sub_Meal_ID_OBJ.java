package com.donty.gymapp.Data_Objects.ID_Objects.MetaData_ID_Object;

import java.time.LocalTime;
import java.util.ArrayList;

public final class Sub_Meal_ID_OBJ extends Meta_Data_ID_Parent
{
    // #################################################################################################################
    // Variables
    // #################################################################################################################
    private final LocalTime sub_meal_Time;
    private Integer source_sub_meal_id = null;
    private final int draft_meal_id;
    private final boolean sub_meal_in_db;

    private ArrayList<ArrayList<Object>> sub_meal_ingredients = new ArrayList<>();

    // #################################################################################################################
    // Constructor
    // #################################################################################################################
    public Sub_Meal_ID_OBJ
    (
            int draft_sub_meal_id,
            int source_sub_meal_id,
            String sub_meal_name,
            LocalTime sub_meal_time,
            int draft_meal_id
    )
    {
        super(draft_sub_meal_id, sub_meal_name);

        this.sub_meal_in_db = true;
        this.sub_meal_Time = sub_meal_time;

        this.source_sub_meal_id = source_sub_meal_id;
        this.draft_meal_id = draft_meal_id;
    }

    public Sub_Meal_ID_OBJ
            (
                    int draft_sub_meal_id,
                    String sub_meal_name,
                    LocalTime sub_meal_time,
                    ArrayList<ArrayList<Object>> sub_meal_ingredients,
                    int draft_meal_id
            )
    {
        super(draft_sub_meal_id, sub_meal_name);

        this.sub_meal_in_db = false;

        this.sub_meal_Time = sub_meal_time;
        this.sub_meal_ingredients = sub_meal_ingredients;

        this.draft_meal_id = draft_meal_id;
    }

    // #################################################################################################################
    //  Methods
    // #################################################################################################################
    public void add_Ingredient_Data(ArrayList<Object> ingredient_row)
    {
        sub_meal_ingredients.add(ingredient_row);
    }

    // #################################################################################################################
    // Accessor Methods
    // #################################################################################################################
    public String get_Sub_Meal_Name()
    {
        return get_Name();
    }

    // #######################################
    // LocalTime
    // #######################################
    public LocalTime get_Sub_Meal_Time()
    {
        return sub_meal_Time;
    }

    // #######################################
    // Integers
    // #######################################
    public int get_Draft_Sub_Meal_ID()
    {
        return get_ID();
    }

    public Integer get_Source_Sub_Meal_ID()
    {
        return source_sub_meal_id;
    }

    public int get_Draft_Meal_ID()
    {
        return draft_meal_id;
    }

    // #######################################
    // Boolean
    // #######################################
    public boolean is_Sub_Meal_In_DB()
    {
        return sub_meal_in_db;
    }

    // #######################################
    // Collections
    // #######################################
    public ArrayList<ArrayList<Object>> get_Sub_Meal_Ingredients()
    {
        return sub_meal_ingredients;
    }
}
