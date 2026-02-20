package com.donty.gymapp.ui.screens.ingredientsAndInventory.storesAndIngredientTypes.ingredientTypes;

import com.donty.gymapp.persistence.database.MyJDBC_Sqlite;
import com.donty.gymapp.persistence.Shared_Data_Registry;
import com.donty.gymapp.ui.meta.ids.storableIDs.Ingredient_Type_ID_OBJ;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.ingredientsInfo.IngredientsInfo.Ingredients_Info_Screen;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.storesAndIngredientTypes.base.Add_Screen;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.storesAndIngredientTypes.base.Parent_Screen;

public class Add_Ingredient_Type extends Add_Screen<Ingredient_Type_ID_OBJ>
{
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Add_Ingredient_Type
    (
            MyJDBC_Sqlite db,
            Shared_Data_Registry shared_Data_Registry,
            Ingredients_Info_Screen ingredient_Info_Screen,
            Parent_Screen<Ingredient_Type_ID_OBJ> parent_Screen
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
        super.main_Label = "Add Ingredient Type Name";

        super.data_gathering_name = "Ingredient Type Name";
        super.db_column_name_field = "ingredient_type_name";
        super.db_table_name = "ingredient_types";
        super.id_column_name = "ingredient_type_id";
    }

    @Override
    protected void additional_Add_Screen_Objects() {}

    @Override
    protected boolean additional_Validate_Form()
    {
        return true;
    }

    @Override
    protected final void update_Other_Screens()
    {
        ingredient_Info_Screen.update_All_Types_JC();
    }
}