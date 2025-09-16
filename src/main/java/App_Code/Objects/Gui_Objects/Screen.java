package App_Code.Objects.Gui_Objects;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;

import javax.swing.*;
import java.awt.*;

public class Screen
{
    protected MyJDBC db;
    protected static GridBagConstraints gbc = new GridBagConstraints();
    protected static JFrame frame = new JFrame();
    protected static Container contentPane;
    protected static ScrollPaneCreator scrollPane = new ScrollPaneCreator();
    protected static JPanel
            scrollPaneJPanel,
            scrollJPanelCenter,
            screenSectioned,
            mainNorthPanel,
            mainCenterPanel,
            scrollJPanelBottom;

    protected int containerYPos = 0, frameWidth, frameHeight;

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
        frame.setLocation(00, 0);

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
    }

    //##################################################################################################################
    // Actions
    //##################################################################################################################

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

    protected void iconSetup(Container mainNorthPanel)
    {
    }

    // Only Works With JPanels
    protected void scrollToJPanelOnScreen(JPanel panel)
    {
        // Scroll to that panel AFTER layout has finished
        SwingUtilities.invokeLater(() -> {
            panel.scrollRectToVisible(
                    new Rectangle(0, 0, panel.getWidth(), panel.getHeight())
            );
        });
    }

    //##################################################################################################################
    // Mutator Methods
    //##################################################################################################################
    public void setFrameVisibility(boolean x)
    {
        frame.setVisible(x);
    }

    //##################################################################################################################
    // Accessor Methods
    //##################################################################################################################
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

    protected JPanel getMainNorthPanel()
    {
        return mainNorthPanel;
    }

    protected JPanel getScrollPaneJPanel()
    {
        return scrollPaneJPanel;
    }

    //##################################################################################################################
    // Sizing & Adding to GUI Methods
    //##################################################################################################################
     protected int getAndIncreaseContainerYPos()
    {
        containerYPos++;
        return containerYPos;
    }

    public void resizeGUI()
    {
        scrollJPanelCenter.revalidate();
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

    //##################################################################################################################
    // Sizing & Adding to GUI Methods
    //##################################################################################################################
    public static void main(String[] args)
    {
        //new Screen(400, 600, 200, 200);
    }

}
