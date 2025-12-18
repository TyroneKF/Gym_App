package App_Code.Objects.Tables.JTable_JDBC.Children.Ingredients_Table.JCombo_Boxes.Parent;

import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Storable_IDS_Parent;

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
