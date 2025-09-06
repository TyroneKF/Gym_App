package App_Code.Objects.Screens.Others;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.MacrosLeftTable;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.MacrosTargetsTable;
import App_Code.Objects.Database_Objects.MealManager;
import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Gui_Objects.IconPanel;
import App_Code.Objects.Gui_Objects.ScrollPaneCreator;
import App_Code.Objects.Screens.Ingredient_Info.Edit_Ingredients_Info.Ingredients_Info_Screen;
import App_Code.Objects.Screens.Others.Loading_Screen.LoadingScreen;
import io.github.cdimascio.dotenv.Dotenv;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class Meal_Plan_Screen extends JPanel
{
    private static boolean
            production = false;

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
    private MacrosLeftTable macrosLeft_JTable;
    private MacrosTargetsTable macros_Targets_Table;

    //##################################################################################################################
    //
    //##################################################################################################################
    private final static String
            version_no = "00001",
            db_Scripts_Folder_Path = "/data/database_scripts",
            db_File_Script_List_Name = "0.) Script_List.txt",
            db_File_Tables_Name = "0.) Database_Names.txt";

    private static String
            user_name = "root",
            password = "password",
            databaseName = "gymapp" + version_no;

    private String JFrameName = databaseName;

    //##################################################################################################################
    // Objects
    //##################################################################################################################
    private GridBagConstraints gbc = new GridBagConstraints();
    private JFrame frame = new JFrame(JFrameName);
    private JPanel scrollPaneJPanel, scrollJPanelCenter, scrollJPanelEnd;
    private Container contentPane;
    private ScrollPaneCreator scrollPane;

    private MyJDBC db;
    private Macros_Targets_Screen macrosTargets_Screen = null;
    private Ingredients_Info_Screen ingredientsInfoScreen = null;
    private ArrayList<MealManager> mealManagerArrayList = new ArrayList<>();

    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private String[] meal_total_columnNames, ingredients_ColumnNames;

    private static Integer user_id;
    private String planName;
    private Integer tempPlanID = 1, planID;
    private int containerYPos = 0, frameHeight = 1082, frameWidth = 1925;

    private boolean macroTargetsChanged = false;

    //##################################################################################################################
    // Ingredients Table Columns
    //##################################################################################################################

    // Table: ingredients_in_sections_of_meal_calculation
    private final ArrayList<String>
            ingredients_Table_Col_Avoid_Centering = new ArrayList<>(Arrays.asList(
            "ingredient_type", "ingredient_name", "supplier", "product_name")),

            ingredientsTableUnEditableCells = new ArrayList<>(Arrays.asList(
            "ingredients_index", "ingredient_id", "ingredient_cost", "protein", "gi", "carbohydrates", "sugars_of_carbs",
            "fibre", "fat", "saturated_fat", "salt", "water_content", "liquid_content", "calories")),

             ingredientsInMeal_Table_ColToHide = new ArrayList<>(Arrays.asList("plan_id", "div_meal_sections_id"));

    //##################################################################################################################
    // Other Table Customisations
    //##################################################################################################################
    private final ArrayList<String>

            // Table : total_meal_view Table
            totalMeal_Table_ColToHide = new ArrayList<String>(Arrays.asList("plan_id","meal_name", "meal_in_plan_id")),

           // Table : plan_Macro_Target_Calculations
           macrosTargets_Table_ColToHide = new ArrayList<String>(Arrays.asList("plan_id", "plan_name", "date_time_of_creation")),

           // Table : planMacrosLeft
           macrosLeft_Table_ColToHide = new ArrayList<String>(Arrays.asList("plan_id", "plan_name"));

    //########################################################

    String lineSeparator = "###############################################################################";

    public static void main(String[] args)
    {
        if (production)
        {
            try
            {
                /*
                // Using System Variables (OS LVL)
                String host = System.getenv("GYM_APP_DB_HOST");
                String port = System.getenv("GYM_APP_DB_PORT");
                String user = System.getenv("GYM_APP_DB_USER");
                String password = System.getenv("GYM_APP_DB_PASS");
                String dbName = System.getenv("GYM_APP_DB_NAME");
                */

                // #########################################
                // Set Path Files
                // #########################################
                String userDirectory = new File("").getAbsolutePath(); // get path file of where this is being executed

                System.out.printf("\nDirectory: \n%s \n\n\nScripts Directory:\n%s", userDirectory, db_Scripts_Folder_Path);
                System.out.println("\n\n\nReading ENV Variables: host, port, user, ****, db_name");

                // #########################################
                // Get .env variables
                // #########################################
                Dotenv dotenv = Dotenv.configure()
                        .directory(userDirectory)
                        .filename(".env") // instead of '.env', use 'env'
                        .load();

                String host = dotenv.get("DB_HOST");
                String port = dotenv.get("DB_PORT");
                String user = dotenv.get("DB_USER");
                String password = dotenv.get("DB_PASS");

                String dbName = dotenv.get("DB_NAME");

                if (host==null || port==null || user==null || password==null || dbName==null)
                {
                    System.err.printf("\n\nDB Values: \nhost: %s \nport: %s \nuser: %s \ndbName: %s",
                            host, port, user, dbName);

                    throw new RuntimeException("Missing one or more required DB environment variables.");
                }

                System.out.println("\n\nSuccessfully retrieved ENV Variables: host, port, user, *****, db_name");

                // #########################################
                // Assigning values to variables &
                // #########################################

                databaseName = dbName;
                user_name = user;

                // #########################################
                // Create DB Object & run SQL Scripts
                // #########################################
                MyJDBC db = new MyJDBC(true, host, port, user, password, dbName, db_Scripts_Folder_Path, db_File_Script_List_Name, db_File_Tables_Name);

                if (db.get_DB_Connection_Status())
                {
                    new Meal_Plan_Screen(db);
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "ERROR, Cannot Connect To Database!");
                    return;
                }
            }
            catch (Exception e)
            {
                System.err.printf("\n\nError Meal_Plan_Screen() \n%s", e);
                return;
            }
        }
        else
        {
            //############################################################################################################
            // Create DB Object & run SQL Script
            //#############################################################################################################

            MyJDBC db = new MyJDBC(false, "localhost", "3306", user_name, password, databaseName, db_Scripts_Folder_Path, db_File_Script_List_Name, db_File_Tables_Name);

            if (db.get_DB_Connection_Status())
            {
                new Meal_Plan_Screen(db);
            }
            else
            {
                JOptionPane.showMessageDialog(null, "ERROR, Cannot Connect To Database!");
            }
        }
    }

    public Meal_Plan_Screen(MyJDBC db)
    {
        this.db = db;

        //##############################################################################################################
        // Getting Selected  User & Plan Info
        //##############################################################################################################
        String queryX = String.format("""
                SELECT u.user_id, p.plan_id, p.plan_name 
                FROM
                (
                  SELECT plan_id, plan_name, user_id, selected_plan_flag from plans
                ) p     
                                           
                LEFT JOIN users u 
                ON u.user_id = p.user_id
                                                
                WHERE p.selected_plan_flag = TRUE AND u.user_name = '%s';""", user_name);

        ArrayList<ArrayList<String>> results1 = db.getMultiColumnQuery(queryX);

        ArrayList<String> results = results1!=null ? results1.get(0):null;

        user_id = results!=null ? Integer.parseInt(results.get(0)):null;
        planID = results!=null ? Integer.parseInt(results.get(1)):null;
        planName = results!=null ? results.get(2):null;

        if (planID == null || user_id == null || planName == null || user_name == null)
        {
            System.err.printf("\n\nUsername : %s \nUser ID : %s \n\nSelected Plan ID : %s  \nSelected Plan Name : %s\n", user_name, user_id, planID, planName);

            JOptionPane.showMessageDialog(null, "No Chosen Plan Or User");
            return;
        }

        //####################################################
        // Getting Number Of Meals & Subsections of meals
        //####################################################
        String query1 = String.format("SELECT COUNT(meal_in_plan_id) AS TotalMeals FROM mealsInPlan WHERE plan_id = %s;", planID);
        String[] mealsInPlanCount = db.getSingleColumnQuery(query1);

        String query2 = String.format("SELECT COUNT(div_meal_sections_id) AS TotalSubMeals FROM dividedMealSections WHERE plan_id = %s;", planID);
        String[] dividedMealSectionsCount = db.getSingleColumnQuery(query2);

        System.out.printf("\n\n%s \nMeals In Plan: %s\nSub-Meals In Plan: %s \n", lineSeparator, mealsInPlanCount[0], dividedMealSectionsCount[0]);

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

        LoadingScreen loadingScreen = new LoadingScreen(totalProgress, this);

        //####################################################
        // Transferring PLan Data To Temp
        //####################################################
        if (!transferPlanData(planID, tempPlanID))
        {
            loadingScreen.closeWindow();
            return;
        }

        loadingScreen.increaseBar(10);
        System.out.printf("\nChosen Plan: %s  & Chosen Plan Name: %s \n\n%s", planID, planName, lineSeparator);

        //####################################################
        // Transferring Targets From Chosen PLan to Temp
        //####################################################
        if (!transferTargets(planID, tempPlanID, true, false))
        {
            loadingScreen.closeWindow();
            return;
        }

        loadingScreen.increaseBar(10);

        //####################################################
        // Transferring this plans Meals  Info to Temp-Plan
        //####################################################

        if (!(transferMealIngredients(planID, tempPlanID)))
        {
            loadingScreen.closeWindow();
            JOptionPane.showMessageDialog(null, "\n\nCannot Create Temporary Plan In DB to Allow Editing");
            return;
        }

        loadingScreen.increaseBar(10);

        //####################################################
        // Get IngredientTypes & Store Data
        //####################################################
        if (!(getIngredientsTypesAndStoresData(true, true, true)))
        {
            loadingScreen.closeWindow();
            JOptionPane.showMessageDialog(null, "\n\nCannot Get IngredientsTypes & Stores Info \n\ngetIngredientsTypesAndStoresData()");
            return;
        }

        loadingScreen.increaseBar(10);

        //#############################################################################################################
        //   1. Create the  GUI framework
        //#############################################################################################################
        System.out.printf("\nMeal_Plan_Screen.java : Creating GUI Screen \n%s", lineSeparator); // Update

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
        loadingScreen.increaseBar(10);

        //#############################################################################################################
        // Table Setup
        //#############################################################################################################
        String tableName;

        //########################################
        // Getting ID's of Meals Of Chosen Plan
        //########################################
        String query = String.format("SELECT meal_in_plan_id, meal_name, meal_time FROM mealsInPlan WHERE plan_id = %s ORDER BY meal_time;", tempPlanID);

        ArrayList<ArrayList<String>> meals_Info_In_Plan = db.getMultiColumnQuery(query);
        meals_Info_In_Plan = meals_Info_In_Plan!=null ? meals_Info_In_Plan:new ArrayList<>();

        //#########################################################################################
        // Macro Targets & Macros Left Setup
        //########################################################################################
        JPanel macrosInfoJPanel = new JPanel(new GridBagLayout());

        addToContainer(scrollJPanelEnd, macrosInfoJPanel, 0, containerYPos++, 1, 1, 0.25, 0.25, "horizontal", 0, 0, "end");

        //#####################################
        // Macro Targets Setup
        //#####################################
        tableName = "plan_Macro_Target_Calculations";
        String planCalcQuery = String.format("SELECT * from %s  WHERE plan_id = %s;", tableName, tempPlanID);

        // HELLO LOGIC Below could be simplified as its doing a db request twice
        Object[][] planData = db.getTableDataObject(planCalcQuery, tableName)!=null ? db.getTableDataObject(planCalcQuery, tableName):new Object[0][0];
        String[] macroTargetsTable_ColumnNames = db.getColumnNames(tableName)!=null ? db.getColumnNames(tableName):new String[0];

        macros_Targets_Table = new MacrosTargetsTable(db, macrosInfoJPanel, planData, macroTargetsTable_ColumnNames, planID, tempPlanID,
                tableName, new ArrayList<>(Arrays.asList(macroTargetsTable_ColumnNames)), null, macrosTargets_Table_ColToHide);

        macros_Targets_Table.setOpaque(true); //content panes must be opaque

        macros_Targets_Table.setTableHeaderFont(new Font("Dialog", Font.BOLD, 14));
        macros_Targets_Table.setTableTextFont(new Font("Dialog", Font.PLAIN, 14));

        addToContainer(macrosInfoJPanel, macros_Targets_Table, 0, 1, 1, 1, 0.25, 0.25, "both", 40, 0, null);
        resizeGUI();

        //########################################
        // macroTargets Complete
        //########################################
        loadingScreen.increaseBar(10);

        //########################################################################################
        // planMacrosLeft Table Setup
        //########################################################################################
        tableName = "planMacrosLeft";
        String macrosQuery = String.format("SELECT * from %s  WHERE plan_id = %s;", tableName, tempPlanID);

        Object[][] macrosData = db.getTableDataObject(macrosQuery, tableName)!=null ? db.getTableDataObject(macrosQuery, tableName):new Object[0][0];
        String[] macros_columnNames = db.getColumnNames(tableName)!=null ? db.getColumnNames(tableName):new String[0];

        macrosLeft_JTable = new MacrosLeftTable(db, macrosInfoJPanel, macrosData, macros_columnNames, planID, tempPlanID,
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
        loadingScreen.increaseBar(10);

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
            int mealInPlanID = Integer.parseInt(meals_Info_In_Plan.get(i).get(0)); // MealID's From Original Plan Not Temp
            String mealName = meals_Info_In_Plan.get(i).get(1);
            String mealTime = meals_Info_In_Plan.get(i).get(2);

            //#####################################################
            // Get MealID's Of SubMeals
            //#####################################################
            String subDivQuery = String.format("SELECT div_meal_sections_id FROM dividedMealSections WHERE meal_in_plan_id = %s AND plan_id = %s;", mealInPlanID, tempPlanID);
            ArrayList<ArrayList<String>> subMealsInMealArrayList = db.getMultiColumnQuery(subDivQuery);

            if (subMealsInMealArrayList==null)
            {
                String message = String.format("\n\nError, gathering sub-meals ID for meal named ' %s ' ! \nA meal must have 1 sub-meal minimum!", mealName);

                System.out.printf("%s", message);
                JOptionPane.showMessageDialog(null, message);

                errorFound = true;
                break;
            }

            //#####################################################
            // Create Meal Component
            //#####################################################

            System.out.printf("\n\nMeal_Plan_Screen.java | MealManager \nmeal_in_plan_id : %s \nmeal_name : %s \nmeal_time : %s \nSub-Meals In MealManager (ID) : %s%n", mealInPlanID, mealName, mealTime, subMealsInMealArrayList);//HELLO DELETE
            MealManager meal = new MealManager(this, scrollJPanelCenter, mealInPlanID, mealName, mealTime, subMealsInMealArrayList);
            mealManagerArrayList.add(meal);

           //######################################################
           // Update Progress
           //######################################################
            loadingScreen.increaseBar(1 + subMealsInMealArrayList.size()); // + original meal + the sub-meal
        }

        //##########################################
        // Make frame visible
        //##########################################
        if (!errorFound)
        {
            frame.setVisible(true); // HELLO REMOVE
        }

        //open_AddIngredients_Screen();
        scrollBarUp_BTN_Action(); // scrolls mealPlan to the top
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

            ingredientsTypesList = db.getSingleColumnQuery_AlphabeticallyOrderedTreeSet("SELECT ingredient_type_name FROM ingredientTypes ORDER BY ingredient_type_name ASC;");

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

            storesNamesList = db.getSingleColumnQuery_AlphabeticallyOrderedTreeSet("SELECT store_name FROM stores ORDER BY store_name ASC;");

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
        String queryIngredientsType = """
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
                ORDER BY N.ingredient_type_name;""";

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
            String queryTypeIngredientNames = String.format("SELECT ingredient_name FROM ingredients_info WHERE ingredient_type_id = %s ORDER BY ingredient_name;", ID);
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
    private boolean transferPlanData(int fromPlan, int toPlan)
    {
        String query0 = String.format("""
                UPDATE `plans` AS `P`,
                (
                	SELECT plan_name, vegan FROM plans WHERE plan_id = %s
                ) AS `SRC`
                                    
                SET
                    `P`.`plan_name` = concat("(Temp) ",`SRC`.`plan_name`),`P`.`vegan` = `SRC`.`vegan`
                WHERE
                    `P`.`plan_id` = %s; """, fromPlan, toPlan);

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
        String query00 = String.format("DELETE FROM macros_Per_Pound_And_Limits WHERE plan_id = %s;", toPlan);
        String query01 = "DROP TABLE IF EXISTS temp_Macros;";
        String query02 = String.format("""
                CREATE table temp_Macros AS SELECT * FROM macros_Per_Pound_And_Limits 
                WHERE plan_id = %s 
                AND date_time_of_creation = (SELECT MAX(date_time_of_creation) FROM macros_Per_Pound_And_Limits);""", fromPlan);
        String query03 = "ALTER TABLE temp_Macros DROP COLUMN current_weight_in_pounds;";
        String query04 = String.format("UPDATE temp_Macros SET plan_id = %s;", toPlan);

        //####################################
        // Gathering Table Columns
        //####################################

        ArrayList<String> columnsToAvoid = new ArrayList<>(List.of("current_weight_in_pounds"));
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

        String query06 = "DROP TABLE temp_Macros;";

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
        String query0 = "DROP TABLE IF EXISTS temp_ingredients_in_sections_of_meal;";
        String query1 = "DROP TABLE IF EXISTS temp_dividedMealSections;";
        String query2 = "DROP TABLE IF EXISTS temp_mealsInPlan;";

        String query3 = "SET FOREIGN_KEY_CHECKS = 0;"; // Disable Foreign Key Checks

        //################################################################
        // Delete Meal & Ingredient Data from to plan_id
        //################################################################
        String query4 = String.format("DELETE FROM ingredients_in_sections_of_meal WHERE plan_id = %s;", toPlanID);
        String query5 = String.format("DELETE FROM dividedMealSections WHERE plan_id = %s;", toPlanID);
        String query6 = String.format("DELETE FROM mealsInPlan WHERE plan_id = %s;", toPlanID);

        String query7 = "SET FOREIGN_KEY_CHECKS = 1;"; // Enable Foreign Key Checks

        //################################################################
        // Transferring Meals From One Plan To Another
        //################################################################
        String query8 = String.format("CREATE table temp_mealsInPlan AS SELECT * FROM mealsInPlan WHERE plan_id = %s ORDER BY meal_in_plan_id;", fromPlanID);
        String query9 = String.format("UPDATE temp_mealsInPlan SET plan_id = %s;", toPlanID);
        String query10 = "INSERT INTO mealsInPlan SELECT * FROM temp_mealsInPlan;";

        //################################################################
        // Transferring Sections Of Meals From One Plan To Another
        //################################################################
        String query11 = String.format("CREATE table temp_dividedMealSections AS SELECT * FROM dividedMealSections WHERE plan_id = %s ORDER BY div_meal_sections_id;", fromPlanID);
        String query12 = String.format("UPDATE temp_dividedMealSections SET plan_id = %s;", toPlanID);
        String query13 = "INSERT INTO dividedMealSections SELECT * FROM temp_dividedMealSections;";

        //################################################################
        // Transferring this plans Ingredients to Temp-Plan
        //################################################################
        // Create Table to transfer ingredients from original plan to temp
        String query14 = String.format("""                  
                CREATE table temp_ingredients_in_sections_of_meal AS
                SELECT i.*
                FROM ingredients_in_sections_of_meal i                                                       
                WHERE i.plan_id = %s;""", fromPlanID);

        String query15 = String.format("UPDATE temp_ingredients_in_sections_of_meal SET plan_id = %s;", toPlanID);
        String query16 = "INSERT INTO ingredients_in_sections_of_meal SELECT * FROM temp_ingredients_in_sections_of_meal;";

        String query17 = "DROP TABLE temp_mealsInPlan;";
        String query18 = "DROP TABLE temp_ingredients_in_sections_of_meal;";
        String query19 = "DROP TABLE temp_dividedMealSections;";

        //####################################################
        // Update
        //####################################################
        String[] query_Temp_Data = new String[]{query0, query1, query2, query3, query4, query5, query6, query7, query8, query9, query10, query11, query12,
                query13, query14, query15, query16, query17, query18, query19};

        if (!(db.uploadData_Batch_Altogether(query_Temp_Data)))
        {
            JOptionPane.showMessageDialog(null, "\n\nError, transferMealIngredients() cannot transfer meal Ingredients");
            return false;
        }

        System.out.printf("\nMealIngredients Successfully Transferred! \n\n%s", lineSeparator);
        return true;
    }

    //##################################################################################################################
    //  Icon Methods & ActionListener Events
    //##################################################################################################################
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
        //  ScrollBar Up
        //##########################
        width = 51;
        height = 53;

        IconButton up_ScrollBar_Icon_Btn = new IconButton("/images/scrollBar_Up/scrollBar_Up.png", "", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor

        up_ScrollBar_Icon_Btn.makeBTntransparent();

        JButton up_ScrollBar_Btn = up_ScrollBar_Icon_Btn.returnJButton();
        up_ScrollBar_Btn.setToolTipText("Scroll to the top of Meal Plan"); //Hover message over icon

        up_ScrollBar_Btn.addActionListener(ae -> {

            scrollBarUp_BTN_Action();
        });

        iconPanelInsert.add(up_ScrollBar_Btn);

        //##########################
        // Refresh Icon
        //##########################
        width = 50;
        height = 50;

        IconButton refresh_Icon_Btn = new IconButton("/images/refresh/++refresh.png", "", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor

        JButton refresh_Btn = refresh_Icon_Btn.returnJButton();
        refresh_Btn.setToolTipText("Restore All Meals Data"); //Hover message over icon
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

        IconButton add_Icon_Btn = new IconButton("/images/add/++add.png", "", width, height, width, height,
                "centre", "right");

        JButton add_Btn = add_Icon_Btn.returnJButton();
        add_Btn.setToolTipText("Add Meal"); //Hover message over icon
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

        IconButton saveIcon_Icon_Btn = new IconButton("/images/save/+++save.png", "", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor
        //saveIcon_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));
        saveIcon_Icon_Btn.makeBTntransparent();

        JButton save_btn = saveIcon_Icon_Btn.returnJButton();
        save_btn.setToolTipText("Save All Meals"); //Hover message over icon


        save_btn.addActionListener(ae -> {

            saveMealData(true, true);
        });

        iconPanelInsert.add(save_btn);

        //##########################
        //  Add_Ingredients Icon
        //##########################

        width = 54;
        height = 57;

        IconButton add_Ingredients_Icon_Btn = new IconButton("/images/add_Ingredients/add_Ingredients.png", "", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor
        //saveIcon_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));
        add_Ingredients_Icon_Btn.makeBTntransparent();

        JButton add_Ingredients_Btn = add_Ingredients_Icon_Btn.returnJButton();
        add_Ingredients_Btn.setToolTipText("Add Ingredients"); //Hover message over icon


        add_Ingredients_Btn.addActionListener(ae -> {

            open_AddIngredients_Screen();
        });

        iconPanelInsert.add(add_Ingredients_Btn);

        //##########################
        //  Macro_Targets Icon
        //##########################

        width = 54;
        height = 50;

        IconButton macro_Targets_Icon_Btn = new IconButton("/images/targets/target.png", "", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor
        //saveIcon_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));
        macro_Targets_Icon_Btn.makeBTntransparent();

        JButton macro_Tagets_Btn = macro_Targets_Icon_Btn.returnJButton();
        macro_Tagets_Btn.setToolTipText("Set Macro Targets"); //Hover message over icon


        macro_Tagets_Btn.addActionListener(ae -> {

            open_MacrosTargets_Screen();
        });

        iconPanelInsert.add(macro_Tagets_Btn);

        //##########################
        //  ScrollBar Bottom
        //##########################
        width = 51;
        height = 51;

        IconButton down_ScrollBar_Icon_Btn = new IconButton("/images/scrollBar_Down/scrollBar_Down5.png", "", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor

        down_ScrollBar_Icon_Btn.makeBTntransparent();

        JButton down_ScrollBar_Btn = down_ScrollBar_Icon_Btn.returnJButton();
        down_ScrollBar_Btn.setToolTipText("Scroll to Bottom Of Meal Plan"); //Hover message over icon

        down_ScrollBar_Btn.addActionListener(ae -> {

            scrollBarDown_BTN_Action();
        });

        iconPanelInsert.add(down_ScrollBar_Btn);
    }

    private Boolean areYouSure(String process)
    {
        int reply = JOptionPane.showConfirmDialog(frame, String.format("Are you sure you want to %s, \nany unsaved changes will be lost in this Table! \nDo you want to %s?", process, process),
                "Close Application", JOptionPane.YES_NO_OPTION); //HELLO Edit

        if (reply==JOptionPane.NO_OPTION || reply==JOptionPane.CLOSED_OPTION)
        {
            return false;
        }
        return true;
    }

    //######################################
    // Icon Button Actions
    //######################################
    public void scrollBarUp_BTN_Action()
    {
        //##############################################
        // Set ScrollPane to the Bottom Straight Away
        //##############################################
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMinimum());
    }

    public void scrollBarDown_BTN_Action()
    {
        //##############################################
        // Set ScrollPane to the Bottom Straight Away
        //##############################################
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    private void addMealToPlan()
    {
        //##############################################################################################################
        //
        //##############################################################################################################
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
        //##############################################################################################################
        //
        //##############################################################################################################
        new MealManager(this, scrollJPanelCenter);
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
        Iterator<MealManager> it = mealManagerArrayList.iterator();
        while (it.hasNext())
        {
            MealManager mealManager = it.next();

            // If mealManager is in DB  then refresh
            if (mealManager.isMealManagerInDB())
            {
                mealManager.reloadingIngredientsTableDataFromRefresh(false);
                continue;
            }

            // Because mealManager is not in the DB erase it from the GUI
            mealManager.completely_Delete_MealManager();

            // Remove mealManager from memory
            it.remove();
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

        // ##############################################################################
        // Remove Delete MealManagers & Check if
        // ##############################################################################
        boolean noMealsLeft = true;

        Iterator<MealManager> it = mealManagerArrayList.iterator();
        while (it.hasNext())
        {
            MealManager mealManager = it.next();
            if (mealManager.getHasMealPlannerBeenDeleted())
            {
                mealManager.completely_Delete_MealManager();
                it.remove();
                continue;
            }
            noMealsLeft = false;
        }

        // ##############################################################################
        // Save meal plan data in DB
        // ##############################################################################
        // if there are no meals in the temp plan delete all meals / ingredients from original plan as opposed to a transfer
        if (noMealsLeft)
        {
            System.out.println("\n\n#################################### \n1.) saveMealData() Empty Meal Plan Save");

            String query0 = "SET FOREIGN_KEY_CHECKS = 0;"; // Disable Foreign Key Checks
            String query1 = String.format("DELETE FROM ingredients_in_sections_of_meal  WHERE plan_id = %s;", planID);
            String query2 = String.format("DELETE FROM dividedMealSections WHERE plan_id = %s;", planID);
            String query3 = String.format("DELETE FROM mealsInPlan WHERE plan_id = %s;", planID);
            String query4 = "SET FOREIGN_KEY_CHECKS = 1;"; // Enable Foreign Key Checks

            if (!(db.uploadData_Batch_Altogether(new String[]{query0, query1, query2, query3, query4})))
            {
                JOptionPane.showMessageDialog(frame, "\n\n1.)  Error \nUnable to save meals in plan!");
                return;
            }
        }
        else // because there are meals save them
        {
            if ((!(transferMealIngredients(tempPlanID, planID)))) // transfer meals and ingredients from temp plan to original plan
            {
                System.out.println("\n\n#################################### \n2.) saveMealData() Meals Transferred to Original Plan");

                JOptionPane.showMessageDialog(frame, "\n\n2.)  Error \nUnable to save meals in plan!");
                return;
            }

            // ###############################################################################
            // Instructing Meal Manager Tables To Update Their Model Data
            // ##############################################################################
            Iterator<MealManager> it2 = mealManagerArrayList.iterator();

            while (it2.hasNext())
            {
                MealManager mealManager = it2.next();
                mealManager.saveData(false);
            }
        }

        // ##############################################################################
        // Update MacrosLeft Targets
        // ##############################################################################
        if (!(macrosLeft_JTable.updateMacrosLeftTableModelData()))
        {
            JOptionPane.showMessageDialog(frame, "\n\n Error \n3.) Unable to save MacrosLeftTable! \n\nPlease retry again!");
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

    //##################################################################################################################
    //  Opening & Closing External Screen
    //##################################################################################################################
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

        ingredientsInfoScreen = new Ingredients_Info_Screen(db, this, planID, tempPlanID, planName,
                map_ingredientTypesToNames, ingredientsTypesList, storesNamesList);
    }

    public void remove_Ingredients_Info_Screen()
    {
        ingredientsInfoScreen = null;
    }

    //##################################################################################################################
    //  Mutator Methods
    //##################################################################################################################
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

    //##################################################################################################################
    //  Accessor Methods
    //##################################################################################################################
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

    //###########################################
    // String
    //###########################################
    public String getPlanName()
    {
        return planName;
    }

    //###########################################
    // Objects
    //###########################################
    public MyJDBC getDb()
    {
        return db;
    }

    public GridBagConstraints getGbc()
    {
        return gbc;
    }

    public JFrame getFrame()
    {
        return frame;
    }

    public Container getContentPane()
    {
        return contentPane;
    }

    public MacrosLeftTable getMacrosLeft_JTable()
    {
        return macrosLeft_JTable;
    }

    //###########################################
    // Integers
    //###########################################
    public Integer getTempPlanID()
    {
        return tempPlanID;
    }

    public Integer getPlanID()
    {
        return planID;
    }

    public Integer getAndIncreaseContainerYPos()
    {
        containerYPos++;
        return containerYPos;
    }

    public void addMealManger(MealManager mealManager)
    {
        mealManagerArrayList.add(mealManager);
    }

    //###########################################
    // Lists
    //###########################################
    public String[] getMeal_total_columnNames()
    {
        return meal_total_columnNames;
    }

    public ArrayList<String> getTotalMeal_Table_ColToHide()
    {
        return totalMeal_Table_ColToHide;
    }

    public TreeMap<String, Collection<String>> getMap_ingredientTypesToNames()
    {
        return map_ingredientTypesToNames;
    }

    public ArrayList<String> getIngredientsTableUnEditableCells()
    {
        return ingredientsTableUnEditableCells;
    }

    public ArrayList<String> getIngredients_Table_Col_Avoid_Centering()
    {
        return ingredients_Table_Col_Avoid_Centering;
    }

    public ArrayList<String> getIngredientsInMeal_Table_ColToHide()
    {
        return ingredientsInMeal_Table_ColToHide;
    }

    public String[] getIngredients_ColumnNames()
    {
        return ingredients_ColumnNames;
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
