package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Shop_Form;

import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Store_ID_OBJ;
import App_Code.Objects.Gui_Objects.Combo_Boxes.Field_JCombo_Storable_ID;
import App_Code.Objects.Gui_Objects.Text_Fields.Field_JTxtField_BD;
import App_Code.Objects.Gui_Objects.Text_Fields.Field_JTxtField_String;

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
            string_Char_Limit = 255,
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
        
        product_Name_JT = new Field_JTxtField_String("name", string_Char_Limit);
        stores_JC = new Field_JCombo_Storable_ID<>("store", Store_ID_OBJ.class, stores);  // Component
        product_Price_JT = new Field_JTxtField_BD("price", decimal_Char_Limit);
        quantity_JT = new Field_JTxtField_BD("quantity", decimal_Char_Limit, false);
        
        //###############################
        // Create GUI
        //###############################
        create_Field_Items_Map();
        create_GUI2();
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
        
        //###############################
        // Creating Sections for GUI
        //###############################
        
        //
        JPanel westPanel = new JPanel(new GridLayout(1, 1));
        westPanel.setBorder(new EmptyBorder(0, 0, 0, 10));
        westPanel.setPreferredSize(new Dimension(150, 25)); // width, height
        add(westPanel, BorderLayout.WEST);
        
        //
        JPanel centrePanel = new JPanel(new BorderLayout());
        centrePanel.setBackground(Color.BLUE);
        add(centrePanel, BorderLayout.CENTER);
        
        // Delete row button section
        JPanel eastPanel = new JPanel(new GridLayout(1, 1));
        eastPanel.setBorder(new EmptyBorder(0, 5, 0, 0));
        eastPanel.setPreferredSize(new Dimension(110, 34)); // width, height
        add(eastPanel, BorderLayout.EAST);
        
        //######################################################
        // West Side
        //######################################################
        // Create JComboBox
        westPanel.add(stores_JC);
        
        //#####################################################
        // Centre Side
        //######################################################
        //Product Name JTextField
        
        JPanel jp = new JPanel(new GridLayout(1, 1));
        jp.setPreferredSize(new Dimension(270, 34));
        jp.setBorder(new EmptyBorder(0, 0, 0, 0)); //Pushes object inside further along
        jp.add(product_Name_JT);
        centrePanel.add(jp, BorderLayout.WEST);
        
        // Product Price
        //product_Price_JT = new Field_JTxtField_BD("Price", 8,  false);
        
        JPanel jp2 = new JPanel(new GridLayout(1, 1));
        jp2.setPreferredSize(new Dimension(10, 25));
        jp2.setBorder(new EmptyBorder(0, 5, 0, 0)); //Pushes object inside further along
        jp2.add(product_Price_JT);
        centrePanel.add(jp2, BorderLayout.CENTER);
        
        // Quantity
        
        JPanel jp3 = new JPanel(new GridLayout(1, 1));
        jp3.setPreferredSize(new Dimension(120, 34));
        jp3.setBorder(new EmptyBorder(0, 5, 0, 0)); //Pushes object inside further along
        jp3.add(quantity_JT);
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
    }
    
    protected void create_GUI2()
    {
        //#########################################################
        //Creating JPanel
        //#########################################################
        setLayout(new BorderLayout()); // Define layout of Object
        
        //###############################
        // Creating Sections for GUI
        //###############################
        
        // West Panel
        JPanel westPanel = create_Section_JP(150, 25,0, 0, 0, 10);
        add(westPanel, BorderLayout.WEST);
        
        // Centre Panel
        JPanel centrePanel = new JPanel(new BorderLayout());
        centrePanel.setBackground(Color.BLUE);
        add(centrePanel, BorderLayout.CENTER);
        
        // East Panel
        JPanel eastPanel =  create_Section_JP(110, 34,0, 5, 0, 0);
        add(eastPanel, BorderLayout.EAST);
        
        //######################################################
        // West Side
        //######################################################
        // Create JComboBox
        westPanel.add(stores_JC);
        
        //#####################################################
        // Centre Side
        //######################################################
        //Product Name JTextField
        
        JPanel jp = new JPanel(new GridLayout(1, 1));
        jp.setPreferredSize(new Dimension(270, 34));
        jp.setBorder(new EmptyBorder(0, 0, 0, 0)); //Pushes object inside further along
        jp.add(product_Name_JT);
        centrePanel.add(jp, BorderLayout.WEST);
        
        // Product Price
        //product_Price_JT = new Field_JTxtField_BD("Price", 8,  false);
        
        JPanel jp2 = new JPanel(new GridLayout(1, 1));
        jp2.setPreferredSize(new Dimension(10, 25));
        jp2.setBorder(new EmptyBorder(0, 5, 0, 0)); //Pushes object inside further along
        jp2.add(product_Price_JT);
        centrePanel.add(jp2, BorderLayout.CENTER);
        
        // Quantity
        
        JPanel jp3 = new JPanel(new GridLayout(1, 1));
        jp3.setPreferredSize(new Dimension(120, 34));
        jp3.setBorder(new EmptyBorder(0, 5, 0, 0)); //Pushes object inside further along
        jp3.add(quantity_JT);
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
    }
    
    
    //##################################################################################################################
    // Other Methods
    //##################################################################################################################
    protected void reload_Stores_JC()
    {
        stores_JC.reload_Items(); // Reload JC
    }
    
    protected void delete_Row_Action()
    {
        shop_form.remove_Shop_Form_Obj(this);
        
        remove_From_Parent_Container(); // remove all the  input GUI objects from memory
        resize_GUI();
    }
    
    //#########################################################
    // Resizing Methods
    //#########################################################
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
    
    public void add_Params(Object[] params, int base) throws Exception
    {
        params[base] = product_Name_JT.getText();
        params[base + 1] = quantity_JT.getText();
        params[base + 2] = product_Price_JT.getText();
        params[base + 3] = stores_JC.get_Selected_Item_ID();
    }
}

