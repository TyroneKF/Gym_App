package App_Code.Objects.Tables.JTable_JDBC;

// Packages to import
// https://stackoverflow.com/questions/10347983/making-a-jbutton-clickable-inside-a-jtable
//http://tips4java.wordpress.com/2009/07/12/table-button-column/

import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Ingredient_Name_ID_OBJ;
import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Ingredient_Type_ID_OBJ;
import App_Code.Objects.Database_Objects.JDBC.MyJDBC;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


public abstract class JDBC_JTable extends JPanel
{
    //##############################################
    // Objects
    //##############################################
    protected MyJDBC db;
    protected Container parent_Container;
    protected JScrollPane scrollPane = new JScrollPane();
    protected static GridBagConstraints gbc = new GridBagConstraints(); //HELLO DELETE
    
    //##############################################
    // Table Customization Variables
    //##############################################
    protected JTable jTable = new JTable();
    protected CustomTableModel tableModel;
    protected ArrayList<ArrayList<Object>> saved_Data;
    
    protected String table_Name;
    protected ArrayList<String> column_Names, gui_Column_Names;
    
    // Collection : Customisation Options
    protected ArrayList<String> col_To_Avoid_Centering;
    protected ArrayList<Integer> un_Editable_Column_Positions = new ArrayList<>();
    protected ArrayList<String> columns_To_Hide;
    
    protected LinkedHashMap<String, Integer[]> column_Names_And_Positions = new LinkedHashMap<>(); // Hello Can be removed
    /*
        Array Pos 1 = Original Position in JTable Data
        Array Pos 2 = Position after columns hidden
    */
    
    //##############################################
    // Other Variables
    //##############################################
    // String
    private final String class_Name;
    
    // Booleans
    protected boolean
            table_Initialised = false,
            add_JTable_Action,
            is_row_Being_Edited = false;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public JDBC_JTable
    (
            MyJDBC db, Container parent_Container, boolean set_Icons_Up, boolean add_JTable_Action,
            String table_Name, ArrayList<ArrayList<Object>> saved_Data, ArrayList<String> column_Names,
            ArrayList<String> un_Editable_Column_Names, ArrayList<String> col_To_Avoid_Centering, ArrayList<String> columns_To_Hide
    )
    {
        setLayout(new GridBagLayout());
        
        //##############################################################
        // Variables
        //##############################################################
        this.db = db;
        this.saved_Data = saved_Data;
        
        this.parent_Container = parent_Container;
        this.add_JTable_Action = add_JTable_Action;
        this.table_Name = table_Name;
        
        this.class_Name = this.getClass().getSimpleName();
        
        //##############################################################
        // Column Names & Their Original Positions
        //##############################################################
        this.column_Names = column_Names;
        
        // Adding column names and their original positions to the hashmap
        for (int pos = 0; pos < column_Names.size(); pos++)
        {
            column_Names_And_Positions.put(column_Names.get(pos), new Integer[]{ pos, pos });
        }
        
        //##############################################################
        
        this.col_To_Avoid_Centering = col_To_Avoid_Centering;
        this.columns_To_Hide = columns_To_Hide;
        
        //##############################################################
        // Get UnEditable Column Positions By Name
        //##############################################################
        if (un_Editable_Column_Names != null)
        {
            for (String column_Name : un_Editable_Column_Names)
            {
                un_Editable_Column_Positions.add(column_Names_And_Positions.get(column_Name)[0]);
            }
        }
        
        //################################################################
        // Reformat Column Names To be Capitalised on the Application lvl
        //################################################################
        gui_Column_Names = new ArrayList<>();
        
        for (String column_Name : column_Names)
        {
            // Get Column Name & Re-assign Re-Capitalised Value into list
            gui_Column_Names.add(Arrays.stream(column_Name.split("[ _]+"))
                    .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                    .collect(Collectors.joining("_")));
        }
        
        //################################################################
        // Table Setup With Table Data
        //################################################################
        if (saved_Data != null)
        {
            tableSetup(saved_Data, gui_Column_Names, set_Icons_Up);
        }
        else
        {
            tableSetup(new ArrayList<>(), gui_Column_Names, set_Icons_Up);
        }
        
        //##############################################################
        // Hide Columns
        //##############################################################
        SetUp_Hidden_Table_Columns(columns_To_Hide);
    }
    
    //##################################################################################################################
    // Table Model Set Up Methods
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
            this.current_Table_Data = clone_Data(input_Data);
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
            return current_Table_Data.get(row).get(col);
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
                Object old_Value = getValueAt(row, col);
                
                //###########################################
                // Exit Clauses
                //###########################################
                if (newValue == null) // EXIT Clause
                {
                    throw new Exception(String.format("\n\n%s Error \nValue Cannot be null", get_Class_And_Method_Name()));
                }
                
                //###########################################
                //
                //###########################################
                if (add_JTable_Action && ! is_row_Being_Edited)
                {
                    if (! has_Cell_Data_Changed(old_Value, newValue, col)) { return; } // If Nothing Changed Exit
                    
                    set_Row_Being_Edited(true);
                    if (! table_Data_Changed_Action(row, col, newValue)) { set_Row_Being_Edited(false); return; }
                }
                
                //###########################################
                // Update Table Data
                //###########################################
                current_Table_Data.get(row).set(col, newValue);
                fireTableCellUpdated(row, col); // Notifies TableModelListeners, JTable that the value has changed = repaint
                
                System.out.printf("\n\n%s \nValue Changed From %s -> %s", get_Class_And_Method_Name(), old_Value, newValue);
                set_Row_Being_Edited(false);
            }
            catch (Exception e)
            {
                JOptionPane.showMessageDialog(null, String.format("\n\nError Accepting Value  ' %s ' in Form!", newValue));
                System.err.printf("%s", e);
                
                set_Row_Being_Edited(false);
            }
        }
        
        private boolean has_Cell_Data_Changed(Object old_Value, Object new_Value, int col) throws Exception
        {
            //########################################
            //  Comparison For Other Types
            //########################################
            
            /*
             *  The Compared Types are only needed for cells that are editable by the user, if the cell isn't editable
             *  has_Cell_Data_Changed isn't called.
             *
             */
            
            Class<?> type = getColumnClass(col);
            
            if (type == Ingredient_Type_ID_OBJ.class) // Ingredient Type
            {
                return ! ((Ingredient_Type_ID_OBJ) old_Value).equals(((Ingredient_Type_ID_OBJ) new_Value));
            }
            else if (type == Ingredient_Name_ID_OBJ.class) // Ingredient Name
            {
                return ! ((Ingredient_Name_ID_OBJ) old_Value).equals(((Ingredient_Name_ID_OBJ) new_Value));
            }
            else if (type == BigDecimal.class) // Quantity Field = Big Decimal
            {
                return ((BigDecimal) old_Value).compareTo(((BigDecimal) new_Value)) != 0;
            }
            
            //########################################
            //  Edge Cases : Error (Unexpected Type)
            //########################################
            throw new Exception(String.format("\n\n%s Error \nUnexpected Class Type: %s", get_Class_And_Method_Name(), new_Value.getClass()));
        }
        
        @Override
        public boolean isCellEditable(int row, int col)
        {
            return true;
        }
        
        @Override
        public Class<?> getColumnClass(int c)
        {
            return getValueAt(0, c).getClass();
        }
        
        public void remove_Row(int model_Row)
        {
            current_Table_Data.remove(model_Row);
            fireTableRowsDeleted(model_Row, model_Row); // notify JTable
        }
        
        public void add_Row(ArrayList<Object> new_Row_Data)
        {
            current_Table_Data.add(new_Row_Data); // Adding new Data to Data Structure backed by model
      
            // Trigger JTable Update Methods
            int size = current_Table_Data.size() - 1;
            fireTableRowsInserted(size, size); // Alerts JTable has been updated and refreshes JTable
            resize_Object(); // resize GUI
        }
        
        public void refresh_Data()
        {
            // ######################################################
            // Clear DATA
            // ######################################################
            current_Table_Data.clear();
            current_Table_Data = clone_Data(get_Saved_Data());
            
            // ######################################################
            //
            // ######################################################
            fireTableDataChanged(); // notifies JTable to redraw everything
            resize_Object();
        }
        
        public void save_Data()
        {
            saved_Data.clear();
            saved_Data = clone_Data(current_Table_Data);
        }
        
        private ArrayList<ArrayList<Object>> clone_Data(ArrayList<ArrayList<Object>> source_Data)
        {
            /*
             * Although this does look long alternative methods dont work
             * .clone() references the original list so still creates error when recalling savedData list
             * Datatypes like BigDecimal & LocalTime needed to be completely recreated from scratch as they
             * will still reference the original source.
             */
            
            // ######################################################
            // Create DataSet by manually adding each Cell
            // ######################################################
            ArrayList<ArrayList<Object>> temp_Data = new ArrayList<>();
            
            int row_Size = source_Data.size(), column_Size = column_Names.size();
            
            for (int row = 0; row < row_Size; row++)
            {
                ArrayList<Object> reading_Row = source_Data.get(row);
                ArrayList<Object> new_Row = new ArrayList<>();
                
                for (int col = 0; col < column_Size; col++)
                {
                    new_Row.add(reading_Row.get(col));
                }
                
                temp_Data.add(new_Row);
            }
            return temp_Data;
        }
        
        protected ArrayList<ArrayList<Object>> get_TableModel_Data()
        {
            return current_Table_Data;
        }
    }
    
    protected void set_Row_Being_Edited(boolean state)
    {
        is_row_Being_Edited = state;
    }
    
    //###################################################
    // Table Setup Methods
    //###################################################
    protected abstract void extra_Table_Setup();
    
    protected void tableSetup(ArrayList<ArrayList<Object>> data, ArrayList<String> column_Names, boolean set_Icons_Up)
    {
        extra_Table_Setup();
        
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
            if (get_Columns_To_Hide() != null)
            {
                SetUp_Hidden_Table_Columns(columns_To_Hide);
            }
            
            extra_TableModel_Setup();
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
    
    protected abstract void extra_TableModel_Setup();
    
    protected void set_Table_Model_Data(ArrayList<ArrayList<Object>> table_Model_Data)
    {
        this.saved_Data = table_Model_Data;
        resize_Object();
    }
    
    //###################################################
    // Table Model Setup Methods
    //###################################################
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
    
    protected abstract boolean table_Data_Changed_Action(int row_Model, int column_Model, Object newValue) throws Exception;
    
    protected void update_Table_Cell_Value(Object data, int row, int col)
    {
        //####################################
        // Update Cell
        //####################################
        tableModel.setValueAt(data, row, col);
    }
    
    protected void update_Table(ArrayList<Object> update_Data, int update_Row)
    {
        //########################################################################
        // Updating Table Info
        //########################################################################
        for (int column_Pos = 0; column_Pos < tableModel.getColumnCount(); column_Pos++)
        {
            tableModel.setValueAt(update_Data.get(column_Pos), update_Row, column_Pos);
        }
        
        //########################################################################
        // Redraw JTable
        //########################################################################
        jTable.repaint();
    }
    
    public void refresh_Data()
    {
        tableModel.refresh_Data();
    }
    
    public void save_Data() { tableModel.save_Data(); }
    
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
    protected ArrayList<String> get_Columns_To_Hide()
    {
        return columns_To_Hide;
    }
    
    protected int get_Rows_In_Table()
    {
        return tableModel.getRowCount();
    }
    
    //######################################################
    // Get Data Methods
    //######################################################
    protected Object get_Value_On_Table(int row, int col)
    {
        return get_Current_Data().get(row).get(col);
    }
    
    protected ArrayList<ArrayList<Object>> get_Saved_Data()
    {
        return saved_Data;
    }
    
    protected ArrayList<ArrayList<Object>> get_Current_Data()
    {
        return tableModel.get_TableModel_Data();
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
    
    protected static void add_To_Container(Container container, Component add_To_Container, int grid_X, int grid_Y, int grid_Width,
                                           int grid_Height, double weight_X, double weight_Y, String fill, String anchor)
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
    public String get_Class_Name()
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
