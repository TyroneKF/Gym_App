package com.donty.gymapp.gui.panels;

import javax.swing.*;
import java.awt.*;

public class IconPanel extends JPanel
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private final GridBagConstraints gbc = new GridBagConstraints();
    private final JPanel iconPanel, iconAreaPanel;



    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public IconPanel(int numberOfIcons, int hgap, String position)
    {
        //Creating JPanels for the area
        iconAreaPanel = new JPanel(new BorderLayout());
        addToContainer(this, iconAreaPanel);

        // Creating JPanel where icons will be inserted into
        GridLayout layout = new GridLayout(1, numberOfIcons);
        layout.setHgap(hgap);


        iconPanel = new JPanel(layout);
        iconPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        switch (position.toLowerCase())
        {
            case "west":
                iconAreaPanel.add(iconPanel, BorderLayout.WEST);break;
            case "north":
                iconAreaPanel.add(iconPanel, BorderLayout.NORTH);break;
            case "south":
                iconAreaPanel.add(iconPanel, BorderLayout.SOUTH);break;
            case "center":
                iconAreaPanel.add(iconPanel, BorderLayout.CENTER);break;
            default:
                iconAreaPanel.add(iconPanel, BorderLayout.EAST);break;
        }
    }

    //##################################################################################################################
    // Methods
    //##################################################################################################################
    public JPanel getIconJpanel()
    {
        return iconPanel;
    }
    public JPanel getIconAreaPanel()
    {
        return iconAreaPanel;
    }

    protected void addToContainer
    (
            Container container,
            Component addToContainer
    )
    {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0.25;
        gbc.weighty = 0.25;

        switch ("horizontal".toLowerCase())
        {
            case "horizontal":
                gbc.fill = GridBagConstraints.HORIZONTAL;
                break;
            case "vertical":
                gbc.fill = GridBagConstraints.VERTICAL;
                break;

            case "both":
                gbc.fill = GridBagConstraints.BOTH;
                break;
        }

        switch ("east".toLowerCase())
        {
            case "east":
                gbc.anchor = GridBagConstraints.EAST;
                break;
            case "west":
                gbc.anchor = GridBagConstraints.WEST;
                break;
            case "north":
                gbc.anchor = GridBagConstraints.NORTH;
                break;
            case "south":
                gbc.anchor = GridBagConstraints.SOUTH;
                break;
        }

        container.add(addToContainer, gbc);

    }
}
