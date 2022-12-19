package App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.Children.TotalMealTable;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.MyJTable_DisplayData;
import App_Code.Objects.Gui_Objects.CollapsibleJPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class TotalMealTable extends MyJTable_DisplayData
{
    protected Integer planID, temp_PlanID = 1, MealInPlanID;
    protected String mealName;
    protected CollapsibleJPanel collapsibleObj;


    public TotalMealTable(MyJDBC db, CollapsibleJPanel collapsibleObj,  Object[][] data, String[] columnNames, int planID,
                          Integer MealInPlanID, String mealName, String tableName,
                          ArrayList<String> unEditableColumns, ArrayList<String> colAvoidCentering, ArrayList<String> columnsToHide)
    {

        super(db, collapsibleObj.getCentreJPanel(),  data, columnNames, planID, tableName, unEditableColumns, colAvoidCentering, columnsToHide);

        this.MealInPlanID = MealInPlanID;
        this.mealName = mealName;
        this.planID = planID;
        this.collapsibleObj = collapsibleObj;
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
        }
        else
        {
            setTableInitialized();
        }
        resizeObject();
    }

    public void updateTotalMealTable()
    {
        String query = String.format("SELECT *  FROM total_meal_view WHERE MealInPlanID = %s AND PlanID = %s;", MealInPlanID, temp_PlanID);
        super.updateTable("TotalMealTable", query, 0);
    }

    public boolean updateTotalMealTableModelData()
    {
        String query = String.format("SELECT * FROM total_meal_view WHERE MealInPlanID = %s AND PlanID = %s;",
                MealInPlanID, temp_PlanID);

        return super.updateTableModelData("total_meal_view", query);
    }

    public String getMealName()
    {
        return mealName;
    }

}

