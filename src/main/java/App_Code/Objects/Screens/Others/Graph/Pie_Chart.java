package App_Code.Objects.Screens.Others.Graph;



import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Map;

import org.javatuples.Pair;
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
    private DefaultPieDataset dataset = new DefaultPieDataset();
    protected JFreeChart chart;

    public Pie_Chart(String title, int frameWidth, int frameHeight, Map<String, Pair<BigDecimal, String>> data)
    {
        //############################################
        // Set Layout Dimensions
        //############################################
        setPreferredSize(new Dimension(frameWidth, frameHeight));
        setLayout(new GridLayout(1, 1));

        //############################################
        // Add Data to Dataset to represent
        //############################################
        update_dataset(data);

        //############################################
        // Create Plot with Data & Configurations
        //############################################
        chart = ChartFactory.createPieChart3D(String.format("%s Macros", title), dataset, true, true, false);

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
        titleObject.setFont(new Font("Serif", Font.PLAIN, 27));

        // Label font size on diagram
        plot.setLabelFont(new Font("SansSerif", Font.BOLD, 22));
        plot.setLabelPaint(Color.BLACK);   // Black stands out better than white

        // Legend Font Size
        chart.getLegend().setItemFont(new Font("Serif", Font.PLAIN, 25));

        // #############################################
        // Auto Rotate Features
        //#############################################
        // 🔹 auto-rotate : auto-rotate (50 ms delay ≈ 20 frames/sec)
        Rotator rotator = new Rotator(plot, 50);
        rotator.start();

        // #############################################
        // Create Plot & Add to GUI
        //#############################################
        // Add chart to panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(frameWidth, frameHeight));

        // #############################################
        // Add Plot to Panel
        //#############################################
        add(chartPanel);
    }

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
            if (angle==360)
            {
                angle = 0;
            }
            plot.setStartAngle(angle);  // update the plot
        }
    }

    public void setTitle(String meal_name)
    {
        chart.setTitle(String.format("%s Macros", meal_name));
    }

    public void update_dataset(Map<String, Pair<BigDecimal, String>> data)
    {
        //############################################
        // Add Data to Dataset to represent
        //############################################]
        dataset.clear();
        for (String name : data.keySet())
        {
            Pair<BigDecimal, String> list = data.get(name);
            BigDecimal value = list.getValue0();
            String measurement  = list.getValue1();

            dataset.setValue(String.format("  %s ( %s %s )  ", name, value, measurement), value);
        }
    }
}
