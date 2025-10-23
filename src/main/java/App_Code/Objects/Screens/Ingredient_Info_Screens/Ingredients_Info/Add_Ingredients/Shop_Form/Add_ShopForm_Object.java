/*
package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Shop_Form;

import App_Code.Objects.Gui_Objects.JTextFieldLimit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Collection;


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

*/
