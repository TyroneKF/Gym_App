package App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JTable_JDBC.JDBC_JTable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

public class MyJTable_DisplayData extends JDBC_JTable
{
    protected Integer planID, temp_PlanID;
    protected String query = "";
    protected int updateRow =  0;

    public MyJTable_DisplayData(MyJDBC db, Container parentContainer, Object[][] data, String[] columnNames, int planID, int temp_PlanID,
                                String tableName, ArrayList<String> unEditableColumns, ArrayList<String> colAvoidCentering,
                                ArrayList<String> columnsToHide)
    {
        super(db, parentContainer, false, false, tableName, data, columnNames, unEditableColumns, colAvoidCentering, columnsToHide);

        this.planID = planID;
        this.temp_PlanID = temp_PlanID;

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

        //content panes must be opaque
        setOpaque(true);

        // Set Header Font
        setTableHeaderFont(new Font("Dialog", Font.BOLD, 14));

        // Set Txt Font
        setTableTextFont(new Font("Dialog", Font.PLAIN, 14));
    }

    @Override
    protected void setCellRenderer()
    {
        // ###############################################################
        // Centering Column Txt
        // ###############################################################

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer()
        {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column)
            {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected)
                {
                    // Always reset to defaults first
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);

                    // Then apply your condition
                    if (value instanceof Number && ((Number) value).doubleValue() < 0)
                    {
                        c.setBackground(table.getSelectionBackground());
                        c.setForeground(table.getSelectionForeground());
                    }
                }

                setHorizontalAlignment(0); // Center text in cell

                return c;
            }
        };

        for (int i = 0; i < jTable.getColumnCount(); i++) // Apply this to all the columns in Jtable
        {
            jTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
    }

    protected void updateTable()
    {
        //###########################################################################
        //   Updating MacrosLeft_Table
        ///##########################################################################
        Object[][] tableDataObject = db.getTableDataObject(query, tableName);

        if (tableDataObject==null)
        {
            JOptionPane.showMessageDialog(null, String.format("ERROR updateTable(): \nUn-able to update %s Table!", tableName));

            return;
        }

        Object[] tableData = tableDataObject[0];
        super.updateTable(tableData, updateRow);
    }

    protected boolean updateTableModelData()
    {
        //##########################################
        // Changing Table Model
        //##########################################

        Object[][] data = db.getTableDataObject(query, tableName);

        if (data == null)
        {
            JOptionPane.showMessageDialog(null, String.format("ERROR updateTableModelData(): \nUn-able to updateTableModel %s !", tableName));

            System.err.printf("\n\nupdateTableModelData() Unable to update %s model data!", tableName);
            return false;
        }

        setTableModelData(data);

        jTable.repaint();
        return true;
    }

    protected void setTableModelData(Object[][] tableModelData)
    {
        super.setTableModelData(tableModelData);
    }
}
