package App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Stores;


import App_Code.Objects.Data_Objects.ID_Object;
import App_Code.Objects.Data_Objects.Storable_Ingredient_IDS.Ingredient_Type_ID_Obj;
import App_Code.Objects.Data_Objects.Storable_Ingredient_IDS.Storable_IDS_Parent;
import App_Code.Objects.Data_Objects.Storable_Ingredient_IDS.Store_ID_OBJ;
import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.Shared_Data_Registry;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Ingredients_Info_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Edit_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Parent_Screen;
import org.javatuples.Pair;

import javax.swing.*;
import java.util.*;

public class Edit_Stores extends Edit_Screen
{
    public Edit_Stores(MyJDBC db, Shared_Data_Registry shared_Data_Registry, Ingredients_Info_Screen ingredient_Info_Screen,
                       Parent_Screen parent_Screen, ArrayList<? extends Storable_IDS_Parent> jComboBox_List)
    {
        super(db, shared_Data_Registry, ingredient_Info_Screen, parent_Screen, jComboBox_List);
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
        super.remove_JComboBox_Items = new ArrayList<>(Arrays.asList(1));
    }
    
    @Override
    protected LinkedHashSet<Pair<String, Object[]>> delete_Prior_Queries(ID_Object id_object, LinkedHashSet<Pair<String, Object[]>> query_And_Params)
    {
        return query_And_Params;
    }
    
    @Override
    protected boolean delete_Shared_Data_Action()
    {
        Storable_IDS_Parent item_ID_Obj = (Storable_IDS_Parent) jCombo_Box.getSelectedItem();
        
        return sharedDataRegistry.remove_Store((Store_ID_OBJ) item_ID_Obj);
    }
    
    @Override
    protected void update_Other_Screens() { }
}