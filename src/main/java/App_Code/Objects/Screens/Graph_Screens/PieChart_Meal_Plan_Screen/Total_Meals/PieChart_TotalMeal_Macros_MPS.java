package App_Code.Objects.Screens.Graph_Screens.PieChart_Meal_Plan_Screen.Total_Meals;

import App_Code.Objects.Database_Objects.MealManager;
import App_Code.Objects.Database_Objects.MealManagerRegistry;
import App_Code.Objects.Graph_Objects.Pie_Chart;
import App_Code.Objects.Gui_Objects.Screens.Screen_JPanel;
import App_Code.Objects.Screens.Meal_Plan_Screen;

import java.util.Random;

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
    
    Color[] colorPalette;
    
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
    
    //##############################################
    // Colors
    //##############################################
    private Random randomIntGenerator = new Random();
    private Color[][] colors = {
            {
                    new Color(0xFF33CC), // contrast - magenta
                    new Color(0x3399FF), // light blue
                    new Color(0x0033CC), // dark blue
                    new Color(0x33FF66), // light green
                    new Color(0x00CC33)  // dark green
            },
            {
                    new Color(0x99FF33), // contrast - lime
                    new Color(0xFF3333), // bright red
                    new Color(0xCC0000), // deep red
                    new Color(0xCC66FF), // lavender
                    new Color(0x9933FF)  // violet
            },
            {
                    new Color(0x00FFFF), // contrast - cyan
                    new Color(0xFF9933), // bright orange
                    new Color(0xFF6600), // deep orange
                    new Color(0xFF66CC), // bright pink
                    new Color(0xFF3399)  // hot pink
            },
            {
                    new Color(0xFFFF33), // contrast - yellow
                    new Color(0x3399FF), // sky blue
                    new Color(0x0033CC), // navy blue
                    new Color(0xCC66FF), // lavender
                    new Color(0x9933FF)  // deep purple
            },
            {
                    new Color(0xFF3333), // contrast - red
                    new Color(0x33FF57), // bright lime
                    new Color(0x00CC44), // forest green
                    new Color(0x3399FF), // bright blue
                    new Color(0x0033CC)  // royal blue
            },
            {
                    new Color(0xFF9900), // contrast - orange
                    new Color(0x00CCCC), // teal
                    new Color(0x009999), // dark teal
                    new Color(0xCC66FF), // light purple
                    new Color(0x9933FF)  // violet
            },
            {
                    new Color(0x3366FF), // contrast - vivid blue
                    new Color(0xFF3333), // bright red
                    new Color(0xCC0000), // crimson
                    new Color(0xFFFF66), // pale yellow
                    new Color(0xFFCC00)  // deep yellow
            },
            {
                    new Color(0x9933FF), // contrast - purple
                    new Color(0x3399FF), // bright blue
                    new Color(0x0033CC), // dark blue
                    new Color(0x66FF66), // bright green
                    new Color(0x33CC33)  // medium green
            },
            {
                    new Color(0xFF66CC), // contrast - pink
                    new Color(0x3399FF), // sky blue
                    new Color(0x0033CC), // royal blue
                    new Color(0xFF9933), // bright orange
                    new Color(0xFF6600)  // deep orange
            },
            {
                    new Color(0x33FF66), // contrast - bright green
                    new Color(0xFF3333), // bright red
                    new Color(0xCC0000), // deep red
                    new Color(0xCC66FF), // lavender
                    new Color(0x9933FF)  // violet
            },
            {
                    new Color(0x00CCCC), // contrast - teal
                    new Color(0xFF9933), // bright orange
                    new Color(0xFF6600), // deep orange
                    new Color(0x3399FF), // light blue
                    new Color(0x0033CC)  // dark blue
            },
            {
                    new Color(0xFFCC33), // contrast - gold
                    new Color(0x33FF57), // neon green
                    new Color(0x00CC44), // dark green
                    new Color(0xFF66CC), // bright pink
                    new Color(0xFF3399)  // hot pink
            }
    };
    
    
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
    private void sort_PieChartEntry_AL()
    {
        pieChart_MPS_Entries.sort((a, b) -> a.get_MealTime().compareTo(b.get_MealTime()));
    }
    
    public void create_And_Draw_GUI()
    {
        // ################################################################
        // Clean & Build
        // ################################################################
        int rows = (int) Math.ceil((double) mealManagerRegistry.get_Active_MealCount() / col);
        
        getScrollPaneJPanel().removeAll();
        getScrollPaneJPanel().setLayout(new GridLayout(rows, col));
        
        // ################################################################
        // Generate Color Palette
        // ################################################################
        // Generate a random integer between 0 (inclusive) and 100 (exclusive)
        int randomInRange = randomIntGenerator.nextInt(colors.length - 1);
        colorPalette = colors[randomInRange];
        
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
            
            Pie_Chart_Totals pieChart = new Pie_Chart_Totals(title, colorPalette, pieWidth, pieHeight, rotateDelay, titleFont, labelFont, legendFont, pieDataset);
            
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
        resetYPos();
        
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
        
        //#####################################
        // Reset GUI Graphics
        //#####################################
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
        
        //#####################################
        // Reset GUI Graphics
        //#####################################
        resizeGUI();
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
        
        Pie_Chart pieChart = new Pie_Chart(title, colorPalette, pieWidth, pieHeight, rotateDelay, titleFont, labelFont, legendFont, pieDataset);
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
        System.out.printf("\n\ndelete_MealManager() %s",
                pieChart_MPS_Entries.stream()
                        .anyMatch(m -> m.get_MealInPlanID() == mealManager.getMealInPlanID()));
        
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
