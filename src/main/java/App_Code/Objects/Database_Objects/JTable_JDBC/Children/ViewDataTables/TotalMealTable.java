package App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.MyJTable_DisplayData;
import App_Code.Objects.Gui_Objects.CollapsibleJPanel;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class TotalMealTable extends MyJTable_DisplayData
{
    private Integer MealInPlanID;
    private String mealName;

    public TotalMealTable(MyJDBC db, CollapsibleJPanel collapsibleObj, Object[][] data, String[] columnNames, int planID, int temp_PlanID,
                          Integer MealInPlanID, String mealName, String tableName, ArrayList<String> unEditableColumns, ArrayList<String> colAvoidCentering,
                          ArrayList<String> columnsToHide)
    {

        super(db, collapsibleObj.getCentreJPanel(), data, columnNames, planID, temp_PlanID, tableName, unEditableColumns, colAvoidCentering, columnsToHide);
        super.query = String.format("SELECT * FROM %s WHERE meal_in_plan_id = %s AND plan_id = %s;", tableName, MealInPlanID, temp_PlanID);

        this.MealInPlanID = MealInPlanID;
        this.mealName = mealName;
    }

    @Override
    public void tableModel_Setup(Object[][] data, String[] columnNames)
    {
        super.tableModel = new DefaultTableModel(data, columnNames)
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

        super.rowsInTable = data.length;

        //#################################################################################
        // Table Personalisation
        //#################################################################################

        //initColumnSizes();
        setCellRenderer();

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
        super.updateTable();
    }

    public boolean updateTotalMealTableModelData()
    {
        return super.updateTableModelData();
    }
}

