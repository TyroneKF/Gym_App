package App_Code.Objects.Screens;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.MacrosLeftTable;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.MacrosTargetsTable;
import App_Code.Objects.Database_Objects.MealManager;
import App_Code.Objects.Database_Objects.MealManagerRegistry;
import App_Code.Objects.Gui_Objects.*;
import App_Code.Objects.Gui_Objects.Screens.Screen_JFrame;
import App_Code.Objects.Screens.Graph_Screens.LineChart_Meal_Plan_Screen.LineChart_MPS;
import App_Code.Objects.Screens.Graph_Screens.PieChart_Meal_Plan_Screen.PieChart_Screen_MPS;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Ingredients_Info_Screen;
import App_Code.Objects.Screens.Loading_Screen.Loading_Screen;
import App_Code.Objects.Screens.Others.Macros_Targets_Screen;
import io.github.cdimascio.dotenv.Dotenv;
import org.javatuples.Pair;
import org.jfree.data.time.Second;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;

public class Meal_Plan_Screen extends Screen_JFrame
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
            database_Name = "gymapp" + version_no,
            user_name = "root",
            password = "password";
    
    private String JFrameName = database_Name;
    
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
    private ArrayList<String> meal_total_columnNames, ingredients_ColumnNames, macroTargets_ColumnNames,
            macrosLeft_columnNames, macros_And_Limits_ColumnNames;
    
    private TreeSet<String>
            ingredientsTypesList,
            storesNamesList;
    
    // Sorted Hashmap by key String
    private TreeMap<String, TreeSet<String>> map_ingredientTypesToNames = new TreeMap<String, TreeSet<String>>(new Comparator<String>()
    {
        public int compare(String o1, String o2)
        {
            return o1.toLowerCase().compareTo(o2.toLowerCase());
        }
    });
    
    MealManagerRegistry mealManagerRegistry;
    
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
    
    
    private PieChart_Screen_MPS pieChart_Screen_MPS = null;
    private LineChart_MPS lineChart_MPS = null;
    
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
            "ingredients_index", "liquid_content", "water_content"));
    
    //##################################################################################################################
    // TotalMealView Table
    //##################################################################################################################
    private final ArrayList<String> totalMeal_Table_ColToHide = new ArrayList<String>(Arrays.asList(
            "plan_id", "meal_name", "meal_in_plan_id", "weight_of_meal"
    ));
    
    /**
     * LinkedHashMap<String, Pair<Integer, String>> totalMeal_macroColNamePos
     * LinkedHashMap<TotalMeal_MacroName, Pair< Position, Measurement>> totalMeal_macroColNamePos
     */
    private LinkedHashMap<String, Pair<Integer, String>> totalMeal_macroColName_And_Pos = new LinkedHashMap<>()
    {{
        put("total_protein", new Pair<>(null, "g"));
        put("total_carbohydrates", new Pair<>(null, "g"));
        put("total_sugars_of_carbs", new Pair<>(null, "g"));
        put("total_fats", new Pair<>(null, "g"));
        put("total_saturated_fat", new Pair<>(null, "g"));
        put("total_salt", new Pair<>(null, "g"));
        put("total_fibre", new Pair<>(null, "g"));
        put("total_water", new Pair<>(null, "ml"));
        put("total_calories", new Pair<>(null, "kcal"));
        // put("total_liquid",new Pair<>(null, "g"));
    }};
    
    private HashMap<String, Integer> totalMeal_Other_Cols_Pos = new HashMap<>()
    {{
        put("meal_time", null);
        put("meal_name", null);
    }};
    
    //##################################################################################################################
    // Other Table Customisations
    //##################################################################################################################
    private final ArrayList<String>
            
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
                // #########################################
                // Set Path Files
                // #########################################
                String userDirectory = new File("").getAbsolutePath(); // get path file of where this is being executed
                
                System.out.printf("\nDirectory: \n%s \n\n\nScripts Directory:\n%s", userDirectory, db_Scripts_Folder_Path);
                System.out.println("\n\n\nReading ENV Variables: host, port, user_name, ****, db_name");
                
                // #########################################
                // Get .env variables
                // #########################################
                Dotenv dotenv = Dotenv.configure()
                        .directory(userDirectory)
                        .filename(".env") // instead of '.env', use 'env'
                        .load();
                
                String host = dotenv.get("DB_HOST");
                String port = dotenv.get("DB_PORT");
                
                database_Name = dotenv.get("DB_NAME");
                user_name = dotenv.get("DB_USER");
                
                String password = dotenv.get("DB_PASS");
                
                if (host == null || port == null || user_name == null || password == null || database_Name == null)
                {
                    System.err.printf("\n\nDB Values: \nhost: %s \nport: %s \nuser_name: %s \ndatabase_Name: %s",
                            host, port, user_name, database_Name);
                    
                    throw new RuntimeException("Missing one or more required DB environment variables.");
                }
                
                System.out.println("\n\nSuccessfully retrieved ENV Variables: host, port, user_name, *****, db_name");
                
                // #########################################
                // Assigning values to variables &
                // #########################################
                
                // #########################################
                // Create DB Object & run SQL Scripts
                // #########################################
                
                MyJDBC db = new MyJDBC(
                        host,
                        port,
                        user_name,
                        password,
                        database_Name,
                        db_Scripts_Folder_Path,
                        db_File_Script_List_Name
                );
                
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
            
            MyJDBC db = new MyJDBC(
                    "localhost",
                    "3306",
                    user_name,
                    password,
                    database_Name,
                    db_Scripts_Folder_Path,
                    db_File_Script_List_Name
            );
            
            if (! db.get_DB_Connection_Status())
            {
                JOptionPane.showMessageDialog(null, "ERROR, Cannot Connect To Database!");
                return;
            }
            
            new Meal_Plan_Screen(db);
        }
    }
    
    public Meal_Plan_Screen(MyJDBC db)
    {
        super(db, true, "Gym App", 1925, 1082, 1300, 0);
        
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
        
        String errorMSG = "Error, Gathering Plan & Personal User Information!";
        
        ArrayList<ArrayList<String>> results = db.get_Multi_Column_Query(queryX, errorMSG);
        
        ArrayList<String> results1 = results != null ? results.getFirst() : null;
        
        user_id = results1 != null ? Integer.parseInt(results1.get(0)) : null;
        planID = results1 != null ? Integer.parseInt(results1.get(1)) : null;
        planName = results1 != null ? results1.get(2) : null;
        
        if (planID == null || user_id == null || planName == null || user_name == null)
        {
            System.err.printf("\n\nUsername : %s \nUser ID : %s \n\nSelected Plan ID : %s  \nSelected Plan Name : %s\n", user_name, user_id, planID, planName);
            return;
        }
        
        //#############################################################################################################
        // 2.) Getting Table Column Names
        //#############################################################################################################
        
        // column names : ingredients_in_sections_of_meal_calculation
        ingredients_ColumnNames = db.get_Column_Names_AL(tableIngredientsCalName);
        
        // column names : total_meal_view
        meal_total_columnNames = db.get_Column_Names_AL(tableTotalMealsTableName);
        
        // column names : plan_macro_target_calculations
        macroTargets_ColumnNames = db.get_Column_Names_AL(tablePlanMacroTargetsNameCalc);
        
        // Get table column names for plan_macros_left
        macrosLeft_columnNames = db.get_Column_Names_AL(tablePlanMacrosLeftName);
        
        // Get table column names for total_meal_view
        meal_total_columnNames = db.get_Column_Names_AL(tableTotalMealsTableName);
        
        // Get table column names for macros_per_pound_and_limits
        macros_And_Limits_ColumnNames = db.get_Column_Names_AL(tableMacrosPerPoundLimitName);
        
        //######################################################################
        // Check IF Data Collections Are NULL
        //######################################################################
        if (ingredients_ColumnNames == null || meal_total_columnNames == null || macroTargets_ColumnNames == null ||
                macrosLeft_columnNames == null || macros_And_Limits_ColumnNames == null)
        {
            System.err.printf("Error, Gathering Column Names for Tables: \n%s = %s%n \n%s = %s%n \n%s = %s%n \n%s = %s%n \n%s = %s%n",
                    
                    tableIngredientsCalName, ingredients_ColumnNames,
                    tableTotalMealsTableName, meal_total_columnNames,
                    tablePlanMacroTargetsNameCalc, macroTargets_ColumnNames,
                    tablePlanMacrosLeftName, macrosLeft_columnNames,
                    tableMacrosPerPoundLimitName, macros_And_Limits_ColumnNames
            );
            
            JOptionPane.showMessageDialog(this, "Error, Getting Column Names For Tables In GUI !!");
            window_Closed_Event();
            return;
        }
        
        //################################
        // column names : total_meal_view
        //################################
        for (int pos = 0; pos < meal_total_columnNames.size(); pos++)
        {
            String columnName = meal_total_columnNames.get(pos);
            
            if (totalMeal_macroColName_And_Pos.containsKey(columnName))
            {
                String symbol = totalMeal_macroColName_And_Pos.get(columnName).getValue1();
                totalMeal_macroColName_And_Pos.put(columnName, new Pair<>(pos, symbol));
            }
            else if (totalMeal_Other_Cols_Pos.containsKey(columnName))
            {
                totalMeal_Other_Cols_Pos.put(columnName, pos);
            }
        }
        
        //##############################################################################################################
        // Getting Number Of Meals & Sub meals Count
        //##############################################################################################################
        String query1 = String.format("SELECT COUNT(meal_in_plan_id) AS total_meals FROM %s WHERE plan_id = %s;", tableMealsInPlanName, planID);
        ArrayList<String> mealsInPlanCount = db.get_Single_Column_Query_AL(query1, "Error, getting Meal Counts!");
        
        String query2 = String.format("SELECT COUNT(div_meal_sections_id) AS total_sub_meals FROM %s WHERE plan_id = %s;", tableSub_MealsName, planID);
        ArrayList<String> dividedMealSectionsCount = db.get_Single_Column_Query_AL(query2, "Error, getting Sub-Meal Counts!");
        
        if (mealsInPlanCount == null | dividedMealSectionsCount == null)
        {
            String msg = "\n\nError, Getting Meal Count Or, Sub Meals Count";
            
            JOptionPane.showMessageDialog(this, msg);
            return;
        }
        
        int
                no_of_meals = Integer.parseInt(mealsInPlanCount.getFirst()),
                no_of_sub_meals = Integer.parseInt(dividedMealSectionsCount.getFirst());
        
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
        Loading_Screen loadingScreen = new Loading_Screen(totalProgress);
        
        //####################################################
        // Transferring PLan Data To Temp
        //####################################################
        if (! transfer_Plan_Data(planID, tempPlanID))
        {
            loadingScreen.window_Closed_Event();
            return;
        }
        
        loadingScreen.increaseBar(10);
        System.out.printf("\nChosen Plan: %s  & Chosen Plan Name: %s \n\n%s", planID, planName, lineSeparator);
        
        //####################################################
        // Transferring Targets From Chosen PLan to Temp
        //####################################################
        if (! transfer_Targets(planID, tempPlanID, true, false))
        {
            loadingScreen.window_Closed_Event();
            return;
        }
        
        loadingScreen.increaseBar(10);
        
        //####################################################
        // Transferring this plans Meals  Info to Temp-Plan
        //####################################################
        if (! (transfer_Meal_Ingredients(planID, tempPlanID)))
        {
            loadingScreen.window_Closed_Event();
            JOptionPane.showMessageDialog(null, "\n\nCannot Create Temporary Plan In DB to Allow Editing");
            return;
        }
        
        loadingScreen.increaseBar(10);
        
        //####################################################
        // Get IngredientTypes & Store Data
        //####################################################
        if (! (get_Ingredient_And_Store_Data()))
        {
            loadingScreen.window_Closed_Event();
            JOptionPane.showMessageDialog(null, "\n\nCannot Get Ingredients_Types & Stores Info \n\ngetIngredientsTypesAndStoresData()");
            return;
        }
        
        loadingScreen.increaseBar(10);
        
        //#############################################################################################################
        // 2.) Getting Meals In Plan : ID , Name , Meal Times
        //#############################################################################################################
        ArrayList<ArrayList<String>> meals_Info_In_Plan = new ArrayList<>();
        
        if (no_of_meals > 0)
        {
            String
                    query = String.format("SELECT meal_in_plan_id, meal_name, meal_time FROM %s WHERE plan_id = %s ORDER BY meal_time ASC;", tableMealsInPlanName, tempPlanID),
                    errorMSG1 = "Error, Unable to get Meals Info in this Plan!";
            
            meals_Info_In_Plan = db.get_Multi_Column_Query(query, errorMSG1);
            
            if (meals_Info_In_Plan == null)
            {
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
        
        int macrosInfoJP_YPos = 0;

        /*//#########################################################################
        // Setting up Horizontal Image Divider
        //#########################################################################
        int height = 75, width = 0;
        JPanel macrosDividerJPanel = new JPanel(new GridLayout(1, 1));
        macrosDividerJPanel.setPreferredSize(new Dimension(width, height));

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

        addToContainer(macrosDividerJPanel, label, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0, null);

        addToContainer(macrosInfoJPanel, macrosDividerJPanel, 0, macrosInfoJP_YPos += 1, 1, 1, 0.25, 0.25, "both", 0, 0, null);

        //#########################################################################
        // Add Space Divider
        //#########################################################################

        addToContainer(macrosInfoJPanel, createSpaceDivider(0, 20), 0, macrosInfoJP_YPos += 1, 1, 1, 0.25, 0.25, "both", 0, 0, null);
*/
        //#########################################################################
        // Setting up MacroTargets Table
        //#########################################################################
        // Getting data for plan_macro_target_calculations
        String
                planCalcQuery = String.format("SELECT * from %s WHERE plan_id = %s;", tablePlanMacroTargetsNameCalc, tempPlanID),
                errorMSG1 = "Error, Gathering Macros Targets Data!";
        
        ArrayList<ArrayList<Object>> planData = db.get_TableData_Objects_AL(planCalcQuery, tablePlanMacroTargetsNameCalc, errorMSG1);
        if (planData == null)
        {
            JOptionPane.showMessageDialog(getFrame(), errorMSG1);
            return;
        }
        
        macros_Targets_Table = new MacrosTargetsTable(db, macrosInfoJPanel, planData, macroTargets_ColumnNames, planID, tempPlanID,
                tablePlanMacroTargetsNameCalc, macroTargets_ColumnNames, null, macrosTargets_Table_ColToHide);
        
        addToContainer(macrosInfoJPanel, macros_Targets_Table, 0, macrosInfoJP_YPos += 1, + 1, 1, 0.25, 0.25, "both", 40, 0, null);
        
        //########################################
        // macroTargets Complete
        //########################################
        loadingScreen.increaseBar(10);
        
        //###########################################################################
        // planMacrosLeft Table Setup
        //###########################################################################
        
        // Get table data from plan_macros_left
        String
                macrosQuery = String.format("SELECT * from %s WHERE plan_id = %s;", tablePlanMacrosLeftName, tempPlanID),
                errorMSG_ML = "Error, Unable to get Plan Macros Left!";
        
        ArrayList<ArrayList<Object>> macrosData = db.get_TableData_Objects_AL(macrosQuery, tablePlanMacrosLeftName, errorMSG_ML);
        if (macrosData == null)
        {
            JOptionPane.showMessageDialog(getFrame(), errorMSG_ML);
            return;
        }
        
        macrosLeft_JTable = new MacrosLeftTable(db, macrosInfoJPanel, macrosData, macrosLeft_columnNames, planID, tempPlanID,
                tablePlanMacrosLeftName, macrosLeft_columnNames, null, macrosLeft_Table_ColToHide);
        
        addToContainer(macrosInfoJPanel, macrosLeft_JTable, 0, macrosInfoJP_YPos += 1, 1, 1, 0.25, 0.25, "both", 30, 0, null);
        
        //########################################
        // macroTargets Complete
        //########################################
        loadingScreen.increaseBar(10);
        
        //##############################################################################################################
        // Centre: Adding Meal Managers to Centre of Screen On ScrollPanel
        //##############################################################################################################
        
        // Create MealRegistry's for MealManagers
        mealManagerRegistry = new MealManagerRegistry(this, totalMeal_macroColName_And_Pos);
        
        // #####################################
        // Add MealManagers to GUI
        // #####################################
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
            String
                    subDivQuery = String.format("SELECT div_meal_sections_id FROM %s WHERE meal_in_plan_id = %s AND plan_id = %s;", tableSub_MealsName, mealInPlanID, tempPlanID),
                    errorMSG2 = String.format("Error, Unable to get Sub-Meal Info for: %s at %s", mealName, mealTime);
            
            ArrayList<ArrayList<String>> subMealsInMealArrayList = db.get_Multi_Column_Query(subDivQuery, errorMSG2);
            
            if (subMealsInMealArrayList == null)
            {
                JOptionPane.showMessageDialog(null, errorMSG2);
                return;
            }
            
            //#####################################################
            // Create MealManager
            //#####################################################
            MealManager mealManager = new MealManager(this, mealInPlanID, mealName, mealTime, subMealsInMealArrayList);
            
            // If Object Creation Failed Exit
            if (! mealManager.isObjectCreated())
            {
                JOptionPane.showMessageDialog(this, String.format("Error, Creating MealManager : %s [%s]", mealName, mealTime));
                return;
            }
            
            //###############################################
            // ADD MealManager Info to DATA
            //###############################################
            mealManagerRegistry.addMealManager(mealManager);
            
            //###############################################
            // ADD to GUI & Charts
            //###############################################
            add_And_Replace_MealManger_POS_GUI(mealManager, false, false); // Add to GUI
            
            //######################################################
            // Update Progress
            //######################################################
            loadingScreen.increaseBar(1 + subMealsInMealArrayList.size()); // + original meal + the sub-meal
        }
        
        //##############################################################################################################
        // GUI Alignments & Configurations
        //##############################################################################################################
        resizeGUI();
        setFrameVisibility(true);
        scroll_To_Top_of_ScrollPane();
    }
    
    //##################################################################################################################
    // Transfer SQL Data & Get Data Methods
    //##################################################################################################################
    // Transfer Data Methods
    private boolean transfer_Plan_Data(int fromPlan, int toPlan)
    {
        String query1 = String.format("""
                UPDATE `plans` AS `P`,
                (
                	SELECT plan_name, vegan FROM %s WHERE plan_id = ?
                ) AS `SRC`
                
                SET
                    `P`.`plan_name` = concat("(Temp) ",`SRC`.`plan_name`),`P`.`vegan` = `SRC`.`vegan`
                WHERE
                    `P`.`plan_id` = ?;""", tablePlansName);
        
        Object[] params = new Object[]{ fromPlan, toPlan };
        
        if (! (db.upload_Data2(query1, params,"Unable to Load / Transfer Plan Data!"))) { return false; }
        
        System.out.printf("\nPlanData Successfully transferred! \n\n%s", lineSeparator);
        return true;
    }
    
    private boolean transfer_Targets(int fromPlan, int toPlan, boolean deleteFromToPlan, boolean showConfirmMsg)
    {
        //####################################
        // Queries
        //####################################
        String query00 = String.format("DELETE FROM %s WHERE plan_id = ?;", tableMacrosPerPoundLimitName);
        String query01 = String.format("DROP TABLE IF EXISTS temp_%s;", tableMacrosPerPoundLimitName);
        
        String query02 = String.format("CREATE TABLE temp_%s AS SELECT * FROM %s WHERE plan_id = ?;",
                tableMacrosPerPoundLimitName, tableMacrosPerPoundLimitName);
        
        String query03 = String.format("UPDATE temp_%s SET plan_id = ?;", tableMacrosPerPoundLimitName);
        String query04 = String.format("INSERT INTO %s SELECT * FROM temp_%s;", tableMacrosPerPoundLimitName, tableMacrosPerPoundLimitName);
        
        String query05 = String.format("DROP TABLE temp_%s;", tableMacrosPerPoundLimitName);
        
        //####################################
        // Prepare Inputs For Execution
        //####################################
        String errorMSG = "Unable to Transfer Targets Data!";
        
        LinkedHashSet<Pair<String, Object[]>> queries_And_Params = new LinkedHashSet<>()
        {{
            if (deleteFromToPlan) { add(new Pair<>(query00, new Object[]{ toPlan })); }
            
            add(new Pair<>(query01, null));
            add(new Pair<>(query02, new Object[]{ fromPlan }));
            add(new Pair<>(query03, new Object[]{ toPlan }));
            add(new Pair<>(query04, null));
            add(new Pair<>(query05, null));
        }};
        
        //####################################
        // Execute Upload Statements
        //####################################
        if (! (db.upload_Data_Batch_Altogether2(queries_And_Params, errorMSG))) { return false; }
        
        if (showConfirmMsg)
        {
            JOptionPane.showMessageDialog(getFrame(), "\n\nTargets Successfully Saved");
        }
        
        //####################################
        // Return Output
        //####################################
        System.out.printf("\nTargets Successfully transferred! \n\n%s", lineSeparator);
        return true;
    }
    
    private boolean transfer_Meal_Ingredients(int fromPlanID, int toPlanID)
    {
        //################################################################
        // Delete temp tables if they already exist
        //################################################################
        String query0 = String.format("DROP TABLE IF EXISTS temp_%s;", tableIngredientsInMealSections);
        String query1 = String.format("DROP TABLE IF EXISTS temp_%s;", tableSub_MealsName);
        String query2 = String.format("DROP TABLE IF EXISTS temp_%s;", tableMealsInPlanName);
        
        //################################################################
        // Delete Meals
        //################################################################
        String query3 = String.format("DELETE FROM %s WHERE plan_id = ?;", tableMealsInPlanName);
        
        //################################################################
        // Transferring Meals From One Plan To Another
        //################################################################
        String query4 = String.format("CREATE table temp_%s AS SELECT * FROM %s WHERE plan_id = ? ORDER BY meal_in_plan_id;", tableMealsInPlanName, tableMealsInPlanName);
        
        String query5 = String.format("UPDATE temp_%s SET plan_id = ?;", tableMealsInPlanName);
        String query6 = String.format("INSERT INTO %s SELECT * FROM temp_%s;", tableMealsInPlanName, tableMealsInPlanName);
        
        //################################################################
        // Transferring Sections Of Meals From One Plan To Another
        //################################################################
        String query7 = String.format("CREATE table temp_%s AS SELECT * FROM %s WHERE plan_id = ? ORDER BY div_meal_sections_id;", tableSub_MealsName, tableSub_MealsName);
        String query8 = String.format("UPDATE temp_%s SET plan_id = ?;", tableSub_MealsName);
        String query9 = String.format("INSERT INTO %s SELECT * FROM temp_%s;", tableSub_MealsName, tableSub_MealsName);
        
        //################################################################
        // Transferring this plans Ingredients to Temp-Plan
        //################################################################
        // Create Table to transfer ingredients from original plan to temp
        String query10 = String.format("""
                CREATE table temp_%s AS
                SELECT i.*
                FROM %s i
                WHERE i.plan_id = ?;""", tableIngredientsInMealSections, tableIngredientsInMealSections);
        
        String query11 = String.format("UPDATE temp_%s SET plan_id = ?;", tableIngredientsInMealSections);
        String query12 = String.format("INSERT INTO %s SELECT * FROM temp_%s;", tableIngredientsInMealSections, tableIngredientsInMealSections);
        
        String query13 = String.format("DROP TABLE temp_%s;", tableMealsInPlanName);
        String query14 = String.format("DROP TABLE temp_%s;", tableIngredientsInMealSections);
        String query15 = String.format("DROP TABLE temp_%s;", tableSub_MealsName);
        
        //####################################################
        // Update
        //####################################################
        String errorMSG = "Error, Unable to Transfer Meal Ingredients";
        
        LinkedHashSet<Pair<String, Object[]>> queries_And_Params = new LinkedHashSet<>()
        {{
            add(new Pair<>(query0, null));
            add(new Pair<>(query1, null));
            add(new Pair<>(query2, null));
            add(new Pair<>(query3, new Object[]{ toPlanID }));
            add(new Pair<>(query4, new Object[]{ fromPlanID }));
            add(new Pair<>(query5, new Object[]{ toPlanID }));
            add(new Pair<>(query6, null));
            add(new Pair<>(query7, new Object[]{ fromPlanID }));
            add(new Pair<>(query8, new Object[]{ toPlanID }));
            add(new Pair<>(query9, null));
            add(new Pair<>(query10, new Object[]{ fromPlanID }));
            add(new Pair<>(query11, new Object[]{ toPlanID }));
            add(new Pair<>(query12, null));
            add(new Pair<>(query13, null));
            add(new Pair<>(query14, null));
            add(new Pair<>(query15, null));
        }};
        
        if (! (db.upload_Data_Batch_Altogether2(queries_And_Params, errorMSG))) { return false; }
        
        //####################################################
        // Output
        //####################################################
        System.out.printf("\nMealIngredients Successfully Transferred! \n\n%s", lineSeparator);
        return true;
    }
    
    //#################################################
    // Get / Update Methods
    //#################################################
    
    /**
     * Ingredient Types Mapped to Ingredient Names
     * IngredientTypes,
     * Stores
     */
    public boolean get_Ingredient_And_Store_Data()
    {
        //#################################################################################
        // Map IngredientTypes  To IngredientNames
        //#################################################################################
        if (! get_Ingredient_Types_Mapped_To_Names())
        {
            JOptionPane.showMessageDialog(this, "\n\nUnable to get 'Ingredient Types To Names'!");
            return false;
        }
        
        //#################################################################################
        // Get All The IngredientsType Inside The DB
        //#################################################################################
        String
                query1 = String.format("SELECT ingredient_type_name FROM %s ORDER BY ingredient_type_name ASC;", tableIngredientsTypeName),
                errorMSG1 = "Error, Unable to get Ingredient Types in Plan!";
        
        ingredientsTypesList = db.get_Single_Col_Alphabetically_Sorted(query1, errorMSG1);
        
        if (ingredientsTypesList == null)
        {
            JOptionPane.showMessageDialog(this, errorMSG1);
            return false;
        }
        
        //#################################################################################
        // Get All The Store Names Inside The DB
        //#################################################################################
        String
                query2 = String.format("SELECT store_name FROM %s ORDER BY store_name ASC;", tableStoresName),
                errorMSG2 = "Error, Unable to get Ingredient Stores in Plan!";
        
        storesNamesList = db.get_Single_Col_Alphabetically_Sorted(query2, errorMSG2);
        
        if (storesNamesList == null)
        {
            JOptionPane.showMessageDialog(this, errorMSG2);
            return false;
        }
        
        //#################################################################################
        // Success MSG
        //#################################################################################
        System.out.printf("\nIngredient Types & Store Names Successfully transferred! \n\n%s", lineSeparator);
        return true;
    }
    
    public boolean get_Ingredient_Types_Mapped_To_Names()
    {
        //###########################################################
        // Store ingredientTypes ID's & IngredientTypeName that occur
        //###########################################################
        String
                queryTypes = String.format(
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
                        ORDER BY N.ingredient_type_name;""", tableIngredientsInfoName, tableIngredientsTypeName),
                
                errorMSG = "\n\nUnable to update Ingredient Type Info";
        
        ArrayList<ArrayList<String>> ingredientTypesNameAndIDResults = db.get_Multi_Column_Query(queryTypes, errorMSG);
        
        if (ingredientTypesNameAndIDResults == null)
        {
            JOptionPane.showMessageDialog(getFrame(), errorMSG);
            return false;
        }
        
        //###########################################################
        // Clear List
        //###########################################################
        map_ingredientTypesToNames.clear();
        
        //######################################
        // Store all ingredient types & names
        //######################################
        for (ArrayList<String> rowData : ingredientTypesNameAndIDResults)
        {
            String ID = rowData.get(0);
            String ingredientType = rowData.get(1);
            
            //########################################
            // Get IngredientNames for Type
            //########################################
            String
                    query_Type_Names = String.format("SELECT ingredient_name FROM %s WHERE ingredient_type_id = %s ORDER BY ingredient_name;",
                    tableIngredientsInfoName, ID),
                    
                    errorMSG2 = String.format("Error, Unable to get Ingredient Names for Ingredient Type '%s'!", ingredientType);
            
            TreeSet<String> ingredientNames = db.get_Single_Col_Alphabetically_Sorted(query_Type_Names, errorMSG2);
            
            if (ingredientNames == null)
            {
                System.err.printf("\n\n%s", errorMSG);
                return false;
            }
            
            //########################################
            // Mapping Ingredient Type to Names
            //########################################
            map_ingredientTypesToNames.put(ingredientType, ingredientNames);
        }
        
        //########################################
        // Output
        //########################################
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
        //  PieChart
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
            
            open_Ingredients_Screen();
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
    
    // ################################################################
    // Delete BTN Actions
    // ###############################################################
    private void delete_btnAction()
    {
        //###########################################################
        // Ask for Confirmation
        //###########################################################
        if (! areYouSure("Delete All Meals", "Are you want to 'DELETE' all the meals in this plan?")) { return; }
        
        //###########################################################
        // Delete all the meals in the plans in SQL
        //###########################################################
        String queryDelete = "DELETE FROM meals_in_plan WHERE plan_id = ?";
        
        Object[] params = new Object[]{ tempPlanID };
        
        if (! db.upload_Data2(queryDelete, params, "Error, unable to DELETE meals in plan!")) { return; }
        
        JOptionPane.showMessageDialog(this, "\n\nSuccessfully, DELETED all meals in plan!");
        
        //###########################################################
        // DELETE all the meals in Memory
        //###########################################################
        mealManagerRegistry.delete_MealManagers_MPS();
        
        //###########################################################
        // Update MacrosLeft
        //###########################################################
        update_MacrosLeftTable();
        
        //###########################################################
        // Update External Graphs
        //###########################################################
        update_External_Charts(true, "clear", null, null, null);
    }
    
    // ###############################################################
    // Recipe BTN Actions
    // ###############################################################
    private void recipeList_BtnAction_OpenScreen()
    {
    
    }
    
    // ###############################################################
    // Pie Chart BTN Actions
    // ###############################################################
    public Boolean is_PieChart_Screen_Open()
    {
        return pieChart_Screen_MPS != null;
    }
    
    public void removePieChartScreen()
    {
        pieChart_Screen_MPS = null;
    }
    
    private void pieChart_BtnAction_OpenScreen()
    {
        if (is_PieChart_Screen_Open())
        {
            pieChart_Screen_MPS.makeJFrameVisible();
            return;
        }
        
        pieChart_Screen_MPS = new PieChart_Screen_MPS(db, this);
    }
    
    // #############################
    // PieChart DATA Methods
    // #############################
    private void clear_PieChart_DATA_MPS()
    {
        if (! is_PieChart_Screen_Open()) { return; }
        
        pieChart_Screen_MPS.clear();
    }
    
    private void refresh_PieChart_DATA_MPS()
    {
        if (! is_PieChart_Screen_Open()) { return; }
        
        pieChart_Screen_MPS.refresh();
    }
    
    private void update_PieChart_MealName(Integer mealInPlanID)
    {
        if (! is_PieChart_Screen_Open()) { return; }
        
        pieChart_Screen_MPS.update_PieChart_MealName(mealInPlanID);
    }
    
    private void update_PieChart_MealTime(Integer mealInPlanID)
    {
        if (! is_PieChart_Screen_Open()) { return; }
        
        pieChart_Screen_MPS.update_PieChart_MealTime(mealInPlanID);
    }
    
    private void update_PieChart_DATA()
    {
        if (! is_PieChart_Screen_Open()) { return; }
        
        pieChart_Screen_MPS.updateData();
    }
    
    private void add_Meal_Manager_PieChart(MealManager mealManager)
    {
        if (! is_PieChart_Screen_Open()) { return; }
        
        pieChart_Screen_MPS.add_MealManager_To_GUI(mealManager);
    }
    
    private void delete_MealManager_PieChart(MealManager mealManager)
    {
        if (! is_PieChart_Screen_Open()) { return; }
        
        // Data Handling already been processed, screen just needs to be re-drawn
        pieChart_Screen_MPS.deleted_MealManager_PieChart(mealManager);
    }
    
    // ###############################################################
    // Line Chart BTN Actions
    // ###############################################################
    private boolean is_LineChart_Screen_Open()
    {
        return lineChart_MPS != null;
    }
    
    public void removeLineChartScreen()
    {
        lineChart_MPS = null;
    }
    
    private void lineChart_Btn_Action_OpenScreen()
    {
        if (! is_LineChart_Screen_Open())
        {
            lineChart_MPS = new LineChart_MPS(db, this);
            return;
        }
        
        lineChart_MPS.makeJFrameVisible();
    }
    
    // #############################
    // LineChart DATA Methods
    // #############################
    private void updateLineChartData(MealManager mealManager, Second previousTime, Second currentTime)
    {
        if (! is_LineChart_Screen_Open()) { return; }
        
        lineChart_MPS.update_MealManager_ChartData(mealManager, previousTime, currentTime);
    }
    
    private void add_Meal_To_LineChart(MealManager mealManager)
    {
        if (! is_LineChart_Screen_Open()) { return; }
        
        lineChart_MPS.add_New_MealManager_Data(mealManager);
    }
    
    private void deleteLineChartData(Second currentTime)
    {
        if (! is_LineChart_Screen_Open()) { return; }
        
        lineChart_MPS.delete_MealManager_Data(currentTime);
    }
    
    private void clearLineChartDataSet()
    {
        if (! is_LineChart_Screen_Open()) { return; }
        
        lineChart_MPS.clear_LineChart_Dataset();
    }
    
    private void refresh_LineChart_Data()
    {
        if (! is_LineChart_Screen_Open()) { return; }
        
        lineChart_MPS.refresh_Data();
    }
    
    // ###############################################################
    // Scroll Up BTN Action
    // ###############################################################
    private void scrollUp_BtnAction()
    {
        super.scroll_To_Top_of_ScrollPane();
    }
    
    // ###############################################################
    // Refresh BTN Actions / Plan Actions
    // ###############################################################
    private void refreshPlan(boolean askPermission)
    {
        String txt = "Are you sure you want to refresh all the meals in this plan?";
        if ((! (get_IsPlanSelected())) || askPermission && ! (areYouSure("Refresh Meal Plan Data", txt))) { return; }
        
        //####################################################################
        // Refresh DB Data
        //####################################################################
        if (! (transfer_Meal_Ingredients(planID, tempPlanID))) // transfer meals and ingredients from temp plan to original plan
        {
            JOptionPane.showMessageDialog(this, "`\n\nError couldn't transfer ingredients data from temp to real plan !!");
            return;
        }
        
        //####################################################################
        // Refresh MealManagers Collections
        //####################################################################
        mealManagerRegistry.refresh_MealManagers_MPS();
        
        //####################################################################
        // Re-draw GUI Screen
        //####################################################################
        reDraw_GUI();
        scrollUp_BtnAction(); // Reposition GUI
        
        //####################################################################
        // Refresh Macro-Targets Table
        //####################################################################
        refresh_MacroTargets(); // if macroTargets changed ask the user if they would like to refresh this data
        
        update_MacrosLeftTable(); // Update macrosLeft, refresh methods do not work here, needs to be computed
        
        //####################################################################
        // Update External Charts
        //####################################################################
        update_External_Charts(true, "refresh", null, null, null);
    }
    
    // ###############################################################
    // Add Meal BTN Actions
    // ###############################################################
    private void add_Meal_Btn_Action()
    {
        //##############################################################################################################
        //
        //##############################################################################################################
        if (! (get_IsPlanSelected()))
        {
            JOptionPane.showMessageDialog(null, "\n\nCannot Add A  Meal As A Plan Is Not Selected! \nPlease Select A Plan First!!");
            return;
        }
        
        //##############################################################################################################
        // Add MealManager To GUI & Charts
        //##############################################################################################################
        MealManager mealManager = new MealManager(this);
        
        //###############################################
        // If Object Creation Failed Exit
        //###############################################
        if (! mealManager.isObjectCreated()) { return; }
        
        //###############################################
        // ADD MealManager Info to DATA
        //###############################################
        mealManagerRegistry.addMealManager(mealManager);
        
        //###############################################
        // ADD to GUI & Charts
        //###############################################
        add_And_Replace_MealManger_POS_GUI(mealManager, true, true); // Add to GUI
        
        //###############################################
        // Add to External Charts
        //###############################################
        update_External_Charts(true, "add", mealManager, null, mealManager.getCurrentMealTime());
    }
    
    public void add_And_Replace_MealManger_POS_GUI(MealManager mealManager, boolean reOrder, boolean expandView)
    {
        //###############################################
        // Exit Clause
        //###############################################
        if (! mealManager.isObjectCreated()) { return; }
        
        //###############################################
        // Add to GUI Meal Manager & Its Space Divider
        //###############################################
        if (! reOrder) // Just add to GUI
        {
            addToContainer(scrollJPanelCenter, mealManager.getCollapsibleJpObj(), 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
            addToContainer(scrollJPanelCenter, mealManager.getSpaceDividerForMealManager(), 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "both", 50, 0, null);
        }
        
        //###############################################
        // Clear and Redraw
        //###############################################
        else
        {
            reDraw_GUI();
        }
        
        //###############################################
        // Expand Meal ?
        //###############################################
        if (expandView) { mealManager.expand_JPanel(); }
        
        //###############################################
        // Add to GUI Meal Manager & Its Space Divider
        //###############################################
        resizeGUI();
        
        //###############################################
        // Scroll to MealManager
        //###############################################
        scrollToJPanelOnScreen(mealManager.getCollapsibleJpObj());
    }
    
    public void reDraw_GUI()
    {
        mealManagerRegistry.sort_MealManager_AL(); // Sort
        
        scrollJPanelCenter.removeAll(); // Clear Screen
        
        // Re-Draw all MealManager to GUI
        ArrayList<MealManager> mealManager_ArrayList = mealManagerRegistry.get_MealManager_ArrayList();
        for (MealManager mm : mealManager_ArrayList)
        {
            System.out.printf("\n\nMealManagerID: %s \nMealName : %s \nMealTime : %s",
                    mm.getMealInPlanID(), mm.getCurrentMealName(), mm.get_Current_Meal_Time_GUI());
            
            mm.collapse_MealManager(); // Collapse all meals
            
            // Add MealManager and its Space Separator to GUI
            addToContainer(scrollJPanelCenter, mm.getCollapsibleJpObj(), 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
            addToContainer(scrollJPanelCenter, mm.getSpaceDividerForMealManager(), 0, getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "both", 50, 0, null);
        }
        
        scrollJPanelCenter.repaint();
    }
    
    // ###############################################################
    // Save Plan BTN
    // ###############################################################
    private void saveMealData(boolean askPermission, boolean showMsg)
    {
        // ##############################################################################
        // Exit Clauses
        // ##############################################################################
        // If no plan is selected exit Or,  if user rejects saving when asked  exit
        String txt = "Are you want to save all the data in this meal plan?";
        if ((! (get_IsPlanSelected())) || askPermission && ! (areYouSure("Save Meal Plan Data", txt))) { return; }
        
        // ##############################################################################
        // Remove Delete MealManagers & Check if
        // ##############################################################################
        boolean noMealsLeft = true;
        
        ArrayList<MealManager> mealManager_ArrayList = mealManagerRegistry.get_MealManager_ArrayList();
        
        Iterator<MealManager> it = mealManager_ArrayList.iterator();
        while (it.hasNext())
        {
            MealManager mealManager = it.next();
            if (mealManager.is_Meal_Deleted())
            {
                it.remove();
                continue;
            }
            noMealsLeft = false; // if this line is reached there is a  MealManager (for loop) that hasn't been deleted
        }
        
        // ##############################################################################
        // Save meal plan data in DB
        // ##############################################################################
        // if there are no meals in the temp plan delete all meals / ingredients from original plan as opposed to a transfer
        if (noMealsLeft)
        {
            System.out.println("\n\n#################################### \n1.) saveMealData() Empty Meal Plan Save");
            
            String query = String.format("DELETE FROM %s WHERE plan_id = ?;", tableMealsInPlanName);
            
            Object[] params = new Object[]{ planID };
            
            if (! (db.upload_Data2(query, params, "Error 2, Unable to Save Meal Data!"))) { return; }
        }
        else // because there are meals save them
        {
            if ((! (transfer_Meal_Ingredients(tempPlanID, planID)))) // transfer meals and ingredients from temp plan to original plan
            {
                System.out.println("\n\n#################################### \n2.) saveMealData() Meals Transferred to Original Plan");
                
                JOptionPane.showMessageDialog(this, "\n\n2.)  Error \nUnable to save meals in plan!");
                return;
            }
            
            // ###############################################################################
            // Instructing Meal Manager Tables To Update Their Model Data
            // ##############################################################################
            Iterator<MealManager> it2 = mealManager_ArrayList.iterator();
            
            while (it2.hasNext())
            {
                MealManager mealManager = it2.next();
                mealManager.saveData(false);
            }
        }
        
        // ##############################################################################
        // Successful Message
        // ##############################################################################
        if (showMsg) { JOptionPane.showMessageDialog(this, "\n\nAll Meals Are Successfully Saved!"); }
    }
    
    // ###############################################################
    // Add Ingredients Screen & Ingredient Methods
    // ###############################################################
    private void open_Ingredients_Screen()
    {
        if (is_IngredientScreen_Open())
        {
            ingredientsInfoScreen.makeJFrameVisible();
            return;
        }
        
        ingredientsInfoScreen = new Ingredients_Info_Screen(db, this);
    }
    
    private Boolean is_IngredientScreen_Open()
    {
        return ingredientsInfoScreen != null;
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
    
    // ###############################################################
    // Macro Targets Screen & Target Methods
    // ###############################################################
    private void open_MacrosTargets_Screen()
    {
        if (! (get_IsPlanSelected()))
        {
            return;
        }
        
        if (is_MacroTargetsScreen_Open())
        {
            macrosTargets_Screen.makeJFrameVisible();
            return;
        }
        macrosTargets_Screen = new Macros_Targets_Screen(db, this, tempPlanID, planName);
    }
    
    private boolean is_MacroTargetsScreen_Open()
    {
        return macrosTargets_Screen != null;
    }
    
    public void remove_macrosTargets_Screen()
    {
        macrosTargets_Screen = null;
    }
    
    public void macrosTargetsChanged(boolean bool)
    {
        macroTargetsChanged = bool;
    }
    
    // Booleans
    public boolean hasMacroTargetsChanged()
    {
        return macroTargetsChanged;
    }
    
    private void saveMacroTargets(boolean askPermission, boolean showUpdateMsg)
    {
        // ##############################################
        // If targets haven't changed exit
        // ##############################################
        if (! hasMacroTargetsChanged())
        {
            return;
        }
        
        // ################################################
        // If askPermission as permission to save targets
        // ################################################
        if (askPermission)
        {
            int reply = JOptionPane.showConfirmDialog(this, String.format("Would you like to save your MacroTarget  Changes Too?"),
                    "Save Macro Targets", JOptionPane.YES_NO_OPTION); //HELLO Edit
            
            if (reply == JOptionPane.NO_OPTION || reply == JOptionPane.CLOSED_OPTION)
            {
                return;
            }
        }
        
        if (transfer_Targets(tempPlanID, planID, false, showUpdateMsg))
        {
            macrosTargetsChanged(false);
            update_Targets_And_MacrosLeftTables();
        }
    }
    
    // ###############################################################
    // Scroll Down BTN Action
    // ###############################################################
    private void scrollDown_BtnAction()
    {
        super.scroll_To_Bottom_of_ScrollPane();
    }
    
    //##################################################################################################################
    // Other Methods
    //##################################################################################################################
    @Override
    public void window_Closed_Event()
    {
        // ##########################################
        // Ask to Save DATA
        // ##########################################
        if (hasMacroTargetsChanged()) // If targets have changed, save them?
        {
            saveMacroTargets(true, false);
        }
        
        saveMealData(true, false);  //Meal Data
        
        // ##########################################
        // Close Other Windows If Open
        // ##########################################
        if (is_MacroTargetsScreen_Open())
        {
            macrosTargets_Screen.window_Closed_Event();
        }
        if (is_IngredientScreen_Open()) // HELLO Refactor into screen method
        {
            ingredientsInfoScreen.window_Closed_Event();
        }
        if (is_PieChart_Screen_Open())
        {
            pieChart_Screen_MPS.window_Closed_Event();
        }
        if (is_LineChart_Screen_Open())
        {
            lineChart_MPS.window_Closed_Event();
        }
        
        // ##########################################
        // Close PieCharts Open by MealManagers
        // ##########################################
        Iterator<MealManager> it = mealManagerRegistry.get_MealManager_ArrayList().iterator();
        while (it.hasNext())
        {
            it.next().close_PieChartScreen();
            it.remove();
        }
        
        // ##########################################
        // Close DB Pool
        // ##########################################
        db.close_Connection();
    }
    
    public void update_External_Charts(boolean mealPlanScreen_Action, String action, MealManager mealManager,
                                       Second previousMealTime, Second currentMealTime)
    {
        //####################################################################
        // MealPlanScreen
        //####################################################################
        if (mealPlanScreen_Action)
        {
            if (action.equals("add")) // New MealManager Added to GUI
            {
                // LineChart Add
                add_Meal_To_LineChart(mealManager);
                
                // PieChart MPS Screen
                add_Meal_Manager_PieChart(mealManager);
            }
            else if (action.equals("clear")) // Delete Button requested on MealPlanScreen
            {
                // Clear LineChart Data
                clearLineChartDataSet();
                
                // Clear PieChart Screen
                clear_PieChart_DATA_MPS();
            }
            else if (action.equals("refresh"))
            {
                // Refresh LineChart Data
                refresh_LineChart_Data();
                
                //Refresh PieChart Screen
                refresh_PieChart_DATA_MPS();
            }
            
            //##########################
            // Exit
            //##########################
            return;
        }
        
        //####################################################################
        // MealManager Requested Action
        //####################################################################
        if (action.equals("update")) // Update MealManager Time
        {
            // Update LineChart Data
            updateLineChartData(mealManager, previousMealTime, currentMealTime);
            
            // Update PieChart DATA
            update_PieChart_DATA();
        }
        else if (action.equals("delete")) // Deleted MealManager
        {
            // Delete LineChart Data
            deleteLineChartData(previousMealTime);
            
            // Delete PieChart AKA Re-draw GUI
            delete_MealManager_PieChart(mealManager);
        }
        else if (action.equals("mealTime")) // MealTime on MealManager Changed
        {
            // Change data points time on LineChart Data
            updateLineChartData(mealManager, previousMealTime, currentMealTime);
            
            // Update PieChart Title OF Meal & Refresh Interface
            update_PieChart_MealTime(mealManager.getMealInPlanID());
        }
        else if (action.equals("mealName")) // MealTime on MealManager Changed
        {
            //############################
            // LineChart
            //############################
            // Nothing Changes
            
            // Change PieChart MealName
            update_PieChart_MealName(mealManager.getMealInPlanID());
        }
        else if (action.equals("refresh")) // Refresh mealPlan was requested
        {
            
            // Refresh MealManager
            updateLineChartData(mealManager, previousMealTime, currentMealTime);
            
            // Change PieChart MealName
            update_PieChart_MealName(mealManager.getMealInPlanID());
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
    // MacrosLeft Targets
    //#########################################
    public void update_MacrosTargetTable()
    {
        macros_Targets_Table.updateMacrosTargetsTable();
    }
    
    private void refresh_MacroTargets()
    {
        // ##############################################
        // If targets have changed prompt to Refresh
        // ##############################################
        if (hasMacroTargetsChanged())
        {
            int reply = JOptionPane.showConfirmDialog(this, String.format("Would you like to refresh your MacroTargets Too?"),
                    "Refresh Macro Targets", JOptionPane.YES_NO_OPTION); //HELLO Edit
            
            if (reply == JOptionPane.YES_OPTION)
            {
                if (transfer_Targets(planID, tempPlanID, true, false))
                {
                    JOptionPane.showMessageDialog(this, "\n\nMacro-Targets Successfully Refreshed!!");
                    macrosTargetsChanged(false);
                    
                    macros_Targets_Table.refreshData();
                }
            }
        }
    }
    
    //##########################################
    // MacrosLeft Table
    //#########################################
    public void update_MacrosLeftTable()
    {
        macrosLeft_JTable.updateMacrosLeftTable();
    }
    
    public MacrosLeftTable getMacrosLeft_JTable()
    {
        return macrosLeft_JTable;
    }
    
    //##################################################################################################################
    //  Accessor Methods
    //##################################################################################################################
    public boolean get_IsPlanSelected()
    {
        if (planID == null)
        {
            JOptionPane.showMessageDialog(this, "Please Select A Plan First!");
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
    
    //###########################################
    // Objects
    //###########################################
    public MealManagerRegistry get_MealManagerRegistry()
    {
        return mealManagerRegistry;
    }
    
    public JPanel getScrollJPanelCenter()
    {
        return scrollJPanelCenter;
    }
    
    //####################################################################
    // Collections :  Accessor Methods
    //#####################################################################
    
    // Others
    public TreeMap<String, TreeSet<String>> getMap_ingredientTypesToNames()
    {
        return map_ingredientTypesToNames;
    }
    
    public TreeSet<String> get_IngredientsTypes_List()
    {
        return ingredientsTypesList;
    }
    
    public TreeSet<String> get_StoresNames_List()
    {
        return storesNamesList;
    }
    
    //###########################################
    // TotalMeal Table Collections
    //###########################################
    public ArrayList<String> getTotalMeal_Table_ColToHide()
    {
        return totalMeal_Table_ColToHide;
    }
    
    public ArrayList<String> getMeal_total_columnNames()
    {
        return meal_total_columnNames;
    }
    
    public HashMap<String, Integer> get_TotalMeal_Other_Cols_Pos()
    {
        return totalMeal_Other_Cols_Pos;
    }
    
    //###########################################
    // Ingredients Table Collections
    //###########################################
    public ArrayList<String> getIngredients_ColumnNames()
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
