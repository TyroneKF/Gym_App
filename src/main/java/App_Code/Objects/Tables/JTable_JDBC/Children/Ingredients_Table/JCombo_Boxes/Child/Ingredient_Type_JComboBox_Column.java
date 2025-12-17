package App_Code.Objects.Tables.JTable_JDBC.Children.Ingredients_Table.JCombo_Boxes.Child;

import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Ingredient_Type_ID_OBJ;
import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Storable_IDS_Parent;
import App_Code.Objects.Tables.JTable_JDBC.Children.Ingredients_Table.JCombo_Boxes.Parent.Parent_JComboBox_Column;

import javax.swing.*;
import java.util.ArrayList;

public class Ingredient_Type_JComboBox_Column<T extends Ingredient_Type_ID_OBJ> extends Parent_JComboBox_Column<T>
{
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Ingredient_Type_JComboBox_Column(JTable jTable, int col, String render_MSG, ArrayList<T> data)
    {
        super(jTable, col, render_MSG, data);
    }
}
