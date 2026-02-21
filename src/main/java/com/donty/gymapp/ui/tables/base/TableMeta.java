package com.donty.gymapp.ui.tables.base;

import com.donty.gymapp.domain.enums.db_enums.TableNames;
import com.donty.gymapp.domain.enums.db_enums.ViewNames;
import com.donty.gymapp.domain.enums.db_enums.columnNames.base.Table_Enum;

/**
 * @param primary_Key_Column ################################################################################################################## Variable##################################################################################################################
 */
public record TableMeta<T extends Enum<T> & Table_Enum>
(
        T primary_Key_Column,
        String descriptive_table_name,
        TableNames write_Table_Name,
        ViewNames read_View_Name
)
{
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
