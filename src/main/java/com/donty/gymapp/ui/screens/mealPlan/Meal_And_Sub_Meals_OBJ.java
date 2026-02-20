package com.donty.gymapp.ui.screens.mealPlan;

import com.donty.gymapp.ui.meta.ids.meta.Meal_ID_OBJ;
import com.donty.gymapp.ui.meta.ids.meta.Sub_Meal_ID_OBJ;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

public class Meal_And_Sub_Meals_OBJ
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private final Meal_ID_OBJ meal_id_obj;

    private final HashMap<Integer, Sub_Meal_ID_OBJ> sub_meal_id_map = new HashMap<>(); //

    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Meal_And_Sub_Meals_OBJ
    (
            int draft_meal_id,
            int source_meal_id,
            String meal_name,
            LocalTime meal_time
    )
    {
         meal_id_obj = new Meal_ID_OBJ(draft_meal_id, source_meal_id, meal_name, meal_time);
    }

    //##################################################################################################################
    // Methods
    //##################################################################################################################
    public void add_sub_meal(Sub_Meal_ID_OBJ sub_meal_id_obj)
    {
        sub_meal_id_map.put(sub_meal_id_obj.get_Draft_Sub_Meal_ID(), sub_meal_id_obj);
    }

    public void add_Ingredients_To_Sub_Meal(int draft_sub_meal_id, ArrayList<Object> ingredient_row) throws Exception
    {
        if(! sub_meal_id_map.containsKey(draft_sub_meal_id))
        {
            throw new Exception(String.format("Sub_Meal with ID doesn't exist : %s", draft_sub_meal_id));
        }

        sub_meal_id_map.get(draft_sub_meal_id).add_Ingredient_Data(ingredient_row);
    }

    //##################################################################################################################
    // Accessor Methods
    //##################################################################################################################
    // Objects
    public Meal_ID_OBJ get_Meal_ID_OBJ()
    {
        return meal_id_obj;
    }

    public boolean is_Sub_Meal_In_List(int draft_sub_meal_id)
    {
        return sub_meal_id_map.containsKey(draft_sub_meal_id);
    }

    // #######################################
    // Integer
    // #######################################
    public int get_No_Of_Sub_Meals_In_Meal()
    {
        return sub_meal_id_map.size();
    }

    public int get_Draft_Meal_ID()
    {
        return meal_id_obj.get_Draft_Meal_ID();
    }

    // #######################################
    // Collections
    // #######################################
    public HashMap<Integer, Sub_Meal_ID_OBJ> get_Sub_Meal_ID_Map()
    {
        return sub_meal_id_map;
    }
}
