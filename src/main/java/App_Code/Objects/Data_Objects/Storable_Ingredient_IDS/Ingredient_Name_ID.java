package App_Code.Objects.Data_Objects.Storable_Ingredient_IDS;

import App_Code.Objects.Data_Objects.ID_Object;

public final class Ingredient_Name_ID extends Storable_IDS_Parent
{
    // #################################################################################################################
    // Variables
    // #################################################################################################################
    private Ingredient_Type_ID assigned_Ingredient_Type_ID_Obj;
    
    // #################################################################################################################
    // Constructor
    // #################################################################################################################
    public Ingredient_Name_ID(int id, String name, Ingredient_Type_ID type_ID_OBJ)
    {
        super(id, name);
        
        this.assigned_Ingredient_Type_ID_Obj = type_ID_OBJ;
    }
    
    // #################################################################################################################
    // Accessor Methods
    // #################################################################################################################
    public int get_Ingredient_Type_ID()
    {
        return assigned_Ingredient_Type_ID_Obj.get_ID();
    }
    
    public Ingredient_Type_ID get_Ingredient_Type_ID_Obj()
    {
        return assigned_Ingredient_Type_ID_Obj;
    }
    
    public String get_Ingredient_Name()
    {
        return assigned_Ingredient_Type_ID_Obj.get_Name();
    }
}
