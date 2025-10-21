package App_Code.Objects.Graph_Objects;


import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.GridArrangement;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.HorizontalAlignment;
import org.jfree.chart.util.Rotation;
import org.jfree.data.general.DefaultPieDataset;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;

public class Pie_Chart<K extends Comparable<K>> extends JPanel
{
    // ############################################################################################
    // Variables
    // ############################################################################################
    
    // Collections
    protected DefaultPieDataset<K> dataset;
    protected Color[] colors;
    
    // Objects
    protected JFreeChart chart;
    protected PiePlot3D plot;
    protected ChartPanel chartPanel;
    
    
    // Font
    protected Font titleFont, labelFont, legendFont;
    
    // String
    protected String title;
    
    // int
    protected int rows, cols = 2;
    
    // BigDecimal
    protected BigDecimal datasetTotal = new BigDecimal(0);
    
    // #################################################################################################################
    // Constructor
    // #################################################################################################################
    public Pie_Chart(String title, Color[] colors, int frameWidth, int frameHeight, int rotateDelay, Font titleFont,
                     Font labelFont, Font legendFont, DefaultPieDataset<K> datasetInput)
    {
        //############################################
        // Set Layout Dimensions
        //############################################
        this.dataset = datasetInput;
        this.colors = colors;
        
        this.title = title;
        
        this.titleFont = titleFont;
        this.labelFont = labelFont;
        this.legendFont = legendFont;
        
        //############################################
        // Set Layout Dimensions
        //############################################
        setPreferredSize(new Dimension(frameWidth, frameHeight));
        setLayout(new GridLayout(1, 1));
        
        //############################################
        // Create Plot with Data & Configurations
        //############################################
        chart = ChartFactory.createPieChart3D(title, dataset, true, true, false);
        
        plot = (PiePlot3D) chart.getPlot();
        plot.setStartAngle(290);
        plot.setDirection(Rotation.CLOCKWISE);
        plot.setForegroundAlpha(0.6f);
        plot.setInteriorGap(0.02);
        
        //############################################
        // Label Generations
        //############################################
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
        plot.setLabelPaint(Color.BLACK);
        
        // #############################################
        // Legend Positioning & Grid
        //#############################################
        reDraw_Legend();
        
        //#############################################
        // Color Palette
        //##############################################
        /**
         * Lock this pieChart to using the same colours in the same sequence
         * Caused mismatch with other charts when the dataset was updated as the order of colours was randomised
         */
        DefaultDrawingSupplier fixedDrawingSupplier = new DefaultDrawingSupplier(
                colors,
                DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE
        );
        
        plot.setDrawingSupplier(fixedDrawingSupplier);
        
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
            dataset_ActionEvents();
        });
        
        //############################################
        // Events Need to be triggered at runtime
        //############################################
        /**
         Event above is only triggered when values change & we need to do check on initialization too
         */
        first_RunTime_Events();
        
        // #############################################
        // Add Plot to Panel
        //#############################################
        add(chartPanel);
    }
    
    // #################################################################################################################
    // Methods
    // #################################################################################################################
    protected void dataset_ActionEvents()
    {
        calculate_Dataset_Total();
        is_PieChart_Empty_MSG();
        reDraw_Legend();
    }
    
    protected void first_RunTime_Events()
    {
        calculate_Dataset_Total();
        is_PieChart_Empty_MSG();
    }
    
    protected void calculate_Dataset_Total()
    {
        // ###################################
        // Reset Value
        // ###################################
        datasetTotal = BigDecimal.ZERO;
        
        // ###################################
        // Calculate Sum
        // ###################################
        for (K macroKey : dataset.getKeys())
        {
            datasetTotal = datasetTotal.add((BigDecimal) dataset.getValue(macroKey));
        }
    }
    
    protected BigDecimal get_DatasetTotal()
    {
        return datasetTotal;
    }
    
    protected void is_PieChart_Empty_MSG()
    {
        //#####################################################
        // Set the no-data message
        //#####################################################
        // If the total is bigger than 0 exit
        if (get_DatasetTotal().compareTo(BigDecimal.ZERO) > 0) { plot.setNoDataMessage(null); return; }
        
        //#####################################################
        // Set the no-data message
        //#####################################################
        plot.setNoDataMessage("No data to display");
        plot.setNoDataMessageFont(new Font("SansSerif", Font.BOLD, 18));
        plot.setNoDataMessagePaint(Color.RED);
    }
    
    protected void reDraw_Legend()
    {
        // #############################################
        // Legend Positioning & Grid
        //#############################################
        if (chart.getLegend() != null) { chart.removeLegend(); } // remove default legend
        
        rows = (int) Math.ceil((double) dataset.getItemCount() / cols);
        
        LegendTitle legend = new LegendTitle(plot, new GridArrangement(rows, cols), new GridArrangement(rows, cols));
        
        legend.setItemFont(legendFont); // Set Legend Font
        chart.addLegend(legend);
        
        // #############################################
        // Set Horizontal Legend Alignment
        //#############################################
        set_Horizontal_LegendAlignment();
        
        // #############################################
        // Update Chart
        //#############################################
        chart.fireChartChanged(); // repaints Chart
    }
    
    protected void set_Horizontal_LegendAlignment()
    {
        LegendTitle legend = plot.getChart().getLegend();
        legend.setPosition(org.jfree.chart.ui.RectangleEdge.BOTTOM);
        legend.setHorizontalAlignment(HorizontalAlignment.LEFT);
    }
    
    protected int percent_Calculator(BigDecimal value, BigDecimal overall)
    {
        //#######################################
        // Exit Clause
        //#######################################
        if (overall.compareTo(BigDecimal.ZERO) == 0) { return 0; }
        
        //#######################################
        // Create % in int
        //#######################################
        BigDecimal ratio = value.divide(overall, 4, RoundingMode.DOWN); // 4 decimal places, rounded
        BigDecimal percent = ratio.multiply(BigDecimal.valueOf(100));      // Convert to %
        return percent.setScale(0, RoundingMode.HALF_DOWN).intValueExact();
    }
    
    public void setTitle(String txt)
    {
        chart.setTitle(txt);
    }
    
    //############################################################################################
    // Rotator Class
    //############################################################################################
    protected class Rotator extends Timer implements ActionListener
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
