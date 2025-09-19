package Tests.Graphs;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.geom.Ellipse2D;
import java.util.Random;

public class yes_TimeSeriesWithBullets extends JFrame
{

    public yes_TimeSeriesWithBullets()
    {
        Random random = new Random();

        // --- Create 3 series ---
        TimeSeries series1 = new TimeSeries("Sensor A");
        TimeSeries series2 = new TimeSeries("Sensor B");
        TimeSeries series3 = new TimeSeries("Sensor C");

        // Start from "now"
        Minute current = new Minute();

        // Add 10 minutes of random data
        for (int i = 0; i < 10; i++)
        {
            series1.add(current, 50 + random.nextInt(50)); // 50–99
            series2.add(current, 20 + random.nextInt(80)); // 20–99
            series3.add(current, random.nextInt(100));     // 0–99
            current = (Minute) current.next();
        }

        // Dataset with all 3
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        dataset.addSeries(series3);

        // Create chart
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "3 Line Time Series with Bullets",
                "Time",
                "Value",
                dataset,
                true, true, false
        );

        XYPlot plot = chart.getXYPlot();

        // --- Renderer: enable lines + bullet dots ---
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        for (int i = 0; i < 3; i++)
        {
            renderer.setSeriesLinesVisible(i, true);   // lines on
            renderer.setSeriesShapesVisible(i, true);  // dots on
            renderer.setSeriesShape(i, new Ellipse2D.Double(- 3, - 3, 6, 6)); // bullet dot
        }

        plot.setRenderer(renderer);

        // Chart panel
        ChartPanel panel = new ChartPanel(chart);
        setContentPane(panel);
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> {
            yes_TimeSeriesWithBullets example = new yes_TimeSeriesWithBullets();
            example.setSize(900, 600);
            example.setLocationRelativeTo(null);
            example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            example.setVisible(true);
        });
    }
}
