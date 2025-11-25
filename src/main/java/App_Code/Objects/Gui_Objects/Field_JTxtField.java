package App_Code.Objects.Gui_Objects;

import javax.swing.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Field_JTxtField extends JTextField
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    protected boolean is_Decimal_Field = false, can_Be_0 = true;
    protected int
            precision = 7,
            expected_decimal_Scale = 2,
            expected_Unscaled_Integer = precision - expected_decimal_Scale,
            char_Limit;
    
    protected String label;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Field_JTxtField(String label, int char_Limit, boolean is_Decimal_Field, boolean can_Be_0) // Specific Decimal
    {
        constructor_Setup(label, char_Limit, is_Decimal_Field, can_Be_0);
    }
    
    public Field_JTxtField(String label, int char_Limit, boolean is_Decimal_Field) // Generic Decimal
    {
        constructor_Setup(label, char_Limit, is_Decimal_Field, null);
    }
    
    public Field_JTxtField(String label, int char_Limit) // Text Field
    {
        constructor_Setup(label, char_Limit, null, null);
    }
    
    
    private void constructor_Setup(String label, int char_Limit, Boolean is_Decimal_Field, Boolean can_Be_0)
    {
        // Field All Constructors use
        this.label = label;
        this.char_Limit = char_Limit; // IF SeText is used on a text bigger than char_Limit the text is scrapped = ""
        
        if (is_Decimal_Field != null) { this.is_Decimal_Field = is_Decimal_Field; }
        
        if (can_Be_0 != null) { this.can_Be_0 = can_Be_0; }
        
        // Optional
        setDocument(new JTextFieldLimit(char_Limit));
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    
    // Reset Methods
    public void reset_Txt_Field() { setText(""); }
    
    //##############################################
    // Accessor Methods
    //##############################################
    public String get_Text()
    {
        return remove_Space_And_Hidden_Chars(getText());
    }
    
    //##############################################
    // Validation Methods
    //##############################################
    public boolean validation_Check(LinkedHashMap<String, ArrayList<String>> error_Map)
    {
        String txt = get_Text();   // Get Text
        
        ArrayList<String> error_MSGs = new ArrayList<>();
        
        if (txt.isEmpty())  // Check text is not Empty
        {
            error_MSGs.add("Cannot be empty !");
            error_Map.put(label, error_MSGs);
            
            return false;
        }
        
        if (! is_Decimal_Field)   // String Validation
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
        else // Decimal Validation
        {
            boolean output = decimal_Validation(txt, error_MSGs); // Capture output
            
            if (! error_MSGs.isEmpty()) { error_Map.put(label, error_MSGs); } // if any errors were found, report
            
            return output;
        }
    }
    
    //#######################
    // Validation Decimals
    //#######################
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
    
    //#######################
    // Validation Strings
    //#######################
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
    
    private String remove_Space_And_Hidden_Chars(String txt_To_Edit)
    {
        // remove all whitespace & hidden characters like \n
        return txt_To_Edit != null && ! txt_To_Edit.isEmpty() ? txt_To_Edit.trim().replaceAll("\\p{C}", "") : "";
    }
}
