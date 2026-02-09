package com.donty.gymapp.ui.tables.base;

import com.donty.gymapp.domain.enums.db_enums.TableNames;
import com.donty.gymapp.domain.enums.db_enums.ViewNames;
import com.donty.gymapp.domain.enums.db_enums.columnNames.base.Table_Enum;

public class TableMeta<T extends Enum<T> & Table_Enum>
{
    //##################################################################################################################
    // Variable
    //##################################################################################################################
    private final T primary_Key_Column;

    private final String descriptive_table_name;
    private final ViewNames read_View_Name;
    private final TableNames write_Table_Name;

    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public TableMeta
    (
            T primary_Key_Column,

            String descriptive_table_name,
            TableNames write_Table_Name,
            ViewNames read_View_Name
    )
    {
        this.primary_Key_Column = primary_Key_Column;
        this.descriptive_table_name = descriptive_table_name;
        this.write_Table_Name = write_Table_Name;
        this.read_View_Name = read_View_Name;
    }

    //##################################################################################################################
    // Method
    //##################################################################################################################
    public String get_Primary_Key_Column()
    {
        return primary_Key_Column.key();
    }

    public String get_Read_View_Name()
    {
        return read_View_Name.key();
    }

    public String get_Descriptive_Table_Name()
    {
        return descriptive_table_name;
    }

    public String get_Write_Table_Name()
    {
        return write_Table_Name != null ? write_Table_Name.key() : null;
    }
}
