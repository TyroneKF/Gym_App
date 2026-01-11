package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients;

import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Ingredient_Name_ID_OBJ;
import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Ingredient_Type_ID_OBJ;
import App_Code.Objects.Database_Objects.Fetched_Results;
import App_Code.Objects.Database_Objects.MyJDBC.MyJDBC_MySQL;
import App_Code.Objects.Database_Objects.Shared_Data_Registry;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Shop_Form.Shop_Form;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Ingredients_Info.Ingredients_Info_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Parent_Ingredients_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Search_For_Food_Info;
import org.javatuples.Pair;

import java.math.BigInteger;
import java.util.*;


public class Ingredients_Screen extends Parent_Ingredients_Screen
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    // Objects
    private Fetched_Results fetched_Results;
    
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Ingredients_Screen(Ingredients_Info_Screen ingredients_info_screen, MyJDBC_MySQL db, Shared_Data_Registry shared_Data_Registry)
    {
        super(ingredients_info_screen, db, shared_Data_Registry); // Super Constructor
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    // Create GUI Methods
    @Override
    protected void create_GUI_Objects()
    {
        ingredients_Form = new Ingredients_Form(scroll_JPanel, db, shared_Data_Registry, "Add Ingredients Info");
        
        shop_Form = new Shop_Form(scroll_JPanel, "Add Suppliers", this, shared_Data_Registry);
        
        search_For_Ingredient_Info = new Search_For_Food_Info(scroll_JPanel, ingredients_Form, "Search For Food Info");
    }
    
    @Override
    protected void prior_GUI_Setup()
    {
    
    }
    
    //##################################################################
    // Submission Button Actions
    //##################################################################
    
    // Validation Methods
    @Override
    protected boolean prior_Form_Validations() { return true; }
    
    //###########################
    // Update Methods
    //###########################
    @Override
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
    
    @Override
    protected boolean update_Shared_Data()
    {
        try
        {
            // Get Ingredient Name Variables
            int ingredient_ID = ((BigInteger) fetched_Results.get_1D_Result_Into_Object(0)).intValueExact();
            String ingredient_Name = (String) ingredients_Form.get_Component_Field_Value("name");
            
            // Get Ingredient Type ID OBJ
            Ingredient_Type_ID_OBJ ingredient_Type_ID_Obj = (Ingredient_Type_ID_OBJ) ingredients_Form.get_Component_Field_Value("type");
            
            // Safety Check
            if ( ingredient_Type_ID_Obj == null)
            {
                throw new Exception(String.format("""
                                Failed Getting Either Ingredient ID, Ingredient Name, Ingredient_Type_ID
                                
                                Ingredient ID : %s
                                Ingredient Name : %s
                                Ingredient_Type_ID : %s""",
                        ingredient_ID, ingredient_Name, null));
            }
            
            // Create & Add Ingredient Name OBJ
            shared_Data_Registry.add_Ingredient_Name(
                    new Ingredient_Name_ID_OBJ(
                            ingredient_ID,
                            false,
                            ingredient_Name,
                            ingredient_Type_ID_Obj
                    ),
                    true
            );
            
            return true;
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s Error \n\n%s", get_Class_And_Method_Name(), e);
            return false;
        }
    }
    
    @Override
    protected void update_Other_Screens()
    {
       ingredients_info_screen.update_Edit_Types();
    }
    
    //##################################################################
    // Clearing GUI Methods
    //##################################################################
    @Override
    protected void clear_Interface() // only available to reset screen
    {
        clear_All_Screens();
        fetched_Results = null;
        resize_GUI();
    }
    
    //##################################################################
    // Update Methods
    //##################################################################
    @Override
    public void reload_Ingredient_Type_JC()
    {
        ingredients_Form.reload_Type_JComboBox();
    }
}



    

