package App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Stores;


import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JDBC.Null_MYSQL_Field;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Edit_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Parent_Screen;
import org.javatuples.Pair;

import javax.swing.*;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class Edit_Stores extends Edit_Screen
{
    public Edit_Stores(MyJDBC db, Parent_Screen parent_Screen)
    {
        super(db, parent_Screen);
    }
    
    @Override
    protected void set_Screen_Variables()
    {
        super.lable1 = "Select Supplier Name To Edit";
        super.label2 = "Change Supplier Name To";
        
        super.data_Gathering_Name = "Supplier Name";
        super.db_ColumnName_Field = "store_name";
        super.db_TableName = "stores";
        
        super.id_ColumnName = "store_id";
        super.fk_Table = "ingredients_in_sections_of_meal";
        super.remove_JComboBox_Items = new ArrayList<>(List.of("No Shop"));
    }
    
    @Override
    protected void success_Upload_Message()
    {
        String text = String.format("\n\nSuccessfully Changed Suppliers From ' %s ' to ' %s ' !", selected_JComboBox_Item_Txt, jTextField_TXT);
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
        // this doesn't have to be in  a particular position, since all objects are deleted
        ingredient_Info_Screen.update_Ingredient_Suppliers_JComboBoxes();  // Clear shop GUI in app
        
        if (item_Deleted) // go through with the deleting process
        {
            ingredient_Info_Screen.add_Or_Remove_Supplier_From_List("delete", null, selected_JComboBox_Item_Txt);
            return;
        }
        
        //  Must be the replace process requested
        ingredient_Info_Screen.add_Or_Remove_Supplier_From_List("replace", jTextField_TXT, selected_JComboBox_Item_Txt);
    }
    
    @Override
    protected LinkedHashSet<Pair<String, Object[]>> delete_Btn_Queries(String mysqlVariableReference1, LinkedHashSet<Pair<String, Object[]>> queries_And_Params)
    {
        //######################################
        // Update ingredients_in_meal
        //######################################
        String query1 = String.format("""
                UPDATE %s
                SET pdid = ?
                WHERE pdid IN (SELECT pdid FROM ingredient_in_shops WHERE store_id = %s);""",
                fk_Table, mysqlVariableReference1);
        
        queries_And_Params.add(new Pair<>(query1, new Object[]{ new Null_MYSQL_Field(Types.INTEGER) }));
        
        //######################################
        // Update  ingredientInShops & Stores
        //######################################
        String query3 = String.format("DELETE FROM stores WHERE store_id = %s;", mysqlVariableReference1);
        
        queries_And_Params.add(new Pair<>(query3, null));
        //######################################
        //Return Results
        //######################################
        return queries_And_Params;
    }
}