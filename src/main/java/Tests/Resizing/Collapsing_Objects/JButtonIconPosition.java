package Tests.Resizing.Collapsing_Objects;
// http://www.java2s.com/Code/Java/Swing-JFC/MovingtheIconinaJButtonComponent.htm

import javax.swing.JButton;
import javax.swing.SwingConstants;

public class JButtonIconPosition {
    public static void main(String[] argv) throws Exception {

        JButton button = new JButton();
        // Place text over center of icon; they both occupy the same space
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setHorizontalTextPosition(SwingConstants.CENTER);

        // Place text above icon
        button.setVerticalTextPosition(SwingConstants.TOP);
        button.setHorizontalTextPosition(SwingConstants.CENTER);

        // Place text below icon
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);

        // Place text to the left of icon, vertically centered
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setHorizontalTextPosition(SwingConstants.LEFT);

        // Place text to the left of icon and align their tops
        button.setVerticalTextPosition(SwingConstants.TOP);
        button.setHorizontalTextPosition(SwingConstants.LEFT);

        // Place text to the left of icon and align their bottoms
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.LEFT);

        // Place text to the right of icon, vertically centered
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setHorizontalTextPosition(SwingConstants.RIGHT);

        // Place text to the right of icon and align their tops
        button.setVerticalTextPosition(SwingConstants.TOP);
        button.setHorizontalTextPosition(SwingConstants.RIGHT);

        // Place text to the right of icon and align their bottoms
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.RIGHT);

    }
}
