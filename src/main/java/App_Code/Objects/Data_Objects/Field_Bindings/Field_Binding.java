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
    
    private Component component;
    
    private int query_Field_Pos;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Field_Binding(String gui_Label, Field_JComboBox<T> component, String mysql_Field, int query_Field_Pos)
    {
        constructor_Setup(gui_Label, component, mysql_Field, query_Field_Pos);
    }
    
    public Field_Binding(String gui_Label, Component component, String mysql_Field, int query_Field_Pos)
    {
        constructor_Setup(gui_Label, component, mysql_Field, query_Field_Pos);
    }
    
    private void constructor_Setup(String gui_Label, Component component, String mysql_Field, int query_Field_Pos)
    {
        this.gui_Label = gui_Label;
        this.component = component;
        this.mysql_Field_Name = mysql_Field;
        
        this.query_Field_Pos = query_Field_Pos;
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
    
    public int get_Field_Query_Pos()
    {
        return query_Field_Pos;
    }
}
