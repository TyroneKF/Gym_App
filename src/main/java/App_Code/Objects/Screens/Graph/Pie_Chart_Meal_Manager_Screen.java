package App_Code.Objects.Screens.Graph;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.TotalMealTable;
import App_Code.Objects.Database_Objects.MealManager;
import App_Code.Objects.Gui_Objects.Screen;
import App_Code.Objects.Graph_Objects.Pie_Chart;
import App_Code.Objects.Screens.Meal_Plan_Screen;
import org.javatuples.Pair;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Map;

public class Pie_Chart_Meal_Manager_Screen extends Screen
{
    private Map<String, Pair<BigDecimal, String>> macros;
    private MealManager mealManager;
    private TotalMealTable totalMealTable;
    private Meal_Plan_Screen meal_plan_screen;
    private Pie_Chart pieChart;
    private String meal_name;

    private int
            frameWidth = 800,
            frameHeight = 600;

    public Pie_Chart_Meal_Manager_Screen(MyJDBC db, MealManager mealManager)
    {
        // ##########################################
        // Super Constructors & Variables
        // ##########################################
        super(db, "Pie Chart : Macronutrients ", 800, 600, 0, 0);

        getScrollPaneJPanel().setBackground(Color.WHITE);
        setResizable(false);

        // ##########################################
        // Variables
        // ##########################################
        this.mealManager = mealManager;

        this.meal_name = mealManager.getCurrentMealName();
        this.totalMealTable = mealManager.getTotalMealTable();
        this.meal_plan_screen = mealManager.getMeal_plan_screen();

        //############################################
        // Creating Macros / Dataset
        //############################################
        if(! updateDataSet()) { getFrame().dispose(); return; }

        // ##########################################
        // Create Graph Object & Adding to GUI
        // ##########################################
        pieChart = new Pie_Chart(String.format("%s Macros", meal_name), frameWidth-50, frameHeight-60, macros);
        addToContainer(getScrollPaneJPanel(), pieChart, 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);

        setFrameVisibility(true);
    }

    @Override
    public void windowClosedEvent()
    {
        mealManager.removePieChartScreen();
    }

    public void makeVisible()
    {
        super.makeJFrameVisible();
    }

    public void update_PieChart_Title()
    {
        meal_name = mealManager.getCurrentMealName();
        pieChart.setTitle(meal_name);
    }

    public void update_pieChart()
    {
        if(updateDataSet()) pieChart.update_dataset(macros);
    }

    private boolean updateDataSet()
    {
        try
        {
            BigDecimal proteinValue = totalMealTable.getValueOnTable("Total_Protein");
            BigDecimal carbsValue = totalMealTable.getValueOnTable("Total_Carbohydrates");
            BigDecimal fatsValue = totalMealTable.getValueOnTable("Total_Fats");

            System.out.printf("\n\nPie_Chart_Meal_Manager_Screen.java : updateDataSet() \n\nProtein : %s \nCarbs : %s \nFats : %s", proteinValue.toString(),carbsValue.toString(), fatsValue.toString());
            if (proteinValue == null || carbsValue == null || fatsValue == null) throw new Exception("null values returned");

            // Get Macros Results on Table
            macros = Map.ofEntries(
                    Map.entry("Protein", new Pair<>(proteinValue, "g")),
                    Map.entry("Carbohydrates", new Pair<>(carbsValue, "g")),
                    Map.entry("Fats", new Pair<>(fatsValue, "g"))
            );

            return  true;
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(meal_plan_screen.getFrame(), "Error, creating Pie Chart with column table names!");
            System.err.printf("\n\nPie_Chart.java : Constructor \n%s", e);

            return false;
        }
    }
}
