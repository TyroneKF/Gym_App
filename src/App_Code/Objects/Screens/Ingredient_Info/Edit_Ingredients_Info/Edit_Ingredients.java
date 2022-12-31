package App_Code.Objects.Screens.Ingredient_Info.Edit_Ingredients_Info;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Gui_Objects.IconPanel;
import App_Code.Objects.Gui_Objects.ScrollPaneCreator;
import App_Code.Objects.Screens.Ingredient_Info.SearchForFoodInfo;
import org.javatuples.Triplet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class Edit_Ingredients extends Add_Ingredients
{
    private boolean jcomboUpdateStaus = false;
    private EditIngredientsForm ingredientsForm;
    private EditShopForm shopForm;

    private String selectedIngredientID, selectedIngredientName, selected_IngredientType_JComboItem;

    private String previousIngredientType, previousIngredientName;
    private ArrayList<Component> formObjects;

    private boolean ingredientEditable = true;

    private JComboBox<String>
            ingredientsNameJComboBox = new JComboBox(),
            ingredientsTypesJComboBox = new JComboBox();


    Edit_Ingredients(Ingredients_Info_Screen parent, MyJDBC db)
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

        ingredientsForm = new EditIngredientsForm(this, "Edit Ingredients Info", 250, 50);
        shopForm = new EditShopForm(scrollPaneJPanel, "Edit Ingredient Suppliers", 250, 50);
        searchForIngredientInfo = new SearchForFoodInfo(scrollPaneJPanel, ingredientsForm, "Search For Food Info", 250, 50);
        searchForIngredientInfo.collapseJPanel();

        //###########################################
        // Set IngredientName in form to un-editable
        //############################################
        ArrayList<Component> componentArrayList = ingredientsForm.getIngredientsFormObjects();
        JTextField ingredientNameJTextField = (JTextField) componentArrayList.get(ingredientsForm.getIngredientNameObjectIndex());
        ingredientNameJTextField.setEditable(ingredientEditable);

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
                if (ie.getStateChange() == ItemEvent.SELECTED)
                {
                    // Get ingredientType
                    selected_IngredientType_JComboItem = (String) ie.getItem().toString();

                    // clear ingredientsNames JCombo
                    refreshInterface(true, false);

                    if (selected_IngredientType_JComboItem.equals(""))
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
                if(! (getUpdateStatusOfIngredientNames())) // only trigger event when the ingredientsName Jcombobox is not updating
                {
                    if (ingredientsNameJComboBox.getSelectedIndex() != -1)
                    {
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

    //#########################################################################
    //
    //#########################################################################

    public void updateIngredientsTypeJComboBox()
    {
        ingredientsTypesJComboBox.removeAllItems(); // clearList

        TreeMap<String, Collection<String>> mapIngredientTypesToNames = parent.getMapIngredientTypesToNames();
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
        TreeMap<String, Collection<String>> map_ingredientTypesToIngredientNames = parent.getMapIngredientTypesToNames();
        map_ingredientTypesToIngredientNames.clear();

        //###########################################################
        // Store ingredientTypes ID's & IngredientTypeName that occur
        //###########################################################
        String queryIngredientsType = String.format("""
                SELECT I.Ingredient_Type_ID, n.Ingredient_Type_Name
                FROM
                (
                  SELECT DISTINCT(Ingredient_Type_ID) FROM ingredients_info
                ) I
                INNER JOIN
                (
                  SELECT Ingredient_Type_ID, Ingredient_Type_Name FROM ingredientTypes
                )n
                ON i.Ingredient_Type_ID = n.Ingredient_Type_ID
                ORDER BY n.Ingredient_Type_Name;""");

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
            String queryTypeIngredientNames = String.format("SELECT Ingredient_Name FROM ingredients_info WHERE Ingredient_Type_ID = %s ORDER BY Ingredient_Name;", ID);
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

    /*
     When the variable it's used to set is true the ingredientNames JComboBox is updating
     this stops the ingredientNames JComboBox from triggering actionListener events!
    */
    private void setUpdateStatusOfIngredientNames(boolean x) // can't be deleted, trust me
    {
        jcomboUpdateStaus = x;
    }

    private boolean getUpdateStatusOfIngredientNames()// can't be deleted, trust me
    {
        return jcomboUpdateStaus;
    }

    public void updateIngredientNamesToTypesJComboBox()
    {
        ingredientsTypesJComboBox.removeAllItems();
        TreeMap<String, Collection<String>> map_ingredientTypesToIngredientNames = parent.getMapIngredientTypesToNames();

        for (String key : map_ingredientTypesToIngredientNames.keySet())
        {
            if (!key.equals("None Of The Above"))
            {
                ingredientsTypesJComboBox.addItem(key);
            }
        }

        setNothingSelectedIngredientTypeJCombo(); // set selected item to nothing
    }

    /*
       Resets Ingredient Type JComboBox
    */
    private void setNothingSelectedIngredientTypeJCombo()
    {
        ingredientsTypesJComboBox.setSelectedIndex(-1);
    }

    private void clearIngredientTypeJCombo()
    {
        ingredientsTypesJComboBox.removeAllItems();
    }

    private void updateIngredientNameJComboBox()
    {
        //##################################
        // Populating IngredientName JComBox
        //##################################
        setUpdateStatusOfIngredientNames(true); // stops ingredientName JComboBox from triggering any  actionListener events

        TreeMap<String, Collection<String>> map_ingredientTypesToIngredientNames = parent.getMapIngredientTypesToNames();
        for (String item : map_ingredientTypesToIngredientNames.get(selected_IngredientType_JComboItem))
        {
            if (!item.equals("None Of The Above"))
            {
                ingredientsNameJComboBox.addItem(item);
            }
        }

        setNothingSelectedIngredientNameJCombo();// set selected item to nothing
        setUpdateStatusOfIngredientNames(false);
    }

    /*
        Resets Ingredient Type JComboBox
    */
    private void setNothingSelectedIngredientNameJCombo()
    {
        ingredientsNameJComboBox.setSelectedIndex(-1);
    }

    private void clearIngredientNameJCombo()
    {
        ingredientsNameJComboBox.removeAllItems();
    }

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

        IconButton delete_Icon_Btn = new IconButton("src/images/x/x.png", "", width, height, width, height,
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

        IconButton refresh_Icon_Btn = new IconButton("src/images/refresh/+++++refresh.png", "", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor

        JButton refresh_Btn = refresh_Icon_Btn.returnJButton();
        refresh_Icon_Btn.makeBTntransparent();

        refresh_Btn.addActionListener(ae -> {

            refreshFormBTNAction();
        });

        iconPanelInsert.add(refresh_Icon_Btn);
    }

    private void refreshFormBTNAction()
    {
        if (selectedIngredientName == null)
        {
            return;
        }

        if (areYouSure("refresh this page, all data on the form will be reset \nhowever, this will not reset deleted Supplier Shop Info as this is permanently deleted"))
        {
            updateFormWithIngredientInfo();
        }
    }

    private void updateFormWithIngredientInfo()
    {
        //#####################################
        // Update IngredientsForms
        //####################################
        ingredientsForm.updateIngredientsFormWithInfoFromDB();

        //#####################################
        // Update ShopForm
        //####################################
        shopForm.updateShopFormWithInfoFromDB();
    }

    private void deleteIngredientBTNAction()
    {
        if (ingredientsNameJComboBox.getSelectedIndex() != -1)
        {

            if (selectedIngredientName.equals("N/A"))
            {
                JOptionPane.showMessageDialog(mealPlanScreen, "This item cannot be deleted from the list (its a placeholder) !");
                refreshInterface(true, true);
                return;
            }

            if (areYouSure(String.format("delete ingredient named '%s' from the database", selectedIngredientName)))
            {
                if (selectedIngredientID == null || selectedIngredientName == null)
                {
                    JOptionPane.showMessageDialog(mealPlanScreen, "Unable to grab Ingredient INFO to delete it!!");
                    return;
                }

                String query0 = String.format("DELETE FROM `ingredients_in_meal` WHERE IngredientID  = %s;", selectedIngredientID);
                String query1 = String.format("DELETE FROM `ingredientInShops` WHERE IngredientID  = %s;", selectedIngredientID);
                String query2 = String.format("DELETE FROM `ingredients_info` WHERE IngredientID  = %s;", selectedIngredientID);

                if (db.uploadData_Batch_Altogether(new String[]{query0, query1, query2}))
                {
                    JOptionPane.showMessageDialog(mealPlanScreen, String.format("Successfully Deleted '%s' From DB!", selectedIngredientName));
                    addOrDeleteIngredientFromMap("delete", selected_IngredientType_JComboItem, selectedIngredientName); // delete ingredient
                    refreshInterface(true, true);
                    parent.setUpdateIngredientInfo(true);
                }
                else
                {
                    JOptionPane.showMessageDialog(mealPlanScreen, "Unable to delete item From DB!");
                }
            }
            return;
        }
        JOptionPane.showMessageDialog(mealPlanScreen, "Please select an item first before attempting to delete an ingredient!");
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
            setNothingSelectedIngredientNameJCombo();
        }
        if (resetIngredientTypeJComBox)
        {
            setNothingSelectedIngredientTypeJCombo();
        }
    }

    private String getSelectedIngredientName()
    {
        try
        {
            Object selectedItem = ingredientsNameJComboBox.getSelectedItem();

            if (selectedItem != null)
            {
                String x = selectedItem.toString();

                if (x.length() > 0)
                {
                    return x;
                }
            }
        }
        catch (Exception e)
        {
            return null;
        }
        return null;
    }

    @Override
    protected void submissionBtnAction()
    {
        if (ingredientsNameJComboBox.getSelectedItem().equals("N/A"))
        {
            JOptionPane.showMessageDialog(mealPlanScreen, "The Store N/A cannot be edited, its a placeholder");
            refreshInterface(true, true);
            return;
        }

        if (areYouSure("update this Ingredients information - this will cause the mealPlan to save its data to the DB"))
        {
            boolean errorFound = false;

            // ingredientsForm
            if (!(ingredientsForm.validate_IngredientsForm()))
            {
                errorFound = true;
            }

            // ShopForm
            if (!(shopForm.validateForm()))
            {
                errorFound = true;
            }

            if (!errorFound)
            {
                //########################
                // Get ingredient ID
                //########################
                if (selectedIngredientID == null)
                {
                    JOptionPane.showMessageDialog(mealPlanScreen, "\n\nUnable To Get Ingredient ID To Edit This Ingredient !!");
                    return;
                }

                //#############################
                // Get Update Strings & Update
                //#############################

                if (updateBothForms(ingredientsForm.get_IngredientsForm_UpdateString(selectedIngredientID), shopForm.get_ShopForm_UpdateString(selectedIngredientID)))
                {
                    // Check if ingredientsName or IngredientType changed
                    String currentIngredientName = ((JTextField) formObjects.get(ingredientsForm.getIngredientNameObjectIndex())).getText().trim();
                    String currentIngredientType = ((JComboBox) formObjects.get(ingredientsForm.getIngredientTypeObjectIndex())).getSelectedItem().toString();

                    //HELLO REMOVE
                    System.out.printf("\n\nIngredientName \nCurrent = '%s' \nPrevious = '%s' \n\nIngredientType \nCurrent = '%s' \nPrevious = '%s'",
                            currentIngredientName, previousIngredientName, previousIngredientType, currentIngredientType);

                    if ((!currentIngredientName.equals(previousIngredientName) || (!currentIngredientType.equals(previousIngredientType))))
                    {
                        parent.setUpdateIngredientInfo(true);
                        // if there is an error trying to add or remove ingredientType throw an error
                        if (!addOrDeleteIngredientFromMap("delete", previousIngredientType, previousIngredientName) // remove old info ingredient
                                || !addOrDeleteIngredientFromMap("add", currentIngredientType, currentIngredientName))// add new info ingredient
                        {
                            JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "\n\nUnable to perform local update for ingredient Info");
                            return;
                        }
                    }

                    JOptionPane.showMessageDialog(mealPlanScreen, "The ingredient updates won't appear on the mealPlan screen until this window is closed!");
                    refreshInterface(true, true);
                    super.resize_GUI();
                }
            }
        }
    }

    //EDITING NOW
    @Override
    protected boolean updateBothForms(String updateIngredients_String, String[] updateIngredientShops_String)
    {
        //####################################
        // Uploading Ingredient Info Query
        //####################################
        if (!(db.uploadData_Batch_Altogether(new String[]{updateIngredients_String})))
        {
            JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "Failed Upload - Unable To Add Ingredient Info & Shop Info & Ingredient Suppliers In DB!");
            return false;
        }

        //####################################
        // Update Shop Info
        //####################################
        boolean errorUploading = false;

        if (updateIngredientShops_String != null)
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

            if (!errorUploading)
            {
                JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), String.format("\n\nUpdated Ingredient Info! \n\nAlso updated %s/%s -  Suppliers For Ingredient Updated In DB!!!",
                        noOfUpdateProcesses, noOfUpdateProcesses));
            }
        }
        return true;
    }

    //########################################################################
    // Ingredients Form
    //########################################################################
    public class EditIngredientsForm extends IngredientsForm
    {
        public EditIngredientsForm(Container parentContainer, String btnText, int btnWidth, int btnHeight)
        {
            super(parentContainer, btnText, btnWidth, btnHeight);
        }

        @Override
        protected void createIconBar()
        {
            createIconBarOnGUI(false);
        }

        @Override
        protected String extra_Validation_IngredientName(String errorTxt, String makeIngredientName)
        {
            if (makeIngredientName != null || !(makeIngredientName.equals("")))
            {
                makeIngredientName = removeSpaceAndHiddenChars(makeIngredientName);
                String previousIngredientName = removeSpaceAndHiddenChars(selectedIngredientName);

                System.out.printf("\n\nName 1: %s || Name2: %s", makeIngredientName, previousIngredientName);

                if (!(previousIngredientName.equals(makeIngredientName)))
                {
                    if (checkIfIngredientNameInDB(makeIngredientName))
                    {
                        errorTxt += String.format("\n\n@@  Ingredient named %s already exists within the database!", makeIngredientName);
                    }
                }
            }
            return errorTxt;
        }

        private void updateIngredientsFormWithInfoFromDB()
        {
            if (getUpdateStatusOfIngredientNames())
            {
                return;
            }

            //############################################################
            // Ingredient ID
            //############################################################
            selectedIngredientName = getSelectedIngredientName();

            if (selectedIngredientName == null)
            {
                JOptionPane.showMessageDialog(mealPlanScreen, "Unable to grab Ingredient Name to edit it!!");
                return;
            }

            selectedIngredientName = selectedIngredientName; //HELLO what is the use?
            previousIngredientName = selectedIngredientName;

            //###############################
            //
            //###############################
            String getIngredientInfoString = ingredientsForm.mysqlGetIngredientInfo(selectedIngredientName);

            ArrayList<ArrayList<String>> ingredientInfo_R = db.getMultiColumnQuery(getIngredientInfoString);

            if (ingredientInfo_R == null)
            {
                JOptionPane.showMessageDialog(mealPlanScreen, "Unable to grab selected ingredient info!");
                return;
            }

            ArrayList<String> ingredientInfo = ingredientInfo_R.get(0);

            //##############################
            // Get Ingredient ID
            //##############################
            selectedIngredientID = ingredientInfo.get(0);

            //##############################
            // Set Form With Ingredient Info
            //##############################

            int formObjectsIndex = 0;

            for (int i = 1; i < ingredientInfo.size(); i++)
            {
                Component comp = formObjects.get(formObjectsIndex); // query size and form objects size arent at the same index
                String value = ingredientInfo.get(i);

                // setting previous ingredient Type value
                if (formObjectsIndex == ingredientsForm.getIngredientTypeObjectIndex()) // accounting for id being added
                {
                    previousIngredientType = value;
                }

                if (comp instanceof JComboBox)
                {
                    ((JComboBox<String>) comp).setSelectedItem(value);
                }
                else if (comp instanceof JTextField)
                {
                    ((JTextField) comp).setText(value);
                }
                formObjectsIndex++;
            }

            //##############################
            // Set Salt To Grams
            //##############################
            saltMeasurement_JComboBox.setSelectedItem("g");
        }

        private String mysqlGetIngredientInfo(String ingredientName)
        {
            try
            {
                //############################################################
                // Update IngredientsForm
                //############################################################
                formObjects = ingredientsForm.getIngredientsFormObjects();

                //##############################
                // Get Ingredient Info
                //##############################

                String
                        sqlIngredientIDNameCol = "IngredientID",
                        tableName = "ingredients_info",
                        tableReference = "info",
                        sqlIngredientTypeNameCol = "Ingredient_Type_Name",
                        sqlIngredientTypeTable = "ingredientTypes",
                        selectStatement = String.format("SELECT \n%s.%s,", tableReference, sqlIngredientIDNameCol);


                int pos = -1, listSize = ingredientsFormLabelsMapsToValues.size();
                String mysqlIngredientNameKey = "";

                for (Map.Entry<String, Triplet<String, String, String>> entry : ingredientsFormLabelsMapsToValues.entrySet())
                {
                    //####################################z
                    //z
                    //####################################z
                    pos++;
                    Triplet<String, String, String> value = entry.getValue();

                    String formLabelName = entry.getKey();
                    String sqlColumnName = value.getValue1();

                    //####################################
                    //
                    //####################################
                    String stringToAdd = "";
                    stringToAdd = String.format("\n%s.%s", tableReference, sqlColumnName);

                    //
                    if (pos == ingredientTypeObjectIndex)
                    {
                        String ingredientTypeStatement = String.format("""
                                        \n(SELECT t.%s FROM %s t  WHERE t.%s = %s.%s)  AS Ingredient_Type""",
                                sqlIngredientTypeNameCol, sqlIngredientTypeTable, sqlColumnName, tableReference, sqlColumnName);

                        stringToAdd = ingredientTypeStatement;
                    }

                    selectStatement += stringToAdd;

                    //
                    if (pos == ingredientNameObjectIndex)
                    {
                        mysqlIngredientNameKey = sqlColumnName;
                    }

                    //
                    if (pos == listSize - 1)
                    {
                        selectStatement += String.format("\n\nFROM %s %s", tableName, tableReference);

                        String whereStatement = String.format("\nWHERE %s.%s = '%s';", tableReference, mysqlIngredientNameKey, ingredientName);

                        selectStatement += whereStatement;
                        break;
                    }
                    selectStatement += ",";
                }

                return selectStatement;
            }
            catch (Exception e)
            {
                System.out.printf("\n\nError mysqlGetIngredientInfo() \n%s", e);
            }

            return null;
        }

        protected String get_IngredientsForm_UpdateString(String ingredientID) // HELLO needs further update methods created for gui
        {
            //####################################
            //
            //####################################
            JTextField ingredientName_JTxtF = (JTextField) ingredientsFormObjects.get(getIngredientNameObjectIndex());
            String ingredientName_Txt = ingredientName_JTxtF.getText().trim();


            String
                    tableName = "ingredients_info",
                    ingredientIDColName = "IngredientID",

                    setQuery = String.format("UPDATE %s \nSET", tableName);
            //####################################
            //
            //####################################
            int pos = -1, listSize = ingredientsFormLabelsMapsToValues.size();
            for (Map.Entry<String, Triplet<String, String, String>> entry : ingredientsFormLabelsMapsToValues.entrySet())
            {
                pos++;
                Triplet<String, String, String> value = entry.getValue();

                String sqlColumnName = value.getValue1();
                String mysqlColumnDataType = value.getValue2();

                //####################################
                //
                //####################################
                Component formObject = ingredientsFormObjects.get(pos);
                String formFieldValue = "";

                if (formObject instanceof JTextField)
                {
                    formFieldValue = ((JTextField) formObject).getText();
                }
                else if (formObject instanceof JComboBox)
                {
                    formFieldValue = ((JComboBox) formObject).getSelectedItem().toString();
                }

                //####################################
                //
                //####################################

                if (pos == ingredientTypeObjectIndex)
                {
                    String ingredientTypeSet = "SELECT Ingredient_Type_ID FROM ingredientTypes WHERE Ingredient_Type_Name = ";
                    formFieldValue = String.format("(%s '%s')", ingredientTypeSet, formFieldValue);
                }
                else if (mysqlColumnDataType.equals("String"))
                {
                    formFieldValue = String.format("'%s'", formFieldValue);
                }

                //####################################
                //
                //####################################

                if (pos == listSize - 1)
                {
                    setQuery += String.format("\n%s = %s", sqlColumnName, formFieldValue);
                    String whereStatement = String.format("\nWHERE %s = %s;", ingredientIDColName, ingredientID);
                    setQuery += whereStatement;

                    break;
                }

                setQuery += String.format("\n%s = %s,", sqlColumnName, formFieldValue);
            }

            //####################################
            // Return results
            //####################################
            return setQuery;
        }
    }

    //########################################################################
    // Shop Form
    //########################################################################
    public class EditShopForm extends ShopForm
    {
        public EditShopForm(Container parentContainer, String btnText, int btnWidth, int btnHeight)
        {
            super(parentContainer, btnText, btnWidth, btnHeight);
        }

        private void updateShopFormWithInfoFromDB()
        {
            //###########################
            // Get New Ingredient Shop Info
            //###########################
            ArrayList<ArrayList<String>> ingredientShops_R = db.getMultiColumnQuery(String.format("""                    
                SELECT i.PDID, s.Store_Name, i.Cost_Per_Unit, i.Volume_Per_Unit
                FROM  ingredientInShops i
                INNER JOIN
                (
                  SELECT StoreID, Store_Name FROM stores
                ) s
                ON s.StoreID = i.StoreID
                AND  i.IngredientID = %s ;""", selectedIngredientID));

            if (ingredientShops_R == null)
            {
                JOptionPane.showMessageDialog(mealPlanScreen, "Unable to grab selected ingredient shop info! \nMaybe there isn't any suppliers created for this Ingredient!");
                return;
            }

            //###########################
            // Clear Supplier Info
            //###########################
            shopForm.clearShopForm();

            //###########################
            //Add Rows for shops onto form
            //###########################

            for (int i = 0; i < ingredientShops_R.size(); i++)
            {
                ArrayList<String> rowData = ingredientShops_R.get(i);

                // Set PDID & Add Row
                ShopForm.AddShopForm_Object row = shopForm.addShopForm_object(Integer.parseInt(rowData.get(0)));
                addShopFormObjects.add(row);

                // Set ShopName
                row.getShops_JComboBox().setSelectedItem(rowData.get(1));// HELLO IDK WHAT I DID HERE in REFACTORING

                // Set Cost Info
                row.getIngredientPrice_TxtField().setText(rowData.get(2));// HELLO IDK WHAT I DID HERE  in REFACTORING

                // Set Volume Info
                row.getQuantityPerPack_TxtField().setText(rowData.get(3));// HELLO IDK WHAT I DID HERE  in REFACTORING
            }
        }

        //EDITING NOW
        public String[] get_ShopForm_UpdateString(String ingredientIDIn) // Not an override method
        {
            //#############################################################
            // Checks if there is anything to update before, updating
            //############################################################
            if (addShopFormObjects.size() == 0)
            {
                return null;
            }

            //################################################################
            // Adding Shops To Category's To Either Insert Or Update into DB
            //################################################################
            ArrayList<AddShopForm_Object> suppliersInDBList = new ArrayList<>();
            ArrayList<AddShopForm_Object> suppliersNeedToBeAddedToDBList = new ArrayList<>();

            for (AddShopForm_Object shopForm_object : addShopFormObjects)
            {
                //  EditAddShopForm_Object obj = (EditAddShopForm_Object) shopForm_object;
                AddShopForm_Object obj = shopForm_object;

                if (obj.getPDID() != null)
                {
                    suppliersInDBList.add(obj);
                }
                else
                {
                    suppliersNeedToBeAddedToDBList.add(obj);
                }
            }

            //###############################
            // Creating Update List
            //###############################
            int suppliersInDBSize = suppliersInDBList.size();
            int suppliersNotInDBSize = suppliersNeedToBeAddedToDBList.size();

            String[] updates = new String[suppliersInDBSize + suppliersNotInDBSize];

            int listPos = 0;

            ///###################################################################
            // Creating Insert Supplier Statement
            //####################################################################

            if (suppliersInDBSize > 0)
            {
                // Creating String Of Add Values
                for (AddShopForm_Object supplierInDB : suppliersInDBList)
                {
                    int objectID = supplierInDB.getObjectID();
                    Integer PDID = supplierInDB.getPDID();

                    //Update String
                    String updateString = String.format("""
                                    UPDATE ingredientInShops
                                    SET Volume_Per_Unit = %s, Cost_Per_Unit = %s, StoreID = (SELECT StoreID FROM stores WHERE Store_Name = '%s')
                                    WHERE PDID = %s;""",
                            supplierInDB.getQuantityPerPack_TxtField().getText(),
                            supplierInDB.getIngredientPrice_TxtField().getText(),
                            supplierInDB.getShops_JComboBox().getSelectedItem().toString(),
                            PDID);

                    // Add to update List
                    updates[listPos] = updateString;

                    listPos++;
                }
            }

            //###################################################################
            // Creating Insert Supplier Statement
            //####################################################################

            String query4_UpdateString = "";

            if (suppliersNotInDBSize > 0)
            {
                // Variables
                String values = "";
                int pos = 0;

                // Insert String
                query4_UpdateString = "INSERT INTO ingredientInShops (IngredientID, Volume_Per_Unit, Cost_Per_Unit, StoreID) VALUES";

                // Creating String Of Add Values
                for (AddShopForm_Object supplierAddToDB : suppliersNeedToBeAddedToDBList)
                {
                    int objectID = supplierAddToDB.getObjectID();

                    values += String.format("\n(%s, %s, %s, (SELECT StoreID FROM stores WHERE Store_Name = '%s'))",
                            ingredientIDIn, quantityPerPack.get(objectID).getText(),
                            prices.get(objectID).getText(), shopJComboBoxes.get(objectID).getSelectedItem().toString());

                    if (pos == suppliersNotInDBSize - 1)
                    {
                        values += ";";
                        continue;
                    }

                    values += ",";
                    pos++;
                }

                // Adding Both The Query & Values Together
                query4_UpdateString += values;

                //########################
                // Adding Update String
                //########################
                updates[listPos] = query4_UpdateString;
            }

            //############################################################
            // Return values
            //############################################################
            return updates;
        }

        //EDITING NOW
        public AddShopForm_Object addShopForm_object(Integer PDID) // Not an override method
        {
            EditAddShopForm_Object obj = new EditAddShopForm_Object(inputArea, PDID, true);
            addToContainer(inputArea, obj, 0, objectID, 1, 1, 0.25, 0.25, "horizontal", 0, 0);
            return obj;
        }

        public class EditAddShopForm_Object extends AddShopForm_Object
        {
            //#######################################
            // Main  Consturctor
            //#######################################
            EditAddShopForm_Object(Container parentContainer, Integer PDID, boolean addRow)
            {
                super(parentContainer, addRow);
                setPDID(PDID);
            }

            //#######################################
            // Inherited Constructor
            //#######################################
            EditAddShopForm_Object(Container parentContainer, boolean addRow)
            {
                super(parentContainer, addRow);
                System.out.printf("\n\nID: %s");
            }

            //#######################################
            // Delete Row
            //#######################################

            @Override
            protected void deleteRowAction()
            {
                //################################################
                // Confirm if the user wants to delete the shop
                //################################################
                String chosenShop = String.valueOf(shops_JComboBox.getSelectedItem());
                if (!(areYouSure(String.format("you want to permanently delete %s as a Supplier for this ingredient", chosenShop))))
                {
                    return;
                }
                //################################################
                // Get Ingredient PDID
                //################################################
                Integer PDID = getPDID();

                //################################################
                // If Object In DB Remove From DB
                //################################################
                if (PDID != null)
                {
                    //################################################
                    // Get Ingredient ID
                    //################################################
                    if (selectedIngredientID == null)
                    {
                        JOptionPane.showMessageDialog(mealPlanScreen, String.format("Unable to get selected ingredients information to delete the Supplier ' %s ' !", chosenShop));
                        return;
                    }

                    //###################################################
                    // Delete Supplier From ingredients_in_meal (PDID)
                    //###################################################
                    String updateQuery = String.format("""
                            UPDATE ingredients_in_meal
                            SET  PDID = NULL
                            WHERE PDID = %s; """, PDID);

                    System.out.printf("\n\n%s", updateQuery);

                    //###################################################
                    // Delete Supplier From ingredientInShops
                    //###################################################
                    String updateQuery2 = String.format("""
                            DELETE FROM ingredientInShops
                            WHERE PDID = %s; """, PDID);

                    System.out.printf("\n\n%s", updateQuery2);

                    //###################################################
                    // Update
                    //###################################################
                    if (!(db.uploadData_Batch_Altogether(new String[]{updateQuery, updateQuery2})))
                    {
                        JOptionPane.showMessageDialog(mealPlanScreen, String.format("Unable to remove the supplier ' %s ' from this ingredient!", chosenShop));
                        return;
                    }
                }

                //################################################
                // Remove Row Object
                //################################################
                removeFromParentContainer();
                addShopFormObjects.remove(this);

                //################################################
                // Remove Row Object
                //################################################
                JOptionPane.showMessageDialog(mealPlanScreen, String.format("Successfully, remove the  supplier ' %s ' from this ingredient!", chosenShop));
            }
        }
    }
}
