package Tests.JTables.JComboBox;

/*
 * Copyright (c) 1995 - 2008 Sun Microsystems, Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


/*
 * TableRenderDemo.java requires no other files.
 */

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;

/**
 * TableRenderDemo is just like TableDemo, except that it
 * explicitly initializes column sizes and it uses a combo box
 * as an editor for the Sport column.
 */

class JcomboBox_Update extends JPanel
{
    private boolean DEBUG = false;

    public JcomboBox_Update()
    {
        super(new GridLayout(1, 0));

        JTable table = new JTable(new MyTableModel());
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        //Fiddle with the Sport column's cell editors/renderers.
        setUpSportColumn(table, table.getColumnModel().getColumn(2));

        //Add the scroll pane to this panel.
        add(scrollPane);
    }

    /*
     * This method picks good column sizes.
     * If all column heads are wider than the column's cells'
     * contents, then you can just use column.sizeWidthToFit().
     */

    public void setUpSportColumn(JTable table,
                                 TableColumn sportColumn)
    {
        //Set up the editor for the sport cells.
        sportColumn.setCellEditor(new ComboEditor());

        //Set up tool tips for the sport cells.
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setToolTipText("Click for combo box");
        sportColumn.setCellRenderer(renderer);
    }

    //##########################################################################################
    /**
     * Custom editor that changes the combo choices based
     * on the "Vegetarian" column value
     */
    class ComboEditor extends DefaultCellEditor
    {
        DefaultComboBoxModel model;
        Map<String, List<String>> choicesMap;

        public ComboEditor()
        {
            super(new JComboBox());
            model = (DefaultComboBoxModel) ((JComboBox) getComponent()).getModel();
            buildComboMap();
        }

        private void buildComboMap()
        {
            choicesMap = new HashMap<String, List<String>>();
            // for "true"
            List<String> choices = new ArrayList<String>();
            choices.add("Knitting");
            choices.add("Interior Decoration");
            choicesMap.put("true", choices);
            // for "false"
            choices = new ArrayList<String>();
            choices.add("Football");
            choices.add("Hockey");
            choices.add("Kickboxing");
            choicesMap.put("false", choices);
        }

        //First time the cell is created
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
        {

            model.removeAllElements();
            String keyColumnValue = table.getValueAt(row, table.getColumnModel().getColumnIndex("Vegetarian")).toString();
            for (String item : choicesMap.get(keyColumnValue))
            {
                model.addElement(item);
            }
            return super.getTableCellEditorComponent(table, value, isSelected, row, column);
        }
    }

    //##########################################################################################
    class MyTableModel extends AbstractTableModel
    {
        private String[] columnNames = {"First Name",
                "Last Name",
                "Sport",
                "# of Years",
                "Vegetarian"};
        private Object[][] data = {
                {"Mary", "Campione",
                        "Football", new Integer(5), new Boolean(false)},
                {"Alison", "Huml",
                        "Interior Decoration", new Integer(3), new Boolean(true)},
                {"Kathy", "Walrath",
                        "Hockey", new Integer(2), new Boolean(false)},
                {"Sharon", "Zakhour",
                        "Knitting", new Integer(20), new Boolean(true)},
                {"Philip", "Milne",
                        "Kickboxing", new Integer(10), new Boolean(false)}
        };

        public final Object[] longValues = {"Sharon", "Campione",
                "None of the above",
                new Integer(20), Boolean.TRUE};

        public int getColumnCount()
        {
            return columnNames.length;
        }

        public int getRowCount()
        {
            return data.length;
        }

        public String getColumnName(int col)
        {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col)
        {
            return data[row][col];
        }

        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        public Class getColumnClass(int c)
        {
            return getValueAt(0, c).getClass();
        }

        /*
         * Don't need to implement this method unless your table's
         * editable.
         */
        public boolean isCellEditable(int row, int col)
        {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            if (col < 2)
            {
                return false;
            }
            else
            {
                return true;
            }
        }

        /*
         * Don't need to implement this method unless your table's
         * data can change.
         */
        public void setValueAt(Object value, int row, int col)
        {
            if (DEBUG)
            {
                System.out.println("Setting value at " + row + "," + col
                        + " to " + value
                        + " (an instance of "
                        + value.getClass() + ")");
            }

            data[row][col] = value;
            fireTableCellUpdated(row, col);

            if (DEBUG)
            {
                System.out.println("New value of data:");
                printDebugData();
            }
        }

        private void printDebugData()
        {
            int numRows = getRowCount();
            int numCols = getColumnCount();

            for (int i = 0; i < numRows; i++)
            {
                System.out.print("    row " + i + ":");
                for (int j = 0; j < numCols; j++)
                {
                    System.out.print("  " + data[i][j]);
                }
                System.out.println();
            }
            System.out.println("--------------------------");
        }
    }
    //##########################################################################################

    public static void main(String[] args)
    {
        //Create and set up the window.
        JFrame frame = new JFrame("TableRenderDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JcomboBox_Update newContentPane = new JcomboBox_Update();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
}