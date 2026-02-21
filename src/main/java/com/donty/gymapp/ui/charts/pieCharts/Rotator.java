package com.donty.gymapp.ui.charts.pieCharts;

import org.jfree.chart.plot.PiePlot;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Rotator extends Timer implements ActionListener
{
    //############################################################################################
    // Variables
    //############################################################################################
    private final PiePlot plot;
    private int angle = 270; // starting angle

    //############################################################################################
    // Constructor
    //############################################################################################
    protected Rotator(PiePlot plot, int delay)
    {
        super(delay, null);
        this.plot = plot;
        addActionListener(this);
    }

    //############################################################################################
    // Method
    //############################################################################################
    @Override
    public void actionPerformed(ActionEvent e)
    {
        angle = angle + 1;          // rotate 1 degree per tick
        if (angle == 360)
        {
            angle = 0;
        }
        plot.setStartAngle(angle);  // update the plot
    }
}