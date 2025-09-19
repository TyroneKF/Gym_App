package Tests.Graphs;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;

public class GroupedBarChartExample {
    public static void main(String[] args) {
        // Create dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        dataset.addValue(10, "Plant 1", "Jan");
        dataset.addValue(20, "Plant 2", "Jan");
        dataset.addValue(40, "Plant 3", "Jan");

        dataset.addValue(25, "Plant 1", "Feb");
        dataset.addValue(50, "Plant 2", "Feb");
        dataset.addValue(55, "Plant 3", "Feb");

        // Create chart
        JFreeChart chart = ChartFactory.createBarChart(
                "Production per Month", // Chart title
                "Month",               // Category axis label
                "Units",               // Value axis label
                dataset
        );

        // Show it in a frame
        JFrame frame = new JFrame("Grouped Bar Chart");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);
    }
}
