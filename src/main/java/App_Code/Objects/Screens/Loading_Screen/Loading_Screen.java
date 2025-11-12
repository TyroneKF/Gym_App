package App_Code.Objects.Screens.Loading_Screen;

import App_Code.Objects.Gui_Objects.Screens.Screen_JFrame;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class Loading_Screen extends Screen_JFrame
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private int
            endCount,
            currentCount = 0;

    private boolean finished = false;
    
    // Objects
    private JProgressBar progressBar = new JProgressBar();

    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Loading_Screen(int endCount)
    {
        //##############################################
        // Super Constructor & Variables
        //##############################################
        super(false, "Gym App Loading", 600, 600, 0, 0);

        this.endCount = endCount;

        if (endCount == 0) { window_Closed_Event(); return; }

        //##############################################
        //  Centre GUI : Image Setup
        //##############################################
        // Create Picture Panel
        JPanel jp = new ImagePanel2();
        jp.setPreferredSize(new Dimension(600, 450));
        addToContainer(getScrollPaneJPanel(), jp, 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);

        //##############################################
        // South GUI : Progress Bar Setup
        //##############################################

        progressBar.setPreferredSize(new Dimension(600, 35));
        progressBar.setBorderPainted(true);
        progressBar.setStringPainted(true);
        progressBar.setBackground(Color.WHITE);
        progressBar.setForeground(Color.BLACK);
        progressBar.setValue(0);

        getMainSouthPanel().setLayout(new GridLayout(1, 1));
        getMainSouthPanel().add(progressBar);

        //##############################################
        // Super Constructor & Variables
        //##############################################
        makeJFrameVisible();
        set_Resizable(false);
    }

    //##################################################################################################################
    // Methods
    //##################################################################################################################
    public void increaseBar(int increaseBy)
    {
        try
        {
            currentCount += increaseBy;

            int percentage = (int) ((currentCount * 100.0f) / endCount);

            Thread.sleep(50); //Pausing execution for 50 milliseconds

            progressBar.setValue(percentage); //Setting value of Progress Bar

            if (currentCount == endCount) { finished = true; window_Closed_Event(); }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void window_Closed_Event()
    {
      dispose();
    }
    
    public boolean isFinished()
    {
        return finished;
    }
    
    //##################################################################################################################
    // ImagePanel Class
    //##################################################################################################################
    public class ImagePanel2 extends JPanel
    {
        private Image imageGif;

        // #########################################
        // Load Image to JPanel
        // #########################################
        public ImagePanel2()
        {
            // Load GIF from resources using classpath
            URL gifUrl = getClass().getResource("/images/0.) Intro Screen/Eating.gif");
            if (gifUrl != null)
            {
                imageGif = Toolkit.getDefaultToolkit().createImage(gifUrl);
            }
            else
            {
                System.err.println("GIF not found at images/0.) Intro Screen");
            }
        }

        // #########################################
        // Draw image on JPanel
        // #########################################
        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            if (imageGif != null)
            {
                g.drawImage(imageGif, 0, 0, this);
            }
        }
    }
}