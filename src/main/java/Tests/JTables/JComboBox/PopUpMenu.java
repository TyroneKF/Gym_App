package Tests.JTables.JComboBox;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JComboBox;

public class PopUpMenu
{
    public static void main(String[] argv) throws Exception {
        String[] items = { "A", "A", "B", "B", "C", "C" };
        JComboBox cb = new JComboBox(items);

        // Create and register the key listener
        cb.addKeyListener(new MyKeyListener());

    }
}

class MyKeyListener extends KeyAdapter {
    public void keyPressed(KeyEvent evt) {
        JComboBox cb = (JComboBox) evt.getSource();

        // Get pressed character
        char ch = evt.getKeyChar();

        // If not a printable character, return
        if (ch != KeyEvent.CHAR_UNDEFINED) {
            cb.showPopup();
        }
    }
}