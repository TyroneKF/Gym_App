package App_Code.Objects.Gui_Objects;

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
}
