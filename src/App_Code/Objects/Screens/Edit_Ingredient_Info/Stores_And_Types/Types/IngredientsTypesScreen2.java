package App_Code.Objects.Screens.Edit_Ingredient_Info.Stores_And_Types.Types;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Screens.Edit_Ingredient_Info.IngredientsInfo.Add_Or_Edit_Ingredients_Screen;

import java.util.Collection;

public class IngredientsTypesScreen2 extends ParentClass
{
    public IngredientsTypesScreen2(MyJDBC db, Add_Or_Edit_Ingredients_Screen add_or_edit_ingredients_screen, Collection<String> jcomboBoxList)
    {
        super(db, add_or_edit_ingredients_screen, jcomboBoxList);
    }


}
