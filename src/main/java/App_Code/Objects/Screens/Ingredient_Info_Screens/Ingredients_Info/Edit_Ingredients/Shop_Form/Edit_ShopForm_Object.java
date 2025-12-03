package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Edit_Ingredients.Shop_Form;

import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Store_ID_OBJ;
import App_Code.Objects.Gui_Objects.Combo_Boxes.Field_JCombo_Storable_ID;
import App_Code.Objects.Gui_Objects.Text_Fields.Parent.Field_JTxtField_Parent;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Shop_Form.ShopForm_Object;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Shop_Form.Shop_Form;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Shop_Form.Shop_Form_Binding;
import org.javatuples.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class Edit_ShopForm_Object extends ShopForm_Object
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private int pdid;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Edit_ShopForm_Object(Container parent_Container, Shop_Form shop_form, ArrayList<Store_ID_OBJ> stores, ArrayList<Object> data) throws Exception
    {
        super(parent_Container, shop_form, stores);
        set_Data(data);
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    private void set_Data(ArrayList<Object> data_AL) throws Exception
    {
        try
        {
            // Set PDID
            pdid = (int) data_AL.getFirst();
            
            // Define Each Column of object
            for (Shop_Form_Binding<?> shop_form_binding : field_Items_Map.values())
            {
                // Variables
                int pos_In_Data = shop_form_binding.get_Field_Query_Pos();
                Object data = data_AL.get(pos_In_Data);
                Component gui_Component = shop_form_binding.get_Gui_Component();
                
                // Set Data by Type
                switch (gui_Component)
                {
                    case Field_JCombo_Storable_ID<?> JCombo -> { JCombo.set_Item_By_ID((Integer) data); }
                    case Field_JTxtField_Parent<?> jTextField -> jTextField.setText(data.toString());
                    default -> throw new IllegalStateException("Unexpected value: " + gui_Component);
                }
            }
            resize_GUI();
        }
        catch (Exception e)
        {
            System.err.printf("\n\nError \n%s", e);
        }
    }
}
