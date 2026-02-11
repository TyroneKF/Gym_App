package com.donty.gymapp.ui.screens.ingredientsAndInventory.Ingredients_Info.edit.products;

import com.donty.gymapp.persistence.Shared_Data_Registry;
import com.donty.gymapp.persistence.database.batch.Batch_Upload_Statements;
import com.donty.gymapp.persistence.database.statements.Upload_Statement;
import com.donty.gymapp.gui.controls.combobox.base.storableID.base.Field_JCombo_Storable_ID;
import com.donty.gymapp.gui.controls.textfields.base.Field_JTxtField_Parent;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.Ingredients_Info.add.products.ShopForm_Object;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.Ingredients_Info.base.products.Shop_Form_Binding;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Edit_ShopForm_Object extends ShopForm_Object
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private int pdid;
    private ArrayList<Object> stored_Data_AL;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Edit_ShopForm_Object
    (
            Container parent_Container,
            Edit_Shop_Form shop_form,
            Shared_Data_Registry shared_data_registry,
            ArrayList<Object> data

    ) throws Exception
    {
        super(parent_Container, shop_form, shared_data_registry);
        set_Data(data);
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    private void set_Data(ArrayList<Object> data_AL) throws Exception
    {
        pdid = (int) data_AL.getFirst();  // Set PDID
        stored_Data_AL = data_AL; // Set Object Data
        
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
                case Field_JCombo_Storable_ID<?> JCombo -> JCombo.set_Item_By_ID((Integer) data);
                case Field_JTxtField_Parent<?> jTextField -> jTextField.setText(data.toString());
                default -> throw new IllegalStateException("Unexpected value: " + gui_Component);
            }
        }
        
        resize_GUI();
    }
    
    public void add_Updates(Batch_Upload_Statements upload_statements) throws Exception
    {
        //##########################
        // Variables
        //##########################
        int size = field_Items_Map.size();
        
        StringBuilder
                insert_Header = new StringBuilder("UPDATE ingredient_in_shops SET "),
                values = new StringBuilder();
        
        Object[] params = new Object[size+1]; // Include where condition param
        
        //##########################
        // Create Update Query
        //##########################
        int pos = 0;
        for(Shop_Form_Binding<?> shop_form_binding: field_Items_Map.values())
        {
            //######################################
            // Variables
            //######################################
            Component component = shop_form_binding.get_Gui_Component(); // Get Component from binding
            
            //######################################
            // Compare Data - Skip if They're Equal
            //######################################
            int pos_In_Query = shop_form_binding.get_Field_Query_Pos(); // Position of data associated with binding in query
            Object stored_Data = stored_Data_AL.get(pos_In_Query); // Get data from source
            
            switch (component)
            {
                case Field_JCombo_Storable_ID<?> jc ->
                {
                    if (jc.does_Selected_Item_ID_Equal((int) stored_Data)) { continue; }
                }
                case Field_JTxtField_Parent<?> jt -> { if (jt.is_Field_Data_Equal_To(stored_Data)) { continue; } }
                default -> throw new Exception(String.format("\n\nUnexpected value:  %s", shop_form_binding.get_Gui_Component()));
            }
            
            //######################################
            // Create Update Data
            //#####################################
            // Add to Values
            values.append(String.format("\n%s = ?,", shop_form_binding.get_Mysql_Field_Name()));
            
            // Add to Params
            switch (shop_form_binding.get_Gui_Component())
            {
                case Field_JCombo_Storable_ID<?> jc -> params[pos] = jc.get_Selected_Item_ID();
                case Field_JTxtField_Parent<?> jt -> params[pos] = jt.get_Text_Casted_To_Type();
                default ->
                        throw new Exception(String.format("\n\nUnexpected value:  %s", shop_form_binding.get_Gui_Component()));
            }
            
            pos++; // Increase pos
        }
        
        //##########################
        // Format Update Data
        //##########################
        if (values.isEmpty()) { return; } // If Nothing Changed Exit
        
        // Edit Ending of Query
        values.deleteCharAt( values.length()-1); // delete last char ','
        values.append("\nWHERE pdid = ?;");
        params[pos] = pdid;
        
        // Remove null values from values not changed
        params = Arrays.stream(params)
                .filter(Objects ::nonNull)
                .toArray(Object[]::new);
        
        System.out.printf("\n\nInsert : \n%s \n%s \n\nParams: \n%s%n", insert_Header, values, Arrays.toString(params));
        
        //##########################
        // Add To Results
        //##########################
        StringBuilder update_Query = insert_Header.append(values);
        upload_statements.add_Uploads(new Upload_Statement(update_Query.toString(), params, true));
    }
}
