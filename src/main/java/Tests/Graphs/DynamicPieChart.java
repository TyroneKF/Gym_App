package Tests.Graphs;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import java.text.DecimalFormat;

public class DynamicPieChart extends JFrame {
    private DefaultPieDataset dataset;

    public DynamicPieChart(String title) {
        super(title);
        setResizable(false);

        // Create dataset
        dataset = new DefaultPieDataset();
        dataset.setValue("Product 1", 15.8);
        dataset.setValue("Product 2", 21);
        dataset.setValue("Product 3", 28.9);
        dataset.setValue("Product 4", 23.6);
        dataset.setValue("Product 5", 10.7);

        // Create chart
        JFreeChart chart = ChartFactory.createPieChart3D(
                "Market Share", dataset, true, true, false);

        PiePlot3D plot = (PiePlot3D) chart.getPlot();
        plot.setStartAngle(290);
        plot.setForegroundAlpha(0.6f);
        plot.setInteriorGap(0.02);

        // Format: "{0}" is the key, "{1}" is the value, "{2}" is the percentage
        PieSectionLabelGenerator labelGenerator =
                new StandardPieSectionLabelGenerator(
                        "{0} = {2}", new DecimalFormat("0"), new DecimalFormat("0%"));
        plot.setLabelGenerator(labelGenerator);

        // Increase font size here
        plot.setLabelFont(new Font("SansSerif", Font.BOLD, 16));

        // Add chart to panel
        ChartPanel chartPanel = new ChartPanel(chart);
        setContentPane(chartPanel);

        // Example: Dynamically update data every 2 seconds
        Timer timer = new Timer(2000, new ActionListener() {
            double increment = 2.0;
            @Override
            public void actionPerformed(ActionEvent e) {
                double value = dataset.getValue("Product 1").doubleValue();
                dataset.setValue("Product 1", (value + increment) % 100);
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DynamicPieChart example = new DynamicPieChart("Dynamic Pie Chart Example");
            example.setSize(800, 600);
            example.setLocationRelativeTo(null);
            example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            example.setVisible(true);
        });
    }
}
