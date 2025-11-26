package App_Code.Objects.Data_Objects.ID_Objects.MetaData_ID_Object;

import App_Code.Objects.Data_Objects.ID_Objects.ID_Object;

import java.time.LocalTime;

public final class Meal_ID extends Meta_Data_ID_Parent
{
    // #################################################################################################################
    // Variables
    // #################################################################################################################
    private LocalTime meal_Time;
    
    
    // #################################################################################################################
    // Constructor
    // #################################################################################################################
    public Meal_ID(int id, String meal_name, LocalTime meal_Time)
    {
        super(id, meal_name);
        this.meal_Time = meal_Time;
    }
    
    // #################################################################################################################
    // Accessor Methods
    // #################################################################################################################
    public LocalTime get_Meal_Time()
    {
        return meal_Time;
    }
}
