package App_Code.Objects.Screens.Graph_Screens.PieChart_Meal_Plan_Screen.Macro_Values;

import App_Code.Objects.Database_Objects.MealManager;
import App_Code.Objects.Database_Objects.MealManagerRegistry;
import App_Code.Objects.Gui_Objects.Screens.Screen_JPanel;
import App_Code.Objects.Screens.Meal_Plan_Screen;
import org.javatuples.Pair;
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
    private Meal_Plan_Screen meal_plan_screen;
    private MealManagerRegistry mealManagerRegistry;
    
    // Graph Preferences
    
    private int
            col = 2,
            pieWidth = (frameWidth / col) - 30,
            pieHeight = 730,
            rotateDelay = 200; //580
    private Font
            titleFont = new Font("Serif", Font.PLAIN, 27),
            labelFont = new Font("SansSerif", Font.BOLD, 22),
            legendFont = new Font("Serif", Font.PLAIN, 21);
    
    private String planName;
    
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
    private LinkedHashMap<String, HashMap<MealManager, BigDecimal>> mealManagers_TotalMeal_MacroValues;
    
    private LinkedHashMap<String, DefaultPieDataset<MacroKey>> macroValue_Dataset_Map = new LinkedHashMap<>();
    
    /**
     * LinkedHashMap<String, Pair<Integer, String>> totalMeal_macroColNamePos
     * LinkedHashMap<TotalMeal_MacroName, Pair< Position, Measurement>> totalMeal_macroColNamePos
     */
    private LinkedHashMap<String, Pair<Integer, String>> totalMeal_Macro_Pos_And_Symbol;
    
    //##################################################################################################################
    // Constructors
    //##################################################################################################################
    public PieChart_Macros_MPS(Meal_Plan_Screen meal_plan_screen, int frameWidth, int frameHeight)
    {
        super(null, true, frameWidth, frameHeight);
        
        // ################################################################
        // Variables
        // ################################################################
        // Objects
        this.meal_plan_screen = meal_plan_screen;
        this.mealManagerRegistry = meal_plan_screen.get_MealManagerRegistry();
        
        this.planName = meal_plan_screen.getPlanName();
        
        // #####################################
        /// Collections
        // ######################################
        mealManagers_TotalMeal_MacroValues = mealManagerRegistry.get_MealManagers_MacroValues();
        totalMeal_Macro_Pos_And_Symbol = mealManagerRegistry.get_TotalMeal_Macro_Pos_And_Symbol();
        
        // #####################################
        /// Create GUI
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
    
        getScrollPaneJPanel().removeAll();
        getScrollPaneJPanel().setLayout(new GridLayout(rows, col));
    
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
        Iterator<String> it = mealManagers_TotalMeal_MacroValues.keySet().iterator();
        while (it.hasNext())
        {
            //##################################
            // Macro Info
            //##################################
            String macroKey = it.next();
            String macroName = String.format("%s", formatStrings(macroKey, true));
    
            /**
             * LinkedHashMap<String, Pair<Integer, String>> totalMeal_macroColNamePos
             * LinkedHashMap<TotalMeal_MacroName, Pair< Position, Measurement>> totalMeal_macroColNamePos
             */
            String macroSymbol = totalMeal_Macro_Pos_And_Symbol.get(macroKey).getValue1();
            
            //##################################
            // Get PieChart DATA & Add to List
            //##################################
            DefaultPieDataset<MacroKey> pieDataset = create_Macro_PieChart_Dataset(macroKey);
            macroValue_Dataset_Map.put(macroKey, pieDataset); // Put Data into memory
        
            Pie_Chart_Macros pieChart = new Pie_Chart_Macros(macroName, macroSymbol, colorPalette, pieWidth, pieHeight, rotateDelay, titleFont,
                    labelFont, legendFont, macroValue_Dataset_Map.get(macroKey));
        
            //##################################
            // Add PieChart to GUI
            //##################################
            JPanel x = new JPanel(new GridBagLayout());
            getScrollPaneJPanel().add(x);
        
            addToContainer(x, pieChart, 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "both", 10, 10, null);
        
            addToContainer(x, createSpaceDivider(20, 50), 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);
        }
    
        //##################################
        // Re-paint GUI
        ///##################################
        resizeGUI();
    }
    
    private DefaultPieDataset<MacroKey> create_Macro_PieChart_Dataset(String macroName)
    {
        //##############################################
        // Macro Info
        //##############################################
        /**
         *   <Key: MealManager, Value: Quantity >>
         */
        Map<MealManager, BigDecimal> macroValues = mealManagers_TotalMeal_MacroValues.get(macroName);
    
        /**
         * LinkedHashMap<String, Pair<Integer, String>> totalMeal_macroColNamePos
         * LinkedHashMap<TotalMeal_MacroName, Pair< Position, Measurement>> totalMeal_macroColNamePos
         */
        String macroSymbol = totalMeal_Macro_Pos_And_Symbol.get(macroName).getValue1();
        
        //##############################################
        //  Sort Data & Create Dataset
        //##############################################
        DefaultPieDataset<MacroKey> macroDataset = new DefaultPieDataset<>();
        
        /**
         * Sort List by time values
         * <Key: MealManager, Value: Quantity >>
         */
        macroValues.entrySet().stream()
                .sorted(Comparator.comparingLong(e -> e.getKey().getCurrentMealTime().getFirstMillisecond()))
                .forEachOrdered(totalMeal_Values ->
                {
                    MealManager mealManager = totalMeal_Values.getKey();
                    BigDecimal macroValue = totalMeal_Values.getValue();
                   
                    macroDataset.setValue(new MacroKey(mealManager, macroName, macroSymbol), macroValue);
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
        Iterator<Map.Entry<String, DefaultPieDataset<MacroKey>>> it = macroValue_Dataset_Map.entrySet().iterator();
        while (it.hasNext())
        {
            // #########################################
            // Get Info
            // #########################################
            Map.Entry<String, DefaultPieDataset<MacroKey>> pieEntry = it.next();
            
            String macroName = pieEntry.getKey();
            DefaultPieDataset<MacroKey> pieDataset = pieEntry.getValue();
            
            // #########################################
            // Get Updated Data
            // #########################################
            DefaultPieDataset<MacroKey> updated_Dataset = create_Macro_PieChart_Dataset(macroName);
            
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
        resizeGUI();
    }
    
    // #################################################################################################################
    //  Methods
    // #################################################################################################################
    public Color[] generate_Random_Palette_Order()
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
        return (int) Math.ceil((double) mealManagers_TotalMeal_MacroValues.size() / col);
    }
  
}
