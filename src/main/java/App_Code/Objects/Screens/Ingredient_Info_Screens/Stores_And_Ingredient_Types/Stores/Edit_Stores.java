package App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Stores;


import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JDBC.Null_MYSQL_Field;
import App_Code.Objects.Database_Objects.Shared_Data_Registry;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Ingredients_Info_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Edit_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Parent_Screen;
import org.javatuples.Pair;

import javax.swing.*;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class Edit_Stores extends Edit_Screen
{
    public Edit_Stores(MyJDBC db, Shared_Data_Registry shared_Data_Registry, Ingredients_Info_Screen ingredient_Info_Screen, Parent_Screen parent_Screen)
    {
        super(db, shared_Data_Registry, ingredient_Info_Screen, parent_Screen);
    }
    
    @Override
    protected void set_Screen_Variables()
    {
        super.lable1 = "Select Supplier Name To Edit";
        super.label2 = "Change Supplier Name To";
        
        super.data_Gathering_Name = "Supplier Name";
        super.db_ColumnName_Field = "store_name";
        super.db_TableName = "stores";
        
        super.id_ColumnName = "store_id";
        super.fk_Table = "ingredients_in_sections_of_meal";
        super.remove_JComboBox_Items = new ArrayList<>();
        // super.remove_JComboBox_Items = new ArrayList<>(List.of("No Shop"));
    }
    
    @Override
    protected void update_Other_Screens() { }
}