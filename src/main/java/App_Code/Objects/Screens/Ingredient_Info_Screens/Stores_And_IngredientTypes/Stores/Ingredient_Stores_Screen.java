package App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_IngredientTypes.Stores;


import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Ingredients_Info_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_IngredientTypes.Add_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_IngredientTypes.Edit_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_IngredientTypes.Parent_Screen;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;

public class Ingredient_Stores_Screen extends Parent_Screen
{
    
    
    public Ingredient_Stores_Screen(MyJDBC db, Ingredients_Info_Screen ingredient_Info_screen, Collection<String> jComboBox_List)
    {
        
        //####################################
        // Super Constructor
        //####################################
        super(db, ingredient_Info_screen,
                "ingredients types",
                "Add Suppliers",
                "Edit Suppliers",
                jComboBox_List,
                "src/main/java/Documentation_And_Scripts/Database/Scripts/Editable_DB_Scripts/5.) IngredientTypes.sql");
    }
    
    //##################################################################################################################
    // Create GUI Methods
    //##################################################################################################################
    @Override
    protected void set_Screen_Variables()
    {
        super.add_Screen = new AddStores(db,this, collapsible_BTN_Txt1, 250, 50);
        super.edit_Screen = new EditStores(db,this, collapsible_BTN_Txt2, 250, 50);
    }
    
    public class AddStores extends Add_Screen
    {
        public AddStores(MyJDBC db, Parent_Screen parent_Screen, String btnText, int btnWidth, int btnHeight)
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
    
    public class EditStores extends Edit_Screen
    {
        public EditStores(MyJDBC db, Parent_Screen parent_Screen, String btnText, int btnWidth, int btnHeight)
        {
            super(db, parent_Screen, btnText, btnWidth, btnHeight);
        }
    
        @Override
        protected void set_Screen_Variables()
        {
            super.lable1 = "Select Supplier Name To Edit";
            super.label2 = "Change Supplier Name";
    
            super.data_Gathering_Name = "Supplier Name";
            super.db_ColumnName_Field = "Store_Name";
            super.db_TableName = "stores";
    
            super.id_ColumnName = "store_id";
            super.fk_Table = "ingredients_info";
            super.remove_JComboBox_Items = new String[]{ "No Shop" };
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
        protected ArrayList<String> delete_Btn_Queries(String mysqlVariableReference1, ArrayList<String> queries)
        {
            //######################################
            // Update ingredients_in_meal
            //######################################
            String query1 = String.format("""
                    UPDATE ingredients_in_sections_of_meal
                    SET pdid = NULL
                    WHERE pdid IN (SELECT pdid FROM ingredient_in_shops WHERE store_id = %s);""", mysqlVariableReference1);
            
            //######################################
            // Update  ingredientInShops & Stores
            //######################################
            String query2 = String.format("DELETE FROM ingredient_in_shops WHERE store_id = %s;", mysqlVariableReference1);
            String query3 = String.format("DELETE FROM stores WHERE store_id = %s;", mysqlVariableReference1);
            
            //#############################################
            //
            //#############################################
            queries.add(query1);
            queries.add(query2);
            queries.add(query3);
            
            //#############################################
            //
            //#############################################
            return queries;
        }
    }
}
