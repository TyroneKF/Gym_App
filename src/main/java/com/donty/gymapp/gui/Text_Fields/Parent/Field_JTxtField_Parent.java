package com.donty.gymapp.Gui_Objects.Text_Fields.Parent;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Field_JTxtField_Parent<T> extends JTextField
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    protected String label;
    protected int char_Limit;
    protected Class<T> type_Cast;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    // Text Field
    public Field_JTxtField_Parent(String label, int char_Limit, Class<T> type_Cast)
    {
        // Field All Constructors use
        this.label = label;
        this.char_Limit = char_Limit; // IF SeText is used on a text bigger than char_Limit the text is scrapped = ""
        this.type_Cast = type_Cast;
        
        setDocument(new JTextFieldLimit(char_Limit)); // Set Character Limit On Text Field
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    // Reset Methods
    public void reset_Txt_Field() { setText(""); }
    
    private String remove_Space_And_Hidden_Chars(String txt_To_Edit)
    {
        // remove all whitespace & hidden characters like \n
        return txt_To_Edit != null && ! txt_To_Edit.isEmpty() ? txt_To_Edit.trim().replaceAll("\\p{C}", "") : "";
    }
    
    //##############################################
    // Mutator Methods
    //##############################################
    @Override
    public void setText(String txt)
    {
        super.setText(remove_Space_And_Hidden_Chars(txt));
    }
    
    //##############################################
    // Accessor Methods
    //##############################################
    public String get_Text()
    {
        return remove_Space_And_Hidden_Chars(getText());
    }
    
    public boolean is_Txt_Field_Empty()
    {
        return get_Text().isEmpty();
    }
    
    //##############################################
    // Casting Types Methods
    //##############################################
    public T get_Text_Casted_To_Type() throws Exception
    {
        return cast_Obj_To_Type(getText()); // JTextField version of getting text as the next method already does remove_Hidden TXT method
    }
    
    private T cast_Obj_To_Type(Object object) throws Exception
    {
        String object_String = remove_Space_And_Hidden_Chars(object.toString()); // Get Object in TXT form
        
        if (type_Cast == BigDecimal.class) { return type_Cast.cast(new BigDecimal(object_String)); }
        
        else if (type_Cast == Integer.class) { return type_Cast.cast(Integer.valueOf(object_String)); }
        
        else if (type_Cast == String.class) { return type_Cast.cast(object_String); }
        
        throw new Exception("Unexpected Type : " + type_Cast);
    }
    
    //##############################################
    // Validation Methods
    //##############################################
    public boolean validation_Check(LinkedHashMap<String, ArrayList<String>> error_Map)
    {
        ArrayList<String> error_MSGs = new ArrayList<>();
        
        if (is_Txt_Field_Empty())  // Check text is not Empty
        {
            error_MSGs.add("Cannot be empty !");
            error_Map.put(label, error_MSGs);
            
            return false;
        }
        
        return validate_Txt_Field(error_Map, error_MSGs);
    }
    
    protected abstract boolean validate_Txt_Field(LinkedHashMap<String, ArrayList<String>> error_Map, ArrayList<String> error_MSGs);
    
    /*
     *  Does string contain given symbols or, numbers or a defined pattern by the user
     *  Allows : \p{L} = any Unicode letter, space, period . , apostrophe ' , hyphen -
     */
    protected boolean does_String_Contain_Given_Characters(String condition)
    {
        // Variables
        String stringToCheck = get_Text();
        
        // Exit Clause
        if (stringToCheck == null) { return false; }
        
        // Does string contain given symbols or, numbers or a defined pattern by the user
        // Allows : \p{L} = any Unicode letter, space, period . , apostrophe ' , hyphen -
        Pattern p1 =
                (condition == null || condition.isEmpty()) ?
                        Pattern.compile("[^a-zA-Z]", Pattern.CASE_INSENSITIVE) :
                        Pattern.compile(condition, Pattern.CASE_INSENSITIVE);
        
        Matcher m1 = p1.matcher(stringToCheck.replaceAll("\\s+", ""));
        
        return m1.find();
    }
    
    public boolean is_Field_Data_Equal_To(Object object)
    {
        try
        {
            T this_Object_Data = get_Text_Casted_To_Type(); // Get this objects Field TXT Type Casted
            T object_Casted_Data = cast_Obj_To_Type(object);
            
            if (this_Object_Data == null || object_Casted_Data == null) // Case edge
            {
                return this_Object_Data == object_Casted_Data;
            }
            
            // return are these objects equal
            return type_Cast.equals(BigDecimal.class)
                    ? ((BigDecimal) this_Object_Data).compareTo((BigDecimal) object_Casted_Data) == 0
                    : this_Object_Data.equals(object_Casted_Data);
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
