package App_Code.Objects.Tables.JTable_JDBC.Children.Ingredients_Table.JCombo_Boxes.Child;

import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Ingredient_Type_ID_OBJ;
import App_Code.Objects.Tables.JTable_JDBC.Children.Ingredients_Table.JCombo_Boxes.Parent.Parent_JComboBox_Column;

import javax.swing.*;
import java.util.ArrayList;

public class Ingredient_Type_JComboBox_Column extends Parent_JComboBox_Column<Ingredient_Type_ID_OBJ>
{
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Ingredient_Type_JComboBox_Column(JTable jTable, int col, String render_MSG, ArrayList<Ingredient_Type_ID_OBJ> data)
    {
        super(jTable, col, render_MSG, data);
    }
}
