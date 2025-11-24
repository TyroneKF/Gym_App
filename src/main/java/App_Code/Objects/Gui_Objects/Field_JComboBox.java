package App_Code.Objects.Gui_Objects;

import App_Code.Objects.Data_Objects.ID_Object;

import javax.swing.*;
import java.util.ArrayList;

public class Field_JComboBox<T> extends JComboBox<T>
{
    protected ArrayList<T> data_AL;
    
    public Field_JComboBox(ArrayList<T> data_AL)
    {
        //############################
        // Variables
        //############################
        this.data_AL = data_AL;
        
        //############################
        // Centre JComboBox Item
        //############################
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
    }
    
    public void reload_Items()
    {
        //############################
        // Load List
        //############################
        DefaultComboBoxModel<T> model = (DefaultComboBoxModel<T>) getModel();
        
        model.removeAllElements(); // Remove all Elements
        
        for (T item : data_AL) // Populate Model From AL that's updated
        {
            model.addElement(item);
        }
        
        reset_JC();// Set Selected Item to Nothing
    }
    
    public void reset_JC()
    {
        setSelectedItem(null);
    }
    
    public boolean validation_Check() { return getSelectedIndex() != - 1; }
    
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
