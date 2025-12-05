package Tests.Resizing;

/// https://stackoverflow.com/questions/19722680/how-to-tell-gridbaglayout-not-to-resize-my-components
//https://stackoverflow.com/questions/4226755/java-jscrollpane-doesnt-work-with-gridbaglayout

//https://stackoverflow.com/questions/6322627/java-toolkit-getting-second-screen-size
//https://stackoverflow.com/questions/2989107/change-font-size-of-a-jpanel-and-all-its-elements
// Multiple windows error withy getting frame size (research)

import App_Code.Objects.Gui_Objects.Screens.ScrollPaneCreator;

import javax.swing.*;
import java.awt.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JFrame;


public class increasingFontSize extends JFrame
{
    GridBagConstraints gbc = new GridBagConstraints();
    GridBagLayout gbl = new GridBagLayout();

    int origWidth = 500, origHeight = 500, currentWidth, currentHeight, originalFontSize = 12;

    JFrame jFrame = this;

    increasingFontSize()
    {
        currentWidth = origWidth;
        currentHeight = origHeight;

        //4. Size the frame.
        setVisible(true);
        setResizable(true);
        setSize(origWidth, origHeight);
        setLocation(100, 200);

        //####################################################################

        // Container (ContentPane)
        Container contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.setVisible(true);

        //####################################################################
        ScrollPaneCreator scrollpane = new ScrollPaneCreator();
        JPanel collasibleJPanel = scrollpane.getJPanel();
        collasibleJPanel.setBackground(Color.RED);

        collasibleJPanel.setLayout(gbl);
        contentPane.add(scrollpane);

        //####################################################################

        JButton x0 = new JButton(String.format("Button %s", 0));
        addToPanel(collasibleJPanel, x0, 0, 0, 1, 1, 0, 0, 0.25, 0.25, "Both");

        JButton x1 = new JButton(String.format("Button %s", 1));
        addToPanel(collasibleJPanel, x1, 1, 1, 1, 1, 0, 0, 0.25, 0.25, "Both");

        JButton x2 = new JButton(String.format("Button %s", 2));
        addToPanel(collasibleJPanel, x2, 2, 2, 1, 1, 0, 0, 0.25, 0.25, "Both");

        JButton x3 = new JButton(String.format("Button %s", 3));
        addToPanel(collasibleJPanel, x3, 3, 3, 1, 1, 0, 0, 0.25, 0.25, "Both");

        JButton x4 = new JButton(String.format("Button %s", 4));
        addToPanel(collasibleJPanel, x4, 4, 4, 1, 1, 0, 0, 0.25, 0.25, "Both");

        int size = 30;
        JPanel jp = new JPanel(new GridLayout(size, 1));
        for (int i = 0; i < size; i++)
        {
            jp.add(new JLabel(String.format("JLabel %s", i)));
        }
        addToPanel(collasibleJPanel, jp, 0, 5, 5, 1, 2, 30, 1.0, 1.0, "both");

        JButton x45 = new JButton(String.format("Button %s", 4));
        addToPanel(collasibleJPanel, x45, 0, 7, 5, 1, 0, 0, 0.25, 0.25, "Both");

        //####################################################################
        addComponentListener(new ComponentListener()
        {

            @Override
            public void componentHidden(ComponentEvent arg0)
            {
            }

            @Override
            public void componentMoved(ComponentEvent arg0)
            {
            }

            @Override
            public void componentResized(ComponentEvent arg0)
            {
                increaseFontSizeOfComponents(jFrame);
            }

            @Override
            public void componentShown(ComponentEvent e)
            {

            }
        });

        getContentPane().revalidate();

    }

    public void increaseFontSizeOfComponents(Container container)
    {
        Component[] components = container.getComponents();
        if (components.length > 0)
        {
            int newWidth = getWidth();
            int newHeight = getHeight();

            if (origWidth + origHeight < newHeight + newWidth)
            {

                System.out.printf("\n\n############################################################################");
                System.out.printf("\nOld: Width %s, Height %s \nNew: Width %s, Height %s", currentWidth, currentWidth, newWidth, newHeight);


                double widthScaleFactor = (double) newWidth / origWidth;
                double heightScaleFactor = (double) 0;
                double scaleFactor = (widthScaleFactor + heightScaleFactor);

                double newFontSize = (scaleFactor * originalFontSize);

                System.out.printf("\n\nwidthScaleFactor %s \nheightScaleFactor %s \nCombined Scalefactor %s",
                        widthScaleFactor, heightScaleFactor, scaleFactor);


                for (Component c : components)
                {
                    //System.out.printf("\nComponent Name %s ", c.getName());

                    Font font = c.getFont();
                    int fontSize = font.getSize();

                    System.out.printf("\n\nOld Font Size %s, New Font Size %s",
                            String.valueOf(fontSize), String.valueOf(newFontSize));

                    c.setFont(new Font(font.getFontName(), font.getStyle(), (int) newFontSize));

                    if (c instanceof Container)
                    {
                        increaseFontSizeOfComponents((Container) c);
                        continue;
                    }

                }
                getContentPane().revalidate();
            }
        }
    }


    public static void main(String[] args)
    {
        new increasingFontSize();
    }

    public void addToPanel(Container panel, Object objToAdd, Integer gridx, Integer gridy, Integer gridwidth, Integer gridheight, Integer ipadx, Integer ipady, Double weightx, Double weighty, String fill)
    {

        gbc.gridx = gridx;
        gbc.gridy = gridy;

        gbc.ipadx = ipadx;
        gbc.ipady = ipady;

        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;

        gbc.weightx = weightx;
        gbc.weighty = weighty;

        switch (fill.toLowerCase())
        {
            case "":
                gbc.fill = GridBagConstraints.VERTICAL;
                break;
            case "horizontal":
                gbc.fill = GridBagConstraints.HORIZONTAL;
                break;
            case "both":
                gbc.fill = GridBagConstraints.BOTH;
                break;
        }

        panel.add((Component) objToAdd, gbc);

    }

}
