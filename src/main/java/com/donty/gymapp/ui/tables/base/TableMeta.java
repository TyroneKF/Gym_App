package com.donty.gymapp.ui.tables.base;

public class TableMeta
{
    //##################################################################################################################
    // Variable
    //##################################################################################################################
    private final String primary_Key_Column;
    private final String tableName;
    private final String write_Table_Name;
    private final String read_View_Name;

    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public TableMeta
    (
            String primary_Key_Column,
            String tableName,
            String write_Table_Name,
            String read_View_Name
    )
    {
        this.primary_Key_Column = primary_Key_Column;
        this.tableName = tableName;
        this.write_Table_Name = write_Table_Name;
        this.read_View_Name = read_View_Name;
    }

    //##################################################################################################################
    // Method
    //##################################################################################################################
    public String get_Primary_Key_Column()
    {
        return primary_Key_Column;
    }

    public String get_Read_View_Name()
    {
        return read_View_Name;
    }

    public String get_Table_Name()
    {
        return tableName;
    }

    public String get_Write_Table_Name()
    {
        return write_Table_Name;
    }
}
