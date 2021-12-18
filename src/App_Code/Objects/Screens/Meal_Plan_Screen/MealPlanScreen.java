package App_Code.Objects.Screens.Meal_Plan_Screen;


import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.MyJTable_JDBC.Ingredients_In_Meal_Table.MyJTable_JDBC5;

import App_Code.Objects.Gui_Objects.CollapsibleJPanel;
import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Gui_Objects.IconPanel;
import App_Code.Objects.Gui_Objects.ScrollPaneCreator;
import App_Code.Objects.Screens.Add_Ingredients_Screen.Add_Ingredients_Screen;
import App_Code.Objects.Screens.MacrosTargets_Screen.macrosTargets_Screen;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class MealPlanScreen extends JPanel
{
    private String name = "MyJTable_JDBC511_05";
    //########################################################
    // Objects
    //########################################################
    private GridBagConstraints gbc = new GridBagConstraints();
    private JFrame frame = new JFrame(name);
    private JPanel scrollPaneJPanel;
    private Container contentPane;

    private MyJDBC db;
    private App_Code.Objects.Screens.MacrosTargets_Screen.macrosTargets_Screen macrosTargets_Screen = null;
    private Add_Ingredients_Screen Add_Ingredients_Screen = null;

    private MyJTable_JDBC5 macrosLeft_JTable, macros_Targets_Table;
    private MyJTable_JDBC5 jTableBeingAdded;
    private ArrayList<MyJTable_JDBC5> listOfJTables = new ArrayList<>();

    String databaseName = "gymapp3";

    //########################################################
    // Variables
    //########################################################
    private ArrayList<String> ingredientsInDB;
    private String[] meal_total_columnNames, ingredients_ColumnNames;

    private String planName;
    private Integer
            tempPlanID = 1, planID;
    private int
            pos = 0, mealNo = 0, frameHeight = 1400, frameWidth = 2000;

    private boolean macroTargetsChanged = false;

    //########################################################
    // Ingredients Table Columns
    //########################################################

    private int ingredientsTable_Quantity_Col = 4, ingredientsTable_IngredientsName_Col = 5,
            ingredientsTable_Supplier_Col = 7;

    private Integer ingredientsTable_DeleteBTN_Col = 17;

    //########################################################
    // Table Customisations
    //########################################################
    private final ArrayList<Integer>
            TotalMeal_Table_Hidden_Columns = new ArrayList<Integer>(Arrays.asList(1, 2)),
            ingredientsTable_Hidden_Columns = new ArrayList<Integer>(Arrays.asList(1, 2)),
            macros_Targets_Table_Hidden_Col = new ArrayList<Integer>(Arrays.asList(1, 2)),
            macrosLeft_Table_Hidden_Col = new ArrayList<Integer>(Arrays.asList(1, 2));

    private final ArrayList<Integer>
            editable_IngredientsTable_Columns = new ArrayList<Integer>(Arrays.asList(ingredientsTable_Quantity_Col, ingredientsTable_IngredientsName_Col, ingredientsTable_Supplier_Col)),
            ingredients_Table_Col_Avoid_Centering = new ArrayList<>(Arrays.asList(ingredientsTable_IngredientsName_Col, ingredientsTable_Supplier_Col, ingredientsTable_DeleteBTN_Col));


    private final Integer ingredientsTable_StartingCol = 2, totalMealTable_StartCol = 3,
            macroTargets_StartCol = 2, totalPlanTable_StartCol = 2, macrosLeftTable_StartCol = 2;
    //########################################################

    public MealPlanScreen()
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

        LinkedHashMap<String, String> tableInitilization = new LinkedHashMap<>();
        tableInitilization.put("data", sql3);

        db = new MyJDBC("root", "password", databaseName, tableInitilization);


        if (!(db.isDatabaseConnected()))
        {
            JOptionPane.showMessageDialog(null, "ERROR, Cannot Connect To Database!");
            return;
        }

        //#############################################################################################################
        //   1. Create the frame.
        //#############################################################################################################

        // Container (ContentPane)
        contentPane = frame.getContentPane();
        contentPane.setLayout(new GridBagLayout());
        contentPane.setVisible(true);

        //##########################################################
        //   Create Interface
        //#########################################################
        JPanel screenSectioned = new JPanel(new BorderLayout());

        JPanel mainCenterPanel = new JPanel(new GridBagLayout());
        JPanel mainNorthPanel = new JPanel(new GridBagLayout());
        JPanel mainSouthPanel = new JPanel(new GridBagLayout());

        //###########################################
        // Adding different section of interface to

        screenSectioned.add(mainNorthPanel, BorderLayout.NORTH);
        screenSectioned.add(mainCenterPanel, BorderLayout.CENTER);
        screenSectioned.add(mainSouthPanel, BorderLayout.SOUTH);

        iconSetup(mainNorthPanel);

        addToContainer(contentPane, screenSectioned, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0);

        //##########################################################
        // Create ScrollPane & add to Interface
        //#########################################################
        ScrollPaneCreator scrollPane = new ScrollPaneCreator();
        scrollPaneJPanel = scrollPane.getJPanel();
        scrollPaneJPanel.setLayout(new GridBagLayout());

        addToContainer(mainCenterPanel, scrollPane, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0);


        //##############################################################################################################
        // Getting selected plan ID
        //##############################################################################################################
        String[] results = db.getSingleColumnQuery("SELECT PlanID FROM plans WHERE SelectedPlan = 1;");
        planID = results!=null ? Integer.parseInt(results[0]):null;


        //HELLO REMOVE
        System.out.printf("\n\nChosen Plan: %s", planID);
        System.out.println("\n\n###############################################################################");


        if (planID!=null)
        {
            //####################################################
            // Transferring Targets From Chosen PLan to Temp
            //####################################################
            if(!transferTargets(planID, tempPlanID, false))
            {
                return;
            }

            //####################################################
            // Update Temp PlanName Based On Selected PlanName
            //####################################################

            String query0 = String.format("""
                    UPDATE `plans` AS `P`,
                    (
                    	SELECT Plan_Name, Vegan FROM Plans WHERE PlanID = %s
                    ) AS `SRC`
                                        
                    SET
                        `P`.`Plan_Name` = concat("(Temp) ",`SRC`.`Plan_Name`),`P`.`Vegan` = `SRC`.`Vegan`
                    WHERE
                        `P`.`PlanID` = %s; """, planID, tempPlanID);

            //####################################################
            // Transferring this plans Meals  Info to Temp-Plan
            //####################################################

            // Delete Old Temp Data
            String query1 = String.format("DELETE FROM ingredients_in_meal  WHERE PlanID = %s;", tempPlanID);
            String query2 = String.format("DELETE FROM meals  WHERE PlanID = %s;", tempPlanID);


            // Create table to transfer meals from original plan to temp
            String query3 = String.format("DROP TABLE IF EXISTS temp_meal;");
            String query4 = String.format("CREATE table temp_meal AS SELECT * FROM meals WHERE PlanID = %s ORDER BY MealID;", planID);

            String query5 = String.format("ALTER TABLE temp_meal MODIFY mealID INT;");
            String query6 = String.format("UPDATE temp_meal SET MealID = NULL;");
            String query7 = String.format("UPDATE temp_meal SET PlanID = %s;", tempPlanID);
            String query8 = String.format("INSERT INTO meals SELECT * FROM temp_meal;");
            String query9 = String.format("DROP TABLE temp_meal;");

            //####################################################
            // Transferring this plans Ingredients to Temp-Plan
            //####################################################

            // Delete tables if they already exist
            String query10 = String.format("DROP TABLE IF EXISTS temp_ingredients_in_meal;");
            String query11 = String.format("DROP TABLE IF EXISTS temp;");

            // Create Table to transfer ingredients from original plan to temp
            String query12 = String.format(""" 
                                        
                    CREATE table temp_ingredients_in_meal  AS
                    SELECT i.*, m.Meal_name
                    FROM ingredients_in_meal i, meals m                                                        
                    WHERE i.PlanID= %s AND i.mealID = m.mealID;
                     
                     """, planID);

            String query13 = String.format("ALTER TABLE temp_ingredients_in_meal  DROP COLUMN mealID;");
            String query14 = String.format("UPDATE temp_ingredients_in_meal  SET PlanID = %s;", tempPlanID);
            String query15 = String.format("""
                    CREATE table temp AS
                                         
                    SELECT * FROM  temp_ingredients_in_meal temp
                    INNER JOIN
                        (
                          SELECT Meal_Name AS Meal_Name2, MEALID 
                    	  FROM meals
                    	  WHERE PlanID = %s
                    	  ORDER BY MEALID
                    	) as M
                    ON
                        temp.Meal_Name = M.Meal_Name2;
                                         
                     """, tempPlanID);

            String query16 = String.format(" ALTER TABLE temp DROP COLUMN Meal_Name, DROP COLUMN Meal_Name2;");
            String query17 = String.format("ALTER TABLE temp MODIFY MEALID INT AFTER Ingredients_Index;");

            String query18 = String.format("INSERT INTO ingredients_in_meal SELECT * FROM temp;");

            String[] query_Temp_Data = new String[]{ query0, query1, query2, query3, query4, query5, query6, query7, query8, query9, query10, query11, query12,
                    query13, query14, query15, query16, query17, query18, query10, query11};

            if (!(db.uploadData_Batch(query_Temp_Data)))
            {
                JOptionPane.showMessageDialog(null, "\n\nCannot Create Temporary Plan In DB to Allow Editing");
                return;
            }

            //##############################
            // Getting PlanName Results
            //##############################

            String[] planNameResults = db.getSingleColumnQuery("SELECT Plan_Name FROM plans WHERE planID = 1;");
            planName = planNameResults!=null ? planNameResults[0]:null;

            System.out.printf("\n\nChosen Plan Name: %s", planName);
            System.out.println("\n\n###############################################################################");

            //#############################################################################################
            // Table Setup
            //###########################################################################################

            //########################################
            // Getting ID's of Meals Of Chosen Plan
            //########################################
            String query = String.format("SELECT MealID, Meal_Name FROM meals WHERE PlanID = %s ORDER BY MealID;", planID);

            ArrayList<ArrayList<String>> plan_Meal_IDs_And_Name = db.getMultiColumnQuery(query);
            plan_Meal_IDs_And_Name = plan_Meal_IDs_And_Name!=null ? plan_Meal_IDs_And_Name:new ArrayList<>();

            int no_of_meals = plan_Meal_IDs_And_Name.size();

            //#################################################
            // Getting ID's Of Meals in Database In Temp Plan
            //##################################################
            String queryGetTempMealsID = String.format("SELECT MealID FROM meals WHERE PlanID = %s ORDER BY MealID;", tempPlanID);

            ArrayList<String> temp_Plan_meal_IDs = db.getSingleColumnQuery_ArrayList(queryGetTempMealsID);
            temp_Plan_meal_IDs = temp_Plan_meal_IDs!=null ? temp_Plan_meal_IDs:new ArrayList<>();


            //######################################################################
            // Macro Targets Setup
            //######################################################################
            String tableName = "plan_Macro_Target_Calculations";
            String planCalcQuery = String.format("SELECT * from %s  WHERE PlanID = %s;", tableName, tempPlanID);

            Object[][] planData = db.getTableDataObject(planCalcQuery, tableName)!=null ? db.getTableDataObject(planCalcQuery, tableName):new Object[0][0];
            String[] plan_columnNames = db.getColumnNames(tableName)!=null ? db.getColumnNames(tableName):new String[0];

            ArrayList<Integer> unEditableCells = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11));
            ArrayList<Integer> ingredients_Table_Col_Avoid_Centering = new ArrayList<>(Arrays.asList());

            macros_Targets_Table = new MyJTable_JDBC5(db, mainSouthPanel, planData, plan_columnNames, planID,
                    tableName, unEditableCells, ingredients_Table_Col_Avoid_Centering);

            macros_Targets_Table.setOpaque(true); //content panes must be opaque
            macros_Targets_Table.SetUp_HiddenTableColumns(macros_Targets_Table_Hidden_Col, macroTargets_StartCol);

            macros_Targets_Table.setTableHeaderFont(new Font("Dialog", Font.BOLD, 14));
            macros_Targets_Table.setTableTextFont(new Font("Dialog", Font.PLAIN, 14));

            addToContainer(mainSouthPanel, macros_Targets_Table, 0, 1, 1, 1, 0.25, 0.25, "both", 40, 0);
            resizeGUi();

            //######################################################################
            // planMacrosLeft Table Setup
            //######################################################################
            tableName = "planMacrosLeft";
            String macrosQuery = String.format("SELECT * from %s  WHERE PlanID = %s;", tableName, tempPlanID);

            Object[][] macrosData = db.getTableDataObject(macrosQuery, tableName)!=null ? db.getTableDataObject(macrosQuery, tableName):new Object[0][0];
            String[] macros_columnNames = db.getColumnNames(tableName)!=null ? db.getColumnNames(tableName):new String[0];

            unEditableCells = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11));
            ArrayList<Integer> macros_Table_Col_Avoid_Centering = new ArrayList<>(Arrays.asList());

            macrosLeft_JTable = new MyJTable_JDBC5(db, mainSouthPanel, macrosData, macros_columnNames, planID,
                    tableName, unEditableCells, macros_Table_Col_Avoid_Centering);

            macrosLeft_JTable.setOpaque(true); //content panes must be opaque

            macrosLeft_JTable.SetUp_HiddenTableColumns(macrosLeft_Table_Hidden_Col, macrosLeftTable_StartCol);

            macrosLeft_JTable.setTableHeaderFont(new Font("Dialog", Font.BOLD, 14));
            macrosLeft_JTable.setTableTextFont(new Font("Dialog", Font.PLAIN, 14));

            addToContainer(mainSouthPanel, macrosLeft_JTable, 0, 2, 1, 1, 0.25, 0.25, "both", 40, 0);
            resizeGUi();


            //######################################################################
            // Ingredients In Meal Calculation && Total_Meal_View  JTable Setup
            //######################################################################

            ///###################################
            // Table Variables
            //###################################

            tableName = "total_meal_view";
            meal_total_columnNames = db.getColumnNames(tableName)!=null ? db.getColumnNames(tableName):new String[0];

            tableName = "ingredients_in_meal_calculation";
            ingredients_ColumnNames = db.getColumnNames(tableName)!=null ? db.getColumnNames(tableName):new String[0];

            ingredientsInDB = db.getSingleColumnQuery_ArrayList("Select ingredient_Name from ingredients_info;");
            ingredientsInDB = ingredientsInDB!=null ? ingredientsInDB:new ArrayList<String>();

            for (int i = 0; i < no_of_meals; i++)
            {
                int mealID = Integer.parseInt(plan_Meal_IDs_And_Name.get(i).get(0)); // MealID's From Original Plan Not Temp
                int temp_MealID = Integer.parseInt(temp_Plan_meal_IDs.get(i));

                String mealName = plan_Meal_IDs_And_Name.get(i).get(1);

                mealNo++;

                //#########################################
                // Create Collapsible Panel
                //#########################################
                CollapsibleJPanel collapsibleJTable = create_CollapsibleJPanel(true, scrollPaneJPanel, mealID, temp_MealID, mealName, mealNo, meal_total_columnNames, ingredients_ColumnNames,
                        ingredientsInDB,  macrosLeft_JTable);

                //##################################################################################################
                // Adding Collapsible Objects && JTables To GUI
                //##################################################################################################

                // adding  CollapsibleOBJ to interface
                addToContainer(scrollPaneJPanel, collapsibleJTable, 0, pos++, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

                //Space Divider between each CollapsibleObj
                JPanel spaceDivider = new JPanel();
                // spaceDivider.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
                jTableBeingAdded.setSpaceDivider(spaceDivider);
                addToContainer(scrollPaneJPanel, spaceDivider, 0, pos++, 1, 1, 0.25, 0.25, "both", 50, 0);

                //##################################################################################################
                // Resize & Update Containers
                //##################################################################################################
                resizeGUi();
            }
        }
        else
        {
            JOptionPane.showMessageDialog(null, "No Chosen Plan");
        }

        //#########################################
        //   Define Frame Properties
        //#########################################

        frame.setVisible(true);
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
                saveMacroTargets(true);
                if (macrosTargets_Screen!=null)
                {
                    macrosTargets_Screen.closeeWindow();
                }
            }
        });

        //HELLO REMOVE
        open_AddIngredients_Screen();
    }

    public static void main(String[] args)
    {
        new MealPlanScreen();
    }

    private CollapsibleJPanel create_CollapsibleJPanel(boolean mealInDB, Container container, Integer mealID, Integer temp_MealID, String mealName, int mealNo, String[] meal_total_columnNames,
                                                       String[] ingredients_ColumnNames, ArrayList<String> ingredientsInDB, MyJTable_JDBC5 macrosLeft_JTable)
    {
        CollapsibleJPanel collapsibleJpObj = new CollapsibleJPanel(container, String.format("   Meal   %s", mealNo), 150, 50);
        JPanel collapsibleJPanel = collapsibleJpObj.getCentreJPanel();
        collapsibleJPanel.setBackground(Color.YELLOW);

        //########################################################################
        //  Total Meal Calculation JTable
        //########################################################################
        String tableName = "total_meal_view";

        JPanel southPanel = collapsibleJpObj.getSouthJPanel();

        String query = String.format("SELECT *  FROM total_meal_view WHERE MealID = %s ", temp_MealID);

        Object[][] meal_Total_Data = db.getTableDataObject(query, tableName)!=null ? db.getTableDataObject(query, tableName):new Object[0][0];

        int columnsInTotalTable = meal_total_columnNames.length;
        ArrayList<Integer> unEditableCells = new ArrayList<Integer>(columnsInTotalTable);
        for (int i = 0; i < columnsInTotalTable; i++)
        {
            unEditableCells.add(i);
        }

        MyJTable_JDBC5 total_Meal_View_Jtable = new MyJTable_JDBC5(db, collapsibleJpObj, databaseName, meal_Total_Data, meal_total_columnNames, planID, mealID, temp_MealID,
                mealName, tableName, unEditableCells, null, false);

        total_Meal_View_Jtable.setOpaque(true); //content panes must be opaque
        total_Meal_View_Jtable.SetUp_HiddenTableColumns(TotalMeal_Table_Hidden_Columns, totalMealTable_StartCol);
        total_Meal_View_Jtable.setTableHeaderFont(new Font("Dialog", Font.BOLD, 12));

        //########################################################################
        //  Ingredients_In_Meal_Calculation JTable
        //########################################################################
        int columnsInIngredientsTable = ingredients_ColumnNames.length;
        int ingredientsNotEditableColSize = columnsInIngredientsTable - editable_IngredientsTable_Columns.size();

        ingredientsNotEditableColSize = ingredientsTable_DeleteBTN_Col==null ? ingredientsNotEditableColSize:ingredientsNotEditableColSize - 1;

        unEditableCells = new ArrayList<Integer>(ingredientsNotEditableColSize);

        for (int i = 0; i < columnsInIngredientsTable; i++)
        {
            if (editable_IngredientsTable_Columns.contains(i))
            {
                continue;
            }
            else if (i==ingredientsTable_DeleteBTN_Col)
            {
                continue;
            }
            unEditableCells.add(i);
        }

        //###########################################
        // Getting Ingredients In Meal
        //###########################################
        query = String.format("SELECT *  FROM ingredients_in_meal_calculation WHERE MealID = %s ORDER BY Ingredients_Index;", temp_MealID);
        tableName = "ingredients_in_meal_calculation";
        Object[][] mealData = db.getTableDataObject(query, tableName)!=null ? db.getTableDataObject(query, tableName):new Object[0][0];

        //##############################################
        // Ingredients_In_Meal_Calculation  Creation
        //##############################################

        MyJTable_JDBC5 ingredients_Calulation_Jtable = new MyJTable_JDBC5(db, collapsibleJpObj, databaseName, mealData, ingredients_ColumnNames, planID, mealID, temp_MealID, mealName,
                tableName, null, unEditableCells, ingredients_Table_Col_Avoid_Centering, true,
                total_Meal_View_Jtable, macrosLeft_JTable);

        ingredients_Calulation_Jtable.setOpaque(true); //content panes must be opaque
        jTableBeingAdded = ingredients_Calulation_Jtable;

        // add ingredients Jtable to list
        listOfJTables.add(ingredients_Calulation_Jtable);

        //##############################################
        // Ingredients_In_Meal_Calculation
        // Customisation
        //#############################################
        ingredients_Calulation_Jtable.setTableHeaderFont(new Font("Dialog", Font.BOLD, 12));

        ingredients_Calulation_Jtable.SetUp_HiddenTableColumns(ingredientsTable_Hidden_Columns, ingredientsTable_StartingCol);
        ingredients_Calulation_Jtable.set_TriggerColumns(new Integer[]{0, 1, 2, 3, 5});

        ingredients_Calulation_Jtable.setUpJComboColumn(3, "IngredientName", ingredientsInDB);
        ingredients_Calulation_Jtable.setUpSupplierColumn(5);
        ingredients_Calulation_Jtable.setupDeleteBtnColumn(15);

        //##################################################################################################
        // Adding Collapsible Objects && JTables To GUI
        //##################################################################################################

        // addding Ingredients_In_Meal_Calculation to CollapsibleOBJ
        addToContainer(collapsibleJPanel, ingredients_Calulation_Jtable, 0, 1, 1, 1, 0.25, 0.25, "both", 0, 0);

        // adds space between Ingredients_In_Meal_Calculation table and total_in_meal table
        addToContainer(southPanel, new JPanel(), 0, 1, 1, 1, 0.25, 0.25, "both", 50, 0);

        // Adding total table to CollapsibleOBJ
        addToContainer(southPanel, total_Meal_View_Jtable, 0, 2, 1, 1, 0.25, 0.25, "both", 0, 0);

        return collapsibleJpObj;
    }

    private void resizeGUi()
    {
        scrollPaneJPanel.revalidate();
        contentPane.revalidate();
    }

    private void addToContainer(Container container, Component addToContainer, int gridx, int gridy, int gridwidth,
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


    //################################################################################################################
    //  Icon Methods & ActionListener Events
    //################################################################################################################
    protected Boolean areYouSure(String process)
    {
        int reply = JOptionPane.showConfirmDialog(frame, String.format("Are you sure you want to %s, \nany unsaved changes will be lost in this Table! \nDo you want to %s?", process, process),
                "Restart Game", JOptionPane.YES_NO_OPTION); //HELLO Edit

        if (reply==JOptionPane.NO_OPTION || reply==JOptionPane.CLOSED_OPTION)
        {
            return false;
        }
        return true;
    }

    private void iconSetup(Container mainNorthPanel)
    {
        //##############################################################################################################
        // Top Bar Icon AREA
        //##############################################################################################################
        //Creating JPanels for the area

        IconPanel iconPanel = new IconPanel(5, 10, "East");
        JPanel iconPanelInsert = iconPanel.getIconJpanel();

        addToContainer(mainNorthPanel, iconPanel.getIconAreaPanel(), 0, 0, 1, 1, 0.25, 0.25, "horizontal", 10, 0);


        //##########################
        // Refresh Icon
        //##########################
        int width = 50;
        int height = 50;

        IconButton refresh_Icon_Btn = new IconButton("src/images/refresh/++refresh.png", "", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor

        JButton refresh_Btn = refresh_Icon_Btn.returnJButton();
        refresh_Icon_Btn.makeBTntransparent();

        refresh_Btn.addActionListener(ae -> {
            refreshPlan();
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

            savePlanData();
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

            open_MacrosTagets_Screen();
        });

        iconPanelInsert.add(macro_Tagets_Btn);
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

        if (newMealName.length()==0)
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

        if (!(db.uploadData_Batch(new String[]{uploadQuery})))
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
            if (!(db.uploadData_Batch(new String[]{deleteQuery})))
            {
                JOptionPane.showMessageDialog(null, "\n\n ERROR:\n\nUnable To Undo Errors Made!\n\nRecommendation Action: Refresh This Plan");
            }

            return;
        }

        Integer tempMealID = Integer.valueOf(results[0]);

        System.out.printf("\n\nTemp Meal ID: %s", tempMealID);

        //#############################################
        // Create New Meal Table In GUI
        //##############################################

        CollapsibleJPanel collapsibleJTable = create_CollapsibleJPanel(false, scrollPaneJPanel, null, tempMealID, newMealName, mealNo, meal_total_columnNames, ingredients_ColumnNames,
                ingredientsInDB,  macrosLeft_JTable);


        //###############################################
        // Adding Collapsible Objects && JTables To GUI
        //################################################


        // adding  CollapsibleOBJ to interface
        addToContainer(scrollPaneJPanel, collapsibleJTable, 0, pos++, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

        //Space Divider between each CollapsibleObj
        JPanel spaceDivider = new JPanel();
        //spaceDivider.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
        jTableBeingAdded.setSpaceDivider(spaceDivider);
        addToContainer(scrollPaneJPanel, spaceDivider, 0, pos++, 1, 1, 0.25, 0.25, "both", 50, 0);

        //##############################################
        // Updating Totals Plan Table
        //##############################################

        JOptionPane.showMessageDialog(null, "\n\n Meal Successfully Created!!");

        //##############################################
        // Resize & Update Containers
        //##############################################
        resizeGUi();
    }

    private void refreshPlan()
    {
        if (!(get_IsPlanSelected()))
        {
            return;
        }

        if (!(areYouSure("Refresh Data")))
        {
            return;
        }

        //###############################################
        // Refresh Macro-Targets Table
        //###############################################
        refreshMacroTargets();

        //###############################################
        // Refresh ingredients meal table & total Tables
        //###############################################
        Iterator<MyJTable_JDBC5> it = listOfJTables.iterator();
        while (it.hasNext())
        {
            MyJTable_JDBC5 ingredientsJtable = it.next();

            // if meal is not saved in DB remove the meal
            if (!(ingredientsJtable.getMealInDB()))
            {
                ingredientsJtable.deleteTableAction(); // delete table from db
                it.remove(); // remove from list
                continue;
            }
            if (!(it.hasNext()))
            {
                ingredientsJtable.outside_Update_MacrosLeft_Table();
                continue;
            }

            ingredientsJtable.refresh_Btn_Action(false);
        }
    }

    private void savePlanData()
    {
        if (!(get_IsPlanSelected()))
        {
            return;
        }

        // ##############################################
        // If targets have changed, save them?
        // ##############################################
        saveMacroTargets(true);
        // ##############################################

        ArrayList<Integer> mealsInPlanList = new ArrayList<>();

        //######################################
        /**
         * Delete all from memory (list) that are
         * self-recorded as deleted.
         *
         * And Collect All the Names of the meals
         * that are still in the database
         */
        //######################################
        Iterator<MyJTable_JDBC5> it = listOfJTables.iterator();
        while (it.hasNext())
        {
            MyJTable_JDBC5 table = it.next();
            if (table.getObjectDeleted())
            {
                table.completely_Deleted_JTables();
                it.remove();
                continue;
            }
            table.saveDataAction();

            mealsInPlanList.add(table.getTempMealID());

            // If Meal is not in DB,  it doesn't have a mealID so do not retrieve it as you cannot delete with an ID of NULL
            if (table.getMealInDB())
            {
                mealsInPlanList.add(table.getMealID());
            }
        }

        //######################################
        // Creating SQL Statements For Delete
        //######################################
        String exceptConditions = "(";
        String condition = "MealID";

        int size = mealsInPlanList.size();
        // delete from mysql.user where (user!='1' AND user!='2');
        for (int i = 0; i < size; i++)
        {
            if (i > 0)
            {
                exceptConditions += String.format(" AND ");
            }
            exceptConditions += String.format("%s!='%s'", condition, mealsInPlanList.get(i));
            if (i==size - 1)
            {
                exceptConditions += ")";
            }
        }

        String deleteIngredients = String.format("DELETE FROM  ingredients_in_meal WHERE %s;", exceptConditions);
        String deleteMeals = String.format("DELETE FROM  meals WHERE %s", exceptConditions);

        //##########################################################
        // If Delete Statements Successful
        //###########################################################

        if (!(db.uploadData_Batch(new String[]{deleteIngredients, deleteMeals})))
        {
            JOptionPane.showMessageDialog(frame, "Unable Save Plan To Database");
            return;
        }

        JOptionPane.showMessageDialog(frame, "Meals Successful Saved!!");
    }

    //#####################################
    //  Add Ingredients  Screen Methods
    //#####################################

    public void open_AddIngredients_Screen()
    {
        if (!(get_IsPlanSelected()))
        {
            return;
        }

        if (Add_Ingredients_Screen!=null)
        {
            Add_Ingredients_Screen.makeJframeVisible();
            return;
        }
        Add_Ingredients_Screen = new Add_Ingredients_Screen(db, this, planID, tempPlanID, planName);
    }

    public void remove_addIngredients_Screen()
    {
        Add_Ingredients_Screen = null;
    }

    //#####################################
    //  Macro Targets  Screen Methods
    //#####################################
    public  void saveMacroTargets(boolean showUpdateMsg)
    {
        // ##############################################
        // If targets have changed, save them?
        // ##############################################
        if(getMacrosTargetsChanged())
        {
            int reply = JOptionPane.showConfirmDialog(frame, String.format("Would you like to save your MacroTarget  Changes Too?"),
                    "Save Macro Targets", JOptionPane.YES_NO_OPTION); //HELLO Edit

            if (reply==JOptionPane.NO_OPTION || reply==JOptionPane.CLOSED_OPTION)
            {
                if(transferTargets(planID, tempPlanID, showUpdateMsg))
                {
                    macrosTargetsChanged(false);
                    updateTargetsAndMacrosLeft();
                    return;
                }
            }
            else
            {
                if(transferTargets(tempPlanID, planID, showUpdateMsg))
                {
                    macrosTargetsChanged(false);
                    updateTargetsAndMacrosLeft();
                    return;
                }
            }
        }
    }

    public  void refreshMacroTargets()
    {
        // ##############################################
        // If targets have changed, save them?
        // ##############################################
        if(getMacrosTargetsChanged())
        {
            int reply = JOptionPane.showConfirmDialog(frame, String.format("Would you like to refresh your MacroTargets Too?"),
                    "Refresh Macro Targets", JOptionPane.YES_NO_OPTION); //HELLO Edit

            if (reply==JOptionPane.YES_OPTION)
            {
                if(transferTargets(planID, tempPlanID, false))
                {
                    JOptionPane.showMessageDialog(frame, "\n\nMacro-Targets Successfully Refreshed!!");
                    macrosTargetsChanged(false);
                    updateTargetsAndMacrosLeft();
                    return;
                }
            }
        }
    }

    public void open_MacrosTagets_Screen()
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
        macrosTargets_Screen = new macrosTargets_Screen(db, this, planID, tempPlanID, planName);
    }

    public void remove_macrosTargets_Screen()
    {
        macrosTargets_Screen = null;
    }

    public void updateTargetsAndMacrosLeft()
    {
        macros_Targets_Table.internal_Update_MacroTargets();
        macrosLeft_JTable.internal_Update_MacrosLeft_Table();
    }

    public boolean transferTargets(int fromPlan, int toPlan, boolean  showConfirmMsg)
    {
        String query000 = String.format("DELETE FROM macros_Per_Pound_And_Limits WHERE PLANID =%s;", toPlan);
        String query01 = String.format("DROP TABLE IF EXISTS temp_Macros;");
        String query02 = String.format(" CREATE table temp_Macros AS SELECT * FROM macros_Per_Pound_And_Limits WHERE PLANID = %s;", fromPlan);
        String query03 = String.format(" ALTER TABLE temp_Macros DROP COLUMN current_Weight_In_Pounds;");
        String query04 = String.format("UPDATE temp_Macros SET PlanID = %s;", toPlan);
        String query05 = String.format("""
                     INSERT INTO macros_Per_Pound_And_Limits
                    (planID, current_Weight_KG, BodyFatPercentage, Protein_PerPound, Carbohydrates_PerPound, Fibre, Fats_PerPound, Saturated_Fat_Limit,
                    Salt_LIMIT, Water_Target, Additional_Calories)
                                        
                    SELECT * FROM temp_Macros;""");

        String query06 = String.format(" DROP TABLE IF EXISTS temp_Macros;");

        if (!(db.uploadData_Batch( new String[]{query000, query01, query02, query03, query04, query05, query06})))
        {
            JOptionPane.showMessageDialog(null, "\n\nCannot Transfer Targets");
            return false;
        }
        else if(showConfirmMsg)
        {
            JOptionPane.showMessageDialog(null, "\n\nTargets Successfully Saved");
        }
        return true;
    }

    //################################################################################################################
    //  Mutator Methods
    //################################################################################################################

    public void macrosTargetsChanged(boolean bool)
    {
        macroTargetsChanged = bool;
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
}
