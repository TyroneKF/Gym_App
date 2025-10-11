package App_Code.Objects.Graph_Objects;


import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
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
import java.util.Iterator;

import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;


public class Pie_Chart extends JPanel
{
    // ############################################################################################
    // Variables
    // ############################################################################################
    protected JFreeChart chart;
    protected DefaultPieDataset<String> dataset;
    protected PiePlot3D plot;
    
    // ############################################################################################
    // Constructor
    // ############################################################################################
    public Pie_Chart(String title, int frameWidth, int frameHeight, int rotateDelay, Font titleFont, Font labelFont,
                     Font legendFont, DefaultPieDataset<String> datasetInput)
    {
        //############################################
        // Set Layout Dimensions
        //############################################
        setPreferredSize(new Dimension(frameWidth, frameHeight));
        setLayout(new GridLayout(1, 1));
        this.dataset = datasetInput;
      
        //############################################
        // Create Plot with Data & Configurations
        //############################################
        chart = ChartFactory.createPieChart3D(title, dataset, true, true, false);
        
        plot = (PiePlot3D) chart.getPlot();
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
        Rotator rotator = new Rotator(plot, rotateDelay);
        rotator.start();
        
        // #############################################
        // Create Plot Dimensions
        //#############################################
        // Add chart to panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(frameWidth, frameHeight));
        
        //############################################
        // Create Plot with Data & Configurations
        //############################################
        /**
         * Event Listener cannot be added to the pieCharts themselves.
         * To detect where the data is empty etc; new meal or values equate to 0, the chart will go blank.
         * To identify this we have to add an eventListener to the data as the dataset is new re-initialized once created
         */
        
        dataset.addChangeListener(event -> {
            is_PieChart_Empty_MSG();
        });
        
        // Event above is only triggered when values change & we need to do check on initialization too
        is_PieChart_Empty_MSG();
        
        // #############################################
        // Add Plot to Panel
        //#############################################
        add(chartPanel);
    }
    
    // ############################################################################################
    // Methods
    // ############################################################################################
    public void setTitle(String txt)
    {
        chart.setTitle(txt);
    }
    
    public void is_PieChart_Empty_MSG()
    {
        Iterator it = dataset.getKeys().iterator();
    
        boolean emptyValues = true;
        while (it.hasNext())
        {
            String key = (String) it.next();
            Number value = dataset.getValue(key);
            BigDecimal bd_Value = BigDecimal.valueOf(value.doubleValue());
        
            if (bd_Value.compareTo(BigDecimal.ZERO) > 0) { emptyValues = false; break; }
        }
    
        if (! emptyValues) { return; }
    
        //#####################################################
        // Set the no-data message
        //#####################################################
        plot.setNoDataMessage("No data to display");
        plot.setNoDataMessageFont(new Font("SansSerif", Font.BOLD, 18));
        plot.setNoDataMessagePaint(Color.RED);
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
