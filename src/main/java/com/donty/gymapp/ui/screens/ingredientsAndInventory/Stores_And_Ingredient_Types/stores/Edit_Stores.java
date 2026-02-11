package com.donty.gymapp.ui.screens.ingredientsAndInventory.Stores_And_Ingredient_Types.stores;

import com.donty.gymapp.ui.meta.ids.ID_Object;
import com.donty.gymapp.ui.meta.ids.Storable_Ingredient_IDS.Storable_IDS_Parent;
import com.donty.gymapp.ui.meta.ids.Storable_Ingredient_IDS.Store_ID_OBJ;
import com.donty.gymapp.persistence.database.batch.Batch_Upload_Statements;
import com.donty.gymapp.persistence.database.MyJDBC_Sqlite;
import com.donty.gymapp.persistence.Shared_Data_Registry;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.Ingredients_Info.Ingredients_Info.Ingredients_Info_Screen;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.Stores_And_Ingredient_Types.base.Edit_Screen;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.Stores_And_Ingredient_Types.base.Parent_Screen;

import java.util.*;


public class Edit_Stores extends Edit_Screen<Store_ID_OBJ>
{

    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Edit_Stores
    (
            MyJDBC_Sqlite db,
            Shared_Data_Registry shared_Data_Registry,
            Ingredients_Info_Screen ingredient_Info_Screen,
            Parent_Screen<Store_ID_OBJ> parent_Screen,
            ArrayList<Store_ID_OBJ> jComboBox_List
    )
    {
        super(
                db,
                shared_Data_Registry,
                ingredient_Info_Screen,
                parent_Screen,
                Store_ID_OBJ.class,
                jComboBox_List
        );
    }

    //##################################################################################################################
    // Methods
    //##################################################################################################################
    @Override
    protected void set_Screen_Variables()
    {
        super.label1 = "Select Supplier Name To Edit";
        super.label2 = "Change Supplier Name To";

        super.data_gathering_name = "Supplier Name";
        super.db_column_name_field = "store_name";
        super.db_table_name = "stores";

        super.id_column_name = "store_id";
        super.fk_Table = "ingredients_in_sections_of_meal";
    }

    @Override
    protected void delete_Prior_Queries(ID_Object id_object, Batch_Upload_Statements upload_statements) { }

    @Override
    protected boolean delete_Shared_Data_Action()
    {
        Storable_IDS_Parent item_ID_Obj = (Storable_IDS_Parent) field_jc.getSelectedItem();

        return sharedDataRegistry.remove_Store((Store_ID_OBJ) item_ID_Obj);
    }

    @Override
    protected void update_Other_Screens() { ingredient_Info_Screen.update_Stores_JC(); }
}