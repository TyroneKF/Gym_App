package App_Code.Objects.Gui_Objects;

import App_Code.Objects.Gui_Objects.Text_Fields.Field_JTxtField_Parent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

class Field_JTxtField_Parent_Test_String_Test
{
    private Field_JTxtField_Parent field;
    private LinkedHashMap<String, ArrayList<String>> error_Map;
    private String input_TXT , label = "Based On Quantity";
    private final String seperator = "##################################################################";
    
    @BeforeEach
    void setup()
    {
        error_Map = new LinkedHashMap<>();
        field = new Field_JTxtField_Parent("Based On Quantity", 255);
    }
    
    void print_Results()
    {
        System.err.printf("\nInput Text = %s \nOutput Txt = %s", input_TXT, field.getText());
        
        if (error_Map.isEmpty()) { return; }
        
        ArrayList<String> errors = error_Map.get(label);
        
        for (String i : errors)
        {
            System.err.printf("\n%s", i);
        }
    }
    
    //##################################################################
    // A. VALID INPUT CASES (Allowed characters only)
    //##################################################################
    
    @Test
    void validation_lettersOnly_valid()
    {
        input_TXT = "John";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator, new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertTrue(result);
        assertTrue(error_Map.isEmpty());
    }
    
    @Test
    void validation_lettersAndSpaces_valid()
    {
        input_TXT = "John Smith";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertTrue(result);
        assertTrue(error_Map.isEmpty());
    }
    
    @Test
    void validation_containsPeriod_valid()
    {
        input_TXT = "Mr. Smith";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertTrue(result);
        assertTrue(error_Map.isEmpty());
    }
    
    @Test
    void validation_containsApostrophe_valid()
    {
        input_TXT = "O'Connor";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertTrue(result);
        assertTrue(error_Map.isEmpty());
    }
    
    @Test
    void validation_containsHyphen_valid()
    {
        input_TXT = "Anne-Marie";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertTrue(result);
        assertTrue(error_Map.isEmpty());
    }
    
    @Test
    void validation_mixedAllowedCharacters_valid()
    {
        input_TXT = "Dr. Anne-Marie O'Neil";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertTrue(result);
        assertTrue(error_Map.isEmpty());
    }
    
    
    //##################################################################
    // B. INVALID INPUT CASES (Digits, symbols, emoji, etc.)
    //##################################################################
    
    @Test
    void validation_containsDigit_invalid()
    {
        input_TXT = "John3";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
        assertFalse(error_Map.isEmpty());
    }
    
    @Test
    void validation_containsMultipleDigits_invalid()
    {
        input_TXT = "12345";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
        assertFalse(error_Map.isEmpty());
    }
    
    @Test
    void validation_containsSpecialSymbol_invalid()
    {
        input_TXT = "John@Smith";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
        assertFalse(error_Map.isEmpty());
    }
    
    @Test
    void validation_containsHash_invalid()
    {
        input_TXT = "#hashtag";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
        assertFalse(error_Map.isEmpty());
    }
    
    @Test
    void validation_containsPlus_invalid()
    {
        input_TXT = "A+B";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
        assertFalse(error_Map.isEmpty());
    }
    
    @Test
    void validation_containsSlash_invalid()
    {
        input_TXT = "John/Smith";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
        assertFalse(error_Map.isEmpty());
    }
    
    @Test
    void validation_containsEmoji_invalid()
    {
        input_TXT = "John🙂";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
        assertFalse(error_Map.isEmpty());
    }
    
    @Test
    void validation_mixedLettersAndNumbers_invalid()
    {
        input_TXT = "A1B2C3";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
        assertFalse(error_Map.isEmpty());
    }
    
    @Test
    void validation_invalidSymbolAtStart_invalid()
    {
        input_TXT = "!John";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
        assertFalse(error_Map.isEmpty());
    }
    
    @Test
    void validation_invalidSymbolAtEnd_invalid()
    {
        input_TXT = "John!";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
        assertFalse(error_Map.isEmpty());
    }
    
    @Test
    void validation_mixedValidAndInvalid_invalid()
    {
        input_TXT = "Anne-Marie 123";
        field.setText(input_TXT);
        
        boolean result = field.validation_Check(error_Map);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
        assertFalse(error_Map.isEmpty());
    }
    
    
}