package App_Code.Objects.Screens.Ingredient_Info.Edit_Ingredients_Info;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Gui_Objects.IconPanel;
import App_Code.Objects.Gui_Objects.ScrollPaneCreator;
import App_Code.Objects.Screens.Ingredient_Info.SearchForFoodInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;

public class Edit_IngredientsScreen extends Add_Ingredients_Screen
{
    //#######################################################
    // Variables
    //#######################################################
    private Edit_IngredientsForm ingredientsForm;
    private Edit_ShopForm shopForm;

    private JComboBox<String>
            ingredientsNameJComboBox = new JComboBox(),
            ingredientsTypesJComboBox = new JComboBox();

    private boolean
            ingredientEditable = true,
            jComboUpdateStatus = false;

    private String
            selected_IngredientType = "",
            selected_IngredientName = "";


    //#############################################################################################################
    // Constructor
    //#############################################################################################################
    Edit_IngredientsScreen(Ingredients_Info_Screen parent, MyJDBC db)
    {
        super(parent, db);

        //###################################################################################
        //   Parent Stuff
        //###################################################################################


        //###################################################################################
        //   Parent Stuff
        //###################################################################################
        this.planID = parent.getPlanID();
        this.tempPlanID = parent.getTempPlanID();
        this.planName = parent.getPlanName();

        this.mealPlanScreen = parent.getMealPlanScreen();

        //###################################################################################
        //   Create Screen for Interface
        //###################################################################################

        setLayout(new BorderLayout());

        //###################################################################################
        //   Create Main Centre Screen for Interface
        //##################################################################################
        JPanel mainCentreScreen = new JPanel(new GridBagLayout());
        add(mainCentreScreen, BorderLayout.CENTER);

        //##########################################################
        // Create ScrollPane & add to Interface
        //#########################################################
        ScrollPaneCreator scrollPane = new ScrollPaneCreator();
        scrollPaneJPanel = scrollPane.getJPanel();
        scrollPaneJPanel.setLayout(new GridBagLayout());
        addToContainer(mainCentreScreen, scrollPane, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0);

        //#################################
        // Needs to be created near this
        //##################################
        ingredientsForm = new Edit_IngredientsForm(this, ingredients_info_screen, this, "Edit Ingredients Info", 250, 50);
        shopForm = new Edit_ShopForm(scrollPaneJPanel, ingredients_info_screen, this, "Edit Ingredient Suppliers", 250, 50);
        searchForIngredientInfo = new SearchForFoodInfo(scrollPaneJPanel, ingredientsForm, "Search For Food Info", 250, 50);

        //shopForm.expandJPanel();//HELLO GET THIS TO WORK
        searchForIngredientInfo.collapseJPanel();

        //##########################################
        // Icon Setup
        //#########################################
        iconSetup();

        //#########################################################################################################
        // Ingredient Type & Name JcomboBox's
        //#########################################################################################################

        // Update  JCombos list with data
        updateMapIngredientsTypesAndNames();

        //#################################################################
        // Ingredient Type Setup
        //#################################################################

        // JCombo Title
        JLabel titleLabel = new JLabel("Select Ingredient Type");
        titleLabel.setFont(new Font("Verdana", Font.PLAIN, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Color.green);
        titlePanel.add(titleLabel);

        // Add title JPanel to scrollPanel Panel Area
        addToContainer(scrollPaneJPanel, titlePanel, 0, yPos += 1, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

        //###########################################
        //  IngredientTypeJComboBox
        //###########################################
        JPanel jp = new JPanel(new GridLayout(1, 1));

        ((JLabel) ingredientsTypesJComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER); // centre text

        //###########################################
        //  Insert into JCombobox
        //###########################################

        updateIngredientNamesToTypesJComboBox(); // add all the ingredientTypes to the IngredientTypes JComboBox
        //################################################
        //  Actionlistener
        //#################################################

        ingredientsTypesJComboBox.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent ie)
            {
                if (ie.getStateChange()==ItemEvent.SELECTED)
                {
                    // Get ingredientType
                    selected_IngredientType = (String) ie.getItem().toString();

                    // clear ingredientsNames JCombo
                    refreshInterface(true, false);

                    if (selected_IngredientType.equals(""))
                    {
                        return;
                    }

                    updateIngredientNameJComboBox();
                }
            }
        });

        jp.add(ingredientsTypesJComboBox);
        jp.setPreferredSize(new Dimension(650, 50));

        addToContainer(scrollPaneJPanel, jp, 0, yPos += 1, 1, 1, 0.25, 0.25, "horizontal", 10, 0);
        //#################################################################
        //  IngredientName Setup
        //#################################################################

        //###########################
        //Space Divider
        //###########################
        addToContainer(scrollPaneJPanel, new JPanel(), 0, yPos += 1, 1, 1, 0.25, 0.25, "both", 10, 0);

        //###########################
        // JCombo Title
        //###########################
        titleLabel = new JLabel("Select Ingredient To Edit");
        titleLabel.setFont(new Font("Verdana", Font.PLAIN, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        titlePanel = new JPanel();
        titlePanel.setBackground(Color.green);
        titlePanel.add(titleLabel);

        // Add title JPanel to scrollPanel Panel Area
        addToContainer(scrollPaneJPanel, titlePanel, 0, yPos += 1, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

        //###########################
        // IngredientName JComboBox
        //###########################

        jp = new JPanel(new GridLayout(1, 1));

        ((JLabel) ingredientsNameJComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER); // centre text

        ingredientsNameJComboBox.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent ie)
            {
                if (!(getUpdateStatusOfIngredientNames())) // only trigger event when the ingredientsName Jcombobox is not updating
                {
                    if (ingredientsNameJComboBox.getSelectedIndex()!=-1)
                    {
                        selected_IngredientName = (String) ie.getItem().toString();
                        updateFormWithIngredientInfo();
                    }
                }
            }
        });

        jp.add(ingredientsNameJComboBox);
        jp.setPreferredSize(new Dimension(650, 50));

        addToContainer(scrollPaneJPanel, jp, 0, yPos += 1, 1, 1, 0.25, 0.25, "horizontal", 10, 0);

        //###########################
        //Space Divider
        //###########################
        addToContainer(scrollPaneJPanel, new JPanel(), 0, yPos += 1, 1, 1, 0.25, 0.25, "both", 10, 0);

        createForms(ingredientsForm, shopForm, searchForIngredientInfo);
    }

    //#############################################################################################################
    // Setup Methods
    //#############################################################################################################
    private void iconSetup()
    {
        //###########################################
        // Icon Setup
        //###########################################

        JPanel iconArea = new JPanel(new GridBagLayout());
        addToContainer(scrollPaneJPanel, iconArea, 0, yPos += 1, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

        IconPanel iconPanel = new IconPanel(2, 10, "East");
        JPanel iconPanelInsert = iconPanel.getIconJpanel();

        addToContainer(iconArea, iconPanel.getIconAreaPanel(), 0, 0, 1, 1, 0.25, 0.25, "horizontal", 10, 0);

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

            deleteIngredientBTNAction();
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

            refreshFormBTNAction();
        });

        iconPanelInsert.add(refresh_Icon_Btn);
    }

    //#############################################################################################################
    // Clear Methods
    //#############################################################################################################
    private void refreshFormBTNAction()
    {
        if (ingredientsForm.getSelectedIngredientName()==null)
        {
            return;
        }

        if (areYouSure("refresh this page, all data on the form will be reset \nhowever, this will not reset deleted Supplier Shop Info as this is permanently deleted"))
        {
            reloadFormWithIngredientInfo();
        }
    }

    private void reloadFormWithIngredientInfo()
    {
        //#####################################
        // Update IngredientsForms
        //####################################
        ingredientsForm.loadIngredientsFormData();

        //#####################################
        // Update ShopForm
        //####################################
        shopForm.loadShopFormData();
    }

    public void refreshInterface(boolean resetIngredientNameJCombo, boolean resetIngredientTypeJComBox) // only available to reset screen
    {
        //##################################
        // Clear both forms of info
        //##################################
        ingredientsForm.clearIngredientsForm();
        shopForm.clearShopForm();
        searchForIngredientInfo.resetFullDisplay();
        searchForIngredientInfo.collapseJPanel();

        //##################################
        // Reset JComboBox's
        //##################################
        if (resetIngredientNameJCombo)
        {
            clearIngredientNameJCombo();
            setIngredientNameJCombo_ToNothingSelected();
        }
        if (resetIngredientTypeJComBox)
        {
            setIngredientTypeJCombo_ToNothingSelected();
        }
    }

    private void clearIngredientNameJCombo()
    {
        ingredientsNameJComboBox.removeAllItems();
    }

    //#############################################################################################################
    // Update Methods
    //#############################################################################################################
    private void updateFormWithIngredientInfo()
    {
        //#####################################
        // Update IngredientsForms
        //####################################
        ingredientsForm.updateIngredientsInfoFromDB();

        //#####################################
        // Update ShopForm
        //####################################
        shopForm.updateShopFormWithInfoFromDB();
    }

    public void updateIngredientsTypeJComboBox()
    {
        ingredientsTypesJComboBox.removeAllItems(); // clearList

        TreeMap<String, Collection<String>> mapIngredientTypesToNames = ingredients_info_screen.getMapIngredientTypesToNames();
        for (String key : mapIngredientTypesToNames.keySet())
        {
            if (!key.equals("None Of The Above"))
            {
                ingredientsTypesJComboBox.addItem(key);
            }
        }
    }

    @Override
    protected void updateIngredientForm_Type_JComboBox()
    {
        ingredientsForm.loadIngredientsTypeJComboBox();
    }

    public void updateMapIngredientsTypesAndNames() // HELLO REFACTOR INTO PARENT
    {
        //###########################################################
        // Clear List
        //###########################################################
        TreeMap<String, Collection<String>> map_ingredientTypesToIngredientNames = ingredients_info_screen.getMapIngredientTypesToNames();
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
                  SELECT ingredient_type_id, ingredient_type_name FROM ingredientTypes
                ) AS N
                ON I.ingredient_type_id = N.ingredient_type_id
                ORDER BY N.ingredient_type_name;""");

        ArrayList<ArrayList<String>> ingredientTypesNameAndIDResults = db.getMultiColumnQuery(queryIngredientsType);

        if (ingredientTypesNameAndIDResults==null)
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
            String queryTypeIngredientNames = String.format("SELECT Ingredient_Name FROM ingredients_info WHERE ingredient_type_id = %s ORDER BY Ingredient_Name;", ID);
            Collection<String> ingredientNames = db.getSingleColumnQuery_AlphabeticallyOrderedTreeSet(queryTypeIngredientNames);


            if (ingredientNames==null)
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

    private void updateIngredientNameJComboBox()
    {
        //##################################
        // Populating IngredientName JComBox
        //##################################
        setUpdateStatusOfIngredientNames(true); // stops ingredientName JComboBox from triggering any  actionListener events

        TreeMap<String, Collection<String>> map_ingredientTypesToIngredientNames = ingredients_info_screen.getMapIngredientTypesToNames();
        for (String item : map_ingredientTypesToIngredientNames.get(selected_IngredientType))
        {
            if (!item.equals("None Of The Above"))
            {
                ingredientsNameJComboBox.addItem(item);
            }
        }

        setIngredientNameJCombo_ToNothingSelected();// set selected item to nothing
        setUpdateStatusOfIngredientNames(false);
    }

    public void updateIngredientNamesToTypesJComboBox()
    {
        ingredientsTypesJComboBox.removeAllItems();
        TreeMap<String, Collection<String>> map_ingredientTypesToIngredientNames = ingredients_info_screen.getMapIngredientTypesToNames();

        for (String key : map_ingredientTypesToIngredientNames.keySet())
        {
            if (!key.equals("None Of The Above"))
            {
                ingredientsTypesJComboBox.addItem(key);
            }
        }

        setIngredientTypeJCombo_ToNothingSelected(); // set selected item to nothing
    }

    //#############################################################################################################
    // Submission Actions
    //#############################################################################################################
    @Override
    protected void submissionBtnAction()
    {
        //##############################################################################################################
        //
        //##############################################################################################################
        if (ingredientsNameJComboBox.getSelectedItem()==null)
        {
            JOptionPane.showMessageDialog(mealPlanScreen, "Please select an ingredient please to edit it!");
            refreshInterface(true, true);
            return;
        }

        if (ingredientsNameJComboBox.getSelectedItem().equals("N/A"))
        {
            JOptionPane.showMessageDialog(mealPlanScreen, "The Store N/A cannot be edited, its a placeholder");
            refreshInterface(true, true);
            return;
        }

        //##############################################################################################################
        //
        //##############################################################################################################
        if (!(areYouSure("update this Ingredients information - this will cause the mealPlan to save its data to the DB")))
        {
            return;
        }

        //##############################################################################################################
        //
        //##############################################################################################################
        if (!(ingredientsForm.validate_IngredientsForm()) || !(shopForm.validateForm()))
        {
            return;
        }

        //##############################################################################################################
        //
        //##############################################################################################################
        String selectedIngredientID = ingredientsForm.getSelectedIngredientID();

        if (selectedIngredientID==null)
        {
            JOptionPane.showMessageDialog(mealPlanScreen, "\n\nUnable To Get Ingredient ID To Edit This Ingredient !!");
            return;
        }

        //###############################################################
        // Update Database Only if Data on form has changed
        //###############################################################
        String ingredientsFormUpdateString = ingredientsForm.get_IngredientsForm_UpdateString(selectedIngredientID);
        String[] shopFormUpdateString = shopForm.get_ShopForm_UpdateString(selectedIngredientID);

        if(ingredientsFormUpdateString==null && shopFormUpdateString == null)
        {
            int reply = JOptionPane.showConfirmDialog(mealPlanScreen,
                    String.format("\n\nNo modifications / changes were made to the ingredient '%s'. \nWould you like to clear the form?",ingredientsNameJComboBox.getSelectedItem()),
                    "", JOptionPane.YES_NO_OPTION); //HELLO Edit

            if (reply == JOptionPane.NO_OPTION || reply == JOptionPane.CLOSED_OPTION)
            {
                return;
            }
            if(reply == JOptionPane.YES_OPTION)
            {
                refreshInterface(true, true);
                return;
            }
        }

        //###############################################################
        // Get Update Strings & Update for both forms using ingredientID
        //###############################################################
        //System.out.printf("\n\nsubmissionBtnAction() \ninfo Update: \n%s \n\nshop Update: \n%s", ingredientsFormUpdateString, Arrays.toString(shopFormUpdateString));

        if (!(updateBothForms(ingredientsFormUpdateString,shopFormUpdateString )))
        {
            JOptionPane.showMessageDialog(mealPlanScreen, "\n\nUnable To Update Ingredient Info !!");
            return;
        }

        //##############################################################################################################
        // Write Ingredients Value To File
        //##############################################################################################################
        if (!(backupDataInSQLFile()))
        {
            JOptionPane.showMessageDialog(null, "Error, replacing ingredients info to SQL file!");
        }

        //##############################################################################################################
        //
        //##############################################################################################################
        // Check if ingredientsName or IngredientType changed
        String currentIngredientName = ingredientsForm.getIngredientNameFormValue();
        String currentIngredientType = ingredientsForm.getIngredientTypeFormValue();

        String previousIngredientName = selected_IngredientName;
        String previousIngredientType = selected_IngredientType;

        //HELLO REMOVE
        System.out.printf("\n\nIngredientName \nCurrent = '%s' \nPrevious = '%s' \n\nIngredientType \nCurrent = '%s' \nPrevious = '%s'",
                currentIngredientName, previousIngredientName, previousIngredientType, currentIngredientType);

        if ((!currentIngredientName.equals(previousIngredientName) || (!currentIngredientType.equals(previousIngredientType))))
        {
            ingredients_info_screen.setUpdateIngredientInfo(true);

            // if there is an error trying to add or remove ingredientType throw an error
            if (!addOrDeleteIngredientFromMap("delete", previousIngredientType, previousIngredientName) // remove old info ingredient
                    || !addOrDeleteIngredientFromMap("add", currentIngredientType, currentIngredientName))// add new info ingredient
            {
                JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "\n\nUnable to perform local update for ingredient Info");
                return;
            }
        }

        //##############################################################################################################
        //
        //##############################################################################################################
        JOptionPane.showMessageDialog(mealPlanScreen, "The ingredient updates won't appear on the mealPlan screen until this window is closed!");
        refreshInterface(true, true);
        super.resize_GUI();
    }

    //EDITING NOW
    @Override
    protected boolean updateBothForms(String updateIngredients_String, String[] updateIngredientShops_String)
    {
        //####################################
        // Nothing to Update
        //####################################
        if (updateIngredients_String==null && updateIngredientShops_String==null)
        {
            System.out.println("\n\nupdateBothForms() no ingredientInfo / shopInfo to Update");
            return false;
        }

        //####################################
        // Uploading Ingredient Info Query
        //####################################
        if (updateIngredients_String!=null && !(db.uploadData_Batch_Altogether(new String[]{updateIngredients_String})))
        {
            JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "Failed Upload - Unable To Add Ingredient Info & Shop Info & Ingredient Suppliers In DB!");
            return false;
        }

        //####################################
        // Uploading Shop Info
        //####################################
        boolean errorUploading = false;

        if (updateIngredientShops_String!=null)
        {
            int noOfUpdateProcesses = updateIngredientShops_String.length;

            for (int x = 0; x < noOfUpdateProcesses; x++)
            {
                if (!(db.uploadData_Batch_Altogether(new String[]{updateIngredientShops_String[x]})))
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
    protected boolean backupDataInSQLFile() //HELLO Iteration has next can predict last loop
    {
        String replacementData = "(null,";
        for (Map.Entry<String, Object[]> entry : ingredientsForm.getIngredientsFormObjectAndValues().entrySet())
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
        String oldIngredientName = String.format("(\"%s\")", getSelectedIngredientName().trim());
        if (!(db.replaceTxtInSQLFile(sqlBackUpPath, false, oldIngredientName, replacementData)))
        {
            JOptionPane.showMessageDialog(null, String.format("Error, changing back-up of %s in SQL file of ingredient info!", oldIngredientName));
            return false;
        }

        return true;
    }

    //#############################################################################################################
    // Delete BTN Actions
    //#############################################################################################################
    private void deleteIngredientBTNAction()//HELLO PROGRAM DELETE BTN
    {
        //##############################################################################################################
        //
        //##############################################################################################################
        if (ingredientsNameJComboBox.getSelectedIndex()==-1)
        {
            JOptionPane.showMessageDialog(mealPlanScreen, "Please select an item first before attempting to delete an ingredient!");
            return;
        }

        //##############################################################################################################
        //
        //##############################################################################################################
        String selectedIngredientID = ingredientsForm.getSelectedIngredientID();
        String selectedIngredientName = ingredientsForm.getSelectedIngredientName();

        //HELLO should be removed as N/A is never in the JComboBox
        if (selectedIngredientName.equals("N/A"))
        {
            JOptionPane.showMessageDialog(mealPlanScreen, "This item cannot be deleted from the list (its a placeholder) !");
            refreshInterface(true, true);
            return;
        }

        //##############################################################################################################
        //
        //##############################################################################################################
        if (!(areYouSure(String.format("delete ingredient named '%s' from the database", selectedIngredientName))))
        {
            return;
        }

        //##############################################################################################################
        //
        //##############################################################################################################
        if (selectedIngredientID==null || selectedIngredientName==null)
        {
            JOptionPane.showMessageDialog(mealPlanScreen, "Unable to grab Ingredient INFO to delete it!!");
            return;
        }

        //##############################################################################################################
        //
        //##############################################################################################################
        String query0 = String.format("DELETE FROM `ingredients_in_sections_of_meal` WHERE IngredientID  = %s;", selectedIngredientID);
        String query1 = String.format("DELETE FROM `ingredientInShops` WHERE IngredientID  = %s;", selectedIngredientID);
        String query2 = String.format("DELETE FROM `ingredients_info` WHERE IngredientID  = %s;", selectedIngredientID);

        if (db.uploadData_Batch_Altogether(new String[]{query0, query1, query2}))
        {
            JOptionPane.showMessageDialog(mealPlanScreen, String.format("Successfully Deleted '%s' From DB!", selectedIngredientName));
            addOrDeleteIngredientFromMap("delete", selected_IngredientType, selectedIngredientName); // delete ingredient
            refreshInterface(true, true);
            ingredients_info_screen.setUpdateIngredientInfo(true);
        }
        else
        {
            JOptionPane.showMessageDialog(mealPlanScreen, "Unable to delete item From DB!");
            return;
        }

        //##############################################################################################################
        // Delete From BackUp SQL File
        //##############################################################################################################

        if (!(db.deleteTxtInFile(super.sqlBackUpPath, String.format("(\"%s\"),", selectedIngredientName))))
        {
            JOptionPane.showMessageDialog(null, String.format("\n\nError, deleteBTNAction() deleting ingredient '%s' from backup files!", selectedIngredientName));
        }
    }

    //#############################################################################################################
    // Mutator Methods
    //#############################################################################################################
    /*
     When the variable it's used to set is true the ingredientNames JComboBox is updating
     this stops the ingredientNames JComboBox from triggering actionListener events!
    */
    private void setUpdateStatusOfIngredientNames(boolean x) // can't be deleted, trust me
    {
        jComboUpdateStatus = x;
    }

    /*
      Resets Ingredient Type JComboBox
   */
    private void setIngredientTypeJCombo_ToNothingSelected()
    {
        ingredientsTypesJComboBox.setSelectedIndex(-1);
    }

    /*
        Resets Ingredient Type JComboBox
    */
    private void setIngredientNameJCombo_ToNothingSelected()
    {
        ingredientsNameJComboBox.setSelectedIndex(-1);
    }

    //#############################################################################################################
    // Get Methods
    //#############################################################################################################
    public boolean getUpdateStatusOfIngredientNames()// can't be deleted, trust me
    {
        return jComboUpdateStatus;
    }

    public String getSelectedIngredientName()
    {
        return selected_IngredientName;
    }

    public Edit_IngredientsForm getIngredientsForm()
    {
        return ingredientsForm;
    }
}
