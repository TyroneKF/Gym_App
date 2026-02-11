package com.donty.gymapp.ui.screens.ingredientsAndInventory.Ingredients_Info.add.ingredients;

import com.donty.gymapp.gui.controls.combobox.Field_JCombo_Default;
import com.donty.gymapp.persistence.database.MyJDBC_Sqlite;
import com.donty.gymapp.persistence.database.batch.Batch_Upload_Statements;
import com.donty.gymapp.persistence.database.statements.Fetch_Statement_Full;
import com.donty.gymapp.persistence.database.statements.Upload_Statement;
import com.donty.gymapp.persistence.Shared_Data_Registry;
import com.donty.gymapp.gui.controls.combobox.base.storableID.base.Field_JCombo_Storable_ID;
import com.donty.gymapp.gui.controls.combobox.base.Field_JComboBox;
import com.donty.gymapp.gui.controls.IconButton;
import com.donty.gymapp.gui.panels.IconPanel;
import com.donty.gymapp.gui.controls.textfields.Field_JTxtField_BD;
import com.donty.gymapp.gui.controls.textfields.Field_JTxtField_INT;
import com.donty.gymapp.gui.controls.textfields.Field_JTxtField_String;
import com.donty.gymapp.gui.controls.textfields.base.Field_JTxtField_Parent;
import com.donty.gymapp.gui.controls.combobox.base.storableID.Field_JC_Ingredient_Type;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.Ingredients_Info.base.ingredients.Field_JC_Measurements;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.Ingredients_Info.base.screen.Parent_Forms_OBJ;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.Ingredients_Info.base.ingredients.Ingredient_Binding;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.Ingredients_Info.base.ingredients.Ingredient_Info_Columns;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Ingredients_Form extends Parent_Forms_OBJ
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    // String
    protected String class_Name = new Object() { }.getClass().getEnclosingClass().getName();

    // Integers
    protected int
            digit_Char_Limit = 8,
            text_Char_Limit = 255;

    //#############################
    // Objects
    //#############################
    protected Shared_Data_Registry shared_data_registry;
    protected MyJDBC_Sqlite db;

    protected JPanel northPanel = new JPanel(new GridBagLayout());

    // Salt JC Object
    protected ArrayList<String> salt_Values_AL = new ArrayList<>(Arrays.asList("mg", "g"));
    protected Field_JCombo_Default<String> salt_JC = new Field_JCombo_Default<>("Salt", String.class, salt_Values_AL);

    protected Field_JC_Ingredient_Type field_jc_ingredient_type;

    //############
    // Maps
    //############
    protected LinkedHashMap<String, Ingredient_Binding<?>> field_Items_Map;

    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Ingredients_Form
    (
            Container parentContainer,
            MyJDBC_Sqlite db,
            Shared_Data_Registry shared_data_registry,
            String btn_Txt
    )
    {
        //############################################
        // Super Constructor
        //############################################
        super(parentContainer, btn_Txt);

        //############################################
        // Variables
        //############################################
        this.db = db;
        this.shared_data_registry = shared_data_registry;
        this.field_jc_ingredient_type = new Field_JC_Ingredient_Type(shared_data_registry, false);

        //############################################
        // Create GUI
        //############################################
        create_Field_Items_Map(); // Create Map, as values were needed from above ^^
        create_Ingredients_Form();
        collapsibleJPanel.expand_JPanel();
    }


    //##################################################################################################################
    // Methods
    //##################################################################################################################
    public void set_Salt_JC_To_Grams()
    {
        salt_JC.setSelectedItem("g");
    }

    //###########################################################
    // Create GUI Methods
    //###########################################################
    private void create_Field_Items_Map()
    {
        field_Items_Map = new LinkedHashMap<>()
        {{
            /*
                 General Structure:
                    * Key
                    * Form Binding:
                        1.) GUI Label
                        2.) Component
                        3.) MySQL Field
                        4.) Pos in MYSQL Table Query
                        5.) NutritionIX Field
            */

            put(
                    "measurement",
                    new Ingredient_Binding<>(
                            "Ingredient Measurement In",
                            new Field_JC_Measurements(shared_data_registry),
                            Ingredient_Info_Columns.MEASUREMENT_ID,
                            2,
                            "serving_unit"
                    )
            );

            put(
                    "name",
                    new Ingredient_Binding<>(
                            "Ingredient Name",
                            new Field_JTxtField_String("Ingredient Name", text_Char_Limit),
                            Ingredient_Info_Columns.INGREDIENT_NAME,
                            4,
                            "food_name"
                    )
            );
            put(
                    "type",
                    new Ingredient_Binding<>(
                            "Ingredient Type",
                           field_jc_ingredient_type,
                            Ingredient_Info_Columns.INGREDIENT_TYPE_ID,
                            3
                    )
            );
            put(
                    "quantity",
                    new Ingredient_Binding<>(
                            "Based On Quantity",
                            new Field_JTxtField_BD("Based On Quantity", digit_Char_Limit, false),
                            Ingredient_Info_Columns.BASED_ON_QUANTITY,
                            5,
                            "serving_weight_grams"
                    )
            );
            put(
                    "gi",
                    new Ingredient_Binding<>(
                            "Glycemic Index",
                            new Field_JTxtField_INT("Glycemic Index", digit_Char_Limit, true, 0, 100),
                            Ingredient_Info_Columns.GLYCEMIC_INDEX,
                            6
                    )
            );
            put(
                    "protein",
                    new Ingredient_Binding<>(
                            "Protein",
                            new Field_JTxtField_BD("Protein", digit_Char_Limit),
                            Ingredient_Info_Columns.PROTEIN,
                            7,
                            "nf_protein"
                    )
            );
            put(
                    "carbs",
                    new Ingredient_Binding<>(
                            "Carbohydrates",
                            new Field_JTxtField_BD("Carbohydrates", digit_Char_Limit),
                            Ingredient_Info_Columns.CARBOHYDRATES,
                            8,
                            "nf_total_carbohydrate"
                    )
            );
            put(
                    "sugars",
                    new Ingredient_Binding<>(
                            "Sugars Of Carbs",
                            new Field_JTxtField_BD("Sugars Of Carbs", digit_Char_Limit),
                            Ingredient_Info_Columns.SUGARS_OF_CARBS,
                            9,
                            "nf_sugars"
                    )
            );
            put(
                    "fibre",
                    new Ingredient_Binding<>(
                            "Fibre",
                            new Field_JTxtField_BD("Fibre", digit_Char_Limit),
                            Ingredient_Info_Columns.FIBRE,
                            10,
                            "nf_dietary_fiber"
                    )
            );
            put(
                    "fat",
                    new Ingredient_Binding<>(
                            "Fat",
                            new Field_JTxtField_BD("Fat", digit_Char_Limit),
                            Ingredient_Info_Columns.FAT,
                            11,
                            "nf_total_fat"
                    )
            );
            put(
                    "sat_fat",
                    new Ingredient_Binding<>(
                            "Saturated Fat",
                            new Field_JTxtField_BD("Saturated Fat", digit_Char_Limit),
                            Ingredient_Info_Columns.SATURATED_FAT,
                            12,
                            "nf_saturated_fat"
                    )
            );
            put(
                    "salt",
                    new Ingredient_Binding<>(
                            "Salt",
                            new Field_JTxtField_BD("Salt", digit_Char_Limit),
                            Ingredient_Info_Columns.SALT,
                            13,
                            "nf_sodium"
                    )
            );
            put(
                    "water",
                    new Ingredient_Binding<>(
                            "Water Content",
                            new Field_JTxtField_BD("Water Content", digit_Char_Limit),
                            Ingredient_Info_Columns.WATER_CONTENT,
                            14
                    )
            );
            put(
                    "liquid",
                    new Ingredient_Binding<>(
                            "Liquid Content",
                            new Field_JTxtField_BD("Liquid Content", digit_Char_Limit),
                            Ingredient_Info_Columns.LIQUID_CONTENT,
                            15
                    )
            );
            put(
                    "cal",
                    new Ingredient_Binding<>(
                            "Calories",
                            new Field_JTxtField_BD("Calories", digit_Char_Limit),
                            Ingredient_Info_Columns.CALORIES,
                            16,
                            "nf_calories"
                    )
            );
        }};
    }

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

        for (Map.Entry<String, Ingredient_Binding<?>> field_Item : field_Items_Map.entrySet())
        {
            //#######################################
            // Variables
            //#######################################
            String key = field_Item.getKey();
            Ingredient_Binding<?> IngredientBinding = field_Item.getValue();

            Component component = IngredientBinding.get_Gui_Component();

            int xPos = 0, yPos = get_And_Increase_YPos();

            //#######################################
            // Set Label
            //#######################################
            JLabel label = new JLabel(String.format("    %s : ", IngredientBinding.get_Gui_Label()));
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
    public void reload_Type_JComboBox()
    {
        Component object = field_Items_Map.get("type").get_Gui_Component();

        if (! (object instanceof Field_JComboBox<?> jComboBox)) { return; }

        jComboBox.load_Items();
    }

    public void reload_Measurements_JComboBox()
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
        for (Map.Entry<String, Ingredient_Binding<?>> field_Item : field_Items_Map.entrySet())
        {
            Component component = field_Item.getValue().get_Gui_Component();

            if (component instanceof Field_JComboBox<?> jComboBox)
            {
                jComboBox.reset_JC();
            }
            else if (component instanceof Field_JTxtField_Parent<?> jTxtField)
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
        for (Ingredient_Binding<?> field_Binding : field_Items_Map.values())
        {
            switch (field_Binding.get_Gui_Component())
            {
                case Field_JComboBox<?> jc -> jc.validation_Check(error_Map);
                case Field_JTxtField_Parent<?> jt -> jt.validation_Check(error_Map);
                default -> throw new IllegalStateException("Unexpected value: " + field_Binding.get_Gui_Component());
            }
        }

        //###############################
        // Check Ingredient Name In DB
        //###############################
        // Check if Ingredients Name Field is not Empty
        if (! ((Field_JTxtField_String) field_Items_Map.get("name").get_Gui_Component()).is_Txt_Field_Empty())
        {
            String error_Msg = "", label = "Ingredient Name";

            try
            {
                if (is_Ingredient_Name_In_DB())  //IF ingredient Name already exists add error error_Msg
                {
                    error_Msg = String.format("'%s' : Already Exists in DB!", label);
                }
            }
            catch (Exception e)
            {
                error_Msg = "Failed Validating Ingredient Name in DB!";
            }

            if (! error_Msg.isEmpty())
            {
                ArrayList<String> ingredient_Name_Errors = error_Map.getOrDefault(label, new ArrayList<>());  // Get Or Create an Empty list of errors if not available
                ingredient_Name_Errors.add(error_Msg); // Add Error MSG to log in AL
                error_Map.put(label, ingredient_Name_Errors); // Add to / back into Map of errors
            }
        }

        //###############################
        // IF no errors returns True
        //###############################
        if (error_Map.isEmpty())
        {
            salt_Formating();  // Convert Salt from MG to Grams if needed
            return true;
        }

        //###############################
        // Display Errors / Output
        //###############################
        UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 16));
        JOptionPane.showMessageDialog(null, build_Error_MSg(error_Map), "Ingredients Form Error Messages", JOptionPane.INFORMATION_MESSAGE);

        return false;
    }

    protected String build_Error_MSg(LinkedHashMap<String, ArrayList<String>> error_Map)
    {
        //###############################
        // Build Error MSGs
        //###############################
        /*
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

        error_MSG_String.append("<br><br></html>");

        //###############################
        // Return Output
        //###############################
        return error_MSG_String.toString();
    }

    protected void salt_Formating()
    {
        try
        {
            if (salt_JC.get_Selected_Item().equals("g")) { return; } // If Salt JC has Mg Selected

            // Get Salt Component
            Field_JTxtField_BD salt_Obj = (Field_JTxtField_BD) field_Items_Map.get("salt").get_Gui_Component();

            // Convert mg to G
            BigDecimal salt_MG_Value = salt_Obj.get_Text_Casted_To_Type();

            // Convert MG to G value
            BigDecimal new_Value_G = salt_MG_Value.divide(new BigDecimal("1000"), 2, RoundingMode.HALF_UP);

            // Set Salt Field to Grams Value
            System.out.printf("\n\nNew Salt %s", new_Value_G);
            salt_Obj.setText(new_Value_G.toPlainString());

            // Set Salt JC to G Salt
            set_Salt_JC_To_Grams();
        }
        catch (Exception e)
        {
            System.err.printf("%s", e);
            throw new RuntimeException(e);
        }
    }


    protected boolean is_Ingredient_Name_In_DB() throws Exception
    {
        //##################################
        // IS Ingredient Name Null or Empty
        //####################################
        String ingredient_Name = ((Field_JTxtField_String) field_Items_Map.get("name").get_Gui_Component()).get_Text();

        if (ingredient_Name == null || ingredient_Name.isEmpty()) { throw new Exception("No ingredient Created!"); }

        //##################################
        // Create Query
        //####################################
        String error_msg = "Error, Failed Validating Ingredient Name in DB!";
        String query = "SELECT ingredient_id FROM ingredients_info WHERE Ingredient_Name = ?;";
        Object[] params = new Object[]{ ingredient_Name };

        Fetch_Statement_Full fetch_statement = new Fetch_Statement_Full(query, params, error_msg);

        //##################################
        // Execute
        //####################################
        return ! db.get_Single_Col_Query_Int(fetch_statement, true).isEmpty();
    }

    //#######################################################
    // Update Methods
    //#######################################################
    public void add_Update_Queries(Batch_Upload_Statements upload_statements) throws Exception
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

        int pos = 0;
        for (Ingredient_Binding<?> field_Binding : field_Items_Map.values())
        {
            boolean last_Iteration = pos == size - 1;

            // Add to Header
            insert_Header.append(last_Iteration
                    ? String.format("%s) VALUES ", field_Binding.get_Mysql_Field_Name())
                    : String.format("%s,", field_Binding.get_Mysql_Field_Name())
            );

            // Add to Values
            values.append(last_Iteration ? "?);" : "?,");

            // Add to params
            switch (field_Binding.get_Gui_Component())
            {
                case Field_JCombo_Storable_ID<?> jc -> params[pos] = jc.get_Selected_Item_ID();
                case Field_JTxtField_Parent<?> jt -> params[pos] = jt.get_Text_Casted_To_Type();
                default -> throw new IllegalStateException("Unexpected value: " + field_Binding.get_Gui_Component());
            }

            pos++; // Increase pos
        }

        System.out.printf("\n\nInsert Headers: \n%s \n\nValues: \n%s  \n\nParams: \n%s%n", insert_Header, values, Arrays.toString(params));

        //##########################
        // Add To Results
        //##########################
        StringBuilder update_Query = insert_Header.append(values);
        upload_statements.add_Uploads(new Upload_Statement(update_Query.toString(), params, true));
    }

    //##################################################################################################################
    // Accessor Methods
    //##################################################################################################################
    public Component get_Component(String key)
    {
        return field_Items_Map.containsKey(key) ? field_Items_Map.get(key).get_Gui_Component() : null;
    }

    public Object get_Component_Field_Value(String key) throws Exception // Cast Output to Type
    {
        Component component = get_Component(key);

        switch (component)
        {
            case Field_JTxtField_Parent<?> jt -> { return jt.get_Text_Casted_To_Type(); } // Returns Selected Obj Type
            case Field_JCombo_Storable_ID<?> jc -> { return jc.get_Selected_Item(); }
            case Field_JComboBox<?> jc -> { return jc.get_Selected_Item_TXT(); } // Returns selected obj to.String
            default -> throw new Exception("Unexpected value: " + component);
        }
    }
}