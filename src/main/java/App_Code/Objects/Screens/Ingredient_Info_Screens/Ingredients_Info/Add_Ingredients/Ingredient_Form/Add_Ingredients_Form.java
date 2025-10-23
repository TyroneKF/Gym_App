package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Ingredient_Form;

import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Gui_Objects.IconPanel;
import App_Code.Objects.Gui_Objects.JTextFieldLimit;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Parent_IngredientForm_And_ShopForm;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Ingredients_Info_Screen;
import org.javatuples.Triplet;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Add_Ingredients_Form extends Parent_IngredientForm_And_ShopForm
{
    //##################################################
    // Variables
    //##################################################
    protected LinkedHashMap<String, Triplet<String, String, String>> ingredientsFormLabelsMapsToValues = new LinkedHashMap<>()
    {{
        // ingredientsFormLabel Key -> ( nutritionIx value, Mysql Column Name, mySql Field Datatype)
        
        put("Ingredient Measurement In", new Triplet<String, String, String>("serving_unit", "Measurement", "String"));
        put("Ingredient Name", new Triplet<String, String, String>("food_name", "Ingredient_Name", "String"));
        put("Ingredient Type", new Triplet<String, String, String>(null, "ingredient_type_id", "Integer"));
        put("Based_On_Quantity", new Triplet<String, String, String>("serving_weight_grams", "Based_On_Quantity", "Double"));
        put("Glycemic Index", new Triplet<String, String, String>(null, "Glycemic_Index", "Double"));
        put("Protein", new Triplet<String, String, String>("nf_protein", "Protein", "Double"));
        put("Carbohydrates", new Triplet<String, String, String>("nf_total_carbohydrate", "Carbohydrates", "Double"));
        put("Sugars Of Carbs", new Triplet<String, String, String>("nf_sugars", "Sugars_Of_Carbs", "Double"));
        put("Fibre", new Triplet<String, String, String>("nf_dietary_fiber", "Fibre", "Double"));
        put("Fat", new Triplet<String, String, String>("nf_total_fat", "Fat", "Double"));
        put("Saturated Fat", new Triplet<String, String, String>("nf_saturated_fat", "Saturated_Fat", "Double"));
        put("Salt", new Triplet<String, String, String>("nf_sodium", "Salt", "Double"));
        //                put("Cholesterol",  new Triplet<String, String, String>("nf_cholesterol", "", "Double"));
        put("Water Content", new Triplet<String, String, String>(null, "Water_Content", "Double"));
        put("Liquid Content", new Triplet<String, String, String>(null, "Liquid_Content", "Double"));
        put("Calories", new Triplet<String, String, String>("nf_calories", "Calories", "Double"));
        //                put("Potassium",  new Triplet<String, String, String>("nf_potassium", "", "Double"));
        
    }};
    
    protected LinkedHashMap<String, Object[]> ingredientsFormObjectAndValues = new LinkedHashMap<>()
    {{
        // ingredientsFormLabel Key -> [Component, FormValue, DB Value]
        
        put("Ingredient Measurement In", new Object[3]);
        put("Ingredient Name", new Object[3]);
        put("Ingredient Type", new Object[3]);
        put("Based_On_Quantity", new Object[3]);
        put("Glycemic Index", new Object[3]);
        put("Protein", new Object[3]);
        put("Carbohydrates", new Object[3]);
        put("Sugars Of Carbs", new Object[3]);
        put("Fibre", new Object[3]);
        put("Fat", new Object[3]);
        put("Saturated Fat", new Object[3]);
        put("Salt", new Object[3]);
        //put("Cholesterol",  new Object[3]);
        put("Water Content", new Object[3]);
        put("Liquid Content", new Object[3]);
        put("Calories", new Object[3]);
        //put("Potassium", new Object[3]);
    }};
    
    protected JComboBox<String>
            ingredientsMeasure_JComboBox = new JComboBox(),
            ingredientsType_JComboBox = new JComboBox(),
            saltMeasurement_JComboBox = new JComboBox();
    
    protected String ingredientsValuesBeingAdded = ""; //HELLO Refactor
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Add_Ingredients_Form(Container parentContainer, Ingredients_Info_Screen ingredients_info_screen, String btnText, int btnWidth, int btnHeight)
    {
        super(parentContainer, ingredients_info_screen, btnText, btnWidth, btnHeight);
        
        create_Ingredients_Form();
        collapsibleJPanel.expand_JPanel();
    }
    
    //##################################################################################################################
    // Creating GUI Methods
    //##################################################################################################################
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
        
        // for each label it is created into a JLabel
        int yPos = - 1;
        for (Map.Entry<String, Object[]> info : ingredientsFormObjectAndValues.entrySet())
        {
            yPos++;
            int xPos = - 1; // Don't refactor out creates problems with drawing diagram
            
            String formLabel = info.getKey();
            Object[] tempValues = info.getValue();
            
            //#########################################
            //
            //#########################################
            boolean jcomboxBeingCreated = false;
            JTextField textField = null;
            JComboBox comboBox = null;
            
            //##########################################################################################################
            // JLabel Column 1
            //##########################################################################################################
            String labelTXT = formLabel + ":";
            
            JLabel label = new JLabel("    " + labelTXT);
            label.setHorizontalAlignment(JLabel.LEFT);
            label.setFont(new Font("Verdana", Font.BOLD, 14));
            
            add_To_Container(inputArea, label, xPos += 1, yPos, 1, 1, 0.25, 0.25, "horizontal", 0, 0, null); ;
            
            //##########################################################################################################
            // JComboFields (Column 2)
            //##########################################################################################################
            
            if (formLabel.equals("Ingredient Measurement In"))
            {
                jcomboxBeingCreated = true;
                String ingredientMeassurements[] = { "Litres", "Grams" };
                ingredientsMeasure_JComboBox = new JComboBox(ingredientMeassurements);
                comboBox = ingredientsMeasure_JComboBox;
            }
            else if (formLabel.equals("Ingredient Type"))
            {
                jcomboxBeingCreated = true;
                ingredientsType_JComboBox = new JComboBox();
                comboBox = ingredientsType_JComboBox;
                
                load_Ingredients_Type_JComboBox();
            }
            else if (formLabel.equals("Salt"))
            {
                // JPanel for salt JComboBox & JTextfield
                JPanel saltJPanelArea = new JPanel(new GridLayout(2, 1));
                
                //############################################
                // Creating & Adding JComboBox
                //############################################
                String[] saltMeasurements = { "mg", "g" };
                saltMeasurement_JComboBox = new JComboBox(saltMeasurements);
                saltJPanelArea.add(saltMeasurement_JComboBox);
                comboBox = saltMeasurement_JComboBox;
                
                // Centre JComboBox Item
                DefaultListCellRenderer listRenderer = new DefaultListCellRenderer();
                listRenderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER); // center-aligned items
                comboBox.setRenderer(listRenderer);
                
                // Set Selected Item To Nothing
                comboBox.setSelectedIndex(- 1);
                
                //############################################
                // Creating JTextfield & Adding JTextField
                //############################################
                textField = new JTextField("");
                textField.setDocument(new JTextFieldLimit(charLimit));
                saltJPanelArea.add(textField);
                
                //############################################
                //
                //############################################
                set_Ingredients_Form_Object_And_Values(formLabel, 0, textField);
                
                add_To_Container(inputArea, saltJPanelArea, xPos += 1, yPos, 1, 1, 0.25, 0.25, "both", 0, 0, null); // Adding JPanel to GUI
                continue;
            }
            
            //#########################################################################################################
            // if a JComboBox is being created Centre JComboBox Items & Set Selected Item to 0
            //#########################################################################################################
            if (jcomboxBeingCreated)
            {
                // Centre JComboBox Item
                DefaultListCellRenderer listRenderer = new DefaultListCellRenderer();
                listRenderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER); // center-aligned items
                comboBox.setRenderer(listRenderer);
                
                // Set Selected Item To Nothing
                comboBox.setSelectedIndex(- 1);
                
                set_Ingredients_Form_Object_And_Values(formLabel, 0, comboBox);
                
                add_To_Container(inputArea, comboBox, xPos += 1, yPos, 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
                continue;
            }
            
            //#########################################################################################################
            // Generic TextFields
            //#########################################################################################################
            textField = new JTextField("");
            if (labelTXT.equals("Ingredient Name:")) //Setting TextField limits
            {
                textField.setDocument(new JTextFieldLimit(255));
            }
            else
            {
                textField.setDocument(new JTextFieldLimit(charLimit));
            }
            
            set_Ingredients_Form_Object_And_Values(formLabel, 0, textField);
            
            add_To_Container(inputArea, textField, xPos += 1, yPos, 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
        }
        
        mainJPanel.add(inputArea, BorderLayout.CENTER);
    }
    
    protected void create_Icon_Bar()
    {
        create_Icon_Bar_On_GUI(true);
    }
    
    protected void create_Icon_Bar_On_GUI(boolean createIconBar)
    {
        //#####################################################
        // Exit Clause
        //#####################################################
        if (! (createIconBar))
        {
            return;
        }
        
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
    
    public void load_Ingredients_Type_JComboBox()
    {
        ingredientsType_JComboBox.removeAllItems();
        for (String ingredientType : ingredients_info_screen.get_IngredientsTypes_List())
        {
            if (ingredientType.equals("None Of The Above"))
            {
                continue;
            }
            
            ingredientsType_JComboBox.addItem(ingredientType);
        }
        
        ingredientsType_JComboBox.setSelectedIndex(- 1);
    }
    
    //##################################################################################################################
    // Clear GUI Methods
    //##################################################################################################################
    public void clear_Ingredients_Form()
    {
        for (Map.Entry<String, Object[]> info : ingredientsFormObjectAndValues.entrySet())
        {
            String rowLabel = info.getKey();
            Object[] row = info.getValue();
            Component comp = (Component) row[0];
            
            if (comp instanceof JComboBox)
            {
                ((JComboBox<?>) comp).setSelectedIndex(- 1);
            }
            else if (comp instanceof JTextField)
            {
                ((JTextField) comp).setText("");
            }
            
            // Remove FormField Values in Memory to null
            set_Ingredients_Form_Object_And_Values(rowLabel, 1, null);
        }
        
        saltMeasurement_JComboBox.setSelectedIndex(- 1);
        extra_Clear_Ingredients_Form();
    }
    
    protected void extra_Clear_Ingredients_Form()
    {
    
    }
    
    //##################################################################################################################
    // API Methods
    //##################################################################################################################
    public void update_Form_With_Nutrition_IX_Search(LinkedHashMap<String, Object> foodInfo)//HELLO EDITED NOW
    {
        if (foodInfo != null)
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
        }
    }
    
    //##################################################################################################################
    // Validate Form
    //##################################################################################################################
    public boolean validate_Ingredients_Form()//HELLO EDITED NOW
    {
        String errorTxt = "", ingredientName_Txt = "";
        
        //##############################
        // Validation Input Fields
        //##############################
        
        int rowPos = 0;
        for (Map.Entry<String, Object[]> info : ingredientsFormObjectAndValues.entrySet())
        {
            rowPos++;
            
            String value = "", rowLabel = info.getKey();
            Object[] listValues = info.getValue();
            Component comp = (Component) listValues[0];
            
            //##########################################################################################################
            if (comp instanceof JComboBox)
            {
                JComboBox comboBox = (JComboBox) comp;
                
                if (comboBox.getSelectedIndex() == - 1) // if no item has been selected by JComboBox
                {
                    errorTxt += String.format("\n\n  ' %s ' on Row: %s, an option inside the dropdown menu must be selected", rowLabel, rowPos);
                    continue;
                }
                
                // Store current JComboBox form Values into store
                value = comboBox.getSelectedItem().toString();
                set_Ingredients_Form_Object_And_Values(rowLabel, 1, value);
                
                continue;
            }
            //##########################################################################################################
            else if (comp instanceof JTextField)
            {
                JTextField jTextField = (JTextField) comp;
                value = jTextField.getText().trim();
                
                //#########################################
                // Check if JTextField input is empty
                //#########################################
                if (value.equals(""))
                {
                    errorTxt += String.format("\n\n  ' %s ' on Row: %s,  must have a value which is not ' NULL '!", rowLabel, rowPos);
                    continue;
                }
                
                //#########################################
                //HELLO Add more processing here
                //#########################################
                if (rowLabel.equals("Ingredient Name"))
                {
                    ingredientName_Txt = value;
                    
                    if (does_String_Contain_Given_Characters(ingredientName_Txt, "'"))
                    {
                        errorTxt += String.format("\n\n  Ingredient named \"%s\" cannot contain the symbol \" ' \"!", ingredientName_Txt);
                    }
                    else
                    {
                        errorTxt = extra_Validation_Ingredient_Name(errorTxt, ingredientName_Txt);
                    }
                    
                    set_Ingredients_Form_Object_And_Values(rowLabel, 1, ingredientName_Txt);
                    continue;
                }
                
                //#########################################
                //
                //#########################################
                else if (rowLabel.equals("Glycemic Index"))
                {
                    try
                    {
                        int intValue = Integer.valueOf(value);
                        
                        if (intValue > 100 || intValue < 0)
                        {
                            throw new Exception();
                        }
                        
                        set_Ingredients_Form_Object_And_Values(rowLabel, 1, value);
                    }
                    catch (Exception e)
                    {
                        errorTxt += String.format("\n\n  ' %s ' on Row: %s,  must be an Integer value which is: 0<= value <=100 !", rowLabel, rowPos);
                    }
                    continue;
                }
                
                //#########################################
                // Change salt value based on mg to g
                //#########################################
                else if (rowLabel.equals("Salt"))
                {
                    if (saltMeasurement_JComboBox.getSelectedIndex() == - 1)
                    {
                        errorTxt += String.format("\n\n  ' %s ' on Row: %s, an option for the measurement chosen for salt needs to be selected in the dropdown box!", rowLabel, rowPos);
                        continue;
                    }
                    
                    if (saltMeasurement_JComboBox.getSelectedItem().equals("mg"))
                    {
                        try
                        {
                            BigDecimal bdFromString = new BigDecimal(String.format("%s", value));
                            BigDecimal result = bdFromString.divide(new BigDecimal("1000"), 2, RoundingMode.HALF_UP);
                            
                            value = String.format("%s", result);
                        }
                        catch (Exception e)
                        {
                            System.out.printf("\n\nError! \nConverting Salt mg to g!");
                        }
                    }
                }
                
                //#########################################
                // Do BigDecimal Processing on Everything
                //#########################################
                errorTxt += convert_To_Big_Decimal(value, rowLabel, rowPos, jTextField, false);
                
                //######################################################################################################
                // After converting values, save it in memories
                set_Ingredients_Form_Object_And_Values(rowLabel, 1, jTextField.getText().trim());
            }
        }
        
        //####################################################
        // Check if any error were found & Process it
        //####################################################
        if (errorTxt.length() == 0)
        {
            return true;
        }
        
        JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), String.format("\n\nPlease fix the following rows being; \n%s", errorTxt));
        return false;
    }
    
    protected String extra_Validation_Ingredient_Name(String errorTxt, String ingredientName)
    {
        //####################################################
        //Check if IngredientName Already exists in DB
        //####################################################
        if (check_IF_IngredientName_In_DB(ingredientName))
        {
            errorTxt += String.format("\n\n  Ingredient named %s already exists within the database!", ingredientName);
        }
        
        return errorTxt;
    }
    
    protected boolean check_IF_IngredientName_In_DB(String ingredientName)
    {
        ingredientName = remove_Space_And_Hidden_Chars(ingredientName);
        String query = String.format("SELECT Ingredient_Name FROM ingredients_info WHERE Ingredient_Name = \"%s\";", ingredientName);
        
        System.out.printf("\n\n%s", query);
        
        if (db.getSingleColumnQuery(query) != null)
        {
            return true;
        }
        return false;
    }
    
    protected String remove_Space_And_Hidden_Chars(String stringToBeEdited)
    {
        return stringToBeEdited.trim().replaceAll("\\p{C}", ""); // remove all whitespace & hidden characters like \n
    }
    
    public String get_Ingredients_Form_Update_String(String ingredientID) // HELLO needs further update methods created for gui
    {
        //##############################################################################################################
        // Creating Upload Query
        //##############################################################################################################
        String
                ingredientTypeSet = "SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = \"",
                insertQuery = "INSERT INTO ingredients_info VALUES";
        
        ingredientsValuesBeingAdded = "(null,";
        //##############################################################################################################
        //
        //##############################################################################################################
        int pos = - 1, endPos = ingredientsFormLabelsMapsToValues.size() - 1;
        for (Map.Entry<String, Triplet<String, String, String>> entry : ingredientsFormLabelsMapsToValues.entrySet())
        {
            pos++;
            
            //####################################
            //
            //####################################
            String key = entry.getKey();
            Triplet<String, String, String> value = entry.getValue();
            String mysqlColumnDataType = value.getValue2();
            
            String
                    fieldsQueryEx = "",
                    formFieldValue = (String) ingredientsFormObjectAndValues.get(key)[1];
            
            //####################################
            //
            //####################################
            if (key.equals("Ingredient Type"))
            {
                fieldsQueryEx = String.format("(%s%s\")", ingredientTypeSet, formFieldValue);
            }
            else
            {
                fieldsQueryEx = mysqlColumnDataType.equals("String")
                        ?
                        String.format("(\"%s\")", formFieldValue)
                        :
                        String.format("(%s)", formFieldValue);
            }
            
            //####################################
            //
            //####################################
            if (pos == endPos)
            {
                ingredientsValuesBeingAdded += String.format("%s)", fieldsQueryEx);
                continue;
            }
            
            //####################################
            //
            //####################################
            ingredientsValuesBeingAdded += String.format("%s,", fieldsQueryEx);
        }
        
        //##############################################################################################################
        // Return results
        //##############################################################################################################
        return (insertQuery += ingredientsValuesBeingAdded + ";");
    }
    
    //##################################################################################################################
    // Mutator Methods
    //##################################################################################################################
    // ingredientsFormLabel Key -> [Component, FormValue, DB Value]
    protected void set_Ingredients_Form_Object_And_Values(String key, int pos, Object valueToChange)
    {
        Object[] list = ingredientsFormObjectAndValues.get(key);
        list[pos] = valueToChange;
        ingredientsFormObjectAndValues.put(key, list);
    }
    
    //##################################################################################################################
    // Accessor Methods
    //##################################################################################################################
    public String get_Ingredient_Type_Form_Value()
    {
        return (String) ingredientsFormObjectAndValues.get("Ingredient Type")[1];
    }
    
    public String get_Ingredient_Name_Form_Value()
    {
        return (String) ingredientsFormObjectAndValues.get("Ingredient Name")[1];
    }
    
    public String get_Ingredients_Values_Being_Added()
    {
        return ingredientsValuesBeingAdded;
    }
    
    public LinkedHashMap<String, Object[]> get_Ingredients_Form_Object_And_Values()
    {
        return ingredientsFormObjectAndValues;
    }
}