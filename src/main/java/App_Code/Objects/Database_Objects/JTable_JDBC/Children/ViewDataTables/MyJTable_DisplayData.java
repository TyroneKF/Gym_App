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
    protected Object[] params;
    protected int updateRow = 0;
    
    public MyJTable_DisplayData(MyJDBC db, Container parentContainer, ArrayList<ArrayList<Object>> data, ArrayList<String> columnNames, int planID, int temp_PlanID,
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
                
                if (! isSelected)
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
        //##########################################################################
        String errorMSG = String.format("Error, Updating Table '%s'!", tableName);
        
        ArrayList<ArrayList<Object>> tableDataObject = db.get_2D_Query_AL_Object(query, params, errorMSG);
        
        if (tableDataObject == null)
        {
            JOptionPane.showMessageDialog(null, String.format("Error, un-able to update %s Table!", tableName));
            return;
        }
      
        super.updateTable(tableDataObject.getFirst(), updateRow);
    }
    
    
    protected void setTableModelData(ArrayList<ArrayList<Object>> tableModelData)
    {
        super.setTableModelData(tableModelData);
    }
    
    protected void update_Table_Cell_Value(Object data, int row, int col)
    {
        super.update_Table_Cell_Value(data, row, col);
    }
}
