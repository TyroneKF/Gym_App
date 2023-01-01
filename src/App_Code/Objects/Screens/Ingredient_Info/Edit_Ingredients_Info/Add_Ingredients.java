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

    protected boolean formEditable = false;

    private final int totalNumbersAllowed = 7, decimalScale = 2, decimalPrecision = totalNumbersAllowed - decimalScale, charLimit = 8;

    protected IngredientsForm ingredientsForm;
    private ShopForm shopForm;
    protected SearchForFoodInfo searchForIngredientInfo;


    //##############################################################
    // NEW SHIT
    //##############################################################
    protected Ingredients_Info_Screen ingredients_info_screen;
    protected Meal_Plan_Screen mealPlanScreen;
    protected MyJDBC db;
    protected Integer planID, tempPlanID;
    protected String planName;


    //#################################################################################################################
    // Constructor
    //##################################################################################################################

    Add_Ingredients(Ingredients_Info_Screen ingredients_info_screen, MyJDBC db)
    {
        //###################################################################################
        //   Parent Stuff
        //###################################################################################
        this.db = db;
        this.ingredients_info_screen = ingredients_info_screen;

        //###################################################################################
        //   Parent Stuff
        //###################################################################################
        this.planID = ingredients_info_screen.getPlanID();
        this.tempPlanID = ingredients_info_screen.getTempPlanID();
        this.planName = ingredients_info_screen.getPlanName();

        this.mealPlanScreen = ingredients_info_screen.getMealPlanScreen();

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
        ingredientsForm = new IngredientsForm(scrollPaneJPanel, ingredients_info_screen, "Add Ingredients Info", 250, 50);

        shopForm = new ShopForm(scrollPaneJPanel, "Add Ingredient Suppliers", 250, 50);

        searchForIngredientInfo = new SearchForFoodInfo(scrollPaneJPanel, ingredientsForm, "Search For Food Info", 250, 50);

        createForms(ingredientsForm, shopForm, searchForIngredientInfo);
    }

    //####################################################
    // Methods
    //###################################################

    protected void updateIngredientForm_Type_JComboBox()
    {
        ingredientsForm.loadIngredientsTypeJComboBox();
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
            if (!areYouSure("upload these values as they may have been changed / adapted to fit our data type format"))
            {
                return;
            }

            if (updateBothForms(ingredientsForm.get_IngredientsForm_UpdateString(null), shopForm.get_ShopForm_UpdateString()))
            {
                ingredients_info_screen.setUpdateIngredientInfo(true);
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

    //HELLO EDIT
    protected boolean addOrDeleteIngredientFromMap(String process, String ingredientType, String ingredientName)
    {
        // Storing
        Collection<String> ingredientTypeList = ingredients_info_screen.getMapIngredientTypesToNames().get(ingredientType);
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
                ingredients_info_screen.getMapIngredientTypesToNames().put(ingredientType, ingredientTypeList);
            }
        }
        else if (process.equals("delete"))
        {
            ingredientTypeList.remove(ingredientName);

            // Remove List as there is no items in it
            if (ingredientTypeList.size() == 0)
            {
                ingredients_info_screen.getMapIngredientTypesToNames().remove(ingredientType);
            }
        }

        ingredients_info_screen.update_EditIngredientsInfo_IngredientsTypes();
        return true;
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
        ingredients_info_screen.getContentPane().revalidate();
        scrollPaneJPanel.revalidate();
        revalidate();
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
            if (prices.size() == 0) // prices is just used but, could be any list stored by the shop object
            {
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
                        mysqlVariableReference, quantityPerPack.get(key).getText(), prices.get(key).getText(),
                        shopJComboBoxes.get(key).getSelectedItem().toString());

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

                if (ingredients_info_screen.getStoresNamesList() == null)
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
                Collection<String> storesNamesList = ingredients_info_screen.getStoresNamesList();

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



    

