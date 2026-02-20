package com.donty.gymapp.gui.textFieldTests;

import com.donty.gymapp.gui.controls.textfields.Field_JTxtField_String;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

class FieldJTxtFieldStringTest extends Test_Field_JxtField_Parent<String>
{
    //##################################################################################################################
    // Setup
    //##################################################################################################################
    @BeforeEach
    void setup()
    {
        field = new Field_JTxtField_String("Based On Quantity", 255);
    }


    //##################################################################################################################
    // Tests
    //##################################################################################################################
    /*


    */

    //##################################################################
    // A. VALID INPUT CASES (Allowed characters only)
    //##################################################################
    
    @Test
    void validation_Letters_Only_valid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "John";

        field.setText(input_txt);
        
        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);
        
        assertTrue(result);
        assertTrue(error_map.isEmpty());
    }
    
    @Test
    void validation_Letters_And_Spaces_Valid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "John Smith";

        field.setText(input_txt);
        
        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);
        
        assertTrue(result);
        assertTrue(error_map.isEmpty());
    }
    
    @Test
    void validation_Contains_Period_Valid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "Mr. Smith";

        field.setText(input_txt);
        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);;
        
        assertTrue(result);
        assertTrue(error_map.isEmpty());
    }
    
    @Test
    void validation_Contains_Apostrophe_Valid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "O'Connor";

        field.setText(input_txt);
        
        boolean result = field.validation_Check(error_map);
        
        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);;
        
        assertTrue(result);
        assertTrue(error_map.isEmpty());
    }
    
    @Test
    void validation_Contains_Hyphen_Valid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "Anne-Marie";

        field.setText(input_txt);
        
        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);;
        
        assertTrue(result);
        assertTrue(error_map.isEmpty());
    }
    
    @Test
    void validation_Mixed_Allowed_Characters_Valid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "Dr. Anne-Marie O'Neil";

        field.setText(input_txt);
        
        boolean result = field.validation_Check(error_map);
        
        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);;
        
        assertTrue(result);
        assertTrue(error_map.isEmpty());
    }

    @Test
    void validation_Contains_Digit_Valid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "John3";

        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);;

        assertTrue(result);
    }

    @Test
    void validation_Contains_Multiple_Digits_Valid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "12345";

        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);;

        assertTrue(result);
        assertTrue(error_map.isEmpty());
    }

    @Test
    void validation_Contains_Plus_Valid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "A+B";

        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);;

        assertTrue(result);
        assertTrue(error_map.isEmpty());
    }

    @Test
    void validation_Contains_Slash_Valid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "John/Smith";

        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);;

        assertTrue(result);
        assertTrue(error_map.isEmpty());
    }

    @Test
    void validation_Mixed_Letters_And_Numbers_Valid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "A1B2C3";

        field.setText(input_txt);

        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);;

        assertTrue(result);
        assertTrue(error_map.isEmpty());
    }

    //##################################################################
    // B. INVALID INPUT CASES (Digits, symbols, emoji, etc.)
    //##################################################################
        @Test
    void validation_Contains_Special_Symbol_Invalid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "John@Smith";

        field.setText(input_txt);
        
        boolean result = field.validation_Check(error_map);
        
        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);;
        
        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }
    
    @Test
    void validation_Contains_Hash_Invalid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "#hashtag";

        field.setText(input_txt);
        
        boolean result = field.validation_Check(error_map);

        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);;
        
        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }
    
    @Test
    void validation_Contains_Emoji_Invalid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "John🙂";

        field.setText(input_txt);
        
        boolean result = field.validation_Check(error_map);
        
        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);;
        
        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }
    
    @Test
    void validation_Invalid_Symbol_At_Start_Invalid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "!John";

        field.setText(input_txt);
        
        boolean result = field.validation_Check(error_map);
        
        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);;
        
        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }
    
    @Test
    void validation_Invalid_Symbol_At_End_Invalid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "John!";

        field.setText(input_txt);
        
        boolean result = field.validation_Check(error_map);
        
        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);;
        
        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }
    
    @Test
    void validation_Mixed_Valid_And_Invalid_Invalid()
    {
        LinkedHashMap<String, ArrayList<String>> error_map = new LinkedHashMap<>();
        String input_txt = "Anne!Marie 123";

        field.setText(input_txt);
        
        boolean result = field.validation_Check(error_map);
        
        System.err.printf("\n\n%s()", get_Method_Name());
        print_Results(input_txt, error_map);;
        
        assertFalse(result);
        assertFalse(error_map.isEmpty());
    }
}