package App_Code.Objects.Database_Objects.JTable_JDBC;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.IngredientsTable;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.Children.MacrosLeftTable;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.Children.TotalMealTable.TotalMealTable;
import App_Code.Objects.Gui_Objects.CollapsibleJPanel;
import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Gui_Objects.IconPanel;
import App_Code.Objects.Screens.Others.Meal_Plan_Screen;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class MealManager
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private boolean mealInDB = false, objectDeleted = false;
    private Integer mealInPlanID, tempPlanID, planID, yPoInternally = 0, mealNo;
    private String mealName;
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
    Meal_Plan_Screen meal_plan_screen;

    //##################################################################################################################
    //
    //##################################################################################################################
    public MealManager(Meal_Plan_Screen meal_plan_screen, Container container, boolean mealInDB, int mealInPlanID, int mealNo, String mealName, ArrayList<ArrayList<String>> subMealsInMealArrayList)
    {
        //##############################################################################################################
        // Global Variables
        //##############################################################################################################
        this.mealInPlanID = mealInPlanID;
        this.mealNo = mealNo;
        this.mealName = mealName;
        this.container = container;
        this.mealInDB = mealInDB;
        this.meal_plan_screen = meal_plan_screen;

        //##############################################################################################################
        // SetUP
        //##############################################################################################################
        setup();

        //##############################################################################################################
        // Add Sub-Meal to GUI
        //##############################################################################################################
        add_MultipleSubMealsToGUI(subMealsInMealArrayList);
    }

    //
    public MealManager(Meal_Plan_Screen meal_plan_screen, Container container)
    {
        //##############################################################################################################
        // Global Variables
        //##############################################################################################################
        this.meal_plan_screen = meal_plan_screen;
        this.container = container;
        this.mealInDB = false;
        this.mealNo = meal_plan_screen.getCurrentMealNo()+1;


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

        //##############################################################################################################
        // Does The Chosen Meal Name Exist Within Temp Plan Or Original Plan
        //##############################################################################################################

        String mealInTempPlan = String.format("""
                SELECT Meal_Name FROM mealsInPlan
                WHERE
                	Meal_Name = '%s' AND PlanID = %s
                OR
                    Meal_Name = '%s' AND PlanID = %s
                	
                LIMIT 1;""", newMealName, tempPlanID, newMealName, planID);

        if (!(db.getSingleColumnQuery(mealInTempPlan) == null))
        {
            JOptionPane.showMessageDialog(null, "\n\n ERROR:\n\nMeal Name Already Exists Within This Plan!!");
            return;
        }

        //##############################################################################################################
        // Upload Meal To Temp Plan
        //##############################################################################################################
        String uploadQuery = String.format(" INSERT INTO mealsInPlan (PlanID, Meal_Name) VALUES (%s,'%s')", tempPlanID, newMealName);

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
    }

    //##################################################################################################################
    //
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
        collapsibleJpObj = new CollapsibleJPanel(container, String.format("   Meal   %s", mealNo), 150, 50);
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

        total_Meal_View_Table = new TotalMealTable(db, collapsibleJpObj, meal_Total_Data, mealTotalTable_ColumnNames, planID,
                mealInPlanID, mealName, tableName, new ArrayList<>(Arrays.asList(mealTotalTable_ColumnNames)), null, totalMeal_Table_ColToHide);

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
        //Add BTN
        //##########################
        IconButton add_Icon_Btn = new IconButton("src/images/add/add.png", "", iconSize, iconSize, iconSize, iconSize, "centre", "right");
        // add_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));

        JButton add_Btn = add_Icon_Btn.returnJButton();
        add_Icon_Btn.makeBTntransparent();

        add_Btn.addActionListener(ae -> {
            addButtonAction();
        });

        iconPanelInsert.add(add_Icon_Btn);

        //##########################
        // Refresh Icon
        //##########################

        IconButton refresh_Icon_Btn = new IconButton("src/images/refresh/+refresh.png", "", iconSize, iconSize, iconSize, iconSize,
                "centre", "right"); // btn text is useless here , refactor

        JButton refresh_Btn = refresh_Icon_Btn.returnJButton();
        refresh_Icon_Btn.makeBTntransparent();

        refresh_Btn.addActionListener(ae -> {

            //#######################################################
            // Ask For Permission
            //#######################################################

            if (areYouSure("Refresh Data"))
            {
                refresh_Btn_Action(true);
            }
        });

        iconPanelInsert.add(refresh_Icon_Btn);

        //##########################
        // Update Icon
        //##########################

        IconButton saveIcon_Icon_Btn = new IconButton("src/images/save/save.png", "", iconSize, iconSize, iconSize, iconSize,
                "centre", "right"); // btn text is useless here , refactor
        //saveIcon_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));
        saveIcon_Icon_Btn.makeBTntransparent();

        JButton save_btn = saveIcon_Icon_Btn.returnJButton();


        save_btn.addActionListener(ae -> {
            if (areYouSure("Save Data"))
            {
                save_Btn_Action(true);
            }
        });

        iconPanelInsert.add(save_btn);

        //##########################
        // Delete Icon
        //##########################

        IconButton deleteIcon_Icon_Btn = new IconButton("src/images/delete/+delete.png", "", iconSize, iconSize, iconSize + 10, iconSize,
                "centre", "right"); // btn text is useless here , refactor
        //deleteIcon_Icon_Btn.setBorder(BorderFactory.createLineBorder(Color.black));
        deleteIcon_Icon_Btn.makeBTntransparent();

        JButton delete_btn = deleteIcon_Icon_Btn.returnJButton();


        delete_btn.addActionListener(ae -> {

            if (areYouSure("Delete"))
            {
                deleteTableAction();
            }
        });

        iconPanelInsert.add(delete_btn);
    }

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
        IngredientsTable ingredients_Calculation_JTable = new IngredientsTable(db, this, mealData, ingredientsTable_ColumnNames, planID, mealInPlanID, divMealSectionsID, subMealInDB, mealName,
                tableName, ingredientsTableUnEditableCells, ingredients_Table_Col_Avoid_Centering, ingredientsInMeal_Table_ColToHide, spaceDivider);

        ingredientsTables.add(ingredients_Calculation_JTable);

        //################################################
        // Ingredients_In_Meal_Calculation Customisation
        //################################################
        addToContainer(collapsibleCenterJPanel, ingredients_Calculation_JTable, 0, yPoInternally++, 1, 1, 0.25, 0.25, "both", 0, 0, null);
        addToContainer(collapsibleCenterJPanel, spaceDivider, 0, yPoInternally++, 1, 1, 0.25, 0.25, "both", 50, 0, null);
    }

    //##################################################################################################################
    // Button Actions
    //##################################################################################################################

    //HELLO Needs to scroll down to the bottom of the MealManager
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

    //######################################
    // Delete
    //######################################
    private void setObjectDeleted(boolean deleted)
    {
        objectDeleted = deleted;
    }

    public void deleteTableAction()
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

        setVisibility(false); // hide collapsible Object

        update_MacrosLeft_Table();// update macrosLeft table, due to number deductions from this meal

        setObjectDeleted(true); // set this object as deleted

        JOptionPane.showMessageDialog(null, "Table Successfully Deleted! nmn");

    }

    public void setVisibility(boolean condition)
    {
        collapsibleJpObj.setVisible(condition);
        spaceDivider.setVisible(condition);
    }

    public void completely_Deleted_JTables()
    {/*
        if(! objectDeleted)
        {
            return;
        }

        //#################################################
        // Remove  Main Jtable from collapsible Object
        //#################################################
        container.remove(collapsibleJpObj);
        container.remove()

        //##################################################
        // Delete Meal Total Table From Collapsible Object
        //##################################################
        if (total_Meal_Table!=null && collapsibleObj!=null)
        {
            collapsibleObj.getParentContainer().remove(spaceDivider); // remove spaceDivider from GUI

            JPanel collapsible_SouthPanel = collapsibleObj.getSouthJPanel();
            collapsible_SouthPanel.remove(total_Meal_Table);
            collapsibleObj.getSouthJPanel();
        }

        parentContainer.revalidate();

        //##################################################
        //  Notify GUI to delete collapsible Object
        //##################################################
        if (collapsibleObj!=null)
        {
            collapsibleObj.removeCollapsibleJPanel(); // notifies GUI to delete collapsible Object
        }*/
    }

    //#################################################################################
    // Save & Refresh
    //#################################################################################
    public boolean transferMealDataToPlan(String process, int fromPlanID, int toPlanID)
    {
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
                CREATE table temp_dividedMealSections  AS
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

        //####################################################
        // Update
        //####################################################
        String[] query_Temp_Data = new String[]{query0, query1, query2, query3, query4, query5, query6, query7, query8, query9, query10, query11};

        if (!(db.uploadData_Batch_Altogether(query_Temp_Data)))
        {
            JOptionPane.showMessageDialog(null, "\n\ntransferMealDataToPlan() Error");
            return false;
        }

        return true;
    }

    //######################################
    // Refresh
    //######################################
    public void refresh_Btn_Action(boolean refreshMacrosLeft)
    {
        //#############################################################################################
        // Reset DB Data
        //##############################################################################################
        if (!(transferMealDataToPlan("Refresh", planID, tempPlanID)))
        {
            JOptionPane.showMessageDialog(null, "\n\nUnable to transfer mealData toS!!");
            return;
        }

        //##############################################################################################
        // Refresh ingredients meal table & total Tables Data
        //##############################################################################################
        Iterator<IngredientsTable> it = ingredientsTables.iterator();
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
            ingredientsTable.reloadingDataFromRefresh(false, false);
        }

        //#############################################################################################
        // Refresh Other Tables
        //##############################################################################################

        // Refresh TotalMeal
        update_TotalMeal_Table();

        // Refresh MacrosLeft
        if (refreshMacrosLeft)
        {
            update_MacrosLeft_Table();
        }
    }

    //######################################
    // Save
    //######################################
    public void save_Btn_Action(boolean showUpdateMessage)
    {
        // ###############################################################################
        // Removing Sub-Meals that have been deleted
        // ##############################################################################
        Iterator<IngredientsTable> it = ingredientsTables.iterator();

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

        // ###############################################################################
        // Transferring Meals & Ingredients from FromPlan to toPlan
        // ##############################################################################
        if (!(transferMealDataToPlan("Saving", tempPlanID, planID))) // transfer meals and ingredients from temp plan to original plan
        {
            System.out.println("\n\n#################################### \nError MealManager saveMealData()");

            JOptionPane.showMessageDialog(frame, "\n\n1.)  Error \nUnable to save this Meal and its sub-meals");
            return;
        }

        // ##############################################################################
        // Save IngredientsTable Data
        // ##############################################################################
        int errorCount = 0;
        Iterator<IngredientsTable> it2 = ingredientsTables.iterator();
        while (it2.hasNext())
        {
            IngredientsTable table = it2.next();

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
            JOptionPane.showMessageDialog(frame, "\n\n Error \n2.) Unable to updateTableModelData() all sub-meals in meal! \n\nPlease retry again!");
            return;
        }

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
    //
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

                case "end":
                    gbc.anchor = GridBagConstraints.PAGE_END;
                    break;
            }
        }

        container.add(addToContainer, gbc);
    }
}
