package com.donty.gymapp.ui.meta.bindings;

import com.donty.gymapp.domain.enums.db_enums.columnNames.base.Table_Enum;
import com.donty.gymapp.gui.controls.combobox.base.Field_JComboBox;
import com.donty.gymapp.gui.controls.textfields.base.Field_JTxtField_Parent;
import java.awt.*;

public class Field_Binding<T>
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private String gui_Label;

    private Component component;

    private int query_Field_Pos;

    private Table_Enum table_Enum;

    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Field_Binding
    (
            String gui_Label,
            Field_JComboBox<T> component,
            Table_Enum table_Enum,
            int query_Field_Pos
    )
    {
        constructor_Setup(gui_Label, component, table_Enum, query_Field_Pos);
    }

    public Field_Binding
            (
                    String gui_Label,
                    Field_JTxtField_Parent<?> component,
                    Table_Enum table_Enum,
                    int query_Field_Pos
            )
    {
        constructor_Setup(gui_Label, component, table_Enum, query_Field_Pos);
    }

    private void constructor_Setup(String gui_Label, Component component, Table_Enum table_Enum, int query_Field_Pos)
    {
        this.gui_Label = gui_Label;
        this.component = component;
        this.table_Enum = table_Enum;

        this.query_Field_Pos = query_Field_Pos;
    }

    //##################################################################################################################
    // Methods
    //##################################################################################################################
    public Component get_Gui_Component()
    {
        return component;
    }

    public String get_Gui_Label()
    {
        return gui_Label;
    }

    public String get_Mysql_Field_Name()
    {
        return table_Enum.key();
    }

    public int get_Field_Query_Pos()
    {
        return query_Field_Pos;
    }
}
