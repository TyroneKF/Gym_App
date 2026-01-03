package App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Ingredient_Types;

import App_Code.Objects.Data_Objects.ID_Objects.ID_Object;
import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Ingredient_Type_ID_OBJ;
import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Storable_IDS_Parent;
import App_Code.Objects.Database_Objects.MyJDBC.MyJDBC_MySQL;
import App_Code.Objects.Database_Objects.Shared_Data_Registry;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Ingredients_Info.Ingredients_Info_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Edit_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Parent_Screen;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class Edit_Ingredient_Type extends Edit_Screen
{
    
    public Edit_Ingredient_Type(MyJDBC_MySQL db, Shared_Data_Registry shared_Data_Registry, Ingredients_Info_Screen ingredient_Info_Screen,
                                Parent_Screen parent_Screen, ArrayList<? extends Storable_IDS_Parent> jComboBox_List)
    {
        super(db, shared_Data_Registry, ingredient_Info_Screen, parent_Screen, jComboBox_List);
    }
    
    @Override
    protected void set_Screen_Variables()
    {
        super.label1 = "Select Ingredient Type Name To Edit";
        super.label2 = "Change Ingredient Type Name To";
        
        super.data_Gathering_Name = "Ingredient Type Name";
        super.db_ColumnName_Field = "ingredient_type_name";
        super.db_TableName = "ingredient_types";
        
        super.id_ColumnName = "ingredient_type_id";
        super.fk_Table = "ingredients_info";
        super.remove_JComboBox_Items = new ArrayList<>(Arrays.asList(1,2));
    }
    
    
    
    @Override
    protected LinkedHashSet<Pair<String, Object[]>> delete_Prior_Queries(ID_Object item_ID_Obj, LinkedHashSet<Pair<String, Object[]>> query_And_Params)
    {
        String upload_Q1 = """
                UPDATE ingredients_info
                SET ingredient_type_id = ?
                WHERE ingredient_type_id = ?""";
        
       query_And_Params.add(new Pair<>(upload_Q1, new Object[]{ 2, item_ID_Obj.get_ID() }));
       
      return query_And_Params;
    }
    
    @Override
    protected boolean delete_Shared_Data_Action()
    {
        Storable_IDS_Parent item_ID_Obj = (Storable_IDS_Parent) jCombo_Box.getSelectedItem();
        return sharedDataRegistry.remove_Ingredient_Type((Ingredient_Type_ID_OBJ) item_ID_Obj);
    }
    
    @Override
    protected void update_Other_Screens()
    {
        ingredient_Info_Screen.update_All_Types_JC();
    }
}
