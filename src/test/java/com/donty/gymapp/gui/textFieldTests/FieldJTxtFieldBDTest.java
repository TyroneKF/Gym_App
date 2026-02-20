package com.donty.gymapp.gui.textFieldTests;

import com.donty.gymapp.gui.controls.textfields.Field_JTxtField_BD;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

class FieldJTxtFieldBDTest extends Test_Field_JxtField_Parent<BigDecimal>
{
    //##################################################################################################################
    // Setup
    //##################################################################################################################
    @BeforeEach
    void setup()
    {
        field = new Field_JTxtField_BD("Based On Quantity", 8, true);
    }


    //##################################################################################################################
    // Tests
    //##################################################################################################################
    /*


    */



    //##################################################################
    // A. VALID CASES WITH TRAILING DECIMAL
    //##################################################################

    @Test
    void decimal_Validation_Trailing_Dot_Valid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "9.";
        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertTrue(result);
        assertTrue(error_map.isEmpty());
    }

    @Test
    void decimal_Validation_Zero_Trailing_Dot_Valid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "0.";
        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertTrue(result);
        assertTrue(error_map.isEmpty());
    }

    @Test
    void decimal_Validation_Integer_Trailing_Dot_Valid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "12.";
        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertTrue(result);
        assertTrue(error_map.isEmpty());
    }

    //##################################################################
    // B. INVALID MULTIPLE DECIMAL POINTS
    //##################################################################

    @Test
    void decimal_Validation_Multiple_Dots_Invalid1()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "12..";
        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }

    @Test
    void decimal_Validation_Multiple_Dots_Invalid2()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "9.5.2";
        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }

    @Test
    void decimal_Validation_Double_Leading_Dots_Invalid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "..5";
        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }

    @Test
    void decimal_Validation_Dot_Middle_And_End_Invalid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "5.3.";
        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }

    //##################################################################
    // C. SINGLE DOT ONLY
    //##################################################################

    @Test
    void decimal_Validation_Single_Dot_Invalid()
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

    @Test
    void decimal_Validation_Two_Dots_Only_Invalid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "..";
        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }

    //##################################################################
    // D. VALID DECIMALS
    //##################################################################

    @Test
    void decimal_Validation_Single_Decimal_Digit_Valid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "9.5";
        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertTrue(result);
        assertTrue(error_map.isEmpty());
    }

    @Test
    void decimal_Validation_Exact_Scale_Valid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "9.00";
        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertTrue(result);
        assertTrue(error_map.isEmpty());
    }

    //##################################################################
    // E. TOO MANY DECIMAL DIGITS (VALID BUT PRODUCES WARNING)
    //##################################################################

    @Test
    void decimal_Too_Many_Decimals_Invalid1()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "9.123";

        field.setText(input_txt);
        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertTrue(result);
        assertFalse(error_map.isEmpty());
    }

    @Test
    void decimal_Too_Many_Decimals_Invalid2()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "0.123456";

        field.setText(input_txt);
        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertTrue(result);
        assertFalse(error_map.isEmpty());
    }

    //##################################################################
    // F. MISSING INTEGER PART
    //##################################################################

    @Test
    void decimal_Validation_Missing_Integer_Valid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = ".5";

        field.setText(input_txt);
        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertTrue(result);
        assertTrue(error_map.isEmpty());
    }

    @Test
    void decimal_Validation_Missing_Integer_Long_Valid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = ".123";

        field.setText(input_txt);
        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertTrue(result);
        assertFalse(error_map.isEmpty());
    }

    //##################################################################
    // G. ADDITIONAL EDGE CASES
    //##################################################################

    @Test
    void decimal_Leading_Zero_Valid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "0009.5";

        field.setText(input_txt);
        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertTrue(result);
        assertTrue(error_map.isEmpty());
    }

    @Test
    void decimal_Large_Integer_Invalid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "12345678";

        field.setText(input_txt);
        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }

    @Test
    void decimal_Negative_Invalid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "-5.00";

        field.setText(input_txt);
        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }

    @Test
    void decimal_White_Space_Invalid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "   5.00";

        field.setText(input_txt);
        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertTrue(result);
        assertTrue(error_map.isEmpty());
    }

    @Test
    void decimal_Empty_Invalid()
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
    void decimal_Non_Numeric_Invalid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "5A.2";

        field.setText(input_txt);
        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }

    //##################################################################
    // H. ZERO-VALUE VALIDATION (ZERO NOT ALLOWED)
    //##################################################################

    @Test
    void decimal_Value_Zero_Invalid_Plain_Zero()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "0";

        field = new Field_JTxtField_BD("Based On Quantity", 8, false);
        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }

    @Test
    void decimal_Value_Zero_Invalid_Trailing_Dot()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "0.";

        field = new Field_JTxtField_BD("Based On Quantity", 8, false);
        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }

    @Test
    void decimal_Value_Zero_Invalid_Single_Decimal()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "0.0";

        field = new Field_JTxtField_BD("Based On Quantity", 8, false);
        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }

    @Test
    void decimal_Value_Zero_Invalid_Exact_Scale()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "0.00";

        field = new Field_JTxtField_BD("Based On Quantity", 8, false);
        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }

    @Test
    void decimal_Value_Zero_Invalid_Multiple_Leading_Zeros()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "0000";

        field = new Field_JTxtField_BD("Based On Quantity", 8, false);
        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }

    @Test
    void decimal_Value_Zero_Invalid_Leading_Zeros_Decimal()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "0000.000";

        field = new Field_JTxtField_BD("Based On Quantity", 8, false);
        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }

    @Test
    void decimal_Value_Zero_Invalid_Fraction_Only_Zero()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = ".0";

        field = new Field_JTxtField_BD("Based On Quantity", 8, false);
        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }

    @Test
    void decimal_Value_Zero_Invalid_Fraction_Only_Zeros()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = ".000";

        field = new Field_JTxtField_BD("Based On Quantity", 8, false);
        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);

        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }
}
