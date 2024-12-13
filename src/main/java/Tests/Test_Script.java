package Tests;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

// https://www.tutorialspoint.com/how-to-change-each-column-width-of-a-jtable-in-java
public class Test_Script extends JFrame {
    private JTable table;
    private JScrollPane scrollPane;
    private DefaultTableModel model;
    private DefaultTableCellRenderer cellRenderer;
    public Test_Script() {
        setTitle("JTable Test");
        setLayout(new FlowLayout());
        scrollPane = new JScrollPane();
        JTable table = new JTable();
        scrollPane.setViewportView(table);
        model = (DefaultTableModel)table.getModel();
        model.addColumn("S.No");
        model.addColumn("First Name");
        model.addColumn("Last Name");
        model.addColumn("Email");
        model.addColumn("Contact");
        for(int i = 0;i < 4; i++) {
            model.addRow(new Object[0]);
            model.setValueAt(i+1, i, 0);
            model.setValueAt("Tutorials", i, 1);
            model.setValueAt("Point", i, 2);
            model.setValueAt("@tutorialspoint.com", i, 3);
            model.setValueAt("123456789", i, 4);
        }
        // set the column width for each column
        table.getColumnModel().getColumn(0).setPreferredWidth(5);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(cellRenderer);
        add(scrollPane);
        setSize(475, 250);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    public static void main(String[] args) {
        new Test_Script();
    }
}