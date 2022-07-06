package App_Code.Objects.Screens.Tests;

import App_Code.Objects.Screens.Others.Meal_Plan_Screen;

import javax.swing.*;
import java.awt.*;


public class SplashScreenDemo
{
    Meal_Plan_Screen mealPlanScreen;
    int endCount, currentCount = 0;

    JFrame frame;
    JLabel text = new JLabel("Gym App Loading");

    JProgressBar progressBar = new JProgressBar();

    public SplashScreenDemo(int endCount, Meal_Plan_Screen mealPlanScreen)
    {
        this.endCount = endCount;
        this.mealPlanScreen = mealPlanScreen;

        createGUI();


        frame.revalidate();
    }

    public void createGUI()
    {
        frame = new JFrame();
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setSize(600, 600);
        frame.setLocationRelativeTo(null);

        Container container = frame.getContentPane();
        container.setLayout(new GridLayout(2,1));
        container.setBackground(Color.magenta);

        // #######################################################################

        container.add(new ImagePanel2());
        // frame.add(iconLabel);

        // #######################################################################

        progressBar.setBorderPainted(true);
        progressBar.setStringPainted(true);
        progressBar.setBackground(Color.WHITE);
        progressBar.setForeground(Color.BLACK);
        progressBar.setValue(0);
        container.add(progressBar);

        // #######################################################################

    }


    public void increaseBar()
    {
        try
        {
            currentCount++;
            int percentage = (int) ((currentCount * 100.0f) / endCount);

            Thread.sleep(50);//Pausing execution for 50 milliseconds

            progressBar.setValue(percentage);//Setting value of Progress Bar


            if (currentCount == endCount)
            {
                frame.dispose();
                mealPlanScreen.setFrameVisibility(true);
                mealPlanScreen.resizeGUI();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public  class ImagePanel2 extends JPanel
    {

        Image image;

        public ImagePanel2()
        {
            super.setBackground(Color.magenta);
            image = Toolkit.getDefaultToolkit().createImage("C:/Users/DonTy/Dropbox/0.) Coding/Gym_App/src/App_Code/Objects/Screens/Tests/Running.gif");
        }

        @Override
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            if (image != null)
            {
                g.drawImage(image, 100, 0, this);
            }
        }
    }

    public static void main(String[] args)
    {
        SplashScreenDemo d = new SplashScreenDemo(250, null);
        d.increaseBar();
        d.increaseBar();
        d.increaseBar();
        d.increaseBar();

    }
}