package com.donty.gymapp.ui.screens.ingredientsAndInventory.storesAndIngredientTypes.ingredientTypes;

import com.donty.gymapp.ui.meta.ids.ID_Object;
import com.donty.gymapp.ui.meta.ids.storableIDs.Ingredient_Type_ID_OBJ;
import com.donty.gymapp.ui.meta.ids.storableIDs.Storable_IDS_Parent;
import com.donty.gymapp.persistence.database.batch.Batch_Upload_Statements;
import com.donty.gymapp.persistence.database.MyJDBC_Sqlite;
import com.donty.gymapp.persistence.database.statements.Upload_Statement;
import com.donty.gymapp.persistence.Shared_Data_Registry;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.ingredientsInfo.Ingredients_Info_Screen;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.storesAndIngredientTypes.base.Edit_Screen;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.storesAndIngredientTypes.base.Parent_Screen;

import java.util.ArrayList;

public class Edit_Ingredient_Type extends Edit_Screen<Ingredient_Type_ID_OBJ>
{
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    private final int na_ingredient_type_id;

    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Edit_Ingredient_Type
    (
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

        na_ingredient_type_id = shared_Data_Registry.get_Un_assigned_Ingredient_Type_ID();
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
    }

    @Override
    protected boolean delete_Action(Storable_IDS_Parent item_ID_Obj, Batch_Upload_Statements upload_statements)
    {
        // Add Special Cases
        String upload_Q1 = """
                UPDATE ingredients_info
                SET ingredient_type_id = ?
                WHERE ingredient_type_id = ?""";

        Object[] params = new Object[]{ na_ingredient_type_id, item_ID_Obj.get_ID() };

        upload_statements.add_Uploads(new Upload_Statement(upload_Q1, params , true));


        // Return to Default delete
        return  super.delete_Action(item_ID_Obj, upload_statements);
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
