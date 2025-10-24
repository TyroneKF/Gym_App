package App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Stores;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Add_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Parent_Screen;

import javax.swing.*;

public class Add_Stores extends Add_Screen
{
    public Add_Stores(MyJDBC db, Parent_Screen parent_Screen, String btnText, int btnWidth, int btnHeight)
    {
        super(db, parent_Screen, btnText, btnWidth, btnHeight);
    }
    
    @Override
    protected void set_Screen_Variables()
    {
        super.main_Label = "Add Supplier Name";
        
        super.data_Gathering_Name = "Supplier Name";
        super.db_ColumnName_Field = "store_name";
        super.db_TableName = "stores";
    }
    
    @Override
    protected void success_Upload_Message()
    {
        String text = "\n\nSuccessfully Added New Supplier!";
        JOptionPane.showMessageDialog(null, text);
    }
    
    @Override
    protected void failure_Message()
    {
        String text = "\n\nFailed To Upload - Couldn't Add Supplier";
        JOptionPane.showMessageDialog(null, text);
    }
    
    @Override
    protected void update_Other_Screens()
    {
        ingredient_Info_Screen.add_Or_Remove_Supplier_From_List("add", jTextField_TXT, null); // add to list
        ingredient_Info_Screen.update_Ingredient_Suppliers_JComboBoxes();
    }
}
