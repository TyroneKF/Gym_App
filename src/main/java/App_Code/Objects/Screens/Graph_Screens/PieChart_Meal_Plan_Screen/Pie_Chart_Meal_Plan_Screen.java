package App_Code.Objects.Screens.Graph_Screens.PieChart_Meal_Plan_Screen;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.MealManager;
import App_Code.Objects.Database_Objects.MealManagerRegistry;
import App_Code.Objects.Graph_Objects.Pie_Chart;
import App_Code.Objects.Gui_Objects.Screen;
import App_Code.Objects.Screens.Meal_Plan_Screen;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.*;

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
    private MealManagerRegistry mealManagerRegistry;
    
    //##############################################
    // Collections
    //##############################################
    private TreeSet<Map.Entry<Integer, PieChart_Entry_MPS>> pieChart_MPS_Entries;
    
    // #################################################################################################################
    // Constructor
    // #################################################################################################################
    public Pie_Chart_Meal_Plan_Screen(MyJDBC db, Meal_Plan_Screen meal_plan_screen)
    {
        // ################################################################
        // Super Constructor
        // ################################################################
        super(db, true, "Pie Chart: Plan Macros", 1935, 1200, 0, 0);
        
        getScrollPaneJPanel().setBackground(Color.WHITE);
        setResizable(true);
        
        // ################################################################
        // Variables
        // ################################################################
        this.meal_plan_screen = meal_plan_screen;
        this.planName = meal_plan_screen.getPlanName();
        this.mealManagerRegistry = meal_plan_screen.get_MealManagerRegistry();
        
        //#####################################
        // Graph Preferences
        //#####################################
        int
                col = 3,
                pieWidth = (frameWidth / col) -20, // 750
                pieHeight = 500,
                rotateDelay = 200; //580
        
        Font
                titleFont = new Font("Serif", Font.PLAIN, 27),
                labelFont = new Font("SansSerif", Font.BOLD, 22),
                legendFont = new Font("Serif", Font.PLAIN, 20);
        
        // #####################################
        /// Collections
        // ######################################
        TreeSet<Map.Entry<Integer, MealManager>> mealManagerTreeSet = mealManagerRegistry.get_MealManagerTreeSet();
        pieChart_MPS_Entries = mealManagerRegistry.get_PieChart_MPS_Entries();
        
        int rows = (int) mealManagerTreeSet.size() / col;
        getScrollPaneJPanel().setLayout(new GridLayout(col, rows));
        // ################################################################
        // Build DATA
        // ################################################################
        Iterator<Map.Entry<Integer, MealManager>> it = mealManagerTreeSet.iterator();
        while (it.hasNext())
        {
            //##############################
            // Get Info
            //##############################
            Map.Entry<Integer, MealManager> entry = it.next();
            
            MealManager mealManager = entry.getValue();
            Integer mealPlanID = entry.getKey();
            
            //##############################
            // Get / Create PieChart Data
            //##############################
            DefaultPieDataset<String> pieDataset = mealManagerRegistry.create_MM_MacroInfo_PieChart(mealPlanID);
            
            //##############################
            // Create PieChart & Add to List
            //##############################
            String title = String.format("[%s]      %s Macros", mealManager.getCurrentMealTimeGUI(), mealManager.getCurrentMealName());
            
            Pie_Chart pieChart = new Pie_Chart(title, pieWidth, pieHeight, rotateDelay, titleFont, labelFont, legendFont, pieDataset);
            pieChart_MPS_Entries.add(Map.entry(mealPlanID, new PieChart_Entry_MPS(mealPlanID, mealManager, pieChart)));
            
            //##############################
            // Add PieChart to GUI
            //##############################
            JPanel x = new JPanel(new GridBagLayout());
            addToContainer(x, pieChart, 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "both", 10, 10, null);
            
            addToContainer(x, createSpaceDivider(20, 50), 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);
            getScrollPaneJPanel().add(x);
        }
        
        
        // ################################################################
        // Make Frame Visible
        // ################################################################
        setFrameVisibility(true);
    }
    
    // #################################################################################################################
    // Methods
    // #################################################################################################################
    @Override
    public void windowClosedEvent() { meal_plan_screen.removePieChartScreen(); closeJFrame(); }
    
    public void add_PieChart_To_GUI(Integer mealInPlanID)
    {
    
    }
    
    // #################################################################################################################
    // Accessor Methods
    // #################################################################################################################
    public void update_PieChart_MealName(Integer mealInPlanID)
    {
       mealManagerRegistry.get_PieChart_MPS(mealInPlanID).update_PieChart_Title();
    }
}
