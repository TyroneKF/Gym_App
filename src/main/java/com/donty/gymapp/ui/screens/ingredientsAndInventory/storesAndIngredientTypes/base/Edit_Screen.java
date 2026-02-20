package com.donty.gymapp.ui.screens.ingredientsAndInventory.storesAndIngredientTypes.base;

import com.donty.gymapp.gui.controls.combobox.base.storableID.Field_JCombo_Storable_ID_Main;
import com.donty.gymapp.gui.controls.combobox.base.storableID.base.Field_JCombo_Storable_ID;
import com.donty.gymapp.ui.meta.ids.ID_Object;
import com.donty.gymapp.ui.meta.ids.storableIDs.Storable_IDS_Parent;
import com.donty.gymapp.persistence.database.batch.Batch_Upload_Statements;
import com.donty.gymapp.persistence.database.MyJDBC_Sqlite;
import com.donty.gymapp.persistence.database.statements.Fetch_Statement_Full;
import com.donty.gymapp.persistence.database.statements.Upload_Statement;
import com.donty.gymapp.persistence.database.statements.Upload_Statement_Full;
import com.donty.gymapp.persistence.Shared_Data_Registry;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.ingredientsInfo.IngredientsInfo.Ingredients_Info_Screen;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public abstract class Edit_Screen<T extends Storable_IDS_Parent> extends Add_Screen<T>
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################

    // GUI Objects
    protected Field_JCombo_Storable_ID<T> field_jc;
    protected JPanel jCombo_box_jp;

    // String
    protected String
            label1,
            label2,
            selected_JComboBox_Item_Txt = "",
            fk_Table;

    // Booleans
    protected boolean item_Deleted = false;

    // Collections
    protected ArrayList<T> jComboBox_List;

    protected Class<T> class_type;

    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Edit_Screen
    (
            MyJDBC_Sqlite db,
            Shared_Data_Registry shared_Data_Registry,
            Ingredients_Info_Screen ingredient_Info_Screen,
            Parent_Screen<T> parent_Screen,

            Class<T> class_type,
            ArrayList<T> jComboBox_List
    )
    {
        super(db, shared_Data_Registry, ingredient_Info_Screen, parent_Screen);

        this.class_type = class_type;
        this.jComboBox_List = jComboBox_List;

        setPreferredSize(new Dimension(200, 160));  // Adjust Screen Size

        // JComboBoxes
        create_JC_Box();
        load_JCombo_Box();
    }

    //##################################################################################################################
    // GUI Setup Methods
    //##################################################################################################################
    @Override
    protected void additional_Add_Screen_Objects()
    {
        // JComboBox  JPanel
        jCombo_box_jp = new JPanel(new GridLayout(1, 1));
        jCombo_box_jp.setPreferredSize(new Dimension(650, 45));
    }

    @Override
    protected void add_Screen_Objects()
    {
        add_To_Container(centre_JPanel, create_Label_Panel(label1), 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
        add_To_Container(centre_JPanel, jCombo_box_jp, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);

        add_To_Container(centre_JPanel, new JPanel(), 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "horizontal", 10, 0, null);

        add_To_Container(centre_JPanel, create_Label_Panel(label2), 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
        add_To_Container(centre_JPanel, jTextField_JP, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);
        add_To_Container(centre_JPanel, submit_button, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);

        resize_GUI();
    }

    protected void create_JC_Box()
    {
        field_jc = new Field_JCombo_Storable_ID_Main<T>("", class_type, true, jComboBox_List)
        {
            @Override
            protected void actionListener()
            {
                selected_JComboBox_Item_Txt = ((Storable_IDS_Parent) Objects.requireNonNull(field_jc.getSelectedItem())).get_Name();
            }
        };

        field_jc.setFont(new Font("Arial", Font.PLAIN, 17)); // setting font
        ((JLabel) field_jc.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER); // centre text

        jCombo_box_jp.add(field_jc);
    }

    protected void load_JCombo_Box()
    {
        field_jc.load_Items();
    }

    //##################################################################################################################
    // Methods
    //##################################################################################################################
    protected Integer get_Selected_Item_ID()
    {
        return field_jc.get_Selected_Item_ID();
    }

    protected T get_Selected_Item()
    {
        return field_jc.get_Selected_Item();
    }

    // Form Methods
    @Override
    protected boolean additional_Validate_Form()
    {
        if (! field_jc.is_Item_Selected())
        {
            JOptionPane.showMessageDialog(null, String.format("\n\nSelect An %s To Edit!", data_gathering_name));
            return false;
        }

        if (field_jt_field.get_Text().equals(selected_JComboBox_Item_Txt))
        {
            JOptionPane.showMessageDialog(null, String.format("\n\n%s also has the value of %s!", data_gathering_name, selected_JComboBox_Item_Txt));
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
        String error_msg = String.format("Error, checking if %s already exists in DB !", data_gathering_name);
        String query = String.format("SELECT %s FROM %s WHERE %s = ?;", db_column_name_field, db_table_name, db_column_name_field);
        Object[] params = new Object[]{ get_JTextField_TXT() };

        Fetch_Statement_Full fetch_statement = new Fetch_Statement_Full(query, params, error_msg);

        if (! db.get_Single_Col_Query_Obj(fetch_statement, true).isEmpty())
        {
            JOptionPane.showMessageDialog(null, String.format("\n\n%s '' %s '' Already Exists!", data_gathering_name, get_JTextField_TXT()));
            return false;
        }

        //################################
        // Upload Query
        //################################
        Integer object_ID = get_Selected_Item_ID();

        String upload_Q2 = String.format("""
                UPDATE %s
                SET %s = ?
                WHERE %s = ?;""", db_table_name, db_column_name_field, id_column_name);

        String errorMSG_Upload = String.format("Unable to Update Ingredient %s to '%s'!", data_gathering_name, get_JTextField_TXT());
        Object[] params_2 = new Object[]{ get_JTextField_TXT(), object_ID };
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
        String text = String.format("\n\nSuccessfully Changed Ingredient %s From : '%s' To '%s' in DB!", process, selected_JComboBox_Item_Txt, get_JTextField_TXT());
        JOptionPane.showMessageDialog(null, text);
    }

    @Override
    protected void failure_Upload_Message()
    {
        String text = String.format("\n\nFailed Changing Ingredient %s From : '%s' To '%s' in DB!", process, selected_JComboBox_Item_Txt, get_JTextField_TXT());
        JOptionPane.showMessageDialog(null, text);
    }

    //#############################################################
    // Reset Methods
    //#############################################################
    @Override
    protected void reset_Actions()
    {
        clear_Btn_Action();
        load_JCombo_Box();
        item_Deleted = false;
    }

    @Override
    protected void clear_Btn_Action()
    {
        field_jt_field.reset_Txt_Field();
        field_jc.reset_JC();
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
        if (! field_jc.is_Item_Selected())
        {
            JOptionPane.showMessageDialog(null, String.format("Select An ' %s 'To Delete It !!!", data_gathering_name));
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

        String errorMSG = String.format("Failed To Delete ' %s ' FROM %s !!", selected_JComboBox_Item_Txt, data_gathering_name);
        Batch_Upload_Statements upload_statements = new Batch_Upload_Statements(errorMSG);

        //##################################
        // SQL
        //##################################

        // Get Child Class Queries
        delete_Prior_Queries(item_ID_Obj, upload_statements);

        // Define Query Variables

        String upload_Q1 = String.format("DELETE FROM %s WHERE %s = ?", db_table_name, id_column_name);

        upload_statements.add_Uploads(new Upload_Statement(upload_Q1, new Object[]{ item_ID_Obj.get_ID() }, true));

        //##################
        // Execute Query
        //##################
        return db.upload_Data_Batch(upload_statements);
    }

    protected abstract void delete_Prior_Queries(ID_Object id_object, Batch_Upload_Statements upload_statements);

    protected abstract boolean delete_Shared_Data_Action();
}
