package com.donty.gymapp.ui.tables.base;

import com.donty.gymapp.persistence.database.MyJDBC_Sqlite;
import com.donty.gymapp.persistence.database.statements.Fetch_Statement_Full;
import com.donty.gymapp.persistence.Shared_Data_Registry;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

public abstract class MyJTable_Display_Data extends MyJTable
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    protected int update_Row = 0;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public MyJTable_Display_Data
    (
            MyJDBC_Sqlite db,
            Shared_Data_Registry shared_data_registry,
            Container parent_Container,
            ArrayList<ArrayList<Object>> data,
            ArrayList<String> column_Names,
            String db_row_id_column_name,
            String table_Name,
            String db_write_table_name,
            String db_read_view_name,
            ArrayList<String> un_Editable_Columns,
            ArrayList<String> col_Avoid_Centering,
            ArrayList<String> columns_To_Hide
    )
    {
        // #################################################
        // Super /  Variables
        // #################################################
        super(
                db,
                shared_data_registry,
                parent_Container,
                false,
                db_row_id_column_name,
                table_Name,
                db_write_table_name,
                db_read_view_name,
                data,
                column_Names,
                un_Editable_Columns,
                col_Avoid_Centering,
                columns_To_Hide
        );
        
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
        
        // Set Header Font
        set_Table_Header_Font(new Font("Dialog", Font.BOLD, 14));
        
        // Set Txt Font
        set_Table_Text_Font(new Font("Dialog", Font.PLAIN, 14));
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    
    
    //###########################################
    // Setup Methods
    //###########################################
    @Override
    protected void child_Variable_Configurations(){}
    
    @Override
    protected void child_Table_Configurations() { }
    
    @Override
    protected boolean format_Table_Data(ArrayList<ArrayList<Object>> table_data) { return true; }
    
    @Override
    protected void format_Table_Row_Data(ArrayList<Object> table_data) throws Exception { }
    
    @Override
    protected void set_Cell_Renderer()
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
        
        for (int i = 0; i < jTable.getColumnCount(); i++) // Apply this to all the columns in JTable
        {
            jTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
    }
    
    @Override
    protected void extra_Table_Setup() { }
    
    @Override
    protected boolean table_Data_Changed_Action(int row_Model, int column_Model, Object newValue) { return false; }
    
    @Override
    protected boolean has_Cell_Data_Changed(Class<?> type, Object old_Value, Object new_Value, int col) throws Exception
    {
        throw new Exception(String.format("%s -> doesn't need a has_Cell_Data_Changed() Method to called, logic Error!", table_name));
    }
    
    protected abstract Object[] get_Params();
    
    protected ArrayList<Object> get_Table_Update_Data() throws Exception
    {
        //###########################################################################
        //   Updating MacrosLeft_Table
        //##########################################################################
        String error_msg = String.format("Error, Updating Table '%s'!", table_name);
        String query = String.format("SELECT * FROM %s WHERE %s = ?;", db_read_view_name, db_row_id_column_name);
        Fetch_Statement_Full fetch_statement = new Fetch_Statement_Full(query, get_Params(), error_msg);

        try
        {
            ArrayList<ArrayList<Object>> tableDataObject = db.get_2D_Query_AL_Object(fetch_statement, false);
            return tableDataObject.getFirst();
        }
        catch (Exception _) // Error is already handled by DB class
        {
            throw new Exception(String.format("%s - Failed Getting Updated Data", table_name));
        }
    }
    
    //###########################################
    // Update Methods
    //###########################################
    public void update_Table()
    {
        try
        {
            super.update_Table_Row(get_Table_Update_Data(), update_Row);
        }
        catch (Exception e) // Error is already handled by DB class / get_Update_Data()
        {
            JOptionPane.showMessageDialog(null, String.format("Unable to Update Table - '%s'!", table_name));
            System.err.printf("\n\n%s", e);
        }
    }
}
