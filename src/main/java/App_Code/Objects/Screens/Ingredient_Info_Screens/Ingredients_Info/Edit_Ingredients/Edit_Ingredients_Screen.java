package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Edit_Ingredients;

import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Ingredient_Name_ID_OBJ;
import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Ingredient_Type_ID_Obj;
import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.Shared_Data_Registry;
import App_Code.Objects.Gui_Objects.Field_JComboBox;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Ingredients_Screen;
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
    protected Field_JComboBox<Ingredient_Type_ID_Obj> ingredient_Type_JC;
    protected Field_JComboBox<Ingredient_Name_ID_OBJ> ingredient_Name_JC;
    
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
    
    // GUI Methods
    protected void prior_GUI_Setup()
    {
        //#########################################
        // Ingredient Type Setup
        //#########################################
        
        // Title
        JPanel ingredient_Type_Title_JP = create_Label_JP("Select Ingredient Type", new Font("Verdana", Font.PLAIN, 24));
        add_To_Container(mainCentre_JPanel, ingredient_Type_Title_JP, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
        
        //######################
        // JComboBox
        //######################
        // Create JPanel
        JPanel ingredientType_JC_JP = create_JComboBox_JP();
        add_To_Container(mainCentre_JPanel, ingredientType_JC_JP, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "horizontal", 10, 0, null);
        
        // Create JCombBox
        ingredient_Type_JC = new Field_JComboBox<>("Ingredient Type", Ingredient_Type_ID_Obj.class, shared_Data_Registry.get_Mapped_Ingredient_Types())
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
        JPanel ingredient_Name_Title_JP = create_Label_JP("Select Ingredient Name", new Font("Verdana", Font.PLAIN, 24));
        add_To_Container(mainCentre_JPanel, ingredient_Name_Title_JP, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "horizontal", 0, 0, null);
        
        //######################
        // JComboBox
        //######################
        // Create JPanel
        JPanel ingredient_Name_JC_JP = create_JComboBox_JP();
        add_To_Container(mainCentre_JPanel, ingredient_Name_JC_JP, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "horizontal", 10, 0, null);
        
        // Create JComboBox
        ingredient_Name_JC = new Field_JComboBox<>("Ingredient Name", Ingredient_Name_ID_OBJ.class, new ArrayList<Ingredient_Name_ID_OBJ>())
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
    @Override
    public void reload_Ingredient_Type_JC()
    {
        ingredient_Type_JC.reload_Items(); // Reload Main Ingredients Type JC on Page
        
        ingredients_Form.reload_Ingredients_Type_JComboBox(); // Reload on ingredients Form
    }
    
    protected void type_JC_Action_Lister_Event()
    {
        try
        {
            Integer selected_Type = ingredient_Type_JC.get_Selected_Item_ID(); // Get Selected Type ID
            
            // Get List of Ingredient Names Associated with Type
            ArrayList<Ingredient_Name_ID_OBJ> new_Ingredients = shared_Data_Registry.get_Ingredient_Names_From_Type_AL(selected_Type);
            
            ingredient_Name_JC.set_Data_AL(new_Ingredients);   // Reset Data associated with JL
            
            ingredient_Name_JC.reload_Items();// Reload List
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, "Error, retrieving Ingredient Names !");
            System.err.printf("\n\n%s", e);
            
            reset_JC();
        }
    }
    
    protected void name_JC_Action_Lister_Event()
    {
    
    }
    
    public void reset_JC()
    {
        ingredient_Name_JC.reset_JC();
        ingredient_Type_JC.reset_JC();
    }
}
