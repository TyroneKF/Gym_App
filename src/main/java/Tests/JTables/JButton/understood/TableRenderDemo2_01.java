package Tests.JTables.JButton.understood;/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
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
 *   - Neither the name of Oracle or the names of its
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

//https://stackoverflow.com/questions/24473418/creating-a-jcombobox-in-a-jtable-with-the-dropdown-always-visible
//https://docs.oracle.com/javase/tutorial/uiswing/examples/components/TableRenderDemoProject/src/components/TableRenderDemo.java
// http://docs.oracle.com/javase/tutorial/uiswing/components/table.html



/*
 * TableRenderDemo.java requires no other files.
 */



import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * TableRenderDemo is just like TableDemo, except that it
 * explicitly initializes column sizes and it uses a combo box
 * as an editor for the Sport column.
 */
public class TableRenderDemo2_01 extends JPanel
{
    private boolean DEBUG = false;
    DefaultComboBoxModel model = new DefaultComboBoxModel();
    JTable table = new JTable(new MyTableModel());

    //############################################################
    public static void main(String[] args)
    {
        //Create and set up the window.
        JFrame frame = new JFrame("TableRenderDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //##################################
        //Create and set up the content pane.
        TableRenderDemo2_01 newContentPane = new TableRenderDemo2_01();

        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
        //##################################
        //Display the window.
        frame.pack();
        frame.setVisible(true);

        frame.setVisible(true);
        frame.setResizable(true);
        frame.setSize(800, 800);
        frame.setLocation(100, 200);
    }

    public TableRenderDemo2_01()
    {
        super(new GridLayout(1, 0));


        table.setPreferredScrollableViewportSize(new Dimension(500, 500));
        table.setFillsViewportHeight(true);

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        //#################################
        // Table Data Setup
        //#################################

        //Set up column sizes.
        initColumnSizes(table);

        //Fiddle with the Sport column's cell editors/renderers.
        setUpSportColumn(table, table.getColumnModel().getColumn(2));

        setupDelteBtn(table, table.getColumnModel().getColumn(5));


        // Align cells JTable centre
        setCellsAlignment(table, 0, Arrays.asList(4, 5));


        // Set JComboBox DropDown Visible
        TableColumn tableColumn = table.getColumnModel().getColumn(2);
        ComboBoxTableCellRenderer renderer = new ComboBoxTableCellRenderer();
        renderer.setModel(model);
        tableColumn.setCellRenderer(renderer);


        //Add the scroll pane to this panel.
        add(scrollPane);

        //Increase cell row height
        table.setRowHeight(table.getRowHeight() + 15); // HELLO Relocate
    }


    private void initColumnSizes(JTable table)
    {

        MyTableModel model = (MyTableModel) table.getModel();
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;
        Object[] longValues = model.longValues;
        TableCellRenderer headerRenderer =
                table.getTableHeader().getDefaultRenderer();

        for (int i = 0; i < 5; i++)
        {
            column = table.getColumnModel().getColumn(i);

            comp = headerRenderer.getTableCellRendererComponent(
                    null, column.getHeaderValue(),
                    false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;

            comp = table.getDefaultRenderer(model.getColumnClass(i)).
                    getTableCellRendererComponent(
                            table, longValues[i],
                            false, false, 0, i);
            cellWidth = comp.getPreferredSize().width;

            if (DEBUG)
            {
                System.out.println("Initializing width of column "
                        + i + ". "
                        + "headerWidth = " + headerWidth
                        + "; cellWidth = " + cellWidth);
            }

            column.setPreferredWidth(Math.max(headerWidth + 20, cellWidth)); //HELLO I added 20

        }
    }

    public void setUpSportColumn(JTable table, TableColumn sportColumn)
    {
        //Set up the editor for the sport cells.
        JComboBox comboBox = new JComboBox();

        model.addElement("Snowboarding");
        model.addElement("Rowing");
        model.addElement("Knitting");
        model.addElement("Speed reading");
        model.addElement("Pool");
        model.addElement("None of the above");
        comboBox.setModel(model);

        sportColumn.setCellEditor(new DefaultCellEditor(comboBox)); // sets column to a comboBox

        model = new DefaultComboBoxModel();
        model.addElement("Snowboarding");
        model.addElement("Rowing");
        model.addElement("Knitting");
        model.addElement("Speed reading");
        model.addElement("Pool");
        model.addElement("None of the above");

        //######################################################
        // Centre ComboBox (Could be removed)
        //######################################################

        DefaultListCellRenderer listRenderer = new DefaultListCellRenderer();
        listRenderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER); // center-aligned items
        comboBox.setRenderer(listRenderer);

        // Centre JCombox Options
        ((JLabel) comboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
    }

    public void setupDelteBtn(JTable table, TableColumn deleteBtnColumn)
    {
        //SET CUSTOM RENDERER TO TEAMS COLUMN
        deleteBtnColumn.setCellRenderer(new ButtonRenderer2());

        //SET CUSTOM EDITOR TO TEAMS COLUMN
       deleteBtnColumn.setCellEditor(new ButtonEditor2(new JCheckBox()));
    }

    public void deleteRow()
    {
        int row = table.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel)table.getModel();
        model.removeRow( row );
    }

    private static void setCellsAlignment(JTable table, int alignment, List<Integer> skipRows)
    {
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(alignment);

        TableModel tableModel = table.getModel();

        for (int columnIndex = 0; columnIndex < tableModel.getColumnCount(); columnIndex++)
        {
            if (skipRows.contains(columnIndex))
            {
                continue;
            }

            table.getColumnModel().getColumn(columnIndex).setCellRenderer(rightRenderer);
        }
    }

    /*
     * This method picks good column sizes.
     * If all column heads are wider than the column's cells'
     * contents, then you can just use column.sizeWidthToFit().
     */

    //#########################################################################################################

    class MyTableModel extends AbstractTableModel
    {
        ArrayList<Integer> unEditableCells = new ArrayList<>(Arrays.asList(0,1));

        private String[] columnNames = {"First Name",
                "Last Name",
                "Sport",
                "# of Years",
                "Vegetarian", "Delete Row"};
        private Object[][] data = {
                {"Kathy", "Smith", "Snowboarding", new Integer(5), new Boolean(false), "Delete Row"},
                {"John", "Doe", "Rowing", new Integer(3), new Boolean(true), "Delete Row"},
                {"Sue", "Black", "Knitting", new Integer(2), new Boolean(false), "Delete Row"},
                {"Jane", "White", "Speed reading", new Integer(20), new Boolean(true), "Delete Row"},
                {"Joe", "Brown", "Pool", new Integer(10), new Boolean(false), "Delete Row"}
        };

        public final Object[] longValues = {"Jane", "Kathy",
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
            if (unEditableCells.contains(col))
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
    //#########################################################################################################

    class ComboBoxTableCellRenderer extends JComboBox implements TableCellRenderer
    {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            setSelectedItem(value);
            return this;
        }

    }

    //#########################################################################################################

    //BUTTON RENDERER CLASS
    class ButtonRenderer2 extends JButton implements TableCellRenderer
    {

        //CONSTRUCTOR
        public ButtonRenderer2()
        {
            //SET BUTTON PROPERTIES
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object obj, boolean selected, boolean focused, int row, int col)
        {

            //SET PASSED OBJECT AS BUTTON TEXT
            setText((obj==null) ? "":obj.toString());

            return this;
        }

    }

    //################################################

    //BUTTON EDITOR CLASS
    class ButtonEditor2 extends DefaultCellEditor
    {
        protected JButton btn;
        private String lbl;
        private Boolean clicked;

        public ButtonEditor2(JCheckBox checkBox)
        {
            super(checkBox);

            btn = new JButton();
            btn.setOpaque(true);

            //WHEN BUTTON IS CLICKED
            btn.addActionListener(ae -> {
               // deleteRow();
                System.out.printf("\nButton Clicked");
                fireEditingStopped();
            });
        }

        //OVERRIDE A COUPLE OF METHODS
        @Override
        public Component getTableCellEditorComponent(JTable table, Object obj, boolean selected, int row, int col)
        {

            //SET TEXT TO BUTTON,SET CLICKED TO TRUE,THEN RETURN THE BTN OBJECT
            lbl = (obj==null) ? "":obj.toString();
            btn.setText(lbl);
            clicked = true;
            return btn;
        }

        //IF BUTTON CELL VALUE CHNAGES,IF CLICKED THAT IS
        @Override
        public Object getCellEditorValue()
        {

            if (clicked)
            {
                //SHOW US SOME MESSAGE
                JOptionPane.showMessageDialog(btn, lbl + " Clicked");

                 deleteRow();

            }
            //SET IT TO FALSE NOW THAT ITS CLICKED
            clicked = false;
            return new String(lbl);
        }

        @Override
        public boolean stopCellEditing()
        {

            //SET CLICKED TO FALSE FIRST
            clicked = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped()
        {
            // TODO Auto-generated method stub
            super.fireEditingStopped();
        }
    }

    //#########################################################################################################

}
