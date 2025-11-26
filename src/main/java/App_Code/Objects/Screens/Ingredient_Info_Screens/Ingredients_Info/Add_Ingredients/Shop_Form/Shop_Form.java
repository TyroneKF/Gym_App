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
    protected JPanel inputArea;
    
    //#############################
    // Collections
    //#############################
    protected ArrayList<Store_ID_OBJ> stores;
    protected ArrayList<ShopForm_Object> shopFormObjects = new ArrayList<>();
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Shop_Form(Container parentContainer, Ingredients_Screen ingredient_Screen, ArrayList<Store_ID_OBJ> stores)
    {
        //############################################
        // Super & Variables
        //############################################
        super(parentContainer, "Add Suppliers");
        
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
        //############################################
        // Creating Form
        //############################################
        JPanel mainJPanel = collapsibleJPanel.get_Centre_JPanel();
        mainJPanel.setLayout(new BorderLayout());
        
        //############################################
        // North Frame
        //############################################
        // Creating North JPanel Area with 2 rows
        JPanel northPanel = new JPanel(new GridBagLayout());
        mainJPanel.add(northPanel, BorderLayout.NORTH);
        
        //############################################
        // Creating area for North JPanel (title area)
        //############################################
        JLabel titleLabel = new JLabel("Add Suppliers");
        titleLabel.setFont(new Font("Verdana", Font.PLAIN, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Color.green);
        titlePanel.add(titleLabel);
        
        // Add title JPanel to North Panel Area
        add_To_Container(northPanel, titlePanel, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0, null);
        
        //############################################
        // Creating area for North JPanel (Add Icon)
        //############################################
        IconPanel iconPanel = new IconPanel(1, 10, "East");
        JPanel iconPanelInsert = iconPanel.getIconJpanel();
        
        add_To_Container(northPanel, iconPanel.getIconAreaPanel(), 0, 1, 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
        
        //##########################
        // Add BTN Icon
        //##########################
        int width = 30;
        int height = 30;
        
        IconButton add_Icon_Btn = new IconButton("/images/add/++add.png", width, height, width, height, "centre", "right"); // btn text is useless here , refactor
        
        JButton add_Btn = add_Icon_Btn.returnJButton();
        add_Icon_Btn.makeBTntransparent();
        
        add_Btn.addActionListener(ae -> {
            
            add_Btn_Action();
        });
        
        iconPanelInsert.add(add_Icon_Btn);
        
        //############################################
        // Centre Form
        //############################################
        inputArea = new JPanel(new GridBagLayout());
        mainJPanel.add(inputArea, BorderLayout.CENTER);
        
        //##############################
        // ShopForm GUI
        //##############################
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
        
        //############################################
        // West Panel
        //############################################
        int fontSize = 14;
        
        // Panel
        westPanel.setBorder(new EmptyBorder(0, 10, 0, 10));
        westPanel.setBackground(Color.LIGHT_GRAY);
        
        // Label
        JLabel westLabel = new JLabel("Select A Store");
        westLabel.setFont(new Font("Arial", Font.BOLD, fontSize)); // setting font
        westPanel.add(westLabel);
        
        //############################################
        // Centre Panel
        //############################################
        
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
        
        //############################################
        // East Panel
        //############################################
        
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
        
        //############################################
        // Adding to GUI
        //############################################
        add_To_Container(inputArea, shopFormPanelGUI, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);
        parentContainer.revalidate();
    }
    
    //#################################
    //  Btn Action Methods
    //#################################
    private void add_Btn_Action()
    {
        // Create ShopForm Object & Add
        ShopForm_Object add_shop_form_object = new ShopForm_Object(inputArea, this, stores);
        shopFormObjects.add(add_shop_form_object);
        
        // Adding Object To GUI
        add_To_Container(inputArea, add_shop_form_object, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);
        
        // Resize GUI
        resize_GUI();
    }
    
    //#################################
    //  Others
    //#################################
    protected void remove_Shop_Form_Obj(ShopForm_Object shop_Object)
    {
        shopFormObjects.remove(shop_Object);
    }
    
    public void reload_Stores_JC()
    {
        for (ShopForm_Object shopForm_object : shopFormObjects)
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
        for (int pos = 0; pos < shopFormObjects.size(); pos++)
        {
            ShopForm_Object shopForm_object = shopFormObjects.get(pos); // Get Shop Form Object
            
            LinkedHashMap<String, ArrayList<String>> error_Map = new LinkedHashMap<>(); // Create error Map
            
            if (shopForm_object.validation_Check(error_Map)) { continue; } // If no error added continue
            
            all_Errors.add(error_Map);
        }
        
        if (all_Errors.isEmpty()) { return true; }
        
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
        
        //###############################
        // Display Errors
        //###############################
        UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 16));
        JOptionPane.showMessageDialog(null, error_MSG, "Shops Form Error Messages", JOptionPane.INFORMATION_MESSAGE);
        
        //##################################
        // Output
        //##################################
        return false;
    }
    
    public void add_Update_Queries(LinkedHashSet<Pair<String, Object[]>> queries_And_Params) throws Exception
    {
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
        int
                size = shopFormObjects.size(),
                input_Params_Per_Item = 4;
        
        StringBuilder insert_Header =
                new StringBuilder("INSERT INTO ingredient_in_shops (ingredient_id, product_name, volume_per_unit, cost_per_unit, store_id) VALUES"),
                values = new StringBuilder();
        
        Object[] params = new Object[size * input_Params_Per_Item];
        
        //######################
        // Create Update Query
        //######################
        try
        {
            for (int pos = 0; pos < size; pos++)
            {
                ShopForm_Object shopForm_object = shopFormObjects.get(pos);
                
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
        Iterator<ShopForm_Object> it = shopFormObjects.iterator();
        
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
        inputArea.revalidate();
        parentContainer.revalidate();
        ingredient_Screen.resize_GUI();
    }
    
}