package App_Code.Objects.Screens.Ingredient_Info_Screens;

import App_Code.Objects.Gui_Objects.JTextFieldLimit;

import javax.swing.*;

public class Field_JTxtField extends JTextField
{
    public Field_JTxtField(int char_Limit)
    {
        setDocument(new JTextFieldLimit(char_Limit));
    }
    
    public void reset_Txt_Field() { setText(""); }
}
