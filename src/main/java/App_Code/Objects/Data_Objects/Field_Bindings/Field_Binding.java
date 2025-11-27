package App_Code.Objects.Data_Objects.Field_Bindings;

import App_Code.Objects.Gui_Objects.Combo_Boxes.Field_JComboBox;

import java.awt.*;

public class Field_Binding<T>
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private String
            gui_Label,
            mysql_Field_Name;
    
    private Component gui_Component;
    private Class<T> field_Type;
    private Component component;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Field_Binding(String gui_Label, Field_JComboBox<T> component, String mysql_Field)
    {
        constructor_Setup(gui_Label, component, mysql_Field, null);
    }
    
    public Field_Binding(String gui_Label, Component component, String mysql_Field, Class<T> type)
    {
        constructor_Setup(gui_Label, component, mysql_Field, type);
    }
    
    private void constructor_Setup(String gui_Label, Component component, String mysql_Field, Class<T> type)
    {
        this.gui_Label = gui_Label;
        this.component = component;
        this.mysql_Field_Name = mysql_Field;
        
        if (type != null) { this.field_Type = type; }
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
    
    public Class<T> get_Field_Type()
    {
        return field_Type;
    }
}
