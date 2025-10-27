package App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Stores;


import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Gui_Objects.Image_JPanel;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Ingredients_Info_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Parent_Screen;

import java.util.Collection;

public class Ingredient_Stores_Screen extends Parent_Screen
{
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Ingredient_Stores_Screen(MyJDBC db, Ingredients_Info_Screen ingredient_Info_screen, Collection<String> jComboBox_List)
    {
        
        //####################################
        // Super Constructor
        //####################################
        super(db, ingredient_Info_screen,
                "Edit Suppliers",
                jComboBox_List,
                "src/main/java/Documentation_And_Scripts/Database/Scripts/Editable_DB_Scripts/5.) IngredientTypes.sql");
        
        //####################################
        //
        //####################################
        initialize_Screens(db);
        create_Interface();
    }
    
    @Override
    protected void initialize_Screens(MyJDBC db)
    {
        this.add_Screen = new Add_Stores(db, this, "Add Suppliers", 250, 50);
        this.edit_Screen = new Edit_Stores(db, this, "Edit Suppliers", 250, 50);
        this.screenImage = new Image_JPanel("/images/stores/store0.png", 500, 470);
    }
}
