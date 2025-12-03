package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Edit_Ingredients.Ingredients_Form;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.Shared_Data_Registry;
import App_Code.Objects.Gui_Objects.Combo_Boxes.Field_JCombo_Storable_ID;
import App_Code.Objects.Gui_Objects.Text_Fields.Parent.Field_JTxtField_Parent;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Ingredient_Form.Ingredients_Form;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Ingredient_Form.Ingredients_Form_Binding;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class Edit_Ingredients_Form extends Ingredients_Form
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    protected Integer ingredient_ID = null;
    protected ArrayList<Object> data;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Edit_Ingredients_Form(Container parentContainer, MyJDBC db, Shared_Data_Registry sharedDataRegistry, String btn_Txt)
    {
        super(parentContainer, db, sharedDataRegistry, btn_Txt);
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    @Override
    public void clear_Ingredients_Form()
    {
        super.clear_Ingredients_Form();
        ingredient_ID = null;
        data = null;
    }
    
    @Override
    protected boolean is_Ingredient_Name_In_DB() throws Exception
    {
        //##################################
        // IS Ingredient Name Null or Empty
        //####################################
        String ingredient_Name = ((Field_JTxtField_Parent<?>) field_Items_Map.get("name").get_Gui_Component()).get_Text();
        
        if (ingredient_Name == null || ingredient_Name.isEmpty()) { throw new Exception("No ingredient Created!"); }
        
        //##################################
        // Create Query
        //####################################
        String errorMSG = "Error, Failed Validating Ingredient Name in DB!";
        String query = "SELECT ingredient_id FROM ingredients_info WHERE Ingredient_Name = ? AND ingredient_id <> ?;";
        
        Object[] params = new Object[]{ ingredient_Name, ingredient_ID };
        
        //##################################
        // Execute
        //####################################
        return ! db.get_Single_Col_Query_Int(query, params, errorMSG, true).isEmpty();
    }
    
    public void set_Data(ArrayList<Object> data_AL) throws Exception
    {
        // Clear Data
        clear_Ingredients_Form();
        
        // Set Ingredient ID & remove from Arraylist
        ingredient_ID = (Integer) data_AL.getFirst();
        data_AL.removeFirst();
        data = data_AL;
        
        // Exit Clause
        int
                dataSize = data_AL.size(),
                fieldSize = field_Items_Map.size(),
                pos = 0;
        
        if (dataSize != fieldSize)
        {
            throw new Exception(String.format("Data doesn't match Field Count \nData Count : %s \nField Count: %s", dataSize, fieldSize));
        }
        
        // Set Data for each component
        for (Map.Entry<String, Ingredients_Form_Binding<?>> field_Item : field_Items_Map.entrySet())
        {
            Component component = field_Item.getValue().get_Gui_Component();
            Object data = data_AL.get(pos);
            
            if (component instanceof Field_JCombo_Storable_ID<?> jComboBox)
            {
                jComboBox.set_Item_By_ID((int) data);
            }
            else if (component instanceof Field_JTxtField_Parent<?> jTxtField)
            {
                jTxtField.setText(data.toString());
            }
            
            pos ++;
        }
        
        set_Salt_JC_To_Grams(); // Set Salt to Grams
    }
    
}
