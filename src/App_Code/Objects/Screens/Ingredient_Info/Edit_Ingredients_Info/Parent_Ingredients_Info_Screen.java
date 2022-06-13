package App_Code.Objects.Screens.Ingredient_Info.Edit_Ingredients_Info;

import java.text.Collator;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Screens.Ingredient_Info.Edit_Stores_And_Types.Edit_Ingredient_Stores_Screen;
import App_Code.Objects.Screens.Ingredient_Info.Edit_Stores_And_Types.Edit_Ingredients_Types_Screen;
import App_Code.Objects.Screens.Others.Meal_Plan_Screen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.*;


public class Parent_Ingredients_Info_Screen extends JFrame
{

    private Collection<String> ingredientsTypesList, storesNamesList;

    //#######################################
    // General Variables
    //#######################################

    private GridBagConstraints gbc = new GridBagConstraints();

    private int jFramewidth = 710, jFrameheight = 850;
    private Container contentPane;

    private MyJDBC db;
    private Integer tempPlanID, planID;
    private String planName;
    private Meal_Plan_Screen mealPlanScreen;

    private boolean
            updateIngredientInfo = false;

    // Sorted Hashmap by key String
    private TreeMap<String, Collection<String>> map_ingredientTypesToNames = new TreeMap<String, Collection<String>>(new Comparator<String>()
    {
        public int compare(String o1, String o2)
        {
            return o1.toLowerCase().compareTo(o2.toLowerCase());
        }
    });

    Add_Ingredients addIngredients;
    Edit_Ingredients editIngredientsInfo;

    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Parent_Ingredients_Info_Screen(MyJDBC db, Meal_Plan_Screen mealPlanScreen, int planID, int tempPlanID, String planName)
    {
        System.out.printf("\n\nParent_Ingredients_Info_Screen");
        this.db = db;
        this.mealPlanScreen = mealPlanScreen;
        this.planID = planID;
        this.tempPlanID = tempPlanID;
        this.planName = planName;


        try
        {
            if (db.isDatabaseConnected())
            {
                //Update Generic  ingredientsType JComboBox which has all the ingredients Types
                ingredientsTypesList = db.getSingleColumnQuery_AlphabeticallyOrderedTreeSet("SELECT Ingredient_Type_Name FROM ingredientTypes;");

                if (ingredientsTypesList == null)
                {
                    JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "\n\nUnable to get IngredientTypes for form!");
                    return;
                }

                storesNamesList = db.getSingleColumnQuery_AlphabeticallyOrderedTreeSet("SELECT Store_Name FROM stores;");

                //###################################################################################
                // Frame Set-Up
                //###################################################################################

                setTitle("Add/Edit Ingredients Screen");
                makeFrameVisible();

                //Delete all temp data on close
                addWindowListener(new java.awt.event.WindowAdapter()
                {
                    @Override //HELLO Causes Error
                    public void windowClosing(WindowEvent windowEvent)
                    {
                        closeWindowEvent();
                        mealPlanScreen.updateIngredientsNameAndTypesInJTables(updateIngredientInfo);
                    }
                });

                //###################################################################################
                // Create ContentPane
                //###################################################################################

                contentPane = super.getContentPane();
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

                addIngredients = new Add_Ingredients(this, db);
                addToContainer(addIngredientsFormJPanel, addIngredients, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0);

                //#################################################
                // Creating Edit Ingredients Screen
                //##################################################
                JPanel editIngredientsFormJPanel = new JPanel(new GridBagLayout());
                tp.add("Edit Ingredients", editIngredientsFormJPanel);

                editIngredientsInfo = new Edit_Ingredients(this, db);
                addToContainer(editIngredientsFormJPanel, editIngredientsInfo, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0);

                //#################################################
                // Creating Edit Ingredient Types Screen
                //##################################################
                JPanel editIngredientsTypesJPanel = new JPanel(new GridBagLayout());
                tp.add("Edit Ingredient Types", editIngredientsTypesJPanel);

                addToContainer(editIngredientsTypesJPanel, new Edit_Ingredients_Types_Screen(db, this, ingredientsTypesList), 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0);

                //#################################################
                // Creating Edit Ingredients Stores Screen
                //##################################################
                JPanel editIngredientsStoreJPanel = new JPanel(new GridBagLayout());
                tp.add("Edit Ingredient Stores", editIngredientsStoreJPanel);

                addToContainer(editIngredientsStoreJPanel, new Edit_Ingredient_Stores_Screen(db, this, storesNamesList), 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0);

                contentPane.revalidate();

            }
            else
            {
                JOptionPane.showMessageDialog(null, "Error bringing up screen to edit ingredients! Database Not Connected");
            }
        }
        catch (Exception e)
        {
             System.out.printf("\n\nParent_Ingredients_Info_Screen() Error \n%s ",e );
        }
    }

    //#########################################################################
    // New Additions
    //#########################################################################
    public Meal_Plan_Screen getMealPlanScreen()
    {
        return mealPlanScreen;
    }

    public TreeMap<String, Collection<String>> getMapIngredientTypesToNames()
    {
        return map_ingredientTypesToNames;
    }

    public Container getContentPane()
    {
        return contentPane;
    }

    public Collection<String> getIngredientsTypesList()
    {
        return ingredientsTypesList;
    }

    public Collection<String> getStoresNamesList()
    {
        return storesNamesList;
    }

    public void update_Edit_IngredientsTypes()
    {
        editIngredientsInfo.updateIngredientsTypeJComboBox();
    }

    public Integer getTempPlanID()
    {
        return tempPlanID;
    }

    public Integer getPlanID()
    {
        return planID;
    }

    public String getPlanName()
    {
        return planName;
    }

    public Edit_Ingredients getEditIngredientsForm()
    {
        return editIngredientsInfo;
    }

    //#########################################################################





    //############################################
    // Return Forms
    //############################################

    //##################################################################################################################
    // Other Classes
    //##################################################################################################################
    public void updateIngredientsFormTypeJComboBoxes()
    {
        addIngredients.updateIngredientForm_Type_JComboBox();
        editIngredientsInfo.updateIngredientForm_Type_JComboBox();
    }

    public void updateIngredientSuppliersJComboBoxes()
    {
        System.out.printf("\n\nUpdating GUI");
        addIngredients.clearShopForm();
        editIngredientsInfo.refreshInterface(true, true);
    }

    //FIX
    /*  public JComboBox<String> getEdit_IngredientTypes_InPlan_JComboBox()
    {
        return editIngredientsInfo.getIngredientsTypesJComboBox();
    }*/

    public boolean changeKeyIngredientsTypesList(String process, String newKey, String oldKey)
    {
        if (process.equals("removeKey"))
        {
            if (map_ingredientTypesToNames.containsKey(oldKey)) // if the key had no ingredientNames attached to it, do nothing
            {
                String unAssignedKey = "UnAssigned";

                Collection<String> oldKeyListData = map_ingredientTypesToNames.remove(oldKey); // get the old ingredientNames associated with the old key

                if (!map_ingredientTypesToNames.containsKey(unAssignedKey)) // add UnAssigned as key if it doesn't exist
                {
                    map_ingredientTypesToNames.put(unAssignedKey, new TreeSet<String>(Collator.getInstance()));
                }

                // Get Unassigned List Data
                Collection<String> unassignedKeyListData = map_ingredientTypesToNames.remove(unAssignedKey); // get ingredientNames assigned with the key unassigned

                // Add OldKey IngredientNames to Unassigned List
                unassignedKeyListData.addAll(oldKeyListData);

                //  Add Unassigned list back to the map
                map_ingredientTypesToNames.put(unAssignedKey, unassignedKeyListData);

                return true;
            }
            return false;
        }
        else if (process.equals("changeKeyName"))
        {
            if (map_ingredientTypesToNames.containsKey(oldKey))
            {
                System.out.printf("\n\nFound in List");
                map_ingredientTypesToNames.put(newKey, map_ingredientTypesToNames.remove(oldKey));
                return true;
            }
        }
        return false;
    }

    public void setUpdateIngredientInfo(boolean status)
    {
        updateIngredientInfo = status;
    }

    //##################################################################################################################
    // General Methods
    //##################################################################################################################

    // HELLO! Stay in Parent
    public void makeFrameVisible()
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
        mealPlanScreen.remove_Ingredients_Info_Screen();
    }

    public void closeWindow()
    {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public void addToContainer(Container container, Component addToContainer, int gridx, int gridy, int gridwidth,
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
