package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Gui_Objects.Screens.Screen_JPanel;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Ingredient_Form.Add_Ingredients_Form;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Shop_Form.Add_Shop_Form;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Ingredients_Info_Screen;
import App_Code.Objects.Screens.Meal_Plan_Screen;

import javax.swing.*;
import java.awt.*;
import java.text.Collator;
import java.util.*;
import java.util.stream.Stream;


public class Add_Ingredients_Screen extends Screen_JPanel
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    
    // Integer
    protected int yPos = 0, planID, tempPlanID;
    
    // String
    protected String
            planName,
            sqlBackUpPath = "src/main/java/Documentation_And_Scripts/Database/Scripts/Editable_DB_Scripts/6.) Ingredients_Info.sql";
    
    // JPanel
    protected JPanel scroll_JPanel, mainCentre_JPanel;
    
    // Objects
    protected GridBagConstraints gbc = new GridBagConstraints();
    protected Meal_Plan_Screen mealPlanScreen;
    protected MyJDBC db;
    
    // Screen Objects
    protected Add_Ingredients_Form add_Ingredients_Form;
    private Add_Shop_Form add_Shop_Form;
    protected Search_For_Food_Info searchForIngredientInfo;
    protected Ingredients_Info_Screen ingredients_info_screen;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Add_Ingredients_Screen(Ingredients_Info_Screen ingredients_info_screen, MyJDBC db)
    {
        //##########################################################
        // Super Constructor
        //##########################################################
        super(null, true, 800, 850);
        
        //##########################################################
        // Variables
        //##########################################################
        // Objects
        this.db = db;
        this.ingredients_info_screen = ingredients_info_screen;
        mealPlanScreen = ingredients_info_screen.get_MealPlan_Screen();
        
        // Integer
        planID = ingredients_info_screen.get_PlanID();
        tempPlanID = ingredients_info_screen.get_TempPlanID();
        
        // String
        planName = ingredients_info_screen.get_PlanName();
        
        //##########################################################
        //   Create Screen for Interface
        ///#########################################################
        scroll_JPanel = get_ScrollPane_JPanel();
        scroll_JPanel.setLayout(new BorderLayout());
        
        // Main Centre JPanel
        mainCentre_JPanel = new JPanel(new GridBagLayout());
        scroll_JPanel.add(mainCentre_JPanel, BorderLayout.CENTER);
        
        //##########################################################
        //   Create GUI Objects
        ///#########################################################
        create_GUI_Objects();
    }
    
    protected void create_GUI_Objects()
    {
        add_Ingredients_Form = new Add_Ingredients_Form(scroll_JPanel, ingredients_info_screen, "Add Ingredients Info", 250, 50);
        
        add_Shop_Form = new Add_Shop_Form(scroll_JPanel, ingredients_info_screen, this, "Add Ingredient Suppliers", 250, 50);
        
        searchForIngredientInfo = new Search_For_Food_Info(scroll_JPanel, add_Ingredients_Form, "Search For Food Info", 250, 50);
        
        create_Forms(add_Ingredients_Form, add_Shop_Form, searchForIngredientInfo);
    }
    
    //####################################################
    // Clearing GUI Methods
    //####################################################
    protected void create_Forms(Add_Ingredients_Form addIngredientsForm, Add_Shop_Form addShopForm, Search_For_Food_Info searchForFoodInfo)
    {
        //##################################################################################
        // Creating Parts of screen & adding it to interface
        //##################################################################################
        
        //###########################
        //Search For Ingredients form
        //###########################
        
        add_To_Container(mainCentre_JPanel, searchForFoodInfo, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);
        
        //###########################
        //Space Divider
        //###########################
        add_To_Container(mainCentre_JPanel, new JPanel(), 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 10, 0, null);
        
        //###########################
        //Ingredients form
        //###########################
        
        add_To_Container(mainCentre_JPanel, addIngredientsForm, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);
        
        //###########################
        //Space Divider
        //###########################
        add_To_Container(mainCentre_JPanel, new JPanel(), 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 10, 0, null);
        
        //###########################
        // Add shop
        //###########################
        add_To_Container(mainCentre_JPanel, addShopForm, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);
        
        
        //###########################
        //Space Divider
        //###########################
        add_To_Container(mainCentre_JPanel, new JPanel(), 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 10, 0, null);
        
        //###################################################################################
        // South Screen for Interface
        //###################################################################################
        
        // Creating submit button
        JButton submitButton = new JButton("Submit Form");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14)); // setting font
        submitButton.setPreferredSize(new Dimension(50, 50)); // width, height
        
        // creating commands for submit button to execute on
        submitButton.addActionListener(ae -> {
            submission_Btn_Action();
        });
        
        get_Main_South_JPanel().setLayout(new GridLayout(1, 1));
        get_Main_South_JPanel().add(submitButton, BorderLayout.SOUTH);
        
        //###################################################################################
        // Resizing GUI
        //###################################################################################
        resize_GUI();
    }
    
    //####################################################
    // Submission Button Actions
    //####################################################
    protected void submission_Btn_Action()
    {
        //###############################
        //
        //###############################
        if (! are_You_Sure("add this new Ingredient - this will cause the mealPlan to save its data to the DB"))
        {
            return;
        }
        
        //###############################
        // IngredientsForm
        //###############################
        if (! (add_Ingredients_Form.validate_Ingredients_Form()))
        {
            return;
        }
        
        //###############################
        // ShopForm
        //###############################
        if (! (add_Shop_Form.validate_Form()))
        {
            return;
        }
        
        //###############################
        // Data Formatting
        //###############################
        if (! are_You_Sure("upload these values as they may have been changed / adapted to fit our data type format"))
        {
            return;
        }
        
        //###############################
        // Update Both Forms
        //###############################
        if (! update_Both_Forms(add_Ingredients_Form.get_Ingredients_Form_Update_String(null), add_Shop_Form.get_ShopForm_Update_String()))
        {
            return;
        }
        
        ingredients_info_screen.set_Update_IngredientInfo(true);
        JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "\n\nUpdated Ingredient Info! \n\nAlso updated 2/2 Shop Info In DB In DB!!!");
        JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "The ingredient updates won't appear on the mealPlan screen until this window is closed!");
        
        //#####################################
        // Reset Ingredient Names/Types
        //####################################
        String ingredientName = add_Ingredients_Form.get_Ingredient_Name_Form_Value();
        String ingredientType = add_Ingredients_Form.get_Ingredient_Type_Form_Value();
        
        System.out.printf("\n\nsubmissionBtnAction() \n%s %s", ingredientName, ingredientType);
        
        add_Or_Delete_Ingredient_From_Map("add", ingredientType, ingredientName);
        
        //#####################################
        // Write Ingredients Value To File
        //####################################
        if (! (backup_Data_In_SQL_File()))
        {
            JOptionPane.showMessageDialog(null, "Error, backing up new ingredients info to SQL file!");
        }
        
        //#####################################
        // Reset Form & Update GUI
        //####################################
        refresh_Interface();
        resize_GUI();
    }
    
    protected Boolean are_You_Sure(String process)
    {
        int reply = JOptionPane.showConfirmDialog(mealPlanScreen.getFrame(), String.format("Are you sure you want to: %s?", process, process),
                "Confirmation", JOptionPane.YES_NO_OPTION); //HELLO Edit
        
        if (reply == JOptionPane.NO_OPTION || reply == JOptionPane.CLOSED_OPTION) { return false; }
        
        return true;
    }
    
    protected boolean update_Both_Forms(String ingredients_Update, String[] shops_Update)
    {
        System.out.printf("\n\nupdateBothForms() \nIngredientValues: \n%s \n\nShopping Info: \n%s", ingredients_Update, Arrays.toString(shops_Update));
        
        //####################################
        // Empty
        //####################################
        if (ingredients_Update == null && shops_Update == null) //HELLO Is this already accounted for by the time the code gets here
        {
            return false;
        }
        
        //####################################
        // Execute Updates
        //####################################
        if (ingredients_Update != null && shops_Update != null) // Both
        {
            // Combine Updates into 1
            String[] combined = Stream.concat(Arrays.stream(new String[]{ ingredients_Update }), Arrays.stream(shops_Update)).toArray(String[] :: new);
            
            // Execute Upload
            String errorMSG = "Failed 2/2 Updates - Unable To Add Ingredient / Shop Info In DB!";
            return  db.upload_Data_Batch_Independently(combined, errorMSG);
        }
        else if (ingredients_Update != null) // Ingredient Update
        {
            String errorMSG = "Failed 1/1 Updates - Updating Ingredient Info !";
            return db.upload_Data(ingredients_Update, errorMSG);
        }
        else // Shop Info
        {
            String errorMSG = "Failed 1/1 Updates - Store Info !";
            return db.upload_Data_Batch_Altogether(shops_Update,  errorMSG);
        }
    }
    
    protected boolean add_Or_Delete_Ingredient_From_Map(String process, String ingredientType, String ingredientName)
    {
        // Storing
        TreeSet<String> ingredientTypeList = ingredients_info_screen.get_Map_IngredientTypes_To_Names().get(ingredientType);
        if (process.equals("add"))// if key exists add the ingredientName in
        {
            if (ingredientTypeList != null)
            {
                // Add ingredientName to collection
                ingredientTypeList.add(ingredientName);
            }
            else // create the list for the new type and add ingredient in
            {
                ingredientTypeList = new TreeSet<>(Collator.getInstance());
                ingredientTypeList.add(ingredientName);
                ingredients_info_screen.get_Map_IngredientTypes_To_Names().put(ingredientType, ingredientTypeList);
            }
        }
        else if (process.equals("delete"))
        {
            ingredientTypeList.remove(ingredientName);
            
            // Remove List as there is no items in it
            if (ingredientTypeList.isEmpty())
            {
                ingredients_info_screen.get_Map_IngredientTypes_To_Names().remove(ingredientType);
            }
        }
        
        ingredients_info_screen.update_EditIngredientsInfo_IngredientsTypes();
        return true;
    }
    
    protected boolean backup_Data_In_SQL_File()
    {
        String ingredientsValuesBeingAdded = add_Ingredients_Form.get_Ingredients_Values_Being_Added();
        String errorMSG = String.format("Unable to add ingredient %s", ingredientsValuesBeingAdded);
        
        return db.write_Txt_To_SQL_File(sqlBackUpPath, ingredientsValuesBeingAdded, errorMSG);
    }
    
    //####################################################
    // Clearing GUI Methods
    //####################################################
    private void refresh_Interface() // only available to reset screen
    {
        clear_Search_For_Ingredient_Info_Form();
        clear_IngredientsForm();
        clear_ShopForm();
    }
    
    protected void clear_Search_For_Ingredient_Info_Form()
    {
        searchForIngredientInfo.resetFullDisplay();
    }
    
    protected void clear_IngredientsForm()
    {
        add_Ingredients_Form.clear_Ingredients_Form();
    }
    
    public void clear_ShopForm()
    {
        add_Shop_Form.clear_Shop_Form();
    }
    
    //####################################################
    // Update Methods
    //####################################################
    public void update_IngredientForm_Type_JComboBox()
    {
        add_Ingredients_Form.load_Ingredients_Type_JComboBox();
    }
}



    

