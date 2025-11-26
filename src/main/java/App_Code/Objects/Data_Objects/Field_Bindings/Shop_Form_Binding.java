package App_Code.Objects.Data_Objects.Field_Bindings;

import App_Code.Objects.Gui_Objects.Field_JComboBox;

import java.awt.*;

public class Shop_Form_Binding<T>  extends Field_Binding<T>
{
    public Shop_Form_Binding(String gui_Label, Field_JComboBox<T> component, String mysql_Field)
    {
        super(gui_Label, component, mysql_Field);
    }
    
    public Shop_Form_Binding(String gui_Label, Component component, String mysql_Field, Class<T> type)
    {
        super(gui_Label, component, mysql_Field, type);
    }
}
