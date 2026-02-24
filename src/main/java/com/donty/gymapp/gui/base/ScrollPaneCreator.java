package com.donty.gymapp.gui.base;

import javax.swing.*;
import java.awt.*;

public class ScrollPaneCreator extends JScrollPane
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private static JPanel containerPanel = new JPanel();


    //##################################################################################################################
    // Constructors
    //##################################################################################################################
    public ScrollPaneCreator()
    {
        super(containerPanel = new JPanel(new GridBagLayout()), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        super.setPreferredSize(super.getPreferredSize());
    }

    //##################################################################################################################
    // Methods
    //##################################################################################################################
    /*
     * Method returns the main container panel of the JscrollPane
     * @return containerPanel (panel inside TabbedPane)
     */
    public JPanel getJPanel()
    {
        return containerPanel;
    }
}
