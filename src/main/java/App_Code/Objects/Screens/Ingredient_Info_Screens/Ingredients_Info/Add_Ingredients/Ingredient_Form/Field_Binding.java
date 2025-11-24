package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Ingredient_Form;

import java.awt.*;

public class Field_Binding<T>
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private String
            gui_Label,
            nutrition_IX_Label,
            mysql_Field_Name;
    
    private Component gui_Component;
    private Class<T> field_Type;
    private Component component;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Field_Binding(
            String gui_Label,
            Component component,
            String mysql_Field,
            Class<T> type,
            String api_Field)
    {
        this.gui_Label = gui_Label;
        this.mysql_Field_Name = mysql_Field;
        this.field_Type = type;
        this.nutrition_IX_Label = api_Field;
        this.component = component;
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    public Component get_Gui_Component()
    {
        return component;
    }
    
    public String get_Gui_Label()
    {
        return gui_Label;
    }
    
    public String get_Mysql_Field_Name()
    {
        return mysql_Field_Name;
    }
    
    public String get_Nutrition_IX_Label()
    {
        return nutrition_IX_Label;
    }
    
    public Class<T> get_Field_Type()
    {
        return field_Type;
    }
}
