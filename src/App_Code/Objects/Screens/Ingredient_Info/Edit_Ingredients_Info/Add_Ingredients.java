package App_Code.Objects.Screens.Ingredient_Info.Edit_Ingredients_Info;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Gui_Objects.*;
import App_Code.Objects.Screens.Ingredient_Info.SearchForFoodInfo;
import App_Code.Objects.Screens.Others.Meal_Plan_Screen;
import org.javatuples.Triplet;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Collator;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Add_Ingredients extends JPanel
{
    protected int yPos = 0;
    protected JPanel scrollPaneJPanel;
    protected GridBagConstraints gbc = new GridBagConstraints();

    protected boolean formEditable = false, updateIngredientsForm = false, updateShops = false;

    private final int totalNumbersAllowed = 7, decimalScale = 2, decimalPrecision = totalNumbersAllowed - decimalScale, charLimit = 8;

    protected IngredientsForm ingredientsForm;
    private ShopForm shopForm;
    protected SearchForFoodInfo searchForIngredientInfo;


    //##############################################################
    // NEW SHIT
    //##############################################################
    protected Parent_Ingredients_Info_Screen parent;
    protected Meal_Plan_Screen mealPlanScreen;
    protected MyJDBC db;
    protected Integer planID, tempPlanID;
    protected String planName;


    //#################################################################################################################
    // Constructor
    //##################################################################################################################

    Add_Ingredients(Parent_Ingredients_Info_Screen parent, MyJDBC db)
    {
        //###################################################################################
        //   Parent Stuff
        //###################################################################################
        this.db = db;
        this.parent = parent;

        //###################################################################################
        //   Parent Stuff
        //###################################################################################
        this.planID = parent.getPlanID();
        this.tempPlanID = parent.getTempPlanID();
        this.planName = parent.getPlanName();

        this.mealPlanScreen = parent.getMealPlanScreen();

        //###################################################################################
        //   Create Screen for Interface
        //###################################################################################

        setLayout(new BorderLayout());

        //###################################################################################
        //   Create Main Centre Screen for Interface
        //##################################################################################
        JPanel mainCentreScreen = new JPanel(new GridBagLayout());
        add(mainCentreScreen, BorderLayout.CENTER);

        //##########################################################
        // Create ScrollPane & add to Interface
        //#########################################################
        ScrollPaneCreator scrollPane = new ScrollPaneCreator();
        scrollPaneJPanel = scrollPane.getJPanel();
        scrollPaneJPanel.setLayout(new GridBagLayout());
        addToContainer(mainCentreScreen, scrollPane, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0);

        //#################################
        // Needs to be created near this
        //##################################
//            ingredientsForm = new IngredientsForm(this, "Add Ingredients Info", 250, 50);// HELLO REMOVE
        ingredientsForm = new IngredientsForm(scrollPaneJPanel, "Add Ingredients Info", 250, 50);

        shopForm = new ShopForm(scrollPaneJPanel, "Add Ingredient Suppliers", 250, 50);

        searchForIngredientInfo = new SearchForFoodInfo(scrollPaneJPanel, ingredientsForm, "Search For Food Info", 250, 50);

        createForms(ingredientsForm, shopForm, searchForIngredientInfo);
    }

    //####################################################
    // Methods
    //###################################################

    protected void updateIngredientForm_Type_JComboBox()
    {
        ingredientsForm.reloadIngredientTypeJComboBox();
    }

    protected void createForms(IngredientsForm ingredientsForm, ShopForm shopForm, SearchForFoodInfo searchForFoodInfo)
    {
        //##################################################################################
        // Creating Parts of screen & adding it to interface
        //##################################################################################

        //###########################
        //Search For Ingredients form
        //###########################

        addToContainer(scrollPaneJPanel, searchForFoodInfo, 0, yPos += 1, 1, 1, 0.25, 0.25, "both", 0, 0);

        //###########################
        //Space Divider
        //###########################
        addToContainer(scrollPaneJPanel, new JPanel(), 0, yPos += 1, 1, 1, 0.25, 0.25, "both", 10, 0);

        //###########################
        //Ingredients form
        //###########################

        addToContainer(scrollPaneJPanel, ingredientsForm, 0, yPos += 1, 1, 1, 0.25, 0.25, "both", 0, 0);

        //###########################
        //Space Divider
        //###########################
        addToContainer(scrollPaneJPanel, new JPanel(), 0, yPos += 1, 1, 1, 0.25, 0.25, "both", 10, 0);

        //###########################
        // Add shop
        //###########################
        addToContainer(scrollPaneJPanel, shopForm, 0, yPos += 1, 1, 1, 0.25, 0.25, "both", 0, 0);

        //###########################
        //Space Divider
        //###########################
        addToContainer(scrollPaneJPanel, new JPanel(), 0, yPos += 1, 1, 1, 0.25, 0.25, "both", 10, 0);

        //###################################################################################
        // South Screen for Interface
        //###################################################################################

        // Creating submit button
        JButton submitButton = new JButton("Submit Form");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14)); // setting font
        submitButton.setPreferredSize(new Dimension(50, 50)); // width, height

        // creating commands for submit button to execute on
        submitButton.addActionListener(ae -> {
            submissionBtnAction();
        });

        add(submitButton, BorderLayout.SOUTH);

        //###################################################################################
        // Resizing GUI
        //###################################################################################
        resize_GUI();
    }

    protected void clearSearchForIngredientInfoForm()
    {
        searchForIngredientInfo.resetFullDisplay();
    }

    protected void clearIngredientsForm()
    {
        ingredientsForm.clearIngredientsForm();
    }

    protected void clearShopForm()
    {
        shopForm.clearShopForm();
    }

    protected void submissionBtnAction()
    {
        if (!areYouSure("add this new Ingredient - this will cause the mealPlan to save its data to the DB"))
        {
            return;
        }

        boolean errorFound = false;

        // ingredientsForm
        if (!(ingredientsForm.validate_IngredientsForm()))
        {
            errorFound = true;
        }

        // ShopForm
        if (!(shopForm.validateForm()))
        {
            errorFound = true;
        }

        if (!errorFound)
        {
            updateShops = false;
            updateIngredientsForm = false; // reset values

            if (!areYouSure("upload these values as they may have been changed / adapted to fit our data type format"))
            {
                return;
            }

            if (updateBothForms(ingredientsForm.get_IngredientsForm_UpdateString(null), shopForm.get_ShopForm_UpdateString()))
            {
                parent.setUpdateIngredientInfo(true);
                JOptionPane.showMessageDialog(mealPlanScreen, "The ingredient updates won't appear on the mealPlan screen until this window is closed!");

                //#####################################
                // Reset Ingredient Names/Types
                //####################################
                String ingredientName = ((JTextField) ingredientsForm.getIngredientsFormObjects().get(1)).getText();
                String ingredientType = (String) ((JComboBox) ingredientsForm.getIngredientsFormObjects().get(2)).getSelectedItem();

                addOrDeleteIngredientFromMap("add", ingredientType, ingredientName);

                //#####################################
                // Reset Form & Update GUI
                //####################################
                refreshInterface();
                resize_GUI();
            }
        }
    }

    protected boolean isUpdateIngredientsForm()
    {
        return updateIngredientsForm;
    }

    protected void setUpdateIngredientsForm(boolean x)
    {
        updateIngredientsForm = x;
    }

    protected void setUpdateShops(boolean x)
    {
        updateShops = x;
    }

    protected boolean isUpdateShops()
    {
        return updateShops;
    }

    protected String convertToBigDecimal(String value, String errorTxt, String rowLabel, int rowNumber, JTextField jTextField)
    {
        String txt = String.format("must be number which has %s numbers in it! Or, a decimal number (%s,%s) with a max of %s numbers before the decimal point and  a of max of  %s numbers after the decimal point!",
                decimalPrecision, decimalPrecision, decimalScale, decimalPrecision, decimalScale);
        try
        {
            BigDecimal zero = new BigDecimal(0);

            //#####################################################
            // Convert Numbers Using Precision & Scale point Values
            //#####################################################
            BigDecimal bdFromString = new BigDecimal(String.format("%s", value));
            int valueScale = bdFromString.scale();
            int valuePrecision = bdFromString.precision();

            if (valueScale > decimalScale) // only round to scale, if needed as otherwise whole numbers get lost etc 5566 = nothing
            {
                bdFromString = bdFromString.setScale(decimalScale, RoundingMode.DOWN); // round the number
            }

            //#####################################################
            // Java Concept Of Precision
            //#####################################################
            if (valueScale == 0 && valuePrecision > decimalPrecision) // the number is too big
            {
                errorTxt += String.format("\n\n  ' %s 'on Row: %s, %s ", rowLabel, rowNumber, txt);
            }

            //#####################################################
            // MySQL Concept Of Precision
            //#####################################################
            else if (valueScale > 0 && bdFromString.setScale(0, RoundingMode.FLOOR).precision() > decimalPrecision)
            {
                errorTxt += String.format("\n\n  ' %s 'on Row: %s, %s ", rowLabel, rowNumber, txt);
            }

            //#####################################################
            // Format Data in Cell
            //#####################################################
            jTextField.setText(String.format("%s", bdFromString));

            //#####################################################
            // Check if the value is bigger than 0
            //#####################################################

            if (bdFromString.compareTo(zero) < 0)// "<")
            {
                errorTxt += String.format("\n\n  ' %s 'on Row: %s, must have a value which is bigger than 0 and %s", rowLabel, rowNumber, txt);
            }
        }
        catch (Exception e)
        {
            errorTxt += String.format("\n\n  ' %s 'on Row: %s, %s ", rowLabel, rowNumber, txt);
        }

        return errorTxt;
    }

    protected Boolean areYouSure(String process)
    {
        int reply = JOptionPane.showConfirmDialog(mealPlanScreen, String.format("Are you sure you want to: %s?", process, process),
                "Confirmation", JOptionPane.YES_NO_OPTION); //HELLO Edit

        if (reply == JOptionPane.NO_OPTION || reply == JOptionPane.CLOSED_OPTION)
        {
            return false;
        }
        return true;
    }

    //HELLO EDIT
    protected boolean addOrDeleteIngredientFromMap(String process, String ingredientType, String ingredientName)
    {
        // Storing
        Collection<String> ingredientTypeList = parent.getMapIngredientTypesToNames().get(ingredientType);
        if (process.equals("add"))// if key exists add the ingredientName in
        {

            if (ingredientTypeList != null)
            {
                // Add ingredientName to collection
                ingredientTypeList.add(ingredientName);
            }
            else // create the list for the new type and add ingredient in
            {
                ingredientTypeList = new TreeSet<>(Collator.getInstance());
                ingredientTypeList.add(ingredientName);
                parent.getMapIngredientTypesToNames().put(ingredientType, ingredientTypeList);
            }
        }
        else if (process.equals("delete"))
        {
            ingredientTypeList.remove(ingredientName);

            // Remove List as there is no items in it
            if (ingredientTypeList.size() == 0)
            {
                parent.getMapIngredientTypesToNames().remove(ingredientType);
            }
        }

        parent.update_Edit_IngredientsTypes();
        return true;
    }

    private void refreshInterface() // only available to reset screen
    {
        clearSearchForIngredientInfoForm();
        clearIngredientsForm();
        clearShopForm();
    }

    protected boolean updateBothForms(String updateIngredients_String, String[] updateIngredientShops_String)
    {
        System.out.printf("\n\n%s", updateIngredients_String, Arrays.toString(updateIngredientShops_String));

        //####################################
        // Error forming update String (exit)
        //####################################

        if (!updateShops || !updateIngredientsForm)
        {
            return false;
        }

        //####################################
        // Uploading Query
        //####################################
        if (!(db.uploadData_Batch_Altogether(new String[]{updateIngredients_String})))
        {
            JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "Failed 2/2 Updates - Unable To Add Ingredient Info In DB!");
            return false;
        }

        if (updateIngredientShops_String != null)
        {
            if (!(db.uploadData_Batch_Independently(updateIngredientShops_String)))
            {
                JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "Failed 1/2 Updates - Unable To Add Shop Supplier For Ingredient In DB!");
                return false;
            }
        }

        JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "\n\nUpdated Ingredient Info! \n\nAlso updated 2/2 Shop Info In DB In DB!!!");

        return true;
    }

    protected void resize_GUI()
    {
        parent.getContentPane().revalidate();
        scrollPaneJPanel.revalidate();
        revalidate();
    }

    //#################################################################################################################
    // IngredientsForm
    //##################################################################################################################
    public class IngredientsForm extends CollapsibleJPanel
    {
        protected LinkedHashMap<String, Triplet<String, String, String>> ingredientsFormLabelsMapsToValues = new LinkedHashMap<>()
        {{
            // ingredientsFormLabel Key -> ( nutritionIx value, Mysql key Value, mySql Field Datatype)

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
        
        protected int ingredientNameObjectIndex, ingredientTypeObjectIndex, glycemicObjectIndex, ingredientSaltObjectIndex, ingredientMeasurementObjectIndex;

        protected JComboBox<String>
                ingredientsMeasure_JComboBox = new JComboBox(),
                ingredientsType_JComboBox = new JComboBox(),
                saltMeasurement_JComboBox = new JComboBox();


        protected ArrayList<Component> ingredientsFormObjects = new ArrayList<>();

        private JPanel northPanel = new JPanel(new GridBagLayout());


        public IngredientsForm(Container parentContainer, String btnText, int btnWidth, int btnHeight)
        {
            super(parentContainer, btnText, btnWidth, btnHeight);

            //##################################################
            //
            //##################################################
            int found = 0, pos = -1;
            for (String key : ingredientsFormLabelsMapsToValues.keySet())
            {
                pos++;
                if (key.equals("Ingredient Name"))
                {
                    found++;
                    ingredientNameObjectIndex = pos;
                }
                else if (key.equals("Ingredient Type"))
                {
                    found++;
                    ingredientTypeObjectIndex = pos;
                }
                else if (key.equals("Glycemic Index"))
                {
                    found++;
                    glycemicObjectIndex = pos;
                }
                else if (key.equals("Ingredient Measurement In"))
                {
                    found++;
                    ingredientMeasurementObjectIndex = pos;
                }

                else if (key.equals("Salt"))
                {
                    found++;
                    ingredientSaltObjectIndex = pos;
                }

                if (found == 5)
                {
                    break;
                }
            }

            //##################################################
            //
            //##################################################
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

            int lblSize = ingredientsFormLabelsMapsToValues.size();
            JPanel inputArea = new JPanel(new GridBagLayout());

            // for each label it is created into a JLabel
            int xPos = -1, yPos = -1;
            for (String key : ingredientsFormLabelsMapsToValues.keySet())
            {
                yPos++;
                xPos = -1;
                //#########################################
                //
                //#########################################
                boolean jcomboxBeingCreated = false;
                JTextField textField = new JTextField("");
                JComboBox comboBox = null;

                //#########################################
                // JLabel Column 1
                //#########################################

                String labelTXT = key + ":";

                JLabel label = new JLabel("    " + labelTXT);
                label.setHorizontalAlignment(JLabel.LEFT);
                label.setFont(new Font("Verdana", Font.BOLD, 14));

                addToContainer(inputArea, label, xPos += 1, yPos, 1, 1, 0.25, 0.25, "horizontal", 0, 0);
//                inputArea.add(label);

                //#########################################
                // JComboField Column 2
                //#########################################

                if (yPos == ingredientMeasurementObjectIndex)
                {
                    String ingredientMeassurements[] = {"Litres", "Grams"};
                    ingredientsMeasure_JComboBox = new JComboBox(ingredientMeassurements);
                    addToContainer(inputArea, ingredientsMeasure_JComboBox, xPos += 1, yPos, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

                    jcomboxBeingCreated = true;
                    comboBox = ingredientsMeasure_JComboBox;
                    ingredientsFormObjects.add(comboBox);
                }
                else if (yPos == ingredientTypeObjectIndex)
                {
                    ingredientsType_JComboBox = new JComboBox();
                    reloadIngredientTypeJComboBox();
                    addToContainer(inputArea, ingredientsType_JComboBox, xPos += 1, yPos, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

                    jcomboxBeingCreated = true;
                    comboBox = ingredientsType_JComboBox;
                    ingredientsFormObjects.add(comboBox);
                }
                else if (yPos == ingredientSaltObjectIndex)
                {
                    // JPanel for salt JComboBox & JTextfield
                    JPanel saltJPanelArea = new JPanel(new GridLayout(2, 1));

                    // Creating & Adding JComboBox
                    jcomboxBeingCreated = true;
                    String[] saltMeasurements = {"mg", "g"};
                    saltMeasurement_JComboBox = new JComboBox(saltMeasurements);
                    saltJPanelArea.add(saltMeasurement_JComboBox);

                    comboBox = saltMeasurement_JComboBox;
//                    ingredientsFormObjects.add(comboBox);

                    // Creating JTextfield & Adding JTextfield
                    textField.setDocument(new JTextFieldLimit(charLimit));
                    saltJPanelArea.add(textField);
                    ingredientsFormObjects.add(textField);

                    // Adding JPanel to GUI
                    addToContainer(inputArea, saltJPanelArea, xPos += 1, yPos, 1, 1, 0.25, 0.25, "both", 0, 0);
                }

                // if a JComboBox is being created Centre JComboBox Items & Set Selected Item to 0
                if (jcomboxBeingCreated)
                {
                    // Centre JComboBox Item
                    DefaultListCellRenderer listRenderer = new DefaultListCellRenderer();
                    listRenderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER); // center-aligned items
                    comboBox.setRenderer(listRenderer);

                    // Set Selected Item To Nothing
                    comboBox.setSelectedIndex(-1);
                    continue;
                }

                //#########################################
                // JTextFields Column 2
                //#########################################
                if (labelTXT.equals("Ingredient Name:")) //Setting TextField limits
                {
                    textField.setDocument(new JTextFieldLimit(255));
                    ingredientNameObjectIndex = yPos;
                }
                else
                {
                    textField.setDocument(new JTextFieldLimit(charLimit));
                }

                ingredientsFormObjects.add(textField);
//                inputArea.add(textField);
                addToContainer(inputArea, textField, xPos += 1, yPos, 1, 1, 0.25, 0.25, "horizontal", 0, 0);
            }
            mainJPanel.add(inputArea, BorderLayout.CENTER);
        }

        protected void reloadIngredientTypeJComboBox()
        {
            ingredientsType_JComboBox.removeAllItems();
            for (String ingredientType : parent.getIngredientsTypesList())
            {
                if (ingredientType.equals("None Of The Above"))
                {
                    continue;
                }

                ingredientsType_JComboBox.addItem(ingredientType);
            }

            ingredientsType_JComboBox.setSelectedIndex(-1);
        }

        protected boolean doesStringContainCharacters(String input)
        {
            Pattern p1 = Pattern.compile("[^a-zA-Z]", Pattern.CASE_INSENSITIVE);
            Matcher m1 = p1.matcher(input.replaceAll("\\s+", ""));
            boolean b1 = m1.find();

            if (b1)
            {
                return true;
            }

            return false;
        }

        public void update_IngredientForm_FromSearch(LinkedHashMap<String, Object> foodInfo)//HELLO EDITED NOW
        {
            if (foodInfo != null)
            {
                //#######################################################################
                // Set Form Values to 0 in case no value match below
                //#######################################################################

                int labelSize = ingredientsFormLabelsMapsToValues.size();

                clearIngredientsForm();

                for (int i = 0; i < labelSize; i++)
                {
                    Component comp = ingredientsFormObjects.get(i);

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
                    formLabelPos++;

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
                    Component comp = ingredientsFormObjects.get(formLabelPos);

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

            IconButton refresh_Icon_Btn = new IconButton("src/images/refresh/++refresh.png", "", width, height, width, height,
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
            for (int i = 0; i < ingredientsFormObjects.size(); i++)
            {
                Component comp = ingredientsFormObjects.get(i);

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
        }

        protected boolean validate_IngredientsForm()//HELLO EDITED NOW
        {
            if (tempPlanID == null && planID == null && planName == null)
            {
                JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "Please Select A Plan First!");
                return false;
            }

            String errorTxt = "", ingredientName_Txt = "";

            //##############################
            // Validation Input Fields
            //##############################            

            int row = -1;
            for (String ingredientFormLabel : ingredientsFormLabelsMapsToValues.keySet())
            {
                row++;

                String value = "";
                Component comp = ingredientsFormObjects.get(row);

                if (comp instanceof JComboBox)
                {
                    JComboBox comboBox = (JComboBox) comp;

                    if (comboBox.getSelectedIndex() == -1) // if no item has been selected by JComboBox
                    {
                        errorTxt += String.format("\n\n  ' %s ' on Row: %s, an option inside the dropdown menu must be selected", ingredientFormLabel, row + 1);
                    }
                    continue;
                }
                else if (comp instanceof JTextField)
                {
                    JTextField jTextField = (JTextField) comp;
                    value = jTextField.getText().trim();

                    //#########################################
                    // Check if JTextfield input is empty
                    //#########################################
                    if (value.equals(""))
                    {
                        errorTxt += String.format("\n\n  ' %s ' on Row: %s,  must have a value which is not ' NULL '!", ingredientFormLabel, row + 1);
                        continue;
                    }

                    //#######################################
                    //
                    //#########################################

                    if (row == ingredientNameObjectIndex)
                    {
                        ingredientName_Txt = value;
                        continue;
                    }

                    if (row == glycemicObjectIndex)
                    {
                        try
                        {
                            int intValue = Integer.valueOf(value);

                            if (intValue > 100 || intValue < 0)
                            {
                                errorTxt += String.format("\n\n  ' %s ' on Row: %s,  must be an Integer value which is: 0<= value <=100 !", ingredientFormLabel, row + 1);
                            }
                        }
                        catch (Exception e)
                        {
                            errorTxt += String.format("\n\n  ' %s ' on Row: %s,  must be an Integer value which is: 0<= value <=100 !", ingredientFormLabel, row + 1);
                            errorTxt += String.format("\n\n  ' %s ' on Row: %s,  must be an Integer value which is: 0<= value <=100 !", ingredientFormLabel, row + 1);
                        }
                        continue;
                    }

                    //#########################################
                    // Change salt value based on mg to g
                    //#########################################
                    else if (row == ingredientSaltObjectIndex)
                    {

                        if (saltMeasurement_JComboBox.getSelectedIndex() == -1)
                        {
                            errorTxt += String.format("\n\n  ' %s ' on Row: %s, an option for the measurement chosen for salt needs to be selected in the dropdown box!", ingredientFormLabel, row + 1);
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

                    errorTxt = convertToBigDecimal(value, errorTxt, ingredientFormLabel, row + 1, jTextField); // HELLO SHouldnt this be +=
                }
            }

            //####################################################
            //Check if ingredient name in DB
            //####################################################
            errorTxt = extra_Validation_IngredientName(errorTxt, ingredientName_Txt);

            //####################################################
            //Check if any error were found & Process it
            //####################################################

            if (errorTxt.length() == 0)
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
            if ( ingredientName != null || !(ingredientName.equals("")))
            {
                if(checkIfIngredientNameInDB(ingredientName))
                {
                    errorTxt += String.format("\n\n  Ingredient named %s already exists within the database!", ingredientName);
                }
            }
            return errorTxt;
        }
        
        protected boolean checkIfIngredientNameInDB(String ingredientName)
        {
            ingredientName = removeSpaceAndHiddenChars(ingredientName);
            String query = String.format("SELECT Ingredient_Name FROM ingredients_info WHERE Ingredient_Name = '%s';", ingredientName);

            System.out.printf("\n\n%s", query);

            if (db.getSingleColumnQuery(query) != null)
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
            int i = 0;

            String ingredientTypeSet = "SELECT Ingredient_Type_ID FROM ingredientTypes WHERE Ingredient_Type_Name = '";
            String tableName = "ingredients_info";


            String insertQuery = String.format("""
                    INSERT INTO %s
                    (""", tableName);

            String fieldsQuery = "\nValues \n(";

            //####################################
            //
            //####################################
            int pos = -1, listSize = ingredientsFormLabelsMapsToValues.size();
            for (Map.Entry<String, Triplet<String, String, String>> entry : ingredientsFormLabelsMapsToValues.entrySet())
            {
                pos++;
                Triplet<String, String, String> value = entry.getValue();

                String sqlColumnName = value.getValue1();
                String mysqlColumnDataType = value.getValue2();

                //####################################
                //
                //####################################
                Component formObject = ingredientsFormObjects.get(pos);
                String formFieldValue = "";
                if (formObject instanceof JTextField)
                {
                    formFieldValue = ((JTextField) formObject).getText();
                }
                else if (formObject instanceof JComboBox)
                {
                    formFieldValue = ((JComboBox) formObject).getSelectedItem().toString();
                }

                //####################################
                //
                //####################################
                String fieldsQueryEx = "";

                if (pos == ingredientTypeObjectIndex)
                {
                    fieldsQueryEx = String.format("\n\t(%s%s')", ingredientTypeSet, formFieldValue);
                }
                else
                {
                    fieldsQueryEx = mysqlColumnDataType.equals("String")
                            ?
                            String.format("\n\t('%s')", formFieldValue)
                            :
                            String.format("\n\t(%s)", formFieldValue);
                }

                //####################################
                //
                //####################################

                if (pos == listSize - 1)
                {
                    insertQuery += String.format("%s)", sqlColumnName);

                    fieldsQuery += String.format("%s\n);", fieldsQueryEx);

                    continue;
                }

                //####################################
                //
                //####################################
                insertQuery += String.format("%s, ", sqlColumnName);
                fieldsQuery += String.format("%s,", fieldsQueryEx);
            }

            //####################################
            //
            //####################################

            updateIngredientsForm = true; //HELLO EDIT NOW

            //####################################
            // Return results
            //####################################
            return (insertQuery += fieldsQuery);
        }

        protected String removeSpaceAndHiddenChars(String stringToBeEdited)
        {
            return stringToBeEdited.trim().replaceAll("\\p{C}", ""); // remove all whitespace & hidden characters like \n
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
    }

    //#################################################################################################################
    // ShopForm
    //##################################################################################################################
    public class ShopForm extends CollapsibleJPanel
    {
        protected int objectID = 0;

        protected HashMap<Integer, JComboBox> shopJComboBoxes = new HashMap<>();
        protected HashMap<Integer, JTextField> prices = new HashMap<>();
        protected HashMap<Integer, JTextField> quantityPerPack = new HashMap<>();

        protected ArrayList<AddShopForm_Object> addShopFormObjects = new ArrayList<>();

        protected Container parentContainer;
        protected JPanel inputArea;

        public ShopForm(Container parentContainer, String btnText, int btnWidth, int btnHeight)
        {
            super(parentContainer, btnText, btnWidth, btnHeight);
            this.parentContainer = parentContainer;

            //##########################################################################################
            // Creating Form
            //##########################################################################################
            JPanel mainJPanel = this.getCentreJPanel();
            mainJPanel.setLayout(new BorderLayout());

            //##########################################################################################
            // North Frame
            //##########################################################################################

            // Creating North JPanel Area with 2 rows
            JPanel northPanel = new JPanel(new GridBagLayout());
            mainJPanel.add(northPanel, BorderLayout.NORTH);

            //#####################################################
            // Creating area for North JPanel (title area)
            //#####################################################
            JLabel titleLabel = new JLabel("Add Suppliers");
            titleLabel.setFont(new Font("Verdana", Font.PLAIN, 24));
            titleLabel.setHorizontalAlignment(JLabel.CENTER);

            JPanel titlePanel = new JPanel();
            titlePanel.setBackground(Color.green);
            titlePanel.add(titleLabel);

            // Add title JPanel to North Panel Area
            addToContainer(northPanel, titlePanel, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0);

            //#####################################################
            // Creating area for North JPanel (Add Icon)
            //#####################################################
            inputArea = new JPanel(new GridBagLayout());

            IconPanel iconPanel = new IconPanel(1, 10, "East");
            JPanel iconPanelInsert = iconPanel.getIconJpanel();

            addToContainer(northPanel, iconPanel.getIconAreaPanel(), 0, 1, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

            //##########################
            // Refresh Icon
            //##########################
            int width = 30;
            int height = 30;

            IconButton add_Icon_Btn = new IconButton("src/images/add/++add.png", "", width, height, width, height,
                    "centre", "right"); // btn text is useless here , refactor

            JButton add_Btn = add_Icon_Btn.returnJButton();
            add_Icon_Btn.makeBTntransparent();

            add_Btn.addActionListener(ae -> {
                addShopForm_object();
            });

            iconPanelInsert.add(add_Icon_Btn);

            //##########################################################################################
            // Centre Form
            //##########################################################################################
            mainJPanel.add(inputArea, BorderLayout.CENTER);

            addToContainer(inputArea, new AddShopForm_Object(inputArea, false), 0, objectID, 1, 1, 0.25, 0.25, "horizontal", 0, 0);
        }

        protected AddShopForm_Object addShopForm_object()
        {
            AddShopForm_Object obj = new AddShopForm_Object(inputArea, true);
            addToContainer(inputArea, obj, 0, objectID, 1, 1, 0.25, 0.25, "horizontal", 0, 0);
            return obj;
        }

        private void setObjectID(int objectID)
        {
            this.objectID = objectID;
        }

        protected boolean validateForm()
        {
            boolean noError = true;

            if (!(validatePrices()))
            {
                noError = false;
            }

            if (!(validateShops()))
            {
                noError = false;
            }

            if (!(validateQuantity()))
            {
                noError = false;
            }

            return noError;
        }

        private boolean validatePrices()
        {
            String errorTxt = "";
            BigDecimal zero = new BigDecimal(0);
            int i = 1;

            for (Integer key : prices.keySet())
            {
                JTextField jTextField = prices.get(key);
                String value = jTextField.getText().trim();

                //#########################################
                // Check if JTextfield input is empty
                //#########################################
                if (value.equals(""))
                {
                    errorTxt += String.format("\n\nOn Row: %s, the 'price' must have a value which is not ' NULL '!", i);
                    i++;
                    continue;
                }

                //#########################################
                // Do BigDecimal Processing
                //#########################################
                errorTxt = convertToBigDecimal(value, errorTxt, "Prices", i, jTextField);

                i++;
            }

            if (errorTxt.equals(""))
            {
                return true;
            }

            JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), String.format("\n\nPlease fix the following rows being; \n%s", errorTxt));
            return false;
        }

        private boolean validateQuantity()
        {
            String errorTxt = "";
            BigDecimal zero = new BigDecimal(0);
            int i = 1;

            for (Integer key : quantityPerPack.keySet())
            {
                JTextField jTextField = quantityPerPack.get(key);
                String value = jTextField.getText().trim();

                //#########################################
                // Check if JTextfield input is empty
                //#########################################
                if (value.equals(""))
                {
                    errorTxt += String.format("\n\nOn Row: %s, the 'Quantity' must have a value which is not ' NULL '!", i);
                    i++;
                    continue;
                }

                //#########################################
                // Do BigDecimal Processing
                //#########################################
                errorTxt = convertToBigDecimal(value, errorTxt, "Quantity", i, jTextField);

                i++;
            }


            if (errorTxt.equals(""))
            {
                return true;
            }

            JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), String.format("\n\nPlease fix the following rows being; \n%s", errorTxt));
            return false;
        }

        private boolean validateShops()
        {
            String errorTxt = "";

            int i = 1;
            ArrayList<String> chosenShops = new ArrayList<>();
            for (Integer key : shopJComboBoxes.keySet())
            {
                String chosenItem = shopJComboBoxes.get(key).getSelectedItem().toString();

                if (chosenItem.equals("No Shop"))
                {
                    errorTxt += String.format("\nOn Row %s,  please Select a shop that isnt 'No Shop'! Or, delete the row!", i);
                }

                if (chosenShops.contains(chosenItem))
                {
                    errorTxt += String.format("\nOn Row %s, there is also another row/rows with with the supplier %s - no duplicate stores!", i, chosenItem);
                }
                else
                {
                    chosenShops.add(chosenItem);
                }

                i++;
            }

            if (errorTxt.equals(""))
            {
                return true;
            }

            JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), String.format("\n\nPlease fix the following rows being; \n%s", errorTxt));
            return false;
        }

        private String[] get_ShopForm_UpdateString()
        {
            //########################################
            // Nothing to Update
            //########################################
            if (!updateIngredientsForm)
            {
                return null;
            }
            else if (prices.size() == 0)
            {
                updateShops = true;
                return null;
            }

            //#############################################################
            // Create Update  String
            //############################################################
            String mysqlVariableReference = "@newIngredientID";
            String createMysqlVariable = String.format("SET %s = (SELECT MAX(IngredientID) FROM ingredients_info);", mysqlVariableReference);
            String updateString = String.format("""
                    INSERT INTO ingredientInShops (IngredientID, Volume_Per_Unit, Cost_Per_Unit, StoreID)
                    VALUES """);

            ///#################################
            // Creating String for Add Values
            //#################################
            String values = "";
            int listSize = prices.size(), pos = 0;

            for (Integer key : prices.keySet())
            {
                pos++;
                values += String.format("(%s, %s, %s, (SELECT StoreID FROM stores WHERE Store_Name = '%s'))",
                        mysqlVariableReference, quantityPerPack.get(key).getText(), prices.get(key).getText(), shopJComboBoxes.get(key).getSelectedItem().toString());

                if (pos == listSize)
                {
                    values += ";";
                    continue;
                }

                values += ",";
            }

            updateString += values;

            //############################################################
            // Return values
            //############################################################

            updateShops = true;

            System.out.printf("\n\n%s \n\n%s", createMysqlVariable, updateString);

            return new String[]{createMysqlVariable, updateString};
        }

        public void clearShopForm()
        {
            Iterator<AddShopForm_Object> it = addShopFormObjects.iterator();
            while (it.hasNext())
            {
                AddShopForm_Object i = it.next();
                i.removeFromParentContainer();
                it.remove();
            }

            parentContainer.revalidate();
        }

        class AddShopForm_Object extends JPanel
        {
            private int posY = 0, id;
            Integer PDID = null;  //EDIT NOW

            Container parentContainer;
            JComboBox<String> shops_JComboBox;
            JTextField ingredientPrice_TxtField, quantityPerPack_TxtField;

            AddShopForm_Object(Container parentContainer, boolean addRow)
            {
                this.parentContainer = parentContainer;
                setObjectID(objectID += 1);
                this.id = getObjectID();

                addRow(addRow);
            }

            //EDIT NOW
            protected void setPDID(Integer PDID)
            {
                this.PDID = PDID;
            }

            //EDIT NOW
            protected Integer getPDID()
            {
                return PDID;
            }

            protected int getObjectID()
            {
                return objectID;
            }

            private void addRow(boolean addRowBool)
            {
                //#########################################################################################################
                //Creating JPanel
                //#########################################################################################################
                JPanel rowPanel = this;
                rowPanel.setLayout(new BorderLayout());

                //###############################
                // Creating Sections for GUI
                //###############################

                JPanel eastPanel = new JPanel();
                eastPanel.setLayout(new GridLayout(1, 1));
                eastPanel.setBorder(new EmptyBorder(0, 10, 0, 10));
                eastPanel.setPreferredSize(new Dimension(120, 34)); // width, height
                rowPanel.add(eastPanel, BorderLayout.EAST);

                JPanel centrePanel = new JPanel();
                GridLayout layout = new GridLayout(1, 2);
                layout.setHgap(10);
                centrePanel.setLayout(layout);
                rowPanel.add(centrePanel, BorderLayout.CENTER);

                JPanel westPanel = new JPanel();
                westPanel.setLayout(new GridLayout(1, 1));
                westPanel.setBorder(new EmptyBorder(0, 0, 0, 10));
                westPanel.setPreferredSize(new Dimension(150, 25)); // width, height
                rowPanel.add(westPanel, BorderLayout.WEST);

                if (addRowBool)
                {
                    //#####################################################
                    // West Side
                    //######################################################

                    if (parent.getStoresNamesList() == null)
                    {
                        JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "Unable To Get ShopNames From DB; \nEither No Shops Exist. \nOr, Internal DB Error");
                        return;
                    }

                    //########################
                    // create JComboBox
                    //########################
                    shops_JComboBox = new JComboBox<String>();
                    loadStoresInJComboBox();

                    ((JLabel) shops_JComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
                    westPanel.add(shops_JComboBox);
                    shopJComboBoxes.put(id, shops_JComboBox);

                    //#####################################################
                    // Centre Side
                    //######################################################
                    ingredientPrice_TxtField = new JTextField();
                    ingredientPrice_TxtField.setDocument(new JTextFieldLimit(charLimit));
                    ingredientPrice_TxtField.setText("0.00");
                    prices.put(id, ingredientPrice_TxtField);
                    centrePanel.add(ingredientPrice_TxtField);

                    quantityPerPack_TxtField = new JTextField();
                    quantityPerPack_TxtField.setDocument(new JTextFieldLimit(charLimit));
                    quantityPerPack_TxtField.setText("0.00");
                    quantityPerPack.put(id, quantityPerPack_TxtField);
                    centrePanel.add(quantityPerPack_TxtField);

                    //#####################################################
                    // East Side
                    //######################################################

                    // Creating submit button
                    JButton deleteRowBtn = new JButton("Delete Row");
                    deleteRowBtn.setFont(new Font("Arial", Font.BOLD, 12)); // setting font

                    // creating commands for submit button to execute on
                    deleteRowBtn.addActionListener(ae -> {
                        deleteRowAction();
                    });

                    eastPanel.add(deleteRowBtn);

                    //#####################################################
                    // Adding row to memory
                    //######################################################
                    addShopFormObjects.add(this);
                }
                else
                {
                    //#####################################################
                    // West Panel
                    //######################################################

                    //panel
                    westPanel.setBorder(new EmptyBorder(0, 10, 0, 10));
                    westPanel.setBackground(Color.LIGHT_GRAY);

                    //Label
                    JLabel westLabel = new JLabel("Select A Store");
                    westLabel.setFont(new Font("Arial", Font.BOLD, 16)); // setting font
                    westPanel.add(westLabel);

                    //#####################################################
                    // Centre Panel
                    //######################################################

                    // centre panel
                    centrePanel.setBackground(Color.LIGHT_GRAY);

                    //Label
                    JLabel setPriceLabel = new JLabel("Set Price (£)");
                    setPriceLabel.setFont(new Font("Arial", Font.BOLD, 16)); // setting font
                    centrePanel.add(panelWithSpace(setPriceLabel, Color.LIGHT_GRAY, 50, 50, 50, 50));
                    // centrePanel.add(setPriceLabel, BorderLayout.CENTER);

                    JLabel setQuantityLabel = new JLabel("Package Quantity (G,L)");
                    setQuantityLabel.setFont(new Font("Arial", Font.BOLD, 14)); // setting font
                    centrePanel.add(panelWithSpace(setQuantityLabel, Color.LIGHT_GRAY, 15, 50, 0, 50));

                    //#####################################################
                    // East Panel
                    //######################################################

                    //panel
                    eastPanel.setBackground(Color.LIGHT_GRAY);

                    //Label
                    JLabel eastLabel = new JLabel("Delete Row");
                    eastLabel.setFont(new Font("Arial", Font.BOLD, 16)); // setting font
                    eastPanel.add(eastLabel);
                }

                //#########################################################################################################
                // Adding Object To GUI
                //#########################################################################################################
                addToContainer(parentContainer, rowPanel, 0, posY += 1, 1, 1, 0.25, 0.25, "both", 0, 0);
                parentContainer.revalidate();
            }

            protected void deleteRowAction()
            {
                removeFromParentContainer();
                addShopFormObjects.remove(this);
            }

            protected void loadStoresInJComboBox()
            {
                shops_JComboBox.removeAllItems();
                Collection<String> storesNamesList = parent.getStoresNamesList();

                for (String storeName : storesNamesList)
                {
                    shops_JComboBox.addItem(storeName);
                }

                shops_JComboBox.setSelectedItem("No Shop");
            }

            JComboBox<String> getShops_JComboBox()
            {
                return shops_JComboBox;
            }

            JTextField getIngredientPrice_TxtField()
            {
                return ingredientPrice_TxtField;
            }

            JTextField getQuantityPerPack_TxtField()
            {
                return quantityPerPack_TxtField;
            }

            public void removeFromParentContainer()
            {
                // Removing Objects from memory as the row they belong to is gone
                prices.remove(id);
                shopJComboBoxes.remove(id);
                quantityPerPack.remove(id);

                //Remove from parent Container
                parentContainer.remove(this);

                //Resizing
                parentContainer.revalidate();
                resize_GUI();
            }
        }
    }

    //#################################################################################################################
    // Other Methods
    //##################################################################################################################
    public JPanel panelWithSpace(Component addObjectToPanel, Color backgroundColour, int westWidth, int westHeight, int eastWidth, int eastHeight)
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(addObjectToPanel, BorderLayout.CENTER);

        //####################
        // Space Filler
        //####################

        // space filler panels
        JPanel westSpaceFiller = new JPanel();
        westSpaceFiller.setPreferredSize(new Dimension(westWidth, westHeight)); // width, height
        westSpaceFiller.setBackground(backgroundColour);
        panel.add(westSpaceFiller, BorderLayout.WEST);

        JPanel eastSpaceFiller = new JPanel();
        eastSpaceFiller.setPreferredSize(new Dimension(eastWidth, eastHeight)); // width, height
        eastSpaceFiller.setBackground(backgroundColour);
        panel.setBackground(backgroundColour);
        panel.add(eastSpaceFiller, BorderLayout.EAST);

        return panel;
    }

    public void addToContainer(Container container, Component addToContainer, int gridx, int gridy, int gridwidth,
                               int gridheight, double weightx, double weighty, String fill, int ipady, int ipadx)
    {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.weightx = weightx;
        gbc.weighty = weighty;

        gbc.ipady = ipady;
        gbc.ipadx = ipadx;

        switch (fill.toLowerCase())
        {
            case "horizontal":
                gbc.fill = GridBagConstraints.HORIZONTAL;
                break;
            case "vertical":
                gbc.fill = GridBagConstraints.VERTICAL;
                break;

            case "both":
                gbc.fill = GridBagConstraints.BOTH;
                break;
        }

        container.add(addToContainer, gbc);
    }
}


    

