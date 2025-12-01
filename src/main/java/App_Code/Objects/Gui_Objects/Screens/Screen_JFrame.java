package App_Code.Objects.Gui_Objects.Screens;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Gui_Objects.ScrollPaneCreator;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class Screen_JFrame extends JFrame
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    
    // Integers
    protected int containerYPos = 0, frameWidth, frameHeight, xPos, yPos;
    
    // String
    protected String lineSeparator = "###############################################################################";
    protected String title;
    private String class_Name;
    
    // Booleans
    private Boolean addScrollPane;
    
    //##############################################
    // Objects
    //##############################################
    protected MyJDBC db;
    protected GridBagConstraints gbc = new GridBagConstraints();
    protected Container contentPane;
    protected ScrollPaneCreator scrollPane = new ScrollPaneCreator();
    
    //##############################################
    // JPanels
    //##############################################
    // Public: JPanels
    protected JPanel mainNorthPanel, mainSouthPanel, scrollPaneJPanel;
    
    //##################################################################################################################
    // Constructors
    //##################################################################################################################
    public Screen_JFrame(MyJDBC db, boolean addScrollPane, String title, int frameWidth, int frameHeight, int xPos, int yPos)
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
        this.class_Name = this.getClass().getName();
        
        this.addScrollPane = addScrollPane;
        this.title = title;
        this.frameHeight = frameHeight;
        this.frameWidth = frameWidth;
        this.xPos = xPos;
        this.yPos = yPos;
        
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
        addToContainer(contentPane, screenSectioned, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0, null);
        
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
            addToContainer(mainCenterPanel, scrollPane, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0, null);
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
    
    protected void iconSetup(Container mainNorthPanel)
    {
    
    }
    
    //##################################################################################################################
    // Actions
    //##################################################################################################################
    protected Boolean areYouSure(String title, String msg)
    {
        int reply = JOptionPane.showConfirmDialog(this, msg, title, JOptionPane.YES_NO_OPTION); //HELLO Edit
        
        if (reply == JOptionPane.NO_OPTION || reply == JOptionPane.CLOSED_OPTION)
        {
            return false;
        }
        return true;
    }
    
    public void window_Closed_Event()
    {
    
    }
    
    public void closeJFrame()
    {
        dispose();
    }
    
    protected boolean is_Results_Empty(Collection<?> c)
    {
        return c == null || c.isEmpty();
    }
    
    //##############################################
    // Screen_JFrame Positioning
    //##############################################
    public void scrollToJPanelOnScreen(JPanel panel) // Only Works With JPanels
    {
        // Scroll to that panel AFTER layout has finished
        SwingUtilities.invokeLater(() -> {
            panel.scrollRectToVisible(
                    new Rectangle(0, 0, panel.getWidth(), panel.getHeight())
            );
        });
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
        if (title.length() > 0) { setTitle(title); }
    }
    
    public void set_Resizable(boolean x)
    {
        setResizable(x);
    }
    
    protected String formatStrings(String txt, boolean separateWords)
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
    protected String get_Class_Name()
    {
        return class_Name;
    }
    
    protected String get_Method_Name()
    {
        return Thread.currentThread().getStackTrace()[1].getMethodName();
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
    
    protected int getFrameHeight()
    {
        return frameHeight;
    }
    
    // ##################################
    // Other Objects
    // ##################################
    public MyJDBC getDb()
    {
        return db;
    }
    
    public GridBagConstraints getGbc()
    {
        return gbc;
    }
    
    public JFrame getFrame()
    {
        return this;
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
    
    protected JPanel createSpaceDivider(int width, int height)
    {
        JPanel spaceDivider = new JPanel(new GridBagLayout());
        spaceDivider.setBackground(Color.WHITE);
        spaceDivider.setPreferredSize(new Dimension(width, height));
        
        return spaceDivider;
    }
    
    //##################################################################################################################
    // Sizing & Adding to GUI Methods
    //##################################################################################################################
    public void makeJFrameVisible()
    {
        setFrameVisibility(true);
        setExtendedState(JFrame.NORMAL); // makes frames visible
        
        getFrame().setLocation(0, 0);
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
    
    protected void addToContainer(Container container, Component addToContainer,
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
