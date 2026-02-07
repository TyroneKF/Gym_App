package com.donty.gymapp.ui.charts.progressWheel;

import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.chart.plot.RingPlot;
import org.jfree.data.general.PieDataset;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class CenterTextRingPlot extends RingPlot
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private String centerText = "";


    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    protected CenterTextRingPlot(PieDataset dataset)
    {
        super(dataset);
    }


    //##################################################################################################################
    // Methods
    //##################################################################################################################
    public void setCenterText(String text)
    {
        this.centerText = text == null ? "" : text;
    }

    @Override
    public void draw(
            Graphics2D g2,
            Rectangle2D plotArea,
            Point2D anchor,
            PlotState parentState,
            PlotRenderingInfo info
    )
    {
        // Draw the normal ring first
        super.draw(g2, plotArea, anchor, parentState, info);

        if (centerText.isEmpty())
        {
            return;
        }

        // Draw centered text
        Font font = new Font("Arial", Font.PLAIN, 45);
        g2.setFont(font);
        g2.setPaint(Color.DARK_GRAY);

        FontMetrics fm = g2.getFontMetrics(font);

        double textWidth = fm.stringWidth(centerText);
        double textHeight = fm.getAscent();

        double x = plotArea.getCenterX() - textWidth / 2.0;
        double y = plotArea.getCenterY() + textHeight / 3.0;

        g2.drawString(centerText, (float) x, (float) y);
    }
}
