package App_Code.Objects.Gui_Objects;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;

import javax.swing.*;
import java.awt.*;

public class Screen
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################

    // Objects
    protected MyJDBC db;
    protected GridBagConstraints gbc = new GridBagConstraints();
    protected JFrame frame = new JFrame();
    protected Container contentPane;
    protected ScrollPaneCreator scrollPane = new ScrollPaneCreator();

    //##############################################
    // JPanels
    //##############################################
    // Public: JPanels
    protected JPanel mainNorthPanel, scrollPaneJPanel;

    // Private: JPanels
    private JPanel screenSectioned, mainCenterPanel;

    //##############################################
    // Others
    //##############################################
    // Integers
    protected int containerYPos = 0, frameWidth, frameHeight;

    // String
    protected String lineSeparator = "###############################################################################";

    //##################################################################################################################
    // Constructors
    //##################################################################################################################
    public Screen(MyJDBC db, String title, int frameWidth, int frameHeight, int xPos, int yPos)
    {
        //########################################################
        //
        //########################################################
        this.db = db;
        this.frameHeight = frameHeight;
        this.frameWidth = frameWidth;

        //########################################################
        //
        //########################################################
        frame.setVisible(false);
        frame.setResizable(true);
        frame.setSize(frameWidth, frameHeight);
        frame.setTitle(title);
        frame.setLocation(xPos, yPos);

        // Container (ContentPane)
        contentPane = frame.getContentPane();
        contentPane.setLayout(new GridBagLayout());
        contentPane.setVisible(true);

        //########################################################
        // Create Interface With Sections
        //########################################################
        screenSectioned = new JPanel(new BorderLayout());
        addToContainer(contentPane, screenSectioned, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0, null);

        // Top of GUI
        mainNorthPanel = new JPanel(new GridBagLayout());
        screenSectioned.add(mainNorthPanel, BorderLayout.NORTH);

        // Centre of GUI
        mainCenterPanel = new JPanel(new GridBagLayout());
        screenSectioned.add(mainCenterPanel, BorderLayout.CENTER);

        //##########################################################
        // Create ScrollPane & Add it to Centre of GUI
        //##########################################################
        // Attach ScrollPane to the centre of the screen
        addToContainer(mainCenterPanel, scrollPane, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0, null);
        scrollPaneJPanel = scrollPane.getJPanel();
        scrollPaneJPanel.setLayout(new GridBagLayout());

        //##########################################################
        // Closing Events on Screen
        //##########################################################
        frame.addWindowListener(new java.awt.event.WindowAdapter()
        {
            @Override //HELLO Causes Error
            public void windowClosing(java.awt.event.WindowEvent windowEvent)
            {
                windowClosedEvent();
            }
        });
    }

    //##################################################################################################################
    // Actions
    //##################################################################################################################
    protected Boolean areYouSure(String process)
    {
        int reply = JOptionPane.showConfirmDialog(frame, String.format("Are you sure you want to %s, \nany unsaved changes will be lost in this Table! \nDo you want to %s?", process, process),
                "Close Application", JOptionPane.YES_NO_OPTION); //HELLO Edit

        if (reply==JOptionPane.NO_OPTION || reply==JOptionPane.CLOSED_OPTION)
        {
            return false;
        }
        return true;
    }

    protected void windowClosedEvent()
    {

    }


    //##############################################
    // GUI Setup
    //##############################################
    protected void iconSetup(Container mainNorthPanel)
    {
    }

    //##############################################
    // Screen Positioning
    //##############################################
    protected void scrollToJPanelOnScreen(JPanel panel) // Only Works With JPanels
    {
        // Scroll to that panel AFTER layout has finished
        SwingUtilities.invokeLater(() -> {
            panel.scrollRectToVisible(
                    new Rectangle(0, 0, panel.getWidth(), panel.getHeight())
            );
        });
    }

    public void scrollBarUp_BTN_Action()
    {
        //##############################################
        // Set ScrollPane to the Bottom Straight Away
        //##############################################
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMinimum());
    }

    public void scrollBarDown_BTN_Action()
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
        frame.setVisible(x);
    }

    public void setTitle(String title)
    {
        if(title.length() > 0) frame.setTitle(title);
    }

    public void setResizable(boolean x)
    {
        frame.setResizable(x);
    }

    //##################################################################################################################
    // Accessor Methods
    //##################################################################################################################

    // Other Objects
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
        return frame;
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

    //##################################################################################################################
    // Sizing & Adding to GUI Methods
    //##################################################################################################################
    public void makeJFrameVisible()
    {
        getFrame().setExtendedState(JFrame.NORMAL); // makes frames visible
        getFrame().setLocation(0, 0);
    }

    protected int getAndIncreaseContainerYPos()
    {
        containerYPos++;
        return containerYPos;
    }

    public void resizeGUI()
    {
        scrollPaneJPanel.revalidate();
        contentPane.revalidate();
    }

    protected void addToContainer(Container container, Component addToContainer,
                                  Integer gridx, Integer gridy, Integer gridwidth, Integer gridheight, Double weightx,
                                  Double weighty, String fill, Integer ipady, Integer ipadx, String anchor)
    {
        if (gridx!=null)
        {
            gbc.gridx = gridx;
        }
        if (gridy!=null)
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

        if (anchor!=null)
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
