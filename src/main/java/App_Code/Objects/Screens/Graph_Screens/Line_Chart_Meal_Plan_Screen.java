package App_Code.Objects.Screens.Graph_Screens;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.MealManager;
import App_Code.Objects.Database_Objects.MealManagerRegistry;
import App_Code.Objects.Graph_Objects.Line_Chart;
import App_Code.Objects.Gui_Objects.Screen;
import App_Code.Objects.Screens.Meal_Plan_Screen;
import org.javatuples.Pair;
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
    //#################################################################################################################
    // Variables
    //#################################################################################################################
    private String planName;

    //##############################################
    // Objects
    //##############################################
    private Meal_Plan_Screen meal_plan_screen;
    private MealManagerRegistry mealManagerRegistry;

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
        this.mealManagerRegistry = meal_plan_screen.get_MealManagerRegistry();
        this.mealManagerTreeSet = meal_plan_screen.getMealManagerTreeSet();
        this.planName = meal_plan_screen.getPlanName();
        this.macronutrientsToCheckAndPos = meal_plan_screen.getTotalMeal_MacroColNamePos();

        //############################################
        // Creating Macros / Dataset
        //############################################
        createDataSet();

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
    public void createDataSet()
    {
        dataset = mealManagerRegistry.get_Plan_MacroValues_LineChart();
    }

    public void updateMealManagerDataChange(MealManager mealManager, Second previousTime, Second currentTime)
    {
        // ####################################################
        // Get MealManager Info
        // ####################################################
        Boolean timeChanged = ! previousTime.equals(currentTime);
   
        int mealInPlanID = mealManager.getMealInPlanID();
        // ####################################################
        // Get MealManager MacroInfo & Replace
        // ####################################################
    
        Iterator<Map.Entry<String, HashMap<Integer, Pair<Second, BigDecimal>>>> it
                = mealManagerRegistry.get_MealManagersMacroValues().entrySet().iterator();
    
        /**
         *  <Key: MacroName | Value: Map<Key: MealManagerID, Value: < MealTime, Quantity>>
         *   Put, Replace
         */
        
        while(it.hasNext())
        {
            // ############################################
            // Create TimeSeries For Macros
            // ############################################
            /**
             *   Map<Key: MealManagerID, Value: < MealTime, Quantity>>
             */
            Map.Entry<String, HashMap<Integer, Pair<Second, BigDecimal>>> macroEntry = it.next();
            String macroName = macroEntry.getKey();
            
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
            BigDecimal newMacroValue = macroEntry.getValue().get(mealInPlanID).getValue1();

            
            // #######################################
            // OLD Time: Replace With New Value
            // #######################################
            if (! timeChanged) // IF the time hasn't changed just update the series value with the old time
            {
                macroSeries.addOrUpdate(previousTime, newMacroValue); // update value
                continue;
            }

            // ########################################
            // New Time : Delete & Add Old / New Value
            // ########################################
            macroSeries.delete(previousTime); // IF the time has changed delete the old time from series value
            macroSeries.add(currentTime, newMacroValue); // Add new value to Series with new Time
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

    //##################################################
    // Conversion Methods
    //##################################################
    private String convertMacroNameToGuiVersion(String macroName)
    {
        // Reformat macroName for GUI purposes  "\u00A0" is like space because \t doesn't work in this label format

        //  return String.format("\u00A0\u00A0%s\u00A0\u00A0", macroName, true);

        return String.format("\u00A0\u00A0%s\u00A0\u00A0", formatStrings(macroName, true));
    }

    //##################################################
    //  Update  Methods
    //##################################################
    public void update_LineChart_Title()
    {
        planName = meal_plan_screen.getPlanName();
        line_chart.setTitle(planName);
    }
    
    //##################################################
    //  Clear Dataset Methods
    //##################################################
    public void clear_And_Rebuild_Dataset()
    {
        dataset = new TimeSeriesCollection();
        line_chart.getXY_Plot().setDataset(dataset);

        createDataSet();
    }

    /*
       Based on MealManagers time the data is deleted in the series collection
     */
    public void deleteMealManagerData(Second mealTime)
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
            macroSeries.delete(mealTime);
        }
    }
}

