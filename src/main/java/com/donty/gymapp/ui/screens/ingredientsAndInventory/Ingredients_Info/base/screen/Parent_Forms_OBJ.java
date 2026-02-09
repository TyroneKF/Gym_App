package com.donty.gymapp.ui.screens.ingredientsAndInventory.Ingredients_Info.base.screen;

import com.donty.gymapp.gui.panels.CollapsibleJPanel;
import com.donty.gymapp.gui.base.Screen_JPanel;

import java.awt.*;

public class Parent_Forms_OBJ extends Screen_JPanel // Shop Form and Ingredients Form
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
