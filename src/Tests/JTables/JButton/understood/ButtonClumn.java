package Tests.JTables.JButton.understood;

import java.awt.Component;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;
import javax.swing.table.*;

//OUR MAIN CLASS
public class ButtonClumn extends JFrame
{
    JTable table;
    private boolean DEBUG = false;

    public final Object[] longValues = {"Jane", "Kathy",
            "None of the above",
            new Integer(20), Boolean.TRUE};

    String columnHeaders[] = {"Position", "Team", "Last Year Won", "Trophies"};

    public ButtonClumn()
    {
        //FORM TITLE
        super("Button Column Example");

        //DATA FOR OUR TABLE
        Object[][] data =
                {
                        {"1", "Man Utd", new Integer(2013), "21"},
                        {"2", "Man City", new Integer(2014), "3"},
                        {"3", "Chelsea", new Integer(2015), "7"},
                        {"4", "Arsenal", new Integer(1999), "10"},
                        {"5", "Liverpool", new Integer(1990), "19"},
                        {"6", "Everton", new Integer(1974), "1"},
                };

        //COLUMN HEADERS

        //CREATE OUR TABLE AND SET HEADER
        table = new JTable(data, columnHeaders);

        //SET CUSTOM RENDERER TO TEAMS COLUMN
        table.getColumnModel().getColumn(1).setCellRenderer(new ButtonRenderer());

        //SET CUSTOM EDITOR TO TEAMS COLUMN
        table.getColumnModel().getColumn(1).setCellEditor(new ButtonEditor(new JCheckBox()));

        //initColumnSizes(table);

        // Align cells JTable centre
        setCellsAlignment(table, 0, Arrays.asList(1));


        //SCROLLPANE,SET SZE,SET CLOSE OPERATION
        JScrollPane pane = new JScrollPane(table);
        getContentPane().add(pane);
        setSize(450, 100);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String[] args)
    {
        ButtonClumn bc = new ButtonClumn();
        bc.setVisible(true);
    }

    private void btnDeleteAction()
    {
        int row = table.getSelectedRow();

        if (row==-1)
        {
            JOptionPane.showMessageDialog(null, "Select a Row to delete it!");
        }

        ((DefaultTableModel) table.getModel()).removeRow(row); // remove row


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

    private void initColumnSizes(JTable table)
    {

        //TableRenderDemo2_00.MyTableModel model = (TableRenderDemo2_00.MyTableModel) table.getModel();
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;
        //Object[] longValues = model.longValues;
        TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();

        for (int i = 0; i < columnHeaders.length; i++)
        {
            try
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
            catch (Exception e)
            {

            }
        }
    }


}

//BUTTON RENDERER CLASS
class ButtonRenderer extends JButton implements TableCellRenderer
{

    //CONSTRUCTOR
    public ButtonRenderer()
    {
        //SET BUTTON PROPERTIES
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object obj,
                                                   boolean selected, boolean focused, int row, int col)
    {

        //SET PASSED OBJECT AS BUTTON TEXT
        setText((obj==null) ? "":obj.toString());

        return this;
    }

}

//BUTTON EDITOR CLASS
class ButtonEditor extends DefaultCellEditor
{
    protected JButton btn;
    private String lbl;
    private Boolean clicked;

    public ButtonEditor(JCheckBox checkBox)
    {
        super(checkBox);

        btn = new JButton();
        btn.setOpaque(true);

        //WHEN BUTTON IS CLICKED
        btn.addActionListener(ae -> {
            fireEditingStopped();
        });
    }

    //OVERRIDE A COUPLE OF METHODS
    @Override
    public Component getTableCellEditorComponent(JTable table, Object obj,
                                                 boolean selected, int row, int col)
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