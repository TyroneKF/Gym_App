package App_Code.Objects.Screens.Ingredient_Info.Edit_Ingredients_Info;

import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Gui_Objects.IconPanel;
import App_Code.Objects.Gui_Objects.JTextFieldLimit;
import org.javatuples.Triplet;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Add_IngredientsForm extends Parent_IngredientsForm_And_ShopForm
{

    //##################################################
    //
    //##################################################
    protected LinkedHashMap<String, Triplet<String, String, String>> ingredientsFormLabelsMapsToValues = new LinkedHashMap<>()
    {{
        // ingredientsFormLabel Key -> ( nutritionIx value, Mysql Column Name, mySql Field Datatype)

        put("Ingredient Measurement In", new Triplet<String, String, String>("serving_unit", "Measurement", "String"));
        put("Ingredient Name", new Triplet<String, String, String>("food_name", "Ingredient_Name", "String"));
        put("Ingredient Type", new Triplet<String, String, String>(null, "Ingredient_Type_ID", "Integer"));
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

    protected int
            ingredientNameObjectIndex,
            ingredientTypeObjectIndex,
            glycemicObjectIndex,
            ingredientSaltObjectIndex,
            ingredientMeasurementObjectIndex;

    protected JComboBox<String>
            ingredientsMeasure_JComboBox = new JComboBox(),
            ingredientsType_JComboBox = new JComboBox(),
            saltMeasurement_JComboBox = new JComboBox();

    protected ArrayList<Component> ingredientsFormObjects = new ArrayList<>();

    protected String ingredientsValuesBeingAdded = "";

    //##################################################################################################################
    //
    //##################################################################################################################
    public Add_IngredientsForm(Container parentContainer, Ingredients_Info_Screen ingredients_info_screen, String btnText, int btnWidth, int btnHeight)
    {
        super(parentContainer, ingredients_info_screen, btnText, btnWidth, btnHeight);

        createIngredientsForm();
        expandJPanel();
    }

    private void createIngredientsForm()
    {
        JPanel mainJPanel = getCentreJPanel();
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
        addToContainer(northPanel, titlePanel, 0, 2, 1, 1, 0.25, 0.25, "both", 0, 0);

        //#################################################################
        // Create Icon Bar
        //#################################################################
        createIconBar();

        //#################################################################
        // Centre Frame
        //#################################################################

        JPanel inputArea = new JPanel(new GridBagLayout());

        // for each label it is created into a JLabel
        int yPos = -1;
        for (Map.Entry<String, Object[]> info : ingredientsFormObjectAndValues.entrySet())
        {
            yPos++;
            int xPos = -1; // Don't refactor out creates problems with drawing diagram

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

            addToContainer(inputArea, label, xPos += 1, yPos, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

            //##########################################################################################################
            // JComboFields (Column 2)
            //##########################################################################################################

            if (formLabel.equals("Ingredient Measurement In"))
            {
                jcomboxBeingCreated = true;
                String ingredientMeassurements[] = {"Litres", "Grams"};
                ingredientsMeasure_JComboBox = new JComboBox(ingredientMeassurements);
                comboBox = ingredientsMeasure_JComboBox;
            }
            else if (formLabel.equals("Ingredient Type"))
            {
                jcomboxBeingCreated = true;
                ingredientsType_JComboBox = new JComboBox();
                comboBox = ingredientsType_JComboBox;

                loadIngredientsTypeJComboBox();
            }
            else if (formLabel.equals("Salt"))
            {
                // JPanel for salt JComboBox & JTextfield
                JPanel saltJPanelArea = new JPanel(new GridLayout(2, 1));

                //############################################
                // Creating & Adding JComboBox
                //############################################
                String[] saltMeasurements = {"mg", "g"};
                saltMeasurement_JComboBox = new JComboBox(saltMeasurements);
                saltJPanelArea.add(saltMeasurement_JComboBox);
                comboBox = saltMeasurement_JComboBox;

                // Centre JComboBox Item
                DefaultListCellRenderer listRenderer = new DefaultListCellRenderer();
                listRenderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER); // center-aligned items
                comboBox.setRenderer(listRenderer);

                // Set Selected Item To Nothing
                comboBox.setSelectedIndex(-1);

                //############################################
                // Creating JTextfield & Adding JTextField
                //############################################
                textField = new JTextField("");
                textField.setDocument(new JTextFieldLimit(charLimit));
                saltJPanelArea.add(textField);

                //############################################
                //
                //############################################
                tempValues[0] = textField;
                ingredientsFormObjectAndValues.put(formLabel, tempValues);

                addToContainer(inputArea, saltJPanelArea, xPos += 1, yPos, 1, 1, 0.25, 0.25, "both", 0, 0); // Adding JPanel to GUI
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
                comboBox.setSelectedIndex(-1);

                tempValues[0] = comboBox;
                ingredientsFormObjectAndValues.put(formLabel, tempValues);

                addToContainer(inputArea, comboBox, xPos += 1, yPos, 1, 1, 0.25, 0.25, "horizontal", 0, 0);
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

            tempValues[0] = textField;
            ingredientsFormObjectAndValues.put(formLabel, tempValues);

            addToContainer(inputArea, textField, xPos += 1, yPos, 1, 1, 0.25, 0.25, "horizontal", 0, 0);
        }

        mainJPanel.add(inputArea, BorderLayout.CENTER);
    }

    protected void loadIngredientsTypeJComboBox()
    {
        ingredientsType_JComboBox.removeAllItems();
        for (String ingredientType : ingredients_info_screen.getIngredientsTypesList())
        {
            if (ingredientType.equals("None Of The Above"))
            {
                continue;
            }

            ingredientsType_JComboBox.addItem(ingredientType);
        }

        ingredientsType_JComboBox.setSelectedIndex(-1);
    }

    public void update_Form_WithNutritionIXSearch(LinkedHashMap<String, Object> foodInfo)//HELLO EDITED NOW
    {
        if (foodInfo!=null)
        {
            clearIngredientsForm();

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
            int formLabelPos = -1;
            for (Map.Entry<String, Triplet<String, String, String>> info : ingredientsFormLabelsMapsToValues.entrySet())
            {
                // Get NutritionIx Label & its value
                String formLabelName = info.getKey();
                Triplet<String, String, String> keyObject = info.getValue();

                //############################
                //
                //############################
                String foodInfoEquivalentLabel = keyObject.getValue0();
                if (foodInfoEquivalentLabel==null)
                {
                    continue;
                }

                //############################
                //
                //############################
                Object foodInfoNutritionValue = foodInfo.get(foodInfoEquivalentLabel);

                System.out.printf("\n\n#############################\n\nNutritionIx Label: %s  = %s \nIngredient Form Label pos: %s | Ingredient Form Label: %s  ",
                        foodInfoEquivalentLabel, foodInfoNutritionValue, formLabelName, formLabelPos);

                if (foodInfoNutritionValue==null || foodInfoNutritionValue.toString().equals("null"))
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
                        foodInfoNutritionValue = foodInfoNutritionValue.toString().equals("g") ? "Grams":"Litres";
                    }

                    JComboBox obj = (JComboBox) comp;
                    obj.setSelectedItem(foodInfoNutritionValue);
                }
            }
        }
    }

    protected void createIconBar()
    {
        createIconBarOnGUI(true);
    }

    protected void createIconBarOnGUI(boolean createIconBar)
    {
        //#####################################################
        // Exit Clause
        //#####################################################
        if (!(createIconBar))
        {
            return;
        }

        //#####################################################
        // Creating area for North JPanel (Refresh Icon)
        //#####################################################

        JPanel iconArea = new JPanel(new GridBagLayout());
        addToContainer(northPanel, iconArea, 0, 1, 1, 1, 0.25, 0.25, "both", 0, 0);

        IconPanel iconPanel = new IconPanel(1, 10, "East");
        JPanel iconPanelInsert = iconPanel.getIconJpanel();

        addToContainer(iconArea, iconPanel.getIconAreaPanel(), 0, 0, 1, 1, 0.25, 0.25, "horizontal", 10, 0);

        //##########################
        // Refresh Icon
        //##########################
        int width = 30;
        int height = 30;

        IconButton refresh_Icon_Btn = new IconButton("src/main/java/images/refresh/++refresh.png", "", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor

        JButton refresh_Btn = refresh_Icon_Btn.returnJButton();
        refresh_Icon_Btn.makeBTntransparent();

        refresh_Btn.addActionListener(ae -> {

            clearIngredientsForm();
        });

        iconPanelInsert.add(refresh_Icon_Btn);
    }

    protected void clearIngredientsForm()
    {
        for (Map.Entry<String, Object[]> info : ingredientsFormObjectAndValues.entrySet())
        {
            //String key = info.getKey();
            Object[] row = info.getValue();
            Component comp = (Component) row[0];

            if (comp instanceof JComboBox)
            {
                ((JComboBox<?>) comp).setSelectedIndex(-1);
            }
            else if (comp instanceof JTextField)
            {
                ((JTextField) comp).setText("");
            }
        }

        saltMeasurement_JComboBox.setSelectedIndex(-1);

        extraClearIngredientsForm();
    }

    protected void extraClearIngredientsForm()
    {

    }

    protected boolean validate_IngredientsForm()//HELLO EDITED NOW
    {
        String errorTxt = "", ingredientName_Txt = "";

        //##############################
        // Validation Input Fields
        //##############################

        int rowPos = 0;
        for (Map.Entry<String, Object[]> info : ingredientsFormObjectAndValues.entrySet())
        {
            rowPos++;

            Object[] row = info.getValue();
            Component comp = (Component) row[0];

            String value = "", rowLabel = info.getKey();


            if (comp instanceof JComboBox)
            {
                JComboBox comboBox = (JComboBox) comp;

                if (comboBox.getSelectedIndex()==-1) // if no item has been selected by JComboBox
                {
                    errorTxt += String.format("\n\n  ' %s ' on Row: %s, an option inside the dropdown menu must be selected", rowLabel, rowPos);
                }
                continue;
            }
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
                //
                //#########################################
                if (rowLabel.equals("Ingredient Name"))
                {
                    ingredientName_Txt = value;
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
                            errorTxt += String.format("\n\n  ' %s ' on Row: %s,  must be an Integer value which is: 0<= value <=100 !", rowLabel, rowPos);
                        }
                    }
                    catch (Exception e)
                    {
                        errorTxt += String.format("\n\n  ' %s ' on Row: %s,  must be an Integer value which is: 0<= value <=100 !", rowLabel, rowPos);
                        errorTxt += String.format("\n\n  ' %s ' on Row: %s,  must be an Integer value which is: 0<= value <=100 !", rowLabel, rowPos);
                    }
                    continue;
                }

                //#########################################
                // Change salt value based on mg to g
                //#########################################
                else if (rowLabel.equals("Salt"))
                {
                    if (saltMeasurement_JComboBox.getSelectedIndex()==-1)
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
                // Do BigDecimal Processing
                //#########################################
                errorTxt += convertToBigDecimal(value, rowLabel, rowPos, jTextField, false);
            }
        }

        //####################################################
        //  ingredientName checks
        //####################################################
        if (!(ingredientName_Txt.equals("")))
        {
            if (doesStringContainGivenCharacters(ingredientName_Txt, "'"))
            {
                errorTxt += String.format("\n\n  Ingredient named \"%s\" cannot contain the symbol \" ' \"!", ingredientName_Txt);
            }
            else
            {
                errorTxt = extra_Validation_IngredientName(errorTxt, ingredientName_Txt);
            }
        }

        //####################################################
        // Check if any error were found & Process it
        //####################################################
        if (errorTxt.length()==0)
        {
            return true;
        }

        JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), String.format("\n\nPlease fix the following rows being; \n%s", errorTxt));
        return false;
    }

    protected String extra_Validation_IngredientName(String errorTxt, String ingredientName)
    {
        //####################################################
        //Check if IngredientName Already exists in DB
        //####################################################
        if (checkIfIngredientNameInDB(ingredientName))
        {
            errorTxt += String.format("\n\n  Ingredient named %s already exists within the database!", ingredientName);
        }

        return errorTxt;
    }

    protected boolean checkIfIngredientNameInDB(String ingredientName)
    {
        ingredientName = removeSpaceAndHiddenChars(ingredientName);
        String query = String.format("SELECT Ingredient_Name FROM ingredients_info WHERE Ingredient_Name = \"%s\";", ingredientName);

        System.out.printf("\n\n%s", query);

        if (db.getSingleColumnQuery(query)!=null)
        {
            return true;
        }
        return false;
    }

    protected String get_IngredientsForm_UpdateString(String ingredientID) // HELLO needs further update methods created for gui
    {
        //####################################
        // Creating Upload Query
        //####################################
        String
                ingredientTypeSet = "SELECT Ingredient_Type_ID FROM ingredientTypes WHERE Ingredient_Type_Name = \"",
                insertQuery = "INSERT INTO ingredients_info VALUES";

        ingredientsValuesBeingAdded = "\n(null,";
        //####################################
        //
        //####################################
        int pos = -1, listSize = ingredientsFormLabelsMapsToValues.size(), endPos = listSize - 1;
        for (Map.Entry<String, Triplet<String, String, String>> entry : ingredientsFormLabelsMapsToValues.entrySet())
        {
            pos++;

            //####################################
            //
            //####################################
            String key = entry.getKey();
            Triplet<String, String, String> value = entry.getValue();
            String mysqlColumnDataType = value.getValue2();
            Component formObject = (Component) ingredientsFormObjectAndValues.get(key)[0];

            //####################################
            //
            //####################################
            String formFieldValue = "";

            if (formObject instanceof JTextField)
            {
                formFieldValue = ((JTextField) formObject).getText().trim();
            }
            else if (formObject instanceof JComboBox)
            {
                formFieldValue = ((JComboBox) formObject).getSelectedItem().toString();
            }

            //####################################
            //
            //####################################
            String fieldsQueryEx = "";

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
            if (pos==endPos)
            {
                ingredientsValuesBeingAdded += String.format("%s);", fieldsQueryEx);
                continue;
            }

            //####################################
            //
            //####################################
            ingredientsValuesBeingAdded += String.format("%s,", fieldsQueryEx);
        }

        //####################################
        // Return results
        //####################################
        return (insertQuery += ingredientsValuesBeingAdded);
    }

    protected String removeSpaceAndHiddenChars(String stringToBeEdited)
    {
        return stringToBeEdited.trim().replaceAll("\\p{C}", ""); // remove all whitespace & hidden characters like \n
    }


    //##################################################################################################################
    // Accessor Methods
    //##################################################################################################################
    protected JComboBox getIngredientTypeJComboBox()
    {
       return  (JComboBox) ingredientsFormObjectAndValues.get("Ingredient Type")[0];
    }

    protected JTextField getIngredientNameJTextfield()
    {
        return  (JTextField) ingredientsFormObjectAndValues.get("Ingredient Name")[0];
    }

    protected JTextField getIngredientTypeJTextField()
    {
        return  (JTextField) ingredientsFormObjectAndValues.get("Ingredient Type")[0];
    }



    protected ArrayList<Component> getIngredientsFormObjects()
    {
        return ingredientsFormObjects;
    }

    protected int getIngredientNameObjectIndex()
    {
        return ingredientNameObjectIndex;
    }

    protected int getIngredientTypeObjectIndex()
    {
        return ingredientTypeObjectIndex;
    }

    protected int getIngredientSaltObjectIndex()
    {
        return ingredientSaltObjectIndex;
    }

    protected String getIngredientsValuesBeingAdded()
    {
        return ingredientsValuesBeingAdded;
    }
}