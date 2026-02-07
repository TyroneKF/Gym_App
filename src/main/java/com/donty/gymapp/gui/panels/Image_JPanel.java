package com.donty.gymapp.gui.panels;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class Image_JPanel extends JPanel
{
    private Image image;
    
    public Image_JPanel(String imagePath, int width, int height)
    {
        
        URL imageUrl = getClass().getResource(imagePath);
        
        if (imageUrl == null)
        {
            System.err.println("Could not load icon: " + imagePath);
            return;
        }
        
        setPreferredSize(new Dimension(width, height));
        
        image = new ImageIcon(imageUrl).getImage();
    }
    
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        // Draw image scaled to panel size
        g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
    }
}