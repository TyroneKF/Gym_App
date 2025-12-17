package App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types;

import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Ingredient_Type_ID_OBJ;
import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Store_ID_OBJ;
import App_Code.Objects.Database_Objects.JDBC.Fetched_Results;
import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.Shared_Data_Registry;
import App_Code.Objects.Gui_Objects.Text_Fields.Parent.JTextFieldLimit;
import App_Code.Objects.Gui_Objects.Screens.Screen_JPanel;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Ingredients_Info.Ingredients_Info_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Ingredient_Types.Add_Ingredient_Type;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Stores.Add_Stores;
import org.javatuples.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Add_Screen extends Screen_JPanel
{
    //##################################################################################################################
    // Variable
    //##################################################################################################################
    
    // Objects
    protected MyJDBC db;
    protected Parent_Screen parent_Screen;
    protected Ingredients_Info_Screen ingredient_Info_Screen;
    protected Shared_Data_Registry sharedDataRegistry;
    protected Fetched_Results fetched_Results_OBJ;
    
    // GUI Objects
    protected GridBagConstraints gbc = new GridBagConstraints();
    protected JTextField jTextField;
    protected JButton submitButton;
    protected JPanel centre_JPanel, jTextField_JP;
    protected Frame frame;
    
    // Integer
    protected int charLimit = 55;
    
    // String
    protected String
            jTextField_TXT,
            main_Label,
            data_Gathering_Name,
            db_ColumnName_Field,
            db_TableName,
            process;
    
    protected String class_Name = new Object() { }.getClass().getEnclosingClass().getName();
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Add_Screen(MyJDBC db, Shared_Data_Registry shared_Data_Registry, Ingredients_Info_Screen ingredient_Info_Screen, Parent_Screen parent_Screen)
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
        //###################################################################
        // Drawing interface
        //####################################################################
        create_Add_Screen_Objects();
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
        
        jTextField = new JTextField("");
        jTextField.setFont(new Font("Verdana", Font.PLAIN, 18));
        jTextField.setHorizontalAlignment(JTextField.CENTER);
        jTextField.setDocument(new JTextFieldLimit(charLimit));
        jTextField_JP.add(jTextField);
        
        //###################################################################
        // South Screen for Interface
        //####################################################################
        
        // Creating submit button
        submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Arial", Font.BOLD, 15)); // setting font
        submitButton.setPreferredSize(new Dimension(50, 35)); // width, height
        
        // creating commands for submit button to execute on
        submitButton.addActionListener(_ -> {
            submission_Btn_Action();
        });
    }
    
    //##############################################
    protected abstract void additional_Add_Screen_Objects();
    
    //##############################################
    protected void add_Screen_Objects()
    {
        add_To_Container(centre_JPanel, create_Label_Panel(main_Label), 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
        add_To_Container(centre_JPanel, jTextField_JP, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);
        
        add_To_Container(centre_JPanel, submitButton, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
        
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
    protected void clear_Btn_Action()
    {
        jTextField.setText("");
    }
    
    protected void reset_Actions()
    {
        clear_Btn_Action();
    }
    
    //#############################################################################
    // Text Formatting Methods
    //#############################################################################
    private boolean does_String_Contain_Characters(String input)
    {
        Pattern p1 = Pattern.compile("[^a-zA-Z]", Pattern.CASE_INSENSITIVE);
        Matcher m1 = p1.matcher(input.replaceAll("\\s+", ""));
        
        return m1.find();
    }
    
    private String remove_Space_And_Hidden_Chars(String stringToBeEdited)
    {
        return stringToBeEdited.trim().replaceAll("\\p{C}", ""); // remove all whitespace & hidden characters like \n
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
        jTextField_TXT = jTextField.getText();
        
        if (! additional_Validate_Form())
        {
            return false;
        }
        
        if (jTextField_TXT.isEmpty())
        {
            JOptionPane.showMessageDialog(null, String.format("\n\nAn %s Cannot Be Null!!", data_Gathering_Name));
            return false;
        }
        
        jTextField_TXT = remove_Space_And_Hidden_Chars(jTextField_TXT);
        
        if (does_String_Contain_Characters(jTextField_TXT))
        {
            JOptionPane.showMessageDialog(null, String.format("\n\nAn %s Cannot Contain Any Symbols Or, Numbers!!", data_Gathering_Name));
            return false;
        }
        
        return true;
    }
    
    /**
     * This is Override by child class
     */
    protected abstract boolean additional_Validate_Form();
    
    //###############################################
    // Update Methods
    //###############################################
    protected boolean upload_DATA() throws Exception
    {
        //########################
        // Variables
        //########################
        LinkedHashSet<Pair<String, Object[]>> upload_Queries_And_Params = new LinkedHashSet<>();
        LinkedHashSet<Pair<String, Object[]>> fetch_Queries_And_Params = new LinkedHashSet<>();
        
        String errorMSG = String.format("Error, checking if %s already exists!", db_ColumnName_Field);
        
        //########################
        // Validation Check
        //########################
        // Check if Value Already Exists
        String query = String.format("SELECT %s FROM %s WHERE %s = ?;", db_ColumnName_Field, db_TableName, db_ColumnName_Field);
        Object[] params = new Object[]{ jTextField_TXT };
        
        if (! db.get_Single_Col_Query_Obj(query, params, errorMSG, true).isEmpty())
        {
            JOptionPane.showMessageDialog(null, String.format("\n\n%s '' %s '' Already Exists!", data_Gathering_Name, jTextField_TXT));
            return false;
        }
        
        //########################
        // Create Queries
        //########################
        // Create Upload Queries
        String upload_Q1 = String.format("INSERT INTO %s (%s) VALUES (?);", db_TableName, db_ColumnName_Field);
        
        upload_Queries_And_Params.add(new Pair<>(upload_Q1, new Object[]{ jTextField_TXT }));
        
        // Create Fetch Queries
        String fetch_Q1 = "SELECT LAST_INSERT_ID();";
        fetch_Queries_And_Params.add(new Pair<>(fetch_Q1, null));
        
        //########################
        // Execute Query
        //########################
        fetched_Results_OBJ = db.upload_And_Get_Batch(upload_Queries_And_Params, fetch_Queries_And_Params, errorMSG);
        
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
            sharedDataRegistry.add_Store(new Store_ID_OBJ(id, jTextField_TXT), true);
        }
        else if (this instanceof Add_Ingredient_Type)
        {
            sharedDataRegistry.add_Ingredient_Type(new Ingredient_Type_ID_OBJ(id, jTextField_TXT), true);
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
        String text = String.format("\n\nSuccessfully Added New Ingredient %s: '%s' To DB!", process, jTextField_TXT);
        JOptionPane.showMessageDialog(null, text);
    }
    
    protected void failure_Upload_Message()
    {
        String text = String.format("\n\nFailed Upload - Couldn't Add New Ingredient %s: '%s' To DB!", process, jTextField_TXT);
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
        return jTextField_TXT;
    }
}

