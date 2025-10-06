package App_Code.Objects.Graph_Objects;


import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.util.Rotation;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;


public class Pie_Chart extends JPanel
{
    // ############################################################################################
    // Variables
    // ############################################################################################
     protected JFreeChart chart;
    
    // ############################################################################################
    // Constructor
    // ############################################################################################
    public Pie_Chart(String title, int frameWidth, int frameHeight, Font titleFont, Font labelFont, Font legendFont,
                     DefaultPieDataset dataset)
    {
        //############################################
        // Set Layout Dimensions
        //############################################
        setPreferredSize(new Dimension(frameWidth, frameHeight));
        setLayout(new GridLayout(1, 1));
        
        //############################################
        // Create Plot with Data & Configurations
        //############################################
        chart = ChartFactory.createPieChart3D(title, dataset, true, true, false);
        
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
        
        // #############################################
        // Setting Font sizes
        //#############################################
        // Set Title Font Size
        TextTitle titleObject = chart.getTitle();
        titleObject.setFont(titleFont);
        
        // Label font size on diagram
        plot.setLabelFont(labelFont);
        plot.setLabelPaint(Color.BLACK);   // Black stands out better than white
        
        // Legend Font Size
        chart.getLegend().setItemFont(legendFont);
        
        // #############################################
        // Auto Rotate Features
        //#############################################
        // 🔹 auto-rotate : auto-rotate (50 ms delay ≈ 20 frames/sec)
        Rotator rotator = new Rotator(plot, 100);
        rotator.start();
        
        // #############################################
        // Create Plot Dimensions
        //#############################################
        // Add chart to panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(frameWidth, frameHeight));
        
        // #############################################
        // Add Plot to Panel
        //#############################################
        add(chartPanel);
    }
    
    // ############################################################################################
    // Methods
    // ############################################################################################
    public void setTitle(String meal_name)
    {
        chart.setTitle(String.format("%s Macros", meal_name));
    }
    
    //############################################################################################
    // Rotator Class
    //############################################################################################
    public class Rotator extends Timer implements ActionListener
    {
        private final PiePlot plot;
        private int angle = 270; // starting angle
        
        public Rotator(PiePlot plot, int delay)
        {
            super(delay, null);
            this.plot = plot;
            addActionListener(this);
        }
        
        @Override
        public void actionPerformed(ActionEvent e)
        {
            angle = angle + 1;          // rotate 1 degree per tick
            if (angle == 360)
            {
                angle = 0;
            }
            plot.setStartAngle(angle);  // update the plot
        }
    }
}
