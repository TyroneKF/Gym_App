package App_Code.Objects.Screens;

import App_Code.Objects.Data_Objects.ID_Objects.MetaData_ID_Object.Meal_ID_OBJ;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Meal_And_Sub_Meals_OBJ
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private Meal_ID_OBJ meal_id_obj;
    private LinkedHashMap<Integer, ArrayList<ArrayList<Object>>> sub_meals_data_map = new LinkedHashMap<>();
    
    private final HashMap<Integer, Integer> sub_meal_id_map; // Stores Draft Sub ID to Source ID
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Meal_And_Sub_Meals_OBJ
    (
            Meal_ID_OBJ meal_id_obj,
            LinkedHashMap<Integer, ArrayList<ArrayList<Object>>> sub_meals_data_map,
            HashMap<Integer, Integer> sub_meal_id_map
    )
    {
        this.meal_id_obj = meal_id_obj;
        this.sub_meals_data_map = sub_meals_data_map;
        this.sub_meal_id_map = sub_meal_id_map;
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    // Objects
    public Meal_ID_OBJ get_Meal_ID_OBJ()
    {
        return meal_id_obj;
    }
    
    // #######################################
    // Integer
    // #######################################
    public int get_No_Of_Sub_Meals_In_Meal()
    {
        return sub_meals_data_map.size();
    }
    
    public int get_Source_Sub_Meal_ID(int draft_sub_meal_id)
    {
        return sub_meal_id_map.get(draft_sub_meal_id);
    }
    
    public int get_Draft_Meal_ID()
    {
       return meal_id_obj.get_Draft_Meal_ID();
    }
    
    // #######################################
    // Collections
    // #######################################
    public LinkedHashMap<Integer, ArrayList<ArrayList<Object>>>  get_Sub_Meals_Data_Map()
    {
        return sub_meals_data_map;
    }
}
