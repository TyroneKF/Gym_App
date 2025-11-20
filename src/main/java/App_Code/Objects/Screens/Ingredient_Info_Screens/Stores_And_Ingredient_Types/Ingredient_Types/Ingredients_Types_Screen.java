package App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Ingredient_Types;

import App_Code.Objects.Data_Objects.Storable_Ingredient_IDS.Ingredient_Type_ID_Obj;
import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.Shared_Data_Registry;
import App_Code.Objects.Gui_Objects.Image_JPanel;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Ingredients_Info_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Parent_Screen;

import java.util.ArrayList;

public class Ingredients_Types_Screen extends Parent_Screen
{
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Ingredients_Types_Screen(MyJDBC db, Shared_Data_Registry shared_Data_Registry, Ingredients_Info_Screen ingredient_Info_Screen,
                                    ArrayList<Ingredient_Type_ID_Obj> jComboBox_List)
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
