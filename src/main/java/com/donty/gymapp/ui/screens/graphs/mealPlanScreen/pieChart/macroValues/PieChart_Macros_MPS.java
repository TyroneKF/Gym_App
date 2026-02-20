package com.donty.gymapp.ui.screens.graphs.mealPlanScreen.pieChart.macroValues;

import com.donty.gymapp.domain.enums.db_enums.columnNames.views.totalmeal.Draft_Gui_Total_Meal_Macro_Columns;
import com.donty.gymapp.ui.components.meal.MealManager;
import com.donty.gymapp.persistence.Shared_Data_Registry;
import com.donty.gymapp.gui.base.Screen_JPanel;
import org.jfree.data.general.DefaultPieDataset;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.*;

public class PieChart_Macros_MPS extends Screen_JPanel
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private Shared_Data_Registry shared_Data_Registry;
    
    // Graph Preferences
    
    private int
            col = 2,
            pieWidth = (frameWidth / col) - 50,
            pieHeight = 730,
            rotateDelay = 200; //580
    private Font
            titleFont = new Font("Serif", Font.PLAIN, 27),
            labelFont = new Font("SansSerif", Font.BOLD, 22),
            legendFont = new Font("Serif", Font.PLAIN, 21);
    
    //##############################################
    // Colors
    //##############################################
    Color[] colors = {
            new Color(0xFF3366), // 1 - pink red
            new Color(0x33CC33), // 2 - green
            new Color(0x3399FF), // 3 - sky blue
            new Color(0xFFCC33), // 4 - yellow
            new Color(0x9933FF), // 5 - violet
            new Color(0xFF6600), // 6 - orange
            new Color(0x00CCCC), // 7 - teal
            new Color(0xFF3399), // 8 - magenta
            new Color(0x66FF66), // 9 - bright lime
            new Color(0x3366FF), // 10 - vivid blue
            new Color(0xFF9933), // 11 - warm orange
            new Color(0xCC33FF), // 12 - purple
            new Color(0xFF3333), // 13 - bright red
            new Color(0x33FFCC), // 14 - aqua mint
            new Color(0xFF66CC), // 15 - pink
            new Color(0x99FF33), // 16 - lime
            new Color(0x0099FF), // 17 - blue cyan
            new Color(0xFFCC00), // 18 - golden
            new Color(0x9900FF), // 19 - deep violet
            new Color(0xFF6633), // 20 - coral orange
            new Color(0x00FF99), // 21 - mint green
            new Color(0xFF99FF), // 22 - light pink
            new Color(0x66CCFF), // 23 - soft sky blue
            new Color(0xCCFF33), // 24 - chartreuse
            new Color(0xFF0066), // 25 - fuchsia red
            new Color(0x33FF66), // 26 - neon green
            new Color(0x0066FF), // 27 - bright blue
            new Color(0xFFCC66), // 28 - light orange
            new Color(0xCC33CC), // 29 - magenta purple
            new Color(0xFF5050), // 30 - tomato
            new Color(0x33FFFF), // 31 - cyan
            new Color(0xFF6699), // 32 - rose pink
            new Color(0x99FF66), // 33 - fresh lime
            new Color(0x3366CC), // 34 - medium blue
            new Color(0xFFB266), // 35 - orange brown
            new Color(0x9933CC), // 36 - purple plum
            new Color(0xFF0000), // 37 - pure red
            new Color(0x00FFCC), // 38 - turquoise
            new Color(0xFF66FF), // 39 - pink magenta
            new Color(0x66FF33), // 40 - green lime
            new Color(0x3399CC), // 41 - light azure
            new Color(0xFFD633), // 42 - saffron
            new Color(0xCC00FF), // 43 - intense violet
            new Color(0xFF704D), // 44 - salmon
            new Color(0x00FF66), // 45 - neon green
            new Color(0xFF99CC), // 46 - pastel pink
            new Color(0x6699FF), // 47 - medium blue
            new Color(0xFFFF66), // 48 - lemon
            new Color(0xCC66FF), // 49 - lilac
            new Color(0xFF3300)  // 50 - bright red-orange
    };
    
    //##############################################
    // Collections
    //##############################################
    /**
     * HashMap<String, HashMap<MealManager, BigDecimal>> mealManagers_TotalMeal_MacroValues = new HashMap<>();
     * Stores all the mealManagers TotalMealValues in collections by the macroName
     * <p>
     * <Key: MacroName | Value: HashMap <Key: MealManager, Value:  Quantity>>
     * Etc;  <Key: Salt | Value: HashMap<MealManager, Quantity: 300g >>
     */
    private final LinkedHashMap<Draft_Gui_Total_Meal_Macro_Columns, HashMap<MealManager, BigDecimal>> totals_by_macro;
    
    private LinkedHashMap<Draft_Gui_Total_Meal_Macro_Columns, DefaultPieDataset<PieChart_MacroKey>> macroValue_Dataset_Map = new LinkedHashMap<>();
    
    private LinkedHashMap<Draft_Gui_Total_Meal_Macro_Columns, String> total_meal_macro_symbol;
    
    //##################################################################################################################
    // Constructors
    //##################################################################################################################
    public PieChart_Macros_MPS(Shared_Data_Registry shared_Data_Registry)
    {
        super(null, true);
        
        // ################################################################
        // Variables
        // ################################################################
        this.shared_Data_Registry = shared_Data_Registry;
        
        // #####################################
        // Collections
        // ######################################
        totals_by_macro = shared_Data_Registry.get_Meal_Managers_Macro_Values();
        total_meal_macro_symbol = shared_Data_Registry.get_Total_Meal_Macro_Symbols();
        
        // #####################################
        // Create GUI
        // ######################################
        create_And_Draw_GUI();
    }
    
    // #################################################################################################################
    //  Update / Draw GUI Methods
    // #################################################################################################################
    private void create_And_Draw_GUI()
    {
        // ################################################################
        // Clean & Build / Clear
        // ################################################################
        int rows = get_RowsCount();
        
        get_ScrollPane_JPanel().removeAll();
        get_ScrollPane_JPanel().setLayout(new GridLayout(rows, col));
        
        macroValue_Dataset_Map.clear(); // Clear Storage
        
        // ################################################################
        // Generate Color Palette
        // ################################################################
        Color[] colorPalette = generate_Random_Palette_Order();
        
        // ################################################################
        // Build DATA
        // ################################################################
        /**
         *   <Key: Salt | Value: HashMap<MealManager, Quantity: 300g >>
         */
        /**
         * LinkedHashMap<String, Pair<Integer, String>> totalMeal_macroColNamePos
         * LinkedHashMap<TotalMeal_MacroName, Pair< Position, Measurement>> totalMeal_macroColNamePos
         */
        for (Draft_Gui_Total_Meal_Macro_Columns macro_enum :  totals_by_macro.keySet())
        {
            //##################################
            // Macro Info
            //##################################
            String macroName = String.format("%s", format_Strings(macro_enum.key(), true));
            
            /**
             * LinkedHashMap<String, Pair<Integer, String>> totalMeal_macroColNamePos
             * LinkedHashMap<TotalMeal_MacroName, Pair< Position, Measurement>> totalMeal_macroColNamePos
             */
            String macroSymbol = total_meal_macro_symbol.get(macro_enum);
            
            //##################################
            // Get PieChart DATA & Add to List
            //##################################
            DefaultPieDataset<PieChart_MacroKey> pieDataset = create_Macro_PieChart_Dataset(macro_enum);
            macroValue_Dataset_Map.put(macro_enum, pieDataset); // Put Data into memory
            
            PieChart_Macros pieChart = new PieChart_Macros(
                    macroName,
                    macroSymbol,
                    colorPalette,
                    pieWidth,
                    pieHeight,
                    rotateDelay,
                    titleFont,
                    labelFont,
                    legendFont,
                    macroValue_Dataset_Map.get(macro_enum)
            );
            
            //##################################
            // Add PieChart to GUI
            //##################################
            JPanel x = new JPanel(new GridBagLayout());
            get_ScrollPane_JPanel().add(x);
            
            add_To_Container(x, pieChart, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 10, 10, null);
            
            add_To_Container(x, create_Space_Divider(20, 50, Color.WHITE), 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);
        }
        
        //##################################
        // Re-paint GUI
        //##################################
        resize_GUI();
    }
    
    private DefaultPieDataset<PieChart_MacroKey> create_Macro_PieChart_Dataset(Draft_Gui_Total_Meal_Macro_Columns macro_name)
    {
        //##############################################
        // Macro Info
        //##############################################
        /**
         *   <Key: MealManager, Value: Quantity >>
         */
        Map<MealManager, BigDecimal> macroValues = totals_by_macro.get(macro_name);
        
        /**
         * LinkedHashMap<String, Pair<Integer, String>> totalMeal_macroColNamePos
         * LinkedHashMap<TotalMeal_MacroName, Pair< Position, Measurement>> totalMeal_macroColNamePos
         */
        String macro_symbol = total_meal_macro_symbol.get(macro_name);
        
        //##############################################
        //  Sort Data & Create Dataset
        //##############################################
        DefaultPieDataset<PieChart_MacroKey> macroDataset = new DefaultPieDataset<>();
        
        /**
         * Sort List by time values
         * <Key: MealManager, Value: Quantity >>
         */
        macroValues
                .entrySet()
                .stream()
                .sorted(Comparator.comparing(e -> e.getKey().get_Current_Meal_Time()))
                .forEachOrdered(totalMeal_Values ->
                {
                    MealManager mealManager = totalMeal_Values.getKey();
                    BigDecimal macroValue = totalMeal_Values.getValue();
                    
                    macroDataset.setValue(new PieChart_MacroKey(mealManager, macro_name, macro_symbol), macroValue);
                });
        
        //##############################################
        // Return Values
        //##############################################
        return macroDataset;
    }
    
    public void update_DATA()
    {
        // ############################################################################
        // Paint GUI
        // ############################################################################
        for (Map.Entry<Draft_Gui_Total_Meal_Macro_Columns, DefaultPieDataset<PieChart_MacroKey>> pie_entry : macroValue_Dataset_Map.entrySet())
        {
            // #########################################
            // Get Info
            // #########################################
            Draft_Gui_Total_Meal_Macro_Columns macro_Name = pie_entry.getKey();
            DefaultPieDataset<PieChart_MacroKey> pieDataset = pie_entry.getValue();
            
            // #########################################
            // Get Updated Data
            // #########################################
            DefaultPieDataset<PieChart_MacroKey> updated_Dataset = create_Macro_PieChart_Dataset(macro_Name);
            
            // #########################################
            // Transfer Data Over into PieChart Dataset
            // #########################################
            // Stop PieDataset event listener from being triggered on each key update and instead on batch (avoids key races)
            pieDataset.setNotify(false);
            
            pieDataset.clear(); // Clear
            updated_Dataset.getKeys().forEach(key -> {
                pieDataset.setValue(key, updated_Dataset.getValue(key));
            });
            
            // Turn Notifications back on
            pieDataset.setNotify(true);
        }
        
        // ############################################################################
        // Resize GUI
        // ############################################################################
        resize_GUI();
    }
    
    // #################################################################################################################
    //  Methods
    // #################################################################################################################
    private Color[] generate_Random_Palette_Order()
    {
        // ################################################################
        // Generate Color Palette
        // ################################################################
        Random randomIntGenerator = new Random();
        
        // Generate a random integer between 0 (inclusive) and 100 (exclusive)
        int randomStart = randomIntGenerator.nextInt(colors.length);
        int colorSize = colors.length;
        
        Color[] output = new Color[colorSize];
        
        // Generate x amount of colors to match meals count
        for (int i = 0; i < colorSize; i++)
        {
            output[i] = colors[randomStart % colorSize];
            randomStart += 1;
        }
        
        // ################################################################
        // Output
        // ################################################################
        return output;
    }
    
    private int get_RowsCount()
    {
        return (int) Math.ceil((double) totals_by_macro.size() / col);
    }
}
