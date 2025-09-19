package Tests.Graphs;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramBin;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class LineHistogramExample {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // --- Generate random "time of day" values (0–24 hours) ---
            double[] times = randomTimeData(200, 24);

            // --- Build histogram with 6 bins ---
            int bins = 6;
            HistogramDataset histDataset = new HistogramDataset();
            histDataset.addSeries("Events", times, bins, 0, 24);

            // --- Convert histogram bins into line series ---
            XYSeries series = new XYSeries("Events (Line Histogram)");

            double binWidth = 24.0 / bins;
            for (int i = 0; i < bins; i++) {
                double binStart = i * binWidth;
                double binMid = binStart + binWidth / 2.0;
                double freq = histDataset.getYValue(0, i); // frequency per bin
                series.add(binMid, freq);
            }

            // Put into dataset
            XYSeriesCollection dataset = new XYSeriesCollection(series);

            // --- Create Line Chart ---
            JFreeChart chart = ChartFactory.createXYLineChart(
                    "Events Over a Day (Line Histogram)",
                    "Time of Day",
                    "Frequency",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );

            XYPlot plot = chart.getXYPlot();

            // --- Replace X axis labels with time ranges ---
            String[] labels = {"0-4h", "4-8h", "8-12h", "12-16h", "16-20h", "20-24h"};
            SymbolAxis xAxis = new SymbolAxis("Time of Day", labels);
            plot.setDomainAxis(xAxis);

            NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
            yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

            // Styling
            plot.setBackgroundPaint(Color.white);
            plot.setDomainGridlinePaint(Color.lightGray);
            plot.setRangeGridlinePaint(Color.lightGray);

            // --- Show ---
            JFrame frame = new JFrame("Line Histogram Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new ChartPanel(chart));
            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static double[] randomTimeData(int count, int maxHour) {
        Random rand = new Random();
        double[] data = new double[count];
        for (int i = 0; i < count; i++) {
            data[i] = rand.nextDouble() * maxHour;
        }
        return data;
    }
}
