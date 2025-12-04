package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Edit_Ingredients;

import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Ingredient_Name_ID_OBJ;
import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Ingredient_Type_ID_Obj;
import App_Code.Objects.Database_Objects.JDBC.Fetched_Results;
import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.Shared_Data_Registry;
import App_Code.Objects.Gui_Objects.Combo_Boxes.Field_JCombo_Storable_ID;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Ingredients_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Search_For_Food_Info;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Edit_Ingredients.Ingredients_Form.Edit_Ingredients_Form;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Edit_Ingredients.Shop_Form.Edit_Shop_Form;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Ingredients_Info_Screen;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Edit_Ingredients_Screen extends Ingredients_Screen
{
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    
    // JComboBox Objects
    protected Field_JCombo_Storable_ID<Ingredient_Type_ID_Obj> ingredient_Type_JC;
    protected Field_JCombo_Storable_ID<Ingredient_Name_ID_OBJ> ingredient_Name_JC;
    
    protected boolean allow_Name_Action_Listener = true;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Edit_Ingredients_Screen(Ingredients_Info_Screen ingredients_info_screen, MyJDBC db, Shared_Data_Registry shared_Data_Registry)
    {
        //###############################
        // Super Constructor
        //###############################
        super(ingredients_info_screen, db, shared_Data_Registry);
        
        //###############################
        // GUI Editing
        //###############################
        search_For_Ingredient_Info.collapse_JPanel();
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    
    @Override
    protected void create_GUI_Objects()
    {
        ingredients_Form = new Edit_Ingredients_Form(scroll_JPanel, db, shared_Data_Registry, "Edit Ingredients Info");
        
        shop_Form = new Edit_Shop_Form(scroll_JPanel, "Add Suppliers", this, shared_Data_Registry);
        
        search_For_Ingredient_Info = new Search_For_Food_Info(scroll_JPanel, ingredients_Form, "Search For Food Info");
    }
    
    @Override
    public void reload_Ingredient_Type_JC()
    {
        ingredient_Type_JC.reload_Items(); // Reload Main Ingredients Type JC on Page
        
        ingredients_Form.reload_Ingredients_Type_JComboBox(); // Reload on ingredients Form
    }
    
    private void reset_JC()
    {
        ingredient_Name_JC.reset_JC();
        ingredient_Type_JC.reset_JC();
    }
    
    @Override
    protected void clear_Interface() // only available to reset screen
    {
        reset_JC(); // Change JC
        super.clear_Interface(); // Parent Clean
    }
    
    //##############################################
    // Validation Methods
    //##############################################
    protected boolean prior_Form_Validations()
    {
        if (! ingredient_Type_JC.is_Item_Selected())
        {
            JOptionPane.showMessageDialog(null, "Select an Ingredient Type to Edit an Ingredient!");
            return false;
        }
        else if (! ingredient_Name_JC.is_Item_Selected())
        {
            JOptionPane.showMessageDialog(null, "Select an Ingredient Name to Edit !");
            return false;
        }
        
        return true;
    }
    
    //##############################################
    // GUI Methods
    //##############################################
    protected void prior_GUI_Setup()
    {
        //#########################################
        // Ingredient Type Setup
        //#########################################
        
        // Title
        JPanel ingredient_Type_Title_JP = create_Label_JP("Select Ingredient Type", new Font("Verdana", Font.PLAIN, 24), Color.GREEN);
        add_To_Container(mainCentre_JPanel, ingredient_Type_Title_JP, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
        
        //######################
        // JComboBox
        //######################
        // Create JPanel
        JPanel ingredientType_JC_JP = create_JComboBox_JP();
        add_To_Container(mainCentre_JPanel, ingredientType_JC_JP, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "horizontal", 10, 0, null);
        
        // Create JCombBox
        ingredient_Type_JC = new Field_JCombo_Storable_ID<>("Ingredient Type", Ingredient_Type_ID_Obj.class, shared_Data_Registry.get_Mapped_Ingredient_Types())
        {
            @Override
            protected void actionListener()
            {
                type_JC_Action_Lister_Event();
            }
        };
        
        ingredientType_JC_JP.add(ingredient_Type_JC); // Add JC to JP
        
        //######################
        //  Add Space Divider
        //######################
        add_To_Container(mainCentre_JPanel, new JPanel(), 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 10, 0, null);
        
        //#########################################
        // Ingredient Name Setup
        //#########################################
        // Title
        JPanel ingredient_Name_Title_JP = create_Label_JP("Select Ingredient Name", new Font("Verdana", Font.PLAIN, 24), Color.GREEN);
        add_To_Container(mainCentre_JPanel, ingredient_Name_Title_JP, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
        
        //######################
        // JComboBox
        //######################
        // Create JPanel
        JPanel ingredient_Name_JC_JP = create_JComboBox_JP();
        add_To_Container(mainCentre_JPanel, ingredient_Name_JC_JP, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "horizontal", 10, 0, null);
        
        // Create JComboBox
        ingredient_Name_JC = new Field_JCombo_Storable_ID<>("Ingredient Name", Ingredient_Name_ID_OBJ.class, new ArrayList<>())
        {
            @Override
            protected void actionListener()
            {
                name_JC_Action_Lister_Event();
            }
        };
        
        ingredient_Name_JC_JP.add(ingredient_Name_JC); // Add JC to JP
        
        //####################
        //  Add Space Divider
        //####################
        add_To_Container(mainCentre_JPanel, new JPanel(), 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 10, 0, null);
    }
    
    private JPanel create_JComboBox_JP()
    {
        JPanel jPanel = new JPanel(new GridLayout(1, 1));
        jPanel.setPreferredSize(new Dimension(650, 50));
        jPanel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        
        return jPanel;
    }
    
    //##############################################
    // ActionListener
    //##############################################
    private void type_JC_Action_Lister_Event()
    {
        try
        {
            allow_Name_Action_Listener = false;
            
            Integer selected_Type = ingredient_Type_JC.get_Selected_Item_ID(); // Get Selected Type ID
            
            // Get List of Ingredient Names Associated with Type
            ArrayList<Ingredient_Name_ID_OBJ> new_Ingredients = shared_Data_Registry.get_Ingredient_Names_From_Type_AL(selected_Type);
            
            ingredient_Name_JC.set_And_Load_Data(new_Ingredients);   // Set Data & Reload associated with JL
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, "Error, retrieving Ingredient Names !");
            System.err.printf("\n\n%s", e);
            
            reset_JC();
        }
        finally
        {
            allow_Name_Action_Listener = true;
        }
    }
    
    //######################
    // Ingredient Name
    //######################
    private void name_JC_Action_Lister_Event()
    {
        if (! allow_Name_Action_Listener) { return; }
        
        try
        {
            //############################
            // Get Ingredient DATA
            //############################
            Integer id = ingredient_Name_JC.get_Selected_Item_ID();
            
            if (id == null) { throw new Exception("Error, getting Ingredient ID!"); }
            
            Fetched_Results fetched_Results = get_Ingredients_Info(id);
            
            //#############################
            // Get Ingredient / Shop  Data
            //#############################
            /*
                Ingredient_Info Table:
             
                ingredient_id, measurement_id, ingredient_name, ingredient_type_id, based_on_quantity, glycemic_index, protein,
                carbohydrates, sugars_of_carbs, fibre, fat, saturated_fat, salt, water_content, liquid_content, calories
             */
            
            ArrayList<Object> ingredient_Data = fetched_Results.get_Result_1D_AL(0); // Get Data in 1D AL Form
            
            //#############################
            // Ingredients Form
            //#############################
            System.out.printf("\n\n Ingredients Info: \n%s%n", ingredient_Data);
            
            ((Edit_Ingredients_Form) ingredients_Form).set_Data(ingredient_Data); // Set Data on form
            
            //#############################
            // ShopForm
            //#############################
            ArrayList<ArrayList<Object>> shop_Data = fetched_Results.get_Fetched_Result_2D_AL(1); // Get Data in 1D AL Form
            System.out.printf("\n\n Shop Info: \n%s%n", shop_Data);
            
            ((Edit_Shop_Form) shop_Form).set_Data(shop_Data);
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, "Error, getting Ingredient Info!");
            System.err.printf("\n\n%s", e);
            reset_JC();
        }
    }
    
    private Fetched_Results get_Ingredients_Info(int id) throws Exception
    {
        //#########################
        // Get DATA
        //#########################
        Fetched_Results queryResults = new Fetched_Results();
        String error_MSG = "Unable to grab Ingredient Info & Shop Data!";
        
        // Get Ingredient Data
        String query_Get_Ingredient_Data = "SELECT * FROM ingredients_info WHERE ingredient_id = ?";
        queryResults.add_2D_Result(db.get_2D_Query_AL_Object(query_Get_Ingredient_Data, new Object[]{ id }, error_MSG, false));
        
        // ShopForm Data
        String query_Shop = "SELECT * FROM ingredient_in_shops WHERE ingredient_id = ?";
        queryResults.add_2D_Result(db.get_2D_Query_AL_Object(query_Shop, new Object[]{ id }, error_MSG, true));
        
        //#########################
        // Return Data
        //#########################
        return queryResults;
    }
}
