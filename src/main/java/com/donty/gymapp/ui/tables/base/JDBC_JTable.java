package com.donty.gymapp.ui.tables.base;

// Packages to import
// https://stackoverflow.com/questions/10347983/making-a-jbutton-clickable-inside-a-jtable
//http://tips4java.wordpress.com/2009/07/12/table-button-column/

import com.donty.gymapp.persistence.database.MyJDBC_Sqlite;
import com.donty.gymapp.persistence.Shared_Data_Registry;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.stream.Collectors;


public abstract class JDBC_JTable extends JPanel
{
    //##############################################
    // Objects
    //##############################################
    protected MyJDBC_Sqlite db;
    protected Shared_Data_Registry shared_data_registry;
    protected Container parent_Container;
    protected JScrollPane scrollPane = new JScrollPane();
    protected static GridBagConstraints gbc = new GridBagConstraints(); //HELLO DELETE
    
    //#####################################################################
    // Table Customization Variables
    //#####################################################################
    protected JTable jTable = new JTable();
    private CustomTableModel tableModel;
    
    
    //#######################
    // Strings
    //#######################
    protected String db_row_id_column_name;
    protected String table_name;
    protected String db_read_view_name;
    protected String db_write_table_name;
    
    //##############################################
    // Collections
    //##############################################
    protected ArrayList<String> column_Names, gui_Column_Names;
    protected ArrayList<ArrayList<Object>> saved_Data;
    
    //########################
    // Customisation Options
    //########################
    protected ArrayList<String> un_Editable_Column_Names;
    protected ArrayList<String> col_To_Avoid_Centering;
    protected ArrayList<Integer> un_Editable_Column_Positions = new ArrayList<>();
    protected ArrayList<String> columns_To_Hide;
    
    protected LinkedHashMap<String, Integer[]> column_Names_And_Positions = new LinkedHashMap<>(); // Hello Can be removed
    /*
        Array Pos 1 = Original Position in JTable Data
        Array Pos 2 = Position after columns hidden
    */
    
    //#####################################################################
    // Other Variables
    //#####################################################################
    // String
    private final String class_Name;
    protected String lineSeparator = "###############################################################################";
    
    //#############################
    // Booleans
    //#############################
    protected boolean
            table_Initialised = false,
            add_JTable_Action,
            is_row_Being_Edited = false;
            
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public JDBC_JTable
    (
            MyJDBC_Sqlite db,
            Shared_Data_Registry shared_data_registry,
            Container parent_Container,
            boolean add_JTable_Action,
            String db_row_id_column_name,
            String table_name,
            String db_write_table_name,
            String db_read_view_name,
            ArrayList<ArrayList<Object>> saved_Data,
            ArrayList<String> column_Names,
            ArrayList<String> un_Editable_Column_Names,
            ArrayList<String> col_To_Avoid_Centering,
            ArrayList<String> columns_To_Hide
    )
    {
        setLayout(new GridBagLayout());
        
        //##############################################################
        // Variables
        //##############################################################
        this.db = db;
        this.shared_data_registry = shared_data_registry;
        
        this.parent_Container = parent_Container;
        this.add_JTable_Action = add_JTable_Action;
        
        this.db_row_id_column_name = db_row_id_column_name;
        this.table_name = table_name;
        this.db_read_view_name = db_read_view_name;
        this.db_write_table_name = db_write_table_name;
        
        this.class_Name = this.getClass().getSimpleName();
        
        this.saved_Data = saved_Data != null ? saved_Data : new ArrayList<>();
        
        this.col_To_Avoid_Centering = col_To_Avoid_Centering;
        this.columns_To_Hide = columns_To_Hide;
        this.column_Names = column_Names;
        this.un_Editable_Column_Names = un_Editable_Column_Names;
        
        //##############################################################
        // Setup
        //##############################################################
        initialize();
    }
    
    protected final void initialize()
    {
        // Variable / column configuration
        parent_Variable_Configurations();
        child_Variable_Configurations();
        
        //  Data formatting (child-controlled)
        if (! format_Table_Data(saved_Data)) { return; }
        
        // Table Configurations
        parent_Table_Configuration();  // Table setup (parent creates table)
        
        child_Table_Configurations(); // Child UI adjustments (safe now)
    }
    
    //########################################
    // Format Table Data
    //########################################
    protected abstract boolean format_Table_Data(ArrayList<ArrayList<Object>> table_data);
    
    protected abstract void format_Table_Row_Data(ArrayList<Object> table_data) throws Exception;
    
    //########################################
    // Variable Configurations
    //########################################
    private void parent_Variable_Configurations()
    {
        //##############################################################
        // Column Configurations
        //##############################################################
        // Adding column names and their original positions to the hashmap
        for (int pos = 0; pos < column_Names.size(); pos++)
        {
            column_Names_And_Positions.put(column_Names.get(pos), new Integer[]{ pos, pos });
        }
        
        //########################################
        // Un-Editable Column Configurations
        //########################################
        // Get UnEditable Column Positions By Name
        if (un_Editable_Column_Names != null)
        {
            for (String column_Name : un_Editable_Column_Names)
            {
                un_Editable_Column_Positions.add(column_Names_And_Positions.get(column_Name)[0]);
            }
        }
        
        //############################################
        // Format Table Names for GUI
        //############################################
        gui_Column_Names = new ArrayList<>();
        for (String column_Name : column_Names) // Reformat Column Names To be Capitalised on the Application lvl
        {
            // Get Column Name & Re-assign Re-Capitalised Value into list
            gui_Column_Names.add(Arrays.stream(column_Name.split("[ _]+"))
                    .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                    .collect(Collectors.joining("_")));
        }
        
        //############################################
        // Variable Reset
        //############################################
        un_Editable_Column_Names = null; // No longer needed
    }
    
    protected abstract void child_Variable_Configurations();
    
    //########################################
    // Table Configurations
    //########################################
    private void parent_Table_Configuration()
    {
        extra_Table_Setup();
        tableSetup(saved_Data, gui_Column_Names); // Table Setup With Table Data
        SetUp_Hidden_Table_Columns(columns_To_Hide); // Hide Columns | Must be the last step in configuration of the table
        setOpaque(true); //content panes must be opaque
    }
    
    protected abstract void child_Table_Configurations();
    
    //##################################################################################################################
    // Table Setup Methods
    //##################################################################################################################
    protected void tableSetup(ArrayList<ArrayList<Object>> data, ArrayList<String> column_Names)
    {
        //###################################################################################
        // Table Setup
        //###################################################################################
        jTable = new JTable(); //Creating Table
        
        jTable.setRowHeight(jTable.getRowHeight() + 15);
        jTable.setFillsViewportHeight(true);
        jTable.getTableHeader().setPreferredSize(new Dimension(100, 50));  // setting header size
        set_Table_Header_Font(new Font("Dialog", Font.BOLD, 16));    // setting text size
        
        tableModel_Setup(data, column_Names); // sets JTable Model
        
        //################################################################################
        // Adding JTable to JScrollPane
        //################################################################################
        
        // Create the scroll pane and add the table to it, has to be added to a scrollpane as otherwise it doesnt work
        
        scrollPane.setViewportView(jTable);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        
        add_To_Container(this, scrollPane, 0, 1, 1, 1, 0.25, 0.25, "both", "");
        
        //#################################################################################
        // Sizing
        //#################################################################################
        resize_Object();
    }
    
    protected abstract void extra_Table_Setup();
    
    protected void tableModel_Setup(ArrayList<ArrayList<Object>> data, ArrayList<String> column_Names)
    {
        tableModel = new CustomTableModel(data, column_Names);
        jTable.setModel(tableModel);
        
        //#################################################################################
        // Table Personalisation
        //#################################################################################
        
        //initColumnSizes();
        set_Cell_Renderer();
        
        if (table_Initialised)  //first time this method is called, special columns aren't defined
        {
            if (columns_To_Hide != null)
            {
                SetUp_Hidden_Table_Columns(columns_To_Hide);
            }
        }
        else
        {
            table_Initialised = true;
        }
        resize_Object();
    }
    
    /**
     * This needs to be done before hiding columns
     * Set Column alignment
     */
    protected void set_Cell_Renderer()
    {
        // ###############################################################
        // Centering Column Txt
        // ###############################################################
        
        DefaultTableCellRenderer right_Renderer = new DefaultTableCellRenderer();
        right_Renderer.setHorizontalAlignment(0);
        
        int pos = - 1;
        for (String column_Name : column_Names)
        {
            pos++;
            
            if (col_To_Avoid_Centering != null && col_To_Avoid_Centering.contains(column_Name))
            {
                continue;
            }
            
            jTable.getColumnModel().getColumn(pos).setCellRenderer(right_Renderer); // Center Column Data
        }
    }
    
    /*
       As columns are hidden the position changes by -1 for the next time its called
     */
    protected void SetUp_Hidden_Table_Columns(ArrayList<String> columns_To_Hide)
    {
        int pos = 0, no_Of_Columns_Hidden = 0, jTable_Pos_After_Hiding = 0;
        
        for (Map.Entry<String, Integer[]> jTable_Column : column_Names_And_Positions.entrySet())
        {
            jTable_Pos_After_Hiding = pos - no_Of_Columns_Hidden;
            
            //########################################################################
            // Extracting Info
            //#######################################################################
            String column_Name = jTable_Column.getKey();

            /*
             Pos 1 column original position in JTable Data
             Pos 2 column position in JTable after columns are hidden
            */
            Integer[] column_Positions_List = jTable_Column.getValue();
            
            //#######################################################################
            // Hide Or Update Column Pos After Hiding
            //#######################################################################
            
            if (columns_To_Hide.contains(column_Name)) // Hide Column In JTable
            {
                column_Positions_List[1] = null; // No Longer In The JTable so position is null
                column_Names_And_Positions.replace(column_Name, column_Positions_List); // Update position
                
                jTable.removeColumn(jTable.getColumnModel().getColumn(jTable_Pos_After_Hiding)); // Hide Column in JTable
                
                no_Of_Columns_Hidden++;
            }
            else // Adjust current column position
            {
                column_Positions_List[1] = jTable_Pos_After_Hiding;
                column_Names_And_Positions.replace(column_Name, column_Positions_List);
            }
            //#######################################################################
            
            pos++;
        }
    }
    
    //##################################################################################################################
    // Table Model
    //##################################################################################################################
    public class CustomTableModel extends AbstractTableModel
    {
        private ArrayList<ArrayList<Object>> current_Table_Data;
        private final ArrayList<String> column_Names;
        
        public CustomTableModel(ArrayList<ArrayList<Object>> input_Data, ArrayList<String> column_Names)
        {
            // ######################################################
            //
            // ######################################################
            this.column_Names = column_Names;
            
            try // Validate
            {
                this.current_Table_Data = clone_Data(input_Data);
            }
            catch (Exception e)
            {
                System.err.printf("\n\n%s \n%s Error -> Failed Setting Table Data \n%s \n\n%s", lineSeparator, get_Class_And_Method_Name(), lineSeparator, e);
                JOptionPane.showMessageDialog(null, String.format("Table Columns & Expected Data Miss-Match - '%s'!", table_name));
            }
        }
        
        @Override
        public int getRowCount()
        {
            return current_Table_Data.size();
        }
        
        @Override
        public int getColumnCount()
        {
            return column_Names.size();
        }
        
        @Override
        public Object getValueAt(int row, int col)
        {
            if (row < 0 || row >= current_Table_Data.size()) { return null; }
            
            ArrayList<Object> r = current_Table_Data.get(row);
            if (col < 0 || col >= r.size()) { return null; }
            
            return r.get(col);
        }
        
        @Override
        public String getColumnName(int col)
        {
            return column_Names.get(col);
        }
        
        @Override
        public void setValueAt(Object newValue, int row, int col)  // This Method is only triggered by the user editing the JTable
        {
            try // Validate Data / Handle Change
            {
                //###########################################
                // Exit Clauses
                //###########################################
                if (newValue == null) // EXIT Clause
                {
                    throw new Exception(String.format("\n\n%s Error \nValue Cannot be null - '%s'!", get_Class_And_Method_Name(), table_name));
                }
                
                //###########################################
                //
                //###########################################
                Object old_Value = getValueAt(row, col);
                
                if (add_JTable_Action && ! is_row_Being_Edited)
                {
                    // If Nothing Changed Exit
                    if (! has_Cell_Data_Changed(getColumnClass(col), old_Value, newValue, col)) { return; }
                    
                    set_Row_Being_Edited(true);
                    boolean tabled_Action_Check = table_Data_Changed_Action(row, col, newValue);
                    
                    set_Row_Being_Edited(false);
                    if (! tabled_Action_Check) { return; } // Exit if Table Action Failed
                }
                
                //###########################################
                // Update Table Data
                //###########################################
                current_Table_Data.get(row).set(col, newValue);
                fireTableCellUpdated(row, col); // Notifies TableModelListeners, JTable that the value has changed = repaint
            }
            catch (Exception e)
            {
                JOptionPane.showMessageDialog(null, String.format("\n\nError Accepting Value  ' %s ' in '%s'!", table_name, newValue));
                System.err.printf("%s", e);
                
                set_Row_Being_Edited(false);
            }
        }
        
        @Override
        public boolean isCellEditable(int row, int col)
        {
            return ! un_Editable_Column_Positions.contains(col);
        }
        
        @Override
        public Class<?> getColumnClass(int c)
        {
            if (current_Table_Data.isEmpty())
            {
                return Object.class;
            }
            
            Object value = current_Table_Data.getFirst().get(c);
            return value == null ? Object.class : value.getClass();
        }
        
        public void remove_Row(int model_Row)
        {
            set_Row_Being_Edited(true);
            
            current_Table_Data.remove(model_Row);
            fireTableRowsDeleted(model_Row, model_Row); // notify JTable
            
            set_Row_Being_Edited(false);
        }
        
        public void add_Row(ArrayList<Object> new_Row_Data)
        {
            current_Table_Data.add(new_Row_Data); // Adding new Data to Data Structure backed by model
            int size = current_Table_Data.size() - 1;
            fireTableRowsInserted(size, size); // Alerts JTable has been updated and refreshes JTable
            
            resize_Object(); // resize GUI
        }
        
        public void refresh_Data()
        {
            try // Refresh Data
            {
                current_Table_Data = clone_Data(saved_Data);
            }
            catch (Exception e)
            {
                JOptionPane.showMessageDialog(null, String.format("Unable to Refresh Table - '%s'!", table_name));
                System.err.printf("\n\n%s \n%s Error -> > Failed Setting Table Data \n%s \n\n%s", lineSeparator, get_Class_And_Method_Name(), lineSeparator, e);
                return;
            }
            
            // Apply Updates
            fireTableDataChanged(); // notifies JTable to redraw everything
            resize_Object();
        }
        
        public void save_Data()
        {
            try
            {
                saved_Data = clone_Data(current_Table_Data);
            }
            catch (Exception e)
            {
                System.err.printf("\n\n%s \n%s Error -> > Failed Setting Table Data \n%s \n\n%s", lineSeparator, get_Class_And_Method_Name(), lineSeparator, e);
                JOptionPane.showMessageDialog(null, String.format("Unable to Save Table - '%s'!", table_name));
            }
        }
        
        private ArrayList<ArrayList<Object>> clone_Data(ArrayList<ArrayList<Object>> source_Data) throws Exception
        {
            // ######################################################
            // Validation Checks
            // ######################################################
            validate_source_data(get_Method_Name(3), source_Data);
            
            // ######################################################
            // Create DataSet by manually adding each Cell
            // ######################################################
            /*
             * Although this does look long alternative methods don't work
             * .clone() references the original list so still creates error when recalling savedData list
             * Datatypes like BigDecimal & LocalTime needed to be completely recreated from scratch as they
             * will still reference the original source.
             */
            
            ArrayList<ArrayList<Object>> temp_Data = new ArrayList<>();
            
            for (ArrayList<Object> reading_Row : source_Data)
            {
                ArrayList<Object> new_Row = new ArrayList<>();
                
                for (Object obj : reading_Row)
                {
                    new_Row.add(obj);
                }
                
                temp_Data.add(new_Row);
            }
            return temp_Data;
        }
    }
    
    protected abstract boolean has_Cell_Data_Changed(Class<?> type, Object old_Value, Object new_Value, int col) throws Exception;
    
    //##############################################
    // Table Model Methods
    //##############################################
    // Accessor Method
    public Object get_Value_On_Model_Data(int row, int col)
    {
        return tableModel.getValueAt(row, col);
    }
    
    private void validate_source_data(String method_name, ArrayList<?> source_Data) throws Exception
    {
        if (source_Data == null) // null data causes an error
        {
            throw new Exception(String.format("\n\n%s Error \nSource_Data cannot be null!", get_Class_And_Method_Name()));
        }
        
        int column_name_size = column_Names.size();
        
        boolean is_2D = source_Data.getFirst() instanceof ArrayList<?>;
        
        int source_data_size = is_2D ? ((ArrayList<ArrayList<?>>) source_Data).getFirst().size() : source_Data.size();
        
        if (source_data_size != column_name_size) // Source Data has to provide data for each expected column
        {
            throw new Exception(String.format("""
                    %s @ -> %s Error
                    Source_Data Column Count Mis-Match!
                    
                    Column Names (%s):
                    %s%n
                    
                    Expected: %s
                    Received:  %s
                    
                    %s%n""", get_Class_Name(), method_name, column_name_size, column_Names, column_name_size, source_data_size, source_Data));
        }
    }
    
    //###########################
    //
    //###########################
    protected void add_Row(ArrayList<Object> data)
    {
        tableModel.add_Row(data);
    }
    
    protected void delete_Row(int model_Row)
    {
        tableModel.remove_Row(model_Row);
    }
    
    protected void refresh_Data()
    {
        tableModel.refresh_Data();
    }
    
    protected void save_Data() { tableModel.save_Data(); }
    
    //###########################
    // Update Methods
    //###########################
    protected void update_Table_Cell_Value(Object data, int row, int col)
    {
        //####################################
        // Update Cell
        //####################################
        tableModel.setValueAt(data, row, col);
    }
    
    protected void update_Table_Row(ArrayList<Object> update_Data, int update_Row) throws Exception
    {
        //########################################################################
        // Validate Source Data
        //########################################################################
        try
        {
            validate_source_data(get_Method_Name(3), update_Data);
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s \n%s Error -> > Failed Setting Table Data \n%s \n\n%s", lineSeparator, get_Class_And_Method_Name(), lineSeparator, e);
            throw new Exception("\n\nFailed Update");
        }
        
        //########################################################################
        // Updating Table Info
        //########################################################################
        set_Row_Being_Edited(true); // Avoid Cell Processing
        
        for (int column_Pos = 0; column_Pos < tableModel.getColumnCount(); column_Pos++)
        {
            tableModel.setValueAt(update_Data.get(column_Pos), update_Row, column_Pos);
        }
        
        set_Row_Being_Edited(false); // Return to default
        
        //########################################################################
        // Redraw JTable
        //########################################################################
        jTable.repaint();
    }
    
    protected void set_Row_Being_Edited(boolean state)
    {
        is_row_Being_Edited = state;
    }
    
    protected abstract boolean table_Data_Changed_Action(int row_Model, int column_Model, Object newValue) throws Exception;
    
    //##################################################################################################################
    // Action Methods
    //##################################################################################################################
    protected Boolean are_You_Sure(String process)
    {
        int reply = JOptionPane.showConfirmDialog(null, String.format("Are you sure you want to %s, \nany unsaved changes will be lost in this Table! \nDo you want to %s?", process, process),
                "Notification", JOptionPane.YES_NO_OPTION); //HELLO Edit
        
        if (reply == JOptionPane.NO_OPTION || reply == JOptionPane.CLOSED_OPTION) { return false; }
        
        return true;
    }
    
    //##################################################################################################################
    // Mutator Methods
    //##################################################################################################################
    public void set_Table_Header_Font(Font font)
    {
        jTable.getTableHeader().setFont(font);
    }
    
    public void set_Table_Text_Font(Font font)
    {
        jTable.setFont(font);
    }
    
    //##################################################################################################################
    // Accessor Methods
    //##################################################################################################################
    protected int get_Rows_In_Table()
    {
        return tableModel.getRowCount();
    }
    
    public JTable get_JTable()
    {
        return jTable;
    }
    
    //##################################################################################################################
    // Resizing GUi & Add Component Methods
    //##################################################################################################################
    protected void resize_Object()
    {
        jTable.repaint();
        jTable.revalidate();
        jTable.setPreferredScrollableViewportSize(
                new Dimension(
                        jTable.getPreferredSize().width,
                        //jTable.getRowHeight() * rowsInTable +20))
                        jTable.getRowHeight() * get_Rows_In_Table())); // get rid of 20, to have the border be exact
        
        scrollPane.revalidate(); // reshapes scrollpane
        revalidate();
        
        if (parent_Container != null)
        {
            parent_Container.revalidate();
        }
    }
    
    protected static void add_To_Container
    (
            Container container,
            Component add_To_Container,
            int grid_X,
            int grid_Y,
            int grid_Width,
            int grid_Height,
            double weight_X,
            double weight_Y,
            String fill,
            String anchor
    )
    {
        gbc.gridx = grid_X;
        gbc.gridy = grid_Y;
        gbc.gridwidth = grid_Width;
        gbc.gridheight = grid_Height;
        gbc.weightx = weight_X;
        gbc.weighty = weight_Y;
        
        switch (fill.toLowerCase())
        {
            case "horizontal":
                gbc.fill = GridBagConstraints.HORIZONTAL;
                break;
            case "vertical":
                gbc.fill = GridBagConstraints.VERTICAL;
                break;
            
            case "both":
                gbc.fill = GridBagConstraints.BOTH;
                break;
        }
        
        switch (anchor.toLowerCase())
        {
            case "east":
                gbc.anchor = GridBagConstraints.EAST;
                break;
            case "west":
                gbc.anchor = GridBagConstraints.WEST;
                break;
            case "north":
                gbc.anchor = GridBagConstraints.NORTH;
                break;
            case "south":
                gbc.anchor = GridBagConstraints.SOUTH;
                break;
        }
        container.add(add_To_Container, gbc);
    }
    
    //##################################################################################################################
    // Debugging
    //##################################################################################################################
    protected String get_Class_Name()
    {
        return class_Name;
    }
    
    protected String get_Method_Name(int thread_Pos)
    {
        return String.format("%s()", Thread.currentThread().getStackTrace()[thread_Pos].getMethodName());
    }
    
    protected String get_Class_And_Method_Name()
    {
        return String.format("%s -> %s", get_Class_Name(), get_Method_Name(3));
    }
}
