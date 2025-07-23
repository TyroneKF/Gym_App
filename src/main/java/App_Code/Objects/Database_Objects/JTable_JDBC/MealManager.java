package App_Code.Objects.Database_Objects.JTable_JDBC;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.IngredientsTable;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.Children.MacrosLeftTable;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.Children.Total_Tables.Children.TotalMealTable;
import App_Code.Objects.Gui_Objects.CollapsibleJPanel;
import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Gui_Objects.IconPanel;
import App_Code.Objects.Screens.Others.Meal_Plan_Screen;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.*;

public class MealManager
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private boolean mealManagerInDB = false, hasMealPlannerBeenDeleted = false, hasMealNameBeenChanged = false, hasMealTimeBeenChanged = false;
    private Integer mealInPlanID, tempPlanID, planID, yPoInternally = 0, mealNo;
    private String savedMealName, savedMealTime, newMealTime = "";
    private String[] mealTotalTable_ColumnNames, ingredientsTable_ColumnNames;
    private ArrayList<String> totalMeal_Table_ColToHide, ingredientsTableUnEditableCells, ingredients_Table_Col_Avoid_Centering, ingredientsInMeal_Table_ColToHide;
    private TreeMap<String, Collection<String>> map_ingredientTypesToNames;

    private ArrayList<IngredientsTable> ingredientsTables = new ArrayList<>();

    String lineSeparator = "###############################################################################";

    //####################
    // Objects
    //####################
    private JPanel collapsibleCenterJPanel, spaceDivider = new JPanel();
    private MyJDBC db;
    private GridBagConstraints gbc;
    private Container container;
    private CollapsibleJPanel collapsibleJpObj;
    private MacrosLeftTable macrosLeft_JTable;
    private TotalMealTable total_Meal_View_Table;
    private Frame frame;
    private Meal_Plan_Screen meal_plan_screen;

    //##################################################################################################################
    // Constructors
    //##################################################################################################################
    public MealManager(Meal_Plan_Screen meal_plan_screen, Container container, boolean mealManagerInDB, int mealInPlanID, int mealNo, String mealName, String savedMealTime, ArrayList<ArrayList<String>> subMealsInMealArrayList)
    {
        //##############################################################################################################
        // Global Variables
        //##############################################################################################################
        this.mealInPlanID = mealInPlanID;
        this.mealNo = mealNo;
        this.savedMealName = mealName;
        this.savedMealTime = savedMealTime;
        this.container = container;
        this.mealManagerInDB = mealManagerInDB;
        this.meal_plan_screen = meal_plan_screen;

        //##############################################################################################################
        // SetUP
        //##############################################################################################################
        setup();

        //##############################################################################################################
        // Add Sub-Meal to GUI
        //##############################################################################################################
        add_MultipleSubMealsToGUI(subMealsInMealArrayList);

        //##############################################################################################################
        // Sort MealPlan GUI Out
        //##############################################################################################################
        scroll_To_The_End();
    }

    //
    public MealManager(Meal_Plan_Screen meal_plan_screen, Container container)
    {
        //##############################################################################################################
        // Global Variables
        //##############################################################################################################
        this.meal_plan_screen = meal_plan_screen;
        this.container = container;
        this.mealManagerInDB = false;
        this.mealNo = meal_plan_screen.getCurrentMealNo() + 1;


        ///############################
        //
        ///############################
        this.tempPlanID = meal_plan_screen.getTempPlanID();
        this.planID = meal_plan_screen.getPlanID();
        this.db = meal_plan_screen.getDb();

        //##############################################################################################################
        // Getting user input for Meal Name
        //##############################################################################################################
        String newMealName = JOptionPane.showInputDialog("Input Meal Name?");

        if (newMealName == null || newMealName.length() == 0)
        {
            JOptionPane.showMessageDialog(null, "\n\nPlease Input A Valid Name With 1+ Characters!");
            return;
        }

        //#############################################
        // Input Time
        //#############################################
        String newMealTime = createTimeString();

        if (newMealTime.equals("void"))
        {
            return;
        }

        //##############################################################################################################
        // Does The Chosen Meal Name Exist Within Temp Plan Or Original Plan
        //##############################################################################################################

        /*

           String mealInTempPlan = String.format("""
                        SELECT Meal_Name FROM mealsInPlan
                        WHERE
                        	Meal_Name = '%s' AND (PlanID = %s OR PlanID = %s)
                        OR
                            Meal_Time = '%s' AND (PlanID = %s OR PlanID = %s)
                        OR
                            `Meal_Time` > '%s' AND (PlanID = %s OR PlanID = %s)

                        LIMIT 1;""",
                newMealName, tempPlanID, planID,
                newMealTime, tempPlanID, planID,
                newMealTime, tempPlanID, planID);
         */

        String mealInTempPlan = String.format("""
                        SELECT Meal_Name FROM mealsInPlan
                        WHERE
                        	Meal_Name = '%s' AND (PlanID = %s OR PlanID = %s)
                        OR  
                            Meal_Time = '%s' AND (PlanID = %s OR PlanID = %s)                                 
                                                                   	
                        LIMIT 1;""",
                newMealName, tempPlanID, planID,
                newMealTime, tempPlanID, planID);

        System.out.printf("""
                \n\nMealManager(Meal_Plan_Screen meal_plan_screen, Container container) 
                
                %s
                """, mealInTempPlan);

        if (!(db.getSingleColumnQuery(mealInTempPlan) == null))
        {
            JOptionPane.showMessageDialog(null, "\n\n ERROR:\n\nMeal Name / Time Already Exists Within This Plan!! \nOr, Meal Time is lower than previously entered meals!");
            return;
        }

        //##############################################################################################################
        // Upload Meal To Temp Plan
        //##############################################################################################################
        String uploadQuery = String.format(" INSERT INTO mealsInPlan (PlanID, Meal_Name, Meal_Time) VALUES (%s,'%s','%s')", tempPlanID, newMealName, newMealTime);

        if (!(db.uploadData_Batch_Altogether(new String[]{uploadQuery})))
        {
            JOptionPane.showMessageDialog(null, "\n\n ERROR:\n\nCreating Meal In DB!");
            return;
        }

        //##############################################################################################################
        // Get mealInPlanID
        //##############################################################################################################
        String query = String.format("Select MealInPlanID from mealsInPlan WHERE PlanID = %s AND  Meal_Name = '%s';", tempPlanID, newMealName);
        String[] results = db.getSingleColumnQuery(query);

        if (results == null)
        {
            JOptionPane.showMessageDialog(null, "\n\n ERROR:\n\nCannot Get Created Meals ID!!");

            String deleteQuery = String.format("DELETE FROM  mealsInPlan WHERE planID = %s AND  MealName = '%s';)", tempPlanID, newMealName);
            if (!(db.uploadData_Batch_Altogether(new String[]{deleteQuery})))
            {
                JOptionPane.showMessageDialog(null, "\n\n ERROR:\n\nUnable To Undo Errors Made!\n\nRecommendation Action: Refresh This Plan");
            }

            return;
        }

        mealInPlanID = Integer.valueOf(results[0]);
        savedMealTime = newMealTime;

        //##############################################################################################################
        // SetUP
        //##############################################################################################################
        setup();

        //##############################################################################################################
        // Add SubMeal To Meal
        //##############################################################################################################
        addButtonAction();

        //##############################################################################################################
        // Add SubMeal To Meal & MealManger Processes
        //##############################################################################################################
        meal_plan_screen.addMealManger(this);
        meal_plan_screen.increaseMealNo();

        //##############################################################################################################
        // Sort MealPlan GUI Out
        //##############################################################################################################
        scroll_To_The_End();
    }

    private String createTimeString()
    {
        try
        {
            String newMealTime = JOptionPane.showInputDialog("Input Meal Time etc \"09:00\"?");

            DateTimeFormatter strictTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                    .withResolverStyle(ResolverStyle.STRICT);

            LocalTime.parse(newMealTime, strictTimeFormatter);

            System.out.printf("\n\ncreateTimeString() %s:00", newMealTime);

            return newMealTime += ":00";
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, "\n\nPlease Input A Valid Time etc \"09:00\"?");
            return "void";
        }
    }

    //##################################################################################################################
    // Setup
    //##################################################################################################################
    private void setup()
    {
        ///############################
        // Objects
        ///############################
        this.db = meal_plan_screen.getDb();
        this.gbc = meal_plan_screen.getGbc();
        this.macrosLeft_JTable = meal_plan_screen.getMacrosLeft_JTable();
        this.frame = meal_plan_screen.getFrame();

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
        collapsibleJpObj = new CollapsibleJPanel(container, savedMealName, 150, 50);
        collapsibleCenterJPanel = collapsibleJpObj.getCentreJPanel();
        collapsibleCenterJPanel.setBackground(Color.YELLOW);
        addToContainer(container, collapsibleJpObj, 0, meal_plan_screen.getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);

        //##############################################################################################################
        // Icon Setup in Collapsible Object
        //##############################################################################################################
        iconSetup();

        //##############################################################################################################
        //  Total Meal Calculation JTable
        //##############################################################################################################
        String tableName = "total_meal_view";

        JPanel southPanel = collapsibleJpObj.getSouthJPanel();

        String query = String.format("SELECT *  FROM total_meal_view WHERE MealInPlanID = %s AND PlanID = %s;", mealInPlanID, tempPlanID);
        Object[][] result = db.getTableDataObject(query, tableName);

        Object[][] meal_Total_Data = result != null ? result : new Object[0][0];

        total_Meal_View_Table = new TotalMealTable(db, collapsibleJpObj, meal_Total_Data, mealTotalTable_ColumnNames, planID, tempPlanID,
                mealInPlanID, savedMealName, tableName, new ArrayList<>(Arrays.asList(mealTotalTable_ColumnNames)), null, totalMeal_Table_ColToHide);

        total_Meal_View_Table.setOpaque(true); //content panes must be opaque
        total_Meal_View_Table.setTableHeaderFont(new Font("Dialog", Font.BOLD, 12));

        //#############################################
        // TotalMealTable to Collapsible Object
        //#############################################

        // adds space between Ingredients_In_Meal_Calculation table and total_in_meal table
        addToContainer(southPanel, new JPanel(), 0, 1, 1, 1, 0.25, 0.25, "both", 50, 0, null);

        // Adding total table to CollapsibleOBJ
        addToContainer(southPanel, total_Meal_View_Table, 0, 2, 1, 1, 0.25, 0.25, "both", 0, 0, null);

        //##############################################################################################################
        // Add Initial Space Between For the First Divided Meal
        //##############################################################################################################
        addToContainer(collapsibleCenterJPanel, new JPanel(), 0, yPoInternally++, 1, 1, 0.25, 0.25, "both", 10, 0, null);

        //##############################################################################################################
        // Add Space Divider At the End Of The Meal Manager
        //##############################################################################################################
        addToContainer(container, spaceDivider, 0, meal_plan_screen.getAndIncreaseContainerYPos(), 1, 1, 0.25, 0.25, "both", 50, 0, null);
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
        // Edit Name BTN
        //##########################
        IconButton edit_Icon_Btn = new IconButton("src/main/java/images/edit/edit.png", "", iconSize, iconSize, iconSize, iconSize, "centre", "right");
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
        IconButton editTime_Icon_Btn = new IconButton("src/main/java/images/edit_Time/edit_Time.png", "", 45, 45, 45, 45, "centre", "right");
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
        IconButton add_Icon_Btn = new IconButton("src/main/java/images/add/add.png", "", iconSize, iconSize, iconSize, iconSize, "centre", "right");
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

        IconButton refresh_Icon_Btn = new IconButton("src/main/java/images/refresh/+refresh.png", "", iconSize, iconSize, iconSize, iconSize,
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

        IconButton saveIcon_Icon_Btn = new IconButton("src/main/java/images/save/save.png", "", iconSize, iconSize, iconSize, iconSize,
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

        IconButton deleteIcon_Icon_Btn = new IconButton("src/main/java/images/delete/+delete.png", "", iconSize, iconSize, iconSize + 10, iconSize,
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

        if (subMealInDB)
        {
            // Getting Ingredients In Meal
            String query = String.format("SELECT *  FROM ingredients_in_sections_of_meal_calculation WHERE DivMealSectionsID = %s AND PlanID = %s ORDER BY Ingredients_Index;", divMealSectionsID, tempPlanID);
            mealData = db.getTableDataObject(query, tableName) != null ? db.getTableDataObject(query, tableName) : mealData;
        }

        //##############################################
        // Ingredients_In_Meal_Calculation  Creation
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

    private void scroll_To_The_End()
    {
        //##############################################################################################################
        // Resize & Update Containers
        //##############################################################################################################
        meal_plan_screen.resizeGUI();

        //##############################################################################################################
        // Set Display to the bottom of the screen
        //##############################################################################################################
        meal_plan_screen.scrollBarDown_BTN_Action();
    }

    //##################################################################################################################
    // Button Actions
    //##################################################################################################################

    private Boolean areYouSure(String process)
    {
        int reply = JOptionPane.showConfirmDialog(null, String.format("Are you sure you want to %s, \nany unsaved changes will be lost in this Table! \nDo you want to %s?", process, process),
                "Restart Game", JOptionPane.YES_NO_OPTION); //HELLO Edit

        if (reply == JOptionPane.NO_OPTION || reply == JOptionPane.CLOSED_OPTION)
        {
            return false;
        }
        return true;
    }

    //#################################################################################
    // Edit BTN
    //#################################################################################
    public void edit_Name_BTN_Action()// Update method to update time aswell
    {
        //###################################
        // Update MealName
        //###################################
        String newMealName = JOptionPane.showInputDialog("Input Meal Name?");

        if (newMealName == null || newMealName.length() == 0)
        {
            JOptionPane.showMessageDialog(frame, "\n\nNew Meal Name Cannot Be Empty!!");
            return;
        }

        newMealName = StringUtils.capitalize(newMealName).trim();

        //########################################
        // Update
        //########################################
        String uploadQuery = String.format(""" 
                UPDATE mealsInPlan
                SET Meal_Name = '%s'
                WHERE PlanID = %s AND  MealInPlanID = %s;""", newMealName, tempPlanID, mealInPlanID);

        //##########################################
        // Upload Into Database Table
        //##########################################
        if (!db.uploadData(uploadQuery, false))
        {
            JOptionPane.showMessageDialog(null, "\n\nUnable to successfully change this  meals name! \n\nMaybe he selected timeframe  meal name already exists within this meal plan!!");
            return;
        }

        //##########################################
        // Change Button Text & Related Variables
        //##########################################
        collapsibleJpObj.setIconBtnText(newMealName);
        hasMealNameBeenChanged = true;
    }

    public void editTime_Btn_Action()
    {
        //###################################
        // Update MealTime
        //###################################
        String newMealTime = createTimeString();
        if (newMealTime.equals("void"))
        {
            return;
        }

        //########################################
        // Update
        //########################################
        String uploadQuery = String.format(""" 
                UPDATE mealsInPlan
                SET Meal_Time = '%s'
                WHERE PlanID = %s AND  MealInPlanID = %s; """, newMealTime, tempPlanID, mealInPlanID);

        //##########################################
        // Upload Into Database Table
        //##########################################
        if (!db.uploadData(uploadQuery, false))
        {
            JOptionPane.showMessageDialog(null, "\n\nUnable to successfully change this  meals name! \n\nMaybe he selected timeframe  meal name already exists within this meal plan!!");
            return;
        }

        //##########################################
        // Change Button Text & Related Variables
        //##########################################
        this.newMealTime = newMealTime;
        hasMealTimeBeenChanged = true;

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
        String getNextIndexQuery = "SELECT IFNULL(MAX(`DivMealSectionsID`),0) + 1 AS nextId FROM `dividedMealSections`;";

        String[] divMealSectionsIDResult = db.getSingleColumnQuery(getNextIndexQuery);

        if (divMealSectionsIDResult == null)
        {
            JOptionPane.showMessageDialog(null, "Unable to create new sub meal in table! \nUnable to generate new DivMealSectionsID !!");
            return;
        }

        Integer divMealSectionsID = Integer.valueOf(divMealSectionsIDResult[0]);

        //##########################################
        // Insert Into Database Table
        //##########################################
        String uploadQuery = String.format(" INSERT INTO dividedMealSections (DivMealSectionsID, MealInPlanID, PlanID) VALUES (%s, %s, %s)",
                divMealSectionsID, mealInPlanID, tempPlanID);

        if (!db.uploadData_Batch_Altogether(new String[]{uploadQuery}))
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
    // Delete
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
        spaceDivider.setVisible(condition);
    }

    public boolean areAllTableBeenDeleted()
    {
        //##########################################
        // IF there are no meals, delete table
        //##########################################
        for (IngredientsTable ingredientsTable : ingredientsTables)
        {
            if (!(ingredientsTable.hasIngredientsTableBeenDeleted())) // if a meal hasn't been deleted, exit method
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
        if (!(areAllTableBeenDeleted()))
        {
            return;
        }

        //##########################################
        // Delete Meal From DB
        //##########################################
        String query1 = "SET FOREIGN_KEY_CHECKS = 0;"; // Disable Foreign Key Checks
        String query2 = String.format("DELETE FROM mealsInPlan WHERE MealInPlanID = %s AND PlanID = %s;", mealInPlanID, tempPlanID);
        String query3 = "SET FOREIGN_KEY_CHECKS = 1;"; // Enable Foreign Key Checks

        if (!(db.uploadData_Batch_Altogether(new String[]{query1, query2, query3})))
        {
            JOptionPane.showMessageDialog(frame, "\n\n1.)  Error MealManager.ingredientsTableHasBeenDeleted() \nUnable to Delete Selected Meal From DB!");
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
                WHERE DivMealSectionsID IN (SELECT DivMealSectionsID FROM dividedMealSections WHERE MealInPlanID = %s AND PlanID = %s) AND PlanID = %s;""", mealInPlanID, tempPlanID, tempPlanID);

        // DELETE dividedMealSections
        String query3 = String.format("""
                DELETE FROM dividedMealSections
                WHERE MealInPlanID = %s AND PlanID = %s;""", mealInPlanID, tempPlanID);

        // DELETE mealsInPlan
        String query4 = String.format("DELETE FROM mealsInPlan WHERE MealInPlanID = %s AND PlanID = %s", mealInPlanID, tempPlanID);

        String query5 = "SET FOREIGN_KEY_CHECKS = 1;"; // Enable Foreign Key Checks

        System.out.printf("\n\n%s \n\n%s \n\n%s \n\n%s \n\n%s", query1, query2, query3, query4, query5);

        if (!db.uploadData_Batch_Altogether(new String[]{query1, query2, query3, query4, query5}))
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
        JOptionPane.showMessageDialog(null, "Table Successfully Deleted! nmn");
    }

    public void completely_Delete_MealManager()
    {
        hideMealManager();
        container.remove(collapsibleJpObj); // remove the GUI elements from GUI
        container.remove(spaceDivider);    // remove space divider from GUI
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
        String query0 = String.format("DROP TABLE IF EXISTS temp_ingredients_in_meal;");

        String query1 = String.format("DROP TABLE IF EXISTS temp_dividedMealSections;");

        //########################################################
        // Clear Old Data from toPlan and & Temp Tables
        //########################################################
        String query2 = "SET FOREIGN_KEY_CHECKS = 0;"; // Disable Foreign Key Checks

        // Delete ingredients from this meal in toPlan
        String query3 = String.format(""" 
                DELETE FROM ingredients_in_sections_of_meal
                WHERE DivMealSectionsID IN (SELECT DivMealSectionsID FROM dividedMealSections WHERE MealInPlanID = %s AND PlanID = %s) AND PlanID = %s;""", mealInPlanID, toPlanID, toPlanID);

        // Delete sub-meals from this meal in toPlan
        String query4 = String.format(""" 
                DELETE FROM dividedMealSections
                WHERE MealInPlanID = %s AND PlanID = %s;""", mealInPlanID, toPlanID);

        String query5 = "SET FOREIGN_KEY_CHECKS = 1;"; // Enable Foreign Key Checks

        //####################################################
        // Transferring this plans Ingredients to Temp-Plan
        //####################################################

        // Create Table to transfer ingredients from original plan to temp
        String query6 = String.format("""
                CREATE TABLE temp_dividedMealSections  AS
                SELECT i.*
                FROM dividedMealSections i
                WHERE i.MealInPlanID = %s AND i.PlanID = %s;
                """, mealInPlanID, fromPlanID);

        String query7 = String.format("UPDATE temp_dividedMealSections  SET PlanID = %s;", toPlanID);

        String query8 = String.format("INSERT INTO dividedMealSections SELECT * FROM temp_dividedMealSections;");
        //####################################################
        // Transferring ingredients from this meal in toPlan
        //####################################################

        // Create Table to transfer ingredients from original plan to temp
        String query9 = String.format("""
                CREATE table temp_ingredients_in_meal  AS
                SELECT i.*
                FROM ingredients_in_sections_of_meal i
                WHERE DivMealSectionsID IN (SELECT DivMealSectionsID FROM dividedMealSections WHERE MealInPlanID = %s AND PlanID = %s) AND PlanID = %s;
                """, mealInPlanID, fromPlanID, fromPlanID);

        String query10 = String.format("UPDATE temp_ingredients_in_meal  SET PlanID = %s;", toPlanID);

        String query11 = String.format("INSERT INTO ingredients_in_sections_of_meal SELECT * FROM temp_ingredients_in_meal;");

        //#####################################################
        // Meal Name & Time Updates If Changed
        //#####################################################
        String[] query_Temp_Data = new String[0];
        int changes = 0;
        String
                uploadQuery = "",
                updateMealName = savedMealName, // set mealName to refresh
                updateMealTime = savedMealTime; // set mealTime to time

        if (!hasMealNameBeenChanged && !hasMealTimeBeenChanged) //  The meal time or name doesn't need to be updated
        {
            query_Temp_Data = new String[]{query0, query1, query2, query3, query4, query5, query6, query7, query8, query9, query10, query11};
        }
        else if (hasMealTimeBeenChanged && hasMealNameBeenChanged) // Both need updating
        {
            if (process.equals("saved"))
            {
                updateMealName = collapsibleJpObj.getBtnText();
                updateMealTime = newMealTime;
            }
            // else is the default value (reset)

            uploadQuery = String.format("UPDATE mealsInPlan SET Meal_Name = '%s', Meal_Time = '%s'  WHERE PlanID = %s AND  MealInPlanID = %s;", updateMealName, updateMealTime, toPlanID, mealInPlanID);

            query_Temp_Data = new String[]{uploadQuery, query0, query1, query2, query3, query4, query5, query6, query7, query8, query9, query10, query11};
        }
        else // either one the time or name has been updated
        {
            if(hasMealNameBeenChanged) // meal name has changed
            {
                if (process.equals("saved"))
                {
                    updateMealName = collapsibleJpObj.getBtnText();
                }
                // else is the default value (reset)

                uploadQuery = String.format("UPDATE mealsInPlan SET Meal_Name = '%s' WHERE PlanID = %s AND  MealInPlanID = %s;", updateMealName, toPlanID, mealInPlanID);
            }
            else // Must mean time has changed
            {
                if (process.equals("saved"))
                {
                    updateMealTime = newMealTime;
                }
                uploadQuery = String.format("UPDATE mealsInPlan SET  Meal_Time = '%s' WHERE PlanID = %s AND  MealInPlanID = %s;", updateMealTime, toPlanID, mealInPlanID);
            }

            query_Temp_Data = new String[]{uploadQuery, query0, query1, query2, query3, query4, query5, query6, query7, query8, query9, query10, query11};
        }

        System.out.printf("\n\n Here  3 \n\n %s", uploadQuery);

        //####################################################
        // Update
        //####################################################
        if (!(db.uploadData_Batch_Altogether(query_Temp_Data)))
        {
            JOptionPane.showMessageDialog(null, "\n\ntransferMealDataToPlan() Error");
            return false;
        }

        //#############################################################################################
        //
        //##############################################################################################
        if (!hasMealNameBeenChanged) //
        {
            return true;
        }

        //##############################################################################################
        //
        //##############################################################################################
        hasMealNameBeenChanged = false; // save or refreshing result in this variable being reset to its original condition which is false

        if (process.equals("refresh")) //
        {
            collapsibleJpObj.setIconBtnText(savedMealName);
        }
        else // save
        {
            savedMealName = updateMealName;
            String.format("\n\n Here  2 %s", savedMealName);
        }

        return true;

    }

    //######################################
    // Refresh
    //######################################
    public void refresh_Btn_Action()
    {
        //#############################################################################################
        // Reset DB Data
        //##############################################################################################
        if (!(transferMealDataToPlan("refresh", planID, tempPlanID)))
        {
            JOptionPane.showMessageDialog(null, "\n\nUnable to transfer mealData toS!!");
            return;
        }

        //#############################################################################################
        // Reset IngredientsTable Data
        //##############################################################################################
        reloadingIngredientsTableDataFromRefresh(true);
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
            if (ingredientsTable.getMealInDB())
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
        if (updateMacrosLeft) // this is optional
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
        // ##############################################################################
        if (!(transferMealDataToPlan("saving", tempPlanID, planID))) // transfer meals and ingredients from temp plan to original plan
        {
            System.out.println("\n\n#################################### \nError MealManager saveMealData()");

            JOptionPane.showMessageDialog(frame, "\n\n1.)  Error \nUnable to save this Meal and its sub-meals");
            return;
        }

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
            if (table.hasIngredientsTableBeenDeleted())   // If objected is deleted, completely delete it then skip to next JTable
            {
                table.completely_Delete_IngredientsJTable();
                it.remove();
                continue;
            }

            // #####################################
            // If error occurred above exit
            // #####################################
            table.set_Meal_In_DB(true);

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
            JOptionPane.showMessageDialog(frame, "\n\n Error \n1.) Unable to updateTableModelData() all sub-meals in meal! \n\nPlease retry again!");
            return;
        }

        // ##############################################################################
        //
        // ##############################################################################
        setMealManagerInDB(true);

        // ##############################################################################
        // Save TotalMealTable
        // ##############################################################################
        total_Meal_View_Table.updateTotalMealTableModelData();

        // ##############################################################################
        // Successful Message
        // ##############################################################################
        if (showUpdateMessage)
        {
            JOptionPane.showMessageDialog(frame, "\n\nAll SubMeals Within Meal Have Successfully Saved!");
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
        total_Meal_View_Table.updateTotalMealTable();
    }

    //##################################################################################################################
    // Accessor Methods
    //##################################################################################################################
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
        return frame;
    }

    //##################################################################################################################
    // Resizing GUI
    //##################################################################################################################
    private void addToContainer(Container container, Component addToContainer, Integer gridx, Integer gridy, Integer gridwidth,
                                Integer gridheight, Double weightx, Double weighty, String fill, Integer ipady, Integer ipadx, String anchor)
    {
        if (gridx != null)
        {
            gbc.gridx = gridx;
        }
        if (gridy != null)
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
