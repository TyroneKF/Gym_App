package App_Code.Objects.Screens.Ingredient_Info.Edit_Ingredients_Info;

import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Gui_Objects.IconPanel;
import App_Code.Objects.Gui_Objects.JTextFieldLimit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class Add_ShopForm extends Parent_IngredientsForm_And_ShopForm
{
    //#######################################################
    //
    //#######################################################
    Add_Ingredients_Screen add_ingredientsScreen;

    //#######################################################
    protected int objectID = 0;

    protected HashMap<Integer, JComboBox> shopJComboBoxes = new HashMap<>();
    protected HashMap<Integer, JTextField> prices = new HashMap<>();
    protected HashMap<Integer, JTextField> quantityPerPack = new HashMap<>();

    protected ArrayList<AddShopForm_Object> addShopFormObjects = new ArrayList<>();

    protected Container parentContainer;
    protected JPanel inputArea;

    public Add_ShopForm(Container parentContainer, Ingredients_Info_Screen ingredients_info_screen, Add_Ingredients_Screen add_ingredientsScreen, String btnText, int btnWidth, int btnHeight)
    {
        super(parentContainer, ingredients_info_screen, btnText, btnWidth, btnHeight);
        this.parentContainer = parentContainer;
        this.add_ingredientsScreen = add_ingredientsScreen;

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

        IconButton add_Icon_Btn = new IconButton("src/main/java/images/add/++add.png", "", width, height, width, height,
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

    public String[] get_ShopForm_UpdateString()
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

        extraClearShopsForm();

        parentContainer.revalidate();
    }

    protected void extraClearShopsForm()
    {

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

            //Resizing Form
            add_ingredientsScreen.resize_GUI();
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
    }
}