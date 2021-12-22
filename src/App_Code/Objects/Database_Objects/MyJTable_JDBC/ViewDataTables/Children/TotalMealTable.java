package App_Code.Objects.Database_Objects.MyJTable_JDBC.ViewDataTables.Children;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.MyJTable_JDBC.ViewDataTables.Parent.MyJTable_DisplayData;
import App_Code.Objects.Gui_Objects.CollapsibleJPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class TotalMealTable extends MyJTable_DisplayData
{
    private Integer planID, temp_PlanID = 1, mealID, tempPlan_Meal_ID;
    private String mealName;
    private CollapsibleJPanel collapsibleObj;




    public TotalMealTable(MyJDBC db, CollapsibleJPanel collapsibleObj, String databaseName, Object[][] data, String[] columnNames, int planID,
                          Integer mealID, Integer tempPlan_Meal_ID, String mealName, String tableName,
                          ArrayList<Integer> unEditableColumns, ArrayList<Integer> colAvoidCentering, boolean setIconsUp)
    {

        super(db, collapsibleObj.getCentreJPanel(), data, columnNames, planID, tableName, unEditableColumns, colAvoidCentering);

        this.databaseName = databaseName;
        this.mealID = mealID;
        this.mealName = mealName;
        this.planID = planID;
        this.tempPlan_Meal_ID = tempPlan_Meal_ID;
        this.setIconsUp = setIconsUp;
        this.collapsibleObj = collapsibleObj;
    }

    public void updateTotalMealTable()
    {
        //##########################################################################
        //   Updating Total  Meal Table
        ///##########################################################################

        String totalMealTableQuery = String.format("SELECT  * FROM total_meal_view   WHERE  MealID = %s; ", tempPlan_Meal_ID);

        ArrayList<ArrayList<Object>> totalMealData = db.get_Multi_ColumnQuery_Object(totalMealTableQuery);

        if (totalMealData == null)
        {
            JOptionPane.showMessageDialog(null, "ERROR: \nUn-able to Update Totals Table!");

            return;
        }
        else
        {
            ArrayList<Object> totalMeal_UpdateData = totalMealData.get(0);
            super.updateTable(this, totalMeal_UpdateData, 0);
        }
    }

    public boolean getIconSetupStatus()
    {
        return setIconsUp;
    }

    public Object[][] getData()
    {
        return  super.getData();
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
    public  void tableModel_Setup(Object[][] data, String[] columnNames)
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

        if (getTableInitilized())  //first time this method is called, special columns aren't defined
        {
            if (getHideColumns() != null)//Must be first
            {
                SetUp_HiddenTableColumns(getHideColumns(), get_StartingUpdateColumn());
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

