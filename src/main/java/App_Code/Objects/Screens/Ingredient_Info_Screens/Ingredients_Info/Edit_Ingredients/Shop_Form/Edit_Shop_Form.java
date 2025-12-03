package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Edit_Ingredients.Shop_Form;

import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Store_ID_OBJ;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Ingredients_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Shop_Form.Shop_Form;

import java.awt.*;
import java.util.ArrayList;

public class Edit_Shop_Form extends Shop_Form
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    protected ArrayList<Edit_ShopForm_Object> edit_Shop_Form_Objects = new ArrayList<>();
    
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Edit_Shop_Form(Container parentContainer, String btn_Text, Ingredients_Screen ingredient_Screen, ArrayList<Store_ID_OBJ> stores)
    {
        super(parentContainer, btn_Text, ingredient_Screen, stores);
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    public void set_Data(ArrayList<ArrayList<Object>> data_AL) throws Exception
    {
        clear_Shop_Form(); // Clear Form
        
        if (! data_AL.isEmpty())
        {
            for (ArrayList<Object> data : data_AL)  // Add Shop Form Objects to Form
            {
                // Create ShopForm Object & Add
                Edit_ShopForm_Object edit_shop_form_object = new Edit_ShopForm_Object(input_Area_JP, this, stores, data);
                edit_Shop_Form_Objects.add(edit_shop_form_object);
                
                // Adding Object To GUI
                add_To_Container(input_Area_JP, edit_shop_form_object, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);
            }
        }
        
        resize_GUI();  // Resize GUI
    }
    
    @Override
    public void clear_Shop_Form()
    {
        edit_Shop_Form_Objects.clear();
        
        super.clear_Shop_Form();
    }
}
