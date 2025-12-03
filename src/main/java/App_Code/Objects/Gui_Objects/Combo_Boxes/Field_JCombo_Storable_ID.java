package App_Code.Objects.Gui_Objects.Combo_Boxes;

import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Storable_IDS_Parent;

import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Optional;

public class Field_JCombo_Storable_ID<T extends Storable_IDS_Parent> extends Field_JComboBox<T>
{
    public Field_JCombo_Storable_ID(String label, Class<T> typeCast, ArrayList<T> data_AL)
    {
        super(label, typeCast, data_AL);
        
        addItemListener(ie -> {
            if (ie.getStateChange() == ItemEvent.SELECTED)
            {
                actionListener();
            }
        });
    }
    
    //##################################################################################################################
    // Accessor Methods
    //##################################################################################################################
    public void set_Item_By_ID(int id) throws Exception
    {
        Optional<T> x = data_AL.stream().filter(e -> e.get_ID() == id).findFirst(); // Stream & find item in list
        
        if(x.isEmpty()) { throw new Exception("set_Item_By_ID() : Item by ID not found! ");} // if optional is empty product doesn't exist
      
        setSelectedItem(x.get()); // Set Item to selected item and get by ID
    }
    
    protected void actionListener() { }
    
    //##################################################################################################################
    // Accessor Methods
    //##################################################################################################################
    public Integer get_Selected_Item_ID()
    {
        // Get Object
        Object item = getSelectedItem();
        
        if (item == null) { return null; }
        
        return typeCast.cast(item).get_ID();
    }
    
    public boolean does_Selected_Item_ID_Equal(int id)
    {
        Integer selected_ID = get_Selected_Item_ID();
        
        return selected_ID != null && id == selected_ID;
    }
}
