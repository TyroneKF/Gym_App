package Tests.Resizing;

// https://www.daniweb.com/programming/software-development/threads/185557/gridbaglayout-problem-resizing

import java.awt.*;
import javax.swing.*;
import java.awt.GridBagLayout;

public class GridBagLayoutTest extends JFrame
{
    GridBagConstraints gbc = new GridBagConstraints ();;

    public static void main (String args[]) {
        GridBagLayoutTest gbe = new GridBagLayoutTest();
    }

    public GridBagLayoutTest() {
        setSize (400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container container = getContentPane();

        JPanel  redPanel = new JPanel ();
        JPanel bluePanel = new JPanel ();
        JPanel greenPanel = new JPanel ();
        JPanel yellowPanel = new JPanel ();
        JPanel pinkPanel = new JPanel ();
        JPanel orangePanel = new JPanel ();
        JPanel grayPanel = new JPanel ();
        JButton shrinkBTN = new JButton("Shrink");


        redPanel.setBackground(Color.RED);
        bluePanel.setBackground(Color.BLUE);
        greenPanel.setBackground(Color.GREEN);
        yellowPanel.setBackground(Color.YELLOW);
        pinkPanel.setBackground(Color.PINK);
        orangePanel.setBackground(Color.ORANGE);
        grayPanel.setBackground(Color.GRAY);

        container.setLayout(new GridBagLayout());

        AddPanel (redPanel, 0, 0, 2, 3, 0.25, 0.25);
        AddPanel (bluePanel, 2, 0, 1, 2, 0.25, 0.25);
        AddPanel (greenPanel, 2, 2, 1, 1, 0.25, 0.25);
        AddPanel (yellowPanel, 3, 0, 1, 1, 0.25, 0.25);
        AddPanel (pinkPanel, 3, 1, 1, 2, 0.25, 0.25);
        AddPanel (orangePanel, 0, 3, 1, 1, 0.25, 0.25);
        AddPanel (grayPanel, 1, 3, 3, 1, 0.25, 0.25);
        AddPanel (shrinkBTN, 0, 5, 5, 1, 0.25, 0.25);




        shrinkBTN.addActionListener(ae -> {

        });

        setVisible (true);
    }

    public void AddPanel (Component panel, int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty)
    {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        gbc.fill = GridBagConstraints.BOTH;
        add(panel, gbc);
    }
}