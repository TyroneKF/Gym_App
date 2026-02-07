package com.donty.gymapp.screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Shop_Form;

import com.donty.gymapp.data_objects.Field_Bindings.Shop_Form_Binding;
import com.donty.gymapp.data_objects.ID_Objects.Storable_Ingredient_IDS.Store_ID_OBJ;
import com.donty.gymapp.gui.Combo_Boxes.Field_JCombo_Storable_ID;
import com.donty.gymapp.gui.Text_Fields.Field_JTxtField_BD;
import com.donty.gymapp.gui.Text_Fields.Field_JTxtField_String;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ShopForm_Object extends JPanel
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    
    // Int
    protected int
            string_Char_Limit = 100,
            decimal_Char_Limit = 8;
    
    // GUI Objects
    protected Container parent_Container;
    protected Field_JCombo_Storable_ID<Store_ID_OBJ> stores_JC;
    
    protected Field_JTxtField_String product_Name_JT;
    protected Field_JTxtField_BD product_Price_JT, quantity_JT;
    
    // Objects
    protected Shop_Form shop_form;
    
    // Collections
    protected ArrayList<Store_ID_OBJ> stores;
    
    //############
    // Maps
    //############
    protected LinkedHashMap<String, Shop_Form_Binding<?>> field_Items_Map;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public ShopForm_Object(Container parent_Container, Shop_Form shop_form, ArrayList<Store_ID_OBJ> stores)// Remove
    {
        //###############################
        // Variables
        //###############################
        this.parent_Container = parent_Container;
        this.shop_form = shop_form;
        this.stores = stores;  // Collections
        
        product_Name_JT = new Field_JTxtField_String("Product Nme", string_Char_Limit);
        stores_JC = new Field_JCombo_Storable_ID<>("Store Name", Store_ID_OBJ.class, stores);  // Component
        product_Price_JT = new Field_JTxtField_BD("Price", decimal_Char_Limit);
        quantity_JT = new Field_JTxtField_BD("Quantity", decimal_Char_Limit, false);
        
        //###############################
        // Create GUI
        //###############################
        create_Field_Items_Map();
        create_GUI();
    }
    
    //##################################################################################################################
    // Create GUI Methods
    //##################################################################################################################
    private void create_Field_Items_Map()
    {
        field_Items_Map = new LinkedHashMap<>()
        {{
            // ingredient_in_shops -> Skips pdid, ingredient_id (Position starts at 2)
            
            put("store", new Shop_Form_Binding<>(
                    "Select A Store",    // GUI Label
                    stores_JC,                    // Component
                    "store_id",                    // MySQL Field
                    5
            ));
            
            put("name", new Shop_Form_Binding<>(
                    "Product Name",       // GUI Label
                    product_Name_JT,               // Component
                    "product_name",                // MySQL Field
                    2
            ));
            
            put("price", new Shop_Form_Binding<>(
                    "Product Price",         // GUI Label
                    product_Price_JT,                // Component
                    "cost_per_unit",                 // MySQL Field
                    4
            ));
            
            put("quantity", new Shop_Form_Binding<>(
                    "Quantity Per Pack",         // GUI Label
                    quantity_JT,                          // Component
                    "volume_per_unit",                   // MySQL Field
                    3
            ));
        }};
    }
    
    protected JPanel create_Section_JP(int width, int height, int top_padding, int left_padding, int bottom_padding, int right_padding)
    {
        JPanel jp = new JPanel(new GridLayout(1, 1));
        jp.setBorder(new EmptyBorder(top_padding, left_padding, bottom_padding, right_padding));
        jp.setPreferredSize(new Dimension(width, height)); // width, height
        
        return jp;
    }
    
    protected void create_GUI()
    {
        //#########################################################
        //Creating JPanel
        //#########################################################
        setLayout(new BorderLayout()); // Define layout of Object
        
        // East Panel
        JPanel eastPanel = create_Section_JP(110, 34, 0, 5, 5, 0);
        add(eastPanel, BorderLayout.EAST);
        
        //######################################################
        // West Side
        //######################################################
        // Create West Side Panel
        JPanel westPanel = create_Section_JP(150, 25, 0, 0, 5, 10);
        add(westPanel, BorderLayout.WEST);
        
        westPanel.add(stores_JC); // Add JComboBox to GUI
        
        //#####################################################
        // Centre Side
        //######################################################
        JPanel centrePanel = new JPanel(new BorderLayout());  // Create Centre Panel
        add(centrePanel, BorderLayout.CENTER);
        
        //############################
        // Product Name : JTextField
        //############################
        JPanel product_Name_JP = create_Section_JP(330, 34, 0, 0, 5, 5);
        centrePanel.add(product_Name_JP, BorderLayout.WEST);
        
        product_Name_JP.add(product_Name_JT); // Add JComboBox to GUI
        
        //############################
        // Product Price : JTextField
        //############################
        JPanel product_Price_JP = create_Section_JP(5, 25, 0, 0, 5, 0);
        centrePanel.add(product_Price_JP, BorderLayout.CENTER);
        
        product_Price_JP.add(product_Price_JT); // Add JComboBox to GUI
        
        //############################
        // Quantity : JTextField
        //############################
        JPanel quantity_JP = create_Section_JP(90, 34, 0, 5, 5, 0);
        centrePanel.add(quantity_JP, BorderLayout.EAST);
        
        quantity_JP.add(quantity_JT); // Add JComboBox to GUI
        
        //######################################################
        // East Side
        //######################################################
        
        // Creating submit button
        JButton delete_Row_Btn = new JButton("Delete Row");
        delete_Row_Btn.setPreferredSize(new Dimension(140, 34));
        delete_Row_Btn.setFont(new Font("Arial", Font.BOLD, 12)); // setting font
        
        // Creating Events For Delete BTN
        delete_Row_Btn.addActionListener(ae -> {
            delete_Row_Action();
        });
        
        eastPanel.add(delete_Row_Btn); // Add Delete Btn GUI
    }
    
    //##################################################################################################################
    // Other Methods
    //##################################################################################################################
    // Action Methods
    protected void reload_Stores_JC()
    {
        stores_JC.reload_Items(); // Reload JC
    }
    
    
    //#########################################################
    // Validation & Update Methods
    //#########################################################
    public boolean validation_Check(LinkedHashMap<String, ArrayList<String>> error_Map)
    {
        product_Name_JT.validation_Check(error_Map);
        quantity_JT.validation_Check(error_Map);
        product_Price_JT.validation_Check(error_Map);
        stores_JC.validation_Check(error_Map);
        
        return error_Map.isEmpty();
    }
    
    public void add_Params(Object[] params, int base)
    {
        params[base] = product_Name_JT.getText();
        params[base + 1] = quantity_JT.getText();
        params[base + 2] = product_Price_JT.getText();
        params[base + 3] = stores_JC.get_Selected_Item_ID();
    }
    
    //#########################################################
    // Resizing Methods
    //#########################################################
    protected void delete_Row_Action()
    {
        shop_form.remove_Shop_Form_Obj_From_AL(this);
        
        remove_From_Parent_Container(); // remove all the  input GUI objects from memory
        resize_GUI();
    }
    
    public void remove_From_Parent_Container()
    {
        parent_Container.remove(this); //Remove from parent Container
    }
    
    protected void resize_GUI()
    {
        parent_Container.revalidate(); //Resizing
        
        shop_form.resize_GUI(); //Resizing Form
    }
    
    //#########################################################
    //  Accessor Methods
    //#########################################################
    public String get_Product_Name()
    {
        return product_Name_JT.get_Text();
    }
    
    public Integer get_Selected_Store_ID()
    {
        return stores_JC.get_Selected_Item_ID();
    }
}

