package com.donty.gymapp.ui.tables.base;

import com.donty.gymapp.domain.enums.db_enums.columnNames.base.Table_Enum;

import java.util.ArrayList;

public class ColumnUiRules<T extends Enum<T> & Table_Enum>
{
    //##################################################################################################################
    // Variable
    //##################################################################################################################
    private final ArrayList<String> column_Names;
    private final ArrayList<T> editable_Column_Names;
    private final ArrayList<T> col_To_Avoid_Centering;
    private final ArrayList<T> columns_To_Hide;

    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public ColumnUiRules
    (
            ArrayList<String> column_Names,

            ArrayList<T> editable_Column_Names,
            ArrayList<T> col_To_Avoid_Centering,
            ArrayList<T> columns_To_Hide
    )
    {
        this.column_Names = column_Names;

        this.editable_Column_Names = editable_Column_Names;
        this.col_To_Avoid_Centering = col_To_Avoid_Centering;
        this.columns_To_Hide = columns_To_Hide;
    }

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
        return columns_To_Hide != null || ! columns_To_Hide.isEmpty();
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
