package App_Code.Objects.Data_Objects;

public class Ingredient_Name_OBJ extends ID_Object
{
    // #################################################################################################################
    // Variables
    // #################################################################################################################
    Ingredient_Type_OBJ type_ID_Obj;
    
    // #################################################################################################################
    // Constructor
    // #################################################################################################################
    public Ingredient_Name_OBJ(int id, String name, Ingredient_Type_OBJ type_ID_OBJ)
    {
        super(id, name);
        
        this.type_ID_Obj = type_ID_OBJ;
    }
    
    // #################################################################################################################
    // Accessor Methods
    // #################################################################################################################
    public int get_Ingredient_Type_ID()
    {
        return type_ID_Obj.get_ID();
    }
    
    public String get_Ingredient_Name()
    {
        return type_ID_Obj.get_Name();
    }
}
