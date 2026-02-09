package com.donty.gymapp.ui.screens.graphs.PieChart_Meal_Plan_Screen.Total_Meals;

import com.donty.gymapp.domain.enums.table_enums.totalMeal.Total_Meal_Macro_Columns;
import com.donty.gymapp.ui.components.meal.MealManager;
import com.donty.gymapp.persistence.Shared_Data_Registry;
import com.donty.gymapp.ui.charts.Pie_Chart;
import com.donty.gymapp.gui.base.Screen_JPanel;
import java.util.Random;
import org.jfree.data.general.DefaultPieDataset;
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class PieChart_TotalMeals_MPS extends Screen_JPanel
{
    // #################################################################################################################
    // Variables
    // #################################################################################################################
    
    // Int
    private int mealCount;
    
    // Graph Preferences
    private int
            col = 3, desiredGridCount = 6,
            pieWidth = (frameWidth / col) - 50,
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
    private Shared_Data_Registry shared_Data_Registry;
    
    private JPanel screen = get_ScrollPane_JPanel();
    
    //##############################################
    // Collections
    //##############################################
    private ArrayList<PieChart_Totals_Entry_MPS> pieChart_MPS_Entries = new ArrayList<>();
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
    public PieChart_TotalMeals_MPS(Shared_Data_Registry shared_data_registry)
    {
        // ################################################################
        // Super
        // ################################################################
        super(null, true);
        setVisible(true);
        
        // ################################################################
        // Variables
        // ################################################################
        this.shared_Data_Registry = shared_data_registry;
        
        // #####################################
        // Collections
        // ######################################
        mealManager_ArrayList = shared_Data_Registry.get_MealManager_ArrayList();
        
        // #####################################
        // Create GUI
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
        // Set GridLayout
        // ################################################################
        screen.removeAll();
        set_GridLayout();
        
        // ################################################################
        // Generate Color Palette
        // ################################################################
        // Generate a random integer between 0 (inclusive) and 100 (exclusive)
        int randomInRange = randomIntGenerator.nextInt(colors.length - 1);
        colorPalette = colors[randomInRange];
        
        // ################################################################
        // Build DATA
        // ################################################################
        for (MealManager mealManager : mealManager_ArrayList)
        {
            // Get / Create PieChart Data
            DefaultPieDataset<Total_Meal_Macro_Columns> pieDataset = shared_Data_Registry.get_OR_Create_Updated_PieChart_Dataset(mealManager);
            
            Pie_Chart_Totals pieChart = new Pie_Chart_Totals( // Create PieChart
                    mealManager,
                    shared_Data_Registry,
                    colorPalette,
                    pieWidth,
                    pieHeight,
                    rotateDelay,
                    titleFont,
                    labelFont,
                    legendFont,
                    pieDataset
            );
            
            pieChart_MPS_Entries.add(new PieChart_Totals_Entry_MPS(mealManager, pieChart)); // Add  PieChart  to List
            
            //##############################
            // Add PieChart to GUI
            //##############################
            JPanel x = new JPanel(new GridBagLayout());
            screen.add(x);
            
            add_To_Container(x, pieChart, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 10, 10, null);
            
            add_To_Container(x, create_Space_Divider(20, 50, Color.WHITE), 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);
        }
        
        //##############################
        // Exception for 1 meal
        //##############################
        fill_GUI_Grid();
        
        //##############################
        // Re-paint GUI
        //##############################
        resize_GUI();
    }
    
    public void redraw_GUI()
    {
        // ####################################################
        // Reset GridLayout
        // ####################################################
        screen.removeAll();
        set_GridLayout();
        
        // ####################################################
        // Sort List by MealTime
        // ####################################################
        sort_PieChartEntry_AL();
        
        // ####################################################
        // Paint GUI
        // ####################################################
        for (PieChart_Totals_Entry_MPS pieChartMpsEntry : pieChart_MPS_Entries)
        {
            //##############################
            // GET Pie_Entry Object
            //##############################
            Pie_Chart pieChart = pieChartMpsEntry.get_PieChart();
            
            //##############################
            // Add PieChart to GUI
            //##############################
            JPanel x = new JPanel(new GridBagLayout());
            add_To_Container(x, pieChart, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 10, 10, null);
            
            add_To_Container(x, create_Space_Divider(20, 50, Color.WHITE), 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);
            screen.add(x);
        }
        
        //##############################
        // Exception for 1 meal
        //##############################
        fill_GUI_Grid();
        
        //#####################################
        // Reset GUI Graphics
        //#####################################
        resize_GUI();
    }
    
    public void set_GridLayout()
    {
        // ####################################################
        // Reset GUI
        // ####################################################
        reset_YPos();
        
        // ####################################################
        // Set GridLayout
        // ####################################################
        mealCount = mealManager_ArrayList.size();
        
        int calc = (int) Math.ceil((double) mealCount / col);
        int rows = Math.max(calc, 2); // returns the larger out of the 2
        
        screen.setLayout(new GridLayout(rows, col));
        
        System.out.printf("\n\nRows: %s | Col : %s \nMeals in GUI: %s", rows, col, mealCount);
    }
    
    public void fill_GUI_Grid()
    {
        //##############################
        // Exception for 1 meal
        //##############################
        /**
         *  If there's one meal in the plan the graph get distorted and stretched because its dragged across the screen
         *  The gui needs atleast 4 objects added to the gui panel to look good
         */
        //##############################
        // Add Blank White JPanel
        //##############################
        
        if (mealCount < desiredGridCount)
        {
            
            for (int i = 1; i <= (desiredGridCount - mealCount); i++)
            {
                JPanel blankJP = new JPanel();
                blankJP.setBackground(Color.WHITE);
                screen.add(blankJP);
            }
        }
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
        screen.removeAll();
        
        //#####################################
        // Reset GUI Graphics
        //#####################################
        resize_GUI();
    }
    
    // #################################################################################################################
    //  Methods
    // #################################################################################################################
    public void update_PieChart_Title(MealManager mealManager)
    {
        pieChart_MPS_Entries
                .stream()
                .filter(e -> e.get_MealManager() == mealManager)
                .findFirst()
                .ifPresent(PieChart_Totals_Entry_MPS :: update_PieChart_Title);
    }
    
    public void add_MealManager_To_GUI(MealManager mealManager)
    {
        // Get / Create PieChart Data
        DefaultPieDataset<Total_Meal_Macro_Columns> pieDataset = shared_Data_Registry.get_OR_Create_Updated_PieChart_Dataset(mealManager);
        
        // Create PieChart & Add to List
        Pie_Chart_Totals pieChart = new Pie_Chart_Totals(
                mealManager,
                shared_Data_Registry,
                colorPalette,
                pieWidth,
                pieHeight,
                rotateDelay,
                titleFont,
                labelFont,
                legendFont,
                pieDataset
        );
        
        pieChart_MPS_Entries.add(new PieChart_Totals_Entry_MPS(mealManager, pieChart));
    
        sort_PieChartEntry_AL();       // Sort Meals in Pie MPS

        redraw_GUI();   // Redraw GUI
    }
    
    public void delete_MealManager(MealManager mealManager)
    {
        System.out.printf("\n\ndelete_MealManager() %s",
                pieChart_MPS_Entries.stream()
                        .anyMatch(m -> m.get_MealManager().equals(mealManager)));
        
        // Remove from PieChart Screen Objects
        pieChart_MPS_Entries.removeIf(e -> e.get_MealManager().equals(mealManager));
        
   
        redraw_GUI();  // Re-Draw GUI
    }
    
    // #################################################################################################################
    // Accessor Methods
    // #################################################################################################################
    public ArrayList<PieChart_Totals_Entry_MPS> get_PieChart_Entry_MPS()
    {
        return pieChart_MPS_Entries;
    }
}
