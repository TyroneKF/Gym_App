package App_Code.Objects.Gui_Objects.Combo_Boxes;

import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Storable_IDS_Parent;
import java.util.ArrayList;

public class Field_JC_Storable_ID <T extends Storable_IDS_Parent> extends Field_JComboBox<T>
{
    public Field_JC_Storable_ID(String label, Class<T> typeCast, ArrayList<T> data_AL)
    {
        super(label, typeCast, data_AL);
    }
    
    //##################################################################################################################
    // Accessor Methods
    //##################################################################################################################
    public void set_Item_By_ID(int id) throws Exception
    {
    
    }
    
    
    //##################################################################################################################
    // Acessor Methods
    //##################################################################################################################
    public Integer get_Selected_Item_ID() throws Exception
    {
        // Get Object
        T item = typeCast.cast(getSelectedItem());
        
        if (item == null) { return null; }
       
       return item.get_ID();
    }
}
