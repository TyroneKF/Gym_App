package App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.Children;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.Parent.MyJTable_DisplayData;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MacrosLeftTable extends MyJTable_DisplayData
{
    public MacrosLeftTable(MyJDBC db, Container parentContainer, Object[][] data, String[] columnNames, int planID,
                           String tableName, ArrayList<String> unEditableColumns, ArrayList<String> colAvoidCentering,
                           ArrayList<String> columnsToHide)
    {
        super(db, parentContainer, data, columnNames, planID, tableName, unEditableColumns, colAvoidCentering, columnsToHide);
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

        ArrayList<Object> macrosLeft_UpdateData = macrosLeftTableData.get(0);
        super.updateTable(this, macrosLeft_UpdateData, 0);
    }

    public boolean updateTableModelData()
    {
        //##########################################
        // Changing Total  Ingredients Table Model
        //##########################################

        // Setting totals tables Data model to new data
        String macrosLeftQuery = String.format("select * from planMacrosLeft WHERE PlanID = %s;", temp_PlanID);
        System.out.printf("\n\n#################################@@ \n\n%s", macrosLeftQuery);

        Object[][] macrosLeftData = db.getTableDataObject(macrosLeftQuery, "planMacrosLeft");

        if (macrosLeftData == null)
        {
            System.out.printf("\n\nUnable to update macrosLeftData");
            return false;
        }

        setTableModelData(macrosLeftData);
        return true;
    }

}
