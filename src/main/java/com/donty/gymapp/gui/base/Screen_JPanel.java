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
    protected int containerYPos = 0, frameWidth, frameHeight;
    
    // String
    private String class_Name;
    protected String lineSeparator = "###############################################################################";
    protected String title;
    
    // Booleans
    protected Boolean addScrollPane;
    
    //##############################################
    // Objects
    //##############################################
    protected GridBagConstraints gbc = new GridBagConstraints();
    protected ScrollPaneCreator scrollPane = new ScrollPaneCreator();
    
    //##############################################
    // JPanels
    //##############################################
    // Public: JPanels
    protected JPanel mainNorthJPanel, mainSouthJPanel, scrollPaneJPanel;
    
    private Container container;
    
    //##################################################################################################################
    // Constructors
    //##################################################################################################################
    public Screen_JPanel(Container container, boolean addScrollPane, int frameWidth, int frameHeight)
    {
        //##############################################
        // Variables
        //##############################################
        this.frameHeight = frameHeight;
        this.frameWidth = frameWidth;
        
        //##############################################
        // Setup
        //##############################################
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
        //########################################################
        // Variables
        //########################################################
        this.class_Name = this.getClass().getSimpleName();
        
        this.container = container;
        this.addScrollPane = addScrollPane;
        
        //########################################################
        // Create Interface With Sections
        //########################################################
        setLayout(new GridLayout(1, 1));
        setVisible(true);
        
        // Private: JPanels
        JPanel screenSectioned = new JPanel(new BorderLayout());
        add_To_Container(this, screenSectioned, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0, null);
        
        // Top of GUI
        mainNorthJPanel = new JPanel(new GridBagLayout());
        screenSectioned.add(mainNorthJPanel, BorderLayout.NORTH);
        
        // Centre of GUI
        JPanel mainCenterPanel = new JPanel(new GridBagLayout());
        screenSectioned.add(mainCenterPanel, BorderLayout.CENTER);
        
        // South of GUI
        mainSouthJPanel = new JPanel(new GridBagLayout());
        screenSectioned.add(mainSouthJPanel, BorderLayout.SOUTH);
        
        //##########################################################
        // Create ScrollPane & Add it to Centre of GUI
        //##########################################################
        
        if (addScrollPane)
        {
            // Attach ScrollPane to the centre of the screen
            add_To_Container(mainCenterPanel, scrollPane, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0, null);
            scrollPaneJPanel = scrollPane.getJPanel();
            scrollPaneJPanel.setBackground(Color.WHITE);
            scrollPaneJPanel.setLayout(new GridBagLayout());
        }
        else
        {
            scrollPaneJPanel = mainCenterPanel;
        }
    }
    
    protected void icon_Setup(Container container)
    {
    
    }
    
    //##################################################################################################################
    // Actions
    //##################################################################################################################
    protected Boolean are_You_Sure(String title, String msg)
    {
        int reply = JOptionPane.showConfirmDialog(this, msg, title, JOptionPane.YES_NO_OPTION); //HELLO Edit
        
        if (reply == JOptionPane.NO_OPTION || reply == JOptionPane.CLOSED_OPTION)
        {
            return false;
        }
        return true;
    }
    
    //##############################################
    // Drawing Methods
    //##############################################
    /* public void set_Panel_IMG(JPanel jPanel, String iconPath, int width, int height)
    {
        //#############################
        //
        //#############################
        JPanel jp = new JPanel(new GridLayout(1,1));
        jp.setPreferredSize(new Dimension(width, height));

        //#############################
        //
        //#############################
        URL imageUrl = getClass().getResource(iconPath);

        if (imageUrl == null)
        {
            System.err.println("Could not load icon: " + iconPath);
            return;
        }

        ImageIcon originalIcon = new ImageIcon(imageUrl);
        Image img = originalIcon.getImage();
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        ImageIcon scaledIcon = new ImageIcon(scaledImg);

        //#############################
        //
        //#############################
        jp.add(scaledIcon);
    }*/
    
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
    
    protected JPanel create_Section_JP(Color color, int width, int height, int top_padding, int left_padding, int bottom_padding, int right_padding)
    {
        JPanel jp = new JPanel(new GridLayout(1, 1));
        jp.setBorder(new EmptyBorder(top_padding, left_padding, bottom_padding, right_padding));
        jp.setPreferredSize(new Dimension(width, height)); // width, height
        jp.setBackground(color);
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
        SwingUtilities.invokeLater(() -> {
            panel.scrollRectToVisible(
                    new Rectangle(0, 0, panel.getWidth(), panel.getHeight())
            );
        });
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
    protected String format_Strings(String txt, boolean separateWords)
    {
        // Re-assign Re-Capitalised Value into list
        return separateWords ?
                Arrays.stream(txt.split("[ _]+"))
                        .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                        .collect(Collectors.joining(" "))
                :
                Arrays.stream(txt.split("[ _]+"))
                        .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                        .collect(Collectors.joining("_"));
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
        return String.format("%s()", Thread.currentThread().getStackTrace()[2].getMethodName());
    }
    
    protected String get_Method_Name(int thread_Pos)
    {
        return String.format("%s()", Thread.currentThread().getStackTrace()[thread_Pos].getMethodName());
    }
    
    protected String get_Class_And_Method_Name()
    {
        return String.format("%s -> %s", get_Class_Name(), get_Method_Name(3));
    }
    
    // ##############################################
    // Get Int Methods
    // ##############################################
    protected int get_Frame_Width()
    {
        return frameWidth;
    }
    
    protected int get_Frame_Height()
    {
        return frameHeight;
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
    
    protected void add_To_Container(Container container, Component addToContainer,
                                    Integer gridx, Integer gridy, Integer gridwidth, Integer gridheight, Double weightx,
                                    Double weighty, String fill, Integer ipady, Integer ipadx, String anchor)
    {
        if (gridx != null)
        {
            gbc.gridx = gridx;
        }
        if (gridy != null)
        {
            gbc.gridy = gridy;
        }
        
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.weightx = weightx;
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
        
        if (anchor != null)
        {
            switch (anchor.toLowerCase())
            {
                case "start":
                    gbc.anchor = GridBagConstraints.PAGE_START;
                    break;
                
                case "end":
                    gbc.anchor = GridBagConstraints.PAGE_END;
                    break;
            }
        }
        container.add(addToContainer, gbc);
    }
}
