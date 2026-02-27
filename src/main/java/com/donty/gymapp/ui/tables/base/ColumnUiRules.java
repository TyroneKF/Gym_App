package com.donty.gymapp.ui.tables.base;

import com.donty.gymapp.domain.enums.db_enums.columnNames.base.Table_Enum;

import java.util.ArrayList;

/**
 * @param column_Names ################################################################################################################## Variable##################################################################################################################
 */
public record ColumnUiRules<T extends Enum<T> & Table_Enum>
(
        ArrayList<String> column_Names,
        ArrayList<T> editable_Column_Names,
        ArrayList<T> col_To_Avoid_Centering,
        ArrayList<T> columns_To_Hide
)
{
    //##################################################################################################################
    // Constructor
    //##################################################################################################################

    //##################################################################################################################
    // Method
    //##################################################################################################################
    public ArrayList<String> get_Column_Names()
    {
        return column_Names;
    }

    public ArrayList<T> get_Columns_To_Hide()
    {
        return columns_To_Hide;
    }

    public boolean is_Columns_To_Hide_Null()
    {
        return columns_To_Hide != null && ! columns_To_Hide.isEmpty();
    }

    public ArrayList<T> get_Editable_Column_Names()
    {
        return editable_Column_Names;
    }

    public ArrayList<T> get_Col_To_Avoid_Centering()
    {
        return col_To_Avoid_Centering;
    }
}
