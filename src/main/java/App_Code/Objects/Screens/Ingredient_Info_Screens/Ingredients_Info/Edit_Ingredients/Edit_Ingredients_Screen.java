package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Edit_Ingredients;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Gui_Objects.IconPanel;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Add_Ingredients_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Edit_Ingredients.Ingredients_Form.Edit_Ingredients_Form;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Edit_Ingredients.Shop_Form.Edit_Shop_Form;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Ingredients_Info_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Search_For_Food_Info;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;

public class Edit_Ingredients_Screen extends Add_Ingredients_Screen
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private Edit_Ingredients_Form ingredientsForm;
    private Edit_Shop_Form shopForm;
    
    private JComboBox<String>
            ingredientsName_JComboBox = new JComboBox(),
            ingredientsTypes_JComboBox = new JComboBox();
    
    private boolean
            ingredientEditable = true,
            jComboUpdateStatus = false;
    
    private String
            selected_IngredientType = "",
            selected_IngredientName = "";
    
    ///##################################################################################################################
    // Constructor
    ///##################################################################################################################
    public Edit_Ingredients_Screen(Ingredients_Info_Screen parent, MyJDBC db)
    {
        //##########################################################
        // Super Constructor
        //##########################################################
        super(parent, db);
        
        //##########################################################
        // Create GUI
        //##########################################################
        icon_Setup(mainCentre_JPanel); // Icon Setup
        create_GUI_Objects(); // Create Form Objects
        
        //##########################################################
        // Ingredient Type JComboBox Area Setup
        //##########################################################
        
        // Creating JPanel for IngredientsType Label
        JPanel ingredientType_Title_JP = new JPanel();
        ingredientType_Title_JP.setBackground(Color.green);
        add_To_Container(mainCentre_JPanel, ingredientType_Title_JP, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
        
        
        // Creating IngredientTypes Label
        JLabel ingredientType_Label = new JLabel("Select Ingredient Type");
        ingredientType_Label.setFont(new Font("Verdana", Font.PLAIN, 24));
        ingredientType_Label.setHorizontalAlignment(JLabel.CENTER);
        ingredientType_Title_JP.add(ingredientType_Label);
        
        // Update IngredientTypes Map
        update_Map_IngredientsTypes_And_Names(); // Update  JCombos list with data 
        
        //##########################################################
        // Ingredient Name JComboBox Area Setup
        //##########################################################
        
        // Create JPanel for IngredientType JComboBox
        JPanel ingredientType_JC_JP = new JPanel(new GridLayout(1, 1));
        ingredientType_JC_JP.setPreferredSize(new Dimension(650, 50));
        add_To_Container(mainCentre_JPanel, ingredientType_JC_JP, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "horizontal", 10, 0, null);
        
        //  Insert into JComboBox
        ingredientType_JC_JP.add(ingredientsTypes_JComboBox); // Add JComboBox to JPanel
        ((JLabel) ingredientsTypes_JComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER); // centre text
        update_IngredientNames_To_Types_JComboBox(); // add all the ingredientTypes to the IngredientTypes JComboBox
        
        // Add Action Listener For JComboBox
        ingredientsTypes_JComboBox.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent ie)
            {
                if (ie.getStateChange() == ItemEvent.SELECTED)
                {
                    // Get ingredientType
                    selected_IngredientType = (String) ie.getItem();
                    
                    // Exit Clause in case nothing is selected
                    if (selected_IngredientType == null || selected_IngredientType.isEmpty()) { return; }
                    
                    // clear ingredientsNames JCombo
                    refresh_Interface(true, false);
                    
                    update_IngredientName_JComboBox();
                }
            }
        });
        
        //##########################################################
        //  Add Space Divider
        //##########################################################
        add_To_Container(mainCentre_JPanel, new JPanel(), 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 10, 0, null);
        
        
        //##########################################################
        //  IngredientName JComboBox Setup
        //##########################################################
        
        // JPanel Creation For JCombo Title
        JPanel ingredientName_Title_JP = new JPanel();
        ingredientName_Title_JP.setBackground(Color.green);
        
        // Add title JPanel to scrollPanel Panel Area
        add_To_Container(mainCentre_JPanel, ingredientName_Title_JP, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
        
        //###########################
        // IngredientName JLabel
        //###########################
        JLabel ingredientName_JLabel = new JLabel("Select Ingredient To Edit");   // JLabel Creation
        ingredientName_JLabel.setFont(new Font("Verdana", Font.PLAIN, 24)); // Set Font
        ingredientName_JLabel.setHorizontalAlignment(JLabel.CENTER);                  // Text Alignment
        
        ingredientName_Title_JP.add(ingredientName_JLabel);  // Add to JPanel
        
        //###########################
        // IngredientName JComboBox
        //###########################
        
        // JPanel
        JPanel ingredientName_JC_JP = new JPanel(new GridLayout(1, 1));
        ingredientName_JC_JP.setPreferredSize(new Dimension(650, 50));
        add_To_Container(mainCentre_JPanel, ingredientName_JC_JP, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "horizontal", 10, 0, null);
        
        // JCombBOX
        ((JLabel) ingredientsName_JComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER); // centre text
        
        ingredientsName_JComboBox.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent ie)
            {
                if (! (get_Update_Status_Of_Ingredient_Names())) // only trigger event when the ingredientsName Jcombobox is not updating
                {
                    if (ingredientsName_JComboBox.getSelectedIndex() != - 1)
                    {
                        selected_IngredientName = (String) ie.getItem();
                        update_Form_With_IngredientInfo();
                    }
                }
            }
        });
        
        ingredientName_JC_JP.add(ingredientsName_JComboBox);
        
        //##########################################################
        //  Add Space Divider
        //##########################################################
        add_To_Container(mainCentre_JPanel, new JPanel(), 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 10, 0, null);
        
        //##########################################################
        //  Add Other GUI Components
        //##########################################################
        create_Forms(ingredientsForm, shopForm, searchForIngredientInfo);
    }
    
    //##################################################################################################################
    // Setup Methods
    //##################################################################################################################
    
    @Override
    protected void create_GUI_Objects()
    {
        ingredientsForm = new Edit_Ingredients_Form(scroll_JPanel, ingredients_info_screen, this, "Edit Ingredients Info", 250, 50);
        shopForm = new Edit_Shop_Form(scroll_JPanel, ingredients_info_screen, this, "Edit Ingredient Suppliers", 250, 50);
        searchForIngredientInfo = new Search_For_Food_Info(scroll_JPanel, ingredientsForm, "Search For Food Info", 250, 50);
        
        //shopForm.expandJPanel();//HELLO GET THIS TO WORK
        searchForIngredientInfo.collapse_JPanel();
    }
    
    @Override
    protected void icon_Setup(Container container)
    {
        //###########################################
        // Icon Setup
        //###########################################
        
        JPanel iconArea = new JPanel(new GridBagLayout());
        add_To_Container(container, iconArea, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
        
        IconPanel iconPanel = new IconPanel(2, 10, "East");
        JPanel iconPanelInsert = iconPanel.getIconJpanel();
        
        add_To_Container(iconArea, iconPanel.getIconAreaPanel(), 0, 0, 1, 1, 0.25, 0.25, "horizontal", 10, 0, null);
        
        //###########################################
        // DELETE Icon
        //###########################################
        int width = 35;
        int height = 35;
        
        IconButton delete_Icon_Btn = new IconButton("/images/x/x.png", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor
        
        JButton delete_Btn = delete_Icon_Btn.returnJButton();
        delete_Icon_Btn.makeBTntransparent();
        
        delete_Btn.addActionListener(ae -> {
            
            delete_Ingredient_BTN_Action();
        });
        
        iconPanelInsert.add(delete_Icon_Btn);
        
        //###########################################
        // Refresh Icon
        //###########################################
        width = 35;
        height = 40;
        
        IconButton refresh_Icon_Btn = new IconButton("/images/refresh/+++++refresh.png", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor
        
        JButton refresh_Btn = refresh_Icon_Btn.returnJButton();
        refresh_Icon_Btn.makeBTntransparent();
        
        refresh_Btn.addActionListener(ae -> {
            
            refresh_Form_BTN_Action();
        });
        
        iconPanelInsert.add(refresh_Icon_Btn);
    }
    
    //#############################################################################################################
    // Clear Methods
    //#############################################################################################################
    private void refresh_Form_BTN_Action()
    {
        if (ingredientsForm.get_Selected_IngredientName() == null)
        {
            return;
        }
        
        if (are_You_Sure("refresh this page, all data on the form will be reset \nhowever, this will not reset deleted Supplier Shop Info as this is permanently deleted"))
        {
            reload_Form_With_IngredientInfo();
        }
    }
    
    private void reload_Form_With_IngredientInfo()
    {
        //#####################################
        // Update IngredientsForms
        //####################################
        ingredientsForm.load_Ingredients_Form_Data();
        
        //#####################################
        // Update ShopForm
        //####################################
        shopForm.load_ShopForm_Data();
    }
    
    public void refresh_Interface(boolean resetIngredientNameJCombo, boolean resetIngredientTypeJComBox) // only available to reset screen
    {
        //##################################
        // Clear both forms of info
        //##################################
        ingredientsForm.clear_Ingredients_Form();
        shopForm.clear_Shop_Form();
        searchForIngredientInfo.resetFullDisplay();
        searchForIngredientInfo.collapse_JPanel();
        
        //##################################
        // Reset JComboBox's
        //##################################
        if (resetIngredientNameJCombo)
        {
            clear_IngredientName_JCombo();
            set_Ingredient_Name_JCombo_To_Nothing_Selected();
        }
        if (resetIngredientTypeJComBox)
        {
            set_Ingredient_Type_JCombo_To_Nothing_Selected();
        }
    }
    
    private void clear_IngredientName_JCombo()
    {
        ingredientsName_JComboBox.removeAllItems();
    }
    
    //##################################################################################################################
    // Update Methods
    //##################################################################################################################
    private void update_Form_With_IngredientInfo()
    {
        //#####################################
        // Update IngredientsForms
        //####################################
        ingredientsForm.update_Ingredients_Info_From_DB();
        
        //#####################################
        // Update ShopForm
        //####################################
        shopForm.update_ShopForm_With_Info_From_DB();
    }
    
    public void update_IngredientsType_JComboBox()
    {
        ingredientsTypes_JComboBox.removeAllItems(); // clearList
        
        TreeMap<String, Collection<String>> mapIngredientTypesToNames = ingredients_info_screen.get_Map_IngredientTypes_To_Names();
        for (String key : mapIngredientTypesToNames.keySet())
        {
            if (! key.equals("None Of The Above"))
            {
                ingredientsTypes_JComboBox.addItem(key);
            }
        }
    }
    
    @Override
    public void update_IngredientForm_Type_JComboBox()
    {
        ingredientsForm.load_Ingredients_Type_JComboBox();
    }
    
    public void update_Map_IngredientsTypes_And_Names() // HELLO REFACTOR INTO PARENT
    {
        //###########################################################
        // Clear List
        //###########################################################
        TreeMap<String, Collection<String>> map_ingredientTypesToIngredientNames = ingredients_info_screen.get_Map_IngredientTypes_To_Names();
        map_ingredientTypesToIngredientNames.clear();
        
        //###########################################################
        // Store ingredientTypes ID's & IngredientTypeName that occur
        //###########################################################
        String queryIngredientsType = String.format("""
                SELECT I.ingredient_type_id, N.ingredient_type_name
                FROM
                (
                  SELECT DISTINCT(ingredient_type_id) FROM ingredients_info
                ) AS I
                INNER JOIN
                (
                  SELECT ingredient_type_id, ingredient_type_name FROM ingredient_types
                ) AS N
                ON I.ingredient_type_id = N.ingredient_type_id
                ORDER BY N.ingredient_type_name;""");
        
        ArrayList<ArrayList<String>> ingredientTypesNameAndIDResults = db.getMultiColumnQuery(queryIngredientsType);
        
        if (ingredientTypesNameAndIDResults == null)
        {
            JOptionPane.showMessageDialog(null, "\n\nUnable to update Ingredient Type Info");
            return;
        }
        
        //######################################
        // Store all ingredient types & names
        //######################################
        String errorTxt = "";
        int listSize = ingredientTypesNameAndIDResults.size();
        for (int i = 0; i < listSize; i++)
        {
            ArrayList<String> row = ingredientTypesNameAndIDResults.get(i);
            String ID = row.get(0);
            String ingredientType = row.get(1);
            
            //########################################
            // Get IngredientNames for Type
            //########################################
            String queryTypeIngredientNames = String.format("SELECT ingredient_name FROM ingredients_info WHERE ingredient_type_id = %s ORDER BY ingredient_name;", ID);
            Collection<String> ingredientNames = db.getSingleColumnQuery_AlphabeticallyOrderedTreeSet(queryTypeIngredientNames);
            
            if (ingredientNames == null)
            {
                errorTxt += String.format("\nUnable to grab ingredient names for Type '%s'!", ingredientType);
                continue;
            }
            
            //########################################
            // Mapping Ingredient Type to Names
            //########################################
            map_ingredientTypesToIngredientNames.put(ingredientType, ingredientNames);
            
            //System.out.printf("\n\nType %s\n%s",ingredientType, ingredientNames);
        }
        
        if (errorTxt.length() > 0)
        {
            JOptionPane.showMessageDialog(null, String.format("Had Errors Trying to map ingredientTypes to IngredientNames: \n\n%s", errorTxt));
        }
    }
    
    private void update_IngredientName_JComboBox()
    {
        //##################################
        // Populating IngredientName JComBox
        //##################################
        set_Update_Status_Of_Ingredient_Names(true); // stops ingredientName JComboBox from triggering any  actionListener events
        
        TreeMap<String, Collection<String>> map_ingredientTypesToIngredientNames = ingredients_info_screen.get_Map_IngredientTypes_To_Names();
        for (String item : map_ingredientTypesToIngredientNames.get(selected_IngredientType))
        {
            if (! item.equals("None Of The Above"))
            {
                ingredientsName_JComboBox.addItem(item);
            }
        }
        
        set_Ingredient_Name_JCombo_To_Nothing_Selected();// set selected item to nothing
        set_Update_Status_Of_Ingredient_Names(false);
    }
    
    public void update_IngredientNames_To_Types_JComboBox()
    {
        ingredientsTypes_JComboBox.removeAllItems();
        TreeMap<String, Collection<String>> map_ingredientTypesToIngredientNames = ingredients_info_screen.get_Map_IngredientTypes_To_Names();
        
        for (String key : map_ingredientTypesToIngredientNames.keySet())
        {
            if (! key.equals("None Of The Above"))
            {
                ingredientsTypes_JComboBox.addItem(key);
            }
        }
        
        set_Ingredient_Type_JCombo_To_Nothing_Selected(); // set selected item to nothing
    }
    
    //##################################################################################################################
    // Submission Actions
    //##################################################################################################################
    @Override
    protected void submission_Btn_Action()
    {
        //##############################################################################################################
        //
        //##############################################################################################################
        if (ingredientsName_JComboBox.getSelectedItem() == null)
        {
            JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "Please select an ingredient please to edit it!");
            refresh_Interface(true, true);
            return;
        }
        
        if (ingredientsName_JComboBox.getSelectedItem().equals("N/A"))
        {
            JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "The Store N/A cannot be edited, its a placeholder");
            refresh_Interface(true, true);
            return;
        }
        
        //##############################################################################################################
        //
        //##############################################################################################################
        if (! (are_You_Sure("update this Ingredients information - this will cause the mealPlan to save its data to the DB")))
        {
            return;
        }
        
        //##############################################################################################################
        //
        //##############################################################################################################
        if (! (ingredientsForm.validate_Ingredients_Form()) || ! (shopForm.validate_Form()))
        {
            return;
        }
        
        //##############################################################################################################
        //
        //##############################################################################################################
        String selectedIngredientID = ingredientsForm.get_Selected_IngredientID();
        
        if (selectedIngredientID == null)
        {
            JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "\n\nUnable To Get Ingredient ID To Edit This Ingredient !!");
            return;
        }
        
        //###############################################################
        // Update Database Only if Data on form has changed
        //###############################################################
        String ingredientsFormUpdateString = ingredientsForm.get_Ingredients_Form_Update_String(selectedIngredientID);
        String[] shopFormUpdateString = shopForm.get_ShopForm_Update_String(selectedIngredientID);
        
        if (ingredientsFormUpdateString == null && shopFormUpdateString == null)
        {
            int reply = JOptionPane.showConfirmDialog(mealPlanScreen.getFrame(),
                    String.format("\n\nNo modifications / changes were made to the ingredient '%s'. \nWould you like to clear the form?", ingredientsName_JComboBox.getSelectedItem()),
                    "", JOptionPane.YES_NO_OPTION); //HELLO Edit
            
            if (reply == JOptionPane.NO_OPTION || reply == JOptionPane.CLOSED_OPTION)
            {
                return;
            }
            if (reply == JOptionPane.YES_OPTION)
            {
                refresh_Interface(true, true);
                return;
            }
        }
        
        //###############################################################
        // Get Update Strings & Update for both forms using ingredientID
        //###############################################################
        //System.out.printf("\n\nsubmissionBtnAction() \ninfo Update: \n%s \n\nshop Update: \n%s", ingredientsFormUpdateString, Arrays.toString(shopFormUpdateString));
        
        if (! (update_Both_Forms(ingredientsFormUpdateString, shopFormUpdateString)))
        {
            JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "\n\nUnable To Update Ingredient Info !!");
            return;
        }
        
        //##############################################################################################################
        // Write Ingredients Value To File
        //##############################################################################################################
        if (! (backup_Data_In_SQL_File()))
        {
            JOptionPane.showMessageDialog(null, "Error, replacing ingredients info to SQL file!");
        }
        
        //##############################################################################################################
        //
        //##############################################################################################################
        // Check if ingredientsName or IngredientType changed
        String currentIngredientName = ingredientsForm.get_Ingredient_Name_Form_Value();
        String currentIngredientType = ingredientsForm.get_Ingredient_Type_Form_Value();
        
        String previousIngredientName = selected_IngredientName;
        String previousIngredientType = selected_IngredientType;
        
        //HELLO REMOVE
        System.out.printf("\n\nIngredientName \nCurrent = '%s' \nPrevious = '%s' \n\nIngredientType \nCurrent = '%s' \nPrevious = '%s'",
                currentIngredientName, previousIngredientName, previousIngredientType, currentIngredientType);
        
        if ((! currentIngredientName.equals(previousIngredientName) || (! currentIngredientType.equals(previousIngredientType))))
        {
            ingredients_info_screen.set_Update_IngredientInfo(true);
            
            // if there is an error trying to add or remove ingredientType throw an error
            if (! add_Or_Delete_Ingredient_From_Map("delete", previousIngredientType, previousIngredientName) // remove old info ingredient
                    || ! add_Or_Delete_Ingredient_From_Map("add", currentIngredientType, currentIngredientName))// add new info ingredient
            {
                JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "\n\nUnable to perform local update for ingredient Info");
                return;
            }
        }
        
        //##############################################################################################################
        //
        //##############################################################################################################
        JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "The ingredient updates won't appear on the mealPlan screen until this window is closed!");
        refresh_Interface(true, true);
        super.resize_GUI();
    }
    
    //EDITING NOW
    @Override
    protected boolean update_Both_Forms(String updateIngredients_String, String[] updateIngredientShops_String)
    {
        //####################################
        // Nothing to Update
        //####################################
        if (updateIngredients_String == null && updateIngredientShops_String == null)
        {
            System.out.println("\n\nupdateBothForms() no ingredientInfo / shopInfo to Update");
            return false;
        }
        
        //####################################
        // Uploading Ingredient Info Query
        //####################################
        if (updateIngredients_String != null && ! (db.uploadData_Batch_Altogether(new String[]{ updateIngredients_String })))
        {
            JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "Failed Upload - Unable To Add Ingredient Info & Shop Info & Ingredient Suppliers In DB!");
            return false;
        }
        
        //####################################
        // Uploading Shop Info
        //####################################
        boolean errorUploading = false;
        
        if (updateIngredientShops_String != null)
        {
            int noOfUpdateProcesses = updateIngredientShops_String.length;
            
            for (int x = 0; x < noOfUpdateProcesses; x++)
            {
                if (! (db.uploadData_Batch_Altogether(new String[]{ updateIngredientShops_String[x] })))
                {
                    JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), String.format("Failed %s/%s Updates - Unable To Add Ingredient Supplier!",
                            x + 1, noOfUpdateProcesses));
                    
                    errorUploading = true;
                }
            }
            
            if (errorUploading) // if there were errors, flag it (return false)
            {
                return false;
            }
            
            JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), String.format("\n\nUpdated Ingredient Info! \n\nAlso updated %s/%s -  Suppliers For Ingredient Updated In DB!!!",
                    noOfUpdateProcesses, noOfUpdateProcesses));
        }
        
        return true;  // No Errors found
    }
    
    @Override
    protected boolean backup_Data_In_SQL_File() //HELLO Iteration has next can predict last loop
    {
        String replacementData = "(null,";
        for (Map.Entry<String, Object[]> entry : ingredientsForm.get_Ingredients_Form_Object_And_Values().entrySet())
        {
            String rowLabel = entry.getKey();
            String formValue = (String) entry.getValue()[1];
            
            if (rowLabel.equals("Ingredient Measurement In") || rowLabel.equals("Ingredient Name"))
            {
                replacementData += String.format("(\"%s\"),", formValue);
                continue;
            }
            else if (rowLabel.equals("Ingredient Type"))
            {
                replacementData += String.format("(SELECT ingredient_type_id FROM ingredientTypes WHERE ingredient_type_name = \"%s\"),", formValue);
                continue;
            }
            replacementData += String.format("(%s),", formValue);
        }
        
        replacementData = replacementData.substring(0, replacementData.length() - 1) + ")";
        
        System.out.printf("\n\nbackupDataInSQLFile() Text to Replace \n%s", replacementData);
        
        //##############################################################################################################
        //
        //##############################################################################################################
        String oldIngredientName = String.format("(\"%s\")", get_Selected_Ingredient_Name().trim());
        if (! (db.replaceTxtInSQLFile(sqlBackUpPath, false, oldIngredientName, replacementData)))
        {
            JOptionPane.showMessageDialog(null, String.format("Error, changing back-up of %s in SQL file of ingredient info!", oldIngredientName));
            return false;
        }
        
        return true;
    }
    
    //##################################################################################################################
    // Delete BTN Actions
    //##################################################################################################################
    private void delete_Ingredient_BTN_Action()//HELLO PROGRAM DELETE BTN
    {
        //##############################################################################################################
        //
        //##############################################################################################################
        if (ingredientsName_JComboBox.getSelectedIndex() == - 1)
        {
            JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "Please select an item first before attempting to delete an ingredient!");
            return;
        }
        
        //##############################################################################################################
        //
        //##############################################################################################################
        String selectedIngredientID = ingredientsForm.get_Selected_IngredientID();
        String selectedIngredientName = ingredientsForm.get_Selected_IngredientName();
        
        //HELLO should be removed as N/A is never in the JComboBox
        if (selectedIngredientName.equals("N/A"))
        {
            JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "This item cannot be deleted from the list (its a placeholder) !");
            refresh_Interface(true, true);
            return;
        }
        
        //##############################################################################################################
        //
        //##############################################################################################################
        if (! (are_You_Sure(String.format("delete ingredient named '%s' from the database", selectedIngredientName))))
        {
            return;
        }
        
        //##############################################################################################################
        //
        //##############################################################################################################
        if (selectedIngredientID == null || selectedIngredientName == null)
        {
            JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "Unable to grab Ingredient INFO to delete it!!");
            return;
        }
        
        //##############################################################################################################
        //
        //##############################################################################################################
        String query0 = String.format("DELETE FROM `ingredients_in_sections_of_meal` WHERE Ingredient_id = %s;", selectedIngredientID);
        String query1 = String.format("DELETE FROM `ingredient_in_shops` WHERE ingredient_id  = %s;", selectedIngredientID);
        String query2 = String.format("DELETE FROM `ingredients_info` WHERE ingredient_id  = %s;", selectedIngredientID);
        
        if (db.uploadData_Batch_Altogether(new String[]{ query0, query1, query2 }))
        {
            JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), String.format("Successfully Deleted '%s' From DB!", selectedIngredientName));
            add_Or_Delete_Ingredient_From_Map("delete", selected_IngredientType, selectedIngredientName); // delete ingredient
            refresh_Interface(true, true);
            ingredients_info_screen.set_Update_IngredientInfo(true);
        }
        else
        {
            JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "Unable to delete item From DB!");
            return;
        }
        
        //##############################################################################################################
        // Delete From BackUp SQL File
        //##############################################################################################################
        
        if (! (db.deleteTxtInFile(super.sqlBackUpPath, String.format("(\"%s\"),", selectedIngredientName))))
        {
            JOptionPane.showMessageDialog(null, String.format("\n\nError, deleteBTNAction() deleting ingredient '%s' from backup files!", selectedIngredientName));
        }
    }
    
    //##################################################################################################################
    // Mutator Methods
    //##################################################################################################################
    /*
     When the variable it's used to set is true the ingredientNames JComboBox is updating
     this stops the ingredientNames JComboBox from triggering actionListener events!
    */
    private void set_Update_Status_Of_Ingredient_Names(boolean x) // can't be deleted, trust me
    {
        jComboUpdateStatus = x;
    }
    
    /*
      Resets Ingredient Type JComboBox
    */
    private void set_Ingredient_Type_JCombo_To_Nothing_Selected()
    {
        ingredientsTypes_JComboBox.setSelectedIndex(- 1);
    }
    
    /*
        Resets Ingredient Type JComboBox
    */
    private void set_Ingredient_Name_JCombo_To_Nothing_Selected()
    {
        ingredientsName_JComboBox.setSelectedIndex(- 1);
    }
    
    //##################################################################################################################
    // Get Methods
    //##################################################################################################################
    public boolean get_Update_Status_Of_Ingredient_Names()// can't be deleted, trust me
    {
        return jComboUpdateStatus;
    }
    
    public String get_Selected_Ingredient_Name()
    {
        return selected_IngredientName;
    }
    
    public Edit_Ingredients_Form get_Ingredients_Form()
    {
        return ingredientsForm;
    }
}
