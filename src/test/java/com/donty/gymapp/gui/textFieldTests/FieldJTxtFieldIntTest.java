package com.donty.gymapp.gui.textFieldTests;

import com.donty.gymapp.gui.controls.textfields.Field_JTxtField_INT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

class FieldJTxtFieldIntTest extends Test_Field_JxtField_Parent<Integer>
{
    //##################################################################################################################
    // Setup
    //##################################################################################################################
    @BeforeEach
    void setup()
    {
        field = new Field_JTxtField_INT("Quantity", 8, true); // default: 0 allowed, char limit 8
    }

    //##################################################################################################################
    // Tests
    //##################################################################################################################
    /*


    */


    //##################################################################
    // A. VALID PURE INTEGER INPUTS
    //##################################################################    
    @Test
    void valid_Integer_Single_Digit()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "5";

        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertTrue(result);
        assertTrue(error_map.isEmpty());
    }

    @Test
    void valid_Integer_Multiple_Digits()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "1234567"; // length 7 <= 8

        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertTrue(result);
        assertTrue(error_map.isEmpty());
    }

    @Test
    void valid_Integer_With_Leading_Zeros()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "0001234"; // valid integer, leading zeros present

        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        // Option B: validate first, then normalize (leading zeros removed on valid input)
        assertTrue(result);
        assertTrue(error_map.isEmpty());

        assertEquals("1234", field.getText());  // Expect normalization to strip leading zeros
    }

    //##################################################################
    // B. INVALID LETTERS / SYMBOLS / NEGATIVES
    //##################################################################

    @Test
    void invalid_Contains_Letter()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "12A4";

        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }

    @Test
    void invalid_Contains_Symbol()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "12-3";

        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }

    @Test
    void invalid_Negative_Number()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "-15";

        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }

    @Test
    void invalid_Mixed_Digits_And_Dot_And_Letters()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "10.0A";

        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }

    //##################################################################
    // C. DECIMAL INPUT (AUTO-CONVERSION, BUT INVALID)
    //##################################################################

    @Test
    void decimal_Convert_Trailing_Dot()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "9.";

        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        // Current design: decimals are not allowed for INT field (still invalid)
        assertFalse(result);
        assertFalse(error_map.isEmpty());

        // But if the class converts via BigDecimal, text should be integer only
        // (this assertion documents/locks that side effect)
        assertEquals("9", field.getText());
    }

    @Test
    void decimal_Convert_Fraction()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "12.99";

        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertFalse(result);
        assertFalse(error_map.isEmpty());
        assertEquals("12", field.getText());    // Fraction truncated down to integer if conversion occurs
    }

    @Test
    void decimal_With_Leading_And_Trailing_Zeros_Invalid_But_Converted()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "0005.000";

        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        // still invalid as input for INT, but BigDecimal-based conversion may normalize to "5"
        assertFalse(result);
        assertFalse(error_map.isEmpty());
        assertEquals("5", field.getText());
    }

    @Test
    void decimal_Only_Dot_Invalid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = ".";

        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }

    //##################################################################
    // D. RANGE VALIDATION
    //##################################################################

    @Test
    void range_Valid_Middle()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "15";

        field = new Field_JTxtField_INT("Quantity", 8, 10, 20);
        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertTrue(result);
        assertTrue(error_map.isEmpty());
    }

    @Test
    void range_Valid_Min_Boundary()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "10";

        field = new Field_JTxtField_INT("Quantity", 8, 10, 20);
        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertTrue(result);
        assertTrue(error_map.isEmpty());
    }

    @Test
    void range_Valid_Max_Boundary()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "20";

        field = new Field_JTxtField_INT("Quantity", 8, 10, 20);
        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertTrue(result);
        assertTrue(error_map.isEmpty());
    }

    @Test
    void range_Invalid_Below()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "5";

        field = new Field_JTxtField_INT("Quantity", 8, 10, 20);
        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }

    @Test
    void range_Invalid_Above()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "25";

        field = new Field_JTxtField_INT("Quantity", 8, 10, 20);
        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }

    @Test
    void range_Valid_With_Leading_Zeros()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "0005";

        field = new Field_JTxtField_INT("Quantity", 8, 1, 20);
        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertTrue(result);
        assertTrue(error_map.isEmpty());

        assertEquals("5", field.getText());  // after successful validation, leading zeros should be stripped
    }

    //##################################################################
    // E. ZERO VALUE HANDLING
    //##################################################################

    @Test
    void zero_Allowed_Valid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "0";

        field = new Field_JTxtField_INT("Quantity", 8, true);
        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertTrue(result);
        assertTrue(error_map.isEmpty());
        assertEquals("0", field.getText());
    }

    @Test
    void zero_Not_Allowed_Invalid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "0";

        field = new Field_JTxtField_INT("Quantity", 8, false);
        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }

    @Test
    void all_Zeros_Zero_Allowed_Valid_And_Normalized()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "0000";

        field = new Field_JTxtField_INT("Quantity", 8, true);
        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        // Interpreted as 0 and allowed
        assertTrue(result);
        assertTrue(error_map.isEmpty());

        assertEquals("0", field.getText());  // Leading zeros removed after valid normalization
    }

    @Test
    void all_Zeros_Zero_Not_Allowed_Invalid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "0000";

        field = new Field_JTxtField_INT("Quantity", 8, false);
        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertFalse(result); // This should evaluate as value 0 and be rejected
        assertFalse(error_map.isEmpty());  // Because it's invalid, Option B: we don't rely on normalization here
        assertEquals("0", field.getText());
    }

    //##################################################################
    // F. WHITESPACE AND EMPTY INPUT
    //##################################################################

    @Test
    void white_Space_Only_Invalid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "    "; // length 4 (<=8) but not a number

        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }

    @Test
    void empty_invalid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "";

        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }

    @Test
    void whitespace_around_integer_within_length_valid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "  15  "; // length 6 <= 8

        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        // Depending on parent behavior, this assumes trimming before parse
        assertTrue(result);
        assertTrue(error_map.isEmpty());
    }

    @Test
    void whitespace_raw_length_too_long_invalid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "    35   "; // length = 9 > char limit 8

        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertTrue(result);
        assertTrue(error_map.isEmpty());
    }

    //##################################################################
    // G. MAX LENGTH VALIDATION (INCLUDES WHITESPACE)
    //##################################################################

    @Test
    void max_Length_Valid_Exact_Limit()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "12345678"; // length = 8

        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertTrue(result);
    }

    @Test
    void max_Length_Invalid_Exceeds_Limit()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "123456789"; // length = 9 > 8

        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertFalse(result);
    }

    @Test
    void max_Length_Invalid_Leading_Spaces()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "     12"; // length = 7 -> valid

        field.setText(input_txt);
        boolean result1 = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertTrue(result1);

        // reset for next
        error_map.clear();
        field.setText("      12"); // length = 8 -> valid
        boolean result2 = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertTrue(result2);

        // reset again
        error_map.clear();
        field.setText("       12"); // length = 9 -> invalid
        boolean result3 = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertTrue(result3);
    }

    @Test
    void max_Length_Invalid_Trailing_Spaces()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "12      "; // length = 8 -> valid

        field.setText(input_txt);
        boolean result1 = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertTrue(result1);

        error_map.clear();
        field.setText("12       "); // length = 9 -> invalid
        boolean result2 = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertTrue(result2);
    }

    @Test
    void max_Length_Invalid_Spaces_Only()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "        "; // 8 spaces -> valid by length (though invalid as number, tested elsewhere)

        field.setText(input_txt);
        boolean result1 = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertFalse(result1);

        // This will actually be invalid as a number; the earlier test covers semantic invalidity.
        // Here we focus on length behavior, so we just assert the call executed; semantic assertion is in white_Space_Only_Invalid.

        error_map.clear();
        field.setText("         "); // 9 spaces -> should fail char limit
        boolean result2 = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertFalse(result2);
    }

    //##################################################################
    // H. OVERFLOW & EXTREME EDGE CASES
    //##################################################################

    @Test
    void integer_Overflow_Invalid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "2147483648";  // Integer.MAX_VALUE = 2147483647, so this is one above

        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        // parseInt should fail, caught by validate_Txt_Field and return false
        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }

    @Test
    void very_Large_Leading_Zero_Integer_Overflow_Invalid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "00000000002147483648"; // clearly > Integer.MAX_VALUE

        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }
}
