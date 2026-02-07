package com.donty.gymapp.screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Ingredient_Types;

import com.donty.gymapp.data_objects.ID_Objects.ID_Object;
import com.donty.gymapp.data_objects.ID_Objects.Storable_Ingredient_IDS.Ingredient_Type_ID_OBJ;
import com.donty.gymapp.data_objects.ID_Objects.Storable_Ingredient_IDS.Storable_IDS_Parent;
import com.donty.gymapp.database.MyJDBC.Batch_Objects.Batch_Upload_Statements;
import com.donty.gymapp.database.MyJDBC.MyJDBC_Sqlite;
import com.donty.gymapp.database.MyJDBC.Statements.Upload_Statement;
import com.donty.gymapp.database.Shared_Data_Registry;
import com.donty.gymapp.screens.Ingredient_Info_Screens.Ingredients_Info.Ingredients_Info.Ingredients_Info_Screen;
import com.donty.gymapp.screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Edit_Screen;
import com.donty.gymapp.screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Parent_Screen;

import java.util.ArrayList;
import java.util.Arrays;

public class Edit_Ingredient_Type extends Edit_Screen
{
    
    public Edit_Ingredient_Type(
            
            MyJDBC_Sqlite db,
            Shared_Data_Registry shared_Data_Registry,
            Ingredients_Info_Screen ingredient_Info_Screen,
            Parent_Screen parent_Screen,
            ArrayList<? extends Storable_IDS_Parent> jComboBox_List
    )
    {
        super(db, shared_Data_Registry, ingredient_Info_Screen, parent_Screen, jComboBox_List);
    }
    
    @Override
    protected void set_Screen_Variables()
    {
        super.label1 = "Select Ingredient Type Name To Edit";
        super.label2 = "Change Ingredient Type Name To";
        
        super.data_Gathering_Name = "Ingredient Type Name";
        super.db_ColumnName_Field = "ingredient_type_name";
        super.db_TableName = "ingredient_types";
        
        super.id_ColumnName = "ingredient_type_id";
        super.fk_Table = "ingredients_info";
        super.remove_JComboBox_Items = new ArrayList<>(Arrays.asList(1, 2));
    }

    @Override
    protected void delete_Prior_Queries(ID_Object id_object, Batch_Upload_Statements upload_statements)
    {
        String upload_Q1 = """
                UPDATE ingredients_info
                SET ingredient_type_id = ?
                WHERE ingredient_type_id = ?""";
        
        upload_statements.add_Uploads(new Upload_Statement(upload_Q1, new Object[]{ 2, id_object.get_ID() }, true));
    }
    
    @Override
    protected boolean delete_Shared_Data_Action()
    {
        Storable_IDS_Parent item_ID_Obj = (Storable_IDS_Parent) jCombo_Box.getSelectedItem();
        return sharedDataRegistry.remove_Ingredient_Type((Ingredient_Type_ID_OBJ) item_ID_Obj);
    }
    
    @Override
    protected void update_Other_Screens()
    {
        ingredient_Info_Screen.update_All_Types_JC();
    }
}
