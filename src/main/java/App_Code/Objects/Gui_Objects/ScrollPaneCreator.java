package App_Code.Objects.Gui_Objects;

import javax.swing.*;
import java.awt.*;

public class ScrollPaneCreator extends JScrollPane
{
    GridBagConstraints gbc = new GridBagConstraints();
    private static final long serialVersionUID = -8276573088940558872L;

    private static JPanel containerPanel = new JPanel();

    public ScrollPaneCreator()
    {

        super(containerPanel = new JPanel(new GridBagLayout()), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        super.setPreferredSize(super.getPreferredSize());
    }

    /*
     * Method returns the main container panel of the JscrollPane
     *
     * @return containerPanel (panel inside TabbedPane)
     */
    public JPanel getJPanel()
    {
        return containerPanel;
    }

    /*
     * Method updates container panel
     */
    public void updateJPanel()
    {
        containerPanel.revalidate();
        super.setPreferredSize(super.getPreferredSize()); // resize

    }
}
