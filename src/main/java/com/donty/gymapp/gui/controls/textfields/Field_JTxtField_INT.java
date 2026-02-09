package com.donty.gymapp.gui.controls.textfields;

import com.donty.gymapp.gui.controls.textfields.base.Field_JTxtField_Parent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Field_JTxtField_INT extends Field_JTxtField_Parent<Integer>
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    protected boolean can_Be_0 = false;
    protected Integer start_Range = null, end_Range = null;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Field_JTxtField_INT(String label, int char_Limit)
    {
        super(label, char_Limit, Integer.class);
    }
    
    public Field_JTxtField_INT(String label, int char_Limit, boolean can_Be_0)
    {
        super(label, char_Limit, Integer.class);
        
        this.can_Be_0 = can_Be_0;
    }
    
    public Field_JTxtField_INT(String label, int char_Limit, int start_Range, int end_Range)
    {
        super(label, char_Limit, Integer.class);
        
        this.start_Range = start_Range;
        this.end_Range = end_Range;
    }
    
    public Field_JTxtField_INT(String label, int char_Limit, boolean can_Be_0, int start_Range, int end_Range)
    {
        super(label, char_Limit, Integer.class);
        
        this.can_Be_0 = can_Be_0;
        this.start_Range = start_Range;
        this.end_Range = end_Range;
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    @Override
    protected boolean validate_Txt_Field(LinkedHashMap<String, ArrayList<String>> error_Map, ArrayList<String> error_MSGs)
    {
        try
        {
            String txt = get_Text();
            
            //#################################
            // Contains any symbols or letters
            //#################################
            if (does_String_Contain_Given_Characters("[^0-9]"))
            {
                error_MSGs.add("Cannot Contain Symbols or, Letter ! Only Integers !");
                error_Map.put(label, error_MSGs);
                
                //#################################
                // Convert Double / BD to Integer
                //#################################
                if (txt.contains("."))
                {
                    BigDecimal number = new BigDecimal(txt);
                    BigDecimal integer_Value = number.setScale(0, RoundingMode.DOWN);
                    
                    setText(integer_Value.toPlainString());
                }
                return false;
            }
            
            //#################################
            // Format Text
            //#################################
            int int_Value = Integer.parseInt(txt);
            setText(String.valueOf(int_Value));
            
            //#################################
            // Range
            //#################################
            if ((start_Range != null && end_Range != null) && (int_Value < start_Range || end_Range < int_Value))
            {
                error_MSGs.add(String.format("Number Must be in Range of %s - %s Inclusive !", start_Range, end_Range));
                error_Map.put(label, error_MSGs);
                return false;
            }
            
            //#################################
            // 0 Check
            //#################################
            if (! can_Be_0 && int_Value == 0)
            {
                error_MSGs.add("Number cannot be 0!");
                error_Map.put(label, error_MSGs);
                return false;
            }
            
            //#################################
            // Return Output
            //#################################
            return true;
        }
        catch (Exception e) { return false; }
    }

    public Integer get_Text_Casted_To_Type() throws Exception
    {
        return super.get_Text_Casted_To_Type();
    }
    
}
