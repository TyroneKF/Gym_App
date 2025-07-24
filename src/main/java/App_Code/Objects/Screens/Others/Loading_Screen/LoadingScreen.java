package App_Code.Objects.Screens.Others.Loading_Screen;

import App_Code.Objects.Screens.Others.Meal_Plan_Screen;

import javax.swing.*;
import java.awt.*;


public class LoadingScreen
{
    private GridBagConstraints gbc = new GridBagConstraints();
    Meal_Plan_Screen mealPlanScreen;
    int endCount, currentCount = 0, posY=0;

    JFrame frame;
    JLabel text = new JLabel("Gym App Loading");

    JProgressBar progressBar = new JProgressBar();

    public LoadingScreen(int endCount, Meal_Plan_Screen mealPlanScreen)
    {
        this.endCount = endCount;
        this.mealPlanScreen = mealPlanScreen;
        // #######################################################################
        if(endCount == 0)
        {
            mealPlanScreen.setFrameVisibility(true);
            return;
        }

        // #######################################################################

        frame = new JFrame();
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setSize(600, 600);
        frame.setLocationRelativeTo(null);

        // #######################################################################

        Container container = frame.getContentPane();
        container.setLayout(new BorderLayout());
        container.setBackground(Color.magenta);

        // #######################################################################

        JPanel mainJP = new JPanel(new GridBagLayout());
        container.add(mainJP, BorderLayout.CENTER);

        // #######################################################################

        JPanel jp = new ImagePanel2();
        jp.setPreferredSize(new Dimension(600, 450));
        addToContainer(mainJP, jp,0, posY += 1, 1, 1, 0.25, 0.25, "both", 0, 0);

        // frame.add(iconLabel);

        // #######################################################################

        progressBar.setPreferredSize(new Dimension(600, 35));
        progressBar.setBorderPainted(true);
        progressBar.setStringPainted(true);
        progressBar.setBackground(Color.WHITE);
        progressBar.setForeground(Color.BLACK);
        progressBar.setValue(0);

        container.add(progressBar, BorderLayout.SOUTH);

//        addToContainer(container, progressBar,0, posY += 1, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

        // #######################################################################

        frame.revalidate();
    }

    public void increaseBar(int increaseBy)
    {
        try
        {
            currentCount += increaseBy;
            int percentage = (int) ((currentCount * 100.0f) / endCount);

            Thread.sleep(50);//Pausing execution for 50 milliseconds

            progressBar.setValue(percentage);//Setting value of Progress Bar


            if (currentCount == endCount)
            {
                closeWindow();
                mealPlanScreen.setFrameVisibility(true);
                mealPlanScreen.resizeGUI();
                mealPlanScreen.scrollBarDown_BTN_Action();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void closeWindow()
    {
        frame.dispose();
    }

    public class ImagePanel2 extends JPanel
    {

        Image imageGif;

        public ImagePanel2()
        {
            //super.setBackground(Color.RED);
           imageGif = Toolkit.getDefaultToolkit().createImage("src/main/java/images/0.) Intro Screen/Eating.gif");
           //imageGif = Toolkit.getDefaultToolkit().createImage("src/main/java/images/0.) Intro Screen/Running.gif");
        }

        @Override
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            if (imageGif!= null)
            {
                g.drawImage(imageGif, 0, 0, this);
            }
        }
    }

    public void addToContainer(Container container, Component addToContainer, int gridx, int gridy, int gridwidth,
                               int gridheight, double weightx, double weighty, String fill, int ipady, int ipadx)
    {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
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

        container.add(addToContainer, gbc);
    }

    public static void main(String[] args)
    {
        LoadingScreen d = new LoadingScreen(250, null);
        d.increaseBar(1);
        d.increaseBar(1);
        d.increaseBar(1);
        d.increaseBar(1);
    }
}