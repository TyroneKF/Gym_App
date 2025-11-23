package App_Code.Objects.Data_Objects.Storable_Ingredient_IDS;

import App_Code.Objects.Data_Objects.ID_Object;

/**
 *  Permits restricts the classes that can extend this object enforces type restriction in case its accidentally extended
 */


public sealed class Storable_IDS_Parent extends ID_Object permits Ingredient_Name_ID_OBJ, Ingredient_Type_ID_Obj, Measurement_ID_OBJ, Store_ID_OBJ
{
    protected Storable_IDS_Parent(int id, String name)
    {
        super(id, name);
    }
}
