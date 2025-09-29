package App_Code.Objects.Screens.Graph_Screens;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.MealManager;
import App_Code.Objects.Gui_Objects.Screen;
import App_Code.Objects.Screens.Meal_Plan_Screen;

import java.awt.*;
import java.util.Map;
import java.util.TreeSet;

public class Pie_Chart_Meal_Plan_Screen extends Screen
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
    

    // #################################################################################################################
    // Constructor
    // #################################################################################################################
    public Pie_Chart_Meal_Plan_Screen(MyJDBC db, Meal_Plan_Screen meal_plan_screen)
    {
        // ##########################################
        // Super Constructors & Variables
        // ##########################################
        super(db, true, "Pie Chart: Plan Macros", 1000, 900, 0, 0);
        getScrollPaneJPanel().setBackground(Color.WHITE);
        setResizable(true);

        // ##########################################
        // Variables
        // ##########################################
        this.meal_plan_screen = meal_plan_screen;
        this.mealManagerTreeSet = meal_plan_screen.getMealManagerTreeSet();
        this.planName = meal_plan_screen.getPlanName();
        this.macronutrientsToCheckAndPos = meal_plan_screen.getTotalMeal_MacroColNamePos();

        // ##########################################
        // Make Frame Visible
        // ##########################################
        setFrameVisibility(true);
    }

    @Override
    public void windowClosedEvent() { meal_plan_screen.removePieChartScreen(); }
}
