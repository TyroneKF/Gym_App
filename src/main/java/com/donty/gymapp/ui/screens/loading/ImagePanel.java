package com.donty.gymapp.ui.screens.loading;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ImagePanel extends JPanel
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################

    private Image imageGif;

    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public ImagePanel()
    {
        // Load GIF from resources using classpath
        URL gifUrl = getClass().getResource("/images/intro/Eating.gif");
        if (gifUrl != null)
        {
            imageGif = Toolkit.getDefaultToolkit().createImage(gifUrl);
        }
        else
        {
            System.err.println("GIF not found at images/0.) Intro Screen");
        }
    }

    //##################################################################################################################
    // Methods
    //##################################################################################################################
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if (imageGif != null)
        {
            g.drawImage(imageGif, 0, 0, this);
        }
    }
}
