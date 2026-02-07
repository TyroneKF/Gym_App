package com.donty.gymapp.ui.charts.progressWheel;

import com.donty.gymapp.gui.panels.Image_JPanel;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import java.awt.*;

public class ProgressWheel extends JPanel
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private final DonutPercentChart donut;



    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public ProgressWheel(
            int start_percent,
            Color used_color,
            Color remaining_color,
            int width_indicator,
            int height_indicator,
            boolean clamp
    )
    {
        //#######################################
        // Configure JPanel
        //#######################################
        setLayout(new GridLayout(1, 1));
        setPreferredSize(new Dimension(width_indicator, height_indicator ));
        //setBorder(BorderFactory.createLineBorder(Color.red));


        //#######################################
        // Create chart
        //#######################################
        donut = new DonutPercentChart(
                start_percent,
                used_color,
                remaining_color,
                clamp
        );

        ChartPanel chartPanel = new ChartPanel(donut.getChart());
        chartPanel.setPreferredSize(new Dimension(width_indicator, height_indicator));
        chartPanel.setOpaque(false);
        chartPanel.setBackground(null);

        //#######################################
        // Add to Swing panel
        //#######################################
        add(chartPanel);
    }

    //##################################################################################################################
    // Methods
    //##################################################################################################################
    public void update(int new_percent)
    {
        donut.update(new_percent);
    }
}
