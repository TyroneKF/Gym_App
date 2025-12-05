package App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Ingredient_Types;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.Shared_Data_Registry;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Ingredients_Info_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Add_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Parent_Screen;

public class Add_Ingredient_Type extends Add_Screen
{
    public Add_Ingredient_Type(MyJDBC db, Shared_Data_Registry shared_Data_Registry, Ingredients_Info_Screen ingredient_Info_Screen,
                               Parent_Screen parent_Screen)
    {
        super(db, shared_Data_Registry, ingredient_Info_Screen, parent_Screen);
    }
    
    @Override
    protected void set_Screen_Variables()
    {
        super.main_Label = "Add Ingredient Type Name";
        
        super.data_Gathering_Name = "Ingredient Type Name";
        super.db_ColumnName_Field = "ingredient_type_name";
        super.db_TableName = "ingredient_types";
    }
    
    @Override
    protected void additional_Add_Screen_Objects()
    {
    
    }
    
    @Override
    protected boolean additional_Validate_Form()
    {
        return true;
    }
   
    @Override
    protected final void update_Other_Screens()
    {
        ingredient_Info_Screen.update_Types_JC();
    }
}