package App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Stores;


import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Store_ID_OBJ;
import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.Shared_Data_Registry;
import App_Code.Objects.Gui_Objects.Image_JPanel;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Ingredients_Info_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Parent.Parent_Screen;

import java.util.ArrayList;

public class Ingredient_Stores_Screen extends Parent_Screen
{
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Ingredient_Stores_Screen(MyJDBC db, Shared_Data_Registry shared_Data_Registry,
                                    Ingredients_Info_Screen ingredient_Info_screen, ArrayList<Store_ID_OBJ> jComboBox_List)
    {
        //####################################
        // Super Constructor
        //####################################
        super(
                db,
                shared_Data_Registry,
                ingredient_Info_screen,
                "stores",
                jComboBox_List
        );
    }
    
    @Override
    protected void initialize_Screens()
    {
        this.add_Screen = new Add_Stores(db, shared_Data_Registry, ingredient_Info_Screen, this);
        this.edit_Screen = new Edit_Stores(db, shared_Data_Registry, ingredient_Info_Screen, this, jComboBox_List);
        this.screenImage = new Image_JPanel("/images/stores/store0.png", 500, 470);
    }
    
    @Override
    protected void additional_Icon_Setup() { }
}
