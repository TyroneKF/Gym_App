package com.donty.gymapp.ui.screens.ingredientsAndInventory.Stores_And_Ingredient_Types.ingredientTypes;

import com.donty.gymapp.ui.meta.ids.Storable_Ingredient_IDS.Ingredient_Type_ID_OBJ;
import com.donty.gymapp.persistence.database.MyJDBC_Sqlite;
import com.donty.gymapp.persistence.Shared_Data_Registry;
import com.donty.gymapp.gui.panels.Image_JPanel;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.Ingredients_Info.Ingredients_Info.Ingredients_Info_Screen;
import com.donty.gymapp.ui.screens.ingredientsAndInventory.Stores_And_Ingredient_Types.base.Parent_Screen;

import java.util.ArrayList;

public class Ingredients_Types_Screen extends Parent_Screen
{
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Ingredients_Types_Screen(
            
            MyJDBC_Sqlite db,
            Shared_Data_Registry shared_Data_Registry,
            Ingredients_Info_Screen ingredient_Info_Screen,
            ArrayList<Ingredient_Type_ID_OBJ> jComboBox_List
    )
    {
        //####################################
        // Super Constructor
        //####################################
        super(
                db,
                shared_Data_Registry,
                ingredient_Info_Screen,
                "type",
                jComboBox_List
        );
    }
    
    @Override
    protected void initialize_Screens()
    {
        this.add_Screen = new Add_Ingredient_Type(db, shared_Data_Registry, ingredient_Info_Screen, this);
        this.edit_Screen = new Edit_Ingredient_Type(db, shared_Data_Registry, ingredient_Info_Screen, this, jComboBox_List);
        this.screenImage = new Image_JPanel("/images/ingredient_Type/ingredientType0.png", 500, 500);
    }
    
    @Override
    protected void additional_Icon_Setup() { }
}
