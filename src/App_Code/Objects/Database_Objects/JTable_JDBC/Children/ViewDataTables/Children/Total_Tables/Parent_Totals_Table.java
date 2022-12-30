package App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.Children.Total_Tables;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.MyJTable_DisplayData;
import App_Code.Objects.Gui_Objects.CollapsibleJPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;


public class Parent_Totals_Table extends MyJTable_DisplayData
{
    protected Integer planID, temp_PlanID = 1, MealInPlanID;
    protected String mealName;




    public Parent_Totals_Table(MyJDBC db, Container parentContainer, Object[][] data, String[] columnNames, int planID, int temp_PlanID, int MealInPlanID,
                               String mealName, String tableName,
                               ArrayList<String> unEditableColumns, ArrayList<String> colAvoidCentering, ArrayList<String> columnsToHide)
    {

        super(db, parentContainer, data, columnNames, planID, tableName, unEditableColumns, colAvoidCentering, columnsToHide);

        this.MealInPlanID = MealInPlanID;
        this.mealName = mealName;
        this.planID = planID;
        this.temp_PlanID = temp_PlanID;
    }

    @Override
    protected void tableModel_Setup(Object[][] data, String[] columnNames)
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

    protected void updateTable(int updateRow, String query, String tableName)
    {
        super.updateTable(tableName, query, updateRow);
    }

    protected boolean updateTableModelData(String databaseTableName, String query)
    {
        return super.updateTableModelData(databaseTableName, query);
    }
}


