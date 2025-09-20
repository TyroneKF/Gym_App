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

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

public class Line_Chart_Meal_Plan_Screen extends Screen
{
    private MyJDBC db;
    private Meal_Plan_Screen meal_plan_screen;
    private String planName;
    private Line_Chart line_chart;

    private TreeSet<Map.Entry<Integer, MealManager>> mealManagerTreeSet;
    private final String[] macronutrientsToCheck = new String[]{
            "total_protein", "total_carbohydrates", "total_sugars_of_carbs", "total_fats", "total_saturated_fat",
            "total_water_content", "total_calories" };

    // Key : Meal ID | Value: MacroName, Time, Value
    private HashMap<Integer, ArrayList<Triplet<String, LocalTime, BigDecimal>>> mealValues = new HashMap<>();
    TimeSeriesCollection dataset = new TimeSeriesCollection();

    Line_Chart_Meal_Plan_Screen(MyJDBC db, Meal_Plan_Screen meal_plan_screen)
    {
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
        if (! updateDataSet(false))
        {
            getFrame().dispose();
            return;
        }

        // ##########################################
        // Create Graph Object & Adding to GUI
        // ##########################################
        line_chart = new Line_Chart(String.format("%s Macros", planName), frameWidth - 50, frameHeight - 60);
        addToContainer(getScrollPaneJPanel(), line_chart, 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);

        setFrameVisibility(true);
    }

    private boolean updateDataSet(boolean clear)
    {
        mealValues.clear();
        HashMap<String, TimeSeries> timeSeriesHashMap = new HashMap<String, TimeSeries>();

        for (String macro : macronutrientsToCheck)
        {
            if (! timeSeriesHashMap.containsKey(macro) || clear)
            {
                timeSeriesHashMap.put(macro, new TimeSeries(macro));
            }

            TimeSeries timeSeries = timeSeriesHashMap.get(macro);

            for (Map.Entry<Integer, MealManager> mapEntry : mealManagerTreeSet)
            {
                // ############################################
                // Get Meal Manager Variables
                // ############################################
                MealManager mealManager = mapEntry.getValue();
                TotalMealTable totalMealTable = mealManager.getTotalMealTable();
                BigDecimal macroValue = totalMealTable.getValueOnTable(macro);

                Integer mealID = mapEntry.getKey();
                String mealName = mealManager.getCurrentMealName();

                // ############################################
                // Meal Time
                // ############################################
                // Convert LocalTime -> Date (fixed base date)
                LocalTime mealTime = mealManager.getCurrentMealTime();
                LocalDate baseDate = LocalDate.of(1970, 1, 1);
                LocalDateTime dateTime = baseDate.atTime(mealTime);
                Date date = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());

                Second second = new Second(date);

                // ############################################
                // Adding Value & to Macro Timeseries
                // ############################################
                timeSeries.add(second, macroValue.doubleValue());
            }

            dataset.addSeries(timeSeries);
        }
        return false;
    }
}

