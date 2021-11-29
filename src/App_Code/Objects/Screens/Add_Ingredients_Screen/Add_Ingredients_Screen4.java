package App_Code.Objects.Screens.Add_Ingredients_Screen;


import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Gui_Objects.*;
import App_Code.Objects.Screens.Meal_Plan_Screen.MealPlanScreen4;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Add_Ingredients_Screen4 extends JFrame
{
    //#######################################
    // General Variables
    //#######################################
    private GridBagConstraints gbc = new GridBagConstraints();

    int jFramewidth = 710, jFrameheight = 750;

    Container contentPane;
    JPanel scrollPaneJPanel;

    private MyJDBC db;
    private Integer temp_PlanID, planID;
    private String planName;
    private MealPlanScreen4 gui;

    private boolean formEditable = false;

    //#################################################################################################################
    // Constuctor
    //##################################################################################################################

    public Add_Ingredients_Screen4(MyJDBC db, MealPlanScreen4 gui, int planID, int temp_PlanID, String planName)
    {
        this.db = db;
        this.gui = gui;

        this.planID = planID;
        this.temp_PlanID = temp_PlanID;

        this.planName = planName;
        try
        {
            if (db.isDatabaseConnected())
            {

                //###################################################################################
                // Frame Set-Up
                //###################################################################################

                setTitle("Add/Edit Ingredients Screen");
                makeJframeVisible();

                //Delete all temp data on close
                addWindowListener(new java.awt.event.WindowAdapter()
                {
                    @Override //HELLO Causes Error
                    public void windowClosing(WindowEvent windowEvent)
                    {
                        closeWindowEvent();
                    }
                });

                //###################################################################################
                // Create ContentPane
                //###################################################################################

                contentPane = getContentPane();
                contentPane.setLayout(new GridBagLayout());
                contentPane.setVisible(true);

                //###################################################################################
                //   Create Screen for Interface
                //###################################################################################

                JPanel screenSectioned = new JPanel(new BorderLayout());
                addToContainer(contentPane, screenSectioned, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0);

                //###################################################################################
                //   Create Main Centre Screen for Interface
                //##################################################################################
                JPanel mainCentreScreen = new JPanel(new GridBagLayout());
                screenSectioned.add(mainCentreScreen, BorderLayout.CENTER);

                //##########################################################
                // Create ScrollPane & add to Interface
                //#########################################################
                ScrollPaneCreator scrollPane = new ScrollPaneCreator();
                scrollPaneJPanel = scrollPane.getJPanel();
                scrollPaneJPanel.setLayout(new GridBagLayout());
                addToContainer(mainCentreScreen, scrollPane, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0);

                //##########################################################
                // Creating Parts of screen & adding it to interface
                //#########################################################
                int yPos = 0;

                //Ingredients form
                IngredientsForm ingredientsForm = new IngredientsForm(this, "Add Ingredinets Info", 250, 50);
                addToContainer(scrollPaneJPanel, ingredientsForm, 0, yPos += 1, 1, 1, 0.25, 0.25, "both", 0, 0);

                //Space Divider
                addToContainer(scrollPaneJPanel, new JPanel(), 0, yPos += 1, 1, 1, 0.25, 0.25, "both", 10, 0);

                // Add shop
                ShopForm shopForm = new ShopForm(scrollPaneJPanel, "Add Ingredient Suppliers", 250, 50);
                addToContainer(scrollPaneJPanel, shopForm, 0, yPos += 1, 1, 1, 0.25, 0.25, "both", 0, 0);

                //Space Divider
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
                        if (ingredientsForm.update_IngredientsForm())
                        {
                            closeWindowEvent();
                            gui.updateInfo();
                            gui.macrosTargetsChanged(true);
                            closeWindow();
                        }
                    }
                });

                screenSectioned.add(submitButton, BorderLayout.SOUTH);

                //###################################################################################
                // Resizing GUI
                //###################################################################################
                resize_IngredientsInfoForm();
            }
        }
        catch (Exception e)
        {

        }
    }

    //#################################################################################################################
    // IngredientsForm Methods
    //##################################################################################################################
    public class IngredientsForm extends CollapsibleJPanel
    {
        //#######################################
        // Ingredients form Variables
        //#######################################

        private final String[] labels = {"Ingredient Measurement In:", "Ingredient Name:", "Ingredient_Type:",
                "Based_On_Quantity:", "Protein:", "Carbohydrates:", "Sugars Of Carbs:", "Fibre:", "Fat:", "Saturated Fat:",
                "Salt:", "Water_Content:", "Calories:"};

        private int ingredientNameLabelIndex = 0;
        private JComboBox ingredientsMeasure_JComboBox = new JComboBox(), ingredientsType_JComboBox = new JComboBox();
        private ArrayList<Component> ingredientsFormObjects = new ArrayList<>();

        public IngredientsForm(Container parentContainer, String btnText, int btnWidth, int btnHeight)
        {
            super(parentContainer, btnText, btnWidth, btnHeight);
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
            JPanel northPanel = new JPanel(new GridBagLayout());
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
            });

            iconPanelInsert.add(refresh_Icon_Btn);

            //#################################################################
            // Centre Frame
            //#################################################################
            JPanel inputArea = new JPanel(new GridLayout(labels.length, 2));

            // for each label it is created into a JLabel
            for (int i = 0; i < labels.length; i++)
            {
                boolean jcomboxBeingCreated = false;

                Object formObject = null;

                JTextField textField = new JTextField("");
                JComboBox comboBox = null;

                //#########################################
                // JLabel
                //#########################################

                String labelTXT = labels[i];

                JLabel label = new JLabel("    " + labelTXT);
                label.setHorizontalAlignment(JLabel.LEFT);
                label.setFont(new Font("Verdana", Font.BOLD, 14));
                inputArea.add(label);

                //#########################################
                // JComboField
                //#########################################

                if (labelTXT.equals("Ingredient Measurement In:"))
                {
                    String ingredientMeassurements[] = {"Litres", "Grams"};
                    ingredientsMeasure_JComboBox = new JComboBox(ingredientMeassurements);
                    inputArea.add(ingredientsMeasure_JComboBox);

                    jcomboxBeingCreated = true;
                    comboBox = ingredientsMeasure_JComboBox;
                }
                else if (labelTXT.equals("Ingredient_Type:"))
                {
                    String ingredientsType[] = {"Breads", "Cereals", "Cereal Bars", "Cheese", "Fish", "Frozen Fruit", "Frozen Vegetables", "Fruit",
                            "Eggs", "Grains & Legumes", "Juice", "Milk", "Lean Meat", "Noodles", "Nuts", "Nuts & Seeds", "Meat", "Other Grains", "Pasta",
                            "Potatoes", "Poultry", "Rice", "Smoothie", "Vegetables", "Yoghurt"};

                    ingredientsType_JComboBox = new JComboBox(ingredientsType);
                    inputArea.add(ingredientsType_JComboBox);

                    jcomboxBeingCreated = true;
                    comboBox = ingredientsType_JComboBox;
                }

                // if a JComboBox is being created Centre JComboBox Items & Set Selected Item to 0
                if (jcomboxBeingCreated && comboBox!=null)
                {
                    // Centre JComboBox Item
                    DefaultListCellRenderer listRenderer = new DefaultListCellRenderer();
                    listRenderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER); // center-aligned items
                    comboBox.setRenderer(listRenderer);

                    // Set Selected Item To Nothing
                    comboBox.setSelectedIndex(-1);

                    ingredientsFormObjects.add(comboBox);

                    continue;
                }

                //#########################################
                // JTextfields
                //#########################################
                if (labelTXT.equals("Ingredient Name:")) //Setting TextField limits
                {
                    textField.setDocument(new JTextFieldLimit(255));
                    ingredientNameLabelIndex = i;
                }
                else
                {
                    textField.setDocument(new JTextFieldLimit(9));
                }

                ingredientsFormObjects.add(textField);
                inputArea.add(textField);
            }
            mainJPanel.add(inputArea, BorderLayout.CENTER);
        }

        private boolean validate_IngredientsForm()// HELLO Modify
        {
            if (temp_PlanID==null && planID==null && planName==null)
            {
                JOptionPane.showMessageDialog(gui.getFrame(), "Please Select A Plan First!");
                return false;
            }

            String errorTxt = "";
            BigDecimal zero = new BigDecimal(0);

            //##############################
            // Validation JTextFields
            //##############################
            for (int row = 0; row < ingredientsFormObjects.size(); row++)
            {
                String value = "";
                Component comp = ingredientsFormObjects.get(row);

                if (comp instanceof JComboBox)
                {
                    JComboBox comboBox = (JComboBox) comp;

                    if (comboBox.getSelectedIndex()==-1) // if no item has been selected by JComboBox
                    {
                        errorTxt += String.format("\n\n  ' %s ' on Row: %s, an option inside the dropdown menu must be selected", labels[row], row + 1);
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
                        errorTxt += String.format("\n\n  ' %s ' on Row: %s,  must have a value which is not ' NULL '!", labels[row], row + 1);
                        continue;
                    }

                /*#######################################
                If JTextField is Ingredient Name Skip
                Decimal eval Below
                #########################################*/

                    if (row==ingredientNameLabelIndex)
                    {
                        continue;
                    }

                    //#########################################
                    // Check if JTextfield input is a Decimal
                    //#########################################
                    try
                    {
                        BigDecimal bdFromString = new BigDecimal(String.format("%s", value));
                        if (bdFromString.compareTo(zero) < 0 || bdFromString.compareTo(zero)==0) // decimal less than 0
                        {
                            if (bdFromString.compareTo(zero)==0 && labels[row].equals("Additional Calories:"))
                            {
                                continue;
                            }
                            errorTxt += String.format("\n\n  ' %s ' on Row: %s,  must have a value which is bigger than 0 !", labels[row], row + 1);
                        }
                    }
                    catch (Exception e)
                    {
                        errorTxt += String.format("\n\n  ' %s 'on Row: %s, must have a value which is a ' Decimal(8,2) ' !'", labels[row], row + 1);
                    }
                }
            }

            //####################################################
            //Check if IngredientName Already exists in DB
            //####################################################

            JTextField ingredientName_JTxtF = (JTextField) ingredientsFormObjects.get(ingredientNameLabelIndex);
            String ingredientName_Txt = ingredientName_JTxtF.getText().trim();

            if (!(ingredientName_Txt.equals("")))
            {
                String query = String.format("SELECT Ingredient_Name FROM ingredients_info WHERE Ingredient_Name = '%s';", ingredientName_Txt);

                if (db.getSingleColumnQuery(query)!=null)
                {
                    errorTxt += String.format("\n\n  Ingredient named %s already exists within the database!", ingredientName_Txt);
                }
            }

            //####################################################
            //Check if any error were found & Process it
            //####################################################

            if (errorTxt.length()==0)
            {
                System.out.printf("\n\nNo Error");
                return true;
            }

            JOptionPane.showMessageDialog(gui.getFrame(), String.format("\n\nPlease fix the following rows being; \n%s", errorTxt));

            return false;
        }

        private boolean update_IngredientsForm() // HELLO needs further update methods created for gui
        {
            //####################################
            // Gathering Form Txt Data
            //####################################
            ArrayList<String> formResults = new ArrayList<>();
            for (Component comp : ingredientsFormObjects)
            {
                if (comp instanceof JTextField)
                {
                    formResults.add(((JTextField) comp).getText());
                }
                else if (comp instanceof JComboBox)
                {
                    formResults.add(((JComboBox) comp).getSelectedItem().toString());
                }
            }

            //####################################
            // Creating Upload Query
            //####################################
            int i = 0;
            String updateTargets_Query = String.format("""
                            INSERT INTO ingredients_info
                                                    
                            (Meassurement, Ingredient_Name, Ingredient_Type, Based_On_Quantity, Protein, Carbohydrates, 
                            Sugars_Of_Carbs, Fibre, Fat, Saturated_Fat, Salt, Water_Content, Calories)
                                                    
                            Values ('%s', '%s', '%s', %s, %s, %s, %s, %s, %s, %s, %s, %s, %s) """,
                    formResults.get(i), formResults.get(i += 1), formResults.get(i += 1), formResults.get(i += 1), formResults.get(i += 1),
                    formResults.get(i += 1), formResults.get(i += 1), formResults.get(i += 1), formResults.get(i += 1),
                    formResults.get(i += 1), formResults.get(i += 1), formResults.get(i += 1), formResults.get(i += 1));

            System.out.printf("\n\nQuery: \n\n%s", updateTargets_Query);

            //####################################
            // Uploading Query
            //####################################
            if (!(db.uploadData_Batch(new String[]{updateTargets_Query})))
            {
                JOptionPane.showMessageDialog(gui.getFrame(), "Un-able to Create Ingredient In DB!");
                return false;
            }

            JOptionPane.showMessageDialog(gui.getFrame(), "Successfully Created Ingredient In DB!!!");

            //HELLO Update feature for ingredients in Meal needed

            return true;
        }
    }

    //#################################################################################################################
    // IngredientsForm Methods
    //##################################################################################################################

    public class ShopForm extends CollapsibleJPanel
    {
        AddShopForm_Object addShopForm_object;
        int objectID = 0;

        HashMap<Integer, JComboBox> shopJComboBoxes = new HashMap<>();
        HashMap<Integer, JTextField> prices = new HashMap<>();
        HashMap<Integer, JTextField> quantityPerPack = new HashMap<>();

        public ShopForm(Container parentContainer, String btnText, int btnWidth, int btnHeight)
        {
            super(parentContainer, btnText, btnWidth, btnHeight);

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
            JPanel inputArea = new JPanel(new GridBagLayout());

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
                setObjectID(objectID += 1);
                addToContainer(inputArea, new AddShopForm_Object(inputArea, true), 0, objectID, 1, 1, 0.25, 0.25, "horizontal", 0, 0);
            });

            iconPanelInsert.add(add_Icon_Btn);

            //##########################################################################################
            // Centre Form
            //##########################################################################################
            mainJPanel.add(inputArea, BorderLayout.CENTER);

            addToContainer(inputArea, new AddShopForm_Object(inputArea, false), 0, objectID, 1, 1, 0.25, 0.25, "horizontal", 0, 0);
        }

        private int getObjectID()
        {
            return objectID;
        }

        private void setObjectID(int objectID)
        {
            this.objectID = objectID;
        }

        private boolean validateForm()
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
                String value = prices.get(key).getText().trim();

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
                // Check if JTextfield input is a Decimal
                //#########################################
                try
                {
                    BigDecimal bdFromString = new BigDecimal(String.format("%s", value));
                    if (bdFromString.compareTo(zero) < 0 || bdFromString.compareTo(zero)==0) // decimal less than 0
                    {
                        errorTxt += String.format("\n\nOn Row: %s, 'price'  must have a value which is bigger than 0 !", i);
                    }
                }
                catch (Exception e)
                {
                    errorTxt += String.format("\n\nOn Row: %s, 'price' must have a value which is a ' Decimal(8,2) ' !'", i);
                }

                i++;
            }

            if (errorTxt.equals(""))
            {
                return true;
            }

            JOptionPane.showMessageDialog(gui.getFrame(), String.format("\n\nPlease fix the following rows being; \n%s", errorTxt));
            return false;
        }

        private boolean validateQuantity()
        {
            String errorTxt = "";
            BigDecimal zero = new BigDecimal(0);
            int i = 1;

            for (Integer key : quantityPerPack.keySet())
            {
                String value = quantityPerPack.get(key).getText().trim();

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
                // Check if JTextfield input is a Decimal
                //#########################################
                try
                {
                    BigDecimal bdFromString = new BigDecimal(String.format("%s", value));
                    if (bdFromString.compareTo(zero) < 0 || bdFromString.compareTo(zero)==0) // decimal less than 0
                    {
                        errorTxt += String.format("\n\nOn Row: %s, the 'Quantity'   must have a value which is bigger than 0 !", i);
                    }
                }
                catch (Exception e)
                {
                    errorTxt += String.format("\n\nOn Row: %s, the 'Quantity'  must have a value which is a ' Decimal(8,2) ' !'", i);
                }

                i++;
            }


            if (errorTxt.equals(""))
            {
                return true;
            }

            JOptionPane.showMessageDialog(gui.getFrame(), String.format("\n\nPlease fix the following rows being; \n%s", errorTxt));
            return false;
        }


        private boolean validateShops()
        {
            String errorTxt = "";

            int i = 1;
            for (Integer key : shopJComboBoxes.keySet())
            {
                if (Objects.equals(shopJComboBoxes.get(key).getSelectedItem(), "No Shop"))
                {
                    errorTxt += String.format("\nOn Row %s,  please Select a shop that isnt 'No Shop'! Or, delete the row!", i);
                }
                System.out.printf("\nObject %s", key);
                i++;
            }

            if (errorTxt.equals(""))
            {
                return true;
            }

            JOptionPane.showMessageDialog(gui.getFrame(), String.format("\n\nPlease fix the following rows being; \n%s", errorTxt));
            return false;
        }

        private String updateString_Shop()
        {
            String updateString = "INSERT INTO stores\n" +
                    "(Store_Name)";

            for (Integer key : prices.keySet())
            {

            }

            return updateString;
        }

        class AddShopForm_Object extends JPanel
        {
            private int posY = 0, id;

            Container parentContainer;

            AddShopForm_Object(Container parentContainer, boolean addRow)
            {
                this.parentContainer = parentContainer;
                this.id = getObjectID();

                addRow(addRow);
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
                    String query = "SELECT DISTINCT Store_Name FROM stores;";

                    String shopsInfo[] = db.getSingleColumnQuery(query);
                    if (shopsInfo==null)
                    {
                        JOptionPane.showMessageDialog(gui.getFrame(), "Unable To Get ShopNames From DB - Internal DB Error");
                        return;
                    }

                    //########################
                    // create JComboBox
                    //########################
                    JComboBox shops_JComboBox = new JComboBox(shopsInfo);
                    shops_JComboBox.setSelectedItem("No Shop");

                    ((JLabel) shops_JComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
                    westPanel.add(shops_JComboBox);
                    shopJComboBoxes.put(id, shops_JComboBox);

                    //#####################################################
                    // Centre Side
                    //######################################################
                    JTextField ingredientPrice_TxtField = new JTextField();
                    ingredientPrice_TxtField.setDocument(new JTextFieldLimit(16));
                    ingredientPrice_TxtField.setText("0.00");
                    prices.put(id, ingredientPrice_TxtField);
                    centrePanel.add(ingredientPrice_TxtField);

                    JTextField quantityPerPack_TxtField = new JTextField();
                    quantityPerPack_TxtField.setDocument(new JTextFieldLimit(16));
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
                        parentContainer.remove(rowPanel); // remove this row from GUI
                        parentContainer.revalidate(); // update screen

                        // Removing Objects from memory as the row they belong to is gone
                        prices.remove(id);
                        shopJComboBoxes.remove(id);
                        quantityPerPack_TxtField.remove(id);
                    });

                    eastPanel.add(deleteRowBtn);
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
                    centrePanel.add(panelWithSpace(setPriceLabel, Color.LIGHT_GRAY, 50,50,50,50));
                    // centrePanel.add(setPriceLabel, BorderLayout.CENTER);

                    JLabel setQuantityLabel = new JLabel("Quantity Per Box");
                    setQuantityLabel.setFont(new Font("Arial", Font.BOLD, 16)); // setting font
                    centrePanel.add(panelWithSpace(setQuantityLabel, Color.LIGHT_GRAY, 30,50,0,50));

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
        }
    }

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

    //#################################################################################################################
    // General Methods
    //##################################################################################################################

    private void resize_IngredientsInfoForm()
    {
        scrollPaneJPanel.revalidate();
        contentPane.revalidate();
    }

    public void makeJframeVisible()
    {
        setExtendedState(JFrame.NORMAL);
        setSize(new Dimension(jFramewidth, jFrameheight));
        setLocation(0, 0);
        setVisible(true);
        setResizable(false);

        setLocationRelativeTo(null);
    }

    public void closeWindow()
    {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public void closeWindowEvent()
    {
        gui.remove_addIngredients_Screen();
    }

    private void addToContainer(Container container, Component addToContainer, int gridx, int gridy, int gridwidth,
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
