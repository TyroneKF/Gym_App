package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Shop_Form;

import App_Code.Objects.Data_Objects.Field_Bindings.Field_Binding;
import App_Code.Objects.Gui_Objects.Combo_Boxes.Field_JComboBox;

import java.awt.*;

public class Shop_Form_Binding<T>  extends Field_Binding<T>
{
    public Shop_Form_Binding(String gui_Label, Field_JComboBox<T> component, String mysql_Field, int query_Field_Pos)
    {
        super(gui_Label, component, mysql_Field, query_Field_Pos);
    }
    
    public Shop_Form_Binding(String gui_Label, Component component, String mysql_Field, int query_Field_Pos)
    {
        super(gui_Label, component, mysql_Field, query_Field_Pos);
    }
}
