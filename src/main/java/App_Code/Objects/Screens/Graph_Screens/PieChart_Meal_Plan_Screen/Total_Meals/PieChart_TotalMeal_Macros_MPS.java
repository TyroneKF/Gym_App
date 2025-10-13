package App_Code.Objects.Screens.Graph_Screens.PieChart_Meal_Plan_Screen.Total_Meals;

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

public class PieChart_TotalMeal_Macros_MPS extends Screen
{
    // #################################################################################################################
    // Variables
    // #################################################################################################################
    // Graph Preferences
    
    private int
            col = 3,
            pieWidth = (frameWidth / col) - 30,
            pieHeight = 500,
            rotateDelay = 200; //580
    private Font
            titleFont = new Font("Serif", Font.PLAIN, 27),
            labelFont = new Font("SansSerif", Font.BOLD, 22),
            legendFont = new Font("Serif", Font.PLAIN, 20);
    
    
    //##############################################
    // Objects
    //##############################################
    private Meal_Plan_Screen meal_plan_screen;
    private MealManagerRegistry mealManagerRegistry;
    
    //##############################################
    // Collections
    //##############################################
    private ArrayList<PieChart_Entry_MPS> pieChart_MPS_Entries;
    private ArrayList<MealManager> mealManager_ArrayList;
    
    // #################################################################################################################
    // Constructor
    // #################################################################################################################
    public PieChart_TotalMeal_Macros_MPS(MyJDBC db, Meal_Plan_Screen meal_plan_screen)
    {
        // ################################################################
        // Super Constructor
        // ################################################################
        super(db, true, String.format(" %s Pie Chart: Plan Macros", meal_plan_screen.getPlanName()), 1935, 1200, 0, 0);
        
        getScrollPaneJPanel().setBackground(Color.WHITE);
        setResizable(true);
        
        // ################################################################
        // Variables
        // ################################################################
        this.meal_plan_screen = meal_plan_screen;
        this.mealManagerRegistry = meal_plan_screen.get_MealManagerRegistry();
        
        // #####################################
        /// Collections
        // ######################################
        mealManager_ArrayList = mealManagerRegistry.get_MealManager_ArrayList();
        pieChart_MPS_Entries = mealManagerRegistry.get_PieChart_MPS_Entries();
        
        // #####################################
        /// Collections
        // ######################################
        create_And_Draw_GUI();
        
        // ################################################################
        // Make Frame Visible
        // ################################################################
        setFrameVisibility(true);
    }
    
    @Override
    public void windowClosedEvent()
    {
        // ####################################
        // Remove Attachment to MealPlanScreen
        // ####################################
        meal_plan_screen.removePieChartScreen();
        
        // ####################################
        // Remove GUI DATA
        // ####################################
        mealManagerRegistry.remove_Unused_PieData();
        
        // ####################################
        // Close JFrame
        // ####################################
        closeJFrame();
    }
    
    private int get_PieChart_Count()
    {
        return (int) mealManager_ArrayList.stream().filter(mealManager -> ! mealManager.is_Meal_Deleted()).count();
    }
    
    // #################################################################################################################
    //  Update / Draw GUI Methods
    // #################################################################################################################
    public void create_And_Draw_GUI()
    {
        // ################################################################
        // Clean & Build
        // ################################################################
        getScrollPaneJPanel().removeAll();
        
        int rows = (int) Math.ceil((double) get_PieChart_Count() / col);
        getScrollPaneJPanel().setLayout(new GridLayout(rows, col));
        
        // ################################################################
        // Build DATA
        // ################################################################
        Iterator<MealManager> it = mealManager_ArrayList.iterator();
        while (it.hasNext())
        {
            //##############################
            // Get Info
            //##############################
            MealManager mealManager = it.next();
            Integer mealPlanID = mealManager.getMealInPlanID();
    
            //##############################
            // IF Meal is Deleted: Continue
            //##############################
            if (mealManager.is_Meal_Deleted()) { continue; }
            
            //##############################
            // Get / Create PieChart Data
            //##############################
            DefaultPieDataset<String> pieDataset = mealManagerRegistry.get_OR_Create_PieChart_Dataset(mealPlanID);
            
            //##############################
            // Create PieChart & Add to List
            //##############################
            String title = String.format("[%s]      %s Macros", mealManager.getCurrentMealTimeGUI(), mealManager.getCurrentMealName());
            
            Pie_Chart pieChart = new Pie_Chart(title, pieWidth, pieHeight, rotateDelay, titleFont, labelFont, legendFont, pieDataset);
            pieChart_MPS_Entries.add(new PieChart_Entry_MPS(mealPlanID, mealManager, pieChart));
            
            //##############################
            // Add PieChart to GUI
            //##############################
            JPanel x = new JPanel(new GridBagLayout());
            addToContainer(x, pieChart, 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "both", 10, 10, null);
            
            addToContainer(x, createSpaceDivider(20, 50), 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);
            getScrollPaneJPanel().add(x);
            
            //##############################
            // Re-paint GUI
            //##############################
            resizeGUI();
        }
    }
    
    public void redraw_GUI()
    {
        // ####################################################
        // Clear GUI
        // ####################################################
        getScrollPaneJPanel().removeAll();
        
        int rows = (int) Math.ceil((double) pieChart_MPS_Entries.size() / col);
        getScrollPaneJPanel().setLayout(new GridLayout(rows, col));
        
        // ####################################################
        // Paint GUI
        // ####################################################
        Iterator<PieChart_Entry_MPS> it = pieChart_MPS_Entries.iterator();
        while (it.hasNext())
        {
            //##############################
            // GET Pie_Entry Object
            //##############################
            Pie_Chart pieChart = it.next().get_PieChart();
            
            //##############################
            // Add PieChart to GUI
            //##############################
            JPanel x = new JPanel(new GridBagLayout());
            addToContainer(x, pieChart, 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "both", 10, 10, null);
            
            addToContainer(x, createSpaceDivider(20, 50), 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);
            getScrollPaneJPanel().add(x);
        }
        
        resizeGUI();
    }
    
    public void update_PieChart_MealName(int mealInPlanID)
    {
        ArrayList<PieChart_Entry_MPS> pieChartEntry_MPS_AL = mealManagerRegistry.get_PieChart_MPS_Entries();
        
        Iterator<PieChart_Entry_MPS> it = pieChartEntry_MPS_AL.iterator();
        while (it.hasNext())
        {
            PieChart_Entry_MPS pieChart_Entry = it.next();
            
            if (pieChart_Entry.get_MealInPlanID() == mealInPlanID) { pieChart_Entry.update_PieChart_Title(); break; }
        }
    }
    
    public void add_MealManager_To_GUI(MealManager mealManager)
    {
        //##############################
        // Get / Create PieChart Data
        //##############################
        Integer mealPlanID = mealManager.getMealInPlanID();
        DefaultPieDataset<String> pieDataset = mealManagerRegistry.get_OR_Create_PieChart_Dataset(mealPlanID);
        
        //##############################
        // Create PieChart & Add to List
        //##############################
        String title = String.format("[%s]      %s Macros", mealManager.getCurrentMealTimeGUI(), mealManager.getCurrentMealName());
        
        Pie_Chart pieChart = new Pie_Chart(title, pieWidth, pieHeight, rotateDelay, titleFont, labelFont, legendFont, pieDataset);
        pieChart_MPS_Entries.add(new PieChart_Entry_MPS(mealPlanID, mealManager, pieChart));
        
        //Set MSG
        //##############################
        // Sort Meals in Pie MPS
        //##############################
        mealManagerRegistry.sort_PieChartEntry_AL();
        
        //##############################
        // Redraw GUI
        //##############################
        redraw_GUI();
    }
}
