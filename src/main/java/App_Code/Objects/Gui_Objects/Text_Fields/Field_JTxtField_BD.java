package App_Code.Objects.Gui_Objects.Text_Fields;

import App_Code.Objects.Gui_Objects.Text_Fields.Parent.Field_JTxtField_Parent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Field_JTxtField_BD extends Field_JTxtField_Parent<BigDecimal>
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    
    protected boolean can_Be_0 = true;
    protected final int expected_decimal_Scale = 2;
    protected int precision, expected_Unscaled_Integer;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Field_JTxtField_BD(String label, int char_Limit)
    {
        super(label, char_Limit, BigDecimal.class);
        variable_Setup();
    }
    
    public Field_JTxtField_BD(String label, int char_Limit, boolean can_Be_0)
    {
        super(label, char_Limit, BigDecimal.class);
        this.can_Be_0 = can_Be_0;
        variable_Setup();
    }
    
    private void variable_Setup()
    {
        precision = char_Limit - 1; // -1 for decimal place, precision is for numbers only
        expected_Unscaled_Integer = precision - expected_decimal_Scale;
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    @Override
    protected boolean validate_Txt_Field(LinkedHashMap<String, ArrayList<String>> error_Map, ArrayList<String> error_MSGs)
    {
        boolean output = decimal_Validation(get_Text(), error_MSGs); // Capture output
        
        if (! error_MSGs.isEmpty()) { error_Map.put(label, error_MSGs); } // if any errors were found, report
        
        return output;
    }
    
    private boolean decimal_Validation(String value, ArrayList<String> error_MSGs)
    {
        boolean no_Error = true;
        //String value = get_Text();
        
        try
        {
            //#####################################################
            // Validate : Decimal Formatting
            //#####################################################
            String[] parts = value.split("\\.");
            int sections = parts.length;
            
            long decimal_Count = value.chars()
                    .mapToObj(c -> (char) c)
                    .filter(ch -> ch == '.')
                    .count();
            
            if (sections > 0 && (parts[0].isEmpty()) && sections == 2)
            {
                value = String.format("0%s", value);
            }
            if (sections > 2 || decimal_Count > 1) // Multiple Decimal Points
            {
                error_MSGs.add("Decimal  cannot contain more than 1 '.' decimal point!");
                return false;
            }
            if (sections == 1 && value.contains(".")) // Just a decimal point by itself = Remove converting to bd = error
            {
                value = value.replace(".", ""); // Remove Decimal point etc; '8.' becomes '8'
            }
            
            //#####################################################
            // Create Variables
            //#####################################################
            BigDecimal bd = new BigDecimal(value); // Convert to Big Decimal
            
            int bd_Scale = bd.scale(); // number of digits after "." decimal point
            int bd_Precision = bd.precision(); // Precision counts all digits, before and after the decimal point, ignoring sign.
            
            //#####################################################
            // Check : bd isn't negative
            //#####################################################
            if (bd.compareTo(BigDecimal.ZERO) < 0)
            {
                error_MSGs.add("Value must be bigger than 0!");
                no_Error = false;
            }
            
            if (! can_Be_0 && bd.compareTo(BigDecimal.ZERO) == 0)
            {
                error_MSGs.add("Cannot be equal to 0!");
                no_Error = false;
            }
            
            //#####################################################
            // Check: Unscaled Integer Valid (Before Decimal)
            //#####################################################
            if ((bd_Precision - bd_Scale) > expected_Unscaled_Integer)
            {
                error_MSGs.add("Can only contain max 5 digits before the decimal point!");
                no_Error = false;
            }
            
            //#####################################################
            // Check: Precision (Max 7 digits) 5 before '.' 2 after
            //#####################################################
            if (bd_Scale > expected_decimal_Scale)
            {
                error_MSGs.add("Can only contain 2 decimal places ('.' 2 digits after)!");
            }
            
            //#####################################################
            // Format : Decimal Scale
            //#####################################################
            if (bd_Scale < expected_decimal_Scale) // Less than required Decimal Places
            {
                StringBuilder new_BD = new StringBuilder(bd.toPlainString()); // convert bd back to string
                
                if (bd_Scale == 0) { new_BD.append("."); } // Add Decimal Point
                
                new_BD.append("0".repeat(Math.max(0, expected_decimal_Scale - bd_Scale))); // Add as many 0's as needed
                
                setText(new_BD.toString());
            }
            else if (bd_Scale > expected_decimal_Scale) // IF decimal scale has more digits than allowed Round DOWN
            {
                setText(String.valueOf(bd.setScale(expected_decimal_Scale, RoundingMode.DOWN))); // round the number
            }
            
            //#####################################################
            // Output
            //#####################################################
            return no_Error;
        }
        catch (Exception e)
        {
            error_MSGs.add("Cannot be converted to a Big Decimal!");
            return false;
        }
    }
}
