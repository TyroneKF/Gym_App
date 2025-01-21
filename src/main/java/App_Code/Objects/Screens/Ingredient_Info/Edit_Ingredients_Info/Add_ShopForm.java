package App_Code.Objects.Screens.Ingredient_Info.Edit_Ingredients_Info;

import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Gui_Objects.IconPanel;
import App_Code.Objects.Gui_Objects.JTextFieldLimit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;

public class Add_ShopForm extends Parent_IngredientsForm_And_ShopForm
{
    //#######################################################
    //
    //#######################################################
    Add_Ingredients_Screen add_ingredientsScreen;

    //#######################################################
    protected int yPos =0;

    protected ArrayList<AddShopForm_Object> shopFormObjects = new ArrayList<>();

    protected Container parentContainer;
    protected JPanel inputArea;

    //#############################################################################################################
    //
    //#############################################################################################################
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

        addToContainer(inputArea, new AddShopForm_Object(inputArea, false), 0, yPos+=1, 1, 1, 0.25, 0.25, "horizontal", 0, 0);
    }

    //#############################################################################################################
    //
    //#############################################################################################################
    protected boolean validateForm()
    {
        //######################################################
        //
        //######################################################
        String overalErrorTxt = "";
        int pos = 0;


        //######################################################
        //
        //######################################################

        for(AddShopForm_Object  shopForm_object : shopFormObjects)
        {
            System.out.println("\n\nhere");
            //#######################################
            // Reset Values
            //#######################################
            pos ++;
            String space = "\n\n", iterationErrorTxt = "";

            //#######################################
            // Validate Stores
            //#######################################
            String shopChosen = shopForm_object.getShops_JComboBox().getSelectedItem().toString();

            if (shopChosen.equals("No Shop"))
            {
                iterationErrorTxt += String.format("%sOn Row %s,  please Select a shop that isn't 'No Shop'! Or, delete the row!",space, pos);
                space = "\n";
            }

            //########################################
            // Validate Product Name
            //########################################
            String productNameTxt = shopForm_object.getProductName_TxtField().getText().trim();
            if(productNameTxt.equals(""))
            {
                iterationErrorTxt += String.format("%sOn Row: %s, the 'Product Name' cannot be empty or, ' NULL '!",space,pos);
                space = "\n";
            }

            //#######################################
            // Validate Prices
            //#######################################
            JTextField prices = shopForm_object.getProductPrice_TxtField();
            String price = prices.getText().trim();

            if (!(price.equals(""))) // Check if text field input is empty
            {
                String txt = convertToBigDecimal(price, "Prices", pos, prices, true);
                if(!txt.equals(""))
                {
                    if(iterationErrorTxt.equals(""))
                    {
                        space="\n";
                        iterationErrorTxt = String.format("\n%s",txt);
                    }
                    else
                    {
                        iterationErrorTxt += txt;
                    }
                }
            }
            else
            {
                iterationErrorTxt += String.format("%sOn Row: %s, the 'Price' must have a value which is not ' NULL '!",space, pos);
                space="\n";
            }

            //#######################################
            // Validate Quantity
            //#######################################
            JTextField quantity = shopForm_object.getQuantityPerPack_TxtField();
            String value = quantity.getText().trim();

            if (!(value.equals(""))) // Check if text field input is empty
            {
                String txt = convertToBigDecimal(value, "Quantity", pos, quantity, true);
                if(!txt.equals(""))
                {
                    if(iterationErrorTxt.equals(""))                    {

                        iterationErrorTxt = String.format("\n%s",txt);
                    }
                    else
                    {
                        iterationErrorTxt += txt;
                    }
                }
            }
            else
            {
                iterationErrorTxt += String.format("%sOn Row: %s, the 'Quantity' must have a value which is not ' NULL '!",space, pos);
            }

           overalErrorTxt += iterationErrorTxt;
        }

        // #################################################
        // End Of Validating process release results
        // #################################################
        if (overalErrorTxt.equals(""))
        {
            return true;
        }

        JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), String.format("\n\nPlease fix the following rows being; %s", overalErrorTxt));
        return false;
    }

    public String[] get_ShopForm_UpdateString()
    {
        //########################################
        // Nothing to Update
        //########################################
        if (shopFormObjects.size() == 0) // prices is just used but, could be any list stored by the shop object
        {
            return null;
        }

        //#############################################################
        // Create Update  String
        //############################################################
        String mysqlVariableReference = "@newIngredientID";
        String createMysqlVariable = String.format("SET %s = (SELECT MAX(IngredientID) FROM ingredients_info);", mysqlVariableReference);
        String updateString = String.format("""
                    INSERT INTO ingredientInShops (IngredientID, Product_Name, Volume_Per_Unit, Cost_Per_Unit, StoreID)
                    VALUES """);

        ///#################################
        // Creating String for Add Values
        //#################################
        String values = "";
        Iterator<AddShopForm_Object> it = shopFormObjects.iterator();

        while(it.hasNext())
        {
            AddShopForm_Object shopForm_object = it.next();

            values += String.format("(%s, '%s', %s, %s, (SELECT StoreID FROM stores WHERE Store_Name = '%s'))",
                    mysqlVariableReference,
                    shopForm_object.getProductName_TxtField().getText(),
                    shopForm_object.getQuantityPerPack_TxtField().getText(),
                    shopForm_object.getProductPrice_TxtField().getText(),
                    shopForm_object.getShops_JComboBox().getSelectedItem().toString());

            if (!(it.hasNext()))
            {
                values += ";";
                break;
            }

            values += ",";
        }

        updateString += values;

        //############################################################
        // Return values
        //############################################################
        System.out.printf("\n\n Add shop form: get_ShopForm_UpdateString() \n%s \n\n%s", createMysqlVariable, updateString);

        return new String[]{createMysqlVariable, updateString};
    }

    //#############################################################################################################
    //
    //#############################################################################################################
    public void clearShopForm()
    {
        Iterator<AddShopForm_Object> it = shopFormObjects.iterator();
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

    //#############################################################################################################
    //
    //#############################################################################################################

    protected AddShopForm_Object addShopForm_object()
    {
        AddShopForm_Object obj = new AddShopForm_Object(inputArea, true);
        addToContainer(inputArea, obj, 0, yPos+=1, 1, 1, 0.25, 0.25, "horizontal", 0, 0);
        return obj;
    }

    //#############################################################################################################
    //
    //#############################################################################################################
    class AddShopForm_Object extends JPanel
    {
        Integer PDID = null;  //EDIT NOW
        Container parentContainer;
        JComboBox<String> shops_JComboBox;
        JTextField productName_TxtField, productPrice_TxtField, quantityPerPack_TxtField;

        //#############################################################################################################
        //
        //#############################################################################################################
        AddShopForm_Object(Container parentContainer, boolean addRow)
        {
            this.parentContainer = parentContainer;
            addRow(addRow);
        }

        //#############################################################################################################
        // Methods
        //#############################################################################################################
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

            //
            JPanel westPanel = new JPanel(new GridLayout(1, 1));
            westPanel.setBorder(new EmptyBorder(0, 0, 0, 10));
            westPanel.setPreferredSize(new Dimension(150, 25)); // width, height
            rowPanel.add(westPanel, BorderLayout.WEST);

            //
            JPanel centrePanel = new JPanel(new BorderLayout());
            centrePanel.setBackground(Color.BLUE);
            rowPanel.add(centrePanel, BorderLayout.CENTER);

            // Delete row button section
            JPanel eastPanel = new JPanel(new GridLayout(1, 1));
            eastPanel.setBorder(new EmptyBorder(0, 5, 0, 0));
            eastPanel.setPreferredSize(new Dimension(110, 34)); // width, height
            rowPanel.add(eastPanel, BorderLayout.EAST);

            if(!addRowBool)
            {
                //#####################################################
                // West Panel
                //######################################################
                int fontSize = 14;

                // Panel
                westPanel.setBorder(new EmptyBorder(0, 10, 0, 10));
                westPanel.setBackground(Color.LIGHT_GRAY);

                // Label
                JLabel westLabel = new JLabel("Select A Store");
                westLabel.setFont(new Font("Arial", Font.BOLD, fontSize)); // setting font
                westPanel.add(westLabel);

                //#####################################################
                // Centre Panel
                //######################################################

                //Label
                JLabel setProductNameLabel = new JLabel("Set Product Name");
                setProductNameLabel.setFont(new Font("Arial", Font.BOLD, fontSize)); // setting font

                JPanel jp = new JPanel(new GridLayout(1,1));
                jp.setPreferredSize(new Dimension(270, 34));
                jp.setBorder(new EmptyBorder(0, 80, 0, 0)); //Pushes object inside further along
                jp.setBackground(Color.LIGHT_GRAY);


                jp.add(setProductNameLabel);
                centrePanel.add(jp, BorderLayout.WEST);

                //########################
                //Label
                JLabel setPriceLabel = new JLabel("Set Price (£)");
                setPriceLabel.setFont(new Font("Arial", Font.BOLD, fontSize)); // setting font

                JPanel jp2 = new JPanel(new GridLayout(1,1));
                jp2.setPreferredSize(new Dimension(10, 25));
                jp2.setBorder(new EmptyBorder(0, 5, 0, 0)); //Pushes object inside further along

                //                jp2.setBackground(Color.ORANGE);
                jp2.setBackground(Color.LIGHT_GRAY);

                jp2.add(setPriceLabel);
                centrePanel.add(jp2, BorderLayout.CENTER);

                //########################
                //Label
                JLabel setQuantityLabel = new JLabel("Quantity (G,L)");
                setQuantityLabel.setFont(new Font("Arial", Font.BOLD, fontSize)); // setting font

                JPanel jp3 = new JPanel(new GridLayout(1,1));
                jp3.setPreferredSize(new Dimension(120, 34));
                jp3.setBorder(new EmptyBorder(0, 15, 0, 0)); //Pushes object inside further along

                //                jp3.setBackground(Color.RED);
                jp3.setBackground(Color.LIGHT_GRAY);

                jp3.add(setQuantityLabel);

                centrePanel.add(jp3, BorderLayout.EAST);

                //#####################################################
                // East Panel
                //######################################################

                //panel
                eastPanel.setBackground(Color.LIGHT_GRAY);

                //Label
                JLabel deleteBtnLabel = new JLabel("Delete Row");
                deleteBtnLabel.setFont(new Font("Arial", Font.BOLD, fontSize)); // setting font

                JPanel jp4 = new JPanel(new GridLayout(1,1));
                jp4.setBackground(Color.LIGHT_GRAY);
                jp4.setPreferredSize(new Dimension(120, 34));
                jp4.setBorder(new EmptyBorder(0, 10, 0, 0)); //Pushes object inside further along

                jp4.add(deleteBtnLabel);
                eastPanel.add(jp4);
            }
            else
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

                //#####################################################
                // Centre Side
                //######################################################

                //Product Name JTextField
                productName_TxtField = new JTextField();
                productName_TxtField.setDocument(new JTextFieldLimit(100));
                productName_TxtField.setText("");

                JPanel jp = new JPanel(new GridLayout(1,1));
                jp.setPreferredSize(new Dimension(270, 34));
                jp.setBorder(new EmptyBorder(0, 0, 0, 0)); //Pushes object inside further along
                jp.add(productName_TxtField);
                centrePanel.add(jp, BorderLayout.WEST);

                // Product Price
                productPrice_TxtField = new JTextField();
                productPrice_TxtField.setDocument(new JTextFieldLimit(charLimit));
                productPrice_TxtField.setText("0.00");

                JPanel jp2 = new JPanel(new GridLayout(1,1));
                jp2.setPreferredSize(new Dimension(10, 25));
                jp2.setBorder(new EmptyBorder(0, 5, 0, 0)); //Pushes object inside further along
                jp2.add(productPrice_TxtField);
                centrePanel.add(jp2,BorderLayout.CENTER);

                // Quantity
                quantityPerPack_TxtField = new JTextField();
                quantityPerPack_TxtField.setDocument(new JTextFieldLimit(charLimit));
                quantityPerPack_TxtField.setText("0.00");

                JPanel jp3 = new JPanel(new GridLayout(1,1));
                jp3.setPreferredSize(new Dimension(120, 34));
                jp3.setBorder(new EmptyBorder(0, 5, 0, 0)); //Pushes object inside further along
                jp3.add(quantityPerPack_TxtField);
                centrePanel.add(jp3,BorderLayout.EAST);

                //#####################################################
                // East Side
                //######################################################

                // Creating submit button
                JButton deleteRowBtn = new JButton("Delete Row");
                deleteRowBtn.setPreferredSize(new Dimension(140,34));
                deleteRowBtn.setFont(new Font("Arial", Font.BOLD, 12)); // setting font

                // creating commands for submit button to execute on
                deleteRowBtn.addActionListener(ae -> {
                    deleteRowAction();
                });

                JPanel jp4 = new JPanel(new GridLayout(1,1));
                jp4.setPreferredSize(new Dimension(140, 34));
                jp4.setBorder(new EmptyBorder(0, 0, 0, 0)); //Pushes object inside further along
                jp4.add(deleteRowBtn);
                eastPanel.add(jp4);

                //#####################################################
                // Adding row to memory
                //######################################################
                shopFormObjects.add(this);
            }

            //#########################################################################################################
            // Adding Object To GUI
            //#########################################################################################################
            addToContainer(parentContainer, rowPanel, 0, yPos += 1, 1, 1, 0.25, 0.25, "both", 0, 0);
            parentContainer.revalidate();
        }

        protected void loadStoresInJComboBox()
        {
            shops_JComboBox.removeAllItems();
            Collection<String> storesNamesList = ingredients_info_screen.getStoresNamesList();

            for (String storeName : storesNamesList)
            {
                shops_JComboBox.addItem(storeName);
            }

            shops_JComboBox.setSelectedItem("No Shop"); // make the first selected item N/A
        }

        //#############################################################################################################
        // Mutator Methods
        //#############################################################################################################
        protected void setPDID(Integer PDID)
        {
            this.PDID = PDID;
        }

        //#############################################################################################################
        // Accessor Methods
        //#############################################################################################################
        protected Integer getPDID()
        {
            return PDID;
        }

        //########################################
        //
        //########################################
        protected JComboBox<String> getShops_JComboBox()
        {
            return shops_JComboBox;
        }

        protected JTextField getProductName_TxtField() {return productName_TxtField;}

        protected JTextField getProductPrice_TxtField()
        {
            return productPrice_TxtField;
        }

        protected JTextField getQuantityPerPack_TxtField()
        {
            return quantityPerPack_TxtField;
        }

        //#############################################################################################################
        //
        //#############################################################################################################
        protected void deleteRowAction()
        {
            removeFromParentContainer(); // remove all the  input GUI objects from memory
            shopFormObjects.remove(this);
        }

        protected void removeFromParentContainer()
        {
            //Remove from parent Container
            parentContainer.remove(this);

            //Resizing
            parentContainer.revalidate();

            //Resizing Form
            add_ingredientsScreen.resize_GUI();
        }
    }
}