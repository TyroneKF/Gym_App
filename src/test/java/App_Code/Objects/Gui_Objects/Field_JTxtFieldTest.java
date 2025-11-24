package App_Code.Objects.Gui_Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class Field_JTxtFieldTest
{
    //##################################################################
    // Set-Up Methods & Variables
    //##################################################################
    // Arrange
    private Field_JTxtField field;
    private ArrayList<String> errors;
    private String input_TXT;
    private final String seperator = "##################################################################";
    
    @BeforeEach
    void setup()
    {
        field = new Field_JTxtField("Based On Quantity", 8, true);
        errors = new ArrayList<>();
    }
    
    void print_Results()
    {
        System.err.printf("\nInput Text = %s \nOutput Txt = %s", input_TXT, field.getText());
        for (String i : errors)
        {
            System.err.printf("\n%s", i);
        }
    }
    
    //##################################################################
    // A. VALID CASES WITH TRAILING DECIMAL
    //##################################################################
    
    @Test
    void decimal_Validation_trailingDot_valid()
    {
        input_TXT = "9.";
        field.setText(input_TXT);
        
        boolean result = field.decimal_Validation(input_TXT, errors);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertTrue(result);
        assertTrue(errors.isEmpty());
    }
    
    @Test
    void decimal_Validation_zeroTrailingDot_valid()
    {
        input_TXT = "0.";
        field.setText(input_TXT);
        
        boolean result = field.decimal_Validation(input_TXT, errors);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertTrue(result);
        assertTrue(errors.isEmpty());
    }
    
    @Test
    void decimal_Validation_integerTrailingDot_valid()
    {
        input_TXT = "12.";
        field.setText(input_TXT);
        
        boolean result = field.decimal_Validation(input_TXT, errors);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertTrue(result);
        assertTrue(errors.isEmpty());
    }
    
    //##################################################################
    // B. INVALID MULTIPLE DECIMAL POINTS
    //##################################################################
    
    @Test
    void decimal_Validation_multipleDots_invalid1()
    {
        input_TXT = "12..";
        field.setText(input_TXT);
        
        boolean result = field.decimal_Validation(input_TXT, errors);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
        assertFalse(errors.isEmpty());
    }
    
    @Test
    void decimal_Validation_multipleDots_invalid2()
    {
        input_TXT = "9.5.2";
        field.setText(input_TXT);
        
        boolean result = field.decimal_Validation(input_TXT, errors);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
        assertFalse(errors.isEmpty());
    }
    
    @Test
    void decimal_Validation_doubleLeadingDots_invalid()
    {
        input_TXT = "..5";
        field.setText(input_TXT);
        
        boolean result = field.decimal_Validation(input_TXT, errors);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
        assertFalse(errors.isEmpty());
    }
    
    @Test
    void decimal_Validation_dotMiddleAndEnd_invalid()
    {
        input_TXT = "5.3.";
        field.setText(input_TXT);
        
        boolean result = field.decimal_Validation(input_TXT, errors);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
        assertFalse(errors.isEmpty());
    }
    
    //##################################################################
    // C. SINGLE DOT ONLY (ALWAYS INVALID)
    //##################################################################
    
    @Test
    void decimal_Validation_singleDot_invalid()
    {
        input_TXT = ".";
        field.setText(input_TXT);
        
        boolean result = field.decimal_Validation(input_TXT, errors);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
        assertFalse(errors.isEmpty());
    }
    
    @Test
    void decimal_Validation_twoDotsOnly_invalid()
    {
        input_TXT = "..";
        field.setText(input_TXT);
        
        boolean result = field.decimal_Validation(input_TXT, errors);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
        assertFalse(errors.isEmpty());
    }
    
    //##################################################################
    // D. VALID DECIMALS WITH AUTO-PADDING
    //##################################################################
    
    @Test
    void decimal_Validation_singleDecimalDigit_valid()
    {
        input_TXT = "9.5";
        field.setText(input_TXT);
        
        boolean result = field.decimal_Validation(input_TXT, errors);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertTrue(result);
        assertTrue(errors.isEmpty());
    }
    
    @Test
    void decimal_Validation_exactScale_valid()
    {
        input_TXT = "9.00";
        field.setText(input_TXT);
        
        boolean result = field.decimal_Validation(input_TXT, errors);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertTrue(result);
        assertTrue(errors.isEmpty());
    }
    
    //##################################################################
    // E. TOO MANY DECIMAL DIGITS (VALID BUT PRODUCES WARNING)
    //##################################################################
    
    @Test
    void decimal_tooManyDecimals_invalid1()
    {
        input_TXT = "9.123";
        field.setText(input_TXT);
        
        boolean result = field.decimal_Validation(input_TXT, errors);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertTrue(result);
        assertFalse(errors.isEmpty());
    }
    
    @Test
    void decimal_tooManyDecimals_invalid2()
    {
        input_TXT = "0.123456";
        field.setText(input_TXT);
        
        boolean result = field.decimal_Validation(input_TXT, errors);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertTrue(result);
        assertFalse(errors.isEmpty());
    }
    
    //##################################################################
    // F. MISSING INTEGER PART
    //##################################################################
    
    @Test
    void decimal_Validation_missingInteger_valid()
    {
        input_TXT = ".5";
        field.setText(input_TXT);
        
        boolean result = field.decimal_Validation(input_TXT, errors);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertTrue(result);
        assertTrue(errors.isEmpty());
    }
    
    @Test
    void decimal_Validation_missingIntegerLong_valid()
    {
        input_TXT = ".123";
        field.setText(input_TXT);
        
        boolean result = field.decimal_Validation(input_TXT, errors);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertTrue(result);
        assertFalse(errors.isEmpty());
    }
    
    //##################################################################
    // G. ADDITIONAL EDGE CASES
    //##################################################################
    
    @Test
    void decimal_leadingZero_valid()
    {
        input_TXT = "0009.5";
        field.setText(input_TXT);
        
        boolean result = field.decimal_Validation(input_TXT, errors);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertTrue(result);
        assertTrue(errors.isEmpty());
    }
    
    @Test
    void decimal_largeInteger_invalid()
    {
        input_TXT = "1234567.89"; // too many digits before decimal
        field.setText(input_TXT);
        
        boolean result = field.decimal_Validation(input_TXT, errors);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
        assertFalse(errors.isEmpty());
    }
    
    @Test
    void decimal_negative_invalid()
    {
        input_TXT = "-5.00";
        field.setText(input_TXT);
        
        boolean result = field.decimal_Validation(input_TXT, errors);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result); // method returns true but logs error
        assertFalse(errors.isEmpty());
    }
    
    @Test
    void decimal_whitespace_invalid()
    {
        input_TXT = "   5.00   ";
        field.setText(input_TXT);
        
        boolean result = field.decimal_Validation(input_TXT.trim(), errors);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertTrue(result);
        assertTrue(errors.isEmpty());
    }
    
    @Test
    void decimal_empty_invalid()
    {
        input_TXT = "";
        field.setText(input_TXT);
        
        boolean result = field.decimal_Validation(input_TXT, errors);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
        assertFalse(errors.isEmpty());
    }
    
    @Test
    void decimal_nonNumeric_invalid()
    {
        input_TXT = "5A.2";
        field.setText(input_TXT);
        
        boolean result = field.decimal_Validation(input_TXT, errors);
        
        String methodName = String.format("\n\n%s \n%s()", seperator,
                new Object(){}.getClass().getEnclosingMethod().getName());
        
        System.err.printf("\n\n%s", methodName);
        print_Results();
        
        assertFalse(result);
        assertFalse(errors.isEmpty());
    }
    
}
