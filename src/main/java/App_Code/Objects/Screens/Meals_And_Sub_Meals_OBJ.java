package App_Code.Objects.Screens;

import App_Code.Objects.Data_Objects.ID_Objects.MetaData_ID_Object.Meal_ID_OBJ;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Meals_And_Sub_Meals_OBJ
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private Meal_ID_OBJ meal_id_obj;
    private LinkedHashMap<Integer, ArrayList<ArrayList<Object>>> sub_meals_data_map = new LinkedHashMap<>();
    
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Meals_And_Sub_Meals_OBJ(Meal_ID_OBJ meal_id_obj, LinkedHashMap<Integer, ArrayList<ArrayList<Object>>> sub_meals_data_map)
    {
        this.meal_id_obj = meal_id_obj;
        this.sub_meals_data_map = sub_meals_data_map;
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    public Meal_ID_OBJ get_Meal_ID_OBJ()
    {
        return meal_id_obj;
    }
    
    public LinkedHashMap<Integer, ArrayList<ArrayList<Object>>>  get_Sub_Meals_Data_Map()
    {
        return sub_meals_data_map;
    }
}
