package com.donty.gymapp.gui.base;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Screen_JPanel extends JPanel
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################

    // Integers
    protected int containerYPos = 0, frameWidth;

    // String
    private String class_Name;

    // Objects
    protected final GridBagConstraints gbc = new GridBagConstraints();
    protected final ScrollPaneCreator scrollPane = new ScrollPaneCreator();

    // JPanels
    protected JPanel
            mainNorthJPanel,
            mainSouthJPanel,
            scrollPaneJPanel;

    private Container container;

    //##################################################################################################################
    // Constructors
    //##################################################################################################################
    public Screen_JPanel(Container container, boolean addScrollPane, int frameWidth)
    {
        // Variables
        this.frameWidth = frameWidth;

        // Setup
        setup(container, addScrollPane); // Main GUI
    }

    public Screen_JPanel(Container container, boolean addScrollPane)
    {
        // Setup
        setup(container, addScrollPane);
    }

    //##################################################################################################################
    // GUI Setup
    //##################################################################################################################
    protected void setup(Container container, boolean addScrollPane)
    {
        // Variables
        this.class_Name = this.getClass().getSimpleName();
        this.container = container;


        // Create Interface With Sections
        setLayout(new GridLayout(1, 1));
        setVisible(true);

        // Private: JPanels
        JPanel screenSectioned = new JPanel(new BorderLayout());
        add_To_Container(this, screenSectioned, 0, 0, 0.25, "both", 0, 0);

        // Top of GUI
        mainNorthJPanel = new JPanel(new GridBagLayout());
        screenSectioned.add(mainNorthJPanel, BorderLayout.NORTH);

        // Centre of GUI
        JPanel mainCenterPanel = new JPanel(new GridBagLayout());
        screenSectioned.add(mainCenterPanel, BorderLayout.CENTER);

        // South of GUI
        mainSouthJPanel = new JPanel(new GridBagLayout());
        screenSectioned.add(mainSouthJPanel, BorderLayout.SOUTH);

        // Create ScrollPane & Add it to Centre of GUI
        if (addScrollPane)
        {
            // Attach ScrollPane to the centre of the screen
            add_To_Container(mainCenterPanel, scrollPane, 0, 0, 0.25, "both", 0, 0);
            scrollPaneJPanel = scrollPane.getJPanel();
            scrollPaneJPanel.setBackground(Color.WHITE);
            scrollPaneJPanel.setLayout(new GridBagLayout());
        }
        else
        {
            scrollPaneJPanel = mainCenterPanel;
        }
    }

    //##################################################################################################################
    // Actions
    //##################################################################################################################
    protected boolean are_You_Not_Sure(String title, String msg)
    {
        int reply = JOptionPane.showConfirmDialog(this, msg, title, JOptionPane.YES_NO_OPTION); //HELLO Edit

        return reply != JOptionPane.YES_NO_OPTION;
    }

    protected JPanel create_Label_JP(String title, Font font, Color color)
    {
        // Title JP
        JPanel title_JP = new JPanel();
        title_JP.setBackground(color);

        // Creating Label
        JLabel label = new JLabel(title);
        label.setFont(font);
        label.setHorizontalAlignment(JLabel.CENTER);

        title_JP.add(label);  // Add Label to JP

        return title_JP;  // Return JP
    }

    protected JPanel create_Section_JP(int width, int height, int left_padding, int right_padding)
    {
        JPanel jp = new JPanel(new GridLayout(1, 1));
        jp.setBorder(new EmptyBorder(0, left_padding, 0, right_padding));
        jp.setPreferredSize(new Dimension(width, height)); // width, height
        jp.setBackground(Color.LIGHT_GRAY);
        return jp;
    }

    protected JPanel create_Space_Divider(int width, int height, Color color)
    {
        JPanel spaceDivider = new JPanel(new GridBagLayout());
        spaceDivider.setBackground(color);
        spaceDivider.setPreferredSize(new Dimension(width, height));

        return spaceDivider;
    }

    //##############################################
    // Screen_JFrame Positioning
    //##############################################
    protected void scroll_To_JPanel_On_Screen(JPanel panel) // Only Works With JPanels
    {
        // Scroll to that panel AFTER layout has finished
        SwingUtilities.invokeLater(() ->
                panel.scrollRectToVisible(new Rectangle(0, 0, panel.getWidth(), panel.getHeight()))
        );
    }

    protected void scroll_To_Top_Of_ScrollPane()
    {
        //##############################################
        // Set ScrollPane to the Bottom Straight Away
        //##############################################
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMinimum());
    }

    protected void scroll_To_Bottom_of_ScrollPane()
    {
        //##############################################
        // Set ScrollPane to the Bottom Straight Away
        //##############################################
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    //##################################################################################################################
    // Mutator Methods
    //##################################################################################################################
    protected String format_Strings(String txt)
    {
        // Re-assign Re-Capitalised Value into list

        return Arrays.stream(txt.split("[ _]+"))
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "));
    }

    //##################################################################################################################
    // Accessor Methods
    //##################################################################################################################

    // String Methods
    protected String get_Class_Name()
    {
        return class_Name;
    }

    protected String get_Method_Name()
    {
        return String.format("%s()", Thread.currentThread().getStackTrace()[3].getMethodName());
    }

    protected String get_Class_And_Method_Name()
    {
        return String.format("%s -> %s", get_Class_Name(), get_Method_Name());
    }

    // ##############################################
    // Objects
    // ##############################################
    public Container get_Container()
    {
        return (container == null) ? this : container;
    }

    // ##################################
    // Get JPanels Methods
    // ##################################
    protected JPanel get_Main_North_JPanel()
    {
        return mainNorthJPanel;
    }

    protected JPanel get_ScrollPane_JPanel()
    {
        return scrollPaneJPanel;
    }

    protected JPanel get_Main_South_JPanel() { return mainSouthJPanel; }

    //##################################################################################################################
    // Sizing & Adding to GUI Methods
    //##################################################################################################################
    protected int get_And_Increase_YPos()
    {
        containerYPos++;
        return containerYPos;
    }

    public void reset_YPos()
    {
        containerYPos = 0;
    }

    public void resize_GUI()
    {
        scrollPaneJPanel.revalidate();
        scrollPaneJPanel.repaint();

        revalidate();
        if (container != null) { container.revalidate(); }
    }

    protected void add_To_Container
            (
                    Container container,
                    Component addToContainer,
                    Integer gridx,
                    Integer gridy,
                    Double weighty,
                    String fill,
                    Integer ipady,
                    Integer ipadx
            )
    {
        if (gridx != null)
        {
            gbc.gridx = gridx;
        }
        if (gridy != null)
        {
            gbc.gridy = gridy;
        }

        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0.25;
        gbc.weighty = weighty;

        gbc.ipady = ipady;
        gbc.ipadx = ipadx;

        switch (fill.toLowerCase())
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

        container.add(addToContainer, gbc);
    }
}
