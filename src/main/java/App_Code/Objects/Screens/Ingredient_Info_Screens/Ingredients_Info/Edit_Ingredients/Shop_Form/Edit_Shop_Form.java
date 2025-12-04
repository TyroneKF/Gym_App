package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Edit_Ingredients.Shop_Form;

import App_Code.Objects.Database_Objects.Shared_Data_Registry;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Ingredients_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Shop_Form.ShopForm_Object;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Shop_Form.Shop_Form;
import org.javatuples.Pair;
import java.awt.*;
import java.util.*;

public class Edit_Shop_Form extends Shop_Form
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    protected ArrayList<Edit_ShopForm_Object> edit_Shop_Form_Objects_AL = new ArrayList<>();
    protected int ingredient_ID;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Edit_Shop_Form(Container parentContainer, String btn_Text, Ingredients_Screen ingredient_Screen, Shared_Data_Registry shared_Data_Registry)
    {
        super(parentContainer, btn_Text, ingredient_Screen, shared_Data_Registry);
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    public void set_Data(ArrayList<ArrayList<Object>> data_AL) throws Exception
    {
        clear_Shop_Form(); // Clear Form
        
        if (! data_AL.isEmpty())
        {
            ingredient_ID = (int) data_AL.getFirst().get(1); // Set Ingredient ID
            for (ArrayList<Object> data : data_AL)  // Add Shop Form Objects to Form
            {
                // Create ShopForm Object & Add
                Edit_ShopForm_Object edit_shop_form_object = new Edit_ShopForm_Object(input_Area_JP, this, stores, data);
                edit_Shop_Form_Objects_AL.add(edit_shop_form_object);
                
                // Adding Object To GUI
                add_To_Container(input_Area_JP, edit_shop_form_object, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);
            }
        }
        
        resize_GUI();  // Resize GUI
    }
    
    @Override
    public void clear_Shop_Form()
    {
        edit_Shop_Form_Objects_AL.clear();
        
        super.clear_Shop_Form();
    }
    
    @Override
    protected String get_Ingredient_ID_SQL_Statement()
    {
        return String.format("Set %s = %s;", var_Ingredient_ID, ingredient_ID);
    }
    
    @Override
    public void add_Update_Queries_Extra(LinkedHashSet<Pair<String, Object[]>> queries_And_Params) throws Exception
    {
        //###########################################
        // Exit Clause
        //###########################################
        if (edit_Shop_Form_Objects_AL.isEmpty()) { return; }
        
        
        //###########################################
        // For Each Shop Object Update
        //###########################################
        for (Edit_ShopForm_Object edit_shopForm_object : edit_Shop_Form_Objects_AL)
        {
            edit_shopForm_object.add_Updates(queries_And_Params);
        }
    }
    
    @Override
    protected boolean validate_No_Repeat_Products(LinkedHashMap<Integer, HashSet<String>> repeated_Product_Names_By_Store)
    {
        // Combine Lists to see combinations
        ArrayList<ShopForm_Object> temp = new ArrayList<>(shop_Form_Objects);
        temp.addAll(edit_Shop_Form_Objects_AL);
        
        validate_No_Repeat_Products_By_List(temp, repeated_Product_Names_By_Store); // Validate Lists together
        
        return repeated_Product_Names_By_Store.isEmpty(); // Output if no errors appeared
    }
}
