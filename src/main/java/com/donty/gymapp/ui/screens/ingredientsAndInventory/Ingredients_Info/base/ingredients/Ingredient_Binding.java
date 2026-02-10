package com.donty.gymapp.ui.screens.ingredientsAndInventory.Ingredients_Info.base.ingredients;

import com.donty.gymapp.ui.meta.bindings.Field_Binding;
import com.donty.gymapp.ui.meta.ids.Storable_Ingredient_IDS.Storable_IDS_Parent;
import com.donty.gymapp.gui.controls.combobox.Field_JCombo_Storable_ID;
import com.donty.gymapp.gui.controls.textfields.base.Field_JTxtField_Parent;

public class Ingredient_Binding<T extends Storable_IDS_Parent> extends Field_Binding<T>
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    protected String nutrition_IX_Label;
    
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    // JComboBoxes
    public Ingredient_Binding
    (
            String gui_Label,
            Field_JCombo_Storable_ID<T> component,
            Ingredient_Info_Columns ingredient_Field,
            int query_Field_Pos
    )
    {
        super(gui_Label, component, ingredient_Field, query_Field_Pos);
    }
    
    public Ingredient_Binding
    (
            String gui_Label,
            Field_JCombo_Storable_ID<T> component,
            Ingredient_Info_Columns ingredient_Field,
            int query_Field_Pos,
            String api_Field
    )
    {
        super(gui_Label, component, ingredient_Field, query_Field_Pos);
        this.nutrition_IX_Label = api_Field;
    }
    
    //####################################
    // Text Fields
    //####################################
    public Ingredient_Binding
    (
            String gui_Label,
            Field_JTxtField_Parent<?> component,
            Ingredient_Info_Columns ingredient_Field,
            int query_Field_Pos
    )
    {
        super(gui_Label, component, ingredient_Field, query_Field_Pos);
    }
    
    public Ingredient_Binding
    (
            String gui_Label,
            Field_JTxtField_Parent<?> component,
            Ingredient_Info_Columns ingredient_Field,
            int query_Field_Pos,
            String api_Field
    )
    {
        super(gui_Label, component, ingredient_Field, query_Field_Pos);
        
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
