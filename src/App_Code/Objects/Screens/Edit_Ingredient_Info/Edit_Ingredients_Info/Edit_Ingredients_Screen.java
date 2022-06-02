package App_Code.Objects.Screens.Edit_Ingredient_Info.Edit_Ingredients_Info;

import java.text.Collator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Gui_Objects.*;
import App_Code.Objects.Screens.Edit_Ingredient_Info.Edit_Stores_And_Types.Edit_Ingredient_Stores_Screen;
import App_Code.Objects.Screens.Edit_Ingredient_Info.Edit_Stores_And_Types.Edit_Ingredients_Types_Screen;
import App_Code.Objects.Screens.Others.Meal_Plan_Screen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.HashMap;


public class Edit_Ingredients_Screen extends JFrame
{

    private Collection<String> all_IngredientsTypeNamesList, all_StoresNamesList;

    //#######################################
    // General Variables
    //#######################################

    private GridBagConstraints gbc = new GridBagConstraints();

    private int jFramewidth = 710, jFrameheight = 850;
    private Container contentPane;

    private MyJDBC db;
    private Integer temp_PlanID, planID;
    private String planName;
    private Meal_Plan_Screen mealPlanScreen;

    private boolean
            updateIngredientInfo = false;

    // Sorted Hashmap by key String
    private TreeMap<String, Collection<String>> map_ingredientTypesToIngredientNames = new TreeMap<String, Collection<String>>(new Comparator<String>()
    {
        public int compare(String o1, String o2)
        {
            return o1.toLowerCase().compareTo(o2.toLowerCase());
        }
    });

    CreateForm createForm;
    EditingCreateForm editingCreateForm;

    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Edit_Ingredients_Screen(MyJDBC db, Meal_Plan_Screen mealPlanScreen, int planID, int temp_PlanID, String planName)
    {
        this.db = db;
        this.mealPlanScreen = mealPlanScreen;

        this.planID = planID;
        this.temp_PlanID = temp_PlanID;

        this.planName = planName;
        try
        {
            if (db.isDatabaseConnected())
            {
                //Update Generic  ingredientsType JComboBox which has all the ingredients Types
                all_IngredientsTypeNamesList = db.getSingleColumnQuery_AlphabeticallyOrderedTreeSet("SELECT Ingredient_Type_Name FROM ingredientTypes;");

                if (all_IngredientsTypeNamesList == null)
                {
                    JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "\n\nUnable to get IngredientTypes for form!");
                    return;
                }

                all_StoresNamesList = db.getSingleColumnQuery_AlphabeticallyOrderedTreeSet("SELECT Store_Name FROM stores;");

                //###################################################################################
                // Frame Set-Up
                //###################################################################################

                setTitle("Add/Edit Ingredients Screen");
                makeJframeVisible();

                //Delete all temp data on close
                addWindowListener(new java.awt.event.WindowAdapter()
                {
                    @Override //HELLO Causes Error
                    public void windowClosing(WindowEvent windowEvent)
                    {
                        closeWindowEvent();
                        mealPlanScreen.updateIngredientsInfo(updateIngredientInfo);
                    }
                });

                //###################################################################################
                // Create ContentPane
                //###################################################################################

                contentPane = getContentPane();
                contentPane.setLayout(new GridLayout(1, 1));
                contentPane.setVisible(true);

                //##################################################################################
                // Creating TabbedPane
                //##################################################################################
                JTabbedPane tp = new JTabbedPane();
                contentPane.add(tp);


                //#################################################
                // Creating Add Ingredients Screen
                //#################################################
                JPanel addIngredientsFormJPanel = new JPanel(new GridBagLayout());
                tp.add("Add Ingredients", addIngredientsFormJPanel);

                createForm = new CreateForm();
                addToContainer(addIngredientsFormJPanel, createForm, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0);

                //#################################################
                // Creating Edit Ingredients Screen
                //##################################################
                JPanel editIngredientsFormJPanel = new JPanel(new GridBagLayout());
                tp.add("Edit Ingredients", editIngredientsFormJPanel);

                editingCreateForm = new EditingCreateForm();
                addToContainer(editIngredientsFormJPanel, editingCreateForm, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0);

                //#################################################
                // Creating Edit Ingredient Types Screen
                //##################################################
                JPanel editIngredientsTypesJPanel = new JPanel(new GridBagLayout());
                tp.add("Edit Ingredient Types", editIngredientsTypesJPanel);

                addToContainer(editIngredientsTypesJPanel, new Edit_Ingredients_Types_Screen(db, this, all_IngredientsTypeNamesList), 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0);

                //#################################################
                // Creating Edit Ingredients Stores Screen
                //##################################################
                JPanel editIngredientsStoreJPanel = new JPanel(new GridBagLayout());
                tp.add("Edit Ingredient Stores", editIngredientsStoreJPanel);


                addToContainer(editIngredientsStoreJPanel, new Edit_Ingredient_Stores_Screen(db, this, all_StoresNamesList), 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0);

            }
        }
        catch (Exception e)
        {

        }
    }

    //##################################################################################################################
    // Create Forms
    //##################################################################################################################

    public class EditingCreateForm extends CreateForm
    {
        private boolean jcomboUpdateStaus = false;
        private EditIngredientsForm ingredientsForm;
        private EditShopForm shopForm;
        private ArrayList<ShopForm.AddShopForm_Object> shopForm_objects = new ArrayList<>();

        private String selectedIngredientID, selectedIngredientName, selected_IngredientType_JComboItem;

        private String previousIngredientType, previousIngredientName;
        private ArrayList<Component> formObjects;

        private boolean ingredientEditable = true;

        private JComboBox<String>
                edit_IngredientName_JComboBox = new JComboBox(),
                edit_IngredientTypes_InPlan_JComboBox = new JComboBox();

        public EditingCreateForm()
        {
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
            SearchForIngredientsInfo searchForIngredientInfo = new SearchForIngredientsInfo(scrollPaneJPanel, ingredientsForm, "Search For Food Info", 250, 50);

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

            ((JLabel) edit_IngredientTypes_InPlan_JComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER); // centre text

            //###########################################
            //  Insert into JCombobox
            //###########################################

            updateIngredientNamesToTypesJComboBox(); // add all the ingredientTypes to the IngredientTypes JComboBox
            //################################################
            //  Actionlistener
            //#################################################

            edit_IngredientTypes_InPlan_JComboBox.addItemListener(new ItemListener()
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

            jp.add(edit_IngredientTypes_InPlan_JComboBox);
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

            ((JLabel) edit_IngredientName_JComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER); // centre text

            edit_IngredientName_JComboBox.addItemListener(new ItemListener()
            {
                public void itemStateChanged(ItemEvent ie)
                {
                    if (edit_IngredientName_JComboBox.getSelectedIndex() != -1)
                    {
                        updateFormWithIngredientInfo();
                    }
                }
            });

            jp.add(edit_IngredientName_JComboBox);
            jp.setPreferredSize(new Dimension(650, 50));

            addToContainer(scrollPaneJPanel, jp, 0, yPos += 1, 1, 1, 0.25, 0.25, "horizontal", 10, 0);

            //###########################
            //Space Divider
            //###########################
            addToContainer(scrollPaneJPanel, new JPanel(), 0, yPos += 1, 1, 1, 0.25, 0.25, "both", 10, 0);

            createForms(ingredientsForm, shopForm, searchForIngredientInfo);
        }

        public JComboBox<String> getEdit_IngredientTypes_InPlan_JComboBox()
        {
            return edit_IngredientTypes_InPlan_JComboBox;
        }

        @Override
        protected void updateIngredientForm_Type_JComboBox()
        {
            ingredientsForm.loadJCOmboBox();
        }

        public void updateMapIngredientsTypesAndNames()
        {
            //###########################################################
            // Clear List
            //###########################################################
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

        private boolean getUpdatingJComboBoxStatus()// can't be deleted, trust me
        {
            return jcomboUpdateStaus;
        }

        /*
      When the variable its used to set is true the ingredientNames JComboBox is updating
      this stops the ingredientNames JComboBox from triggering actionListener events!
     */
        private void setUpdatingJComboBoxStatus(boolean x) // can't be deleted, trust me
        {
            jcomboUpdateStaus = x;
        }

        public void updateIngredientNamesToTypesJComboBox()
        {
            edit_IngredientTypes_InPlan_JComboBox.removeAllItems();

            for (String key : map_ingredientTypesToIngredientNames.keySet())
            {
                if (!key.equals("None Of The Above"))
                {
                    edit_IngredientTypes_InPlan_JComboBox.addItem(key);
                }
            }

            setNothingSelectedIngredientTypeJCombo(); // set selected item to nothing
        }

        /*
           Resets Ingredient Type JComboBox
        */
        private void setNothingSelectedIngredientTypeJCombo()
        {
            edit_IngredientTypes_InPlan_JComboBox.setSelectedIndex(-1);
        }

        private void clearIngredientTypeJCombo()
        {
            edit_IngredientTypes_InPlan_JComboBox.removeAllItems();
        }

        private void updateIngredientNameJComboBox()
        {
            //##################################
            // Populating IngredientName JComBox
            //##################################
            setUpdatingJComboBoxStatus(true); // stops ingredientName JComboBox from triggering any  actionListener events

            for (String item : map_ingredientTypesToIngredientNames.get(selected_IngredientType_JComboItem))
            {
                if (!item.equals("None Of The Above"))
                {
                    edit_IngredientName_JComboBox.addItem(item);
                }
            }

            setUpdatingJComboBoxStatus(false);
            setNothingSelectedIngredientNameJCombo();// set selected item to nothing
        }

        /*
            Resets Ingredient Type JComboBox
        */
        private void setNothingSelectedIngredientNameJCombo()
        {
            edit_IngredientName_JComboBox.setSelectedIndex(-1);
        }

        private void clearIngredientNameJCombo()
        {
            edit_IngredientName_JComboBox.removeAllItems();
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

        protected ArrayList<ShopForm.AddShopForm_Object> getShopForm_objects()
        {
            return shopForm_objects;
        }

        private void updateFormWithIngredientInfo()
        {
            if (getUpdatingJComboBoxStatus())
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

            selectedIngredientName = selectedIngredientName;
            previousIngredientName = selectedIngredientName;

            //############################################################
            // Update IngredientsForm
            //############################################################
            formObjects = ingredientsForm.getIngredientsFormObjects();

            //##############################
            // Get Ingredient Info
            //##############################
            ArrayList<ArrayList<String>> ingredientInfo_R = db.getMultiColumnQuery(String.format("""
                    SELECT  info.IngredientID, info.Meassurement, info.Ingredient_Name,
                                        
                    (SELECT t.Ingredient_Type_Name FROM ingredientTypes t WHERE t.Ingredient_Type_ID = info.Ingredient_Type_ID)  AS Ingredient_Type,
                                        
                    info.Based_On_Quantity, info.Protein, info.Glycemtric_Index, info.Carbohydrates, info.Sugars_Of_Carbs, info.Fibre, info.Fat, info.Saturated_Fat, info.Salt,
                    info.Water_Content, info.Calories
                    							
                    FROM ingredients_info info
                    WHERE info.Ingredient_Name = '%s';""", selectedIngredientName));

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
                if (formObjectsIndex == getIngredientTypeObjectIndex()) // accounting for id being added
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
            ArrayList<ShopForm.AddShopForm_Object> shopForm_objects = getShopForm_objects();
            for (int i = 0; i < ingredientShops_R.size(); i++)
            {
                ArrayList<String> rowData = ingredientShops_R.get(i);

                // Set PDID & Add Row
                ShopForm.AddShopForm_Object row = shopForm.addShopForm_object(Integer.parseInt(rowData.get(0)));
                shopForm_objects.add(row);

                // Set ShopName
                row.getShops_JComboBox().setSelectedItem(rowData.get(1));

                // Set Cost Info
                row.getIngredientPrice_TxtField().setText(rowData.get(2));

                // Set Volume Info
                row.getQuantityPerPack_TxtField().setText(rowData.get(3));
            }
        }

        private void deleteIngredientBTNAction()
        {
            if (edit_IngredientName_JComboBox.getSelectedIndex() != -1)
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
                        updateIngredientInfo = true;
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
                Object selectedItem = edit_IngredientName_JComboBox.getSelectedItem();

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
            if (edit_IngredientName_JComboBox.getSelectedItem().equals("N/A"))
            {
                JOptionPane.showMessageDialog(mealPlanScreen, "The Store N/A cannot be edited, its a placeholder");
                refreshInterface(true, true);
                return;
            }

            if (areYouSure("update this Ingredients information - this will cause the mealPlan to save its data to the DB"))
            {
                boolean errorFound = false;

                // ingredientsForm
                if (!(ingredientsForm.validate_IngredientsForm(false)))
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
                    super.updateShops = false;
                    super.updateIngredientsForm = false; // reset values

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
                        String currentIngredientName = ((JTextField) formObjects.get(getIngredientNameObjectIndex())).getText().trim();
                        String currentIngredientType = ((JComboBox) formObjects.get(getIngredientTypeObjectIndex())).getSelectedItem().toString();

                        //HELLO REMOVE
                        System.out.printf("\n\nIngredientName \nCurrent = '%s' \nPrevious = '%s' \n\nIngredientType \nCurrent = '%s' \nPrevious = '%s'",
                                currentIngredientName, previousIngredientName, previousIngredientType, currentIngredientType);

                        if ((!currentIngredientName.equals(previousIngredientName) || (!currentIngredientType.equals(previousIngredientType))))
                        {
                            updateIngredientInfo = true;
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
            // Error forming update String (exit)
            //####################################

            if (!isUpdateShops() || !isUpdateIngredientsForm())
            {
                return false;
            }

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
            protected boolean validate_IngredientsForm(boolean checkIfItemIsInDB)// HELLO Modify
            {
                if (temp_PlanID == null && planID == null && planName == null)
                {
                    JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "Please Select A Plan First!");
                    return false;
                }

                String errorTxt = "";


                //##############################
                // Validation JTextFields
                //##############################
                for (int row = 0; row < ingredientsFormObjects.size(); row++)
                {
                    String value = "";
                    Component comp = ingredientsFormObjects.get(row);

                    if (comp instanceof JComboBox)
                    {
                        JComboBox comboBox = (JComboBox) comp;

                        if (comboBox.getSelectedIndex() == -1) // if no item has been selected by JComboBox
                        {
                            errorTxt += String.format("\n\n  ' %s ' on Row: %s, an option inside the dropdown menu must be selected", labels[row], row + 1);
                        }
                        continue;
                    }
                    else if (comp instanceof JTextField)
                    {
                        JTextField jTextField = (JTextField) comp;
                        value = jTextField.getText().trim();


                        //#########################################
                        // Check if JTextfield input is empty
                        //#########################################
                        if (value.equals(""))
                        {
                            errorTxt += String.format("\n\n  ' %s ' on Row: %s,  must have a value which is not ' NULL '!", labels[row], row + 1);
                            continue;
                        }

                        //#######################################
		                /*
		                   Check if the ingredientName contains any
		                   numbers or, characters
		                */
                        //#########################################

                        if (row == getIngredientNameObjectIndex())
                        {
                            String ingredientName_Txt = ((JTextField) comp).getText();

                            if (doesStringContainCharacters(ingredientName_Txt))
                            {
                                errorTxt += String.format("\n\n  Ingredient named %s can only contain alphabet character! Symbols, numbers aren't allowed in the ingredient name!", ingredientName_Txt);
                            }
                            continue;
                        }

                        //#########################################
                        // Do BigDecimal Processing
                        //#########################################
                        errorTxt = convertToBigDecimal(value, errorTxt, labels[row], row + 1, jTextField);
                    }
                }

                //####################################################
                //Check if IngredientName Already exists in DB
                //####################################################

                JTextField ingredientName_JTxtF = (JTextField) ingredientsFormObjects.get(getIngredientNameObjectIndex());
                String ingredientName_Txt = ingredientName_JTxtF.getText().trim();

                if (!(ingredientName_Txt.equals("")))
                {
                    ingredientName_Txt = removeSpaceAndHiddenChars(ingredientName_Txt);
                    if (doesStringContainCharacters(ingredientName_Txt))
                    {
                        errorTxt += String.format("\n\n  Ingredient named %s can only contain alphabet character! Symbols, numbers aren't allowed in the ingredient name!", ingredientName_Txt);
                    }
                    else
                    {
                        String query = String.format("SELECT Ingredient_Name FROM ingredients_info WHERE Ingredient_Name = '%s' AND IngredientID != %s;", ingredientName_Txt, selectedIngredientID);

                        if (db.getSingleColumnQuery(query) != null)
                        {
                            errorTxt += String.format("\n\n  Ingredient named %s already exists within the database!", ingredientName_Txt);
                        }
                    }
                }

                //####################################################
                //Check if any error were found & Process it
                //####################################################

                if (errorTxt.length() == 0)
                {
                    return true;
                }

                JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), String.format("\n\nPlease fix the following rows being; \n%s", errorTxt));

                return false;
            }

            private String get_IngredientsForm_UpdateString(String ingreientID) // HELLO needs further update methods created for gui
            {
                //####################################
                // Get Current ID
                //####################################

                JTextField ingredientName_JTxtF = (JTextField) ingredientsFormObjects.get(getIngredientNameObjectIndex());
                String ingredientName_Txt = ingredientName_JTxtF.getText().trim();

                //####################################
                // Gathering Form Txt Data
                //####################################
                ArrayList<String> formResults = new ArrayList<>();
                ArrayList<Component> ingredientsFormObjects = super.getIngredientsFormObjects();

                int pos = 0;
                for (Component comp : ingredientsFormObjects)
                {
                    if (comp instanceof JTextField)
                    {
                        String fieldText = ((JTextField) comp).getText();
                        if (pos == getIngredientNameObjectIndex())
                        {
                            fieldText = removeSpaceAndHiddenChars(fieldText);
                        }
                        formResults.add(fieldText);
                    }
                    else if (comp instanceof JComboBox)
                    {
                        formResults.add(((JComboBox) comp).getSelectedItem().toString());
                    }
                    pos++;
                }

                //####################################
                // Creating Upload Query
                //####################################
                int i = 0;
                String updateTargets_Query = String.format("""
                                UPDATE ingredients_info 
                                SET  
                                Meassurement = '%s', Ingredient_Name = '%s', 
                                Ingredient_Type_ID = (SELECT Ingredient_Type_ID FROM ingredientTypes WHERE Ingredient_Type_Name = '%s'),                                
                                                                
                                Based_On_Quantity = %s, 
                                Protein = %s, Glycemtric_Index = %s, Carbohydrates = %s, Sugars_Of_Carbs = %s, Fibre = %s, Fat = %s, Saturated_Fat = %s,
                                Salt = %s, Water_Content = %s, Calories = %s
                                WHERE IngredientID = %s; """,
                        formResults.get(i), formResults.get(i += 1), formResults.get(i += 1), formResults.get(i += 1), formResults.get(i += 1),
                        formResults.get(i += 1), formResults.get(i += 1), formResults.get(i += 1), formResults.get(i += 1), formResults.get(i += 1),
                        formResults.get(i += 1), formResults.get(i += 1), formResults.get(i += 1), formResults.get(i += 1), ingreientID);

                //####################################
                // Return results
                //####################################
                setUpdateIngredientsForm(true);
                return updateTargets_Query;
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

            //EDITING NOW
            public String[] get_ShopForm_UpdateString(String IngredientIDIn) // Not an override method
            {
                //#############################################################
                // Checks if there is anything to update before, updating
                //############################################################
                if (rowsInTable.size() == 0)
                {
                    setUpdateShops(true);
                    return null;
                }

                //################################################################
                // Adding Shops To Categories To Either Insert Or Update into DB
                //################################################################
                ArrayList<AddShopForm_Object> suppliersInDBList = new ArrayList<>();
                ArrayList<AddShopForm_Object> suppliersNeedToBeAddedToDBList = new ArrayList<>();

                for (AddShopForm_Object shopForm_object : rowsInTable)
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
                    query4_UpdateString = "INSERT INTO ingredientInShops (IngredientID, Volume_Per_Unit, Cost_Per_Unit, Store_Name) VALUES";

                    // Creating String Of Add Values
                    for (AddShopForm_Object supplierAddToDB : suppliersNeedToBeAddedToDBList)
                    {
                        int objectID = supplierAddToDB.getObjectID();

                        values += String.format("\n(%s, %s, %s, '%s')", IngredientIDIn, quantityPerPack.get(objectID).getText(),
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
                setUpdateShops(true);
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
                    rowsInTable.remove(this);

                    //################################################
                    // Remove Row Object
                    //################################################
                    JOptionPane.showMessageDialog(mealPlanScreen, String.format("Successfully, remove the  supplier ' %s ' from this ingredient!", chosenShop));
                }
            }
        }
    }

    public class CreateForm extends JPanel
    {
        protected int yPos = 0;
        protected JPanel scrollPaneJPanel;

        private int ingredientNameObjectIndex = 1, ingredientTypeObjectIndex = 2, glycemicObjectIndex = 5;

        private boolean formEditable = false, updateIngredientsForm = false, updateShops = false;

        private int totalNumbersAllowed = 7, decimalScale = 2, decimalPrecision = totalNumbersAllowed - decimalScale, charlimit = 8;

        protected IngredientsForm ingredientsForm;
        private ShopForm shopForm;
        //#################################################################################################################
        // Constructor
        //##################################################################################################################

        CreateForm()
        {
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
//            ingredientsForm = new IngredientsForm(this, "Add Ingredients Info", 250, 50);// HELLO REMOVE
            ingredientsForm = new IngredientsForm(scrollPaneJPanel, "Add Ingredients Info", 250, 50);

            shopForm = new ShopForm(scrollPaneJPanel, "Add Ingredient Suppliers", 250, 50);

            SearchForIngredientsInfo searchForIngredientInfo = new SearchForIngredientsInfo(scrollPaneJPanel, ingredientsForm, "Search For Food Info", 250, 50);

            createForms(ingredientsForm, shopForm, searchForIngredientInfo);
        }

        //####################################################
        // Methods
        //###################################################

        protected void updateIngredientForm_Type_JComboBox()
        {
            ingredientsForm.loadJCOmboBox();
        }

        protected void createForms(IngredientsForm ingredientsForm, ShopForm shopForm, SearchForIngredientsInfo searchForIngredientsInfo)
        {
            //##################################################################################
            // Creating Parts of screen & adding it to interface
            //##################################################################################

            //###########################
            //Search For Ingredients form
            //###########################

            addToContainer(scrollPaneJPanel, searchForIngredientsInfo, 0, yPos += 1, 1, 1, 0.25, 0.25, "both", 0, 0);

            //###########################
            //Space Divider
            //###########################
            addToContainer(scrollPaneJPanel, new JPanel(), 0, yPos += 1, 1, 1, 0.25, 0.25, "both", 10, 0);

            //###########################
            //Ingredients form
            //###########################

            addToContainer(scrollPaneJPanel, ingredientsForm, 0, yPos += 1, 1, 1, 0.25, 0.25, "both", 0, 0);

            //###########################
            //Space Divider
            //###########################
            addToContainer(scrollPaneJPanel, new JPanel(), 0, yPos += 1, 1, 1, 0.25, 0.25, "both", 10, 0);

            //###########################
            // Add shop
            //###########################
            addToContainer(scrollPaneJPanel, shopForm, 0, yPos += 1, 1, 1, 0.25, 0.25, "both", 0, 0);

            //###########################
            //Space Divider
            //###########################
            addToContainer(scrollPaneJPanel, new JPanel(), 0, yPos += 1, 1, 1, 0.25, 0.25, "both", 10, 0);

            //###################################################################################
            // South Screen for Interface
            //###################################################################################

            // Creating submit button
            JButton submitButton = new JButton("Submit Form");
            submitButton.setFont(new Font("Arial", Font.BOLD, 14)); // setting font
            submitButton.setPreferredSize(new Dimension(50, 50)); // width, height

            // creating commands for submit button to execute on
            submitButton.addActionListener(ae -> {
                submissionBtnAction();
            });

            add(submitButton, BorderLayout.SOUTH);

            //###################################################################################
            // Resizing GUI
            //###################################################################################
            resize_GUI();
        }

        protected void clearShopForm()
        {
            shopForm.clearShopForm();
        }

        protected void clearIngredientsForm()
        {
            ingredientsForm.clearIngredientsForm();
        }

        protected void submissionBtnAction()
        {
            if (!areYouSure("add this new Ingredient - this will cause the mealPlan to save its data to the DB"))
            {
                return;
            }

            boolean errorFound = false;

            // ingredientsForm
            if (!(ingredientsForm.validate_IngredientsForm(true)))
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
                updateShops = false;
                updateIngredientsForm = false; // reset values

                if (!areYouSure("upload these values as they may have been changed / adapted to fit our data type format"))
                {
                    return;
                }

                if (updateBothForms(ingredientsForm.get_IngredientsForm_UpdateString(), shopForm.get_ShopForm_UpdateString()))
                {
                    updateIngredientInfo = true;
                    JOptionPane.showMessageDialog(mealPlanScreen, "The ingredient updates won't appear on the mealPlan screen until this window is closed!");

                    //#####################################
                    // Reset Ingredient Names/Types
                    //####################################
                    String ingredientName = ((JTextField) ingredientsForm.getIngredientsFormObjects().get(1)).getText();
                    String ingredientType = (String) ((JComboBox) ingredientsForm.getIngredientsFormObjects().get(2)).getSelectedItem();

                    addOrDeleteIngredientFromMap("add", ingredientType, ingredientName);

                    //#####################################
                    // Reset Form & Update GUI
                    //####################################
                    refreshInterface();
                    resize_GUI();
                }
            }
        }

        protected boolean isUpdateIngredientsForm()
        {
            return updateIngredientsForm;
        }

        protected void setUpdateIngredientsForm(boolean x)
        {
            updateIngredientsForm = x;
        }

        protected void setUpdateShops(boolean x)
        {
            updateShops = x;
        }

        protected boolean isUpdateShops()
        {
            return updateShops;
        }

        protected int getIngredientNameObjectIndex()
        {
            return ingredientNameObjectIndex;
        }

        protected int getIngredientTypeObjectIndex()
        {
            return ingredientTypeObjectIndex;
        }

        //HELLO EDIT
        protected boolean addOrDeleteIngredientFromMap(String process, String ingredientType, String ingredientName)
        {
            // Storing
            Collection<String> ingredientTypeList = map_ingredientTypesToIngredientNames.get(ingredientType);
            if (process.equals("add"))// if key exists add the ingredientName in
            {

                if (ingredientTypeList != null)
                {
                    // Add ingredientName to collection
                    ingredientTypeList.add(ingredientName);
                }
                else // create the list for the new type and add ingredient in
                {
                    ingredientTypeList = new TreeSet<String>(Collator.getInstance());
                    ingredientTypeList.add(ingredientName);
                    map_ingredientTypesToIngredientNames.put(ingredientType, ingredientTypeList);
                }
            }
            else if (process.equals("delete"))
            {
                ingredientTypeList.remove(ingredientName);

                // Remove List as there is no items in it
                if (ingredientTypeList.size() == 0)
                {
                    map_ingredientTypesToIngredientNames.remove(ingredientType);
                }
            }

            // Redraw ingredientsTypes
            JComboBox<String> edit_IngredientTypes_InPlan_JComboBox = getEdit_IngredientTypes_InPlan_JComboBox();

            edit_IngredientTypes_InPlan_JComboBox.removeAllItems(); // clearList
            for (String key : map_ingredientTypesToIngredientNames.keySet())
            {
                if (!key.equals("None Of The Above"))
                {
                    edit_IngredientTypes_InPlan_JComboBox.addItem(key);
                }
            }
            return true;
        }

        private void refreshInterface() // only available to reset screen
        {
            clearIngredientsForm();
            clearShopForm();
        }

        protected boolean updateBothForms(String updateIngredients_String, String[] updateIngredientShops_String)
        {
            System.out.printf("\n\n%s", updateIngredients_String, Arrays.toString(updateIngredientShops_String));

            //####################################
            // Error forming update String (exit)
            //####################################

            if (!updateShops || !updateIngredientsForm)
            {
                return false;
            }

            //####################################
            // Uploading Query
            //####################################
            if (!(db.uploadData_Batch_Altogether(new String[]{updateIngredients_String})))
            {
                JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "Failed 2/2 Updates - Unable To Add Ingredient Info In DB!");
                return false;
            }

            if (updateIngredientShops_String != null)
            {
                if (!(db.uploadData_Batch_Independently(updateIngredientShops_String)))
                {
                    JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "Failed 1/2 Updates - Unable To Add Shop Supplier For Ingredient In DB!");
                    return false;
                }
            }

            JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "\n\nUpdated Ingredient Info! \n\nAlso updated 2/2 Shop Info In DB In DB!!!");

            return true;
        }

        private void resize_GUI()
        {
            contentPane.revalidate();
            scrollPaneJPanel.revalidate();
            revalidate();
        }

        protected String convertToBigDecimal(String value, String errorTxt, String rowlabel, int rowNumber, JTextField jTextField)
        {
            String txt = String.format("must be number which has %s numbers in it! Or, a decimal number (%s,%s) with a max of %s numbers before the decimal point and  a of max of  %s numbers after the decimal point!",
                    decimalPrecision, decimalPrecision, decimalScale, decimalPrecision, decimalScale);
            try
            {
                BigDecimal zero = new BigDecimal(0);

                //#####################################################
                // Convert Numbers Using Precision & Scale point Values
                //#####################################################
                BigDecimal bdFromString = new BigDecimal(String.format("%s", value));
                int valueScale = bdFromString.scale();
                int valuePrecision = bdFromString.precision();

                if (valueScale > decimalScale) // only round to scale, if needed as otherwise whole numbers get lost etc 5566 = nothing
                {
                    bdFromString = bdFromString.setScale(decimalScale, RoundingMode.DOWN); // round the number
                }

                //#####################################################
                // Java Concept Of Precision
                //#####################################################
                if (valueScale == 0 && valuePrecision > decimalPrecision) // the number is too big
                {
                    errorTxt += String.format("\n\n  ' %s 'on Row: %s, %s ", rowlabel, rowNumber, txt);
                }

                //#####################################################
                // MySQL Concept Of Precision
                //#####################################################
                else if (valueScale > 0 && bdFromString.setScale(0, RoundingMode.FLOOR).precision() > decimalPrecision)
                {
                    errorTxt += String.format("\n\n  ' %s 'on Row: %s, %s ", rowlabel, rowNumber, txt);
                }

                //#####################################################
                // Format Data in Cell
                //#####################################################
                jTextField.setText(String.format("%s", bdFromString));

                //#####################################################
                // Check if the value is bigger than 0
                //#####################################################

                if (bdFromString.compareTo(zero) < 0)// "<")
                {
                    errorTxt += String.format("\n\n  ' %s 'on Row: %s, must have a value which is bigger than 0 and %s", rowlabel, rowNumber, txt);
                }
            }
            catch (Exception e)
            {
                errorTxt += String.format("\n\n  ' %s 'on Row: %s, %s ", rowlabel, rowNumber, txt);
            }

            return errorTxt;
        }

        protected boolean doesStringContainCharacters(String input)
        {
            Pattern p1 = Pattern.compile("[^a-zA-Z]", Pattern.CASE_INSENSITIVE);
            Matcher m1 = p1.matcher(input.replaceAll("\\s+", ""));
            boolean b1 = m1.find();

            if (b1)
            {
                return true;
            }

            return false;
        }

        protected Boolean areYouSure(String process)
        {
            int reply = JOptionPane.showConfirmDialog(mealPlanScreen, String.format("Are you sure you want to: %s?", process, process),
                    "Confirmation", JOptionPane.YES_NO_OPTION); //HELLO Edit

            if (reply == JOptionPane.NO_OPTION || reply == JOptionPane.CLOSED_OPTION)
            {
                return false;
            }
            return true;
        }


        //#################################################################################################################
        // IngredientsForm
        //##################################################################################################################

        public class SearchForIngredientsInfo extends CollapsibleJPanel
        {

            public SearchForIngredientsInfo(Container parentContainer, IngredientsForm ingredientsForm, String btnText, int btnWidth, int btnHeight)
            {
                super(parentContainer, btnText, btnWidth, btnHeight);
                expandJPanel();
            }
        }

        //#################################################################################################################
        // IngredientsForm
        //##################################################################################################################
        public class IngredientsForm extends CollapsibleJPanel
        {
            //#######################################
            // Ingredients form Variables
            //#######################################

            protected final String[] labels = {"Ingredient Measurement In:", "Ingredient Name:", "Ingredient_Type:",
                    "Based_On_Quantity:", "Protein:", "Glycemic Index", "Carbohydrates:", "Sugars Of Carbs:", "Fibre:", "Fat:", "Saturated Fat:",
                    "Salt:", "Water_Content:", "Calories:"};

            protected JComboBox ingredientsMeasure_JComboBox = new JComboBox();
            protected ArrayList<Component> ingredientsFormObjects = new ArrayList<>();

            private JPanel northPanel = new JPanel(new GridBagLayout());
            private JComboBox<String> ingredientsType_JComboBox = new JComboBox();

            public IngredientsForm(Container parentContainer, String btnText, int btnWidth, int btnHeight)
            {
                super(parentContainer, btnText, btnWidth, btnHeight);
                createIngredientsForm();
                expandJPanel();
            }

            private void createIngredientsForm()
            {
                JPanel mainJPanel = getCentreJPanel();
                mainJPanel.setLayout(new BorderLayout());

                //###################################################################
                // North Frame
                //###################################################################

                // Creating North JPanel Area with 2 rows
                mainJPanel.add(northPanel, BorderLayout.NORTH);

                //#####################################################
                // Creating area for North JPanel (title area)
                //#####################################################
                JLabel titleLabel = new JLabel("Add Ingredient");
                titleLabel.setFont(new Font("Verdana", Font.PLAIN, 24));
                titleLabel.setHorizontalAlignment(JLabel.CENTER);

                JPanel titlePanel = new JPanel();
                titlePanel.setBackground(Color.green);
                titlePanel.add(titleLabel);

                // Add title JPanel to North Panel Area
                addToContainer(northPanel, titlePanel, 0, 2, 1, 1, 0.25, 0.25, "both", 0, 0);

                //#################################################################
                // Create Icon Bar
                //#################################################################
                createIconBar();

                //#################################################################
                // Centre Frame
                //#################################################################
                JPanel inputArea = new JPanel(new GridLayout(labels.length, 2));

                // for each label it is created into a JLabel
                for (int i = 0; i < labels.length; i++)
                {
                    boolean jcomboxBeingCreated = false;
                    JTextField textField = new JTextField("");
                    JComboBox comboBox = null;

                    //#########################################
                    // JLabel
                    //#########################################

                    String labelTXT = labels[i];

                    JLabel label = new JLabel("    " + labelTXT);
                    label.setHorizontalAlignment(JLabel.LEFT);
                    label.setFont(new Font("Verdana", Font.BOLD, 14));
                    inputArea.add(label);

                    //#########################################
                    // JComboField
                    //#########################################

                    if (labelTXT.equals("Ingredient Measurement In:"))
                    {
                        String ingredientMeassurements[] = {"Litres", "Grams"};
                        ingredientsMeasure_JComboBox = new JComboBox(ingredientMeassurements);
                        inputArea.add(ingredientsMeasure_JComboBox);

                        jcomboxBeingCreated = true;
                        comboBox = ingredientsMeasure_JComboBox;
                    }
                    else if (labelTXT.equals("Ingredient_Type:"))
                    {
                        ingredientsType_JComboBox = new JComboBox();
                        loadJCOmboBox();
                        inputArea.add(ingredientsType_JComboBox);

                        jcomboxBeingCreated = true;
                        comboBox = ingredientsType_JComboBox;
                    }

                    // if a JComboBox is being created Centre JComboBox Items & Set Selected Item to 0
                    if (jcomboxBeingCreated && comboBox != null)
                    {
                        // Centre JComboBox Item
                        DefaultListCellRenderer listRenderer = new DefaultListCellRenderer();
                        listRenderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER); // center-aligned items
                        comboBox.setRenderer(listRenderer);

                        // Set Selected Item To Nothing
                        comboBox.setSelectedIndex(-1);

                        ingredientsFormObjects.add(comboBox);

                        continue;
                    }

                    //#########################################
                    // JTextfields
                    //#########################################
                    if (labelTXT.equals("Ingredient Name:")) //Setting TextField limits
                    {
                        textField.setDocument(new JTextFieldLimit(255));
                        ingredientNameObjectIndex = i;
                    }
                    else
                    {
                        textField.setDocument(new JTextFieldLimit(charlimit));
                    }

                    ingredientsFormObjects.add(textField);
                    inputArea.add(textField);
                }
                mainJPanel.add(inputArea, BorderLayout.CENTER);
            }

            protected void loadJCOmboBox()
            {
                ingredientsType_JComboBox.removeAllItems();
                for (String ingredientType : all_IngredientsTypeNamesList)
                {
                    if (ingredientType.equals("None Of The Above"))
                    {
                        continue;
                    }

                    ingredientsType_JComboBox.addItem(ingredientType);
                }

                ingredientsType_JComboBox.setSelectedIndex(-1);
            }

            protected void createIconBar()
            {
                createIconBarOnGUI(true);
            }

            protected void createIconBarOnGUI(boolean createIconBar)
            {
                //#####################################################
                // Exit Clause
                //#####################################################
                if (!(createIconBar))
                {
                    return;
                }

                //#####################################################
                // Creating area for North JPanel (Refresh Icon)
                //#####################################################

                JPanel iconArea = new JPanel(new GridBagLayout());
                addToContainer(northPanel, iconArea, 0, 1, 1, 1, 0.25, 0.25, "both", 0, 0);

                IconPanel iconPanel = new IconPanel(1, 10, "East");
                JPanel iconPanelInsert = iconPanel.getIconJpanel();

                addToContainer(iconArea, iconPanel.getIconAreaPanel(), 0, 0, 1, 1, 0.25, 0.25, "horizontal", 10, 0);

                //##########################
                // Refresh Icon
                //##########################
                int width = 30;
                int height = 30;

                IconButton refresh_Icon_Btn = new IconButton("src/images/refresh/++refresh.png", "", width, height, width, height,
                        "centre", "right"); // btn text is useless here , refactor

                JButton refresh_Btn = refresh_Icon_Btn.returnJButton();
                refresh_Icon_Btn.makeBTntransparent();

                refresh_Btn.addActionListener(ae -> {

                    clearIngredientsForm();
                });

                iconPanelInsert.add(refresh_Icon_Btn);

            }

            protected void clearIngredientsForm()
            {
                for (int i = 0; i < ingredientsFormObjects.size(); i++)
                {
                    Component comp = ingredientsFormObjects.get(i);

                    if (comp instanceof JComboBox)
                    {
                        ((JComboBox<?>) comp).setSelectedIndex(-1);
                    }
                    else if (comp instanceof JTextField)
                    {
                        ((JTextField) comp).setText("");
                    }
                }
            }

            protected boolean validate_IngredientsForm(boolean checkIfItemIsInDB)// HELLO Modify
            {
                if (temp_PlanID == null && planID == null && planName == null)
                {
                    JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "Please Select A Plan First!");
                    return false;
                }

                String errorTxt = "";

                //##############################
                // Validation JTextFields
                //##############################
                String ingredientName_Txt = "";

                int listSize = ingredientsFormObjects.size();
                for (int row = 0; row < listSize; row++)
                {
                    String value = "";
                    Component comp = ingredientsFormObjects.get(row);

                    if (comp instanceof JComboBox)
                    {
                        JComboBox comboBox = (JComboBox) comp;

                        if (comboBox.getSelectedIndex() == -1) // if no item has been selected by JComboBox
                        {
                            errorTxt += String.format("\n\n  ' %s ' on Row: %s, an option inside the dropdown menu must be selected", labels[row], row + 1);
                        }
                        continue;
                    }
                    else if (comp instanceof JTextField)
                    {
                        JTextField jTextField = (JTextField) comp;
                        value = jTextField.getText().trim();

                        //#########################################
                        // Check if JTextfield input is empty
                        //#########################################
                        if (value.equals(""))
                        {
                            errorTxt += String.format("\n\n  ' %s ' on Row: %s,  must have a value which is not ' NULL '!", labels[row], row + 1);
                            continue;
                        }

                        //#######################################
                        /*
                         Check if the ingredientName contains any
                         numbers or, characters
                         */
                        //#########################################

                        if (row == ingredientNameObjectIndex)
                        {
                            System.out.printf("\n\nIngredientName value: ''%s''", value);
                            ingredientName_Txt = value;
                            if (doesStringContainCharacters(value))
                            {
                                errorTxt += String.format("\n\n  ' %s ' on Row: %s,  can only contain alphabet character! Symbols, numbers aren't allowed in the ingredient name!", labels[row], row + 1, value);
                            }
                            continue;
                        }

                        if (row == glycemicObjectIndex)
                        {
                            Integer intValue = Integer.valueOf(value);

                            if (intValue > 100 || intValue < 0)
                            {
                                errorTxt += String.format("\n\n  ' %s ' on Row: %s,  must be an Integer value which is: 0<= value <=100 !", labels[row], row + 1);
                            }
                            continue;
                        }

                        //#########################################
                        // Do BigDecimal Processing
                        //#########################################
                        errorTxt = convertToBigDecimal(value, errorTxt, labels[row], row + 1, jTextField);
                    }
                }

                //####################################################
                //Check if IngredientName Already exists in DB
                //####################################################
                if (checkIfItemIsInDB && !(ingredientName_Txt.equals("")))
                {
                    ingredientName_Txt = removeSpaceAndHiddenChars(ingredientName_Txt);
                    String query = String.format("SELECT Ingredient_Name FROM ingredients_info WHERE Ingredient_Name = '%s';", ingredientName_Txt);

                    if (db.getSingleColumnQuery(query) != null)
                    {
                        errorTxt += String.format("\n\n  Ingredient named %s already exists within the database!", ingredientName_Txt);
                    }
                }

                //####################################################
                //Check if any error were found & Process it
                //####################################################

                if (errorTxt.length() == 0)
                {
                    return true;
                }

                JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), String.format("\n\nPlease fix the following rows being; \n%s", errorTxt));

                return false;
            }

            private String get_IngredientsForm_UpdateString() // HELLO needs further update methods created for gui
            {
                //####################################
                // Gathering Form Txt Data
                //####################################
                ArrayList<String> formResults = new ArrayList<>();
                int pos = 0;
                for (Component comp : ingredientsFormObjects)
                {
                    if (comp instanceof JTextField)
                    {
                        String fieldText = ((JTextField) comp).getText(); // CHECKA
                        if (pos == ingredientNameObjectIndex || pos == ingredientTypeObjectIndex)
                        {
                            fieldText = removeSpaceAndHiddenChars(fieldText);
                        }

                        formResults.add(fieldText);
                    }
                    else if (comp instanceof JComboBox)
                    {
                        formResults.add(((JComboBox) comp).getSelectedItem().toString());
                    }
                    pos++;
                }

                //####################################
                // Creating Upload Query
                //####################################
                int i = 0;

                String ingredientTypeSet = "SELECT Ingredient_Type_ID FROM ingredientTypes WHERE Ingredient_Type_Name = '";

                String updateTargets_Query = String.format("""
                                INSERT INTO ingredients_info
                                (Meassurement, Ingredient_Name, Ingredient_Type_ID, Based_On_Quantity, 
                                Protein, Glycemtric_Index, Carbohydrates, Sugars_Of_Carbs, Fibre, Fat, Saturated_Fat, Salt, Water_Content, Calories)   
                                                                                 
                                Values 
                                (                                 
                                  ('%s'), 
                                  ('%s'), 
                                  (%s), 
                                  (%s),
                                  (%s), 
                                  (%s), 
                                  (%s), 
                                  (%s), 
                                  (%s), 
                                  (%s), 
                                  (%s), 
                                  (%s), 
                                  (%s), 
                                  (%s)
                                ); """,
                        formResults.get(i), formResults.get(i += 1),

                        ingredientTypeSet += formResults.get(i += 1) + "'",

                        formResults.get(i += 1), formResults.get(i += 1), formResults.get(i += 1),
                        formResults.get(i += 1), formResults.get(i += 1), formResults.get(i += 1), formResults.get(i += 1),
                        formResults.get(i += 1), formResults.get(i += 1), formResults.get(i += 1), formResults.get(i += 1));

                //####################################
                // Return results
                //####################################
                updateIngredientsForm = true;
                return updateTargets_Query;
            }

            protected String removeSpaceAndHiddenChars(String stringToBeEdited)
            {
                return stringToBeEdited.trim().replaceAll("\\p{C}", ""); // remove all whitespace & hidden characters like \n
            }

            protected ArrayList<Component> getIngredientsFormObjects()
            {
                return ingredientsFormObjects;
            }

            protected int getIngredientNameObjectIndex()
            {
                return ingredientNameObjectIndex;
            }
        }

        //#################################################################################################################
        // ShopForm
        //##################################################################################################################
        public class ShopForm extends CollapsibleJPanel
        {
            protected int objectID = 0;

            protected HashMap<Integer, JComboBox> shopJComboBoxes = new HashMap<>();
            protected HashMap<Integer, JTextField> prices = new HashMap<>();
            protected HashMap<Integer, JTextField> quantityPerPack = new HashMap<>();

            protected ArrayList<AddShopForm_Object> rowsInTable = new ArrayList<>();

            protected Container parentContainer;
            protected JPanel inputArea;

            public ShopForm(Container parentContainer, String btnText, int btnWidth, int btnHeight)
            {
                super(parentContainer, btnText, btnWidth, btnHeight);
                this.parentContainer = parentContainer;

                //##########################################################################################
                // Creating Form
                //##########################################################################################
                JPanel mainJPanel = this.getCentreJPanel();
                mainJPanel.setLayout(new BorderLayout());

                //##########################################################################################
                // North Frame
                //##########################################################################################

                // Creating North JPanel Area with 2 rows
                JPanel northPanel = new JPanel(new GridBagLayout());
                mainJPanel.add(northPanel, BorderLayout.NORTH);

                //#####################################################
                // Creating area for North JPanel (title area)
                //#####################################################
                JLabel titleLabel = new JLabel("Add Suppliers");
                titleLabel.setFont(new Font("Verdana", Font.PLAIN, 24));
                titleLabel.setHorizontalAlignment(JLabel.CENTER);

                JPanel titlePanel = new JPanel();
                titlePanel.setBackground(Color.green);
                titlePanel.add(titleLabel);

                // Add title JPanel to North Panel Area
                addToContainer(northPanel, titlePanel, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0);

                //#####################################################
                // Creating area for North JPanel (Add Icon)
                //#####################################################
                inputArea = new JPanel(new GridBagLayout());

                IconPanel iconPanel = new IconPanel(1, 10, "East");
                JPanel iconPanelInsert = iconPanel.getIconJpanel();

                addToContainer(northPanel, iconPanel.getIconAreaPanel(), 0, 1, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

                //##########################
                // Refresh Icon
                //##########################
                int width = 30;
                int height = 30;

                IconButton add_Icon_Btn = new IconButton("src/images/add/++add.png", "", width, height, width, height,
                        "centre", "right"); // btn text is useless here , refactor

                JButton add_Btn = add_Icon_Btn.returnJButton();
                add_Icon_Btn.makeBTntransparent();

                add_Btn.addActionListener(ae -> {
                    addShopForm_object();
                });

                iconPanelInsert.add(add_Icon_Btn);

                //##########################################################################################
                // Centre Form
                //##########################################################################################
                mainJPanel.add(inputArea, BorderLayout.CENTER);

                addToContainer(inputArea, new AddShopForm_Object(inputArea, false), 0, objectID, 1, 1, 0.25, 0.25, "horizontal", 0, 0);
            }

            protected AddShopForm_Object addShopForm_object()
            {
                AddShopForm_Object obj = new AddShopForm_Object(inputArea, true);
                addToContainer(inputArea, obj, 0, objectID, 1, 1, 0.25, 0.25, "horizontal", 0, 0);
                return obj;
            }

            private void setObjectID(int objectID)
            {
                this.objectID = objectID;
            }

            protected boolean validateForm()
            {
                boolean noError = true;

                if (!(validatePrices()))
                {
                    noError = false;
                }

                if (!(validateShops()))
                {
                    noError = false;
                }

                if (!(validateQuantity()))
                {
                    noError = false;
                }

                return noError;
            }

            private boolean validatePrices()
            {
                String errorTxt = "";
                BigDecimal zero = new BigDecimal(0);
                int i = 1;

                for (Integer key : prices.keySet())
                {
                    JTextField jTextField = prices.get(key);
                    String value = jTextField.getText().trim();

                    //#########################################
                    // Check if JTextfield input is empty
                    //#########################################
                    if (value.equals(""))
                    {
                        errorTxt += String.format("\n\nOn Row: %s, the 'price' must have a value which is not ' NULL '!", i);
                        i++;
                        continue;
                    }

                    //#########################################
                    // Do BigDecimal Processing
                    //#########################################
                    errorTxt = convertToBigDecimal(value, errorTxt, "Prices", i, jTextField);

                    i++;
                }

                if (errorTxt.equals(""))
                {
                    return true;
                }

                JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), String.format("\n\nPlease fix the following rows being; \n%s", errorTxt));
                return false;
            }

            private boolean validateQuantity()
            {
                String errorTxt = "";
                BigDecimal zero = new BigDecimal(0);
                int i = 1;

                for (Integer key : quantityPerPack.keySet())
                {
                    JTextField jTextField = quantityPerPack.get(key);
                    String value = jTextField.getText().trim();

                    //#########################################
                    // Check if JTextfield input is empty
                    //#########################################
                    if (value.equals(""))
                    {
                        errorTxt += String.format("\n\nOn Row: %s, the 'Quantity' must have a value which is not ' NULL '!", i);
                        i++;
                        continue;
                    }

                    //#########################################
                    // Do BigDecimal Processing
                    //#########################################
                    errorTxt = convertToBigDecimal(value, errorTxt, "Quantity", i, jTextField);

                    i++;
                }


                if (errorTxt.equals(""))
                {
                    return true;
                }

                JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), String.format("\n\nPlease fix the following rows being; \n%s", errorTxt));
                return false;
            }

            private boolean validateShops()
            {
                String errorTxt = "";

                int i = 1;
                ArrayList<String> chosenShops = new ArrayList<>();
                for (Integer key : shopJComboBoxes.keySet())
                {
                    String chosenItem = shopJComboBoxes.get(key).getSelectedItem().toString();

                    if (chosenItem.equals("No Shop"))
                    {
                        errorTxt += String.format("\nOn Row %s,  please Select a shop that isnt 'No Shop'! Or, delete the row!", i);
                    }

                    if (chosenShops.contains(chosenItem))
                    {
                        errorTxt += String.format("\nOn Row %s, there is also another row/rows with with the supplier %s - no duplicate stores!", i, chosenItem);
                    }
                    else
                    {
                        chosenShops.add(chosenItem);
                    }

                    i++;
                }

                if (errorTxt.equals(""))
                {
                    return true;
                }

                JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), String.format("\n\nPlease fix the following rows being; \n%s", errorTxt));
                return false;
            }

            private String[] get_ShopForm_UpdateString()
            {
                //########################################
                // Nothing to Update
                //########################################
                if (!updateIngredientsForm)
                {
                    return null;
                }
                else if (prices.size() == 0)
                {
                    updateShops = true;
                    return null;
                }

                //#############################################################
                // Create Update  String
                //############################################################
                String mysqlVariableReference = "@newIngredientID";
                String createMysqlVariable = String.format("SET %s = (SELECT MAX(IngredientID) FROM ingredients_info);", mysqlVariableReference);
                String updateString = String.format("""
                        INSERT INTO ingredientInShops (IngredientID, Volume_Per_Unit, Cost_Per_Unit, StoreID)
                        VALUES """);

                ///#################################
                // Creating String for Add Values
                //#################################
                String values = "";
                int listSize = prices.size(), pos = 0;

                for (Integer key : prices.keySet())
                {
                    pos++;
                    values += String.format("(%s, %s, %s, (SELECT StoreID FROM stores WHERE Store_Name = '%s'))",
                            mysqlVariableReference, quantityPerPack.get(key).getText(), prices.get(key).getText(), shopJComboBoxes.get(key).getSelectedItem().toString());

                    if (pos == listSize)
                    {
                        values += ";";
                        continue;
                    }

                    values += ",";
                }

                updateString += values;

                //############################################################
                // Return values
                //############################################################

                updateShops = true;

                System.out.printf("\n\n%s \n\n%s", createMysqlVariable, updateString);

                return new String[]{createMysqlVariable, updateString};
            }

            public void clearShopForm()
            {
                Iterator<AddShopForm_Object> it = rowsInTable.iterator();
                while (it.hasNext())
                {
                    AddShopForm_Object i = it.next();
                    i.removeFromParentContainer();
                    it.remove();
                }

                parentContainer.revalidate();
            }

            class AddShopForm_Object extends JPanel
            {
                private int posY = 0, id;
                Integer PDID = null;  //EDIT NOW

                Container parentContainer;
                JComboBox<String> shops_JComboBox;
                JTextField ingredientPrice_TxtField, quantityPerPack_TxtField;

                AddShopForm_Object(Container parentContainer, boolean addRow)
                {
                    this.parentContainer = parentContainer;
                    setObjectID(objectID += 1);
                    this.id = getObjectID();

                    addRow(addRow);
                }

                //EDIT NOW
                protected void setPDID(Integer PDID)
                {
                    this.PDID = PDID;
                }

                //EDIT NOW
                protected Integer getPDID()
                {
                    return PDID;
                }

                protected int getObjectID()
                {
                    return objectID;
                }

                private void addRow(boolean addRowBool)
                {
                    //#########################################################################################################
                    //Creating JPanel
                    //#########################################################################################################
                    JPanel rowPanel = this;
                    rowPanel.setLayout(new BorderLayout());

                    //###############################
                    // Creating Sections for GUI
                    //###############################

                    JPanel eastPanel = new JPanel();
                    eastPanel.setLayout(new GridLayout(1, 1));
                    eastPanel.setBorder(new EmptyBorder(0, 10, 0, 10));
                    eastPanel.setPreferredSize(new Dimension(120, 34)); // width, height
                    rowPanel.add(eastPanel, BorderLayout.EAST);

                    JPanel centrePanel = new JPanel();
                    GridLayout layout = new GridLayout(1, 2);
                    layout.setHgap(10);
                    centrePanel.setLayout(layout);
                    rowPanel.add(centrePanel, BorderLayout.CENTER);

                    JPanel westPanel = new JPanel();
                    westPanel.setLayout(new GridLayout(1, 1));
                    westPanel.setBorder(new EmptyBorder(0, 0, 0, 10));
                    westPanel.setPreferredSize(new Dimension(150, 25)); // width, height
                    rowPanel.add(westPanel, BorderLayout.WEST);

                    if (addRowBool)
                    {
                        //#####################################################
                        // West Side
                        //######################################################

                        if (all_StoresNamesList == null)
                        {
                            JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "Unable To Get ShopNames From DB; \nEither No Shops Exist. \nOr, Internal DB Error");
                            return;
                        }

                        //########################
                        // create JComboBox
                        //########################
                        shops_JComboBox = new JComboBox<String>();
                        loadJComboBox();

                        ((JLabel) shops_JComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
                        westPanel.add(shops_JComboBox);
                        shopJComboBoxes.put(id, shops_JComboBox);

                        //#####################################################
                        // Centre Side
                        //######################################################
                        ingredientPrice_TxtField = new JTextField();
                        ingredientPrice_TxtField.setDocument(new JTextFieldLimit(charlimit));
                        ingredientPrice_TxtField.setText("0.00");
                        prices.put(id, ingredientPrice_TxtField);
                        centrePanel.add(ingredientPrice_TxtField);

                        quantityPerPack_TxtField = new JTextField();
                        quantityPerPack_TxtField.setDocument(new JTextFieldLimit(charlimit));
                        quantityPerPack_TxtField.setText("0.00");
                        quantityPerPack.put(id, quantityPerPack_TxtField);
                        centrePanel.add(quantityPerPack_TxtField);

                        //#####################################################
                        // East Side
                        //######################################################

                        // Creating submit button
                        JButton deleteRowBtn = new JButton("Delete Row");
                        deleteRowBtn.setFont(new Font("Arial", Font.BOLD, 12)); // setting font

                        // creating commands for submit button to execute on
                        deleteRowBtn.addActionListener(ae -> {
                            deleteRowAction();
                        });

                        eastPanel.add(deleteRowBtn);

                        //#####################################################
                        // Adding row to memory
                        //######################################################
                        rowsInTable.add(this);
                    }
                    else
                    {
                        //#####################################################
                        // West Panel
                        //######################################################

                        //panel
                        westPanel.setBorder(new EmptyBorder(0, 10, 0, 10));
                        westPanel.setBackground(Color.LIGHT_GRAY);

                        //Label
                        JLabel westLabel = new JLabel("Select A Store");
                        westLabel.setFont(new Font("Arial", Font.BOLD, 16)); // setting font
                        westPanel.add(westLabel);

                        //#####################################################
                        // Centre Panel
                        //######################################################

                        // centre panel
                        centrePanel.setBackground(Color.LIGHT_GRAY);

                        //Label
                        JLabel setPriceLabel = new JLabel("Set Price (£)");
                        setPriceLabel.setFont(new Font("Arial", Font.BOLD, 16)); // setting font
                        centrePanel.add(panelWithSpace(setPriceLabel, Color.LIGHT_GRAY, 50, 50, 50, 50));
                        // centrePanel.add(setPriceLabel, BorderLayout.CENTER);

                        JLabel setQuantityLabel = new JLabel("Package Quantity (G,L)");
                        setQuantityLabel.setFont(new Font("Arial", Font.BOLD, 14)); // setting font
                        centrePanel.add(panelWithSpace(setQuantityLabel, Color.LIGHT_GRAY, 15, 50, 0, 50));

                        //#####################################################
                        // East Panel
                        //######################################################

                        //panel
                        eastPanel.setBackground(Color.LIGHT_GRAY);

                        //Label
                        JLabel eastLabel = new JLabel("Delete Row");
                        eastLabel.setFont(new Font("Arial", Font.BOLD, 16)); // setting font
                        eastPanel.add(eastLabel);
                    }

                    //#########################################################################################################
                    // Adding Object To GUI
                    //#########################################################################################################
                    addToContainer(parentContainer, rowPanel, 0, posY += 1, 1, 1, 0.25, 0.25, "both", 0, 0);
                    parentContainer.revalidate();
                }

                protected void deleteRowAction()
                {
                    removeFromParentContainer();
                    rowsInTable.remove(this);
                }

                protected void loadJComboBox()
                {
                    shops_JComboBox.removeAllItems();
                    for (String storeName : all_StoresNamesList)
                    {
                        shops_JComboBox.addItem(storeName);
                        shops_JComboBox.setSelectedItem("No Shop");
                    }
                }

                private JComboBox<String> getShops_JComboBox()
                {
                    return shops_JComboBox;
                }

                private JTextField getIngredientPrice_TxtField()
                {
                    return ingredientPrice_TxtField;
                }

                private JTextField getQuantityPerPack_TxtField()
                {
                    return quantityPerPack_TxtField;
                }

                public void removeFromParentContainer()
                {
                    // Removing Objects from memory as the row they belong to is gone
                    prices.remove(id);
                    shopJComboBoxes.remove(id);
                    quantityPerPack.remove(id);

                    //Remove from parent Container
                    parentContainer.remove(this);

                    //Resizing
                    parentContainer.revalidate();
                    resize_GUI();
                }
            }
        }

        //#################################################################################################################
        // Other Methods
        //##################################################################################################################
        public JPanel panelWithSpace(Component addObjectToPanel, Color backgroundColour, int westWidth, int westHeight, int eastWidth, int eastHeight)
        {
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(addObjectToPanel, BorderLayout.CENTER);

            //####################
            // Space Filler
            //####################

            // space filler panels
            JPanel westSpaceFiller = new JPanel();
            westSpaceFiller.setPreferredSize(new Dimension(westWidth, westHeight)); // width, height
            westSpaceFiller.setBackground(backgroundColour);
            panel.add(westSpaceFiller, BorderLayout.WEST);

            JPanel eastSpaceFiller = new JPanel();
            eastSpaceFiller.setPreferredSize(new Dimension(eastWidth, eastHeight)); // width, height
            eastSpaceFiller.setBackground(backgroundColour);
            panel.setBackground(backgroundColour);
            panel.add(eastSpaceFiller, BorderLayout.EAST);

            return panel;
        }
    }

    //############################################
    // Return Forms
    //############################################

    public EditingCreateForm getEditingCreateForm()
    {
        return editingCreateForm;
    }

    public CreateForm getCreateForm()
    {
        return createForm;
    }

    //##################################################################################################################
    // Other Classes
    //##################################################################################################################

    public void updateIngredientsFormTypeJComboBoxes()
    {
        createForm.updateIngredientForm_Type_JComboBox();
        editingCreateForm.updateIngredientForm_Type_JComboBox();
    }

    public void updateIngredientSuppliersJComboBoxes()
    {
        System.out.printf("\n\nUpdating GUI");
        createForm.clearShopForm();
        editingCreateForm.refreshInterface(true, true);
    }

    //FIX
    private JComboBox<String> getEdit_IngredientTypes_InPlan_JComboBox()
    {
        return editingCreateForm.getEdit_IngredientTypes_InPlan_JComboBox();
    }

    public boolean changeKeyIngredientsTypesList(String process, String newKey, String oldKey)
    {
        if (process.equals("removeKey"))
        {
            if (map_ingredientTypesToIngredientNames.containsKey(oldKey)) // if the key had no ingredientNames attached to it, do nothing
            {
                String unAssignedKey = "UnAssigned";

                Collection<String> oldKeyListData = map_ingredientTypesToIngredientNames.remove(oldKey); // get the old ingredientNames associated with the old key

                if (!map_ingredientTypesToIngredientNames.containsKey(unAssignedKey)) // add UnAssigned as key if it doesn't exist
                {
                    map_ingredientTypesToIngredientNames.put(unAssignedKey, new TreeSet<String>(Collator.getInstance()));
                }

                // Get Unassigned List Data
                Collection<String> unassignedKeyListData = map_ingredientTypesToIngredientNames.remove(unAssignedKey); // get ingredientNames assigned with the key unassigned

                // Add OldKey IngredientNames to Unassigned List
                unassignedKeyListData.addAll(oldKeyListData);

                //  Add Unassigned list back to the map
                map_ingredientTypesToIngredientNames.put(unAssignedKey, unassignedKeyListData);

                return true;
            }
            return false;
        }
        else if (process.equals("changeKeyName"))
        {
            if (map_ingredientTypesToIngredientNames.containsKey(oldKey))
            {
                System.out.printf("\n\nFound in List");
                map_ingredientTypesToIngredientNames.put(newKey, map_ingredientTypesToIngredientNames.remove(oldKey));
                return true;
            }
        }
        return false;
    }

    //##################################################################################################################
    // General Methods
    //##################################################################################################################
    public void makeJframeVisible()
    {
        setExtendedState(JFrame.NORMAL);
        setSize(new Dimension(jFramewidth, jFrameheight));
        setLocation(0, 0);
        setVisible(true);
        setResizable(false);

        setLocationRelativeTo(null);
    }

    public void closeWindowEvent()
    {
        mealPlanScreen.remove_addIngredients_Screen();
    }

    public void closeWindow()
    {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    private void addToContainer(Container container, Component addToContainer, int gridx, int gridy, int gridwidth,
                                int gridheight, double weightx, double weighty, String fill, int ipady, int ipadx)
    {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.weightx = weightx;
        gbc.weighty = weighty;

        gbc.ipady = ipady;
        gbc.ipadx = ipadx;

        switch (fill.toLowerCase())
        {
            case "horizontal":
                gbc.fill = GridBagConstraints.HORIZONTAL;
                break;
            case "vertical":
                gbc.fill = GridBagConstraints.VERTICAL;
                break;

            case "both":
                gbc.fill = GridBagConstraints.BOTH;
                break;
        }

        container.add(addToContainer, gbc);
    }
}
