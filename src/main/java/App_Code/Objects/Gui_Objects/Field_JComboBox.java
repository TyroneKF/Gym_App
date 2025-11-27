package App_Code.Objects.Gui_Objects;

import App_Code.Objects.Data_Objects.ID_Objects.ID_Object;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class Field_JComboBox<T> extends JComboBox<T>
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    protected ArrayList<T> data_AL;
    protected Class<T> typeCast;
    protected String label;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Field_JComboBox(String label, Class<T> typeCast, ArrayList<T> data_AL)
    {
        //############################
        // Variables
        //############################
        this.label = Objects.requireNonNull(label, "Label cannot be null");
        this.data_AL = Objects.requireNonNull(data_AL, "Data cannot be null");
        this.typeCast = Objects.requireNonNull(typeCast, "Type Cast cannot be null");
        
        //############################
        // JComboBox Setup
        //############################
        // Centre JComboBox Item
        DefaultListCellRenderer listRenderer = new DefaultListCellRenderer();
        listRenderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER); // center-aligned items
        setRenderer(listRenderer);
        
        //############################
        // Set Model
        //############################
        DefaultComboBoxModel<T> model = new DefaultComboBoxModel<>();
        setModel(model);
        
        //############################
        // Load List
        //############################
        reload_Items();
        
        //############################
        // ActionListener
        //############################
        addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent ie)
            {
                if (ie.getStateChange() == ItemEvent.SELECTED)
                {
                    actionListener();
                }
            }
        });
        
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    protected void actionListener(){ }
    
    public void set_Data_AL(ArrayList<T> data_AL)
    {
        this.data_AL = Objects.requireNonNull(data_AL, "Data cannot be null");
    }
    
    public void reload_Items()
    {
        //############################
        // Get Previous Item
        //############################
        T selected_Item = typeCast.cast(getSelectedItem());
        
        boolean
                item_Not_Null = selected_Item != null,
                item_in_List = false;
        
        //############################
        // Load List
        //############################
        DefaultComboBoxModel<T> model = (DefaultComboBoxModel<T>) getModel();
        
        model.removeAllElements(); // Remove all Elements
        
        for (T item : data_AL) // Populate Model From AL that's updated
        {
            if (item_Not_Null && ! item_in_List && item.equals(selected_Item)) { item_in_List = true; } // Check if selected item is in list
            
            model.addElement(item);
        }
        
        //############################
        // Set Selected Item
        //############################
        if (item_Not_Null && item_in_List)  // Set Item back to original Item
        {
            setSelectedItem(selected_Item);
        }
        else
        {
            reset_JC(); // Set Selected Item to Nothing
        }
    }
    
    public void reset_JC()
    {
        setSelectedItem(null);
    }
    
    public boolean validation_Check(LinkedHashMap<String, ArrayList<String>> error_Map)
    {
        if (getSelectedIndex() == - 1)
        {
            error_Map.put(label, new ArrayList<>(List.of("Select an item !")));
            return false;
        }
        else
        {
            return true;
        }
    }
    
    //##################################################################################################################
    // Acessor Methods
    //##################################################################################################################
    public Integer get_Selected_Item_ID() throws Exception
    {
        String method_Name = String.format(" Field_JComboBox.java -> %s()", new Object() { }.getClass().getEnclosingMethod().getName());
        
        // Get Object
        Object item = getSelectedItem();
        
        if (item == null) { return null; }
        
        // If this is an ID object return the ID
        if (item instanceof ID_Object id_object) { return id_object.get_ID(); }
        
        String errorMSG = String.format("%s \nItem is not an D_Object id_object to get ID but, is -> %s",
                method_Name, item.getClass().getSimpleName());
        
        throw new Exception(errorMSG);
    }
}
