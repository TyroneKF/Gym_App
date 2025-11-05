package App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Ingredient_Types;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Gui_Objects.Image_JPanel;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Ingredients_Info_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Parent_Screen;

import javax.swing.*;
import java.util.Collection;

public class Ingredients_Types_Screen extends Parent_Screen
{
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Ingredients_Types_Screen(MyJDBC db, Ingredients_Info_Screen ingredient_Info_Screen, Collection<String> jComboBox_List)
    {
        //####################################
        // Super Constructor
        //####################################
        super(db, ingredient_Info_Screen,
                "Add Ingredients Type",
                jComboBox_List,
                "/data/database_scripts/8.) stores.sql");
        
        //####################################
        // Create Interface
        //####################################
        initialize_Screens(db);
        create_Interface();
    }
    
    @Override
    protected void initialize_Screens(MyJDBC db)
    {
        this.add_Screen = new Add_Ingredient_Type(db, this);
        this.edit_Screen = new Edit_Ingredient_Type(db, this);
        this.screenImage = new Image_JPanel("/images/ingredient_Type/ingredientType0.png", 500, 500);
    }
    
    @Override
    protected void additional_Icon_Setup(JPanel iconPanelInsert)
    {
    
    }
}
