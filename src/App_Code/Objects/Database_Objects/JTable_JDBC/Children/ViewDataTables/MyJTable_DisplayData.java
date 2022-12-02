package App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JTable_JDBC.JDBC_JTable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MyJTable_DisplayData extends JDBC_JTable
{
    protected Integer planID, temp_PlanID = 1;

    public MyJTable_DisplayData(MyJDBC db, Container parentContainer, String databaseName, Object[][] data, String[] columnNames, int planID,
                                String tableName, ArrayList<String> unEditableColumns, ArrayList<String> colAvoidCentering,
                                ArrayList<String> columnsToHide)
    {
        super(db, parentContainer, false, databaseName, tableName, data, columnNames, unEditableColumns, colAvoidCentering, columnsToHide);

        this.planID = planID;
    }

    protected void updateTable(String tableName, String query, int updateRow)
    {
        //###########################################################################
        //   Updating MacrosLeft_Table
        ///##########################################################################

        ArrayList<ArrayList<Object>> tableDataObject = db.get_Multi_ColumnQuery_Object(query);

        if (tableDataObject== null)
        {
            JOptionPane.showMessageDialog(null, String.format("ERROR: \nUn-able to update %s Table!", tableName));

            return;
        }

        ArrayList<Object> tableData = tableDataObject.get(0);
        super.updateTable(tableData, updateRow);
    }

}
