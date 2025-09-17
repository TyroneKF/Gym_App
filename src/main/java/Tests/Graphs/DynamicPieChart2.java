package Tests.Graphs;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.util.Rotation;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;

import java.text.DecimalFormat;

public class DynamicPieChart2 extends JFrame
{
    private DefaultPieDataset dataset;

    public DynamicPieChart2(String title)
    {
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
        plot.setDirection(Rotation.CLOCKWISE);
        plot.setForegroundAlpha(0.6f);
        plot.setInteriorGap(0.02);

        // Format: "{0}" = key, "{1}" = value, "{2}" = percentage
        PieSectionLabelGenerator labelGenerator =
                new StandardPieSectionLabelGenerator(
                        "{2}",  // show key = value (percentage)
                        new DecimalFormat("0.0"),   // one decimal place for values
                        new DecimalFormat("0%"));   // percentage
        plot.setLabelGenerator(labelGenerator);

        // Place labels inside slices
        plot.setSimpleLabels(true);
        plot.setLabelGap(0.02);
        plot.setLabelFont(new Font("SansSerif", Font.BOLD, 16));
        plot.setLabelPaint(Color.BLACK);   // Black stands out better than white

        // Optional: change legend font too
        chart.getLegend().setItemFont(new Font("SansSerif", Font.PLAIN, 14));

        // Add chart to panel
        ChartPanel chartPanel = new ChartPanel(chart);
        setContentPane(chartPanel);

        // Example: Dynamically update data every 2 seconds
        Timer timer = new Timer(2000, new ActionListener()
        {
            double increment = 2.0;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                double value = dataset.getValue("Product 1").doubleValue();
                dataset.setValue("Product 1", (value + increment) % 100);
            }
        });
        timer.start();
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> {
            DynamicPieChart2 example = new DynamicPieChart2("Dynamic Pie Chart Example");
            example.setSize(800, 600);
            example.setLocationRelativeTo(null);
            example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            example.setVisible(true);
        });
    }
}
