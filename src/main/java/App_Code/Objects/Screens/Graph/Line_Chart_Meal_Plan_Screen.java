package App_Code.Objects.Screens.Graph;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.TotalMealTable;
import App_Code.Objects.Database_Objects.MealManager;
import App_Code.Objects.Graph_Objects.Line_Chart;
import App_Code.Objects.Graph_Objects.Pie_Chart;
import App_Code.Objects.Gui_Objects.Screen;
import App_Code.Objects.Screens.Meal_Plan_Screen;
import org.apache.commons.lang3.tuple.Triple;
import org.javatuples.Triplet;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

public class Line_Chart_Meal_Plan_Screen extends Screen
{
    // #################################################################################################################
    // Variables
    // #################################################################################################################
    private String planName;

    //##############################################
    // Objects
    //##############################################
    private MyJDBC db;
    private Meal_Plan_Screen meal_plan_screen;

    //##############################################
    // Collections
    //##############################################
    private final String[] macronutrientsToCheck = new String[]{
            "total_protein", "total_carbohydrates", "total_sugars_of_carbs", "total_fats", "total_saturated_fat",
            "total_salt", "total_fibre"
            // ,"total_calories"
            // , "total_water_content"
    };

    private HashMap<Integer, ArrayList<Triplet<String, LocalTime, BigDecimal>>> mealValues = new HashMap<>();
    // Key : Meal ID | Value: MacroName, Time, Value

    private TreeSet<Map.Entry<Integer, MealManager>> mealManagerTreeSet;

    //##############################################
    // Datasets Objects
    //##############################################
    private Line_Chart line_chart;
    TimeSeriesCollection dataset = new TimeSeriesCollection();

    // #################################################################################################################
    // Constructor
    // #################################################################################################################
    public Line_Chart_Meal_Plan_Screen(MyJDBC db, Meal_Plan_Screen meal_plan_screen)
    {
        // ##########################################
        // Super Constructors & Variables
        // ##########################################
        super(db, "Line Chart: Plan Macros", 1000, 900, 0, 0);
        getScrollPaneJPanel().setBackground(Color.WHITE);
        setResizable(true);

        // ##########################################
        // Variables
        // ##########################################
        this.db = db;
        this.mealManagerTreeSet = meal_plan_screen.getMealManagerTreeSet();
        this.meal_plan_screen = meal_plan_screen;
        this.planName = meal_plan_screen.getPlanName();

        //############################################
        // Creating Macros / Dataset
        //############################################
        if (! createDataSet(false))
        {
            getFrame().dispose();
            return;
        }

        // ##########################################
        // Create Graph Object & Adding to GUI
        // ##########################################
        line_chart = new Line_Chart(String.format("%s : Macros Over 24 Hours", planName), frameWidth - 100, frameHeight - 60, dataset);
        addToContainer(getScrollPaneJPanel(), line_chart, 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);

        // ##########################################
        // Make Frame Visible
        // ##########################################
        setFrameVisibility(true);
    }

    // #################################################################################################################
    // Methods
    // #################################################################################################################
    @Override
    public void windowClosedEvent()
    {
        meal_plan_screen.removeLineChartScreen();
    }

    // ##################################################
    // Dataset / Update Methods
    // ##################################################
    private boolean createDataSet(boolean clear)
    {
        // ############################################
        // Clear Old Data
        // ############################################
        mealValues.clear();
        dataset = new TimeSeriesCollection();

        // ############################################
        // Build Dataset
        // ############################################
        HashMap<String, TimeSeries> timeSeriesHashMap = new HashMap<String, TimeSeries>();

        for (String macroName : macronutrientsToCheck)
        {
            if (! timeSeriesHashMap.containsKey(macroName) || clear)
            {
                String macroNameGUI = String.format("  %s  ", macroName).replaceAll("_", " "); // reformat macroName for GUI purposes
                timeSeriesHashMap.put(macroName, new TimeSeries(macroNameGUI));
            }

            TimeSeries timeSeries = timeSeriesHashMap.get(macroName);

            for (Map.Entry<Integer, MealManager> mapEntry : mealManagerTreeSet)
            {
                try{
                    // ############################################
                    // Get Meal Manager Variables
                    // ############################################
                    MealManager mealManager = mapEntry.getValue();
                    TotalMealTable totalMealTable = mealManager.getTotalMealTable();
                    BigDecimal macroValue = totalMealTable.getValueOnTable(macroName);

                    Integer mealID = mapEntry.getKey();
                    String mealName = mealManager.getCurrentMealName();

                    // ############################################
                    // Meal Time
                    // ############################################
                    LocalTime mealTime = mealManager.getCurrentMealTime();

                    // Convert LocalTime -> Date (fixed base date)
                    LocalDate baseDate = LocalDate.of(2025, 1, 1);
                    LocalDateTime dateTime = baseDate.atTime(mealTime);
                    Date date = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
                    Second second = new Second(date);

                    // ############################################
                    // Adding Value & to Macro TimeSeries
                    // ############################################
                    timeSeries.add(second, macroValue.doubleValue());
                }
                catch (Exception e)
                {
                    JOptionPane.showMessageDialog(meal_plan_screen.getFrame(), "Error, creating Line Chart with column table names!");
                    System.err.printf("\n\nLine_Chart_Meal_Plan_Screen.java : updateDataSet() \nValue: %s \n%s", macroName, e);
                    return false;
                }
            }

            dataset.addSeries(timeSeries);
        }
        return true;
    }

    public void update_LineChart_Title()
    {
        planName = meal_plan_screen.getPlanName();
        line_chart.setTitle(planName);
    }
}

