package com.donty.gymapp.gui.base;

import com.donty.gymapp.persistence.database.MyJDBC_Sqlite;
import javax.swing.*;
import java.awt.*;


public class Screen_JFrame extends JFrame
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    
    // Integers
    protected int
            containerYPos = 0,
            frameWidth;
    
    // String
    protected final String lineSeparator = "###############################################################################";
    private String class_Name;
    
    //##############################################
    // Objects
    //##############################################
    protected MyJDBC_Sqlite db;
    protected final GridBagConstraints gbc = new GridBagConstraints();
    protected Container contentPane;
    protected final ScrollPaneCreator scrollPane = new ScrollPaneCreator();
    
    //##############################################
    // JPanels
    //##############################################
    // Public: JPanels
    protected JPanel mainNorthPanel, mainSouthPanel, scrollPaneJPanel;
    
    //##################################################################################################################
    // Constructors
    //##################################################################################################################
   public Screen_JFrame(MyJDBC_Sqlite db, boolean addScrollPane, String title, int frameWidth, int frameHeight, int xPos, int yPos)
    {
        this.db = db;  // Variables
        
        // Setup
        setup(addScrollPane, title, frameWidth, frameHeight, xPos, yPos);
    }
    
    public Screen_JFrame(boolean addScrollPane, String title, int frameWidth, int frameHeight, int xPos, int yPos)
    {
        // Setup
        setup(addScrollPane, title, frameWidth, frameHeight, xPos, yPos);
    }
    
    //##################################################################################################################
    // GUI Setup
    //##################################################################################################################
    private void setup(boolean addScrollPane, String title, int frameWidth, int frameHeight, int xPos, int yPos)
    {
        //########################################################
        // Variables
        //########################################################
        this.class_Name = this.getClass().getSimpleName();
        
        this.frameWidth = frameWidth;
        
        //########################################################
        // Configurations
        //########################################################
        setVisible(false);
        set_Resizable(true);
        setSize(frameWidth, frameHeight);
        set_Title(title);
        setLocation(xPos, yPos);
        
        // Container (ContentPane)
        contentPane = super.getContentPane();
        contentPane.setLayout(new GridBagLayout());
        contentPane.setVisible(true);
        
        //########################################################
        // Create Interface With Sections
        //########################################################
        // Private: JPanels
        JPanel screenSectioned = new JPanel(new BorderLayout());
        addToContainer(contentPane, screenSectioned, 0, 0, "both", 0, null);
        
        // Top of GUI
        mainNorthPanel = new JPanel(new GridBagLayout());
        screenSectioned.add(mainNorthPanel, BorderLayout.NORTH);
        
        // Centre of GUI
        JPanel mainCenterPanel = new JPanel(new GridBagLayout());
        screenSectioned.add(mainCenterPanel, BorderLayout.CENTER);
        
        // South of GUI
        mainSouthPanel = new JPanel(new GridBagLayout());
        screenSectioned.add(mainSouthPanel, BorderLayout.SOUTH);
        
        //#########################
        // Create ScrollPane
        //#########################
        if (addScrollPane)
        {
            // Attach ScrollPane to the centre of the screen
            addToContainer(mainCenterPanel, scrollPane, 0, 0, "both", 0, null);
            scrollPaneJPanel = scrollPane.getJPanel();
            scrollPaneJPanel.setLayout(new GridBagLayout());
        }
        else
        {
            scrollPaneJPanel = mainCenterPanel;
        }
        
        //##########################################################
        // Closing Events on Screen_JFrame
        //##########################################################
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            @Override //HELLO Causes Error
            public void windowClosed(java.awt.event.WindowEvent windowEvent)
            {
                window_Closed_Event();
            }
        });
    }

    //##################################################################################################################
    // Actions
    //##################################################################################################################
    protected Boolean areYouSure(String title, String msg)
    {
        int reply = JOptionPane.showConfirmDialog(this, msg, title, JOptionPane.YES_NO_OPTION); //HELLO Edit

        return reply == JOptionPane.YES_NO_OPTION;
    }
    
    public void window_Closed_Event()
    {
    
    }
    
    public void closeJFrame()
    {
        dispose();
    }

    //##############################################
    // Screen_JFrame Positioning
    //##############################################
    public void scrollToJPanelOnScreen(JPanel panel) // Only Works With JPanels
    {
        // Scroll to that panel AFTER layout has finished
        SwingUtilities.invokeLater(() ->
            panel.scrollRectToVisible(new Rectangle(0, 0, panel.getWidth(), panel.getHeight()))
        );
    }
    
    protected void scroll_To_Top_of_ScrollPane()
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
    public void setFrameVisibility(boolean x)
    {
        setVisible(x);
    }
    
    public void set_Title(String title)
    {
        if (! title.isEmpty()) { setTitle(title); }
    }
    
    public void set_Resizable(boolean x)
    {
        setResizable(x);
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
    // ##################################
    // Get Int Methods
    // ##################################
    protected int getFrameWidth()
    {
        return frameWidth;
    }

    // ##################################
    // Get JPanels Methods
    // ##################################
    protected JPanel getMainNorthPanel()
    {
        return mainNorthPanel;
    }

    protected JPanel getScrollPaneJPanel()
    {
        return scrollPaneJPanel;
    }

    protected JPanel getMainSouthPanel() { return mainSouthPanel; }


    //##################################################################################################################
    // Sizing & Adding to GUI Methods
    //##################################################################################################################
    public void makeJFrameVisible()
    {
        setFrameVisibility(true);
        setExtendedState(JFrame.NORMAL); // makes frames visible
        
        setLocation(0, 0);
        setLocationRelativeTo(null);
    }
    
    protected int getAndIncreaseContainerYPos()
    {
        containerYPos++;
        return containerYPos;
    }
    
    public void resizeGUI()
    {
        scrollPaneJPanel.revalidate();
        scrollPaneJPanel.repaint();
        contentPane.revalidate();
    }
    
    protected void addToContainer(
            Container container,
            Component addToContainer,
            Integer gridx,
            Integer gridy,
            String fill,
            Integer ipady,
            String anchor
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
        gbc.weighty = 0.25;
        
        gbc.ipady = ipady;
        gbc.ipadx = 0;
        
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
