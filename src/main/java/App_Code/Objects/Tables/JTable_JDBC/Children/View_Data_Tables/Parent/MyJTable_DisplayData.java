package App_Code.Objects.Tables.JTable_JDBC.Children.View_Data_Tables.Parent;

import App_Code.Objects.Database_Objects.MyJDBC.MyJDBC_Sqlite;
import App_Code.Objects.Tables.JTable_JDBC.Parent.JDBC_JTable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

public abstract class MyJTable_DisplayData extends JDBC_JTable
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    protected int update_Row = 0;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public MyJTable_DisplayData
    (
                    MyJDBC_Sqlite db,
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
        
        //content panes must be opaque
        setOpaque(true);
        
        // Set Header Font
        set_Table_Header_Font(new Font("Dialog", Font.BOLD, 14));
        
        // Set Txt Font
        set_Table_Text_Font(new Font("Dialog", Font.PLAIN, 14));
    }
    
    //##################################################################################################################
    // Variables
    //##################################################################################################################
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
    
    private String get_Query()
    {
        return String.format("SELECT * FROM %s WHERE %s = ?;", db_read_view_name, db_row_id_column_name);
    }
    
    protected abstract Object[] get_Params();
    
    public void update_Table()
    {
        //###########################################################################
        //   Updating MacrosLeft_Table
        //##########################################################################
        String errorMSG = String.format("Error, Updating Table '%s'!", table_name);
        
        try
        {
            ArrayList<ArrayList<Object>> tableDataObject = sqlite_db.get_2D_Query_AL_Object(get_Query(), get_Params(), errorMSG, false);
            super.update_Table_Row(tableDataObject.getFirst(), update_Row);
        }
        catch (Exception _) // Error is already handled by DB class
        {
            JOptionPane.showMessageDialog(null, String.format("Unable to Update Table - '%s'!", table_name));
        }
    }
}
