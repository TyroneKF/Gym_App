package App_Code.Objects.Screens.Graph_Screens.PieChart_Meal_Plan_Screen.Total_Meals;

import App_Code.Objects.Database_Objects.MealManager;
import App_Code.Objects.Database_Objects.MealManagerRegistry;
import App_Code.Objects.Graph_Objects.Pie_Chart;
import App_Code.Objects.Gui_Objects.Screens.Screen_JPanel;
import App_Code.Objects.Screens.Meal_Plan_Screen;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class PieChart_TotalMeal_Macros_MPS extends Screen_JPanel
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
    private ArrayList<PieChart_Entry_MPS> pieChart_MPS_Entries = new ArrayList<>();
    private ArrayList<MealManager> mealManager_ArrayList;
    
    // #################################################################################################################
    // Constructor
    // #################################################################################################################
    public PieChart_TotalMeal_Macros_MPS(Meal_Plan_Screen meal_plan_screen, int frameWidth, int frameHeight)
    {
        // ################################################################
        // Super
        // ################################################################
        super(null, true, frameWidth, frameHeight);
        
        setVisible(true);
        
        // ################################################################
        // Variables
        // ################################################################
        this.meal_plan_screen = meal_plan_screen;
        this.mealManagerRegistry = meal_plan_screen.get_MealManagerRegistry();
        
        // #####################################
        /// Collections
        // ######################################
        mealManager_ArrayList = mealManagerRegistry.get_MealManager_ArrayList();
        
        // #####################################
        /// Create GUI
        // ######################################
        create_And_Draw_GUI();
    }
    
    // #################################################################################################################
    //  Update / Draw GUI Methods
    // #################################################################################################################
    private int get_PieChart_Count()
    {
        return (int) mealManager_ArrayList.stream().filter(mealManager -> ! mealManager.is_Meal_Deleted()).count();
    }
    
    private void sort_PieChartEntry_AL()
    {
        pieChart_MPS_Entries.sort((a, b) -> a.get_MealTime().compareTo(b.get_MealTime()));
    }
    
    public void create_And_Draw_GUI()
    {
        // ################################################################
        // Clean & Build
        // ################################################################
        int rows = (int) Math.ceil((double) get_PieChart_Count() / col);
        
        getScrollPaneJPanel().removeAll();
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
            DefaultPieDataset<String> pieDataset = mealManagerRegistry.get_OR_Create_PieChart_Dataset(mealManager);
            
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
            getScrollPaneJPanel().add(x);
            
            addToContainer(x, pieChart, 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "both", 10, 10, null);
            
            addToContainer(x, createSpaceDivider(20, 50), 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);
            
        }
    
        //##############################
        // Re-paint GUI
        //##############################
        resizeGUI();
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
        // Sort List by MealTime
        // ####################################################
        sort_PieChartEntry_AL();
        
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
    
    public void clear()
    {
        //#####################################
        // Clear Collection Data
        //#####################################
        pieChart_MPS_Entries.clear();
    
        //#####################################
        // Clear GUI
        //#####################################
        getScrollPaneJPanel().removeAll();
    }
    
    // #################################################################################################################
    //  Methods
    // #################################################################################################################
    public void update_PieChart_MealName(int mealInPlanID)
    {
        Iterator<PieChart_Entry_MPS> it = pieChart_MPS_Entries.iterator();
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
        DefaultPieDataset<String> pieDataset = mealManagerRegistry.get_OR_Create_PieChart_Dataset(mealManager);
        
        //##############################
        // Create PieChart & Add to List
        //##############################
        String title = String.format("[%s]      %s Macros", mealManager.getCurrentMealTimeGUI(), mealManager.getCurrentMealName());
        
        Pie_Chart pieChart = new Pie_Chart(title, pieWidth, pieHeight, rotateDelay, titleFont, labelFont, legendFont, pieDataset);
        pieChart_MPS_Entries.add(new PieChart_Entry_MPS(mealPlanID, mealManager, pieChart));
        
        //##############################
        // Sort Meals in Pie MPS
        //##############################
        sort_PieChartEntry_AL();
        
        //##############################
        // Redraw GUI
        //##############################
        redraw_GUI();
    }
    
    public void delete_MealManager(MealManager mealManager)
    {
        //############################################
        // Remove from PieChart Screen Objects
        //############################################
        pieChart_MPS_Entries.removeIf(e -> e.get_MealInPlanID() == mealManager.getMealInPlanID());
    
        //############################################
        // Re-Draw GUI
        //############################################
        redraw_GUI();
    }
    
    // #################################################################################################################
    // Accessor Methods
    // #################################################################################################################
    public ArrayList<PieChart_Entry_MPS> get_PieChart_Entry_MPS()
    {
        return pieChart_MPS_Entries;
    }
}
