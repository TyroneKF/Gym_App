package App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_IngredientTypes;


import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Ingredients_Info_Screen;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class Parent_Screen extends JPanel
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    // String
    protected String
            collapsible_BTN_Txt1 = "",
            collapsible_BTN_Txt2 = "",
            sql_File_Path,
            process;
    
    //############################
    // Integer
    //############################
    protected int yPos = 0;
    
    //############################
    // Collections
    //############################
    protected Collection<String> jComboBox_List;
    
    //############################
    // Objects
    //############################
    protected MyJDBC db;
    protected GridBagConstraints gbc = new GridBagConstraints();
    
    //############################
    // Screen Objects
    //############################
    protected Edit_Screen edit_Screen;
    protected Add_Screen add_Screen;
    protected Ingredients_Info_Screen ingredient_Info_Screen;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Parent_Screen(MyJDBC db, Ingredients_Info_Screen ingredients_Info_Screen, String process,
                         String collapsible_BTN_Txt1, String collapsible_BTN_Txt2,
                         Collection<String> jComboBox_List, String sql_File_Path)
    {
        //####################################
        // Variables
        //####################################
        this.db = db;      // Object
        this.ingredient_Info_Screen = ingredients_Info_Screen;     // Screen Objects
        this.jComboBox_List = jComboBox_List;  // Collections
        this.process = process;  // String
        this.sql_File_Path = sql_File_Path; // String
        this.collapsible_BTN_Txt1 = collapsible_BTN_Txt1; // String
        this.collapsible_BTN_Txt2 = collapsible_BTN_Txt2; // String
        
        //####################################
        // Create GUI
        //####################################
        set_Screen_Variables();
        create_Interface();
    }
    
    //##################################################################################################################
    // Create GUI Methods
    //##################################################################################################################
    protected void set_Screen_Variables()
    {
    
    }
    
    protected void create_Interface()
    {
        //###################################################################################
        //   Create Screen for Interface
        //###################################################################################
        setLayout(new BorderLayout());
        
        //###################################################################################
        //   Create Main Centre Screen for Interface
        //##################################################################################
        JPanel mainCentreScreen = new JPanel(new GridBagLayout());
        add(mainCentreScreen, BorderLayout.CENTER);
        
        //###########################
        // Add  Form
        //###########################
        add_To_Container(mainCentreScreen, add_Screen, 0, yPos += 1, 1, 1, 0.25, 0.25, "horizontal", 0, 0);
        
        //###########################
        // Edit  Form
        //###########################
        add_To_Container(mainCentreScreen, edit_Screen, 0, yPos += 1, 1, 1, 0.25, 0.25, "horizontal", 0, 0);
        
        //###########################
        // Space Divider
        //###########################
        JPanel jPanel = new JPanel();
        jPanel.setPreferredSize(new Dimension(630, 50));
        add_To_Container(mainCentreScreen, jPanel, 0, yPos += 1, 1, 1, 0.25, 0.25, "both", 10, 0);
        
        //###########################
        // Re-Draw GUI
        //###########################
        revalidate();
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    protected void reset_Actions()
    {
        add_Screen.refresh_Btn_Action();
        edit_Screen.load_JComboBox();
    }
    
    //#########################################
    // Get Methods
    //#########################################
    // Objects
    public JPanel get_Container()
    {
        return this;
    }
    
    // String
    public String get_SQL_File_Path()
    {
        return sql_File_Path;
    }
    
    public String get_Process()
    {
        return process;
    }
    
    // Collections
    public Collection<String> get_JComboBox_List()
    {
        return jComboBox_List;
    }
    
    //##################################################################################################################
    // Resizing & Add to GUI Methods
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
