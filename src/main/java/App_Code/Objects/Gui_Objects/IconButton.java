package App_Code.Objects.Gui_Objects;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Locale;

public class IconButton extends JPanel
{
    private JButton button = new JButton();
    private Font font = null;
    
    private String iconPath, verticalTextPos, horizontalTextPos, btnText = null;
    private int iconWidth, iconHeight, btnWidth, btnHeight;
    
    public IconButton(String iconPath, int iconWidth, int iconHeight, int btnWidth, int btnHeight, String verticalTextPos,
                      String horizontalTextPos)
    {
        // ###############################
        // Setting Variables
        // ###############################
        this.iconPath = iconPath;
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
        this.btnWidth = btnWidth;
        this.btnHeight = btnHeight;
        this.verticalTextPos = verticalTextPos;
        this.horizontalTextPos = horizontalTextPos;
        
        setup(); // Build Object
    }
    
    
    public IconButton(String iconPath, Font font, String btnText, int iconWidth, int iconHeight, int btnWidth, int btnHeight,
                      String verticalTextPos, String horizontalTextPos)
    {
        // ###############################
        // Setting Variables
        // ###############################
        this.font = font;
        this.btnText = btnText;
        
        this.iconPath = iconPath;
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
        this.btnWidth = btnWidth;
        this.btnHeight = btnHeight;
        this.verticalTextPos = verticalTextPos;
        this.horizontalTextPos = horizontalTextPos;
        
        setup();   // Build Object
    }
    
    private void setup()
    {
        // ###############################
        // setBackground(Color.YELLOW);
        setPreferredSize(new Dimension(btnWidth + 10, btnHeight + 10));
        setIconIMG(iconPath, iconWidth, iconHeight);
        
        if (font != null && btnText != null)
        {
            button.setFont(font);
            button.setText(String.format("%s", btnText));
        }
        
        button.setPreferredSize(new Dimension(btnWidth, btnHeight));
        button.setFocusPainted(false); //remove icon border
        button.setHorizontalTextPosition(posToInt(horizontalTextPos, "horizontal"));
        button.setVerticalTextPosition(posToInt(verticalTextPos, "vertical"));
        
        add(button);
    }
    
    public JButton returnJButton()
    {
        return button;
    }
    
    public void makeBTntransparent()
    {
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
    }
    
    public void setButtonFont()
    {
    
    }
    
    public void setIconIMG(String iconPath, int iconWidth, int iconHeight)
    {
        URL imageUrl = getClass().getResource(iconPath);
        
        if (imageUrl == null)
        {
            System.err.println("Could not load icon: " + iconPath);
            return;
        }
        
        ImageIcon originalIcon = new ImageIcon(imageUrl);
        Image img = originalIcon.getImage();
        Image scaledImg = img.getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH);
        
        ImageIcon scaledIcon = new ImageIcon(scaledImg);
        button.setIcon(scaledIcon);
    }
    
    public Integer posToInt(String position, String axis)
    {
        if (axis.toLowerCase(Locale.ROOT).equals("horizontal")) // Horizontal
        {
            switch (position.toLowerCase())
            {
                case "left":
                    return 2;
                case "right":
                    return 4;
                case "centre":
                    return 0;
            }
        }
        else // Vertical
        {
            switch (position.toLowerCase())
            {
                case "centre":
                    return 0;
                case "top":
                    return 1;
                case "bottom":
                    return 3;
            }
        }
        return null;
        
    }
}
