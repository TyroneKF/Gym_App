package com.donty.gymapp.ui.screens.graphs.PieChart_MealManager_Screen;

import com.donty.gymapp.persistence.database.MyJDBC_Sqlite;
import com.donty.gymapp.domain.enums.table_enums.totalmeal.Total_Meal_Macro_Columns;
import com.donty.gymapp.ui.components.meal.MealManager;
import com.donty.gymapp.persistence.Shared_Data_Registry;
import com.donty.gymapp.gui.base.Screen_JFrame;
import com.donty.gymapp.ui.screens.graphs.PieChart_Meal_Plan_Screen.Total_Meals.Pie_Chart_Totals;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.HorizontalAlignment;
import org.jfree.data.general.DefaultPieDataset;
import java.awt.*;
import java.util.Random;

public class Pie_Chart_Meal_Manager_Screen extends Screen_JFrame
{
    //###########################################################################################
    // Variables
    //###########################################################################################
    
    
    //#####################################
    // Integers
    //#####################################
    private int frameWidth = 800;
    private int frameHeight = 600;
    
    //######################################
    // Objects
    //######################################
    private final MealManager mealManager;
    private final Pie_Chart_Totals pieChart;
    
    //######################################
    // Colors
    //######################################
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
    
    //###########################################################################################
    // Constructor
    //###########################################################################################
    public Pie_Chart_Meal_Manager_Screen
    (
            MyJDBC_Sqlite db,
            Shared_Data_Registry shared_Data_Registry,
            MealManager mealManager
    )
    {
        // ##########################################
        // Super Constructors & Variables
        // ##########################################
        super(db, false, "Pie Chart : Macronutrients ", 850, 700, 0, 0);
        
        getScrollPaneJPanel().setBackground(Color.WHITE);
        set_Resizable(false);
        
        // ##########################################
        // Variables
        // ##########################################
        this.mealManager = mealManager;
        
        //############################################
        // Creating Macros / Dataset
        //############################################
        DefaultPieDataset<Total_Meal_Macro_Columns> dataset = shared_Data_Registry.get_OR_Create_Updated_PieChart_Dataset(mealManager);
        
        //#####################################
        // Graph Preferences
        //#####################################
        int
                pieWidth = frameWidth - 50,
                pieHeight = frameHeight - 20;
        
        Font
                titleFont = new Font("Serif", Font.PLAIN, 27),
                labelFont = new Font("SansSerif", Font.BOLD, 22),
                legendFont = new Font("Serif", Font.PLAIN, 23);
        
        // ################################################################
        // Generate Color Palette
        // ################################################################
        // Generate a random integer between 0 (inclusive) and 100 (exclusive)
        int randomInRange = randomIntGenerator.nextInt(colors.length);
        Color[] colorPalette = colors[randomInRange];
        
        //#####################################
        // Create PieChart
        //#####################################
        pieChart = new Pie_Chart_Totals(
                mealManager,
                shared_Data_Registry,
                colorPalette,
                pieWidth,
                pieHeight,
                150,
                titleFont,
                labelFont,
                legendFont,
                dataset
        )
        {
            @Override
            protected String getSpace()
            {
                return "\u00A0\u00A0\u00A0";
            }
            
            @Override
            protected void set_Horizontal_LegendAlignment()
            {
                LegendTitle legend = plot.getChart().getLegend();
                legend.setPosition(org.jfree.chart.ui.RectangleEdge.BOTTOM);
                legend.setHorizontalAlignment(HorizontalAlignment.CENTER);
            }
        };
        
        addToContainer(getScrollPaneJPanel(), pieChart, 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
        
        //#####################################
        // Frame
        //#####################################
        setFrameVisibility(true);
    }
    
    //###########################################################################################
    // Methods
    //###########################################################################################
    @Override
    public void window_Closed_Event() { mealManager.remove_Pie_Chart_Screen(); closeJFrame(); }
    
    //####################################
    // Update Methods
    //####################################
    public void update_Pie_Chart_Title()
    {
        pieChart.update_PieChart_Title();
    }
}
