package com.donty.gymapp.Data_Objects.Field_Bindings;

import com.donty.gymapp.Data_Objects.Field_Bindings.Parent.Field_Binding;
import com.donty.gymapp.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Storable_IDS_Parent;
import com.donty.gymapp.Gui_Objects.Combo_Boxes.Field_JCombo_Storable_ID;
import com.donty.gymapp.Gui_Objects.Text_Fields.Parent.Field_JTxtField_Parent;

public class Ingredients_Form_Binding<T extends Storable_IDS_Parent> extends Field_Binding<T>
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    protected String nutrition_IX_Label;
    
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    // JComboBoxes
    public Ingredients_Form_Binding(String gui_Label, Field_JCombo_Storable_ID<T> component, String mysql_Field, int query_Field_Pos)
    {
        super(gui_Label, component, mysql_Field, query_Field_Pos);
    }
    
    public Ingredients_Form_Binding(String gui_Label, Field_JCombo_Storable_ID<T> component, String mysql_Field, int query_Field_Pos, String api_Field)
    {
        super(gui_Label, component, mysql_Field, query_Field_Pos);
        this.nutrition_IX_Label = api_Field;
    }
    
    //####################################
    // Text Fields
    //####################################
    public Ingredients_Form_Binding(String gui_Label, Field_JTxtField_Parent<?> component, String mysql_Field, int query_Field_Pos)
    {
        super(gui_Label, component, mysql_Field, query_Field_Pos);
    }
    
    public Ingredients_Form_Binding(String gui_Label, Field_JTxtField_Parent<?> component, String mysql_Field, int query_Field_Pos, String api_Field)
    {
        super(gui_Label, component, mysql_Field, query_Field_Pos);
        
        this.nutrition_IX_Label = api_Field;
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    public String get_Nutrition_IX_Label()
    {
        return nutrition_IX_Label;
    }
}
