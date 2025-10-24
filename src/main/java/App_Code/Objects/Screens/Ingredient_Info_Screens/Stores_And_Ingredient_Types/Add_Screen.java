package App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Gui_Objects.CollapsibleJPanel;
import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Gui_Objects.IconPanel;
import App_Code.Objects.Gui_Objects.JTextFieldLimit;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Ingredients_Info_Screen;

import javax.swing.*;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Add_Screen extends CollapsibleJPanel
{
    //##################################################################################################################
    // Variable
    //##################################################################################################################
    
    // Objects
    protected MyJDBC db;
    protected Parent_Screen parent_Screen;
    
    // GUI Objects
    protected GridBagConstraints gbc = new GridBagConstraints();
    protected JTextField jTextField;
    protected JButton submitButton;
    protected JPanel jTextField_JP, main_JPanel, main_JPanel2;
    
    // Integer
    protected int
            charLimit = 55,
            ypos2 = 0;
    
    // String
    protected String
            jTextField_TXT,
            main_Label,
            data_Gathering_Name,
            db_ColumnName_Field,
            db_TableName,
            sql_File_Path,
            process;
    
    protected Ingredients_Info_Screen ingredient_Info_Screen;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Add_Screen(MyJDBC db, Parent_Screen parent_Screen, String btnText, int btnWidth, int btnHeight)
    {
        //########################################
        //
        //########################################
        super(parent_Screen.get_Container(), btnText, btnWidth, btnHeight);
    
        //########################################
        //
        //########################################
        this.db = db;
        this.parent_Screen = parent_Screen;
        sql_File_Path = parent_Screen.get_SQL_File_Path();
        process = parent_Screen.get_Process();
        
        ingredient_Info_Screen = parent_Screen.get_Ingredient_Info_Screen();
        
        //########################################
        //
        //########################################
        expand_JPanel();
    
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
    protected void create_Form()
    {
        //###################################################################
        // Drawing interface
        //####################################################################
        create_Add_Screen_Objects();
        creating_Additional_Add_Screen_Objects(); // for overwrite purposes
        add_Screen_Objects(); // adding all objects to the screen
    }
    
    //##############################################
    //
    //##############################################
    protected void create_Add_Screen_Objects()
    {
        main_JPanel = get_Centre_JPanel();
        main_JPanel.setLayout(new GridBagLayout());
        
        main_JPanel2 = new JPanel(new GridBagLayout());
        main_JPanel2.setBackground(Color.black);
        add_To_Container(main_JPanel, main_JPanel2, 0, 0, 1, 1, 0.25, 0.25, "horizontal", 0, 0);
        
        //#######################
        // Create Icon Bar
        //#######################
        create_Icon_Bar();
        
        //#################################################################
        //  Centre & Create Form
        //#################################################################
        jTextField_JP = new JPanel(new GridLayout());
        jTextField_JP.setPreferredSize(new Dimension(630, 50));
        jTextField_JP.setBackground(Color.red);
        
        //#######################
        // JTextField
        //#######################
        
        jTextField = new JTextField("");
        jTextField.setFont(new Font("Verdana", Font.PLAIN, 15));
        jTextField.setHorizontalAlignment(JTextField.CENTER);
        jTextField.setDocument(new JTextFieldLimit(charLimit));
        jTextField_JP.add(jTextField);
        
        //###################################################################
        // South Screen for Interface
        //####################################################################
        
        // Creating submit button
        submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Arial", Font.BOLD, 15)); // setting font
        submitButton.setPreferredSize(new Dimension(50, 50)); // width, height
        
        // creating commands for submit button to execute on
        submitButton.addActionListener(ae -> {
            submission_Btn_Action();
        });
    }
    
    //#################
    // Icon Setup
    //#################
    private void create_Icon_Bar()
    {
        //#####################################################
        // Creating area for North JPanel (Refresh Icon)
        //#####################################################
        
        JPanel iconArea = new JPanel(new GridBagLayout());
        
        IconPanel iconPanel = new IconPanel(1, 10, "East");
        JPanel iconPanelInsert = iconPanel.getIconJpanel();
        
        add_To_Container(iconArea, iconPanel.getIconAreaPanel(), 0, 0, 1, 1, 0.25, 0.25, "horizontal", 10, 0);
        
        //##########################
        // Refresh Icon
        //##########################
        int width = 35;
        int height = 35;
        
        IconButton refresh_Icon_Btn = new IconButton("/images/refresh/++refresh.png", width, height, width, height,
                "centre", "right"); // btn text is useless here , refactor
        
        JButton refresh_Btn = refresh_Icon_Btn.returnJButton();
        refresh_Icon_Btn.makeBTntransparent();
        
        refresh_Btn.addActionListener(ae -> {
            
            refresh_Btn_Action();
        });
        
        iconPanelInsert.add(refresh_Icon_Btn);
        
        add_To_Container(main_JPanel2, iconArea, 0, ypos2 += 1, 1, 1, 0.25, 0.25, "both", 0, 0);
        
        additional_Icon_Setup(iconPanelInsert);
    }
    
    protected void additional_Icon_Setup(JPanel iconPanelInsert)
    {
    }
    
    //##############################################
    protected void creating_Additional_Add_Screen_Objects()
    {
    
    }
    
    //##############################################
    protected void add_Screen_Objects()
    {
        add_To_Container(main_JPanel2, create_Label_Panel(main_Label), 0, ypos2 += 1, 1, 1, 0.25, 0.25, "both", 0, 0);
        add_To_Container(main_JPanel2, jTextField_JP, 0, ypos2 += 1, 1, 1, 0.25, 0.25, "both", 0, 0);
        
        add_To_Container(main_JPanel2, submitButton, 0, ypos2 += 1, 1, 1, 0.25, 0.25, "both", 0, 0);
        
        main_JPanel.revalidate();
        main_JPanel2.revalidate();
    }
    
    protected JPanel create_Label_Panel(String labelTXT)
    {
        //###########################
        // Create JPanel
        //###########################
        JPanel jpanel = new JPanel(new GridBagLayout());
        jpanel.setPreferredSize(new Dimension(630, 50));
        jpanel.setBackground(Color.GREEN);
        
        //###########################
        // Creating Label
        //###########################
        JLabel jLabel = new JLabel(labelTXT);
        jLabel.setFont(new Font("Verdana", Font.PLAIN, 24));
        jLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Add title JPanel to North Panel Area
        add_To_Container(jpanel, jLabel, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0);
        
        //###########################
        // Return Label
        //###########################
        return jpanel;
    }
    
    //##################################################################################################################
    // GUI Methods
    //##################################################################################################################
    protected void refresh_Btn_Action()
    {
        jTextField.setText("");
    }
    
    protected void reset_Actions()
    {
        refresh_Btn_Action();
    }
    
    //##########################################################
    // Text Formatting Methods
    //##########################################################
    protected boolean does_String_Contain_Characters(String input)
    {
        Pattern p1 = Pattern.compile("[^a-zA-Z]", Pattern.CASE_INSENSITIVE);
        Matcher m1 = p1.matcher(input.replaceAll("\\s+", ""));
        boolean b1 = m1.find();
        
        if (b1)
        {
            return true;
        }
        
        return false;
    }
    
    protected String remove_Space_And_Hidden_Chars(String stringToBeEdited)
    {
        return stringToBeEdited.trim().replaceAll("\\p{C}", ""); // remove all whitespace & hidden characters like \n
    }
    
    //##########################################################
    // Submission Actions
    //##########################################################
    protected void submission_Btn_Action()
    {
        if (validate_Form())
        {
            if (upload_Form())
            {
                update_Other_Screens();
                backup_Data_In_SQL_File();
                success_Upload_Message();
                parent_Screen.reset_Actions();
            }
            else
            {
                failure_Message();
            }
        }
    }
    
    protected boolean validate_Form()
    {
        jTextField_TXT = jTextField.getText();
        
        if (! additional_Validate_Form())
        {
            return false;
        }
        
        if (jTextField_TXT.equals(""))
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
    
    protected boolean additional_Validate_Form()
    {
        return true;
    }
    
    protected boolean upload_Form()
    {
        String query = String.format("SELECT %s  FROM %s WHERE %s = '%s';", db_ColumnName_Field, db_TableName, db_ColumnName_Field, jTextField_TXT);
        
        if (db.getSingleColumnQuery(query) != null)
        {
            JOptionPane.showMessageDialog(null, String.format("\n\n%s '' %s '' Already Exists!", data_Gathering_Name, jTextField_TXT));
            return false;
        }
        
        
        String uploadString = String.format("""
                INSERT INTO %s (%s) VALUES
                ('%s');
                """, db_TableName, db_ColumnName_Field, jTextField_TXT);
        
        if (db.uploadData_Batch_Altogether(new String[]{ uploadString }))
        {
            return true;
        }
        
        return false;
    }
    
    protected boolean backup_Data_In_SQL_File()
    {
        String txtToAdd = String.format("('%s')", jTextField_TXT);
        
        if (! (db.writeTxtToSQLFile(sql_File_Path, txtToAdd)))
        {
            JOptionPane.showMessageDialog(null, String.format("Error, backing up new %s to SQL file!", process));
            return false;
        }
        return true;
    }
    
    //########################
    // Messages
    //########################
    protected abstract void success_Upload_Message();
    
    protected abstract void failure_Message();
    
    protected abstract void update_Other_Screens();
    
    //##################################################################################################################
    // Create GUI Methods
    //##################################################################################################################
    protected void add_To_Container(Container container, Component addToContainer, int gridx, int gridy, int gridwidth,
                                    int gridheight, double weightx, double weighty, String fill, int ipady, int ipadx)
    {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        
        gbc.ipady = ipady;
        gbc.ipadx = ipadx;
        
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
        
        container.add(addToContainer, gbc);
    }
}

