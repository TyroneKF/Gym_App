package Tests.Graphs;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;

import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;

public class LineChart_AWT extends JFrame
{

    public LineChart_AWT(String applicationTitle, String chartTitle)
    {
        super(applicationTitle);
        setVisible(true);
        Container contentPane = getContentPane();
        setSize(new Dimension(900, 900));
        contentPane.setLayout(new GridLayout(1,1));


        JFreeChart lineChart = ChartFactory.createLineChart(
                chartTitle,
                "Years", "Number of Schools",
                createDataset(),
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(lineChart);
        contentPane.add(chartPanel);
    }

    private DefaultCategoryDataset createDataset()
    {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(15, "schools", "1970");
        dataset.addValue(30, "schools", "1980");
        dataset.addValue(60, "schools", "1990");
        dataset.addValue(120, "schools", "2000");
        dataset.addValue(240, "schools", "2010");
        dataset.addValue(300, "schools", "2014");
        return dataset;
    }

    public static void main(String[] args)
    {
        LineChart_AWT chart = new LineChart_AWT(
                "School Vs Years",
                "Numer of Schools vs years");


    }
}
