package App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS;

import java.util.Objects;

public final class Ingredient_Name_ID_OBJ extends Storable_IDS_Parent
{
    // #################################################################################################################
    // Variables
    // #################################################################################################################
    private Ingredient_Type_ID_OBJ ingredient_Type_ID_Obj = null;
    
    // #################################################################################################################
    // Constructor
    // #################################################################################################################
    public Ingredient_Name_ID_OBJ(int id, String name, Ingredient_Type_ID_OBJ type_ID_OBJ)
    {
        super(id, name);
        
        set_Ingredient_Type_ID_Obj(type_ID_OBJ);
    }
    
    // #################################################################################################################
    // Mutator Methods
    // #################################################################################################################
    public void set_Ingredient_Type_ID_Obj(Ingredient_Type_ID_OBJ ingredient_type_ID_Obj)
    {
        Objects.requireNonNull(ingredient_type_ID_Obj, "Ingredient type cannot be null");
        this.ingredient_Type_ID_Obj = ingredient_type_ID_Obj;
    }
    
    // #################################################################################################################
    // Accessor Methods
    // #################################################################################################################
    public Ingredient_Type_ID_OBJ get_Ingredient_Type_Obj()
    {
        return ingredient_Type_ID_Obj;
    }
    
    public int get_Ingredient_Type_ID()
    {
        return ingredient_Type_ID_Obj.get_ID();
    }
    
    public String get_Ingredient_Type_Name()
    {
        return ingredient_Type_ID_Obj.get_Name();
    }
}
