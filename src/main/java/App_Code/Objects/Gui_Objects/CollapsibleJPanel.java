package App_Code.Objects.Gui_Objects;

import javax.swing.*;
import java.awt.*;

public class CollapsibleJPanel extends JPanel
{
    boolean isCollapsed = false;
    String btnText;
    JPanel collapsibleJPanel, southPanel, eastJPanel;
    JButton iconBtn;
    IconButton collapse_And_Expand_Btn;
    Container parentContainer;

    public CollapsibleJPanel(Container parentContainer, String btnText, int btnWidth, int btnHeight)
    {
        this.btnText = btnText;
        this.parentContainer = parentContainer;

        //####################################################
        // Defining JPanel Features
        //####################################################

        //setBackground(Color.RED);
        setLayout(new BorderLayout());

        JPanel topJPanel = new JPanel(new BorderLayout());
        //topJPanel.setBackground(Color.YELLOW);
        add(topJPanel, BorderLayout.NORTH);

        //####################################################
        // Icon Button
        //####################################################
        collapse_And_Expand_Btn = new IconButton("/images/expand2.png", btnText, 50, 50, btnWidth, btnHeight,
                "centre", "right"); // btn text is useless here , refactor

        topJPanel.add(collapse_And_Expand_Btn, BorderLayout.WEST);

        iconBtn = collapse_And_Expand_Btn.returnJButton();
        iconBtn.addActionListener(ae -> {

            if (isCollapsed) // expand window
            {
                expandJPanel();
            }
            else // close window
            {
                collapseJPanel();
            }
        });

        //####################################################
        eastJPanel = new JPanel();
        topJPanel.add(eastJPanel, BorderLayout.EAST);

        //####################################################
        // ScrollPanel
        //####################################################

        collapsibleJPanel = new JPanel(new GridBagLayout());

        add(collapsibleJPanel, BorderLayout.CENTER);

        southPanel = new JPanel(new GridBagLayout());
        add(southPanel, BorderLayout.SOUTH);

        setBorder(BorderFactory.createLineBorder(Color.blue, 3));

        //####################################################
        // Collapsing JPanel

        collapseJPanel(); //HELLO Remove
    }

    public JPanel getEastJPanel()
    {
        return eastJPanel;
    }

    public JPanel getCentreJPanel()
    {
        return collapsibleJPanel;
    }

    public JPanel getSouthJPanel()
    {
        return southPanel;
    }

    public void setIconBtnText(String txt)
    {
        if (txt!=null || txt.length() > 0)
        {
            btnText = txt;
            iconBtn.setText(btnText);
        }
    }

    public String getBtnText()
    {
        return btnText;
    }


    public void expandJPanel()
    {
        isCollapsed = false;

        collapsibleJPanel.revalidate();
        collapsibleJPanel.setVisible(true);

        //iconBtn.setText(String.format("Collapse %s", btnText));
        collapse_And_Expand_Btn.setIconIMG("/images/+collapse2.png", 40, 40);
    }

    public void collapseJPanel()
    {
        isCollapsed = true;

        collapsibleJPanel.setSize(0, 0);
        collapsibleJPanel.setVisible(false);

        // iconBtn.setText(String.format("Expand %s", btnText));
        collapse_And_Expand_Btn.setIconIMG("/images/expand2.png", 40, 40);
    }

    public void removeCollapsibleJPanel()
    {   parentContainer.setSize(new Dimension(0,0));
        parentContainer.revalidate();
        parentContainer.remove(this);
    }

    public Container getParentContainer()
    {
        return parentContainer;
    }
}
