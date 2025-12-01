package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Ingredient_Form;

import App_Code.Objects.Data_Objects.Field_Bindings.Field_Binding;
import App_Code.Objects.Gui_Objects.Combo_Boxes.Field_JComboBox;

import java.awt.*;

public class Ingredients_Form_Binding<T> extends Field_Binding<T>
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    protected String nutrition_IX_Label;
    
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    // JComboBoxes
    public Ingredients_Form_Binding(String gui_Label, Field_JComboBox<T> component, String mysql_Field, int query_Field_Pos)
    {
        super(gui_Label, component, mysql_Field, query_Field_Pos);
    }
    
    public Ingredients_Form_Binding(String gui_Label, Field_JComboBox<T> component, String mysql_Field, int query_Field_Pos, String api_Field)
    {
        super(gui_Label, component, mysql_Field, query_Field_Pos);
        this.nutrition_IX_Label = api_Field;
    }
    
    //####################################
    // Text Fields
    //####################################
    public Ingredients_Form_Binding(String gui_Label, Component component, String mysql_Field, int query_Field_Pos, Class<T> type, String api_Field)
    {
        super(gui_Label, component, mysql_Field, query_Field_Pos, type);
        
        this.nutrition_IX_Label = api_Field;
    }
    
    public Ingredients_Form_Binding(String gui_Label, Component component, String mysql_Field, int query_Field_Pos, Class<T> type)
    {
        super(gui_Label, component, mysql_Field, query_Field_Pos, type);
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    public String get_Nutrition_IX_Label()
    {
        return nutrition_IX_Label;
    }
}
