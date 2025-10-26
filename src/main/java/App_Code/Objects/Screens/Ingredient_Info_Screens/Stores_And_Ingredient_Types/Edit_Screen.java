package App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Gui_Objects.IconButton;

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
    protected String[] remove_JComboBox_Items = new String[]{};
    protected Collection<String> jComboBox_List;
    
    // GUI Objects
    protected JComboBox<String> jCombo_Box_Object;
    protected JPanel jComboBox_JPanel;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Edit_Screen(MyJDBC db, Parent_Screen parent_Screen, String btnText, int btnWidth, int btnHeight)
    {
        super(db, parent_Screen, btnText, btnWidth, btnHeight);
    }
    
    //##################################################################################################################
    // GUI Setup Methods
    //##################################################################################################################
    @Override
    protected void creating_Additional_Add_Screen_Objects()
    {
        //########################################################################################################
        //  JComboBox
        //########################################################################################################
        jComboBox_List = parent_Screen.get_JComboBox_List();
        
        jComboBox_JPanel = new JPanel(new GridLayout(1, 1));
        jComboBox_JPanel.setPreferredSize(new Dimension(650, 45));
        
        jCombo_Box_Object = new JComboBox<String>();
        
        load_JComboBox();
        
        jCombo_Box_Object.setFont(new Font("Arial", Font.PLAIN, 17)); // setting font
        ((JLabel) jCombo_Box_Object.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER); // centre text
    
        jCombo_Box_Object.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent ie)
            {
                if (ie.getStateChange() == ItemEvent.SELECTED)
                {
                    selected_JComboBox_Item_Txt = (String) jCombo_Box_Object.getSelectedItem();
                }
            }
        });
    
        jComboBox_JPanel.add(jCombo_Box_Object);
    }
    
    @Override
    protected void add_Screen_Objects()
    {
        add_To_Container(centre_JPanel, create_Label_Panel(lable1), 0, ypos2 += 1, 1, 1, 0.25, 0.25, "horizontal", 0, 0);
        add_To_Container(centre_JPanel, jComboBox_JPanel, 0, ypos2 += 1, 1, 1, 0.25, 0.25, "horizontal", 0, 0);
        
        add_To_Container(centre_JPanel, new JPanel(), 0, ypos2 += 1, 1, 1, 0.25, 0.25, "horizontal", 10, 0);
        
        add_To_Container(centre_JPanel, create_Label_Panel(label2), 0, ypos2 += 1, 1, 1, 0.25, 0.25, "horizontal", 0, 0);
        add_To_Container(centre_JPanel, jTextField_JP, 0, ypos2 += 1, 1, 1, 0.25, 0.25, "both", 0, 0);
        add_To_Container(centre_JPanel, submitButton, 0, ypos2 += 1, 1, 1, 0.25, 0.25, "horizontal", 0, 0);
        
        revalidate();
        centre_JPanel.revalidate();
    }
    
    @Override
    protected void additional_Icon_Setup(JPanel iconPanelInsert)
    {
        //###########################################
        // DELETE Icon
        //###########################################
        int width = 35;
        int height = 35;
        
        IconButton delete_Icon_Btn = new IconButton("/images/x/x.png", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor
        
        JButton delete_Btn = delete_Icon_Btn.returnJButton();
        delete_Icon_Btn.makeBTntransparent();
        
        delete_Btn.addActionListener(ae -> {
            
            delete_Btn_Action_Listener();
        });
        
        iconPanelInsert.add(delete_Icon_Btn);
    }
    
    protected void load_JComboBox()
    {
        jCombo_Box_Object.removeAllItems();
        for (String object : jComboBox_List)
        {
            jCombo_Box_Object.addItem(object);
        }
        jCombo_Box_Object.setSelectedIndex(- 1);
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    // BackUp Methods
    @Override
    protected boolean backup_Data_In_SQL_File()
    {
        System.out.printf("\n\nSql File Path: %s \nSelectedJComboBox: %s \nJTextfield: %s", sql_File_Path, selected_JComboBox_Item_Txt, jTextField_TXT);
    
        if (! (db.replaceTxtInSQLFile(sql_File_Path, false, String.format("('%s')", selected_JComboBox_Item_Txt), String.format("('%s')", jTextField_TXT))))
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
        if (jCombo_Box_Object.getSelectedIndex() == - 1)
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
        
        if (db.getSingleColumnQuery(query) != null)
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
        
        if (db.uploadData_Batch_Independently(new String[]{ createMysqlVariable1, uploadString }))
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
        refresh_Btn_Action();
        load_JComboBox();
        item_Deleted = false;
    }
    
    @Override
    protected void refresh_Btn_Action()
    {
        try
        {
            jTextField.setText("");
            jCombo_Box_Object.setSelectedIndex(- 1);
            selected_JComboBox_Item_Txt = "";
        }
        catch (Exception e)
        {
            System.out.printf("\n\n%s", e);
        }
    }
    
    //#############################################################
    // Delete Methods
    //#############################################################
    protected boolean delete_Btn_Action()
    {
        //##########################################################################################################
        // Delete From SQL Database
        //##########################################################################################################
        System.out.printf("\n#################################################################################");
    
        String mysqlVariableReference1 = "@CurrentID";
        String createMysqlVariable1 = String.format("SET %s = (SELECT %s FROM %s WHERE %s = '%s');",
                mysqlVariableReference1, id_ColumnName, db_TableName, db_ColumnName_Field, selected_JComboBox_Item_Txt);
    
        ArrayList<String> queries = delete_Btn_Queries(mysqlVariableReference1, new ArrayList<>(Arrays.asList(createMysqlVariable1)));
    
        //##########################################################################################################
        //
        //##########################################################################################################
        if (! db.uploadData_Batch_Independently(queries))
        {
            JOptionPane.showMessageDialog(null, String.format("\n\nFailed To Delete ' %s ' FROM %s !!", selected_JComboBox_Item_Txt, data_Gathering_Name));
            return false;
        }
    
        item_Deleted = true;
    
        //##########################################################################################################
        // Delete From BackUp SQL File
        //##########################################################################################################
    
        if (! (db.deleteTxtInFile(sql_File_Path, String.format("('%s')", selected_JComboBox_Item_Txt))))
        {
            JOptionPane.showMessageDialog(null, String.format("\n\nError, deleteBTNAction() deleting ingredient type '%s' from backup files!", selected_JComboBox_Item_Txt));
        }
    
        //##########################################################################################################
        //
        //##########################################################################################################
        return true;
    }
    
    private void delete_Btn_Action_Listener()
    {
        if (selected_JComboBox_Item_Txt.isEmpty())
        {
            JOptionPane.showMessageDialog(null, String.format("Select An ' %s 'To Delete It !!!", data_Gathering_Name));
            return;
        }
        
        if (delete_Btn_Action())
        {
            JOptionPane.showMessageDialog(null, String.format("\n\nSelected Item ''%s'' Has Successfully Been Deleted!!!", selected_JComboBox_Item_Txt));
            
            update_Other_Screens();
            reset_Actions();
        }
        else
        {
            JOptionPane.showMessageDialog(null, String.format("\n\nFailed To Delete Selected Item ''%s'' !!", selected_JComboBox_Item_Txt));
        }
    }
    
    protected abstract ArrayList<String> delete_Btn_Queries(String mysqlVariableReference1, ArrayList<String> queries);
}
