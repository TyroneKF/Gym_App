package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Shop_Form;

import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Store_ID_OBJ;
import App_Code.Objects.Gui_Objects.Combo_Boxes.Field_JCombo_Storable_ID;
import App_Code.Objects.Gui_Objects.Text_Fields.Field_JTxtField;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
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
    protected Field_JTxtField
            product_Name_JT = new Field_JTxtField("name", string_Char_Limit),
            product_Price_JT = new Field_JTxtField("price", decimal_Char_Limit, true, false),
            quantity_JT = new Field_JTxtField("quantity", decimal_Char_Limit, true, false);
    
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
        
        stores_JC = new Field_JCombo_Storable_ID<>("store", Store_ID_OBJ.class, stores);  // Component
        
        //###############################
        // Create GUI
        //###############################
        create_Field_Items_Map();
        create_GUI();
    }
    
    private void create_Field_Items_Map()
    {
        field_Items_Map = new LinkedHashMap<>()
        {
            {
               put("name", new Shop_Form_Binding<>(
                        "Product Name",       // GUI Label
                        product_Name_JT,               // Component
                        "product_name",                // MySQL Field
                        String.class                   // Field Type
                ));
                
                put("quantity", new Shop_Form_Binding<>(
                        "Quantity Per Pack",         // GUI Label
                        quantity_JT,                          // Component
                        "volume_per_unit",                   // MySQL Field
                        BigDecimal.class                    // Field Type
                ));
                
                put("price", new Shop_Form_Binding<>(
                        "Product Price",         // GUI Label
                        product_Price_JT,                // Component
                        "cost_per_unit",                 // MySQL Field
                        BigDecimal.class                // Field Type
                ));
                
                put("store", new Shop_Form_Binding<>(
                        "Select A Store",    // GUI Label
                        stores_JC,                    // Component
                        "store_id"                    // MySQL Field
                ));
            }
        };
    }
    
    protected void create_GUI()
    {
        //#########################################################
        //Creating JPanel
        //#########################################################
        setLayout(new BorderLayout());
        
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
        
        //#####################################################
        // West Side
        //######################################################
        
        // create JComboBox
        westPanel.add(field_Items_Map.get("store").get_Gui_Component());
        
        //#####################################################
        // Centre Side
        //######################################################
        //Product Name JTextField
        product_Name_JT = new Field_JTxtField("Product Name", 255);
        
        JPanel jp = new JPanel(new GridLayout(1, 1));
        jp.setPreferredSize(new Dimension(270, 34));
        jp.setBorder(new EmptyBorder(0, 0, 0, 0)); //Pushes object inside further along
        jp.add(product_Name_JT);
        centrePanel.add(jp, BorderLayout.WEST);
        
        // Product Price
        product_Price_JT = new Field_JTxtField("Price", 8, true, false);
        
        JPanel jp2 = new JPanel(new GridLayout(1, 1));
        jp2.setPreferredSize(new Dimension(10, 25));
        jp2.setBorder(new EmptyBorder(0, 5, 0, 0)); //Pushes object inside further along
        jp2.add(product_Price_JT);
        centrePanel.add(jp2, BorderLayout.CENTER);
        
        // Quantity
        quantity_JT = new Field_JTxtField("Quantity", 8, true, false);
        
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
    protected void remove_From_Parent_Container()
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

