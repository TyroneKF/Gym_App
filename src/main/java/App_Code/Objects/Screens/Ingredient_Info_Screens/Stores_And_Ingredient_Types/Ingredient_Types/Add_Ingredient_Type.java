package App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Ingredient_Types;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Add_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Parent_Screen;

import javax.swing.*;

public class Add_Ingredient_Type extends Add_Screen
{
    public Add_Ingredient_Type(MyJDBC db, Parent_Screen parent_Screen)
    {
        super(db, parent_Screen);
    }
    
    @Override
    protected void  set_Screen_Variables()
    {
        super.main_Label = "Add Ingredient Type Name";
        
        super.data_Gathering_Name = "Ingredient Type Name";
        super.db_ColumnName_Field = "ingredient_type_name";
        super.db_TableName = "ingredient_types";
    }
    
    @Override
    protected void additional_Add_Screen_Objects()
    {
    
    }
    
    @Override
    protected void success_Upload_Message()
    {
        String text = String.format("\n\nSuccessfully Added New Ingredient Type: '%s'", jTextField_TXT);
        JOptionPane.showMessageDialog(null, text);
    }
    
    @Override
    protected void failure_Message()
    {
        String text = "\n\nFailed Upload - Couldn't Add New Ingredient Type";
        JOptionPane.showMessageDialog(null, text);
    }
    
    @Override
    protected void update_Other_Screens()
    {
        ingredient_Info_Screen.add_Change_Or_Remove_IngredientsTypeName("addKey", jTextField_TXT, null);
        ingredient_Info_Screen.update_IngredientsForm_Type_JComboBoxes();
    }
}