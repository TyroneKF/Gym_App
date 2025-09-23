package App_Code.Objects.Graph_Objects;

import org.javatuples.Triplet;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Collection;
import java.util.HashMap;

public class Line_Chart extends JPanel
{
    // ############################################################################################
    // Variables
    // ############################################################################################
    JFreeChart chart;
    TimeSeriesCollection dataset = new TimeSeriesCollection();

    // ############################################################################################
    // Constructor
    // ############################################################################################
    public Line_Chart(String title, int frameWidth, int frameHeight, TimeSeriesCollection dataset)
    {
        //############################################
        // Set Layout Dimensions
        //############################################
        setPreferredSize(new Dimension(frameWidth, frameHeight));
        setLayout(new GridLayout(1, 1));

        //############################################
        // Add Data to Dataset to represent
        //############################################
        update_dataset(dataset);

        //############################################
        // Create Plot
        //############################################
        chart = ChartFactory.createTimeSeriesChart(
                title,
                "Time of Day (Consumption)",
                "Grams (g)",
                dataset,
                true, true, false
        );

        XYPlot plot = chart.getXYPlot();

        // ################################################
        // Edit Colour Pallet to avoid repeating colours
        //#################################################
        Paint[] customPaints = new Paint[]{
                Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE,
                Color.MAGENTA, Color.PINK, Color.CYAN,  Color.YELLOW,
                Color.BLACK
        };

        plot.setDrawingSupplier(new DefaultDrawingSupplier(
                customPaints,
                DefaultDrawingSupplier.DEFAULT_FILL_PAINT_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE
        ));

        // ################################################
        // --- Renderer: enable lines + bullet dots ---
        //#################################################
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        for (int i = 0; i < dataset.getSeriesCount(); i++)
        {
            renderer.setSeriesLinesVisible(i, true);   // lines on
            renderer.setSeriesShapesVisible(i, true);  // dots on
            renderer.setSeriesShape(i, new Ellipse2D.Double(- 3, - 3, 6, 6)); // bullet dot
            renderer.setSeriesPaint(i, customPaints[i]);
        }

        plot.setRenderer(renderer);

        // #############################################
        // Setting Font sizes
        //#############################################
        // Set Title Font Size
        TextTitle titleObject = chart.getTitle();
        titleObject.setFont(new Font("Serif", Font.PLAIN, 27));

        // Label font size on diagram
        plot.getRangeAxis().setLabelFont(new Font("SansSerif", Font.BOLD, 22));;
        plot.getDomainAxis().setLabelFont(new Font("SansSerif", Font.BOLD, 22));;

        // Legend Font Size
        chart.getLegend().setItemFont(new Font("Serif", Font.PLAIN, 21));

        // #############################################
        // Create Plot Dimensions
        //#############################################
        // Add chart to panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(frameWidth, frameHeight));

        //############################################
        // Ad Diagram to GUI
        //############################################
        add(chartPanel);
    }

    // ############################################################################################
    // Methods
    // ############################################################################################
    public void setTitle(String meal_name)
    {
        chart.setTitle(String.format("%s Macros Over 24 Hours", meal_name));
    }

    public void update_dataset(TimeSeriesCollection dataset)
    {
        this.dataset = dataset;
    }

    public XYPlot getXY_Plot()
    {
        return chart.getXYPlot();
    }


}
