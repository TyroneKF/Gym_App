package App_Code.Objects.Screens.Graph;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.TotalMealTable;
import App_Code.Objects.Database_Objects.MealManager;
import App_Code.Objects.Graph_Objects.Line_Chart;
import App_Code.Objects.Gui_Objects.Screen;
import App_Code.Objects.Screens.Meal_Plan_Screen;
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
import java.util.List;

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
//            ,"total_calories"
//            ,"total_water_content"
    };

    private TreeSet<Map.Entry<Integer, MealManager>> mealManagerTreeSet;

    //##############################################
    // Datasets Objects
    //##############################################
    private TimeSeriesCollection dataset  = new TimeSeriesCollection(); // Clear Dataset;
    private Line_Chart line_chart;

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
        if (! createDataSet())
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

    // ##################################################
    // Build Methods
    // ##################################################
    public boolean createDataSet()
    {
        // ############################################
        // Build Dataset
        // ############################################
        for (String macroName : macronutrientsToCheck)
        {
            // ############################################
            // Create TimeSeries For Macros
            // ############################################
            String macroNameGUI = convertMacroNameToGuiVersion(macroName);
            TimeSeries timeSeries = new TimeSeries(macroNameGUI);

            // #################################################
            // Per MacroName Get Values From Each MealManager
            // #################################################
            for (Map.Entry<Integer, MealManager> mapEntry : mealManagerTreeSet)
            {
                MealManager mealManager = mapEntry.getValue();

                // If meal has been deleted don't include it in the count
                if (mealManager.getHasMealPlannerBeenDeleted()) { continue; }

                try
                {
                    // ############################################
                    // Get Meal Manager Variables
                    // ############################################
                    TotalMealTable totalMealTable = mealManager.getTotalMealTable();
                    BigDecimal macroValue = totalMealTable.getValueOnTable(macroName);

                    // ############################################
                    // Meal Time Refactoring for TimeSeries Format
                    // ############################################
                    LocalTime mealTime = mealManager.getCurrentMealTime();
                    Second second = localTimeToSecond(mealTime);

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

    public void updateMealManagerDataChange(MealManager mealManager, LocalTime previousTime, LocalTime currentTime)
    {
        // ####################################################
        // Get MealManager Info
        // ####################################################
        Boolean timeChanged = ! previousTime.equals(currentTime);


        // ####################################################
        // Get MealManager MacroInfo & Replace
        // ####################################################

        for (String macroName : macronutrientsToCheck)
        {
            // ########################################
            // Get Correlated Macro TimeSeries
            // ########################################
            // Convert Table Column to Key in TimeSeries Collection
            String macroNameGUI = convertMacroNameToGuiVersion(macroName);

            // Get TimeSeries correlated to MacroName in collection
            TimeSeries macroSeries = dataset.getSeries(macroNameGUI);

            // ########################################
            // Get Macronutrient Info
            // ########################################
            // MealManager / TotalMealView Table Macro Result
            BigDecimal newMacroValue = mealManager.getTotalMealTable().getValueOnTable(macroName);

            // convert old time to second for collection
            Second oldMealTime = localTimeToSecond(previousTime);

            // #######################################
            // OLD Time: Replace With New Value
            // #######################################
            if (! timeChanged) // IF the time hasn't changed just update the series value with the old time
            {
                macroSeries.addOrUpdate(oldMealTime, newMacroValue); // update value
                continue;
            }

            // ########################################
            // New Time : Delete & Add Old / New Value
            // ########################################
            macroSeries.delete(oldMealTime); // IF the time has changed delete the old time from series value
            macroSeries.add(localTimeToSecond(currentTime), newMacroValue); // Add new value to Series with new Time
        }
    }

    // #################################################################################################################
    // Methods
    // #################################################################################################################
    @Override
    protected void windowClosedEvent()
    {
        meal_plan_screen.removeLineChartScreen();
    }

    // ##################################################
    // Conversion Methods
    // ##################################################
    private String convertMacroNameToGuiVersion(String macroName)
    {
        // Reformat macroName for GUI purposes  "\u00A0" is like space because \t doesn't work in this label format

      //  return String.format("\u00A0\u00A0%s\u00A0\u00A0", macroName, true);

        return String.format("\u00A0\u00A0%s\u00A0\u00A0", formatStrings(macroName, true));
    }

    private Second localTimeToSecond(LocalTime localTime)
    {
        // Convert LocalTime -> Date (fixed base date)
        LocalDate baseDate = LocalDate.of(2025, 1, 1);
        LocalDateTime dateTime = baseDate.atTime(localTime);
        Date date = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());

        return new Second(date);
    }

    // ##################################################
    //  Update  Methods
    // ##################################################
    public void update_LineChart_Title()
    {
        planName = meal_plan_screen.getPlanName();
        line_chart.setTitle(planName);
    }


    // ##################################################
    //  Clear Dataset Methods
    // ##################################################
    public void clear_Dataset()
    {
        dataset = new TimeSeriesCollection();
        line_chart.getXY_Plot().setDataset(dataset);

        createDataSet();
    }

    /*
       Based on MealManagers time the data is deleted in the series collection
     */
    public void deleteMealManagerData(MealManager mealManager, LocalTime mealTime)
    {
        for (String macroName : macronutrientsToCheck)
        {
            // ###########################################
            // Get Correlated Macro TimeSeries
            // ############################################
            // Convert Table Column to Key in TimeSeries Collection
            String macroNameGUI = convertMacroNameToGuiVersion(macroName);

            // Get TimeSeries correlated to MacroName in collection
            TimeSeries macroSeries = dataset.getSeries(macroNameGUI);

            // #############################################
            // Delete MealManagers Correlated Macro Value
            // #############################################
            macroSeries.delete(localTimeToSecond(mealTime));
        }
    }


}

