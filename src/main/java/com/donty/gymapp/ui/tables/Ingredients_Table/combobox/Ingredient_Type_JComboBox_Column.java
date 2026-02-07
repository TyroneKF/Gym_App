package com.donty.gymapp.ui.tables.Ingredients_Table.combobox;

import com.donty.gymapp.ui.meta.ids.Storable_Ingredient_IDS.Ingredient_Type_ID_OBJ;
import com.donty.gymapp.ui.tables.Ingredients_Table.combobox.base.Parent_JComboBox_Column;

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
