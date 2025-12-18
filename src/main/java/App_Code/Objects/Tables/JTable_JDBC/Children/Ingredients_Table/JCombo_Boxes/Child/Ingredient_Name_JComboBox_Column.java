package App_Code.Objects.Tables.JTable_JDBC.Children.Ingredients_Table.JCombo_Boxes.Child;

import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Ingredient_Name_ID_OBJ;
import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Ingredient_Type_ID_OBJ;
import App_Code.Objects.Database_Objects.Shared_Data_Registry;
import App_Code.Objects.Tables.JTable_JDBC.Children.Ingredients_Table.JCombo_Boxes.Parent.Parent_JComboBox_Column;

import javax.swing.*;
import java.util.ArrayList;

public class Ingredient_Name_JComboBox_Column extends Parent_JComboBox_Column<Ingredient_Name_ID_OBJ>
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private final int ingredient_Type_Column;
    private final Shared_Data_Registry shared_Data_Registry;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Ingredient_Name_JComboBox_Column(JTable jTable, Shared_Data_Registry shared_Data_Registry, int col, int ingredient_Type_Column, String render_MSG)
    {
        super(jTable, col, render_MSG);
        
        this.ingredient_Type_Column = ingredient_Type_Column;
        this.shared_Data_Registry = shared_Data_Registry;
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    @Override
    protected ArrayList<Ingredient_Name_ID_OBJ> get_Data(int row)
    {
        Ingredient_Type_ID_OBJ selected_Ingredient_Type_ID_OBJ = (Ingredient_Type_ID_OBJ) jTable.getValueAt(row, ingredient_Type_Column);
        int selected_Type_ID = selected_Ingredient_Type_ID_OBJ.get_ID();
        
        return shared_Data_Registry.get_Ingredient_Names_From_Type_AL(selected_Type_ID);
    }
}
