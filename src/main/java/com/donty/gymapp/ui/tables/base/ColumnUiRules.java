package com.donty.gymapp.ui.tables.base;

import java.util.ArrayList;

public class ColumnUiRules
{

    //##################################################################################################################
    // Variable
    //##################################################################################################################
    private final ArrayList<String> column_Names;
    private final ArrayList<String> un_Editable_Column_Names;
    private final ArrayList<String> col_To_Avoid_Centering;
    private final ArrayList<String> columns_To_Hide;

    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public ColumnUiRules
    (
            ArrayList<String> column_Names,
            ArrayList<String> un_Editable_Column_Names,
            ArrayList<String> col_To_Avoid_Centering,
            ArrayList<String> columns_To_Hide
    )
    {
        this.column_Names = column_Names;
        this.un_Editable_Column_Names = un_Editable_Column_Names;
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

    public ArrayList<String> get_Columns_To_Hide()
    {
        return columns_To_Hide;
    }

    public ArrayList<String> get_Un_Editable_Column_Names()
    {
        return un_Editable_Column_Names;
    }

    public ArrayList<String> get_Col_To_Avoid_Centering()
    {
        return col_To_Avoid_Centering;
    }
}
