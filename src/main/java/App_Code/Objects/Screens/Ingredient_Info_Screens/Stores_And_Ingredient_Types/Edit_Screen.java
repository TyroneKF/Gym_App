package App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public abstract class Edit_Screen extends Add_Screen
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    
    // String
    protected String
            lable1, label2,
            id_ColumnName,
            selected_JComboBox_Item_Txt = "",
            fk_Table;
    
    // Booleans
    protected boolean item_Deleted = false;
    
    // Collections
    protected ArrayList<String> remove_JComboBox_Items = new ArrayList<>();
    protected Collection<String> jComboBox_List;
    
    // GUI Objects
    protected JComboBox<String> jCombo_Box;
    protected JPanel jComboBox_JPanel;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Edit_Screen(MyJDBC db, Parent_Screen parent_Screen)
    {
        super(db, parent_Screen);
        
        // Adjust Screen Size
        setPreferredSize(new Dimension(200, 160));
    }
    
    //##################################################################################################################
    // GUI Setup Methods
    //##################################################################################################################
    @Override
    protected void additional_Add_Screen_Objects()
    {
        //########################################################################################################
        //  JComboBox
        //########################################################################################################
        jComboBox_List = parent_Screen.get_JComboBox_List();
        
        jComboBox_JPanel = new JPanel(new GridLayout(1, 1));
        jComboBox_JPanel.setPreferredSize(new Dimension(650, 45));
        
        jCombo_Box = new JComboBox<String>();
        
        load_JComboBox();
        
        jCombo_Box.setFont(new Font("Arial", Font.PLAIN, 17)); // setting font
        ((JLabel) jCombo_Box.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER); // centre text
        
        jCombo_Box.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent ie)
            {
                if (ie.getStateChange() == ItemEvent.SELECTED)
                {
                    selected_JComboBox_Item_Txt = (String) jCombo_Box.getSelectedItem();
                }
            }
        });
        
        jComboBox_JPanel.add(jCombo_Box);
    }
    
    @Override
    protected void add_Screen_Objects()
    {
        add_To_Container(centre_JPanel, create_Label_Panel(lable1), 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
        add_To_Container(centre_JPanel, jComboBox_JPanel, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
        
        add_To_Container(centre_JPanel, new JPanel(), 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "horizontal", 10, 0, null);
        
        add_To_Container(centre_JPanel, create_Label_Panel(label2), 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
        add_To_Container(centre_JPanel, jTextField_JP, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);
        add_To_Container(centre_JPanel, submitButton, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
        
        resize_GUI();
    }
    
    protected void load_JComboBox()
    {
        //###############################
        // Clear List
        //###############################
        jCombo_Box.removeAllItems();
        
        //###############################
        // Populate List
        //###############################
        for (String list_Item : jComboBox_List)
        {
            // If Item is in List to avoid then skip
            if (remove_JComboBox_Items.contains(list_Item)) { continue; }
            
            // Add Item
            jCombo_Box.addItem(list_Item);
        }
        
        //###############################
        // Reset JComboBox
        //###############################
        jCombo_Box.setSelectedIndex(- 1);
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    // BackUp Methods
    @Override
    protected boolean backup_Data_In_SQL_File()
    {
        System.out.printf("\n\nSql File Path: %s \nSelectedJComboBox: %s \nJTextfield: %s", sql_File_Path, selected_JComboBox_Item_Txt, jTextField_TXT);
        
        if (! (db.replace_Txt_In_SQL_File(sql_File_Path, false, String.format("('%s')", selected_JComboBox_Item_Txt), String.format("('%s')", jTextField_TXT))))
        {
            JOptionPane.showMessageDialog(null, String.format("Error, changing back-up of %s in SQL file!", process));
            return false;
        }
        return true;
    }
    
    //#############################################################
    // Form Methods
    //#############################################################
    @Override
    protected boolean additional_Validate_Form()
    {
        if (jCombo_Box.getSelectedIndex() == - 1)
        {
            JOptionPane.showMessageDialog(null, String.format("\n\nSelect An %s To Edit!", data_Gathering_Name));
            return false;
        }
        return true;
    }
    
    @Override
    protected boolean upload_Form()
    {
        String query = String.format("SELECT %s  FROM %s WHERE %s = '%s';", db_ColumnName_Field, db_TableName, db_ColumnName_Field, jTextField_TXT);
        
        if (db.get_Single_Column_Query(query) != null)
        {
            JOptionPane.showMessageDialog(null, String.format("\n\n%s '' %s '' Already Exists!", data_Gathering_Name, jTextField_TXT));
            return false;
        }
        
        String mysqlVariableReference1 = "@CurrentID";
        String createMysqlVariable1 = String.format("SET %s = (SELECT %s FROM %s WHERE %s = '%s');",
                
                mysqlVariableReference1, id_ColumnName, db_TableName, db_ColumnName_Field, selected_JComboBox_Item_Txt);
        
        String uploadString = String.format("""
                        UPDATE %s
                        SET %s = '%s'
                        WHERE %s = %s;""",
                
                db_TableName, db_ColumnName_Field, jTextField_TXT, id_ColumnName, mysqlVariableReference1);
        
        if (db.upload_Data_Batch_Independently(new String[]{ createMysqlVariable1, uploadString }))
        {
            return true;
        }
        
        return false;
    }
    
    //#############################################################
    // Reset Methods
    //#############################################################
    @Override
    protected void reset_Actions()
    {
        clear_Btn_Action();
        load_JComboBox();
        item_Deleted = false;
    }
    
    @Override
    protected void clear_Btn_Action()
    {
        jTextField.setText("");
        jCombo_Box.setSelectedIndex(- 1);
        selected_JComboBox_Item_Txt = "";
    }
    
    //#############################################################
    // Delete Methods
    //#############################################################
    private boolean delete_Btn_Action()
    {
        //##########################################################################################################
        // Delete From SQL Database
        //##########################################################################################################
        String mysqlVariableReference1 = "@CurrentID";
        String createMysqlVariable1 = String.format("SET %s = (SELECT %s FROM %s WHERE %s = '%s');",
                mysqlVariableReference1, id_ColumnName, db_TableName, db_ColumnName_Field, selected_JComboBox_Item_Txt);
        
        ArrayList<String> queries = delete_Btn_Queries(mysqlVariableReference1, new ArrayList<>(Arrays.asList(createMysqlVariable1)));
        
        //##########################################################################################################
        //
        //##########################################################################################################
        if (! db.upload_Data_Batch_Independently(queries))
        {
            JOptionPane.showMessageDialog(null, String.format("\n\nFailed To Delete ' %s ' FROM %s !!", selected_JComboBox_Item_Txt, data_Gathering_Name));
            return false;
        }
        
        item_Deleted = true;
        
        //##########################################################################################################
        // Delete From BackUp SQL File
        //##########################################################################################################
        
        if (! (db.delete_Txt_In_File(sql_File_Path, String.format("('%s')", selected_JComboBox_Item_Txt))))
        {
            JOptionPane.showMessageDialog(null, String.format("\n\nError, deleteBTNAction() deleting ingredient type '%s' from backup files!", selected_JComboBox_Item_Txt));
        }
        
        //##########################################################################################################
        //
        //##########################################################################################################
        return true;
    }
    
    protected void delete_Btn_Action_Listener()
    {
        //##################################
        //
        //##################################
        if (selected_JComboBox_Item_Txt.isEmpty())
        {
            JOptionPane.showMessageDialog(null, String.format("Select An ' %s 'To Delete It !!!", data_Gathering_Name));
            return;
        }
        
        //##################################
        //
        //##################################
        if (! delete_Btn_Action())
        {
            JOptionPane.showMessageDialog(null, String.format("\n\nFailed To Delete Selected Item ''%s'' !!", selected_JComboBox_Item_Txt));
            return;
        }
        
        //##################################
        //
        //##################################
        JOptionPane.showMessageDialog(null, String.format("\n\nSelected Item ''%s'' Has Successfully Been Deleted!!!", selected_JComboBox_Item_Txt));
        
        update_Other_Screens();
        reset_Actions();
    }
    
    protected abstract ArrayList<String> delete_Btn_Queries(String mysqlVariableReference1, ArrayList<String> queries);
}
