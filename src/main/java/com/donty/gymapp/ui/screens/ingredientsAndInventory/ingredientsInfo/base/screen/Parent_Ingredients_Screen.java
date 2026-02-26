package com.donty.gymapp.ui.screens.ingredientsAndInventory.ingredientsInfo.base.screen;

import com.donty.gymapp.persistence.database.MyJDBC_Sqlite;
import com.donty.gymapp.persistence.Shared_Data_Registry;
import com.donty.gymapp.gui.base.Screen_JPanel;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.ingredientsInfo.add.ingredients.Ingredients_Form;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.ingredientsInfo.add.products.Shop_Form;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.ingredientsInfo.Ingredients_Info_Screen;

import javax.swing.*;
import java.awt.*;

public abstract class Parent_Ingredients_Screen extends Screen_JPanel
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    // JPanel
    protected final JPanel scroll_JPanel;
    protected final JPanel mainCentre_JPanel;

    // Objects
    protected final MyJDBC_Sqlite db;

    // Screen Objects
    protected Ingredients_Form ingredients_Form;
    protected Shop_Form shop_Form;
    protected final Ingredients_Info_Screen ingredients_info_screen;
    protected final Shared_Data_Registry shared_data_registry;

    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Parent_Ingredients_Screen
    (
            Ingredients_Info_Screen ingredients_info_screen,
            MyJDBC_Sqlite db,
            Shared_Data_Registry shared_data_registry
    )
    {
        // Super Constructor
        super(null, true, 800);

        // Variables
        this.ingredients_info_screen = ingredients_info_screen;
        this.db = db;
        this.shared_data_registry = shared_data_registry;

        // Create Screen for Interface
        scroll_JPanel = get_ScrollPane_JPanel();
        scroll_JPanel.setLayout(new BorderLayout());

        // Main Centre JPanel
        mainCentre_JPanel = new JPanel(new GridBagLayout());
        scroll_JPanel.add(mainCentre_JPanel, BorderLayout.CENTER);

        // Create GUI Objects
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
        add_To_Container(mainCentre_JPanel, new JPanel(), 0, get_And_Increase_YPos(), 0.25, "both", 10, 0);

        add_To_Container(mainCentre_JPanel, ingredients_Form, 0, get_And_Increase_YPos(), 0.25, "both", 0, 0);

        add_To_Container(mainCentre_JPanel, new JPanel(), 0, get_And_Increase_YPos(), 0.25, "both", 10, 0);

        //#############################
        // Add shop
        //#############################
        add_To_Container(mainCentre_JPanel, shop_Form, 0, get_And_Increase_YPos(), 0.25, "both", 0, 0);


        //##############################
        //Space Divider
        //##############################
        add_To_Container(mainCentre_JPanel, new JPanel(), 0, get_And_Increase_YPos(), 0.25, "both", 10, 0);


        //###############################
        // South Screen for Interface
        //###############################
        // Creating Submit Button
        JButton submitButton = new JButton("Submit Form");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14)); // setting font
        submitButton.setPreferredSize(new Dimension(50, 50)); // width, height

        // creating commands for submit button to execute on
        submitButton.addActionListener(ae -> submission_Btn_Action());

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
        String title_Upload = "Accept Data Formatting";

        String message_Upload = """
        Are you sure you want to upload these values as they may have been changed / adapted
        to fit our data type format ?""";

        if (! are_You_Sure(title_Upload, message_Upload)) { return; }

        //#################################
        // Ask to Add Ingredient / Products
        //#################################
        if (! are_You_Sure(get_Question_Prompt_Title(), get_Task_Question_Prompt())) { return; }


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
            JOptionPane.showMessageDialog(null, "Failed To Update GUI With Ingredient Info, Reload App to Fix Issues!");
        }
        else
        {
            update_Other_Screens(); // Update Other Screens
        }

        //################################
        // Reset Form & Update GUI
        //################################
        clear_Interface();
    }

    protected abstract String get_Task_Question_Prompt();

    protected abstract String get_Question_Prompt_Title();

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
        clear_Ingredients_Form();
        clear_Shop_Form();
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

    //##################################################################################################################
    // Accessor Methods Methods
    //##################################################################################################################
    public final Frame get_Frame()
    {
        return ingredients_info_screen;
    }

    public String get_Ingredients_Name() throws Exception
    {
        return (String) ingredients_Form.get_Component_Field_Value("name");
    }
}
