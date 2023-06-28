package Tests.Jframe;

import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.TimeUnit;
import javax.swing.*;
public class JFrameIconifiedTest extends JFrame implements ActionListener {
    private JButton iconifyButton, maximizeButton;
    public JFrameIconifiedTest() {
        setTitle("JFrameIconified Test");
        iconifyButton = new JButton("JFrame Iconified");
        add(iconifyButton, BorderLayout.NORTH);
        iconifyButton.addActionListener(this);
        maximizeButton = new JButton("JFrame Maximized");
        add(maximizeButton, BorderLayout.SOUTH);
        maximizeButton.addActionListener(this);
        setSize(400, 275);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    public void actionPerformed(ActionEvent ae) {
        if(ae.getSource().equals(iconifyButton)) {
            setState(JFrame.ICONIFIED); // To minimize a frame
            try
            {
                TimeUnit.SECONDS.sleep(10);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            setExtendedState(JFrame.MAXIMIZED_BOTH); // To maximize a frame

        } else if(ae.getSource().equals(maximizeButton)) {
            setExtendedState(JFrame.MAXIMIZED_BOTH); // To maximize a frame
        }
    }
    public static void main(String args[]) {
        new JFrameIconifiedTest();
    }
}