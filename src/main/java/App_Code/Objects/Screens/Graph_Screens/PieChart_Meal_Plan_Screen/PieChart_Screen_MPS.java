package App_Code.Objects.Screens.Graph_Screens.PieChart_Meal_Plan_Screen;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.MealManagerRegistry;
import App_Code.Objects.Gui_Objects.Screens.Screen_JFrame;
import App_Code.Objects.Screens.Graph_Screens.PieChart_Meal_Plan_Screen.Total_Meals.PieChart_TotalMeal_Macros_MPS;
import App_Code.Objects.Screens.Meal_Plan_Screen;

import javax.swing.*;
import java.awt.*;

public class PieChart_Screen_MPS extends Screen_JFrame
{
    //#################################################################################################################
    // Variables
    // #################################################################################################################
    private Meal_Plan_Screen meal_plan_screen;
    private MealManagerRegistry mealManagerRegistry;
    
    private PieChart_TotalMeal_Macros_MPS pieChart_Total_MPS;
    // #################################################################################################################
    // Constructor
    // #################################################################################################################
    public PieChart_Screen_MPS(MyJDBC db, Meal_Plan_Screen meal_plan_screen)
    {
        // ################################################################
        // Super Constructor
        // ################################################################
        super(db, false, String.format(" %s Pie Chart: Plan Macros", meal_plan_screen.getPlanName()), 1935, 1200, 0, 0);
    
        getScrollPaneJPanel().setBackground(Color.WHITE);
        set_Resizable(true);
    
        // ################################################################
        // Variables
        // ################################################################
        this.meal_plan_screen = meal_plan_screen;
        this.mealManagerRegistry = meal_plan_screen.get_MealManagerRegistry();
    
        //###################################################################################
        // Create ContentPane
        //###################################################################################
        contentPane.setLayout(new GridLayout(1, 1));
        contentPane.setVisible(true);
    
        //##################################################################################
        // Creating TabbedPane
        //##################################################################################
        JTabbedPane tp = new JTabbedPane();
        contentPane.add(tp);
    
        //#################################################
        // Creating Add Ingredients Screen_JFrame
        //#################################################
        JPanel addIngredientsFormJPanel = new JPanel(new GridBagLayout());
        tp.add("Add Ingredients", addIngredientsFormJPanel);
    
        /*pieChart_Total_MPS = new PieChart_TotalMeal_Macros_MPS (db, meal_plan_screen);
        addToContainer(addIngredientsFormJPanel, pieChart_Total_MPS, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0);
    
    */
    
    
        // ################################################################
        // Make Frame Visible
        // ################################################################
        setFrameVisibility(true);
    }
}
