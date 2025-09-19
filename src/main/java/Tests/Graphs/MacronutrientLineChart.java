package Tests.Graphs;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.Plot;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.*;
import java.awt.*;

public class MacronutrientLineChart {

    public static void main(String[] args) {
        // Create the data for the chart
        DefaultCategoryDataset dataset = createDataset();

        // Create the chart
        JFreeChart lineChart = createChart(dataset);

        // Display the chart in a panel
        JPanel chartPanel = new ChartPanel(lineChart);
        chartPanel.setPreferredSize(new Dimension(800, 600));

        // Create a frame to display the chart
        JFrame frame = new JFrame("Macronutrient Consumption Over the Day");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(chartPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    private static DefaultCategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Macronutrient values for each meal
        // Each meal is represented on the x-axis, with three series for carbs, protein, and fat

        // Meal data (example)
        dataset.addValue(60, "Carbs", "Meal 1");
        dataset.addValue(20, "Protein", "Meal 1");
        dataset.addValue(10, "Fat", "Meal 1");

        dataset.addValue(70, "Carbs", "Meal 2");
        dataset.addValue(25, "Protein", "Meal 2");
        dataset.addValue(12, "Fat", "Meal 2");

        dataset.addValue(80, "Carbs", "Meal 3");
        dataset.addValue(30, "Protein", "Meal 3");
        dataset.addValue(15, "Fat", "Meal 3");

        dataset.addValue(90, "Carbs", "Meal 4");
        dataset.addValue(35, "Protein", "Meal 4");
        dataset.addValue(18, "Fat", "Meal 4");

        // Add more meals as necessary

        return dataset;
    }

    private static JFreeChart createChart(DefaultCategoryDataset dataset) {
        // Create the line chart using the dataset
        JFreeChart chart = ChartFactory.createLineChart(
                "Macronutrient Consumption Over the Day", // Chart title
                "Meal", // X-axis label
                "Grams", // Y-axis label
                dataset, // Dataset
                PlotOrientation.VERTICAL, // Plot orientation
                true, // Include legend
                true, // Tooltips
                false // URLs
        );

        // Customize the chart appearance
        Plot plot = chart.getPlot();
        plot.setBackgroundPaint(Color.white);

        return chart;
    }
}
