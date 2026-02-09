package com.donty.gymapp.ui.tables.ingredients.combobox.base;

import com.donty.gymapp.ui.meta.ids.Storable_Ingredient_IDS.Storable_IDS_Parent;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;

public class Parent_JComboBox_Column<T extends Storable_IDS_Parent>
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    protected DefaultComboBoxModel<T> model;
    protected JComboBox<T> comboBox;
    protected ArrayList<T> data;
    
    protected JTable jTable;
    
    protected int col;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Parent_JComboBox_Column(JTable jTable, int col, String render_MSG, ArrayList<T> data)
    {
        constructor_Setup(jTable, col, render_MSG, data);
    }
    
    public Parent_JComboBox_Column(JTable jTable, int col, String render_MSG)
    {
        constructor_Setup(jTable, col, render_MSG, null);
    }
    
    private void constructor_Setup(JTable jTable, int col, String render_MSG, ArrayList<T> data)
    {
        //####################################
        // Constructor Variables
        //####################################
        this.col = col;
        this.jTable = jTable;
        this.data = data;
        
        //####################################
        // Creating JC
        //####################################
        model = new DefaultComboBoxModel<>();
        comboBox = new JComboBox<>();
        comboBox.setModel(model);
        
        //####################################
        // Add Tool Tip to
        //####################################
        TableColumn tableColumn = jTable.getColumnModel().getColumn(col);
        tableColumn.setCellEditor(new ComboEditor());  //Set up the editor for the cells.
        
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer()  //Set up tool tips for the cells.
        {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column)
            {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setIcon(UIManager.getIcon("Table.descendingSortIcon"));
                return label;
            }
        };
        
        renderer.setToolTipText(render_MSG);
        tableColumn.setCellRenderer(renderer);
    }
    
    //##################################################################################################################
    //
    //##################################################################################################################
    private class ComboEditor extends DefaultCellEditor
    {
        public ComboEditor()
        {
            super(comboBox);
            comboBox.setEditable(true);
            
            //######################################################
            // Centre ComboBox Items
            //######################################################
            ((JLabel) comboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
            
            //######################################################
            // Make JComboBox Visible
            //######################################################
            ComboBox_Table_Cell_Renderer<T> renderer = new ComboBox_Table_Cell_Renderer<>();
            
            renderer.setModel(model);
            
            TableColumn tableColumn = jTable.getColumnModel().getColumn(col);
            tableColumn.setCellRenderer(renderer);
        }
        
        @Override //First time the cell is created
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
        {
            //########################################
            // Remove All and Fill List
            //######################################
            model.removeAllElements();
            
            for (T id_Object : get_Data(row))
            {
                model.addElement(id_Object);
            }
            
            return super.getTableCellEditorComponent(table, value, isSelected, row, column);
        }
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    protected ArrayList<T> get_Data(int row)
    {
        return data;
    }
}
