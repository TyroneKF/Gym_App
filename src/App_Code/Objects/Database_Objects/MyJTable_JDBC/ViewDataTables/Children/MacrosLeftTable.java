package App_Code.Objects.Database_Objects.MyJTable_JDBC.ViewDataTables.Children;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.MyJTable_JDBC.ViewDataTables.Parent.MyJTable_DisplayData;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MacrosLeftTable extends MyJTable_DisplayData
{
    public MacrosLeftTable(MyJDBC db, Container parentContainer, Object[][] data, String[] columnNames, int planID,
                           String tableName, ArrayList<Integer> unEditableColumns, ArrayList<Integer> colAvoidCentering)
    {
        super(db, parentContainer, data, columnNames, planID, tableName, unEditableColumns, colAvoidCentering);
    }

    public void updateMacrosLeft()
    {
        //##########################################################################
        //   Updating MacrosLeft_Table
        ///##########################################################################


        String macrosLeftQuery = String.format("select * from planMacrosLeft WHERE PlanID = %s", temp_PlanID);

        ArrayList<ArrayList<Object>> macrosLeftTableData = db.get_Multi_ColumnQuery_Object(macrosLeftQuery);

        if (macrosLeftTableData == null)
        {
            JOptionPane.showMessageDialog(null, "ERROR: \nUn-able to MacrosLeft Table!");

            return;
        }
        else
        {
            ArrayList<Object> macrosLeft_UpdateData = macrosLeftTableData.get(0);
            super.updateTable(this, macrosLeft_UpdateData, 0);
        }

        return;
    }

    public void refreshData()
    {
        super.refreshData();
    }
}
