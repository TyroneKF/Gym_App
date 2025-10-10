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
    private ArrayList<PieChart_Entry_MPS> pieChartEntry_MPS_AL = new ArrayList<>();
    
    //#####################################
    // Graph Preferences
    //#####################################
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
        
        // #####################################
        /// Collections
        // ######################################
        ArrayList<MealManager> mealManager_ArrayList = mealManagerRegistry.get_MealManager_ArrayList();
        pieChart_MPS_Entries = mealManagerRegistry.get_PieChart_MPS_Entries();
        
        int rows = (int) mealManager_ArrayList.size() / col;
        getScrollPaneJPanel().setLayout(new GridLayout(col, rows));
        
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
        }
        
        
        // ################################################################
        // Make Frame Visible
        // ################################################################
        setFrameVisibility(true);
        resizeGUI();
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
        mealManagerRegistry.clear_PieChart_DATA_MPS();
        
        mealManagerRegistry.remove_Unused_PieData();
        
        // ####################################
        // Close JFrame
        // ####################################
        closeJFrame();
    }
    
    // #################################################################################################################
    //  Update / Draw GUI Methods
    // #################################################################################################################
    public void redraw_GUI()
    {
        // ####################################################
        // Clear GUI
        // ####################################################
        getScrollPaneJPanel().removeAll();
        
        int rows = (int) pieChart_MPS_Entries.size() / col;
        getScrollPaneJPanel().setLayout(new GridLayout(col, rows));
        
        // ####################################################
        // Paint GUI
        // ####################################################
        Iterator<PieChart_Entry_MPS> it = pieChart_MPS_Entries.iterator();
        while (it.hasNext())
        {
            //##############################
            // GET Pie_Entry Object
            //##############################
            Pie_Chart pieChart = it.next().getPieChart();
            
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
    
    public Pie_Chart add_MealManagerToGUI(MealManager mealManager)
    {
        Integer mealPlanID = mealManager.getMealInPlanID();
    
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
        
        return pieChart;
    }
}
