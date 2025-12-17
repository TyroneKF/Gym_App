package App_Code.Objects.Tables.JTable_JDBC.Children.Ingredients_Table.JCombo_Boxes.Child;

import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Ingredient_Name_ID_OBJ;
import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Ingredient_Type_ID_OBJ;
import App_Code.Objects.Database_Objects.Shared_Data_Registry;
import App_Code.Objects.Tables.JTable_JDBC.Children.Ingredients_Table.JCombo_Boxes.Parent.Parent_JComboBox_Column;

import javax.swing.*;
import java.util.ArrayList;

public class Ingredient_Name_JComboBox_Column<T extends Ingredient_Name_ID_OBJ> extends Parent_JComboBox_Column<T>
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private final int ingredient_Type_Column;
    private final Shared_Data_Registry shared_Data_Registry;
    private final Class<T> type_Cast;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Ingredient_Name_JComboBox_Column(JTable jTable, Shared_Data_Registry shared_Data_Registry, Class<T> type_Cast, int col, int ingredient_Type_Column, String render_MSG)
    {
        super(jTable, col, render_MSG);
        
        this.ingredient_Type_Column = ingredient_Type_Column;
        this.shared_Data_Registry = shared_Data_Registry;
        this.type_Cast = type_Cast;
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    @Override
    protected ArrayList<T> get_Data(int row)
    {
        Ingredient_Type_ID_OBJ selected_Ingredient_Type_ID_OBJ = (Ingredient_Type_ID_OBJ) jTable.getValueAt(row, ingredient_Type_Column);
        int selected_Type_ID = selected_Ingredient_Type_ID_OBJ.get_ID();
        
        ArrayList<Ingredient_Name_ID_OBJ> ingredient_Names_From_Type_Al = shared_Data_Registry.get_Ingredient_Names_From_Type_AL(selected_Type_ID);
        
        ArrayList<T> list = new ArrayList<>();
        
        for(Ingredient_Name_ID_OBJ ingredient_name_id_obj : ingredient_Names_From_Type_Al )
        {
            list.add(type_Cast.cast(ingredient_name_id_obj));
        }
        
        return list;
    }
}
