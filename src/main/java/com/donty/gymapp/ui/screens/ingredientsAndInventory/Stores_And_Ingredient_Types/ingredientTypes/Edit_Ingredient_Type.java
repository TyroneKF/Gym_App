package com.donty.gymapp.ui.screens.ingredientsAndInventory.Stores_And_Ingredient_Types.ingredientTypes;

import com.donty.gymapp.ui.meta.ids.ID_Object;
import com.donty.gymapp.ui.meta.ids.Storable_Ingredient_IDS.Ingredient_Type_ID_OBJ;
import com.donty.gymapp.ui.meta.ids.Storable_Ingredient_IDS.Storable_IDS_Parent;
import com.donty.gymapp.persistence.database.batch.Batch_Upload_Statements;
import com.donty.gymapp.persistence.database.MyJDBC_Sqlite;
import com.donty.gymapp.persistence.database.statements.Upload_Statement;
import com.donty.gymapp.persistence.Shared_Data_Registry;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.Ingredients_Info.Ingredients_Info.Ingredients_Info_Screen;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.Stores_And_Ingredient_Types.base.Edit_Screen;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.Stores_And_Ingredient_Types.base.Parent_Screen;

import java.util.ArrayList;

public class Edit_Ingredient_Type extends Edit_Screen<Ingredient_Type_ID_OBJ>
{

    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Edit_Ingredient_Type(
            
            MyJDBC_Sqlite db,
            Shared_Data_Registry shared_Data_Registry,
            Ingredients_Info_Screen ingredient_Info_Screen,
            Parent_Screen<Ingredient_Type_ID_OBJ> parent_Screen,

            ArrayList<Ingredient_Type_ID_OBJ> jComboBox_List
    )
    {
        super(
                db,
                shared_Data_Registry,
                ingredient_Info_Screen,
                parent_Screen,
                Ingredient_Type_ID_OBJ.class,
                jComboBox_List
        );
    }

    //##################################################################################################################
    // Methods
    //##################################################################################################################
    @Override
    protected void set_Screen_Variables()
    {
        super.label1 = "Select Ingredient Type Name To Edit";
        super.label2 = "Change Ingredient Type Name To";
        
        super.data_gathering_name = "Ingredient Type Name";
        super.db_column_name_field = "ingredient_type_name";
        super.db_table_name = "ingredient_types";
        
        super.id_column_name = "ingredient_type_id";
        super.fk_Table = "ingredients_info";
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
        Storable_IDS_Parent item_ID_Obj = (Storable_IDS_Parent) field_jc.getSelectedItem();
        return sharedDataRegistry.remove_Ingredient_Type((Ingredient_Type_ID_OBJ) item_ID_Obj);
    }
    
    @Override
    protected void update_Other_Screens()
    {
        ingredient_Info_Screen.update_All_Types_JC();
    }
}
