package App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types;

import App_Code.Objects.Data_Objects.Storable_Ingredient_IDS.Storable_IDS_Parent;
import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.Shared_Data_Registry;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Ingredients_Info_Screen;
import org.javatuples.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.LinkedHashSet;
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
            lable1, label2,
            id_ColumnName,
            selected_JComboBox_Item_Txt = "",
            fk_Table;
    
    // Booleans
    protected boolean item_Deleted = false;
    
    // Collections
    protected ArrayList<? extends Storable_IDS_Parent> remove_JComboBox_Items = new ArrayList<>();
    protected ArrayList<? extends Storable_IDS_Parent> jComboBox_List;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Edit_Screen(MyJDBC db, Shared_Data_Registry shared_Data_Registry, Ingredients_Info_Screen ingredient_Info_Screen, Parent_Screen parent_Screen)
    {
        super(db, shared_Data_Registry, ingredient_Info_Screen, parent_Screen);
       
        setPreferredSize(new Dimension(200, 160));  // Adjust Screen Size
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
        
        jCombo_Box = new JComboBox<>();
        
        load_JComboBox();
        
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
        for (Storable_IDS_Parent id_Obj: jComboBox_List)
        {
            // If Item is in List to avoid then skip
            if (remove_JComboBox_Items.contains(id_Obj)) { continue; }
            
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
    protected boolean upload_DATA()
    {
        //################################
        // Check if Value Already Exists
        //################################
        String
                errorMSG = "Error, unable to edit Ingredients Info / Shop Info",
                query = String.format("SELECT %s FROM %s WHERE %s = ?;", db_ColumnName_Field, db_TableName, db_ColumnName_Field);
        
        Object[] params = new Object[]{ jTextField_TXT };
        
        if (db.get_Single_Col_Query_Obj(query, params, errorMSG) != null)
        {
            JOptionPane.showMessageDialog(null, String.format("\n\n%s '' %s '' Already Exists!", data_Gathering_Name, jTextField_TXT));
            return false;
        }
        
        //################################
        // Upload Query
        //################################
        String mysqlVariableReference1 = "@CurrentID";
        String createMysqlVariable1 = String.format("SET %s = (SELECT %s FROM %s WHERE %s = ?);",
                mysqlVariableReference1, id_ColumnName, db_TableName, db_ColumnName_Field);
        
        String uploadString = String.format("""
                        UPDATE %s
                        SET %s = ?
                        WHERE %s = %s;""",
                db_TableName,
                db_ColumnName_Field,
                id_ColumnName, mysqlVariableReference1);
        
        //################################
        // Return Query Result
        //################################
        String errorMSG_Upload = String.format("Unable to Update Ingredient %s to '%s'!", data_Gathering_Name, jTextField_TXT);
        
        LinkedHashSet<Pair<String, Object[]>> queries_And_Params = new LinkedHashSet<>()
        {{
            add(new Pair<>(createMysqlVariable1, new Object[]{ selected_JComboBox_Item_Txt }));
            add(new Pair<>(uploadString, new Object[]{ jTextField_TXT, }));
        }};
        
        return db.upload_Data_Batch2(queries_And_Params, errorMSG_Upload);
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
    
    private boolean delete_Btn_Action()
    {
        //##################################
        // SQL Variables
        //##################################
        String
                mysqlVariableReference1 = "@CurrentID",
                createMysqlVariable1 = String.format("SET %s = (SELECT %s FROM %s WHERE %s = ?);",
                        mysqlVariableReference1, id_ColumnName, db_TableName, db_ColumnName_Field);
        
        String errorMSG1 = String.format("\n\nFailed To Delete ' %s ' FROM %s !!", selected_JComboBox_Item_Txt, data_Gathering_Name);
        
        // Generate Queries
        LinkedHashSet<Pair<String, Object[]>> queries_And_Params = new LinkedHashSet<>()
        {{
            add(new Pair<>(createMysqlVariable1, new Object[]{ selected_JComboBox_Item_Txt }));
        }};
        
      
        
        //##################################
        // Execute Query
        //##################################
        if (! db.upload_Data_Batch2(queries_And_Params, errorMSG1)) { return false; }
        
        item_Deleted = true;
        
        //##################################
        // Return Value
        //##################################
        return true;
    }
}
