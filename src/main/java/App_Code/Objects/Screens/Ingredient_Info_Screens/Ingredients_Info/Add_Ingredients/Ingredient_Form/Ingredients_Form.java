package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Ingredient_Form;

import App_Code.Objects.Data_Objects.Storable_Ingredient_IDS.Ingredient_Type_ID_Obj;
import App_Code.Objects.Data_Objects.Storable_Ingredient_IDS.Measurement_ID_OBJ;
import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.Shared_Data_Registry;
import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Gui_Objects.IconPanel;
import App_Code.Objects.Gui_Objects.Field_JComboBox;
import App_Code.Objects.Gui_Objects.Field_JTxtField;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Parent_Forms_OBJ;
import org.javatuples.Pair;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

public class Ingredients_Form extends Parent_Forms_OBJ
{
    //##################################################
    // Variables
    //##################################################
    
    // Objects
    protected Shared_Data_Registry sharedDataRegistry;
    protected MyJDBC db;
    
    protected ArrayList<String> salt_Values_AL = new ArrayList<>(Arrays.asList("mg", "g"));
    protected Field_JComboBox<String> salt_JC = new Field_JComboBox<>("Salt", salt_Values_AL);
    
    // Integers
    protected int charLimit = 8;
    
    //##############################
    // Collections
    //#############################
    protected ArrayList<Ingredient_Type_ID_Obj> ingredient_Types_Obj_AL;
    protected ArrayList<Measurement_ID_OBJ> ingredient_Measurement_Obj_AL;
    
    //############
    // Maps
    //############
    protected LinkedHashMap<String, Field_Binding<?>> field_Items_Map;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Ingredients_Form(Container parentContainer, MyJDBC db, Shared_Data_Registry sharedDataRegistry, String btn_Txt)
    {
        // Super Constructor
        super(parentContainer, btn_Txt);
        
        // Objects
        this.db = db;
        this.sharedDataRegistry = sharedDataRegistry;
        
        // Collections
        ingredient_Types_Obj_AL = sharedDataRegistry.get_Ingredient_Types();
        ingredient_Measurement_Obj_AL = sharedDataRegistry.get_Ingredient_Measurement_Obj_AL();
        
        create_Field_Items_Map(); // Create Map, as values were needed from above ^^
        
        // GUI Setup
        create_Ingredients_Form();
        collapsibleJPanel.expand_JPanel();
    }
    
    private void create_Field_Items_Map()
    {
        field_Items_Map = new LinkedHashMap<>()
        {{
            put("measurement", new Field_Binding<>(
                    "Ingredient Measurement In",                                                   // GUI Label
                    new Field_JComboBox<>("Ingredient Measurement In", ingredient_Measurement_Obj_AL), // Component
                    "measurement_id",                                       // MySQL Field
                    Integer.class,                                          // Field Type
                    "serving_unit"                                          // NutritionIX Field
            ));
            
            put("name", new Field_Binding<>(
                    "Ingredient Name",                                                       // GUI Label
                    new Field_JTxtField("Ingredient Name", 255, false),  // Component
                    "ingredient_name",                                                                // MySQL Field
                    String.class,                                                                    // Field Type
                    "food_name"                                                                   // NutritionIX Field
            ));
            
            put("type", new Field_Binding<>(
                    "Ingredient Type",                                             // GUI Label
                    new Field_JComboBox<>("Ingredient Type", ingredient_Types_Obj_AL), // Component
                    "ingredient_type_id",                                                    // MySQL Field
                    Integer.class,                                                           // Field Type
                    null                                                                     // NutritionIX Field
            ));
            
            put("quantity", new Field_Binding<>(
                    "Based On Quantity",                                                   // GUI Label
                                                                                                   // Component
                    new Field_JTxtField("Based On Quantity", charLimit, true, false),
                    "based_on_quantity",                                                // MySQL Field
                    BigDecimal.class,                                                              // Field Type
                    "serving_weight_grams"                                                        // NutritionIX Field
            ));
            
            put("gi", new Field_Binding<>(
                    "Glycemic Index",                                                  // GUI Label
                    new Field_JTxtField("Glycemic Index", charLimit, true),  // Component
                    "glycemic_index",                                                           // MySQL Field
                    BigDecimal.class,                                                           // Field Type
                    null                                                                      // NutritionIX Field
            ));
            
            put("protein", new Field_Binding<>(
                    "Protein",                                                  // GUI Label
                    new Field_JTxtField("Protein", charLimit, true),  // Component
                    "protein",                                                            // MySQL Field
                    BigDecimal.class,                                                     // Field Type
                    "nf_protein"                                                         // NutritionIX Field
            ));
            
            put("carbohydrates", new Field_Binding<>(
                    "Carbohydrates",                                                   // GUI Label
                    new Field_JTxtField("Carbohydrates", charLimit, true),  // Component
                    "carbohydrates",                                                           // MySQL Field
                    BigDecimal.class,                                                          // Field Type
                    "nf_total_carbohydrate"                                                    // NutritionIX Field
            ));
            
            put("sugars_of_carbs", new Field_Binding<>(
                    "Sugars Of Carbs",                                                  // GUI Label
                    new Field_JTxtField("Sugars Of Carbs", charLimit, true),  // Component
                    "sugars_of_carbs",                                                           // MySQL Field
                    BigDecimal.class,                                                           // Field Type
                    "nf_sugars"                                                                 // NutritionIX Field
            ));
            
            put("fibre", new Field_Binding<>(
                    "Fibre",                                                   // GUI Label
                    new Field_JTxtField("Fibre", charLimit, true),  // Component
                    "fibre",                                                            // MySQL Field
                    BigDecimal.class,                                                   // Field Type
                    "nf_dietary_fiber"                                                  // NutritionIX Field
            ));
            
            put("fat", new Field_Binding<>(
                    "Fat",                                                        // GUI Label
                    new Field_JTxtField("Fat", charLimit, true),       // Component
                    "fat",                                                                // MySQL Field
                    BigDecimal.class,                                                     // Field Type
                    "nf_total_fat"                                                        // NutritionIX Field
            ));
            
            put("sat_fat", new Field_Binding<>(
                    "Saturated Fat",                                                  // GUI Label
                    new Field_JTxtField("Saturated Fat", charLimit, true),  // Component
                    "saturated_fat",                                                           // MySQL Field
                    BigDecimal.class,                                                         // Field Type
                    "nf_saturated_fat"                                                        // NutritionIX Field
            ));
            
            put("salt", new Field_Binding<>(
                    "Salt",                                                   // GUI Label
                    new Field_JTxtField("Salt", charLimit, true),  // Component
                    "salt",                                                             // MySQL Field
                    BigDecimal.class,                                                  // Field Type
                    "nf_sodium"                                                        // NutritionIX Field
            ));
            
            put("water", new Field_Binding<>(
                    "Water Content",                                                  // GUI Label
                    new Field_JTxtField("Water Content", charLimit, true),  // Component
                    "water_content",                                                           // MySQL Field
                    BigDecimal.class,                                                          // Field Type
                    null                                                                       // NutritionIX Field
            ));
            
            put("liquid", new Field_Binding<>(
                    "Liquid Content",                                                 // GUI Label
                    new Field_JTxtField("Liquid Content", charLimit, true), // Component
                    "liquid_content",                                                          // MySQL Field
                    BigDecimal.class,                                                         // Field Type
                    null                                                                      // NutritionIX Field
            ));
            
            put("calories", new Field_Binding<>(
                    "Calories",                                                  // GUI Label
                    new Field_JTxtField("Calories", charLimit, true),  // Component
                    "calories",                                                          // MySQL Field
                    BigDecimal.class,                                                    // Field Type
                    "nf_calories"                                                       // NutritionIX Field
            ));
        }};
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    // IconBar
    protected void create_Icon_Bar()
    {
        //#####################################################
        // Creating area for North JPanel (Refresh Icon)
        //#####################################################
        
        JPanel iconArea = new JPanel(new GridBagLayout());
        add_To_Container(northPanel, iconArea, 0, 1, 1, 1, 0.25, 0.25, "both", 0, 0, null);
        
        IconPanel iconPanel = new IconPanel(1, 10, "East");
        JPanel iconPanelInsert = iconPanel.getIconJpanel();
        
        add_To_Container(iconArea, iconPanel.getIconAreaPanel(), 0, 0, 1, 1, 0.25, 0.25, "horizontal", 10, 0, null);
        
        //##########################
        // Refresh Icon
        //##########################
        int width = 30;
        int height = 30;
        
        IconButton refresh_Icon_Btn = new IconButton("/images/refresh/++refresh.png", width, height, width, height, "centre", "right"); // btn text is useless here , refactor
        
        JButton refresh_Btn = refresh_Icon_Btn.returnJButton();
        refresh_Icon_Btn.makeBTntransparent();
        
        refresh_Btn.addActionListener(ae -> {
            
            clear_Ingredients_Form();
        });
        
        iconPanelInsert.add(refresh_Icon_Btn);
    }
    
    //##############################
    // Create GUI Methods
    //##############################
    private void create_Ingredients_Form()
    {
        JPanel mainJPanel = collapsibleJPanel.get_Centre_JPanel();
        mainJPanel.setLayout(new BorderLayout());
        
        //###################################################################
        // North Frame
        //###################################################################
        
        // Creating North JPanel Area with 2 rows
        mainJPanel.add(northPanel, BorderLayout.NORTH);
        
        //#####################################################
        // Creating area for North JPanel (title area)
        //#####################################################
        JLabel titleLabel = new JLabel("Add Ingredient");
        titleLabel.setFont(new Font("Verdana", Font.PLAIN, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Color.green);
        titlePanel.add(titleLabel);
        
        // Add title JPanel to North Panel Area
        add_To_Container(northPanel, titlePanel, 0, 2, 1, 1, 0.25, 0.25, "both", 0, 0, null);
        
        //#################################################################
        // Create Icon Bar
        //#################################################################
        create_Icon_Bar();
        
        //#################################################################
        // Centre Frame
        //#################################################################
        JPanel inputArea = new JPanel(new GridBagLayout());
        
        for (Map.Entry<String, Field_Binding<?>> field_Item : field_Items_Map.entrySet())
        {
            //#######################################
            // Variables
            //#######################################
            String key = field_Item.getKey();
            Field_Binding<?> field_Binding = field_Item.getValue();
            
            Component component = field_Binding.get_Gui_Component();
            
            int xPos = 0, yPos = get_And_Increase_YPos();
            
            //#######################################
            // Set Label
            //#######################################
            JLabel label = new JLabel(String.format("    %s : ", field_Binding.get_Gui_Label()));
            label.setHorizontalAlignment(JLabel.LEFT);
            label.setFont(new Font("Verdana", Font.BOLD, 14));
            
            add_To_Container(inputArea, label, xPos, yPos, 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
            
            if (key.equals("salt"))
            {
                JPanel salt_JPanel_Area = new JPanel(new GridLayout(2, 1));
                add_To_Container(inputArea, salt_JPanel_Area, xPos += 1, yPos, 1, 1, 0.25, 0.25, "both", 0, 0, null); // Adding JPanel to GUI
                
                salt_JPanel_Area.add(salt_JC); // Add Salt JComboBox
                salt_JPanel_Area.add(component); // Add Salt JTextField
                continue;
            }
            
            //#######################################
            // Add Component
            //#######################################
            add_To_Container(inputArea, component, xPos += 1, yPos, 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
        }
        
        mainJPanel.add(inputArea, BorderLayout.CENTER);
    }
    
    //##############################
    // Load JComboBox
    //##############################
    public void load_Ingredients_Type_JComboBox()
    {
        Component object = field_Items_Map.get("type").get_Gui_Component();
        
        if (! (object instanceof Field_JComboBox<?> jComboBox)) { return; }
        
        jComboBox.reset_JC();
    }
    
    public void load_Ingredients_Measurements_JComboBox()
    {
        Component object = field_Items_Map.get("measurement").get_Gui_Component();
        
        if (! (object instanceof Field_JComboBox<?> jComboBox)) { return; }
        
        jComboBox.reset_JC();
    }
    
    //#########################################################
    // Clear GUI Methods
    //########################################################
    public void clear_Ingredients_Form()
    {
        // Reset All Component Items (JTextField / JComboBox)
        for (Map.Entry<String, Field_Binding<?>> field_Item : field_Items_Map.entrySet())
        {
            Component component = field_Item.getValue().get_Gui_Component();
            
            if (component instanceof Field_JComboBox<?> jComboBox)
            {
                jComboBox.reset_JC();
            }
            else if (component instanceof Field_JTxtField jTxtField)
            {
                jTxtField.reset_Txt_Field();
            }
        }
        
        // Reset Salt JComboBox
        salt_JC.reset_JC();
    }
    
    //#######################################################
    // Get DATA From API Methods
    //#######################################################
    public void update_Form_With_Nutrition_IX_Search(LinkedHashMap<String, Object> foodInfo)
    {
        /*if (foodInfo != null)
        {
            clear_Ingredients_Form();
            
            //#######################################################################
            // Set Form Values to 0 in case no value match below
            //#######################################################################
            
            System.out.println("\n\nupdate_Form_WithNutritionIXSearch()");
            for (Map.Entry<String, Object[]> info : ingredientsFormObjectAndValues.entrySet())
            {
                String key = info.getKey();
                Object[] row = info.getValue();
                Component comp = (Component) row[0];
                
                if (comp instanceof JTextField)
                {
                    JTextField obj = (JTextField) comp;
                    
                    if (obj.getText().equals(""))
                    {
                        obj.setText("0");
                    }
                }
            }
            
            //#######################################################################
            // Set values in form based on equivalent labels form to nutritionIx
            //#######################################################################
            int formLabelPos = - 1;
            for (Map.Entry<String, Triplet<String, String, String>> info : ingredientsFormLabelsMapsToValues.entrySet())
            {
                // Get NutritionIx Label & its value
                String formLabelName = info.getKey();
                Triplet<String, String, String> keyObject = info.getValue();
                
                //############################
                //
                //############################
                String foodInfoEquivalentLabel = keyObject.getValue0();
                if (foodInfoEquivalentLabel == null)
                {
                    continue;
                }
                
                //############################
                //
                //############################
                Object foodInfoNutritionValue = foodInfo.get(foodInfoEquivalentLabel);
                
                System.out.printf("\n\n#############################\n\nNutritionIx Label: %s  = %s \nIngredient Form Label pos: %s | Ingredient Form Label: %s  ",
                        foodInfoEquivalentLabel, foodInfoNutritionValue, formLabelName, formLabelPos);
                
                if (foodInfoNutritionValue == null || foodInfoNutritionValue.toString().equals("null"))
                {
                    continue;
                }
                
                //############################
                //
                //############################
                // HELLO MAY NEED TO CONSIDER ALL DATA TYPE CONVERSIONS
                Component comp = (Component) ingredientsFormObjectAndValues.get(formLabelName)[0];
                
                if (comp instanceof JTextField)
                {
                    JTextField obj = (JTextField) comp;
                    
                    if (foodInfoNutritionValue instanceof BigDecimal)
                    {
                        BigDecimal bd1 = ((BigDecimal) foodInfoNutritionValue).setScale(2, RoundingMode.HALF_DOWN);
                        
                        System.out.printf("\nDouble Value %s", bd1);
                        
                        obj.setText(String.format("%s", bd1));
                        
                        continue;
                    }
                    
                    obj.setText(String.format("%s", foodInfoNutritionValue));
                }
                else if (comp instanceof JComboBox)
                {
                    if (formLabelName.equals("Ingredient Measurement In"))
                    {
                        foodInfoNutritionValue = foodInfoNutritionValue.toString().equals("g") ? "Grams" : "Litres";
                    }
                    
                    JComboBox obj = (JComboBox) comp;
                    obj.setSelectedItem(foodInfoNutritionValue);
                }
            }
        }*/
    }
    
    //#######################################################
    // Validate Form
    //#######################################################
    public boolean validate_Ingredients_Form()
    {
        LinkedHashMap<String, ArrayList<String>> error_Map = new LinkedHashMap<>();
        
        // Get Error MSGs from Components
        for (Field_Binding<?> field_Binding : field_Items_Map.values())
        {
            switch (field_Binding.get_Gui_Component())
            {
                case Field_JComboBox<?> jc -> jc.validation_Check(error_Map);
                case Field_JTxtField jt -> jt.validation_Check(error_Map);
                default -> throw new IllegalStateException("Unexpected value: " + field_Binding.get_Gui_Component());
            }
        }
        
        // IF no errors returns True
        if (error_Map.isEmpty()) { return true; }
        
        // Build Error MSGS
        StringBuilder error_MSG = new StringBuilder();
        
        for (Map.Entry<String, ArrayList<String>> error_MSGs : error_Map.entrySet())
        {
            error_MSG.append("\n");
            
            for (String error : error_MSGs.getValue())
            {
                error_MSG.append(String.format("\n%s", error));
            }
        }
        
        // Display Errors
        UIManager.put("OptionPane.messageFont", new Font("Arial", Font.BOLD, 16));
        JOptionPane.showMessageDialog(null, error_MSG);
        
        // Outputs
        return false;
    }
    
    protected boolean check_IF_IngredientName_In_DB(String ingredientName)
    {
        //##################################
        // Create Query
        //####################################
        ingredientName = remove_Space_And_Hidden_Chars(ingredientName);
        String
                errorMSG = "Error, checking if Ingredient is in DB!",
                query = "SELECT ingredient_id FROM ingredients_info WHERE Ingredient_Name = ?;";
        
        Object[] params = new Object[]{ ingredientName };
        
        return db.get_Single_Col_Query_String(query, params, errorMSG) == null;
    }
    
    protected String remove_Space_And_Hidden_Chars(String stringToBeEdited)
    {
        return stringToBeEdited.trim().replaceAll("\\p{C}", ""); // remove all whitespace & hidden characters like \n
    }
    
    //#######################################################
    // Update Methods
    //#######################################################
    public String add_Updates(LinkedHashSet<Pair<String, Object[]>> queries_And_Params)
    {
        return null;
    }
}