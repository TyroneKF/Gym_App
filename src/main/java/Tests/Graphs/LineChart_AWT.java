package Tests.Graphs;

import org.apache.commons.lang3.RandomStringUtils;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;

import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class LineChart_AWT extends JFrame
{

    public LineChart_AWT(String applicationTitle, String chartTitle)
    {
        super(applicationTitle);
        setVisible(true);
        Container contentPane = getContentPane();
        setSize(new Dimension(900, 900));
        contentPane.setLayout(new GridLayout(1, 1));


        JFreeChart lineChart = ChartFactory.createLineChart(
                chartTitle,
                "Years", "Number of Schools",
                createDataset(),
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(lineChart);
        contentPane.add(chartPanel);
        contentPane.revalidate();
    }

    private DefaultCategoryDataset createDataset()
    {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Group 1
        dataset.addValue((int) (Math.random() * 300), "schools", "09:00");
        dataset.addValue((int) (Math.random() * 300), "schools", "12:00");
        dataset.addValue((int) (Math.random() * 300), "schools", "15:00");
        dataset.addValue((int) (Math.random() * 300), "schools", "17:00");
        dataset.addValue((int) (Math.random() * 300), "schools", "18:30");
        dataset.addValue((int) (Math.random() * 300), "schools", "22:00");

        // Group 2
        dataset.addValue((int) (Math.random() * 300), "Dog", "09:00");
        dataset.addValue((int) (Math.random() * 300), "Dog", "12:00");
        dataset.addValue((int) (Math.random() * 300), "Dog", "15:00");
        dataset.addValue((int) (Math.random() * 300), "Dog", "17:00");
        dataset.addValue((int) (Math.random() * 300), "Dog", "18:30");
        dataset.addValue((int) (Math.random() * 300), "Dog", "22:00");

        // Group 3
        dataset.addValue((int) (Math.random() * 300), "Cat", "09:00");
        dataset.addValue((int) (Math.random() * 300), "Cat", "12:00");
        dataset.addValue((int) (Math.random() * 300), "Cat", "15:00");
        dataset.addValue((int) (Math.random() * 300), "Cat", "17:00");
        dataset.addValue((int) (Math.random() * 300), "Cat", "18:30");
        dataset.addValue((int) (Math.random() * 300), "Cat", "22:00");

        return dataset;
    }

    public static void main(String[] args)
    {
        LineChart_AWT chart = new LineChart_AWT(
                "School Vs Years",
                "Numer of Schools vs years");


    }
}
