package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients;

import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Ingredient_Name_ID_OBJ;
import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Ingredient_Type_ID_Obj;
import App_Code.Objects.Database_Objects.JDBC.Fetched_Results;
import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.Shared_Data_Registry;
import App_Code.Objects.Gui_Objects.Screens.Screen_JPanel;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Ingredient_Form.Ingredients_Form;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Shop_Form.Shop_Form;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Ingredients_Info_Screen;
import org.javatuples.Pair;

import javax.swing.*;
import java.awt.*;
import java.math.BigInteger;
import java.util.*;


public class Ingredients_Screen extends Screen_JPanel
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    
    // JPanel
    protected JPanel scroll_JPanel, mainCentre_JPanel;
    
    // Objects
    protected MyJDBC db;
    protected Frame frame;
    
    // Screen Objects
    protected Ingredients_Form ingredients_Form;
    protected Shop_Form shop_Form;
    protected Search_For_Food_Info search_For_Ingredient_Info;
    protected Ingredients_Info_Screen ingredients_info_screen;
    protected Shared_Data_Registry shared_Data_Registry;
    private Fetched_Results fetched_Results;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Ingredients_Screen(Ingredients_Info_Screen ingredients_info_screen, MyJDBC db, Shared_Data_Registry shared_Data_Registry)
    {
        //##########################################################
        // Super Constructor
        //##########################################################
        super(null, true, 800, 850);
        
        //##########################################################
        // Variables
        //##########################################################
        // Objects
        this.ingredients_info_screen = ingredients_info_screen;
        this.db = db;
        this.shared_Data_Registry = shared_Data_Registry;
        
        frame = ingredients_info_screen.getFrame();
        
        //#########################################################
        //   Create Screen for Interface
        //#########################################################
        scroll_JPanel = get_ScrollPane_JPanel();
        scroll_JPanel.setLayout(new BorderLayout());
        
        // Main Centre JPanel
        mainCentre_JPanel = new JPanel(new GridBagLayout());
        scroll_JPanel.add(mainCentre_JPanel, BorderLayout.CENTER);
        
        //##########################################################
        //   Create GUI Objects
        //#########################################################
        create_GUI_Objects();
        create_GUI();
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    // Create GUI Methods
    protected void create_GUI_Objects()
    {
        ingredients_Form = new Ingredients_Form(scroll_JPanel, db, shared_Data_Registry, "Add Ingredients Info");
        
        shop_Form = new Shop_Form(scroll_JPanel, "Add Suppliers", this, shared_Data_Registry);
        
        search_For_Ingredient_Info = new Search_For_Food_Info(scroll_JPanel, ingredients_Form, "Search For Food Info");
    }
    
    protected void prior_GUI_Setup()
    {
    
    }
    
    protected void create_GUI()
    {
        //#############################
        // Prior Setup
        //#############################
        prior_GUI_Setup();
        
        //#############################
        // Add Objects to GUI
        //#############################
        //Search For Ingredients Form
        add_To_Container(mainCentre_JPanel, search_For_Ingredient_Info, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);
        
        add_To_Container(mainCentre_JPanel, new JPanel(), 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 10, 0, null);
        
        add_To_Container(mainCentre_JPanel, ingredients_Form, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);
        
        add_To_Container(mainCentre_JPanel, new JPanel(), 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 10, 0, null);
        
        //#############################
        // Add shop
        //#############################
        add_To_Container(mainCentre_JPanel, shop_Form, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);
        
        
        //##############################
        //Space Divider
        //##############################
        add_To_Container(mainCentre_JPanel, new JPanel(), 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 10, 0, null);
        
        
        //###############################
        // South Screen for Interface
        //###############################
        // Creating Submit Button
        JButton submitButton = new JButton("Submit Form");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14)); // setting font
        submitButton.setPreferredSize(new Dimension(50, 50)); // width, height
        
        // creating commands for submit button to execute on
        submitButton.addActionListener(ae -> {
            submission_Btn_Action();
        });
        
        get_Main_South_JPanel().setLayout(new GridLayout(1, 1));
        get_Main_South_JPanel().add(submitButton, BorderLayout.SOUTH);
        
        //#############################
        // Resizing GUI
        //#############################
        resize_GUI();
    }
    
    //##################################################################
    // Submission Button Actions
    //##################################################################
    protected void submission_Btn_Action()
    {
        //###############################
        // Validate Screen / Forms
        //###############################
        if (! prior_Form_Validations()) { return; }
        
        boolean
                ingredients_Form_Validated = ingredients_Form.validate_Ingredients_Form(),
                shop_Form_Validated = shop_Form.validate_Form();
        
        //###############################
        // Update
        //###############################
        if (! ingredients_Form_Validated || ! shop_Form_Validated) { return; }
        
        //###############################
        // Accept : Data Formatting
        //###############################
        String
                title_Upload = "Accept Data Formatting",
                message_Upload = "upload these values as they may have been changed / adapted to fit our data type format";
        
        if (! are_You_Sure(title_Upload, message_Upload)) { return; }
        
        //#################################
        // Ask to Add Ingredient / Products
        //#################################
        String title_Create = "Create New Ingredient";
        String message_Create = "Are you sure you want to add this Ingredient?";
        
        if (! are_You_Sure(title_Create, message_Create)) { return; }
        
        //##################################
        // Update Both Forms
        //##################################
        if (! update_Both_Forms()) // MYSQL & Shared Data
        {
            JOptionPane.showMessageDialog(null, "\n\nError, Uploading Ingredients / Product Values!");
            return;
        }
        
        // Generate Update MSG depending on what was updated
        StringBuilder update_MSG = new StringBuilder("\n\nUpdated Ingredient Info & Product Info ! ");
        JOptionPane.showMessageDialog(get_Frame(), update_MSG);
        
        //##################################
        // Update Shared Data
        //##################################
        if (! update_Shared_Data()) // Update Shared Data with Fetched Results
        {
            JOptionPane.showMessageDialog(null, "Failed Adding Ingredient to GUI, Reload App will Fix Issue!");
        }
        else
        {
            JOptionPane.showMessageDialog(get_Frame(), "The ingredient updates won't appear on the mealPlan screen until this window is closed!");
            ingredients_info_screen.set_Update_IngredientInfo(true); // Update Status
        }
        
        //################################
        // Reset Form & Update GUI
        //################################
        clear_Interface();
    }
    
    //###########################
    // Validation Methods
    //###########################
    protected boolean prior_Form_Validations() { return true; }
    
    //###########################
    // Update Methods
    //###########################
    protected final LinkedHashSet<Pair<String, Object[]>> get_Update_Query_And_Params()
    {
        LinkedHashSet<Pair<String, Object[]>> queries_And_Params = new LinkedHashSet<>();
        
        //###########################
        // Get Each Forms Update
        //###########################
        try
        {
            ingredients_Form.add_Update_Queries(queries_And_Params);
            shop_Form.add_Update_Queries(queries_And_Params);
            
            return queries_And_Params;
        }
        catch (Exception e)
        {
            System.out.printf("\n\n%s", e);
            return null;
        }
    }
    
    protected boolean update_Both_Forms()
    {
        //######################
        // Create Variables
        //######################
        String errorMSG = "Error, Unable to add new Ingredient !"; // Error MSG
        
        // Get Upload Queries & Params
        LinkedHashSet<Pair<String, Object[]>> upload_Queries_And_Params = get_Update_Query_And_Params();
        
        if (upload_Queries_And_Params == null) { return false; } // IF getting elements failed, return false
        
        //######################
        // Create Fetch Query
        //######################
        LinkedHashSet<Pair<String, Object[]>> fetch_Queries_And_Params = new LinkedHashSet<>();
        fetch_Queries_And_Params.add(new Pair<>("SELECT LAST_INSERT_ID();", null));
        
        //######################
        // Return Fetched Results
        //######################
        fetched_Results = db.upload_And_Get_Batch(upload_Queries_And_Params, fetch_Queries_And_Params, errorMSG);
        
        return fetched_Results != null;
    }
    
    protected boolean update_Shared_Data()
    {
        try
        {
            // Get Ingredient Name Variables
            int ingredient_ID = ((BigInteger) fetched_Results.get_1D_Result_Into_Object(0)).intValueExact();
            String ingredient_Name = (String) ingredients_Form.get_Component_Field_Value("name");
            
            // Get Ingredient Type ID OBJ
            Ingredient_Type_ID_Obj ingredient_Type_ID_Obj = (Ingredient_Type_ID_Obj) ingredients_Form.get_Component_Field_Value("type");
            
            // Safety Check
            if (ingredient_Name == null || ingredient_Type_ID_Obj == null)
            {
                throw new Exception(String.format("""
                                Failed Getting Either Ingredient ID, Ingredient Name, Ingredient_Type_ID
                                
                                Ingredient ID : %s
                                Ingredient Name : %s
                                Ingredient_Type_ID : %s""",
                        ingredient_ID, ingredient_Name, ingredient_Type_ID_Obj != null ? ingredient_Type_ID_Obj.toString() : null));
            }
            
            // Create & Add Ingredient Name OBJ
            shared_Data_Registry.add_Ingredient_Name(
                    new Ingredient_Name_ID_OBJ(ingredient_ID, ingredient_Name, ingredient_Type_ID_Obj),
                    true
            );
            
            return true;
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s Error \n%s", get_Class_And_Method_Name(), e);
            return false;
        }
    }
    
    //##################################################################
    // Clearing GUI Methods
    //##################################################################
    protected void clear_Interface() // only available to reset screen
    {
        clear_All_Screens();
        fetched_Results = null;
        resize_GUI();
    }
    
    protected void clear_All_Screens()
    {
        clear_Search_For_Ingredient_Info_Form();
        clear_Ingredients_Form();
        clear_Shop_Form();
    }
    
    protected void clear_Search_For_Ingredient_Info_Form()
    {
        search_For_Ingredient_Info.resetFullDisplay();
    }
    
    protected void clear_Ingredients_Form()
    {
        ingredients_Form.clear_Ingredients_Form();
    }
    
    public void clear_Shop_Form()
    {
        shop_Form.clear_Shop_Form();
    }
    
    //##################################################################
    // Update Methods
    //##################################################################
    public void reload_Ingredient_Type_JC()
    {
        ingredients_Form.reload_Type_JComboBox();
    }
    
    public void reload_Stores_JC()
    {
        shop_Form.reload_Stores_JC();
    }
    
    //##################################################################
    // Accessor Methods
    //##################################################################
    public Frame get_Frame()
    {
        return frame;
    }
}



    

