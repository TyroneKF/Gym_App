package com.donty.gymapp.gui.controls.textfields;

import com.donty.gymapp.gui.controls.textfields.base.Field_JTxtField_Parent;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Field_JTxtField_String extends Field_JTxtField_Parent<String>
{
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Field_JTxtField_String(String label, int char_Limit)
    {
        super(label, char_Limit, String.class);
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    @Override
    protected boolean validate_Txt_Field(LinkedHashMap<String, ArrayList<String>> error_Map, ArrayList<String> error_MSGs)
    {
        // Does string contain given symbols or, numbers or a defined pattern by the user
        // Matches any character that is NOT a letter, space, period, apostrophe, or hyphen
        if (does_String_Contain_Given_Characters("[^\\p{L} .'\\-]"))
        {
            error_MSGs.add("Can only contain; letters, spaces, period, apostrophe, or hyphens!");
            error_Map.put(label, error_MSGs);
            
            return false;
        }
        
        return true;
    }
}
