package Tests.Graphs;

// Run this as a single file (MultiLineHistogramExample.java)
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.statistics.HistogramDataset;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class MultiLineHistogramExample {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // --- create data (or replace with your own double[] arrays) ---
            double[] values1 = randomData(1000, 5.0, 1.0);   // mean 5, sd 1
            double[] values2 = randomData(1000, 7.0, 1.5);   // mean 7, sd 1.5
            double[] values3 = randomData(1000, 8.0, 1.5);   // mean 7, sd 1.5
            double[] values4 = randomData(1000, 9.0, 1.5);   // mean 7, sd 1.5
            double[] values5 = randomData(1000, 10.0, 1.5);   // mean 7, sd 1.5
            double[] values6 = randomData(1000, 11.0, 1.5);   // mean 7, sd 1.5

            // --- build the histogram dataset (multiple series) ---
            HistogramDataset dataset = new HistogramDataset();
            dataset.addSeries("Group A", values1, 40); // 40 bins
            dataset.addSeries("Group B", values2, 40);
            dataset.addSeries("Group C", values3, 40);
            dataset.addSeries("Group D", values4, 40);
            dataset.addSeries("Group E", values5, 40);
            dataset.addSeries("Group F", values6, 40);

            // --- create chart ---
            JFreeChart chart = ChartFactory.createHistogram(
                    "Multiple-Line Histogram",
                    "Value",
                    "Frequency",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );

            // --- switch to line renderer so each series is drawn as a line ---
            XYPlot plot = (XYPlot) chart.getXYPlot();
            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false); // lines=true, shapes=false

            renderer.setSeriesPaint(0, new Color(52, 152, 219));
            renderer.setSeriesStroke(0, new BasicStroke(2.0f));
            renderer.setSeriesPaint(1, new Color(231, 76, 60));
            renderer.setSeriesStroke(1, new BasicStroke(2.0f));

            // styling
            plot.setRenderer(renderer);
            plot.setBackgroundPaint(Color.white);
            plot.setRangeGridlinePaint(Color.lightGray);
            plot.setDomainGridlinePaint(Color.lightGray);

            // --- show ---
            JFrame frame = new JFrame("Multi-Line Histogram Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new ChartPanel(chart));
            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    // --- helper that generates gaussian random data ---
    private static double[] randomData(int size, double mean, double stdDev) {
        Random rand = new Random();
        double[] data = new double[size];
        for (int i = 0; i < size; i++) {
            data[i] = mean + stdDev * rand.nextGaussian();
        }
        return data;
    }
}
