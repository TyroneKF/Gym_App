package App_Code.Objects.Screens.Others.Graph;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Gui_Objects.Screen;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Map;

import Tests.Graphs.DynamicPieChart2;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.util.Rotation;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;

import java.text.DecimalFormat;


public class Meal_Graph_Screen extends Screen
{
    private final static String
            db_Scripts_Folder_Path = "/data/database_scripts",
            db_File_Script_List_Name = "0.) Script_List.txt",
            db_File_Tables_Name = "0.) Database_Names.txt";

    private String meal_name;
    private int meal_in_plan_id, temp_planID;
    private Map<String, Double> macros;
    private DefaultPieDataset dataset = new DefaultPieDataset();

    // replace with mealManager eventually
    public Meal_Graph_Screen(MyJDBC db, String meal_name, int meal_in_plan_id, int temp_planID, Map<String, Double> macros)
    {
        //############################################
        // Variables
        //############################################
        super(db, meal_name, 800, 600, 0, 0);
        getScrollPaneJPanel().setBackground(Color.WHITE);
        setResizable(false);

        this.meal_in_plan_id = meal_in_plan_id;
        this.temp_planID = temp_planID;
        this.meal_name = meal_name;
        this.macros = macros;

        //############################################
        // Add Data to Dataset to represent
        //############################################
        for (String key : macros.keySet())
        {
            dataset.setValue(String.format("  %s (%s g )  ", key, macros.get(key)), macros.get(key) );
        }

        //############################################
        // Create Plot with Data & Configurations
        //############################################
        JFreeChart chart = ChartFactory.createPieChart3D(String.format("%s Macros", meal_name), dataset, true, true, false);

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
        TextTitle title = chart.getTitle();
        title.setFont(new Font("Serif", Font.PLAIN, 27));

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
        chartPanel.setPreferredSize(new java.awt.Dimension(getFrameWidth() - 50, getFrameHeight() - 60));
        addToContainer(getScrollPaneJPanel(), chartPanel, 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
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


    public static void main(String[] args) throws IOException
    {
        // #########################################
        // Create DB Object & run SQL Scripts
        // #########################################
        MyJDBC db = new MyJDBC(true, "localhost", "3306", "root", "password", "gymapp00001", db_Scripts_Folder_Path, db_File_Script_List_Name, db_File_Tables_Name);

        if (db.get_DB_Connection_Status())
        {
            Map<String, Double> macros = Map.ofEntries(
                    Map.entry("Protein", 71.89),
                    Map.entry("Carbs", 231.15),
                    Map.entry("Fats", 104.62)
            );

            Meal_Graph_Screen x = new Meal_Graph_Screen(db, "Breakfast", 2, 1, macros);
            x.setFrameVisibility(true);
        }
        else
        {
            JOptionPane.showMessageDialog(null, "ERROR, Cannot Connect To Database!");
            return;
        }
    }
}
