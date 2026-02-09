package com.donty.gymapp.ui.screens.macroTargets;


import com.donty.gymapp.domain.enums.db_enums.tables.MacroTargetsColumns;
import com.donty.gymapp.gui.controls.textfields.base.Field_JTxtField_Parent;
import com.donty.gymapp.ui.meta.bindings.Field_Binding;

public class Macro_Targets_Field_Binding<T> extends Field_Binding<T>
{
    public Macro_Targets_Field_Binding
            (
                    String gui_Label,
                    MacroTargetsColumns macro_Column_Enum,
                    Field_JTxtField_Parent<T> component,
                    int query_Field_Pos
            )
    {
        super(gui_Label, component, macro_Column_Enum, query_Field_Pos);
    }
}
