package com.donty.gymapp.ui.charts.progressWheel;

import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.*;

public class DonutPercentChart
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private final DefaultPieDataset<String> dataset;
    private final CenterTextRingPlot plot;
    private final JFreeChart chart;

    //##################################################################################################################
    //  Class
    //##################################################################################################################
    protected DonutPercentChart(double percent, Color used_color, Color remaining_color)
    {

        percent = clamp(percent);

        dataset = new DefaultPieDataset<>();

        dataset.setValue("Used", percent);
        dataset.setValue("Remaining", 100 - percent);

        plot = new CenterTextRingPlot(dataset);
        plot.setSectionDepth(0.35);
        plot.setSectionPaint("Used", used_color);
        plot.setSectionPaint("Remaining", remaining_color);
        plot.setLabelGenerator(null);
        plot.setOutlineVisible(false);
        plot.setShadowPaint(null);
        plot.setBackgroundPaint(null);

        plot.setCenterText(Math.round(percent) + "%");

        chart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        chart.setBackgroundPaint(null);
    }

    //##################################################################################################################
    // Methods
    //##################################################################################################################
    public JFreeChart getChart()
    {
        return chart;
    }

    /**
     * Update percentage (0–100)
     * */
    public void update(double percent)
    {
        percent = clamp(percent);

        dataset.setValue("Used", percent);
        dataset.setValue("Remaining", 100 - percent);

        plot.setCenterText(Math.round(percent) + "%");
    }

    private static double clamp(double v)
    {
        return Math.max(0, Math.min(100, v));
    }
}

