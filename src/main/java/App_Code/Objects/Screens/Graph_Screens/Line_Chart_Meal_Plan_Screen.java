package App_Code.Objects.Screens.Graph_Screens;

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

public class Line_Chart_Meal_Plan_Screen extends Screen
{
    // #################################################################################################################
    // Variables
    // #################################################################################################################
    private String planName;

    //##############################################
    // Objects
    //##############################################
    private Meal_Plan_Screen meal_plan_screen;

    //##############################################
    // Collections
    //##############################################
    private TreeSet<Map.Entry<Integer, MealManager>> mealManagerTreeSet;

    private Map<String, Integer> macronutrientsToCheckAndPos;

    //##############################################
    // Datasets Objects
    //##############################################
    private TimeSeriesCollection dataset = new TimeSeriesCollection(); // Clear Dataset;
    private Line_Chart line_chart;

    // #################################################################################################################
    // Constructor
    // #################################################################################################################
    public Line_Chart_Meal_Plan_Screen(MyJDBC db, Meal_Plan_Screen meal_plan_screen)
    {
        // ##########################################
        // Super Constructors & Variables
        // ##########################################
        super(db, true, "Line Chart: Plan Macros", 1000, 900, 0, 0);
        getScrollPaneJPanel().setBackground(Color.WHITE);
        setResizable(true);

        // ##########################################
        // Variables
        // ##########################################
        this.db = db;
        this.meal_plan_screen = meal_plan_screen;
        this.mealManagerTreeSet = meal_plan_screen.getMealManagerTreeSet();
        this.planName = meal_plan_screen.getPlanName();
        this.macronutrientsToCheckAndPos = meal_plan_screen.getTotalMeal_MacroColNamePos();

        //############################################
        // Creating Macros / Dataset
        //############################################
        if (! createDataSet()) { windowClosedEvent(); return; }

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
        Iterator<Map.Entry<Integer, MealManager>> it = mealManagerTreeSet.iterator();
        while(it.hasNext())
        {
            // ############################################
            // MealManager Object & Info
            // ############################################
            Map.Entry<Integer, MealManager> mealManagerEntry = it.next();
            MealManager mealManager = mealManagerEntry.getValue();
    
            // MealManager MealName
            String mealManagerName = mealManager.getCurrentMealName();
    
            // Meal Time In TimeSeries Format
            Second mealTimeSeconds = localTimeToSecond(mealManager.getCurrentMealTime());
          
            // Get MealManager TotalMeal Data
            Object[] totalMealTableData = mealManager.getTotalMealTable().getTableData();
    
            // ############################################
            // Add Desired Macros From TotalMeal to DATA
            // ############################################
            Iterator<Map.Entry<String, Integer>> macrosIT = macronutrientsToCheckAndPos.entrySet().iterator();
            while(macrosIT.hasNext())
            {
                // Get MacroDetails
                Map.Entry<String, Integer> macroEntry = macrosIT.next();
                String macroGUIName = convertMacroNameToGuiVersion(macroEntry.getKey());
                Integer macroColPos = macroEntry.getValue();
        
                // ############################################
                // Get Macro TimeSeries
                // ############################################
                TimeSeries macroTimeSeries;
                if(dataset.getSeriesIndex(macroGUIName) < 0) // IF Macros Series doesn't exist in collection, add it
                {
                    macroTimeSeries = new TimeSeries(macroGUIName);
                    dataset.addSeries(macroTimeSeries);
                }
                else
                {
                    macroTimeSeries = dataset.getSeries(macroGUIName);
                }
                
                macroTimeSeries.add(mealTimeSeconds, (BigDecimal) totalMealTableData[macroColPos]);
            }
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
    
        Iterator<Map.Entry<String, Integer>> it = macronutrientsToCheckAndPos.entrySet().iterator();
    
        while(it.hasNext())
        {
            // ############################################
            // Create TimeSeries For Macros
            // ############################################
            Map.Entry<String, Integer> macroEntry = it.next();
            String macroName = macroEntry.getKey();
            Integer macroColPos = macroEntry.getValue();
            
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
            BigDecimal newMacroValue = mealManager.getTotalMealTable().get_ValueOnTable(0, macroColPos);

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
    public void windowClosedEvent()
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
    public void clear_And_Rebuild_Dataset()
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
        for (String macroName : macronutrientsToCheckAndPos.keySet())
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

