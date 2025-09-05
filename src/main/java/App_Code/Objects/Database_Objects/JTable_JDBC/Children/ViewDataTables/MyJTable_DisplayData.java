package App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JTable_JDBC.JDBC_JTable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

public class MyJTable_DisplayData extends JDBC_JTable
{
    protected Integer planID, temp_PlanID = 1;

    public MyJTable_DisplayData(MyJDBC db, Container parentContainer, Object[][] data, String[] columnNames, int planID,
                                String tableName, ArrayList<String> unEditableColumns, ArrayList<String> colAvoidCentering,
                                ArrayList<String> columnsToHide)
    {
        super(db, parentContainer, false,  tableName, data, columnNames, unEditableColumns, colAvoidCentering, columnsToHide);

        this.planID = planID;

        // #################################################
        // Stop Rows From Being Highlighted From Selection
        // #################################################

        // Disable row selection
        jTable.setRowSelectionAllowed(false);

        // Disable column selection
        jTable.setColumnSelectionAllowed(false);

        // Disable cell selection
        jTable.setCellSelectionEnabled(false);

        // Remove the focus outline from cells
        jTable.setFocusable(false);
    }

    @Override
    protected void setCellRenderer()
    {
        // ###############################################################
        // Centering Column Txt
        // ###############################################################

        DefaultTableCellRenderer cellRenderer2 = new DefaultTableCellRenderer();

        for (String columnName : columnNamesAndPositions.keySet())
        {
            int colPos = columnNamesAndPositions.get(columnName)[0];

            if (colAvoidCentering == null) // if list is null apply to all fields
            {
                cellRenderer2.setHorizontalAlignment(0);
                jTable.getColumnModel().getColumn(colPos).setCellRenderer(cellRenderer2); // Center Column Data
            }
            else if (! colAvoidCentering.contains(columnName)) // If list != null &  column is not in the list to avoid centering, center it
            {
                cellRenderer2.setHorizontalAlignment(0);
                jTable.getColumnModel().getColumn(colPos).setCellRenderer(cellRenderer2); // Center Column Data
            }
        }
    }

    protected void updateTable(String tableName, String query, int updateRow)
    {
        //###########################################################################
        //   Updating MacrosLeft_Table
        ///##########################################################################

        ArrayList<ArrayList<Object>> tableDataObject = db.get_Multi_ColumnQuery_Object(query);

        if (tableDataObject== null)
        {
            JOptionPane.showMessageDialog(null, String.format("ERROR updateTable(): \nUn-able to update %s Table!", tableName));

            return;
        }

        ArrayList<Object> tableData = tableDataObject.get(0);
        super.updateTable(tableData, updateRow);
    }

    protected  boolean updateTableModelData(String tableName, String query)
    {
        //##########################################
        // Changing Table Model
        //##########################################

        Object[][] data = db.getTableDataObject(query, tableName);

        if (data == null)
        {
            System.out.printf("\n\nupdateTableModelData() Unable to update %s model data!", tableName);
            return false;
        }

        setTableModelData(data);
        return true;
    }

    protected void setTableModelData(Object[][] tableModelData)
    {
        super.setTableModelData(tableModelData);
    }
}
