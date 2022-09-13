package App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.Children;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.Parent.MyJTable_DisplayData;
import App_Code.Objects.Gui_Objects.CollapsibleJPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class TotalMealTable extends MyJTable_DisplayData
{
    private Integer planID, temp_PlanID = 1, mealID;
    private String mealName;
    private CollapsibleJPanel collapsibleObj;


    public TotalMealTable(MyJDBC db, CollapsibleJPanel collapsibleObj,  String databaseName, Object[][] data, String[] columnNames, int planID,
                          Integer mealID, String mealName, String tableName,
                          ArrayList<String> unEditableColumns, ArrayList<String> colAvoidCentering, boolean setIconsUp,
                          ArrayList<String> columnsToHide)
    {

        super(db, collapsibleObj.getCentreJPanel(), databaseName, data, columnNames, planID, tableName, unEditableColumns, colAvoidCentering, columnsToHide);

        this.mealID = mealID;
        this.mealName = mealName;
        this.planID = planID;
        this.collapsibleObj = collapsibleObj;
    }

    public boolean updateTableModelData()
    {
        //##########################################
        // Changing Total  Ingredients Table Model
        //##########################################

        // Setting totals tables Data model to new data
        String totalTableQuery = String.format("SELECT *  FROM total_meal_view WHERE MealID = %s AND PlanID = %s;", mealID, temp_PlanID);

        Object[][] totalTableData = db.getTableDataObject(totalTableQuery, "total_meal_view");

        if (totalTableData == null)
        {
            System.out.printf("\n\nUnable to update total meal table for table: %s", getMealName());
            return false;
        }

        setTableModelData(totalTableData);

        return true;
    }

    public void updateTotalMealTable()
    {
        //##########################################################################
        //   Updating Total  Meal Table
        ///##########################################################################

        String totalMealTableQuery = String.format("SELECT *  FROM total_meal_view WHERE MealID = %s AND PlanID = %s;", mealID, temp_PlanID);

        ArrayList<ArrayList<Object>> totalMealData = db.get_Multi_ColumnQuery_Object(totalMealTableQuery);

        if (totalMealData == null)
        {
            JOptionPane.showMessageDialog(null, String.format("ERROR: \nUn-able to Update Totals Table for Table %s!", mealName));

            return;
        }

        ArrayList<Object> totalMeal_UpdateData = totalMealData.get(0);
        super.updateTable(this, totalMeal_UpdateData, 0);
    }

    public Object[][] getData()
    {
        return super.getData();
    }

    public String getMealName()
    {
        return mealName;
    }

    public void refreshData()
    {
        super.refreshData();
    }

    public void setTableModelData(Object[][] tableModelData)
    {
        super.setTableModelData(tableModelData);
    }

    @Override
    public void tableModel_Setup(Object[][] data, String[] columnNames)
    {
        tableModel = new DefaultTableModel(data, columnNames)
        {
            @Override
            public boolean isCellEditable(int row, int col)
            {
                //Note that the data/cell address is constant,
                //no matter where the cell appears onscreen.
                if (unEditableColumns.contains(col))
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @Override
            public Class getColumnClass(int c)
            {
                return getValueAt(0, c).getClass();
            }
        };

        tableModel.addTableModelListener(
                evt -> tableDataChange_Action());

        jTable.setModel(tableModel);

        rowsInTable = data.length;

        //#################################################################################
        // Table Personalisation
        //#################################################################################

        //initColumnSizes();
        setCellsAlignment(0, colAvoidCentering);

        if (getTableInitialised())  //first time this method is called, special columns aren't defined
        {
            if (getColumnsToHide() != null)//Must be first
            {
                SetUp_HiddenTableColumns(getColumnsToHide());
            }

            if (getDeleteBTN_Col() != null)
            {
                setupDeleteBtnColumn(getDeleteBTN_Col()); // specifying delete column
            }

            // Setting up JcomboBox Field
            for (Integer key : getJcomboMap().keySet())
            {
                setUpJComboColumn(key, "IngredientName", getJcomboMap().get(key));
            }
        }
        else
        {
            setTableInitilized();
        }
        resizeObject();
    }
}

