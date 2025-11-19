package App_Code.Objects.Tables;

import App_Code.Objects.Data_Objects.MetaData_ID_Object.Meal_ID;
import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JDBC.Null_MYSQL_Field;
import App_Code.Objects.Database_Objects.JDBC.Query_Results;
import App_Code.Objects.Database_Objects.Shared_Data_Registry;
import App_Code.Objects.Tables.JTable_JDBC.Children.IngredientsTable;
import App_Code.Objects.Tables.JTable_JDBC.Children.ViewDataTables.MacrosLeft_Table;
import App_Code.Objects.Tables.JTable_JDBC.Children.ViewDataTables.TotalMeal_Table;
import App_Code.Objects.Gui_Objects.CollapsibleJPanel;
import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Gui_Objects.IconPanel;
import App_Code.Objects.Screens.Graph_Screens.PieChart_MealManager_Screen.Pie_Chart_Meal_Manager_Screen;
import App_Code.Objects.Screens.Meal_Plan_Screen;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;
import org.jfree.data.time.Second;

import javax.swing.*;
import java.awt.*;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MealManager
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    
    // Boolean Variables
    private boolean
            isObjectCreated = false,
            mealManagerInDB = false,
            hasMealBeenDeleted = false,
            hasMealNameBeenChanged = false,
            hasMealTimeBeenChanged = false;
    
    // Integer Variables
    private Integer meal_In_Plan_ID, tempPlanID, planID, yPoInternally = 0;
    
    // String Variables
    String lineSeparator = "###############################################################################";
    private String savedMealName = "", currentMealName = "";
    
    // Time Variables
    private Second savedMealTime = null, currentMealTime = null;
    private DateTimeFormatter strictTimeFormatter = DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);
    
    // Collections
    private ArrayList<String> mealTotalTable_ColumnNames, ingredientsTable_ColumnNames;
    private ArrayList<String> totalMeal_Table_ColToHide, ingredientsTableUnEditableCells, ingredients_Table_Col_Avoid_Centering,
            ingredientsInMeal_Table_ColToHide;
    private TreeMap<String, TreeSet<String>> map_ingredientTypesToNames;
    private ArrayList<IngredientsTable> ingredientTables_In_MealManager = new ArrayList<>();
    private HashMap<String, Integer> totalMeal_Other_Cols_Pos;
    
    //################################################################################
    // Objects
    //################################################################################
    
    // Other Objects
    private JPanel collapsibleCenterJPanel, spaceDividerForMealManager = new JPanel();
    private MyJDBC db;
    private GridBagConstraints gbc;
    private Container container;
    private CollapsibleJPanel collapsibleJpObj;
    private Shared_Data_Registry sharedDataRegistry;
    
    // Screens
    private Pie_Chart_Meal_Manager_Screen pie_chart_meal_manager_screen;
    
    // Table Objects
    private MacrosLeft_Table macrosLeft_JTable;
    private Meal_Plan_Screen meal_plan_screen;
    private TotalMeal_Table totalMealTable;
    
    //##################################################################################################################
    // Constructors
    //##################################################################################################################
    public MealManager(Meal_Plan_Screen meal_plan_screen, MyJDBC db, MacrosLeft_Table macrosLeft_JTable,
                       Meal_ID meal_ID_Obj, LinkedHashMap<Integer, ArrayList<ArrayList<Object>>> sub_Meal_DATA,
                       ArrayList<Object> total_Meal_Data
    )
    {
        //################################################
        // Global Variables
        //################################################
        this.meal_plan_screen = meal_plan_screen;
        this.db = db;
        this.macrosLeft_JTable = macrosLeft_JTable;
        
        meal_In_Plan_ID = meal_ID_Obj.get_ID();
        tempPlanID = meal_plan_screen.getTempPlanID();
        planID = meal_plan_screen.getPlanID();
        
        // Set Meal Time Variables
        Second timeStringFormatted = localTimeToSecond(meal_ID_Obj.get_Meal_Time()); // turns etc 09:30:00 into 09:30 for GUI purposes & in second format
        setTimeVariables(false, timeStringFormatted, timeStringFormatted); // Set MealTime Variables
        
        // Set Meal Name Variables
        String mealName = meal_ID_Obj.get_Name();
        setMealNameVariables(false, mealName, mealName); // Set MealName Variables
        
        setMealManagerInDB(true);  // Set Variable which identifies in this meal associated with this object is in the database
        
        //################################################
        // Setup Methods
        //################################################
        setup_GUI(total_Meal_Data); // GUI
        
        add_Multiple_Sub_Meals(sub_Meal_DATA); // Add Sub-Meal to GUI
    }
    
    //
    public MealManager(Meal_Plan_Screen meal_plan_screen, MyJDBC db, MacrosLeft_Table macrosLeft_JTable)
    {
        //############################################################################
        // Setting Variables
        //############################################################################
        this.meal_plan_screen = meal_plan_screen;
        this.macrosLeft_JTable = macrosLeft_JTable;
        this.db = db;
        
        tempPlanID = meal_plan_screen.getTempPlanID();
        planID = meal_plan_screen.getPlanID();
        
        //############################################################################
        // Getting user input for Meal Name & Time
        //############################################################################
        String new_Meal_Name = promptUserForMealName(true, false);
        
        if (new_Meal_Name == null) { return; } // Error occurred in validation checks above
        
        //############################################################################
        // Validating User Input
        //############################################################################
        LocalTime new_Meal_Time = promptUserForMealTime(true, false);
        
        if (new_Meal_Time == null) { return; } // Error occurred in validation checks above
        
        //############################################################################
        // Upload & Fetch Variables
        //############################################################################
        String errorMSG = String.format("\n\nError Creating Meal with credentials: \n\nMeal Name: '%s' \nMeal Time: %s!", new_Meal_Name, new_Meal_Time);
        
        LinkedHashSet<Pair<String, Object[]>> upload_Queries_And_Params = new LinkedHashSet<>();
        LinkedHashSet<Pair<String, Object[]>> fetch_Queries_And_Params = new LinkedHashSet<>();
        
        int sub_Meal_ID;
        ArrayList<ArrayList<Object>> sub_Meal_DATA;
        ArrayList<Object> total_Meal_Data = null;
        
        //################################################
        // Uploads
        //###############################################
        
        // 1.) Create Meal Upload
        String upload_Q1 = "INSERT INTO meals_in_plan (plan_id, meal_name, meal_time) VALUES (?,?,?);";
        upload_Queries_And_Params.add(new Pair<>(upload_Q1, new Object[]{ tempPlanID, new_Meal_Name, new_Meal_Time }));
        
        //######################################
        // 2.) Set Meal ID
        //######################################
        String var_Meal_ID = "@mealID";
        String upload_Q2 = String.format("Set %s = LAST_INSERT_ID();", var_Meal_ID);
        upload_Queries_And_Params.add(new Pair<>(upload_Q2, null));
        
        //######################################
        // 3.) Create Sub-Meal
        //######################################
        String upload_Q3 = String.format( """
                INSERT INTO divided_meal_sections (meal_in_plan_id, plan_id) VALUES
                (%s, ?);""", var_Meal_ID);
        upload_Queries_And_Params.add(new Pair<>(upload_Q3, new Object[]{ tempPlanID }));
        
        //######################################
        // 4.) Set Sub-Meal ID
        //######################################
        String var_Sub_Meal_ID = "@subMealID";
        String upload_Q4 = String.format("Set %s = LAST_INSERT_ID();", var_Sub_Meal_ID);
        
        upload_Queries_And_Params.add(new Pair<>(upload_Q4, null));
        
        //########################################
        // 5.) Insert None of the Above into Sub-Meal
        //########################################
        
        String upload_Q5 = String.format("""
                INSERT INTO ingredients_in_sections_of_meal
                (plan_id, pdid, div_meal_sections_id, ingredient_id, quantity) VALUES
                (?, ?, %s, ?, ?);""", var_Sub_Meal_ID);
        
        upload_Queries_And_Params.add(new Pair<>(upload_Q5,
                new Object[]{ tempPlanID, new Null_MYSQL_Field(Types.INTEGER), 1, 0 }));
        
        //#######################################################
        // Fetch Queries
        //#######################################################
        
        // 1.) Get Meal_ID
        String get_Q1 = String.format("SELECT %s;", var_Meal_ID);
        fetch_Queries_And_Params.add(new Pair<>(get_Q1, null));
        
        //#############################
        // 2.) Get Sub-Meal ID
        //#############################
        String get_Q2 = String.format("SELECT %s;", var_Sub_Meal_ID);
        fetch_Queries_And_Params.add(new Pair<>(get_Q2, null));
        
        //#############################
        // 3.) Get Ingredients Table DATA
        //#############################
        String get_Q3 = String.format("""
                SELECT *
                FROM ingredients_in_sections_of_meal_calculation
                WHERE plan_id = ? AND div_meal_sections_id = %s;""", var_Sub_Meal_ID);
        fetch_Queries_And_Params.add(new Pair<>(get_Q3, new Object[]{ tempPlanID }));
        
        //#############################
        // 4.) Get Total Meal DATA
        //#############################
        String get_Q4 = String.format("""
                SELECT *
                FROM total_meal_view
                WHERE plan_id = ? AND meal_in_plan_id = %s;""", var_Meal_ID);
        fetch_Queries_And_Params.add(new Pair<>(get_Q4, new Object[]{ tempPlanID }));
        
        //#######################################################
        // Execute Query
        //#######################################################
        Query_Results results_OBJ = db.upload_And_Get_Batch(upload_Queries_And_Params, fetch_Queries_And_Params, errorMSG);
        
        if (results_OBJ == null || results_OBJ.is_Empty()) { System.err.println("\n\n\nFailed Creating Meal"); return; }
        
        //#######################################################
        // Set Variables from Results
        //#######################################################
        try
        {
            meal_In_Plan_ID = ((Number)  results_OBJ.get_Result_Object(0)).intValue();
            sub_Meal_ID = ((Number) results_OBJ.get_Result_Object(1)).intValue();
            
            sub_Meal_DATA = results_OBJ.get_Fetched_Result_2D_AL(2);
            total_Meal_Data = results_OBJ.get_Result_1D_AL(3);
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s", e);
            return;
        }
     
        //#############################
        // Set Name & Time Variables
        //#############################
        setMealNameVariables(false, new_Meal_Name, new_Meal_Name); // Set MealName Variables
        
        Second convertedNewMealTime = localTimeToSecond(new_Meal_Time);
        
        setTimeVariables(false, convertedNewMealTime, convertedNewMealTime);     // Set MealTime Variables
        
        setMealManagerInDB(false);
        
        //#######################################################
        // Add Meals To GUI
        //#######################################################
        setup_GUI(total_Meal_Data); // GUI
        
        add_Sub_Meal(false, sub_Meal_ID, sub_Meal_DATA); // Add Sub-Meal to GUI
        
        //#######################################################
        // Return Variables
        //#######################################################
        isObjectCreated = true;
    }
    
    //##################################################################################################################
    //  Setup
    //##################################################################################################################
    private void iconSetup()
    {
        int iconSize = 40;
        //########################################################################
        // Icons Top RIGHT
        //########################################################################
        JPanel eastJPanel = collapsibleJpObj.get_East_JPanel();
        eastJPanel.setLayout(new GridBagLayout());
        
        IconPanel iconPanel = new IconPanel(2, 10, "East");
        JPanel iconPanelInsert = iconPanel.getIconJpanel();
        
        add_To_Container(eastJPanel, iconPanel.getIconAreaPanel(), 0, 0, 1, 1, 0.25, 0.25, "horizontal", 10, 0, null);
        
        //##########################
        // Pie Chart Graph BTN
        //##########################
        IconButton graph_Icon_Btn = new IconButton("/images/graph/pie2.png", iconSize, iconSize, iconSize, iconSize, "centre", "right");
        // add_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));
        
        JButton graph_Btn = graph_Icon_Btn.returnJButton();
        graph_Btn.setToolTipText("Get Pie Chart Data"); //Hover message over icon
        graph_Icon_Btn.makeBTntransparent();
        
        graph_Btn.addActionListener(ae -> {
            pieChart_Action();
        });
        
        iconPanelInsert.add(graph_Icon_Btn);
        
        //##########################
        // Edit Name BTN
        //##########################
        IconButton edit_Icon_Btn = new IconButton("/images/edit/edit.png", iconSize, iconSize, iconSize, iconSize, "centre", "right");
        // add_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));
        
        JButton edit_Btn = edit_Icon_Btn.returnJButton();
        edit_Btn.setToolTipText("Edit Meal Name"); //Hover message over icon
        edit_Icon_Btn.makeBTntransparent();
        
        edit_Btn.addActionListener(ae -> {
            edit_Name_BTN_Action();
        });
        
        iconPanelInsert.add(edit_Icon_Btn);
        
        //##########################
        // Edit Time BTN
        //##########################
        IconButton editTime_Icon_Btn = new IconButton("/images/edit_Time/edit_Time.png", 45, 45, 45, 45, "centre", "right");
        // add_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));
        
        JButton editTime_Btn = editTime_Icon_Btn.returnJButton();
        editTime_Btn.setToolTipText("Edit Meal Time"); //Hover message over icon
        editTime_Icon_Btn.makeBTntransparent();
        
        editTime_Btn.addActionListener(ae -> {
            editTime_Btn_Action();
        });
        
        iconPanelInsert.add(editTime_Icon_Btn);
        
        //##########################
        //Add BTN
        //##########################
        IconButton add_Icon_Btn = new IconButton("/images/add/add.png", iconSize, iconSize, iconSize, iconSize, "centre", "right");
        // add_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));
        
        JButton add_Btn = add_Icon_Btn.returnJButton();
        add_Icon_Btn.setToolTipText("Add Sub-Meal In Meal"); //Hover message over icon
        add_Icon_Btn.makeBTntransparent();
        
        add_Btn.addActionListener(ae -> {
            addButtonAction();
        });
        
        iconPanelInsert.add(add_Icon_Btn);
        
        //##########################
        // Refresh Icon
        //##########################
        
        IconButton refresh_Icon_Btn = new IconButton("/images/refresh/+refresh.png", iconSize, iconSize, iconSize, iconSize,
                "centre", "right"); // btn text is useless here , refactor
        
        JButton refresh_Btn = refresh_Icon_Btn.returnJButton();
        refresh_Btn.setToolTipText("Restore All Sub-Meals Data"); //Hover message over icon
        refresh_Icon_Btn.makeBTntransparent();
        
        refresh_Btn.addActionListener(ae -> {
            
            //#######################################################
            // Ask For Permission
            //#######################################################
            
            if (areYouSure("Refresh Data"))
            {
                refresh_Btn_Action();
            }
        });
        
        iconPanelInsert.add(refresh_Icon_Btn);
        
        //##########################
        // Update Icon
        //##########################
        
        IconButton saveIcon_Icon_Btn = new IconButton("/images/save/save.png", iconSize, iconSize, iconSize, iconSize,
                "centre", "right"); // btn text is useless here , refactor
        //saveIcon_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));
        saveIcon_Icon_Btn.makeBTntransparent();
        
        JButton save_btn = saveIcon_Icon_Btn.returnJButton();
        save_btn.setToolTipText("Save All Sub-Meals Data"); //Hover message over icon
        
        save_btn.addActionListener(ae -> {
            if (areYouSure("Save Data"))
            {
                save_Btn_Action();
            }
        });
        
        iconPanelInsert.add(save_btn);
        
        //##########################
        // Delete Icon
        //##########################
        
        IconButton deleteIcon_Icon_Btn = new IconButton("/images/delete/+delete.png", iconSize, iconSize, iconSize + 10, iconSize,
                "centre", "right"); // btn text is useless here , refactor
        //deleteIcon_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));
        deleteIcon_Icon_Btn.makeBTntransparent();
        
        JButton delete_btn = deleteIcon_Icon_Btn.returnJButton();
        delete_btn.setToolTipText("Delete Meal"); //Hover message over icon
        
        delete_btn.addActionListener(ae -> {
            
            if (areYouSure("Delete"))
            {
                deleteMealManagerAction();
            }
        });
        
        iconPanelInsert.add(delete_btn);
    }
    
    // GUI Setup
    private void setup_GUI(ArrayList<Object> total_Meal_Data)
    {
        //################################################################
        // Variables
        //################################################################
        gbc = new GridBagConstraints();
        container = meal_plan_screen.getScrollJPanelCenter();
        sharedDataRegistry = meal_plan_screen.get_MealManagerRegistry();
        macrosLeft_JTable = meal_plan_screen.get_MacrosLeft_JTable();
        
        //############################
        // Lists & Arraylists & Maps
        //############################
        
        // Total Meal
        mealTotalTable_ColumnNames = meal_plan_screen.getMeal_total_columnNames();
        totalMeal_Table_ColToHide = meal_plan_screen.getTotalMeal_Table_ColToHide();
        totalMeal_Other_Cols_Pos = meal_plan_screen.get_TotalMeal_Other_Cols_Pos();
        
        // Ingredients Table
        map_ingredientTypesToNames = meal_plan_screen.getMap_ingredientTypesToNames();
        ingredientsTableUnEditableCells = meal_plan_screen.getIngredientsTableUnEditableCells();
        ingredients_Table_Col_Avoid_Centering = meal_plan_screen.getIngredients_Table_Col_Avoid_Centering();
        ingredientsInMeal_Table_ColToHide = meal_plan_screen.getIngredientsInMeal_Table_ColToHide();
        ingredientsTable_ColumnNames = meal_plan_screen.getIngredients_ColumnNames();
        
        //################################################################
        // Create Collapsible Object
        //################################################################
        //collapsibleJpObj = new CollapsibleJPanel(container, removeSecondsOnTimeString(savedMealTime), 150, 50); // time as btn txt
        collapsibleJpObj = new CollapsibleJPanel(container, savedMealName, 180, 50); // time as btn txt
        collapsibleCenterJPanel = collapsibleJpObj.get_Centre_JPanel();
        collapsibleCenterJPanel.setBackground(Color.YELLOW);
        
        //################################################################
        // Icon Setup in Collapsible Object
        //################################################################
        iconSetup();
        
        //################################################################
        // Create TotalMeal Objects
        //################################################################
        totalMealTable = new TotalMeal_Table(db, this, meal_In_Plan_ID, total_Meal_Data);
        
        //######################################
        // TotalMeal_Table to Collapsible Object
        //######################################
        JPanel southPanel = collapsibleJpObj.get_South_JPanel();
        
        // adds space between Ingredients_In_Meal_Calculation table and total_in_meal table
        add_To_Container(southPanel, new JPanel(), 0, 1, 1, 1, 0.25, 0.25, "both", 50, 0, null);
        
        // Adding total table to CollapsibleOBJ
        add_To_Container(southPanel, totalMealTable, 0, 2, 1, 1, 0.25, 0.25, "both", 0, 0, null);
        
        //################################################################
        // Add Initial Space Between For the First Divided Meal
        //################################################################
        add_To_Container(collapsibleCenterJPanel, new JPanel(), 0, yPoInternally++, 1, 1, 0.25, 0.25, "both", 10, 0, null);
        
        //################################################################
        // Set Object Created
        //################################################################
        isObjectCreated = true;
    }
    
    //#################################
    // Meal Setup Methods
    //#################################
    private void add_Multiple_Sub_Meals(LinkedHashMap<Integer, ArrayList<ArrayList<Object>>> sub_Meal_DATA)
    {
        for (Map.Entry<Integer, ArrayList<ArrayList<Object>>> div_data : sub_Meal_DATA.entrySet())
        {
            add_Sub_Meal(true, div_data.getKey(), div_data.getValue());
        }
    }
    
    private void add_Sub_Meal(boolean is_Sub_Meal_In_DB, int div_id, ArrayList<ArrayList<Object>> sub_Meal_Data)
    {
        //##############################################
        // Create Ingredient Table Object
        //##############################################
        JPanel spaceDivider = new JPanel();
        
        IngredientsTable ingredients_Table =
                new IngredientsTable(db, this, div_id, sub_Meal_Data, is_Sub_Meal_In_DB, spaceDivider);
        
        //################################################
        // Add Ingredients Table To GUI
        //################################################
        add_To_Container(collapsibleCenterJPanel, ingredients_Table, 0, yPoInternally++, 1, 1, 0.25, 0.25, "both", 0, 0, null);
        add_To_Container(collapsibleCenterJPanel, spaceDivider, 0, yPoInternally++, 1, 1, 0.25, 0.25, "both", 50, 0, null);
        
        //################################################
        // Add Ingredient Table To Collection
        //################################################
        ingredientTables_In_MealManager.add(ingredients_Table);
    }
    
    //##################################################################################################################
    // Actions
    //##################################################################################################################
    private Boolean areYouSure(String process)
    {
        int reply = JOptionPane.showConfirmDialog(getFrame(), String.format("You are requesting to %s ! \n\nAre you sure you want to %s?", process, process),
                "Notification", JOptionPane.YES_NO_OPTION); //HELLO Edit
        
        if (reply == JOptionPane.NO_OPTION || reply == JOptionPane.CLOSED_OPTION)
        {
            return false;
        }
        return true;
    }
    
    //#################################################################################
    // Pie Charts
    //#################################################################################
    private void pieChart_Action()
    {
        // If pieChart is already created bring up to the surface and make it visible
        if (is_PieChartOpen())
        {
            System.out.println("\n\nB : pieChart_Action() 1 !!!!");
            
            pie_chart_meal_manager_screen.makeJFrameVisible();
            return;
        }
        
        pie_chart_meal_manager_screen = new Pie_Chart_Meal_Manager_Screen(db, this);
    }
    
    private void pieChart_UpdateMealName()
    {
        //#########################################################################################################
        // Change Internal Graph Title if exists
        //#########################################################################################################
        if (is_PieChartOpen())
        {
            pie_chart_meal_manager_screen.update_PieChart_Title();
        }
    }
    
    /**
     * 1.) Remove Pie Chart Object
     * <p>
     * 2.) IF Meal_Plan_Screen PieChart Screen is NULL, Pie Data can be removed
     * as it's not in use and not on display somewhere else     *
     */
    public void removePieChartScreen()
    {
        pie_chart_meal_manager_screen = null;
        
        //############################################
        // External DATA if not USED
        //############################################
        if (meal_plan_screen.is_PieChart_Screen_Open()) { return; }
        
        sharedDataRegistry.remove_PieChart_DatasetValues(meal_In_Plan_ID);
    }
    
    // External Call Usage
    public void close_PieChartScreen()
    {
        if (! is_PieChartOpen()) { return; }
        
        pie_chart_meal_manager_screen.window_Closed_Event();
    }
    
    private void update_Pie_Chart_Screen()
    {
        /**
         * Update data behind pieCharts which will effectively update all pieCharts actively using this data
         */
        
        if (! sharedDataRegistry.update_PieChart_Values(this))
        {
            System.err.printf("\n\nShared_Data_Registry.java : updatePieChart_MM_Values() \nPieChart not Open %s", meal_In_Plan_ID);
        }
    }
    
    public Boolean is_PieChartOpen()
    {
        return pie_chart_meal_manager_screen != null;
    }
    
    //#################################################################################
    // Meal Name & Meal Time Functions
    //#################################################################################
    private Object inputValidation(String variableName, String input, boolean comparison, boolean skipConfirmation)
    {
        // Remove whitespace at the end of variable
        input = input.trim();
        
        //##############################################################################################################
        // Validation Checks
        //##############################################################################################################
        // Prior to this method being called the users input is checked if its null or "" and rejected
        LocalTime inputConvertedToLocalTime = null;
        Second inputConvertedToSeconds = null;
        
        if (variableName.equals("time"))
        {
            input = removeSecondsOnTimeString(input);
            
            try
            {
                inputConvertedToLocalTime = LocalTime.parse(input);
                if (inputConvertedToLocalTime == null) { throw new Exception("\n\nError, time variable null"); }
            }
            catch (Exception e)
            {
                System.err.printf("\n\nMealManager.java: inputValidation() | Error, converting input to time string! \n%s", e);
                JOptionPane.showMessageDialog(getFrame(), "Error, converting input to time string !!");
                return null;
            }
        }
        else if (containsSymbols(input)) // Name: check if any symbols are inside
        {
            JOptionPane.showMessageDialog(getFrame(), String.format("\n\nError, '%s' cannot contain symbols!", variableName));
            return null;
        }
        
        // ####################################################
        // Compare with saved correlating values
        // ####################################################
        if (comparison)
        {
            // User enters same meal name
            if (variableName.equals("name") && input.equals(get_Current_Meal_Name()))
            {
                JOptionPane.showMessageDialog(getFrame(), String.format("This meal '%s' already has the value '%s' !!", variableName, get_Current_Meal_Name()));
                return null;
            }
            else if (variableName.equals("time"))
            {
                inputConvertedToSeconds = convertMysqlTimeToSecond(input);
                
                if (getCurrentMealTime().equals(inputConvertedToSeconds)) // Time : User enters same meal time
                {
                    JOptionPane.showMessageDialog(getFrame(), String.format("This meal '%s' already has the value '%s' !!", variableName, get_Current_Meal_Time_GUI()));
                    return null;
                }
            }
        }
        
        // #############################################################################################################
        // Check Database if Value Already Exists
        // #############################################################################################################
        String
                query = "",
                errorMSG = "";
        
        Object[] params;
        
        if (variableName.equals("time")) // Validate time String
        {
            query = String.format("""
                    SELECT IFNULL((
                        SELECT meal_time
                        FROM meals_in_plan
                        WHERE plan_id = ? AND meal_time = ?
                        LIMIT 1
                    ), 'N/A') AS meal_time;""");
            
            errorMSG = "Error, Validating Meal Time!";
            
            params = new Object[]{ tempPlanID, inputConvertedToLocalTime };
        }
        else // Last possible option based on logic is Meal Name
        {
            input = StringUtils.capitalize(input);
            
            query = """
                    SELECT IFNULL((
                        SELECT meal_name
                        FROM meals_in_plan
                        WHERE plan_id = ? AND meal_name = ?
                        LIMIT 1
                    ), 'N/A') AS meal_name;""";
            
            errorMSG = "Error, Validating Meal Name!";
            
            params = new Object[]{ tempPlanID, input };
        }
        
        // #########################################
        // Execute Query
        // #########################################
        ArrayList<Object> results = db.get_Single_Col_Query_Obj(query, params, errorMSG);
        
        if (results == null) { return null; } // Error occurred during script
        
        if (! results.getFirst().equals("N/A")) // Means value already exists, returns N/A if the value doesn't
        {
            JOptionPane.showMessageDialog(getFrame(), String.format("A meal in this plan already has a meal %s of '%s' !!", variableName, input));
            return null;
        }
        
        //##############################################################################################################
        // User Confirmation
        //##############################################################################################################
        if (! skipConfirmation) // If requested not to skip a confirmation msg prompt confirmation
        {
            String currentVariableValue = variableName.equals("name") ? get_Current_Meal_Name() : get_Current_Meal_Time_GUI().toString();
            if (! areYouSure(String.format("change meal %s from '%s' to '%s'", variableName, currentVariableValue, input)))
            {
                return null;
            }
        }
        
        //##############################################################################################################
        // Return Value
        //##############################################################################################################
        if (variableName.equals("name")) { return input; }
        else { return inputConvertedToLocalTime; }
    }
    
    private boolean containsSymbols(String stringToCheck)
    {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9 '\\-&]+$");
        Matcher matcher = pattern.matcher(stringToCheck);
        
        return ! matcher.matches();
    }
    
    //####################################
    // Meal Time Functions
    //####################################
    private void editTime_Btn_Action()
    {
        //#########################################################################################################
        // Prompt User for time Input
        //#########################################################################################################
        
        LocalTime newMealTime = promptUserForMealTime(false, true);
        
        if (newMealTime == null) { return; } // Error occurred in validation checks above
        
        Second newMealSecond = localTimeToSecond(newMealTime);
        Second oldMealSeconds = getCurrentMealTime();
        
        //#########################################################################################################
        // Update
        //#########################################################################################################
        String uploadQuery = """
                UPDATE meals_in_plan
                SET meal_time = ?
                WHERE plan_id = ? AND meal_in_plan_id = ?;""";
        
        Object[] params = new Object[]{ newMealTime, tempPlanID, meal_In_Plan_ID };
        
        //##########################################
        // Upload Into Database Table
        //##########################################
        if (! db.upload_Data2(uploadQuery, params, "Error, unable to change Meal Time!")) { return; }
        
        //#########################################################################################################
        // Update GUI & Variables
        //#########################################################################################################
        
        JOptionPane.showMessageDialog(getFrame(), String.format("Successfully, changed meal time from '%s' to '%s'",
                get_Current_Meal_Time_GUI(), newMealTime)); // Success MSG
        
        //#######################################
        // Update total Meal View Time Col
        //#######################################
        totalMealTable.set_Value_On_Table(newMealTime, 0, totalMeal_Other_Cols_Pos.get("meal_time"));
        
        //#######################################
        // Update Time Variables
        //#######################################
        setTimeVariables(true, savedMealTime, newMealSecond); // Set Meal Time Variables
        
        //#######################################
        // Update Internal PieChart Name
        //#######################################
        pieChart_UpdateMealName();
        
        //#######################################
        //  Update GUI
        //#######################################
        meal_plan_screen.add_And_Replace_MealManger_POS_GUI(this, true, true);
        
        //#######################################
        // Update External Charts
        //#######################################
        meal_plan_screen.update_External_Charts(false, "mealTime", this, oldMealSeconds, newMealSecond);
    }
    
    private LocalTime promptUserForMealTime(boolean skipConfirmation, boolean comparison)
    {
        // User info prompt
        String inputMealTime = JOptionPane.showInputDialog("Input Meal Time etc \"09:00\"?");
        
        if (inputMealTime == null || inputMealTime.equals("")) { return null; }
        
        return (LocalTime) inputValidation("time", inputMealTime, comparison, skipConfirmation);
    }
    
    private Second convertMysqlTimeToSecond(String timeString)
    {
        return localTimeToSecond(LocalTime.parse(removeSecondsOnTimeString(timeString)));
    }
    
    private Second localTimeToSecond(LocalTime localTime)
    {
        // Convert LocalTime -> Date (fixed base date)
        Date date = Date.from(localTime.atDate(LocalDate.of(1970, 1, 1))
                .atZone(ZoneId.systemDefault())
                .toInstant());
        
        // Wrap into JFree Second
        return new Second(date);
    }
    
    private String removeSecondsOnTimeString(String mealTime)
    {
        StringBuilder returnString = new StringBuilder();
        int colonCount = 0;
        
        for (char c : mealTime.toCharArray())
        {
            if (c == ':') { colonCount++; if (colonCount == 2) { return returnString.toString(); } }
            returnString.append(c);
        }
        
        return mealTime;
    }
    
    
    private void setTimeVariables(boolean hasMealTimeBeenChanged, Second savedMealTime, Second currentMealTime)
    {
        this.hasMealTimeBeenChanged = hasMealTimeBeenChanged;
        this.savedMealTime = savedMealTime;
        this.currentMealTime = currentMealTime;
    }
    
    // Accessor Methods
    public Second getCurrentMealTime()
    {
        return currentMealTime;
    }
    
    public LocalTime get_Current_Meal_Time_GUI()
    {
        return currentMealTime.getStart()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalTime();
    }
    
    public LocalTime get_Saved_MealTime_GUI()
    {
        return savedMealTime.getStart()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalTime();
    }
    
    //####################################
    // Meal Name Functions
    //####################################
    private void edit_Name_BTN_Action()
    {
        //#########################################################################################################
        // Validation Checks
        //#########################################################################################################
        // Get User Input
        String inputMealName = promptUserForMealName(false, true);
        
        if (inputMealName == null) { return; } // Error occurred in validation checks above
        
        //#########################################################################################################
        // Update DB
        //#########################################################################################################
        String uploadQuery = """
                UPDATE meals_in_plan
                SET meal_name = ?
                WHERE plan_id = ? AND meal_in_plan_id = ?;""";
        
        Object[] params = new Object[]{ inputMealName, tempPlanID, meal_In_Plan_ID };
        
        if (! db.upload_Data2(uploadQuery, params, "Error, unable to change Meal Name!")) { return; }
        
        //#########################################################################################################
        // Change Variable DATA & Object
        //#########################################################################################################
        collapsibleJpObj.set_Icon_Btn_Text(inputMealName);
        setMealNameVariables(true, savedMealName, inputMealName);  // Set Meal Name Variables
        
        //#########################################################################################################
        // Update total Meal View Time Col
        //#########################################################################################################
        totalMealTable.set_Value_On_Table(inputMealName, 0, totalMeal_Other_Cols_Pos.get("meal_name"));
        
        //#########################################################################################################
        // Internal / External Graphs
        //#########################################################################################################
        pieChart_UpdateMealName(); // Change Internal Graph Title if exists
        
        // Update External
        meal_plan_screen.update_External_Charts(false, "mealName", this, null, null);
        
        //#########################################################################################################
        // Change Button Text & Related Variables
        //#########################################################################################################
        // Success MSG
        JOptionPane.showMessageDialog(getFrame(), String.format("Successfully, changed meal name from ' %s ' to ' %s ' ", currentMealName, inputMealName));
    }
    
    private String promptUserForMealName(boolean skipConfirmation, boolean comparison)
    {
        // Get User Input For Meal Name
        String newMealName = JOptionPane.showInputDialog(getFrame(), "Input Meal Name?");
        
        // User Cancelled or entered nothing
        if (newMealName == null || newMealName.equals("")) { return null; }
        
        // validate user input
        return (String) inputValidation("name", newMealName, comparison, skipConfirmation);
    }
    
    private void setMealNameVariables(boolean hasMealNameBeenChanged, String savedMealName, String currentMealName)
    {
        this.hasMealNameBeenChanged = hasMealNameBeenChanged;
        this.savedMealName = savedMealName;
        this.currentMealName = currentMealName;
    }
    
    public String get_Current_Meal_Name()
    {
        return currentMealName;
    }
    
    //#################################################################################
    // Add BTN
    //#################################################################################
    // HELLO, Needs to scroll down to the bottom of the MealManager
    private void addButtonAction()
    {
        //###########################################################
        // Upload & Fetch Variables
        //############################################################
        String errorMSG = "Error, unable to add SubMeal to Meal!";
        
        LinkedHashSet<Pair<String, Object[]>> upload_Queries_And_Params = new LinkedHashSet<>();
        LinkedHashSet<Pair<String, Object[]>> fetch_Queries_And_Params = new LinkedHashSet<>();
        
        int sub_Meal_ID;
        ArrayList<ArrayList<Object>> sub_Meal_DATA;
        
        //################################################
        // Uploads
        //###############################################
        
        // 1.) Create Sub-Meal
        String upload_Q1 = """
                INSERT INTO divided_meal_sections (meal_in_plan_id, plan_id) VALUES
                (?, ?);""";
        upload_Queries_And_Params.add(new Pair<>(upload_Q1, new Object[]{ meal_In_Plan_ID, tempPlanID }));
        
        //######################################
        // 2.) Set Sub-Meal ID
        //######################################
        String var_Sub_Meal_ID = "@subMealID";
        String upload_Q2 = String.format("Set %s = LAST_INSERT_ID();", var_Sub_Meal_ID);
        
        upload_Queries_And_Params.add(new Pair<>(upload_Q2, null));
        
        //########################################
        // 3.) Insert None of the Above into Sub-Meal
        //########################################
        
        String upload_Q3 = String.format("""
                INSERT INTO ingredients_in_sections_of_meal
                (plan_id, pdid, div_meal_sections_id, ingredient_id, quantity) VALUES
                (?, ?, %s, ?, ?);""", var_Sub_Meal_ID);
        
        upload_Queries_And_Params.add(new Pair<>(upload_Q3,
                new Object[]{ tempPlanID, new Null_MYSQL_Field(Types.INTEGER), 1, 0 }));
        
        //#######################################################
        // Fetch Queries
        //#######################################################
 
        // 1.) Get Sub-Meal ID
        String get_Q1 = String.format("SELECT %s;", var_Sub_Meal_ID);
        fetch_Queries_And_Params.add(new Pair<>(get_Q1, null));
        
        //#############################
        // 2.) Get Ingredients Table DATA
        //#############################
        String get_Q2 = String.format("""
                SELECT *
                FROM ingredients_in_sections_of_meal_calculation
                WHERE plan_id = ? AND div_meal_sections_id = %s;""", var_Sub_Meal_ID);
        fetch_Queries_And_Params.add(new Pair<>(get_Q2, new Object[]{ tempPlanID }));
        
        //#######################################################
        // Execute Query
        //#######################################################
        Query_Results results_OBJ = db.upload_And_Get_Batch(upload_Queries_And_Params, fetch_Queries_And_Params, errorMSG);
        
        if (results_OBJ == null || results_OBJ.is_Empty()) { System.err.println("\n\n\nFailed Creating Meal"); return; }
        
        //#######################################################
        // Set Variables from Results
        //#######################################################
        try
        {
            sub_Meal_ID = ((Number) results_OBJ.get_Result_Object(0)).intValue();
            sub_Meal_DATA = results_OBJ.get_Fetched_Result_2D_AL(1);
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s", e);
            return;
        }
        
        //#######################################################
        // Add Meal To GUI
        //#######################################################
        add_Sub_Meal(false, sub_Meal_ID, sub_Meal_DATA); // Add Sub-Meal to GUI
        
        //#######################################################
        // Success MSG & Expand Meal View
        //#######################################################
        JOptionPane.showMessageDialog(null, String.format("Successfully Created Sub-Meal in %s at [%s]",
                currentMealName, get_Current_Meal_Time_GUI() )); // Show Success MSG
       
        expand_JPanel(); // Expand Meal View
       
        meal_plan_screen.scrollToJPanelOnScreen(get_Collapsible_JP_Obj()); // Scroll GUI to MealManager
    }
    
    //#################################################################################
    // Delete BTN Methods
    //#################################################################################
    private void deleteMealManagerAction()
    {
        //##########################################
        // Delete MealManager Queries
        //##########################################
        String query = "DELETE FROM meals_in_plan WHERE meal_in_plan_id = ? AND plan_id = ?";
        Object[] params = new Object[]{ meal_In_Plan_ID, tempPlanID };
        
        //##########################################
        // Execute Update
        //##########################################
        if (! db.upload_Data2(query, params, "Table Un-Successfully Deleted!")) { return; }
        
        //##########################################
        // Delete MealManager Actions
        //##########################################
        delete_MealManager();
        
        //##########################################
        // Update MacrosLeft_Table
        //##########################################
        update_MacrosLeft_Table();// update macrosLeft table, due to number deductions from this meal
        
        //##########################################
        // Delete in External Charts
        //##########################################
        meal_plan_screen.update_External_Charts(false, "delete", this, getCurrentMealTime(), getCurrentMealTime());
        
        //##########################################
        // Show MSG
        //##########################################
        JOptionPane.showMessageDialog(null, "Table Successfully Deleted!");
    }
    
    public void delete_MealManager()
    {
        //##########################################
        // Update Registry Data
        //##########################################
        sharedDataRegistry.delete_MealManager(this);
        
        //##########################################
        // Hide JTable object & Collapsible OBJ
        //##########################################
        hideMealManager();
    }
    
    private void hideMealManager()
    {
        //##########################################
        // Set Variables
        //##########################################
        setVisibility(false); // hide collapsible Object
        set_Has_Meal_Been_Deleted(true); // set this object as deleted
        
        //##########################################
        // Remove From GUI
        //##########################################
        container.remove(collapsibleJpObj); // remove the GUI elements from GUI
        container.remove(spaceDividerForMealManager);    // remove space divider from GUI
        
        //##########################################
        // Delete PieChart (Meal is Gone)
        //##########################################
        close_PieChartScreen();
    }
    
    private void unHideMealManager()
    {
        setVisibility(true); // hide collapsible Object
        set_Has_Meal_Been_Deleted(false); // set this object as deleted
    }
    
    private void setVisibility(boolean condition)
    {
        collapsibleJpObj.setVisible(condition);
        spaceDividerForMealManager.setVisible(condition);
    }
    
    private void set_Has_Meal_Been_Deleted(boolean x)
    {
        hasMealBeenDeleted = x;
    }
    
    public boolean is_Meal_Deleted()
    {
        return hasMealBeenDeleted;
    }
    
    //################################################
    // Delete IngredientsTable Methods
    //################################################
    public void ingredientsTableHasBeenDeleted()
    {
        //##########################################
        // If there are no meals, delete table
        //##########################################
        if (! (areAllTableBeenDeleted()))
        {
            return;
        }
        
        //##########################################
        // Delete Meal From DB
        //##########################################
        String query1 = "DELETE FROM meals_in_plan WHERE meal_in_plan_id = ? AND plan_id = ?;";
        
        Object[] params = new Object[]{ meal_In_Plan_ID, tempPlanID };
        
        if (! (db.upload_Data2(query1, params, "Error, Unable to DELETE IngredientsTable!!"))) { return; }
        
        delete_MealManager();
    }
    
    public boolean areAllTableBeenDeleted()
    {
        //##########################################
        // IF there are no meals, delete table
        //##########################################
        for (IngredientsTable ingredientsTable : ingredientTables_In_MealManager)
        {
            if (! (ingredientsTable.is_Table_Deleted())) // if a meal hasn't been deleted, exit method
            {
                return false;
            }
        }
        
        //##########################################
        return true;
    }
    
    public void removeIngredientsTable(IngredientsTable ingredientsTable)
    {
        ingredientTables_In_MealManager.remove(ingredientsTable);
    }
    
    //#################################################################################
    // Save & Refresh Methods
    //#################################################################################
    private boolean transferMealDataToPlan(String process, int fromPlanID, int toPlanID)
    {
        System.out.printf("\n\n%s transferMealDataToPlan()  %s %s %s", lineSeparator, process, fromPlanID, toPlanID);
        //########################################################
        // Drop Temp Tables
        //########################################################
        
        // Delete tables if they already exist
        String query0 = "DROP TABLE IF EXISTS temp_ingredients_in_meal;";
        
        String query1 = "DROP TABLE IF EXISTS temp_divided_meal_sections;";
        
        //########################################################
        // Clear Old Data from toPlan and & Temp Tables
        //########################################################
        // Delete sub-meals from this meal in toPlan
        String query2 = "DELETE FROM divided_meal_sections WHERE meal_in_plan_id = ? AND plan_id = ? ;";
        
        //####################################################
        // Transferring this plans Ingredients to Temp-Plan
        //####################################################
        
        // Create Table to transfer ingredients from original plan to temp
        String query3 = """
                CREATE TABLE temp_divided_meal_sections  AS
                SELECT i.*
                FROM divided_meal_sections i
                WHERE i.meal_in_plan_id = ? AND i.plan_id = ?;""";
        
        String query4 = "UPDATE temp_divided_meal_sections SET plan_id = ?;";
        
        String query5 = "INSERT INTO divided_meal_sections SELECT * FROM temp_divided_meal_sections;";
        //####################################################
        // Transferring ingredients from this meal in toPlan
        //####################################################
        
        // Create Table to transfer ingredients from original plan to temp
        String query6 = """
                CREATE table temp_ingredients_in_meal  AS
                SELECT i.*
                FROM ingredients_in_sections_of_meal i
                WHERE div_meal_sections_id IN (SELECT div_meal_sections_id FROM divided_meal_sections WHERE meal_in_plan_id = ? AND plan_id = ?)
                AND plan_id = ?;""";
        
        String query7 = "UPDATE temp_ingredients_in_meal SET plan_id = ?;";
        
        String query8 = "INSERT INTO ingredients_in_sections_of_meal SELECT * FROM temp_ingredients_in_meal;";
        
        String query9 = "DROP TABLE IF EXISTS temp_ingredients_in_meal;";
        
        String query10 = "DROP TABLE IF EXISTS temp_divided_meal_sections;";
        
        //#####################################################
        // Meal Name & Time Updates If Changed
        //#####################################################
        String updateMealName = process.equals("refresh") ? savedMealName : currentMealName; // set mealName to refresh
        LocalTime updateMealTime = process.equals("refresh") ? get_Saved_MealTime_GUI() : get_Current_Meal_Time_GUI(); // set mealTime to time
        
        //#####################################################
        // Create Query Formatted Data for Method
        //#####################################################
        String errorMSG = "Error, Unable to Transfer Plan Data!";
        
        LinkedHashSet<Pair<String, Object[]>> queries_And_Params = new LinkedHashSet<>()
        {{
            
            add(new Pair<>(query0, null));
            add(new Pair<>(query1, null));
            
            if (hasMealNameBeenChanged || hasMealTimeBeenChanged)
            {
                String uploadQuery = """
                        UPDATE meals_in_plan
                        SET meal_name = ?, meal_time = ?
                        WHERE plan_id = ? AND meal_in_plan_id = ?;""";
                
                add(new Pair<>(uploadQuery, new Object[]{ updateMealName, updateMealTime, toPlanID, meal_In_Plan_ID }));
            }
            
            add(new Pair<>(query2, new Object[]{ meal_In_Plan_ID, toPlanID })); // Already in correct dataType
            add(new Pair<>(query3, new Object[]{ meal_In_Plan_ID, fromPlanID })); //
            add(new Pair<>(query4, new Object[]{ toPlanID }));
            add(new Pair<>(query5, null));
            add(new Pair<>(query6, new Object[]{ meal_In_Plan_ID, fromPlanID, fromPlanID }));
            add(new Pair<>(query7, new Object[]{ toPlanID }));
            add(new Pair<>(query8, null));
            add(new Pair<>(query9, null));
            add(new Pair<>(query10, null));
        }};
        
        //####################################################
        // Return Update /Output
        //####################################################
        return db.upload_Data_Batch2(queries_And_Params, errorMSG);
    }
    
    //######################################
    // Refresh BTN Methods
    //######################################
    private void refresh_Btn_Action()
    {
        //#############################################################################################
        // Check IF OLD Table Name & Meal Time Are Available To Assign Back To This Meal
        //##############################################################################################
        String errorMSG = "Error, Unable to Refresh Meal!";
        String query = """
                SELECT IFNULL(M.pos, "N/A") AS pos
                FROM
                (
                  -- This being the anchor had to restricted to make sure there's always a true value
                  -- to attach to for IFNULL to work
                
                  SELECT plan_id FROM plans WHERE plan_id = ?
                
                ) AS P
                LEFT JOIN
                (
                	-- Has to be restricted on plan_id then count as
                
                	SELECT
                	ROW_NUMBER() OVER (ORDER BY meal_time ASC) AS pos,
                	meal_in_plan_id, plan_id, meal_name, meal_time
                	FROM meals_in_plan
                	WHERE plan_id = ?
                
                ) AS M
                
                ON P.plan_id = M.plan_id
                AND (M.meal_name = ? OR M.meal_time = ?)
                AND M.meal_in_plan_id != ?;""";
        
        Object[] params_refresh = new Object[]{ tempPlanID, tempPlanID, savedMealName, get_Saved_MealTime_GUI(), meal_In_Plan_ID };
        
        ArrayList<ArrayList<Object>> results = db.get_2D_Query_AL_Object(query, params_refresh, errorMSG);
        if (results == null)
        {
            JOptionPane.showMessageDialog(getFrame(), errorMSG);
            return;
        }
        
        //###########################################
        // IF TRUE Notify Meal Name/Time Taken
        //###########################################
        ArrayList<Object> positions = new ArrayList<>();
        
        if (! results.isEmpty() && ! (results.get(0).get(0).toString()).equals("N/A"))
        {
            positions.add(results.get(0).get(0));
        }
        
        if (results.size() >= 2 && ! (results.get(1).get(0).toString()).equals("N/A"))
        {
            positions.add(results.get(1).get(0));
        }
        
        if (! positions.isEmpty())
        {
            JOptionPane.showMessageDialog(getFrame(), String.format("""
                            \n\nA meal in this plan already has this meals saved info:
                            
                            'Meal Name' of : '%s' or 'Meal Time' of : '%s'
                            
                            at positions : %s  which is stopping this meal from refreshing !
                            
                            Change those values first at positions : %s in this plan
                            to be able to refresh this meal!
                            
                            Or, refresh the whole plan if the other meals won't be affected !""", savedMealName,
                    savedMealTime, positions, positions));
            
            return;
        }
        
        //##############################################################################################
        // Reset DB Data
        //##############################################################################################
        if (! (transferMealDataToPlan("refresh", planID, tempPlanID)))
        {
            JOptionPane.showMessageDialog(null, "\n\nUnable to transfer mealData toS!!");
            return;
        }
        
        //##############################################################################################
        // RELOAD IngredientsTable & TotalMeal Table & Update  Chart DATA
        //##############################################################################################
        reloadTableAndChartsData(true, true);
        
        //##############################################################################################
        // Remove & Re-add to GUI
        ///##############################################################################################
        meal_plan_screen.add_And_Replace_MealManger_POS_GUI(this, true, true);
    }
    
    public void reloadTableAndChartsData(boolean updateMacrosLeft, boolean updateExternalCharts)
    {
        //#############################################################################################
        // Reset GUI  & Variables
        //##############################################################################################
        // Reset Time & MealName Variables
        setTimeVariables(false, savedMealTime, savedMealTime);
        setMealNameVariables(false, savedMealName, savedMealName);
        
        collapsibleJpObj.set_Icon_Btn_Text(savedMealName); // Reset Meal Name in GUI to Old Txt
        
        //##############################################################################################
        // Refresh IngredientTables
        //##############################################################################################
        Iterator<IngredientsTable> it = ingredientTables_In_MealManager.iterator();
        while (it.hasNext())
        {
            IngredientsTable ingredientsTable = it.next();
            
            // If ingredientsTable is in DB  then refresh
            if (ingredientsTable.get_Meal_In_DB())
            {
                ingredientsTable.refresh_Data(false, false);
                continue;
            }
            
            // Because mealManager has been deleted remove it
            ingredientsTable.completely_Delete_Ingredients_Table(); // delete table from db
            
            // Because mealManager has been deleted remove it
            it.remove(); // remove from list
        }
        
        //##############################################################################################
        // Refresh TotalMeal_Table DATA & Charts
        //##############################################################################################
        update_MealManager_DATA(true, updateExternalCharts);
        
        pieChart_UpdateMealName(); // Update Internal PieChart Name
        
        //##############################################################################################
        // Refresh MacrosLeft
        //##############################################################################################
        if (updateMacrosLeft) { update_MacrosLeft_Table(); }// this is optional
        
        //##############################################################################################
        // Make This MealManager Visible
        //##############################################################################################
        unHideMealManager();
    }
    
    //######################################
    // Save BTN Methods
    //######################################
    public void save_Btn_Action()
    {
        // ###############################################################################
        // Transferring Meals & Ingredients from FromPlan to toPlan
        // ###############################################################################
        if (! (transferMealDataToPlan("saving", tempPlanID, planID))) // transfer meals and ingredients from temp plan to original plan
        {
            System.out.println("\n\n#################################### \nError MealManager saveMealData()");
            
            JOptionPane.showMessageDialog(getFrame(), "\n\n1.)  Error \nUnable to save this Meal and its sub-meals");
            return;
        }
        
        saveData(true);
    }
    
    public void saveData(boolean showUpdateMessage)
    {
        // ###############################################################################
        // Set Variables
        // ###############################################################################
        setTimeVariables(false, currentMealTime, currentMealTime);
        setMealNameVariables(false, currentMealName, currentMealName);
        setMealManagerInDB(true);
        
        // ###############################################################################
        // Removing Sub-Meals that have been deleted & Saving The Other Tables
        // ##############################################################################
        Iterator<IngredientsTable> it = ingredientTables_In_MealManager.iterator();
        while (it.hasNext())
        {
            IngredientsTable table = it.next();
            
            // #####################################
            // Remove Deleted Ingredients Table
            // #####################################
            if (table.is_Table_Deleted())   // If objected is deleted, completely delete it then skip to next JTable
            {
                table.completely_Delete_Ingredients_Table();
                it.remove();
                continue;
            }
            
            // #####################################
            // Saved Data & Reset Variables
            // #####################################
            table.set_Meal_In_DB(true);
            table.save_Data();
        }
        
        // ##############################################################################
        // Successful Message
        // ##############################################################################
        if (showUpdateMessage)
        {
            JOptionPane.showMessageDialog(getFrame(), "\n\nAll SubMeals Within Meal Have Successfully Saved!");
        }
    }
    
    private void setMealManagerInDB(boolean mealManagerInDB)
    {
        this.mealManagerInDB = mealManagerInDB;
    }
    
    public boolean isMealManagerInDB()
    {
        return mealManagerInDB;
    }
    
    //######################################
    // Others
    //######################################
    public void expand_JPanel()
    {
        get_Collapsible_JP_Obj().expand_JPanel();
    }
    
    public void collapse_MealManager()
    {
        get_Collapsible_JP_Obj().collapse_JPanel();
    }
    
    //##################################################################################################################
    // Updating Other Tables
    //##################################################################################################################
    public void update_MealManager_DATA(Boolean updateInternalCharts, Boolean updateExternalCharts)
    {
        // Update TotalMealView (Has to be first)
        update_TotalMeal_Table();
        
        // Update Registry Data (Second)
        sharedDataRegistry.add_OR_Replace_MealManager_Macros_DATA(this);
        
        // Update Charts
        updateCharts(updateInternalCharts, updateExternalCharts);
    }
    
    private void update_TotalMeal_Table()
    {
        totalMealTable.update_Table();
    }
    
    public void update_MacrosLeft_Table()
    {
        macrosLeft_JTable.update_Table();
    }
    
    private void updateCharts(Boolean updateInternalCharts, Boolean updateExternalCharts)
    {
        if (updateInternalCharts)
        {
            update_Pie_Chart_Screen(); // Update Pie Chart Screen
        }
        
        if (updateExternalCharts) // Update External Charts
        {
            meal_plan_screen.update_External_Charts(false, "update", this, getCurrentMealTime(), getCurrentMealTime());
        }
    }
    
    //##################################################################################################################
    // Accessor Methods
    //##################################################################################################################
    
    // Other Objects
    public Frame getFrame()
    {
        return meal_plan_screen.getFrame();
    }
    
    public JPanel getSpaceDividerForMealManager()
    {
        return spaceDividerForMealManager;
    }
    
    public Shared_Data_Registry getMealManagerRegistry()
    {
        return sharedDataRegistry;
    }
    
    // ############################################
    // Booleans
    // ############################################
    public boolean isObjectCreated()
    {
        return isObjectCreated;
    }
    
    // ###########################
    // JPanel
    // ###########################
    public CollapsibleJPanel get_Collapsible_JP_Obj()
    {
        return collapsibleJpObj;
    }
    
    public JPanel getCollapsibleCenterJPanel()
    {
        return collapsibleCenterJPanel;
    }
    
    // ############################################
    // Collections
    // ############################################
    // Total Meal Table
    public ArrayList<String> get_Total_Meal_Table_Column_Names()
    {
        return mealTotalTable_ColumnNames;
    }
    
    public ArrayList<String> get_TotalMeal_Table_Cols_To_Hide()
    {
        return totalMeal_Table_ColToHide;
    }
    
    public ArrayList<String> get_TotalMeal_Table_UnEditable_Cells()
    {
        return get_Total_Meal_Table_Column_Names(); // All the cells are un-editable
    }
    
    // ############################
    // Ingredients Table
    // ############################
    public TreeMap<String, TreeSet<String>> getMap_ingredient_Types_To_Names()
    {
        return map_ingredientTypesToNames;
    }
    
    public ArrayList<String> get_Ingredients_Table_Col_Avoid_Centering()
    {
        return ingredients_Table_Col_Avoid_Centering;
    }
    
    public ArrayList<String> get_Ingredients_Table_UnEditable_Cells()
    {
        return ingredientsTableUnEditableCells;
    }
    
    public ArrayList<String> get_Ingredients_Table_Col_To_Hide()
    {
        return ingredientsInMeal_Table_ColToHide;
    }
    
    public ArrayList<String> get_Ingredients_Table_Column_Names()
    {
        return ingredientsTable_ColumnNames;
    }
    
    // ############################################
    // Integers
    // ############################################
    public int get_Meal_In_Plan_ID()
    {
        return meal_In_Plan_ID;
    }
    
    public int get_Temp_PlanID()
    {
        return tempPlanID;
    }
    
    public int get_Plan_ID()
    {
        return planID;
    }
    
    // #############################################
    // Table Objects
    // ############################################
    public TotalMeal_Table get_Total_Meal_Table()
    {
        return totalMealTable;
    }
    
    public Meal_Plan_Screen get_Meal_plan_screen()
    {
        return meal_plan_screen;
    }
    
    //##################################################################################################################
    // Resizing GUI
    //##################################################################################################################
    private void add_To_Container(Container container, Component addToContainer, Integer gridX, Integer gridy, Integer gridWidth,
                                  Integer gridHeight, Double weightX, Double weightY, String fill, Integer ipadY, Integer ipadX, String anchor)
    {
        if (gridX != null)
        {
            gbc.gridx = gridX;
        }
        if (gridy != null)
        {
            gbc.gridy = gridy;
        }
        
        gbc.gridwidth = gridWidth;
        gbc.gridheight = gridHeight;
        gbc.weightx = weightX;
        gbc.weighty = weightY;
        
        gbc.ipady = ipadY;
        gbc.ipadx = ipadX;
        
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
        
        if (anchor != null)
        {
            switch (anchor.toLowerCase())
            {
                case "start":
                    gbc.anchor = GridBagConstraints.PAGE_START;
                    break;
                
                case "scroll_To_The_End":
                    gbc.anchor = GridBagConstraints.PAGE_END;
                    break;
            }
        }
        container.add(addToContainer, gbc);
    }
}
