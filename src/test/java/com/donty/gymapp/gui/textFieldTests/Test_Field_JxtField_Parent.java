package com.donty.gymapp.gui.textFieldTests;

import com.donty.gymapp.gui.controls.textfields.base.Field_JTxtField_Parent;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public abstract class Test_Field_JxtField_Parent<T>
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    protected Field_JTxtField_Parent<T> field;
    protected final String seperator = "##################################################################";


    //##################################################################################################################
    // Setup Methods
    //##################################################################################################################
    @BeforeEach
    abstract void setup();


    //##################################################################################################################
    // Methods
    //##################################################################################################################
    protected void print_Results(String input_txt, LinkedHashMap<String, ArrayList<String>> error_map)
    {
        System.err.printf("\nInput Text = %s \nOutput Txt = %s", input_txt, field.getText());

        if (! error_map.isEmpty())
        {
            ArrayList<String> errors = error_map.get(field.get_Label());

            for (String i : errors)
            {
                System.err.printf("\n%s", i);
            }
        }

        System.err.printf("\n\n%s", seperator);
    }

    protected String get_Method_Name()
    {
        return StackWalker.getInstance()
                .walk(s -> s.skip(1)
                        .findFirst()
                        .map(StackWalker.StackFrame :: getMethodName)
                        .orElse("Unknown"));
    }
}
