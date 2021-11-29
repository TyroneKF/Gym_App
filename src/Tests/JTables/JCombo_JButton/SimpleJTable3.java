package Tests.JTables.JCombo_JButton;

// Packages to import
// https://stackoverflow.com/questions/10347983/making-a-jbutton-clickable-inside-a-jtable
//http://tips4java.wordpress.com/2009/07/12/table-button-column/

import Tests.JTables.Working_ButtonColumn;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleJTable3 extends  JPanel
{
    // frame
    JFrame frame;
    // Table
    JTable jTable;
    DefaultTableModel tableModel;
    DefaultComboBoxModel model = new DefaultComboBoxModel();

    boolean DEBUG = false;

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




    // Constructor
    SimpleJTable3()
    {
        super(new GridLayout(1, 0));

        //###################################################################################
        // Table Setup
        //###################################################################################
        //Creating Table

        jTable = new JTable();
        //instance table model
        tableModel = new DefaultTableModel(data, columnNames)
        {
            //HELLO Not sure what this is for
            public final Object[] longValues = {"Jane", "Kathy",
                    "None of the above",
                    new Integer(20), Boolean.TRUE};

            @Override
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

            @Override
            public Class getColumnClass(int c)
            {
                return getValueAt(0, c).getClass();
            }

        };

        jTable.setModel(tableModel);
        jTable.setPreferredScrollableViewportSize(new Dimension(500, 500));
        jTable.setFillsViewportHeight(true);

        jTable.setRowHeight(jTable.getRowHeight() + 15); // HELLO Relocate

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(jTable);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        //#################################
        // Table Data Setup
        //#################################

        initColumnSizes(jTable);

        //Fiddle with the Sport column's cell editors/renderers.
        setUpSportColumn(jTable, jTable.getColumnModel().getColumn(2));

       // setupCheckBoxColumn(jTable, jTable.getColumnModel().getColumn(4));

        setupDeleteBtnColumn(jTable, 5);

        setCellsAlignment(jTable, 0, Arrays.asList(4, 5));

        //#################################
        // Set JComboBox DropDown Visible
        //#################################

        TableColumn tableColumn = jTable.getColumnModel().getColumn(2);
        ComboBoxTableCellRenderer renderer = new ComboBoxTableCellRenderer();
        renderer.setModel(model);
        tableColumn.setCellRenderer(renderer);

        //###################################################################################
        // Adding to JPanel
        //###################################################################################

        //Add the scroll pane to this panel.
        add(scrollPane);

    }

    //############################################################
    public static void main(String[] args)
    {
        //Create and set up the window.
        JFrame frame = new JFrame("JDBC_JTable");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //##################################
        //Create and set up the content pane.
        SimpleJTable3 newContentPane = new SimpleJTable3();

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
    //#########################################################################################################

    public void setupDeleteBtnColumn(JTable table, int deleteBtnColumn)
    {
        Action delete = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                JTable table = (JTable)e.getSource();
                int modelRow = Integer.valueOf( e.getActionCommand() );
                ((DefaultTableModel)table.getModel()).removeRow(modelRow);
            }
        };
        Working_ButtonColumn workingButtonColumn = new Working_ButtonColumn(table, delete, deleteBtnColumn);
        workingButtonColumn.setMnemonic(KeyEvent.VK_D);
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

    public void setupCheckBoxColumn(JTable table, TableColumn checkbox_column)
    {
        //checkbox_column.setCellEditor(new DefaultCellEditor(new JCheckBox()));
       // table.getTableHeader().setReorderingAllowed(false);
    }

    private void initColumnSizes(JTable table)
    {
        Object[] longValues = {"Jane", "Kathy",
                "None of the above",
                new Integer(20), Boolean.TRUE};

        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;

        TableCellRenderer headerRenderer =
                table.getTableHeader().getDefaultRenderer();

        for (int i = 0; i < 5; i++)
        {
            column = table.getColumnModel().getColumn(i);

            comp = headerRenderer.getTableCellRendererComponent(
                    null, column.getHeaderValue(),
                    false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;


            comp = table.getDefaultRenderer(tableModel.getColumnClass(i)).
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

    private static void setCellsAlignment(JTable table, int alignment, List<Integer> skipRows)
    {
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(alignment);

        TableModel tableModel = table.getModel();

        for (int columnIndex = 0; columnIndex < tableModel.getColumnCount(); columnIndex++)
        {
            if (skipRows!=null && skipRows.contains(columnIndex))
            {
                continue;
            }

            table.getColumnModel().getColumn(columnIndex).setCellRenderer(rightRenderer);
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


}
