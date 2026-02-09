package com.donty.gymapp.ui.screens.ingredientsAndInventory.Ingredients_Info.base.screen;

import com.donty.gymapp.persistence.database.MyJDBC_Sqlite;
import com.donty.gymapp.persistence.Shared_Data_Registry;
import com.donty.gymapp.gui.base.Screen_JPanel;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.Ingredients_Info.Search_For_Food_Info;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.Ingredients_Info.add.ingredients.Ingredients_Form;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.Ingredients_Info.add.products.Shop_Form;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.Ingredients_Info.Ingredients_Info.Ingredients_Info_Screen;

import javax.swing.*;
import java.awt.*;

public abstract class Parent_Ingredients_Screen extends Screen_JPanel
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    // JPanel
    protected JPanel scroll_JPanel, mainCentre_JPanel;
    
    // Objects
    protected MyJDBC_Sqlite db;
    protected Frame frame;
    
    // Screen Objects
    protected Ingredients_Form ingredients_Form;
    protected Shop_Form shop_Form;
    protected Search_For_Food_Info search_For_Ingredient_Info;
    protected Ingredients_Info_Screen ingredients_info_screen;
    protected Shared_Data_Registry shared_Data_Registry;

    protected String error_msg;

    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Parent_Ingredients_Screen(Ingredients_Info_Screen ingredients_info_screen, MyJDBC_Sqlite db, Shared_Data_Registry shared_Data_Registry)
    {
        //##########################################################
        // Super Constructor
        //##########################################################
        super(null, true, 800, 850);
        
        //##########################################################
        // Variables
        //##########################################################
        // Objects
        this.ingredients_info_screen = ingredients_info_screen;
        this.db = db;
        this.shared_Data_Registry = shared_Data_Registry;
        
        frame = ingredients_info_screen.getFrame();
        
        //#########################################################
        //   Create Screen for Interface
        //#########################################################
        scroll_JPanel = get_ScrollPane_JPanel();
        scroll_JPanel.setLayout(new BorderLayout());
        
        // Main Centre JPanel
        mainCentre_JPanel = new JPanel(new GridBagLayout());
        scroll_JPanel.add(mainCentre_JPanel, BorderLayout.CENTER);
        
        //##########################################################
        //   Create GUI Objects
        //#########################################################
        create_GUI_Objects();
        create_GUI();
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    // Create GUI Methods
    protected abstract void create_GUI_Objects();
    
    protected abstract void prior_GUI_Setup();
    
    protected final void create_GUI()
    {
        //#############################
        // Prior Setup
        //#############################
        prior_GUI_Setup();
        
        //#############################
        // Add Objects to GUI
        //#############################
        //Search For Ingredients Form
        add_To_Container(mainCentre_JPanel, search_For_Ingredient_Info, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);
        
        add_To_Container(mainCentre_JPanel, new JPanel(), 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 10, 0, null);
        
        add_To_Container(mainCentre_JPanel, ingredients_Form, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);
        
        add_To_Container(mainCentre_JPanel, new JPanel(), 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 10, 0, null);
        
        //#############################
        // Add shop
        //#############################
        add_To_Container(mainCentre_JPanel, shop_Form, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);
        
        
        //##############################
        //Space Divider
        //##############################
        add_To_Container(mainCentre_JPanel, new JPanel(), 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 10, 0, null);
        
        
        //###############################
        // South Screen for Interface
        //###############################
        // Creating Submit Button
        JButton submitButton = new JButton("Submit Form");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14)); // setting font
        submitButton.setPreferredSize(new Dimension(50, 50)); // width, height
        
        // creating commands for submit button to execute on
        submitButton.addActionListener(ae -> {
            submission_Btn_Action();
        });
        
        get_Main_South_JPanel().setLayout(new GridLayout(1, 1));
        get_Main_South_JPanel().add(submitButton, BorderLayout.SOUTH);
        
        //#############################
        // Resizing GUI
        //#############################
        resize_GUI();
    }
    
    //##################################################################
    // Submission Button Actions
    //##################################################################
    protected final void submission_Btn_Action()
    {
        //###############################
        // Validate Screen / Forms
        //###############################
        if (! prior_Form_Validations()) { return; }
        
        boolean
                ingredients_Form_Validated = ingredients_Form.validate_Ingredients_Form(),
                shop_Form_Validated = shop_Form.validate_Form();
        
        //###############################
        // Update
        //###############################
        if (! ingredients_Form_Validated || ! shop_Form_Validated) { return; }
        
        //###############################
        // Accept : Data Formatting
        //###############################
        String
                title_Upload = "Accept Data Formatting",
                message_Upload = "upload these values as they may have been changed / adapted to fit our data type format";
        
        if (! are_You_Sure(title_Upload, message_Upload)) { return; }
        
        //#################################
        // Ask to Add Ingredient / Products
        //#################################
        String title_Create = "Create New Ingredient";
        String message_Create = "Are you sure you want to add this Ingredient?";
        
        if (! are_You_Sure(title_Create, message_Create)) { return; }
        
        //##################################
        // Update Both Forms
        //##################################
        if (! update_Both_Forms()) // MYSQL & Shared Data
        {
            JOptionPane.showMessageDialog(null, "\n\nError, Uploading Ingredients / Product Values!");
            return;
        }
        
        // Generate Update MSG depending on what was updated
        JOptionPane.showMessageDialog(get_Frame(), "\n\nUpdated Ingredient Info & Product Info ! ");
        
        //##################################
        // Update Shared Data
        //##################################
        if (! update_Shared_Data()) // Update Shared Data with Fetched Results
        {
            JOptionPane.showMessageDialog(null, "Failed Adding Ingredient to GUI, Reload App will Fix Issue!");
        }
        else
        {
            JOptionPane.showMessageDialog(get_Frame(), "The ingredient updates won't appear on the mealPlan screen until this window is closed!");
            ingredients_info_screen.set_Update_IngredientInfo(true); // Update Status
            update_Other_Screens(); // Update Other Screens
        }
        
        //################################
        // Reset Form & Update GUI
        //################################
        clear_Interface();
    }
    
    //###########################
    // Validation Methods
    //###########################
    protected abstract boolean prior_Form_Validations();
    
    //###########################
    // Update Methods
    //###########################
    protected abstract boolean update_Both_Forms();
    
    protected abstract boolean update_Shared_Data();
    
    protected abstract void update_Other_Screens();
    
    //##################################################################
    // Clearing GUI Methods
    //##################################################################
    protected abstract void clear_Interface();
    
    protected final void clear_All_Screens()
    {
        clear_Search_For_Ingredient_Info_Form();
        clear_Ingredients_Form();
        clear_Shop_Form();
    }
    
    protected final void clear_Search_For_Ingredient_Info_Form()
    {
        search_For_Ingredient_Info.resetFullDisplay();
    }
    
    protected final void clear_Ingredients_Form()
    {
        ingredients_Form.clear_Ingredients_Form();
    }
    
    public final void clear_Shop_Form()
    {
        shop_Form.clear_Shop_Form();
    }
    
    //##################################################################
    // Update Methods
    //##################################################################
    public abstract void reload_Ingredient_Type_JC();
    
    public final void reload_Stores_JC()
    {
        shop_Form.reload_Stores_JC();
    }
    
    //##################################################################
    // Accessor Methods
    //##################################################################
    public final Frame get_Frame()
    {
        return frame;
    }
}
