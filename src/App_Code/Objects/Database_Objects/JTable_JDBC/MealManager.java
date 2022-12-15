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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.TreeMap;

public class MealManager
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################

    private Integer   mealInPlanID, tempPlanID, planID, yPoInternally = 0, containerYPos;
    private String mealName, databaseName;
    private String[] mealTotalTable_ColumnNames, ingredientsTable_ColumnNames;
    private ArrayList<String> totalMeal_Table_ColToHide, ingredientsTableUnEditableCells, ingredients_Table_Col_Avoid_Centering, ingredientsInMeal_Table_ColToHide;
    private TreeMap<String, Collection<String>>  map_ingredientTypesToNames;

    private ArrayList<IngredientsTable> ingredientsTables = new ArrayList<>();

    //####################
    // Objects
    //####################
    private JPanel collapsibleJPanel;
    private MyJDBC db;
    private GridBagConstraints gbc;
    private Container container;
    private CollapsibleJPanel collapsibleJpObj;

    //##################################################################################################################
    public MealManager(Meal_Plan_Screen meal_plan_screen, int mealInPlanID, int mealNo, String mealName, Container container)
    {
        //##############################################################################################################
        // Global Variables
        //##############################################################################################################
        this.mealInPlanID = mealInPlanID;
        this.mealName = mealName;
        this.container = container;

        ///############################
        // Objects
        ///############################
        this.db = meal_plan_screen.getDb();
        this.gbc = meal_plan_screen.getGbc();

        ///############################
        //Lists & Arraylists & Maps
        ///############################
        this.mealTotalTable_ColumnNames = meal_plan_screen.getMeal_total_columnNames();
        this.totalMeal_Table_ColToHide = meal_plan_screen.getTotalMeal_Table_ColToHide();
        this. map_ingredientTypesToNames = meal_plan_screen.getMap_ingredientTypesToNames();
        this.ingredientsTableUnEditableCells = meal_plan_screen.getIngredientsTableUnEditableCells();
        this.ingredients_Table_Col_Avoid_Centering = meal_plan_screen.getIngredients_Table_Col_Avoid_Centering();
        this.ingredientsInMeal_Table_ColToHide = meal_plan_screen.getIngredientsInMeal_Table_ColToHide();
        this.ingredientsTable_ColumnNames = meal_plan_screen.getIngredients_ColumnNames();

        ///############################
        // Integers
        ///############################
        this.tempPlanID = meal_plan_screen.getTempPlanID();
        this.planID = meal_plan_screen.getPlanID();
        this.containerYPos = meal_plan_screen.getContainerYPos();

        ///############################
        // String
        ///############################
        this.databaseName = meal_plan_screen.getDatabaseName();

        //##############################################################################################################
        // Get MealID's Of SubMeals
        //##############################################################################################################
        String subDivQuery = String.format("\nSELECT DivMealSectionsID FROM dividedMealSections WHERE MealInPlanID = %s AND PlanID = %s;", mealInPlanID, tempPlanID);
        ArrayList<ArrayList<String>> subMealsInMealArrayList = db.getMultiColumnQuery(subDivQuery);

        if(subMealsInMealArrayList == null)
        {
            String message = "\n\nError, gathering sub-meals ID for meal named ' %s ' ! \nA meal must have 1 sub-meal minimum!";

            System.out.printf("%s",message);
            JOptionPane.showMessageDialog(null, message);

            return;
        }

        //##############################################################################################################
        // Create Collapsible Object
        //##############################################################################################################
        collapsibleJpObj = new CollapsibleJPanel(container, String.format("   Meal   %s", mealNo), 150, 50);
        collapsibleJPanel = collapsibleJpObj.getCentreJPanel();
        collapsibleJPanel.setBackground(Color.YELLOW);
        addToContainer(container, collapsibleJpObj, 0, containerYPos++, 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);

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

        Object[][] meal_Total_Data = result!=null ? result:new Object[0][0];

        TotalMealTable total_Meal_View_Table = new TotalMealTable(db, collapsibleJpObj, databaseName, meal_Total_Data, mealTotalTable_ColumnNames, planID,
                mealInPlanID, mealName, tableName, new ArrayList<>(Arrays.asList(mealTotalTable_ColumnNames)), null,  totalMeal_Table_ColToHide);

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
        //  Total Meal Calculation JTable
        //##############################################################################################################



        //##############################################################################################################
        // Adding Collapsible Objects && JTables To GUI
        //##############################################################################################################
        /*
        // adding  CollapsibleOBJ to interface


        //Space Divider between each CollapsibleObj
        JPanel spaceDivider = new JPanel();
        jTableBeingAdded.setSpaceDivider(spaceDivider);
        addToContainer(scrollJPanelCenter, spaceDivider, 0, pos++, 1, 1, 0.25, 0.25, "both", 50, 0, null);
        */


    }

    private void iconSetup()
    {
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
    }

    private CollapsibleJPanel add_IngredientsTableToGUI(boolean mealInDB, Container container, Integer mealID, String mealName, int mealNo, ArrayList<ArrayList<String>> subMealIDs, MacrosLeftTable macrosLeft_JTable)
    {


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
        int yPos =1, no_Of_SubMealID = subMealIDs.size();
        for (int i = 0; i < no_Of_SubMealID; i++)
        {
            int divMealSectionsID = Integer.parseInt(subMealIDs.get(i).get(0));

            // Getting Ingredients In Meal
            query = String.format("SELECT *  FROM ingredients_in_sections_of_meal_calculation WHERE DivMealSectionsID = %s AND PlanID = %s ORDER BY Ingredients_Index;", divMealSectionsID, tempPlanID);

            tableName = "ingredients_in_sections_of_meal_calculation";
            Object[][] mealData = db.getTableDataObject(query, tableName)!=null ? db.getTableDataObject(query, tableName):new Object[0][0];

            //##############################################
            // Ingredients_In_Meal_Calculation  Creation
            //##############################################

            IngredientsTable ingredients_Calculation_JTable = new IngredientsTable(db, collapsibleJpObj, map_ingredientTypesToNames, databaseName, mealData, ingredientsTable_ColumnNames, planID, mealID,divMealSectionsID, mealInDB, mealName,
                    tableName, ingredientsTableUnEditableCells, ingredients_Table_Col_Avoid_Centering, ingredientsInMeal_Table_ColToHide, total_Meal_View_Table, macrosLeft_JTable);

            ingredients_Calculation_JTable.setOpaque(true); //content panes must be opaque

            // HELLO!! CHECK
            // add ingredients JTable to list
            //listOfJTables.add(ingredients_Calculation_JTable);

            //##############################################
            // Ingredients_In_Meal_Calculation Customisation
            //#############################################
            ingredients_Calculation_JTable.setTableHeaderFont(new Font("Dialog", Font.BOLD, 12));

            // Adding Ingredients_In_Meal_Calculation to CollapsibleOBJ
            addToContainer(collapsibleJPanel, ingredients_Calculation_JTable, 0, yPos++, 1, 1, 0.25, 0.25, "both", 0, 0, null);

            //#############################################
            // Space Divider
            //#############################################
            // Don't add space divider on last sub-meal
            if(i < no_Of_SubMealID-1)
            {
                // adds space between Ingredients_In_Meal_Calculation table and total_in_meal table
                addToContainer(collapsibleJPanel, new JPanel(), 0, yPos++, 1, 1, 0.25, 0.25, "both", 20, 0, null);
            }
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
