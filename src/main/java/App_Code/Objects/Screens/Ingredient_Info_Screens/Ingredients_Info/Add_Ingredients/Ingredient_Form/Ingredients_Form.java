package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Ingredient_Form;

import App_Code.Objects.Data_Objects.Field_Bindings.Ingredients_Form_Binding;
import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Ingredient_Type_ID_Obj;
import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Measurement_ID_OBJ;
import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.Shared_Data_Registry;
import App_Code.Objects.Gui_Objects.Combo_Boxes.Field_JCombo_Storable_ID;
import App_Code.Objects.Gui_Objects.Combo_Boxes.Field_JComboBox;
import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Gui_Objects.IconPanel;
import App_Code.Objects.Gui_Objects.Text_Fields.Field_JTxtField;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Parent_Forms_OBJ;
import org.javatuples.Pair;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.*;

public class Ingredients_Form extends Parent_Forms_OBJ
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    // Integers
    protected int digit_Char_Limit = 8, text_Char_Limit = 255;
    
    //#############################
    // Objects
    //#############################
    protected Shared_Data_Registry sharedDataRegistry;
    protected MyJDBC db;
   
    protected JPanel northPanel = new JPanel(new GridBagLayout());
    
    // Salt JC Object
    protected ArrayList<String> salt_Values_AL = new ArrayList<>(Arrays.asList("mg", "g"));
    protected Field_JComboBox<String> salt_JC = new Field_JComboBox<>("Salt", String.class, salt_Values_AL);
    
    //#############################
    // Collections
    //#############################
    protected ArrayList<Ingredient_Type_ID_Obj> ingredient_Types_Obj_AL;
    protected ArrayList<Measurement_ID_OBJ> ingredient_Measurement_Obj_AL;
    
    //############
    // Maps
    //############
    protected LinkedHashMap<String, Ingredients_Form_Binding<?>> field_Items_Map;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Ingredients_Form(Container parentContainer, MyJDBC db, Shared_Data_Registry sharedDataRegistry, String btn_Txt)
    {
        //############################################
        // Super Constructor
        //############################################
        super(parentContainer, btn_Txt);
        
        //############################################
        // Variables
        //############################################
        this.db = db;
        this.sharedDataRegistry = sharedDataRegistry;
        
        // Collections
        ingredient_Types_Obj_AL = sharedDataRegistry.get_All_Ingredient_Types_AL();
        
        ingredient_Measurement_Obj_AL = sharedDataRegistry.get_Ingredient_Measurement_Obj_AL();
        ingredient_Measurement_Obj_AL.removeIf(e -> e.get_ID()== 3); // Remove N/A Measurement
        
        create_Field_Items_Map(); // Create Map, as values were needed from above ^^
        
        //############################################
        // Create GUI
        //############################################
        create_Ingredients_Form();
        collapsibleJPanel.expand_JPanel();
    }

    //##################################################################################################################
    // Methods
    //##################################################################################################################
    private void create_Field_Items_Map()
    {
        field_Items_Map = new LinkedHashMap<>()
        {{
            put("measurement", new Ingredients_Form_Binding<>(
                    "Ingredient Measurement In",                                                   // GUI Label
                    new Field_JCombo_Storable_ID<>("Ingredient Measurement In", Measurement_ID_OBJ.class, ingredient_Measurement_Obj_AL),
                    // Component
                    "measurement_id",                                       // MySQL Field
                    "serving_unit"                                          // NutritionIX Field
            ));
            
            put("name", new Ingredients_Form_Binding<>(
                    "Ingredient Name",                                                       // GUI Label
                    new Field_JTxtField("Ingredient Name", text_Char_Limit),  // Component
                    "ingredient_name",                                                                // MySQL Field
                    String.class,                                                                    // Field Type
                    "food_name"                                                                   // NutritionIX Field
            ));
            
            put("type", new Ingredients_Form_Binding<>(
                    "Ingredient Type",                                             // GUI Label
                    // Component
                    new Field_JCombo_Storable_ID<>("Ingredient Type", Ingredient_Type_ID_Obj.class, ingredient_Types_Obj_AL),
                    "ingredient_type_id"                                                   // MySQL Field
            ));
            
            put("quantity", new Ingredients_Form_Binding<>(
                    "Based On Quantity",                                                   // GUI Label
                    // Component
                    new Field_JTxtField("Based On Quantity", digit_Char_Limit, true, false),
                    "based_on_quantity",                                                // MySQL Field
                    BigDecimal.class,                                                              // Field Type
                    "serving_weight_grams"                                                        // NutritionIX Field
            ));
            
            put("gi", new Ingredients_Form_Binding<>(
                    "Glycemic Index",                                                  // GUI Label
                    new Field_JTxtField("Glycemic Index", digit_Char_Limit, true),  // Component
                    "glycemic_index",                                                           // MySQL Field
                    BigDecimal.class                                                          // Field Type
            ));
            
            put("protein", new Ingredients_Form_Binding<>(
                    "Protein",                                                  // GUI Label
                    new Field_JTxtField("Protein", digit_Char_Limit, true),  // Component
                    "protein",                                                            // MySQL Field
                    BigDecimal.class,                                                     // Field Type
                    "nf_protein"                                                         // NutritionIX Field
            ));
            
            put("carbohydrates", new Ingredients_Form_Binding<>(
                    "Carbohydrates",                                                   // GUI Label
                    new Field_JTxtField("Carbohydrates", digit_Char_Limit, true),  // Component
                    "carbohydrates",                                                           // MySQL Field
                    BigDecimal.class,                                                          // Field Type
                    "nf_total_carbohydrate"                                                    // NutritionIX Field
            ));
            
            put("sugars_of_carbs", new Ingredients_Form_Binding<>(
                    "Sugars Of Carbs",                                                  // GUI Label
                    new Field_JTxtField("Sugars Of Carbs", digit_Char_Limit, true),  // Component
                    "sugars_of_carbs",                                                           // MySQL Field
                    BigDecimal.class,                                                           // Field Type
                    "nf_sugars"                                                                 // NutritionIX Field
            ));
            
            put("fibre", new Ingredients_Form_Binding<>(
                    "Fibre",                                                   // GUI Label
                    new Field_JTxtField("Fibre", digit_Char_Limit, true),  // Component
                    "fibre",                                                            // MySQL Field
                    BigDecimal.class,                                                   // Field Type
                    "nf_dietary_fiber"                                                  // NutritionIX Field
            ));
            
            put("fat", new Ingredients_Form_Binding<>(
                    "Fat",                                                        // GUI Label
                    new Field_JTxtField("Fat", digit_Char_Limit, true),       // Component
                    "fat",                                                                // MySQL Field
                    BigDecimal.class,                                                     // Field Type
                    "nf_total_fat"                                                        // NutritionIX Field
            ));
            
            put("sat_fat", new Ingredients_Form_Binding<>(
                    "Saturated Fat",                                                  // GUI Label
                    new Field_JTxtField("Saturated Fat", digit_Char_Limit, true),  // Component
                    "saturated_fat",                                                           // MySQL Field
                    BigDecimal.class,                                                         // Field Type
                    "nf_saturated_fat"                                                        // NutritionIX Field
            ));
            
            put("salt", new Ingredients_Form_Binding<>(
                    "Salt",                                                   // GUI Label
                    new Field_JTxtField("Salt", digit_Char_Limit, true),  // Component
                    "salt",                                                             // MySQL Field
                    BigDecimal.class,                                                  // Field Type
                    "nf_sodium"                                                        // NutritionIX Field
            ));
            
            put("water", new Ingredients_Form_Binding<>(
                    "Water Content",                                                  // GUI Label
                    new Field_JTxtField("Water Content", digit_Char_Limit, true),  // Component
                    "water_content",                                                           // MySQL Field
                    BigDecimal.class                                                          // Field Type
            ));
            
            put("liquid", new Ingredients_Form_Binding<>(
                    "Liquid Content",                                                 // GUI Label
                    new Field_JTxtField("Liquid Content", digit_Char_Limit, true), // Component
                    "liquid_content",                                                          // MySQL Field
                    BigDecimal.class                                                        // Field Type
            ));
            
            put("calories", new Ingredients_Form_Binding<>(
                    "Calories",                                                  // GUI Label
                    new Field_JTxtField("Calories", digit_Char_Limit, true),  // Component
                    "calories",                                                          // MySQL Field
                    BigDecimal.class,                                                    // Field Type
                    "nf_calories"                                                       // NutritionIX Field
            ));
        }};
    }
    
    public void set_Salt_JC()
    {
        salt_JC.setSelectedItem("g");
    }
    
    //###########################################################
    // Create GUI Methods
    //###########################################################
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
        
        for (Map.Entry<String, Ingredients_Form_Binding<?>> field_Item : field_Items_Map.entrySet())
        {
            //#######################################
            // Variables
            //#######################################
            String key = field_Item.getKey();
            Ingredients_Form_Binding<?> Ingredients_Form_Binding = field_Item.getValue();
            
            Component component = Ingredients_Form_Binding.get_Gui_Component();
            
            int xPos = 0, yPos = get_And_Increase_YPos();
            
            //#######################################
            // Set Label
            //#######################################
            JLabel label = new JLabel(String.format("    %s : ", Ingredients_Form_Binding.get_Gui_Label()));
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
    // Load JComboBox
    //##############################
    public void reload_Ingredients_Type_JComboBox()
    {
        Component object = field_Items_Map.get("type").get_Gui_Component();
        
        if (! (object instanceof Field_JComboBox<?> jComboBox)) { return; }
        
        jComboBox.reset_JC();
    }
    
    public void reload_Ingredients_Measurements_JComboBox()
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
        for (Map.Entry<String, Ingredients_Form_Binding<?>> field_Item : field_Items_Map.entrySet())
        {
            Component component = field_Item.getValue().get_Gui_Component();
            
            if (component instanceof Field_JCombo_Storable_ID<?> jComboBox)
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
        
        //###############################
        // Get Error MSGs from Components
        //###############################
        for (Ingredients_Form_Binding<?> field_Binding : field_Items_Map.values())
        {
            switch (field_Binding.get_Gui_Component())
            {
                case Field_JComboBox<?> jc -> jc.validation_Check(error_Map);
                case Field_JTxtField jt -> jt.validation_Check(error_Map);
                default -> throw new IllegalStateException("Unexpected value: " + field_Binding.get_Gui_Component());
            }
        }
        
        //###############################
        // Check Ingredient Name In DB
        //###############################
        String label = "Ingredient Name";
        ArrayList<String> ingredient_Name_Errors = error_Map.getOrDefault(label, new ArrayList<>());  // Get Or Create an Empty list if not available
        
        if (is_Ingredient_Name_In_DB()) // IF ingredient Name already exists add error msg
        {
            ingredient_Name_Errors.add(String.format("'%s' : Already Exists in DB!", label));
            error_Map.put(label, ingredient_Name_Errors);
        }
        
        //###############################
        // IF no errors returns True
        //###############################
        if (error_Map.isEmpty()) { return true; }
        
        //###############################
        // Build Error MSGs
        //###############################
        /**
         * HTML:
         * &nbsp; = space
         * <br> = line break
         * <b></b> = bold
         */
        
        StringBuilder error_MSG_String = new StringBuilder("<html>");
        
        for (Map.Entry<String, ArrayList<String>> error_Entry : error_Map.entrySet())
        {
            ArrayList<String> error_MSGs = error_Entry.getValue();
            
            // Singular Error MSG
            if (error_MSGs.size() == 1)
            {
                error_MSG_String.append(String.format("<br><br><b>%s&nbsp;:&nbsp;</b> %s", error_Entry.getKey(), error_MSGs.getFirst()));
                continue;
            }
            
            // Multiple Error Messages
            error_MSG_String.append(String.format("<br><br><b>%s:</b>", error_Entry.getKey()));
            
            for (String error : error_MSGs)
            {
                error_MSG_String.append(String.format("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>.</b>&nbsp; %s", error));
            }
        }
        
        error_MSG_String.append("</html>");
        
        //###############################
        // Display Errors
        //###############################
        UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 16));
        JOptionPane.showMessageDialog(null, error_MSG_String, "Ingredients Form Error Messages", JOptionPane.INFORMATION_MESSAGE);
        
        //###############################
        // Outputs
        //###############################
        return false;
    }
    
    protected boolean is_Ingredient_Name_In_DB()
    {
        //##################################
        // IS Ingredient Name Null or Empty
        //####################################
        String ingredient_Name = ((Field_JTxtField) field_Items_Map.get("name").get_Gui_Component()).get_Text();
        
        if (ingredient_Name == null || ingredient_Name.isEmpty()) { return false; }
        
        //##################################
        // Create Query
        //####################################
        String
                errorMSG = "Error, checking if Ingredient is in DB!",
                query = "SELECT ingredient_id FROM ingredients_info WHERE Ingredient_Name = ?;";
        
        Object[] params = new Object[]{ ingredient_Name };
        
        //##################################
        // Execute
        //####################################
        return db.get_Single_Col_Query_Int(query, params, errorMSG) != null;
    }
    
    //#######################################################
    // Update Methods
    //#######################################################
    public void add_Update_Queries(LinkedHashSet<Pair<String, Object[]>> queries_And_Params) throws Exception
    {
        //##########################
        // Variables
        //##########################
        int size = field_Items_Map.size();
        
        StringBuilder
                insert_Header = new StringBuilder("INSERT INTO ingredients_info ("),
                values = new StringBuilder("(");
        
        Object[] params = new Object[size];
        
        //##########################
        // Create Update Query
        //##########################
        try
        {
            int pos = 0;
            for (Ingredients_Form_Binding<?> fb : field_Items_Map.values())
            {
                // Add to Header
                insert_Header.append(pos == size - 1
                        ? String.format("%s) VALUES ", fb.get_Mysql_Field_Name())
                        : String.format("%s,", fb.get_Mysql_Field_Name())
                );
                
                // Add to Values
                values.append(pos == size - 1 ? "?);" : "?,");
                
                // Add to params
                switch (fb.get_Gui_Component())
                {
                    case Field_JCombo_Storable_ID<?> jc -> params[pos] = jc.get_Selected_Item_ID();
                    case Field_JTxtField jt ->  // Get Text & Type cast to expected type
                    {
                        Class<?> type = fb.get_Field_Type();
                        
                        if (type == BigDecimal.class)  // IF type is BigDecimal Convert TXT to
                        {
                            params[pos] = new BigDecimal(jt.get_Text());
                        }
                        else if (type == Integer.class)
                        {
                            params[pos] = Integer.valueOf(jt.get_Text());
                        }
                        else // Default is Text
                        {
                            params[pos] = jt.get_Text();
                        }
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + fb.get_Gui_Component());
                }
                
                pos++; // Increase pos
            }
        }
        catch (Exception e)
        {
            throw e;
        }
        
        System.out.printf("\n\nInsert Headers: \n%s \n\nValues: \n%s  \n\nParams: \n%s%n", insert_Header, values, Arrays.toString(params));
        
        //##########################
        // Add To Results
        //##########################
        StringBuilder update_Query = insert_Header.append(values);
        queries_And_Params.add(new Pair<>(update_Query.toString(), params));
    }
}