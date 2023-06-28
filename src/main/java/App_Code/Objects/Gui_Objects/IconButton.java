package App_Code.Objects.Gui_Objects;

import javax.swing.*;
import java.awt.*;

public class IconButton extends JPanel
{
    Icon icon;
    Image img;
    JButton button = new JButton();

    public IconButton(String iconPath, String btnText, int iconWidth, int iconHeight, int btnWidth, int btnHeight,
                      String verticalTextPos, String horizontalTextPos)
    {
        // setBackground(Color.YELLOW);
        setPreferredSize(new Dimension(btnWidth+10, btnHeight+10));
        setIconIMG(iconPath, iconWidth, iconHeight);

        button.setText(btnText);
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
        icon = new ImageIcon(String.format("%s", iconPath));
        img = ((ImageIcon) icon).getImage();

        Image newimg = img.getScaledInstance(iconWidth, iconHeight, java.awt.Image.SCALE_SMOOTH);
        icon = new ImageIcon(newimg);

        button.setIcon(icon);

    }

    public Integer posToInt(String position, String axis)
    {
        if (axis.equals("horizontal")) // Horizontal
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
