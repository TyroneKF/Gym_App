package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Gui_Objects.Screens.Screen_JPanel;
import App_Code.Objects.Screens.Ingredient_Info_Screens.SearchForFoodInfo;
import App_Code.Objects.Screens.Meal_Plan_Screen;

import javax.swing.*;
import java.awt.*;
import java.text.Collator;
import java.util.*;


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
    protected Add_IngredientsForm addIngredientsForm;
    private Add_ShopForm addShopForm;
    protected SearchForFoodInfo searchForIngredientInfo;
    protected Ingredients_Info_Screen ingredients_info_screen;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    Add_Ingredients_Screen(Ingredients_Info_Screen ingredients_info_screen, MyJDBC db)
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
        addIngredientsForm = new Add_IngredientsForm(scroll_JPanel, ingredients_info_screen, "Add Ingredients Info", 250, 50);
    
        addShopForm = new Add_ShopForm(scroll_JPanel, ingredients_info_screen, this, "Add Ingredient Suppliers", 250, 50);
    
        searchForIngredientInfo = new SearchForFoodInfo(scroll_JPanel, addIngredientsForm, "Search For Food Info", 250, 50);
    
        createForms(addIngredientsForm, addShopForm, searchForIngredientInfo);
    }
    
    //####################################################
    // Clearing GUI Methods
    //####################################################
    protected void createForms(Add_IngredientsForm addIngredientsForm, Add_ShopForm addShopForm, SearchForFoodInfo searchForFoodInfo)
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
        add_To_Container(mainCentre_JPanel, addShopForm, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null); ;
        
        //###########################
        //Space Divider
        //###########################
        add_To_Container(mainCentre_JPanel, new JPanel(), 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 10, 0, null); ;
        
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
    
        get_Main_South_JPanel().setLayout(new GridLayout(1,1));
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
        if (! areYouSure("add this new Ingredient - this will cause the mealPlan to save its data to the DB"))
        {
            return;
        }
        
        //###############################
        // IngredientsForm
        //###############################
        if (! (addIngredientsForm.validate_IngredientsForm()))
        {
            return;
        }
        
        //###############################
        // ShopForm
        //###############################
        if (! (addShopForm.validateForm()))
        {
            return;
        }
        
        //###############################
        // Data Formatting
        //###############################
        if (! areYouSure("upload these values as they may have been changed / adapted to fit our data type format"))
        {
            return;
        }
        
        //###############################
        // ShopForm
        //###############################
        if (update_Both_Forms(addIngredientsForm.get_IngredientsForm_UpdateString(null), addShopForm.get_ShopForm_UpdateString()))
        {
            ingredients_info_screen.setUpdateIngredientInfo(true);
            JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "The ingredient updates won't appear on the mealPlan screen until this window is closed!");
            
            //#####################################
            // Reset Ingredient Names/Types
            //####################################
            String ingredientName = addIngredientsForm.getIngredientNameFormValue();
            String ingredientType = addIngredientsForm.getIngredientTypeFormValue();
            
            System.out.printf("\n\nsubmissionBtnAction() \n%s %s", ingredientName, ingredientType);
            
            addOrDeleteIngredientFromMap("add", ingredientType, ingredientName);
            
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
            refreshInterface();
            resize_GUI();
        }
        
    }
    
    protected Boolean areYouSure(String process)
    {
        int reply = JOptionPane.showConfirmDialog(mealPlanScreen.getFrame(), String.format("Are you sure you want to: %s?", process, process),
                "Confirmation", JOptionPane.YES_NO_OPTION); //HELLO Edit
        
        if (reply == JOptionPane.NO_OPTION || reply == JOptionPane.CLOSED_OPTION)
        {
            return false;
        }
        return true;
    }
    
    protected boolean update_Both_Forms(String updateIngredients_String, String[] updateIngredientShops_String)
    {
        System.out.printf("\n\nupdateBothForms() \nIngredientValues: \n%s \n\nShopping Info: \n%s", updateIngredients_String, Arrays.toString(updateIngredientShops_String));
        
        //####################################
        // Uploading Query
        //####################################
        if (! (db.uploadData_Batch_Altogether(new String[]{ updateIngredients_String })))
        {
            JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "Failed 2/2 Updates - Unable To Add Ingredient Info In DB!");
            return false;
        }
        
        if (updateIngredientShops_String != null)
        {
            if (! (db.uploadData_Batch_Independently(updateIngredientShops_String)))
            {
                JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "Failed 1/2 Updates - Unable To Add Shop Supplier For Ingredient In DB!");
                return false;
            }
        }
        
        JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "\n\nUpdated Ingredient Info! \n\nAlso updated 2/2 Shop Info In DB In DB!!!");
        
        return true;
    }
    
    protected boolean addOrDeleteIngredientFromMap(String process, String ingredientType, String ingredientName)
    {
        // Storing
        Collection<String> ingredientTypeList = ingredients_info_screen.get_Map_IngredientTypes_To_Names().get(ingredientType);
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
            if (ingredientTypeList.size() == 0)
            {
                ingredients_info_screen.get_Map_IngredientTypes_To_Names().remove(ingredientType);
            }
        }
        
        ingredients_info_screen.update_EditIngredientsInfo_IngredientsTypes();
        return true;
    }
    
    protected boolean backup_Data_In_SQL_File()
    {
        String ingredientsValuesBeingAdded = addIngredientsForm.getIngredientsValuesBeingAdded();
        System.out.printf("\n\nbackupDataInSQLFile() \n%s", ingredientsValuesBeingAdded);
        if (! (db.writeTxtToSQLFile(sqlBackUpPath, ingredientsValuesBeingAdded)))
        {
            return false;
        }
        return true;
    }
    
    //####################################################
    // Clearing GUI Methods
    //####################################################
    private void refreshInterface() // only available to reset screen
    {
        clearSearchForIngredientInfoForm();
        clearIngredientsForm();
        clearShopForm();
    }
    
    protected void clearSearchForIngredientInfoForm()
    {
        searchForIngredientInfo.resetFullDisplay();
    }
    
    protected void clearIngredientsForm()
    {
        addIngredientsForm.clearIngredientsForm();
    }
    
    protected void clearShopForm()
    {
        addShopForm.clearShopForm();
    }
    
    //####################################################
    // Update Methods
    //####################################################
    protected void update_IngredientForm_Type_JComboBox()
    {
        addIngredientsForm.loadIngredientsTypeJComboBox();
    }
}



    

