package com.donty.gymapp.ui.screens.MealPlanScreen.macroIndicator;

import com.donty.gymapp.gui.panels.Image_JPanel;
import com.donty.gymapp.ui.charts.progressWheel.ProgressWheel;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class MacroIndicators extends JPanel
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    ProgressWheel progress_wheel;


    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public MacroIndicators
    (
            int panel_height,
            ProgressWheelKey progress_wheel_key,
            String img_path,
            int width_img,
            int height_img
    )
    {
        //#######################################
        // Configure JPanel
        //#######################################
        // Configure Measurements
        int width_jpanel = width_img + progress_wheel_key.get_Width();
        setPreferredSize(new Dimension(width_jpanel, panel_height));

        // Configure Layout
        setLayout(new BorderLayout());

        // Other Settings
        setBorder(BorderFactory.createLineBorder(Color.blue));
        setOpaque(false);

        //#######################################
        // Add Icon
        //#######################################
        Image_JPanel macro_img = new Image_JPanel(img_path, width_img, height_img);
        add(BorderLayout.WEST, macro_img );


        //#######################################
        // Add Progress Wheel
        //#######################################
        progress_wheel = new ProgressWheel(
                progress_wheel_key.get_Percent(),
                progress_wheel_key.get_Used_Color(),
                progress_wheel_key.get_Remaining_Color(),
                progress_wheel_key.get_Width(),
                progress_wheel_key.get_Height(),
                false
        );

        add(BorderLayout.EAST, progress_wheel);
    }

    //##################################################################################################################
    // Methods
    //##################################################################################################################
    public void update_Macro_Wheel(int new_percent)
    {
        progress_wheel.update(new_percent);
    }

    public void update_Macro_Wheel(BigDecimal new_percent)
    {
        int value = new_percent
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();

        progress_wheel.update(value);
    }
}
