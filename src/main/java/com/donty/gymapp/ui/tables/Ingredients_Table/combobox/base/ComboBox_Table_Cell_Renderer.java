package com.donty.gymapp.ui.tables.Ingredients_Table.combobox.base;

import com.donty.gymapp.ui.meta.ids.Storable_Ingredient_IDS.Storable_IDS_Parent;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class ComboBox_Table_Cell_Renderer<T extends Storable_IDS_Parent> extends JComboBox<T> implements TableCellRenderer
{
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        setSelectedItem(value);
        return this;
    }
}
