package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Shop_Form;

import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Store_ID_OBJ;
import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Gui_Objects.IconPanel;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Ingredients_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Parent_Forms_OBJ;
import org.javatuples.Pair;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;

public class Shop_Form extends Parent_Forms_OBJ
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    
    // Objects
    protected Ingredients_Screen ingredient_Screen;
    
    // Screen Objects
    protected Container parentContainer;
    protected JPanel input_Area, northPanel;
    
    //#############################
    // Collections
    //#############################
    protected ArrayList<Store_ID_OBJ> stores;
    protected ArrayList<ShopForm_Object> add_shop_Form_Objects = new ArrayList<>();
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Shop_Form(Container parentContainer, String btn_Text, Ingredients_Screen ingredient_Screen, ArrayList<Store_ID_OBJ> stores)
    {
        //############################################
        // Super
        //############################################
        super(parentContainer, btn_Text);
        
        
        //############################################
        // Variables
        //############################################
        this.parentContainer = parentContainer;
        this.ingredient_Screen = ingredient_Screen;
        this.stores = stores;
        
        //############################################
        // Create GUI
        //############################################
        create_Gui();
    }
    
    //##################################################################################################################
    //  Methods
    //##################################################################################################################
    //  Create GUI Methods
    public void create_Gui()
    {
        int fontSize = 14;
        //#############################################################
        // Creating GUI Section
        //#############################################################
        JPanel screen_JP = collapsibleJPanel.get_Centre_JPanel();
        screen_JP.setLayout(new BorderLayout());
        
        //#############################################
        // North
        //#############################################
        // Creating North JPanel Area with 2 rows
        northPanel = new JPanel(new GridBagLayout());
        screen_JP.add(northPanel, BorderLayout.NORTH);
        
        // Main Label
        JPanel title_Label_Panel = create_Label_JP("Add Suppliers", new Font("Verdana", Font.PLAIN, 24), Color.GREEN);
        add_To_Container(northPanel, title_Label_Panel, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0, null);
        
        // Create Icons
        create_Icon_Section();
        
        //############################################
        // Centre
        //############################################
        input_Area = new JPanel(new GridBagLayout());
        screen_JP.add(input_Area, BorderLayout.CENTER);
        
        JPanel shop_Form_Panel_GUI = new JPanel(new BorderLayout());
        add_To_Container(input_Area, shop_Form_Panel_GUI, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);
        
        //##################
        //
        //##################
        //
        JPanel west_JPanel = create_Section_JP(150, 25,0, 0, 0, 10 );
        shop_Form_Panel_GUI.add(west_JPanel, BorderLayout.WEST);
        
        // Panel
        west_JPanel.setBorder(new EmptyBorder(0, 10, 0, 10));
        west_JPanel.setBackground(Color.LIGHT_GRAY);
        
        // Label
        JPanel westLabel = create_Label_JP("Select A Store",new Font("Arial", Font.BOLD, fontSize),Color.LIGHT_GRAY);
        west_JPanel.add(westLabel);
        
        //###################
        //
        //###################
        //
        JPanel centre_JPanel = new JPanel(new BorderLayout());
        shop_Form_Panel_GUI.add(centre_JPanel, BorderLayout.CENTER);
        
        //############################################
        // Centre Panel
        //############################################
        JPanel jp = create_Section_JP(270, 34, 0, 80, 0, 0);
        centre_JPanel.add(jp, BorderLayout.WEST);
        
        //Label
        JPanel setProductNameLabel = create_Label_JP("Set Product Name", new Font("Arial", Font.BOLD, fontSize),Color.LIGHT_GRAY );
        jp.add(setProductNameLabel);
        
        
        //########################
        //Label
        JPanel jp2 = create_Section_JP(10, 25, 0, 5, 0, 0);
        centre_JPanel.add(jp2, BorderLayout.CENTER);
        
        JPanel setPriceLabel = create_Label_JP("Set Price (£)", new Font("Arial", Font.BOLD, fontSize),Color.LIGHT_GRAY );
        jp2.add(setPriceLabel);
        
        //########################
        JPanel jp3 = create_Section_JP(120, 34, 0, 15, 0, 0);
        jp3.setBackground(Color.LIGHT_GRAY);
        centre_JPanel.add(jp3, BorderLayout.EAST);
        
        //Label
        JPanel setQuantityLabel = create_Label_JP("Quantity (G,L)", new Font("Arial", Font.BOLD, fontSize),Color.LIGHT_GRAY);
        jp3.add(setQuantityLabel);
        
        //############################################
        // East Panel
        //############################################
        
        // Delete row button section
        JPanel east_JPanel = create_Section_JP(110, 34, 0, 5, 0, 0);
        shop_Form_Panel_GUI.add(east_JPanel, BorderLayout.EAST);
        
        //panel
        east_JPanel.setBackground(Color.LIGHT_GRAY);
        
        
        JPanel jp4 = create_Section_JP(120, 34, 0, 10, 0, 0);
        jp4.setBackground(Color.LIGHT_GRAY);
        east_JPanel.add(jp4);
        
        //Label
        JPanel deleteBtnLabel = create_Label_JP("Delete Row",new Font("Arial", Font.BOLD, fontSize), Color.LIGHT_GRAY );
        jp4.add(deleteBtnLabel);
        
        //############################################
        // Adding to GUI
        //############################################
      
        resize_GUi();
    }
    
    protected void create_Icon_Section()
    {
        //############################
        // Icon Section
        //############################
        IconPanel iconPanel = new IconPanel(1, 10, "East");
        add_To_Container(northPanel, iconPanel.getIconAreaPanel(), 0, 1, 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
        
        JPanel iconPanelInsert = iconPanel.getIconJpanel();
        
        //###############
        // Add BTN Icon
        //###############
        int width = 30, height = 30;
        
        IconButton add_Icon_Btn = new IconButton("/images/add/++add.png", width, height, width, height, "centre", "right"); // btn text is useless here , refactor
        iconPanelInsert.add(add_Icon_Btn); // Add btn to GUI
        
        JButton add_Btn = add_Icon_Btn.returnJButton();
        add_Icon_Btn.makeBTntransparent();
        
        add_Btn.addActionListener(ae -> {
            
            add_Btn_Action();
        });
    }
    
    //#################################
    //  Btn Action Methods
    //#################################
    protected void add_Btn_Action()
    {
        // Create ShopForm Object & Add
        ShopForm_Object add_shop_form_object = new ShopForm_Object(input_Area, this, stores);
        add_shop_Form_Objects.add(add_shop_form_object);
        
        // Adding Object To GUI
        add_To_Container(input_Area, add_shop_form_object, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);
        
        // Resize GUI
        resize_GUI();
    }
    
    //#################################
    //  Others
    //#################################
    protected void remove_Shop_Form_Obj(ShopForm_Object shop_Object)
    {
        add_shop_Form_Objects.remove(shop_Object);
    }
    
    public void reload_Stores_JC()
    {
        for (ShopForm_Object shopForm_object : add_shop_Form_Objects)
        {
            shopForm_object.reload_Stores_JC();
        }
    }
    
    //#################################################################
    // Validation & Updates
    //#################################################################
    public boolean validate_Form()
    {
        ArrayList<LinkedHashMap<String, ArrayList<String>>> all_Errors = new ArrayList<>();
        
        //##################################
        // Get Errors Per Row / Add to DATA
        //##################################
        for (int pos = 0; pos < add_shop_Form_Objects.size(); pos++)
        {
            ShopForm_Object shopForm_object = add_shop_Form_Objects.get(pos); // Get Shop Form Object
            
            LinkedHashMap<String, ArrayList<String>> error_Map = new LinkedHashMap<>(); // Create error Map
            
            if (shopForm_object.validation_Check(error_Map)) { continue; } // If no error added continue
            
            all_Errors.add(error_Map);
        }
        
        //###############################
        // Escape Clause
        //###############################
        if (all_Errors.isEmpty()) { return true; }
        
        //###############################
        // Display Errors
        //###############################
        UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 16));
        JOptionPane.showMessageDialog(null, build_Error_MSg(all_Errors), "Shops Form Error Messages", JOptionPane.INFORMATION_MESSAGE);
        
        //##################################
        // Output
        //##################################
        return false;
    }
    
    protected String build_Error_MSg(ArrayList<LinkedHashMap<String, ArrayList<String>>> all_Errors)
    {
        //##################################
        // Build Error MSG
        //##################################
        /**
         * HTML:
         * &nbsp; = space
         * <br> = line break
         * <b></b> = bold
         */
        
        StringBuilder error_MSG = new StringBuilder("<html>");
        
        for (int pos = 0; pos < all_Errors.size(); pos++)
        {
            error_MSG.append(String.format("<br><br><div align='center'><b> Shop Form Row %s </b></div>", pos + 1));
            LinkedHashMap<String, ArrayList<String>> errors = all_Errors.get(pos); // = One Rows Errors
            
            for (Map.Entry<String, ArrayList<String>> error_Entry : errors.entrySet())
            {
                ArrayList<String> error_MSGs = error_Entry.getValue();
                
                // Singular Error MSG
                if (error_MSGs.size() == 1)
                {
                    error_MSG.append(String.format("<br><br><b>%s&nbsp;:&nbsp;</b> %s", error_Entry.getKey(), error_MSGs.getFirst()));
                    continue;
                }
                
                // Multiple Error Messages
                error_MSG.append(String.format("<br><br><b>%s:</b>", error_Entry.getKey()));
                
                for (String error : error_MSGs)
                {
                    error_MSG.append(String.format("<br>&nbsp;&nbsp;&nbsp;&nbsp;<b>.</b>&nbsp; %s", error));
                }
            }
        }
        
        error_MSG.append("</html>");
        
        //##################################
        // Return Output
        //##################################
        return error_MSG.toString();
    }
    
    public void add_Update_Queries(LinkedHashSet<Pair<String, Object[]>> queries_And_Params) throws Exception
    {
        //###########################################
        // Exit Clause
        //###########################################
        if (add_shop_Form_Objects.isEmpty()) { return; }
        
        
        //###########################################
        // Get Inserted Ingredient ID Query
        //###########################################
        // Get Insert Statement
        String var_Ingredient_ID = "@IngredientID";
        String upload_Q1 = String.format("Set %s = LAST_INSERT_ID();", var_Ingredient_ID);
        
        queries_And_Params.add(new Pair<>(upload_Q1, null));
        
        //###########################################
        // Create Query for Upload Products
        //###########################################
        int size = add_shop_Form_Objects.size();
        int input_Params_Per_Item = 4; // Ingredient ID isn't a field inputted by the user
        
        //######################
        // Create Params Query
        //######################
        StringBuilder insert_Header = new StringBuilder("INSERT INTO ingredient_in_shops (ingredient_id, product_name, volume_per_unit, cost_per_unit, store_id) VALUES");
        StringBuilder values = new StringBuilder();
        
        
        //######################
        // Create Params Query
        //######################
        Object[] params = new Object[size * input_Params_Per_Item];
        
        try
        {
            for (int pos = 0; pos < size; pos++)
            {
                ShopForm_Object shopForm_object = add_shop_Form_Objects.get(pos);
                
                int base = pos * input_Params_Per_Item;
                
                // Add to Values
                values.append(
                        pos == size - 1
                                ? String.format("(%s, ?, ?, ?, ?);", var_Ingredient_ID)
                                : String.format("(%s, ?, ?, ?, ?),", var_Ingredient_ID)
                );
                
                shopForm_object.add_Params(params, base); // Each Item add its update statements
            }
        }
        catch (Exception e)
        {
            throw e;
        }
        
        //###########################
        // Edit Values Statement
        //###########################
        System.out.printf("\n\nInsert Headers: \n%s \n\nValues: \n%s  \n\nParams: \n%s%n", insert_Header, values, Arrays.toString(params));
        
        //##########################
        // Add To Results
        //##########################
        StringBuilder update_Query = insert_Header.append(values);
        queries_And_Params.add(new Pair<>(update_Query.toString(), params));
    }
    
    //################################################################
    // Clear Form Methods
    //################################################################
    public void clear_Shop_Form()
    {
        Iterator<ShopForm_Object> it = add_shop_Form_Objects.iterator();
        
        while (it.hasNext())
        {
            ShopForm_Object i = it.next();
            i.remove_From_Parent_Container();
            it.remove();
        }
        
        extra_Clear_Shops_Form(); // Extra Clear elements
        resize_GUi(); // Resize GUI  Elements
    }
    
    protected void extra_Clear_Shops_Form()
    {
    
    }
    
    //################################################################
    // Resizing Methods
    //################################################################
    public void resize_GUi()
    {
        input_Area.revalidate();
        parentContainer.revalidate();
        ingredient_Screen.resize_GUI();
    }
    
}