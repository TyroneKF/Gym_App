package Tests.Resizing.Collapsing_Objects;

import java.awt.*;
import javax.swing.*;

public class TestCollapsible extends JPanel
{
    private Collapsible collapsible = new Collapsible();

    public TestCollapsible()
    {
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        JLabel title = new JLabel("Test Collapsible");
        title.setFont(new Font(Font.DIALOG, Font.BOLD, 32));
        JPanel titlePanel = new JPanel();
        titlePanel.add(title);
        add(titlePanel);
        add(Box.createVerticalStrut(15));
        collapsible.setBorder(BorderFactory.createLineBorder(Color.blue));
        add(collapsible);
    }

    private static void createAndShowUI()
    {
        JFrame frame = new JFrame("TestCollapsible");
        frame.getContentPane().add(new TestCollapsible());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                createAndShowUI();
            }
        });
    }
}