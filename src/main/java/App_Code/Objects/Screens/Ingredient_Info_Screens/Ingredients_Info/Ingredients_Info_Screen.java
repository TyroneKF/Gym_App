package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info;

import java.text.Collator;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_IngredientTypes.Edit_Ingredient_Stores_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_IngredientTypes.Edit_Ingredients_Types_Screen;
import App_Code.Objects.Screens.Meal_Plan_Screen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.*;


public class Ingredients_Info_Screen extends JFrame
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    
    // Objects
    private MyJDBC db;
    private Container contentPane;
    private Meal_Plan_Screen mealPlanScreen;
    protected GridBagConstraints gbc = new GridBagConstraints();
    
    // Screen Objects
    private Add_Ingredients_Screen addIngredientsInfo;
    private Edit_IngredientsScreen editIngredientsInfo;
    
    // Int
    private int
            tempPlanID, planID,
            jFrameWidth = 800,
            jFrameHeight = 850;
    
    // String
    private String planName;
    
    // Booleans
    private boolean
            updateIngredientInfo = false;
    
    //#######################################
    // Collections
    //#######################################
    // Ordered Collections applied from once it's released from MYJDC LVL
    private Collection<String> ingredientsTypesList, storesNamesList;
    
    // Sorted Hashmap by key String
    private TreeMap<String, Collection<String>> map_ingredientTypesToNames = new TreeMap<String, Collection<String>>(new Comparator<String>()
    {
        public int compare(String o1, String o2)
        {
            return o1.toLowerCase().compareTo(o2.toLowerCase());
        }
    });
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Ingredients_Info_Screen(MyJDBC db, Meal_Plan_Screen mealPlanScreen,
                                   TreeMap<String, Collection<String>> map_ingredientTypesToNames,
                                   Collection<String> ingredientsTypesList, Collection<String> storesNamesList)
    {
        //###################################################################################
        // Variables
        //###################################################################################
        this.db = db;
        this.mealPlanScreen = mealPlanScreen;
        this.planID = mealPlanScreen.getPlanID();
        this.tempPlanID = mealPlanScreen.getTempPlanID();
        this.planName = mealPlanScreen.getPlanName();
        
        this.map_ingredientTypesToNames = map_ingredientTypesToNames;
        this.ingredientsTypesList = ingredientsTypesList;
        this.storesNamesList = storesNamesList;
        
        //###################################################################################
        // Check DB Connection
        //###################################################################################
        if (! db.get_DB_Connection_Status())
        {
            JOptionPane.showMessageDialog(null, "Error bringing up screen to edit ingredients! Database Not Connected");
            return;
        }
        
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
        addIngredientsInfo = new Add_Ingredients_Screen(this, db);
        tp.add("Add Ingredients", addIngredientsInfo);
        
        //#################################################
        // Creating Edit Ingredients Screen
        //##################################################
        editIngredientsInfo = new Edit_IngredientsScreen(this, db);
        tp.add("Edit Ingredients", editIngredientsInfo);
        
        //#################################################
        // Creating Edit Ingredient Types Screen
        //##################################################
        Edit_Ingredients_Types_Screen edit_IngredientTypes = new Edit_Ingredients_Types_Screen(db, this, ingredientsTypesList);
        tp.add("Edit Ingredient Types", edit_IngredientTypes);
        
        //#################################################
        // Creating Edit Ingredients Stores Screen
        //##################################################
        Edit_Ingredient_Stores_Screen edit_Stores_Screen = new Edit_Ingredient_Stores_Screen(db, this, storesNamesList);
        tp.add("Edit Ingredient Stores", edit_Stores_Screen);
        
        //#################################################
        //
        //#################################################
        contentPane.revalidate();
        
    }
    
    //##################################################################################################################
    // Accessor Methods (Get) Methods
    //##################################################################################################################
    
    // Objects
    public Edit_IngredientsScreen getEditIngredientsForm()
    {
        return editIngredientsInfo;
    }
    
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
    
    public MyJDBC getDb()
    {
        return db;
    }
    
    //############################################
    // Get Values & Memory
    //############################################
    public Collection<String> getIngredientsTypesList()
    {
        return ingredientsTypesList;
    }
    
    public Collection<String> getStoresNamesList()
    {
        return storesNamesList;
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
    
    //##################################################################################################################
    // Update Methods
    //##################################################################################################################
    public void updateIngredientsFormTypeJComboBoxes()
    {
        addIngredientsInfo.updateIngredientForm_Type_JComboBox();
        editIngredientsInfo.updateIngredientForm_Type_JComboBox();
    }
    
    public void updateIngredientSuppliersJComboBoxes()
    {
        System.out.printf("\n\nUpdating Suppliers GUI");
        addIngredientsInfo.clearShopForm();
        editIngredientsInfo.refreshInterface(true, true);
    }
    
    public void update_EditIngredientsInfo_IngredientsTypes()
    {
        editIngredientsInfo.updateIngredientsTypeJComboBox();
    }
    
    //FIX
    /*  public JComboBox<String> getEdit_IngredientTypes_InPlan_JComboBox()
    {
        return editIngredientsInfo.getIngredientsTypesJComboBox();
    }*/
    
    //##################################################################################################################
    // Mutator Methods
    //##################################################################################################################
    
    // Ingredient Types
    public boolean addChangeOrRemoveIngredientsTypeName(String process, String newKey, String oldKey)// HELLO CHECKA
    {
        /*//####################################################################
        System.out.printf("\n\n##################################### \n\naddChangeOrRemoveIngredientsTypeName() \nprocess: %s \nnewKey: %s \noldKey: %s \n\nMap:",
                process, newKey, oldKey);

        for (String key : map_ingredientTypesToNames.keySet())
        {
            System.out.printf("\n%s", key);
        }

        System.out.println("\n\nList:");
        for (String key : ingredientsTypesList)
        {
            System.out.printf("\n%s", key);
        }
        //####################################################################
*/
        
        if (process.equals("addKey"))
        {
            return addOrRemoveIngredientsTypeNameFromList(process, newKey, oldKey);
        }
        else if (map_ingredientTypesToNames.containsKey(oldKey)) // if the key had no ingredientNames attached to it, do nothing
        {
            if (process.equals("removeKey"))
            {
                String unAssignedKey = "UnAssigned";
                
                Collection<String> oldKeyListData = map_ingredientTypesToNames.remove(oldKey); // get the old ingredientNames associated with the old key
                
                if (! map_ingredientTypesToNames.containsKey(unAssignedKey)) // add UnAssigned as key if it doesn't exist
                {
                    map_ingredientTypesToNames.put(unAssignedKey, new TreeSet<String>(Collator.getInstance()));
                }
                
                // Get Unassigned List Data
                Collection<String> unassignedKeyListData = map_ingredientTypesToNames.remove(unAssignedKey); // get ingredientNames assigned with the key unassigned
                
                // Add OldKey IngredientNames to Unassigned List
                unassignedKeyListData.addAll(oldKeyListData);
                
                //  Add Unassigned list back to the map
                map_ingredientTypesToNames.put(unAssignedKey, unassignedKeyListData);
            }
            else if (process.equals("changeKeyName"))
            {
                if (map_ingredientTypesToNames.containsKey(oldKey))
                {
                    map_ingredientTypesToNames.put(newKey, map_ingredientTypesToNames.remove(oldKey));
                    
                }
            }
        }
        
        return addOrRemoveIngredientsTypeNameFromList(process, newKey, oldKey);
    }
    
    private boolean addOrRemoveIngredientsTypeNameFromList(String process, String newKey, String oldKey)
    {
        //####################################################################
        System.out.printf("\n\n##################################### \n\naddOrRemoveIngredientsTypeNameFromList() \nprocess: %s \nnewKey: %s \noldKey: %s \n\nMap:",
                process, newKey, oldKey);
        
        for (String key : map_ingredientTypesToNames.keySet())
        {
            System.out.printf("\n%s", key);
        }
        
        System.out.println("\n\nList:");
        for (String key : ingredientsTypesList)
        {
            System.out.printf("\n%s", key);
        }
        //####################################################################
        
        //#########################################
        // addKey
        //#########################################
        if (process.equals("addKey"))
        {
            ingredientsTypesList.add(newKey);
            return true;
        }
        
        //#########################################
        // Check if oldKey exists if not error
        //#########################################
        if (oldKey == null || ! (ingredientsTypesList.contains(oldKey)))
        {
            System.out.print("\n\nError1");
            return false;
        }
        
        //#########################################
        // removeKey / changeKey
        //#########################################
        if (process.equals("removeKey"))
        {
            ingredientsTypesList.remove(oldKey);
        }
        else if (process.equals("changeKeyName"))
        {
            ingredientsTypesList.remove(oldKey);
            ingredientsTypesList.add(newKey);
        }
        
        //####################################################################
        System.out.printf("\n\n##################################### \n\naddOrRemoveIngredientsTypeNameFromList() \nprocess: %s \nnewKey: %s \noldKey: %s \n\nMap:",
                process, newKey, oldKey);
        
        for (String key : map_ingredientTypesToNames.keySet())
        {
            System.out.printf("\n%s", key);
        }
        
        System.out.println("\n\nList:");
        for (String key : ingredientsTypesList)
        {
            System.out.printf("\n%s", key);
        }
        //####################################################################
        
        return true;
    }
    
    //
    public void addOrRemoveSupplierFromList(String process, String newKey, String oldKey)
    {
        // Validate input value
        Set<String> processes = Set.of("add", "replace", "delete");
        assert processes.contains(process) : String.format("addOrRemoveIngredientsTypeNameFromList() Invalid process: %s", process);
        
        // Add Supplier
        if (process.equals("add"))
        {
            storesNamesList.add(newKey);
            return;
        }
        
        // Processes left are delete & Replace Suppliers which both involve deleting oldKey
        storesNamesList.remove(oldKey); // Delete suppliers
        
        if (process.equals("delete")) { return; } // Nothing left to do
        
        // only process left is replace which is only to add the newKey
        storesNamesList.add(newKey);
    }
    
    public void setUpdateIngredientInfo(boolean status)
    {
        updateIngredientInfo = status;
    }
    
    //##################################################################################################################
    // Resizing Methods
    //##################################################################################################################
    // HELLO! Stay in Parent
    public void makeFrameVisible()
    {
        setExtendedState(JFrame.NORMAL);
        setSize(new Dimension(jFrameWidth, jFrameHeight));
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
    
    public void resize_GUI()
    {
        getContentPane().revalidate();
    }
}
