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
        // Create a time series
        TimeSeries series = new TimeSeries("Random Data");
        Random random = new Random();

        // Add random values for 10 minutes
        Minute current = new Minute();
        for (int i = 0; i < 10; i++)
        {
            series.add(current, random.nextInt(100)); // y = random value
            current = (Minute) current.next();
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection(series);

        // Create chart
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Time vs Random Value",
                "Time",
                "Value",
                dataset,
                true, true, false
        );

        XYPlot plot = chart.getXYPlot();

        // Renderer with bullet dots
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, true);   // true = show connecting lines
        renderer.setSeriesShapesVisible(0, true);  // show bullets/dots
        renderer.setSeriesShape(0, new Ellipse2D.Double(- 4, - 4, 8, 8)); // circle marker

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
