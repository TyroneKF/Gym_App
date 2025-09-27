package App_Code.Objects.Screens;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.MacrosLeftTable;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.MacrosTargetsTable;
import App_Code.Objects.Database_Objects.MealManager;
import App_Code.Objects.Gui_Objects.*;
import App_Code.Objects.Screens.Graph.Line_Chart_Meal_Plan_Screen;
import App_Code.Objects.Screens.Ingredient_Info.Edit_Ingredients_Info.Ingredients_Info_Screen;
import App_Code.Objects.Screens.Loading_Screen.LoadingScreen;
import io.github.cdimascio.dotenv.Dotenv;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.time.LocalTime;
import java.util.*;
import java.util.List;

public class Meal_Plan_Screen extends Screen
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################

    // Integers
    private final Integer tempPlanID = 1;
    private Integer planID;
    private static Integer user_id;

    //###############################################
    // String
    //###############################################
    private static String
            version_no = "00001",
            databaseName = "gymapp" + version_no,
            user_name = "root",
            password = "password";

    private String JFrameName = databaseName;

    private String
            planName,
            lineSeparator = "###############################################################################";

    //###############################################
    // Booleans
    //###############################################
    private boolean macroTargetsChanged = false;
    private static boolean production = false;

    //###############################################
    // Collections
    //###############################################
    private String[] meal_total_columnNames, ingredients_ColumnNames, macroTargetsTable_ColumnNames, macrosLeft_columnNames;

    private Collection<String> ingredientsTypesList, storesNamesList;

    // Sorted Hashmap by key String
    private TreeMap<String, Collection<String>> map_ingredientTypesToNames = new TreeMap<String, Collection<String>>(new Comparator<String>()
    {
        public int compare(String o1, String o2)
        {
            return o1.toLowerCase().compareTo(o2.toLowerCase());
        }
    });

    private TreeSet<Map.Entry<Integer, MealManager>> mealManagerTreeSet = new TreeSet<Map.Entry<Integer, MealManager>>(new Comparator<Map.Entry<Integer, MealManager>>()
    {
        @Override
        public int compare(Map.Entry<Integer, MealManager> o1, Map.Entry<Integer, MealManager> o2)
        {
            return o1.getValue().getCurrentMealTime().compareTo(o2.getValue().getCurrentMealTime());
        }
    });

    //#################################################
    // Objects
    //#################################################

    // JPanels
    private JPanel scrollJPanelCenter, scrollJPanelBottom;

    // Table Objects
    private MacrosLeftTable macrosLeft_JTable;
    private MacrosTargetsTable macros_Targets_Table;

    // Screen Objects
    private Macros_Targets_Screen macrosTargets_Screen = null;
    private Ingredients_Info_Screen ingredientsInfoScreen = null;
    private Line_Chart_Meal_Plan_Screen lineChartMealPlanScreen = null;

    //##################################################
    // Database Table Names
    //##################################################

    private final static String
            db_Scripts_Folder_Path = "/data/database_scripts",
            db_File_Script_List_Name = "0.) Script_List.txt",
            db_File_Tables_Name = "0.) Database_Names.txt",

    // Table Names Frequently Used
    tablePlansName = "plans",
            tableMacrosPerPoundLimitName = "macros_per_pound_and_limits",
            tablePlanMacroTargetsNameCalc = "plan_macro_target_calculations",
            tableIngredientsInfoName = "ingredients_info",
            tableIngredientsTypeName = "ingredient_types",
            tableStoresName = "stores",

    tableMealsInPlanName = "meals_in_plan",
            tableSub_MealsName = "divided_meal_sections",
            tableIngredientsInMealSections = "ingredients_in_sections_of_meal",
            tableIngredientsCalName = "ingredients_in_sections_of_meal_calculation",
            tableTotalMealsTableName = "total_meal_view",
            tableTotalPlanTableName = "total_plan_view",
            tablePlanMacrosLeftName = "plan_macros_left";

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

    ingredientsInMeal_Table_ColToHide = new ArrayList<>(Arrays.asList("plan_id", "div_meal_sections_id", "ingredient_id",
            "ingredients_index", "liquid_content", "water_content" ));

    //##################################################################################################################
    // Other Table Customisations
    //##################################################################################################################
    private final ArrayList<String>

            // Table : total_meal_view Table
            totalMeal_Table_ColToHide = new ArrayList<String>(Arrays.asList("plan_id", "meal_name", "meal_in_plan_id",
            "weight_of_meal")),

    // Table : plan_macro_target_calculations
    macrosTargets_Table_ColToHide = new ArrayList<String>(Arrays.asList("plan_id", "plan_name", "date_time_of_creation")),

    // Table : plan_macros_left
    macrosLeft_Table_ColToHide = new ArrayList<String>(Arrays.asList("plan_id", "plan_name"));

    //##################################################################################################################
    // Constructor & Main
    //##################################################################################################################
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

                if (host == null || port == null || user == null || password == null || dbName == null)
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
        super(db, "Gym App", 1925, 1082, 0, 0);

        //##############################################################################################################
        // Getting Selected User & Plan Info
        //##############################################################################################################
        String queryX = String.format("""
                SELECT U.user_id, P.plan_id, P.plan_name 
                FROM
                (
                  SELECT plan_id, plan_name, user_id, selected_plan_flag from %s
                ) P     
                LEFT JOIN users U 
                ON U.user_id = P.user_id                                                
                WHERE P.selected_plan_flag = TRUE AND U.user_name = '%s';""", tablePlansName, user_name);

        ArrayList<ArrayList<String>> results1 = db.getMultiColumnQuery(queryX);

        ArrayList<String> results = results1 != null ? results1.get(0) : null;

        user_id = results != null ? Integer.parseInt(results.get(0)) : null;
        planID = results != null ? Integer.parseInt(results.get(1)) : null;
        planName = results != null ? results.get(2) : null;

        if (planID == null || user_id == null || planName == null || user_name == null)
        {
            System.err.printf("\n\nUsername : %s \nUser ID : %s \n\nSelected Plan ID : %s  \nSelected Plan Name : %s\n", user_name, user_id, planID, planName);

            JOptionPane.showMessageDialog(null, "No Chosen Plan Or User");
            return;
        }

        //##############################################################################################################
        // Getting Number Of Meals & Sub meals Count
        //##############################################################################################################
        String query1 = String.format("SELECT COUNT(meal_in_plan_id) AS total_meals FROM %s WHERE plan_id = %s;", tableMealsInPlanName, planID);
        String[] mealsInPlanCount = db.getSingleColumnQuery(query1);

        String query2 = String.format("SELECT COUNT(div_meal_sections_id) AS total_sub_meals FROM %s WHERE plan_id = %s;", tableSub_MealsName, planID);
        String[] dividedMealSectionsCount = db.getSingleColumnQuery(query2);

        if (mealsInPlanCount == null | dividedMealSectionsCount == null)
        {
            String msg = "\n\nError, Getting Meal Count Or, Sub Meals Count";

            System.err.printf("\n%s \nQuery 1: %s \nQuery 2: %s", msg, query1, query2);

            JOptionPane.showMessageDialog(getFrame(), msg);
            return;
        }

        int
                no_of_meals = Integer.parseInt(mealsInPlanCount[0]),
                no_of_sub_meals = Integer.parseInt(dividedMealSectionsCount[0]);

        System.out.printf("\n\n%s \nMeals In Plan: %s\nSub-Meals In Plan: %s \n", lineSeparator, no_of_meals, no_of_sub_meals);

        //##############################################################################################################
        // Setting Up Loading Screen & Data Transfer
        //##############################################################################################################

        /**
         *  1.) Transfer Plan Data
         *  2.) Transfer Plan Targets
         *  3.) Transferring Meals Data
         *  4.) Get Ingredient Names & Types
         *  5.) Main GUI Setup (excluding adding meals)
         *  6.) MacroTargets Setup
         *  7.) MacrosLeft Setup
         */

        // Setting Up Loading Screen
        int totalProgress = no_of_meals + no_of_sub_meals + (7 * 10);
        LoadingScreen loadingScreen = new LoadingScreen(totalProgress, this);

        //####################################################
        // Transferring PLan Data To Temp
        //####################################################
        if (! transferPlanData(planID, tempPlanID))
        {
            loadingScreen.closeWindow();
            return;
        }

        loadingScreen.increaseBar(10);
        System.out.printf("\nChosen Plan: %s  & Chosen Plan Name: %s \n\n%s", planID, planName, lineSeparator);

        //####################################################
        // Transferring Targets From Chosen PLan to Temp
        //####################################################
        if (! transferTargets(planID, tempPlanID, true, false))
        {
            loadingScreen.closeWindow();
            return;
        }

        loadingScreen.increaseBar(10);

        //####################################################
        // Transferring this plans Meals  Info to Temp-Plan
        //####################################################
        if (! (transferMealIngredients(planID, tempPlanID)))
        {
            loadingScreen.closeWindow();
            JOptionPane.showMessageDialog(null, "\n\nCannot Create Temporary Plan In DB to Allow Editing");
            return;
        }

        loadingScreen.increaseBar(10);

        //####################################################
        // Get IngredientTypes & Store Data
        //####################################################
        if (! (getIngredientsTypesAndStoresData(true, true, true)))
        {
            loadingScreen.closeWindow();
            JOptionPane.showMessageDialog(null, "\n\nCannot Get Ingredients_Types & Stores Info \n\ngetIngredientsTypesAndStoresData()");
            return;
        }

        loadingScreen.increaseBar(10);

        //#############################################################################################################
        // 2.) Getting Table Column Names
        //#############################################################################################################

        // column names : ingredients_in_sections_of_meal_calculation
        ingredients_ColumnNames = db.getColumnNames(tableIngredientsCalName);

        // column names : total_meal_view
        meal_total_columnNames = db.getColumnNames(tableTotalMealsTableName);

        // column names : plan_macro_target_calculations
        macroTargetsTable_ColumnNames = db.getColumnNames(tablePlanMacroTargetsNameCalc);

        // Get table column names for plan_macros_left
        macrosLeft_columnNames = db.getColumnNames(tablePlanMacrosLeftName);

        if (ingredients_ColumnNames == null | meal_total_columnNames == null | macroTargetsTable_ColumnNames == null | macrosLeft_columnNames == null)
        {
            System.err.printf("Error, Gathering Column Names for Tables: \n%s = %s%n \n%s = %s%n \n%s = %s%n \n%s = %s%n",

                    tableIngredientsCalName, Arrays.toString(ingredients_ColumnNames),
                    tableTotalMealsTableName, Arrays.toString(meal_total_columnNames),
                    tablePlanMacroTargetsNameCalc, Arrays.toString(macroTargetsTable_ColumnNames),
                    tablePlanMacrosLeftName, Arrays.toString(macrosLeft_columnNames)
            );

            JOptionPane.showMessageDialog(getFrame(), "Error, Getting Column Names For Tables In GUI !!");
            return;
        }

        //#############################################################################################################
        // 2.) Getting Meals In Plan : ID , Name , Meal Times
        //#############################################################################################################
        ArrayList<ArrayList<String>> meals_Info_In_Plan = new ArrayList<>();

        if (no_of_meals > 0)
        {
            String query = String.format("SELECT meal_in_plan_id, meal_name, meal_time FROM %s WHERE plan_id = %s ORDER BY meal_time ASC;", tableMealsInPlanName, tempPlanID);

            meals_Info_In_Plan = db.getMultiColumnQuery(query);

            if (meals_Info_In_Plan == null)
            {
                JOptionPane.showMessageDialog(getFrame(), "Error, getting meal_in_plan_id, meal_name, meal_time from Meals in plan");
                System.err.printf("\n\nMeal_Plan_Screen.java Meal_Plan_Screen() Error with script \n%s", query);
                return;
            }
        }

        //#############################################################################################################
        //  Setup GUI & Split ScrollPane Into Sections
        //#############################################################################################################
        System.out.printf("\nMeal_Plan_Screen.java : Creating GUI Screen \n%s", lineSeparator); // Update

        iconSetup(getMainNorthPanel()); // Icon Setup in mainNorthPanel

        //##############################
        // Splitting Scroll JPanel
        //##############################
        scrollJPanelCenter = new JPanel(new GridBagLayout());
        addToContainer(getScrollPaneJPanel(), scrollJPanelCenter, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0, "center");

        scrollJPanelBottom = new JPanel(new GridBagLayout());
        addToContainer(getScrollPaneJPanel(), scrollJPanelBottom, 0, 1, 1, 1, 0.25, 0.25, "both", 0, 0, "end");

        //###################################
        // Increase Progress
        //###################################
        loadingScreen.increaseBar(10);

        //##############################################################################################################
        //Bottom : ScrollPanel
        //##############################################################################################################

        // Add Bottom JPanel to GUI
        JPanel macrosInfoJPanel = new JPanel(new GridBagLayout());
        addToContainer(scrollJPanelBottom, macrosInfoJPanel, 0, 0, 1, 1, 0.25, 0.25, "horizontal", 0, 0, "end");

        int  macrosInfoJP_YPos = 0;

        //#########################################################################
        // Setting up Horizontal Image Divider
        //#########################################################################
        int height = 75, width = 0;
        JPanel macrosDividerJPanel = new JPanel(new GridLayout(1,1));
        macrosDividerJPanel.setPreferredSize(new Dimension(width,height));

        // Border Line Config
        BevelBorder borderLine = new BevelBorder(BevelBorder.LOWERED);  // Create a red line border
        LineBorder redLine = new LineBorder(Color.RED, 2); // 2px thick red border
        CompoundBorder compoundBorder = new CompoundBorder(redLine, borderLine); // Combine them: outer = red line, inner = raised bevel
        macrosDividerJPanel.setBorder(compoundBorder); // Set Border

        // Add image
        URL imageUrl = getClass().getResource("/images/border/border_divider/border_divider4.jpg");
        ImageIcon originalIcon = new ImageIcon(imageUrl);
        Image img = originalIcon.getImage();
        Image scaledImg = img.getScaledInstance(450, 200, Image.SCALE_SMOOTH); // W: 1925
        ImageIcon scaledIcon = new ImageIcon(scaledImg);
        JLabel label = new JLabel(scaledIcon);

        addToContainer(macrosDividerJPanel, label, 0,0 , 1, 1, 0.25, 0.25, "both", 0, 0, null);

        addToContainer(macrosInfoJPanel, macrosDividerJPanel, 0, macrosInfoJP_YPos+=1, 1, 1, 0.25, 0.25, "both", 0, 0, null);

        //#########################################################################
        // Add Space Divider
        //#########################################################################

        addToContainer(macrosInfoJPanel, createSpaceDivider(0,20), 0, macrosInfoJP_YPos+=1, 1, 1, 0.25, 0.25, "both", 0, 0, null);

        //#########################################################################
        // Setting up macroTargets Table
        //#########################################################################
        // Getting data for plan_macro_target_calculations
        String planCalcQuery = String.format("SELECT * from %s WHERE plan_id = %s;", tablePlanMacroTargetsNameCalc, tempPlanID);

        Object[][] planData = db.getTableDataObject(planCalcQuery, tablePlanMacroTargetsNameCalc);
        planData = planData != null ? planData : new Object[0][0];

        macros_Targets_Table = new MacrosTargetsTable(db, macrosInfoJPanel, planData, macroTargetsTable_ColumnNames, planID, tempPlanID,
                tablePlanMacroTargetsNameCalc, new ArrayList<>(Arrays.asList(macroTargetsTable_ColumnNames)), null, macrosTargets_Table_ColToHide);

        addToContainer(macrosInfoJPanel, macros_Targets_Table, 0, macrosInfoJP_YPos+=1, +1, 1, 0.25, 0.25, "both", 40, 0, null);

        //########################################
        // macroTargets Complete
        //########################################
        loadingScreen.increaseBar(10);

        //###########################################################################
        // planMacrosLeft Table Setup
        //###########################################################################

        // Get table data from plan_macros_left
        String macrosQuery = String.format("SELECT * from %s WHERE plan_id = %s;", tablePlanMacrosLeftName, tempPlanID);

        Object[][] macrosData = db.getTableDataObject(macrosQuery, tablePlanMacrosLeftName);
        macrosData = macrosData != null ? macrosData : new Object[0][0];

        macrosLeft_JTable = new MacrosLeftTable(db, macrosInfoJPanel, macrosData, macrosLeft_columnNames, planID, tempPlanID,
                tablePlanMacrosLeftName, new ArrayList<>(Arrays.asList(macrosLeft_columnNames)), null, macrosLeft_Table_ColToHide);

        addToContainer(macrosInfoJPanel, macrosLeft_JTable, 0, macrosInfoJP_YPos+=1, 1, 1, 0.25, 0.25, "both", 30, 0, null);

        //########################################
        // macroTargets Complete
        //########################################
        loadingScreen.increaseBar(10);

        //##############################################################################################################
        // Centre: Adding Meal Managers to Centre of Screen On ScrollPanel
        //##############################################################################################################

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
            String subDivQuery = String.format("SELECT div_meal_sections_id FROM %s WHERE meal_in_plan_id = %s AND plan_id = %s;", tableSub_MealsName, mealInPlanID, tempPlanID);
            ArrayList<ArrayList<String>> subMealsInMealArrayList = db.getMultiColumnQuery(subDivQuery);

            if (subMealsInMealArrayList == null)
            {
                String message = String.format("\n\nError, gathering sub-meals ID for meal named ' %s ' ! \nA meal must have 1 sub-meal minimum!", mealName);

                System.err.printf("%s", message);
                JOptionPane.showMessageDialog(null, message);

                errorFound = true;
                break;
            }

            //#####################################################
            // Create Meal Component
            //#####################################################
            System.out.printf("\n\nMeal_Plan_Screen.java | MealManager \nmeal_in_plan_id : %s \nmeal_name : %s \nmeal_time : %s \nSub-Meals In MealManager (ID) : %s%n", mealInPlanID, mealName, mealTime, subMealsInMealArrayList);//HELLO DELETE
            addMealManger(new MealManager(this, scrollJPanelCenter, mealInPlanID, mealName, mealTime, subMealsInMealArrayList), false);

            //######################################################
            // Update Progress
            //######################################################
            loadingScreen.increaseBar(1 + subMealsInMealArrayList.size()); // + original meal + the sub-meal
        }

        if (errorFound) { return; }

        System.out.printf("\n\n%s", lineSeparator);

        //##############################################################################################################
        // GUI Alignments & Configurations
        //##############################################################################################################
        resizeGUI();
        scroll_To_Top_of_ScrollPane();

        setFrameVisibility(true);

        scrollDown_BtnAction();
    }

    //##################################################################################################################
    // Transfer SQL Data & Get Data Methods
    //##################################################################################################################

    // Transfer Data Methods
    private boolean transferPlanData(int fromPlan, int toPlan)
    {
        String query0 = String.format("""
                UPDATE `plans` AS `P`,
                (
                	SELECT plan_name, vegan FROM %s WHERE plan_id = %s
                ) AS `SRC`
                                    
                SET
                    `P`.`plan_name` = concat("(Temp) ",`SRC`.`plan_name`),`P`.`vegan` = `SRC`.`vegan`
                WHERE
                    `P`.`plan_id` = %s; """, tablePlansName, fromPlan, toPlan);

        if (! (db.uploadData_Batch_Altogether(new String[]{ query0 })))
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
        String query00 = String.format("DELETE FROM %s WHERE plan_id = %s;", tableMacrosPerPoundLimitName, toPlan);
        String query01 = String.format("DROP TABLE IF EXISTS temp_%s;", tableMacrosPerPoundLimitName);
        String query02 = String.format("""
                        CREATE TABLE temp_%s AS SELECT * FROM %s 
                        WHERE plan_id = %s 
                        AND date_time_of_creation = (SELECT MAX(date_time_of_creation) FROM %s);""",
                tableMacrosPerPoundLimitName, tableMacrosPerPoundLimitName, fromPlan, tableMacrosPerPoundLimitName);

        String query03 = String.format("ALTER TABLE temp_%s DROP COLUMN current_weight_in_pounds;", tableMacrosPerPoundLimitName);
        String query04 = String.format("UPDATE temp_%s SET plan_id = %s;", tableMacrosPerPoundLimitName, toPlan);

        //####################################
        // Gathering Table Columns
        //####################################

        ArrayList<String> columnsToAvoid = new ArrayList<>(List.of("current_weight_in_pounds"));
        String[] macrosColumns = db.getColumnNames(tableMacrosPerPoundLimitName);

        String query05 = String.format("INSERT INTO %s \n(", tableMacrosPerPoundLimitName);
        int listSize = macrosColumns.length;

        for (int i = 0; i <= listSize - 1; i++)
        {
            String colToAdd = columnsToAvoid.contains(macrosColumns[i]) ? "" : String.format("\n\t%s", macrosColumns[i]);

            query05 = ! colToAdd.equals("") ? String.format("%s %s,", query05, colToAdd) : query05;
        }

        query05 = query05.substring(0, query05.length() - 1); // Remove last ',' from query

        query05 += String.format("\n) \n\nSELECT * FROM temp_%s;", tableMacrosPerPoundLimitName);

        //####################################

        String query06 = String.format("DROP TABLE temp_%s;", tableMacrosPerPoundLimitName);

        //####################################
        // Perform Upload
        //####################################
        String[] uploadQueries = new String[0];

        if (deleteFromToPlan)
        {
            uploadQueries = new String[]{ query00, query01, query02, query03, query04, query05, query06 };
        }
        else
        {
            uploadQueries = new String[]{ query01, query02, query03, query04, query05, query06 };
        }

        if (! (db.uploadData_Batch_Altogether(uploadQueries)))
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
        String query0 = String.format("DROP TABLE IF EXISTS temp_%s;", tableIngredientsInMealSections);
        String query1 = String.format("DROP TABLE IF EXISTS temp_%s;", tableSub_MealsName);
        String query2 = String.format("DROP TABLE IF EXISTS temp_%s;", tableMealsInPlanName);

        String query3 = "SET FOREIGN_KEY_CHECKS = 0;"; // Disable Foreign Key Checks

        //################################################################
        // Delete Meal & Ingredient Data from to plan_id
        //################################################################
        String query4 = String.format("DELETE FROM %s WHERE plan_id = %s;", tableIngredientsInMealSections, toPlanID);
        String query5 = String.format("DELETE FROM %s WHERE plan_id = %s;", tableSub_MealsName, toPlanID);
        String query6 = String.format("DELETE FROM %s WHERE plan_id = %s;", tableMealsInPlanName, toPlanID);

        String query7 = "SET FOREIGN_KEY_CHECKS = 1;"; // Enable Foreign Key Checks

        //################################################################
        // Transferring Meals From One Plan To Another
        //################################################################
        String query8 = String.format("CREATE table temp_%s AS SELECT * FROM %s WHERE plan_id = %s ORDER BY meal_in_plan_id;", tableMealsInPlanName, tableMealsInPlanName, fromPlanID);
        String query9 = String.format("UPDATE temp_%s SET plan_id = %s;", tableMealsInPlanName, toPlanID);
        String query10 = String.format("INSERT INTO %s SELECT * FROM temp_%s;", tableMealsInPlanName, tableMealsInPlanName);

        //################################################################
        // Transferring Sections Of Meals From One Plan To Another
        //################################################################
        String query11 = String.format("CREATE table temp_%s AS SELECT * FROM %s WHERE plan_id = %s ORDER BY div_meal_sections_id;", tableSub_MealsName, tableSub_MealsName, fromPlanID);
        String query12 = String.format("UPDATE temp_%s SET plan_id = %s;", tableSub_MealsName, toPlanID);
        String query13 = String.format("INSERT INTO %s SELECT * FROM temp_%s;", tableSub_MealsName, tableSub_MealsName);

        //################################################################
        // Transferring this plans Ingredients to Temp-Plan
        //################################################################
        // Create Table to transfer ingredients from original plan to temp
        String query14 = String.format("""                  
                CREATE table temp_%s AS
                SELECT i.*
                FROM %s i                                                       
                WHERE i.plan_id = %s;""", tableIngredientsInMealSections, tableIngredientsInMealSections, fromPlanID);

        String query15 = String.format("UPDATE temp_%s SET plan_id = %s;", tableIngredientsInMealSections, toPlanID);
        String query16 = String.format("INSERT INTO %s SELECT * FROM temp_%s;", tableIngredientsInMealSections, tableIngredientsInMealSections);

        String query17 = String.format("DROP TABLE temp_%s;", tableMealsInPlanName);
        String query18 = String.format("DROP TABLE temp_%s;", tableIngredientsInMealSections);
        String query19 = String.format("DROP TABLE temp_%s;", tableSub_MealsName);

        //####################################################
        // Update
        //####################################################
        String[] query_Temp_Data = new String[]{ query0, query1, query2, query3, query4, query5, query6, query7, query8, query9, query10, query11, query12,
                query13, query14, query15, query16, query17, query18, query19 };

        if (! (db.uploadData_Batch_Altogether(query_Temp_Data)))
        {
            JOptionPane.showMessageDialog(null, "\n\nError, transferMealIngredients() cannot transfer meal Ingredients");
            return false;
        }

        System.out.printf("\nMealIngredients Successfully Transferred! \n\n%s", lineSeparator);
        return true;
    }

    // Get Data Methods
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
            if (! updateIngredientTypesMappedToIngredientsName())
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
            if (ingredientsTypesList != null)
            {
                ingredientsTypesList.clear();
            }

            ingredientsTypesList = db.getSingleColumnQuery_AlphabeticallyOrderedTreeSet(String.format("SELECT ingredient_type_name FROM %s ORDER BY ingredient_type_name ASC;", tableIngredientsTypeName));

            if (ingredientsTypesList == null)
            {
                errorTxt += "\n\nUnable to get ingredient_types";
                errorFound = true;
            }
        }

        //#################################################################################
        // Get All The Store Names Inside The DB
        //#################################################################################

        if (getIngredientStores)
        {
            if (storesNamesList != null)
            {
                storesNamesList.clear();
            }

            storesNamesList = db.getSingleColumnQuery_AlphabeticallyOrderedTreeSet(String.format("SELECT store_name FROM %s ORDER BY store_name ASC;", tableStoresName));

            if (storesNamesList == null)
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
        String queryIngredientsType = String.format(
                """
                        SELECT I.ingredient_type_id, N.ingredient_type_name
                        FROM
                        (
                          SELECT DISTINCT(ingredient_type_id) FROM %s
                        ) AS I
                        INNER JOIN
                        (
                          SELECT ingredient_type_id, ingredient_type_name FROM %s
                        ) AS N
                        ON I.ingredient_type_id = N.ingredient_type_id 
                        ORDER BY N.ingredient_type_name;""", tableIngredientsInfoName, tableIngredientsTypeName);

        ArrayList<ArrayList<String>> ingredientTypesNameAndIDResults = db.getMultiColumnQuery(queryIngredientsType);

        if (ingredientTypesNameAndIDResults == null)
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
            String queryTypeIngredientNames = String.format("SELECT ingredient_name FROM %s WHERE ingredient_type_id = %s ORDER BY ingredient_name;", tableIngredientsInfoName, ID);
            ArrayList<String> ingredientNames = db.getSingleColumnQuery_ArrayList(queryTypeIngredientNames);

            if (ingredientNames == null)
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
            JOptionPane.showMessageDialog(null, String.format("Had Errors Trying to map ingredient_types to IngredientNames: \n\n%s", errorTxt));
            return false;
        }

        return true;
    }

    //##################################################################################################################
    //  Icon Methods & ActionListener Events
    //##################################################################################################################
    @Override
    protected void iconSetup(Container mainNorthPanel)
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
        // Clear
        //##########################
        width = 53;
        height = 50;

        IconButton clear_Icon_Btn = new IconButton("/images/close_btn/delete.png", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor

        clear_Icon_Btn.makeBTntransparent();

        JButton clear_Btn = clear_Icon_Btn.returnJButton();
        clear_Btn.setToolTipText("Clear Meal Plan"); //Hover message over icon

        clear_Btn.addActionListener(ae -> {
            delete_btnAction();
        });

        iconPanelInsert.add(clear_Btn);

        //##########################
        // Recipe List
        //##########################
        width = 53;
        height = 50;

        IconButton RecipeList_Icon_Btn = new IconButton("/images/RecipeList/recipeList1.png", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor

        RecipeList_Icon_Btn.makeBTntransparent();

        JButton recipe_Btn = RecipeList_Icon_Btn.returnJButton();
        recipe_Btn.setToolTipText("Get Plan Recipe List"); //Hover message over icon

        recipe_Btn.addActionListener(ae -> {
            recipeList_BtnAction_OpenScreen();
        });

        iconPanelInsert.add(recipe_Btn);

        //##########################
        //  BarChart
        //##########################
        width = 55;
        height = 55;

        IconButton pieChart_Icon_Btn = new IconButton("/images/graph/pie7.png", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor

        pieChart_Icon_Btn.makeBTntransparent();

        JButton pieChart_Btn = pieChart_Icon_Btn.returnJButton();
        pieChart_Btn.setToolTipText("Display Macronutrient Data in Pie"); //Hover message over icon

        pieChart_Btn.addActionListener(ae -> {
            pieChart_BtnAction_OpenScreen();
        });

        iconPanelInsert.add(pieChart_Btn);

        //##########################
        //  LineChart
        //##########################
        width = 51;
        height = 53;

        IconButton lineChart_Icon_Btn = new IconButton("/images/graph/bar1.png", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor

        lineChart_Icon_Btn.makeBTntransparent();

        JButton lineChart_Btn = lineChart_Icon_Btn.returnJButton();
        lineChart_Btn.setToolTipText("Display Macronutrient Data in LineChart"); //Hover message over icon

        lineChart_Btn.addActionListener(ae -> {
            lineChart_Btn_Action_OpenScreen();
        });

        iconPanelInsert.add(lineChart_Btn);

        //##########################
        //  ScrollBar Up
        //##########################
        width = 51;
        height = 53;

        IconButton up_ScrollBar_Icon_Btn = new IconButton("/images/scrollBar_Up/scrollBar_Up.png", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor

        up_ScrollBar_Icon_Btn.makeBTntransparent();

        JButton up_ScrollBar_Btn = up_ScrollBar_Icon_Btn.returnJButton();
        up_ScrollBar_Btn.setToolTipText("Scroll to the top of Meal Plan"); //Hover message over icon

        up_ScrollBar_Btn.addActionListener(ae -> {

            scrollUp_BtnAction();
        });

        iconPanelInsert.add(up_ScrollBar_Btn);

        //##########################
        // Refresh Icon
        //##########################
        width = 50;
        height = 50;

        IconButton refresh_Icon_Btn = new IconButton("/images/refresh/++refresh.png", width, height, width, height,
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

        IconButton add_Icon_Btn = new IconButton("/images/add/++add.png", width, height, width, height,
                "centre", "right");

        JButton add_Btn = add_Icon_Btn.returnJButton();
        add_Btn.setToolTipText("Add Meal"); //Hover message over icon
        add_Icon_Btn.makeBTntransparent();

        add_Btn.addActionListener(ae -> {

            add_Meal_Btn_Action();
        });

        iconPanelInsert.add(add_Icon_Btn);

        //##########################
        // Save BTN
        //##########################

        width = 54;
        height = 57;

        IconButton saveIcon_Icon_Btn = new IconButton("/images/save/+++save.png", width, height, width, height,
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

        IconButton add_Ingredients_Icon_Btn = new IconButton("/images/add_Ingredients/add_Ingredients.png", width, height, width, height,
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

        IconButton macro_Targets_Icon_Btn = new IconButton("/images/targets/target.png", width, height, width, height,
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
        //  Scroll Bottom
        //##########################
        width = 51;
        height = 51;

        IconButton down_ScrollBar_Icon_Btn = new IconButton("/images/scrollBar_Down/scrollBar_Down5.png", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor

        down_ScrollBar_Icon_Btn.makeBTntransparent();

        JButton down_ScrollBar_Btn = down_ScrollBar_Icon_Btn.returnJButton();
        down_ScrollBar_Btn.setToolTipText("Scroll to Bottom Of Meal Plan"); //Hover message over icon

        down_ScrollBar_Btn.addActionListener(ae -> {

            scrollDown_BtnAction();
        });

        iconPanelInsert.add(down_ScrollBar_Btn);
    }

    // ###################################################
    // Delete BTN Actions
    // ###################################################
    private void delete_btnAction()
    {
        //###########################################################
        // Ask for Confirmation
        //###########################################################
        if (! areYouSure("delete all the meals in this plan")) { return; }

        //###########################################################
        // Delete all the meals in the plans in SQL
        //###########################################################
        String queryDelete = String.format("DELETE FROM meals_in_plan WHERE plan_id = %s", tempPlanID);

        if (! db.uploadData(queryDelete, false))
        {
            JOptionPane.showMessageDialog(null, "\n\nUnable to DELETE All Meals in Plan!");
        }

        Iterator<Map.Entry<Integer, MealManager>> it = mealManagerTreeSet.iterator();
        while (it.hasNext())
        {
            it.next().getValue().hideMealManager(); // Hide Meal Managers
        }

        JOptionPane.showMessageDialog(getFrame(), "\n\nSuccessfully, DELETED all meals in plan!");

        //###########################################################
        // Update MacrosLeft
        //###########################################################
        update_MacrosLeftTable();

        //###########################################################
        // Update LineChart Screen
        //###########################################################
        clearLineChartDataSet();
    }

    // ###################################################
    // Recipe BTN Actions
    // ###################################################
    private void recipeList_BtnAction_OpenScreen()
    {

    }

    // ###################################################
    // Pie Chart BTN Actions
    // ###################################################
    private void pieChart_BtnAction_OpenScreen()
    {

    }

    // #########################################
    // Line Chart BTN Actions
    // #########################################
    private void lineChart_Btn_Action_OpenScreen()
    {
        if (lineChartMealPlanScreen == null)
        {
            lineChartMealPlanScreen = new Line_Chart_Meal_Plan_Screen(db, this);
            return;
        }

        lineChartMealPlanScreen.makeJFrameVisible();
    }

    public void removeLineChartScreen()
    {
        lineChartMealPlanScreen = null;
    }

    public void updateLineChartData(MealManager mealManager, LocalTime previousTime, LocalTime currentTime)
    {
        if(lineChartMealPlanScreen == null) return;

        lineChartMealPlanScreen.updateMealManagerDataChange(mealManager, previousTime, currentTime);
    }

    public void deleteLineChartData(MealManager mealManager, LocalTime currentTime)
    {
        if(lineChartMealPlanScreen == null) return;

        lineChartMealPlanScreen.deleteMealManagerData(mealManager, currentTime);
    }

    public void clearLineChartDataSet()
    {
        if(lineChartMealPlanScreen == null) return;

        lineChartMealPlanScreen.clear_And_Rebuild_Dataset();
    }

    // ###################################################
    // Scroll Up BTN Action
    // ###################################################
    private void scrollUp_BtnAction()
    {
        super.scroll_To_Top_of_ScrollPane();
    }

    // ###################################################
    // Refresh BTN Actions / Plan Actions
    // ###################################################
    private void refreshPlan(boolean askPermission)
    {
        if ((! (get_IsPlanSelected())) || askPermission && ! (areYouSure("Refresh Data")))
        {
            return;
        }

        //####################################################################
        // Refresh DB Data
        //####################################################################
        if (! (transferMealIngredients(planID, tempPlanID))) // transfer meals and ingredients from temp plan to original plan
        {
            JOptionPane.showMessageDialog(frame, "`\n\nError couldn't transfer ingredients data from temp to real plan !!");
            return;
        }

        //####################################################################
        // Refresh ingredients meal table & total Tables Data
        //####################################################################
        Iterator<Map.Entry<Integer, MealManager>> it = mealManagerTreeSet.iterator();
        while (it.hasNext())
        {
            MealManager mealManager = it.next().getValue();

            // If mealManager is in DB  then refresh
            if (mealManager.isMealManagerInDB())
            {
                mealManager.reloadingIngredientsTableDataFromRefresh(false, false);
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
        refresh_MacroTargets(); // if macroTargets changed ask the user if they would like to refresh this data
        refresh_MacrosLeft();

        //####################################################################
        // Re-Upload LineChart Data in One Go
        //####################################################################
        if(lineChartMealPlanScreen == null) return;

         lineChartMealPlanScreen.clear_And_Rebuild_Dataset(); // clear but, also adds data in
    }

    // ###################################################
    // Add Meal BTN Actions
    // ###################################################
    private void add_Meal_Btn_Action()
    {
        //##############################################################################################################
        //
        //##############################################################################################################
        if (! (get_IsPlanSelected()))
        {
            return;
        }

        // If No Plan Is Selected
        if (planID == null)
        {
            JOptionPane.showMessageDialog(null, "\n\nCannot Add A  Meal As A Plan Is Not Selected! \nPlease Select A Plan First!!");
            return;
        }
        //##############################################################################################################
        //
        //##############################################################################################################
        addMealManger(new MealManager(this, scrollJPanelCenter), true);
    }

    public void addMealManger(MealManager mealManager, boolean clearGUIThenAdd)
    {
        if (! mealManager.isObjectCreated())
        {
            return;
        }  // If object was rejected in creation, will cause an error below with time comparison

        Integer meal_in_plan_id = mealManager.getMealInPlanID();

        if (! clearGUIThenAdd)  // Just add this Meal Manager to the screen without clearing the screen
        {
            // Add Meal Manager to collection
            mealManagerTreeSet.add(Map.entry(meal_in_plan_id, mealManager));

            // Add to GUI Meal Manager & Its Space Divider
            addToContainer(scrollJPanelCenter, mealManager.getCollapsibleJpObj(), 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
            addToContainer(scrollJPanelCenter, mealManager.getSpaceDividerForMealManager(), 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "both", 50, 0, null);
        }
        else
        {
            // Expand Meal in GUI
            mealManager.getCollapsibleJpObj().expandJPanel();

            // Remove MealManager From List
            mealManagerTreeSet.removeIf(e -> e.getKey().equals(meal_in_plan_id));

            // If MealManager is the latest time in the plan just add it to the bottom without clearing the screen
            if (mealManagerTreeSet.size() == 0 || mealManager.getCurrentMealTime().isAfter(mealManagerTreeSet.last().getValue().getCurrentMealTime()))
            {
                addMealManger(mealManager, false);
                return;
            }

            // Add Meal Manager to collection & it should be sorted in order now
            mealManagerTreeSet.add(Map.entry(meal_in_plan_id, mealManager));

            // Re-add all the MealManagers to GUI
            for (Map.Entry<Integer, MealManager> x : mealManagerTreeSet)
            {
                MealManager m = x.getValue();

                if (! meal_in_plan_id.equals(m.getMealInPlanID()))
                {
                    // remove from old position in GUI
                    m.getCollapsibleJpObj().collapseJPanel(); // Minimise Meal
                    m.removeMealManagerFromGUI();
                }

                // Add to GUI Meal Manager & Its Space Divider
                addToContainer(scrollJPanelCenter, m.getCollapsibleJpObj(), 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
                addToContainer(scrollJPanelCenter, m.getSpaceDividerForMealManager(), 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "both", 50, 0, null);
            }
        }

        // ########################################################
        // Resize & Align GUI screen so new object is visible
        // ########################################################
        resizeGUI();

        // Scroll to MealManager
        scrollToJPanelOnScreen(mealManager.getCollapsibleJpObj());
    }

    // ###################################################
    // Save Plan BTN
    // ###################################################
    private void saveMealData(boolean askPermission, boolean showMsg)
    {
        // ##############################################################################
        // Exit Clauses
        // ##############################################################################
        // If no plan is selected exit Or,  if user rejects saving when asked  exit
        if ((! (get_IsPlanSelected())) || askPermission && ! (areYouSure("Save Data")))
        {
            return;
        }

        // ##############################################################################
        // Remove Delete MealManagers & Check if
        // ##############################################################################
        boolean noMealsLeft = true;

        Iterator<Map.Entry<Integer, MealManager>> it = mealManagerTreeSet.iterator();
        while (it.hasNext())
        {
            MealManager mealManager = it.next().getValue();
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
            String query1 = String.format("DELETE FROM %s  WHERE plan_id = %s;", tableIngredientsInMealSections, planID);
            String query2 = String.format("DELETE FROM %s WHERE plan_id = %s;", tableSub_MealsName, planID);
            String query3 = String.format("DELETE FROM %s WHERE plan_id = %s;", tableMealsInPlanName, planID);
            String query4 = "SET FOREIGN_KEY_CHECKS = 1;"; // Enable Foreign Key Checks

            if (! (db.uploadData_Batch_Altogether(new String[]{ query0, query1, query2, query3, query4 })))
            {
                JOptionPane.showMessageDialog(frame, "\n\n1.)  Error \nUnable to save meals in plan!");
                return;
            }
        }
        else // because there are meals save them
        {
            if ((! (transferMealIngredients(tempPlanID, planID)))) // transfer meals and ingredients from temp plan to original plan
            {
                System.out.println("\n\n#################################### \n2.) saveMealData() Meals Transferred to Original Plan");

                JOptionPane.showMessageDialog(frame, "\n\n2.)  Error \nUnable to save meals in plan!");
                return;
            }

            // ###############################################################################
            // Instructing Meal Manager Tables To Update Their Model Data
            // ##############################################################################
            Iterator<Map.Entry<Integer, MealManager>> it2 = mealManagerTreeSet.iterator();

            while (it2.hasNext())
            {
                MealManager mealManager = it2.next().getValue();
                mealManager.saveData(false);
            }
        }

        // ##############################################################################
        // Update MacrosLeft Targets
        // ##############################################################################
        if (! (macrosLeft_JTable.updateMacrosLeftTableModelData()))
        {
            JOptionPane.showMessageDialog(frame, "\n\n Error \n3.) Unable to save MacrosLeftTable! \n\nPlease retry again!");
            return;
        }

        // ##############################################################################
        // Successful Message
        // ##############################################################################
        if(showMsg)
        {
            JOptionPane.showMessageDialog(frame, "\n\nAll Meals Are Successfully Saved!");
        }
    }

    // ###################################################
    // Add Ingredients Screen & Ingredient Methods
    // ###################################################
    private void open_AddIngredients_Screen()
    {
        if (! (get_IsPlanSelected()))
        {
            return;
        }

        if (ingredientsInfoScreen != null)
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

    // ###################################################
    // Macro Targets Screen & Target Methods
    // ###################################################
    private void open_MacrosTargets_Screen()
    {
        if (! (get_IsPlanSelected()))
        {
            return;
        }

        if (macrosTargets_Screen != null)
        {
            macrosTargets_Screen.makeJFrameVisible();
            return;
        }
        macrosTargets_Screen = new Macros_Targets_Screen(db, this, planID, tempPlanID, planName);
    }

    public void remove_macrosTargets_Screen()
    {
        macrosTargets_Screen = null;
    }

    public void macrosTargetsChanged(boolean bool)
    {
        macroTargetsChanged = bool;
    }

    private void saveMacroTargets(boolean askPermission, boolean showUpdateMsg)
    {
        // ##############################################
        // If targets haven't changed exit
        // ##############################################
        if (! getMacrosTargetsChanged())
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

            if (reply == JOptionPane.NO_OPTION || reply == JOptionPane.CLOSED_OPTION)
            {
                return;
            }
        }

        if (transferTargets(tempPlanID, planID, false, showUpdateMsg))
        {
            macrosTargetsChanged(false);
            update_Targets_And_MacrosLeftTables();
        }
    }

    // ###################################################
    // Scroll Down BTN Action
    // ###################################################
    private void scrollDown_BtnAction()
    {
        super.scroll_To_Bottom_of_ScrollPane();
    }

    //##################################################################################################################
    // Other Methods
    //##################################################################################################################
    @Override
    public void windowClosedEvent()
    {
        if (macroTargetsChanged) // If targets have changed, save them?
        {
            saveMacroTargets(true, false);
        }

        saveMealData(true, false);

        // Close Other Windows If Open
        if (macrosTargets_Screen != null)
        {
            macrosTargets_Screen.windowClosedEvent();
        }
        if (ingredientsInfoScreen != null)
        {
            ingredientsInfoScreen.closeWindow();
        }
    }

    //##################################################################################################################
    //  Macro Targets/Left Table Methods
    //##################################################################################################################
    public void update_Targets_And_MacrosLeftTables()
    {
        update_MacrosTargetTable();
        update_MacrosLeftTable();
    }

    //##########################################
    // MacrosLeft Table
    //#########################################
    public void update_MacrosLeftTable()
    {
        macrosLeft_JTable.updateMacrosLeftTable();
    }

    private void refresh_MacrosLeft()
    {
        macrosLeft_JTable.refreshData();
    }

    public MacrosLeftTable getMacrosLeft_JTable()
    {
        return macrosLeft_JTable;
    }

    //##########################################
    // MacroTargets Table
    //#########################################
    public void update_MacrosTargetTable()
    {
        macros_Targets_Table.updateMacrosTargetsTable();
    }

    private void refresh_MacroTargets()
    {
        // ##############################################
        // If targets have changed, save them?
        // ##############################################
        if (getMacrosTargetsChanged())
        {
            int reply = JOptionPane.showConfirmDialog(frame, String.format("Would you like to refresh your MacroTargets Too?"),
                    "Refresh Macro Targets", JOptionPane.YES_NO_OPTION); //HELLO Edit

            if (reply == JOptionPane.YES_OPTION)
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

    //##################################################################################################################
    //  Accessor Methods
    //##################################################################################################################

    // Booleans
    public boolean getMacrosTargetsChanged()
    {
        return macroTargetsChanged;
    }

    public boolean get_IsPlanSelected()
    {
        if (planID == null)
        {
            JOptionPane.showMessageDialog(frame, "Please Select A Plan First!");
            return false;
        }
        return true;
    }

    //###########################################
    // String
    //###########################################
    public String getPlanName()
    {
        return planName;
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

    //####################################################################
    // Collections :  Accessor Methods
    //#####################################################################

    // Others
    public TreeMap<String, Collection<String>> getMap_ingredientTypesToNames()
    {
        return map_ingredientTypesToNames;
    }

    public TreeSet<Map.Entry<Integer, MealManager>> getMealManagerTreeSet()
    {
        return mealManagerTreeSet;
    }

    //###########################################
    // TotalMeal Table Collections
    //###########################################
    public ArrayList<String> getTotalMeal_Table_ColToHide()
    {
        return totalMeal_Table_ColToHide;
    }

    public String[] getMeal_total_columnNames()
    {
        return meal_total_columnNames;
    }

    //###########################################
    // Ingredients Table Collections
    //###########################################
    public String[] getIngredients_ColumnNames()
    {
        return ingredients_ColumnNames;
    }

    public ArrayList<String> getIngredientsTableUnEditableCells()
    {
        return ingredientsTableUnEditableCells;
    }

    public ArrayList<String> getIngredients_Table_Col_Avoid_Centering() { return ingredients_Table_Col_Avoid_Centering; }

    public ArrayList<String> getIngredientsInMeal_Table_ColToHide()
    {
        return ingredientsInMeal_Table_ColToHide;
    }
}
