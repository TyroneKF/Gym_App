package App_Code.Objects.Data_Objects.Field_Bindings;

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
    public Ingredients_Form_Binding(String gui_Label, Field_JComboBox<T> component, String mysql_Field)
    {
        super(gui_Label, component, mysql_Field);
    }
    
    public Ingredients_Form_Binding(String gui_Label, Field_JComboBox<T> component, String mysql_Field, String api_Field)
    {
        super(gui_Label, component, mysql_Field);
        this.nutrition_IX_Label = api_Field;
    }
    
    //####################################
    // Text Fields
    //####################################
    public Ingredients_Form_Binding(String gui_Label, Component component, String mysql_Field, Class<T> type, String api_Field)
    {
        super(gui_Label, component, mysql_Field, type);
        
        this.nutrition_IX_Label = api_Field;
    }
    
    public Ingredients_Form_Binding(String gui_Label, Component component, String mysql_Field, Class<T> type)
    {
        super(gui_Label, component, mysql_Field, type);
    }
    
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    public String get_Nutrition_IX_Label()
    {
        return nutrition_IX_Label;
    }
}
