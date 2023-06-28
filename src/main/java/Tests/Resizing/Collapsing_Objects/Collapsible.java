package Tests.Resizing.Collapsing_Objects;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Collapsible extends JPanel
{
    private static final String COLLAPSE = "Collapse";
    private static final String EXPAND = "Expand";

    private static final Dimension PREFERRED_DIMENSION = new Dimension(200, 200);
    private JTextArea textarea = new JTextArea(60, 80);
    private JScrollPane scrollpane = new JScrollPane(textarea);
    private JButton collapseBtn = new JButton(COLLAPSE);

    public Collapsible()
    {
        setLayout(new BorderLayout());
        scrollpane.setPreferredSize(PREFERRED_DIMENSION);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 400; i++)
        {
            sb.append("what the heck?  ");
        }
        textarea.setText(sb.toString());
        textarea.setWrapStyleWord(true);
        textarea.setLineWrap(true);
        add(scrollpane, BorderLayout.CENTER);
        collapseBtn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String actionCommand = e.getActionCommand();
                if (actionCommand.equals(COLLAPSE))
                {
                    scrollpane.setVisible(false);
                    scrollpane.setPreferredSize(new Dimension(getWidth(), 0));
                    ((JFrame) Collapsible.this.getTopLevelAncestor()).pack();
                    collapseBtn.setText("Expand");
                }
                else if (actionCommand.equals(EXPAND))
                {
                    scrollpane.setVisible(true);
                    scrollpane.setPreferredSize(PREFERRED_DIMENSION);
                    ((JFrame) Collapsible.this.getTopLevelAncestor()).pack();
                    collapseBtn.setText("Collapse");
                }
            }
        });
        add(collapseBtn, BorderLayout.NORTH);
    }
}