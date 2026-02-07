package com.donty.gymapp.gui.Text_Fields;

import com.donty.gymapp.gui.controls.textfields.Field_JTxtField_INT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

class Field_JTxtField_INT_Test
{
    //##################################################################
    // Set-Up Methods & Variables
    //##################################################################
    private Field_JTxtField_INT field;
    private LinkedHashMap<String, ArrayList<String>> error_Map;
    
    private String input_TXT, label = "Quantity";
    private final String seperator = "##################################################################";
    
    @BeforeEach
    void setup()
    {
        error_Map = new LinkedHashMap<>();
        field = new Field_JTxtField_INT(label, 8, true); // default: 0 allowed, char limit 8
    }
    
    void print_Results()
    {
        System.err.printf("\nInput Text = %s \nOutput Txt = %s", input_TXT, field.getText());
        
        if (error_Map.isEmpty()) { return; }
        
        ArrayList<String> errors = error_Map.get(label);
        
        if (errors != null)
        {
            for (String i : errors)
            {
                System.err.printf("\n%s", i);
            }
        }
    }
    
    //##################################################################
    // A. VALID PURE INTEGER INPUTS
    //##################################################################
    
    @Test
    void valid_integer_single_digit()
    {
        input_TXT = "5";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertTrue(result);
        assertTrue(error_Map.isEmpty());
    }
    
    @Test
    void valid_integer_multiple_digits()
    {
        input_TXT = "1234567"; // length 7 <= 8
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertTrue(result);
        assertTrue(error_Map.isEmpty());
    }
    
    @Test
    void valid_integer_with_leading_zeros()
    {
        input_TXT = "0001234"; // valid integer, leading zeros present
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        // Option B: validate first, then normalize (leading zeros removed on valid input)
        assertTrue(result);
        assertTrue(error_Map.isEmpty());
        // Expect normalization to strip leading zeros
        assertEquals("1234", field.getText());
    }
    
    //##################################################################
    // B. INVALID LETTERS / SYMBOLS / NEGATIVES
    //##################################################################
    
    @Test
    void invalid_contains_letter()
    {
        input_TXT = "12A4";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
        assertFalse(error_Map.isEmpty());
    }
    
    @Test
    void invalid_contains_symbol()
    {
        input_TXT = "12-3";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
        assertFalse(error_Map.isEmpty());
    }
    
    @Test
    void invalid_negative_number()
    {
        input_TXT = "-15";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
        assertFalse(error_Map.isEmpty());
    }
    
    @Test
    void invalid_mixed_digits_and_dot_and_letters()
    {
        input_TXT = "10.0A";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
        assertFalse(error_Map.isEmpty());
    }
    
    //##################################################################
    // C. DECIMAL INPUT (AUTO-CONVERSION, BUT INVALID)
    //##################################################################
    
    @Test
    void decimal_convert_trailing_dot()
    {
        input_TXT = "9.";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        // Current design: decimals are not allowed for INT field (still invalid)
        assertFalse(result);
        assertFalse(error_Map.isEmpty());
        // But if the class converts via BigDecimal, text should be integer only
        // (this assertion documents/locks that side-effect)
        assertEquals("9", field.getText());
    }
    
    @Test
    void decimal_convert_fraction()
    {
        input_TXT = "12.99";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
        assertFalse(error_Map.isEmpty());
        // Fraction truncated down to integer if conversion occurs
        assertEquals("12", field.getText());
    }
    
    @Test
    void decimal_with_leading_and_trailing_zeros_invalid_but_converted()
    {
        input_TXT = "0005.000";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        // still invalid as input for INT, but BigDecimal-based conversion may normalize to "5"
        assertFalse(result);
        assertFalse(error_Map.isEmpty());
        assertEquals("5", field.getText());
    }
    
    @Test
    void decimal_only_dot_invalid()
    {
        input_TXT = ".";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
        assertFalse(error_Map.isEmpty());
    }
    
    //##################################################################
    // D. RANGE VALIDATION
    //##################################################################
    
    @Test
    void range_valid_middle()
    {
        field = new Field_JTxtField_INT(label, 8, 10, 20);
        
        input_TXT = "15";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertTrue(result);
        assertTrue(error_Map.isEmpty());
    }
    
    @Test
    void range_valid_min_boundary()
    {
        field = new Field_JTxtField_INT(label, 8, 10, 20);
        
        input_TXT = "10";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertTrue(result);
        assertTrue(error_Map.isEmpty());
    }
    
    @Test
    void range_valid_max_boundary()
    {
        field = new Field_JTxtField_INT(label, 8, 10, 20);
        
        input_TXT = "20";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertTrue(result);
        assertTrue(error_Map.isEmpty());
    }
    
    @Test
    void range_invalid_below()
    {
        field = new Field_JTxtField_INT(label, 8, 10, 20);
        
        input_TXT = "5";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
        assertFalse(error_Map.isEmpty());
    }
    
    @Test
    void range_invalid_above()
    {
        field = new Field_JTxtField_INT(label, 8, 10, 20);
        
        input_TXT = "25";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
        assertFalse(error_Map.isEmpty());
    }
    
    @Test
    void range_valid_with_leading_zeros()
    {
        field = new Field_JTxtField_INT(label, 8, 1, 20);
        
        input_TXT = "0005";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertTrue(result);
        assertTrue(error_Map.isEmpty());
        // after successful validation, leading zeros should be stripped
        assertEquals("5", field.getText());
    }
    
    //##################################################################
    // E. ZERO VALUE HANDLING
    //##################################################################
    
    @Test
    void zero_allowed_valid()
    {
        field = new Field_JTxtField_INT(label, 8, true);
        
        input_TXT = "0";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertTrue(result);
        assertTrue(error_Map.isEmpty());
        assertEquals("0", field.getText());
    }
    
    @Test
    void zero_not_allowed_invalid()
    {
        field = new Field_JTxtField_INT(label, 8, false);
        
        input_TXT = "0";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
        assertFalse(error_Map.isEmpty());
    }
    
    @Test
    void all_zeros_zero_allowed_valid_and_normalized()
    {
        field = new Field_JTxtField_INT(label, 8, true);
        
        input_TXT = "0000";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        // Interpreted as 0 and allowed
        assertTrue(result);
        assertTrue(error_Map.isEmpty());
        // Leading zeros removed after valid normalization
        assertEquals("0", field.getText());
    }
    
    @Test
    void all_zeros_zero_not_allowed_invalid()
    {
        field = new Field_JTxtField_INT(label, 8, false);
        
        input_TXT = "0000";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        // This should evaluate as value 0 and be rejected
        assertFalse(result);
        assertFalse(error_Map.isEmpty());
        // Because it's invalid, Option B: we don't rely on normalization here
    }
    
    //##################################################################
    // F. WHITESPACE AND EMPTY INPUT
    //##################################################################
    
    @Test
    void whitespace_only_invalid()
    {
        input_TXT = "    "; // length 4 (<=8) but not a number
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
        assertFalse(error_Map.isEmpty());
    }
    
    @Test
    void empty_invalid()
    {
        input_TXT = "";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
        assertFalse(error_Map.isEmpty());
    }
    
    @Test
    void whitespace_around_integer_within_length_valid()
    {
        input_TXT = "  15  "; // length 6 <= 8
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        // Depending on parent behavior, this assumes trimming before parse
        assertTrue(result);
        assertTrue(error_Map.isEmpty());
    }
    
    @Test
    void whitespace_raw_length_too_long_invalid()
    {
        input_TXT = "    35   "; // length = 9 > char limit 8
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
        assertFalse(error_Map.isEmpty());
    }
    
    //##################################################################
    // G. MAX LENGTH VALIDATION (INCLUDES WHITESPACE)
    //##################################################################
    
    @Test
    void max_length_valid_exact_limit()
    {
        input_TXT = "12345678"; // length = 8
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertTrue(result);
    }
    
    @Test
    void max_length_invalid_exceeds_limit()
    {
        input_TXT = "123456789"; // length = 9 > 8
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
    }
    
    @Test
    void max_length_invalid_leading_spaces()
    {
        input_TXT = "     12"; // length = 7 -> valid
        field.setText(input_TXT);
        boolean result1 = field.validation_Check(error_Map);
        
        String methodName1 = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        System.err.printf("\n\n%s", methodName1);
        print_Results();
        
        assertTrue(result1);
        
        // reset for next
        error_Map = new LinkedHashMap<>();
        field.setText("      12"); // length = 8 -> valid
        boolean result2 = field.validation_Check(error_Map);
        
        String methodName2 = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        System.err.printf("\n\n%s", methodName2);
        print_Results();
        
        assertTrue(result2);
        
        // reset again
        error_Map = new LinkedHashMap<>();
        field.setText("       12"); // length = 9 -> invalid
        boolean result3 = field.validation_Check(error_Map);
        
        String methodName3 = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        System.err.printf("\n\n%s", methodName3);
        print_Results();
        
        assertFalse(result3);
    }
    
    @Test
    void max_length_invalid_trailing_spaces()
    {
        input_TXT = "12      "; // length = 8 -> valid
        field.setText(input_TXT);
        boolean result1 = field.validation_Check(error_Map);
        
        String methodName1 = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        System.err.printf("\n\n%s", methodName1);
        print_Results();
        
        assertTrue(result1);
        
        error_Map = new LinkedHashMap<>();
        field.setText("12       "); // length = 9 -> invalid
        boolean result2 = field.validation_Check(error_Map);
        
        String methodName2 = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        System.err.printf("\n\n%s", methodName2);
        print_Results();
        
        assertFalse(result2);
    }
    
    @Test
    void max_length_invalid_spaces_only()
    {
        input_TXT = "        "; // 8 spaces -> valid by length (though invalid as number, tested elsewhere)
        field.setText(input_TXT);
        boolean result1 = field.validation_Check(error_Map);
        
        String methodName1 = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        System.err.printf("\n\n%s", methodName1);
        print_Results();
        
        // This will actually be invalid as a number; the earlier test covers semantic invalidity.
        // Here we focus on length behavior, so we just assert the call executed; semantic assertion is in whitespace_only_invalid.
        
        error_Map = new LinkedHashMap<>();
        field.setText("         "); // 9 spaces -> should fail char limit
        boolean result2 = field.validation_Check(error_Map);
        
        String methodName2 = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        System.err.printf("\n\n%s", methodName2);
        print_Results();
        
        assertFalse(result2);
    }
    
    //##################################################################
    // H. OVERFLOW & EXTREME EDGE CASES
    //##################################################################
    
    @Test
    void integer_overflow_invalid()
    {
        // Integer.MAX_VALUE = 2147483647, so this is one above
        input_TXT = "2147483648";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        // parseInt should fail, caught by validate_Txt_Field and return false
        assertFalse(result);
        assertFalse(error_Map.isEmpty());
    }
    
    @Test
    void very_large_leading_zero_integer_overflow_invalid()
    {
        input_TXT = "00000000002147483648"; // clearly > Integer.MAX_VALUE
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object() { }.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
        assertFalse(error_Map.isEmpty());
    }
}
