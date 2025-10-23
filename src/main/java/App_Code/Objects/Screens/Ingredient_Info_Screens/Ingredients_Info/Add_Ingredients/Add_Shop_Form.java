package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients;

import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Gui_Objects.IconPanel;
import App_Code.Objects.Gui_Objects.JTextFieldLimit;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Ingredients_Info_Screen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;

public class Add_Shop_Form extends Parent_IngredientForm_And_ShopForm
{
    //#######################################################
    // Variables
    //#######################################################
    Add_Ingredients_Screen add_ingredientsScreen;
    
    protected int yPos = 0;
    
    protected ArrayList<Add_ShopForm_Object> shopFormObjects = new ArrayList<>();
    
    protected Container parentContainer;
    protected JPanel inputArea;
    
    //#############################################################################################################
    // Constructor
    //#############################################################################################################
    public Add_Shop_Form(Container parentContainer, Ingredients_Info_Screen ingredients_info_screen, Add_Ingredients_Screen add_ingredientsScreen, String btnText, int btnWidth, int btnHeight)
    {
        super(parentContainer, ingredients_info_screen, btnText, btnWidth, btnHeight);
        this.parentContainer = parentContainer;
        this.add_ingredientsScreen = add_ingredientsScreen;
        
        //#############################################################################################################
        // Creating Form
        //#############################################################################################################
        JPanel mainJPanel = collapsibleJPanel.get_Centre_JPanel();
        mainJPanel.setLayout(new BorderLayout());
        
        //#############################################################################################################
        // North Frame
        //#############################################################################################################
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
        add_To_Container(northPanel, titlePanel, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0, null);
        
        //#####################################################
        // Creating area for North JPanel (Add Icon)
        //#####################################################
        IconPanel iconPanel = new IconPanel(1, 10, "East");
        JPanel iconPanelInsert = iconPanel.getIconJpanel();
        
        add_To_Container(northPanel, iconPanel.getIconAreaPanel(), 0, 1, 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
        
        //##########################
        // Refresh Icon
        //##########################
        int width = 30;
        int height = 30;
        
        IconButton add_Icon_Btn = new IconButton("/images/add/++add.png", width, height, width, height, "centre", "right"); // btn text is useless here , refactor
        
        JButton add_Btn = add_Icon_Btn.returnJButton();
        add_Icon_Btn.makeBTntransparent();
        
        add_Btn.addActionListener(ae -> {
            new Add_ShopForm_Object(inputArea);
        });
        
        iconPanelInsert.add(add_Icon_Btn);
        
        //#############################################################################################################
        // Centre Form
        //#############################################################################################################
        inputArea = new JPanel(new GridBagLayout());
        mainJPanel.add(inputArea, BorderLayout.CENTER);
        mainJPanel.add(inputArea, BorderLayout.CENTER);
        
        //##########################################
        // ShopForm GUI
        //##########################################
        JPanel shopFormPanelGUI = new JPanel(new BorderLayout());
        
        //###############################
        // Creating Sections for GUI
        //###############################
        
        //
        JPanel westPanel = new JPanel(new GridLayout(1, 1));
        westPanel.setBorder(new EmptyBorder(0, 0, 0, 10));
        westPanel.setPreferredSize(new Dimension(150, 25)); // width, height
        shopFormPanelGUI.add(westPanel, BorderLayout.WEST);
        
        //
        JPanel centrePanel = new JPanel(new BorderLayout());
        centrePanel.setBackground(Color.BLUE);
        shopFormPanelGUI.add(centrePanel, BorderLayout.CENTER);
        
        // Delete row button section
        JPanel eastPanel = new JPanel(new GridLayout(1, 1));
        eastPanel.setBorder(new EmptyBorder(0, 5, 0, 0));
        eastPanel.setPreferredSize(new Dimension(110, 34)); // width, height
        shopFormPanelGUI.add(eastPanel, BorderLayout.EAST);
        
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
        
        JPanel jp = new JPanel(new GridLayout(1, 1));
        jp.setPreferredSize(new Dimension(270, 34));
        jp.setBorder(new EmptyBorder(0, 80, 0, 0)); //Pushes object inside further along
        jp.setBackground(Color.LIGHT_GRAY);
        
        jp.add(setProductNameLabel);
        centrePanel.add(jp, BorderLayout.WEST);
        
        //########################
        //Label
        JLabel setPriceLabel = new JLabel("Set Price (£)");
        setPriceLabel.setFont(new Font("Arial", Font.BOLD, fontSize)); // setting font
        
        JPanel jp2 = new JPanel(new GridLayout(1, 1));
        jp2.setPreferredSize(new Dimension(10, 25));
        jp2.setBorder(new EmptyBorder(0, 5, 0, 0)); //Pushes object inside further along
        
        jp2.setBackground(Color.LIGHT_GRAY);
        jp2.add(setPriceLabel);
        centrePanel.add(jp2, BorderLayout.CENTER);
        
        //########################
        //Label
        JLabel setQuantityLabel = new JLabel("Quantity (G,L)");
        setQuantityLabel.setFont(new Font("Arial", Font.BOLD, fontSize)); // setting font
        
        JPanel jp3 = new JPanel(new GridLayout(1, 1));
        jp3.setPreferredSize(new Dimension(120, 34));
        jp3.setBorder(new EmptyBorder(0, 15, 0, 0)); //Pushes object inside further along
        
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
        
        JPanel jp4 = new JPanel(new GridLayout(1, 1));
        jp4.setBackground(Color.LIGHT_GRAY);
        jp4.setPreferredSize(new Dimension(120, 34));
        jp4.setBorder(new EmptyBorder(0, 10, 0, 0)); //Pushes object inside further along
        
        jp4.add(deleteBtnLabel);
        eastPanel.add(jp4);
        
        //#########################################################################################################
        // Adding to GUI
        //#########################################################################################################
        add_To_Container(inputArea, shopFormPanelGUI, 0, yPos += 1, 1, 1, 0.25, 0.25, "both", 0, 0, null);
        parentContainer.revalidate();
    }
    
    //#############################################################################################################
    // Methods
    //#############################################################################################################
    public boolean validate_Form()
    {
        //######################################################
        //
        //######################################################
        String overalErrorTxt = "";
        int pos = 0;
        
        
        //######################################################
        //
        //######################################################
        
        for (Add_ShopForm_Object shopForm_object : shopFormObjects)
        {
            System.out.println("\n\nhere");
            //#######################################
            // Reset Values
            //#######################################
            pos++;
            String space = "\n\n", iterationErrorTxt = "";
            
            //#######################################
            // Validate Stores
            //#######################################
            String shopChosen = shopForm_object.get_ProductShops_TXT();
            
            if (shopChosen.equals("No Shop"))
            {
                iterationErrorTxt += String.format("%sOn Row %s,  please Select a shop that isn't 'No Shop'! Or, delete the row!", space, pos);
                space = "\n";
            }
            
            //########################################
            // Validate Product Name
            //########################################
            String productNameTxt = shopForm_object.get_ProductName_Txt();
            if (productNameTxt.equals(""))
            {
                iterationErrorTxt += String.format("%sOn Row: %s, the 'Product Name' cannot be empty or, ' NULL '!", space, pos);
                space = "\n";
            }
            
            //#######################################
            // Validate Prices
            //#######################################
            JTextField prices = shopForm_object.get_Product_Price_TxtField();
            String price = shopForm_object.get_ProductPrice_Txt();
            
            if (! (price.equals(""))) // Check if text field input is empty
            {
                String txt = convert_To_Big_Decimal(price, "Prices", pos, prices, true);
                if (! txt.equals(""))
                {
                    if (iterationErrorTxt.equals(""))
                    {
                        space = "\n";
                        iterationErrorTxt = String.format("\n%s", txt);
                    }
                    else
                    {
                        iterationErrorTxt += txt;
                    }
                }
            }
            else
            {
                iterationErrorTxt += String.format("%sOn Row: %s, the 'Price' must have a value which is not ' NULL '!", space, pos);
                space = "\n";
            }
            
            //#######################################
            // Validate Quantity
            //#######################################
            JTextField quantityObj = shopForm_object.get_Product_Quantity_Per_Pack_TxtField();
            String quantity = shopForm_object.get_Product_Quantity_Per_Pack_Txt();
            
            if (! (quantity.equals(""))) // Check if text field input is empty
            {
                String txt = convert_To_Big_Decimal(quantity, "Quantity", pos, quantityObj, true);
                if (! txt.equals(""))
                {
                    if (iterationErrorTxt.equals(""))
                    {
                        iterationErrorTxt = String.format("\n%s", txt);
                    }
                    else
                    {
                        iterationErrorTxt += txt;
                    }
                }
            }
            else
            {
                iterationErrorTxt += String.format("%sOn Row: %s, the 'Quantity' must have a value which is not ' NULL '!", space, pos);
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
    
    public String[] get_ShopForm_Update_String()
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
        String updateString = String.format("INSERT INTO ingredient_in_shops (ingredient_id, product_name, volume_per_unit, cost_per_unit, store_id) VALUES ");
        
        ///#################################
        // Creating String for Add Values
        //#################################
        String values = "";
        Iterator<Add_ShopForm_Object> it = shopFormObjects.iterator();
        
        while (it.hasNext())
        {
            Add_ShopForm_Object shopForm_object = it.next();
            
            values += String.format("(%s, '%s', %s, %s, (SELECT store_id FROM stores WHERE store_name = '%s'))",
                    mysqlVariableReference,
                    shopForm_object.get_ProductName_Txt(),
                    shopForm_object.get_Product_Quantity_Per_Pack_Txt(),
                    shopForm_object.get_ProductPrice_Txt(),
                    shopForm_object.get_ProductShops_TXT());
            
            if (! (it.hasNext()))
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
        
        return new String[]{ createMysqlVariable, updateString };
    }
    
    //#############################################################################################################
    // Clear Form Methods
    //#############################################################################################################
    public void clear_Shop_Form()
    {
        Iterator<Add_ShopForm_Object> it = shopFormObjects.iterator();
        while (it.hasNext())
        {
            Add_ShopForm_Object i = it.next();
            i.remove_From_Parent_Container();
            it.remove();
        }
        
        extra_Clear_Shops_Form();
        
        parentContainer.revalidate();
    }
    
    protected void extra_Clear_Shops_Form()
    {
    
    }
    
    //#############################################################################################################
    // NEW CLASS
    //#############################################################################################################
    public class Add_ShopForm_Object extends JPanel
    {
        Integer PDID = null;  //EDIT NOW
        Container parentContainer;
        JComboBox<String> productShops_JComboBox;
        JTextField productName_TxtField, productPrice_TxtField, quantityPerPack_TxtField;
        
        //#############################################################################################################
        // Constructor
        //#############################################################################################################
        Add_ShopForm_Object()
        { }
        
        public Add_ShopForm_Object(Container parentContainer)// Remove
        {
            this.parentContainer = parentContainer;
            
            //###############################
            // Creating Sections for GUI
            //###############################
            if (ingredients_info_screen.get_StoresNames_List() == null)
            {
                JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "Unable To Get ShopNames From DB; \nEither No Shops Exist. \nOr, Internal DB Error");
                return;
            }
            
            //#########################################################################################################
            //Creating JPanel
            //#########################################################################################################
            
            this.setLayout(new BorderLayout());
            
            //###############################
            // Creating Sections for GUI
            //###############################
            
            //
            JPanel westPanel = new JPanel(new GridLayout(1, 1));
            westPanel.setBorder(new EmptyBorder(0, 0, 0, 10));
            westPanel.setPreferredSize(new Dimension(150, 25)); // width, height
            this.add(westPanel, BorderLayout.WEST);
            
            //
            JPanel centrePanel = new JPanel(new BorderLayout());
            centrePanel.setBackground(Color.BLUE);
            this.add(centrePanel, BorderLayout.CENTER);
            
            // Delete row button section
            JPanel eastPanel = new JPanel(new GridLayout(1, 1));
            eastPanel.setBorder(new EmptyBorder(0, 5, 0, 0));
            eastPanel.setPreferredSize(new Dimension(110, 34)); // width, height
            this.add(eastPanel, BorderLayout.EAST);
            
            //#####################################################
            // West Side
            //######################################################
            
            // create JComboBox
            productShops_JComboBox = new JComboBox<String>();
            load_Stores_In_JComboBox();
            
            ((JLabel) productShops_JComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
            westPanel.add(productShops_JComboBox);
            
            //#####################################################
            // Centre Side
            //######################################################
            
            //Product Name JTextField
            productName_TxtField = new JTextField();
            productName_TxtField.setDocument(new JTextFieldLimit(100));
            productName_TxtField.setText("");
            
            JPanel jp = new JPanel(new GridLayout(1, 1));
            jp.setPreferredSize(new Dimension(270, 34));
            jp.setBorder(new EmptyBorder(0, 0, 0, 0)); //Pushes object inside further along
            jp.add(productName_TxtField);
            centrePanel.add(jp, BorderLayout.WEST);
            
            // Product Price
            productPrice_TxtField = new JTextField();
            productPrice_TxtField.setDocument(new JTextFieldLimit(charLimit));
            productPrice_TxtField.setText("0.00");
            
            JPanel jp2 = new JPanel(new GridLayout(1, 1));
            jp2.setPreferredSize(new Dimension(10, 25));
            jp2.setBorder(new EmptyBorder(0, 5, 0, 0)); //Pushes object inside further along
            jp2.add(productPrice_TxtField);
            centrePanel.add(jp2, BorderLayout.CENTER);
            
            // Quantity
            quantityPerPack_TxtField = new JTextField();
            quantityPerPack_TxtField.setDocument(new JTextFieldLimit(charLimit));
            quantityPerPack_TxtField.setText("0.00");
            
            JPanel jp3 = new JPanel(new GridLayout(1, 1));
            jp3.setPreferredSize(new Dimension(120, 34));
            jp3.setBorder(new EmptyBorder(0, 5, 0, 0)); //Pushes object inside further along
            jp3.add(quantityPerPack_TxtField);
            centrePanel.add(jp3, BorderLayout.EAST);
            
            //#####################################################
            // East Side
            //######################################################
            
            // Creating submit button
            JButton deleteRowBtn = new JButton("Delete Row");
            deleteRowBtn.setPreferredSize(new Dimension(140, 34));
            deleteRowBtn.setFont(new Font("Arial", Font.BOLD, 12)); // setting font
            
            // creating commands for submit button to execute on
            deleteRowBtn.addActionListener(ae -> {
                delete_Row_Action();
            });
            
            JPanel jp4 = new JPanel(new GridLayout(1, 1));
            jp4.setPreferredSize(new Dimension(140, 34));
            jp4.setBorder(new EmptyBorder(0, 0, 0, 0)); //Pushes object inside further along
            jp4.add(deleteRowBtn);
            eastPanel.add(jp4);
            
            //#####################################################
            // Adding row to memory
            //######################################################
            shopFormObjects.add(this);
            
            //#########################################################################################################
            // Adding Object To GUI
            //#########################################################################################################
            add_To_Container(parentContainer, this, 0, yPos += 1, 1, 1, 0.25, 0.25, "both", 0, 0, null);
            parentContainer.revalidate();
            
        }
        
        //#############################################################################################################
        // Other Methods
        //#############################################################################################################
        protected void load_Stores_In_JComboBox()
        {
            productShops_JComboBox.removeAllItems();
            Collection<String> storesNamesList = ingredients_info_screen.get_StoresNames_List();
            
            for (String storeName : storesNamesList)
            {
                productShops_JComboBox.addItem(storeName);
            }
            
            productShops_JComboBox.setSelectedItem("No Shop"); // make the first selected item N/A
        }
        
        protected void delete_Row_Action()
        {
            remove_From_Parent_Container(); // remove all the  input GUI objects from memory
            shopFormObjects.remove(this);
        }
        
        protected void remove_From_Parent_Container()
        {
            //Remove from parent Container
            parentContainer.remove(this);
            
            //Resizing
            parentContainer.revalidate();
            
            //Resizing Form
            add_ingredientsScreen.resize_GUI();
        }
        
        //#############################################################################################################
        // Mutator Methods
        //#############################################################################################################
        protected void set_PDID(Integer PDID)
        {
            this.PDID = PDID;
        }
        
        //#############################################################################################################
        // Accessor Methods
        //#############################################################################################################
        public Integer get_PDID()
        {
            return PDID;
        }
        
        //########################################
        // Get TXT
        //########################################
        public String get_ProductShops_TXT()
        {
            return productShops_JComboBox.getSelectedItem().toString();
        }
        
        public String get_ProductName_Txt()
        {
            return productName_TxtField.getText().trim();
        }
        
        public String get_ProductPrice_Txt()
        {
            return productPrice_TxtField.getText().trim();
        }
        
        public String get_Product_Quantity_Per_Pack_Txt()
        {
            return quantityPerPack_TxtField.getText().trim();
        }
        
        //########################################
        // Get Object
        //########################################
        protected JComboBox<String> get_ProductShop_JComboBox()
        {
            return productShops_JComboBox;
        }
        
        protected JTextField get_ProductName_TxtField()
        {
            return productName_TxtField;
        }
        
        protected JTextField get_Product_Price_TxtField()
        {
            return productPrice_TxtField;
        }
        
        protected JTextField get_Product_Quantity_Per_Pack_TxtField()
        {
            return quantityPerPack_TxtField;
        }
    }
}