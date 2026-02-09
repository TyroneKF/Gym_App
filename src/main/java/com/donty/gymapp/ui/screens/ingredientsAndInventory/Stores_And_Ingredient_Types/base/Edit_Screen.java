package com.donty.gymapp.ui.screens.ingredientsAndInventory.Stores_And_Ingredient_Types.base;

import com.donty.gymapp.ui.meta.ids.ID_Object;
import com.donty.gymapp.ui.meta.ids.Storable_Ingredient_IDS.Storable_IDS_Parent;
import com.donty.gymapp.persistence.database.batch.Batch_Upload_Statements;
import com.donty.gymapp.persistence.database.MyJDBC_Sqlite;
import com.donty.gymapp.persistence.database.statements.Fetch_Statement_Full;
import com.donty.gymapp.persistence.database.statements.Upload_Statement;
import com.donty.gymapp.persistence.database.statements.Upload_Statement_Full;
import com.donty.gymapp.persistence.Shared_Data_Registry;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.Ingredients_Info.Ingredients_Info.Ingredients_Info_Screen;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Objects;

public abstract class Edit_Screen extends Add_Screen
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    
    // GUI Objects
    protected JComboBox<Storable_IDS_Parent> jCombo_Box;
    protected JPanel jComboBox_JPanel;
    
    // String
    protected String
            label1, label2,
            id_ColumnName,
            selected_JComboBox_Item_Txt = "",
            fk_Table;
    
    // Booleans
    protected boolean item_Deleted = false;
    
    // Collections
    protected ArrayList<Integer> remove_JComboBox_Items;
    protected ArrayList<? extends Storable_IDS_Parent> jComboBox_List;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Edit_Screen(
            
            MyJDBC_Sqlite db,
            Shared_Data_Registry shared_Data_Registry,
            Ingredients_Info_Screen ingredient_Info_Screen,
            Parent_Screen parent_Screen,
            ArrayList<? extends Storable_IDS_Parent> jComboBox_List
    )
    {
        super(db, shared_Data_Registry, ingredient_Info_Screen, parent_Screen);
        setPreferredSize(new Dimension(200, 160));  // Adjust Screen Size
        
        this.jComboBox_List = jComboBox_List;
        load_JComboBox();
    }
    
    //##################################################################################################################
    // GUI Setup Methods
    //##################################################################################################################
    @Override
    protected void additional_Add_Screen_Objects()
    {
        //#########################################################
        //  JComboBox
        //#########################################################
        jComboBox_JPanel = new JPanel(new GridLayout(1, 1));
        jComboBox_JPanel.setPreferredSize(new Dimension(650, 45));
        
        jCombo_Box = new JComboBox<>();
        
        jCombo_Box.setFont(new Font("Arial", Font.PLAIN, 17)); // setting font
        ((JLabel) jCombo_Box.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER); // centre text
        
        jCombo_Box.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent ie)
            {
                if (ie.getStateChange() == ItemEvent.SELECTED)
                {
                    selected_JComboBox_Item_Txt = ((Storable_IDS_Parent) Objects.requireNonNull(jCombo_Box.getSelectedItem())).get_Name();
                }
            }
        });
        
        jComboBox_JPanel.add(jCombo_Box);
    }
    
    @Override
    protected void add_Screen_Objects()
    {
        add_To_Container(centre_JPanel, create_Label_Panel(label1), 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
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
        for (Storable_IDS_Parent id_Obj : jComboBox_List)
        {
            // If Item is in List to avoid then skip
            if (remove_JComboBox_Items.contains(id_Obj.get_ID())) { continue; }
            
            // Add Item
            jCombo_Box.addItem(id_Obj);
        }
        
        //###############################
        // Reset JComboBox
        //###############################
        jCombo_Box.setSelectedIndex(- 1);
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    protected Integer get_Selected_Item_ID()
    {
        Object item_ID_Obj = jCombo_Box.getSelectedItem();
        
        return item_ID_Obj != null ? ((Storable_IDS_Parent) item_ID_Obj).get_ID() : null;
    }
    
    protected Storable_IDS_Parent get_Selected_Item()
    {
        Object item_ID_Obj = jCombo_Box.getSelectedItem();
        
        return item_ID_Obj != null ? (Storable_IDS_Parent) item_ID_Obj : null;
    }
    
    // Form Methods
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
    protected boolean upload_DATA() throws Exception
    {
        //################################
        // Check if Value Already Exists
        //################################
        
        // Variables
        String error_msg = "Error, unable to access DB to process request!";
        String query = String.format("SELECT %s FROM %s WHERE %s = ?;", db_ColumnName_Field, db_TableName, db_ColumnName_Field);
        Object[] params = new Object[]{ jTextField_TXT };

        Fetch_Statement_Full fetch_statement = new Fetch_Statement_Full(query, params, error_msg);
        
        if (! db.get_Single_Col_Query_Obj(fetch_statement, true).isEmpty())
        {
            JOptionPane.showMessageDialog(null, String.format("\n\n%s '' %s '' Already Exists!", data_Gathering_Name, jTextField_TXT));
            return false;
        }
        
        //################################
        // Upload Query
        //################################
        Integer object_ID = get_Selected_Item_ID();
        
        String upload_Q2 = String.format("""
                UPDATE %s
                SET %s = ?
                WHERE %s = ?;""", db_TableName, db_ColumnName_Field, id_ColumnName);

        String errorMSG_Upload = String.format("Unable to Update Ingredient %s to '%s'!", data_Gathering_Name, jTextField_TXT);
        Object[] params_2 = new Object[]{ jTextField_TXT, object_ID };
        Upload_Statement_Full sql_statement = new Upload_Statement_Full(upload_Q2, params_2, errorMSG_Upload, true);

        //################################
        // Return Query Result
        //################################
        return db.upload_Data(sql_statement);
    }
    
    @Override
    protected final boolean update_Shared_DATA()
    {
        get_Selected_Item().set_Name(get_JTextField_TXT()); // Update Item Text
        
        sharedDataRegistry.sort_ID_Objects_AL(jComboBox_List); // Sort List, due to name change
        
        return true;
    }
    
    //###############################################
    // Upload Messages Output
    //###############################################
    
    /**
     * All the methods are Override by child class
     */
    @Override
    protected void success_Upload_Message()
    {
        String text = String.format("\n\nSuccessfully Changed Ingredient %s From : '%s' To '%s' in DB!", process, selected_JComboBox_Item_Txt, jTextField_TXT);
        JOptionPane.showMessageDialog(null, text);
    }
    
    @Override
    protected void failure_Upload_Message()
    {
        String text = String.format("\n\nFailed Changing Ingredient %s From : '%s' To '%s' in DB!", process, selected_JComboBox_Item_Txt, jTextField_TXT);
        JOptionPane.showMessageDialog(null, text);
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
    
    //###################################################################
    // Delete Methods
    //###################################################################
    protected void delete_Btn_Action_Listener()
    {
        //##################################
        // IS Item Selected
        //##################################
        if (jCombo_Box.getSelectedIndex() == - 1)
        {
            JOptionPane.showMessageDialog(null, String.format("Select An ' %s 'To Delete It !!!", data_Gathering_Name));
            return;
        }
        
        //##################################
        // MYSQL
        //##################################
        if (! delete_Btn_DB_Action())
        {
            JOptionPane.showMessageDialog(null, String.format("\n\nFailed To Delete Selected Item ''%s'' !!", selected_JComboBox_Item_Txt));
            return;
        }
        
        //##################################
        // Shared_DATA
        //##################################
        if (! delete_Shared_Data_Action())
        {
            JOptionPane.showMessageDialog(null, String.format("\n\nFailed To Delete Selected Item ''%s'' From GUI (Restart App) !!", selected_JComboBox_Item_Txt));
            return;
        }
        
        //##################################
        // Success MSG
        //##################################
        JOptionPane.showMessageDialog(null, String.format("\n\nSelected Item ''%s'' Has Successfully Been Deleted!!!", selected_JComboBox_Item_Txt));
        
        update_Other_Screens();
        reset_Actions();
    }
    
    private boolean delete_Btn_DB_Action()
    {
        //##################################
        // Variables
        //##################################
        Storable_IDS_Parent item_ID_Obj = get_Selected_Item();

        String errorMSG = String.format("Failed To Delete ' %s ' FROM %s !!", selected_JComboBox_Item_Txt, data_Gathering_Name);
        Batch_Upload_Statements upload_statements = new Batch_Upload_Statements(errorMSG);
        
        //##################################
        // SQL
        //##################################
        
        // Get Child Class Queries
        delete_Prior_Queries(item_ID_Obj, upload_statements);
        
        // Define Query Variables

        String upload_Q1 = String.format("DELETE FROM %s WHERE %s = ?", db_TableName, id_ColumnName);
        
        upload_statements.add_Uploads(new Upload_Statement(upload_Q1, new Object[]{ item_ID_Obj.get_ID() }, true));
        
        //##################
        // Execute Query
        //##################
        return db.upload_Data_Batch(upload_statements);
    }
    
    protected abstract void delete_Prior_Queries(ID_Object id_object, Batch_Upload_Statements upload_statements);
    
    protected abstract boolean delete_Shared_Data_Action();
}
