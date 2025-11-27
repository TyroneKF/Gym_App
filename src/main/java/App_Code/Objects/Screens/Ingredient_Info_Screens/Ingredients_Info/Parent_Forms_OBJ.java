package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info;

import App_Code.Objects.Gui_Objects.CollapsibleJPanel;
import App_Code.Objects.Gui_Objects.Screens.Screen_JPanel;
import javax.swing.*;
import java.awt.*;

// Shop Form and Ingredients Form
public class Parent_Forms_OBJ extends Screen_JPanel
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    protected CollapsibleJPanel collapsibleJPanel;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Parent_Forms_OBJ(Container parentContainer, String btnText)
    {
        //##################################################
        // Super & Variables
        //##################################################
        super(parentContainer, false);
        
     
        //##################################################
        // Collapsible JPanel Creation
        //##################################################
        collapsibleJPanel = new CollapsibleJPanel(parentContainer, btnText, 250, 50);
        collapsibleJPanel.expand_JPanel();
        
        get_ScrollPane_JPanel().setLayout(new GridLayout(1, 1));
        get_ScrollPane_JPanel().add(collapsibleJPanel);
    }
    

}
