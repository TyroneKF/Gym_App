package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Edit_Ingredients;

import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Ingredient_Name_ID_OBJ;
import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Ingredient_Type_ID_OBJ;
import App_Code.Objects.Database_Objects.Fetched_Results;
import App_Code.Objects.Database_Objects.MyJDBC.MyJDBC_Sqlite;
import App_Code.Objects.Database_Objects.MyJDBC.Batch_Objects.Batch_Upload_And_Fetch_Statements;
import App_Code.Objects.Database_Objects.MyJDBC.Batch_Objects.Batch_Upload_Statements;
import App_Code.Objects.Database_Objects.MyJDBC.Statements.Fetch_Statement_Full;
import App_Code.Objects.Database_Objects.Shared_Data_Registry;
import App_Code.Objects.Gui_Objects.Combo_Boxes.Field_JCombo_Storable_ID;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Search_For_Food_Info;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Edit_Ingredients.Shop_Form.Edit_Shop_Form;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Ingredients_Info.Ingredients_Info_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Parent_Ingredients_Screen;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Edit_Ingredients_Screen extends Parent_Ingredients_Screen
{
    //##################################################################################################################
    // Constructor
    //##################################################################################################################

    // JComboBox Objects
    protected Field_JCombo_Storable_ID<Ingredient_Type_ID_OBJ> ingredient_Main_Type_JC;
    protected Field_JCombo_Storable_ID<Ingredient_Name_ID_OBJ> ingredient_Main_Name_JC;

    protected boolean
            allow_Name_Action_Listener = true,
            has_Ingredient_Name_Changed = false,
            has_Ingredient_Type_Changed = false;

    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Edit_Ingredients_Screen
    (
            Ingredients_Info_Screen ingredients_info_screen,
            MyJDBC_Sqlite db, Shared_Data_Registry shared_Data_Registry
    )
    {
        super(ingredients_info_screen, db, shared_Data_Registry);  // Super Constructor

        // GUI Editing
        search_For_Ingredient_Info.collapse_JPanel();
    }

    //##################################################################################################################
    // Methods
    //##################################################################################################################
    // GUI Methods
    @Override
    protected void create_GUI_Objects()
    {
        ingredients_Form = new Edit_Ingredients_Form(scroll_JPanel, db, shared_Data_Registry, "Edit Ingredients Info");

        shop_Form = new Edit_Shop_Form(scroll_JPanel, "Add Suppliers", this, shared_Data_Registry);

        search_For_Ingredient_Info = new Search_For_Food_Info(scroll_JPanel, ingredients_Form, "Search For Food Info");
    }

    @Override
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
        ingredient_Main_Type_JC = new Field_JCombo_Storable_ID<>("Ingredient Type", Ingredient_Type_ID_OBJ.class, shared_Data_Registry.get_Mapped_Ingredient_Types())
        {
            @Override
            protected void actionListener()
            {
                type_JC_Action_Lister_Event();
            }
        };

        ingredientType_JC_JP.add(ingredient_Main_Type_JC); // Add JC to JP

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
        ingredient_Main_Name_JC = new Field_JCombo_Storable_ID<>("Ingredient Name", Ingredient_Name_ID_OBJ.class, new ArrayList<>())
        {
            @Override
            protected void actionListener()
            {
                name_JC_Action_Lister_Event();
            }
        };

        ingredient_Name_JC_JP.add(ingredient_Main_Name_JC); // Add JC to JP

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

    //######################################################################
    // Validation Methods
    //######################################################################
    @Override
    protected boolean prior_Form_Validations()
    {
        if (! ingredient_Main_Type_JC.is_Item_Selected())
        {
            JOptionPane.showMessageDialog(null, "Select an Ingredient Type to Edit an Ingredient!");
            return false;
        }
        else if (! ingredient_Main_Name_JC.is_Item_Selected())
        {
            JOptionPane.showMessageDialog(null, "Select an Ingredient Name to Edit !");
            return false;
        }

        return true;
    }

    //######################################################################
    // Update Methods
    //######################################################################
    @Override
    public void reload_Ingredient_Type_JC()
    {
        reload_Main_Ingredient_Type_JC(); // Reload Main Ingredients Type JC on Page

        ingredients_Form.reload_Type_JComboBox(); // do main reload
    }

    private void reload_Main_Ingredient_Type_JC()
    {
        ingredient_Main_Type_JC.reload_Items(); // Reload Main Ingredients Type JC on Page
    }

    @Override
    protected boolean update_Both_Forms()
    {
        Batch_Upload_Statements upload_statements = new Batch_Upload_And_Fetch_Statements(error_msg);

        //###########################
        // Get Each Forms Update
        //###########################
        try
        {
            ingredients_Form.add_Update_Queries(upload_statements);
            shop_Form.add_Update_Queries(upload_statements);
        }
        catch (Exception e)
        {
            System.out.printf("\n\n%s", e);
            return false;
        }

        //###########################
        // Upload
        //###########################
        return db.upload_Data_Batch(upload_statements);
    }

    @Override
    protected void update_Other_Screens()
    {
        // Update Ingredients Types Objects If Changed On This Screen & related things
        if (has_Ingredient_Type_Changed) { reload_Main_Ingredient_Type_JC(); }

        // Update Ingredients Name Related Things
        if (has_Ingredient_Name_Changed) { }
    }

    //############################################
    // Shared Data Updates
    //############################################
    @Override
    protected boolean update_Shared_Data()
    {
        try
        {
            Edit_Ingredients_Form edit_Ingredients_Form = (Edit_Ingredients_Form) ingredients_Form; // Cast to Type

            // Get Ingredient Name OBj Used in Both Updates
            Ingredient_Name_ID_OBJ ingredient_name_id_obj = ingredient_Main_Name_JC.get_Selected_Item();

            // Update Ingredient Type in Shared Data if Changed
            if (edit_Ingredients_Form.has_Ingredient_Type_Changed())
            {
                if (! update_Ingredient_Type_Shared_Data(ingredient_name_id_obj, edit_Ingredients_Form)) // Failed Update
                {
                    return false;
                }
                has_Ingredient_Type_Changed = true;
            }

            if (edit_Ingredients_Form.has_Ingredient_Name_Changed()) // Update Ingredient Name in Shared Data if Changed
            {
                update_Ingredient_Name_Shared_Data(ingredient_name_id_obj, edit_Ingredients_Form);
                has_Ingredient_Name_Changed = true;
            }

            return true; // Output
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s -> \n\n%s", get_Class_And_Method_Name(), e);
        }
        return false;
    }

    protected void update_Ingredient_Name_Shared_Data(Ingredient_Name_ID_OBJ ingredient_name_id_obj, Edit_Ingredients_Form edit_ingredients_form) throws Exception
    {
        // Get New Ingredient Name Value From Form
        String txt = (String) edit_ingredients_form.get_Component_Field_Value("name");

        ingredient_name_id_obj.set_Name(txt); // Update Name Field Inside Object
    }

    protected boolean update_Ingredient_Type_Shared_Data(Ingredient_Name_ID_OBJ ingredient_name_id_obj, Edit_Ingredients_Form edit_ingredients_form) throws Exception
    {
        // Get Ingredient Type JC Associated with this field
        Ingredient_Type_ID_OBJ ingredient_type_id_obj = (Ingredient_Type_ID_OBJ) edit_ingredients_form.get_Component_Field_Value("type");

        // Change Ingredient Type on Ingredient Name
        return shared_Data_Registry.change_Ingredient_Type(ingredient_type_id_obj, ingredient_name_id_obj);
    }

    //######################################################################
    // Clear / Reset Methods
    //######################################################################
    @Override
    protected void clear_Interface() // only available to reset screen
    {
        reset_Variables(); // Reset Variables

        clear_All_Screens(); // Normal Methods

        reset_JC(); // Specific to this screen & Change JC

        resize_GUI(); // resize GUI
    }

    private void reset_Variables()
    {
        has_Ingredient_Name_Changed = false;
        has_Ingredient_Type_Changed = false;
    }

    private void reset_JC()
    {
        reset_Ingredient_Types_JC();
        reset_Ingredient_Names_JC();
    }

    private void reset_Ingredient_Names_JC()
    {
        ingredient_Main_Name_JC.reset_JC();
    }

    private void reset_Ingredient_Types_JC() { ingredient_Main_Type_JC.reset_JC(); }

    //######################################################################
    // ActionListener Methods
    //######################################################################
    private void type_JC_Action_Lister_Event()
    {
        try
        {
            allow_Name_Action_Listener = false;

            Integer selected_Type = ingredient_Main_Type_JC.get_Selected_Item_ID(); // Get Selected Type ID

            // Get List of Ingredient Names Associated with Type
            ArrayList<Ingredient_Name_ID_OBJ> new_Ingredients = shared_Data_Registry.get_Ingredient_Names_From_Type_AL(selected_Type);

            ingredient_Main_Name_JC.set_And_Load_Data(new_Ingredients);   // Set Data & Reload associated with JL

            // Clear Screen From Previous Info
            ingredients_Form.clear_Ingredients_Form();
            shop_Form.clear_Shop_Form();
            reset_Ingredient_Names_JC();
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

    //################################
    // Ingredient Name
    //###############################
    private void name_JC_Action_Lister_Event()
    {
        if (! allow_Name_Action_Listener) { return; }

        try
        {
            //############################
            // Get Ingredient DATA
            //############################
            Integer id = ingredient_Main_Name_JC.get_Selected_Item_ID();

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
        Fetched_Results query_results = new Fetched_Results();

        String error_msg = "Unable to grab Ingredient Info & Shop Data!";
        String query = "SELECT * FROM ingredients_info WHERE ingredient_id = ?";
        Object[] params = new Object[]{ id };

        Fetch_Statement_Full fetch_statement = new Fetch_Statement_Full(query, params, error_msg);

        // Get Ingredient Data
        query_results.add_2D_Result(db.get_2D_Query_AL_Object(fetch_statement, false));

        // ShopForm Data
        String query_Shop = "SELECT * FROM ingredient_in_shops WHERE ingredient_id = ?";
        Fetch_Statement_Full fetch_statement_2 = new Fetch_Statement_Full(query_Shop, new Object[]{ id }, error_msg);

        query_results.add_2D_Result(db.get_2D_Query_AL_Object(fetch_statement_2, true));

        //#########################
        // Return Data
        //#########################
        return query_results;
    }
}
