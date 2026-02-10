package com.donty.gymapp.ui.screens.ingredientsAndInventory.Stores_And_Ingredient_Types.stores;

import com.donty.gymapp.persistence.database.MyJDBC_Sqlite;
import com.donty.gymapp.persistence.Shared_Data_Registry;
import com.donty.gymapp.ui.meta.ids.Storable_Ingredient_IDS.Store_ID_OBJ;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.Ingredients_Info.Ingredients_Info.Ingredients_Info_Screen;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.Stores_And_Ingredient_Types.base.Add_Screen;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.Stores_And_Ingredient_Types.base.Parent_Screen;

public  class Add_Stores extends Add_Screen<Store_ID_OBJ>
{
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Add_Stores(
            
            MyJDBC_Sqlite db,
            Shared_Data_Registry shared_Data_Registry,
            Ingredients_Info_Screen ingredient_Info_Screen,
            Parent_Screen<Store_ID_OBJ> parent_Screen
    )
    {
        super(db, shared_Data_Registry, ingredient_Info_Screen, parent_Screen);
    }

    //##################################################################################################################
    // Methods
    //##################################################################################################################
    @Override
    protected void set_Screen_Variables()
    {
        super.main_Label = "Add Supplier Name";
        
        super.data_gathering_name = "Supplier Name";
        super.db_column_name_field = "store_name";
        super.db_table_name = "stores";
        super.id_column_name = "store_id";
    }
    
    @Override
    protected void additional_Add_Screen_Objects() {}

    @Override
    protected boolean additional_Validate_Form()
    {
        return true;
    }
    
    @Override
    protected void update_Other_Screens()
    {
        ingredient_Info_Screen.update_Stores_JC();
    }
}
