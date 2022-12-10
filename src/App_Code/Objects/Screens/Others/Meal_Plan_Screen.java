package App_Code.Objects.Screens.Others;


import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.Children.MacrosLeftTable;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.Children.MacrosTargetsTable;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.IngredientsTable;

import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.Children.TotalMealTable;
import App_Code.Objects.Gui_Objects.CollapsibleJPanel;
import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Gui_Objects.IconPanel;
import App_Code.Objects.Gui_Objects.ScrollPaneCreator;
import App_Code.Objects.Screens.Ingredient_Info.Edit_Ingredients_Info.Parent_Ingredients_Info_Screen;
import App_Code.Objects.Screens.Others.Loading_Screen.SplashScreenDemo;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Meal_Plan_Screen extends JPanel
{
    private Collection<String> ingredientsTypesList, storesNamesList;

    // Sorted Hashmap by key String
    private TreeMap<String, Collection<String>> map_ingredientTypesToNames = new TreeMap<String, Collection<String>>(new Comparator<String>()
    {
        public int compare(String o1, String o2)
        {
            return o1.toLowerCase().compareTo(o2.toLowerCase());
        }
    });

    //#################################################################################################################
    private MacrosLeftTable macrosLeft_JTable;
    private MacrosTargetsTable macros_Targets_Table;

    //#################################################################################################################
    //
    //#################################################################################################################

    private static String databaseName = "gymapp12";
    private String JFrameName = databaseName;

    //#################################################################################################################
    // Objects
    //#################################################################################################################
    private GridBagConstraints gbc = new GridBagConstraints();
    private JFrame frame = new JFrame(JFrameName);
    private JPanel scrollPaneJPanel, scrollJPanelCenter, scrollJPanelEnd;
    private Container contentPane;
    private ScrollPaneCreator scrollPane;

    private MyJDBC db;
    private Macros_Targets_Screen macrosTargets_Screen = null;
    private Parent_Ingredients_Info_Screen ingredientsInfoScreen = null;

    private IngredientsTable jTableBeingAdded;
    private ArrayList<IngredientsTable> listOfJTables = new ArrayList<>();

    //#################################################################################################################
    // Variables
    //#################################################################################################################
    private String[] meal_total_columnNames, ingredients_ColumnNames;

    private String planName;
    private Integer  tempPlanID = 1, planID;
    private int  pos = 0, mealNo = 0, frameHeight = 1082, frameWidth = 1925;

    private boolean macroTargetsChanged = false;

    //#################################################################################################################
    // Ingredients Table Columns
    //#################################################################################################################

    private final ArrayList<String>
            ingredients_Table_Col_Avoid_Centering = new ArrayList<>(Arrays.asList("Ingredient_Type","Ingredient_Name","Supplier")),
            ingredientsTableUnEditableCells = new ArrayList<>(Arrays.asList(
                    "Ingredients_Index","IngredientID", "Ingredient_Cost", "Protein","GI","Carbohydrates", "Sugars_Of_Carbs", "Fibre", "Fat", "Saturated_Fat", "Salt", "Water_Content", "Liquid_Content", "Calories"));

    //#################################################################################################################
    // Table Customisations
    //#################################################################################################################

    private final ArrayList<String>
            ingredientsInMeal_Table_ColToHide = new ArrayList<String>(Arrays.asList("PlanID", "MealID")),
            totalMeal_Table_ColToHide = new ArrayList<String>(Arrays.asList("PlanID", "MealID")),
            macrosTargets_Table_ColToHide = new ArrayList<String>(Arrays.asList("PlanID", "Plan_Name","DateTime_Of_Creation")),
            macrosLeft_Table_ColToHide = new ArrayList<String>(Arrays.asList("PlanID", "Plan_Name"));

    //########################################################

    String lineSeparator = "###############################################################################";

    //##################################################################################################################
    // Constructor & Main
    //##################################################################################################################
    public static void main(String[] args)
    {
        //############################################################################################################
        // Database Setup
        //#############################################################################################################

        String sql3 = """                                
                                 CREATE TABLE IF NOT EXISTS data
                                 (
                                    FirstName VARCHAR(32) PRIMARY KEY,
                                    LastName VARCHAR(30) NOT NULL,
                                    Sport    CHAR(32)   CHECK(Sport IN('Snowboarding', 'Rowing', 'Knitting', 'Speed Reading', 'Pool', 'None of the above')),
                                    Years  INT NOT NULL,
                                    Vegetarian BOOLEAN NOT NULL,
                                    DeleteRow CHAR(10) CHECK(DeleteRow IN('Delete Row'))
                                 ) 
                                                                  
                """;

        LinkedHashMap<String, String> tableInitialization = new LinkedHashMap<>();
        tableInitialization.put("data", sql3);

        MyJDBC db = new MyJDBC("root", "password", databaseName, tableInitialization);


        if (!(db.isDatabaseConnected()))
        {
            JOptionPane.showMessageDialog(null, "ERROR, Cannot Connect To Database!");
            return;
        }
        new Meal_Plan_Screen(db);
    }


    public Meal_Plan_Screen(MyJDBC db)
    {
        this.db = db;

        //##############################################################################################################
        // Getting selected plan Info
        //##############################################################################################################

        ArrayList<ArrayList<String>> results1 = db.getMultiColumnQuery(String.format("SELECT PlanID, Plan_Name FROM plans WHERE SelectedPlan = %s;", tempPlanID));
        ArrayList<String> results = results1!=null ? results1.get(0):null;

        planID = results!=null ? Integer.parseInt(results.get(0)):null;
        planName = results!=null ? results.get(1):null;

        if (planID!=null)
        {
            //####################################################
            // Getting Number Of Meals & Subsections of meals
            //####################################################
            String query1 = String.format("SELECT COUNT(MealInPlanID) AS TotalMeals FROM mealsInPlan WHERE PlanID = %s;", planID);
            String[] mealsInPlanCount = db.getSingleColumnQuery(query1);

            String query2 = String.format("SELECT COUNT(DivMealSectionsID) AS TotalSubMeals FROM dividedMealSections WHERE PlanID = %s;", planID);
            String[] dividedMealSectionsCount = db.getSingleColumnQuery(query2);

            //####################################################
            // Setting Up Loading Screen
            //####################################################
            int
                    no_of_meals = mealsInPlanCount==null ? 0:Integer.parseInt(mealsInPlanCount[0]),
                    no_of_sub_meals = dividedMealSectionsCount==null ? 0:Integer.parseInt(dividedMealSectionsCount[0]),
                    totalProgress = no_of_meals + no_of_sub_meals + (7 * 10);
            /**
             *  1.) Transfer Plan Data
             *  2.) Transfer Plan Targets
             *  3.) Transferring Meals Data
             *  4.) Get Ingredient Names & Types
             *  5.) Main GUI Setup (excluding adding meals)
             *  6.) MacroTargets Setup
             *  7.) MacrosLeft Setup
             */

            SplashScreenDemo splashScreenDemo = new SplashScreenDemo(totalProgress, this);


            //####################################################
            // Transferring PLan Data To Temp
            //####################################################
            if (!transferPlanData(planID, tempPlanID))
            {
                splashScreenDemo.closeWindow();
                return;
            }

            splashScreenDemo.increaseBar(10);
            System.out.printf("\nChosen Plan: %s  & Chosen Plan Name: %s \n\n%s", planID, planName, lineSeparator);

            //####################################################
            // Transferring Targets From Chosen PLan to Temp
            //####################################################
            if (!transferTargets(planID, tempPlanID, true, false))
            {
                splashScreenDemo.closeWindow();
                return;
            }

            splashScreenDemo.increaseBar(10);

            //####################################################
            // Transferring this plans Meals  Info to Temp-Plan
            //####################################################

            if (!(transferMealIngredients(planID, tempPlanID)))
            {
                splashScreenDemo.closeWindow();
                JOptionPane.showMessageDialog(null, "\n\nCannot Create Temporary Plan In DB to Allow Editing");
                return;
            }

            splashScreenDemo.increaseBar(10);

            //####################################################
            // Get IngredientTypes & Store Data
            //####################################################
            if (!(getIngredientsTypesAndStoresData(true, true, true)))
            {
                splashScreenDemo.closeWindow();
                JOptionPane.showMessageDialog(null, "\n\nCannot Get IngredientsTypes & Stores Info \n\ngetIngredientsTypesAndStoresData()");
                return;
            }

            splashScreenDemo.increaseBar(10);

            //#############################################################################################################
            //   1. Create the  GUI framework
            //#############################################################################################################

            // Container (ContentPane)
            contentPane = frame.getContentPane();
            contentPane.setLayout(new GridBagLayout());
            contentPane.setVisible(true);

            //#########################################
            //   Define Frame Properties
            //#########################################
            frame.setVisible(false);
            frame.setResizable(true);
            frame.setSize(frameWidth, frameHeight);
            frame.setLocation(00, 0);

            //Delete all temp data on close
            frame.addWindowListener(new java.awt.event.WindowAdapter()
            {
                @Override //HELLO Causes Error
                public void windowClosing(java.awt.event.WindowEvent windowEvent)
                {
                    // ##############################################
                    // If targets have changed, save them?
                    // ##############################################
                    if (macroTargetsChanged)
                    {
                        saveMacroTargets(true, false);
                    }

                    saveMealData(true, false);

                    // ##############################################
                    // Close Other Windows If Open
                    // ##############################################
                    if (macrosTargets_Screen!=null)
                    {
                        macrosTargets_Screen.closeeWindow();
                    }
                    if (ingredientsInfoScreen!=null)
                    {
                        ingredientsInfoScreen.closeWindow();
                    }
                }
            });

            //#########################################
            //   Create Interface
            //#########################################
            JPanel screenSectioned = new JPanel(new BorderLayout());

            JPanel mainNorthPanel = new JPanel(new GridBagLayout());
            JPanel mainCenterPanel = new JPanel(new GridBagLayout());

            //###########################################
            // Icon Setup in mainNorthPanel
            //###########################################

            iconSetup(mainNorthPanel);
            //###########################################

            // Adding different section of interface to

            screenSectioned.add(mainNorthPanel, BorderLayout.NORTH);
            screenSectioned.add(mainCenterPanel, BorderLayout.CENTER);

            addToContainer(contentPane, screenSectioned, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0, null);

            //##########################################################
            // Create ScrollPane & add to Interface
            //#########################################################
            scrollPane = new ScrollPaneCreator();
            scrollPaneJPanel = scrollPane.getJPanel();
            scrollPaneJPanel.setLayout(new GridBagLayout());

            scrollJPanelCenter = new JPanel(new GridBagLayout());
            scrollJPanelEnd = new JPanel(new GridBagLayout());

            addToContainer(scrollPaneJPanel, scrollJPanelCenter, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0, "center");
            addToContainer(scrollPaneJPanel, scrollJPanelEnd, 0, 1, 1, 1, 0.25, 0.25, "both", 0, 0, "end");

            addToContainer(mainCenterPanel, scrollPane, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0, null);

            //##########################################################
            // Main GUI Complete
            //#########################################################
            splashScreenDemo.increaseBar(10);

            //#############################################################################################################
            // Table Setup
            //#############################################################################################################
            String tableName;

            //########################################
            // Getting ID's of Meals Of Chosen Plan
            //########################################
            String query = String.format("SELECT MealInPlanID, Meal_Name FROM mealsInPlan WHERE PlanID = %s ORDER BY MealInPlanID;", tempPlanID);

            ArrayList<ArrayList<String>> plan_Meal_IDs_And_Name = db.getMultiColumnQuery(query);
            plan_Meal_IDs_And_Name = plan_Meal_IDs_And_Name!=null ? plan_Meal_IDs_And_Name:new ArrayList<>();

            //#########################################################################################
            // Macro Targets & Macros Left Setup
            //########################################################################################
            JPanel macrosInfoJPanel = new JPanel(new GridBagLayout());

            addToContainer(scrollJPanelEnd, macrosInfoJPanel, 0, pos++, 1, 1, 0.25, 0.25, "horizontal", 0, 0, "end");

            //#####################################
            // Macro Targets Setup
            //#####################################
            tableName = "plan_Macro_Target_Calculations";
            String planCalcQuery = String.format("SELECT * from %s  WHERE PlanID = %s;", tableName, tempPlanID);

            Object[][] planData = db.getTableDataObject(planCalcQuery, tableName)!=null ? db.getTableDataObject(planCalcQuery, tableName):new Object[0][0];
            String[] macroTargetsTable_ColumnNames = db.getColumnNames(tableName)!=null ? db.getColumnNames(tableName):new String[0];

            macros_Targets_Table = new MacrosTargetsTable(db, macrosInfoJPanel, databaseName, planData, macroTargetsTable_ColumnNames, planID,
                    tableName, new ArrayList<>(Arrays.asList(macroTargetsTable_ColumnNames)), null, macrosTargets_Table_ColToHide);

            macros_Targets_Table.setOpaque(true); //content panes must be opaque

            macros_Targets_Table.setTableHeaderFont(new Font("Dialog", Font.BOLD, 14));
            macros_Targets_Table.setTableTextFont(new Font("Dialog", Font.PLAIN, 14));

            addToContainer(macrosInfoJPanel, macros_Targets_Table, 0, 1, 1, 1, 0.25, 0.25, "both", 40, 0, null);
            resizeGUI();

            //########################################
            // macroTargets Complete
            //########################################
            splashScreenDemo.increaseBar(10);

            //########################################################################################
            // planMacrosLeft Table Setup
            //########################################################################################
            tableName = "planMacrosLeft";
            String macrosQuery = String.format("SELECT * from %s  WHERE PlanID = %s;", tableName, tempPlanID);

            Object[][] macrosData = db.getTableDataObject(macrosQuery, tableName)!=null ? db.getTableDataObject(macrosQuery, tableName):new Object[0][0];
            String[] macros_columnNames = db.getColumnNames(tableName)!=null ? db.getColumnNames(tableName):new String[0];

            macrosLeft_JTable = new MacrosLeftTable(db, macrosInfoJPanel, databaseName, macrosData, macros_columnNames, planID,
                    tableName, new ArrayList<>(Arrays.asList(macros_columnNames)), null, macrosLeft_Table_ColToHide);

            macrosLeft_JTable.setOpaque(true); //content panes must be opaque

            // macrosLeft_JTable.SetUp_HiddenTableColumns(macrosLeft_Table_Hidden_Col); // NOW

            macrosLeft_JTable.setTableHeaderFont(new Font("Dialog", Font.BOLD, 14));
            macrosLeft_JTable.setTableTextFont(new Font("Dialog", Font.PLAIN, 14));

            addToContainer(macrosInfoJPanel, macrosLeft_JTable, 0, 2, 1, 1, 0.25, 0.25, "both", 30, 0, null);
            resizeGUI();

            //########################################
            // macroTargets Complete
            //########################################
            splashScreenDemo.increaseBar(10);

            //########################################################################################
            // Ingredients In Meal Calculation && Total_Meal_View  JTable Setup
            //########################################################################################

            // Table Variables
            tableName = "total_meal_view";
            meal_total_columnNames = db.getColumnNames(tableName)!=null ? db.getColumnNames(tableName):new String[0];

            tableName = "ingredients_in_sections_of_meal_calculation";
            ingredients_ColumnNames = db.getColumnNames(tableName)!=null ? db.getColumnNames(tableName):new String[0];

            boolean errorFound = false;
            for (int i = 0; i < no_of_meals; i++)
            {
                //#####################################################
                // Get MealID  & Name For Meal
                //#####################################################
                int mealInPlanID = Integer.parseInt(plan_Meal_IDs_And_Name.get(i).get(0)); // MealID's From Original Plan Not Temp
                String mealName = plan_Meal_IDs_And_Name.get(i).get(1);

                //#####################################################
                // Get MealID's Of SubMeals
                //#####################################################
                ArrayList<ArrayList<String>> subMealsInMealArrayList = db.getMultiColumnQuery(String.format("SELECT DivMealSectionsID FROM dividedMealSections WHERE MealInPlanID = %s;", mealInPlanID));

                if(subMealsInMealArrayList == null)
                {
                    String message = "\n\nError, gathering sub-meals ID for meal named ' %s ' ! \nA meal must have 1 sub-meal minimum!";

                    System.out.printf(message);
                    JOptionPane.showMessageDialog(null, message);

                    errorFound = true;
                    break;
                }

                ArrayList<String> subMealsArrayList = subMealsInMealArrayList.get(0);

                //#####################################################
                // Create Collapsible Panel
                //#####################################################
                mealNo++;
                CollapsibleJPanel collapsibleJTable = create_CollapsibleJPanel(true, scrollJPanelCenter, mealInPlanID, mealName, mealNo, subMealsArrayList, meal_total_columnNames, ingredients_ColumnNames,
                        macrosLeft_JTable);

                //######################################################
                // Adding Collapsible Objects && JTables To GUI
                //######################################################

                // adding  CollapsibleOBJ to interface
                addToContainer(scrollJPanelCenter, collapsibleJTable, 0, pos++, 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);

                //Space Divider between each CollapsibleObj
                JPanel spaceDivider = new JPanel();
                jTableBeingAdded.setSpaceDivider(spaceDivider);
                addToContainer(scrollJPanelCenter, spaceDivider, 0, pos++, 1, 1, 0.25, 0.25, "both", 50, 0, null);

                //######################################################
                // Update Progress
                //######################################################
                splashScreenDemo.increaseBar(1 + subMealsInMealArrayList.size()); // + original meal + the sub-meal
            }

            //##########################################
            // Make frame visible
            //##########################################
            if(! errorFound)
            {
                frame.setVisible(true); // HELLO REMOVE
            }
        }
        else
        {
            JOptionPane.showMessageDialog(null, "No Chosen Plan");
        }
    }

    public void setFrameVisibility(boolean x)
    {
        frame.setVisible(x);
    }

    public boolean getIngredientsTypesAndStoresData(boolean getMapIngredientsTypesToNames, boolean getIngredientsTypes, boolean getIngredientStores)
    {
        //#################################################################################
        //
        //#################################################################################

        boolean errorFound = false;
        String errorTxt = "";
        //#################################################################################
        // Map IngredientTypes  To IngredientNames
        //#################################################################################

        if (getMapIngredientsTypesToNames)
        {
            if (!updateIngredientTypesMappedToIngredientsName())
            {
                errorTxt += "\n\nUnable to get IngredientTypesToNames";
                errorFound = true;
            }
        }

        //#################################################################################
        // Get All The IngredientsType Inside The DB
        //#################################################################################

        if (getIngredientsTypes)
        {
            if (ingredientsTypesList!=null)
            {
                ingredientsTypesList.clear();
            }

            ingredientsTypesList = db.getSingleColumnQuery_AlphabeticallyOrderedTreeSet("SELECT Ingredient_Type_Name FROM ingredientTypes;");

            if (ingredientsTypesList==null)
            {
                errorTxt += "\n\nUnable to get IngredientTypes";
                errorFound = true;
            }
        }

        //#################################################################################
        // Get All The Store Names Inside The DB
        //#################################################################################

        if (getIngredientStores)
        {
            if (storesNamesList!=null)
            {
                storesNamesList.clear();
            }

            storesNamesList = db.getSingleColumnQuery_AlphabeticallyOrderedTreeSet("SELECT Store_Name FROM stores;");

            if (storesNamesList==null)
            {
                errorTxt += "\n\nUnable to get storesList";
                errorFound = true;
            }
        }

        //#################################################################################
        //
        //#################################################################################

        if (errorFound)
        {
            JOptionPane.showMessageDialog(frame, String.format("\n\nError \n%s", errorTxt));
            return false;
        }

        System.out.printf("\nIngredient Types & Names Successfully transferred! \n\n%s", lineSeparator);
        return true;
    }

    public boolean updateIngredientTypesMappedToIngredientsName()
    {

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
                ORDER BY Ingredient_Type_Name;""");

        ArrayList<ArrayList<String>> ingredientTypesNameAndIDResults = db.getMultiColumnQuery(queryIngredientsType);

        if (ingredientTypesNameAndIDResults==null)
        {
            JOptionPane.showMessageDialog(null, "\n\nUnable to update Ingredient Type Info");
            return false;
        }

        //###########################################################
        // Clear List
        //###########################################################

        map_ingredientTypesToNames.clear();

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
            ArrayList<String> ingredientNames = db.getSingleColumnQuery_ArrayList(queryTypeIngredientNames);

            if (ingredientNames==null)
            {
                errorTxt += String.format("\nUnable to grab ingredient names for Type '%s'!", ingredientType);
                continue;
            }

            //########################################
            // Mapping Ingredient Type to Names
            //########################################
            map_ingredientTypesToNames.put(ingredientType, ingredientNames);

            //System.out.printf("\n\nType %s\n%s",ingredientType, ingredientNames);
        }

        if (errorTxt.length() > 0)
        {
            JOptionPane.showMessageDialog(null, String.format("Had Errors Trying to map ingredientTypes to IngredientNames: \n\n%s", errorTxt));
            return false;
        }

        return true;
    }

    //##################################################################################################################
    // Frequently Used Methods
    //##################################################################################################################
    private CollapsibleJPanel create_CollapsibleJPanel(boolean mealInDB, Container container, Integer mealID, String mealName, int mealNo, ArrayList<String> subMealIDs, String[] mealTotalTable_ColumnNames,
                                                       String[] ingredients_ColumnNames, MacrosLeftTable macrosLeft_JTable)
    {
        //########################################################################
        // Collapsible JPanel Button Title
        //########################################################################
        CollapsibleJPanel collapsibleJpObj = new CollapsibleJPanel(container, String.format("   Meal   %s", mealNo), 150, 50);
        JPanel collapsibleJPanel = collapsibleJpObj.getCentreJPanel();
        collapsibleJPanel.setBackground(Color.YELLOW);

        //########################################################################
        // Icons Top RIGHT
        //########################################################################
        JPanel eastJPanel = collapsibleJpObj.getEastJPanel();
        eastJPanel.setLayout(new GridBagLayout());

        IconPanel iconPanel = new IconPanel(1, 10, "East");
        JPanel iconPanelInsert = iconPanel.getIconJpanel();

        addToContainer(eastJPanel, iconPanel.getIconAreaPanel(), 0, 0, 1, 1, 0.25, 0.25, "horizontal", 10, 0, null);

        //##########################
        //Add BTN
        //##########################
        IconButton add_Icon_Btn = new IconButton("src/images/add/add.png", "", 40, 40, 40, 40, "centre", "right");
        // add_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));

        JButton add_Btn = add_Icon_Btn.returnJButton();
        add_Icon_Btn.makeBTntransparent();

        add_Btn.addActionListener(ae -> {

        });

        iconPanelInsert.add(add_Icon_Btn);

        //########################################################################
        //  Total Meal Calculation JTable
        //########################################################################
        String tableName = "total_meal_view";

        JPanel southPanel = collapsibleJpObj.getSouthJPanel();

        String query = String.format("SELECT *  FROM total_meal_view WHERE MealInPlanID = %s AND PlanID = %s;", mealID, tempPlanID);
        Object[][] result = db.getTableDataObject(query, tableName);

        Object[][] meal_Total_Data = result!=null ? result:new Object[0][0];

        TotalMealTable total_Meal_View_Table = new TotalMealTable(db, collapsibleJpObj, databaseName, meal_Total_Data, mealTotalTable_ColumnNames, planID,
                mealID, mealName, tableName, new ArrayList<>(Arrays.asList(mealTotalTable_ColumnNames)), null,  totalMeal_Table_ColToHide);

        total_Meal_View_Table.setOpaque(true); //content panes must be opaque
        total_Meal_View_Table.setTableHeaderFont(new Font("Dialog", Font.BOLD, 12));

        //########################################################################
        //  Ingredients_In_Meal_Calculation JTable
        //########################################################################
/*
        // Getting Ingredients In Meal
        query = String.format("SELECT *  FROM ingredients_in_meal_calculation WHERE MealID = %s AND PlanID = %s ORDER BY Ingredients_Index;", mealID, tempPlanID);
        tableName = "ingredients_in_meal_calculation";
        Object[][] mealData = db.getTableDataObject(query, tableName)!=null ? db.getTableDataObject(query, tableName):new Object[0][0];

        //##############################################
        // Ingredients_In_Meal_Calculation  Creation
        //##############################################


        IngredientsTable ingredients_Calculation_JTable = new IngredientsTable(db, collapsibleJpObj, map_ingredientTypesToNames, databaseName, mealData, ingredients_ColumnNames, planID, mealID, mealInDB, mealName,
                tableName, ingredientsTableUnEditableCells, ingredients_Table_Col_Avoid_Centering, ingredientsInMeal_Table_ColToHide, total_Meal_View_Table, macrosLeft_JTable);

        ingredients_Calculation_JTable.setOpaque(true); //content panes must be opaque
        jTableBeingAdded = ingredients_Calculation_JTable;

        // add ingredients JTable to list
        listOfJTables.add(ingredients_Calculation_JTable);

        //##############################################
        // Ingredients_In_Meal_Calculation Customisation
        //#############################################
        ingredients_Calculation_JTable.setTableHeaderFont(new Font("Dialog", Font.BOLD, 12));*/

        int yPos =1;
        for (String subMeal: subMealIDs)
        {

            // Getting Ingredients In Meal
            query = String.format("SELECT *  FROM ingredients_in_sections_of_meal_calculation WHERE DivMealSectionsID = %s AND PlanID = %s ORDER BY Ingredients_Index;", subMeal, tempPlanID);

            System.out.printf("\n\n%s", query);

            tableName = "ingredients_in_sections_of_meal_calculation";
            Object[][] mealData = db.getTableDataObject(query, tableName)!=null ? db.getTableDataObject(query, tableName):new Object[0][0];

            //##############################################
            // Ingredients_In_Meal_Calculation  Creation
            //##############################################

            IngredientsTable ingredients_Calculation_JTable = new IngredientsTable(db, collapsibleJpObj, map_ingredientTypesToNames, databaseName, mealData, ingredients_ColumnNames, planID, mealID, mealInDB, mealName,
                    tableName, ingredientsTableUnEditableCells, ingredients_Table_Col_Avoid_Centering, ingredientsInMeal_Table_ColToHide, total_Meal_View_Table, macrosLeft_JTable);

            ingredients_Calculation_JTable.setOpaque(true); //content panes must be opaque
            jTableBeingAdded = ingredients_Calculation_JTable;

            // add ingredients JTable to list
            listOfJTables.add(ingredients_Calculation_JTable);

            //##############################################
            // Ingredients_In_Meal_Calculation Customisation
            //#############################################
            ingredients_Calculation_JTable.setTableHeaderFont(new Font("Dialog", Font.BOLD, 12));

            // Adding Ingredients_In_Meal_Calculation to CollapsibleOBJ
            addToContainer(collapsibleJPanel, ingredients_Calculation_JTable, 0, yPos++, 1, 1, 0.25, 0.25, "both", 0, 0, null);
        }

        //##################################################################################################
        // Adding Collapsible Objects && JTables To GUI
        //##################################################################################################

        // adds space between Ingredients_In_Meal_Calculation table and total_in_meal table
        addToContainer(southPanel, new JPanel(), 0, 1, 1, 1, 0.25, 0.25, "both", 50, 0, null);

        // Adding total table to CollapsibleOBJ
        addToContainer(southPanel, total_Meal_View_Table, 0, 2, 1, 1, 0.25, 0.25, "both", 0, 0, null);

        return collapsibleJpObj;
    }

    private boolean transferPlanData(int fromPlan, int toPlan)
    {
        String query0 = String.format("""
                UPDATE `plans` AS `P`,
                (
                	SELECT Plan_Name, Vegan FROM Plans WHERE PlanID = %s
                ) AS `SRC`
                                    
                SET
                    `P`.`Plan_Name` = concat("(Temp) ",`SRC`.`Plan_Name`),`P`.`Vegan` = `SRC`.`Vegan`
                WHERE
                    `P`.`PlanID` = %s; """, fromPlan, toPlan);

        if (!(db.uploadData_Batch_Altogether(new String[]{query0})))
        {
            JOptionPane.showMessageDialog(null, "\n\ntransferPlanData() Cannot Transfer Plan Data");
            return false;
        }

        System.out.printf("\nPlanData Successfully transferred! \n\n%s", lineSeparator);
        return true;
    }

    private boolean transferTargets(int fromPlan, int toPlan, boolean deleteFromToPlan, boolean showConfirmMsg)
    {
        //####################################
        // Mysql Transferring Data
        //####################################
        String query00 = String.format("DELETE FROM macros_Per_Pound_And_Limits WHERE PlanID = %s;", toPlan);
        String query01 = String.format("DROP TABLE IF EXISTS temp_Macros;");
        String query02 = String.format("""
                CREATE table temp_Macros AS SELECT * FROM macros_Per_Pound_And_Limits 
                WHERE PlanID = %s 
                AND DateTime_Of_Creation = (SELECT MAX(DateTime_Of_Creation)  FROM macros_Per_Pound_And_Limits);""", fromPlan);
        String query03 = String.format("ALTER TABLE temp_Macros DROP COLUMN current_Weight_In_Pounds;");
        String query04 = String.format("UPDATE temp_Macros SET PlanID = %s;", toPlan);

        //####################################
        // Gathering Table Columns
        //####################################

        ArrayList<String> columnsToAvoid = new ArrayList<>(List.of("current_Weight_In_Pounds"));
        String[] macrosColumns = db.getColumnNames("macros_Per_Pound_And_Limits");

        String query05 = "INSERT INTO macros_Per_Pound_And_Limits \n(";
        int listSize = macrosColumns.length;

        for (int i = 0; i <= listSize - 1; i++)
        {
            String colToAdd = columnsToAvoid.contains(macrosColumns[i]) ? "":String.format("\n\t%s", macrosColumns[i]);

            if (i==listSize - 1)
            {
                query05 += String.format("%s \n) \nSELECT * FROM temp_Macros;", colToAdd);
                break;
            }

            query05 = !colToAdd.equals("") ? String.format("%s %s,", query05, colToAdd):query05;
        }

        //####################################

        String query06 = String.format("DROP TABLE IF EXISTS temp_Macros;");

        //####################################
        // Perform Upload
        //####################################
        String[] uploadQueries = new String[0];

        if (deleteFromToPlan)
        {
            uploadQueries = new String[]{query00, query01, query02, query03, query04, query05, query06};
        }
        else
        {
            uploadQueries = new String[]{query01, query02, query03, query04, query05, query06};
        }

        if (!(db.uploadData_Batch_Altogether(uploadQueries)))
        {
            JOptionPane.showMessageDialog(null, "\n\nCannot Transfer Targets");
            return false;
        }
        else if (showConfirmMsg)
        {
            JOptionPane.showMessageDialog(null, "\n\nTargets Successfully Saved");
        }

        System.out.printf("\nTargets Successfully transferred! \n\n%s", lineSeparator);
        return true;
    }

    private boolean transferMealIngredients(int fromPlanID, int toPlanID)
    {
        //################################################################
        // Delete temp tables if they already exist
        //################################################################
        String query0 = String.format("DROP TABLE IF EXISTS temp_ingredients_in_sections_of_meal;");
        String query1 = String.format("DROP TABLE IF EXISTS temp_dividedMealSections;");
        String query2 = String.format("DROP TABLE IF EXISTS temp_mealsInPlan;");

        String query3 = "SET FOREIGN_KEY_CHECKS = 0;"; // Disable Foreign Key Checks

        //################################################################
        // Delete Meal & Ingredient Data from toPlanID
        //################################################################
        String query4 = String.format("DELETE FROM ingredients_in_sections_of_meal WHERE PlanID = %s;", toPlanID);
        String query5 = String.format("DELETE FROM dividedMealSections WHERE PlanID = %s;", toPlanID);
        String query6 = String.format("DELETE FROM mealsInPlan WHERE PlanID = %s;", toPlanID);

        String query7 = "SET FOREIGN_KEY_CHECKS = 1;"; // Enable Foreign Key Checks

        //################################################################
        // Transferring Meals From One Plan To Another
        //################################################################
        String query8 = String.format("CREATE table temp_mealsInPlan AS SELECT * FROM mealsInPlan WHERE PlanID = %s ORDER BY MealInPlanID;", fromPlanID);
        String query9 = String.format("UPDATE temp_mealsInPlan SET PlanID = %s;", toPlanID);
        String query10 = String.format("INSERT INTO mealsInPlan SELECT * FROM temp_mealsInPlan;");

        //################################################################
        // Transferring Sections Of Meals From One Plan To Another
        //################################################################
        String query11 = String.format("CREATE table temp_dividedMealSections AS SELECT * FROM dividedMealSections WHERE PlanID = %s ORDER BY DivMealSectionsID;", fromPlanID);
        String query12 = String.format("UPDATE temp_dividedMealSections SET PlanID = %s;", toPlanID);
        String query13 = String.format("INSERT INTO dividedMealSections SELECT * FROM temp_dividedMealSections;");

        //################################################################
        // Transferring this plans Ingredients to Temp-Plan
        //################################################################
        // Create Table to transfer ingredients from original plan to temp
        String query14 = String.format(""" 
                                    
                CREATE table temp_ingredients_in_sections_of_meal AS
                SELECT i.*
                FROM ingredients_in_sections_of_meal i                                                       
                WHERE i.PlanID = %s;                  
                """, fromPlanID);

        String query15 = String.format("UPDATE temp_ingredients_in_sections_of_meal SET PlanID = %s;", toPlanID);
        String query16 = String.format("INSERT INTO ingredients_in_sections_of_meal SELECT * FROM temp_ingredients_in_sections_of_meal;");

        //####################################################
        // Update
        //####################################################
       String[] query_Temp_Data = new String[]{query0,query1,query2,query3,query4,query5,query6,query7,query8,query9,query10,query11,query12,
               query13,query14,query15,query16};

        if (!(db.uploadData_Batch_Altogether(query_Temp_Data)))
        {
            JOptionPane.showMessageDialog(null, "\n\nError, transferMealIngredients() cannot transfer meal Ingredients");
            return false;
        }

        System.out.printf("\nMealIngredients Successfully Transferred! \n\n%s", lineSeparator);
        return true;
    }

    //################################################################################################################
    //  Icon Methods & ActionListener Events
    //################################################################################################################
    private void iconSetup(Container mainNorthPanel)
    {
        int width, height;
        //##############################################################################################################
        // Top Bar Icon AREA
        //##############################################################################################################
        //Creating JPanels for the area

        IconPanel iconPanel = new IconPanel(6, 10, "East");
        JPanel iconPanelInsert = iconPanel.getIconJpanel();

        addToContainer(mainNorthPanel, iconPanel.getIconAreaPanel(), 0, 0, 1, 1, 0.25, 0.25, "horizontal", 10, 0, null);

        //##########################
        //  ScrollBar Bottom
        //##########################

        width = 51;
        height = 51;

        IconButton down_ScrollBar_Icon_Btn = new IconButton("src/images/scrollBar_Down/scrollBar_Down5.png", "", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor

        down_ScrollBar_Icon_Btn.makeBTntransparent();

        JButton down_ScrollBar_Btn = down_ScrollBar_Icon_Btn.returnJButton();

        down_ScrollBar_Btn.addActionListener(ae -> {

            scrollBarDown_BTN_Action();
        });

        iconPanelInsert.add(down_ScrollBar_Btn);

        //##########################
        // Refresh Icon
        //##########################
        width = 50;
        height = 50;

        IconButton refresh_Icon_Btn = new IconButton("src/images/refresh/++refresh.png", "", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor

        JButton refresh_Btn = refresh_Icon_Btn.returnJButton();
        refresh_Icon_Btn.makeBTntransparent();

        refresh_Btn.addActionListener(ae -> {
            refreshPlan(true);
        });

        iconPanelInsert.add(refresh_Icon_Btn);

        //##########################
        //Add BTN
        //##########################
        width = 50;
        height = 52;

        IconButton add_Icon_Btn = new IconButton("src/images/add/++add.png", "", width, height, width, height,
                "centre", "right");

        JButton add_Btn = add_Icon_Btn.returnJButton();
        add_Icon_Btn.makeBTntransparent();

        add_Btn.addActionListener(ae -> {

            addMealToPlan();
        });

        iconPanelInsert.add(add_Icon_Btn);

        //##########################
        // Update Icon
        //##########################

        width = 54;
        height = 57;

        IconButton saveIcon_Icon_Btn = new IconButton("src/images/save/+++save.png", "", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor
        //saveIcon_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));
        saveIcon_Icon_Btn.makeBTntransparent();

        JButton save_btn = saveIcon_Icon_Btn.returnJButton();


        save_btn.addActionListener(ae -> {

            saveMealData(true, true);
        });

        iconPanelInsert.add(save_btn);

        //##########################
        //  Add_Ingredients Icon
        //##########################

        width = 54;
        height = 57;

        IconButton add_Ingredients_Icon_Btn = new IconButton("src/images/add_Ingredients/add_Ingredients.png", "", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor
        //saveIcon_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));
        add_Ingredients_Icon_Btn.makeBTntransparent();

        JButton add_Ingredients_Btn = add_Ingredients_Icon_Btn.returnJButton();


        add_Ingredients_Btn.addActionListener(ae -> {

            open_AddIngredients_Screen();
        });

        iconPanelInsert.add(add_Ingredients_Btn);

        //##########################
        //  Macro_Targets Icon
        //##########################

        width = 54;
        height = 50;

        IconButton macro_Targets_Icon_Btn = new IconButton("src/images/targets/target.png", "", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor
        //saveIcon_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));
        macro_Targets_Icon_Btn.makeBTntransparent();

        JButton macro_Tagets_Btn = macro_Targets_Icon_Btn.returnJButton();


        macro_Tagets_Btn.addActionListener(ae -> {

            open_MacrosTargets_Screen();
        });

        iconPanelInsert.add(macro_Tagets_Btn);
    }

    private Boolean areYouSure(String process)
    {
        int reply = JOptionPane.showConfirmDialog(frame, String.format("Are you sure you want to %s, \nany unsaved changes will be lost in this Table! \nDo you want to %s?", process, process),
                "Restart Game", JOptionPane.YES_NO_OPTION); //HELLO Edit

        if (reply==JOptionPane.NO_OPTION || reply==JOptionPane.CLOSED_OPTION)
        {
            return false;
        }
        return true;
    }

    //######################################
    // Icon Button Actions
    //######################################
    public void scrollBarDown_BTN_Action()
    {
        //##############################################
        // Set ScrollPane to the Bottom Straight Away
        //##############################################
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());

        //OR

        /*
        //##############################################
        // Set ScrollPane to the Bottom Animated View
        //##############################################
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();

        int currentScrollValue = verticalBar.getValue();
        int previousScrollValue = -1;

        while (currentScrollValue != previousScrollValue) {
            // Scroll down a bit
            int downDirection = 1;
            int amountToScroll = verticalBar.getUnitIncrement(downDirection);
            verticalBar.setValue(currentScrollValue + amountToScroll);

            previousScrollValue = currentScrollValue;
            currentScrollValue = verticalBar.getValue();
        }
        */

    }

    private void addMealToPlan()
    {
        if (!(get_IsPlanSelected()))
        {
            return;
        }

        // If No Plan Is Selected
        if (planID==null)
        {
            JOptionPane.showMessageDialog(null, "\n\nCannot Add A  Meal As A Plan Is Not Selected! \nPlease Select A Plan First!!");
            return;
        }

        //##################################
        //Getting user input for Meal Name
        //#################################
        String newMealName = JOptionPane.showInputDialog("Input Meal Name?");

        if (newMealName==null || newMealName.length()==0)
        {
            JOptionPane.showMessageDialog(null, "\n\nPlease Input A Valid Name With 1+ Characters!");
            return;
        }

        //##################################
        // Error Un-Unique Name //HELLO  THis process Could be simplified
        //#################################

        // Does Meal Exist Within Temp Plan
        String mealInTempPlan = String.format("Select Meal_Name FROM Meals WHERE Meal_Name = '%s' AND PlanID = %s;", newMealName, tempPlanID);

        if (!(db.getSingleColumnQuery(mealInTempPlan)==null))
        {
            JOptionPane.showMessageDialog(null, "\n\n ERROR:\n\nMeal Name Already Exists Within This Plan!!");
            return;
        }

        // Does meal exist in original unsaved plan
        String mealInPlan = String.format("Select Meal_Name FROM Meals WHERE Meal_Name = '%s' AND PlanID = %s;", newMealName, planID);

        if (!(db.getSingleColumnQuery(mealInPlan)==null))
        {
            JOptionPane.showMessageDialog(null, "\n\n ERROR:\n\nMeal Name Already Exists Within The Original Meal Plan\n " +
                    "Please Save This Plan Before Attempting To Create A Meal With This Name! ");
            return;
        }

        //##################################
        // Upload Meal To Temp Plan
        //#################################
        String uploadQuery = String.format(" INSERT INTO meals (PlanID, Meal_Name) VALUES (%s,'%s')", tempPlanID, newMealName);

        if (!(db.uploadData_Batch_Altogether(new String[]{uploadQuery})))
        {
            JOptionPane.showMessageDialog(null, "\n\n ERROR:\n\nCreating Meal In DB!");
            return;
        }

        //##################################
        // Get Meal ID
        //##################################

        String query = String.format("Select MealID from meals WHERE PlanID = %s AND  Meal_Name = '%s';", tempPlanID, newMealName);
        String[] results = db.getSingleColumnQuery(query);

        if (results==null)
        {
            JOptionPane.showMessageDialog(null, "\n\n ERROR:\n\nCannot Get Created Meals ID!!");

            String deleteQuery = String.format("DELETE FROM  meals WHERE planID = %s AND  MealName = '%s';)", tempPlanID, newMealName);
            if (!(db.uploadData_Batch_Altogether(new String[]{deleteQuery})))
            {
                JOptionPane.showMessageDialog(null, "\n\n ERROR:\n\nUnable To Undo Errors Made!\n\nRecommendation Action: Refresh This Plan");
            }

            return;
        }

        Integer mealID = Integer.valueOf(results[0]);

        System.out.printf("\n\nTemp Meal ID: %s", mealID);

        //#############################################
        // Create New Meal Table In GUI
        //##############################################

        CollapsibleJPanel collapsibleJTable = create_CollapsibleJPanel(false, scrollJPanelCenter, mealID, newMealName, mealNo, null, meal_total_columnNames, ingredients_ColumnNames,
                macrosLeft_JTable);

        //###############################################
        // Adding Collapsible Objects && JTables To GUI
        //################################################


        // adding  CollapsibleOBJ to interface
        addToContainer(scrollJPanelCenter, collapsibleJTable, 0, pos++, 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);

        //Space Divider between each CollapsibleObj
        JPanel spaceDivider = new JPanel();
        //spaceDivider.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
        jTableBeingAdded.setSpaceDivider(spaceDivider);
        addToContainer(scrollJPanelCenter, spaceDivider, 0, pos++, 1, 1, 0.25, 0.25, "both", 50, 0, null);

        //##############################################
        // Success Message
        //##############################################

        JOptionPane.showMessageDialog(null, "\n\n Meal Successfully Created!!");

        //##############################################
        // Resize & Update Containers
        //##############################################
        resizeGUI();

        scrollBarDown_BTN_Action();
    }

    private void refreshPlan(boolean askPermission)
    {
        if ((!(get_IsPlanSelected())) || askPermission && !(areYouSure("Refresh Data")))
        {
            return;
        }

        //####################################################################
        // Refresh DB Data
        //####################################################################
        if (!(transferMealIngredients(planID, tempPlanID))) // transfer meals and ingredients from temp plan to original plan
        {
            JOptionPane.showMessageDialog(frame, "`\n\nError couldn't transfer ingredients data from temp to real plan !!");
            return;
        }

        //####################################################################
        // Refresh ingredients meal table & total Tables Data
        //####################################################################
        Iterator<IngredientsTable> it = listOfJTables.iterator();
        while (it.hasNext())
        {
            IngredientsTable ingredientsTable = it.next();

            // if meal is not saved in DB remove the meal
            if (!(ingredientsTable.getMealInDB()))
            {
                ingredientsTable.deleteTableAction(); // delete table from db
                it.remove(); // remove from list
                continue;
            }

            // Refresh Table data
            ingredientsTable.refresh(false);
        }

        //####################################################################
        // Refresh Macro-Targets Table
        //####################################################################
        refreshMacroTargets(); // if macroTargets changed ask the user if they would like to refresh this data
        refreshMacrosLeft();
    }

    private void saveMealData(boolean askPermission, boolean showMsg)
    {

        // ##############################################################################
        // Exit Clauses
        // ##############################################################################

        // If no plan is selected exit Or,  if user rejects saving when asked  exit
        if ((!(get_IsPlanSelected())) || askPermission && !(areYouSure("Save Data")))
        {
            return;
        }

        // ###############################################################################
        // Removing Meals that have been deleted
        // ##############################################################################
        Iterator<IngredientsTable> it = listOfJTables.iterator();

        int listSize = listOfJTables.size(), errorCount = 0;

        while (it.hasNext())
        {
            IngredientsTable table = it.next();

            // If objected is deleted, completely delete it then skip to next JTable
            if (table.getObjectDeleted())
            {
                table.completely_Deleted_JTables();
                it.remove();
            }
        }

        // ##############################################################################
        // Save meal plan data in DB
        // ##############################################################################
        if (listSize==0) // if there are no meals in the temp plan delete all meals / ingredients from original plan
        {
            System.out.println("\n\n#################################### \n1.) saveMealData() Empty Meal Plan Save");

            String query0 = "SET FOREIGN_KEY_CHECKS = 0;"; // Disable Foreign Key Checks
            String query1 = String.format("DELETE FROM ingredients_in_meal  WHERE PlanID = %s;", planID);
            String query2 = String.format("DELETE FROM meals  WHERE PlanID = %s;", planID);
            String query3 = "SET FOREIGN_KEY_CHECKS = 1;"; // Enable Foreign Key Checks

            if (!(db.uploadData_Batch_Altogether(new String[]{query0, query1, query2, query3})))
            {
                JOptionPane.showMessageDialog(frame, "\n\n1.)  Error \nUnable to save meals in plan!");
                return;
            }
        }
        else if ((!(transferMealIngredients(tempPlanID, planID)))) // transfer meals and ingredients from temp plan to original plan
        {
            System.out.println("\n\n#################################### \n2.) saveMealData() Meals Transferred to Original Plan");

            JOptionPane.showMessageDialog(frame, "\n\n2.)  Error \nUnable to save meals in plan!");
            return;
        }

        // ##############################################################################
        // Save IngredientsTable Data
        // ##############################################################################

        Iterator<IngredientsTable> it2 = listOfJTables.iterator();
        while (it2.hasNext())
        {
            IngredientsTable table = it2.next();

            if (!(table.getMealInDB()))
            {
                table.set_Meal_In_DB(true);
            }

            if (!(table.updateTableModelData()))
            {
                errorCount++;
            }
        }

        // #####################################
        // If error occurred above exit
        // #####################################
        if (errorCount > 0)
        {
            JOptionPane.showMessageDialog(frame, "\n\n Error \n3.) Unable to save all meals in plan! \n\nPlease retry again!");
            return;
        }

        // ##############################################################################
        // Update MacrosLeft Targets
        // ##############################################################################
        if (!(macrosLeft_JTable.updateMacrosLeftTableModelData()))
        {
            JOptionPane.showMessageDialog(frame, "\n\n Error \n4.) Unable to save MacrosLeftTable! \n\nPlease retry again!");
            return;
        }

        // ##############################################################################
        // Successful Message
        // ##############################################################################
        JOptionPane.showMessageDialog(frame, "\n\nAll Meals Are Successfully Saved!");

    }

    //##################################################################################################################
    //  Macro Targets  Screen Methods
    //##################################################################################################################
    private void saveMacroTargets(boolean askPermission, boolean showUpdateMsg)
    {
        // ##############################################
        // If targets haven't changed exit
        // ##############################################
        if (!getMacrosTargetsChanged())
        {
            return;
        }

        // ################################################
        // If askPermission as permission to save targets
        // ################################################
        if (askPermission)
        {
            int reply = JOptionPane.showConfirmDialog(frame, String.format("Would you like to save your MacroTarget  Changes Too?"),
                    "Save Macro Targets", JOptionPane.YES_NO_OPTION); //HELLO Edit

            if (reply==JOptionPane.NO_OPTION || reply==JOptionPane.CLOSED_OPTION)
            {
                return;
            }
        }

        if (transferTargets(tempPlanID, planID, false, showUpdateMsg))
        {
            macrosTargetsChanged(false);
            updateTargetsAndMacrosLeft();
        }
    }

    public void updateTargetsAndMacrosLeft()
    {
        macros_Targets_Table.updateMacrosTargetsTable();
        macrosLeft_JTable.updateMacrosLeftTable();
    }

    //##########################################
    // Refresh macrosLeft & macroTargets Table
    //#########################################

    private void refreshMacroTargets()
    {
        // ##############################################
        // If targets have changed, save them?
        // ##############################################
        if (getMacrosTargetsChanged())
        {
            int reply = JOptionPane.showConfirmDialog(frame, String.format("Would you like to refresh your MacroTargets Too?"),
                    "Refresh Macro Targets", JOptionPane.YES_NO_OPTION); //HELLO Edit

            if (reply==JOptionPane.YES_OPTION)
            {
                if (transferTargets(planID, tempPlanID, true, false))
                {
                    JOptionPane.showMessageDialog(frame, "\n\nMacro-Targets Successfully Refreshed!!");
                    macrosTargetsChanged(false);

                    macros_Targets_Table.refreshData();
                }
            }
        }
    }

    private void refreshMacrosLeft()
    {
        macrosLeft_JTable.refreshData();
    }

    //###############################################################################################################
    //  Opening & Closing External Screen
    //###############################################################################################################

    // Macro Targets Screen
    private void open_MacrosTargets_Screen()
    {
        if (!(get_IsPlanSelected()))
        {
            return;
        }

        if (macrosTargets_Screen!=null)
        {
            macrosTargets_Screen.makeJframeVisible();
            return;
        }
        macrosTargets_Screen = new Macros_Targets_Screen(db, this, planID, tempPlanID, planName);
    }

    public void remove_macrosTargets_Screen()
    {
        macrosTargets_Screen = null;
    }

    // Add Ingredients Screen
    private void open_AddIngredients_Screen()
    {
        if (!(get_IsPlanSelected()))
        {
            return;
        }

        if (ingredientsInfoScreen!=null)
        {
            ingredientsInfoScreen.makeFrameVisible();
            return;
        }

        ingredientsInfoScreen = new Parent_Ingredients_Info_Screen(db, this, planID, tempPlanID, planName,
                map_ingredientTypesToNames, ingredientsTypesList, storesNamesList);
    }

    public void remove_Ingredients_Info_Screen()
    {
        ingredientsInfoScreen = null;
    }

    //################################################################################################################
    //  Mutator Methods
    //################################################################################################################

    public void macrosTargetsChanged(boolean bool)
    {
        macroTargetsChanged = bool;
    }

    public void updateIngredientsNameAndTypesInJTables(boolean ingredientsAddedOrRemove)
    {
        if (ingredientsAddedOrRemove)
        {
            //#####################################
            // Save Plan & Refresh Plan
            //#####################################
            saveMealData(false, false); // Save Plan
            refreshPlan(false); // Refresh Plan
        }
    }

    //################################################################################################################
    //  Accessor Methods
    //################################################################################################################

    public JFrame getFrame()
    {
        return frame;
    }

    public boolean get_IsPlanSelected()
    {
        if (planID==null)
        {
            JOptionPane.showMessageDialog(frame, "Please Select A Plan First!");
            return false;
        }
        return true;
    }

    public boolean getMacrosTargetsChanged()
    {
        return macroTargetsChanged;
    }

    //##################################################################################################################
    // Sizing & Adding to GUI Methods
    //##################################################################################################################
    public void resizeGUI()
    {
        scrollJPanelCenter.revalidate();
        scrollPaneJPanel.revalidate();
        contentPane.revalidate();
    }

    private void addToContainer(Container container, Component addToContainer, Integer gridx, Integer gridy, Integer gridwidth,
                                Integer gridheight, Double weightx, Double weighty, String fill, Integer ipady, Integer ipadx, String anchor)
    {
        if (gridx!=null)
        {
            gbc.gridx = gridx;
        }
        if (gridy!=null)
        {
            gbc.gridy = gridy;
        }

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

        if (anchor!=null)
        {
            switch (anchor.toLowerCase())
            {
                case "start":
                    gbc.anchor = GridBagConstraints.PAGE_START;
                    break;

                case "end":
                    gbc.anchor = GridBagConstraints.PAGE_END;
                    break;
            }
        }

        container.add(addToContainer, gbc);
    }
}
