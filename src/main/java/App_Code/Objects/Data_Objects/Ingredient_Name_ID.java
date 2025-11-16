package App_Code.Objects.Data_Objects;

public class Ingredient_Name_ID extends ID_Object
{
    // #################################################################################################################
    // Variables
    // #################################################################################################################
    private Ingredient_Type_ID type_ID_Obj;
    
    // #################################################################################################################
    // Constructor
    // #################################################################################################################
    public Ingredient_Name_ID(int id, String name, Ingredient_Type_ID type_ID_OBJ)
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
