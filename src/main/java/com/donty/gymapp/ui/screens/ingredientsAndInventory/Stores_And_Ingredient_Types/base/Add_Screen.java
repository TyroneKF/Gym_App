package com.donty.gymapp.ui.screens.ingredientsAndInventory.Stores_And_Ingredient_Types.base;

import com.donty.gymapp.gui.controls.textfields.Field_JTxtField_String;
import com.donty.gymapp.ui.meta.ids.Storable_Ingredient_IDS.Ingredient_Type_ID_OBJ;
import com.donty.gymapp.ui.meta.ids.Storable_Ingredient_IDS.Storable_IDS_Parent;
import com.donty.gymapp.ui.meta.ids.Storable_Ingredient_IDS.Store_ID_OBJ;
import com.donty.gymapp.persistence.database.Fetched_Results;
import com.donty.gymapp.persistence.database.MyJDBC_Sqlite;
import com.donty.gymapp.persistence.database.batch.Batch_Upload_And_Fetch_Statements;
import com.donty.gymapp.persistence.database.statements.Fetch_Statement;
import com.donty.gymapp.persistence.database.statements.Fetch_Statement_Full;
import com.donty.gymapp.persistence.database.statements.Upload_Statement;
import com.donty.gymapp.persistence.Shared_Data_Registry;
import com.donty.gymapp.gui.controls.textfields.base.JTextFieldLimit;
import com.donty.gymapp.gui.base.Screen_JPanel;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.Ingredients_Info.Ingredients_Info.Ingredients_Info_Screen;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.Stores_And_Ingredient_Types.ingredientTypes.Add_Ingredient_Type;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.Stores_And_Ingredient_Types.stores.Add_Stores;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class Add_Screen<T extends Storable_IDS_Parent> extends Screen_JPanel
{
    //##################################################################################################################
    // Variable
    //##################################################################################################################

    // Objects
    protected MyJDBC_Sqlite db;
    protected Parent_Screen<T> parent_Screen;
    protected Ingredients_Info_Screen ingredient_Info_Screen;
    protected Shared_Data_Registry sharedDataRegistry;
    protected Fetched_Results fetched_Results_OBJ;

    // GUI Objects
    protected Field_JTxtField_String field_jt_field;
    protected JButton submit_button;
    protected JPanel centre_JPanel, jTextField_JP;
    protected Frame frame;

    // Integer
    protected int charLimit = 55;

    // String
    protected String
            process,
            main_Label,
            data_gathering_name,
            db_column_name_field,
            db_table_name,
            id_column_name;

    protected String class_Name = new Object() { }.getClass().getEnclosingClass().getName();

    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Add_Screen
    (
            MyJDBC_Sqlite db,
            Shared_Data_Registry shared_Data_Registry,
            Ingredients_Info_Screen ingredient_Info_Screen,
            Parent_Screen<T> parent_Screen
    )
    {
        //########################################
        //
        //########################################
        super(parent_Screen.get_Container(), false);

        //########################################
        //
        //########################################
        this.db = db;
        this.sharedDataRegistry = shared_Data_Registry;
        this.parent_Screen = parent_Screen;
        this.ingredient_Info_Screen = ingredient_Info_Screen;
        process = parent_Screen.get_Process();

        setPreferredSize(new Dimension(200, 125));

        //########################################
        //
        //########################################
        set_Screen_Variables();
        create_Form();
    }

    protected abstract void set_Screen_Variables();

    //##################################################################################################################
    // GUI Methods
    //##################################################################################################################
    private void create_Form()
    {
        create_Add_Screen_Objects(); // Drawing interface
        additional_Add_Screen_Objects(); // for overwrite purposes
        add_Screen_Objects(); // override purposes /  adding all objects to the screen
    }

    //####################################################################################
    //
    //####################################################################################
    private void create_Add_Screen_Objects()
    {
        //#################################################
        //  Centre & Create Form
        //#################################################
        get_ScrollPane_JPanel().setLayout(new GridBagLayout());

        centre_JPanel = new JPanel(new GridBagLayout());
        centre_JPanel.setBackground(Color.black);
        add_To_Container(get_ScrollPane_JPanel(), centre_JPanel, 0, 0, 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);

        //#################################################################
        //  Centre & Create Form
        //#################################################################
        jTextField_JP = new JPanel(new GridLayout());
        jTextField_JP.setPreferredSize(new Dimension(630, 40));
        jTextField_JP.setBackground(Color.red);

        //#######################
        // JTextField
        //#######################

        field_jt_field = new Field_JTxtField_String("Item", charLimit);
        field_jt_field.setFont(new Font("Verdana", Font.PLAIN, 18));
        field_jt_field.setHorizontalAlignment(JTextField.CENTER);
        field_jt_field.setDocument(new JTextFieldLimit(charLimit));
        jTextField_JP.add(field_jt_field);

        //###################################################################
        // South Screen for Interface
        //####################################################################

        // Creating submit button
        submit_button = new JButton("Submit");
        submit_button.setFont(new Font("Arial", Font.BOLD, 15)); // setting font
        submit_button.setPreferredSize(new Dimension(50, 35)); // width, height

        // creating commands for submit button to execute on
        submit_button.addActionListener(e -> {
            submission_Btn_Action();
        });
    }

    protected abstract void additional_Add_Screen_Objects();

    protected void add_Screen_Objects()
    {
        add_To_Container(centre_JPanel, create_Label_Panel(main_Label), 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
        add_To_Container(centre_JPanel, jTextField_JP, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);

        add_To_Container(centre_JPanel, submit_button, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);

        resize_GUI();
    }

    protected JPanel create_Label_Panel(String labelTXT)
    {
        //###########################
        // Create JPanel
        //###########################
        JPanel jpanel = new JPanel(new GridBagLayout());
        jpanel.setPreferredSize(new Dimension(630, 35));
        jpanel.setBackground(Color.GREEN);

        //###########################
        // Creating Label
        //###########################
        JLabel jLabel = new JLabel(labelTXT);
        jLabel.setFont(new Font("Verdana", Font.PLAIN, 20));
        jLabel.setHorizontalAlignment(JLabel.CENTER);

        // Add title JPanel to North Panel Area
        add_To_Container(jpanel, jLabel, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0, null);

        //###########################
        // Return Label
        //###########################
        return jpanel;
    }

    //##################################################################################################################
    // GUI Methods
    //##################################################################################################################
    protected void reset_Actions()
    {
        clear_Btn_Action();
    }

    protected void clear_Btn_Action()
    {
        field_jt_field.reset_Txt_Field();
    }

    //#############################################################################
    // Submission Actions
    //#############################################################################
    private void submission_Btn_Action()
    {
        // Validate
        if (! validate_Form()) { return; }

        // Execute Queries
        try
        {
            if (! upload_DATA()) { throw new Exception("Failed Update"); }

            success_Upload_Message(); // Successful Query & Update DATA
        }
        catch (Exception e)
        {
            failure_Upload_Message();

            System.err.printf("\n\n%s -> %s %s", class_Name, new Object() { }.getClass().getEnclosingMethod().getName(), e);
            return;
        }

        if (! update_Shared_DATA()) { failed_Upload_Shared_Data_Message(); }

        update_Other_Screens();

        parent_Screen.reset_Actions();

        fetched_Results_OBJ = null;
    }

    //###############################################
    // Validation Methods
    //###############################################
    private boolean validate_Form()
    {
        // Validation
        LinkedHashMap<String, ArrayList<String>> error_Map = new LinkedHashMap<>();

        field_jt_field.validation_Check(error_Map);

        if (! additional_Validate_Form())
        {
            return false;
        }

        db_Validation_Check(error_Map);

        // IF no errors returns True
        if (error_Map.isEmpty()) { return true; }

        // Display Errors / Output
        JOptionPane.showMessageDialog(null, build_Error_MSg(error_Map), "Add Form Error Msg", JOptionPane.INFORMATION_MESSAGE);
        return false;
    }

    /**
     * This is Override by child class
     */
    protected void db_Validation_Check(LinkedHashMap<String, ArrayList<String>> error_Map)
    {
        String error_msg = String.format("Error, checking if %s already exists!", data_gathering_name);

        try
        {
            String db_fetch_check = String.format("""
                    SELECT EXISTS (
                        SELECT 1
                        FROM %s
                        WHERE %s = ?
                    ) AS exists_flag;""", db_table_name, db_column_name_field);

            Fetch_Statement_Full fetch_statement = new Fetch_Statement_Full(db_fetch_check, new Object[]{ get_JTextField_TXT() }, error_msg);

            int result = (Integer) (db.get_Single_Col_Query_Obj(fetch_statement, false)).getFirst();

            boolean does_Type_exist = result == 1;

            if (! does_Type_exist) { return; } // if value doesn't exist exit

            // Add Error Msg Value Already Exists
            String value_exists = "already exists in the DB!";
            error_Map.put(data_gathering_name, new ArrayList<>(java.util.List.of(value_exists)));

        }
        catch (Exception e)
        {
            System.err.printf("%s  \n%s", get_Class_And_Method_Name(), e);

            error_Map.put(data_gathering_name, new ArrayList<>(List.of(error_msg)));
        }
    }

    protected abstract boolean additional_Validate_Form();

    protected String build_Error_MSg(LinkedHashMap<String, ArrayList<String>> error_Map)
    {
        //###############################
        // Build Error MSGs
        //###############################
        /*
         * HTML:
         * &nbsp; = space
         * <br> = line break
         * <b></b> = bold
         */

        StringBuilder error_MSG_String = new StringBuilder("<html>");

        for (Map.Entry<String, ArrayList<String>> error_Entry : error_Map.entrySet())
        {
            ArrayList<String> error_MSGs = error_Entry.getValue();

            // Singular Error MSG
            if (error_MSGs.size() == 1)
            {
                error_MSG_String.append(String.format("<br><br><b>%s&nbsp;:&nbsp;</b> %s", error_Entry.getKey(), error_MSGs.getFirst()));
                continue;
            }

            // Multiple Error Messages
            error_MSG_String.append(String.format("<br><br><b>%s:</b>", error_Entry.getKey()));

            for (String error : error_MSGs)
            {
                error_MSG_String.append(String.format("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>.</b>&nbsp; %s", error));
            }
        }

        error_MSG_String.append("<br><br></html>");

        //###############################
        // Return Output
        //###############################
        return error_MSG_String.toString();
    }

    //###############################################
    // Update Methods
    //###############################################
    protected boolean upload_DATA() throws Exception
    {
        //########################
        // Create Queries
        //########################
        String error_msg_01 = String.format("Error adding %s to DB!", data_gathering_name);
        Batch_Upload_And_Fetch_Statements batch_statements = new Batch_Upload_And_Fetch_Statements(error_msg_01);

        // Create Upload Queries
        String upload_Q1 = String.format("INSERT INTO %s (%s) VALUES (?);", db_table_name, db_column_name_field);
        batch_statements.add_Uploads(new Upload_Statement(upload_Q1, new Object[]{ get_JTextField_TXT() }, true));

        // Create Fetch Queries
        String fetch_Q1 = String.format("SELECT %s FROM %s WHERE %s = ?;", id_column_name, db_table_name, db_column_name_field);
        batch_statements.add_Fetches(new Fetch_Statement(fetch_Q1, new Object[]{ get_JTextField_TXT() }));

        //########################
        // Execute Query
        //########################
        fetched_Results_OBJ = db.upload_And_Get_Batch(batch_statements);

        //########################
        // Return
        //########################
        return (fetched_Results_OBJ != null);
    }

    protected boolean update_Shared_DATA()
    {
        //########################
        // Get ID Variable
        //########################
        String method_Name = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName());
        int id;

        try
        {
            id = ((Number) fetched_Results_OBJ.get_1D_Result_Into_Object(0)).intValue();
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s Error, \n\n%s", method_Name, e);
            return false;
        }

        //########################
        // Update Process
        //########################
        if (this instanceof Add_Stores)
        {
            sharedDataRegistry.add_Store(
                    new Store_ID_OBJ(id, false, get_JTextField_TXT()),
                    true
            );
        }
        else if (this instanceof Add_Ingredient_Type)
        {
            sharedDataRegistry.add_Ingredient_Type(
                    new Ingredient_Type_ID_OBJ(id, false, get_JTextField_TXT()),
                    true
            );
        }
        else
        {
            return false;
        }

        //########################
        // Return
        //########################
        return true;
    }

    //###############################################
    // Upload Messages Output
    //###############################################

    /**
     * All the methods are Override by child class
     */
    protected void success_Upload_Message()
    {
        String text = String.format("\n\nSuccessfully Added New Ingredient %s: '%s' To DB!", process, get_JTextField_TXT());
        JOptionPane.showMessageDialog(null, text);
    }

    protected void failure_Upload_Message()
    {
        String text = String.format("\n\nFailed Upload - Couldn't Add New Ingredient %s: '%s' To DB!", process, get_JTextField_TXT());
        JOptionPane.showMessageDialog(null, text);
    }

    protected void failed_Upload_Shared_Data_Message()
    {
        String text = String.format("\n\nFailed Updating GUI Data - Couldn't Add New Ingredient %s", process);
        JOptionPane.showMessageDialog(null, text);
    }

    //###############################################
    // Update Other Screens
    //###############################################
    protected abstract void update_Other_Screens();

    //##################################################################################################################
    // Accessor Methods
    //##################################################################################################################
    protected String get_JTextField_TXT()
    {
        return field_jt_field.get_Text();
    }
}

