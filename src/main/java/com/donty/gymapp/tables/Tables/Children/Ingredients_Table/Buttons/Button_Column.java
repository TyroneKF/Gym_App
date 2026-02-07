package com.donty.gymapp.tables.Tables.Children.Ingredients_Table.Buttons;

import com.donty.gymapp.tables.Tables.Children.Ingredients_Table.IngredientsTable;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;

/**
 * http://www.camick.com/java/source/ButtonColumn.java
 * <p>
 * The Button_Column class provides a renderer and an editor that looks like a
 * JButton. The renderer and editor will then be used for a specified column
 * in the table. The TableModel will contain the String to be displayed on
 * the button.
 * <p>
 * The button can be invoked by a mouse click or by pressing the space bar
 * when the cell has focus. Optionally a mnemonic can be set to invoke the
 * button. When the button is invoked the provided Action is invoked. The
 * source of the Action will be the table. The action command will contain
 * the model row number of the button that was clicked.
 */

public class Button_Column extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener, MouseListener
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private JTable table;
    
    private final Action action;
    private int mnemonic;
    private final Border originalBorder;
    private Border focusBorder;
    
    private final JButton renderButton;
    private final JButton editButton;
    private Object editorValue;
    private boolean isButtonColumnEditor;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    
    /**
     * Create the Button_Column to be used as a renderer and editor. The
     * renderer and editor will automatically be installed on the TableColumn
     * of the specified delete_Btn_Column.
     * <p>
     * table  the table containing the button renderer/editor
     *
     * @param delete_Btn_Column the delete_Btn_Column to which the button renderer/editor is added
     */
    public Button_Column(IngredientsTable ingredients_Table, int delete_Btn_Column, int ingredients_Index_Column)
    {
        //##############################################
        // Variables
        //##############################################
        table = ingredients_Table.get_JTable();
        
        // Action
        action = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                JTable table = (JTable) e.getSource();
                int row = table.getSelectedRow();
                
                int ingredients_Index = (Integer) ingredients_Table.get_Value_On_Model_Data(row, ingredients_Index_Column);
               
                ingredients_Table.delete_Row_Action(ingredients_Index, row); // command to update db
                
            }
        };
        
        //##############################################
        //
        //##############################################
        renderButton = new JButton();
        editButton = new JButton();
        editButton.setFocusPainted(false);
        editButton.addActionListener(this);
        originalBorder = editButton.getBorder();
        setFocusBorder(new LineBorder(Color.BLUE));
        
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(delete_Btn_Column).setCellRenderer(this);
        columnModel.getColumn(delete_Btn_Column).setCellEditor(this);
        table.addMouseListener(this);
        
        setMnemonic(KeyEvent.VK_D);
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    
    /**
     * Get foreground color of the button when the cell has focus
     *
     * @return the foreground color
     */
    public Border getFocusBorder()
    {
        return focusBorder;
    }
    
    /**
     * The foreground color of the button when the cell has focus
     *
     * @param focusBorder the foreground color
     */
    public void setFocusBorder(Border focusBorder)
    {
        this.focusBorder = focusBorder;
        editButton.setBorder(focusBorder);
    }
    
    public int getMnemonic()
    {
        return mnemonic;
    }
    
    /**
     * The mnemonic to activate the button when the cell has focus
     *
     * @param mnemonic the mnemonic
     */
    public void setMnemonic(int mnemonic)
    {
        this.mnemonic = mnemonic;
        renderButton.setMnemonic(mnemonic);
        editButton.setMnemonic(mnemonic);
    }
    
    @Override
    public Component getTableCellEditorComponent(
            JTable table, Object value, boolean isSelected, int row, int column)
    {
        if (value == null)
        {
            editButton.setText("");
            editButton.setIcon(null);
        }
        else if (value instanceof Icon)
        {
            editButton.setText("");
            editButton.setIcon((Icon) value);
        }
        else
        {
            editButton.setText(value.toString());
            editButton.setIcon(null);
        }
        
        this.editorValue = value;
        return editButton;
    }
    
    @Override
    public Object getCellEditorValue()
    {
        return editorValue;
    }
    
    //
    //  Implement TableCellRenderer interface
    //
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        if (isSelected)
        {
            renderButton.setForeground(table.getSelectionForeground());
            renderButton.setBackground(table.getSelectionBackground());
        }
        else
        {
            renderButton.setForeground(table.getForeground());
            renderButton.setBackground(UIManager.getColor("Button.background"));
        }
        
        if (hasFocus)
        {
            renderButton.setBorder(focusBorder);
        }
        else
        {
            renderButton.setBorder(originalBorder);
        }
        
        //		renderButton.setText( (value == null) ? "" : value.toString() );
        if (value == null)
        {
            renderButton.setText("");
            renderButton.setIcon(null);
        }
        else if (value instanceof Icon)
        {
            renderButton.setText("");
            renderButton.setIcon((Icon) value);
        }
        else
        {
            renderButton.setText(value.toString());
            renderButton.setIcon(null);
        }
        
        return renderButton;
    }
    
    //
    //  Implement ActionListener interface
    //
    /*
     *	The button has been pressed. Stop editing and invoke the custom Action
     */
    public void actionPerformed(ActionEvent e)
    {
        int row = table.getEditingRow();
        
        fireEditingStopped();
        
        //  Invoke the Action
        
        ActionEvent event = new ActionEvent(
                table,
                ActionEvent.ACTION_PERFORMED,
                "" + row);
        action.actionPerformed(event);
        
    }
    
    //
    //  Implement MouseListener interface
    //
    /*
     *  When the mouse is pressed the editor is invoked. If you then then drag
     *  the mouse to another cell before releasing it, the editor is still
     *  active. Make sure editing is stopped when the mouse is released.
     */
    public void mousePressed(MouseEvent e)
    {
        if (table.isEditing()
                && table.getCellEditor() == this)
        {
            isButtonColumnEditor = true;
        }
    }
    
    public void mouseReleased(MouseEvent e)
    {
        if (isButtonColumnEditor
                && table.isEditing())
        {
            table.getCellEditor().stopCellEditing();
        }
        
        isButtonColumnEditor = false;
    }
    
    public void mouseClicked(MouseEvent e)
    {
    }
    
    public void mouseEntered(MouseEvent e)
    {
    }
    
    public void mouseExited(MouseEvent e)
    {
    }
}