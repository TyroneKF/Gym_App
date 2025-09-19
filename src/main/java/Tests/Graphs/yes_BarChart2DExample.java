package Tests.Graphs;

import org.apache.commons.lang3.RandomStringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;

public class yes_BarChart2DExample
{
    public static void main(String[] args)
    {
        // Dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < 8; i++)
        {
            String generatedString = RandomStringUtils.random(6, true, false);
            dataset.addValue((int) (Math.random() * 1001), "Plant 1", generatedString);
            dataset.addValue((int) (Math.random() * 1001), "Plant 2", generatedString);
            dataset.addValue((int) (Math.random() * 1001), "Plant 3", generatedString);
        }

        // Create chart
        JFreeChart chart = ChartFactory.createBarChart(
                "Production by Month",   // Title
                "Month",                 // X-axis label
                "Units",                 // Y-axis label
                dataset
        );


        CategoryPlot plot = chart.getCategoryPlot();
        plot.setForegroundAlpha(0.6f);
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(true);
        renderer.setBarPainter(new BarRenderer().getBarPainter()); // flat style
        renderer.setShadowVisible(true); // add shadows
        renderer.setMaximumBarWidth(0.1); // thinner bars

        // Colors
        renderer.setSeriesPaint(0, new Color(241, 196, 15)); // Random color 1 (Yellow)
        renderer.setSeriesPaint(1, new Color(46, 204, 113)); // Random color 2 (Green)
        renderer.setSeriesPaint(2, new Color(155, 89, 182)); // Random color 3 (Purple)


        // Show chart in JFrame
        JFrame frame = new JFrame("2D Grouped Bar Chart Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
