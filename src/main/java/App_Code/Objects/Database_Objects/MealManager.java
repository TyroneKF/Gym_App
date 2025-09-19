package App_Code.Objects.Database_Objects;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.IngredientsTable;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.MacrosLeftTable;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.TotalMealTable;
import App_Code.Objects.Gui_Objects.CollapsibleJPanel;
import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Gui_Objects.IconPanel;
import App_Code.Objects.Screens.Graph.Pie_Chart_Meal_Manager_Screen;
import App_Code.Objects.Screens.Meal_Plan_Screen;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
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
    private boolean
            isObjectCreated = false,
            mealManagerInDB = false, hasMealPlannerBeenDeleted = false,
            hasMealNameBeenChanged = false, hasMealTimeBeenChanged = false;

    private String savedMealName = "", currentMealName = "";
    private LocalTime savedMealTime = null, currentMealTime = null;
    private DateTimeFormatter strictTimeFormatter = DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);

    private Integer mealInPlanID, tempPlanID, planID, yPoInternally = 0;
    private String[] mealTotalTable_ColumnNames, ingredientsTable_ColumnNames;
    private ArrayList<String> totalMeal_Table_ColToHide, ingredientsTableUnEditableCells, ingredients_Table_Col_Avoid_Centering, ingredientsInMeal_Table_ColToHide;
    private TreeMap<String, Collection<String>> map_ingredientTypesToNames;

    private ArrayList<IngredientsTable> ingredientsTables = new ArrayList<>();

    String lineSeparator = "###############################################################################";

    //################################################################################
    // Objects
    //################################################################################
    private JPanel collapsibleCenterJPanel, spaceDividerForMealManager = new JPanel();
    private MyJDBC db;
    private GridBagConstraints gbc;
    private Container container;
    private CollapsibleJPanel collapsibleJpObj;
    private MacrosLeftTable macrosLeft_JTable;
    private TotalMealTable totalMealTable;
    private Meal_Plan_Screen meal_plan_screen;
    private Pie_Chart_Meal_Manager_Screen pie_chart_meal_manager_screen;

    //##################################################################################################################
    // Constructors
    //##################################################################################################################
    public MealManager(Meal_Plan_Screen meal_plan_screen, Container container, int mealInPlanID, String mealName, String mealTime, ArrayList<ArrayList<String>> subMealsInMealArrayList)
    {
        //##############################################################################################################
        // Global Variables
        //##############################################################################################################
        this.meal_plan_screen = meal_plan_screen;
        this.container = container;
        this.db = meal_plan_screen.getDb();


        this.tempPlanID = meal_plan_screen.getTempPlanID();
        this.planID = meal_plan_screen.getPlanID();
        this.mealInPlanID = mealInPlanID;

        LocalTime timeStringFormatted = LocalTime.parse(removeSecondsOnTimeString(mealTime)); // turns etc 09:30:00 into 09:30 for GUI purposes & in time format
        setTimeVariables(false, timeStringFormatted, timeStringFormatted); // Set MealTime Variables

        setMealNameVariables(false, mealName, mealName); // Set MealName Variables

        setMealManagerInDB(true);  // Set Variable which identifies in this meal associated with this object is in the database

        setup(); // GUI Setup of MealManager

        add_MultipleSubMealsToGUI(subMealsInMealArrayList); // Add Sub-Meal to GUI
    }

    //
    public MealManager(Meal_Plan_Screen meal_plan_screen, Container container)
    {
        //##############################################################################################################
        // Setting Variable
        //##############################################################################################################
        this.meal_plan_screen = meal_plan_screen;
        this.container = container;
        this.db = meal_plan_screen.getDb();

        this.tempPlanID = meal_plan_screen.getTempPlanID();
        this.planID = meal_plan_screen.getPlanID();

        //##############################################################################################################
        // Getting user input for Meal Name & Time
        //##############################################################################################################

        String newMealName = promptUserForMealName(true, false);

        if(newMealName == null) { return; } // Error occurred in validation checks above

        //#######################################################
        // Validating User Input
        //#######################################################

        LocalTime newMealTime = promptUserForMealTime(true, false);

        if(newMealTime == null) { return; } // Error occurred in validation checks above

        //##############################################################################################################
        // Upload Meal To Temp Plan
        //##############################################################################################################
        String uploadQuery = String.format("INSERT INTO meals_in_plan (plan_id, meal_name, meal_time) VALUES (%s,'%s','%s')", tempPlanID, newMealName, newMealTime);

        if(! (db.uploadData_Batch_Altogether(new String[]{ uploadQuery })))
        {
            JOptionPane.showMessageDialog(null, "\n\n ERROR:\n\nCreating Meal In DB!");
            return;
        }

        //##############################################################################################################
        // Get mealInPlanID
        //##############################################################################################################
        String query = String.format("Select meal_in_plan_id FROM meals_in_plan WHERE plan_id = %s AND meal_name = '%s';", tempPlanID, newMealName);
        String[] results = db.getSingleColumnQuery(query);

        if(results == null)
        {
            JOptionPane.showMessageDialog(null, "\n\n ERROR:\n\nCannot Get Created Meals ID!!");

            String deleteQuery = String.format("DELETE FROM meals_in_plan WHERE plan_id = %s AND meal_name = '%s';)", tempPlanID, newMealName);
            if(! (db.uploadData_Batch_Altogether(new String[]{ deleteQuery })))
            {
                JOptionPane.showMessageDialog(null, "\n\n ERROR:\n\nUnable To Undo Errors Made!\n\nRecommendation Action: Refresh This Plan");
            }

            return;
        }

        mealInPlanID = Integer.valueOf(results[0]);

        //##############################################################################################################
        // Set Name & Time Variables
        //##############################################################################################################

        setMealNameVariables(false, newMealName, newMealName); // Set MealName Variables

        setTimeVariables(false, newMealTime, newMealTime);     // Set MealTime Variables

        setMealManagerInDB(false);

        //##############################################################################################################
        // SetUP
        //##############################################################################################################
        setup();

        //##############################################################################################################
        // Add A SubMeal To Meal
        //##############################################################################################################
        addButtonAction();
    }

    //##################################################################################################################
    // GUI Setup
    //##################################################################################################################
    private void setup()
    {
        ///############################
        // Objects
        ///############################
        this.db = meal_plan_screen.getDb();
        this.gbc = meal_plan_screen.getGbc();
        this.macrosLeft_JTable = meal_plan_screen.getMacrosLeft_JTable();

        ///############################
        // Lists & Arraylists & Maps
        ///############################
        this.mealTotalTable_ColumnNames = meal_plan_screen.getMeal_total_columnNames();
        this.totalMeal_Table_ColToHide = meal_plan_screen.getTotalMeal_Table_ColToHide();
        this.map_ingredientTypesToNames = meal_plan_screen.getMap_ingredientTypesToNames();
        this.ingredientsTableUnEditableCells = meal_plan_screen.getIngredientsTableUnEditableCells();
        this.ingredients_Table_Col_Avoid_Centering = meal_plan_screen.getIngredients_Table_Col_Avoid_Centering();
        this.ingredientsInMeal_Table_ColToHide = meal_plan_screen.getIngredientsInMeal_Table_ColToHide();
        this.ingredientsTable_ColumnNames = meal_plan_screen.getIngredients_ColumnNames();

        ///############################
        // Integers
        ///############################
        this.tempPlanID = meal_plan_screen.getTempPlanID();
        this.planID = meal_plan_screen.getPlanID();

        //##############################################################################################################
        // Create Collapsible Object
        //##############################################################################################################
        //collapsibleJpObj = new CollapsibleJPanel(container, removeSecondsOnTimeString(savedMealTime), 150, 50); // time as btn txt
        collapsibleJpObj = new CollapsibleJPanel(container, savedMealName, 180, 50); // time as btn txt
        collapsibleCenterJPanel = collapsibleJpObj.getCentreJPanel();
        collapsibleCenterJPanel.setBackground(Color.YELLOW);

        //##############################################################################################################
        // Icon Setup in Collapsible Object
        //##############################################################################################################
        iconSetup();

        //##############################################################################################################
        //  Total Meal Calculation JTable
        //##############################################################################################################
        String tableName = "total_meal_view";

        JPanel southPanel = collapsibleJpObj.getSouthJPanel();

        String query = String.format("SELECT * FROM total_meal_view WHERE meal_in_plan_id = %s AND plan_id = %s;", mealInPlanID, tempPlanID);
        Object[][] result = db.getTableDataObject(query, tableName);

        Object[][] meal_Total_Data = result != null ? result : new Object[0][0];

        totalMealTable = new TotalMealTable(db, collapsibleJpObj, meal_Total_Data, mealTotalTable_ColumnNames, planID, tempPlanID,
                mealInPlanID, savedMealName, tableName, new ArrayList<>(Arrays.asList(mealTotalTable_ColumnNames)), null, totalMeal_Table_ColToHide);

        //#############################################
        // TotalMealTable to Collapsible Object
        //#############################################

        // adds space between Ingredients_In_Meal_Calculation table and total_in_meal table
        addToContainer(southPanel, new JPanel(), 0, 1, 1, 1, 0.25, 0.25, "both", 50, 0, null);

        // Adding total table to CollapsibleOBJ
        addToContainer(southPanel, totalMealTable, 0, 2, 1, 1, 0.25, 0.25, "both", 0, 0, null);

        //##############################################################################################################
        // Add Initial Space Between For the First Divided Meal
        //##############################################################################################################
        addToContainer(collapsibleCenterJPanel, new JPanel(), 0, yPoInternally++, 1, 1, 0.25, 0.25, "both", 10, 0, null);

        //##############################################################################################################
        // Set Object Created
        //##############################################################################################################
        isObjectCreated = true;
    }

    private void iconSetup()
    {
        int iconSize = 40;
        //########################################################################
        // Icons Top RIGHT
        //########################################################################
        JPanel eastJPanel = collapsibleJpObj.getEastJPanel();
        eastJPanel.setLayout(new GridBagLayout());

        IconPanel iconPanel = new IconPanel(2, 10, "East");
        JPanel iconPanelInsert = iconPanel.getIconJpanel();

        addToContainer(eastJPanel, iconPanel.getIconAreaPanel(), 0, 0, 1, 1, 0.25, 0.25, "horizontal", 10, 0, null);

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

            if(areYouSure("Refresh Data"))
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
            if(areYouSure("Save Data"))
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

            if(areYouSure("Delete"))
            {
                deleteMealManagerAction();
            }
        });

        iconPanelInsert.add(delete_btn);
    }

    private void add_MultipleSubMealsToGUI(ArrayList<ArrayList<String>> subMealIDs)
    {
        ///##############################################################################################################
        //  Ingredients_In_Meal_Calculation JTable
        //##############################################################################################################
        int no_Of_SubMealID = subMealIDs.size();
        for (int i = 0; i < no_Of_SubMealID; i++)
        {
            int divMealSectionsID = Integer.parseInt(subMealIDs.get(i).get(0));
            add_SubMealToGUi(true, divMealSectionsID);
        }
    }

    private void add_SubMealToGUi(boolean subMealInDB, Integer divMealSectionsID)
    {
        String tableName = "ingredients_in_sections_of_meal_calculation";
        Object[][] mealData = new Object[0][0];

        if(subMealInDB)
        {
            // Getting Ingredients In Meal
            String query = String.format("SELECT * FROM %s WHERE div_meal_sections_id = %s AND plan_id = %s ORDER BY ingredients_index;", tableName, divMealSectionsID, tempPlanID);
            mealData = db.getTableDataObject(query, tableName) != null ? db.getTableDataObject(query, tableName) : mealData;
        }

        //##############################################
        // Ingredients_In_Meal_Calculation Creation
        //##############################################
        JPanel spaceDivider = new JPanel();
        IngredientsTable ingredients_Calculation_JTable = new IngredientsTable(db, this, mealData, ingredientsTable_ColumnNames, planID, mealInPlanID, divMealSectionsID, subMealInDB, savedMealName,
                tableName, ingredientsTableUnEditableCells, ingredients_Table_Col_Avoid_Centering, ingredientsInMeal_Table_ColToHide, spaceDivider);

        ingredientsTables.add(ingredients_Calculation_JTable);

        //################################################
        // Ingredients_In_Meal_Calculation Customisation
        //################################################
        addToContainer(collapsibleCenterJPanel, ingredients_Calculation_JTable, 0, yPoInternally++, 1, 1, 0.25, 0.25, "both", 0, 0, null);
        addToContainer(collapsibleCenterJPanel, spaceDivider, 0, yPoInternally++, 1, 1, 0.25, 0.25, "both", 50, 0, null);
    }

    //##################################################################################################################
    // Actions
    //##################################################################################################################
    private Boolean areYouSure(String process)
    {
        int reply = JOptionPane.showConfirmDialog(getFrame(), String.format("You are requesting to %s ! \n\nAre you sure you want to %s?", process, process),
                "Notification", JOptionPane.YES_NO_OPTION); //HELLO Edit

        if(reply == JOptionPane.NO_OPTION || reply == JOptionPane.CLOSED_OPTION)
        {
            return false;
        }
        return true;
    }

    //#################################################################################
    // Pie Charts
    //#################################################################################
    public void pieChart_Action()
    {
        // If pieChart is already created bring up to the surface and make it visible
        if(pie_chart_meal_manager_screen != null)
        {
            System.out.println("\n\nB : pieChart_Action() 1 !!!!");

            pie_chart_meal_manager_screen.makeVisible();
            return;
        }

        pie_chart_meal_manager_screen = new Pie_Chart_Meal_Manager_Screen(db, this);
    }

    public void pieChart_UpdateMealName()
    {
        //#########################################################################################################
        // Change Graph Title if exists
        //#########################################################################################################
        if(pie_chart_meal_manager_screen != null)
        {
            pie_chart_meal_manager_screen.update_PieChart_Title();
        }
    }

    public void removePieChartScreen()
    {
        pie_chart_meal_manager_screen = null;
    }


    //#################################################################################
    // Meal Time Functions
    //#################################################################################
    public LocalTime promptUserForMealTime(boolean skipConfirmation, boolean comparison)
    {
        // User info prompt
        String inputMealTime = JOptionPane.showInputDialog("Input Meal Time etc \"09:00\"?");

        if(inputMealTime == null || inputMealTime.equals("")) { return null; }

        return (LocalTime) inputValidation("time", inputMealTime, comparison, currentMealTime, skipConfirmation);
    }

    public void editTime_Btn_Action()
    {
        //#########################################################################################################
        // Prompt User for time Input
        //#########################################################################################################

        LocalTime newMealTime = promptUserForMealTime(false, true);

        if(newMealTime == null) { return; } // Error occurred in validation checks above

        //#########################################################################################################
        // Update
        //#########################################################################################################
        String uploadQuery = String.format(""" 
                UPDATE meals_in_plan
                SET meal_time = '%s'
                WHERE plan_id = %s AND meal_in_plan_id = %s; """, newMealTime, tempPlanID, mealInPlanID);

        System.out.printf("\n\neditTime_Btn_Action() uploadQuery = \n%s", uploadQuery);

        //##########################################
        // Upload Into Database Table
        //##########################################
        if(! db.uploadData(uploadQuery, false))
        {
            JOptionPane.showMessageDialog(null, "\n\nUnable to successfully change this  meals time! \n\nMaybe he selected timeframe  meal name already exists within this meal plan!!");
            return;
        }

        //#########################################################################################################
        // Update GUI & Variables
        //#########################################################################################################
        JOptionPane.showMessageDialog(getFrame(), String.format("Successfully, changed meal time from '%s' to '%s'", currentMealTime, newMealTime)); // Success MSG

        // Time Variables
        setTimeVariables(true, savedMealTime, newMealTime); // Set Meal Time Variables
        meal_plan_screen.addMealManger2(this, true); // Update Meal Plan Screen

        // Update total Meal View
        totalMealTable.updateTotalMealTable(); // Make Updates Visible
    }

    private String removeSecondsOnTimeString(String mealTime)
    {
        StringBuilder returnString = new StringBuilder();
        int colonCount = 0;

        for (char c : mealTime.toCharArray())
        {
            if(c == ':') { colonCount++; if(colonCount == 2) { return returnString.toString(); } }
            returnString.append(c);
        }

        return mealTime;
    }

    private void setTimeVariables(boolean hasMealTimeBeenChanged, LocalTime savedMealTime, LocalTime currentMealTime)
    {
        this.hasMealTimeBeenChanged = hasMealTimeBeenChanged;
        this.savedMealTime = savedMealTime;
        this.currentMealTime = currentMealTime;
    }

    public LocalTime getCurrentMealTime()
    {
        return currentMealTime;
    }

    //#################################################################################
    // Meal Name Functions
    //#################################################################################
    private String promptUserForMealName(boolean skipConfirmation, boolean comparison)
    {
        // Get User Input For Meal Name
        String newMealName = JOptionPane.showInputDialog(getFrame(), "Input Meal Name?");

        // User Cancelled or entered nothing
        if(newMealName == null || newMealName.equals("")) { return null; }

        // validate user input
        return (String) inputValidation("name", newMealName, comparison, currentMealName, skipConfirmation);
    }

    private void edit_Name_BTN_Action()// Update method to update time as well
    {
        //#########################################################################################################
        // Validation Checks
        //#########################################################################################################

        // Get User Input
        String inputMealName = promptUserForMealName(false, true);

        if(inputMealName == null) { return; } // Error occurred in validation checks above

        //#########################################################################################################
        // Update
        //#########################################################################################################

        String uploadQuery = String.format(""" 
                UPDATE meals_in_plan
                SET meal_name = '%s'
                WHERE plan_id = %s AND meal_in_plan_id = %s;""", inputMealName, tempPlanID, mealInPlanID);

        if(! db.uploadData(uploadQuery, false))
        {
            JOptionPane.showMessageDialog(getFrame(), "\n\nUnable to successfully change this  meals name! \n\nMaybe he selected meal name already exists within this meal plan!!");
            return;
        }

        //#########################################################################################################
        // Change Button Text & Related Variables
        //#########################################################################################################

        // Success MSG
        JOptionPane.showMessageDialog(getFrame(), String.format("Successfully, changed meal name from ' %s ' to ' %s ' ", currentMealName, inputMealName));

        collapsibleJpObj.setIconBtnText(inputMealName);
        setMealNameVariables(true, savedMealName, inputMealName);  // Set Meal Name Variables
        totalMealTable.updateTotalMealTable(); // Make Updates Visible

        //#########################################################################################################
        // Change Graph Title if exists
        //#########################################################################################################
        pieChart_UpdateMealName();
    }

    private void setMealNameVariables(boolean hasMealNameBeenChanged, String savedMealName, String currentMealName)
    {
        this.hasMealNameBeenChanged = hasMealNameBeenChanged;
        this.savedMealName = savedMealName;
        this.currentMealName = currentMealName;
    }

    public String getCurrentMealName()
    {
        return currentMealName;
    }

    //#################################################################################
    // Meal Name & Meal Time Functions
    //#################################################################################
    private boolean containsSymbols(String stringToCheck)
    {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9 '\\-&]+$");
        Matcher matcher = pattern.matcher(stringToCheck);

        return ! matcher.matches();
    }

    private Object inputValidation(String variableName, String input, boolean comparison, Object currentVariableValue, boolean skipConfirmation)
    {
        // Remove whitespace at the end of variable
        input = input.trim();

        //#########################################################################################################
        // Check the Right Parameters are Entered Per Request Type
        //#########################################################################################################

        // variable : input due to prior checks will not be null or "" at this stage. Will always have a value.
        // Instance of also returns false on null values
        // Prior to this method being called the users input is checked if its null or "" and rejected

        if(variableName.equals("name") && comparison && ! (currentVariableValue instanceof String))
        {
            System.err.printf("\n\nMealManager.java : inputValidation() for 'name' variable; must be 2 strings");
            return null;
        }
        else if(variableName.equals("time") && comparison && ! (currentVariableValue instanceof LocalTime))
        {
            System.err.printf("\n\nMealManager.java : inputCheck() for 'time' variable; must be a string & time variable");
            return null;
        }
        else if(! variableName.equals("name") && ! variableName.equals("time"))
        {
            System.err.printf("\n\nMealManager.java : inputCheck() Unknown Request");
            return null;
        }

        //##############################################################################################################
        // Validation Checks
        //##############################################################################################################
        // Prior to this method being called the users input is checked if its null or "" and rejected

        LocalTime newTimeVar = null;

        if(variableName.equals("time"))
        {
            input = removeSecondsOnTimeString(input);

            try
            {
                newTimeVar = LocalTime.parse(input);
                if(newTimeVar == null) { throw new Exception("\n\nError, time variable null"); }
            }
            catch (Exception e)
            {
                System.err.printf("\n\nMealManager.java: inputValidation() | Error, converting input to time string! \n%s", e);
                JOptionPane.showMessageDialog(getFrame(), "Error, converting input to time string !!");
                return null;
            }
        }
        else if(containsSymbols(input)) // Name: check if any symbols are inside
        {
            JOptionPane.showMessageDialog(getFrame(), String.format("\n\nError, '%s' cannot contain symbols!", variableName));
            return null;
        }

        // ####################################################
        // Compare with save Values
        // ####################################################
        if(comparison)
        {
            // User enters same meal name
            if(variableName.equals("name") && input.equals(currentVariableValue.toString()))
            {
                JOptionPane.showMessageDialog(getFrame(), String.format("This meal '%s' already has the value '%s' !!", variableName, currentVariableValue));
                return null;
            }
            else if(variableName.equals("time"))
            {
                LocalTime currentTime = (LocalTime) currentVariableValue;
                if(newTimeVar.equals(currentTime)) // Time : User enters same meal time
                {
                    JOptionPane.showMessageDialog(getFrame(), String.format("This meal '%s' already has the value '%s' !!", variableName, currentVariableValue));
                    return null;
                }
            }
        }

        // #############################################################################################################
        // Check Database if Value Already Exists
        // #############################################################################################################
        String query = "";

        if(variableName.equals("time")) // Validate time String
        {
            query = String.format("""
                    SELECT IFNULL((
                        SELECT meal_time
                        FROM meals_in_plan
                        WHERE plan_id = %s AND meal_time = '%s:00'
                        LIMIT 1
                    ), 'N/A') AS meal_time;""", tempPlanID, newTimeVar.toString());
        }
        else // Last possible option based on logic is Meal Name
        {
            input = StringUtils.capitalize(input);

            query = String.format("""
                    SELECT IFNULL((
                        SELECT meal_name
                        FROM meals_in_plan
                        WHERE plan_id = %s AND meal_name = '%s'
                        LIMIT 1
                    ), 'N/A') AS meal_name;""", tempPlanID, input);
        }

        // #########################################
        // Execute Query
        // #########################################
        System.out.printf("\n\nQuery: \n%s", query);

        ArrayList<String> results = db.getSingleColumnQuery_ArrayList(query);

        if(results == null) { return null; } // Error occurred during script

        if(! results.get(0).equals("N/A")) // Means value already exists, returns N/A if the value doesn't
        {
            JOptionPane.showMessageDialog(getFrame(), String.format("A meal in this plan already has a meal %s of '%s' !!", variableName, input));
            return null;
        }

        //##############################################################################################################
        // User Confirmation
        //##############################################################################################################
        // If requested not to skip a confirmation msg prompt confirmation
        if(! skipConfirmation && ! areYouSure(String.format("change meal %s from '%s' to '%s'", variableName, currentVariableValue, input)))
        {
            return null;
        }

        //##############################################################################################################
        // Return Value
        //##############################################################################################################
        if(variableName.equals("name")) { return input; }
        else { return newTimeVar; }
    }

    //#################################################################################
    // Add BTN
    //#################################################################################
    // HELLO, Needs to scroll down to the bottom of the MealManager
    public void addButtonAction()
    {
        //##########################################
        // Get New ID For SubMeal
        //##########################################
        String getNextIndexQuery = "SELECT IFNULL(MAX(`div_meal_sections_id`),0) + 1 AS nextId FROM `divided_meal_sections`;";

        String[] divMealSectionsIDResult = db.getSingleColumnQuery(getNextIndexQuery);

        if(divMealSectionsIDResult == null)
        {
            JOptionPane.showMessageDialog(null, "Unable to create new sub meal in table! \nUnable to generate new div_meal_sections_id !!");
            return;
        }

        Integer divMealSectionsID = Integer.valueOf(divMealSectionsIDResult[0]);

        //##########################################
        // Insert Into Database Table
        //##########################################
        String uploadQuery = String.format("INSERT INTO divided_meal_sections (div_meal_sections_id, meal_in_plan_id, plan_id) VALUES (%s, %s, %s)", divMealSectionsID, mealInPlanID, tempPlanID);

        if(! db.uploadData_Batch_Altogether(new String[]{ uploadQuery }))
        {
            JOptionPane.showMessageDialog(null, "Unable to successfully add subMeal to meal! ");
            return;
        }

        //##########################################
        // Add Sub-Meal To Meal GUI
        //##########################################
        add_SubMealToGUi(false, divMealSectionsID);
    }

    //#################################################################################
    // Delete BTN Methods
    //#################################################################################
    private void setHasMealPlannerBeenDeleted(boolean x)
    {
        hasMealPlannerBeenDeleted = x;
    }

    private void hideMealManager()
    {
        setVisibility(false); // hide collapsible Object
        setHasMealPlannerBeenDeleted(true); // set this object as deleted
    }

    private void unHideMealManager()
    {
        setVisibility(true); // hide collapsible Object
        setHasMealPlannerBeenDeleted(false); // set this object as deleted
    }

    public boolean getHasMealPlannerBeenDeleted()
    {
        return hasMealPlannerBeenDeleted;
    }

    public void setVisibility(boolean condition)
    {
        collapsibleJpObj.setVisible(condition);
        spaceDividerForMealManager.setVisible(condition);
    }

    public boolean areAllTableBeenDeleted()
    {
        //##########################################
        // IF there are no meals, delete table
        //##########################################
        for (IngredientsTable ingredientsTable : ingredientsTables)
        {
            if(! (ingredientsTable.hasIngredientsTableBeenDeleted())) // if a meal hasn't been deleted, exit method
            {
                return false;
            }
        }

        //##########################################
        return true;
    }

    public void ingredientsTableHasBeenDeleted()
    {
        //##########################################
        // If there are no meals, delete table
        //##########################################
        if(! (areAllTableBeenDeleted()))
        {
            return;
        }

        //##########################################
        // Delete Meal From DB
        //##########################################
        String query1 = "SET FOREIGN_KEY_CHECKS = 0;"; // Disable Foreign Key Checks
        String query2 = String.format("DELETE FROM meals_in_plan WHERE meal_in_plan_id = %s AND plan_id = %s;", mealInPlanID, tempPlanID);
        String query3 = "SET FOREIGN_KEY_CHECKS = 1;"; // Enable Foreign Key Checks

        if(! (db.uploadData_Batch_Altogether(new String[]{ query1, query2, query3 })))
        {
            JOptionPane.showMessageDialog(getFrame(), "\n\n1.)  Error MealManager.ingredientsTableHasBeenDeleted() \nUnable to Delete Selected Meal From DB!");
            return;
        }

        hideMealManager();
    }

    public void deleteMealManagerAction()
    {
        //##########################################
        // Delete Meal from database
        //##########################################
        String query1 = "SET FOREIGN_KEY_CHECKS = 0;"; // Disable Foreign Key Checks

        // DELETE ingredients_in_sections_of_meal
        String query2 = String.format(""" 
                DELETE FROM ingredients_in_sections_of_meal
                WHERE div_meal_sections_id IN (SELECT div_meal_sections_id FROM divided_meal_sections WHERE meal_in_plan_id = %s AND plan_id = %s) AND plan_id = %s;""", mealInPlanID, tempPlanID, tempPlanID);

        // DELETE dividedMealSections
        String query3 = String.format("DELETE FROM divided_meal_sections WHERE meal_in_plan_id = %s AND plan_id = %s;", mealInPlanID, tempPlanID);

        // DELETE mealsInPlan
        String query4 = String.format("DELETE FROM meals_in_plan WHERE meal_in_plan_id = %s AND plan_id = %s", mealInPlanID, tempPlanID);

        String query5 = "SET FOREIGN_KEY_CHECKS = 1;"; // Enable Foreign Key Checks

        System.out.printf("\n\n%s \n\n%s \n\n%s \n\n%s \n\n%s", query1, query2, query3, query4, query5);

        if(! db.uploadData_Batch_Altogether(new String[]{ query1, query2, query3, query4, query5 }))
        {
            JOptionPane.showMessageDialog(null, "Table Un-Successfully Deleted! ");
            return;
        }

        //##########################################
        // Hide JTable object & Collapsible OBJ
        //##########################################
        hideMealManager();

        //##########################################
        // Update MacrosLeftTable
        //##########################################
        update_MacrosLeft_Table();// update macrosLeft table, due to number deductions from this meal

        //##########################################
        // Update Message
        //##########################################
        JOptionPane.showMessageDialog(null, "Table Successfully Deleted!");
    }

    public void completely_Delete_MealManager()
    {
        hideMealManager();
        removeMealManagerFromGUI();
    }

    public void removeMealManagerFromGUI()
    {
        container.remove(collapsibleJpObj); // remove the GUI elements from GUI
        container.remove(spaceDividerForMealManager);    // remove space divider from GUI
    }

    public JPanel getSpaceDividerForMealManager()
    {
        return spaceDividerForMealManager;
    }

    public void removeIngredientsTable(IngredientsTable ingredientsTable)
    {
        ingredientsTables.remove(ingredientsTable);
    }

    //#################################################################################
    // Save & Refresh
    //#################################################################################
    public boolean transferMealDataToPlan(String process, int fromPlanID, int toPlanID)
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
        String query2 = "SET FOREIGN_KEY_CHECKS = 0;"; // Disable Foreign Key Checks

        // Delete ingredients from this meal in toPlan
        String query3 = String.format(""" 
                DELETE FROM ingredients_in_sections_of_meal
                WHERE div_meal_sections_id IN (SELECT div_meal_sections_id FROM divided_meal_sections WHERE meal_in_plan_id = %s AND plan_id = %s) AND plan_id = %s;""", mealInPlanID, toPlanID, toPlanID);

        // Delete sub-meals from this meal in toPlan
        String query4 = String.format("DELETE FROM divided_meal_sections WHERE meal_in_plan_id = %s AND plan_id = %s;", mealInPlanID, toPlanID);

        String query5 = "SET FOREIGN_KEY_CHECKS = 1;"; // Enable Foreign Key Checks

        //####################################################
        // Transferring this plans Ingredients to Temp-Plan
        //####################################################

        // Create Table to transfer ingredients from original plan to temp
        String query6 = String.format("""
                CREATE TABLE temp_divided_meal_sections  AS
                SELECT i.*
                FROM divided_meal_sections i
                WHERE i.meal_in_plan_id = %s AND i.plan_id = %s;""", mealInPlanID, fromPlanID);

        String query7 = String.format("UPDATE temp_divided_meal_sections SET plan_id = %s;", toPlanID);

        String query8 = "INSERT INTO divided_meal_sections SELECT * FROM temp_divided_meal_sections;";
        //####################################################
        // Transferring ingredients from this meal in toPlan
        //####################################################

        // Create Table to transfer ingredients from original plan to temp
        String query9 = String.format("""
                CREATE table temp_ingredients_in_meal  AS
                SELECT i.*
                FROM ingredients_in_sections_of_meal i
                WHERE div_meal_sections_id IN (SELECT div_meal_sections_id FROM divided_meal_sections WHERE meal_in_plan_id = %s AND plan_id = %s) AND plan_id = %s;
                """, mealInPlanID, fromPlanID, fromPlanID);

        String query10 = String.format("UPDATE temp_ingredients_in_meal SET plan_id = %s;", toPlanID);

        String query11 = "INSERT INTO ingredients_in_sections_of_meal SELECT * FROM temp_ingredients_in_meal;";

        //#####################################################
        // Meal Name & Time Updates If Changed
        //#####################################################
        String[] query_Temp_Data = new String[0];
        int changes = 0;
        String
                uploadQuery = "",
                updateMealName = process.equals("refresh") ? savedMealName : currentMealName, // set mealName to refresh
                updateMealTime = process.equals("refresh") ? savedMealTime.toString() : currentMealTime.toString(); // set mealTime to time

        if(hasMealNameBeenChanged || hasMealTimeBeenChanged) //  The meal time or name doesn't need to be updated
        {
            uploadQuery = String.format("UPDATE meals_in_plan SET meal_name = '%s', meal_time = '%s'  WHERE plan_id = %s AND  meal_in_plan_id = %s;", updateMealName, updateMealTime, toPlanID, mealInPlanID);

            query_Temp_Data = new String[]{ uploadQuery, query0, query1, query2, query3, query4, query5, query6, query7, query8, query9, query10, query11 };
        }
        else // if either of the time and name has been changed update it
        {
            query_Temp_Data = new String[]{ query0, query1, query2, query3, query4, query5, query6, query7, query8, query9, query10, query11 };
        }

        System.out.printf("\n\n Here  3 \n\n %s", uploadQuery);

        //####################################################
        // Update
        //####################################################
        if(! (db.uploadData_Batch_Altogether(query_Temp_Data)))
        {
            JOptionPane.showMessageDialog(null, "\n\ntransferMealDataToPlan() Error");
            return false;
        }

        return true;
    }

    //######################################
    // Refresh
    //######################################
    public void refresh_Btn_Action()
    {
        //#############################################################################################
        // Check IF OLD Table Name & Meal Time Are Available To Assign Back To This Meal
        //##############################################################################################

        String query = String.format("""                                
                SELECT IFNULL(M.pos, "N/A") AS pos		
                FROM
                (
                  -- This being the anchor had to restricted to make sure there's always a true value
                  -- to attach to for IFNULL to work
                 
                  SELECT plan_id FROM plans WHERE plan_id = %s
                 
                ) AS P
                LEFT JOIN
                (
                	-- Has to be restricted on plan_id then count as
                	
                	SELECT
                	ROW_NUMBER() OVER (ORDER BY meal_time ASC) AS pos,
                	meal_in_plan_id, plan_id, meal_name, meal_time
                	FROM meals_in_plan
                	WHERE plan_id = %s
                	
                ) AS M
                  
                ON P.plan_id = M.plan_id
                AND (M.meal_name = '%s' OR M.meal_time = '%s')
                AND M.meal_in_plan_id != %s; """, tempPlanID, tempPlanID, savedMealName, savedMealTime, mealInPlanID);

        // Execute Query
        System.out.printf("\n\nQuery: \n %s", query);

        ArrayList<ArrayList<String>> results = db.getMultiColumnQuery(query);
        if(results == null)
        {
            JOptionPane.showMessageDialog(getFrame(), "\n\nError, unable to use DB to gather whether old meal time/name are \nattached to another meal in plan right now!");
            System.err.printf("\n\nError,MealManager.java: refresh_Btn_Action() \nUsing script: \n\n%s", query);
            return;
        }

        //##############################################################################################
        // Check if results prove another table in plan has this meal saved time/name & cancel if needed
        //##############################################################################################
        ArrayList<String> positions = new ArrayList<>();

        if(results.size() >= 1 && ! results.get(0).get(0).equals("N/A")) { positions.add(results.get(0).get(0)); }
        if(results.size() >= 2 && ! results.get(1).get(0).equals("N/A")) { positions.add(results.get(1).get(0)); }

        if(positions.size() > 0)
        {
            JOptionPane.showMessageDialog(getFrame(), String.format("""
                    \n\nA meal in this plan already has this meals saved info:
                                
                    'Meal Name' of : '%s' or 'Meal Time' of : '%s' 
                                
                    at positions : %s  which is stopping this meal from refreshing !
                                
                    Change those values first at positions : %s in this plan 
                    to be able to refresh this meal!
                                
                    Or, refresh the whole plan if the other meals won't be affected !""", savedMealName, savedMealTime, positions, positions));

            return;
        }

        //##############################################################################################
        // Reset DB Data
        //##############################################################################################
        if(! (transferMealDataToPlan("refresh", planID, tempPlanID)))
        {
            JOptionPane.showMessageDialog(null, "\n\nUnable to transfer mealData toS!!");
            return;
        }

        //#############################################################################################
        // Reset GUI  & Variables
        //##############################################################################################
        // Reset Time & MealName Variables
        setTimeVariables(false, savedMealTime, savedMealTime);
        setMealNameVariables(false, savedMealName, savedMealName);

        collapsibleJpObj.setIconBtnText(savedMealName); // Reset Meal Name in GUI to Old Txt

        reloadingIngredientsTableDataFromRefresh(true);

        // Update PieChart Name
        pieChart_UpdateMealName();

        // Reset GUI by removing and adding this object to the GUI
        meal_plan_screen.addMealManger2(this, true);
    }

    public void reloadingIngredientsTableDataFromRefresh(boolean updateMacrosLeft)
    {
        //##############################################################################################
        // Refresh ingredients meal table & total Tables Data
        //##############################################################################################
        Iterator<IngredientsTable> it = ingredientsTables.iterator();
        while (it.hasNext())
        {
            IngredientsTable ingredientsTable = it.next();

            // If ingredientsTable is in DB  then refresh
            if(ingredientsTable.getMealInDB())
            {
                ingredientsTable.reloadingDataFromRefresh(false, false);
                continue;
            }

            // Because mealManager has been deleted remove it
            ingredientsTable.completely_Delete_IngredientsJTable(); // delete table from db

            // Because mealManager has been deleted remove it
            it.remove(); // remove from list
        }

        //##############################################################################################
        // Make This MealManager Visible
        //##############################################################################################
        unHideMealManager();

        //##############################################################################################
        // Refresh TotalMeal
        //##############################################################################################
        update_TotalMeal_Table(); // this has to be done

        //##############################################################################################
        // Refresh MacrosLeft
        //##############################################################################################
        if(updateMacrosLeft) // this is optional
        {
            update_MacrosLeft_Table();
        }
    }

    //######################################
    // Save
    //######################################
    public void setMealManagerInDB(boolean mealManagerInDB)
    {
        this.mealManagerInDB = mealManagerInDB;
    }

    public boolean isMealManagerInDB()
    {
        return mealManagerInDB;
    }

    public void save_Btn_Action()
    {
        // ###############################################################################
        // Transferring Meals & Ingredients from FromPlan to toPlan
        // ###############################################################################
        if(! (transferMealDataToPlan("saving", tempPlanID, planID))) // transfer meals and ingredients from temp plan to original plan
        {
            System.out.println("\n\n#################################### \nError MealManager saveMealData()");

            JOptionPane.showMessageDialog(getFrame(), "\n\n1.)  Error \nUnable to save this Meal and its sub-meals");
            return;
        }

        // Set Variables
        setTimeVariables(false, currentMealTime, currentMealTime);
        setMealNameVariables(false, currentMealName, currentMealName);
        setMealManagerInDB(true);

        saveData(true);
    }

    public void saveData(boolean showUpdateMessage)
    {
        // ###############################################################################
        // Removing Sub-Meals that have been deleted & Saving The Other Tables
        // ##############################################################################
        Iterator<IngredientsTable> it = ingredientsTables.iterator();
        int errorCount = 0;
        while (it.hasNext())
        {
            IngredientsTable table = it.next();

            // #####################################
            // Remove Deleted Ingredients Table
            // #####################################
            if(table.hasIngredientsTableBeenDeleted())   // If objected is deleted, completely delete it then skip to next JTable
            {
                table.completely_Delete_IngredientsJTable();
                it.remove();
                continue;
            }

            // #####################################
            // If error occurred above exit
            // #####################################
            table.set_Meal_In_DB(true);

            if(! (table.updateTableModelData()))
            {
                errorCount++;
            }
        }

        // #####################################
        // If error occurred above exit
        // #####################################
        if(errorCount > 0)
        {
            JOptionPane.showMessageDialog(getFrame(), "\n\n Error \n1.) Unable to updateTableModelData() all sub-meals in meal! \n\nPlease retry again!");
            return;
        }

        // ##############################################################################
        // Save TotalMealTable
        // ##############################################################################
        totalMealTable.updateTotalMealTableModelData();

        // ##############################################################################
        // Successful Message
        // ##############################################################################
        if(showUpdateMessage)
        {
            JOptionPane.showMessageDialog(getFrame(), "\n\nAll SubMeals Within Meal Have Successfully Saved!");
        }
    }

    //##################################################################################################################
    // Updating Other Tables
    //##################################################################################################################
    public void update_MacrosLeft_Table()
    {
        macrosLeft_JTable.updateMacrosLeftTable();
    }

    public void update_TotalMeal_Table()
    {
        totalMealTable.updateTotalMealTable();
        update_Pie_Chart_Screen();
    }

    public void update_Pie_Chart_Screen()
    {
        if(pie_chart_meal_manager_screen != null) { pie_chart_meal_manager_screen.update_pieChart(); }
    }

    //##################################################################################################################
    // Accessor Methods
    //##################################################################################################################
    public CollapsibleJPanel getCollapsibleJpObj()
    {
        return collapsibleJpObj;
    }

    public boolean isObjectCreated()
    {
        return isObjectCreated;
    }

    public int getMealInPlanID()
    {
        return mealInPlanID;
    }

    public int getTemp_PlanID()
    {
        return tempPlanID;
    }

    public JPanel getCollapsibleCenterJPanel()
    {
        return collapsibleCenterJPanel;
    }

    public TreeMap<String, Collection<String>> getMap_ingredientTypesToNames()
    {
        return map_ingredientTypesToNames;
    }

    public Frame getFrame()
    {
        return meal_plan_screen.getFrame();
    }

    public TotalMealTable getTotalMealTable()
    {
        return totalMealTable;
    }

    public Meal_Plan_Screen getMeal_plan_screen()
    {
        return meal_plan_screen;
    }

    //##################################################################################################################
    // Resizing GUI
    //##################################################################################################################
    private void addToContainer(Container container, Component addToContainer, Integer gridx, Integer gridy, Integer gridwidth,
                                Integer gridheight, Double weightx, Double weighty, String fill, Integer ipady, Integer ipadx, String anchor)
    {
        if(gridx != null)
        {
            gbc.gridx = gridx;
        }
        if(gridy != null)
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

        if(anchor != null)
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
